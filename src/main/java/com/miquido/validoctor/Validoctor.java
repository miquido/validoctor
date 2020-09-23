package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.diagnosis.DiagnosisException;
import com.miquido.validoctor.multirule.MultiRule;
import com.miquido.validoctor.multirule._InternalMultiRuleMappings;
import com.miquido.validoctor.multirule.PropertyRule;
import com.miquido.validoctor.reducerrule.ReducerRule;
import com.miquido.validoctor.rule.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Good Morning! Patients please stand in line, I'll be calling you one by one.
 */
public final class Validoctor {

  public static Builder builder() {
    return new Builder();
  }

  private static final String DEFAULT_NAME = "OBJECT";


  private final boolean pedantic;
  private final boolean exceptional;
  private final Function<Diagnosis, RuntimeException> exceptionFactory;

  private Validoctor(boolean pedantic, boolean exceptional, Function<Diagnosis, RuntimeException> exceptionFactory) {
    this.pedantic = pedantic;
    this.exceptional = exceptional;
    this.exceptionFactory = exceptionFactory;
  }

  /**
   * Single value examination with default patientName == {@value DEFAULT_NAME}.
   * See {@link Validoctor#examine(Object, String, Rule[])}
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, Rule<T>... rules) {
    return examine(patient, DEFAULT_NAME, rules);
  }

  /**
   * Single value examination.
   * @param patient value to validate
   * @param patientName name of the validated value to be used in resulting Diagnosis
   * @param rules validation rules
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, String patientName, Rule<T>... rules) {
    return examine(patient, _InternalMultiRuleMappings.of(patientName, rules));
  }

  /**
   * Multi property object examination.
   * @param patient object to validate
   * @param multiRules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, MultiRule<T>... multiRules) {
    return examine(patient, MultiRule.flatten(multiRules));
  }

  /**
   * Object examination with ReducerRules.
   * @param patient object to validate
   * @param reducerRules validation ReducerRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, ReducerRule<T, ?>... reducerRules) {
    return examine(patient, _InternalMultiRuleMappings.of(reducerRules));
  }

  /**
   * Multi property object examination, pre-examined with a single Rule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.<br/>
   * This method does not provide any safeguard against null patient, so a NullPointerException may occur in case of one,
   * depending on exact Rules used.
   * Checking for non-null patient before attempting validation of its properties is a typical use case for this method:
   * <br/><br/>{@code examineCombo(patient, Rules.notNull(), multiRules)}<br/>
   *
   * @param patient object to validate
   * @param patientName name of the validated value to be used in resulting Diagnosis
   * @param preRule Rule applied to patient object as a whole before attempting to apply other rules
   * @param multiRules MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examineChain(T patient, String patientName, Rule<T> preRule, MultiRule<T>... multiRules) {
    Diagnosis preDiagnosis = examine(patient, patientName, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis : examine(patient, MultiRule.flatten(multiRules));
  }

  /**
   * Multi property object examination, pre-examined with a single Rule, with default patientName == {@value DEFAULT_NAME}.
   * See {@link Validoctor#examineChain(Object, String, Rule, MultiRule[])}
   */
  @SafeVarargs
  public final <T> Diagnosis examineChain(T patient, Rule<T> preRule, MultiRule<T>... multiRules) {
    return examineChain(patient, DEFAULT_NAME, preRule, multiRules);
  }

  /**
   * Object examination with ReducerRules, pre-examined with a single Rule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.<br/>
   * This method does not provide any safeguard against null patient, so a NullPointerException may occur in case of one,
   * depending on exact Rules used.
   * Checking for non-null patient before attempting validation of its properties is a typical use case for this method:
   * <br/><br/>{@code examineCombo(patient, Rules.notNull(), reducerRules)}<br/>
   *
   * @param patient object to validate
   * @param patientName name of the validated value to be used in resulting Diagnosis
   * @param preRule Rule applied to patient object as a whole before attempting to apply other rules
   * @param reducerRules ReducerRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examineChain(T patient, String patientName, Rule<T> preRule, ReducerRule<T, ?>... reducerRules) {
    Diagnosis preDiagnosis = examine(patient, patientName, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis : examine(patient, _InternalMultiRuleMappings.of(reducerRules));
  }

  /**
   * Object examination with ReducerRules, pre-examined with a single Rule, with default patientName == {@value DEFAULT_NAME}.
   * See {@link Validoctor#examineChain(Object, String, Rule, ReducerRule[])}
   */
  @SafeVarargs
  public final <T> Diagnosis examineChain(T patient, Rule<T> preRule, ReducerRule<T, ?>... reducerRules) {
    return examineChain(patient, DEFAULT_NAME, preRule, reducerRules);
  }

