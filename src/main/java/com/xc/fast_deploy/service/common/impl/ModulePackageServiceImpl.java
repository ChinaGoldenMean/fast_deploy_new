package com.xc.fast_deploy.service.common.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.ModuleCenterMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleCertificateMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleManageMapper;
import com.xc.fast_deploy.dao.master_dao.ModulePackageMapper;
import com.xc.fast_deploy.dto.CodeUpdateInfoDTO;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.dto.module.ModulePackageCertDTO;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.dto.module.permission.ModuleSubPackageDTO;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModulePackage;
import com.xc.fast_deploy.model.master_model.example.ModulePackageExample;
import com.xc.fast_deploy.myException.GitOwnException;
import com.xc.fast_deploy.myException.NotDeleteOkException;
import com.xc.fast_deploy.myException.SvnUpdateException;
import com.xc.fast_deploy.myException.SvnUrlNotExistException;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.service.common.ModuleCertificateService;
import com.xc.fast_deploy.service.common.ModulePackageService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.utils.*;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.code_utils.GitUtils;
import com.xc.fast_deploy.utils.code_utils.SvnUtils;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.vo.module_vo.ModulePackageVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.yeauty.pojo.Session;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xc.fast_deploy.utils.FoldUtils.SEP;

@Service
@Slf4j
public class ModulePackageServiceImpl extends BaseServiceImpl<ModulePackage, Integer> implements ModulePackageService {
  @Autowired
  private ModuleCertificateService certificateService;
  @Autowired
  private ModulePackageMapper packageMapper;
  
  @Autowired
  private ModuleCertificateMapper certificateMapper;
  
  @Autowired
  private ModuleManageMapper manageMapper;
  
  @Autowired
  private ModuleCenterMapper centerMapper;
  
  @Autowired
  private SyncService syncService;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  @Value("${file.storge.path.moduleUploadPath}")
  private String moduleUploadPath;
  
  @PostConstruct
  private void init() {
    super.init(packageMapper);
  }
  
  /**
   * 批量插入package数据
   *
   * @param list
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertAll(List<ModulePackage> list) {
    int insertNum = 0;
    if (list != null && list.size() > 0) {
      Integer first = packageMapper.selectIdFirst();
      for (int i = 0; i < list.size(); i++) {
        list.get(i).setId(first++);
      }
      packageMapper.updateVal(first - 1);
      insertNum = packageMapper.insertBatch(list);
    }
    return insertNum;
  }
  
  /**
   * 根据模块id查询package的所有内容
   *
   * @param moduleId
   * @return
   */
  @Override
  public List<ModulePackageDTO> selectByModuleId(Integer moduleId) {
    return packageMapper.selectPackageInfoByModuleId(moduleId);
  }
  
  /**
   * 获取某个svn地址对应的更新日志信息
   *
   * @return
   */
  @Override
  public List<CodeUpdateInfoDTO> getCodeUpdateInfo(Integer packageId) {
    List<CodeUpdateInfoDTO> codeUpdateInfoDTOS = new ArrayList<>();
    ModulePackageVo packageVo = packageMapper.selectCertInfoById(packageId);
    if (packageVo != null && StringUtils.isNotBlank(packageVo.getUsername()) && StringUtils.isNotBlank(packageVo.getPassword())
        && StringUtils.isNotBlank(packageVo.getCodeUrl())) {
      ModuleTypeEnum typeByCode = ModuleTypeEnum.getTypeByCode(packageVo.getCodeType());
      if (typeByCode != null) {
        switch (typeByCode) {
          case SVN_AUTO_UP_CODE:
          case SVN_SOURCE_CODE:
            SVNRepository svnRepository = SvnUtils.authSvn(packageVo.getCodeUrl(), packageVo.getUsername(),
                EncryptUtil.decrypt(packageVo.getPassword()));
            if (svnRepository != null) {
              codeUpdateInfoDTOS = SvnUtils.getUpdateInfo(svnRepository, packageVo.getCodeUrl());
            }
            break;
          case GIT_SOURCE_CODE:
            try {
              codeUpdateInfoDTOS = GitUtils.gitLog(storgePrefix + packageVo.getPackagePathName());
            } catch (IOException | GitAPIException e) {
              e.printStackTrace();
            }
            break;
        }
      }
    }
    return codeUpdateInfoDTOS;
  }
  
