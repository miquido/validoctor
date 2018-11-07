package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.rule.Rule;

import java.util.Map;

class RealShadowRule<PatientType> implements Rule<PatientType> {

  private final Rule<PatientType> delegate;
  private Boolean result;
  private Ailment ailmentResult;

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
  public Ailment apply(PatientType obj) {
    if (ailmentResult == null) {
      ailmentResult = delegate.apply(obj);
    }
    return ailmentResult;
  }

  @Override
  public Ailment peekAilment() {
    return delegate.peekAilment();
  }

  @Override
  public Map<String, Object> getParams() {
    return delegate.getParams();
  }
}
