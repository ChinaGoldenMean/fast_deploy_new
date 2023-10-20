package com.xc.fast_deploy.service.common.impl;

import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleManageMapper;
import com.xc.fast_deploy.dto.module.ModuleEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.example.ModuleManageExample;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.ModuleEnvService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.constant.RedisKeyPrefix;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvVo;
import com.xc.fast_deploy.vo.module_vo.PsPaasEnvVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.xc.fast_deploy.utils.constant.RedisKeyPrefix.ENV_CONFIG_PREFIX;

@Service
@Slf4j
public class ModuleEnvServiceImpl extends BaseServiceImpl<ModuleEnv, Integer> implements ModuleEnvService {
  
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private JedisManage jedisManage;
  
  @PostConstruct
  public void init() {
    super.init(envMapper);
  }
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  @Value("${myself.pspass.prod}")
  private boolean isProd;
  
  @Override
  public ModuleEnvVo selectWithCertById(Integer id) {
    return envMapper.selectWithCertById(id);
  }
  
  /**
   * 添加一条环境配置数据
   *
   * @param moduleEnv
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean insertOneEnv(ModuleEnv moduleEnv) {
    moduleEnv.setCreateTime(new Date());
    moduleEnv.setUpdateTime(new Date());
    List<ModuleEnv> moduleEnvs = envMapper.selectAll();
    for (ModuleEnv env : moduleEnvs) {
      if (moduleEnv.getEnvCode().equals(env.getEnvCode())
          || moduleEnv.getEnvName().equals(env.getEnvName())) {
        return false;
      }
    }
    Integer prod = isProd ? 1 : 0;
    moduleEnv.setIsProd(prod);
    int insert = envMapper.insert(moduleEnv);
    if (insert > 0) {
      log.info("添加环境数据成功");
      JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
      return true;
    } else {
      log.error("添加环境数据失败");
      return false;
    }
  }
  
  /**
   * 根据id更新一条数据
   *
   * @param moduleEnv
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateAll(ModuleEnv moduleEnv) {
    boolean success = false;
    if (moduleEnv != null && moduleEnv.getId() != null) {
      ModuleEnv env = envMapper.selectByPrimaryKey(moduleEnv.getId());
      if (env != null) {
        if (StringUtils.isNotBlank(moduleEnv.getEnvName()) && !moduleEnv.getEnvName().equals(env.getEnvName())) {
          ModuleManage moduleManage = new ModuleManage();
          moduleManage.setMark(moduleEnv.getEnvName());
          ModuleManageExample manageExample = new ModuleManageExample();
          manageExample.createCriteria().andEnvIdEqualTo(moduleEnv.getId());
          manageMapper.updateByExampleSelective(moduleManage, manageExample);
        }
        //envCode不允许更改,涉及到整体的目录结构问题
        moduleEnv.setEnvCode(null);
        moduleEnv.setUpdateTime(new Date());
        if (envMapper.updateByPrimaryKeySelective(moduleEnv) > 0) {
          //更改K8S config配置文件后，清空redis里存的配置文件，目前只有四中心优先读取redis配置文件
          jedisManage.delete(ENV_CONFIG_PREFIX + env.getEnvCode());
          success = true;
        }
      }
    }
    return success;
  }
  
  /**
   * 根据id删除一条数据
   *
   * @param id
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delectOne(Integer id) {
    int i = envMapper.deleteByPrimaryKey(id);
    if (i == 1) {
      log.info("删除环境数据成功");
      return true;
    } else {
      log.error("删除环境数据失败");
      return false;
    }
  }
  
  @Override
  public ModuleEnv selectOne(Integer id) {
    return envMapper.selectOne(id);
  }
  
  /**
   * 查询环境的单条数据
   *
   * @param id
   * @return
   */
  @Override
  public ModuleEnvDTO selectCerEnvInfoById(Integer id) {
    return envMapper.selectCerEnvInfoById(id);
  }
  
  /**
   * 查询环境模块中paas的envname，作为env表单更新添加下拉列表
   *
   * @param envIds
   * @return
   */
//    @Override
//    public List<ModuleEnv> selectPsAll(Set<Integer> envIds) {
//        return envMapper.selectPsAll(envIds);
//    }
  @Override
  public List<ModuleEnv> selectEnvAll(Set<Integer> envIds) {
    return envMapper.selectEnvAll(envIds);
  }
  
  /**
   * 查询所有数据
   *
   * @param envIds
   * @return
   */
  
  @Override
  public List<ModuleEnv> getAllModuleEnv(Set<Integer> envIds) {
    Integer prod = isProd ? 1 : 0;
    return envMapper.getAllModuleEnv(envIds, prod);
  }
  
  @Override
  public ModuleEnv selectBlobsById(Integer billEnvId) {
    return envMapper.selectWithBlobsByPrimaryKey(billEnvId);
  }
  
  @Override
  public List<PsPaasEnvVo> selectPsAll() {
    return envMapper.selectPsAllData();
  }
}
