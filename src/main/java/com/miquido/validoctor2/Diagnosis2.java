package com.miquido.validoctor2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Diagnosis2 {
  private boolean valid = true;
  private Map<String, Set<String>> ailments = new HashMap<>();

  public Diagnosis2(Set<Ailment2> results) {
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
