package com.miquido.validoctor.target;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeRuleTarget<T, P> implements RuleTarget<T, P> {

  private final List<Field> fields;

  public TypeRuleTarget(Class<T> enclosingClass, Class<? extends P> fieldsClass, boolean strictMatch) {
    this.fields = findFields(enclosingClass, fieldsClass, strictMatch);
  }

  @Override
  public List<P> getPatients(T object) {
    return fields.stream().map(field -> {
      try {
        field.setAccessible(true); //TODO should we revert accessible setting after reading?
        return (P) field.get(object);
      } catch (IllegalAccessException | ClassCastException e) {
        throw new RuntimeException("Can't read field " + field.getName(), e);
      }
    }).collect(Collectors.toList());
  }

  @Override
  public List<String> getFieldNames() {
    return fields.stream()
        .map(Field::getName)
        .collect(Collectors.toList());
  }

  private List<Field> findFields(Class<T> enclosingClass, Class<? extends P> fieldsClass, boolean strictMatch) {
    return Arrays.stream(enclosingClass.getDeclaredFields())
        .filter(field -> strictMatch
            ? field.getType().equals(fieldsClass)
            : fieldsClass.isAssignableFrom(field.getType())
        )
        .collect(Collectors.toList());
  }
}
