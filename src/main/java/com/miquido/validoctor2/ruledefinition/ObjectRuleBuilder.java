package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.ObjectRule;

public class ObjectRuleBuilder<T> {

  final ObjectRule<T> rule;
  final Class<T> objectClass;

  public ObjectRuleBuilder(Class<T> objectClass) {
    this.objectClass = objectClass;
    rule = new ObjectRule<>(objectClass);
  }

  public SingleFieldRulesBuilder<T> field(String field) {
    return new SingleFieldRulesBuilder<>(field, this);
  }

  public CollectionFieldRulesBuilder<T> fieldElements(String field) {
    return new CollectionFieldRulesBuilder<>(field, this);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allTyped(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(objectClass, clazz, true, this);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allAssignable(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(objectClass, clazz, false, this);
  }

  public TwoFieldsRulesBuilder<T> fields(String field1, String field2) {
    return new TwoFieldsRulesBuilder<>(this);
  }

  public ObjectRule<T> build() {
    return rule;
  }
}
