package com.miquido.validoctor2.target;

import java.lang.reflect.Field;
import java.util.List;

public interface RuleTarget<T, P> {
  List<P> getPatients(T object);
  List<String> getFieldNames();

  default P readField(Class<T> enclosingClass, String fieldName, T object) {
    try {
      Field field = enclosingClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      return object == null ? null : (P) field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Could not read field", e);
    }
  }
}
