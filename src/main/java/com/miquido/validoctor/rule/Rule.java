package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;

/**
 * Rule for determining validity.
 * @param <T> type of object tested
 */
public interface Rule<T> {

  /**
   * @param obj object to validate.
   * @return whether the object fulfills or violates this rule
   */
  boolean test(T obj);

  /**
   * @return Ailment that should be stated in case of violation
   */
  Ailment getAilment();

}
