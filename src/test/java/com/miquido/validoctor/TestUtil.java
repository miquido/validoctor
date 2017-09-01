package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;

import static org.junit.Assert.assertEquals;

public class TestUtil {

  public static void assertError(Diagnosis diagnosis) {
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
  }

  public static void assertOk(Diagnosis diagnosis) {
    assertEquals(Severity.OK, diagnosis.getSeverity());
  }
}
