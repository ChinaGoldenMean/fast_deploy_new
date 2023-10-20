package com.xc.fast_deploy.dto.jenkins;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JobDetailsDTO implements Serializable {
  private static final long serialVersionUID = 9113247121598744143L;
  
  private String jobName;
  
  private Integer jobId;
  
  private List<JobBuildDetailsDTO> buildDetails;
  
}
