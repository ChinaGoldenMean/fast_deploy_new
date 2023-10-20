package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleMirrorExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleMirrorExample() {
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
    
    public Criteria andModuleEnvIdIsNull() {
      addCriterion("module_env_id is null");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdIsNotNull() {
      addCriterion("module_env_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdEqualTo(Integer value) {
      addCriterion("module_env_id =", value, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdNotEqualTo(Integer value) {
      addCriterion("module_env_id <>", value, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdGreaterThan(Integer value) {
      addCriterion("module_env_id >", value, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("module_env_id >=", value, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdLessThan(Integer value) {
      addCriterion("module_env_id <", value, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdLessThanOrEqualTo(Integer value) {
      addCriterion("module_env_id <=", value, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdIn(List<Integer> values) {
      addCriterion("module_env_id in", values, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdNotIn(List<Integer> values) {
      addCriterion("module_env_id not in", values, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdBetween(Integer value1, Integer value2) {
      addCriterion("module_env_id between", value1, value2, "moduleEnvId");
      return (Criteria) this;
    }
    
    public Criteria andModuleEnvIdNotBetween(Integer value1, Integer value2) {
      addCriterion("module_env_id not between", value1, value2, "moduleEnvId");
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
    
    public Criteria andJobIdIsNull() {
      addCriterion("job_id is null");
      return (Criteria) this;
    }
    
    public Criteria andJobIdIsNotNull() {
      addCriterion("job_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andJobIdEqualTo(Integer value) {
      addCriterion("job_id =", value, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdNotEqualTo(Integer value) {
      addCriterion("job_id <>", value, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdGreaterThan(Integer value) {
      addCriterion("job_id >", value, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("job_id >=", value, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdLessThan(Integer value) {
      addCriterion("job_id <", value, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdLessThanOrEqualTo(Integer value) {
      addCriterion("job_id <=", value, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdIn(List<Integer> values) {
      addCriterion("job_id in", values, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdNotIn(List<Integer> values) {
      addCriterion("job_id not in", values, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdBetween(Integer value1, Integer value2) {
      addCriterion("job_id between", value1, value2, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobIdNotBetween(Integer value1, Integer value2) {
      addCriterion("job_id not between", value1, value2, "jobId");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionIsNull() {
      addCriterion("job_reversion is null");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionIsNotNull() {
      addCriterion("job_reversion is not null");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionEqualTo(Integer value) {
      addCriterion("job_reversion =", value, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionNotEqualTo(Integer value) {
      addCriterion("job_reversion <>", value, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionGreaterThan(Integer value) {
      addCriterion("job_reversion >", value, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionGreaterThanOrEqualTo(Integer value) {
      addCriterion("job_reversion >=", value, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionLessThan(Integer value) {
      addCriterion("job_reversion <", value, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionLessThanOrEqualTo(Integer value) {
      addCriterion("job_reversion <=", value, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionIn(List<Integer> values) {
      addCriterion("job_reversion in", values, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionNotIn(List<Integer> values) {
      addCriterion("job_reversion not in", values, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionBetween(Integer value1, Integer value2) {
      addCriterion("job_reversion between", value1, value2, "jobReversion");
      return (Criteria) this;
    }
    
    public Criteria andJobReversionNotBetween(Integer value1, Integer value2) {
      addCriterion("job_reversion not between", value1, value2, "jobReversion");
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
    
    public Criteria andIsAvailableIsNull() {
      addCriterion("is_available is null");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableIsNotNull() {
      addCriterion("is_available is not null");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableEqualTo(Integer value) {
      addCriterion("is_available =", value, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableNotEqualTo(Integer value) {
      addCriterion("is_available <>", value, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableGreaterThan(Integer value) {
      addCriterion("is_available >", value, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableGreaterThanOrEqualTo(Integer value) {
      addCriterion("is_available >=", value, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableLessThan(Integer value) {
      addCriterion("is_available <", value, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableLessThanOrEqualTo(Integer value) {
      addCriterion("is_available <=", value, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableIn(List<Integer> values) {
      addCriterion("is_available in", values, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableNotIn(List<Integer> values) {
      addCriterion("is_available not in", values, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableBetween(Integer value1, Integer value2) {
      addCriterion("is_available between", value1, value2, "isAvailable");
      return (Criteria) this;
    }
    
    public Criteria andIsAvailableNotBetween(Integer value1, Integer value2) {
      addCriterion("is_available not between", value1, value2, "isAvailable");
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