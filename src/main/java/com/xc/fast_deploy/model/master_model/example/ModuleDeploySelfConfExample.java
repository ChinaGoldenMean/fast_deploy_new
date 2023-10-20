package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleDeploySelfConfExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleDeploySelfConfExample() {
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
    
    public Criteria andResourceNameIsNull() {
      addCriterion("resource_name is null");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameIsNotNull() {
      addCriterion("resource_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameEqualTo(String value) {
      addCriterion("resource_name =", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameNotEqualTo(String value) {
      addCriterion("resource_name <>", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameGreaterThan(String value) {
      addCriterion("resource_name >", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameGreaterThanOrEqualTo(String value) {
      addCriterion("resource_name >=", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameLessThan(String value) {
      addCriterion("resource_name <", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameLessThanOrEqualTo(String value) {
      addCriterion("resource_name <=", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameLike(String value) {
      addCriterion("resource_name like", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameNotLike(String value) {
      addCriterion("resource_name not like", value, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameIn(List<String> values) {
      addCriterion("resource_name in", values, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameNotIn(List<String> values) {
      addCriterion("resource_name not in", values, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameBetween(String value1, String value2) {
      addCriterion("resource_name between", value1, value2, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceNameNotBetween(String value1, String value2) {
      addCriterion("resource_name not between", value1, value2, "resourceName");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindIsNull() {
      addCriterion("resource_kind is null");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindIsNotNull() {
      addCriterion("resource_kind is not null");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindEqualTo(String value) {
      addCriterion("resource_kind =", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindNotEqualTo(String value) {
      addCriterion("resource_kind <>", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindGreaterThan(String value) {
      addCriterion("resource_kind >", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindGreaterThanOrEqualTo(String value) {
      addCriterion("resource_kind >=", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindLessThan(String value) {
      addCriterion("resource_kind <", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindLessThanOrEqualTo(String value) {
      addCriterion("resource_kind <=", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindLike(String value) {
      addCriterion("resource_kind like", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindNotLike(String value) {
      addCriterion("resource_kind not like", value, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindIn(List<String> values) {
      addCriterion("resource_kind in", values, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindNotIn(List<String> values) {
      addCriterion("resource_kind not in", values, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindBetween(String value1, String value2) {
      addCriterion("resource_kind between", value1, value2, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceKindNotBetween(String value1, String value2) {
      addCriterion("resource_kind not between", value1, value2, "resourceKind");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathIsNull() {
      addCriterion("resource_file_path is null");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathIsNotNull() {
      addCriterion("resource_file_path is not null");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathEqualTo(String value) {
      addCriterion("resource_file_path =", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathNotEqualTo(String value) {
      addCriterion("resource_file_path <>", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathGreaterThan(String value) {
      addCriterion("resource_file_path >", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathGreaterThanOrEqualTo(String value) {
      addCriterion("resource_file_path >=", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathLessThan(String value) {
      addCriterion("resource_file_path <", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathLessThanOrEqualTo(String value) {
      addCriterion("resource_file_path <=", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathLike(String value) {
      addCriterion("resource_file_path like", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathNotLike(String value) {
      addCriterion("resource_file_path not like", value, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathIn(List<String> values) {
      addCriterion("resource_file_path in", values, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathNotIn(List<String> values) {
      addCriterion("resource_file_path not in", values, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathBetween(String value1, String value2) {
      addCriterion("resource_file_path between", value1, value2, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceFilePathNotBetween(String value1, String value2) {
      addCriterion("resource_file_path not between", value1, value2, "resourceFilePath");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceIsNull() {
      addCriterion("resource_namespace is null");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceIsNotNull() {
      addCriterion("resource_namespace is not null");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceEqualTo(String value) {
      addCriterion("resource_namespace =", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceNotEqualTo(String value) {
      addCriterion("resource_namespace <>", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceGreaterThan(String value) {
      addCriterion("resource_namespace >", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceGreaterThanOrEqualTo(String value) {
      addCriterion("resource_namespace >=", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceLessThan(String value) {
      addCriterion("resource_namespace <", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceLessThanOrEqualTo(String value) {
      addCriterion("resource_namespace <=", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceLike(String value) {
      addCriterion("resource_namespace like", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceNotLike(String value) {
      addCriterion("resource_namespace not like", value, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceIn(List<String> values) {
      addCriterion("resource_namespace in", values, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceNotIn(List<String> values) {
      addCriterion("resource_namespace not in", values, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceBetween(String value1, String value2) {
      addCriterion("resource_namespace between", value1, value2, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andResourceNamespaceNotBetween(String value1, String value2) {
      addCriterion("resource_namespace not between", value1, value2, "resourceNamespace");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdIsNull() {
      addCriterion("env_id is null");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdIsNotNull() {
      addCriterion("env_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdEqualTo(Integer value) {
      addCriterion("env_id =", value, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdNotEqualTo(Integer value) {
      addCriterion("env_id <>", value, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdGreaterThan(Integer value) {
      addCriterion("env_id >", value, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("env_id >=", value, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdLessThan(Integer value) {
      addCriterion("env_id <", value, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdLessThanOrEqualTo(Integer value) {
      addCriterion("env_id <=", value, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdIn(List<Integer> values) {
      addCriterion("env_id in", values, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdNotIn(List<Integer> values) {
      addCriterion("env_id not in", values, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdBetween(Integer value1, Integer value2) {
      addCriterion("env_id between", value1, value2, "envId");
      return (Criteria) this;
    }
    
    public Criteria andEnvIdNotBetween(Integer value1, Integer value2) {
      addCriterion("env_id not between", value1, value2, "envId");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusIsNull() {
      addCriterion("resource_status is null");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusIsNotNull() {
      addCriterion("resource_status is not null");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusEqualTo(Integer value) {
      addCriterion("resource_status =", value, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusNotEqualTo(Integer value) {
      addCriterion("resource_status <>", value, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusGreaterThan(Integer value) {
      addCriterion("resource_status >", value, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusGreaterThanOrEqualTo(Integer value) {
      addCriterion("resource_status >=", value, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusLessThan(Integer value) {
      addCriterion("resource_status <", value, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusLessThanOrEqualTo(Integer value) {
      addCriterion("resource_status <=", value, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusIn(List<Integer> values) {
      addCriterion("resource_status in", values, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusNotIn(List<Integer> values) {
      addCriterion("resource_status not in", values, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusBetween(Integer value1, Integer value2) {
      addCriterion("resource_status between", value1, value2, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andResourceStatusNotBetween(Integer value1, Integer value2) {
      addCriterion("resource_status not between", value1, value2, "resourceStatus");
      return (Criteria) this;
    }
    
    public Criteria andUserIdIsNull() {
      addCriterion("user_id is null");
      return (Criteria) this;
    }
    
    public Criteria andUserIdIsNotNull() {
      addCriterion("user_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andUserIdEqualTo(String value) {
      addCriterion("user_id =", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdNotEqualTo(String value) {
      addCriterion("user_id <>", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdGreaterThan(String value) {
      addCriterion("user_id >", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdGreaterThanOrEqualTo(String value) {
      addCriterion("user_id >=", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdLessThan(String value) {
      addCriterion("user_id <", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdLessThanOrEqualTo(String value) {
      addCriterion("user_id <=", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdLike(String value) {
      addCriterion("user_id like", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdNotLike(String value) {
      addCriterion("user_id not like", value, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdIn(List<String> values) {
      addCriterion("user_id in", values, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdNotIn(List<String> values) {
      addCriterion("user_id not in", values, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdBetween(String value1, String value2) {
      addCriterion("user_id between", value1, value2, "userId");
      return (Criteria) this;
    }
    
    public Criteria andUserIdNotBetween(String value1, String value2) {
      addCriterion("user_id not between", value1, value2, "userId");
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