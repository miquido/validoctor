package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;

import java.util.Map;

/**
 * Rule for determining validity. When building custom validations, implementing this interface should be considered
 * the last resort only for the most specific cases impossible to cover with {@link SimpleRule},
 * {@link com.miquido.validoctor.multirule.MultiRule} and {@link com.miquido.validoctor.reducerrule.ReducerRule}.
 * @param <T> type of object tested
 */
public interface Rule<T> {

  /**
   * DEPRECATED. Use {@link Rule#apply(Object)} instead.
   * @param obj object to validate.
   * @return whether the object fulfills or violates this rule
   */
  @Deprecated
  default boolean test(T obj) {
    return apply(obj) != null;
  }

  /**
   * @param obj object to validate.
   * @return null if object is valid, or Ailment if it violates this Rule. Depending on specific rule and Validoctor traits,
   * {@link Ailment#getSpecs() specs} of Ailment returned might differ from ones found in Ailment obtained from {@link Rule#getAilment()}.
   */
  Ailment apply(T obj);

  /**
   * DEPRECATED. Use {@link Rule#peekAilment()} instead.
   */
  @Deprecated
  default Ailment getAilment() {
    return peekAilment();
  }

  /**
   * Returns Ailment as it would be stated if rule was applied to patient violating it. Useful when consequences of
   * violation must be known before actually performing the validation. {@link Ailment#getSpecs() Specs} of returned
   * Ailment will obviously not contain any specs entries that would be added during validation
   * (like {@link com.miquido.validoctor.ailment.SpecsKey#PATIENT_VALUE}).
   * @return Ailment that should be stated in case of violation
   */
  Ailment peekAilment();

  /**
   * @return parameters describing this rule
   */
  Map<String, Object> getParams();

}
