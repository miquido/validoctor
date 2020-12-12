package com.miquido.validoctor.target;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionFieldRuleTarget<T, P> implements RuleTarget<T, P> {

  private final String fieldName;
  private final Class<T> enclosingClass;

  public CollectionFieldRuleTarget(String fieldName, Class<T> enclosingClass) {
    this.fieldName = fieldName;
    this.enclosingClass = enclosingClass;
  }

  @Override
  public List<P> getPatients(T object) {
    Object fieldValue = readField(enclosingClass, fieldName, object);
    return fieldValue == null ? Collections.emptyList()
        : new ArrayList<>((Collection<P>) fieldValue); //TODO handle class cast exc
  }

  @Override
  public List<String> getFieldNames() {
    return Collections.singletonList(fieldName);
  }
}
