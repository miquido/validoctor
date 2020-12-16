package com.miquido.validoctor2.target;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;

public class ReducedFieldsRuleTarget<T, P> implements RuleTarget<T, P> {

  private final List<String> fieldNames;
  private final Class<T> enclosingClass;
  private final BinaryOperator<P> reducer;

  public ReducedFieldsRuleTarget(List<String> fieldNames, Class<T> enclosingClass,
                                 BinaryOperator<P> reducer) {
    this.fieldNames = fieldNames;
    this.enclosingClass = enclosingClass;
    this.reducer = reducer;
  }

  @Override
  public List<P> getPatients(T object) {
    return fieldNames.stream()
        .map(field -> readField(object, field))
        .reduce(reducer)
        .map(Collections::singletonList)
        .orElse(Collections.emptyList());
  }

  @Override
  public List<String> getFieldNames() {
    return fieldNames;
  }


  private P readField(T object, String fieldName) {
    try {
      Field field = enclosingClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      return (P) field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Could not read field", e);
    }
  }
}
