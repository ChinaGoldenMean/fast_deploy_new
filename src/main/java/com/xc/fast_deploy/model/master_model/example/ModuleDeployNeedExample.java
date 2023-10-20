package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleDeployNeedExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleDeployNeedExample() {
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
    
    public Criteria andApproveStatusIsNull() {
      addCriterion("approve_status is null");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusIsNotNull() {
      addCriterion("approve_status is not null");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusEqualTo(Integer value) {
      addCriterion("approve_status =", value, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusNotEqualTo(Integer value) {
      addCriterion("approve_status <>", value, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusGreaterThan(Integer value) {
      addCriterion("approve_status >", value, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusGreaterThanOrEqualTo(Integer value) {
      addCriterion("approve_status >=", value, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusLessThan(Integer value) {
      addCriterion("approve_status <", value, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusLessThanOrEqualTo(Integer value) {
      addCriterion("approve_status <=", value, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusIn(List<Integer> values) {
      addCriterion("approve_status in", values, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusNotIn(List<Integer> values) {
      addCriterion("approve_status not in", values, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusBetween(Integer value1, Integer value2) {
      addCriterion("approve_status between", value1, value2, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andApproveStatusNotBetween(Integer value1, Integer value2) {
      addCriterion("approve_status not between", value1, value2, "approveStatus");
      return (Criteria) this;
    }
    
    public Criteria andDrIsNull() {
      addCriterion("dr is null");
      return (Criteria) this;
    }
    
    public Criteria andDrIsNotNull() {
      addCriterion("dr is not null");
      return (Criteria) this;
    }
    
    public Criteria andDrEqualTo(Integer value) {
      addCriterion("dr =", value, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrNotEqualTo(Integer value) {
      addCriterion("dr <>", value, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrGreaterThan(Integer value) {
      addCriterion("dr >", value, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrGreaterThanOrEqualTo(Integer value) {
      addCriterion("dr >=", value, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrLessThan(Integer value) {
      addCriterion("dr <", value, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrLessThanOrEqualTo(Integer value) {
      addCriterion("dr <=", value, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrIn(List<Integer> values) {
      addCriterion("dr in", values, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrNotIn(List<Integer> values) {
      addCriterion("dr not in", values, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrBetween(Integer value1, Integer value2) {
      addCriterion("dr between", value1, value2, "dr");
      return (Criteria) this;
    }
    
    public Criteria andDrNotBetween(Integer value1, Integer value2) {
      addCriterion("dr not between", value1, value2, "dr");
      return (Criteria) this;
    }
    
    public Criteria andPstTestIsNull() {
      addCriterion("pst_test is null");
      return (Criteria) this;
    }
    
    public Criteria andPstTestIsNotNull() {
      addCriterion("pst_test is not null");
      return (Criteria) this;
    }
    
    public Criteria andPstTestEqualTo(Integer value) {
      addCriterion("pst_test =", value, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestNotEqualTo(Integer value) {
      addCriterion("pst_test <>", value, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestGreaterThan(Integer value) {
      addCriterion("pst_test >", value, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestGreaterThanOrEqualTo(Integer value) {
      addCriterion("pst_test >=", value, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestLessThan(Integer value) {
      addCriterion("pst_test <", value, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestLessThanOrEqualTo(Integer value) {
      addCriterion("pst_test <=", value, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestIn(List<Integer> values) {
      addCriterion("pst_test in", values, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestNotIn(List<Integer> values) {
      addCriterion("pst_test not in", values, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestBetween(Integer value1, Integer value2) {
      addCriterion("pst_test between", value1, value2, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andPstTestNotBetween(Integer value1, Integer value2) {
      addCriterion("pst_test not between", value1, value2, "pstTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestIsNull() {
      addCriterion("dr_test is null");
      return (Criteria) this;
    }
    
    public Criteria andDrTestIsNotNull() {
      addCriterion("dr_test is not null");
      return (Criteria) this;
    }
    
    public Criteria andDrTestEqualTo(Integer value) {
      addCriterion("dr_test =", value, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestNotEqualTo(Integer value) {
      addCriterion("dr_test <>", value, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestGreaterThan(Integer value) {
      addCriterion("dr_test >", value, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestGreaterThanOrEqualTo(Integer value) {
      addCriterion("dr_test >=", value, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestLessThan(Integer value) {
      addCriterion("dr_test <", value, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestLessThanOrEqualTo(Integer value) {
      addCriterion("dr_test <=", value, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestIn(List<Integer> values) {
      addCriterion("dr_test in", values, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestNotIn(List<Integer> values) {
      addCriterion("dr_test not in", values, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestBetween(Integer value1, Integer value2) {
      addCriterion("dr_test between", value1, value2, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDrTestNotBetween(Integer value1, Integer value2) {
      addCriterion("dr_test not between", value1, value2, "drTest");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeIsNull() {
      addCriterion("deploy_time is null");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeIsNotNull() {
      addCriterion("deploy_time is not null");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeEqualTo(Date value) {
      addCriterion("deploy_time =", value, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeNotEqualTo(Date value) {
      addCriterion("deploy_time <>", value, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeGreaterThan(Date value) {
      addCriterion("deploy_time >", value, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeGreaterThanOrEqualTo(Date value) {
      addCriterion("deploy_time >=", value, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeLessThan(Date value) {
      addCriterion("deploy_time <", value, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeLessThanOrEqualTo(Date value) {
      addCriterion("deploy_time <=", value, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeIn(List<Date> values) {
      addCriterion("deploy_time in", values, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeNotIn(List<Date> values) {
      addCriterion("deploy_time not in", values, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeBetween(Date value1, Date value2) {
      addCriterion("deploy_time between", value1, value2, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andDeployTimeNotBetween(Date value1, Date value2) {
      addCriterion("deploy_time not between", value1, value2, "deployTime");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberIsNull() {
      addCriterion("need_number is null");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberIsNotNull() {
      addCriterion("need_number is not null");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberEqualTo(String value) {
      addCriterion("need_number =", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberNotEqualTo(String value) {
      addCriterion("need_number <>", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberGreaterThan(String value) {
      addCriterion("need_number >", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberGreaterThanOrEqualTo(String value) {
      addCriterion("need_number >=", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberLessThan(String value) {
      addCriterion("need_number <", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberLessThanOrEqualTo(String value) {
      addCriterion("need_number <=", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberLike(String value) {
      addCriterion("need_number like", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberNotLike(String value) {
      addCriterion("need_number not like", value, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberIn(List<String> values) {
      addCriterion("need_number in", values, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberNotIn(List<String> values) {
      addCriterion("need_number not in", values, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberBetween(String value1, String value2) {
      addCriterion("need_number between", value1, value2, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedNumberNotBetween(String value1, String value2) {
      addCriterion("need_number not between", value1, value2, "needNumber");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentIsNull() {
      addCriterion("need_content is null");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentIsNotNull() {
      addCriterion("need_content is not null");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentEqualTo(String value) {
      addCriterion("need_content =", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentNotEqualTo(String value) {
      addCriterion("need_content <>", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentGreaterThan(String value) {
      addCriterion("need_content >", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentGreaterThanOrEqualTo(String value) {
      addCriterion("need_content >=", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentLessThan(String value) {
      addCriterion("need_content <", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentLessThanOrEqualTo(String value) {
      addCriterion("need_content <=", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentLike(String value) {
      addCriterion("need_content like", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentNotLike(String value) {
      addCriterion("need_content not like", value, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentIn(List<String> values) {
      addCriterion("need_content in", values, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentNotIn(List<String> values) {
      addCriterion("need_content not in", values, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentBetween(String value1, String value2) {
      addCriterion("need_content between", value1, value2, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedContentNotBetween(String value1, String value2) {
      addCriterion("need_content not between", value1, value2, "needContent");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeIsNull() {
      addCriterion("need_describe is null");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeIsNotNull() {
      addCriterion("need_describe is not null");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeEqualTo(String value) {
      addCriterion("need_describe =", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeNotEqualTo(String value) {
      addCriterion("need_describe <>", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeGreaterThan(String value) {
      addCriterion("need_describe >", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeGreaterThanOrEqualTo(String value) {
      addCriterion("need_describe >=", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeLessThan(String value) {
      addCriterion("need_describe <", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeLessThanOrEqualTo(String value) {
      addCriterion("need_describe <=", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeLike(String value) {
      addCriterion("need_describe like", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeNotLike(String value) {
      addCriterion("need_describe not like", value, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeIn(List<String> values) {
      addCriterion("need_describe in", values, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeNotIn(List<String> values) {
      addCriterion("need_describe not in", values, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeBetween(String value1, String value2) {
      addCriterion("need_describe between", value1, value2, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andNeedDescribeNotBetween(String value1, String value2) {
      addCriterion("need_describe not between", value1, value2, "needDescribe");
      return (Criteria) this;
    }
    
    public Criteria andApproverIsNull() {
      addCriterion("approver is null");
      return (Criteria) this;
    }
    
    public Criteria andApproverIsNotNull() {
      addCriterion("approver is not null");
      return (Criteria) this;
    }
    
    public Criteria andApproverEqualTo(String value) {
      addCriterion("approver =", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverNotEqualTo(String value) {
      addCriterion("approver <>", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverGreaterThan(String value) {
      addCriterion("approver >", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverGreaterThanOrEqualTo(String value) {
      addCriterion("approver >=", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverLessThan(String value) {
      addCriterion("approver <", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverLessThanOrEqualTo(String value) {
      addCriterion("approver <=", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverLike(String value) {
      addCriterion("approver like", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverNotLike(String value) {
      addCriterion("approver not like", value, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverIn(List<String> values) {
      addCriterion("approver in", values, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverNotIn(List<String> values) {
      addCriterion("approver not in", values, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverBetween(String value1, String value2) {
      addCriterion("approver between", value1, value2, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproverNotBetween(String value1, String value2) {
      addCriterion("approver not between", value1, value2, "approver");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeIsNull() {
      addCriterion("approve_time is null");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeIsNotNull() {
      addCriterion("approve_time is not null");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeEqualTo(Date value) {
      addCriterion("approve_time =", value, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeNotEqualTo(Date value) {
      addCriterion("approve_time <>", value, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeGreaterThan(Date value) {
      addCriterion("approve_time >", value, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeGreaterThanOrEqualTo(Date value) {
      addCriterion("approve_time >=", value, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeLessThan(Date value) {
      addCriterion("approve_time <", value, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeLessThanOrEqualTo(Date value) {
      addCriterion("approve_time <=", value, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeIn(List<Date> values) {
      addCriterion("approve_time in", values, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeNotIn(List<Date> values) {
      addCriterion("approve_time not in", values, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeBetween(Date value1, Date value2) {
      addCriterion("approve_time between", value1, value2, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andApproveTimeNotBetween(Date value1, Date value2) {
      addCriterion("approve_time not between", value1, value2, "approveTime");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperIsNull() {
      addCriterion("developer is null");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperIsNotNull() {
      addCriterion("developer is not null");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperEqualTo(String value) {
      addCriterion("developer =", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperNotEqualTo(String value) {
      addCriterion("developer <>", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperGreaterThan(String value) {
      addCriterion("developer >", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperGreaterThanOrEqualTo(String value) {
      addCriterion("developer >=", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperLessThan(String value) {
      addCriterion("developer <", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperLessThanOrEqualTo(String value) {
      addCriterion("developer <=", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperLike(String value) {
      addCriterion("developer like", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperNotLike(String value) {
      addCriterion("developer not like", value, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperIn(List<String> values) {
      addCriterion("developer in", values, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperNotIn(List<String> values) {
      addCriterion("developer not in", values, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperBetween(String value1, String value2) {
      addCriterion("developer between", value1, value2, "developer");
      return (Criteria) this;
    }
    
    public Criteria andDeveloperNotBetween(String value1, String value2) {
      addCriterion("developer not between", value1, value2, "developer");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathIsNull() {
      addCriterion("test_report_path is null");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathIsNotNull() {
      addCriterion("test_report_path is not null");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathEqualTo(String value) {
      addCriterion("test_report_path =", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathNotEqualTo(String value) {
      addCriterion("test_report_path <>", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathGreaterThan(String value) {
      addCriterion("test_report_path >", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathGreaterThanOrEqualTo(String value) {
      addCriterion("test_report_path >=", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathLessThan(String value) {
      addCriterion("test_report_path <", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathLessThanOrEqualTo(String value) {
      addCriterion("test_report_path <=", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathLike(String value) {
      addCriterion("test_report_path like", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathNotLike(String value) {
      addCriterion("test_report_path not like", value, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathIn(List<String> values) {
      addCriterion("test_report_path in", values, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathNotIn(List<String> values) {
      addCriterion("test_report_path not in", values, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathBetween(String value1, String value2) {
      addCriterion("test_report_path between", value1, value2, "testReportPath");
      return (Criteria) this;
    }
    
    public Criteria andTestReportPathNotBetween(String value1, String value2) {
      addCriterion("test_report_path not between", value1, value2, "testReportPath");
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