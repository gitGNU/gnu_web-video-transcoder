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
 * without even the implied warranty of MERCHANTABILITY or FITNESSO FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with WebVideoTranscoder.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.webvideotranscoder.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.webvideotranscoder.App;
import org.webvideotranscoder.data.Format;
import org.webvideotranscoder.data.MediaSettings;
import org.webvideotranscoder.ffmpeg.FFmpegHelper;

public class WorkingDialog extends JDialog implements Runnable {

  private static final long serialVersionUID = 1L;

  private final JProgressBar progressBar = new JProgressBar(0, 1000);
  private final JButton cancelButton = new JButton(App.RB.getString("dialog.button.cancel"));

  private final File in;
  private final File out;
  private final Format format;
  private final MediaSettings mediaSettings;

  private Process ffmpegProcess;
  private int lastPercent = -1;
  private boolean cancel = false;


  public WorkingDialog(final JFrame parent, final File in, final File out, final Format format,
      final MediaSettings mediaSettings) {
    super(parent);
    this.in = in;
    this.out = out;
    this.format = format;
    this.mediaSettings = mediaSettings;

    initGui();
    initListeners();

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setLocationRelativeTo(parent);
    setModal(true);
    setResizable(false);
    setTitle(App.RB.getString("working.title"));

    new Thread(this).start();
  }

  private void failMessage(final File in, final String uid) {
    if (uid != null) {
      FFmpegHelper.cleanPassLogFiles(uid);
    }
    if (!cancel) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(WorkingDialog.this,
              String.format(App.RB.getString("working.encoding.fail"), in.getName()),
              App.RB.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }
      });
    }
  }

  private void initGui() {
    JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
    rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    progressBar.setPreferredSize(new Dimension(400, 20));
    progressBar.setIndeterminate(true);
    rootPanel.add(progressBar, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(cancelButton, BorderLayout.EAST);
    rootPanel.add(bottomPanel, BorderLayout.SOUTH);

    setContentPane(rootPanel);

    pack();
  }


  private void initListeners() {
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        if (ffmpegProcess != null) {
          int option =
              JOptionPane.showConfirmDialog(WorkingDialog.this,
                  App.RB.getString("working.cancel.confirm.message"),
                  App.RB.getString("working.cancel.confirm.title"), JOptionPane.YES_NO_OPTION);
          if (option == JOptionPane.YES_OPTION) {
            cancel = true;
            ffmpegProcess.destroy();
          }
        }
      }
    });
  }

  /**
   * @param duration total duration of media
   */
  private void updateProgress(long frames, int offset, int multiply) {
    Scanner sc = new Scanner(ffmpegProcess.getErrorStream());
    try {
      while (sc.hasNextLine()) {
        String line = sc.nextLine();

        if (line.startsWith(FFmpegHelper.FRAME_PATTERN)) {

          // extract time progress information from ffmpeg raw output
          long frame = FFmpegHelper.extractFrame(line);

          // compute progress by comparing current progress time and total duration
          final int progress = offset + (int) (((double) frame / (double) frames) * multiply);

          // update progress bar in EDT thread
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              progressBar.setIndeterminate(false);
              progressBar.setValue(progress);
              int percent = Math.min(progress / 10, 100);
              if (percent > lastPercent) {
                WorkingDialog.this.setTitle(App.RB.getString("working.title") + " "
                    + String.format(App.RB.getString("working.progress"), percent));
                lastPercent = percent;
              }
            }
          });

        }
      }
    } finally {
      sc.close();
    }
  }

  private void disposeLater() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        dispose();
      }
    });
  }

  public void run() {
    try {
      long duration = mediaSettings.getDuration();
      long frames = mediaSettings.getFrames();
      boolean twoPass = format.isTwoPassSupport() && mediaSettings.isTwoPassEncoding();
      String videoCodec;
      String audioCodec;
      switch (format) {
        default:
        case WEBM_VP9_OPUS:
          videoCodec = "libvpx-vp9";
          audioCodec = "libopus";
          break;
        case WEBM_VP8_VORBIS:
          videoCodec = "libvpx";
          audioCodec = "libvorbis";
          break;
        case OGG_THEORA_VORBIS:
          videoCodec = "libtheora";
          audioCodec = "libvorbis";
          break;
      }
      if (duration > 0L) {
        ffmpegProcess =
            FFmpegHelper.createProcess(in, out, mediaSettings, videoCodec, audioCodec, twoPass ? 1
                : 0);
        if (ffmpegProcess != null) {
          if (twoPass) {
            updateProgress(frames, 0, 300);
          } else {
            updateProgress(frames, 0, 1000);
          }
          int status = ffmpegProcess.waitFor();
          if (status != 0) {
            failMessage(in, mediaSettings.getUid());
          } else if (twoPass) {
            ffmpegProcess =
                FFmpegHelper.createProcess(in, out, mediaSettings, videoCodec, audioCodec, 2);
            if (ffmpegProcess != null) {
              updateProgress(frames, 300, 700);
              status = ffmpegProcess.waitFor();
              if (status != 0) {
                failMessage(in, mediaSettings.getUid());
              }
              disposeLater();
            }
          }
          disposeLater();
        }
      }
    } catch (Exception e) {
      failMessage(in, null);
      if (ffmpegProcess != null) {
        ffmpegProcess.destroy();
      }
      disposeLater();
    }
  }
}
