package com.miquido.validoctor2.target;

import java.util.Collections;
import java.util.List;

public class EnclosingObjectTarget<T> implements RuleTarget<T, T> {

  private final String fieldName;

  public EnclosingObjectTarget(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public List<T> getPatients(T object) {
    return Collections.singletonList(object);
  }

  @Override
  public List<String> getFieldNames() {
    return Collections.singletonList(fieldName);
  }
}
