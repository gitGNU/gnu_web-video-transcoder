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
package org.webvideotranscoder.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.webvideotranscoder.App;
import org.webvideotranscoder.data.Format;
import org.webvideotranscoder.data.MediaSettings;
import org.webvideotranscoder.ffmpeg.FFmpegHelper;


public final class AppWindow extends JFrame {

  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(AppWindow.class.getName());

  // translated strings
  private static final String APP_NAME = App.RB.getString("app.name");
  private static final String SRC_TITLE = App.RB.getString("source.title");
  private static final String SRC_BROWSE = App.RB.getString("source.button");
  private static final String CONV_TITLE = App.RB.getString("conversion.title");
  private static final String SETTINGS = App.RB.getString("conversion.settings");
  private static final String FORMAT = App.RB.getString("conversion.format");
  private static final String ABOUT = App.RB.getString("button.about");
  private static final String EXIT = App.RB.getString("button.exit");
  private static final String SAVE = App.RB.getString("button.save");
  private static final String NOT_VALID_VIDEO = App.RB.getString("not.valid.video");
  private static final String ERROR_TITLE = App.RB.getString("error.title");
  private static final String FILETYPE_DESC = App.RB.getString("source.filetypes.description");
  private static final String OVERWRITE_MSG = App.RB.getString("save.existing.message");
  private static final String OVERWRITE_TITLE = App.RB.getString("save.existing.title");

  // icon path
  private static final String APP_ICN_PATH = "/icons/48px-Video-x-generic.png";

  private static final String[] VIDEO_FILES_EXT = {"264", "3g2", "3gp", "arf", "asf", "asx", "avi",
      "bik", "dash", "dat", "dvr", "flv", "h264", "m2t", "m2ts", "m4v", "mkv", "mod", "mov", "mp4",
      "mpeg", "mpg", "mts", "ogg", "ogv", "prproj", "rec", "rm", "rmvb", "swf", "tod", "tp", "ts",
      "vob", "webm", "wlmp", "wmv"};

  // ui components
  private final JTextField sourceTextField = new JTextField(30);
  private final JButton browseSource = new JButton(SRC_BROWSE);
  private final FileFilter sourceFileFilter = new SourceVideoFileFilter();
  private final JComboBox<Format> formats = new JComboBox<Format>(Format.values());
  private final JButton settingsButton = new JButton(SETTINGS);
  private final JButton aboutButton = new JButton(ABOUT);
  private final JButton exitButton = new JButton(EXIT);
  private final JButton saveButton = new JButton(SAVE);

  private MediaSettings currentMediaSettings = new MediaSettings();

  public AppWindow() {
    super();
    
    initGui();
    initListeners();

    setTitle(APP_NAME);
    setIconImage(new ImageIcon(getClass().getResource(APP_ICN_PATH)).getImage());
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
  }

  private void initGui() {
    Box mainPanel = new Box(BoxLayout.PAGE_AXIS);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel sourceTitlePanel = new JPanel(new BorderLayout());
    sourceTitlePanel.setBorder(BorderFactory.createTitledBorder(SRC_TITLE));
    JPanel sourcePanel = new JPanel(new BorderLayout(5, 0));
    sourcePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    sourceTextField.setFocusable(false);
    sourceTextField.setEditable(false);
    sourcePanel.add(sourceTextField, BorderLayout.CENTER);
    sourcePanel.add(browseSource, BorderLayout.EAST);
    sourceTitlePanel.add(sourcePanel, BorderLayout.CENTER);
    mainPanel.add(sourceTitlePanel);

    mainPanel.add(Box.createVerticalStrut(15));

    JPanel conversionTitlePanel = new JPanel(new BorderLayout());
    conversionTitlePanel.setBorder(BorderFactory.createTitledBorder(CONV_TITLE));
    JPanel presetPanel = new JPanel(new BorderLayout(5, 0));
    presetPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    presetPanel.add(new JLabel(FORMAT), BorderLayout.WEST);
    presetPanel.add(formats, BorderLayout.CENTER);
    presetPanel.add(settingsButton, BorderLayout.EAST);
    conversionTitlePanel.add(presetPanel);
    mainPanel.add(conversionTitlePanel);

    mainPanel.add(Box.createVerticalStrut(15));

    Box buttonsPanel = new Box(BoxLayout.LINE_AXIS);
    buttonsPanel.add(aboutButton);
    buttonsPanel.add(Box.createHorizontalGlue());
    buttonsPanel.add(saveButton);
    buttonsPanel.add(Box.createHorizontalStrut(5));
    buttonsPanel.add(exitButton);
    mainPanel.add(buttonsPanel);

    setContentPane(mainPanel);
    setConversionEnabled(false);
    
    pack();
  }