  /**
   * Multi property object examination, pre-examined with a single ReducerRule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.
   * This method does not provide any safeguard against null patient, so a NullPointerException may occur in case of one,
   * depending on exact Rules used.
   * @param patient object to validate
   * @param preRule ReducerRule applied to patient object as a whole before attempting to apply other rules
   * @param multiRules MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examineChain(T patient, ReducerRule<T, ?> preRule, MultiRule<T>... multiRules) {
    Diagnosis preDiagnosis = examine(patient, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis : examine(patient, MultiRule.flatten(multiRules));
  }

  /**
   * Multi property object examination joined with a single Rule examination. Both the single Rule and all MultiRules
   * are always applied. Resulting Diagnosis contains all Ailments from all violated Rules.
   * This method does not provide any safeguard against null patient, so a NullPointerException may occur in case of one,
   * depending on exact Rules used.
   * @param patient object to validate
   * @param patientName name of the validated value to be used in resulting Diagnosis
   * @param rule Rule applied to patient object as a whole
   * @param multiRules MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examineJoin(T patient, String patientName, Rule<T> rule, MultiRule<T>... multiRules) {
    MultiRule<T> multiRule = MultiRule.flatten(multiRules);
    return examine(patient, _InternalMultiRuleMappings.of(patientName, rule).and(multiRule));
  }

  /**
   * Multi property object examination joined with a single Rule examination, with default patientName == {@value DEFAULT_NAME}.
   * See {@link Validoctor#examineJoin(Object, String, Rule, MultiRule[])}
   */
  @SafeVarargs
  public final <T> Diagnosis examineJoin(T patient, Rule<T> rule, MultiRule<T>... multiRules) {
    return examineJoin(patient, DEFAULT_NAME, rule, multiRules);
  }

  /**
   * Object examination with ReducerRules joined with a single Rule examination. Both the single Rule and all ReducerRules
   * are always applied. Resulting Diagnosis contains all Ailments from all violated Rules.
   * This method does not provide any safeguard against null patient, so a NullPointerException may occur in case of one,
   * depending on exact Rules used.
   * @param patient object to validate
   * @param patientName name of the validated value to be used in resulting Diagnosis
   * @param rule Rule applied to patient object as a whole before attempting to apply other rules
   * @param reducerRules ReducerRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examineJoin(T patient, String patientName, Rule<T> rule, ReducerRule<T, ?>... reducerRules) {
    return examine(patient, _InternalMultiRuleMappings.of(patientName, rule).and(_InternalMultiRuleMappings.of(reducerRules)));
  }

  /**
   * Object examination with ReducerRules joined with a single Rule examination, with default patientName == {@value DEFAULT_NAME}.
   * See {@link Validoctor#examineJoin(Object, String, Rule, ReducerRule[])}
   */
  @SafeVarargs
  public final <T> Diagnosis examineJoin(T patient, Rule<T> rule, ReducerRule<T, ?>... reducerRules) {
    return examineJoin(patient, DEFAULT_NAME, rule, reducerRules);
  }

