package com.xc.fast_deploy.dao.slave_dao;

import com.xc.fast_deploy.model.slave_model.BillingImageDeploy;
import com.xc.fast_deploy.model.slave_model.example.BillingImageDeployExample;
import com.xc.fast_deploy.vo.module_vo.param.BillingImageSelectParamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BillingImageDeployMapper {
  int countByExample(BillingImageDeployExample example);
  
  int deleteByExample(BillingImageDeployExample example);
  
  int deleteByPrimaryKey(Long id);
  
  int insert(BillingImageDeploy record);
  
  int insertSelective(BillingImageDeploy record);
  
  List<BillingImageDeploy> selectByExample(BillingImageDeployExample example);
  
  BillingImageDeploy selectByPrimaryKey(Long id);
  
  int updateByExampleSelective(@Param("record") BillingImageDeploy record, @Param("example") BillingImageDeployExample example);
  
  int updateByExample(@Param("record") BillingImageDeploy record, @Param("example") BillingImageDeployExample example);
  
  int updateByPrimaryKeySelective(BillingImageDeploy record);
  
  int updateByPrimaryKey(BillingImageDeploy record);
  
  List<BillingImageDeploy> selectPageInfo(BillingImageSelectParamVo imageSelectParamVo);
  
}