  /**
   * 根据模块id删除对应的package内容
   *
   * @param moduleId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteByModuleId(Integer moduleId) {
    ModulePackageExample packageExample = new ModulePackageExample();
    packageExample.createCriteria().andModuleIdEqualTo(moduleId);
    return packageMapper.deleteByExample(packageExample) > 0;
  }
  
  /**
   * 添加一条package信息
   *
   * @param modulePackage
   * @param moduleId
   * @param moduleType
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean insertInfo(ModuleSubPackageDTO modulePackage) {
    //验证新添加的数据是否重复,也就是
    Integer moduleId = modulePackage.getModuleId();
    Integer moduleType = modulePackage.getModuleType();
    ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleId);
    if (moduleManageDTO != null && ModuleTypeEnum.getTypeByCode(moduleManageDTO.getModuleType()) != null) {
      ModuleTypeEnum typeEnum = ModuleTypeEnum.getTypeByCode(moduleManageDTO.getModuleType());
      if (typeEnum != null) {
        StringBuilder filePrefix = new StringBuilder();
        filePrefix.append(moduleManageDTO.getCenterPath()).append(SEP)
            .append(moduleManageDTO.getModuleContentName()).append(SEP);
        switch (typeEnum) {
          case GIT_SOURCE_CODE:
            if (modulePackage != null && StringUtils.isNotBlank(modulePackage.getCodeUrl()) &&
                StringUtils.isNotBlank(modulePackage.getContentName())) {
              ModulePackageExample packageExample = new ModulePackageExample();
              packageExample.createCriteria().andContentNameEqualTo(modulePackage.getContentName()).andModuleIdEqualTo(moduleId);
              List<ModulePackage> packages = packageMapper.selectByExample(packageExample);
              if (packages == null || packages.size() == 0) {
                ModuleCertificate certificate = certificateMapper.selectCertByModuleId(moduleId);
                if (certificate != null) {
                  String reversion = null;
                  try {
                    reversion = GitUtils.getUrlReversion(modulePackage.getCodeUrl(),
                        certificate.getUsername(),
                        EncryptUtil.decrypt(certificate.getPassword()));
                    if (StringUtils.isNotBlank(reversion)) {
                      //到这里统一验证通过
                      ModulePackage mPackage = new ModulePackage();
                      mPackage.setCodeUrl(modulePackage.getCodeUrl());
                      mPackage.setContentName(modulePackage.getContentName());
                      mPackage.setCreateTime(new Date());
                      mPackage.setModuleId(moduleId);
                      mPackage.setCodeReversion("-1");
                      mPackage.setCodeType(ModuleTypeEnum.GIT_SOURCE_CODE.getModuleTypeCode());
                      mPackage.setPackagePathName(filePrefix.append(
                          modulePackage.getContentName()).toString());
                      String gitBranch = modulePackage.getGitBranch();
                      if (StringUtils.isBlank(gitBranch)) {
                        gitBranch = "master";
                      }
                      mPackage.setGitBranch(gitBranch);
                      //插入modulePackage相关数据
                      packageMapper.insertSelective(mPackage);
                      List<ModulePackage> modulePackages = new ArrayList<>();
                      modulePackages.add(mPackage);
                      //这个时候需要异步拉取代码到指定目录中
                      syncService.checkoutAllAsync(modulePackages, certificate);
                      return true;
                    }
                  } catch (GitAPIException e) {
                    throw new SvnUpdateException(e.getMessage());
                  }
                }
              }
            }
            break;
          case SVN_SOURCE_CODE:
            if (modulePackage != null && StringUtils.isNotBlank(modulePackage.getCodeUrl()) &&
                StringUtils.isNotBlank(modulePackage.getContentName())) {
              //判断contentName不是重复添加的
              ModulePackageExample packageExample = new ModulePackageExample();
              packageExample.createCriteria().andContentNameEqualTo(modulePackage
                  .getContentName()).andModuleIdEqualTo(moduleId);
              List<ModulePackage> packages = packageMapper.selectByExample(packageExample);
              if (packages == null || packages.size() == 0) {
                //然后验证svnUrl的正确性
                ModuleCertificate certificate = certificateMapper.selectCertByModuleId(moduleId);
                if (certificate != null) {
                  Long urlReversion = null;
                  try {
                    urlReversion = SvnUtils.getUrlReversion(modulePackage.getCodeUrl(), certificate.getUsername(),
                        EncryptUtil.decrypt(certificate.getPassword()));
                  } catch (SVNException e) {
                    throw new SvnUpdateException(e.getMessage());
                  }
                  if (urlReversion != null) {
                    //到这里统一验证通过
                    ModulePackage mPackage = new ModulePackage();
                    mPackage.setCodeUrl(modulePackage.getCodeUrl());
                    mPackage.setContentName(modulePackage.getContentName());
                    mPackage.setCreateTime(new Date());
                    mPackage.setModuleId(moduleId);
                    mPackage.setCodeReversion(String.valueOf(SvnUtils.LATEST_REVERSION));
                    mPackage.setPackagePathName(filePrefix.append(modulePackage.getContentName()).toString());
                    mPackage.setCodeType(ModuleTypeEnum.SVN_SOURCE_CODE.getModuleTypeCode());
                    packageMapper.insertSelective(mPackage);
                    List<ModulePackage> modulePackages = new ArrayList<>();
                    modulePackages.add(mPackage);
                    //这个时候需要异步拉取代码到指定目录中
                    syncService.checkoutAllAsync(modulePackages, certificate);
                    return true;
                  }
                }
              }
            }
            break;
          case YAML_DEPLOY_TYPE:
          case PROJECT_PACKAGE:
            //程序包类型的新增 //包名重名的情况下 不能进行添加
            if (moduleId != null && StringUtils.isNotBlank(modulePackage.getProgramFileName())) {
              String tmpFilePath = moduleUploadPath + modulePackage.getProgramFileName();
              File tmpFile = new File(tmpFilePath);
              if (tmpFile.exists()) {
                StringBuilder fileName = new StringBuilder();
                fileName.append(storgePrefix).append(moduleManageDTO.getCenterPath()).append(SEP).
                    append(moduleManageDTO.getModuleContentName()).append(SEP)
                    .append(modulePackage.getProgramFileName());
                ModulePackageExample packageExample = new ModulePackageExample();
                ModulePackageExample.Criteria criteria = packageExample.createCriteria();
                criteria.andModuleIdEqualTo(moduleId);
                criteria.andPackagePathNameEqualTo(fileName.toString());
                //经过判断是不重复的
                //然后先存数据再去存文件
                List<ModulePackage> packages = packageMapper.selectByExample(packageExample);
                if (packages == null || packages.size() == 0) {
                  ModulePackage mPackage = new ModulePackage();
                  mPackage.setModuleId(moduleId);
                  mPackage.setPackagePathName(fileName.toString().replace(storgePrefix, ""));
                  mPackage.setCreateTime(new Date());
                  mPackage.setCodeType(moduleType);
                  packageMapper.insertSelective(mPackage);
                  syncService.copyFile2File(tmpFile, new File(fileName.toString()));
                  return true;
                }
                packages.clear();
              }
            }
            break;
          default:
            return false;
        }
      }
    }
    return false;
  }
  
  /**
   * 删除package信息根据packageId
   *
   * @param packageId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deletePackageInfo(Integer packageId) {
    ModulePackage modulePackage = packageMapper.selectByPrimaryKey(packageId);
    boolean success = false;
    if (modulePackage != null) {
      //先删除数据
      packageMapper.deleteByPrimaryKey(modulePackage.getId());
      //再删除文件
      if (StringUtils.isNotBlank(modulePackage.getPackagePathName())) {
        if (!FoldUtils.deleteFolders(storgePrefix + modulePackage.getPackagePathName())) {
          throw new NotDeleteOkException("文件删除失败异常");
        }
        success = true;
      }
    }
    return success;
  }
  
  /**
   * 更新package信息 只能修改源码类型的数据
   *
   * @param packageId
   * @param codeUrl
   * @param contentName
   * @param gitBranch
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updatePackageInfo(Integer packageId, String codeUrl,
                                   String contentName, String gitBranch) {
    if (packageId != null && StringUtils.isNotBlank(codeUrl) && StringUtils.isNotBlank(contentName)) {
      ModulePackage modulePackage = packageMapper.selectByPrimaryKey(packageId);
      if (modulePackage != null) {
        ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(modulePackage.getModuleId());
        ModuleCertificate certificate = certificateMapper
            .selectCertByModuleId(modulePackage.getModuleId());
        if (certificate != null && moduleManageDTO != null) {
          //判断contentName不是重复添加的
          ModulePackageExample packageExample = new ModulePackageExample();
          packageExample.createCriteria()
              .andContentNameEqualTo(contentName)
              .andModuleIdEqualTo(modulePackage.getModuleId())
              .andIdNotEqualTo(modulePackage.getId());
          List<ModulePackage> packages = packageMapper.selectByExample(packageExample);
          
          ModuleTypeEnum typeByCode = ModuleTypeEnum.getTypeByCode(modulePackage.getCodeType());
          String reversion = null;
          if (typeByCode != null) {
            switch (typeByCode) {
              case GIT_SOURCE_CODE:
                try {
                  reversion = GitUtils.getUrlReversion(codeUrl, certificate.getUsername(),
                      EncryptUtil.decrypt(certificate.getPassword()));
                } catch (GitAPIException e) {
                  e.printStackTrace();
                }
                break;
              case SVN_SOURCE_CODE:
                try {
                  reversion = String.valueOf(SvnUtils.getUrlReversion(codeUrl,
                      certificate.getUsername(),
                      EncryptUtil.decrypt(certificate.getPassword())));
                } catch (SVNException e) {
                  throw new SvnUpdateException(e.getMessage());
                }
                break;
            }
          }
          
          if ((packages == null || packages.size() == 0)
              && StringUtils.isNotBlank(modulePackage.getPackagePathName())
              && StringUtils.isNotBlank(reversion)) {
            String sourceFile = storgePrefix + modulePackage.getPackagePathName();
            //判断完成之后添加数据
            StringBuilder packagePathName = new StringBuilder();
            modulePackage.setCodeUrl(codeUrl);
            modulePackage.setContentName(contentName);
            packagePathName.append(moduleManageDTO.getCenterPath()).append(SEP)
                .append(moduleManageDTO.getModuleContentName())
                .append(SEP).append(contentName);
            modulePackage.setPackagePathName(packagePathName.toString());
            modulePackage.setCreateTime(new Date());
            modulePackage.setCodeReversion("-1");
            if (StringUtils.isBlank(gitBranch)) {
              gitBranch = "master";
            }
            modulePackage.setGitBranch(gitBranch);
//                        modulePackage.setSvnReversion(urlReversion.intValue());
            if (packageMapper.updateByPrimaryKey(modulePackage) > 0) {
              //然后就要删除原有的数据 在下拉新的代码
              if (FoldUtils.deleteFolders(sourceFile)) {
                //删除成功之后再重新下拉代码
                List<ModulePackage> packageList = new ArrayList<>();
                packageList.add(modulePackage);
                //同步拉取代码
                syncService.checkoutAllSync(packageList, certificate);
                return true;
              } else {
                throw new NotDeleteOkException("文件未被删除: " + sourceFile);
              }
            }
          }
        }
      }
    }
    return false;
  }
  
  /**
   * 更改 模块子模块package的code版本号
   *
   * @param reversion
   * @param packageId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updatePackageReversion(Integer packageId, String reversion) {
    boolean success = false;
    if (packageId != null && StringUtils.isNotBlank(reversion)) {
      
      ModulePackage modulePackage = packageMapper.selectByPrimaryKey(packageId);
      
      if (modulePackage != null) {
        ModuleCertificate certificate = certificateMapper.selectCertByModuleId(modulePackage.getModuleId());
        modulePackage.setCodeReversion(reversion);
        ModuleTypeEnum typeByCode = ModuleTypeEnum.getTypeByCode(modulePackage.getCodeType());
        if (typeByCode != null) {
          switch (typeByCode) {
            case SVN_SOURCE_CODE:
              try {
                long update = SvnUtils.update(SvnUtils.getManager(certificate.getUsername(),
                        EncryptUtil.decrypt(certificate.getPassword())),
                    new File(storgePrefix + modulePackage.getPackagePathName()),
                    SVNRevision.create(Integer.valueOf(reversion)), SVNDepth.INFINITY);
                log.info("update param {} {} {}", reversion, JSONObject.toJSONString(modulePackage), update);
                if (update > 0 && packageMapper.updateByPrimaryKey(modulePackage) > 0) {
                  success = true;
                }
              } catch (SVNException e) {
                throw new SvnUpdateException("当前svn_url: " + modulePackage.getCodeUrl() +
                    "  " + e.getErrorMessage().getMessage());
              }
              break;
            case GIT_SOURCE_CODE:
              try {
                if ("-1".equals(reversion)) {
                  GitUtils.gitPull(storgePrefix + modulePackage.getPackagePathName(),
                      certificate.getUsername(), EncryptUtil.decrypt(certificate.getPassword()));
                } else {
                  GitUtils.gitReset(storgePrefix + modulePackage.getPackagePathName(), reversion);
                }
                if (packageMapper.updateByPrimaryKey(modulePackage) > 0) {
                  success = true;
                }
              } catch (IOException | GitAPIException e) {
                throw new SvnUpdateException("当前codePath: " + modulePackage.getPackagePathName() +
                    "  " + e.getMessage());
              }
              break;
          }
        }
      }
    }
    return success;
  }
  
  /**
   * 根据packageId获取该package所在的模块的环境id
   *
   * @param packageId
   * @return
   */
  @Override
  public Integer selectEnvIdByPackageId(Integer packageId) {
    return packageMapper.selectEnvIdByPackageId(packageId);
  }
  
