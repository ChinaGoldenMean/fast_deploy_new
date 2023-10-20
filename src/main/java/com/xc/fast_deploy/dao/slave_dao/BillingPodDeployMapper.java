package com.xc.fast_deploy.dao.slave_dao;

import com.xc.fast_deploy.dto.module.BillingPodDeployDTO;
import com.xc.fast_deploy.model.slave_model.BillingPodDeploy;
import com.xc.fast_deploy.model.slave_model.example.BillingPodDeployExample;
import com.xc.fast_deploy.vo.module_vo.param.BillingPodDeployParamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BillingPodDeployMapper {
  int countByExample(BillingPodDeployExample example);
  
  int deleteByExample(BillingPodDeployExample example);
  
  int deleteByPrimaryKey(Long id);
  
  int insert(BillingPodDeploy record);
  
  int insertSelective(BillingPodDeploy record);
  
  List<BillingPodDeploy> selectByExampleWithBLOBs(BillingPodDeployExample example);
  
  List<BillingPodDeploy> selectByExample(BillingPodDeployExample example);
  
  BillingPodDeploy selectByPrimaryKey(Long id);
  
  int updateByExampleSelective(@Param("record") BillingPodDeploy record, @Param("example") BillingPodDeployExample example);
  
  int updateByExampleWithBLOBs(@Param("record") BillingPodDeploy record, @Param("example") BillingPodDeployExample example);
  
  int updateByExample(@Param("record") BillingPodDeploy record, @Param("example") BillingPodDeployExample example);
  
  int updateByPrimaryKeySelective(BillingPodDeploy record);
  
  int updateByPrimaryKeyWithBLOBs(BillingPodDeploy record);
  
  int updateByPrimaryKey(BillingPodDeploy record);
  
  BillingPodDeploy selectInfoByPodName(BillingPodDeploy param);
  
  List<BillingPodDeployDTO> selectPageInfo(BillingPodDeployParamVo billingPodDeployParamVo);
}