package com.miquido.validoctor2.definition;

import com.miquido.validoctor2.result.Ailment2;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class SimpleRule2<T> implements Rule2<T> {

  private final String violationMessage;
  private final Predicate<T> predicate;
  private final Predicate<T> condition;
  private final Rule2<T> dependency;

  public SimpleRule2(String violationMessage, Predicate<T> predicate) {
    this(violationMessage, predicate, obj -> true, null);
  }

  public SimpleRule2(String violationMessage, Predicate<T> predicate, Predicate<T> condition, Rule2<T> dependency) {
    this.violationMessage = violationMessage;
    this.predicate = predicate;
    this.condition = condition;
    this.dependency = dependency;
  }

  @Override
  public Set<Ailment2> apply(T patient) {
    boolean conditionMet = condition.test(patient);
    Set<Ailment2> dependencyAilments = dependency == null || !conditionMet
        ? Collections.emptySet() : dependency.apply(patient);
    if (conditionMet && dependencyAilments.isEmpty() && !predicate.test(patient)) {
      //TODO 1. this is always a singleton set, can we avoid creating those sets somehow?
      return Collections.singleton(new Ailment2(null, violationMessage));
    } else {
      return dependencyAilments;
    }
  }

  @Override
  public Rule2<T> withCondition(Predicate<T> condition) {
    return new SimpleRule2<>(violationMessage, predicate, condition, dependency);
  }

  @Override
  public Rule2<T> withDependency(Rule2<T> previousRule) {
    return new SimpleRule2<>(violationMessage, predicate, condition, previousRule);
  }

  @Override
  public Rule2<T> withViolationMessage(String violationMessage) {
    return new SimpleRule2<>(violationMessage, predicate, condition, dependency);
  }
}
