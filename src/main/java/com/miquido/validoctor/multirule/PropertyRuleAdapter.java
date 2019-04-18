package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.rule.Rule;

import java.util.Map;

class PropertyRuleAdapter<T> implements PropertyRule<T> {

  private final String objectName;
  private final Rule<T> rule;

  PropertyRuleAdapter(String objectName, Rule<T> rule) {
    this.objectName = objectName;
    this.rule = rule;
  }

  @Override
  public String getProperty() {
    return objectName;
  }

  @Override
  public boolean test(T obj) {
    return rule.test(obj);
  }

  @Override
  public Ailment apply(T obj) {
    return rule.apply(obj);
  }

  @Override
  public Ailment peekAilment() {
    return rule.peekAilment();
  }

  @Override
  public Map<String, Object> getParams() {
    return rule.getParams();
  }
}
