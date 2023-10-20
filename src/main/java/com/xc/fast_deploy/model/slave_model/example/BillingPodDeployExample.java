package com.xc.fast_deploy.model.slave_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillingPodDeployExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public BillingPodDeployExample() {
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
    
    public Criteria andIdEqualTo(Long value) {
      addCriterion("id =", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdIn(List<Long> values) {
      addCriterion("id in", values, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdNotIn(List<Long> values) {
      addCriterion("id not in", values, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdBetween(Long value1, Long value2) {
      addCriterion("id between", value1, value2, "id");
      return (Criteria) this;
    }
    
    public Criteria andIdNotBetween(Long value1, Long value2) {
      addCriterion("id not between", value1, value2, "id");
      return (Criteria) this;
    }
    
    public Criteria andPodnameIsNull() {
      addCriterion("podName is null");
      return (Criteria) this;
    }
    
    public Criteria andPodnameIsNotNull() {
      addCriterion("podName is not null");
      return (Criteria) this;
    }
    
    public Criteria andPodnameEqualTo(String value) {
      addCriterion("podName =", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameNotEqualTo(String value) {
      addCriterion("podName <>", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameGreaterThan(String value) {
      addCriterion("podName >", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameGreaterThanOrEqualTo(String value) {
      addCriterion("podName >=", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameLessThan(String value) {
      addCriterion("podName <", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameLessThanOrEqualTo(String value) {
      addCriterion("podName <=", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameLike(String value) {
      addCriterion("podName like", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameNotLike(String value) {
      addCriterion("podName not like", value, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameIn(List<String> values) {
      addCriterion("podName in", values, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameNotIn(List<String> values) {
      addCriterion("podName not in", values, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameBetween(String value1, String value2) {
      addCriterion("podName between", value1, value2, "podname");
      return (Criteria) this;
    }
    
    public Criteria andPodnameNotBetween(String value1, String value2) {
      addCriterion("podName not between", value1, value2, "podname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameIsNull() {
      addCriterion("appName is null");
      return (Criteria) this;
    }
    
    public Criteria andAppnameIsNotNull() {
      addCriterion("appName is not null");
      return (Criteria) this;
    }
    
    public Criteria andAppnameEqualTo(String value) {
      addCriterion("appName =", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameNotEqualTo(String value) {
      addCriterion("appName <>", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameGreaterThan(String value) {
      addCriterion("appName >", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameGreaterThanOrEqualTo(String value) {
      addCriterion("appName >=", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameLessThan(String value) {
      addCriterion("appName <", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameLessThanOrEqualTo(String value) {
      addCriterion("appName <=", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameLike(String value) {
      addCriterion("appName like", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameNotLike(String value) {
      addCriterion("appName not like", value, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameIn(List<String> values) {
      addCriterion("appName in", values, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameNotIn(List<String> values) {
      addCriterion("appName not in", values, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameBetween(String value1, String value2) {
      addCriterion("appName between", value1, value2, "appname");
      return (Criteria) this;
    }
    
    public Criteria andAppnameNotBetween(String value1, String value2) {
      addCriterion("appName not between", value1, value2, "appname");
      return (Criteria) this;
    }
    
    public Criteria andImagenameIsNull() {
      addCriterion("imageName is null");
      return (Criteria) this;
    }
    
    public Criteria andImagenameIsNotNull() {
      addCriterion("imageName is not null");
      return (Criteria) this;
    }
    
    public Criteria andImagenameEqualTo(String value) {
      addCriterion("imageName =", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameNotEqualTo(String value) {
      addCriterion("imageName <>", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameGreaterThan(String value) {
      addCriterion("imageName >", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameGreaterThanOrEqualTo(String value) {
      addCriterion("imageName >=", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameLessThan(String value) {
      addCriterion("imageName <", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameLessThanOrEqualTo(String value) {
      addCriterion("imageName <=", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameLike(String value) {
      addCriterion("imageName like", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameNotLike(String value) {
      addCriterion("imageName not like", value, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameIn(List<String> values) {
      addCriterion("imageName in", values, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameNotIn(List<String> values) {
      addCriterion("imageName not in", values, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameBetween(String value1, String value2) {
      addCriterion("imageName between", value1, value2, "imagename");
      return (Criteria) this;
    }
    
    public Criteria andImagenameNotBetween(String value1, String value2) {
      addCriterion("imageName not between", value1, value2, "imagename");
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
    
    public Criteria andEnvCodeIsNull() {
      addCriterion("env_code is null");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeIsNotNull() {
      addCriterion("env_code is not null");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeEqualTo(String value) {
      addCriterion("env_code =", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeNotEqualTo(String value) {
      addCriterion("env_code <>", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeGreaterThan(String value) {
      addCriterion("env_code >", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeGreaterThanOrEqualTo(String value) {
      addCriterion("env_code >=", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeLessThan(String value) {
      addCriterion("env_code <", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeLessThanOrEqualTo(String value) {
      addCriterion("env_code <=", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeLike(String value) {
      addCriterion("env_code like", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeNotLike(String value) {
      addCriterion("env_code not like", value, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeIn(List<String> values) {
      addCriterion("env_code in", values, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeNotIn(List<String> values) {
      addCriterion("env_code not in", values, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeBetween(String value1, String value2) {
      addCriterion("env_code between", value1, value2, "envCode");
      return (Criteria) this;
    }
    
    public Criteria andEnvCodeNotBetween(String value1, String value2) {
      addCriterion("env_code not between", value1, value2, "envCode");
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
    
    public Criteria andCreateResultIsNull() {
      addCriterion("create_result is null");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultIsNotNull() {
      addCriterion("create_result is not null");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultEqualTo(Integer value) {
      addCriterion("create_result =", value, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultNotEqualTo(Integer value) {
      addCriterion("create_result <>", value, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultGreaterThan(Integer value) {
      addCriterion("create_result >", value, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultGreaterThanOrEqualTo(Integer value) {
      addCriterion("create_result >=", value, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultLessThan(Integer value) {
      addCriterion("create_result <", value, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultLessThanOrEqualTo(Integer value) {
      addCriterion("create_result <=", value, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultIn(List<Integer> values) {
      addCriterion("create_result in", values, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultNotIn(List<Integer> values) {
      addCriterion("create_result not in", values, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultBetween(Integer value1, Integer value2) {
      addCriterion("create_result between", value1, value2, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultNotBetween(Integer value1, Integer value2) {
      addCriterion("create_result not between", value1, value2, "createResult");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoIsNull() {
      addCriterion("create_result_info is null");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoIsNotNull() {
      addCriterion("create_result_info is not null");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoEqualTo(String value) {
      addCriterion("create_result_info =", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoNotEqualTo(String value) {
      addCriterion("create_result_info <>", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoGreaterThan(String value) {
      addCriterion("create_result_info >", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoGreaterThanOrEqualTo(String value) {
      addCriterion("create_result_info >=", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoLessThan(String value) {
      addCriterion("create_result_info <", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoLessThanOrEqualTo(String value) {
      addCriterion("create_result_info <=", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoLike(String value) {
      addCriterion("create_result_info like", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoNotLike(String value) {
      addCriterion("create_result_info not like", value, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoIn(List<String> values) {
      addCriterion("create_result_info in", values, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoNotIn(List<String> values) {
      addCriterion("create_result_info not in", values, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoBetween(String value1, String value2) {
      addCriterion("create_result_info between", value1, value2, "createResultInfo");
      return (Criteria) this;
    }
    
    public Criteria andCreateResultInfoNotBetween(String value1, String value2) {
      addCriterion("create_result_info not between", value1, value2, "createResultInfo");
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