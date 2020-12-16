package com.miquido.validoctor2.target;

import java.lang.reflect.Field;
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
    return Collections.singletonList(readField(object));
  }

  @Override
  public List<String> getFieldNames() {
    return Collections.singletonList(fieldName);
  }

  protected P readField(T object) {
    try {
      Field field = enclosingClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      return (P) field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Could not read field", e);
    }
  }
}
