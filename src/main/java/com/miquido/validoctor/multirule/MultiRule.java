package com.miquido.validoctor.multirule;

import com.miquido.validoctor.reducerrule.ReducerRule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * List of {@link PropertyRule}s for validation of a single class of objects.
 * @param <T> type of object validated with these rules
 */
public class MultiRule<T> extends ArrayList<PropertyRule<T>> {

  /**
   * Flattens a collection of MultiRules into one MultiRule.
   * @param multiRules multiRules to flatten into one
   * @param <T> type of patient object
   * @return flattened multiRule containing all rules from passed multiRules
   */
  @SafeVarargs
  public static <T> MultiRule<T> flatten(MultiRule<T>...multiRules) {
    return Stream.of(multiRules).flatMap(List::stream).collect(_InternalMultiRuleMappings.collector());
  }

  /**
   * Creates an instance builder which is a recommended way to construct a MultiRule.
   * @param <T> type of patient object
   * @return builder
   */
  public static <T> MultiRuleBuilder<T> builder() {
    return new MultiRuleBuilder<>();
  }


  MultiRule(int initialCapacity) {
    super(initialCapacity);
  }

  MultiRule() {
    super();
  }

  /**
   * Merges two MultiRules dealing with the same type of patient into one MultiRule.
   * @param other MultiRule to merge with this MultiRule
   * @return new MultiRule, with all rules from both this and the other MultiRule added
   */
  public MultiRule<T> and(MultiRule<T> other) {
    MultiRule<T> multiRule = new MultiRule<>(this.size() + other.size());
    multiRule.addAll(this);
    multiRule.addAll(other);
    return multiRule;
  }

  /**
   * Merges this MultiRule and a ReducerRule dealing with the same type of patient into one MultiRule.
   * Convenience method same as {@code and(MultiRule.of(reducerRule))}
   * @param reducerRule ReducerRule to merge with this MultiRule
   * @return new MultiRule, with all rules from this MultiRule and ones inferred from ReducerRule.
   */
  public MultiRule<T> and(ReducerRule<T, ?> reducerRule) {
    return and(_InternalMultiRuleMappings.of(reducerRule));
  }

}
