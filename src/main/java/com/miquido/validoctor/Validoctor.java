package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.diagnosis.DiagnosisException;
import com.miquido.validoctor.multirule.MultiRule;
import com.miquido.validoctor.rule.PropertyRule;
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

  private Validoctor(boolean pedantic, boolean exceptional) {
    this.pedantic = pedantic;
    this.exceptional = exceptional;
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
   * @param rules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, MultiRule<T>... rules) {
    return examine(patient, Stream.of(rules)
        .reduce(MultiRule::and)
        .orElseThrow(() -> new RuntimeException("Never happens")));
  }

  /**
   * Multi property object examination. Convenience method same as:<br/>
   * {@code examine(patient, MultiRule.of(rule), multiRules)}
   * @param patient object to validate
   * @param rule validation rule applied to patient object as a whole
   * @param multiRules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examineCombo(T patient, Rule<T> rule, MultiRule<T>... multiRules) {
    return examine(patient, MultiRule.of(rule).and(Stream.of(multiRules)
        .reduce(MultiRule::and)
        .orElseThrow(() -> new RuntimeException("Never happens"))));
  }

  /**
   * Multi property object examination.
   * @param patient object to validate
   * @param rules validation MultiRules for properties of patient
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  public final <T> Diagnosis examine(T patient, MultiRule<T> rules) {
    Severity severity = Severity.OK;
    Map<String, Set<Ailment>> ailments = new HashMap<>();
    for (PropertyRule<T> rule : rules) {
      boolean valid = rule.test(patient);
      if (!valid) {
        Ailment ailment = rule.getAilment();
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
      throw new DiagnosisException(diagnosis);
    }
    return diagnosis;
  }


  public static final class Builder {

    private boolean pedantic = true;
    private boolean exceptional = false;

    public Builder pedantic(boolean pedantic) {
      this.pedantic = pedantic;
      return this;
    }

    public Builder exceptional(boolean exceptional) {
      this.exceptional = exceptional;
      return this;
    }

    public Validoctor build() {
      return new Validoctor(pedantic, exceptional);
    }
  }

}
