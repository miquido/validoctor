package com.miquido.validoctor2;

import com.miquido.validoctor2.definition.Rule2;
import com.miquido.validoctor2.definition.RuleBuilder;
import com.miquido.validoctor2.definition.Rules2;
import com.miquido.validoctor2.result.Ailment2;
import com.miquido.validoctor2.result.Diagnosis2;
import com.miquido.validoctor2.result.DiagnosisException2;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Validoctor2 {

  private static boolean throwing = false;
  private static Function<Diagnosis2, RuntimeException> exceptionFactory = DiagnosisException2::new;

  /**
   * Set whether Validoctor should throw exceptions on failed examinations, or just return the {@link Diagnosis2}.
   */
  public static void setThrowing(boolean throwing) {
    Validoctor2.throwing = throwing;
  }

  /**
   * Set exception factory to use when creating an exception in case of examination finding any violations.</br>
   * Note that exceptions are only ever thrown if Validoctor is set to {@link Validoctor2#setThrowing(boolean) throwing}.
   */
  public static void setExceptionFactory(Function<Diagnosis2, RuntimeException> factory) {
    exceptionFactory = factory;
  }

  /**
   * Starts rules building for given Patient class.<br>
   * After rules for patient and its fields are defined in the builder call {@link RuleBuilder#build()}
   * to get a Rule ready to use in {@link Validoctor2#examine(Object, Rule2[])} calls.</br>
   * Rules are stateless, immutable and reusable.
   * @param clazz class of patient
   * @param <Patient> type of patient
   * @return builder
   */
  public static <Patient> RuleBuilder<Patient> rulesFor(Class<Patient> clazz) {
    return new RuleBuilder<>(clazz);
  }

  /**
   * Examines the patient object with given rules. Those may be predefined simple rules from
   * {@link Rules2} or composites made with {@link Validoctor2#rulesFor(Class)},
   * also used interchangeably.
   * @param patient object to examine
   * @param rules vararg list of rules to apply
   * @param <Patient> type of patient
   * @return diagnosis object containing all discovered violations in the patient object
   */
  @SafeVarargs
  public static <Patient> Diagnosis2 examine(Patient patient, Rule2<Patient>... rules) {
    return examine(patient, "", rules);
  }

  /**
   * Same as {@link Validoctor2#examine(Object, Rule2[])} but allows giving a custom name to patient object that will
   * be used in resulting Diagnosis.
   * @param patient object to examine
   * @param patientName name of patient
   * @param rules vararg list of rules to apply
   * @param <Patient> type of patient
   * @return diagnosis object containing all discovered violations in the patient object
   */
  @SafeVarargs
  public static <Patient> Diagnosis2 examine(Patient patient, String patientName, Rule2<Patient>... rules) {
    Set<Ailment2> ailments = Arrays.stream(rules)
        .flatMap(rule ->
            rule.apply(patient).stream()
                .map(ailment -> {
                  if (patientName == null) {
                    return ailment;
                  } else {
                    String field = ailment.field == null ? patientName
                        : patientName.isEmpty() ? ailment.field : patientName + "." + ailment.field;
                    return new Ailment2(field, ailment.ailments);
                  }
                })
        )
        .collect(Collectors.toSet());
    Diagnosis2 diagnosis = new Diagnosis2(ailments);
    if (throwing && !diagnosis.isValid()) {
      throw exceptionFactory.apply(diagnosis);
    } else {
      return diagnosis;
    }
  }
}
