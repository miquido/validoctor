package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.diagnosis.DiagnosisException;
import com.miquido.validoctor.multirule.MultiRule;
import com.miquido.validoctor.multirule.PropertyRule;
import com.miquido.validoctor.reducerrule.ReducerRule;
import com.miquido.validoctor.rule.MeticulousRuleDecorator;
import com.miquido.validoctor.rule.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Good Morning! Patients please stand in line, I'll be calling you one by one.
 */
public final class Validoctor {

  public static Builder builder() {
    return new Builder();
  }


  private final boolean pedantic;
  private final boolean exceptional;
  private final boolean meticulous;

  private Validoctor(boolean pedantic, boolean exceptional, boolean meticulous) {
    this.pedantic = pedantic;
    this.exceptional = exceptional;
    this.meticulous = meticulous;
  }

  /**
   * Single value examination.
   * @param patient value to validate
   * @param rules validation rules
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, Rule<T>... rules) {
    return examine(patient, MultiRule.of(rules));
  }

  /**
   * Multi property object examination.
   * @param patient object to validate
   * @param multiRules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, MultiRule<T>... multiRules) {
    return examine(patient, Stream.of(multiRules).reduce(MultiRule::and)
        .orElseThrow(() -> new RuntimeException("Never happens")));
  }

  /**
   * Object examination with ReducerRules.
   * @param patient object to validate
   * @param reducerRules validation ReducerRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, ReducerRule<T, ?>... reducerRules) {
    return examine(patient, MultiRule.of(reducerRules));
  }

  /**
   * Object examination with ReducerRules, pre-examined with a single Rule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.<br/>
   * Typical use case for this method is to check for non-null patient before attempting validation of its properties:<br/><br/>
   * {@code examineCombo(patient, Rules.notNull(), reducerRules)}<br/>
   * @param patient object to validate
   * @param preRule validation Rule applied to patient object as a whole before attempting to apply other rules
   * @param reducerRules validation ReducerRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, Rule<T> preRule, ReducerRule<T, ?>... reducerRules) {
    Diagnosis preDiagnosis = examine(patient, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis : examine(patient, MultiRule.of(reducerRules));
  }

  /**
   * Multi property object examination, pre-examined with a single Rule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.<br/>
   * Typical use case for this method is to check for non-null patient before attempting validation of its properties:<br/><br/>
   * {@code examineCombo(patient, Rules.notNull(), multiRules)}<br/>
   * @param patient object to validate
   * @param preRule validation Rule applied to patient object as a whole before attempting to apply other rules
   * @param multiRules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, Rule<T> preRule, MultiRule<T>... multiRules) {
    Diagnosis preDiagnosis = examine(patient, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis
        : examine(patient, Stream.of(multiRules).reduce(MultiRule::and).orElseThrow(() -> new RuntimeException("Never happens")));
  }

  /**
   * Multi property object examination, pre-examined with a single ReducerRule. If the preRule examination results in
   * {@link Severity#ERROR} diagnosis, it is returned and rest of the rules are not applied.
   * @param patient object to validate
   * @param preRule validation ReducerRule applied to patient object as a whole before attempting to apply other rules
   * @param multiRules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, ReducerRule<T, ?> preRule, MultiRule<T>... multiRules) {
    Diagnosis preDiagnosis = examine(patient, preRule);
    return preDiagnosis.getSeverity() == Severity.ERROR ? preDiagnosis
        : examine(patient, Stream.of(multiRules).reduce(MultiRule::and).orElseThrow(() -> new RuntimeException("Never happens")));
  }

  /**
   * Multi property object examination.
   * @param patient object to validate
   * @param rules validation MultiRule for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  public final <T> Diagnosis examine(T patient, MultiRule<T> rules) {
    Severity severity = Severity.OK;
    Map<String, Set<Ailment>> ailments = new HashMap<>();
    for (PropertyRule<T> rule : rules) {
      PropertyRule<T> theRule = meticulous ? new MeticulousRuleDecorator<>(rule) : rule;
      Ailment ailment = theRule.apply(patient);
      if (ailment != null) {
        if (ailment.getSeverity().isWorseThan(severity)) {
          severity = ailment.getSeverity();
        }
        ailments.computeIfAbsent(theRule.getProperty(), key -> new HashSet<>()).add(ailment);
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
      throw new DiagnosisException(diagnosis);
    }
    return diagnosis;
  }


  public static final class Builder {

    private boolean pedantic = true;
    private boolean exceptional = false;
    private boolean meticulous = false;

    /**
     * Sets whether this Validoctor will be pedantic or not. Defaults to true.
     * <li>If true, will execute all passed rules, continuing even after encountering violations, and will return
     * a diagnosis with all violations found.</li>
     * <li>If false, will only execute rules until first violation, and will return a diagnosis with just this one.</li>
     * @param pedantic
     * @return this Builder
     */
    public Builder pedantic(boolean pedantic) {
      this.pedantic = pedantic;
      return this;
    }

    /**
     * Sets whether this Validoctor will be exceptional or not. Defaults to false.
     * <li>If true, will throw an exception containing the Diagnosis.</li>
     * <li>If false, will just return the Diagnosis.</li>
     * @param exceptional
     * @return this Builder
     */
    public Builder exceptional(boolean exceptional) {
      this.exceptional = exceptional;
      return this;
    }

    /**
     * Sets whether this Validoctor will be meticulous or not. Defaults to false.
     * <li>If true, will put some additional information like actual value of patient object and examination duration
     * in {@link Ailment#getSpecs() specs of Ailments} returned in Diagnosis, in addition to rule's
     * {@link Rule#getParams() params}.</li>
     * <li>If false, will only put rule's {@link Rule#getParams() params} into
     * {@link Ailment#getSpecs() specs of Ailments}.</li>
     * @param meticulous
     * @return this Builder
     */
    public Builder meticulous(boolean meticulous) {
      this.meticulous = meticulous;
      return this;
    }

    public Validoctor build() {
      return new Validoctor(pedantic, exceptional, meticulous);
    }
  }

}
