package com.xc.fast_deploy.service.k8s.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.k8s.K8sNodeDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.k8s.K8sNodeService;
import com.xc.fast_deploy.utils.constant.K8sPatchMirror;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sPatchVo;
import com.xc.fast_deploy.vo.k8s_vo.K8sNodeVo;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

@Service
public class K8sNodeServiceImpl implements K8sNodeService {
  
  @Autowired
  private ModuleEnvMapper envMapper;
  
  /**
   * 列出所有node节点对应的信息
   *
   * @param envId
   * @param nodeIPKey
   * @param labelKeyName
   * @return
   */
  @Override
  public List<K8sNodeDTO> selectInfo(Integer envId, String nodeIPKey, String labelKeyName) {
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    List<K8sNodeDTO> nodeDTOList = new ArrayList<>();
    if (moduleEnv != null) {
      CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
      try {
        V1NodeList v1NodeList = coreV1Api.listNode(null, true, null,
            null, null, null,
            null, null, null);
        List<V1Node> items = v1NodeList.getItems();
        if (items != null && items.size() > 0) {
          for (V1Node v1Node : items) {
            K8sNodeDTO k8sNodeDTO = new K8sNodeDTO();
            k8sNodeDTO.setNodeName(v1Node.getMetadata().getName());
            List<V1NodeAddress> addresses = v1Node.getStatus().getAddresses();
            String nodeIP = null;
            for (V1NodeAddress nodeAddress : addresses) {
              if ("InternalIP".equals(nodeAddress.getType())) {
                nodeIP = nodeAddress.getAddress();
              }
            }
            //加入nodeIPKey的过滤条件
            if (StringUtils.isNotBlank(nodeIP) && StringUtils.isNotBlank(nodeIPKey)
                && !nodeIP.contains(nodeIPKey)) {
              continue;
            }
            k8sNodeDTO.setNodeIP(nodeIP);
            k8sNodeDTO.setCreateTime(v1Node.getMetadata().getCreationTimestamp().toDate());
            k8sNodeDTO.setPodCIDR(v1Node.getSpec().getPodCIDR());
            List<V1NodeCondition> conditions = v1Node.getStatus().getConditions();
            if (conditions != null && conditions.size() > 0) {
              for (V1NodeCondition condition : conditions) {
                String type = condition.getType();
                if ("Ready".equals(type)) {
                  if ("True".equals(condition.getStatus())) {
                    k8sNodeDTO.setStatus("Ready");
                  } else {
                    k8sNodeDTO.setStatus("NotReady");
                  }
                }
              }
            }
            if (v1Node.getSpec().getUnschedulable() != null &&
                v1Node.getSpec().getUnschedulable()) {
              k8sNodeDTO.setStatus(k8sNodeDTO.getStatus() + ",SchedulingDisabled");
            }
            Map<String, String> labels = v1Node.getMetadata().getLabels();
            Map<String, String> nodeLabelMap = new HashMap<>();
            if (labels != null && labels.size() > 0) {
              Set<String> labelKeySet = labels.keySet();
              for (String key : labelKeySet) {
                String value = labels.get(key);
                if (StringUtils.isNotBlank(key) && !key.contains("kubernetes.io")) {
                  nodeLabelMap.put(key, value);
                }
              }
            }
            k8sNodeDTO.setLabelMap(nodeLabelMap);
            //加入标签labelKeyName的过滤条件
            if (StringUtils.isNotBlank(labelKeyName)
                && !nodeLabelMap.toString().contains(labelKeyName)) {
              continue;
            }
            List<V1Taint> taints = v1Node.getSpec().getTaints();
            k8sNodeDTO.setRoleName("Node");
            if (taints != null && taints.size() > 0) {
              for (V1Taint v1Taint : taints) {
                if ("node-role.kubernetes.io/master".equals(v1Taint.getKey())) {
                  k8sNodeDTO.setRoleName("Master");
                }
              }
            }
            nodeDTOList.add(k8sNodeDTO);
          }
        }
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
    return nodeDTOList;
  }
  
  /**
   * 隔离node节点
   *
   * @param envId
   * @param nodeName
   * @return
   */
  @Override
  public boolean scheduleNode(Integer envId, String nodeName, boolean unScheduleNode) {
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv != null) {
      K8sPatchVo k8sPatchVo = new K8sPatchVo();
      k8sPatchVo.setOp("add");
      k8sPatchVo.setPath(K8sPatchMirror.NODE_UNSCHEDULABLE);
      k8sPatchVo.setValue(unScheduleNode);
      JsonElement element = new Gson().fromJson(JSONObject.toJSONString(k8sPatchVo), JsonElement.class);
      JsonObject jsonObject = element.getAsJsonObject();
      List<JsonObject> pathList = new ArrayList<>();
      pathList.add(jsonObject);
      
      CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
      
      try {
        V1Node v1Node = coreV1Api.patchNode(nodeName, new V1Patch(pathList.toString()), null, null, null, null);
        if (v1Node != null) {
          return true;
        }
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
  
  /**
   * 修改node节点的标签
   *
   * @param k8sNodeVo
   * @return
   */
  @Override
  public boolean updateNodeLabel(K8sNodeVo k8sNodeVo) {
    ModuleEnv moduleEnv = envMapper.selectOne(k8sNodeVo.getEnvId());
    if (moduleEnv != null) {
      CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
      try {
        V1Node v1Node = coreV1Api.readNode(k8sNodeVo.getNodeName(),
            null, null, null);
        if (v1Node != null) {
          Map<String, String> labels = v1Node.getMetadata().getLabels();
          Map<String, String> kubenerateIoMap = new HashMap<>();
          if (labels != null && labels.size() > 0) {
            Set<String> labelKeySet = labels.keySet();
            for (String labelKey : labelKeySet) {
              if (labelKey.contains("kubernetes.io")) {
                kubenerateIoMap.put(labelKey, labels.get(labelKey));
              }
            }
          }
          Map<String, String> labelMap = k8sNodeVo.getLabelMap();
          Set<String> kubeSet = kubenerateIoMap.keySet();
          Set<String> labelMapSet = labelMap.keySet();
          //去两个set集合的并集成为整个的标签集合
          Set<String> differenceSet = Sets.union(labelMapSet, kubeSet);
          Map<String, String> result = Maps.newHashMap();
          for (String key : differenceSet) {
            if (kubenerateIoMap.containsKey(key)) {
              result.put(key, kubenerateIoMap.get(key));
            } else {
              result.put(key, labelMap.get(key));
            }
          }
          List<JsonObject> pathList =
              K8sUtils.generatePatchPath(K8sPatchMirror.NODE_LABEL, result, null);
          V1Node pathV1Node = coreV1Api.patchNode(k8sNodeVo.getNodeName(), new V1Patch(pathList.toString()),
              null, null, null, null);
          if (pathV1Node != null) {
            return true;
          }
        }
      } catch (ApiException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
}
