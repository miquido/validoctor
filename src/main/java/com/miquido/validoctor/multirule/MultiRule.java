package com.miquido.validoctor.multirule;

import com.miquido.validoctor.reducerrule.ReducerRule;
import com.miquido.validoctor.rule.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
   * For internal use. Creates a MultiRule out of passed Rules.<br/>
   * Important: it adapts the rules as {@link PropertyRule}s with null property association.
   * If these already were PropertyRules, their associations will be overridden with null, which is interpreted as
   * association to whole object, not a property of it. This is usually undesired. If you need to perform both whole
   * object and properties validation in one call, use:<br/><br/>
   * {@code validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2)}.<br/>
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

  /**
   * For internal use. Creates a MultiRule out of passed ReducerRules. This results in a MultiRule containing a
   * PropertyRule for each property the ReducerRule is associated with. These are special implementations of
   * PropertyRule that are aware of rules for other properties and share the results of their examination to them,
   * avoiding excess validations.
   * @param reducerRules rules to translate into new MultiRule. Non null.
   * @param <T> type of patient object
   * @return a new MultiRule
   */
  @SafeVarargs
  public static <T> MultiRule<T> of(ReducerRule<T, ?>... reducerRules) {
    int sumSizes = Stream.of(reducerRules)
        .map(rule -> rule.getProperties().size())
        .reduce((sumSize, size) -> sumSize + size).orElse(0);
    MultiRule<T> multiRule = new MultiRule<>(sumSizes);

    Stream.of(reducerRules).forEach(rule -> {
      RealShadowRule<T> originalShadow = new RealShadowRule<>(rule);
      rule.getProperties().forEach(property -> multiRule.add(new ShadowRule<>(property, originalShadow)));
    });
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
    return and(MultiRule.of(reducerRule));
  }

}
