package com.miquido.validoctor;

import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.rule.Rule;

import static org.junit.Assert.*;

public class TestUtil {

  public static void assertError(Diagnosis diagnosis) {
    assertEquals(Severity.ERROR, diagnosis.getSeverity());
  }

  public static void assertOk(Diagnosis diagnosis) {
    assertEquals(Severity.OK, diagnosis.getSeverity());
  }

  public static void assertWarn(Diagnosis diagnosis) {
    assertEquals(Severity.WARN, diagnosis.getSeverity());
  }

  public static void assertOnlyViolationForProperty(Rule rule, Diagnosis diagnosis, String property) {
    assertEquals(1, diagnosis.getAilments().get(property).size());
    assertEquals(rule.getAilment().getName(), diagnosis.getAilments().get(property).iterator().next().getName());
  }

  public static void assertPropertyViolates(Rule rule, Diagnosis diagnosis, String property) {
    assertTrue(diagnosis.getAilments().get(property).stream().anyMatch(ailment -> ailment.getName().equals(rule.getAilment().getName())));
  }

}
