package com.xc.fast_deploy.model.master_model.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModuleJobExample {
  protected String orderByClause;
  
  protected boolean distinct;
  
  protected List<Criteria> oredCriteria;
  
  public ModuleJobExample() {
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
    
    public Criteria andJobNameIsNull() {
      addCriterion("job_name is null");
      return (Criteria) this;
    }
    
    public Criteria andJobNameIsNotNull() {
      addCriterion("job_name is not null");
      return (Criteria) this;
    }
    
    public Criteria andJobNameEqualTo(String value) {
      addCriterion("job_name =", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameNotEqualTo(String value) {
      addCriterion("job_name <>", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameGreaterThan(String value) {
      addCriterion("job_name >", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameGreaterThanOrEqualTo(String value) {
      addCriterion("job_name >=", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameLessThan(String value) {
      addCriterion("job_name <", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameLessThanOrEqualTo(String value) {
      addCriterion("job_name <=", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameLike(String value) {
      addCriterion("job_name like", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameNotLike(String value) {
      addCriterion("job_name not like", value, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameIn(List<String> values) {
      addCriterion("job_name in", values, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameNotIn(List<String> values) {
      addCriterion("job_name not in", values, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameBetween(String value1, String value2) {
      addCriterion("job_name between", value1, value2, "jobName");
      return (Criteria) this;
    }
    
    public Criteria andJobNameNotBetween(String value1, String value2) {
      addCriterion("job_name not between", value1, value2, "jobName");
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
    
    public Criteria andCrontabExpressionIsNull() {
      addCriterion("crontab_expression is null");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionIsNotNull() {
      addCriterion("crontab_expression is not null");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionEqualTo(String value) {
      addCriterion("crontab_expression =", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionNotEqualTo(String value) {
      addCriterion("crontab_expression <>", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionGreaterThan(String value) {
      addCriterion("crontab_expression >", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionGreaterThanOrEqualTo(String value) {
      addCriterion("crontab_expression >=", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionLessThan(String value) {
      addCriterion("crontab_expression <", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionLessThanOrEqualTo(String value) {
      addCriterion("crontab_expression <=", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionLike(String value) {
      addCriterion("crontab_expression like", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionNotLike(String value) {
      addCriterion("crontab_expression not like", value, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionIn(List<String> values) {
      addCriterion("crontab_expression in", values, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionNotIn(List<String> values) {
      addCriterion("crontab_expression not in", values, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionBetween(String value1, String value2) {
      addCriterion("crontab_expression between", value1, value2, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andCrontabExpressionNotBetween(String value1, String value2) {
      addCriterion("crontab_expression not between", value1, value2, "crontabExpression");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixIsNull() {
      addCriterion("mirror_prefix is null");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixIsNotNull() {
      addCriterion("mirror_prefix is not null");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixEqualTo(String value) {
      addCriterion("mirror_prefix =", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixNotEqualTo(String value) {
      addCriterion("mirror_prefix <>", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixGreaterThan(String value) {
      addCriterion("mirror_prefix >", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixGreaterThanOrEqualTo(String value) {
      addCriterion("mirror_prefix >=", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixLessThan(String value) {
      addCriterion("mirror_prefix <", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixLessThanOrEqualTo(String value) {
      addCriterion("mirror_prefix <=", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixLike(String value) {
      addCriterion("mirror_prefix like", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixNotLike(String value) {
      addCriterion("mirror_prefix not like", value, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixIn(List<String> values) {
      addCriterion("mirror_prefix in", values, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixNotIn(List<String> values) {
      addCriterion("mirror_prefix not in", values, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixBetween(String value1, String value2) {
      addCriterion("mirror_prefix between", value1, value2, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andMirrorPrefixNotBetween(String value1, String value2) {
      addCriterion("mirror_prefix not between", value1, value2, "mirrorPrefix");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathIsNull() {
      addCriterion("compile_file_path is null");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathIsNotNull() {
      addCriterion("compile_file_path is not null");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathEqualTo(String value) {
      addCriterion("compile_file_path =", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathNotEqualTo(String value) {
      addCriterion("compile_file_path <>", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathGreaterThan(String value) {
      addCriterion("compile_file_path >", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathGreaterThanOrEqualTo(String value) {
      addCriterion("compile_file_path >=", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathLessThan(String value) {
      addCriterion("compile_file_path <", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathLessThanOrEqualTo(String value) {
      addCriterion("compile_file_path <=", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathLike(String value) {
      addCriterion("compile_file_path like", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathNotLike(String value) {
      addCriterion("compile_file_path not like", value, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathIn(List<String> values) {
      addCriterion("compile_file_path in", values, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathNotIn(List<String> values) {
      addCriterion("compile_file_path not in", values, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathBetween(String value1, String value2) {
      addCriterion("compile_file_path between", value1, value2, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileFilePathNotBetween(String value1, String value2) {
      addCriterion("compile_file_path not between", value1, value2, "compileFilePath");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandIsNull() {
      addCriterion("compile_command is null");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandIsNotNull() {
      addCriterion("compile_command is not null");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandEqualTo(String value) {
      addCriterion("compile_command =", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandNotEqualTo(String value) {
      addCriterion("compile_command <>", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandGreaterThan(String value) {
      addCriterion("compile_command >", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandGreaterThanOrEqualTo(String value) {
      addCriterion("compile_command >=", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandLessThan(String value) {
      addCriterion("compile_command <", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandLessThanOrEqualTo(String value) {
      addCriterion("compile_command <=", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandLike(String value) {
      addCriterion("compile_command like", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandNotLike(String value) {
      addCriterion("compile_command not like", value, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandIn(List<String> values) {
      addCriterion("compile_command in", values, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandNotIn(List<String> values) {
      addCriterion("compile_command not in", values, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandBetween(String value1, String value2) {
      addCriterion("compile_command between", value1, value2, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andCompileCommandNotBetween(String value1, String value2) {
      addCriterion("compile_command not between", value1, value2, "compileCommand");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathIsNull() {
      addCriterion("dockerfile_path is null");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathIsNotNull() {
      addCriterion("dockerfile_path is not null");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathEqualTo(String value) {
      addCriterion("dockerfile_path =", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathNotEqualTo(String value) {
      addCriterion("dockerfile_path <>", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathGreaterThan(String value) {
      addCriterion("dockerfile_path >", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathGreaterThanOrEqualTo(String value) {
      addCriterion("dockerfile_path >=", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathLessThan(String value) {
      addCriterion("dockerfile_path <", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathLessThanOrEqualTo(String value) {
      addCriterion("dockerfile_path <=", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathLike(String value) {
      addCriterion("dockerfile_path like", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathNotLike(String value) {
      addCriterion("dockerfile_path not like", value, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathIn(List<String> values) {
      addCriterion("dockerfile_path in", values, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathNotIn(List<String> values) {
      addCriterion("dockerfile_path not in", values, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathBetween(String value1, String value2) {
      addCriterion("dockerfile_path between", value1, value2, "dockerfilePath");
      return (Criteria) this;
    }
    
    public Criteria andDockerfilePathNotBetween(String value1, String value2) {
      addCriterion("dockerfile_path not between", value1, value2, "dockerfilePath");
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
    
    public Criteria andStatusIsNull() {
      addCriterion("status is null");
      return (Criteria) this;
    }
    
    public Criteria andStatusIsNotNull() {
      addCriterion("status is not null");
      return (Criteria) this;
    }
    
    public Criteria andStatusEqualTo(Integer value) {
      addCriterion("status =", value, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusNotEqualTo(Integer value) {
      addCriterion("status <>", value, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusGreaterThan(Integer value) {
      addCriterion("status >", value, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
      addCriterion("status >=", value, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusLessThan(Integer value) {
      addCriterion("status <", value, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusLessThanOrEqualTo(Integer value) {
      addCriterion("status <=", value, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusIn(List<Integer> values) {
      addCriterion("status in", values, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusNotIn(List<Integer> values) {
      addCriterion("status not in", values, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusBetween(Integer value1, Integer value2) {
      addCriterion("status between", value1, value2, "status");
      return (Criteria) this;
    }
    
    public Criteria andStatusNotBetween(Integer value1, Integer value2) {
      addCriterion("status not between", value1, value2, "status");
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