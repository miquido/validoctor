package com.miquido.validoctor2.ruletarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionFieldRuleTarget<T, P> extends FieldRuleTarget<T, P> {

  public CollectionFieldRuleTarget(String fieldName, Class<T> enclosingClass) {
    super(fieldName, enclosingClass);
  }

  @Override
  public List<P> getPatients(T object) {
    Object fieldValue = readField(object);
    return new ArrayList<>((Collection<P>) fieldValue); //TODO handle class cast exc
  }
}
