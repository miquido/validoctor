package com.miquido.validoctor2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectRule<T> implements Rule2<T> {

  public static class FieldRules {
    public String fieldName;
    public int layer;
    public List<Rule2> rules;

    public FieldRules(String fieldName, int layer, List<Rule2> rules) {
      this.fieldName = fieldName;
      this.layer = layer; //0 always exists
      this.rules = rules;
    }
  }

  public final List<FieldRules> fieldsRules = new ArrayList<>();
  private final Class<T> clazz;

  public ObjectRule(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Set<Violation> test(T patient) {
    Set<Violation> results = new HashSet<>();
    Map<String, List<FieldRules>> rulesByFields = groupByFields();
    rulesByFields.forEach((field, rulesForField) -> {
      Map<Integer, List<FieldRules>> rulesByLayer = groupByLayer(rulesForField);
      int maxLayer = getDeepestLayer(rulesByLayer);
      for (int i = 0; i <= maxLayer; i++) {
        FieldRules fieldRules = rulesByLayer.get(i).get(0); //there always is just one
        Set<Violation> violations = runRuleSet(patient, fieldRules);
        if (!violations.isEmpty()) {
          results.addAll(violations);
          break; //if there are any violations, do not run any further layers for this field
        }
      }
    });
    return results;
  }

  @Override
  public String getViolationMessage() {
    return null;
  }

  private Integer getDeepestLayer(Map<Integer, List<FieldRules>> rulesByLayer) {
    return rulesByLayer.keySet().stream().max(Integer::compareTo).orElse(0);
  }

  private Map<Integer, List<FieldRules>> groupByLayer(List<FieldRules> rulesForField) {
    return rulesForField.stream()
        .collect(Collectors.groupingBy(fr -> fr.layer));
  }

  private Map<String, List<FieldRules>> groupByFields() {
    return fieldsRules.stream()
        .collect(Collectors.groupingBy(fieldRules -> fieldRules.fieldName));
  }

  private Set<Violation> runRuleSet(T patient, FieldRules fieldRules) {
    Set<Violation> results = new HashSet<>();
    fieldRules.rules.forEach(rule -> {
      String fieldName = fieldRules.fieldName;
      Set<Violation> violations = rule.test(readField(fieldName, patient));
      results.addAll(
          violations.stream()
              .map(r -> {
                String field = fieldName;
                if (!r.field.isEmpty()) {
                  field += "." + r.field;
                }
                //TODO append collection index here, if rule is a collection one
                return new Violation(field, r.value, r.ailments);
              })
              .collect(Collectors.toSet())
      );
    });
    return results;
  }

  private Object readField(String fieldName, T object) {
    try {
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      //TODO
      return null;
    }
  }
}
