package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleDeployYamlExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleDeployYamlExample() {
    oredCriteria = new ArrayList<Criteria>();
  }
  
  public void setOrderByClause(String orderByClause) {
    this.orderByClause = orderByClause;
  }
  
  public String getOrderByClause() {
    return orderByClause;
  }
  
  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }
  
  public boolean isDistinct() {
    return distinct;
  }
  
  public List<Criteria> getOredCriteria() {
    return oredCriteria;
  }
  
  public void or(Criteria criteria) {
    oredCriteria.add(criteria);
  }
  
  public Criteria or() {
    Criteria criteria = createCriteriaInternal();
    oredCriteria.add(criteria);
    return criteria;
  }
  
  public Criteria createCriteria() {
    Criteria criteria = createCriteriaInternal();
    if (oredCriteria.size() == 0) {
      oredCriteria.add(criteria);
    }
    return criteria;
  }
  
  protected Criteria createCriteriaInternal() {
    Criteria criteria = new Criteria();
    return criteria;
  }
  
  public void clear() {
    oredCriteria.clear();
    orderByClause = null;
    distinct = false;
  }
  
  protected abstract static class GeneratedCriteria {
    protected List<Criterion> criteria;
    
    protected GeneratedCriteria() {
      super();
      criteria = new ArrayList<Criterion>();
    }
    
    public boolean isValid() {
      return criteria.size() > 0;
    }
    
    public List<Criterion> getAllCriteria() {
      return criteria;
    }
    
    public List<Criterion> getCriteria() {
      return criteria;
    }
    
    protected void addCriterion(String condition) {
      if (condition == null) {
        throw new RuntimeException("Value for condition cannot be null");
      }
      criteria.add(new Criterion(condition));
    }
    
    protected void addCriterion(String condition, Object value, String property) {
      if (value == null) {
        throw new RuntimeException("Value for " + property + " cannot be null");
      }
      criteria.add(new Criterion(condition, value));
    }
    
    protected void addCriterion(String condition, Object value1, Object value2, String property) {
      if (value1 == null || value2 == null) {
        throw new RuntimeException("Between values for " + property + " cannot be null");
      }
      criteria.add(new Criterion(condition, value1, value2));
    }
    
    public Criteria andIdIsNull() {
      addCriterion("id is null");
      return (Criteria) this;
    }
    
    public Criteria andIdIsNotNull() {
      addCriterion("id is not null");
      return (Criteria) this;
    }
    
    public Criteria andIdEqualTo(Integer value) {
      addCriterion("id =", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdNotEqualTo(Integer value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdGreaterThan(Integer value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdLessThan(Integer value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdLessThanOrEqualTo(Integer value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdIn(List<Integer> values) {
      addCriterion("id in", values, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdNotIn(List<Integer> values) {
      addCriterion("id not in", values, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdBetween(Integer value1, Integer value2) {
      addCriterion("id between", value1, value2, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdNotBetween(Integer value1, Integer value2) {
      addCriterion("id not between", value1, value2, "id");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameIsNull() {
      addCriterion("yaml_name is null");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameIsNotNull() {
      addCriterion("yaml_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameEqualTo(String value) {
      addCriterion("yaml_name =", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameNotEqualTo(String value) {
      addCriterion("yaml_name <>", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameGreaterThan(String value) {
      addCriterion("yaml_name >", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameGreaterThanOrEqualTo(String value) {
      addCriterion("yaml_name >=", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameLessThan(String value) {
      addCriterion("yaml_name <", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameLessThanOrEqualTo(String value) {
      addCriterion("yaml_name <=", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameLike(String value) {
      addCriterion("yaml_name like", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameNotLike(String value) {
      addCriterion("yaml_name not like", value, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameIn(List<String> values) {
      addCriterion("yaml_name in", values, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameNotIn(List<String> values) {
      addCriterion("yaml_name not in", values, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameBetween(String value1, String value2) {
      addCriterion("yaml_name between", value1, value2, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlNameNotBetween(String value1, String value2) {
      addCriterion("yaml_name not between", value1, value2, "yamlName");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeIsNull() {
      addCriterion("yaml_type is null");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeIsNotNull() {
      addCriterion("yaml_type is not null");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeEqualTo(String value) {
      addCriterion("yaml_type =", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeNotEqualTo(String value) {
      addCriterion("yaml_type <>", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeGreaterThan(String value) {
      addCriterion("yaml_type >", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeGreaterThanOrEqualTo(String value) {
      addCriterion("yaml_type >=", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeLessThan(String value) {
      addCriterion("yaml_type <", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeLessThanOrEqualTo(String value) {
      addCriterion("yaml_type <=", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeLike(String value) {
      addCriterion("yaml_type like", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeNotLike(String value) {
      addCriterion("yaml_type not like", value, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeIn(List<String> values) {
      addCriterion("yaml_type in", values, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeNotIn(List<String> values) {
      addCriterion("yaml_type not in", values, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeBetween(String value1, String value2) {
      addCriterion("yaml_type between", value1, value2, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlTypeNotBetween(String value1, String value2) {
      addCriterion("yaml_type not between", value1, value2, "yamlType");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathIsNull() {
      addCriterion("yaml_path is null");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathIsNotNull() {
      addCriterion("yaml_path is not null");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathEqualTo(String value) {
      addCriterion("yaml_path =", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathNotEqualTo(String value) {
      addCriterion("yaml_path <>", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathGreaterThan(String value) {
      addCriterion("yaml_path >", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathGreaterThanOrEqualTo(String value) {
      addCriterion("yaml_path >=", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathLessThan(String value) {
      addCriterion("yaml_path <", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathLessThanOrEqualTo(String value) {
      addCriterion("yaml_path <=", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathLike(String value) {
      addCriterion("yaml_path like", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathNotLike(String value) {
      addCriterion("yaml_path not like", value, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathIn(List<String> values) {
      addCriterion("yaml_path in", values, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathNotIn(List<String> values) {
      addCriterion("yaml_path not in", values, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathBetween(String value1, String value2) {
      addCriterion("yaml_path between", value1, value2, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andYamlPathNotBetween(String value1, String value2) {
      addCriterion("yaml_path not between", value1, value2, "yamlPath");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedIsNull() {
      addCriterion("is_deployed is null");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedIsNotNull() {
      addCriterion("is_deployed is not null");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedEqualTo(Integer value) {
      addCriterion("is_deployed =", value, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedNotEqualTo(Integer value) {
      addCriterion("is_deployed <>", value, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedGreaterThan(Integer value) {
      addCriterion("is_deployed >", value, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedGreaterThanOrEqualTo(Integer value) {
      addCriterion("is_deployed >=", value, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedLessThan(Integer value) {
      addCriterion("is_deployed <", value, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedLessThanOrEqualTo(Integer value) {
      addCriterion("is_deployed <=", value, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedIn(List<Integer> values) {
      addCriterion("is_deployed in", values, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedNotIn(List<Integer> values) {
      addCriterion("is_deployed not in", values, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedBetween(Integer value1, Integer value2) {
      addCriterion("is_deployed between", value1, value2, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andIsDeployedNotBetween(Integer value1, Integer value2) {
      addCriterion("is_deployed not between", value1, value2, "isDeployed");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameIsNull() {
      addCriterion("mirror_name is null");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameIsNotNull() {
      addCriterion("mirror_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameEqualTo(String value) {
      addCriterion("mirror_name =", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameNotEqualTo(String value) {
      addCriterion("mirror_name <>", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameGreaterThan(String value) {
      addCriterion("mirror_name >", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameGreaterThanOrEqualTo(String value) {
      addCriterion("mirror_name >=", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameLessThan(String value) {
      addCriterion("mirror_name <", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameLessThanOrEqualTo(String value) {
      addCriterion("mirror_name <=", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameLike(String value) {
      addCriterion("mirror_name like", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameNotLike(String value) {
      addCriterion("mirror_name not like", value, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameIn(List<String> values) {
      addCriterion("mirror_name in", values, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameNotIn(List<String> values) {
      addCriterion("mirror_name not in", values, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameBetween(String value1, String value2) {
      addCriterion("mirror_name between", value1, value2, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andMirrorNameNotBetween(String value1, String value2) {
      addCriterion("mirror_name not between", value1, value2, "mirrorName");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlIsNull() {
      addCriterion("is_online_yaml is null");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlIsNotNull() {
      addCriterion("is_online_yaml is not null");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlEqualTo(Integer value) {
      addCriterion("is_online_yaml =", value, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlNotEqualTo(Integer value) {
      addCriterion("is_online_yaml <>", value, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlGreaterThan(Integer value) {
      addCriterion("is_online_yaml >", value, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlGreaterThanOrEqualTo(Integer value) {
      addCriterion("is_online_yaml >=", value, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlLessThan(Integer value) {
      addCriterion("is_online_yaml <", value, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlLessThanOrEqualTo(Integer value) {
      addCriterion("is_online_yaml <=", value, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlIn(List<Integer> values) {
      addCriterion("is_online_yaml in", values, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlNotIn(List<Integer> values) {
      addCriterion("is_online_yaml not in", values, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlBetween(Integer value1, Integer value2) {
      addCriterion("is_online_yaml between", value1, value2, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andIsOnlineYamlNotBetween(Integer value1, Integer value2) {
      addCriterion("is_online_yaml not between", value1, value2, "isOnlineYaml");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceIsNull() {
      addCriterion("yaml_namespace is null");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceIsNotNull() {
      addCriterion("yaml_namespace is not null");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceEqualTo(String value) {
      addCriterion("yaml_namespace =", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceNotEqualTo(String value) {
      addCriterion("yaml_namespace <>", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceGreaterThan(String value) {
      addCriterion("yaml_namespace >", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceGreaterThanOrEqualTo(String value) {
      addCriterion("yaml_namespace >=", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceLessThan(String value) {
      addCriterion("yaml_namespace <", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceLessThanOrEqualTo(String value) {
      addCriterion("yaml_namespace <=", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceLike(String value) {
      addCriterion("yaml_namespace like", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceNotLike(String value) {
      addCriterion("yaml_namespace not like", value, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceIn(List<String> values) {
      addCriterion("yaml_namespace in", values, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceNotIn(List<String> values) {
      addCriterion("yaml_namespace not in", values, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceBetween(String value1, String value2) {
      addCriterion("yaml_namespace between", value1, value2, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andYamlNamespaceNotBetween(String value1, String value2) {
      addCriterion("yaml_namespace not between", value1, value2, "yamlNamespace");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameIsNull() {
      addCriterion("container_name is null");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameIsNotNull() {
      addCriterion("container_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameEqualTo(String value) {
      addCriterion("container_name =", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameNotEqualTo(String value) {
      addCriterion("container_name <>", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameGreaterThan(String value) {
      addCriterion("container_name >", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameGreaterThanOrEqualTo(String value) {
      addCriterion("container_name >=", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameLessThan(String value) {
      addCriterion("container_name <", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameLessThanOrEqualTo(String value) {
      addCriterion("container_name <=", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameLike(String value) {
      addCriterion("container_name like", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameNotLike(String value) {
      addCriterion("container_name not like", value, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameIn(List<String> values) {
      addCriterion("container_name in", values, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameNotIn(List<String> values) {
      addCriterion("container_name not in", values, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameBetween(String value1, String value2) {
      addCriterion("container_name between", value1, value2, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andContainerNameNotBetween(String value1, String value2) {
      addCriterion("container_name not between", value1, value2, "containerName");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdIsNull() {
      addCriterion("deploy_id is null");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdIsNotNull() {
      addCriterion("deploy_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdEqualTo(Integer value) {
      addCriterion("deploy_id =", value, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdNotEqualTo(Integer value) {
      addCriterion("deploy_id <>", value, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdGreaterThan(Integer value) {
      addCriterion("deploy_id >", value, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("deploy_id >=", value, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdLessThan(Integer value) {
      addCriterion("deploy_id <", value, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdLessThanOrEqualTo(Integer value) {
      addCriterion("deploy_id <=", value, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdIn(List<Integer> values) {
      addCriterion("deploy_id in", values, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdNotIn(List<Integer> values) {
      addCriterion("deploy_id not in", values, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdBetween(Integer value1, Integer value2) {
      addCriterion("deploy_id between", value1, value2, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andDeployIdNotBetween(Integer value1, Integer value2) {
      addCriterion("deploy_id not between", value1, value2, "deployId");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeIsNull() {
      addCriterion("create_time is null");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeIsNotNull() {
      addCriterion("create_time is not null");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeEqualTo(Date value) {
      addCriterion("create_time =", value, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeNotEqualTo(Date value) {
      addCriterion("create_time <>", value, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeGreaterThan(Date value) {
      addCriterion("create_time >", value, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
      addCriterion("create_time >=", value, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeLessThan(Date value) {
      addCriterion("create_time <", value, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
      addCriterion("create_time <=", value, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeIn(List<Date> values) {
      addCriterion("create_time in", values, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeNotIn(List<Date> values) {
      addCriterion("create_time not in", values, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeBetween(Date value1, Date value2) {
      addCriterion("create_time between", value1, value2, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
      addCriterion("create_time not between", value1, value2, "createTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeIsNull() {
      addCriterion("update_time is null");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeIsNotNull() {
      addCriterion("update_time is not null");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeEqualTo(Date value) {
      addCriterion("update_time =", value, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeNotEqualTo(Date value) {
      addCriterion("update_time <>", value, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeGreaterThan(Date value) {
      addCriterion("update_time >", value, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
      addCriterion("update_time >=", value, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeLessThan(Date value) {
      addCriterion("update_time <", value, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
      addCriterion("update_time <=", value, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeIn(List<Date> values) {
      addCriterion("update_time in", values, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeNotIn(List<Date> values) {
      addCriterion("update_time not in", values, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeBetween(Date value1, Date value2) {
      addCriterion("update_time between", value1, value2, "updateTime");
      return (Criteria) this;
    }
    
    public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
      addCriterion("update_time not between", value1, value2, "updateTime");
      return (Criteria) this;
    }
  }
  
  public static class Criteria extends GeneratedCriteria {
    
    protected Criteria() {
      super();
    }
  }
  
  public static class Criterion {
    private String condition;
    
    private Object value;
    
    private Object secondValue;
    
    private boolean noValue;
    
    private boolean singleValue;
    
    private boolean betweenValue;
    
    private boolean listValue;
    
    private String typeHandler;
    
    public String getCondition() {
      return condition;
    }
    
    public Object getValue() {
      return value;
    }
    
    public Object getSecondValue() {
      return secondValue;
    }
    
    public boolean isNoValue() {
      return noValue;
    }
    
    public boolean isSingleValue() {
      return singleValue;
    }
    
    public boolean isBetweenValue() {
      return betweenValue;
    }
    
    public boolean isListValue() {
      return listValue;
    }
    
    public String getTypeHandler() {
      return typeHandler;
    }
    
    protected Criterion(String condition) {
      super();
      this.condition = condition;
      this.typeHandler = null;
      this.noValue = true;
    }
    
    protected Criterion(String condition, Object value, String typeHandler) {
      super();
      this.condition = condition;
      this.value = value;
      this.typeHandler = typeHandler;
      if (value instanceof List<?>) {
        this.listValue = true;
      } else {
        this.singleValue = true;
      }
    }
    
    protected Criterion(String condition, Object value) {
      this(condition, value, null);
    }
    
    protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
      super();
      this.condition = condition;
      this.value = value;
      this.secondValue = secondValue;
      this.typeHandler = typeHandler;
      this.betweenValue = true;
    }
    
    protected Criterion(String condition, Object value, Object secondValue) {
      this(condition, value, secondValue, null);
    }
  }
}