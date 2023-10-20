package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModulePackageExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModulePackageExample() {
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
    
    public Criteria andCodeReversionIsNull() {
      addCriterion("code_reversion is null");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionIsNotNull() {
      addCriterion("code_reversion is not null");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionEqualTo(Integer value) {
      addCriterion("code_reversion =", value, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionNotEqualTo(Integer value) {
      addCriterion("code_reversion <>", value, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionGreaterThan(Integer value) {
      addCriterion("code_reversion >", value, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionGreaterThanOrEqualTo(Integer value) {
      addCriterion("code_reversion >=", value, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionLessThan(Integer value) {
      addCriterion("code_reversion <", value, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionLessThanOrEqualTo(Integer value) {
      addCriterion("code_reversion <=", value, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionIn(List<Integer> values) {
      addCriterion("code_reversion in", values, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionNotIn(List<Integer> values) {
      addCriterion("code_reversion not in", values, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionBetween(Integer value1, Integer value2) {
      addCriterion("code_reversion between", value1, value2, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andCodeReversionNotBetween(Integer value1, Integer value2) {
      addCriterion("code_reversion not between", value1, value2, "codeReversion");
      return (Criteria) this;
    }
    
    public Criteria andContentNameIsNull() {
      addCriterion("content_name is null");
      return (Criteria) this;
    }
    
    public Criteria andContentNameIsNotNull() {
      addCriterion("content_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andContentNameEqualTo(String value) {
      addCriterion("content_name =", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameNotEqualTo(String value) {
      addCriterion("content_name <>", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameGreaterThan(String value) {
      addCriterion("content_name >", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameGreaterThanOrEqualTo(String value) {
      addCriterion("content_name >=", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameLessThan(String value) {
      addCriterion("content_name <", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameLessThanOrEqualTo(String value) {
      addCriterion("content_name <=", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameLike(String value) {
      addCriterion("content_name like", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameNotLike(String value) {
      addCriterion("content_name not like", value, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameIn(List<String> values) {
      addCriterion("content_name in", values, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameNotIn(List<String> values) {
      addCriterion("content_name not in", values, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameBetween(String value1, String value2) {
      addCriterion("content_name between", value1, value2, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andContentNameNotBetween(String value1, String value2) {
      addCriterion("content_name not between", value1, value2, "contentName");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlIsNull() {
      addCriterion("code_url is null");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlIsNotNull() {
      addCriterion("code_url is not null");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlEqualTo(String value) {
      addCriterion("code_url =", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlNotEqualTo(String value) {
      addCriterion("code_url <>", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlGreaterThan(String value) {
      addCriterion("code_url >", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlGreaterThanOrEqualTo(String value) {
      addCriterion("code_url >=", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlLessThan(String value) {
      addCriterion("code_url <", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlLessThanOrEqualTo(String value) {
      addCriterion("code_url <=", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlLike(String value) {
      addCriterion("code_url like", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlNotLike(String value) {
      addCriterion("code_url not like", value, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlIn(List<String> values) {
      addCriterion("code_url in", values, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlNotIn(List<String> values) {
      addCriterion("code_url not in", values, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlBetween(String value1, String value2) {
      addCriterion("code_url between", value1, value2, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeUrlNotBetween(String value1, String value2) {
      addCriterion("code_url not between", value1, value2, "codeUrl");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeIsNull() {
      addCriterion("code_type is null");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeIsNotNull() {
      addCriterion("code_type is not null");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeEqualTo(Integer value) {
      addCriterion("code_type =", value, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeNotEqualTo(Integer value) {
      addCriterion("code_type <>", value, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeGreaterThan(Integer value) {
      addCriterion("code_type >", value, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeGreaterThanOrEqualTo(Integer value) {
      addCriterion("code_type >=", value, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeLessThan(Integer value) {
      addCriterion("code_type <", value, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeLessThanOrEqualTo(Integer value) {
      addCriterion("code_type <=", value, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeIn(List<Integer> values) {
      addCriterion("code_type in", values, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeNotIn(List<Integer> values) {
      addCriterion("code_type not in", values, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeBetween(Integer value1, Integer value2) {
      addCriterion("code_type between", value1, value2, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andCodeTypeNotBetween(Integer value1, Integer value2) {
      addCriterion("code_type not between", value1, value2, "codeType");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameIsNull() {
      addCriterion("package_path_name is null");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameIsNotNull() {
      addCriterion("package_path_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameEqualTo(String value) {
      addCriterion("package_path_name =", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameNotEqualTo(String value) {
      addCriterion("package_path_name <>", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameGreaterThan(String value) {
      addCriterion("package_path_name >", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameGreaterThanOrEqualTo(String value) {
      addCriterion("package_path_name >=", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameLessThan(String value) {
      addCriterion("package_path_name <", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameLessThanOrEqualTo(String value) {
      addCriterion("package_path_name <=", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameLike(String value) {
      addCriterion("package_path_name like", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameNotLike(String value) {
      addCriterion("package_path_name not like", value, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameIn(List<String> values) {
      addCriterion("package_path_name in", values, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameNotIn(List<String> values) {
      addCriterion("package_path_name not in", values, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameBetween(String value1, String value2) {
      addCriterion("package_path_name between", value1, value2, "packagePathName");
      return (Criteria) this;
    }
    
    public Criteria andPackagePathNameNotBetween(String value1, String value2) {
      addCriterion("package_path_name not between", value1, value2, "packagePathName");
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
    
    public Criteria andGitBranchIsNull() {
      addCriterion("git_branch is null");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchIsNotNull() {
      addCriterion("git_branch is not null");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchEqualTo(String value) {
      addCriterion("git_branch =", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchNotEqualTo(String value) {
      addCriterion("git_branch <>", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchGreaterThan(String value) {
      addCriterion("git_branch >", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchGreaterThanOrEqualTo(String value) {
      addCriterion("git_branch >=", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchLessThan(String value) {
      addCriterion("git_branch <", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchLessThanOrEqualTo(String value) {
      addCriterion("git_branch <=", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchLike(String value) {
      addCriterion("git_branch like", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchNotLike(String value) {
      addCriterion("git_branch not like", value, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchIn(List<String> values) {
      addCriterion("git_branch in", values, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchNotIn(List<String> values) {
      addCriterion("git_branch not in", values, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchBetween(String value1, String value2) {
      addCriterion("git_branch between", value1, value2, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andGitBranchNotBetween(String value1, String value2) {
      addCriterion("git_branch not between", value1, value2, "gitBranch");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderIsNull() {
      addCriterion("is_pom_folder is null");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderIsNotNull() {
      addCriterion("is_pom_folder is not null");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderEqualTo(Integer value) {
      addCriterion("is_pom_folder =", value, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderNotEqualTo(Integer value) {
      addCriterion("is_pom_folder <>", value, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderGreaterThan(Integer value) {
      addCriterion("is_pom_folder >", value, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderGreaterThanOrEqualTo(Integer value) {
      addCriterion("is_pom_folder >=", value, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderLessThan(Integer value) {
      addCriterion("is_pom_folder <", value, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderLessThanOrEqualTo(Integer value) {
      addCriterion("is_pom_folder <=", value, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderIn(List<Integer> values) {
      addCriterion("is_pom_folder in", values, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderNotIn(List<Integer> values) {
      addCriterion("is_pom_folder not in", values, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderBetween(Integer value1, Integer value2) {
      addCriterion("is_pom_folder between", value1, value2, "isPomFolder");
      return (Criteria) this;
    }
    
    public Criteria andIsPomFolderNotBetween(Integer value1, Integer value2) {
      addCriterion("is_pom_folder not between", value1, value2, "isPomFolder");
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