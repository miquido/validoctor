package com.miquido.validoctor.reducerrule;

import com.miquido.validoctor.TestPatient;
import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.multirule.MultiRule;
import com.miquido.validoctor.rule.Rules;
import org.junit.Test;

import static com.miquido.validoctor.TestUtil.*;
import static com.miquido.validoctor.rule.Rules.*;
import static org.junit.Assert.*;

public class ReducerRuleTest {

  private static final Validoctor validoctor = Validoctor.builder().build();

  private ReducerRule<TestPatient, String> rule1 = ReducerRule.builder(TestPatient.class, String.class)
      .properties("phone", "name")
      .reducer(String::concat)
      .rule(stringMaxLength(20))
      .build();

  private ReducerRule<TestPatient, String> rule2 = ReducerRule.builder(TestPatient.class, String.class)
      .properties("phone", "name")
      .reducer(String::concat)
      .rule(stringMinLength(8))
      .nullIgnoring()
      .build();

  private ReducerRule<TestPatient, Long> rule3 = ReducerRule.builder(TestPatient.class, Long.class)
      .properties("id", "ordinal")
      .reducer(Long::sum)
      .rule(numberInRange(2, 8))
      .build();

  @Test
  public void reducerRulesForSameProperties() {
    TestPatient patient = new TestPatient(1L, "Name", "+48123", true);
    assertOk(validoctor.examine(patient, rule1, rule2));

    patient.setName("N");
    Diagnosis diagnosis = validoctor.examine(patient, rule1, rule2);
    assertError(diagnosis);
    assertEquals(2, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(stringMinLength(8), diagnosis, "name");
    assertOnlyViolationForProperty(stringMinLength(8), diagnosis, "phone");

    patient.setPhone("+48123456789123456789");
    diagnosis = validoctor.examine(patient, rule1, rule2);
    assertError(diagnosis);
    assertEquals(2, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(stringMaxLength(20), diagnosis, "name");
    assertOnlyViolationForProperty(stringMaxLength(20), diagnosis, "phone");
  }

  @Test
  public void reducerRulesForDifferentProperties() {
    TestPatient patient = new TestPatient(1L, "Name", "+481", 7L, true);
    assertOk(validoctor.examine(patient, rule2, rule3));

    patient.setOrdinal(8L);
    Diagnosis diagnosis = validoctor.examine(patient, rule2, rule3);
    assertError(diagnosis);
    assertEquals(2, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(numberInRange(2, 8), diagnosis, "id");
    assertOnlyViolationForProperty(numberInRange(2, 8), diagnosis, "ordinal");

    patient.setName("N");
    diagnosis = validoctor.examine(patient, rule2, rule3);
    assertError(diagnosis);
    assertEquals(4, diagnosis.getAilments().size());
  }

  @Test
  public void reducerRulesWithRule() {
    TestPatient patient = new TestPatient(1L, "Name", "+48123", 5L, true);
    assertOk(validoctor.examineCombo(patient, Rules.notNull(), rule3, rule2));

    patient = null;
    Diagnosis diagnosis = validoctor.examineCombo(patient, Rules.notNull(), rule3, rule2);
    assertError(diagnosis);
    assertEquals(1, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(Rules.notNull(), diagnosis, null);

    patient = new TestPatient(1L, "Name", "+48", 8L, true);
    diagnosis = validoctor.examineCombo(patient, Rules.notNull(), rule3, rule2);
    assertError(diagnosis);
    assertEquals(4, diagnosis.getAilments().size());
  }

  @Test
  public void multiRulesWithReducerRule() {
    TestPatient patient = new TestPatient(1L, "Name", "+48123", 5L, true);

    MultiRule<TestPatient> multiRule1 = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .addRules("name", stringMinLength(3), stringMaxLength(20))
        .addRules("phone", stringExactLength(6))
        .addRules("registered", notNull(), isTrue())
        .build();

    MultiRule<TestPatient> multiRule2 = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .addRulesForAll(Long.class, notNull(), numberNonNegative())
        .build();

    assertOk(validoctor.examineCombo(patient, rule1, multiRule1, multiRule2));

    patient = new TestPatient(1L, "Name Length 15 ", "+48123", 5L, true);
    Diagnosis diagnosis = validoctor.examineCombo(patient, rule1, multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(rule1, diagnosis, "phone");
    assertOnlyViolationForProperty(rule1, diagnosis, "name");

    patient = new TestPatient(1L, "Name", "+48123", -2L, true);
    diagnosis = validoctor.examineCombo(patient, rule1, multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(numberNonNegative(), diagnosis, "ordinal");
  }

  @Test
  public void reducerRuleWithNullValues() {
    TestPatient patient = new TestPatient(1L, "Name", null, 5L, true);
    assertError(validoctor.examine(patient, rule2)); //rule2 is nullIgnoring

    ReducerRule<TestPatient, String> nonNullIgnoringRule = //but with null handling reducer
        ReducerRule.builder(TestPatient.class, String.class)
            .properties("phone", "name")
            .reducer((s, s2) -> {
              if (s == null) return s2;
              else return s.concat(s2);
            })
            .rule(stringMinLength(8))
            .build();

    assertError(validoctor.examine(patient, nonNullIgnoringRule));

    patient.setName("Name8888");
    assertOk(validoctor.examine(patient, rule2));
    assertOk(validoctor.examine(patient, nonNullIgnoringRule));
  }

}
