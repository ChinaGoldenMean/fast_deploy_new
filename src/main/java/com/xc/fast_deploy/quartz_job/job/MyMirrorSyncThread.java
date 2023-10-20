package com.xc.fast_deploy.quartz_job.job;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.ModuleMirrorMapper;
import com.xc.fast_deploy.dto.harborMirror.MirrorTagDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorCertificateEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleMirror;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.xc.fast_deploy.utils.constant.HarborContants.*;

@Slf4j
public class MyMirrorSyncThread implements Runnable {
  
  private List<ModuleMirrorCertificateEnvDTO> envDTOList;
  private ModuleMirrorMapper mirrorMapper;
  
  public MyMirrorSyncThread(List<ModuleMirrorCertificateEnvDTO> envDTOList, ModuleMirrorMapper mirrorMapper) {
    this.envDTOList = envDTOList;
    this.mirrorMapper = mirrorMapper;
  }
  
  @Override
  public void run() {
    Map<String, List<String>> tagsMap = new ConcurrentHashMap<>();
    for (ModuleMirrorCertificateEnvDTO mirrorCertificateEnvDTO : envDTOList) {
      String replace = mirrorCertificateEnvDTO.getMirrorName()
          .replace(mirrorCertificateEnvDTO.getHarborUrl(), "");
      String[] splits = replace.split(":");
      String mirrorName = splits[0];
      if (splits.length != 2) {
        log.info("格式错误无法解析镜像信息,同步失败 mirrorName: {}", mirrorCertificateEnvDTO.getMirrorName());
        return;
      }
      if (!tagsMap.containsKey(mirrorName)) {
        StringBuffer sb = new StringBuffer();
        String mirrorTagsUrl = sb.append(HTTP_PREFIX).append(mirrorCertificateEnvDTO.getHarborUrl())
            .append(CONTACT).append(API).append(CONTACT).append(REPOSTIRY)
            .append(mirrorName).append(CONTACT).append(TAGS).toString();
        String result = HttpUtils.doGetHarborInfo(mirrorTagsUrl,
            mirrorCertificateEnvDTO.getUsername(),
            EncryptUtil.decrypt(mirrorCertificateEnvDTO.getPassword()));
        
        if (StringUtils.isNotBlank(result)) {
          List<MirrorTagDTO> tagDTOList = JSONObject.parseArray(result, MirrorTagDTO.class);
          synchronized (this) {
            if (tagDTOList != null) {
              List<String> mirrorTagList = new ArrayList<>();
              for (MirrorTagDTO mirrorTagDTO : tagDTOList) {
                mirrorTagList.add(mirrorTagDTO.getName());
              }
              tagsMap.put(mirrorName, mirrorTagList);
              String mirrorTag = splits[1];
              if (!mirrorTagList.contains(mirrorTag)) {
                ModuleMirror moduleMirror = new ModuleMirror();
                moduleMirror.setId(mirrorCertificateEnvDTO.getId());
                moduleMirror.setIsAvailable(0);
                mirrorMapper.updateByPrimaryKeySelective(moduleMirror);
              }
            }
          }
        } else {
          //  log.info("获取harbor仓库信息数据失败,同步失败 mirrorName: {}",
          //   mirrorCertificateEnvDTO.getMirrorName());
        }
      } else {
        List<String> mirrorTagList = tagsMap.get(mirrorName);
        synchronized (this) {
          if (mirrorTagList != null && !mirrorTagList.contains(splits[1])) {
            ModuleMirror moduleMirror = new ModuleMirror();
            moduleMirror.setId(mirrorCertificateEnvDTO.getId());
            moduleMirror.setIsAvailable(0);
            mirrorMapper.updateByPrimaryKeySelective(moduleMirror);
          }
        }
      }
    }
  }
  
}

