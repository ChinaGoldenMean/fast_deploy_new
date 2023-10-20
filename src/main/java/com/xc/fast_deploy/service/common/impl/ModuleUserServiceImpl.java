package com.xc.fast_deploy.service.common.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleUserMapper;
import com.xc.fast_deploy.dto.module.permission.ModuleUserPermissionDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModulePermissionUser;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.permission.PModuleUserService;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.SessionCookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class ModuleUserServiceImpl extends BaseServiceImpl<ModuleUser, Integer> implements ModuleUserService {
  
  @Autowired
  private ModuleUserMapper userMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private JedisManage jedisManage;
  @Autowired
  private PModuleUserService pModuleUserService;
  
  @Value("${myself.pspass.ids}")
  private String pspaasIds;
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  
  private static final List<Integer> pspaasIdList = new ArrayList<>();
  
  @PostConstruct
  public void init() {
    if (StringUtils.isNotBlank(pspaasIds)) {
      String[] split = pspaasIds.split(",");
      if (split.length > 1) {
        for (String id : split) {
          pspaasIdList.add(Integer.valueOf(id));
        }
      }
    }
    super.init(userMapper);
  }
  
  /**
   * 获取用户对应的所有环境id
   * 所有环境的环境id为0
   *
   * @param userId
   * @return
   */
  @Override
  public Map<Integer, Set<String>> selectEnvPermissionByUserId(String userId) {
    
    Map<Integer, Set<String>> envPermissionMap = new HashMap<>();
    List<ModuleUserPermissionDTO> userEnvPermissions =
        pModuleUserService.getAllPermissionsByUserId(userId);
    if (userEnvPermissions != null && userEnvPermissions.size() > 0) {
      for (ModuleUserPermissionDTO moduleUserPermissionDTO : userEnvPermissions) {
        envPermissionMap.put(moduleUserPermissionDTO.getEnvId(), moduleUserPermissionDTO.getPermissionSet());
      }
      userEnvPermissions.clear();
    }
    return envPermissionMap;
  }
  
  @Override
  public Map<Integer, Set<String>> selectEnvPermissionByUserName(String userNameFromToken) {
    Map<Integer, Set<String>> envPermissionMap = new HashMap<>();
    List<ModuleUserPermissionDTO> userEnvPermissions =
        pModuleUserService.getAllPermissionsByUserName(userNameFromToken);
    if (userEnvPermissions != null && userEnvPermissions.size() > 0) {
      for (ModuleUserPermissionDTO moduleUserPermissionDTO : userEnvPermissions) {
        envPermissionMap.put(moduleUserPermissionDTO.getEnvId(), moduleUserPermissionDTO.getPermissionSet());
      }
      userEnvPermissions.clear();
    }
    return envPermissionMap;
  }
  
  /**
   * 根据用户id查询对应的ps环境权限
   *
   * @param userId
   * @return
   */
//    @Override
//    public List<ModuleEnv> selectPsPermissionByUserId(String userId) {
//        List<ModuleEnv> envList = new ArrayList<>();
//        if (StringUtils.isNotBlank(userId)) {
//            String result = HttpUtils.httpGetUrl(HttpUtils.GET_PERMISSION_URL + userId,
//                    HttpUtils.ZDYW_COOKEY_KEY, jedisManage.hGet(userId, HttpUtils.ZDYW_COOKEY_KEY));
//
//            log.info(HttpUtils.ZDYW_COOKEY_KEY);
//            log.info(jedisManage.hGet(userId, HttpUtils.ZDYW_COOKEY_KEY));
//
//            if (StringUtils.isNotBlank(result)) {
//                JSONObject jsonObject = JSONObject.parseObject(result);
//                String status = jsonObject.getString("status");
//                if ("ok".equals(status)) {
//                    JSONObject detailObject = jsonObject.getJSONObject("detail");
//                    if (detailObject != null) {
//                        JSONObject userPermissionObject = detailObject.getJSONObject(userId);
//                        if (userPermissionObject != null) {
//                            JSONObject envJsonObject = userPermissionObject.getJSONObject("env");
//                            Set<String> envSetStrings = envJsonObject.keySet();
//                            if (envSetStrings.size() > 0) {
//                                List<ModuleEnv> allEnvByPaas = envMapper.getEnvIdByPaasId();
//                                Iterator<String> iterator = envSetStrings.iterator();
//                                while (iterator.hasNext()) {
//                                    String next = iterator.next();
//                                    JSONObject object = envJsonObject.getJSONObject(next);
//                                    Integer paasId = Integer.valueOf(next);
//                                    String paasname = object.getString("paasname");
//                                    ModuleEnv moduleEnv = new ModuleEnv();
//                                    moduleEnv.setPaasId(paasId);
//                                    moduleEnv.setPaasname(paasname);
//                                    envList.add(moduleEnv);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return envList;
//    }
  
  /**
   * 删除权限对应的数据
   *
   * @param userId
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePermissionByUserId(String userId) {
    userMapper.deletePermissionByUserId(userId);
  }
  
  /**
   * 获取用户对应的权限信息,调用zdyw的获取权限的接口
   *
   * @param userId
   * @return
   */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public List<ModuleUserPermissionDTO> getUserEnvPermission(String userId) {
//        log.info("获取用户权限信息,用户id:{}", userId);
//        List<ModuleUserPermissionDTO> prodPermissionDTOS = new ArrayList<>();
//        List<ModuleUserPermissionDTO> testPermissionDTOS = new ArrayList<>();
//        Map<String, List<ModuleUserPermissionDTO>> userPermissionMap = new HashMap<>();
//        if (StringUtils.isNotBlank(userId)) {
//            String s = userMapper.selectUserPermissionById(userId);
//            if (StringUtils.isNotBlank(s)) {
//                List<ModuleUserPermissionDTO> userPermissionDTOS = new ArrayList<>();
//                JSONObject jsonObject = JSONObject.parseObject(s);
//                if (jsonObject != null) {
//                    //根据配置好的测试环境和生产环境的区别取得对应权限信息,
//                    // 即发布在测试环境的只能取得测试环境的权限,发布在生产环境的只能取得生产环境的权限
//                    Object permissionDTOObject =
//                            isProdEnv ? jsonObject.get("PROD") : jsonObject.get("TEST");
//                    if (permissionDTOObject != null) {
//                        userPermissionDTOS =
//                                JSONObject.parseArray(JSONObject.toJSONString(permissionDTOObject),
//                                        ModuleUserPermissionDTO.class);
//                    } else {
//                        return null;
//                    }
//                }
//                if (userPermissionDTOS != null && userPermissionDTOS.size() > 0) {
//                    return userPermissionDTOS;
//                }
//            }
//
//            String result = HttpUtils.httpGetUrl(HttpUtils.GET_PERMISSION_URL + userId,
//                    HttpUtils.ZDYW_COOKEY_KEY, jedisManage.hGet(userId, HttpUtils.ZDYW_COOKEY_KEY));
//            if (StringUtils.isNotBlank(result)) {
//                JSONObject jsonObject = JSONObject.parseObject(result);
//                String status = jsonObject.getString("status");
//                if ("ok".equals(status)) {
//                    JSONObject detailObject = jsonObject.getJSONObject("detail");
//                    if (detailObject != null) {
//                        JSONObject userPermissionObject = detailObject.getJSONObject(userId);
//                        if (userPermissionObject != null) {
//                            JSONObject envJsonObject = userPermissionObject.getJSONObject("env");
//                            Set<String> envSetStrings = envJsonObject.keySet();
//                            if (envSetStrings.size() > 0) {
//                                List<ModuleEnv> envList = envMapper.getEnvIdByPaasId();
//                                Map<Integer, Integer> envPaasMap = new HashMap<>();
//                                if (envList != null && envList.size() > 0) {
//                                    for (ModuleEnv env : envList) {
//                                        envPaasMap.put(env.getPaasId(), env.getId());
//                                    }
//                                    Iterator<String> iterator = envSetStrings.iterator();
//                                    while (iterator.hasNext()) {
//                                        ModuleUserPermissionDTO permissionDTO = new ModuleUserPermissionDTO();
//                                        String next = iterator.next();
//                                        Integer paasId = Integer.valueOf(next);
//                                        Integer envId = envPaasMap.get(Integer.valueOf(next));
//                                        if (envId != null) {
//                                            permissionDTO.setEnvId(envId);
//                                            JSONObject object = envJsonObject.getJSONObject(next);
//                                            JSONArray smallArray = object.getJSONArray("popdomsmall");
//                                            List<String> permissionList = smallArray.toJavaList(String.class);
//                                            HashSet<String> permissionSet = new HashSet<>(permissionList);
//                                            String envName = object.getString("paasname");
//                                            permissionDTO.setEnvName(envName);
//                                            permissionDTO.setPermissionSet(permissionSet);
//                                            if (pspaasIdList.contains(paasId)) {
//                                                prodPermissionDTOS.add(permissionDTO);
//                                            } else {
//                                                testPermissionDTOS.add(permissionDTO);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if (prodPermissionDTOS.size() > 0 || testPermissionDTOS.size() > 0) {
//                        userPermissionMap.put("PROD", prodPermissionDTOS);
//                        userPermissionMap.put("TEST", testPermissionDTOS);
//                        ModulePermissionUser permissionUser = new ModulePermissionUser();
//                        permissionUser.setUserId(userId);
//                        permissionUser.setCreateTime(new Date());
//                        permissionUser.setUserPermissionInfo(JSONObject.toJSONString(userPermissionMap));
//                        userMapper.insertPermission(permissionUser);
//                    }
//                }
//            }
//        }
//        return isProdEnv ? prodPermissionDTOS : testPermissionDTOS;
//    }
  
  /**
   * 获取用户对应的所有权限并集,也就是所有环境的权限
   *
   * @param
   * @return
   */
  @Override
  public Set<String> getAllPermission(String userId) {
    List<ModuleUserPermissionDTO> userEnvPermissions =
        pModuleUserService.getAllPermissionsByUserId(userId);
//        List<ModuleUserPermissionDTO> userEnvPermissions = getUserEnvPermission(userId);
    Set<String> permissionSet = new HashSet<>();
    if (userEnvPermissions != null && userEnvPermissions.size() > 0) {
      for (ModuleUserPermissionDTO moduleUserPermissionDTO : userEnvPermissions) {
        permissionSet.addAll(moduleUserPermissionDTO.getPermissionSet());
      }
      userEnvPermissions.clear();
    }
    return permissionSet;
  }
  
}
