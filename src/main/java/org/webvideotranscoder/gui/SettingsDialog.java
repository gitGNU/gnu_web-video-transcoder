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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

import org.webvideotranscoder.App;
import org.webvideotranscoder.data.Format;
import org.webvideotranscoder.data.MediaSettings;
import org.webvideotranscoder.data.Resolution;


public final class SettingsDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  // translated strings
  private static final String SETTINGS = App.RB.getString("conversion.settings");
  private static final String VIDEO_QUALITY = App.RB.getString("conversion.settings.video.quality");
  private static final String RESOLUTION = App.RB.getString("conversion.settings.video.resolution");
  private static final String TWO_PASS = App.RB.getString("conversion.settings.video.twopass");
  private static final String AUDIO_QUALITY = App.RB.getString("conversion.settings.audio.quality");
  private static final String CANCEL = App.RB.getString("dialog.button.cancel");
  private static final String OK = App.RB.getString("dialog.button.ok");

  // ui components
  private final JLabel videoQualityLbl = new JLabel(VIDEO_QUALITY);
  private final JSlider videoQualitySlider = new JSlider(0, 10, 8);
  private final JLabel audioQualityLbl = new JLabel(AUDIO_QUALITY);
  private final JSlider audioQualitySlider = new JSlider(0, 10, 3);
  private final JComboBox<Resolution> resolutionCb = new JComboBox<Resolution>(Resolution.values());
  private final JCheckBox twoPassCheckBox = new JCheckBox(TWO_PASS);
  private final JButton cancelButton = new JButton(CANCEL);
  private final JButton okButton = new JButton(OK);

  private final MediaSettings mediaSettings;

  public SettingsDialog(final JFrame parent, MediaSettings mediaSettings, Format format) {
    super(parent);
    this.mediaSettings = mediaSettings;

    initGui();
    initListeners();

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(parent);
    setModal(true);
    setResizable(false);
    setTitle(SETTINGS);
    
    load(format);
  }

  private void initGui() {
    Box mainPanel = new Box(BoxLayout.PAGE_AXIS);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel videoQualityPanel = new JPanel(new BorderLayout(5, 0));
    videoQualityPanel.add(videoQualityLbl, BorderLayout.WEST);
    videoQualitySlider.setMajorTickSpacing(1);
    videoQualitySlider.setPaintLabels(true);
    videoQualityPanel.add(videoQualitySlider, BorderLayout.CENTER);
    mainPanel.add(videoQualityPanel);

    mainPanel.add(Box.createVerticalStrut(10));

    JPanel audioQualityPanel = new JPanel(new BorderLayout(5, 0));
    audioQualityPanel.add(audioQualityLbl, BorderLayout.WEST);
    audioQualitySlider.setMajorTickSpacing(1);
    audioQualitySlider.setPaintLabels(true);
    audioQualityPanel.add(audioQualitySlider, BorderLayout.CENTER);
    mainPanel.add(audioQualityPanel);

    mainPanel.add(Box.createVerticalStrut(10));

    JPanel presetPanel = new JPanel(new BorderLayout(10, 0));
    presetPanel.add(new JLabel(RESOLUTION), BorderLayout.WEST);
    presetPanel.add(resolutionCb, BorderLayout.CENTER);
    mainPanel.add(presetPanel);

    mainPanel.add(Box.createVerticalStrut(10));

    JPanel twoPassPanel = new JPanel(new BorderLayout(10, 0));
    twoPassPanel.add(twoPassCheckBox, BorderLayout.WEST);
    mainPanel.add(twoPassPanel);

    mainPanel.add(Box.createVerticalStrut(10));

    JPanel sepPanel = new JPanel(new BorderLayout());
    sepPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
    mainPanel.add(sepPanel);

    mainPanel.add(Box.createVerticalStrut(10));

    Box buttonsPanel = new Box(BoxLayout.LINE_AXIS);
    buttonsPanel.add(Box.createHorizontalGlue());
    buttonsPanel.add(cancelButton);
    buttonsPanel.add(Box.createHorizontalStrut(5));
    buttonsPanel.add(okButton);
    mainPanel.add(buttonsPanel);

    setContentPane(mainPanel);
    
    pack();
  }

  private void load(Format format) {
    videoQualitySlider.setValue(mediaSettings.getVideoQuality());
    audioQualitySlider.setValue(mediaSettings.getAudioQuality());
    resolutionCb.setSelectedItem(mediaSettings.getResolution());
    twoPassCheckBox.setEnabled(format.isTwoPassSupport());
    twoPassCheckBox.setSelected(format.isTwoPassSupport() && mediaSettings.isTwoPassEncoding());
  }

  private void save() {
    mediaSettings.setVideoQuality(videoQualitySlider.getValue());
    mediaSettings.setAudioQuality(audioQualitySlider.getValue());
    mediaSettings.setResolution((Resolution) resolutionCb.getSelectedItem());
    mediaSettings.setTwoPassEncoding(twoPassCheckBox.isSelected());
  }


  private void initListeners() {
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        dispose();
      }
    });

    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        save();
        dispose();
      }
    });
  }
}
