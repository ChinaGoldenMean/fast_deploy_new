package com.xc.fast_deploy.utils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MyZipUtils {
  
  public static void getFileListFromFile(File file, List<File> files) {
    if (!file.exists()) {
      return;
    }
    if (file.isDirectory()) {
      File[] listFiles = file.listFiles();
      if (listFiles != null && listFiles.length > 0) {
        for (File childFile : listFiles) {
          getFileListFromFile(childFile, files);
        }
      }
    } else {
      files.add(file);
    }
  }
  
  public static void zipAllFile(File sourceFile, File outputFile) throws IOException {
    ZipOutputStream zipOutputStream = null;
    try {
      zipOutputStream = new ZipOutputStream(new FileOutputStream(outputFile));
      compress(zipOutputStream, sourceFile, sourceFile.getName());
    } finally {
      if (zipOutputStream != null) {
        try {
          zipOutputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public static void compress(ZipOutputStream outputStream,
                              File sourceFile, String fileName) throws IOException {
    if (sourceFile.isDirectory()) {
      File[] files = sourceFile.listFiles();
      if (files != null && files.length > 0) {
        for (File file : files) {
          compress(outputStream, file, fileName + "/" + file.getName());
        }
      } else {
        outputStream.putNextEntry(new ZipEntry(fileName + "/"));
      }
    } else {
      if (!sourceFile.exists()) {
        outputStream.putNextEntry(new ZipEntry("/"));
        outputStream.closeEntry();
      } else {
        outputStream.putNextEntry(new ZipEntry(fileName));
        FileInputStream inputStream = new FileInputStream(sourceFile);
        
        byte[] buf = new byte[1024];
        int len;
        
        while ((len = inputStream.read(buf)) != -1) {
          outputStream.write(buf, 0, len);
        }
        outputStream.closeEntry();
        inputStream.close();
      }
    }
  }
  
}
