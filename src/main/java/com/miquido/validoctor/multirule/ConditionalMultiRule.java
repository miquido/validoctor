package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.rule.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Allows conditional validation of multiple properties of a single object.
 * Use {@link MultiRuleBuilder} to create an instance.
 * @param <T> type of validated object
 */
class ConditionalMultiRule<T> implements MultiRule<T> {

  private final Map<String, Function<T, ?>> propertiesGettersMap;
  private final Map<String, Set<Rule>> propertiesRulesMap;
  private final Map<String, Predicate<T>> propertiesConditionsMap;

  ConditionalMultiRule(Map<String, Function<T, ?>> propertiesGettersMap,
                       Map<String, Set<Rule>> propertiesRulesMap,
                       Map<String, Predicate<T>> propertiesConditionsMap) {
    this.propertiesGettersMap = propertiesGettersMap;
    this.propertiesRulesMap = propertiesRulesMap;
    this.propertiesConditionsMap = propertiesConditionsMap;
  }

  @Override
  public Map<String, Set<Ailment>> test(T obj) {
    Map<String, Set<Ailment>> propertiesAilments = new HashMap<>();
    for (String property : propertiesGettersMap.keySet()) {
      for (Rule rule : propertiesRulesMap.get(property)) {
        Predicate<T> condition = propertiesConditionsMap.get(property);
        if (condition == null || condition.test(obj)) {
          boolean passed = rule.test(propertiesGettersMap.get(property).apply(obj));
          if (!passed) {
            propertiesAilments.computeIfAbsent(property, k -> new HashSet<>()).add(rule.getAilment());
          }
        }
      }
    }
    return propertiesAilments;
  }

  @Override
  public Map<String, Set<Ailment>> getAilments() {
    return propertiesRulesMap.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue().stream().map(Rule::getAilment).collect(Collectors.toSet())));
  }
}
