package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleManageExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleManageExample() {
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
    
    public Criteria andModuleNameIsNull() {
      addCriterion("module_name is null");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameIsNotNull() {
      addCriterion("module_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameEqualTo(String value) {
      addCriterion("module_name =", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameNotEqualTo(String value) {
      addCriterion("module_name <>", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameGreaterThan(String value) {
      addCriterion("module_name >", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameGreaterThanOrEqualTo(String value) {
      addCriterion("module_name >=", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameLessThan(String value) {
      addCriterion("module_name <", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameLessThanOrEqualTo(String value) {
      addCriterion("module_name <=", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameLike(String value) {
      addCriterion("module_name like", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameNotLike(String value) {
      addCriterion("module_name not like", value, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameIn(List<String> values) {
      addCriterion("module_name in", values, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameNotIn(List<String> values) {
      addCriterion("module_name not in", values, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameBetween(String value1, String value2) {
      addCriterion("module_name between", value1, value2, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleNameNotBetween(String value1, String value2) {
      addCriterion("module_name not between", value1, value2, "moduleName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameIsNull() {
      addCriterion("module_content_name is null");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameIsNotNull() {
      addCriterion("module_content_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameEqualTo(String value) {
      addCriterion("module_content_name =", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameNotEqualTo(String value) {
      addCriterion("module_content_name <>", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameGreaterThan(String value) {
      addCriterion("module_content_name >", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameGreaterThanOrEqualTo(String value) {
      addCriterion("module_content_name >=", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameLessThan(String value) {
      addCriterion("module_content_name <", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameLessThanOrEqualTo(String value) {
      addCriterion("module_content_name <=", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameLike(String value) {
      addCriterion("module_content_name like", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameNotLike(String value) {
      addCriterion("module_content_name not like", value, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameIn(List<String> values) {
      addCriterion("module_content_name in", values, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameNotIn(List<String> values) {
      addCriterion("module_content_name not in", values, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameBetween(String value1, String value2) {
      addCriterion("module_content_name between", value1, value2, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleContentNameNotBetween(String value1, String value2) {
      addCriterion("module_content_name not between", value1, value2, "moduleContentName");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeIsNull() {
      addCriterion("module_project_code is null");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeIsNotNull() {
      addCriterion("module_project_code is not null");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeEqualTo(String value) {
      addCriterion("module_project_code =", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeNotEqualTo(String value) {
      addCriterion("module_project_code <>", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeGreaterThan(String value) {
      addCriterion("module_project_code >", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeGreaterThanOrEqualTo(String value) {
      addCriterion("module_project_code >=", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeLessThan(String value) {
      addCriterion("module_project_code <", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeLessThanOrEqualTo(String value) {
      addCriterion("module_project_code <=", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeLike(String value) {
      addCriterion("module_project_code like", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeNotLike(String value) {
      addCriterion("module_project_code not like", value, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeIn(List<String> values) {
      addCriterion("module_project_code in", values, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeNotIn(List<String> values) {
      addCriterion("module_project_code not in", values, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeBetween(String value1, String value2) {
      addCriterion("module_project_code between", value1, value2, "moduleProjectCode");
      return (Criteria) this;
    }
    
    public Criteria andModuleProjectCodeNotBetween(String value1, String value2) {
      addCriterion("module_project_code not between", value1, value2, "moduleProjectCode");
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
    
    public Criteria andMarkIsNull() {
      addCriterion("mark is null");
      return (Criteria) this;
    }
    
    public Criteria andMarkIsNotNull() {
      addCriterion("mark is not null");
      return (Criteria) this;
    }
    
    public Criteria andMarkEqualTo(String value) {
      addCriterion("mark =", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkNotEqualTo(String value) {
      addCriterion("mark <>", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkGreaterThan(String value) {
      addCriterion("mark >", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkGreaterThanOrEqualTo(String value) {
      addCriterion("mark >=", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkLessThan(String value) {
      addCriterion("mark <", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkLessThanOrEqualTo(String value) {
      addCriterion("mark <=", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkLike(String value) {
      addCriterion("mark like", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkNotLike(String value) {
      addCriterion("mark not like", value, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkIn(List<String> values) {
      addCriterion("mark in", values, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkNotIn(List<String> values) {
      addCriterion("mark not in", values, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkBetween(String value1, String value2) {
      addCriterion("mark between", value1, value2, "mark");
      return (Criteria) this;
    }
    
    public Criteria andMarkNotBetween(String value1, String value2) {
      addCriterion("mark not between", value1, value2, "mark");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonIsNull() {
      addCriterion("charge_person is null");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonIsNotNull() {
      addCriterion("charge_person is not null");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonEqualTo(String value) {
      addCriterion("charge_person =", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonNotEqualTo(String value) {
      addCriterion("charge_person <>", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonGreaterThan(String value) {
      addCriterion("charge_person >", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonGreaterThanOrEqualTo(String value) {
      addCriterion("charge_person >=", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonLessThan(String value) {
      addCriterion("charge_person <", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonLessThanOrEqualTo(String value) {
      addCriterion("charge_person <=", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonLike(String value) {
      addCriterion("charge_person like", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonNotLike(String value) {
      addCriterion("charge_person not like", value, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonIn(List<String> values) {
      addCriterion("charge_person in", values, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonNotIn(List<String> values) {
      addCriterion("charge_person not in", values, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonBetween(String value1, String value2) {
      addCriterion("charge_person between", value1, value2, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargePersonNotBetween(String value1, String value2) {
      addCriterion("charge_person not between", value1, value2, "chargePerson");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneIsNull() {
      addCriterion("charge_telephone is null");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneIsNotNull() {
      addCriterion("charge_telephone is not null");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneEqualTo(String value) {
      addCriterion("charge_telephone =", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneNotEqualTo(String value) {
      addCriterion("charge_telephone <>", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneGreaterThan(String value) {
      addCriterion("charge_telephone >", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneGreaterThanOrEqualTo(String value) {
      addCriterion("charge_telephone >=", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneLessThan(String value) {
      addCriterion("charge_telephone <", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneLessThanOrEqualTo(String value) {
      addCriterion("charge_telephone <=", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneLike(String value) {
      addCriterion("charge_telephone like", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneNotLike(String value) {
      addCriterion("charge_telephone not like", value, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneIn(List<String> values) {
      addCriterion("charge_telephone in", values, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneNotIn(List<String> values) {
      addCriterion("charge_telephone not in", values, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneBetween(String value1, String value2) {
      addCriterion("charge_telephone between", value1, value2, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andChargeTelephoneNotBetween(String value1, String value2) {
      addCriterion("charge_telephone not between", value1, value2, "chargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonIsNull() {
      addCriterion("offical_charge_person is null");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonIsNotNull() {
      addCriterion("offical_charge_person is not null");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonEqualTo(String value) {
      addCriterion("offical_charge_person =", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonNotEqualTo(String value) {
      addCriterion("offical_charge_person <>", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonGreaterThan(String value) {
      addCriterion("offical_charge_person >", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonGreaterThanOrEqualTo(String value) {
      addCriterion("offical_charge_person >=", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonLessThan(String value) {
      addCriterion("offical_charge_person <", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonLessThanOrEqualTo(String value) {
      addCriterion("offical_charge_person <=", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonLike(String value) {
      addCriterion("offical_charge_person like", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonNotLike(String value) {
      addCriterion("offical_charge_person not like", value, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonIn(List<String> values) {
      addCriterion("offical_charge_person in", values, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonNotIn(List<String> values) {
      addCriterion("offical_charge_person not in", values, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonBetween(String value1, String value2) {
      addCriterion("offical_charge_person between", value1, value2, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargePersonNotBetween(String value1, String value2) {
      addCriterion("offical_charge_person not between", value1, value2, "officalChargePerson");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneIsNull() {
      addCriterion("offical_charge_telephone is null");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneIsNotNull() {
      addCriterion("offical_charge_telephone is not null");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneEqualTo(String value) {
      addCriterion("offical_charge_telephone =", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneNotEqualTo(String value) {
      addCriterion("offical_charge_telephone <>", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneGreaterThan(String value) {
      addCriterion("offical_charge_telephone >", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneGreaterThanOrEqualTo(String value) {
      addCriterion("offical_charge_telephone >=", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneLessThan(String value) {
      addCriterion("offical_charge_telephone <", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneLessThanOrEqualTo(String value) {
      addCriterion("offical_charge_telephone <=", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneLike(String value) {
      addCriterion("offical_charge_telephone like", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneNotLike(String value) {
      addCriterion("offical_charge_telephone not like", value, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneIn(List<String> values) {
      addCriterion("offical_charge_telephone in", values, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneNotIn(List<String> values) {
      addCriterion("offical_charge_telephone not in", values, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneBetween(String value1, String value2) {
      addCriterion("offical_charge_telephone between", value1, value2, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andOfficalChargeTelephoneNotBetween(String value1, String value2) {
      addCriterion("offical_charge_telephone not between", value1, value2, "officalChargeTelephone");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdIsNull() {
      addCriterion("certificate_id is null");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdIsNotNull() {
      addCriterion("certificate_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdEqualTo(Integer value) {
      addCriterion("certificate_id =", value, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdNotEqualTo(Integer value) {
      addCriterion("certificate_id <>", value, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdGreaterThan(Integer value) {
      addCriterion("certificate_id >", value, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("certificate_id >=", value, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdLessThan(Integer value) {
      addCriterion("certificate_id <", value, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdLessThanOrEqualTo(Integer value) {
      addCriterion("certificate_id <=", value, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdIn(List<Integer> values) {
      addCriterion("certificate_id in", values, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdNotIn(List<Integer> values) {
      addCriterion("certificate_id not in", values, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdBetween(Integer value1, Integer value2) {
      addCriterion("certificate_id between", value1, value2, "certificateId");
      return (Criteria) this;
    }
    
    public Criteria andCertificateIdNotBetween(Integer value1, Integer value2) {
      addCriterion("certificate_id not between", value1, value2, "certificateId");
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
    
    public Criteria andCenterIdIsNull() {
      addCriterion("center_id is null");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdIsNotNull() {
      addCriterion("center_id is not null");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdEqualTo(Integer value) {
      addCriterion("center_id =", value, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdNotEqualTo(Integer value) {
      addCriterion("center_id <>", value, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdGreaterThan(Integer value) {
      addCriterion("center_id >", value, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdGreaterThanOrEqualTo(Integer value) {
      addCriterion("center_id >=", value, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdLessThan(Integer value) {
      addCriterion("center_id <", value, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdLessThanOrEqualTo(Integer value) {
      addCriterion("center_id <=", value, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdIn(List<Integer> values) {
      addCriterion("center_id in", values, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdNotIn(List<Integer> values) {
      addCriterion("center_id not in", values, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdBetween(Integer value1, Integer value2) {
      addCriterion("center_id between", value1, value2, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andCenterIdNotBetween(Integer value1, Integer value2) {
      addCriterion("center_id not between", value1, value2, "centerId");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeIsNull() {
      addCriterion("module_type is null");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeIsNotNull() {
      addCriterion("module_type is not null");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeEqualTo(Integer value) {
      addCriterion("module_type =", value, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeNotEqualTo(Integer value) {
      addCriterion("module_type <>", value, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeGreaterThan(Integer value) {
      addCriterion("module_type >", value, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeGreaterThanOrEqualTo(Integer value) {
      addCriterion("module_type >=", value, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeLessThan(Integer value) {
      addCriterion("module_type <", value, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeLessThanOrEqualTo(Integer value) {
      addCriterion("module_type <=", value, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeIn(List<Integer> values) {
      addCriterion("module_type in", values, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeNotIn(List<Integer> values) {
      addCriterion("module_type not in", values, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeBetween(Integer value1, Integer value2) {
      addCriterion("module_type between", value1, value2, "moduleType");
      return (Criteria) this;
    }
    
    public Criteria andModuleTypeNotBetween(Integer value1, Integer value2) {
      addCriterion("module_type not between", value1, value2, "moduleType");
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
    
    public Criteria andShPathIsNull() {
      addCriterion("sh_path is null");
      return (Criteria) this;
    }
    
    public Criteria andShPathIsNotNull() {
      addCriterion("sh_path is not null");
      return (Criteria) this;
    }
    
    public Criteria andShPathEqualTo(String value) {
      addCriterion("sh_path =", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathNotEqualTo(String value) {
      addCriterion("sh_path <>", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathGreaterThan(String value) {
      addCriterion("sh_path >", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathGreaterThanOrEqualTo(String value) {
      addCriterion("sh_path >=", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathLessThan(String value) {
      addCriterion("sh_path <", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathLessThanOrEqualTo(String value) {
      addCriterion("sh_path <=", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathLike(String value) {
      addCriterion("sh_path like", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathNotLike(String value) {
      addCriterion("sh_path not like", value, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathIn(List<String> values) {
      addCriterion("sh_path in", values, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathNotIn(List<String> values) {
      addCriterion("sh_path not in", values, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathBetween(String value1, String value2) {
      addCriterion("sh_path between", value1, value2, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andShPathNotBetween(String value1, String value2) {
      addCriterion("sh_path not between", value1, value2, "shPath");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlIsNull() {
      addCriterion("svn_auto_url is null");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlIsNotNull() {
      addCriterion("svn_auto_url is not null");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlEqualTo(String value) {
      addCriterion("svn_auto_url =", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlNotEqualTo(String value) {
      addCriterion("svn_auto_url <>", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlGreaterThan(String value) {
      addCriterion("svn_auto_url >", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlGreaterThanOrEqualTo(String value) {
      addCriterion("svn_auto_url >=", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlLessThan(String value) {
      addCriterion("svn_auto_url <", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlLessThanOrEqualTo(String value) {
      addCriterion("svn_auto_url <=", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlLike(String value) {
      addCriterion("svn_auto_url like", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlNotLike(String value) {
      addCriterion("svn_auto_url not like", value, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlIn(List<String> values) {
      addCriterion("svn_auto_url in", values, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlNotIn(List<String> values) {
      addCriterion("svn_auto_url not in", values, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlBetween(String value1, String value2) {
      addCriterion("svn_auto_url between", value1, value2, "svnAutoUrl");
      return (Criteria) this;
    }
    
    public Criteria andSvnAutoUrlNotBetween(String value1, String value2) {
      addCriterion("svn_auto_url not between", value1, value2, "svnAutoUrl");
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
    
    public Criteria andRemarkYwIsNull() {
      addCriterion("remark_yw is null");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwIsNotNull() {
      addCriterion("remark_yw is not null");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwEqualTo(String value) {
      addCriterion("remark_yw =", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwNotEqualTo(String value) {
      addCriterion("remark_yw <>", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwGreaterThan(String value) {
      addCriterion("remark_yw >", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwGreaterThanOrEqualTo(String value) {
      addCriterion("remark_yw >=", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwLessThan(String value) {
      addCriterion("remark_yw <", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwLessThanOrEqualTo(String value) {
      addCriterion("remark_yw <=", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwLike(String value) {
      addCriterion("remark_yw like", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwNotLike(String value) {
      addCriterion("remark_yw not like", value, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwIn(List<String> values) {
      addCriterion("remark_yw in", values, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwNotIn(List<String> values) {
      addCriterion("remark_yw not in", values, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwBetween(String value1, String value2) {
      addCriterion("remark_yw between", value1, value2, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkYwNotBetween(String value1, String value2) {
      addCriterion("remark_yw not between", value1, value2, "remarkYw");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakIsNull() {
      addCriterion("remark_bak is null");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakIsNotNull() {
      addCriterion("remark_bak is not null");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakEqualTo(String value) {
      addCriterion("remark_bak =", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakNotEqualTo(String value) {
      addCriterion("remark_bak <>", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakGreaterThan(String value) {
      addCriterion("remark_bak >", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakGreaterThanOrEqualTo(String value) {
      addCriterion("remark_bak >=", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakLessThan(String value) {
      addCriterion("remark_bak <", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakLessThanOrEqualTo(String value) {
      addCriterion("remark_bak <=", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakLike(String value) {
      addCriterion("remark_bak like", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakNotLike(String value) {
      addCriterion("remark_bak not like", value, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakIn(List<String> values) {
      addCriterion("remark_bak in", values, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakNotIn(List<String> values) {
      addCriterion("remark_bak not in", values, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakBetween(String value1, String value2) {
      addCriterion("remark_bak between", value1, value2, "remarkBak");
      return (Criteria) this;
    }
    
    public Criteria andRemarkBakNotBetween(String value1, String value2) {
      addCriterion("remark_bak not between", value1, value2, "remarkBak");
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