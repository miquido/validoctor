package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.AilmentFactory;
import com.miquido.validoctor.ailment.CachingAilmentFactory;
import com.miquido.validoctor.ailment.Severity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Basic rule that checks one predicate for one value.
 * @param <T> type of value to check
 */
public class SimpleRule<T> implements Rule<T> {

  private final Predicate<T> predicate;
  private final Map<String, Object> params;
  private final AilmentFactory ailmentFactory;

  /**
   * @param ruleName name of rule to be used in {@link Ailment} caused by violation
   * @param params map of parameters describing the rule, with no influence on actual validation. Will be copied into new immutable map.
   * @param predicate predicate determining violation
   * @param violationSeverity {@link Severity} of {@link Ailment} caused by violation
   */
  public SimpleRule(String ruleName, Map<String, Object> params, Predicate<T> predicate, Severity violationSeverity) {
    this.predicate = predicate;
    this.params = Collections.unmodifiableMap(new HashMap<>(params));
    ailmentFactory = new CachingAilmentFactory(specs -> new Ailment(ruleName, violationSeverity, specs));
  }

  /**
   * Constructs a new rule with empty params.<br/>
   * @see SimpleRule#SimpleRule(String, Map, Predicate, Severity)
   */
  public SimpleRule(String ruleName, Predicate<T> predicate, Severity violationSeverity) {
    this(ruleName, Collections.emptyMap(), predicate, violationSeverity);
  }

  /**
   * Constructs a new rule with violationSeverity set to {@link Severity#ERROR ERROR}.<br/>
   * @see SimpleRule#SimpleRule(String, Map, Predicate, Severity)
   */
  public SimpleRule(String ruleName, Map<String, Object> params, Predicate<T> predicate) {
    this(ruleName, params, predicate, Severity.ERROR);
  }

  /**
   * Constructs a new rule with violationSeverity set to {@link Severity#ERROR ERROR} and empty params.<br/>
   * @see SimpleRule#SimpleRule(String, Map, Predicate, Severity)
   */
  public SimpleRule(String ruleName, Predicate<T> predicate) {
    this(ruleName, predicate, Severity.ERROR);
  }


  @Override
  public boolean test(T obj) {
    return predicate.test(obj);
  }

  @Override
  public Ailment apply(T obj) {
    boolean valid = predicate.test(obj);
    return valid ? null : peekAilment();
  }

  @Override
  public Ailment peekAilment() {
    return ailmentFactory.state(params);
  }

  @Override
  public Map<String, Object> getParams() {
    return params;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SimpleRule)) return false;
    SimpleRule<?> that = (SimpleRule<?>) o;
    return Objects.equals(predicate, that.predicate) &&
        Objects.equals(params, that.params) &&
        Objects.equals(ailmentFactory, that.ailmentFactory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(predicate, params, ailmentFactory);
  }
}
