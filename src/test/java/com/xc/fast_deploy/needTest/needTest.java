package com.xc.fast_deploy.needTest;

import com.xc.fast_deploy.dao.master_dao.ModuleDeployNeedMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployNeedExample;
import com.xc.fast_deploy.service.common.ModuleNeedService;
import com.xc.fast_deploy.vo.module_vo.ModuleUpgradeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class needTest {
  
  @Autowired
  private ModuleNeedService needService;
  @Autowired
  private ModuleDeployNeedMapper needMapper;
  @Value("${myself.pspass.ids}")
  private Integer[] ids;
  
  @Test
  public void testExportNeed() {
    Integer[] needIds = {4, 5, 6, 7, 8};
    File file = needService.exportNeed(needIds);
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(file));
      bos = new BufferedOutputStream(new FileOutputStream("C:\\Users\\luochangbin\\Desktop\\need.xls"));
      byte[] bytes = new byte[1024];
      int len = 0;
      while ((len = bis.read(bytes)) != -1) {
        bos.write(bytes, 0, len);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        bis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  @Test
  public void findModuleEnvByNeeds() {
    
    List<ModuleUpgradeVo> upgradeVos = needMapper.findModuleEnvByNeeds(Arrays.asList(new Integer[]{12}));
    System.out.println(upgradeVos);
  }
  
  @Test
  public void selectNeed() {
//        int hour = LocalDateTime.now().getHour();
////        System.out.println(hour);
//        ModuleDeployNeedExample needExample = new ModuleDeployNeedExample();
//        needExample.createCriteria().andDeployTimeGreaterThan(new Date());
//        List<ModuleDeployNeed> deployNeeds = needMapper.selectByExample(needExample);
//        System.out.println(deployNeeds);
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(12);
    Date deployTime = deployNeed.getDeployTime();
    long l = System.currentTimeMillis();
    DateUtils.isSameDay(new Date(), deployTime);
    long e = System.currentTimeMillis();
    System.out.println(e - l);
    
    long l1 = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    sdf.format(deployTime).equals(sdf.format(new Date()));
    long e1 = System.currentTimeMillis();
    System.out.println(e1 - l1);
  }
}
