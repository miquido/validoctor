package com.miquido.validoctor.result;

public class DiagnosisException extends RuntimeException {
  private final Diagnosis diagnosis;

  public DiagnosisException(Diagnosis diagnosis) {
    this.diagnosis = diagnosis;
  }

  public Diagnosis getDiagnosis() {
    return diagnosis;
  }
}
