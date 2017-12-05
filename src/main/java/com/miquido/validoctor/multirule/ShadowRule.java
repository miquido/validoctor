package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;

class ShadowRule<PatientType> implements PropertyRule<PatientType> {

  private final String property;
  private final RealShadowRule<PatientType> delegate;

  ShadowRule(String property, RealShadowRule<PatientType> delegate) {
    this.property = property;
    this.delegate = delegate;
  }

  @Override
  public String getProperty() {
    return property;
  }

  @Override
  public boolean test(PatientType obj) {
    return delegate.test(obj);
  }

  @Override
  public Ailment getAilment() {
    return delegate.getAilment();
  }
}
