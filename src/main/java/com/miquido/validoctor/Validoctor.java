package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.DiagnosisException;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.multirule.MultiRule;
import com.miquido.validoctor.rule.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    Severity severity = Severity.OK;
    Set<Ailment> ailments = new HashSet<>(rules.length);
    for (Rule<T> rule : rules) {
      boolean valid = rule.test(patient);
      if (!valid) {
        Ailment ailment = rule.getAilment();
        if (ailment.getSeverity().isWorseThan(severity)) {
          severity = ailment.getSeverity();
        }
        ailments.add(ailment);
        if (!pedantic) {
          break;
        }
      }
    }
    Diagnosis diagnosis = new Diagnosis(severity, ailments);
    if (exceptional && severity == Severity.ERROR) {
      throw new DiagnosisException(diagnosis);
    }
    return diagnosis;
  }

  /**
   * Object examination with {@link MultiRule}s.
   * @param patient object to validate
   * @param rules validation MultiRules
   * @return diagnosis detailing validity of patient
   * @throws DiagnosisException if this Validoctor is exceptional and resulting diagnosis is {@link Severity#ERROR}
   */
  @SafeVarargs
  public final <T> Diagnosis examine(T patient, MultiRule<T>... rules) {
    Severity severity = Severity.OK;
    Map<String, Set<Ailment>> ailments = new HashMap<>(rules.length);
    for (MultiRule<T> rule : rules) {
      Map<String, Set<Ailment>> ruleAilments = rule.test(patient);
      if (!ruleAilments.isEmpty()) {
        Severity ailmentSeverity = ruleAilments.values().stream()
            .flatMap(Set::stream)
            .reduce((a1, a2) -> a2.isMoreSevereThan(a1) ? a2 : a1)
            .map(Ailment::getSeverity)
            .orElse(Severity.OK);
        if (ailmentSeverity.isWorseThan(severity)) {
          severity = ailmentSeverity;
        }
        ailments.putAll(ruleAilments);
        if (!pedantic) {
          break;
        }
      }
    }
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
