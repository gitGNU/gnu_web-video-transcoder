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
package org.webvideotranscoder.data;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.webvideotranscoder.App;

public enum Format {
  

  WEBM_VP9_OPUS("WebM (VP9/Opus)", ".webm", true), 
  WEBM_VP8_VORBIS("WebM (VP8/Vorbis)", ".webm", true), 
  OGG_THEORA_VORBIS("Ogg (Theora/Vorbis)", ".ogv", false);

  private final String name;
  private final String fileExt;
  private final boolean twoPassSupport;

  private Format(final String name, final String fileExt, final boolean twoPassSupport) {
    this.name = name;
    this.fileExt = fileExt;
    this.twoPassSupport = twoPassSupport;
  }
  
  public String getFileExt() {
    return fileExt;
  }
  
  public FileFilter getFileFilter() {
    return new FileFilter() {
      @Override
      public String getDescription() {
        return App.RB.getString("source.filetypes.description") + " (*" + fileExt + ")";
      }
      
      @Override
      public boolean accept(File file) {
        return file.isDirectory() || file.getName().toLowerCase().endsWith(fileExt);
      }
    };
  }
  
  public boolean isTwoPassSupport() {
    return twoPassSupport;
  }

  @Override
  public String toString() {
    return name;
  }
}
