package com.miquido.validoctor2.result;

public class DiagnosisException2 extends RuntimeException {
  private final Diagnosis2 diagnosis;

  public DiagnosisException2(Diagnosis2 diagnosis) {
    this.diagnosis = diagnosis;
  }
}
