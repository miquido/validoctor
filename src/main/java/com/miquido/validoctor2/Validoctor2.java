package com.miquido.validoctor2;

import com.miquido.validoctor2.ruledefinition.ObjectRuleBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Validoctor2 {

  public static <Patient> ObjectRuleBuilder<Patient> rulesFor(Class<Patient> clazz) {
    return new ObjectRuleBuilder<>(clazz);
  }

  @SafeVarargs
  public static <Patient> Diagnosis2 examine(Patient patient, Rule2<Patient>... rules) {
    Set<Violation> results = new HashSet<>();
    Arrays.stream(rules).forEach(rule -> results.addAll(rule.test(patient)));
    return new Diagnosis2(results);
  }
}
