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
package org.webvideotranscoder;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.webvideotranscoder.ffmpeg.FFmpegHelper;
import org.webvideotranscoder.gui.AppWindow;


/**
 * Main application class.
 */
public class App {

  private static final Logger log = Logger.getLogger(App.class.getName());
  public static final ResourceBundle RB = ResourceBundle.getBundle("translations.Messages");

  public static void main(final String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      log.log(Level.WARNING, e.getMessage(), e);
    }

    final boolean ffmpegOk = FFmpegHelper.checkFFmpegPresence();
    final boolean ffprobeOk = FFmpegHelper.checkFFprobePresence();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (!ffmpegOk) {
          showExecutableErrorMessage(FFmpegHelper.FFMPEG_EXE);
        } else if (!ffprobeOk) {
          showExecutableErrorMessage(FFmpegHelper.FFPROBE_EXE);
        } else {
          new AppWindow().setVisible(true);
        }
      }
    });

  }

  private static void showExecutableErrorMessage(final String executable) {
    JOptionPane.showMessageDialog(null, String.format(RB.getString("ffmpeg.missing"), executable),
        RB.getString("error.title"), JOptionPane.ERROR_MESSAGE);
  }

}
