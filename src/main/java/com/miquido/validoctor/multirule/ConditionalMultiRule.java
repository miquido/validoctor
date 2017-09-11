package com.miquido.validoctor.multirule;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.rule.Rule;
import lombok.Data;

import java.util.Collections;
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


  @Data
  public static class PropertyValidation<T> {
    private final Function<T, ?> propertyGetter;
    private final Set<Rule> rules;
    private final Predicate<T> condition;
  }


  private final Map<String, PropertyValidation<T>> propertyValidations;

  ConditionalMultiRule(Map<String, PropertyValidation<T>> propertyValidations) {
    this.propertyValidations = propertyValidations;
  }

  @Override
  public Map<String, Set<Ailment>> test(T patient) {
    Map<String, Set<Ailment>> propertiesAilments = new HashMap<>();
    propertyValidations.forEach((property, validation) -> {
      Predicate<T> condition = validation.getCondition();
      if (condition == null || condition.test(patient)) {
        propertiesAilments.putAll(checkRules(property, validation, patient));
      }
    });
    return propertiesAilments;
  }

  @Override
  public Map<String, Set<Ailment>> getAilments() {
    return propertyValidations.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue().getRules().stream().map(Rule::getAilment).collect(Collectors.toSet())));
  }


  private Map<String, Set<Ailment>> checkRules(String property, PropertyValidation<T> validation, T patient) {
    Map<String, Set<Ailment>> propertiesAilments = new HashMap<>();
    validation.getRules().forEach(rule -> {
      if (!rule.test(validation.getPropertyGetter().apply(patient))) {
        propertiesAilments.computeIfAbsent(property, k -> new HashSet<>()).add(rule.getAilment());
      }
    });
    return propertiesAilments;
  }
}
