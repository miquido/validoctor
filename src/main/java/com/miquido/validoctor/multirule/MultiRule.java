package com.miquido.validoctor.multirule;

import com.miquido.validoctor.rule.PropertyRule;
import com.miquido.validoctor.rule.PropertyRuleAdapter;
import com.miquido.validoctor.rule.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List of {@link PropertyRule}s for validation of a single class of objects.
 * @param <T> type of object validated with these rules
 */
public class MultiRule<T> extends ArrayList<PropertyRule<T>> {

  /**
   * Creates an instance builder which is a recommended way to construct a MultiRule.
   * @param <T> type of patient object
   * @return builder
   */
  public static <T> MultiRuleBuilder<T> builder() {
    return new MultiRuleBuilder<>();
  }

  /**
   * Creates a MultiRule out of passed Rules.<br/>
   * Important: it adapts the rules as {@link PropertyRule}s with null property association.
   * If these already were PropertyRules, their associations will be overridden with null, which is interpreted as
   * association to whole object, not a property of it. This is usually undesired, but may be useful for passing
   * both whole object value and properties validations in one Validoctor call:<br/><br/>
   * {@code validoctor.examine(patient, MultiRule.of(notNull(), collectionNotEmpty()), multiRule1, multiRule2)}.
   * @param rules rules to put into new MultiRule. Non null.
   * @param <T> type of patient object
   * @return a new MultiRule
   */
  @SafeVarargs
  public static <T> MultiRule<T> of(Rule<T>... rules) {
    return MultiRule.of(Arrays.asList(rules));
  }

  /**
   * See {@link MultiRule#of(Rule[])}
   */
  public static <T> MultiRule<T> of(List<? extends Rule<T>> list) {
    MultiRule<T> multiRule = new MultiRule<>(list.size());
    list.forEach(rule -> multiRule.add(new PropertyRuleAdapter<>(rule)));
    return multiRule;
  }


  MultiRule(int initialCapacity) {
    super(initialCapacity);
  }

  MultiRule() {
    super();
  }

  /**
   * Merges two MultiRules dealing with the same type of patient into one MultiRule.
   * @param other MultiRule to merge with this rule
   * @return new MultiRule, with all rules from both this and the other MultiRule added
   */
  public MultiRule<T> and(MultiRule<T> other) {
    MultiRule<T> multiRule = new MultiRule<>(this.size() + other.size());
    multiRule.addAll(this);
    multiRule.addAll(other);
    return multiRule;
  }

}