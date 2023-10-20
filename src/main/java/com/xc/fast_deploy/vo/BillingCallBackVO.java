package com.xc.fast_deploy.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class BillingCallBackVO implements Serializable {
  
  private static final long serialVersionUID = 3032774780168163641L;
  private Integer globalStaus;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date finishTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;
  
  private Integer callType;
  
  private List<BillingPackageVo> packages;
}
