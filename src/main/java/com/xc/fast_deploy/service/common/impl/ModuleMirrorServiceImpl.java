package com.xc.fast_deploy.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.harborMirror.MirrorProjectDTO;
import com.xc.fast_deploy.dto.harborMirror.MirrorTagDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorCertificateEnvDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorDTO;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleMirror;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.model.master_model.example.ModuleMirrorExample;
import com.xc.fast_deploy.myenum.ModuleMirrorStatusEnum;
import com.xc.fast_deploy.quartz_job.job.MyMirrorSyncThread;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.common.ModuleMirrorService;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.jenkins.JenkinsManage;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleMirrorSelectParamVo;
import com.xc.fast_deploy.websocketConfig.myThread.ShutDownThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xc.fast_deploy.utils.constant.HarborContants.*;

@Service
@Slf4j
public class ModuleMirrorServiceImpl extends BaseServiceImpl<ModuleMirror, Integer> implements ModuleMirrorService {
  @Value("${myself.pspass.harbor.uri}")
  String harborUri;
  @Value("${myself.pspass.harbor.account}")
  String harborAccount;
  @Value("${myself.pspass.harbor.password}")
  String harborPassword;
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  @Autowired
  private ModuleMirrorMapper mirrorMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModuleDeployLogMapper deployLogMapper;
  @Autowired
  private ModuleDeployService deployService;
  @Autowired
  private PModuleUserMapper userMapper;
  @Resource
  private JenkinsManage jenkinsManage;
  
  @PostConstruct
  public void init() {
    super.init(mirrorMapper);
  }
  
  /**
   * 根据jobId查询可用的镜像列表信息
   *
   * @param jobId
   * @return
   */
  @Override
  public List<ModuleMirror> selectAvailableInfoByJobId(Integer jobId) {
    ModuleMirrorExample mirrorExample = new ModuleMirrorExample();
    ModuleMirrorExample.Criteria criteria = mirrorExample.createCriteria();
    criteria.andJobIdEqualTo(jobId).andIsAvailableEqualTo(1);
    mirrorExample.setOrderByClause("update_time DESC");
    return mirrorMapper.selectByExample(mirrorExample);
  }
  
  /**
   * 更新镜像状态
   *
   * @param mirrorId
   * @return
   */
  @Override
  //@Transactional(rollbackFor = Exception.class)
  public boolean updateMirror(Integer mirrorId) {
    ModuleMirror moduleMirror = mirrorMapper.selectByPrimaryKey(mirrorId);
    if (moduleMirror != null) {
      
      moduleMirror.setIsAvailable(ModuleMirrorStatusEnum.AVAILAVLE.getCode());
      moduleMirror.setUpdateTime(new Date());
      if (mirrorMapper.updateByPrimaryKeySelective(moduleMirror) > 0) {
        Integer isPromptly = moduleMirror.getIsPromptly();
        if (isPromptly != null && isPromptly == 1) {
          PModuleUser user = userMapper.selectByUserId(moduleMirror.getOpUserId());
          deployService.changeMirror(moduleMirror.getModuleId(), moduleMirror.getModuleEnvId(), mirrorId, new ModuleUser(user), null);
        }
        return true;
      }
      
    }
    return false;
  }
  
