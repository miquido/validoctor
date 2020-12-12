package com.miquido.validoctor;

import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.definition.RuleBuilder;
import com.miquido.validoctor.definition.Rules;
import com.miquido.validoctor.result.Ailment;
import com.miquido.validoctor.result.Diagnosis;
import com.miquido.validoctor.result.DiagnosisException;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Validoctor {

  private Validoctor() {}

  private static boolean throwing = false;
  private static Function<Diagnosis, RuntimeException> exceptionFactory = DiagnosisException::new;

  /**
   * Set whether Validoctor should throw exceptions on failed examinations, or just return the {@link Diagnosis}.
   */
  public static void setThrowing(boolean throwing) {
    Validoctor.throwing = throwing;
  }

  /**
   * Set exception factory to use when creating an exception in case of examination finding any violations.<br>
   * Note that exceptions are only ever thrown if Validoctor is set to {@link Validoctor#setThrowing(boolean) throwing}.
   */
  public static void setExceptionFactory(Function<Diagnosis, RuntimeException> factory) {
    exceptionFactory = factory;
  }

  /**
   * Starts rules building for given Patient class.<br>
   * After rules for patient and its fields are defined in the builder call {@link RuleBuilder#build()}
   * to get a Rule ready to use in {@link Validoctor#examine(Object, Rule[])} calls.<br>
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
   * {@link Rules} or composites made with {@link Validoctor#rulesFor(Class)},
   * also used interchangeably.
   * @param patient object to examine
   * @param rules vararg list of rules to apply
   * @param <Patient> type of patient
   * @return diagnosis object containing all discovered violations in the patient object
   */
  @SafeVarargs
  public static <Patient> Diagnosis examine(Patient patient, Rule<Patient>... rules) {
    return examine(patient, "", rules);
  }

  /**
   * Same as {@link Validoctor#examine(Object, Rule[])} but allows giving a custom name to patient object that will
   * be used in resulting Diagnosis.
   * @param patient object to examine
   * @param patientName name of patient
   * @param rules vararg list of rules to apply
   * @param <Patient> type of patient
   * @return diagnosis object containing all discovered violations in the patient object
   */
  @SafeVarargs
  public static <Patient> Diagnosis examine(Patient patient, String patientName, Rule<Patient>... rules) {
    Set<Ailment> ailments = Arrays.stream(rules)
        .flatMap(rule ->
            rule.apply(patient).stream()
                .map(ailment -> {
                  if (patientName == null) {
                    return ailment;
                  } else {
                    String field = ailment.field == null ? patientName
                        : patientName.isEmpty() ? ailment.field : patientName + "." + ailment.field;
                    return new Ailment(field, ailment.ailments);
                  }
                })
        )
        .collect(Collectors.toSet());
    Diagnosis diagnosis = new Diagnosis(ailments);
    if (throwing && !diagnosis.isValid()) {
      throw exceptionFactory.apply(diagnosis);
    } else {
      return diagnosis;
    }
  }
}