  /**
   * 根据package中的存储目录地址重新下拉全部的代码(最新的)
   */
  @Override
  public void downSvnAll() {
    ModulePackageCertDTO modulePackageCertDTO = packageMapper.selectAllInfoWithCert();
    
    log.info("svnPackageAll的全部信息: {}", JSONObject.toJSONString(modulePackageCertDTO));

//        SVNClientManager clientManager = SvnUtils.getManager(certificate.getUsername(), certificate.getPassword());
//
//        clientManager.createRepository(SVNURL.parseURIEncoded(modulePackage.getSvnUrl()), true);
//
//        File file = new File(storgePrefix + modulePackage.getPackagePathName());
//
//        SvnUtils.checkout(clientManager, SVNURL.parseURIEncoded(modulePackage.getSvnUrl()), SVNRevision.create(SvnUtils.LATEST_REVERSION),
//                file, SVNDepth.INFINITY);
  }
  
  /**
   * 下拉某个模块下的所有代码,如果有websocket session可更新实时进度
   *
   * @param moduleId
   * @param session
   * @return
   * @throws SVNException
   * @throws FileNotFoundException
   */
  @SneakyThrows
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateModuleAllCode(Integer moduleId, Session session) {
    boolean success = false;
    ModuleManageDTO manageDTO = manageMapper.selectInfoById(moduleId);
    
    if (manageDTO != null) {
      StatusDTO statusDTO = new StatusDTO();
      statusDTO.setModuleId(moduleId);
      statusDTO.setStatus(0);
      statusDTO.setModuleName(manageDTO.getModuleName());
      ModuleTypeEnum typeEnum = ModuleTypeEnum.getTypeByCode(manageDTO.getModuleType());
      ModuleCertificate certificate = certificateMapper.selectCertByModuleId(moduleId);
      StringBuilder filePrefix = new StringBuilder();
      filePrefix.append(storgePrefix).append(manageDTO.getCenterPath()).append(SEP)
          .append(manageDTO.getModuleContentName()).append(SEP);
      
      if (typeEnum != null) {
        switch (typeEnum) {
          case SVN_AUTO_UP_CODE:
            List<ModulePackageDTO> packageDTOS = selectByModuleId(moduleId);
            SVNClientManager clientManager = SvnUtils.getManager(certificate.getUsername(),
                EncryptUtil.decrypt(certificate.getPassword()));
            String shFilePath = storgePrefix + manageDTO.getShPath();
            File file = new File(shFilePath);
            //下拉一遍代码  覆盖 更新的方式
            SvnUtils.doExport(clientManager, SVNURL.parseURIEncoded(manageDTO.getSvnAutoUrl()),
                SVNRevision.create(SvnUtils.LATEST_REVERSION), file, SVNDepth.INFINITY);
            //解析更新过的脚本获取最新数据
            
            List<ModulePackageParamVo> paramVoList =
                ExcelPhraseUtils.getAllShData(new FileInputStream(file));
            statusDTO.setTotal(paramVoList.size() * 2);
            for (int i = 0; i < paramVoList.size(); i++) {
              statusDTO.setCurrent(i + 1);
              statusDTO.setName(paramVoList.get(i).getContentName());
              if (session != null) {
                session.sendText(JSONObject.toJSONString(statusDTO));
              }
              
              boolean exist = SvnUtils.isURLExist(paramVoList.get(i).getCodeUrl(),
                  certificate.getUsername(), EncryptUtil.decrypt(certificate.getPassword()));
              if (!exist) {
                String url = paramVoList.get(i).getCodeUrl();
                paramVoList.clear();
                throw new SvnUrlNotExistException("svnUrl不存在异常: " + url);
              }
            }
            
            //删除原有的数据
            for (ModulePackageDTO packageDTO : packageDTOS) {
              //首先删除记录
              deleteById(packageDTO.getId());
              //然后删除对应的所有文件
              FoldUtils.deleteFolders(storgePrefix + packageDTO.getPackagePathName());
            }
            
            List<ModulePackage> packageList = new ArrayList<>();
            for (int j = 0; j < paramVoList.size(); j++) {
              ModulePackage modulePackage = new ModulePackage();
              try {
                statusDTO.setCurrent(j + paramVoList.size() + 1);
                statusDTO.setName(paramVoList.get(j).getContentName());
                if (session != null) {
                  session.sendText(JSONObject.toJSONString(statusDTO));
                }
                //checkout 新代码到模块目录下
                SvnUtils.checkout(clientManager, SVNURL.parseURIEncoded(paramVoList.get(j).getCodeUrl()),
                    SVNRevision.create(SvnUtils.LATEST_REVERSION),
                    new File(filePrefix + paramVoList.get(j).getContentName()), SVNDepth.INFINITY);
              } catch (SVNException e) {
                FoldUtils.deleteFolders(filePrefix.toString());
                throw new SvnUrlNotExistException(e.getMessage());
              }
              modulePackage.setCodeReversion(String.valueOf(SvnUtils.LATEST_REVERSION));
              modulePackage.setCreateTime(new Date());
              modulePackage.setCodeType(ModuleTypeEnum.SVN_AUTO_UP_CODE.getModuleTypeCode());
              modulePackage.setContentName(paramVoList.get(j).getContentName());
              modulePackage.setCodeUrl(paramVoList.get(j).getCodeUrl());
              modulePackage.setModuleId(manageDTO.getModuleId());
              modulePackage.setPackagePathName((filePrefix + paramVoList.get(j).getContentName()).
                  replace(storgePrefix, ""));
              packageList.add(modulePackage);
            }
            insertAll(packageList);
            success = true;
            break;
          case SVN_SOURCE_CODE:
            if (certificate != null) {
              SVNClientManager clientManager2 = SvnUtils.getManager(certificate.getUsername(),
                  EncryptUtil.decrypt(certificate.getPassword()));
              List<ModulePackageDTO> packageDTOS2 = selectByModuleId(moduleId);
              if (packageDTOS2 != null && packageDTOS2.size() > 0) {
                statusDTO.setTotal(packageDTOS2.size());
                for (int i = 0; i < packageDTOS2.size(); i++) {
                  File packagePathFile = new File(storgePrefix +
                      packageDTOS2.get(i).getPackagePathName());
                  if (!packagePathFile.exists()) {
                    //如果源文件不存在 则重新checkout新代码
                    SvnUtils.checkout(clientManager2,
                        SVNURL.parseURIEncoded(packageDTOS2.get(i).getCodeUrl()),
                        SVNRevision.create(Integer.valueOf(
                            packageDTOS2.get(i).getCodeReversion())),
                        packagePathFile, SVNDepth.INFINITY);
                  } else {
                    try {
                      clientManager2.createRepository(SVNURL.parseURIEncoded(
                          packageDTOS2.get(i).getCodeUrl()), true);
                      SvnUtils.update(clientManager2, packagePathFile, SVNRevision.
                              create(Integer.valueOf(
                                  packageDTOS2.get(i).getCodeReversion())),
                          SVNDepth.INFINITY);
                    } catch (SVNException e) {
                      throw new SvnUpdateException(e.getMessage());
                    }
                  }
                  statusDTO.setCurrent(i + 1);
                  statusDTO.setName(packageDTOS2.get(i).getContentName());
                  if (session != null) {
                    session.sendText(JSONObject.toJSONString(statusDTO));
                  }
                  
                }
                success = true;
              }
            }
            break;
          case GIT_SOURCE_CODE:
            if (certificate != null) {
              List<ModulePackageDTO> packageDTOS3 = selectByModuleId(moduleId);
              if (packageDTOS3 != null && packageDTOS3.size() > 0) {
                statusDTO.setTotal(packageDTOS3.size());
                for (int i = 0; i < packageDTOS3.size(); i++) {
                  String reversion = packageDTOS3.get(i).getCodeReversion();
                  try {
                    if ("-1".equals(reversion)) {
                      File packagePathFile = new File(storgePrefix +
                          packageDTOS3.get(i).getPackagePathName());
                      if (!packagePathFile.exists()) {
                        GitUtils.gitClone(packageDTOS3.get(i).getCodeUrl(),
                            storgePrefix + packageDTOS3.get(i).getPackagePathName(),
                            certificate.getUsername(),
                            EncryptUtil.decrypt(certificate.getPassword()),
                            packageDTOS3.get(i).getGitBranch());
                      } else {
                        GitUtils.gitPull(storgePrefix + packageDTOS3.get(i)
                                .getPackagePathName(),
                            certificate.getUsername(),
                            EncryptUtil.decrypt(certificate.getPassword()));
                      }
                      
                    } else {
                      //reset指的是根据指定的reversion回退到某个特定的版本
                      GitUtils.gitReset(storgePrefix +
                          packageDTOS3.get(i).getPackagePathName(), reversion);
                    }
                  } catch (IOException | GitAPIException e) {
                    throw new GitOwnException(e.getMessage());
                  }
                  
                  statusDTO.setCurrent(i + 1);
                  statusDTO.setName(packageDTOS3.get(i).getContentName());
                  if (session != null) {
                    session.sendText(JSONObject.toJSONString(statusDTO));
                  }
                  
                }
                success = true;
              }
            }
            break;
          
          case GIT_AUTO_UP_SOURCE_CODE:
            
            gitAutoType(certificate, manageMapper.selectByPrimaryKey(moduleId), statusDTO, session, null);
            success = true;
            break;
          default:
            break;
        }
      }
    }
    return success;
  }
  
