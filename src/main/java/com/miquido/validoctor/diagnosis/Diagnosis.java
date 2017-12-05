package com.miquido.validoctor.diagnosis;


import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Complete result of examining a patient object, containing final result and all discovered {@link Ailment}s.
 * Ailments are contained in a map, grouped by properties they affect. Ailments affecting the object as a whole,
 * and not any of its properties, are mapped under null key.
 */
public class Diagnosis {

  private final Severity severity;
  private final Map<String, Set<Ailment>> ailments;


  public Diagnosis(Severity severity, Map<String, Set<Ailment>> ailments) {
    this.severity = severity;
    this.ailments = ailments;
  }

  public Severity getSeverity() {
    return severity;
  }

  public Map<String, Set<Ailment>> getAilments() {
    return ailments;
  }

  /**
   * Creates a new Diagnosis that is the sum of this Diagnosis and the specified one. New Diagnosis has the worse
   * (see {@link Severity#isWorseThan(Severity)}) severity of the two, and contains all the ailments of the two.
   * @param other diagnosis to sum with this one
   * @return new Diagnosis
   */
  public Diagnosis and(Diagnosis other) {
    Severity severity = getSeverity().isWorseThan(other.getSeverity()) ? getSeverity() : other.getSeverity();
    Map<String, Set<Ailment>> ailments = new HashMap<>(getAilments());
    other.getAilments().forEach((property, propertyAilments) -> ailments.computeIfAbsent(property, prop -> new HashSet<>()).addAll(propertyAilments));
    return new Diagnosis(severity, ailments);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Diagnosis diagnosis = (Diagnosis) o;

    if (severity != diagnosis.severity) return false;
    return ailments.equals(diagnosis.ailments);
  }

  @Override
  public int hashCode() {
    int result = severity.hashCode();
    result = 31 * result + ailments.hashCode();
    return result;
  }
}
