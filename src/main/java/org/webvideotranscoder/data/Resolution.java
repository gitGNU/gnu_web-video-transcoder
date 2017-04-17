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

import org.webvideotranscoder.App;

public enum Resolution {
  
  ORIGINAL(App.RB.getString("resolution.original"), -1, -1),
  NHD("nHD (640 × 360)", 640, 360),
  FWVGA("Full WVGA (854 × 480)", 854, 480),
  QHD("qHD (960 × 540)", 960, 540),
  HD("HD (1280 × 720)", 1280, 720),
  FHD("Full HD (1920 × 1080)", 1920, 1080),
  UHD4K("4K UHD (3840 × 2160)", 3840, 2160);

  private final String name;
  private final int width;
  private final int height;

  private Resolution(String name, int width, int height) {
    this.name = name;
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public String toString() {
    return name;
  }
}
