package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.SpecsKey;

import java.util.Map;

/**
 * Rule that does not return original patient value in {@link Ailment#getSpecs() specs} of Ailment stated upon violation.
 */
class ConfidentialSimpleRule<T> implements Rule<T> {

  private final Rule<T> delegate;

  ConfidentialSimpleRule(Rule<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Ailment apply(T obj) {
    Ailment ailment = delegate.apply(obj);
    if (ailment == null) {
      return null;
    } else {
      ailment.getSpecs().remove(SpecsKey.PATIENT_VALUE);
      return ailment;
    }
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
