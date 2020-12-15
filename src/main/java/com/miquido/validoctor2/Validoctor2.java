package com.miquido.validoctor2;

import com.miquido.validoctor2.ruledefinition.RuleBuilder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Validoctor2 {

  public static <Patient> RuleBuilder<Patient> rulesFor(Class<Patient> clazz) {
    return new RuleBuilder<>(clazz);
  }

  @SafeVarargs
  public static <Patient> Diagnosis2 examine(Patient patient, Rule2<Patient>... rules) {
    return examine(patient, null, rules);
  }

  @SafeVarargs
  public static <Patient> Diagnosis2 examine(Patient patient, String patientName, Rule2<Patient>... rules) {
    Set<Ailment2> ailments = Arrays.stream(rules)
        .flatMap(rule ->
            rule.apply(patient).stream()
                .map(ailment -> {
                  if (patientName == null) {
                    return ailment;
                  } else {
                    String field = ailment.field == null ? patientName : patientName + "." + ailment.field;
                    return new Ailment2(field, ailment.value, ailment.ailments);
                  }
                })
        )
        .collect(Collectors.toSet());
    return new Diagnosis2(ailments);
  }
}