  @SneakyThrows
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean gitAutoType(ModuleCertificate certificate, ModuleManage moduleManage, StatusDTO statusDTO, Session session, StringBuilder filePrefixBase) {
    StringBuilder filePrefix = new StringBuilder();
    if (filePrefixBase == null) {
      ModuleManageDTO manageDTO = manageMapper.selectInfoById(moduleManage.getId());
      filePrefix.append(storgePrefix).append(manageDTO.getCenterPath()).append(SEP)
          .append(manageDTO.getModuleContentName()).append(SEP);
    } else {
      filePrefix = filePrefixBase;
    }
    
    //if(statusDTO!=null){
    //    GitUtils.gitPull(filePrefix.toString(),
    //        certificate.getUsername(), EncryptUtil.decrypt(certificate.getPassword()));
    //}else{
    ////删除原有的数据
    List<ModulePackageDTO> packageGitDTOS = selectByModuleId(moduleManage.getId());
    if (!CollectionUtils.isEmpty(packageGitDTOS)) {
      for (ModulePackageDTO packageDTO : packageGitDTOS) {
        //首先删除记录
        deleteById(packageDTO.getId());
        //然后删除对应的所有文件
        FoldUtils.deleteFolders(storgePrefix + packageDTO.getPackagePathName());
      }
    }
    String username = certificate.getUsername();
    String password = EncryptUtil.decrypt(certificate.getPassword());
    //先删除，再clone
    //  FoldUtils.deleteFolders(filePrefix.toString()  );
    GitUtils.cloneOrPull(moduleManage.getSvnAutoUrl(), filePrefix.toString(), username, password, null);
    
    Integer certificatiId = Integer.valueOf(certificate.getId());
    certificate = certificateService.selectById(certificatiId);
    if (certificate != null) {
      moduleManage.setCertificateId(certificatiId);
      File shFile = FileUtil.loopFiles(new File(filePrefix.toString()), 1, file -> file.isFile() && file.getName().endsWith(".sh")).stream().findFirst().orElse(null);
      if (shFile != null) {
        System.out.println("文件夹中存在.sh文件");
        //GitUtils.gitClone(filePrefix.toString(),shFile.getName(),
        //moduleManage.setShPath(shFile.getPath().toString().replace(storgePrefix, "")));
        // moduleManage.setSvnAutoUrl(mangeVo.getSvnAutoUpUrl());
        List<ModulePackageParamVo> paramVos =
            ExcelPhraseUtils.getAllGitShData(new FileInputStream(shFile));
        GitUtils.generateXML(filePrefix.toString(), shFile.getName());
        return saveGitModulePackage(certificate, moduleManage, paramVos, filePrefix, statusDTO, session);
      } else {
        System.out.println("文件夹中不存在.sh文件");
      }
      
    }
    return false;
  }
  
