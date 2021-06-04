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

  /**
   * Starts rule batches building for given Patient class.<br>
   * Batch is a set of rules for given field, that can be linked to the next batch for the same field.<br>
   * Linked batch is only executed if its preceding batch has succeeded.<br>
   * First batch in a series (one that none other links to) is called a root batch.<br>
   * Multiple independent root batches for same field can exist.
   * <br><br>
   * <pre>
   *   A  B   C   D    <- fields of patient object
   *   |  |\  |  /|\   <- root batches - these will always execute
   *   |  |      | |   <- linked batches - these will only execute if their direct predecessors succeed
   *   |               <- another linked batch - there can be any number of batches in the branch
   * </pre><br><br>
   * Rules are stateless, immutable and reusable
   * @param clazz class of patient
   * @param <Patient> type of patient
   * @return builder
   */
  public static <Patient> RuleBuilder<Patient> rulesFor(Class<Patient> clazz) {
    return new RuleBuilder<>(clazz);
  }

  /**
   * Examines the patient object with given rules. Those may be predefined simple rules from
   * {@link com.miquido.validoctor2.rule.Rules2} or composites made with {@link Validoctor2#rulesFor(Class)},
   * also used interchangeably.
   * @param patient object to examine
   * @param rules vararg list of rules to apply
   * @param <Patient> type of patient
   * @return diagnosis object containing all discovered violations in the patient object
   */
  @SafeVarargs
  public static <Patient> Diagnosis2 examine(Patient patient, Rule2<Patient>... rules) {
    return examine(patient, null, rules);
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
                    String field = ailment.field == null ? patientName : patientName + "." + ailment.field;
                    return new Ailment2(field, ailment.value, ailment.ailments);
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
