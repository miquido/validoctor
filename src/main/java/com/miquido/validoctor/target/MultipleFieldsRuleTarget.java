package com.miquido.validoctor.target;

import java.util.List;
import java.util.stream.Collectors;

public class MultipleFieldsRuleTarget<T, P> implements RuleTarget<T, P> {

  private final List<String> fieldNames;
  private final Class<T> enclosingClass;

  public MultipleFieldsRuleTarget(List<String> fieldNames, Class<T> enclosingClass) {
    this.fieldNames = fieldNames;
    this.enclosingClass = enclosingClass;
  }

  @Override
  public List<P> getPatients(T object) {
    return fieldNames.stream()
        .map(field -> readField(enclosingClass, field, object))
        .collect(Collectors.toList());
  }

  @Override
  public List<String> getFieldNames() {
    return fieldNames;
  }
}
