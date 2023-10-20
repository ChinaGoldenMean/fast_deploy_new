package com.xc.fast_deploy.service.slave;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.module.BillingPodDeployDTO;
import com.xc.fast_deploy.model.slave_model.BillingPodDeploy;
import com.xc.fast_deploy.vo.module_vo.param.BillingPodDeployParamVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IBillingPodDeployService {
  
  MyPageInfo<BillingPodDeployDTO> selectBillingPodDeployPage(int pageNum, int pageSize,
                                                             BillingPodDeployParamVo billingPodDeployParamVo);
  
  BillingPodDeploy selectBillingPodBlobs(Integer envId, Long billingPodId);
  
}