  @SneakyThrows
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean saveGitModulePackage(ModuleCertificate certificate, ModuleManage moduleManage, List<ModulePackageParamVo> packageList, StringBuilder filePrefix, StatusDTO statusDTO, Session session) {
    if (certificate != null) {
      if (statusDTO == null) {
        manageMapper.insertSelective(moduleManage);
      }
      List<ModulePackageDTO> packageGitDTOS = selectByModuleId(moduleManage.getId());
      List<ModulePackage> modulePackages = new ArrayList<>();
      //然后对package里面的svnUrl做Url验证
      for (int i = 0; i < packageList.size(); i++) {
        ModulePackageParamVo packageVo = packageList.get(i);
        
        //如果源文件不存在 则重新checkout新代码
        GitUtils.cloneOrPull(packageVo.getCodeUrl(), filePrefix + packageVo.getContentName(), certificate.getUsername(), EncryptUtil.decrypt(certificate.getPassword()), packageVo.getGitBranch());
        
        ModulePackage modulePackage = new ModulePackage();
        modulePackage.setCodeReversion("-1");
        modulePackage.setCreateTime(new Date());
        modulePackage.setCodeType(ModuleTypeEnum.GIT_AUTO_UP_SOURCE_CODE.getModuleTypeCode());
        modulePackage.setContentName(packageVo.getContentName());
        modulePackage.setCodeUrl(packageVo.getCodeUrl());
        modulePackage.setModuleId(moduleManage.getId());
        modulePackage.setGitBranch("master");
        modulePackage.setPackagePathName((filePrefix + packageVo.getContentName()).
            replace(storgePrefix, ""));
        modulePackages.add(modulePackage);
        
        if (session != null) {
          statusDTO.setTotal(packageList.size());
          statusDTO.setModuleName(packageVo.getContentName());
          statusDTO.setCurrent(i + 1);
          statusDTO.setResult(true);
          session.sendText(JSONObject.toJSONString(statusDTO));
        }
        //  return true;
      }
      insertAll(modulePackages);
    }
    return false;
  }

//@SneakyThrows
//private void updateGitSource(ModuleCertificate certificate, Integer moduleId,StatusDTO statusDTO,Session session ,boolean success   ){
//    if (certificate != null) {
//
//        List<ModulePackageDTO> packageDTOS2 = selectByModuleId(moduleId);
//        if (packageDTOS2 != null && packageDTOS2.size() > 0) {
//            statusDTO.setTotal(packageDTOS2.size());
//            for (int i = 0; i < packageDTOS2.size(); i++) {
//                File packagePathFile = new File(storgePrefix +
//                    packageDTOS2.get(i).getPackagePathName());
//                if (!packagePathFile.exists()) {
//                    //如果源文件不存在 则重新checkout新代码
//
//                    GitUtils.gitClone(packageDTOS2.get(i).getCodeUrl(),
//                        storgePrefix + packageDTOS2.get(i).getPackagePathName(),
//                        certificate.getUsername(),
//                        EncryptUtil.decrypt(certificate.getPassword()),
//                        packageDTOS3.get(i).getGitBranch());
//                } else {
//                    try {
//                        clientManager2.createRepository(SVNURL.parseURIEncoded(
//                            packageDTOS2.get(i).getCodeUrl()), true);
//                        SvnUtils.update(clientManager2, packagePathFile, SVNRevision.
//                                create(Integer.valueOf(
//                                    packageDTOS2.get(i).getCodeReversion())),
//                            SVNDepth.INFINITY);
//                    } catch (SVNException e) {
//                        throw new SvnUpdateException(e.getMessage());
//                    }
//                }
//                statusDTO.setCurrent(i + 1);
//                statusDTO.setName(packageDTOS2.get(i).getContentName());
//                if (session != null) {
//                    session.sendText(JSONObject.toJSONString(statusDTO));
//                }
//
//            }
//            success = true;
//        }
//    }
//}
  