  /**
   * 根据模块id和环境id查询可用的镜像列表信息
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @Override
  public List<ModuleMirror> selectAvailableInfoById(Integer moduleId, Integer envId) {
    List<ModuleMirror> mirrorList = null;
//        ModuleEnv moduleEnv = envMapper.selectByPrimaryKey(envId);
    ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleId);
//        ModuleManage moduleManage = manageMapper.selectByPrimaryKey(moduleId);
    if (moduleId != null && envId != null) {
      ModuleMirror moduleMirror = new ModuleMirror();
      moduleMirror.setModuleId(moduleId);
      moduleMirror.setModuleEnvId(envId);
      mirrorList = mirrorMapper.selectAvailableMirrorById(moduleMirror);
//            StringBuilder sb = new StringBuilder();
//
//            sb.append(HTTP_PREFIX).append(moduleEnv.getHarborUrl()).append(CONTACT).append(REPOSTIRY)
//                    .append(CONTACT).append(moduleManage.getModuleProjectCode()).append(CONTACT)
//                    .append(moduleManage.getModuleContentName()).append(CONTACT).append(TAGS);
//
//            String s = HttpUtils.doGetHarborInfo(sb.toString(),
//                    moduleEnv.getUsername(), EncryptUtil.decrypt(moduleEnv.getPaasname()));
//
//            sb.delete(0,sb.length());
//
//            if (StringUtils.isNotBlank(s)) {
//                List<MirrorTagDTO> mirrorTagDTOS = JSONObject.parseArray(s, MirrorTagDTO.class);
//                if (mirrorTagDTOS != null && mirrorTagDTOS.size() > 0) {
//                    for (MirrorTagDTO mirrorTagDTO : mirrorTagDTOS) {
//                        String name = mirrorTagDTO.getName();
//                        sb.append(moduleEnv.getHarborUrl()).append(CONTACT).append(moduleManage.getModuleContentName())
//                                .append(CONTACT).append(name);
//                    }
//                }
//            }
    }
    return mirrorList;
    
  }

//    public static void main(String[] args) {
//        String api = "http://134.108.3.220/api/repositories/project_code/order-center/smt-bss-order-query-project/tags";
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(HTTP_PREFIX).append("134.108.3.220").append(CONTACT).append(API).append(CONTACT)
//                .append(REPOSTIRY).append(CONTACT).append("project_code").append(CONTACT)
//                .append("order-center/smt-bss-order-query-project").append(CONTACT).append(TAGS);
//
//
//        String s = HttpUtils.doGetHarborInfo(sb.toString(),
//                moduleEnv.getUsername(), EncryptUtil.decrypt(moduleEnv.getPaasname()));
//    }
  
  /**
   * 查询manageName
   *
   * @param envId
   * @param moduleName
   * @return
   */
  @Override
  public List<ModuleEnvCenterManageVo> selectCenManage(Integer envId, String moduleName) {
    List<ModuleEnvCenterManageVo> envCenterManageVo = null;
    ModuleEnvCenterManageVo envCenterManageVo1 = new ModuleEnvCenterManageVo();
    envCenterManageVo1.setEnvId(envId);
    envCenterManageVo1.setModuleName(moduleName);
    envCenterManageVo = mirrorMapper.selectCenManage(envCenterManageVo1);
    return envCenterManageVo;
  }
  
