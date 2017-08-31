package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;

/**
 * Rule for determining validity. Calling {@link Rule#test(Object)} determines whether the object fulfills or violates
 * this rule, while {@link Rule#getAilment()} returns {@link Ailment} that should be stated in case of violation.
 * @param <T> type of object tested
 */
public interface Rule<T> {

  boolean test(T obj);

  Ailment getAilment();
}
