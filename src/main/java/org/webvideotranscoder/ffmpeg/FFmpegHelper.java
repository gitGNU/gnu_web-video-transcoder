/*
 * Copyright (C) 2016, 2017 Vincent Gravade
 * 
 * This file is part of WebVideoTranscoder.
 * 
 * Web Video Transcoder is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Web Video Transcoder is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WebVideoTranscoder.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.webvideotranscoder.ffmpeg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.webvideotranscoder.data.MediaSettings;
import org.webvideotranscoder.data.Resolution;


public class FFmpegHelper {

  private static final Logger log = Logger.getLogger(FFmpegHelper.class.getName());

  private static final boolean IS_WINDOWS = System.getProperty("os.name", "").startsWith("Windows");

  public static final String FFMPEG_EXE = "ffmpeg" + (IS_WINDOWS ? ".exe" : "");
  public static final String FFPROBE_EXE = "ffprobe" + (IS_WINDOWS ? ".exe" : "");

  public static final String FRAME_PATTERN = "frame=";

  private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

  private static String ffmpegPath = FFMPEG_EXE;
  private static String ffprobePath = FFPROBE_EXE;

  private static final String[] WEBM_CRF = {"63", "56", "50", "44", "37", "31", "25", "18", "12",
      "6", "0"};

  private static final String[] OPUS_BITRATE = {"64k", "80k", "96k", "112k", "128k", "160k",
      "192k", "224k", "256k", "320k", "500k"};

  private static final String NB_THREADS = String.valueOf(Runtime.getRuntime()
      .availableProcessors());

  public static boolean checkFFmpegPresence() {
    if (checkFFmpegExecutable(new File(System.getProperty("user.dir", ""), FFMPEG_EXE))) {
      return true;
    }
    if (checkFFmpegExecutable(new File(System.getProperty("java.class.path", ""), FFMPEG_EXE))) {
      return true;
    }
    if (checkFFmpegExecutable(new File("/usr/bin/", FFMPEG_EXE))) {
      return true;
    }
    return false;
  }

  public static boolean checkFFprobePresence() {
    if (checkFFprobeExecutable(new File(System.getProperty("user.dir", ""), FFPROBE_EXE))) {
      return true;
    }
    if (checkFFprobeExecutable(new File(System.getProperty("java.class.path", ""), FFPROBE_EXE))) {
      return true;
    }
    if (checkFFprobeExecutable(new File("/usr/bin/", FFPROBE_EXE))) {
      return true;
    }
    return false;
  }

  private static boolean checkFFmpegExecutable(final File file) {
    if (file.canExecute()) {
      log.fine(String.format("%s FOUND", file));
      ffmpegPath = file.getAbsolutePath();
      return true;
    }
    log.fine(String.format("%s NOT found", file));
    return false;
  }

  private static boolean checkFFprobeExecutable(final File file) {
    if (file.canExecute()) {
      log.fine(String.format("%s FOUND", file));
      ffprobePath = file.getAbsolutePath();
      return true;
    }
    log.fine(String.format("%s NOT found", file));
    return false;
  }

  public static MediaSettings getMediaInfo(File file) throws Exception {

    MediaSettings info = new MediaSettings();
    File output = File.createTempFile("ffmprobe_", ".xml");

    ProcessBuilder pb =
        new ProcessBuilder(ffprobePath, "-v", "quiet", "-print_format", "xml", "-show_format",
            "-show_streams", file.getAbsolutePath());
    pb.redirectOutput(output);
    Process p = pb.start();
    p.waitFor();

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(output);
    doc.getDocumentElement().normalize();

    Node formatNode = doc.getElementsByTagName("format").item(0);
    if (formatNode.getNodeType() == Node.ELEMENT_NODE) {
      Element eElement = (Element) formatNode;

      String duration = eElement.getAttribute("duration");
      info.setDuration(duration.length() > 0 ? convertDuration(duration) : 0);
    }

    NodeList nList = doc.getElementsByTagName("stream");
    for (int i = 0; i < nList.getLength(); i++) {
      Node nNode = nList.item(i);
      if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        Element eElement = (Element) nNode;
        String type = eElement.getAttribute("codec_type");
        if ("video".equals(type)) {
          String fps = eElement.getAttribute("r_frame_rate");
          info.setFps(fps.length() > 0 ? convertFps(fps) : -1F);
        }
      }
    }

    output.delete();

    log.fine(info.toString());

    return info;
  }

  public static long extractFrame(String line) {
    String removeBefore =
        line.substring(line.indexOf(FRAME_PATTERN) + FRAME_PATTERN.length()).trim();
    String removeAfter = removeBefore.substring(0, removeBefore.indexOf(' ')).trim();
    return Integer.valueOf(removeAfter);
  }

  /**
   * Delete log files generated by ffmpeg during passes.
   * <p>
   * 
   * @param passlogfile the argument value passed to ffmpeg
   */
  public static void cleanPassLogFiles(final String passlogfile) {
    String[] fileNames = TMP_DIR.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith(passlogfile);
      }
    });
    if (fileNames != null) {
      for (String fileName : fileNames) {
        new File(TMP_DIR, fileName).delete();
      }
    }
  }

  /**
   * Creates and starts process.
   * <p>
   * 
   * @param command the command line parts
   * @return the process created and started
   * @throws IOException
   */
  public static Process createProcess(File in, File out, MediaSettings settings, String videoCodec,
      String audioCodec, int pass) throws IOException {
    List<String> command = new ArrayList<String>();
    command.add(ffmpegPath);

    // input file
    command.add("-i");
    command.add(in.getAbsolutePath());

    // video codec
    command.add("-c:v");
    command.add(videoCodec);

    // passes
    if (pass > 0) {
      command.add("-pass");
      command.add(String.valueOf(pass));
      command.add("-passlogfile");
      command.add(settings.getUid());
    }

    // video size
    if (!Resolution.ORIGINAL.equals(settings.getResolution())) {
      command.add("-vf");
      command.add(String.format("scale=w=%d:h=%d:force_original_aspect_ratio=decrease", settings
          .getResolution().getWidth(), settings.getResolution().getHeight()));
    }

    // video quality
    if (videoCodec.equals("libtheora")) {

      command.add("-q:v");
      command.add(String.valueOf(settings.getVideoQuality()));

    } else if (videoCodec.startsWith("libvpx")) {

      command.add("-crf");
      command.add(WEBM_CRF[settings.getVideoQuality()]);

      // no bitrate specified, only global quality
      command.add("-b:v");
      command.add("0");

      command.add("-threads");
      command.add(NB_THREADS);

      command.add("-speed");
      command.add(String.valueOf(pass == 1 ? 4 : 2));

      command.add("-auto-alt-ref");
      command.add("1");

      command.add("-lag-in-frames");
      command.add("25");

      if (videoCodec.endsWith("vp9")) {
        command.add("-tile-columns");
        command.add("6");

        command.add("-frame-parallel");
        command.add("1");
      }
    }

    if (pass == 1) {
      command.add("-an");
    } else {
      // audio codec
      command.add("-c:a");
      command.add(audioCodec);

      if (audioCodec.equals("libvorbis")) {
        // audio quality
        command.add("-q:a");
        command.add(String.valueOf(settings.getAudioQuality()));
      } else if (audioCodec.equals("libopus")) {
        // audio quality
        command.add("-b:a");
        command.add(OPUS_BITRATE[settings.getAudioQuality()]);
      }
    }

    // output file
    command.add("-y"); // force overwrite
    command.add(out.getAbsolutePath());

    if (log.isLoggable(Level.FINE)) {
      StringBuilder cmdLine = new StringBuilder("COMMAND LINE:");
      for (String arg : command) {
        cmdLine.append(' ');
        cmdLine.append(arg);
      }
      log.fine(cmdLine.toString());
    }



    ProcessBuilder pb = new ProcessBuilder(command);
    pb.directory(TMP_DIR);

    return pb.start();
  }


  private static long convertDuration(String str) {
    return (long) (Float.valueOf(str) * 1000);
  }

  private static float convertFps(final String str) {
    String[] parts = str.split("\\/");
    return Integer.valueOf(parts[0]) / Integer.valueOf(parts[1]);
  }
}
