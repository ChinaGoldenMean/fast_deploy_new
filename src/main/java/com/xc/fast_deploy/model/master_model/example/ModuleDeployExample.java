package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleDeployExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleDeployExample() {
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
    
    public Criteria andDeployNameIsNull() {
      addCriterion("deploy_name is null");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameIsNotNull() {
      addCriterion("deploy_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameEqualTo(String value) {
      addCriterion("deploy_name =", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameNotEqualTo(String value) {
      addCriterion("deploy_name <>", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameGreaterThan(String value) {
      addCriterion("deploy_name >", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameGreaterThanOrEqualTo(String value) {
      addCriterion("deploy_name >=", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameLessThan(String value) {
      addCriterion("deploy_name <", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameLessThanOrEqualTo(String value) {
      addCriterion("deploy_name <=", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameLike(String value) {
      addCriterion("deploy_name like", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameNotLike(String value) {
      addCriterion("deploy_name not like", value, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameIn(List<String> values) {
      addCriterion("deploy_name in", values, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameNotIn(List<String> values) {
      addCriterion("deploy_name not in", values, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameBetween(String value1, String value2) {
      addCriterion("deploy_name between", value1, value2, "deployName");
      return (Criteria) this;
    }
    
    public Criteria andDeployNameNotBetween(String value1, String value2) {
      addCriterion("deploy_name not between", value1, value2, "deployName");
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
    
    public Criteria andGenernateNameIsNull() {
      addCriterion("genernate_name is null");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameIsNotNull() {
      addCriterion("genernate_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameEqualTo(String value) {
      addCriterion("genernate_name =", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameNotEqualTo(String value) {
      addCriterion("genernate_name <>", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameGreaterThan(String value) {
      addCriterion("genernate_name >", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameGreaterThanOrEqualTo(String value) {
      addCriterion("genernate_name >=", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameLessThan(String value) {
      addCriterion("genernate_name <", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameLessThanOrEqualTo(String value) {
      addCriterion("genernate_name <=", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameLike(String value) {
      addCriterion("genernate_name like", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameNotLike(String value) {
      addCriterion("genernate_name not like", value, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameIn(List<String> values) {
      addCriterion("genernate_name in", values, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameNotIn(List<String> values) {
      addCriterion("genernate_name not in", values, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameBetween(String value1, String value2) {
      addCriterion("genernate_name between", value1, value2, "genernateName");
      return (Criteria) this;
    }
    
    public Criteria andGenernateNameNotBetween(String value1, String value2) {
      addCriterion("genernate_name not between", value1, value2, "genernateName");
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
    
    public Criteria andDeployStatusIsNull() {
      addCriterion("deploy_status is null");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusIsNotNull() {
      addCriterion("deploy_status is not null");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusEqualTo(String value) {
      addCriterion("deploy_status =", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusNotEqualTo(String value) {
      addCriterion("deploy_status <>", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusGreaterThan(String value) {
      addCriterion("deploy_status >", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusGreaterThanOrEqualTo(String value) {
      addCriterion("deploy_status >=", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusLessThan(String value) {
      addCriterion("deploy_status <", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusLessThanOrEqualTo(String value) {
      addCriterion("deploy_status <=", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusLike(String value) {
      addCriterion("deploy_status like", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusNotLike(String value) {
      addCriterion("deploy_status not like", value, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusIn(List<String> values) {
      addCriterion("deploy_status in", values, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusNotIn(List<String> values) {
      addCriterion("deploy_status not in", values, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusBetween(String value1, String value2) {
      addCriterion("deploy_status between", value1, value2, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andDeployStatusNotBetween(String value1, String value2) {
      addCriterion("deploy_status not between", value1, value2, "deployStatus");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteIsNull() {
      addCriterion("is_delete is null");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteIsNotNull() {
      addCriterion("is_delete is not null");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteEqualTo(Integer value) {
      addCriterion("is_delete =", value, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteNotEqualTo(Integer value) {
      addCriterion("is_delete <>", value, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteGreaterThan(Integer value) {
      addCriterion("is_delete >", value, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteGreaterThanOrEqualTo(Integer value) {
      addCriterion("is_delete >=", value, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteLessThan(Integer value) {
      addCriterion("is_delete <", value, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteLessThanOrEqualTo(Integer value) {
      addCriterion("is_delete <=", value, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteIn(List<Integer> values) {
      addCriterion("is_delete in", values, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteNotIn(List<Integer> values) {
      addCriterion("is_delete not in", values, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteBetween(Integer value1, Integer value2) {
      addCriterion("is_delete between", value1, value2, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andIsDeleteNotBetween(Integer value1, Integer value2) {
      addCriterion("is_delete not between", value1, value2, "isDelete");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeIsNull() {
      addCriterion("last_deploy_time is null");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeIsNotNull() {
      addCriterion("last_deploy_time is not null");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeEqualTo(Date value) {
      addCriterion("last_deploy_time =", value, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeNotEqualTo(Date value) {
      addCriterion("last_deploy_time <>", value, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeGreaterThan(Date value) {
      addCriterion("last_deploy_time >", value, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeGreaterThanOrEqualTo(Date value) {
      addCriterion("last_deploy_time >=", value, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeLessThan(Date value) {
      addCriterion("last_deploy_time <", value, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeLessThanOrEqualTo(Date value) {
      addCriterion("last_deploy_time <=", value, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeIn(List<Date> values) {
      addCriterion("last_deploy_time in", values, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeNotIn(List<Date> values) {
      addCriterion("last_deploy_time not in", values, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeBetween(Date value1, Date value2) {
      addCriterion("last_deploy_time between", value1, value2, "lastDeployTime");
      return (Criteria) this;
    }
    
    public Criteria andLastDeployTimeNotBetween(Date value1, Date value2) {
      addCriterion("last_deploy_time not between", value1, value2, "lastDeployTime");
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