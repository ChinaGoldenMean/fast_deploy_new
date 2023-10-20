package com.xc.fast_deploy.utils;

import com.xc.fast_deploy.dto.module.ModuleCenterDTO;
import com.xc.fast_deploy.dto.module.ModuleCenterEnvDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BufferModuleMangeUtils {
  
  /**
   * 数据格式转换重新封装
   *
   * @param deployList
   * @return
   */
  public static List<ModuleDeployEnvDTO> bufferModuleEnvCenterManage(List<ModuleEnvCenterManageVo> deployList) {
    List<ModuleDeployEnvDTO> deployEnvDTOS;
    Map<Integer, ModuleDeployEnvDTO> deployEnvDTOMap = new HashMap<>();
    if (deployList != null && deployList.size() > 0) {
      for (ModuleEnvCenterManageVo moduleEnvCenterManageVo : deployList) {
        ModuleManage moduleManage = new ModuleManage();
        moduleManage.setModuleName(moduleEnvCenterManageVo.getModuleName());
        moduleManage.setId(moduleEnvCenterManageVo.getModuleId());
        moduleManage.setEnvId(moduleEnvCenterManageVo.getEnvId());
        //环境分离
        if (!deployEnvDTOMap.containsKey(moduleEnvCenterManageVo.getEnvId())) {
          //如果不包含的话说明是一个新的环境 所有的东西都需要新建
          ModuleDeployEnvDTO deployEnvDTO = new ModuleDeployEnvDTO();
          deployEnvDTO.setEnvId(moduleEnvCenterManageVo.getEnvId());
          deployEnvDTO.setEnvName(moduleEnvCenterManageVo.getEnvName());
          deployEnvDTO.setEnvCode(moduleEnvCenterManageVo.getEnvCode());
          List<ModuleCenterEnvDTO> centerEnvDTOList = new ArrayList<>();
          List<ModuleManage> manageList = new ArrayList<>();
          manageList.add(moduleManage);
          //设置中心
          ModuleCenterEnvDTO centerEnvDTO = new ModuleCenterEnvDTO();
          centerEnvDTO.setCenterName(moduleEnvCenterManageVo.getCenterName());
          //设置子中心
          List<ModuleCenterDTO> centerDTOS = new ArrayList<>();
          ModuleCenterDTO centerDTO = new ModuleCenterDTO();
          centerDTO.setManageList(manageList);
          centerDTO.setCenterId(moduleEnvCenterManageVo.getCenterId());
          centerDTO.setChildCenterName(moduleEnvCenterManageVo.getChildCenterName());
          centerDTOS.add(centerDTO);
          centerEnvDTO.setCenterDTOList(centerDTOS);
          centerEnvDTOList.add(centerEnvDTO);
          deployEnvDTO.setCenterList(centerEnvDTOList);
          deployEnvDTOMap.put(moduleEnvCenterManageVo.getEnvId(), deployEnvDTO);
        } else {
          List<ModuleCenterEnvDTO> centerList =
              deployEnvDTOMap.get(moduleEnvCenterManageVo.getEnvId()).getCenterList();
          Integer key = null;
          //遍历中心
          for (int i = 0; i < centerList.size(); i++) {
            if (centerList.get(i).getCenterName().equals(moduleEnvCenterManageVo.getCenterName())) {
              key = i;
              break;
            }
          }
          //已有对应中心的数据
          if (key != null) {
            List<ModuleCenterDTO> centerDTOList =
                deployEnvDTOMap.get(moduleEnvCenterManageVo.getEnvId())
                    .getCenterList().get(key).getCenterDTOList();
            //然后判断是否有对应子中心的数据
            Integer key2 = null;
            //遍历子中心
            for (int i = 0; i < centerDTOList.size(); i++) {
              if (centerDTOList.get(i).getCenterId()
                  .equals(moduleEnvCenterManageVo.getCenterId())) {
                key2 = i;
                break;
              }
            }
            //已有子中心数据
            if (key2 != null) {
              deployEnvDTOMap.get(moduleEnvCenterManageVo.getEnvId())
                  .getCenterList()
                  .get(key)
                  .getCenterDTOList()
                  .get(key2)
                  .getManageList()
                  .add(moduleManage);
            } else {
              //无子中心数据
              ModuleCenterDTO centerDTO = new ModuleCenterDTO();
              List<ModuleManage> manageList = new ArrayList<>();
              manageList.add(moduleManage);
              centerDTO.setManageList(manageList);
              centerDTO.setCenterId(moduleEnvCenterManageVo.getCenterId());
              centerDTO.setChildCenterName(moduleEnvCenterManageVo.getChildCenterName());
              deployEnvDTOMap.get(moduleEnvCenterManageVo.getEnvId())
                  .getCenterList().get(key).getCenterDTOList().add(centerDTO);
            }
          } else {
            //中心暂无 创建新的中心
            ModuleCenterEnvDTO centerEnvDTO = new ModuleCenterEnvDTO();
            centerEnvDTO.setCenterName(moduleEnvCenterManageVo.getCenterName());
            List<ModuleManage> manageList = new ArrayList<>();
            manageList.add(moduleManage);
            //创建新的子中心
            List<ModuleCenterDTO> centerDTOS = new ArrayList<>();
            ModuleCenterDTO centerDTO = new ModuleCenterDTO();
            centerDTO.setManageList(manageList);
            centerDTO.setCenterId(moduleEnvCenterManageVo.getCenterId());
            centerDTO.setChildCenterName(moduleEnvCenterManageVo.getChildCenterName());
            centerDTOS.add(centerDTO);
            centerEnvDTO.setCenterDTOList(centerDTOS);
            deployEnvDTOMap
                .get(moduleEnvCenterManageVo
                    .getEnvId())
                .getCenterList()
                .add(centerEnvDTO);
          }
        }
      }
    }
    Collection<ModuleDeployEnvDTO> values = deployEnvDTOMap.values();
    deployEnvDTOS = new ArrayList<>(values);
    return deployEnvDTOS;
  }
}
