package com.miquido.validoctor.diagnosis;


import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * Complete result of examining a patient object, containing final result and all discovered {@link Ailment}s.
 * Ailments are contained in a map, grouped by properties they affect. Ailments affecting the object as a whole,
 * and not any of its properties, are mapped under null key.
 */
@Data
public class Diagnosis {

  private final Severity severity;
  private final Map<String, Set<Ailment>> ailments;


  public Diagnosis(Severity severity, Map<String, Set<Ailment>> ailments) {
    this.severity = severity;
    this.ailments = ailments;
  }

}
