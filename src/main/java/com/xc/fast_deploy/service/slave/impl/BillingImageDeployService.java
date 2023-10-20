package com.xc.fast_deploy.service.slave.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.slave_dao.BillingImageDeployMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.model.slave_model.BillingImageDeploy;
import com.xc.fast_deploy.myenum.BillingImageReviewEnum;
import com.xc.fast_deploy.service.slave.IBillingImageDeployService;
import com.xc.fast_deploy.vo.module_vo.param.BillingImageSelectParamVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BillingImageDeployService implements IBillingImageDeployService {
  
  @Autowired
  private BillingImageDeployMapper imageDeployMapper;
  
  @Override
  public MyPageInfo<BillingImageDeploy> selectBillingImagePage(int pageNum, int pageSize,
                                                               BillingImageSelectParamVo imageSelectParamVo) {
    MyPageInfo<BillingImageDeploy> myPageInfo = new MyPageInfo<>();
    if (pageNum > 0 && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<BillingImageDeploy> imageDeploys = imageDeployMapper.selectPageInfo(imageSelectParamVo);
      PageInfo<BillingImageDeploy> pageInfo = new PageInfo<>(imageDeploys);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateBatchImageReviewed(Integer envId, Integer[] imageIds, Integer reviewCode) {
    boolean success = false;
    //根据环境id来操作对应镜像的审核
    if (imageIds != null && imageIds.length > 0) {
      for (Integer imageId : imageIds) {
        //只能改变待审核的镜像的状态
        BillingImageDeploy imageDeploy = imageDeployMapper.selectByPrimaryKey(Long.valueOf(imageId));
        if (imageDeploy != null) {
          BillingImageDeploy billingImageDeploy = new BillingImageDeploy();
          billingImageDeploy.setIsReviewed(reviewCode);
          billingImageDeploy.setReviewTime(new Date());
          billingImageDeploy.setUpdateTime(new Date());
          billingImageDeploy.setId(Long.valueOf(imageId));
          imageDeployMapper.updateByPrimaryKeySelective(billingImageDeploy);
          success = true;
        }
      }
    }
    return success;
  }
}
