package com.xc.fast_deploy.model.slave_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillingOpOnOffExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public BillingOpOnOffExample() {
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
    
    public Criteria andIsAbleNextIsNull() {
      addCriterion("is_able_next is null");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextIsNotNull() {
      addCriterion("is_able_next is not null");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextEqualTo(Integer value) {
      addCriterion("is_able_next =", value, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextNotEqualTo(Integer value) {
      addCriterion("is_able_next <>", value, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextGreaterThan(Integer value) {
      addCriterion("is_able_next >", value, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextGreaterThanOrEqualTo(Integer value) {
      addCriterion("is_able_next >=", value, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextLessThan(Integer value) {
      addCriterion("is_able_next <", value, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextLessThanOrEqualTo(Integer value) {
      addCriterion("is_able_next <=", value, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextIn(List<Integer> values) {
      addCriterion("is_able_next in", values, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextNotIn(List<Integer> values) {
      addCriterion("is_able_next not in", values, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextBetween(Integer value1, Integer value2) {
      addCriterion("is_able_next between", value1, value2, "isAbleNext");
      return (Criteria) this;
    }
    
    public Criteria andIsAbleNextNotBetween(Integer value1, Integer value2) {
      addCriterion("is_able_next not between", value1, value2, "isAbleNext");
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