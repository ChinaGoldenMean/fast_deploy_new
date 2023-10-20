package com.xc.fast_deploy.service.slave;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.model.slave_model.BillingImageDeploy;
import com.xc.fast_deploy.vo.module_vo.param.BillingImageSelectParamVo;

public interface IBillingImageDeployService {
  
  /**
   * 分页查询镜像信息
   *
   * @param pageNum
   * @param pageSize
   * @param imageSelectParamVo
   * @return
   */
  MyPageInfo<BillingImageDeploy> selectBillingImagePage(int pageNum, int pageSize,
                                                        BillingImageSelectParamVo imageSelectParamVo);
  
  /**
   * 批量审核镜像的操作
   *
   * @param envId
   * @param imageIds
   * @param reviewCode
   * @return
   */
  boolean updateBatchImageReviewed(Integer envId, Integer[] imageIds, Integer reviewCode);
}
