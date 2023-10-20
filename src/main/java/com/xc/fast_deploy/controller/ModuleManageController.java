package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.Chunk;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleCenter;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.myException.*;
import com.xc.fast_deploy.myenum.DownloadModuleTypeEnum;
import com.xc.fast_deploy.service.common.ModuleCenterService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.FoldUtils;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.utils.SessionCookieUtils;
import com.xc.fast_deploy.vo.FoldDataVo;
import com.xc.fast_deploy.vo.module_vo.ModuleMangeVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.myenum.CodeUpTypeEnum;
import com.xc.fast_deploy.service.common.ModuleManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping(value = "/module")
@Slf4j
public class ModuleManageController {
  
  @Autowired
  private ModuleManageService manageService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleCenterService centerService;
  
  @Value("${file.storge.path.excelPath}")
  private String excelModuleFilePath;
  
  @Value("${file.storge.path.modelPath}")
  private String modelFilePath;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  @Value("${file.storge.path.moduleUploadPath}")
  private String moduleUploadPath;
  
  /**
   * 模块添加接口
   *
   * @param mangeVo       模块相关数据内容
   * @param codeExcelFile svnExcel文件
   * @return
   */
  @PostMapping(value = "/insert")
  public String addModuleManage(HttpServletRequest request,
                                ModuleManageParamVo mangeVo, MultipartFile codeExcelFile,
                                MultipartFile yamlFile) {
    String param = SessionCookieUtils.getJsonParam(request);
    if (StringUtils.isNotBlank(param)) {
      mangeVo = JSONObject.parseObject(param, ModuleManageParamVo.class);
    }
    log.info("调用添加模块,入参 manageVo: {},svnExcelFile: {}", JSONObject.toJSONString(mangeVo), codeExcelFile);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("模块保存失败");
    try {
      if (validate(mangeVo, codeExcelFile, yamlFile)) {
        ModuleUser userInfo = JwtUtil.getModuleUserInfo();
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_add",
            userInfo.getId(), Integer.valueOf(mangeVo.getEnvId()));
        synchronized (this) {
          if (manageService.saveAll(userInfo.getId(), mangeVo, codeExcelFile, yamlFile)) {
            responseDTO.success("模块创建成功");
          }
        }
      } else {
        responseDTO.fail("输入参数无法通过校验逻辑");
      }
    } catch (FileStoreException | SvnUrlNotExistException |
             ValidateExcetion | TransYaml2K8sVoException e) {
      responseDTO.fail(e.getMessage());
    }
    return JSONObject.toJSONString(responseDTO);
    
  }
  
  /**
   * 分片上传接口
   *
   * @param request
   * @param chunk
   * @return
   */
  @PostMapping(value = "/upload")
  public String webUploader(HttpServletRequest request, Chunk chunk) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.success();
    boolean multipartContent = ServletFileUpload.isMultipartContent(request);
    if (multipartContent) {
      MultipartFile file = chunk.getFile();
      if (file == null) {
        responseDTO.fail();
      } else {
        try {
          byte[] bytes = file.getBytes();
          StringBuilder sb = new StringBuilder();
          sb.append(moduleUploadPath);
          File contentFile = new File(sb.toString());
          if (!contentFile.exists()) {
            contentFile.mkdirs();
          }
          Path path = Paths.get(moduleUploadPath,
              chunk.getIdentifier() + "-" + chunk.getChunkNumber());
          //文件写入指定路径
          Files.write(path, bytes);
          if (chunk.getChunkNumber().equals(chunk.getTotalChunks())) {
            //合并文件
            log.info("合并文件");
            String folder = sb.toString();
            String pathFile = sb.append(chunk.getFilename()).toString();
            merge(pathFile, folder, chunk.getIdentifier());
            responseDTO.success(pathFile);
          }
        } catch (IOException e) {
          e.printStackTrace();
          responseDTO.fail();
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 合并文件夹下面的对应标识所有文件为一个文件
   *
   * @param targetFile
   * @param folder
   * @param identifier
   * @throws IOException
   */
  private static void merge(String targetFile, String folder, String identifier) throws IOException {
    File file = new File(targetFile);
    FileOutputStream outputStream = new FileOutputStream(file);
    FileChannel outputStreamChannel = outputStream.getChannel();
    File foldFile = new File(folder);
    File[] files = foldFile.listFiles();
    List<File> fileList = new ArrayList<>();
    if (files != null && files.length > 0) {
      for (File childFile : files) {
        if (childFile.getName().startsWith(identifier)) {
          fileList.add(childFile);
        }
      }
      //取文件名称的最后 - 字符后的数字作为依据排序
      fileList.sort((File o1, File o2) -> {
        String s1 = o1.getName().substring(o1.getName().lastIndexOf("-") + 1,
            o1.getName().length());
        String s2 = o2.getName().substring(o2.getName().lastIndexOf("-") + 1,
            o2.getName().length());
        if (Integer.valueOf(s1) > Integer.valueOf(s2)) {
          return 1;
        } else if (Integer.valueOf(s1) < Integer.valueOf(s2)) {
          return -1;
        } else {
          return 0;
        }
      });
      //合并数据文件
      ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
      for (File sonFile : fileList) {
        FileInputStream inputStream = new FileInputStream(sonFile);
        FileChannel fileInputStreamChannel = inputStream.getChannel();
        while (fileInputStreamChannel.read(byteBuffer) != -1) {
          byteBuffer.flip();
          outputStreamChannel.write(byteBuffer);
          byteBuffer.clear();
        }
        outputStream.flush();
        inputStream.close();
        fileInputStreamChannel.close();
        sonFile.delete();
      }
      outputStream.flush();
      outputStream.close();
      outputStreamChannel.close();
      log.info("合并文件成功:{}", targetFile);
    }
  }
  
  /**
   * 校验添加模块时参数的正确性
   *
   * @param mangeVo       其他参数
   * @param codeExcelFile svn_excel文件
   * @return
   */
  private boolean validate(ModuleManageParamVo mangeVo, MultipartFile codeExcelFile, MultipartFile yamlFile) {
    if (mangeVo != null
        && StringUtils.isNotBlank(mangeVo.getModuleType())
        && StringUtils.isNotBlank(mangeVo.getModuleName())
        && StringUtils.isNotBlank(mangeVo.getModuleContentName())
        && FoldUtils.judgeContent(mangeVo.getModuleContentName())
        && StringUtils.isNotBlank(mangeVo.getModuleProjectCode())
        && StringUtils.isNotBlank(mangeVo.getChargePerson())) {
      ModuleTypeEnum typeEnum = null;
      CodeUpTypeEnum codeUpTypeEnum = null;
      try {
        typeEnum = ModuleTypeEnum.getTypeByCode(Integer.valueOf(mangeVo.getModuleType()));
        Integer.valueOf(mangeVo.getCenterId());
        Integer.valueOf(mangeVo.getEnvId());
      } catch (NumberFormatException e) {
        return false;
      }
      if (typeEnum != null) {
        switch (typeEnum) {
          case GIT_SOURCE_CODE:
          case SVN_SOURCE_CODE:
            try {
              codeUpTypeEnum = CodeUpTypeEnum.getTypeByCode(Integer.valueOf(mangeVo.getCodeUpType()));
              Integer.valueOf(mangeVo.getCertificateId());
              if (StringUtils.isBlank(mangeVo.getCertificateId()) || codeUpTypeEnum == null) {
                return false;
              }
            } catch (NumberFormatException e) {
              return false;
            }
            if (StringUtils.isNotBlank(mangeVo.getCodeUpType()) && mangeVo.getCodeUpType().
                equals(CodeUpTypeEnum.UPLOAD_FILE.getCode().toString())) {
              if (codeExcelFile != null && !codeExcelFile.isEmpty()) {
                return true;
              }
            } else if (StringUtils.isNotBlank(mangeVo.getCodeUpType()) && mangeVo.getCodeUpType()
                .equals(CodeUpTypeEnum.TEXT_PUTIN.getCode().toString())) {
              List<ModulePackageParamVo> packageList = mangeVo.getPackageList();
              if (packageList.size() > 0) {
                Set<String> set1 = new HashSet<>();
                //校验非空和重复性校验
                for (ModulePackageParamVo packageParmVo : packageList) {
                  if (StringUtils.isBlank(packageParmVo.getContentName()) ||
                      StringUtils.isBlank(packageParmVo.getCodeUrl())) {
                    return false;
                  }
                  set1.add(packageParmVo.getContentName());
                }
                if (set1.size() == packageList.size()) {
                  return true;
                }
                set1.clear();
              }
            }
          case SVN_AUTO_UP_CODE:
          case GIT_AUTO_UP_SOURCE_CODE:
            //凭证id验证
            try {
              Integer.valueOf(mangeVo.getCertificateId());
              if (StringUtils.isBlank(mangeVo.getCertificateId())) {
                return false;
              }
            } catch (NumberFormatException e) {
              return false;
            }
            if (StringUtils.isNotBlank(mangeVo.getSvnAutoUpUrl())) {
              return true;
            }
            break;
          case PROJECT_PACKAGE:
            if (mangeVo.getProgramFileNameList() != null && mangeVo.getProgramFileNameList().size() > 0) {
              return true;
            }
          case YAML_DEPLOY_TYPE:
            //yaml文件
            if (yamlFile != null && !yamlFile.isEmpty()) {
              return true;
            }
            break;
          default:
            break;
        }
      }
    }
    return false;
  }
  
  /**
   * excel 模板文件下载
   * type: 1 为单模块模板下载 2 为zip模板下载
   *
   * @param response
   */
  @GetMapping(value = "/downloadFile")
  public void downloadExcelFile(Integer type, HttpServletResponse response) {
    log.info("访问文件下载接口");
    File file = null;
    String filename = null;
    if (type != null) {
      DownloadModuleTypeEnum typeEnum = DownloadModuleTypeEnum.getEnumByCode(type);
      if (typeEnum != null) {
        switch (typeEnum) {
          case MODULE_MANAGE_SVN_SINGLE:
            file = new File(excelModuleFilePath);
            filename = "excel_module.xlsx";
            break;
          case MODULE_MANAGE_SVN_BATCH:
            file = new File(modelFilePath);
            filename = "model.zip";
            break;
          default:
            break;
        }
      }
    }
    if (file != null && file.exists() && StringUtils.isNotBlank(filename)) {
      response.setContentType("application/force-download");// 设置强制下载不打开
      response.addHeader("Content-Disposition", "attachment;fileName=" + filename);// 设置文件名
      FileInputStream inputStream = null;
      try {
        inputStream = new FileInputStream(file);
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
        log.info("excel 文件下载成功");
      } catch (FileNotFoundException e) {
        log.error("文件下载出现错误fileNoteFound", e);
      } catch (IOException e) {
        log.error("文件下载出现错误", e);
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
  
  /**
   * 分页查询所有的模块详细信息
   */
  @GetMapping(value = "/pageAll")
  public String getAllModulePage(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
      ModuleManageSelectParamVo manageSelectParamVo) {
    log.info("模块查询入参: pageNum: {}, pageSize: {}, param: {}", pageNum,
        pageSize, JSONObject.toJSONString(manageSelectParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal()));
    boolean flag = false;
    if (envPermissionMap.size() > 0) {
      if (manageSelectParamVo.getEnvId() != null) {
        if (envPermissionMap.containsKey(manageSelectParamVo.getEnvId())) {
          flag = true;
        }
      } else {
        manageSelectParamVo.setEnvIds(envPermissionMap.keySet());
        flag = true;
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
    if (StringUtils.isNotBlank(manageSelectParamVo.getModuleName())) {
      manageSelectParamVo.setModuleName(manageSelectParamVo.getModuleName().trim());
    }
    MyPageInfo<ModuleMangeVo> mangeVoMyPageInfo = manageService.getAllModuleDTOBySelect(pageNum, pageSize, manageSelectParamVo);
    if (mangeVoMyPageInfo.getList() == null || mangeVoMyPageInfo.getList().size() <= 0) {
      responseDTO.fail("未查询到任何数据");
    } else {
      responseDTO.success(mangeVoMyPageInfo);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据module id 获取该模块的信息 用来去修改数据
   *
   * @param moduleId
   * @return
   */
  @GetMapping(value = "/getOne")
  public String getModuleById(Integer moduleId) {
    log.info("getOne 访问入参: moduleId: {}", moduleId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到相关数据");
    if (moduleId != null) {
      ModuleManage moduleManage = manageService.selectById(moduleId);
      if (moduleManage != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, null,
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleManage.getEnvId());
        //获取模块对应的数据
        ModuleManageDTO moduleManageDTO = manageService.selectInfoById(moduleId);
        responseDTO.success(moduleManageDTO);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更新模块信息,如果涉及到文件的重新上传将会把之前的所有package记录覆盖掉
   *
   * @param
   * @return
   */
  @PostMapping(value = "/manage/updateInfo")
  public String updateModuleManage(Integer moduleId, String chargePerson, String moduleName,
                                   String chargeTelephone, String officalChargePerson,
                                   String officalChargeTelephone,
                                   Integer moduleType, String svnAutoUpUrl) {
    //log.info("传入参数为: moduleId:{},chargePerson:{},moduleName:{},chargeTelephone:{}," +
    //                "officalChargePerson:{},officalChargeTelephone:{},moduleType:{},svnUrlPath:{}",
    //        moduleId, chargePerson, moduleName, chargeTelephone, officalChargePerson,
    //        officalChargeTelephone, moduleType, svnAutoUpUrl);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更改失败");
    //表示修改的时候重新上传了excel文件,
    if (moduleId != null) {
      ModuleManage moduleManage = manageService.selectById(moduleId);
      if (moduleManage != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_mange_update",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
            moduleManage.getEnvId());
        ModuleManage manage = new ModuleManage();
        manage.setModuleName(moduleName);
        manage.setChargePerson(chargePerson);
        manage.setChargeTelephone(chargeTelephone);
        manage.setOfficalChargeTelephone(officalChargeTelephone);
        manage.setOfficalChargePerson(officalChargePerson);
        manage.setId(moduleId);
        manage.setCertificateId(moduleManage.getCertificateId());
        manage.setCenterId(moduleManage.getCenterId());
        manage.setModuleContentName(moduleManage.getModuleContentName());
        try {
          if (manageService.updateBySelective(manage, moduleType, svnAutoUpUrl) > 0) {
            responseDTO.success("更改成功");
          }
        } catch (SVNException e) {
          responseDTO.fail("svn地址不存在: " + e.getMessage());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 删除模块信息
   *
   * @param moduleId
   * @return
   */
  @PostMapping(value = "/deleteModule")
  public String deleteModuleManage(@RequestParam(value = "moduleId") Integer moduleId) {
    log.info("删除模块入参: " + moduleId);
    //物理文件存储直接删除 //表字段数据逻辑删除
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败!");
    if (moduleId != null) {
      ModuleManage moduleManage = manageService.selectById(moduleId);
      if (moduleManage != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_delete",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleManage.getEnvId());
        try {
          if (manageService.deleteInfoById(moduleId, moduleManage)) {
            responseDTO.success("删除成功");
          }
        } catch (DeployIsOnlineExcetion | NotDeleteOkException e) {
          responseDTO.fail(e.getMessage());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 批量删除模块
   *
   * @param moduleIds
   * @return
   */
  @PostMapping(value = "/batchDeleteModule")
  public String batchDeleteMdoule(Integer[] moduleIds) {
    log.info("批量删除模块入参: {}", Arrays.toString(moduleIds));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败!");
    if (moduleIds.length > 0) {
      List<ModuleManage> moduleManages = manageService.selectByIds(moduleIds);
      if (moduleManages != null && moduleManages.size() > 0) {
        PermissionJudgeUtils.judgeUserPermission(userService, "module_manage_delete",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
            moduleManages.get(0).getEnvId());
        Map<String, String> resultMap = new HashMap<>();
        for (ModuleManage moduleManage : moduleManages) {
          try {
            if (manageService.deleteInfoById(moduleManage.getId(), moduleManage)) {
              resultMap.put(moduleManage.getModuleName(), "删除成功");
            } else {
              resultMap.put(moduleManage.getModuleName(), "删除失败");
            }
          } catch (DeployIsOnlineExcetion | NotDeleteOkException e) {
            resultMap.put(moduleManage.getModuleName(), e.getMessage());
          }
        }
        responseDTO.success(resultMap);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  //获取模块文件夹的全部内容
  @GetMapping(value = "/getFoldersByModuleId")
  public String getAllFolders(Integer moduleId) {
    log.info("getFoldersByModuleId 入参: moduleId: " + moduleId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("无法获取到内容");
    if (moduleId != null) {
      ModuleManage moduleManage = manageService.selectById(moduleId);
      if (moduleManage != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, null,
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleManage.getEnvId());
        FoldDataVo foldDataVo = manageService.getAllManageFolders(moduleId);
        if (foldDataVo != null && StringUtils.isNotBlank(foldDataVo.getName())) {
          responseDTO.success(foldDataVo);
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 定位模块dockerfile或者k8s文件夹所在的位置
   */
  @GetMapping(value = "/getFilePathByModuleId")
  public String getFilePath(Integer moduleId, Integer type) {
    log.info("getFoldersByModuleId 入参: moduleId: {}, type:{}", moduleId, type);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("无法获取到内容");
    if (moduleId != null && type != null) {
      ModuleManage moduleManage = manageService.selectById(moduleId);
      if (moduleManage != null) {
        String filePath = manageService.getFilePath(moduleId, type);
        if (StringUtils.isNotBlank(filePath)) {
          responseDTO.success(filePath);
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据centerId获取该中心下的所有模块(未添加到job中的模块)
   *
   * @param centerId
   * @return
   */
  @GetMapping(value = "/getAllModuleByCenterId")
  public String getAllModuleByCenterId(@RequestParam(value = "centerId") Integer centerId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到任何数据");
    if (centerId != null) {
      ModuleCenter center = centerService.selectById(centerId);
      if (center != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, null,
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), center.getEnvId());
        List<ModuleManage> manageList = manageService.selectAllNotInJobById(centerId);
        if (manageList != null && manageList.size() > 0) {
          responseDTO.success(manageList);
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 迁移某个环境的模块到另外一个环境中去
   *
   * @param moduleIds
   * @param srcEnvId
   * @param desEnvId
   * @return
   */
  @PostMapping(value = "/copyModule2Other")
  public String copyModuleToOtherModuler(ModuleManage manage, Integer[] moduleIds, Integer srcEnvId, Integer desEnvId, String projectCode) {
    log.info("平移模块入参: moduleIds:{} srcEnvId:{} desEnvId:{} hProjectCode",
        Arrays.toString(moduleIds), srcEnvId, desEnvId, projectCode);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("迁移失败");
    if (moduleIds != null && srcEnvId != null
        && desEnvId != null
        && moduleIds.length > 0
        && StringUtils.isNotBlank(projectCode)) {
      //首先需要验证用户是否拥有两个环境的权限
      String userIdFromToken = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
      PermissionJudgeUtils.judgeUserPermission(userService,
          "module_manage_migration", userIdFromToken, srcEnvId);
      Map<String, String> stringMap;
      //防止重复提交的请求造成重复数据
      synchronized (this) {
        stringMap = manageService.copyModule2OtherEnv(Arrays.asList(moduleIds),
            srcEnvId, desEnvId, userIdFromToken, projectCode);
      }
      if (stringMap.size() > 0) {
        responseDTO.success(stringMap);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 下拉当前配置好的所有发布的模块信息
   *
   * @param envId
   * @param response
   */
  @GetMapping(value = "/export")
  public void downloadManageFile(Integer envId, HttpServletResponse response) {
    //权限验证
    PermissionJudgeUtils.judgeUserPermission(userService, "publish_manage_module_export",
        JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), envId);
    //获取数据形成文件
    File excelFile = manageService.genManageFile(envId);
    //输出文件
    response.setContentType("application/force-download");// 设置强制下载不打开
    response.addHeader("Content-Disposition", "attachment;fileName=" +
        excelFile.getName());// 设置文件名
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(excelFile);
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();
    } catch (FileNotFoundException e) {
      log.error("文件下载出现错误fileNoteFound", e);
    } catch (IOException e) {
      log.error("文件下载出现错误", e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
