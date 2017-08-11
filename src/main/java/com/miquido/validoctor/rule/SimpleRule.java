package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;

import java.util.Collections;
import java.util.function.Predicate;

/**
 * Basic rule that checks one predicate for one value.
 * @param <T> type of value to check
 */
public class SimpleRule<T> implements Rule<T> {

  private final Predicate<T> predicate;
  private final Ailment ailment;

  /**
   * @param ruleName name of rule to be used in {@link Ailment} caused by violation
   * @param predicate predicate determining violation
   * @param violationSeverity {@link Severity} of {@link Ailment} caused by violation
   */
  public SimpleRule(String ruleName, Predicate<T> predicate, Severity violationSeverity) {
    this.predicate = predicate;
    ailment = new Ailment(ruleName, Collections.emptyMap(), violationSeverity);
  }

  /**
   * Constructs a new rule with violationSeverity set to {@link Severity#ERROR ERROR}.<br/>
   * @see SimpleRule#SimpleRule(String, Predicate, Severity)
   */
  public SimpleRule(String ruleName, Predicate<T> predicate) {
    this(ruleName, predicate, Severity.ERROR);
  }

  @Override
  public boolean test(T obj) {
    return predicate.test(obj);
  }

  @Override
  public Ailment getAilment() {
    return ailment;
  }
}
