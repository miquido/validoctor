package com.miquido.validoctor.complexrule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.rule.Rule;

import java.util.List;

/**
 * Type of Rule that validates multiple properties of the validated object. It gets a whole object as the input
 * and does not differ much from a {@link com.miquido.validoctor.rule.SimpleRule} in how validation is performed.
 * The difference is in Diagnosis stated in case of validation failure: instead of just stating the patient object
 * as ailed, only the properties that this ComplexRule deals with will be listed as ailed.
 * @param <PatientType> type of validated object
 */
public class ComplexRule<PatientType> implements Rule<PatientType> {

  private final List<String> properties;
  private final Rule<PatientType> rule;


  public ComplexRule(List<String> properties, Rule<PatientType> rule) {
    this.properties = properties;
    this.rule = rule;
  }

  @Override
  public boolean test(PatientType obj) {
    return rule.test(obj);
  }

  @Override
  public Ailment getAilment() {
    return rule.getAilment();
  }

  /**
   * @return list of names of properties that this rule validates.
   */
  public List<String> getProperties() {
    return properties;
  }
}