  /**
   * Multi property object examination joined with a single ReducerRule examination. Both the ReducerRule and all MultiRules
   * are always applied. Resulting Diagnosis contains all Ailments from all violated Rules.
   * This method does not provide any safeguard against null patient, so a NullPointerException may occur in case of one,
   * depending on exact Rules used.
   * @param patient object to validate
   * @param reducerRule ReducerRule applied to patient object as a whole
   * @param multiRules MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  @SafeVarargs
  public final <T> Diagnosis examineJoin(T patient, ReducerRule<T, ?> reducerRule, MultiRule<T>... multiRules) {
    MultiRule<T> multiRule = MultiRule.flatten(multiRules);
    return examine(patient, _InternalMultiRuleMappings.of(reducerRule).and(multiRule));
  }

  // ---------------- DEPRECATED BELOW ------------------

  /**
   * DEPRECATED. Will be removed in 1.2.0. Use {@link Validoctor#examineChain(Object, String, Rule, ReducerRule[])} instead.
   *
   * Object examination with ReducerRules, pre-examined with a single Rule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.<br/>
   * Typical use case for this method is to check for non-null patient before attempting validation of its properties:<br/><br/>
   * {@code examineCombo(patient, Rules.notNull(), reducerRules)}<br/>
   *
   * @param patient object to validate
   * @param patientName name of the validated value to be used in resulting Diagnosis
   * @param preRule validation Rule applied to patient object as a whole before attempting to apply other rules
   * @param reducerRules validation ReducerRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @Deprecated
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, String patientName, Rule<T> preRule, ReducerRule<T, ?>... reducerRules) {
    Diagnosis preDiagnosis = examine(patient, patientName, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis : examine(patient, _InternalMultiRuleMappings.of(reducerRules));
  }

  /**
   * DEPRECATED. Will be removed in 1.2.0. Use {@link Validoctor#examineChain(Object, Rule, ReducerRule[])} instead.
   *
   * Object examination with ReducerRules, pre-examined with a single Rule, with default patientName == {@value DEFAULT_NAME}.
   * See {@link Validoctor#examineCombo(Object, String, Rule, ReducerRule[])}
   */
  @Deprecated
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, Rule<T> preRule, ReducerRule<T, ?>... reducerRules) {
    return examineCombo(patient, DEFAULT_NAME, preRule, reducerRules);
  }

  /**
   * DEPRECATED. Will be removed in 1.2.0. Use {@link Validoctor#examineChain(Object, String, Rule, MultiRule[])} instead.
   *
   * Multi property object examination, pre-examined with a single Rule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.<br/>
   * Typical use case for this method is to check for non-null patient before attempting validation of its properties:<br/><br/>
   * {@code examineCombo(patient, Rules.notNull(), multiRules)}<br/>
   *
   * @param patient object to validate
   * @param patientName name of the validated value to be used in resulting Diagnosis
   * @param preRule validation Rule applied to patient object as a whole before attempting to apply other rules
   * @param multiRules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @Deprecated
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, String patientName, Rule<T> preRule, MultiRule<T>... multiRules) {
    Diagnosis preDiagnosis = examine(patient, patientName, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis : examine(patient, MultiRule.flatten(multiRules));
  }

  /**
   * DEPRECATED. Will be removed in 1.2.0. Use {@link Validoctor#examineChain(Object, Rule, MultiRule[])} instead.
   *
   * Multi property object examination, pre-examined with a single Rule, with default patientName == {@value DEFAULT_NAME}.
   * See {@link Validoctor#examineCombo(Object, String, Rule, MultiRule[])}
   */
  @Deprecated
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, Rule<T> preRule, MultiRule<T>... multiRules) {
    return examineCombo(patient, DEFAULT_NAME, preRule, multiRules);
  }

  /**
   * DEPRECATED. Will be removed in 1.2.0. Use {@link Validoctor#examineChain(Object, ReducerRule, MultiRule[])} instead.
   *
   * Multi property object examination, pre-examined with a single ReducerRule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.
   * @param patient object to validate
   * @param preRule validation ReducerRule applied to patient object as a whole before attempting to apply other rules
   * @param multiRules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @Deprecated
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, ReducerRule<T, ?> preRule, MultiRule<T>... multiRules) {
    //examine call here must be duplicated from method above due to static typing of MultiRule.of falling back to Rule
    Diagnosis preDiagnosis = examine(patient, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis : examine(patient, MultiRule.flatten(multiRules));
  }

  // ---------------- DEPRECATED ABOVE ------------------

  /**
   * Multi property object examination.
   * @param patient object to validate
   * @param rules validation MultiRule for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException or exception created with exceptionFactory specified when building this Validoctor.
   * Only if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}.
   */
  public final <T> Diagnosis examine(T patient, MultiRule<T> rules) {
    Severity severity = Severity.OK;
    Map<String, Set<Ailment>> ailments = new HashMap<>();
    for (PropertyRule<T> rule : rules) {
      Ailment ailment = rule.apply(patient);
      if (ailment != null) {
        if (ailment.getSeverity().isWorseThan(severity)) {
          severity = ailment.getSeverity();
        }
        ailments.computeIfAbsent(rule.getProperty(), key -> new HashSet<>()).add(ailment);
        if (!pedantic) {
          break;
        }
      }
    }
    return stateDiagnosis(severity, ailments);
  }

  private Diagnosis stateDiagnosis(Severity severity, Map<String, Set<Ailment>> ailments) {
    Diagnosis diagnosis = new Diagnosis(severity, ailments);
    if (exceptional && severity == Severity.ERROR) {
      if (exceptionFactory == null) {
        throw new DiagnosisException(diagnosis);
      } else {
        throw exceptionFactory.apply(diagnosis);
      }
    }
    return diagnosis;
  }


  public static final class Builder {

    private boolean pedantic = true;
    private boolean exceptional = false;
    private Function<Diagnosis, RuntimeException> exceptionFactory;

    /**
     * Sets whether this Validoctor will be pedantic or not. Defaults to true.
     * <li>If true, will execute all passed rules, continuing even after encountering violations, and will return
     * a diagnosis with all violations found.</li>
     * <li>If false, will only execute rules until first violation, and will return a diagnosis with just this one.</li>
     * @return this Builder
     */
    public Builder pedantic(boolean pedantic) {
      this.pedantic = pedantic;
      return this;
    }

    /**
     * Sets whether this Validoctor will be exceptional or not. Defaults to false.
     * <li>If true, will throw an exception created using specified {@link Builder#exceptionFactory(Function) exceptionFactory}
     * or {@link DiagnosisException} containing the Diagnosis if no exceptionFactory was specified.</li>
     * <li>If false, will just return the Diagnosis.</li>
     * @return this Builder
     */
    public Builder exceptional(boolean exceptional) {
      this.exceptional = exceptional;
      return this;
    }

    /**
     * Sets function used to create exceptions thrown by {@link Builder#exceptional(boolean) exceptional} Validoctor.
     * If no function is set, exceptional Validoctor will throw {@link DiagnosisException}s wrapping stated {@link Diagnosis}.
     * @return this Builder
     */
    public Builder exceptionFactory(Function<Diagnosis, RuntimeException> factory) {
      this.exceptionFactory = factory;
      return this;
    }

    public Validoctor build() {
      return new Validoctor(pedantic, exceptional, exceptionFactory);
    }
  }

}
