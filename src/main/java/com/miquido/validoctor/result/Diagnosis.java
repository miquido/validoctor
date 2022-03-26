package com.miquido.validoctor.result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Diagnosis {
  private boolean valid = true;
  private Map<String, Set<String>> ailments = new HashMap<>();

  public Diagnosis(Set<Ailment> results) {
    results.forEach(result -> {
      Set<String> resultAilments = result.ailments;
      if (!resultAilments.isEmpty()) {
        valid = false;
        Set<String> ailmentsToAdd = ailments.computeIfAbsent(result.field, key -> new HashSet<>());
        ailmentsToAdd.addAll(resultAilments);
      }
    });
  }

  public boolean isValid() {
    return valid;
  }

  /**
   * @return map of field names to sets of ailment (rule violation) names found in them
   */
  public Map<String, Set<String>> getAilments() {
    return ailments;
  }

  @Override
  public String toString() {
    return "Diagnosis2{" +
        "valid=" + valid +
        ", ailments=" + ailments +
        '}';
  }
}
