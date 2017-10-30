package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;

public class PropertyRuleAdapter<T> implements PropertyRule<T> {

  private final Rule<T> rule;

  public PropertyRuleAdapter(Rule<T> rule) {
    this.rule = rule;
  }

  @Override
  public String getProperty() {
    return null;
  }

  @Override
  public boolean test(T obj) {
    return rule.test(obj);
  }

  @Override
  public Ailment getAilment() {
    return rule.getAilment();
  }
}
