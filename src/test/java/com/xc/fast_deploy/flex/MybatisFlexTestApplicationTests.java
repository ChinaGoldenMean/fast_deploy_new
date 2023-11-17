//package com.xc.fast_deploy.flex;
//
///**
// * @Author litiewang
// * @Date 2023/10/23 10:56
// * @Version 1.0
// */
//
//import com.xc.fast_deploy.dao.master_dao.ModuleBuildInfoMapper;
//import com.xc.fast_deploy.model.master_model.ModuleBuildInfo;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@ActiveProfiles("dev")
//public class MybatisFlexTestApplicationTests {
//
//  @Autowired
//  private ModuleBuildInfoMapper accountMapper;
//
//  @Test
//  public void contextLoads() {
//    QueryWrapper queryWrapper = QueryWrapper.create()
//        .select();
//       // .where(ModuleBuildInfo.AGE.eq(18));
//    ModuleBuildInfo account = accountMapper.selectOneByQuery(queryWrapper);
//    System.out.println(account);
//  }
//
//}