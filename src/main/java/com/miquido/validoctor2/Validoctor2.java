package com.miquido.validoctor2;

import com.miquido.validoctor2.result.Ailment2;
import com.miquido.validoctor2.result.Diagnosis2;
import com.miquido.validoctor2.result.DiagnosisException2;
import com.miquido.validoctor2.rule.Rule2;
import com.miquido.validoctor2.definition.RuleBuilder;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Validoctor2 {

  private static boolean throwing = false;
  private static Function<Diagnosis2, RuntimeException> exceptionFactory = DiagnosisException2::new;

  public static void setThrowing(boolean throwing) {
    Validoctor2.throwing = throwing;
  }

  public static void setExceptionFactory(Function<Diagnosis2, RuntimeException> factory) {
    exceptionFactory = factory;
  }

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
    Diagnosis2 diagnosis = new Diagnosis2(ailments);
    if (throwing) {
      throw exceptionFactory.apply(diagnosis);
    } else {
      return diagnosis;
    }
  }
}
