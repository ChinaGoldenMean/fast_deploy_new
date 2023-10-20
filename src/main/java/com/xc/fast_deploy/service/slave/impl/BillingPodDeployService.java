package com.xc.fast_deploy.service.slave.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.slave_dao.BillingPodDeployMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.module.BillingPodDeployDTO;
import com.xc.fast_deploy.model.slave_model.BillingImageDeploy;
import com.xc.fast_deploy.model.slave_model.BillingPodDeploy;
import com.xc.fast_deploy.model.slave_model.example.BillingPodDeployExample;
import com.xc.fast_deploy.service.slave.IBillingPodDeployService;
import com.xc.fast_deploy.vo.module_vo.param.BillingPodDeployParamVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingPodDeployService implements IBillingPodDeployService {
  
  @Autowired
  private BillingPodDeployMapper billingPodDeployMapper;
  
  /**
   * 分页展示发布的pod信息
   *
   * @param pageNum
   * @param pageSize
   * @param billingPodDeployParamVo
   * @return
   */
  @Override
  public MyPageInfo<BillingPodDeployDTO> selectBillingPodDeployPage(
      int pageNum,
      int pageSize,
      BillingPodDeployParamVo billingPodDeployParamVo) {
    
    MyPageInfo<BillingPodDeployDTO> myPageInfo = new MyPageInfo<>();
    if (pageNum > 0 && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<BillingPodDeployDTO> imageDeploys = billingPodDeployMapper.selectPageInfo(billingPodDeployParamVo);
      PageInfo<BillingPodDeployDTO> pageInfo = new PageInfo<>(imageDeploys);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    
    return myPageInfo;
  }
  
  /**
   * 根据环境id和计费发布id获取pod的详细信息
   *
   * @param envId
   * @param billingPodId
   * @return
   */
  @Override
  public BillingPodDeploy selectBillingPodBlobs(Integer envId, Long billingPodId) {
    BillingPodDeployExample deployExample = new BillingPodDeployExample();
    BillingPodDeployExample.Criteria criteria = deployExample.createCriteria();
    criteria.andEnvIdEqualTo(envId).andIdEqualTo(billingPodId);
    List<BillingPodDeploy> deployList = billingPodDeployMapper.selectByExampleWithBLOBs(deployExample);
    if (deployList != null && deployList.size() > 0) {
      return deployList.get(0);
    }
    return null;
  }
  
}
