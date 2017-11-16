package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;

/**
 * Duplicate rule created for each property that is validated by a ComplexRule. Once one of the shadows is tested,
 * all other shadows of the same rule for other properties will be marked as tested. They will then return the same
 * result without running the test again.
 */
class ShadowRule<PatientType> implements PropertyRule<PatientType> {

  private final String property;
  private final TrueShadowRule<PatientType> delegate;

  ShadowRule(String property, TrueShadowRule<PatientType> delegate) {
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
