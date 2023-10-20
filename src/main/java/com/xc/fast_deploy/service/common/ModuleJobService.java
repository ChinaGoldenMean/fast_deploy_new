package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.jenkins.JobDetailsDTO;
import com.xc.fast_deploy.dto.module.ModuleJobDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleJob;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleMirror;
import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleJobParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleJobSelectParamVo;
import org.springframework.web.multipart.MultipartFile;
import org.yeauty.pojo.Session;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ModuleJobService extends BaseService<ModuleJob, Integer> {
    
    //新增一个job
    boolean saveJob(ModuleJobParamVo paramVo, ModuleManage manage, MultipartFile dockerfile, MultipartFile compilerFile);
    
    //开始构建一个任务
    boolean runJob(Integer jobId, Session session,
                   String token, boolean isUpdateAllCode, boolean isNeedUpCode, Boolean isPromptly, String needIdStr, Boolean isOffline) throws Exception;
    
    //存储镜像生成信息
    // Integer saveMirrorInfo(ModuleJob moduleJob, String mirrorTag,Integer buildNumber);
    
    MyPageInfo<ModuleJobVo> selectPageAllInfo(Integer pageNum, Integer pageSize, ModuleJobSelectParamVo selectParamVo);
    
    ModuleJobDTO getOneJobById(Integer jobId);
    
    boolean deleteJobById(Integer jobId);
    
    JobDetailsDTO getJobDetails(Integer jobId, ModuleJob moduleJob);
    
    StringBuilder getConsoleOutput(Integer jobId, Integer buildNumber, ModuleJob moduleJob);
    
    List<ModuleMirror> getAvailableMirrorByJobId(Integer jobId);
    
    List<ModuleManageDTO> selectJobModuleByEnvId(Integer envId);
    
    ModuleManageDTO selectJobModuleByModuleAndEnvId(Integer envId, Integer moduleId);
    
    ModuleJob selectJobByModuleAndEnvId(Integer envId, Integer moduleId);
    
    boolean updateJobInfo(ModuleJobParamVo paramVo, MultipartFile dockerfile, MultipartFile complieFile);
    
    ResponseDTO stopJob(Integer moduleId, Integer envId);
    
}
