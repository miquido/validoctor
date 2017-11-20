package com.miquido.validoctor.diagnosis;

import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.rule.Rules;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiagnosisTest {

  private static final Validoctor validoctor = Validoctor.builder().build();

  @Test
  public void sumErrorDiagnoses() {
    Diagnosis d1 = validoctor.examine("test", Rules.stringMinLength(5));
    Diagnosis d2 = validoctor.examine("test", Rules.stringMaxLength(3));
    Diagnosis d3 = d1.and(d2);
    assertEquals(Severity.ERROR, d3.getSeverity());
    assertEquals(2, d3.getAilments().get(null).size());
  }

  @Test
  public void sumErrorAndOkDiagnoses() {
    Diagnosis d1 = validoctor.examine("test", Rules.stringMinLength(5));
    Diagnosis d2 = validoctor.examine("test", Rules.stringMaxLength(5));
    Diagnosis d3 = d1.and(d2);
    assertEquals(Severity.ERROR, d3.getSeverity());
    assertEquals(1, d3.getAilments().get(null).size());
  }

}
