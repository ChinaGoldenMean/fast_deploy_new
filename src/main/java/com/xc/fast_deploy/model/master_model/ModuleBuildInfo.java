package com.xc.fast_deploy.model.master_model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.util.Date;
//
//import com.mybatisflex.annotation.Id;
//import com.mybatisflex.annotation.KeyType;
//import com.mybatisflex.annotation.Table;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @TableName module_build_info
 */
@Data
public class ModuleBuildInfo implements Serializable {
  
  /**
   *
   */
  @ApiModelProperty("")
  private Integer id;
  /**
   * 创建时间
   */
  @ApiModelProperty("创建时间")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  /**
   * 创建人
   */
  @ApiModelProperty("创建人")
  private String creator;
  /**
   * 上一次构建状态
   */
  @ApiModelProperty("上一次构建状态")
  private String lastBuildStatusPrev;
  /**
   * 本次构建状态
   */
  @ApiModelProperty("本次构建状态")
  private String currentBuildStatus;
  /**
   * 模块id
   */
  @ApiModelProperty("模块id")
  private Integer moduleId;
  /**
   * 环境id
   */
  @ApiModelProperty("环境id")
  private Integer envId;
  
}
