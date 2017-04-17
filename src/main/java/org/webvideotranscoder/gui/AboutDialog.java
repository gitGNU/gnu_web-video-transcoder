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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.webvideotranscoder.App;

public final class AboutDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(AboutDialog.class.getName());

  // translated strings
  private static final String TITLE = App.RB.getString("about.title");
  private static final String CONTENT = App.RB.getString("about.content");
  private static final String CLOSE = App.RB.getString("about.close.button");

  // ui components
  private final JEditorPane contentPane = new JEditorPane();
  private final JButton closeButton = new JButton(CLOSE);

  public AboutDialog(JFrame parent) {
    super(parent);
    
    initGui();
    initListeners();
    
    setModal(true);
    setTitle(TITLE);
    setResizable(false);
    setLocationRelativeTo(parent);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  private void initGui() {
    JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
    rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    contentPane.setEditable(false);
    HTMLEditorKit kit = new HTMLEditorKit();
    contentPane.setEditorKit(kit);
    contentPane.setDocument(kit.createDefaultDocument());
    contentPane.setText(CONTENT);
    JScrollPane scrollPane = new JScrollPane(contentPane);
    scrollPane.setPreferredSize(new Dimension(550, 300));

    rootPanel.add(scrollPane, BorderLayout.CENTER);

    Box closeButtonBox = new Box(BoxLayout.LINE_AXIS);
    closeButtonBox.add(Box.createHorizontalGlue());
    closeButtonBox.add(closeButton);
    closeButtonBox.add(Box.createHorizontalGlue());
    rootPanel.add(closeButtonBox, BorderLayout.SOUTH);

    setContentPane(rootPanel);
    
    pack();
  }


  private void initListeners() {
    contentPane.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED
            && Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(event.getURL().toURI());
          } catch (Exception e) {
            log.log(Level.SEVERE, "Error opening link in browser", e);
          }
        }
      }
    });

    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        dispose();
      }
    });
  }

}
