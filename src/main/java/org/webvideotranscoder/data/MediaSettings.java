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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MediaSettings {

  private final String uid = UUID.randomUUID().toString();

  private long duration = -1;
  private float fps = -1;
  private int videoQuality = 8;
  private int audioQuality = 3;
  private Resolution resolution = Resolution.ORIGINAL;
  private boolean twoPassEncoding = false;

  public String getUid() {
    return uid;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public float getFps() {
    return fps;
  }

  public void setFps(float fps) {
    this.fps = fps;
  }

  public long getFrames() {
    long seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS);
    return (long) (fps * seconds);
  }

  public int getVideoQuality() {
    return videoQuality;
  }

  public void setVideoQuality(int videoQuality) {
    this.videoQuality = videoQuality;
  }

  public int getAudioQuality() {
    return audioQuality;
  }

  public void setAudioQuality(int audioQuality) {
    this.audioQuality = audioQuality;
  }

  public Resolution getResolution() {
    return resolution;
  }

  public void setResolution(Resolution resolution) {
    this.resolution = resolution;
  }

  public boolean isTwoPassEncoding() {
    return twoPassEncoding;
  }

  public void setTwoPassEncoding(boolean twoPassEncoding) {
    this.twoPassEncoding = twoPassEncoding;
  }

  public String toString() {
    return String.format("duration: %d ms, fps: %f", duration, fps);
  }
}
