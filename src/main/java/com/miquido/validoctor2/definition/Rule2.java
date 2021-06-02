package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.result.Ailment2;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Rule ready to be passed to
 * {@link com.miquido.validoctor2.Validoctor2#examine(Object, Rule2[]) Validoctor's examine method}.
 * @param <T> type of patient
 */
public interface Rule2<T> {
  Set<Ailment2> apply(T patient);

  /**
   * Creates and returns a new instance of this Rule, that only tests its predicate if the specified condition is met.
   * @param condition condition required to test the rule
   */
  Rule2<T> withCondition(Predicate<T> condition);

  /**
   * Creates and returns a new instance of this Rule, that only tests its predicate if specified previousRule passed.
   * @param previousRule rule to test before testing predicate
   */
  Rule2<T> withDependency(Rule2<T> previousRule);

  /**
   * Can be supported by implementing classes by creating and returning a new instance of this Rule
   * that produces a specified violationMessage in case of failing predicate, instead of its default one.
   * @param violationMessage new message to use on violation
   */
  Rule2<T> withViolationMessage(String violationMessage);
}
