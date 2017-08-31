package com.miquido.validoctor.ailment;

public enum Severity {
  OK, WARN, ERROR;

  public boolean isWorseThan(Severity other) {
    return ordinal() > other.ordinal();
  }
}