  /**
   * 获取远程分支列表
   *
   * @param envId
   * @param packageId
   * @return
   */
  @Override
  public List<String> getRemoteBranches(Integer envId, Integer packageId) {
    ModulePackageVo packageVo = packageMapper.selectCertInfoById(packageId);
    if (packageVo != null) {
      try {
        return GitUtils.branchRemoteList(storgePrefix + packageVo.getPackagePathName(),
            packageVo.getUsername(), EncryptUtil.decrypt(packageVo.getPassword()));
      } catch (GitAPIException | IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  /**
   * 对子模块做切换分支处理
   *
   * @param envId
   * @param packageId
   * @param branchName
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean chanageBranch(Integer envId, Integer packageId, String branchName) {
    boolean success = false;
    ModulePackageVo packageVo = packageMapper.selectCertInfoById(packageId);
    if (packageVo != null) {
      //先切换分支
      try {
        //首先切换分支并拉取代码
        GitUtils.gitCheckoutBranchAndPull(storgePrefix + packageVo.getPackagePathName(),
            packageVo.getUsername(), EncryptUtil.decrypt(packageVo.getPassword()), branchName);
        //成功后更新数据
        ModulePackage modulePackage = new ModulePackage();
        modulePackage.setGitBranch(branchName);
        modulePackage.setId(packageId);
        packageMapper.updateByPrimaryKeySelective(modulePackage);
        success = true;
      } catch (GitAPIException | IOException e) {
        throw new GitOwnException("切换分支出错: " + e.getMessage());
      }
    }
    return success;
  }
  
}
