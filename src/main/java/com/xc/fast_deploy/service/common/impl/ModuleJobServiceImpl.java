package com.xc.fast_deploy.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.Range;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.dto.jenkins.JobBuildDetailsDTO;
import com.xc.fast_deploy.dto.jenkins.JobDetailsDTO;
import com.xc.fast_deploy.dto.module.ModuleJobDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployExample;
import com.xc.fast_deploy.model.master_model.example.ModuleJobExample;
import com.xc.fast_deploy.model.master_model.*;
import com.xc.fast_deploy.myException.*;
import com.xc.fast_deploy.myenum.ComplieTypeEnum;
import com.xc.fast_deploy.myenum.DockerfileTypeEnum;
import com.xc.fast_deploy.myenum.ModuleMirrorStatusEnum;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.service.common.*;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.*;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.code_utils.GitUtils;
import com.xc.fast_deploy.utils.code_utils.SvnUtils;
import com.xc.fast_deploy.utils.code_utils.XMLUtils;
import com.xc.fast_deploy.utils.constant.JenkinsOperate;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.utils.jenkins.JenkinsManage;
import com.xc.fast_deploy.vo.RunJobDataVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvVo;
import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleJobParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleJobSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.yeauty.pojo.Session;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.xc.fast_deploy.utils.FoldUtils.SEP;

@Service
@Slf4j
@DependsOn("modulePackageServiceImpl")
public class ModuleJobServiceImpl extends BaseServiceImpl<ModuleJob, Integer> implements ModuleJobService {
    @Autowired
    ModuleDeployNeedMapper needMapper;
    @Autowired
    private ModuleManageMapper manageMapper;
    @Autowired
    private ModuleMirrorService mirrorService;
    @Autowired
    private ModuleJobMapper jobMapper;
    @Autowired
    private ModuleEnvService envService;
    @Autowired
    private ModulePackageService packageService;
    @Autowired
    private ModuleCertificateMapper certificateMapper;
    @Autowired
    private ModuleDeployMapper deployMapper;
    @Resource
    private JenkinsManage jenkinsManage;
    
    @Value("${file.storge.path.prefix}")
    private String storgePrefix;
    
    @PostConstruct
    public void init() {
        super.init(jobMapper);
    }
    
