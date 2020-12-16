package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.execution.RuleExecutionBranch;
import com.miquido.validoctor2.rule.Rule2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

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

  public <P> CollectionFieldRulesBuilder<T, P> collectionField(String field) {
    return new CollectionFieldRulesBuilder<>(field, this);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allTyped(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, true, this);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allAssignable(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(clazz, false, this);
  }

  public TypeFieldsRulesBuilder<T, Object> all() {
    return new TypeFieldsRulesBuilder<>(Object.class, false, this);
  }

  public <P> ReducedFieldsRulesBuilder<T, P> fields(String field1, String field2, BinaryOperator<P> reducer) {
    return new ReducedFieldsRulesBuilder<>(this, Arrays.asList(field1, field2), reducer);
  }

  public Rule2<T> build() {
    return new ExaminationDefinition<>(rootBranches);
  }
}
