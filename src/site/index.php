<?php

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

session_start();
header('Cache-control: private'); // IE 6 FIX

$lang = substr($_SERVER['HTTP_ACCEPT_LANGUAGE'], 0, 2);

switch ($lang) {
  case 'fr':
      $lang_file = 'lang.fr.php';
      $fmt = numfmt_create( $lang , NumberFormatter::DECIMAL );
      break;

  default:
    $lang_file = 'lang.en.php';
    $fmt = numfmt_create( 'en', NumberFormatter::DECIMAL );
}

include_once $lang_file;

function formatbytes($file) {
   global $fmt, $lang;
   $units = array('', 'Ki', 'Mi', 'Gi', 'Ti');
   $filesize = filesize("$_SERVER[DOCUMENT_ROOT]/".$file);
   if($filesize <= 0){
      return '?';
   } else{
      $bytes = max($filesize, 0);
      $pow = floor(($bytes ? log($bytes) : 0) / log(1024)); 
      $pow = min($pow, count($units) - 1); 
      $bytes /= (1 << (10 * $pow)); 
      return $fmt->format(round($bytes, 1)).' '.$units[$pow].$lang['UNIT_BYTE'];
   }
} 

?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">

<html dir="ltr" lang="<?php echo $lang['LANGUAGE']; ?>">
<head>
  <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
  <meta content="Vincent Gravade" name="Author">
  <meta content="width=device-width" name="viewport">
  <link href="data:;base64,iVBORw0KGgo=" rel="icon">

  <title>[[appname]]</title>
</head>

<body>
  <h1><?php echo $lang['APP_NAME']; ?></h1>
  <p><?php echo $lang['DESCRIPTION']; ?></p>
  <p><img alt="<?php echo $lang['SCREENSHOT_ALT']; ?>" src="<?php echo $lang['SCREENSHOT_FILE']; ?>" width="<?php echo $lang['SCREENSHOT_WIDTH']; ?>" height="<?php echo $lang['SCREENSHOT_HEIGHT']; ?>" ></p>
  <h2><?php echo $lang['LICENSE_HEAD']; ?></h2>
  <?php echo $lang['LICENSE_CONTENT']; ?>

  <h2><?php echo $lang['DOWNLOAD_HEAD']; ?></h2>
  <p><?php echo $lang['APP_NAME']; ?> 0.4.0</p>
  <ul>
    <li>
       <a href="[[wininstaller]]"><?php echo $lang['DOWNLOAD_WIN_INSTALLER']; ?></a> (<?php echo formatBytes("[[wininstaller]]"); ?>)
       <small>— <?php echo $lang['FFMPEG_INCLUDED']; ?></small>
    </li>

    <li>
       <a href="[[winportable]]"><?php echo $lang['DOWNLOAD_WIN_PORTABLE']; ?></a> (<?php echo formatBytes("[[winportable]]"); ?>)
       <small>— <?php echo $lang['FFMPEG_INCLUDED']; ?></small>
    </li>

    <li>
       <a href="[[deb]]"><?php echo $lang['DOWNLOAD_GNULINUX_DEB']; ?></a> (<?php echo formatBytes("[[deb]]"); ?>)
    </li>

    <li>
       <a href="[[jar]]"><?php echo $lang['DOWNLOAD_JAR']; ?></a> (<?php echo formatBytes("[[jar]]"); ?>)
    </li>

    <li>
       <a href="[[src]]"><?php echo $lang['DOWNLOAD_SRC']; ?></a> (<?php echo formatBytes("[[src]]"); ?>)
    </li>
  </ul>
  <p><?php echo $lang['JAVA_REQUIREMENTS']; ?></p>
</body>
</html>
