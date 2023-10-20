package com.xc.fast_deploy.service.permission.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.master_dao.ModuleCenterMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dao.master_dao.PModuleRoleMapper;
import com.xc.fast_deploy.dao.master_dao.PModuleUserMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.permission.ModulePermissionDTO;
import com.xc.fast_deploy.dto.module.permission.ModuleUserPermissionDTO;
import com.xc.fast_deploy.model.master_model.ModuleCenter;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.PModuleRole;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.myException.ServiceException;
import com.xc.fast_deploy.myException.ValidateExcetion;
import com.xc.fast_deploy.service.permission.PModuleUserService;
import com.xc.fast_deploy.utils.encyption_utils.Pbkdf2Sha256;
import com.xc.fast_deploy.vo.module_vo.param.PModuleUserSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleDto;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleVo;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PModuleUserServiceImpl implements PModuleUserService {
  
  @Autowired
  private PModuleUserMapper userMapper;
  @Autowired
  private PModuleRoleMapper roleMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleCenterMapper centerMapper;
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  
  /**
   * 注册用户信息
   *
   * @param moduleUser
   * @return
   */
  @Override
  public ResponseDTO addUser(PModuleUser moduleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    synchronized (this) {
      //需要判断用户名不能重复
      PModuleUser user = userMapper.selectByUserNameAndCname(moduleUser.getUsername(), moduleUser.getCname());
      
      if (user != null) {
        responseDTO.fail(String.format("用户名重复: %s", user.getUsername()));
        return responseDTO;
      } else {
        //取出最新的user_id,然后+1
        PModuleUser lastUser = userMapper.selectLastOne();
        Integer user_id = Integer.valueOf(lastUser.getUserId().substring(5)) + 1;
        moduleUser.setCreateTime(new Date());
        moduleUser.setUpdateTime(new Date());
        moduleUser.setPassword(Pbkdf2Sha256.encode(moduleUser.getPassword()));
        //moduleUser.setUserId(UUID.randomUUID().toString().replace("-", ""));
        moduleUser.setUserId("ZJXCA" + (user_id >= 1000 ? user_id.toString() : "0" + user_id));
        userMapper.insertSelective(moduleUser);
        responseDTO.success("添加成功");
      }
    }
    return responseDTO;
  }
  
  /**
   * 通过用户名获取所有的用户权限信息
   *
   * @param username
   * @return
   */
  @Override
  public List<ModuleUserPermissionDTO> getAllPermissionsByUserName(String username) {
    //判断当前环境是否为生产环境
    Integer isProd = isProdEnv ? 1 : 0;
    if (StringUtils.isBlank(username)) {
      throw new ServiceException("用户名不能为空");
    }
    List<ModulePermissionDTO> permissionDTOS = userMapper.selectPermissionByUserName(username, isProd);
    return wrapperPermDtoInfo(permissionDTOS);
  }
  
  /**
   * 通过用户名获取所有的用户权限信息
   *
   * @param userId
   * @return
   */
  @Override
  public List<ModuleUserPermissionDTO> getAllPermissionsByUserId(String userId) {
    Integer isProd = isProdEnv ? 1 : 0;
    List<ModulePermissionDTO> permissionDTOS = userMapper.selectPermissionByUserId(userId, isProd);
    return wrapperPermDtoInfo(permissionDTOS);
  }
  
  private List<ModuleUserPermissionDTO> wrapperPermDtoInfo(List<ModulePermissionDTO> permissionDTOS) {
    List<ModuleUserPermissionDTO> permissionDTOList = new ArrayList<>();
    if (permissionDTOS != null && permissionDTOS.size() > 0) {
      Map<Integer, ModuleUserPermissionDTO> permissionDTOMap =
          new HashMap<>(permissionDTOList.size());
      for (ModulePermissionDTO permissionDTO : permissionDTOS) {
        if (!permissionDTOMap.containsKey(permissionDTO.getEnvId())) {
          ModuleUserPermissionDTO dto = new ModuleUserPermissionDTO();
          dto.setEnvId(permissionDTO.getEnvId());
          dto.setEnvName(permissionDTO.getEnvName());
          Set<String> permissionSet = new HashSet<>();
          permissionSet.add(permissionDTO.getAction());
          dto.setPermissionSet(permissionSet);
          permissionDTOList.add(dto);
          permissionDTOMap.put(permissionDTO.getEnvId(), dto);
        } else {
          ModuleUserPermissionDTO dto = permissionDTOMap.get(permissionDTO.getEnvId());
          dto.getPermissionSet().add(permissionDTO.getAction());
        }
      }
      permissionDTOMap.clear();
    }
    return permissionDTOList;
  }
  
  /**
   * 添加用户绑定角色以及环境关联信息
   *
   * @param userId
   * @param roleId
   * @param envIds
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO addUserBindRole(Integer userId, Integer roleId,
                                     Integer[] envIds, Integer[] centerIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("添加失败");
    //判断该用户是否存在
    synchronized (this) {
      PModuleUser pModuleUser = userMapper.selectByPrimaryKey(userId);
      if (pModuleUser != null) {
        PModuleRole pModuleRole = roleMapper.selectByPrimaryKey(roleId);
        if (pModuleRole != null) {
          Set<Integer> ids = new HashSet<>(Arrays.asList(envIds));
          List<ModuleEnv> envList = envMapper.selectEnvAll(ids);
          if (ids.size() != envList.size()) {
            responseDTO.fail("环境不存在异常");
            return responseDTO;
          }
          //根据用户和角色判断在哪些环境下已经是哪些角色
          //  List<ModuleUserEnvRoleVo> userEnvRoleVos = userMapper.selectRoleBindUser(userId, roleId);
          //如果当前用户已经和某个角色绑定了,则不能重复绑定 即
//                    if (userEnvRoleVos.size() > 0) {
//                        responseDTO.fail("当前用户已与该角色绑定,不可重复绑定");
//                        return responseDTO;
//                    }
          List<ModuleUserEnvRoleVo> EnvRoleVos = new ArrayList<>();
          Set<Integer> cenIds = new HashSet<>(Arrays.asList(centerIds));
          //去重复之后添加进入用户对应的角色
          if (cenIds.isEmpty()) {
            for (ModuleEnv env : envList) {
              ModuleUserEnvRoleVo envRoleVo = new ModuleUserEnvRoleVo();
              envRoleVo.setEnvId(env.getId());
              envRoleVo.setUserId(userId);
              envRoleVo.setRoleId(roleId);
              //绑定关系若已存在直接跳过
              List<ModuleUserEnvRoleVo> userEnvRoleVos =
                  userMapper.selectUserRoleBind(envRoleVo);
              if (userEnvRoleVos != null && userEnvRoleVos.size() > 0) continue;
              EnvRoleVos.add(envRoleVo);
            }
          } else {
            for (ModuleEnv env : envList) {
              for (Integer centerId : cenIds) {
                ModuleCenter center = centerMapper.selectByPrimaryKey(centerId);
                if (center == null) continue;
                ModuleUserEnvRoleVo envRoleVo = new ModuleUserEnvRoleVo();
                envRoleVo.setEnvId(env.getId());
                envRoleVo.setUserId(userId);
                envRoleVo.setRoleId(roleId);
                envRoleVo.setCenterId(centerId);
                List<ModuleUserEnvRoleVo> userEnvRoleVos =
                    userMapper.selectUserRoleBind(envRoleVo);
                if (userEnvRoleVos != null && userEnvRoleVos.size() > 0) continue;
                EnvRoleVos.add(envRoleVo);
              }
              
            }
          }
          if (EnvRoleVos.size() > 0) {
            userMapper.insertUserRoleEnvBatch(EnvRoleVos);
            responseDTO.success("添加成功");
          }
        }
      }
    }
    return responseDTO;
  }
  
  @Override
  public ResponseDTO batchAddUserRoleBind(String userNames, Integer roleId, Integer envId, String centerNames) {
    Integer[] userIdArray = toUserIds(userNames.split(","));
    String[] centerNameArray = centerNames.split(",");
    Integer[] centerIds = centerMapper.findCenterIdByCenterName(centerNameArray, envId);
    if (centerIds.length != centerNameArray.length || userNames.split(",").length != userIdArray.length) {
      throw new ValidateExcetion("环境名称对应不上");
    }
    for (int i = 0; i < userIdArray.length; i++) {
      addUserBindRole(userIdArray[i], roleId, new Integer[]{envId}, centerIds);
    }
    return null;
  }
  
  private Integer[] toUserIds(String[] userNameArray) {
    Integer[] userIds = new Integer[userNameArray.length];
    for (int i = 0; i < userNameArray.length; i++) {
      PModuleUser user = userMapper.selectByUserName(userNameArray[i]);
      userIds[i] = user.getId();
    }
    return userIds;
  }
  
  /**
   * 根据用户名查询用户数据信息
   *
   * @param username
   * @return
   */
  @Override
  public PModuleUser selectUserByName(String username) {
    return userMapper.selectByUserName(username);
  }
  
  @Override
  public int updateByPrimaryKeySelective(PModuleUser moduleUser) {
    return userMapper.updateByPrimaryKeySelective(moduleUser);
  }
  
  /**
   * 更新用户绑定角色以及环境信息
   *
   * @param userRoleBindId
   * @param userId
   * @param roleId
   * @param envIds
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO updateUserRoleBind(Integer userRoleBindId, Integer userId,
                                        Integer roleId, Integer[] envIds, Integer[] centerIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败!");
    //首先需要验证用户是否存在
    synchronized (this) {
      PModuleUser pModuleUser = userMapper.selectByPrimaryKey(userId);
      if (pModuleUser != null) {
        PModuleRole pModuleRole = roleMapper.selectByPrimaryKey(roleId);
        //原有的userId和roleId查询结果
        //ModuleUserEnvRoleVo envRoleVo = userMapper.selectUserRoleBindInfoById(userRoleBindId);
        ModuleUserEnvRoleVo envRoleVo = new ModuleUserEnvRoleVo();
        envRoleVo.setUserId(userId);
        envRoleVo.setRoleId(roleId);
        if (pModuleRole != null) {
          //删除原有的关联数据
          userMapper.deleteUserRoleDataByEnvRoleVo(envRoleVo);
          Set<Integer> ids = new HashSet<>(Arrays.asList(envIds));
          ids.removeIf(Objects::isNull);
          if (ids.size() > 0) {
            List<ModuleEnv> envList = envMapper.selectEnvAll(ids);
            if (ids.size() != envList.size()) {
              responseDTO.fail("环境存在异常");
              return responseDTO;
            }
            envRoleVo.setRoleId(null);
            Set<Integer> cenIds = new HashSet<>(Arrays.asList(centerIds));
            List<ModuleUserEnvRoleVo> userEnvRoleVos;
            for (ModuleEnv env : envList) {
              envRoleVo.setEnvId(env.getId());
              //查看对应环境是否已有绑定关系，如果有将该环境移除
              userEnvRoleVos = userMapper.selectUserRoleBind(envRoleVo);
              if (userEnvRoleVos.size() > 0) {
                ids.remove(env.getId());
              }
              if (ids.size() == 0) {
                return responseDTO.fail("不可重复绑定");
              }
            }
            //添加新的关联数据
            List<ModuleUserEnvRoleVo> EnvRoleVos = new ArrayList<>();
            if (centerIds == null) {
              for (Integer envId : ids) {
                envRoleVo = new ModuleUserEnvRoleVo();
                envRoleVo.setEnvId(envId);
                envRoleVo.setUserId(userId);
                envRoleVo.setRoleId(roleId);
                EnvRoleVos.add(envRoleVo);
              }
            } else {
              for (Integer envId : ids) {
                for (Integer centerId : cenIds) {
                  envRoleVo = new ModuleUserEnvRoleVo();
                  envRoleVo.setEnvId(envId);
                  envRoleVo.setUserId(userId);
                  envRoleVo.setRoleId(roleId);
                  envRoleVo.setCenterId(centerId);
                  EnvRoleVos.add(envRoleVo);
                }
                
              }
            }
            userMapper.insertUserRoleEnvBatch(EnvRoleVos);
          }
          responseDTO.success("修改成功");
        }
      }
    }
    return responseDTO;
  }
  
  /**
   * 分页查询用户角色环境关联信息
   *
   * @param pageNum
   * @param pageSize
   * @param userEnvRoleParamVo
   * @return
   */
  @Override
  public MyPageInfo<ModuleUserEnvRoleDto> selectUserRoleBindPageAllInfo(
      Integer pageNum, Integer pageSize,
      ModuleUserEnvRoleParamVo userEnvRoleParamVo) {
    MyPageInfo<ModuleUserEnvRoleDto> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<ModuleUserEnvRoleDto> list = userMapper.selectUserEnvRoleVoPageByVo(userEnvRoleParamVo);
      // List<ModuleUserEnvRoleDto> list = userMapper.selectUserEnvRolePageByVo(userEnvRoleParamVo);
//            for (int i=0;i < list.size(); i++){
//                if (list.get(i).getCenterId() != null){
//                    ModuleCenter center = centerMapper.selectByPrimaryKey(list.get(i).getCenterId());
//                    list.get(i).setCenterNames(center.getChildCenterName());
//                }
//            }
      PageInfo<ModuleUserEnvRoleDto> pageInfo = new PageInfo<>(list);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  /**
   * 批量删除用户绑定关系
   *
   * @param userRoleBindIds
   * @return
   */
  @Override
  public boolean deleteUserRoleBind(Integer[] userRoleBindIds) {
    Set<Integer> bindIds = new HashSet<>(Arrays.asList(userRoleBindIds));
    bindIds.removeIf(Objects::isNull);
    if (bindIds != null && bindIds.size() > 0) {
      if (userMapper.deleteUserRoleBind(bindIds) > 0) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * 删除用户信息 并级联删除用户环境关联信息
   *
   * @param userId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteUserInfoByUserId(Integer userId) {
    userMapper.deleteByPrimaryKey(userId);
    ModuleUserEnvRoleVo envRoleVo = new ModuleUserEnvRoleVo();
    envRoleVo.setUserId(userId);
    //删除用户关联角色对应的信息
    userMapper.deleteUserRoleDataByEnvRoleVo(envRoleVo);
    return true;
  }
  
  /**
   * 分页查询用户数据
   *
   * @param pageNum
   * @param pageSize
   * @param moduleUserSelectParamVo
   * @return
   */
  @Override
  public MyPageInfo<PModuleUser> selectPageAllInfo(Integer pageNum, Integer pageSize, PModuleUserSelectParamVo moduleUserSelectParamVo) {
    MyPageInfo<PModuleUser> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<PModuleUser> list =
          userMapper.selectUserPageByVo(moduleUserSelectParamVo);
      for (PModuleUser moduleUser : list) {
        moduleUser.setPassword(null);
        moduleUser.setUserPermissionInfo(null);
      }
      PageInfo<PModuleUser> pageInfo = new PageInfo<>(list);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  /**
   * 更新用户数据
   *
   * @param pModuleUser
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO updateUserInfo(PModuleUser pModuleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("参数输入错误!");
    if (pModuleUser.getId() != null) {
      PModuleUser moduleUser = userMapper.selectByPrimaryKey(pModuleUser.getId());
      if (moduleUser != null) {
        //表示密码数据有变更
        if (!moduleUser.getPassword().equals(pModuleUser.getPassword())) {
          //密码变更 重新设置密码
          pModuleUser.setPassword(Pbkdf2Sha256.encode(pModuleUser.getPassword()));
        }
        pModuleUser.setUpdateTime(new Date());
        //除开密码数据库 其他的数据直接全部替换 有数据库校验某些字段如user_id和username的唯一性
        userMapper.updateByPrimaryKeySelective(pModuleUser);
        responseDTO.success("修改成功");
      }
    }
    return responseDTO;
  }
  
  /**
   * 主键查询用户信息
   *
   * @param id
   * @return
   */
  @Override
  public PModuleUser selectUserInfoById(Integer id) {
    
    return userMapper.selectByPrimaryKey(id);
  }
  
  @Override
  public ResponseDTO changePass(String userId, String pass) {
    PModuleUser pModuleUser = userMapper.selectByUserId(userId);
    ResponseDTO responseDTO = new ResponseDTO();
    PModuleUser moduleUser = new PModuleUser();
    if (Pbkdf2Sha256.verification(pass, pModuleUser.getPassword())) {
      responseDTO.fail("不能和旧密码一致");
    } else {
      moduleUser.setId(pModuleUser.getId());
      moduleUser.setPassword(Pbkdf2Sha256.encode(pass));
      moduleUser.setUpdateTime(new Date());
      userMapper.updateByPrimaryKeySelective(moduleUser);
      responseDTO.success("修改成功");
    }
    return responseDTO;
  }
  
}
