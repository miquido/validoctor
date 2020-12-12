package com.miquido.validoctor.target;

import java.util.Collections;
import java.util.List;

public class FieldRuleTarget<T, P> implements RuleTarget<T, P> {

  private final String fieldName;
  private final Class<T> enclosingClass;

  public FieldRuleTarget(String fieldName, Class<T> enclosingClass) {
    this.fieldName = fieldName;
    this.enclosingClass = enclosingClass;
  }

  @Override
  public List<P> getPatients(T object) {
    return Collections.singletonList(readField(enclosingClass, fieldName, object));
  }

  @Override
  public List<String> getFieldNames() {
    return Collections.singletonList(fieldName);
  }
}