    /**
     * 根据前台填写的内容创建一个job
     *
     * @param paramVo 前台填写的参数
     * @param manage  前台查询出来的参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveJob(ModuleJobParamVo paramVo, ModuleManage manage, MultipartFile dockerfile, MultipartFile compilerFile) {
        //模块编译类型
        ComplieTypeEnum enumByType = ComplieTypeEnum.getEnumByType(paramVo.getCompileType());
        //dockerfile指定类型
        Integer dockerfileType = paramVo.getDockerfileType();
        
        ModuleJob moduleJob = new ModuleJob();
        //jobName 不能重复
//        JenkinsJobDTO jobDTO = jenkinsManage.selectByName(paramVo.getJobName());
//        if (jobDTO != null) {
//            log.info("该jobName 已经存在");
//        }
        if (manage != null) {
            ModuleEnv moduleEnv = envService.selectById(paramVo.getEnvId());
            ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(paramVo.getModuleId());
            if (moduleEnv != null && moduleManageDTO != null && moduleManageDTO.getCenterId().equals(paramVo.getCenterId())) {
                //首先验证同一个环境中同一个模块不能做两次镜像制作的job
                ModuleJobExample jobExample = new ModuleJobExample();
                jobExample.createCriteria().andModuleIdEqualTo(paramVo.getModuleId()).andIsDeleteEqualTo(0)
                    .andModuleEnvIdEqualTo(paramVo.getEnvId());
                List<ModuleJob> moduleJobs = jobMapper.selectByExample(jobExample);
                if (moduleJobs != null && moduleJobs.size() > 0) {
                    throw new ModuleJobSaveException("模块已在该环境建立制作镜像任务");
                }
                //首先需要知道模块类型
                ModuleTypeEnum moduleTypeEnum = ModuleTypeEnum.getTypeByCode(moduleManageDTO.getModuleType());
                String contentName = manage.getModuleContentName();
                paramVo.setJobId(null);
                BeanUtils.copyProperties(paramVo, moduleJob);
                StringBuilder jobName = new StringBuilder();
                moduleJob.setJobName(jobName.append(moduleEnv.getEnvCode()).append("-").append(moduleManageDTO.getModuleContentName()).
                    append("-").append(UUID.randomUUID().toString().split("-")[1]).toString());
                
                //先存数据再存文件
                StringBuffer sb = new StringBuffer();
                if (dockerfile != null && dockerfileType.equals(DockerfileTypeEnum.FILE_UPLOAD.getCode())) {
                    sb.append(storgePrefix).append(moduleManageDTO.getCenterPath()).append(SEP).append(contentName).append(SEP).
                        append(dockerfile.getOriginalFilename()).append(DateUtils.generateDateString());
                    try {
                        dockerfile.transferTo(new File(sb.toString()));
                        moduleJob.setDockerfilePath(sb.toString().replace(storgePrefix, ""));
                    } catch (IOException e) {
                        log.error("dockerfile 文件存储异常: {}", sb.toString());
                        throw new FileStoreException("dockerfile 文件存储异常");
                    }
                }
                //文件不存在即添加失败
                File file = new File(storgePrefix + moduleJob.getDockerfilePath());
                if (!file.exists()) {
                    log.error("该文件不存在: {}{}", storgePrefix, moduleJob.getDockerfilePath());
                    return false;
                }
                sb.delete(0, sb.length());
                //先将filePath设置为null
                moduleJob.setCompileFilePath(null);
                //指定编译脚本进行编译类型,这里就需要存储编译脚本
                if (compilerFile != null && ModuleTypeEnum.SVN_SOURCE_CODE.equals(moduleTypeEnum) &&
                    ComplieTypeEnum.FILE_COMPILIE.equals(enumByType)) {
                    sb.append(storgePrefix).append(moduleManageDTO.getCenterPath())
                        .append(SEP).append(contentName).append(SEP)
                        .append(compilerFile.getOriginalFilename())
                        .append(DateUtils.generateDateString());
                    try {
                        compilerFile.transferTo(new File(sb.toString()));
                        moduleJob.setCompileFilePath(sb.toString().replace(storgePrefix, ""));
                        moduleJob.setCompileCommand(null);
                    } catch (IOException e) {
                        log.error("compiler 文件存储异常: " + sb.toString());
                        throw new FileStoreException("compiler 文件存储异常");
                    }
                    
                }
                sb.delete(0, sb.length());
                //程序包类型的不能添加compile相关信息
                if (ModuleTypeEnum.PROJECT_PACKAGE.equals(moduleTypeEnum)) {
                    moduleJob.setCompileFilePath(null);
                    moduleJob.setCompileCommand(null);
                }
                moduleJob.setCreateTime(new Date());
                moduleJob.setUpdateTime(new Date());
                moduleJob.setModuleEnvId(paramVo.getEnvId());
                StringBuilder mirrorPreSb = new StringBuilder();
                moduleJob.setMirrorPrefix(mirrorPreSb.append(moduleEnv.getHarborUrl()).append(SEP)
                    .append(moduleManageDTO.getModuleProjectCode()).append(SEP)
                    .append(manage.getModuleContentName().toLowerCase()).toString());
                jobMapper.insertSelective(moduleJob);
                //连接jenkins 创建job
                return jenkinsManage.createRealJob(moduleJob, moduleTypeEnum,
                    enumByType, moduleEnv.getEnvCode());
            }
        }
        return false;
    }
    
    /**
     * 开始启动一个构建job
     * 直接填充到argsmap
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean runJob(Session session, String token,
                          RunJobDataVo jobDataVo) throws Exception {
        boolean isUpdateAllCode = jobDataVo.getIsUpdateAllCode();
        
        boolean isNeedUpCode = jobDataVo.getIsNeedUpCode();
        Boolean isPromptly = jobDataVo.getIsPromptly();
        String needIdStr = jobDataVo.getNeedIdStr();
        Boolean isOffline = jobDataVo.getIsOffline();
        Integer jobId = jobDataVo.getJobId();
        // isUpdateAllCode=false;
        //首先获取jobId对应的数据库数据
        ModuleJob moduleJob = selectById(jobId);
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setJobId(jobId);
        statusDTO.setStatus(0);
        //正在进行当中的job是不能重复去启动构建job的
        if (moduleJob == null) {
            return false;
        }
        JobWithDetails jobWithDetails =
            jenkinsManage.getJenkinsServer().getJob(moduleJob.getJobName());
        if (jobWithDetails != null && jobWithDetails.getLastBuild() != null
            && jobWithDetails.getLastBuild().details().isBuilding()) {
            return false;
        }
        if (StringUtils.isNotBlank(token)) {
            //配置相关参数的建立
            Map<String, String> argsMap = new ConcurrentHashMap<>();
            String compileCommand = moduleJob.getCompileCommand();
            if (StringUtils.isNotBlank(compileCommand)) {
                String offline = "--offline";
                if (isOffline && !compileCommand.contains(offline) && compileCommand.contains("mvn ")) {
                    compileCommand = compileCommand + " " + offline;
                }
                //如果选中了在线构建，但是命令中有offline，则去除
                if (!isOffline && compileCommand.contains(offline)) {
                    compileCommand = compileCommand.replaceAll(offline, "");
                }
                if (moduleJob.getModuleEnvId() == 25) {
                    //crm做适配
                }
            }
            
            String dockerfilePath = storgePrefix + moduleJob.getDockerfilePath();
            String compileFilePath = moduleJob.getCompileFilePath();
            Integer moduleEnvId = moduleJob.getModuleEnvId();
            argsMap.put("token", token);
            ModuleEnvVo moduleEnvVo = envService.selectWithCertById(moduleEnvId);
            if (moduleEnvVo != null) {
                String harborUrl = moduleEnvVo.getHarborUrl();
                String username = moduleEnvVo.getUsername();
                String password = EncryptUtil.decrypt(moduleEnvVo.getPassword());
                if (StringUtils.isNotBlank(harborUrl)
                    && StringUtils.isNotBlank(username)
                    && StringUtils.isNotBlank(password)) {
                    argsMap.put("harbor_url", harborUrl);
                    argsMap.put("harbor_username", username);
                    argsMap.put("harbor_password", password);
                } else {
                    return false;
                }
                if (StringUtils.isNotBlank(dockerfilePath)) {
                    String[] split = dockerfilePath.split(SEP);
                    if (split.length > 0) {
                        String dockerfileName = split[split.length - 1];
                        String dockerfileEnvPath = dockerfilePath
                            .replace(SEP + dockerfileName, "");
                        argsMap.put("dockerfile_path", dockerfileEnvPath);
                        argsMap.put("dockerfile_name", dockerfileName);
                    }
                } else {
                    return false;
                }
                
                Integer moduleId = moduleJob.getModuleId();
                List<ModulePackageDTO> packageDTOS = packageService.selectByModuleId(moduleId);
                ModuleCertificate certificate = certificateMapper.selectCertByModuleId(moduleId);
                ModuleManageDTO manageDTO = manageMapper.selectInfoById(moduleId);
                if (packageDTOS != null && packageDTOS.size() > 0) {
                    ModuleTypeEnum typeEnum = ModuleTypeEnum.getTypeByCode(manageDTO.getModuleType());
                    //svn自动更新的类型 先查询到自动更新的脚本
                    if (typeEnum != null && (typeEnum.equals(ModuleTypeEnum.SVN_SOURCE_CODE)
                        || typeEnum.equals(ModuleTypeEnum.GIT_SOURCE_CODE)
                        || typeEnum.equals(ModuleTypeEnum.SVN_AUTO_UP_CODE) || typeEnum.equals(ModuleTypeEnum.GIT_AUTO_UP_SOURCE_CODE))
                        && certificate != null) {
                        //执行编译文件的path
                        if (StringUtils.isNotBlank(compileFilePath)) {
                            compileFilePath = storgePrefix + compileFilePath;
                            String[] split = compileFilePath.split(SEP);
                            if (split.length > 0) {
                                String compileFileName = split[split.length - 1];
                                String compileEnvPath = compileFilePath.replace(SEP +
                                    compileFileName, "");
                                argsMap.put("compile_path", compileEnvPath);
                                argsMap.put("compile_file_name", compileFileName);
                            }
                        }
                        String packagePathName = null;
                        if (isNeedUpCode) {
                            //更新代码的操作处理
                            List<ModulePackageDTO> packageDTOList = upCode(typeEnum,
                                isUpdateAllCode, manageDTO, certificate,
                                session, statusDTO, packageDTOS);
                            if (packageDTOList != null && packageDTOList.size() > 0) {
                                packagePathName = packageDTOList.get(0).getPackagePathName();
                            } else {
                                packagePathName = packageDTOS.get(0).getPackagePathName();
                            }
                        } else {
                            packagePathName = packageDTOS.get(0).getPackagePathName();
                        }
                        
                        String compilePath = XMLUtils.genPOMXml(manageDTO, storgePrefix);
                        if (StringUtils.isBlank(compilePath)) {
                            log.error("生成pom文件失败");
                            compilePath = storgePrefix + packagePathName;
                        }
                        
                        if (StringUtils.isNotBlank(compileCommand)) {
                            argsMap.put("compile_command", compileCommand);
                            argsMap.put("compile_path", compilePath);
                        }
                    }
                    packageDTOS.clear();
                }
                log.info("开始执行jenkins 任务");
                argsMap.put("mirror_tag", moduleJob.getMirrorPrefix() + ":" + DateUtils.generateDateString());
                Integer buildNumber;
                try {
                    log.info("jenkins start");
                    JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
                    JobWithDetails job = jenkinsServer.getJob(moduleJob.getJobName());
                    int nextBuildNumber = job.getNextBuildNumber();
                    String userIdFromToken = JwtUtil.getUserIdFromToken(token);
                    Integer mirrorId;
                    log.info("userId from token", userIdFromToken);
                    synchronized (this) {
                        mirrorId = saveMirrorInfo(moduleJob, argsMap.get("mirror_tag"),
                            nextBuildNumber, userIdFromToken, isPromptly, needIdStr);
                    }
                    if (mirrorId == null) {
                        throw new ModuleJobSaveException("保存镜像信息失败!");
                    }
                    argsMap.put("mirror_id", mirrorId.toString());
                    log.info("runjob jobName is {} 相关参数为: {}", moduleJob.getJobName(),
                        JSONObject.toJSONString(argsMap));
                    buildNumber = jenkinsManage.runJob(jenkinsServer, moduleJob.getJobName(), argsMap);
                    if (buildNumber != null) {
                        return true;
                    } else {
                        ModuleMirror moduleMirror = new ModuleMirror();
                        //镜像生成失败状态更改
                        moduleMirror.setId(mirrorId);
                        moduleMirror.setIsAvailable(ModuleMirrorStatusEnum.FAIL.getCode());
                        int i = mirrorService.updateSelective(moduleMirror);
                    }
                } catch (IOException e) {
                    log.error("run job error");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    
    /**
     * 更新代码的操作
     *
     * @param typeEnum
     * @param isUpdateAllCode
     * @param manageDTO
     * @param certificate
     * @param session
     * @param statusDTO
     * @param packageDTOS
     * @return
     */
    private List<ModulePackageDTO> upCode(ModuleTypeEnum typeEnum, boolean isUpdateAllCode, ModuleManageDTO manageDTO, ModuleCertificate certificate,
                                          Session session, StatusDTO statusDTO,
                                          List<ModulePackageDTO> packageDTOS) {
        List<ModulePackageDTO> packageDTOList = new LinkedList<>();
        //是svn_auto_up的类型的话,需要根据最新的脚本更新整个子模块的内容
        if (typeEnum.equals(ModuleTypeEnum.SVN_AUTO_UP_CODE) && isUpdateAllCode) {
            svnAutoUp(manageDTO, certificate, session, statusDTO, packageDTOS, packageDTOList);
        } else if (typeEnum.equals(ModuleTypeEnum.GIT_AUTO_UP_SOURCE_CODE) && isUpdateAllCode) {
            packageService.gitAutoType(certificate, manageMapper.selectByPrimaryKey(manageDTO.getModuleId()), statusDTO, session, null);
        } else {
            statusDTO.setTotal(packageDTOS.size());
            try {
                ModuleTypeEnum typeByCode = ModuleTypeEnum.
                    getTypeByCode(packageDTOS.get(0).getModuleType());
                for (int i = 0; i < packageDTOS.size(); i++) {
                    //更新到最新的版本
                    statusDTO.setCurrent(i + 1);
                    statusDTO.setName(packageDTOS.get(i).getContentName());
                    if (session != null && session.channel().isActive()) {
                        session.sendText(JSONObject.toJSONString(statusDTO));
                    }
                    if (typeByCode != null) {
                        switch (typeByCode) {
                            case GIT_SOURCE_CODE:
                            case GIT_AUTO_UP_SOURCE_CODE:
                                String reversion = packageDTOS.get(i).getCodeReversion();
                                if ("-1".equals(reversion)) {
                                    GitUtils.gitPull(storgePrefix +
                                            packageDTOS.get(i).getPackagePathName(),
                                        certificate.getUsername(),
                                        EncryptUtil.decrypt(certificate.getPassword()));
                                } else {
                                    //切换版本
                                    GitUtils.gitReset(storgePrefix +
                                            packageDTOS.get(i).getPackagePathName(),
                                        reversion);
                                }
                                break;
                            case SVN_AUTO_UP_CODE:
                                //自动更新的类型
                                //这种类型不需要更新构建的话直接按照svn_source_code类型形式的操作
                            case SVN_SOURCE_CODE:
                                SVNClientManager clientManager =
                                    SvnUtils.getManager(certificate.getUsername(),
                                        EncryptUtil.decrypt(certificate.getPassword()));
                                clientManager.createRepository(SVNURL.parseURIEncoded(
                                    packageDTOS.get(i).getCodeUrl()), true);
                                File file = new File(storgePrefix + packageDTOS.get(i).getPackagePathName());
                                //当文件因为未知的原因不存在的时候直接重新拉取代码即可
                                //如果文件还是存在的话就可以直接
                                if (!file.exists()) {
                                    SvnUtils.checkout(clientManager,
                                        SVNURL.parseURIEncoded(packageDTOS.get(i).getCodeUrl()),
                                        SVNRevision.create(SvnUtils.LATEST_REVERSION), file, SVNDepth.INFINITY);
                                } else {
                                    long update = SvnUtils.update(clientManager, file, SVNRevision.
                                            create(Integer.valueOf(packageDTOS.get(i).getCodeReversion())),
                                        SVNDepth.INFINITY);
                                    if (update <= 0) {
                                        throw new SvnUpdateException("svn up出现错误");
                                    }
                                }
                                break;
                        }
                    }
                }
            } catch (SVNException e) {
                log.error("svnURL error: {}", e.getMessage());
                if (session != null && session.channel().isActive()) {
                    session.sendText("svnURL error: {}" + e.getMessage());
                }
                throw new SvnUpdateException(e.getMessage());
            } catch (GitAPIException | IOException e) {
                if (session != null && session.channel().isActive()) {
                    session.sendText(" error: {}" + e.getMessage());
                }
                throw new SvnUpdateException(e.getMessage());
            }
        }
        return packageDTOList;
    }
    