  private void setConversionEnabled(boolean enabled) {
    formats.setEnabled(enabled);
    settingsButton.setEnabled(enabled);
    saveButton.setEnabled(enabled);
  }

  private void initListeners() {
    browseSource.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        browseSource();
      }
    });

    settingsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        new SettingsDialog(AppWindow.this, currentMediaSettings, getSelectedFormat())
            .setVisible(true);
      }
    });

    aboutButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        new AboutDialog(AppWindow.this).setVisible(true);
      }
    });

    exitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        dispose();
      }
    });

    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        Format format = getSelectedFormat();
        File out = browseDestination(format);
        if (out != null) {
          File in = new File(sourceTextField.getText());
          new WorkingDialog(AppWindow.this, in, out, format, currentMediaSettings).setVisible(true);
        }
      }
    });
  }

  private Format getSelectedFormat() {
    return (Format) formats.getSelectedItem();
  }

  private void browseSource() {
    String folder =
        sourceTextField.getText().isEmpty() ? System.getProperty("user.home") : sourceTextField
            .getText();

    JFileChooser fileChooser = new JFileChooser(folder);
    fileChooser.setFileFilter(sourceFileFilter);

    int status = fileChooser.showOpenDialog(this);
    if (status == JFileChooser.APPROVE_OPTION) {
      File in = fileChooser.getSelectedFile();
      try {
        MediaSettings info = FFmpegHelper.getMediaInfo(in);
        if (info.getDuration() > 0) {
          currentMediaSettings = info;
          sourceTextField.setText(in.getAbsolutePath());
          setConversionEnabled(true);
          saveButton.requestFocusInWindow();
        } else {
          showNotValidMessage(in);
        }
      } catch (Exception e) {
        log.log(Level.SEVERE, "Media analyse error", e);
        showNotValidMessage(in);
      }
    }
  }

  private void showNotValidMessage(File in) {
    JOptionPane.showMessageDialog(AppWindow.this, String.format(NOT_VALID_VIDEO, in.getName()),
        ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
  }

  private File browseDestination(final Format format) {
    String inPath = sourceTextField.getText();
    String outPath =
        sourceTextField.getText().substring(0, inPath.lastIndexOf('.')) + format.getFileExt();
    File out = new File(outPath);
    JFileChooser fileChooser = new JFileChooser(out) {
      private static final long serialVersionUID = 1L;

      @Override
      public void approveSelection() {
        File f = getSelectedFile();
        if (f.exists() && getDialogType() == SAVE_DIALOG) {
          int result =
              JOptionPane.showConfirmDialog(this, OVERWRITE_MSG, OVERWRITE_TITLE,
                  JOptionPane.YES_NO_CANCEL_OPTION);
          switch (result) {
            case JOptionPane.YES_OPTION:
              super.approveSelection();
              return;
            case JOptionPane.NO_OPTION:
              return;
            case JOptionPane.CLOSED_OPTION:
              return;
            case JOptionPane.CANCEL_OPTION:
              cancelSelection();
              return;
          }
        }
        super.approveSelection();
      }
    };
    fileChooser.setSelectedFile(out);
    fileChooser.setFileFilter(format.getFileFilter());

    int status = fileChooser.showSaveDialog(this);
    if (status == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile();
    }

    return null;
  }


  /**
   * Video files filter for file chooser dialog.
   */
  private final class SourceVideoFileFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
      boolean accept = file.isDirectory();
      if (!accept) {
        String fileName = file.getName().toLowerCase();
        int extIndex = fileName.lastIndexOf('.') + 1;
        if (extIndex != -1) {
          int maxIndex = fileName.length() - 1;
          String ext = fileName.substring(Math.min(extIndex, maxIndex));
          accept = Arrays.binarySearch(VIDEO_FILES_EXT, ext) >= 0;
        }
      }
      return accept;
    }

    @Override
    public String getDescription() {
      return FILETYPE_DESC;
    }
  }
}
