package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.ruleexecution.RuleExecutionBranch;

import java.util.ArrayList;
import java.util.List;

public class RuleBuilder<T> {

  final List<RuleExecutionBranch<T>> rootBranches;
  final Class<T> objectClass;

  public RuleBuilder(Class<T> objectClass) {
    this.objectClass = objectClass;
    rootBranches = new ArrayList<>();
  }

  public <P> SingleFieldRulesBuilder<T, P> field(String field) {
    return new SingleFieldRulesBuilder<>(field, this);
  }

  public <P> CollectionFieldRulesBuilder<T, P> fieldElements(String field) {
    return new CollectionFieldRulesBuilder<>(field, this);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allTyped(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, true, this);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allAssignable(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, false, this);
  }

  public <P> TwoFieldsRulesBuilder<T, P> fields(String field1, String field2) {
    return new TwoFieldsRulesBuilder<>(this);
  }

  public ExaminationDefinition<T> build() {
    return new ExaminationDefinition<>(rootBranches);
  }
}