    private void svnAutoUp(ModuleManageDTO manageDTO, ModuleCertificate certificate, Session session, StatusDTO statusDTO, List<ModulePackageDTO> packageDTOS, List<ModulePackageDTO> packageDTOList) {
        try {
            SVNClientManager clientManager =
                SvnUtils.getManager(certificate.getUsername(),
                    EncryptUtil.decrypt(certificate.getPassword()));
            String shFilePath = storgePrefix + manageDTO.getShPath();
            File file = new File(shFilePath);
            //下拉一遍代码  覆盖 更新的方式
            SvnUtils.doExport(clientManager, SVNURL.parseURIEncoded(manageDTO.getSvnAutoUrl()),
                SVNRevision.create(SvnUtils.LATEST_REVERSION), file, SVNDepth.INFINITY);
            //解析更新过的脚本获取最新数据
            StringBuilder filePrefix = new StringBuilder();
            filePrefix.append(storgePrefix).append(manageDTO.getCenterPath()).append(SEP)
                .append(manageDTO.getModuleContentName()).append(SEP);
            
            List<ModulePackageParamVo> paramVoList =
                ExcelPhraseUtils.getAllShData(new FileInputStream(file));
            
            //然后 拉取代码到模块目录下
            statusDTO.setTotal(paramVoList.size() * 2);
            log.info("paramVoList数据为:{}", JSONObject.toJSONString(paramVoList));
            for (int i = 0; i < paramVoList.size(); i++) {
                statusDTO.setCurrent(i + 1);
                statusDTO.setName(paramVoList.get(i).getContentName());
                if (session != null) {
                    session.sendText(JSONObject.toJSONString(statusDTO));
                }
                synchronized (this) {
                    boolean exist = SvnUtils.isURLExist(paramVoList.get(i).getCodeUrl(),
                        certificate.getUsername(),
                        EncryptUtil.decrypt(certificate.getPassword()));
                    if (!exist) {
                        paramVoList.clear();
                        throw new SvnUrlNotExistException("svnUrl不存在异常: "
                            + paramVoList.get(i).getCodeUrl());
                    }
                }
            }
            //删除原有的数据
            for (ModulePackageDTO packageDTO : packageDTOS) {
                //首先删除记录
                packageService.deleteById(packageDTO.getId());
                //然后删除对应的所有文件
                FoldUtils.deleteFolders(storgePrefix + packageDTO.getPackagePathName());
            }
            //如果 sh文件中的数量子模块数目不同，则返回错误
            //if(paramVoList.size()!=packageDTOS.size()){
            //    throw new SvnUpdateException("子模块数目"+packageDTOS.size()+"与sh文件内模块数目"+paramVoList.size()+"不同，请检查！");
            //}
            List<ModulePackage> packageList = new ArrayList<>(paramVoList.size());
            
            for (int j = 0; j < paramVoList.size(); j++) {
                ModulePackage modulePackage = new ModulePackage();
                ModulePackageDTO modulePackageDTO = new ModulePackageDTO();
                statusDTO.setCurrent(j + paramVoList.size() + 1);
                statusDTO.setName(paramVoList.get(j).getContentName());
                if (session != null && session.channel().isActive()) {
                    session.sendText(JSONObject.toJSONString(statusDTO));
                }
                try {
                    //checkout 新代码到模块目录下
                    //SvnUtils.checkout(clientManager,
                    //        SVNURL.parseURIEncoded(paramVoList.get(j).getCodeUrl()),
                    //        SVNRevision.create(SvnUtils.LATEST_REVERSION),
                    //        new File(filePrefix + paramVoList.get(j).getContentName()),
                    //        SVNDepth.INFINITY);
                    //
                    //clientManager.createRepository(SVNURL.parseURIEncoded(
                    //    packageDTOS.get(j).getCodeUrl()), true);
                    File fileModel = new File(filePrefix + paramVoList.get(j).getContentName());
                    //当文件因为未知的原因不存在的时候直接重新拉取代码即可
                    //如果文件还是存在的话就可以直接
                    if (!fileModel.exists()) {
                        SvnUtils.checkout(clientManager,
                            SVNURL.parseURIEncoded(paramVoList.get(j).getCodeUrl()),
                            SVNRevision.create(SvnUtils.LATEST_REVERSION), fileModel, SVNDepth.INFINITY);//packageDTOS.get(j).getCodeUrl()
                    } else {
                        long update = SvnUtils.update(clientManager, fileModel, SVNRevision.
                                create(-1),
                            SVNDepth.INFINITY);//Integer.valueOf(packageDTOS.get(j).getCodeReversion())
                        if (update <= 0) {
                            throw new SvnUpdateException("svn up出现错误");
                        }
                    }
                } catch (SVNException e) {
                    throw new SvnUrlNotExistException(e.getMessage());
                }
                addPackageList(manageDTO, packageDTOList, filePrefix, paramVoList, packageList, j, modulePackage, modulePackageDTO);
            }
            //存入package信息
            packageService.insertAll(packageList);
            //然后生成pom文件
            
        } catch (SVNException | IOException e) {
            e.printStackTrace();
            throw new SvnUpdateException(e.getMessage());
        }
    }
    
