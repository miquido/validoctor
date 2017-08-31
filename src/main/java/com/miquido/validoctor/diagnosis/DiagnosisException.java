package com.miquido.validoctor.diagnosis;

import lombok.Getter;

public class DiagnosisException extends RuntimeException {

  @Getter
  private final Diagnosis diagnosis;

  public DiagnosisException(Diagnosis diagnosis) {
    this.diagnosis = diagnosis;
  }
}
