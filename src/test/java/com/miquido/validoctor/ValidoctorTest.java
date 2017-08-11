package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.diagnosis.DiagnosisException;
import com.miquido.validoctor.rule.Rules;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidoctorTest {

  @Test
  public void nonExceptionalValidoctor_notThrows() {
    Validoctor validoctor = Validoctor.builder().exceptional(false).build();
    Diagnosis diagnosis = null;
    try {
      diagnosis = validoctor.examine("", Rules.stringNotEmpty());
    } catch (DiagnosisException e) {
      fail("Exception thrown from non-exceptional validoctor running a non-exceptional rule");
    }
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(1, diagnosis.getAilments().size());
  }

  @Test
  public void exceptionalValidoctor_throws() {
    Validoctor validoctor = Validoctor.builder().exceptional(true).build();
    try {
      validoctor.examine("", Rules.stringNotEmpty());
    } catch (DiagnosisException e) {
      assertEquals(1, e.getDiagnosis().getAilments().size());
      return;
    }
    fail("Exception not thrown from exceptional validoctor");
  }

  @Test
  public void pedanticValidoctor_testsAll() {
    Validoctor validoctor = Validoctor.builder().pedantic(true).build();
    Diagnosis diagnosis = validoctor.examine(-5, Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(2, diagnosis.getAilments().size());
  }

  @Test
  public void nonPedanticValidoctor_stopsOnFirstFail() {
    Validoctor validoctor = Validoctor.builder().pedantic(false).build();
    Diagnosis diagnosis = validoctor.examine(-5, Rules.numberNonNegative(), Rules.numberInRange(0, 5));
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
    assertEquals(1, diagnosis.getAilments().size());
  }


}