    private void addPackageList(ModuleManageDTO manageDTO, List<ModulePackageDTO> packageDTOList, StringBuilder filePrefix, List<ModulePackageParamVo> paramVoList, List<ModulePackage> packageList, int j, ModulePackage modulePackage, ModulePackageDTO modulePackageDTO) {
        modulePackage.setCodeReversion(String.valueOf(SvnUtils.LATEST_REVERSION));
        modulePackage.setCreateTime(new Date());
        modulePackage.setCodeType(ModuleTypeEnum.SVN_AUTO_UP_CODE.getModuleTypeCode());
        modulePackage.setContentName(paramVoList.get(j).getContentName());
        modulePackage.setCodeUrl(paramVoList.get(j).getCodeUrl());
        modulePackage.setModuleId(manageDTO.getModuleId());
        modulePackage.setPackagePathName((filePrefix + paramVoList.get(j).getContentName()).
            replace(storgePrefix, ""));
        BeanUtils.copyProperties(modulePackage, modulePackageDTO);
        modulePackageDTO.setModuleName(manageDTO.getModuleName());
        modulePackageDTO.setModuleContentName(manageDTO.getModuleContentName());
        packageList.add(modulePackage);
        packageDTOList.add(modulePackageDTO);
    }
    
    /**
     * svn 根据脚本自动更新package结构
     *
     * @param moduleId
     * @param certificate
     * @param packageDTOS
     * @return
     */
//    private List<ModulePackageDTO> updateModuleManageAll(Integer moduleId,
//                                                         ModuleCertificate certificate,
//                                                         List<ModulePackageDTO> packageDTOS)
//            throws SVNException, FileNotFoundException {
//        log.info("执行自动更新下拉代码脚本的操作:{}", packageDTOS.get(0).getModuleName());
//        ModuleManageDTO manageDTO = manageMapper.selectInfoById(moduleId);
//        if (manageDTO != null) {
//            //首先更新一遍脚本
//            String shFilePath = storgePrefix + manageDTO.getShPath();
//            File file = new File(shFilePath);
//            SVNClientManager clientManager = SvnUtils.getManager(certificate.getUsername(),
//                    EncryptUtil.decrypt(certificate.getPassword()));
//            //下拉一遍代码
//            SvnUtils.doExport(clientManager, SVNURL.parseURIEncoded(manageDTO.getSvnAutoUrl()),
//                    SVNRevision.create(SvnUtils.LATEST_REVERSION), file, SVNDepth.INFINITY);
////            } else {
////                clientManager.createRepository(SVNURL.parseURIEncoded(manageDTO.getSvnAutoUrl()),
////                        true);
////                SvnUtils.update(clientManager, file, SVNRevision.create(SvnUtils.LATEST_REVERSION),
////                        SVNDepth.INFINITY);
////            }
//            //文件更新完之后 然后再去解析文件 获取最新的代码目录结构
//            List<ModulePackageParamVo> paramVoList = ExcelPhraseUtils.getAllShData(new FileInputStream(file));
//
//            if (paramVoList != null && paramVoList.size() > 0) {
//                List<ModulePackageParamVo> packageParamVos = new ArrayList<>();
//                //把相同的地方找出来 不同的地方也找出来 吧原来的多出来的删除掉 原来的少的加上
//                for (int i = 0; i < paramVoList.size(); i++) {
//                    boolean flag = false;
//                    for (ModulePackageDTO modulePackageDTO : packageDTOS) {
//                        if (modulePackageDTO.getCodeUrl().equals(paramVoList.get(i).getCodeUrl())) {
//                            flag = true;
//                            break;
//                        }
//                    }
//                    if (!flag) {
//                        //表示新的在原来的里面没有找到 需要添加信息package信息
//                        packageParamVos.add(paramVoList.get(i));
//                    }
//                }
//                //添加新的modulePackage信息
//                if (packageParamVos.size() > 0) {
//                    StringBuilder filePrefix = new StringBuilder();
//                    filePrefix.append(manageDTO.getCenterPath()).append(SEP).
//                            append(manageDTO.getModuleContentName()).append(SEP);
//
//                    List<ModulePackage> packageList = new ArrayList<>();
//                    for (ModulePackageParamVo packageParamVo : packageParamVos) {
//                        ModulePackageDTO packageDTO = new ModulePackageDTO();
//                        packageDTO.setModuleContentName(manageDTO.getModuleContentName());
//                        packageDTO.setModuleName(manageDTO.getModuleName());
//
//                        ModulePackage modulePackage = new ModulePackage();
//                        modulePackage.setCodeReversion(String.valueOf(SvnUtils.LATEST_REVERSION));
//                        modulePackage.setCreateTime(new Date());
//                        modulePackage.setCodeType(ModuleTypeEnum.SVN_SOURCE_CODE.getModuleTypeCode());
//                        modulePackage.setContentName(packageParamVo.getContentName());
//                        modulePackage.setCodeUrl(packageParamVo.getCodeUrl());
//                        modulePackage.setModuleId(manageDTO.getModuleId());
//                        modulePackage.setPackagePathName((filePrefix + packageParamVo.getContentName()).
//                                replace(storgePrefix, ""));
//                        BeanUtils.copyProperties(modulePackage, packageDTO);
//
//                        //还需要checkout 代码
//                        packageList.add(modulePackage);
//                        packageDTOS.add(packageDTO);
//                    }
//                    packageService.insertAll(packageList);
//                    syncService.checkoutAllSync(packageList, certificate);
//                }
//                packageParamVos.clear();
//                Map<Integer, ModulePackageDTO> modulePackageDTOMap = new HashMap<>();
////                List<ModulePackageDTO> modulePackageDTOs = new ArrayList<>();
//                for (int i = 0; i < packageDTOS.size(); i++) {
//                    boolean flag = false;
//                    for (ModulePackageParamVo packageParamVo : paramVoList) {
//                        //能找到对应的关系
//                        if (packageParamVo.getCodeUrl().equals(packageDTOS.get(i).getCodeUrl())) {
//                            flag = true;
//                            break;
//                        }
//                    }
//                    if (!flag) {
//                        //表示原来的在新的里面没有找到 需要将原来的删除
//                        modulePackageDTOMap.put(packageDTOS.get(i).getId(), packageDTOS.get(i));
//                    }
//                }
////                log.info("modulePackageDTOSet:{}", JSONObject.toJSONString(modulePackageDTOs));
//                //删除变更后不存在的信息
//                if (modulePackageDTOMap.size() > 0) {
//                    Set<Integer> integerSet = modulePackageDTOMap.keySet();
//                    List<ModulePackageDTO> newModulePackageDTOs = new ArrayList<>();
//                    for (ModulePackageDTO packageDTO : packageDTOS) {
//                        if (integerSet.contains(packageDTO.getId())) {
//                            log.info("删除packageDTO:{}", packageDTO.getPackagePathName());
//                            //删除原来的数据和相关文件
//                            packageService.deleteById(packageDTO.getId());
//                            if (!FoldUtils.deleteFolders(storgePrefix + packageDTO.getPackagePathName())) {
//                                throw new RuntimeException("删除文件夹失败: " + packageDTO.getPackagePathName());
//                            }
//                        } else {
//                            newModulePackageDTOs.add(packageDTO);
//                        }
//                        packageDTOS = newModulePackageDTOs;
//                    }
//                }
//                modulePackageDTOMap.clear();
//            }
//            //更新完
//        }
//        return packageDTOS;
//    }
    
