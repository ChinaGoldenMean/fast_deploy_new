package com.xc.fast_deploy.utils;

import com.xc.fast_deploy.vo.FoldDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FoldUtils {
  
  public static final String SEP = "/";
  public static final String SSH = "ssh";
  public static final String CONTENT_REGEX = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\-]+$";
  public static final String YAML_NAME_REGEX = "";
  
  /**
   * 获取file文件目录下的所有子文件目录结构
   *
   * @param file
   * @param dataVo
   * @param storgePrefix
   */
  public static void getAllFoldJson(File file, FoldDataVo dataVo, String storgePrefix, int count) {
    count++;
    if (file != null && file.exists()) {
      dataVo.setName(file.getName());
      String name = file.getName();
      dataVo.setAbsolutePath(file.getAbsolutePath().replace(storgePrefix, ""));
      if (file.isDirectory()) {
        dataVo.setDir(true);
        if (count <= 5) {
          if (StringUtils.isNotBlank(name) && !name.contains(".svn") && !name.contains(".git")) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
              List<FoldDataVo> dataVos = new ArrayList<>();
              for (int i = 0; i < files.length; i++) {
                FoldDataVo vo = new FoldDataVo();
                getAllFoldJson(files[i], vo, storgePrefix, count);
                dataVos.add(vo);
              }
              dataVo.setChildren(dataVos);
            }
            
          }
        } else {
          return;
        }
      } else {
        return;
      }
    }
  }
  
  /**
   * 删除文件 删除文件夹及子文件夹的内容
   */
  public static boolean deleteFolders(String path) {
    File file = new File(path);
    if (!file.exists()) {
      return true;
    }
    if (file.isFile()) {
      return file.delete();
    }
    File[] files = file.listFiles();
    if (files != null && files.length > 0) {
      for (File f : files) {
        if (f.isFile()) {
          if (!f.delete()) {
            return false;
          }
        } else {
          if (!deleteFolders(f.getAbsolutePath())) {
            return false;
          }
        }
      }
    }
    return file.delete();
  }
  
  public static boolean deleteFoldersByPath(String path) {
    File file = new File(path);
    if (!file.exists()) {
      return true;
    }
    if (file.isFile()) {
      try {
        FileInputStream fis = new FileInputStream(file);
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
      if (file.canWrite() && file.delete()) {
        return true;
      } else {
        return false;
      }
    }
    File[] files = file.listFiles();
    if (files != null && files.length > 0) {
      for (File f : files) {
        if (f.isFile()) {
          try {
            FileInputStream fis = new FileInputStream(f);
            fis.close();
          } catch (IOException e) {
            e.printStackTrace();
            return false;
          }
          if (!f.canWrite() || !f.delete()) {
            return false;
          }
        } else {
          if (!deleteFoldersByPath(f.getAbsolutePath())) {
            return false;
          }
        }
      }
    }
    return file.delete();
  }
  
  /**
   * 目录名称只能字母或者数字且不能以_ .开头 _ 结尾的正则匹配
   *
   * @param contentName
   * @return
   */
  public static boolean judgeContent(String contentName) {
    Pattern pattern = Pattern.compile(CONTENT_REGEX);
    Matcher matcher = pattern.matcher(contentName);
    return matcher.find();
  }
  
  /**
   * 查找指定file的文件位置
   *
   * @param file
   * @param storgePrefix
   * @param fileName
   * @param foldDataVo
   */
  public static void getFilePath(File file, String storgePrefix, String contentName, String fileName, FoldDataVo foldDataVo) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        //下一层直接获取
        for (File childFile : files) {
          if (!childFile.isDirectory() && childFile.getName().equals(fileName)) {
            foldDataVo.setAbsolutePath(childFile.getAbsolutePath().replace(storgePrefix, ""));
            return;
          }
        }
        //下一层根据条件判断到下下层获取
        for (File childFile : files) {
          if (childFile.isDirectory()) {
            File[] files2 = childFile.listFiles();
            if (childFile.getName().equals(contentName) || contentName.contains(childFile.getName())) {
              getSonFile(files2, foldDataVo, fileName, storgePrefix, contentName);
              if (StringUtils.isNotBlank(foldDataVo.getAbsolutePath())) {
                return;
              }
            }
          }
        }
        for (File childFile : files) {
          if (childFile.isDirectory()) {
            File[] files2 = childFile.listFiles();
            getSonFile(files2, foldDataVo, fileName, storgePrefix, contentName);
          }
        }
      }
    }
  }
  
  private static void getSonFile(File[] files2, FoldDataVo foldDataVo, String fileName, String storgePrefix, String contentName) {
    if (files2 != null && files2.length > 0) {
      for (File sonFile : files2) {
        if (sonFile.getName().equals(fileName)) {
          foldDataVo.setAbsolutePath(sonFile.getAbsolutePath().
              replace(storgePrefix, ""));
          return;
        }
      }
      for (File sonFile : files2) {
        if (sonFile.getName().equals(contentName) || contentName.contains(sonFile.getName())) {
          File[] files = sonFile.listFiles();
          if (files != null && files.length > 0) {
            for (File gFile : files) {
              if (gFile.getName().equals(fileName)) {
                foldDataVo.setAbsolutePath(gFile.getAbsolutePath().
                    replace(storgePrefix, ""));
                log.info("找到对应数据:{}", gFile.getAbsolutePath());
                return;
              }
            }
          }
        }
      }
      for (File sonFile : files2) {
        if (sonFile.isDirectory()) {
          File[] files3 = sonFile.listFiles();
          if (files3 != null && files3.length > 0) {
            for (File grandFile : files3) {
              if (grandFile.getName().equals(fileName)) {
                foldDataVo.setAbsolutePath(grandFile.getAbsolutePath().
                    replace(storgePrefix, ""));
                return;
              }
            }
            for (File grandFile : files3) {
              if (grandFile.isDirectory()) {
                File[] files4 = grandFile.listFiles();
                if (files4 != null && files4.length > 0) {
                  for (File grandGrandFile : files4) {
                    if (grandGrandFile.getName().equals(fileName)) {
                      foldDataVo.setAbsolutePath(grandGrandFile.getAbsolutePath().
                          replace(storgePrefix, ""));
                      return;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * 复制文件 文件夹到另外一个文件夹中
   *
   * @param srcFile
   * @param descFile
   * @throws IOException
   */
  public static void copyFileDirToPath(File srcFile, File descFile) throws IOException {
    if (srcFile != null && descFile != null) {
//            log.info("文件转移: srcFileName:{},descFileName:{}", srcFile.getName(), descFile.getName());
//            descFile.mkdirs()
      if (!descFile.exists()) {
        boolean mkdirs = descFile.mkdirs();
      }
      //获取源文件目录的所有文件
      if (srcFile.isDirectory()) {
        File[] files = srcFile.listFiles();
        if (files != null && files.length > 0) {
          for (File file : files) {
            StringBuilder sb = new StringBuilder();
            sb.append(descFile.getAbsolutePath()).append(SEP).append(file.getName());
            String descRealFile = sb.toString();
            if (file.isDirectory()) {
              copyFileDirToPath(file, new File(descRealFile));
            } else {
              copyFiletoFile(file, new File(descRealFile));
            }
          }
        }
      }
    }
  }
  
  /**
   * 文件的复制
   *
   * @param srcFile
   * @param descFile
   * @throws IOException
   */
  public static void copyFiletoFile(File srcFile, File descFile) throws IOException {
    FileChannel inputChannel = null;
    FileChannel outputChannel = null;
    if (srcFile != null && descFile != null && srcFile.isFile()) {
      try {
        inputChannel = new FileInputStream(srcFile).getChannel();
        outputChannel = new FileOutputStream(descFile).getChannel();
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
      } finally {
        if (inputChannel != null) {
          inputChannel.close();
        }
        if (outputChannel != null) {
          outputChannel.close();
        }
      }
    }
  }
  
}
