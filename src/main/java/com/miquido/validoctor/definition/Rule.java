package com.miquido.validoctor.definition;

import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.result.Ailment;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Rule ready to be passed to
 * {@link Validoctor#examine(Object, Rule[]) Validoctor's examine method}.
 * @param <T> type of patient
 */
public interface Rule<T> {
  Set<Ailment> apply(T patient);

  /**
   * Creates and returns a new instance of this Rule, that only tests its predicate if the specified condition is met.
   * @param condition condition required to test the rule
   */
  Rule<T> withCondition(Predicate<T> condition);

  /**
   * Creates and returns a new instance of this Rule, that only tests its predicate if specified previousRule passed.
   * @param previousRule rule to test before testing predicate
   */
  Rule<T> withDependency(Rule<T> previousRule);

  /**
   * Can be supported by implementing classes by creating and returning a new instance of this Rule
   * that produces a specified violationMessage in case of failing predicate, instead of its default one;
   * or by replacing any existing violationMessages by specified one.
   * @param violationMessage new message to use on violation
   */
  Rule<T> withViolationMessage(String violationMessage);
}