    /**
     * 保存镜像信息
     *
     * @param moduleJob
     * @param mirrorTag
     * @param buildNumber
     * @return
     */
    private Integer saveMirrorInfo(ModuleJob moduleJob, String mirrorTag, Integer buildNumber, String userId, Boolean isPromptly, String needIdStr) {
        
        log.info("保存mirror信息,当前buildNumber: {}, mirrorTag : {}", buildNumber, mirrorTag);
        Integer mirrorId = null;
        if (moduleJob != null && StringUtils.isNotBlank(mirrorTag)
            && buildNumber != null && buildNumber > 0) {
            ModuleMirror moduleMirror = new ModuleMirror();
            moduleMirror.setJobId(moduleJob.getId());
            moduleMirror.setCreateTime(new Date());
            moduleMirror.setJobReversion(buildNumber);
            moduleMirror.setIsPromptly(isPromptly == true ? 1 : 0);
            moduleMirror.setMirrorName(mirrorTag);
            moduleMirror.setModuleEnvId(moduleJob.getModuleEnvId());
            moduleMirror.setModuleId(moduleJob.getModuleId());
            moduleMirror.setUpdateTime(new Date());
            moduleMirror.setOpUserId(userId);
            if (StringUtils.isNotBlank(needIdStr)) {
                String devUserId = needMapper.selectDeveploperById(needIdStr);
                moduleMirror.setDevUserId(devUserId);
            }
            //镜像制作正在进行当中
            moduleMirror.setIsAvailable(ModuleMirrorStatusEnum.ONLINE.getCode());
            mirrorService.insertSelective(moduleMirror);
            mirrorId = moduleMirror.getId();
        }
        return mirrorId;
    }
    
