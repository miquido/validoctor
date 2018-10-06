package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.multirule.PropertyRule;

import java.util.Map;

/**
 * For internal use. See {@link com.miquido.validoctor.Validoctor.Builder#meticulous(boolean)}.
 * @param <PatientType>
 */
public class MeticulousRuleDecorator<PatientType> implements PropertyRule<PatientType> {


  public static final String PATIENT_VALUE = "patient_value";
  public static final String EXAMINATION_DURATION = "examination_duration_ms";


  private final PropertyRule<PatientType> delegate;

  public MeticulousRuleDecorator(PropertyRule<PatientType> delegate) {
    this.delegate = delegate;
  }


  @Override
  public Ailment apply(PatientType obj) {
    long time = System.currentTimeMillis();
    Ailment ailment = delegate.apply(obj);
    long duration = System.currentTimeMillis() - time;
    if (ailment != null) {
      Map<String, Object> specs = ailment.getSpecs();
      specs.put(PATIENT_VALUE, obj);
      specs.put(EXAMINATION_DURATION, duration);
    }
    return ailment;
  }

  @Override
  public Ailment peekAilment() {
    return delegate.peekAilment();
  }

  @Override
  public Map<String, Object> getParams() {
    return delegate.getParams();
  }

  @Override
  public String getProperty() {
    return delegate.getProperty();
  }
}
