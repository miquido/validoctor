package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.ObjectRule;

public class AbstractFieldsRulesBuilder<T> {
  protected final ObjectRuleBuilder<T> objectRuleBuilder;

  protected AbstractFieldsRulesBuilder(ObjectRuleBuilder<T> objectRuleBuilder) {
    this.objectRuleBuilder = objectRuleBuilder;
  }

  public SingleFieldRulesBuilder<T> field(String field) {
    return new SingleFieldRulesBuilder<>(field, objectRuleBuilder);
  }

  public CollectionFieldRulesBuilder<T> collectionField(String field) {
    return new CollectionFieldRulesBuilder<>(field, objectRuleBuilder);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allTyped(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(objectRuleBuilder.objectClass, clazz, true, objectRuleBuilder);
  }

  public <P> TypeFieldsRulesBuilder<T, P> allAssignable(Class<P> clazz) {
    return new TypeFieldsRulesBuilder<>(objectRuleBuilder.objectClass, clazz, false, objectRuleBuilder);
  }

  public TwoFieldsRulesBuilder<T> fields(String field1, String field2) {
    return new TwoFieldsRulesBuilder<>(objectRuleBuilder);
  }

  public ObjectRule<T> build() {
    return objectRuleBuilder.build();
  }
}
