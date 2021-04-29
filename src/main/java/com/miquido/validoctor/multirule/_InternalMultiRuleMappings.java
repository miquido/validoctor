package com.miquido.validoctor.multirule;

import com.miquido.validoctor.reducerrule.ReducerRule;
import com.miquido.validoctor.rule.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Class containing internally used mapping methods from other Rule types to MultiRule.<br>
 * Usually not suitable for client code needs.
 */
public class _InternalMultiRuleMappings {
  private _InternalMultiRuleMappings() {}


  /**
   * Creates a collector to use in stream operations on MultiRule objects.
   * @param <T> type of patient object
   * @return collector to be used in stream operations
   */
  public static <T> Collector<PropertyRule<T>, MultiRule<T>, MultiRule<T>> collector() {
    return Collector.of(
        MultiRule::new,
        ArrayList::add,
        MultiRule::and,
        Collector.Characteristics.UNORDERED
    );
  }

  /**
   * For internal use. Creates a MultiRule out of passed Rules.<br>
   * Important: it adapts the rules as {@link PropertyRule}s with <strong>objectName</strong> property association.
   * If these already were PropertyRules, their associations will be overridden, erasing all
   * mapping of rules to properties. This is usually undesired. If you need to perform both whole
   * object and properties validation in one call, use:<br><br>
   * {@code validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2)}.<br>
   * @param objectName name of the object to report the Ailments for
   * @param rules rules to put into new MultiRule. Non null.
   * @param <T> type of patient object
   * @return a new MultiRule
   */
  @SafeVarargs
  public static <T> MultiRule<T> of(String objectName, Rule<T>... rules) {
    return of(objectName, Arrays.asList(rules));
  }

  /**
   * See {@link _InternalMultiRuleMappings#of(String, Rule[])}
   */
  public static <T> MultiRule<T> of(String objectName, List<? extends Rule<T>> list) {
    MultiRule<T> multiRule = new MultiRule<>(list.size());
    list.forEach(rule -> multiRule.add(new PropertyRuleAdapter<>(objectName, rule)));
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
        .mapToInt(rule -> rule.getProperties().size())
        .sum();
    MultiRule<T> multiRule = new MultiRule<>(sumSizes);

    Stream.of(reducerRules).forEach(rule -> {
      RealShadowRule<T> originalShadow = new RealShadowRule<>(rule);
      rule.getProperties().forEach(property -> multiRule.add(new ShadowRule<>(property, originalShadow)));
    });
    return multiRule;
  }
}
