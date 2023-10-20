package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleDeployLogExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleDeployLogExample() {
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
    
    public Criteria andModuleIdIsNull() {
      addCriterion("module_id is null");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdIsNotNull() {
      addCriterion("module_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdEqualTo(Integer value) {
      addCriterion("module_id =", value, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdNotEqualTo(Integer value) {
      addCriterion("module_id <>", value, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdGreaterThan(Integer value) {
      addCriterion("module_id >", value, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("module_id >=", value, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdLessThan(Integer value) {
      addCriterion("module_id <", value, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdLessThanOrEqualTo(Integer value) {
      addCriterion("module_id <=", value, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdIn(List<Integer> values) {
      addCriterion("module_id in", values, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdNotIn(List<Integer> values) {
      addCriterion("module_id not in", values, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdBetween(Integer value1, Integer value2) {
      addCriterion("module_id between", value1, value2, "moduleId");
      return (Criteria) this;
    }
    
    public Criteria andModuleIdNotBetween(Integer value1, Integer value2) {
      addCriterion("module_id not between", value1, value2, "moduleId");
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
    
    public Criteria andOpUsernameIsNull() {
      addCriterion("op_username is null");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameIsNotNull() {
      addCriterion("op_username is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameEqualTo(String value) {
      addCriterion("op_username =", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameNotEqualTo(String value) {
      addCriterion("op_username <>", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameGreaterThan(String value) {
      addCriterion("op_username >", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameGreaterThanOrEqualTo(String value) {
      addCriterion("op_username >=", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameLessThan(String value) {
      addCriterion("op_username <", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameLessThanOrEqualTo(String value) {
      addCriterion("op_username <=", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameLike(String value) {
      addCriterion("op_username like", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameNotLike(String value) {
      addCriterion("op_username not like", value, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameIn(List<String> values) {
      addCriterion("op_username in", values, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameNotIn(List<String> values) {
      addCriterion("op_username not in", values, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameBetween(String value1, String value2) {
      addCriterion("op_username between", value1, value2, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUsernameNotBetween(String value1, String value2) {
      addCriterion("op_username not between", value1, value2, "opUsername");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdIsNull() {
      addCriterion("op_user_id is null");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdIsNotNull() {
      addCriterion("op_user_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdEqualTo(String value) {
      addCriterion("op_user_id =", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdNotEqualTo(String value) {
      addCriterion("op_user_id <>", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdGreaterThan(String value) {
      addCriterion("op_user_id >", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdGreaterThanOrEqualTo(String value) {
      addCriterion("op_user_id >=", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdLessThan(String value) {
      addCriterion("op_user_id <", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdLessThanOrEqualTo(String value) {
      addCriterion("op_user_id <=", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdLike(String value) {
      addCriterion("op_user_id like", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdNotLike(String value) {
      addCriterion("op_user_id not like", value, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdIn(List<String> values) {
      addCriterion("op_user_id in", values, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdNotIn(List<String> values) {
      addCriterion("op_user_id not in", values, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdBetween(String value1, String value2) {
      addCriterion("op_user_id between", value1, value2, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIdNotBetween(String value1, String value2) {
      addCriterion("op_user_id not between", value1, value2, "opUserId");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveIsNull() {
      addCriterion("op_active is null");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveIsNotNull() {
      addCriterion("op_active is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveEqualTo(String value) {
      addCriterion("op_active =", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveNotEqualTo(String value) {
      addCriterion("op_active <>", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveGreaterThan(String value) {
      addCriterion("op_active >", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveGreaterThanOrEqualTo(String value) {
      addCriterion("op_active >=", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveLessThan(String value) {
      addCriterion("op_active <", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveLessThanOrEqualTo(String value) {
      addCriterion("op_active <=", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveLike(String value) {
      addCriterion("op_active like", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveNotLike(String value) {
      addCriterion("op_active not like", value, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveIn(List<String> values) {
      addCriterion("op_active in", values, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveNotIn(List<String> values) {
      addCriterion("op_active not in", values, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveBetween(String value1, String value2) {
      addCriterion("op_active between", value1, value2, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andOpActiveNotBetween(String value1, String value2) {
      addCriterion("op_active not between", value1, value2, "opActive");
      return (Criteria) this;
    }
    
    public Criteria andArgsIsNull() {
      addCriterion("args is null");
      return (Criteria) this;
    }
    
    public Criteria andArgsIsNotNull() {
      addCriterion("args is not null");
      return (Criteria) this;
    }
    
    public Criteria andArgsEqualTo(String value) {
      addCriterion("args =", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsNotEqualTo(String value) {
      addCriterion("args <>", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsGreaterThan(String value) {
      addCriterion("args >", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsGreaterThanOrEqualTo(String value) {
      addCriterion("args >=", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsLessThan(String value) {
      addCriterion("args <", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsLessThanOrEqualTo(String value) {
      addCriterion("args <=", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsLike(String value) {
      addCriterion("args like", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsNotLike(String value) {
      addCriterion("args not like", value, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsIn(List<String> values) {
      addCriterion("args in", values, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsNotIn(List<String> values) {
      addCriterion("args not in", values, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsBetween(String value1, String value2) {
      addCriterion("args between", value1, value2, "args");
      return (Criteria) this;
    }
    
    public Criteria andArgsNotBetween(String value1, String value2) {
      addCriterion("args not between", value1, value2, "args");
      return (Criteria) this;
    }
    
    public Criteria andOpResultIsNull() {
      addCriterion("op_result is null");
      return (Criteria) this;
    }
    
    public Criteria andOpResultIsNotNull() {
      addCriterion("op_result is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpResultEqualTo(Integer value) {
      addCriterion("op_result =", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultNotEqualTo(Integer value) {
      addCriterion("op_result <>", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultGreaterThan(Integer value) {
      addCriterion("op_result >", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultGreaterThanOrEqualTo(Integer value) {
      addCriterion("op_result >=", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultLessThan(Integer value) {
      addCriterion("op_result <", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultLessThanOrEqualTo(Integer value) {
      addCriterion("op_result <=", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultIn(List<Integer> values) {
      addCriterion("op_result in", values, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultNotIn(List<Integer> values) {
      addCriterion("op_result not in", values, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultBetween(Integer value1, Integer value2) {
      addCriterion("op_result between", value1, value2, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultNotBetween(Integer value1, Integer value2) {
      addCriterion("op_result not between", value1, value2, "opResult");
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