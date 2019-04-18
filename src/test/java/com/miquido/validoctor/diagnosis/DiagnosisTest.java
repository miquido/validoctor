package com.miquido.validoctor.diagnosis;

import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.rule.Rules;
import org.junit.Test;

import static com.miquido.validoctor.ailment.Ailment.*;
import static org.junit.Assert.*;

public class DiagnosisTest {

  private static final Validoctor validoctor = Validoctor.builder().build();

  @Test
  public void sumErrorDiagnoses() {
    Diagnosis d1 = validoctor.examine("test", Rules.stringMinLength(5));
    Diagnosis d2 = validoctor.examine("test", Rules.stringMaxLength(3));
    Diagnosis d3 = d1.and(d2);
    System.out.println("sumErrorDiagnoses: resulting diagnosis: " + d3.toString());
    assertEquals(Severity.ERROR, d3.getSeverity());
    assertEquals(2, d3.getAilments().get("OBJECT").size());
  }

  @Test
  public void sumErrorAndOkDiagnoses() {
    Diagnosis d1 = validoctor.examine("test", "a", Rules.stringMinLength(5));
    Diagnosis d2 = validoctor.examine("test", "b", Rules.stringMaxLength(5));
    Diagnosis d3 = d1.and(d2);
    System.out.println("sumErrorAndOkDiagnoses: resulting diagnosis: " + d3.toString());
    assertEquals(Severity.ERROR, d3.getSeverity());
    assertEquals(1, d3.getAilments().get("a").size());
  }

  @Test
  public void equals_sameDiagnosis() {
    Diagnosis d1 = validoctor.examine("test", Rules.stringMinLength(5));
    Diagnosis d2 = validoctor.examine("test", Rules.stringMinLength(5));
    assertEquals(d1, d2);
  }

  @Test
  public void equals_differentSeverityDiagnosis() {
    Diagnosis d1 = validoctor.examine("test", Rules.stringMinLength(5));
    Diagnosis d2 = validoctor.examine("test", Rules.stringMaxLength(5));
    assertNotEquals(d1, d2);
  }

  @Test
  public void equals_differentAilmentDiagnosis() {
    Diagnosis d1 = validoctor.examine("test", Rules.stringMinLength(5));
    Diagnosis d2 = validoctor.examine("test", Rules.stringMaxLength(3));
    assertNotEquals(d1, d2);
  }

  @Test
  public void equals_differenAilmentSpecDiagnosis() {
    Diagnosis d1 = validoctor.examine("test", Rules.stringMaxLength(2));
    Diagnosis d2 = validoctor.examine("test", Rules.stringMaxLength(3));
    assertNotEquals(d1, d2);
  }

}
