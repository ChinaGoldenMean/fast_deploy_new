package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleNeedLogExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleNeedLogExample() {
    oredCriteria = new ArrayList<>();
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
      criteria = new ArrayList<>();
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
    
    public Criteria andNeedIdIsNull() {
      addCriterion("need_id is null");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdIsNotNull() {
      addCriterion("need_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdEqualTo(Integer value) {
      addCriterion("need_id =", value, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdNotEqualTo(Integer value) {
      addCriterion("need_id <>", value, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdGreaterThan(Integer value) {
      addCriterion("need_id >", value, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("need_id >=", value, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdLessThan(Integer value) {
      addCriterion("need_id <", value, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdLessThanOrEqualTo(Integer value) {
      addCriterion("need_id <=", value, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdIn(List<Integer> values) {
      addCriterion("need_id in", values, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdNotIn(List<Integer> values) {
      addCriterion("need_id not in", values, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdBetween(Integer value1, Integer value2) {
      addCriterion("need_id between", value1, value2, "needId");
      return (Criteria) this;
    }
    
    public Criteria andNeedIdNotBetween(Integer value1, Integer value2) {
      addCriterion("need_id not between", value1, value2, "needId");
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
    
    public Criteria andOpResultIsNull() {
      addCriterion("op_result is null");
      return (Criteria) this;
    }
    
    public Criteria andOpResultIsNotNull() {
      addCriterion("op_result is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpResultEqualTo(String value) {
      addCriterion("op_result =", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultNotEqualTo(String value) {
      addCriterion("op_result <>", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultGreaterThan(String value) {
      addCriterion("op_result >", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultGreaterThanOrEqualTo(String value) {
      addCriterion("op_result >=", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultLessThan(String value) {
      addCriterion("op_result <", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultLessThanOrEqualTo(String value) {
      addCriterion("op_result <=", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultLike(String value) {
      addCriterion("op_result like", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultNotLike(String value) {
      addCriterion("op_result not like", value, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultIn(List<String> values) {
      addCriterion("op_result in", values, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultNotIn(List<String> values) {
      addCriterion("op_result not in", values, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultBetween(String value1, String value2) {
      addCriterion("op_result between", value1, value2, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpResultNotBetween(String value1, String value2) {
      addCriterion("op_result not between", value1, value2, "opResult");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIsNull() {
      addCriterion("op_user is null");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIsNotNull() {
      addCriterion("op_user is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpUserEqualTo(String value) {
      addCriterion("op_user =", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserNotEqualTo(String value) {
      addCriterion("op_user <>", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserGreaterThan(String value) {
      addCriterion("op_user >", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserGreaterThanOrEqualTo(String value) {
      addCriterion("op_user >=", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserLessThan(String value) {
      addCriterion("op_user <", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserLessThanOrEqualTo(String value) {
      addCriterion("op_user <=", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserLike(String value) {
      addCriterion("op_user like", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserNotLike(String value) {
      addCriterion("op_user not like", value, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserIn(List<String> values) {
      addCriterion("op_user in", values, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserNotIn(List<String> values) {
      addCriterion("op_user not in", values, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserBetween(String value1, String value2) {
      addCriterion("op_user between", value1, value2, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpUserNotBetween(String value1, String value2) {
      addCriterion("op_user not between", value1, value2, "opUser");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsIsNull() {
      addCriterion("op_args is null");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsIsNotNull() {
      addCriterion("op_args is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsEqualTo(String value) {
      addCriterion("op_args =", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsNotEqualTo(String value) {
      addCriterion("op_args <>", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsGreaterThan(String value) {
      addCriterion("op_args >", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsGreaterThanOrEqualTo(String value) {
      addCriterion("op_args >=", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsLessThan(String value) {
      addCriterion("op_args <", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsLessThanOrEqualTo(String value) {
      addCriterion("op_args <=", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsLike(String value) {
      addCriterion("op_args like", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsNotLike(String value) {
      addCriterion("op_args not like", value, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsIn(List<String> values) {
      addCriterion("op_args in", values, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsNotIn(List<String> values) {
      addCriterion("op_args not in", values, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsBetween(String value1, String value2) {
      addCriterion("op_args between", value1, value2, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpArgsNotBetween(String value1, String value2) {
      addCriterion("op_args not between", value1, value2, "opArgs");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeIsNull() {
      addCriterion("op_time is null");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeIsNotNull() {
      addCriterion("op_time is not null");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeEqualTo(Date value) {
      addCriterion("op_time =", value, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeNotEqualTo(Date value) {
      addCriterion("op_time <>", value, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeGreaterThan(Date value) {
      addCriterion("op_time >", value, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeGreaterThanOrEqualTo(Date value) {
      addCriterion("op_time >=", value, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeLessThan(Date value) {
      addCriterion("op_time <", value, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeLessThanOrEqualTo(Date value) {
      addCriterion("op_time <=", value, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeIn(List<Date> values) {
      addCriterion("op_time in", values, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeNotIn(List<Date> values) {
      addCriterion("op_time not in", values, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeBetween(Date value1, Date value2) {
      addCriterion("op_time between", value1, value2, "opTime");
      return (Criteria) this;
    }
    
    public Criteria andOpTimeNotBetween(Date value1, Date value2) {
      addCriterion("op_time not between", value1, value2, "opTime");
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