package com.xc.fast_deploy.myenum;

public enum BillingImageReviewEnum {
  
  NOT_ACCESS_REVIEW(0, "未通过审核"),
  ACCESS_REVIEW(1, "通过审核"),
  WAIT_REVIEW(2, "待审核");
  
  private Integer code;
  private String msg;
  
  BillingImageReviewEnum(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getMsg() {
    return msg;
  }
  
  public void setMsg(String msg) {
    this.msg = msg;
  }
  
  public static BillingImageReviewEnum getTypeByCode(Integer imageReivewCode) {
    for (BillingImageReviewEnum imageReviewEnum : values()) {
      if (imageReviewEnum.getCode().equals(imageReivewCode)) {
        return imageReviewEnum;
      }
    }
    return null;
  }
}
