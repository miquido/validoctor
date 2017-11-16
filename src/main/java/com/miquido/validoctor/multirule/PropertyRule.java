package com.miquido.validoctor.multirule;

import com.miquido.validoctor.rule.Rule;

/**
 * Type of Rule that may be associated with a single property of the validated object. Null association means that Rule
 * is associated with object as a whole, not a property.
 * @param <PatientType>
 */
public interface PropertyRule<PatientType> extends Rule<PatientType> {

  /**
   * @return Name of property this Rule is associated with, or null if there is not property association and Rule is used
   * for object as a whole.
   */
  String getProperty();

}
