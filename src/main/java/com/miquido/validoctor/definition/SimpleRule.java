package com.miquido.validoctor.definition;

import com.miquido.validoctor.result.Ailment;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class SimpleRule<T> implements Rule<T> {

  private final String violationMessage;
  private final Predicate<T> predicate;
  private final Predicate<T> condition;
  private final Rule<T> dependency;

  public SimpleRule(String violationMessage, Predicate<T> predicate) {
    this(violationMessage, predicate, obj -> true, null);
  }

  public SimpleRule(String violationMessage, Predicate<T> predicate, Predicate<T> condition, Rule<T> dependency) {
    this.violationMessage = violationMessage;
    this.predicate = predicate;
    this.condition = condition;
    this.dependency = dependency;
  }

  @Override
  public Set<Ailment> apply(T patient) {
    boolean conditionMet = condition.test(patient);
    Set<Ailment> dependencyAilments = dependency == null || !conditionMet
        ? Collections.emptySet() : dependency.apply(patient);
    if (conditionMet && dependencyAilments.isEmpty() && !predicate.test(patient)) {
      //TODO 1. this is always a singleton set, can we avoid creating those sets somehow?
      return Collections.singleton(new Ailment(null, violationMessage));
    } else {
      return dependencyAilments;
    }
  }

  @Override
  public Rule<T> withCondition(Predicate<T> condition) {
    return new SimpleRule<>(violationMessage, predicate, condition, dependency);
  }

  @Override
  public Rule<T> withDependency(Rule<T> previousRule) {
    return new SimpleRule<>(violationMessage, predicate, condition, previousRule);
  }

  @Override
  public Rule<T> withViolationMessage(String violationMessage) {
    return new SimpleRule<>(violationMessage, predicate, condition, dependency);
  }
}
