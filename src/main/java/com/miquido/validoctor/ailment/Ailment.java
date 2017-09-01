package com.miquido.validoctor.ailment;


import lombok.Data;

import java.util.Map;

/**
 * Class describing a validation error.
 */
@Data
public class Ailment {

  private final String name;
  private final Map<String, String> parameters;
  private final Severity severity;


  public boolean isMoreSevereThan(Ailment other) {
    return severity.isWorseThan(other.getSeverity());
  }

}
