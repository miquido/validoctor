package com.miquido.validoctor.ailment;

public enum Severity {
  OK, WARN, ERROR;

  /**
   * @param other constant to compare this with
   * @return the more severe constant, for example (OK, WARN) returns WARN and (WARN, ERROR) returns ERROR
   */
  public boolean isWorseThan(Severity other) {
    return ordinal() > other.ordinal();
  }
}
