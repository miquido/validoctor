package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.rule.Rule;

class RealShadowRule<PatientType> implements Rule<PatientType> {

  private final Rule<PatientType> delegate;
  private Boolean result;

  RealShadowRule(Rule<PatientType> delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean test(PatientType obj) {
    if (result == null) {
      result = delegate.test(obj);
    }
    return result;
  }

  @Override
  public Ailment getAilment() {
    return delegate.getAilment();
  }
}
