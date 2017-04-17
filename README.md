# Web Video Transcoder

## Licence

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

## Copyright

Copyright © 2016, 2017 Vincent Gravade

## Third-parties licences


### OpenJDK

GNU General Public License, version 2, with the Classpath Exception.

<http://openjdk.java.net/legal/>

### FFmpeg

FFmpeg is licensed under the GNU Lesser General Public License (LGPL)
version 2.1 or later. However, FFmpeg incorporates several optional
parts and optimizations that are covered by the GNU General Public
License (GPL) version 2 or later. If those parts get used the GPL
applies to all of FFmpeg.

<https://ffmpeg.org/legal.html>

### Application icon

* src/main/resources/icons/48px-Video-x-generic.png
* src/main/resources/icons/48px-Video-x-generic.ico

The application icon came from the Tango Project.

The Tango base icon theme is released to the Public Domain.

<http://tango.freedesktop.org/Tango_Desktop_Project>


## Requirements

(Open)JDK is required for building (and running) Web Video Transcoder.

Java sources are compatibles with Java 1.5 version.

FFmpeg is needed by Web Video Transcoder.

Inno Setup (can run with wine) is needed if you want build a windows installer.

## Build

*Ant* build.xml is under "scripts/" folder.

### Example

```sh
cd scripts
ant run
```

### Main *ant* targets

- **clean**: delete "build" ant "dist" folders
- **compile**: compile java sources under "build/classes" folder
- **jar** (default): generate executable JAR archive under "build/jar" folder
- **run**: compile and run application
- **dist-jar**: generate executable JAR archive under "dist/" folder
- **dist-src**: generate a distributable source archive under "dist/" folder
- **dist-deb**: generate a DEB package under "dist/" folder
- **dist-win**: generate a ZIP file embedding windows executable and ffmpeg executables under "dist/" folder (and optionnaly a windows installer, requires Inno Setup)
- **dist-all**: execute "dist-deb", "dist-jar", "dist-src" and "dist-win" targets
- **website**: generate "webvideotranscoder.org" website
- **publish**: execute "dist-all" and "website" targets
