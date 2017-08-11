package com.miquido.validoctor.diagnosis;


import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Complete result of examining a patient object, containing final result and all discovered {@link Ailment}s.
 */
@Data
public class Diagnosis {

  private static final String NO_FIELD = ">";

  private final Severity severity;
  private final Map<String, List<Ailment>> fieldAilments;


  public Diagnosis(Severity severity, List<Ailment> ailments) {
    this.severity = severity;
    this.fieldAilments = Collections.singletonMap(NO_FIELD, ailments);
  }

  public Diagnosis(Severity severity, Map<String, List<Ailment>> ailments) {
    this.severity = severity;
    this.fieldAilments = ailments;
  }

  /**
   * @return if this is a result of single value validation, list of its {@link Ailment}s.
   *         Otherwise, result value of {@link Diagnosis#getFieldAilments()} flattened to single list, with field name
   *         prepended to each Ailment's name.
   */
  public List<Ailment> getAilments() {
    if (fieldAilments.isEmpty()) {
      return Collections.emptyList();
    }
    if (fieldAilments.size() == 1 && fieldAilments.containsKey(NO_FIELD)) {
      return fieldAilments.get(NO_FIELD);
    }
    return fieldAilments.entrySet().stream().map(entry -> {
      String field = entry.getKey();
      List<Ailment> ailments = entry.getValue();
      return ailments.stream()
          .map(ailment -> new Ailment(field + ">" + ailment.getName(), ailment.getParameters(), ailment.getSeverity()))
          .collect(Collectors.toList());
    }).reduce(new ArrayList<>(), (summedAilments, ailments) -> {
      summedAilments.addAll(ailments);
      return summedAilments;
    });
  }

}
