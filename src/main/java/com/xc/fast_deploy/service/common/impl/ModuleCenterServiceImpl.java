package com.xc.fast_deploy.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.MyPageInfo;

import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleCenterEnvDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleCenter;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.example.ModuleCenterExample;
import com.xc.fast_deploy.model.master_model.example.ModuleManageExample;
import com.xc.fast_deploy.myException.NotDeleteOkException;
import com.xc.fast_deploy.service.common.ModuleCenterService;
import com.xc.fast_deploy.utils.BufferModuleMangeUtils;
import com.xc.fast_deploy.utils.FoldUtils;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleCenterSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class ModuleCenterServiceImpl extends BaseServiceImpl<ModuleCenter, Integer> implements ModuleCenterService {
  
  @Autowired
  private ModuleCenterMapper centerMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private PModuleUserMapper userMapper;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  @PostConstruct
  public void init() {
    super.init(centerMapper);
  }
  
  /**
   * 分页条件查询
   *
   * @param pageNum
   * @param pageSize
   * @param moduleCenterSelectParamVo
   * @return
   */
  @Override
  public MyPageInfo<ModuleCenterEnvDTO> selectPageCen(Integer pageNum, Integer pageSize, ModuleCenterSelectParamVo moduleCenterSelectParamVo) {
    MyPageInfo<ModuleCenterEnvDTO> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<ModuleCenterEnvDTO> centerEnvDTOS = centerMapper.selectByParamVo(moduleCenterSelectParamVo);
      PageInfo<ModuleCenterEnvDTO> pageInfo = new PageInfo<>(centerEnvDTOS);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  /**
   * 查询所有可用的中心与模块的关系
   *
   * @param envIds
   * @return
   */
  @Override
  public List<ModuleDeployEnvDTO> selectAllCenterModule(Set<Integer> envIds) {
    List<ModuleEnvCenterManageVo> centerManageVos = centerMapper.selectCenterModule(envIds);
//        List<ModuleDeployEnvDTO> deployEnvDTOS = BufferModuleMangeUtils.bufferModuleEnvCenterManage(centerManageVos);
//        Set<String> centerNameSet = new HashSet<>();
//        if (deployEnvDTOS != null && deployEnvDTOS.size() > 0) {
//            for (ModuleDeployEnvDTO moduleDeployEnvDTO : deployEnvDTOS) {
//                List<ModuleCenterEnvDTO> centerList = moduleDeployEnvDTO.getCenterList();
//                if (centerList != null && centerList.size() > 0) {
//                    for (ModuleCenterEnvDTO moduleCenterEnvDTO : centerList) {
//                        String centerName = moduleCenterEnvDTO.getCenterName();
//                        if (!centerNameSet.contains(centerName)) {
//                            List<ModuleCenterDTO> centerDTOS = new ArrayList<>();
//                            ModuleCenterDTO centerDTO = new ModuleCenterDTO();
//                            centerDTO.setCenterId(moduleCenterEnvDTO.getCenterId());
//                            centerDTO.setChildCenterName(moduleCenterEnvDTO.getChildCenterName());
//                            centerDTO.setCenterName(null);
//                            centerDTO.setManageList(moduleCenterEnvDTO.getManageList());
//                            centerDTOS.add(centerDTO);
//
//                            moduleCenterEnvDTO.setCenterDTOList(centerDTOS);
//                            moduleCenterEnvDTO.setManageList(null);
//                            centerNameSet.add(centerName);
//                        } else {
//                            ModuleCenterDTO centerDTO = new ModuleCenterDTO();
//                            centerDTO.setCenterId(moduleCenterEnvDTO.getCenterId());
//                            centerDTO.setChildCenterName(moduleCenterEnvDTO.getChildCenterName());
//                            centerDTO.setCenterName(null);
//                            centerDTO.setManageList(moduleCenterEnvDTO.getManageList());
//                            moduleCenterEnvDTO.getCenterDTOList().add(centerDTO);
//                            moduleCenterEnvDTO.setManageList(null);
//                        }
//                    }
//                }
//            }
//        }
    return BufferModuleMangeUtils.bufferModuleEnvCenterManage(centerManageVos);
  }
  
  /**
   * 删除一条中心信息
   *
   * @param id
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO delInfoById(Integer id) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    ModuleManageExample manageExample = new ModuleManageExample();
    ModuleManageExample.Criteria criteria = manageExample.createCriteria();
    criteria.andCenterIdEqualTo(id).andIsDeleteEqualTo(0);
    List<ModuleManage> manageList = manageMapper.selectByExample(manageExample);
    if (manageList != null && manageList.size() > 0) {
      responseDTO.fail("该中心下仍然可用模块,不可删除");
    } else {
      ModuleCenter center = centerMapper.selectByPrimaryKey(id);
      center.setIsDeleted(1);
      centerMapper.deleteByPrimaryKey(center.getId());
      if (FoldUtils.deleteFolders(storgePrefix + center.getCenterPath())) {
        responseDTO.success();
      } else {
        throw new NotDeleteOkException("中心目录删除失败");
      }
    }
    return responseDTO;
  }
  
  /**
   * 更新一条数据
   *
   * @param moduleCenter
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public ResponseDTO updateCenterInfo(ModuleCenter moduleCenter) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败");
    ModuleCenter center = centerMapper.selectByPrimaryKey(moduleCenter.getId());
    if (center != null) {
      ModuleCenter changeCenter = new ModuleCenter();
      changeCenter.setId(moduleCenter.getId());
      changeCenter.setRemark(moduleCenter.getRemark());
      changeCenter.setCenterName(moduleCenter.getCenterName());
      changeCenter.setChildCenterName(moduleCenter.getChildCenterName());
      //中心的环境不允许替换
      changeCenter.setEnvId(null);
      changeCenter.setUpdateTime(new Date());
      if (centerMapper.updateByPrimaryKeySelective(changeCenter) > 0) {
        responseDTO.success("更新成功!");
      }
    }
    return responseDTO;
  }
  
  /**
   * 添加一条中心信息
   *
   * @param moduleCenter
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO insertOneInfo(ModuleCenter moduleCenter) {
    ResponseDTO responseDTO = new ResponseDTO();
    ModuleEnv moduleEnv = envMapper.selectByPrimaryKey(moduleCenter.getEnvId());
    if (moduleEnv == null) {
      responseDTO.fail("该环境不存在");
      return responseDTO;
    }
    ModuleCenterExample centerExm = new ModuleCenterExample();
    centerExm.createCriteria()
        .andChildCenterNameEqualTo(moduleCenter.getChildCenterName())
        .andIsDeletedEqualTo(0);
    List<ModuleCenter> centers = centerMapper.selectByExample(centerExm);
    if (centers != null && centers.size() > 0) {
      for (ModuleCenter center : centers) {
        if (!center.getChildCenterContentName().equals(moduleCenter.getChildCenterContentName())) {
          responseDTO.fail("同中心名称的中心目录不一致,请将目录修改为:" +
              center.getChildCenterContentName());
          return responseDTO;
        }
      }
    }
    ModuleCenterExample centerExample = new ModuleCenterExample();
    centerExample.createCriteria().andEnvIdEqualTo(moduleCenter.getEnvId())
        .andChildCenterContentNameEqualTo(moduleCenter.getChildCenterContentName())
        .andIsDeletedEqualTo(0);
    
    List<ModuleCenter> centerList = centerMapper.selectByExample(centerExample);
    
    if (centerList != null && centerList.size() > 0) {
      responseDTO.fail("该环境下center_content_name_code不能重复");
      return responseDTO;
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append(storgePrefix).append(moduleEnv.getEnvCode())
          .append(moduleEnv.getId()).append(FoldUtils.SEP)
          .append(moduleCenter.getChildCenterContentName());
      File file = new File(sb.toString());
      if (file.exists()) {
        responseDTO.fail("该环境下center目录不能重复创建");
        return responseDTO;
      } else {
        boolean b = file.mkdirs();
        log.info("创建文件夹 : {}", b);
      }
      //存储的CenterPath是不包含storgePrefix前缀的 这样方便重新生成新的目录结构
      moduleCenter.setCenterPath(sb.toString().replace(storgePrefix, ""));
      moduleCenter.setCreateTime(new Date());
      moduleCenter.setUpdateTime(new Date());
      centerMapper.insertSelective(moduleCenter);
      responseDTO.success("添加成功");
    }
    return responseDTO;
  }
  
  /**
   * 根据环境id查询该环境下对应的所有中心
   *
   * @param envId
   * @return
   */
  @Override
  public List<ModuleCenter> selectAllCenterByEnvId(Integer envId, String userId) {
    ModuleCenterExample centerExample = new ModuleCenterExample();
    centerExample.createCriteria().andEnvIdEqualTo(envId).andIsDeletedEqualTo(0);
    Set<Integer> centers = userMapper.selectUserIdAllCenters(userId, envId);
    if (!centers.isEmpty() && !centers.contains(null)) {
      centerExample.clear();
      centerExample.createCriteria().andEnvIdEqualTo(envId).andIsDeletedEqualTo(0)
          .andIdIn(new ArrayList<>(centers));
    }
    return centerMapper.selectByExample(centerExample);
  }
  
}
