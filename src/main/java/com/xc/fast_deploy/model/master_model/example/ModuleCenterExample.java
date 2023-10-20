package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleCenterExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleCenterExample() {
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
    
    public Criteria andCenterNameIsNull() {
      addCriterion("center_name is null");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameIsNotNull() {
      addCriterion("center_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameEqualTo(String value) {
      addCriterion("center_name =", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameNotEqualTo(String value) {
      addCriterion("center_name <>", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameGreaterThan(String value) {
      addCriterion("center_name >", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameGreaterThanOrEqualTo(String value) {
      addCriterion("center_name >=", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameLessThan(String value) {
      addCriterion("center_name <", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameLessThanOrEqualTo(String value) {
      addCriterion("center_name <=", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameLike(String value) {
      addCriterion("center_name like", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameNotLike(String value) {
      addCriterion("center_name not like", value, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameIn(List<String> values) {
      addCriterion("center_name in", values, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameNotIn(List<String> values) {
      addCriterion("center_name not in", values, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameBetween(String value1, String value2) {
      addCriterion("center_name between", value1, value2, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterNameNotBetween(String value1, String value2) {
      addCriterion("center_name not between", value1, value2, "centerName");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeIsNull() {
      addCriterion("center_code is null");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeIsNotNull() {
      addCriterion("center_code is not null");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeEqualTo(String value) {
      addCriterion("center_code =", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeNotEqualTo(String value) {
      addCriterion("center_code <>", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeGreaterThan(String value) {
      addCriterion("center_code >", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeGreaterThanOrEqualTo(String value) {
      addCriterion("center_code >=", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeLessThan(String value) {
      addCriterion("center_code <", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeLessThanOrEqualTo(String value) {
      addCriterion("center_code <=", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeLike(String value) {
      addCriterion("center_code like", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeNotLike(String value) {
      addCriterion("center_code not like", value, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeIn(List<String> values) {
      addCriterion("center_code in", values, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeNotIn(List<String> values) {
      addCriterion("center_code not in", values, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeBetween(String value1, String value2) {
      addCriterion("center_code between", value1, value2, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andCenterCodeNotBetween(String value1, String value2) {
      addCriterion("center_code not between", value1, value2, "centerCode");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameIsNull() {
      addCriterion("child_center_name is null");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameIsNotNull() {
      addCriterion("child_center_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameEqualTo(String value) {
      addCriterion("child_center_name =", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameNotEqualTo(String value) {
      addCriterion("child_center_name <>", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameGreaterThan(String value) {
      addCriterion("child_center_name >", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameGreaterThanOrEqualTo(String value) {
      addCriterion("child_center_name >=", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameLessThan(String value) {
      addCriterion("child_center_name <", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameLessThanOrEqualTo(String value) {
      addCriterion("child_center_name <=", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameLike(String value) {
      addCriterion("child_center_name like", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameNotLike(String value) {
      addCriterion("child_center_name not like", value, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameIn(List<String> values) {
      addCriterion("child_center_name in", values, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameNotIn(List<String> values) {
      addCriterion("child_center_name not in", values, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameBetween(String value1, String value2) {
      addCriterion("child_center_name between", value1, value2, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterNameNotBetween(String value1, String value2) {
      addCriterion("child_center_name not between", value1, value2, "childCenterName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameIsNull() {
      addCriterion("child_center_content_name is null");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameIsNotNull() {
      addCriterion("child_center_content_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameEqualTo(String value) {
      addCriterion("child_center_content_name =", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameNotEqualTo(String value) {
      addCriterion("child_center_content_name <>", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameGreaterThan(String value) {
      addCriterion("child_center_content_name >", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameGreaterThanOrEqualTo(String value) {
      addCriterion("child_center_content_name >=", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameLessThan(String value) {
      addCriterion("child_center_content_name <", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameLessThanOrEqualTo(String value) {
      addCriterion("child_center_content_name <=", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameLike(String value) {
      addCriterion("child_center_content_name like", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameNotLike(String value) {
      addCriterion("child_center_content_name not like", value, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameIn(List<String> values) {
      addCriterion("child_center_content_name in", values, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameNotIn(List<String> values) {
      addCriterion("child_center_content_name not in", values, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameBetween(String value1, String value2) {
      addCriterion("child_center_content_name between", value1, value2, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andChildCenterContentNameNotBetween(String value1, String value2) {
      addCriterion("child_center_content_name not between", value1, value2, "childCenterContentName");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathIsNull() {
      addCriterion("center_path is null");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathIsNotNull() {
      addCriterion("center_path is not null");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathEqualTo(String value) {
      addCriterion("center_path =", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathNotEqualTo(String value) {
      addCriterion("center_path <>", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathGreaterThan(String value) {
      addCriterion("center_path >", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathGreaterThanOrEqualTo(String value) {
      addCriterion("center_path >=", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathLessThan(String value) {
      addCriterion("center_path <", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathLessThanOrEqualTo(String value) {
      addCriterion("center_path <=", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathLike(String value) {
      addCriterion("center_path like", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathNotLike(String value) {
      addCriterion("center_path not like", value, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathIn(List<String> values) {
      addCriterion("center_path in", values, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathNotIn(List<String> values) {
      addCriterion("center_path not in", values, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathBetween(String value1, String value2) {
      addCriterion("center_path between", value1, value2, "centerPath");
      return (Criteria) this;
    }
    
    public Criteria andCenterPathNotBetween(String value1, String value2) {
      addCriterion("center_path not between", value1, value2, "centerPath");
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
    
    public Criteria andRemarkIsNull() {
      addCriterion("remark is null");
      return (Criteria) this;
    }
    
    public Criteria andRemarkIsNotNull() {
      addCriterion("remark is not null");
      return (Criteria) this;
    }
    
    public Criteria andRemarkEqualTo(String value) {
      addCriterion("remark =", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkNotEqualTo(String value) {
      addCriterion("remark <>", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkGreaterThan(String value) {
      addCriterion("remark >", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkGreaterThanOrEqualTo(String value) {
      addCriterion("remark >=", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkLessThan(String value) {
      addCriterion("remark <", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkLessThanOrEqualTo(String value) {
      addCriterion("remark <=", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkLike(String value) {
      addCriterion("remark like", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkNotLike(String value) {
      addCriterion("remark not like", value, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkIn(List<String> values) {
      addCriterion("remark in", values, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkNotIn(List<String> values) {
      addCriterion("remark not in", values, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBetween(String value1, String value2) {
      addCriterion("remark between", value1, value2, "remark");
      return (Criteria) this;
    }
    
    public Criteria andRemarkNotBetween(String value1, String value2) {
      addCriterion("remark not between", value1, value2, "remark");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedIsNull() {
      addCriterion("is_deleted is null");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedIsNotNull() {
      addCriterion("is_deleted is not null");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedEqualTo(Integer value) {
      addCriterion("is_deleted =", value, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedNotEqualTo(Integer value) {
      addCriterion("is_deleted <>", value, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedGreaterThan(Integer value) {
      addCriterion("is_deleted >", value, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedGreaterThanOrEqualTo(Integer value) {
      addCriterion("is_deleted >=", value, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedLessThan(Integer value) {
      addCriterion("is_deleted <", value, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedLessThanOrEqualTo(Integer value) {
      addCriterion("is_deleted <=", value, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedIn(List<Integer> values) {
      addCriterion("is_deleted in", values, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedNotIn(List<Integer> values) {
      addCriterion("is_deleted not in", values, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedBetween(Integer value1, Integer value2) {
      addCriterion("is_deleted between", value1, value2, "isDeleted");
      return (Criteria) this;
    }
    
    public Criteria andIsDeletedNotBetween(Integer value1, Integer value2) {
      addCriterion("is_deleted not between", value1, value2, "isDeleted");
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