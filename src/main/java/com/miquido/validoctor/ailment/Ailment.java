package com.miquido.validoctor.ailment;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class describing a validation error.
 */
public class Ailment {

  private static final int DEFAULT_SPECS_MAP_SIZE = 4;


  private final String name;
  @Deprecated
  private final Map<String, String> parameters;
  private final Map<String, Object> specs;
  private final Severity severity;


  /**
   * DEPRECATED. Use {@link Ailment#Ailment(String, Severity)} or {@link Ailment#Ailment(String, Severity, Map)} instead.
   * @param name name of the Ailment to be used in {@link com.miquido.validoctor.diagnosis.Diagnosis}
   * @param parameters optional map of custom parameters that can be used to more precisely describe an Ailment
   * @param severity severity of Ailment
   */
  @Deprecated
  public Ailment(String name, Map<String, String> parameters, Severity severity) {
    this.name = name;
    this.parameters = parameters;
    this.severity = severity;
    specs = new HashMap<>(DEFAULT_SPECS_MAP_SIZE);
  }

  /**
   * @param name name of the Ailment to be used in {@link com.miquido.validoctor.diagnosis.Diagnosis}
   * @param severity severity of Ailment
   */
  public Ailment(String name, Severity severity) {
    this.name = name;
    this.severity = severity;
    parameters = new HashMap<>(DEFAULT_SPECS_MAP_SIZE);
    specs = new HashMap<>(DEFAULT_SPECS_MAP_SIZE);
  }

  /**
   * @param name name of the Ailment to be used in {@link com.miquido.validoctor.diagnosis.Diagnosis}
   * @param severity severity of Ailment
   * @param specs entries that will be copied into this Ailment's {@link Ailment#getSpecs() specs}
   */
  public Ailment(String name, Severity severity, Map<String, Object> specs) {
    this.name = name;
    this.severity = severity;
    parameters = new HashMap<>(DEFAULT_SPECS_MAP_SIZE);
    this.specs = new HashMap<>(specs);
  }

  public boolean isMoreSevereThan(Ailment other) {
    return severity.isWorseThan(other.getSeverity());
  }

  public String getName() {
    return name;
  }

  /**
   * DEPRECATED. Use {@link Ailment#getSpecs()} instead.
   * @return map of optional custom String to String parameters that may describe this Ailment more precisely
   */
  @Deprecated
  public Map<String, String> getParameters() {
    return parameters;
  }

  /**
   * @return map of this Ailment details.
   */
  public Map<String, Object> getSpecs() {
    return specs;
  }

  public Severity getSeverity() {
    return severity;
  }


  @Override
  public String toString() {
    return "{ name: " + name + ", severity: " + severity + ", specs: " + specs + " }" ;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Ailment)) return false;
    Ailment ailment = (Ailment) o;
    return Objects.equals(name, ailment.name) &&
        Objects.equals(parameters, ailment.parameters) &&
        Objects.equals(specs, ailment.specs) &&
        severity == ailment.severity;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, parameters, specs, severity);
  }
}
