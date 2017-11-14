package com.miquido.validoctor.ailment;


import java.util.Map;

/**
 * Class describing a validation error.
 */
public class Ailment {

  private final String name;
  private final Map<String, String> parameters;
  private final Severity severity;


  /**
   * @param name name of the Ailment to be used in {@link com.miquido.validoctor.diagnosis.Diagnosis}
   * @param parameters optional map of custom parameters that can be used to more precisely describe an Ailment
   * @param severity severity of Ailment
   */
  public Ailment(String name, Map<String, String> parameters, Severity severity) {
    this.name = name;
    this.parameters = parameters;
    this.severity = severity;
  }

  public boolean isMoreSevereThan(Ailment other) {
    return severity.isWorseThan(other.getSeverity());
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public Severity getSeverity() {
    return severity;
  }
}