  /**
   * 分页查询可用的镜像列表信息
   *
   * @param pageNum
   * @param pageSize
   * @param moduleMirrorSelectParamVo
   * @return
   */
  @Override
  public MyPageInfo<ModuleMirrorDTO> selectAvailInfoPageByParam(Integer pageNum, Integer pageSize, ModuleMirrorSelectParamVo moduleMirrorSelectParamVo) {
    MyPageInfo<ModuleMirrorDTO> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<ModuleMirrorDTO> mirrorDTOS = mirrorMapper.selectAvailInfoByParam(moduleMirrorSelectParamVo);
      PageInfo<ModuleMirrorDTO> pageInfo = new PageInfo<>(mirrorDTOS);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  /**
   * 删除镜像信息,并且同时删除该镜像在harbor中的信息
   *
   * @param mirrorId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteMirrorInfo(Integer mirrorId) {
    boolean success = false;
    ModuleMirrorCertificateEnvDTO mirrorCertificateEnvDTO = mirrorMapper.selectMirrorInfoById(mirrorId);
    if (mirrorCertificateEnvDTO != null) {
      String mirrorName = mirrorCertificateEnvDTO.getMirrorName();
      if (StringUtils.isNotBlank(mirrorName)) {
        String replace = mirrorName.replace(mirrorCertificateEnvDTO.getHarborUrl(), "");
        String[] splits = replace.split(":");
        if (splits.length == 2) {
          mirrorName = splits[0];
          String tags = splits[1];
          StringBuilder sb = new StringBuilder();
          String deleteUrl = sb.append(HTTP_PREFIX).append(mirrorCertificateEnvDTO.getHarborUrl())
              .append(CONTACT).append(API).append(CONTACT).append(REPOSTIRY)
              .append(mirrorName).append(CONTACT).append(TAGS)
              .append(CONTACT).append(tags).toString();
          HttpUtils.doDeleteHarborInfo(deleteUrl, mirrorCertificateEnvDTO.getUsername(),
              EncryptUtil.decrypt(mirrorCertificateEnvDTO.getPassword()));
          success = true;
          //修改镜像状态
          ModuleMirror moduleMirror = new ModuleMirror();
          moduleMirror.setIsAvailable(0);
          moduleMirror.setId(mirrorCertificateEnvDTO.getId());
          mirrorMapper.updateByPrimaryKeySelective(moduleMirror);
          
        }
      }
    }
    return success;
  }
  
  /**
   * 同步harbor仓库中的数据到本地数据库的镜像信息,主要是在harbor中已经删除的镜像在本地数据做更新操作
   */
  @Override
  public void clearNotInHaroborMirrorInfo() {
    List<ModuleMirrorCertificateEnvDTO> envDTOS = mirrorMapper.selectAllAvailableMirrorInfo();
    Map<Integer, List<ModuleMirrorCertificateEnvDTO>> envDTOMap = new HashMap<>();
    if (envDTOS != null && envDTOS.size() > 0) {
      for (ModuleMirrorCertificateEnvDTO mirrorCertificateEnvDTO : envDTOS) {
        if (!envDTOMap.containsKey(mirrorCertificateEnvDTO.getEnvId())) {
          List<ModuleMirrorCertificateEnvDTO> envDTOList = new LinkedList<>();
          envDTOList.add(mirrorCertificateEnvDTO);
          envDTOMap.put(mirrorCertificateEnvDTO.getEnvId(), envDTOList);
        } else {
          envDTOMap.get(mirrorCertificateEnvDTO.getEnvId()).add(mirrorCertificateEnvDTO);
        }
      }
      envDTOS.clear();
      ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(envDTOMap.size(), envDTOMap.size(), 0L,
          TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
      Set<Integer> keySet = envDTOMap.keySet();
      for (Integer envId : keySet) {
        List<ModuleMirrorCertificateEnvDTO> envDTOList = envDTOMap.get(envId);
        poolExecutor.submit(new MyMirrorSyncThread(envDTOList, mirrorMapper));
      }
      //关闭线程池的操作
      Executors.newSingleThreadExecutor().submit(new ShutDownThread(poolExecutor));
//            PoolExcutorUtils.shutDownExcutor(poolExecutor, envDTOMap.size());
    }
  }
  
  /**
   * 根据环境id 获取harbor仓库对应的所欲project_code
   *
   * @param envId
   * @return
   */
  @Override
  public List<String> getHarborProjectCode(Integer envId) {
    List<String> projectNameList = new ArrayList<>();
    ModuleEnvVo moduleEnvVo = envMapper.selectWithCertById(envId);
    if (moduleEnvVo != null) {
      StringBuilder harborUrl = new StringBuilder();
      harborUrl.append(HTTP_PREFIX).append(moduleEnvVo.getHarborUrl()).append(CONTACT).append(API).append(CONTACT).append(PROJECTS);
      String s = HttpUtils.doGetHarborInfo(harborUrl.toString(),
          moduleEnvVo.getUsername(), EncryptUtil.decrypt(moduleEnvVo.getPassword()));
      if (StringUtils.isNotBlank(s)) {
        List<MirrorProjectDTO> projectDTOS = JSONObject.parseArray(s, MirrorProjectDTO.class);
        if (projectDTOS != null && projectDTOS.size() > 0) {
          for (MirrorProjectDTO mirrorProjectDTO : projectDTOS) {
            projectNameList.add(mirrorProjectDTO.getName());
          }
          projectDTOS.clear();
        }
      }
    }
    log.info("获取harbor的信息为:{}", JSONObject.toJSONString(projectNameList));
    return projectNameList;
  }
  
  /**
   * 根据jobIds 查询对应的生成镜像的最新数据
   *
   * @param jobIds
   * @return
   */
  @Override
  public List<ModuleMirror> selectByJobIds(Set<Integer> jobIds) {
    return mirrorMapper.selectByJobIds(jobIds);
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateSelective(ModuleMirror moduleMirror) {
    return mirrorMapper.updateByPrimaryKeySelective(moduleMirror);
  }
  
  /**
   * 获取当前构建的消息日志,每个用户对应自己不用的消息提示
   *
   * @param envIdSet
   * @param userIdFromToken
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<ModuleMirrorDTO> getPollingMirrorJobInfoByEnvId(Set<Integer> envIdSet,
                                                              Integer size,
                                                              Integer isUsedSelect,
                                                              String userIdFromToken) {
    //访问获取最近构建生成
    ModuleMirrorSelectParamVo selectParamVo = new ModuleMirrorSelectParamVo();
    selectParamVo.setEnvIds(envIdSet);
    selectParamVo.setSize(size);
    selectParamVo.setOpUserId(userIdFromToken);
    if (isUsedSelect == 1 || isUsedSelect == 0) {
      selectParamVo.setIsUsed(isUsedSelect);
    }
    //查询到最新的10条记录
    List<ModuleMirrorDTO> mirrorDTOS = mirrorMapper.selectLastestMirrorInfo(selectParamVo);
    //然后去做遍历
    if (mirrorDTOS.size() > 0) {
      JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
      for (ModuleMirrorDTO mirrorDTO : mirrorDTOS) {
        Integer isAvailable = mirrorDTO.getIsAvailable();
        //正在进行中的
        ModuleMirrorStatusEnum statusEnum = ModuleMirrorStatusEnum.getTypeByCode(isAvailable);
        if (statusEnum != null) {
          switch (statusEnum) {
            case ONLINE:
              try {
                JobWithDetails job = jenkinsServer.getJob(mirrorDTO.getJobName());
                if (job != null) {
                  Build build = job.getBuildByNumber(mirrorDTO.getJobReversion());
                  //判定该镜像对应的构建结果为FAILURE的情况下才去更改镜像的可用状态为不可用
                  if (build != null
                      && build.details() != null
                      && build.details().getResult() != null
                      && "FAILURE".equals(build.details().getResult().name())) {
                    ModuleMirror moduleMirror = new ModuleMirror();
                    moduleMirror.setId(mirrorDTO.getId());
                    moduleMirror.setIsAvailable(ModuleMirrorStatusEnum.FAIL.getCode());
                    mirrorMapper.updateByPrimaryKeySelective(moduleMirror);
                    mirrorDTO.setIsAvailable(ModuleMirrorStatusEnum.FAIL.getCode());
                  }
                }
              } catch (IOException e) {
                log.error("获取job信息出错:{}", mirrorDTO.getJobName());
                e.printStackTrace();
              }
              break;
            default:
              break;
          }
        }
      }
    }
    return mirrorDTOS;
  }
  
  /**
   * 获取某个环境的某个模块的harbor仓库对应的mirrorlist
   *
   * @param envId
   * @param moduleId
   * @return
   */
  @Override
  public List<String> getHarborMirrorList(Integer envId, Integer moduleId) {
    List<String> mirrorNameList = new ArrayList<>();
    ModuleEnvVo moduleEnvVo = envMapper.selectWithCertById(envId);
    ModuleManage moduleManage = manageMapper.selectByPrimaryKey(moduleId);
    //验证数据
    if (moduleEnvVo != null && moduleManage != null && envId.equals(moduleManage.getEnvId())) {
      //形成镜像名称
      StringBuilder mirrorName = new StringBuilder();
      mirrorName.append(moduleManage.getModuleProjectCode())
          .append(CONTACT).append(moduleManage.getModuleContentName().toLowerCase());
    
      StringBuilder sb = new StringBuilder();
      //形成访问harborapi的taglist的url路径
      String mirrorTagsUrl = sb.append(HTTP_PREFIX).append(moduleEnvVo.getHarborUrl())
          .append(CONTACT).append(API).append(CONTACT).append(REPOSTIRY).append(CONTACT)
          .append(mirrorName).append(CONTACT).append(TAGS).toString();
      //http请求数据获取返回
      String result = HttpUtils.doGetHarborInfo(mirrorTagsUrl,
          moduleEnvVo.getUsername(), EncryptUtil.decrypt(moduleEnvVo.getPassword()));
      if (StringUtils.isNotBlank(result)) {
        List<MirrorTagDTO> tagDTOList = JSONObject.parseArray(result, MirrorTagDTO.class);
        if (tagDTOList != null && tagDTOList.size() > 0) {
          for (MirrorTagDTO mirrorTagDTO : tagDTOList) {
            String tagName = mirrorTagDTO.getName();
            StringBuffer mirrorTagName = new StringBuffer();
            mirrorTagName.append(moduleEnvVo.getHarborUrl()).append(CONTACT)
                .append(mirrorName).append(":").append(tagName);
            mirrorNameList.add(mirrorTagName.toString());
          }
        }
      }
    }
    return mirrorNameList;
  }
  
  @Override
  public List<String> getYunHarborMirrorList(Integer envId, Integer moduleId) {
    List<String> mirrorNameList = new ArrayList<>();
    ModuleEnvVo moduleEnvVo = envMapper.selectWithCertById(envId);
    ModuleManage moduleManage = manageMapper.selectByPrimaryKey(moduleId);
    //验证数据
    if (moduleEnvVo != null && moduleManage != null && envId.equals(moduleManage.getEnvId())) {
      //形成镜像名称
      StringBuilder mirrorName = new StringBuilder();
      String uri = manageMapper.selectHarborUri(envId, moduleId);
      //mirrorName.append("crm-test-repo").append(CONTACT).
      mirrorName.append(uri.toLowerCase());
  
      StringBuilder sb = new StringBuilder();
      //形成访问harborapi的taglist的url路径
      String mirrorTagsUrl = sb.append(HTTP_PREFIX)
          //.append(CONTACT).append(API).append(CONTACT).append(REPOSTIRY).append(CONTACT)
          .append(mirrorName).append(CONTACT).append(TAGS).toString();
      //http请求数据获取返回
      String result;
      if (isProdEnv) {
        result = HttpUtils.doGetHarborInfo(mirrorTagsUrl, moduleEnvVo.getUsername(), EncryptUtil.decrypt(moduleEnvVo.getPassword()));
      } else {
        result = HttpUtils.doGetHarborInfo(mirrorTagsUrl, harborAccount, harborPassword);
      }
      if (StringUtils.isNotBlank(result)) {
        List<MirrorTagDTO> tagDTOList = JSONObject.parseArray(result, MirrorTagDTO.class);
        if (tagDTOList != null && tagDTOList.size() > 0) {
          tagDTOList.sort(Comparator.comparing(MirrorTagDTO::getName).reversed());
          for (MirrorTagDTO mirrorTagDTO : tagDTOList) {
            String tagName = mirrorTagDTO.getName();
            StringBuffer mirrorTagName = new StringBuffer();
            mirrorTagName
                .append(uri.replace("/api/repositories", "")).append(":").append(tagName);
            mirrorNameList.add(mirrorTagName.toString());
          }
        }
      }
    }
    return mirrorNameList;
  }
  
  @Override
  public void clearHarborNotUsedMirror(Set<Integer> envIds) {
    List<ModuleMirrorCertificateEnvDTO> mirrorList = mirrorMapper.selectIsNotUsedMirror(envIds);
    Map<String, Map<String, String>> harborInfoMap = new HashMap<>();
    if (mirrorList.size() > 0) {
      StringBuilder sb = new StringBuilder();
      for (ModuleMirrorCertificateEnvDTO mirrorCertificateEnvDTO : mirrorList) {
        String mirrorName = mirrorCertificateEnvDTO.getMirrorName();
        if (StringUtils.isNotBlank(mirrorName)) {
          String replace = mirrorName.replace(mirrorCertificateEnvDTO.getHarborUrl(), "");
          String[] splits = replace.split(":");
          if (splits.length == 2) {
            mirrorName = splits[0];
            String tags = splits[1];
            String deleteUrl = sb.append(HTTP_PREFIX).append(mirrorCertificateEnvDTO.getHarborUrl())
                .append(CONTACT).append(API).append(CONTACT).append(REPOSTIRY)
                .append(mirrorName).append(CONTACT).append(TAGS)
                .append(CONTACT).append(tags).toString();
            HttpUtils.doDeleteHarborInfo(deleteUrl, mirrorCertificateEnvDTO.getUsername(),
                EncryptUtil.decrypt(mirrorCertificateEnvDTO.getPassword()));
            mirrorMapper.deleteByPrimaryKey(mirrorCertificateEnvDTO.getId());
            sb.setLength(0);
          }
          if (!harborInfoMap.containsKey(mirrorCertificateEnvDTO.getHarborUrl())) {
            Map<String, String> map = new HashMap<>();
            map.put("user", mirrorCertificateEnvDTO.getUsername());
            map.put("pass", mirrorCertificateEnvDTO.getPassword());
            harborInfoMap.put(mirrorCertificateEnvDTO.getHarborUrl(), map);
          }
          ;
        }
      }
      //对harbor仓库做GC释放磁盘空间
      for (String url : harborInfoMap.keySet()) {
        String gcUrl = sb.append(HTTP_PREFIX).append(url).append(CONTACT).append(API)
            .append(CONTACT).append(SYSTEM_GC).toString();
        HttpUtils.doPost(gcUrl, GC_PARAMETERS, harborInfoMap.get(url).get("user"),
            EncryptUtil.decrypt(harborInfoMap.get(url).get("pass")));
        sb.setLength(0);
      }
    }
  }
}
