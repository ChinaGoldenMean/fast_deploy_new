package com.xc.fast_deploy.moduleTest;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.ModuleJobMapper;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.service.common.ModuleCenterService;
import com.xc.fast_deploy.service.common.ModulePackageService;
import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ModuleJobTest {
  
  @Autowired
  private ModuleCenterService centerService;
  @Autowired
  private ModuleJobMapper jobMapper;
  @Autowired
  private ModulePackageService packageService;
  
  @Test
  public void testGetModuleManageByCenterId() {
//        List<ModuleManage> manageList = centerService.selectAllModuleById(1);
//        System.out.println(JSONObject.toJSONString(manageList));
//        FoldDataVo foldDataVo = new FoldDataVo();
//        FoldUtils.getAllFoldJson(new File("F:/storge/center_code2/two"), foldDataVo, storgePrefix);
//        System.out.println(JSONObject.toJSONString(foldDataVo));
  }
  
  @Test
  public void testCreateJob() {
    List<ModuleJobVo> jobVos = jobMapper.selectJobVoPageByVo(null);
    System.out.println(JSONObject.toJSONString(jobVos));
  }
  
  @Test
  public void testGetJobEnvByModuleId() {
    List<ModulePackageDTO> packageDTOS = packageService.selectByModuleId(36);
    System.out.println(JSONObject.toJSONString(packageDTOS));
//        String s = XMLUtils.genPOMXml("3255", packageDTOS, "F:/storge/");
  }
  
}