    /**
     * 分页按照条件查询job信息
     *
     * @param pageNum       页数
     * @param pageSize      每页数量
     * @param selectParamVo 查询参数
     * @return 分页job数据
     */
    @Override
    public MyPageInfo<ModuleJobVo> selectPageAllInfo(Integer pageNum, Integer pageSize, ModuleJobSelectParamVo selectParamVo) {
        MyPageInfo<ModuleJobVo> myPageInfo = new MyPageInfo<>();
        if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
            List<ModuleJobVo> list = jobMapper.selectJobVoPageByVo(selectParamVo);
            PageInfo<ModuleJobVo> pageInfo = new PageInfo<>(list);
            BeanUtils.copyProperties(pageInfo, myPageInfo);
        }
        List<ModuleJobVo> jobVoList = myPageInfo.getList();
        if (jobVoList.size() > 0) {
            Set<Integer> jobIds = new HashSet<>();
            for (ModuleJobVo jobVo : jobVoList) {
                jobIds.add(jobVo.getJobId());
            }
            List<ModuleMirror> mirrorList = mirrorService.selectByJobIds(jobIds);
            HashMap<Integer, Date> hashMap = new HashMap<>();
            for (ModuleMirror moduleMirror : mirrorList) {
                hashMap.put(moduleMirror.getJobId(), moduleMirror.getUpdateTime());
            }
            for (ModuleJobVo jobVo : jobVoList) {
                if (hashMap.containsKey(jobVo.getJobId())) {
                    jobVo.setStatus("1");
                    jobVo.setUpdateTime(hashMap.get(jobVo.getJobId()));
                } else {
                    jobVo.setStatus("0");
                    jobVo.setUpdateTime(null);
                }
            }
        }
        myPageInfo.setList(jobVoList);
        return myPageInfo;
    }
    
    /**
     * 根据jobId查询与之相关联的数据
     *
     * @param jobId
     * @return
     */
    @Override
    public ModuleJobDTO getOneJobById(Integer jobId) {
        ModuleJobDTO moduleJobDTO = jobMapper.selectJobDTOById(jobId);
        BufferedReader bufferedReader = null;
        try {
            File file = new File(storgePrefix + moduleJobDTO.getDockerfilePath());
            bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (StringUtils.isNotBlank(line)) {
                    content.append(line + "\n\n");
                }
            }
            moduleJobDTO.setDockerfileContent(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return moduleJobDTO;
    }
    
    /**
     * 根据jobId 删除该条job
     *
     * @param jobId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobById(Integer jobId) {
        if (jobId != null) {
            JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
            if (jenkinsServer != null) {
                ModuleJobExample jobExample = new ModuleJobExample();
                ModuleJobExample.Criteria criteria = jobExample.createCriteria();
                criteria.andIdEqualTo(jobId).andIsDeleteEqualTo(0);
                List<ModuleJob> moduleJobs = jobMapper.selectByExample(jobExample);
                if (moduleJobs != null && moduleJobs.size() > 0) {
                    ModuleJob moduleJob = moduleJobs.get(0);
                    moduleJob.setIsDelete(1);
                    moduleJob.setId(jobId);
                    try {
                        if (jobMapper.updateByPrimaryKey(moduleJob) > 0) {
                            jenkinsServer.deleteJob(moduleJob.getJobName());
                            return true;
                        }
                    } catch (IOException e) {
                        log.error("jenkins 删除job出现错误");
                        throw new NotDeleteOkException("jenkins 删除job出现错误");
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * 获取最近 最多10条的build信息
     *
     * @param jobId
     * @param moduleJob
     * @return
     */
    @Override
    public JobDetailsDTO getJobDetails(Integer jobId, ModuleJob moduleJob) {
        JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
        if (moduleJob != null && jenkinsServer != null) {
            JobDetailsDTO jobDetailsDTO = new JobDetailsDTO();
            try {
                JobWithDetails jobWithDetails = jenkinsServer.getJob(moduleJob.getJobName());
                log.info("jobid:{},modulejob:{},jobWithDetails:{}", jobId, moduleJob, jobWithDetails);
                if (jobWithDetails != null) {
                    jobDetailsDTO.setJobId(jobId);
                    jobDetailsDTO.setJobName(jobWithDetails.getName());
                    List<Build> allBuilds = jobWithDetails.getAllBuilds(Range.build()
                        .to(JenkinsOperate.JOB_DETAILS_MAX_NUMBER).build());
                    
                    List<JobBuildDetailsDTO> detailsDTOS = new LinkedList<>();
                    if (allBuilds != null && allBuilds.size() > 0) {
                        for (Build build : allBuilds) {
                            JobBuildDetailsDTO jobBuildDetailsDTO = new JobBuildDetailsDTO();
                            jobBuildDetailsDTO.setBuildNumber(build.getNumber());
                            BuildWithDetails details = build.details();
                            if (details.getResult() != null) {
                                jobBuildDetailsDTO.setResult(details.getResult().name());
                            }
                            long timestamp = details.getTimestamp();
                            if (timestamp != 0) {
                                jobBuildDetailsDTO.setBuildDate(new Date(timestamp));
                            }
                            jobBuildDetailsDTO.setBuilding(details.isBuilding());
                            jobBuildDetailsDTO.setDuration(details.getDuration());
                            detailsDTOS.add(jobBuildDetailsDTO);
                        }
                        jobDetailsDTO.setBuildDetails(detailsDTOS);
                        return jobDetailsDTO;
                    }
                }
            } catch (IOException e) {
                log.info("获取job details error");
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 获取某个job的某次构建的输出日志 ,文本形式
     *
     * @param
     * @param jobId
     * @param buildNumber
     * @param moduleJob
     * @return
     */
    @Override
    public StringBuilder getConsoleOutput(Integer jobId, Integer buildNumber, ModuleJob moduleJob) {
        StringBuilder sb = new StringBuilder();
        JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
        if (moduleJob != null && jenkinsServer != null) {
            try {
                JobWithDetails jobWithDetails = jenkinsServer.getJob(moduleJob.getJobName());
                if (jobWithDetails != null) {
                    Build build = jobWithDetails.getBuildByNumber(buildNumber);
                    if (build != null) {
                        BuildWithDetails details = build.details();
                        sb.append(details.getConsoleOutputText(JenkinsOperate.MAX_OFFSET_DATA_NUMBER).getConsoleLog());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb;
    }
    
    /**
     * 根据jobId 查询可用的镜像信息
     *
     * @param jobId
     * @return
     */
    @Override
    public List<ModuleMirror> getAvailableMirrorByJobId(Integer jobId) {
        return mirrorService.selectAvailableInfoByJobId(jobId);
    }
    
    /**
     * 查询某个环境中的模块job信息 并且未添加到发布信息中的;
     *
     * @param envId
     * @return
     */
    @Override
    public List<ModuleManageDTO> selectJobModuleByEnvId(Integer envId) {
        List<ModuleManageDTO> manageDTOList = jobMapper.selectJobModuleByEnvId(envId);
        List<ModuleManageDTO> returnModuleManageDTOList = new ArrayList<>();
        if (manageDTOList != null && manageDTOList.size() > 0) {
            ModuleDeployExample deployExample = new ModuleDeployExample();
            ModuleDeployExample.Criteria criteria = deployExample.createCriteria();
            criteria.andIsDeleteEqualTo(0).andEnvIdEqualTo(envId);
            List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(deployExample);
            if (moduleDeploys != null && moduleDeploys.size() > 0) {
                Set<Integer> moduleIds = new HashSet<>();
                for (ModuleDeploy moduleDeploy : moduleDeploys) {
                    boolean b = moduleIds.add(moduleDeploy.getModuleId());
                }
                
                if (moduleIds.size() > 0) {
                    for (int i = 0; i < manageDTOList.size(); i++) {
                        if (!moduleIds.contains(manageDTOList.get(i).getModuleId())) {
                            returnModuleManageDTOList.add(manageDTOList.get(i));
                        }
                    }
                    return returnModuleManageDTOList;
                }
            }
        }
        return manageDTOList;
    }
    
    /**
     * 根据环境id和模块id查询对应的该模块的job信息
     *
     * @param envId
     * @param moduleId
     * @return
     */
    @Override
    public ModuleManageDTO selectJobModuleByModuleAndEnvId(Integer envId, Integer moduleId) {
        ModuleJob moduleJob = new ModuleJob();
        moduleJob.setModuleEnvId(envId);
        moduleJob.setModuleId(moduleId);
        return jobMapper.selectJobModuleByEnvAndModuleId(moduleJob);
    }
    
    /**
     * 根据环境id和模块id查询对应的该模块的job信息
     *
     * @param envId
     * @param moduleId
     * @return
     */
    @Override
    public ModuleJob selectJobByModuleAndEnvId(Integer envId, Integer moduleId) {
        ModuleJob moduleJob = null;
        if (envId != null && moduleId != null) {
            ModuleJobExample jobExample = new ModuleJobExample();
            ModuleJobExample.Criteria criteria = jobExample.createCriteria();
            criteria.andModuleEnvIdEqualTo(envId).andIsDeleteEqualTo(0).andModuleIdEqualTo(moduleId);
            List<ModuleJob> moduleJobs = jobMapper.selectByExample(jobExample);
            if (moduleJobs != null && moduleJobs.size() == 1) {
                moduleJob = moduleJobs.get(0);
            }
        }
        return moduleJob;
    }
    
    /**
     * 更新moduleJob信息
     *
     * @param paramVo
     * @param dockerfile
     * @param complieFile
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJobInfo(ModuleJobParamVo paramVo, MultipartFile dockerfile, MultipartFile complieFile) {
        boolean success = false;
        ModuleJob moduleJob = jobMapper.selectByPrimaryKey(paramVo.getJobId());
        ComplieTypeEnum complieTypeEnum = ComplieTypeEnum.getEnumByType(paramVo.getCompileType());
        DockerfileTypeEnum dockerfileTypeEnum = DockerfileTypeEnum.getEnumByType(paramVo.getDockerfileType());
        if (moduleJob != null) {
            ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleJob.getModuleId());
            if (moduleManageDTO != null) {
                ModuleJob saveJob = new ModuleJob();
                saveJob.setId(paramVo.getJobId());
                saveJob.setUpdateTime(new Date());
                if (complieTypeEnum != null) {
                    switch (complieTypeEnum) {
                        case COMMAND_COMPILIE:
                            saveJob.setCompileCommand(paramVo.getCompileCommand());
                            break;
                        case FILE_COMPILIE:
                            StringBuilder sb = new StringBuilder();
                            sb.append(storgePrefix).append(moduleManageDTO.getCenterPath()).append(SEP)
                                .append(moduleManageDTO.getModuleContentName()).append(SEP).
                                append(complieFile.getOriginalFilename()).append(DateUtils.generateDateString());
                            try {
                                complieFile.transferTo(new File(sb.toString()));
                                saveJob.setCompileFilePath(sb.toString().replace(storgePrefix, ""));
                                saveJob.setCompileCommand(null);
                            } catch (IOException e) {
                                log.error("complieFile 文件存储异常: " + sb.toString());
                                throw new FileStoreException("complieFile 文件存储异常");
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (dockerfileTypeEnum != null) {
                    switch (dockerfileTypeEnum) {
                        case FILE_APPOINT:
                            saveJob.setDockerfilePath(paramVo.getDockerfilePath());
                            break;
                        case FILE_UPLOAD:
                            StringBuilder sb = new StringBuilder();
                            sb.append(storgePrefix).append(moduleManageDTO.getCenterPath()).append(SEP)
                                .append(moduleManageDTO.getModuleContentName()).append(SEP).
                                append(dockerfile.getOriginalFilename()).append(DateUtils.generateDateString());
                            try {
                                dockerfile.transferTo(new File(sb.toString()));
                                saveJob.setDockerfilePath(sb.toString().replace(storgePrefix, ""));
                            } catch (IOException e) {
                                log.error("dockerfile 文件存储异常: " + sb.toString());
                                throw new FileStoreException("dockerfile 文件存储异常");
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (jobMapper.updateByPrimaryKeySelective(saveJob) > 0) {
                    success = true;
                }
            }
        }
        return success;
    }
    
    @Override
    public ResponseDTO stopJob(Integer moduleId, Integer envId) {
        ResponseDTO responseDTO = new ResponseDTO();
        ModuleJob moduleJob = selectJobByModuleAndEnvId(envId, moduleId);
        responseDTO.fail("操作失败");
        if (moduleJob != null) {
            try {
                Build build = jenkinsManage.getJenkinsServer().getJob(moduleJob.getJobName()).getLastBuild();
                if (build.details().isBuilding()) {
                    build.Stop();
                    responseDTO.success("job已取消");
                } else {
                    return responseDTO.fail("没有正在运行的job");
                }
            } catch (IOException e) {
                return responseDTO.fail("获取job失败");
            }
        }
        return responseDTO;
    }
}


