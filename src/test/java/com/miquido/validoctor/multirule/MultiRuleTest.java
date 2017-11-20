package com.miquido.validoctor.multirule;

import com.miquido.validoctor.TestPatient;
import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.rule.Rule;
import com.miquido.validoctor.rule.SimpleRule;
import org.junit.Test;

import java.util.Set;
import java.util.regex.Pattern;

import static com.miquido.validoctor.TestUtil.*;
import static com.miquido.validoctor.rule.Rules.*;
import static org.junit.Assert.*;

public class MultiRuleTest {

  private static final Validoctor validoctor = Validoctor.builder().build();

  private static final Pattern PHONE_PATTERN = Pattern.compile("\\+[0-9]{1,3}\\.[0-9]+");
  private static Rule<String> PHONE_FORMAT =
      new SimpleRule<>("PHONE_FORMAT",
          phone -> phone == null || PHONE_PATTERN.matcher(phone).matches()
      );


  @Test
  public void nonConditional_nonReflexive() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder()
        .withRules("id", TestPatient::getId, notNull(), numberNonNegative())
        .withRules("name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .withRules("phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .withRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();

    testNonConditionalMultiRule(multiRule);
  }

  @Test
  public void nonConditional_reflexive() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .withRules("id", notNull(), numberNonNegative())
        .withRules("name", stringMinLength(3), stringMaxLength(20))
        .withRules("phone", stringExactLength(13), PHONE_FORMAT)
        .withRules("registered", notNull(), isTrue())
        .build();

    testNonConditionalMultiRule(multiRule);
  }

  private void testNonConditionalMultiRule(MultiRule<TestPatient> multiRule) {
    TestPatient patient = new TestPatient(1L, "Name", "+48.123123123", true);

    assertOk(validoctor.examine(patient, multiRule));

    patient.setPhone("?48.123123123");
    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertOnlyViolationForProperty(PHONE_FORMAT, diagnosis, "phone");

    patient = new TestPatient();
    patient.setName("Name");
    patient.setPhone("+48.123123");
    patient.setRegistered(true);

    diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertOnlyViolationForProperty(notNull(), diagnosis, "id");
  }


  @Test
  public void conditional() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder()
        .withConditionalRules(TestPatient::isIdSet, "id", TestPatient::getId, notNull(), numberNonNegative())
        .withConditionalRules(TestPatient::isNameSet, "name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .withConditionalRules(TestPatient::isPhoneSet, "phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .withRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();

    testConditionalMultiRule(multiRule);
  }

  @Test
  public void conditional_reflexive() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .withConditionalRules(TestPatient::isIdSet, "id", notNull(), numberNonNegative(), numberPositive())
        .withConditionalRules(TestPatient::isNameSet, "name", stringMinLength(3), stringMaxLength(20))
        .withConditionalRules(TestPatient::isPhoneSet, "phone", stringExactLength(13), PHONE_FORMAT)
        .withRules("registered", notNull(), isTrue())
        .build();

    testConditionalMultiRule(multiRule);
  }

  private void testConditionalMultiRule(MultiRule<TestPatient> multiRule) {
    TestPatient patient = new TestPatient();
    patient.setName("Name");
    patient.setPhone("+48.123123123");
    patient.setRegistered(true);

    assertOk(validoctor.examine(patient, multiRule));

    patient = new TestPatient();
    patient.setRegistered(true);

    assertOk(validoctor.examine(patient, multiRule));

    patient = new TestPatient();
    patient.setName("aa");

    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(2, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(stringMinLength(3), diagnosis, "name");
    assertOnlyViolationForProperty(isTrue(), diagnosis, "registered");
  }


  @Test
  public void sameForAll() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .withRulesForAll(String.class, stringMinLength(3), stringMaxLength(20))
        .withRulesForAll(boolean.class, notNull(), isTrue())
        .withRulesForAll(long.class, numberPositive())
        .build();

    TestPatient patient = new TestPatient(1L, "Name", "+48.123123123", 1L, true);

    assertOk(validoctor.examine(patient, multiRule));

    patient.setPhone("12");

    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(1, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(stringMinLength(3), diagnosis, "phone");
  }

  @Test
  public void multipleMultiRules() {
    MultiRule<TestPatient> multiRule1 = MultiRule.<TestPatient>builder()
        .withRules("name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .withRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();
    MultiRule<TestPatient> multiRule2 = MultiRule.<TestPatient>builder()
        .withRules("phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .withRules("name", TestPatient::getName, stringExactLength(10))
        .build();

    TestPatient patient = new TestPatient(1L, "Name10long", "+48.123123123", true);

    Diagnosis diagnosis1 = validoctor.examine(patient, multiRule1, multiRule2);
    assertOk(diagnosis1);

    patient.setName("name9long");

    Diagnosis diagnosis2 = validoctor.examine(patient, multiRule1, multiRule2);
    assertError(diagnosis2);
    assertEquals(1, diagnosis2.getAilments().size());
    assertOnlyViolationForProperty(stringExactLength(10), diagnosis2, "name");

    patient.setName("a");

    Diagnosis diagnosis3 = validoctor.examine(patient, multiRule1, multiRule2);
    assertError(diagnosis3);
    assertEquals(2, diagnosis3.getAilments().get("name").size());
    assertPropertyViolates(stringMinLength(3), diagnosis3, "name");
    assertPropertyViolates(stringExactLength(10), diagnosis3, "name");
  }

  @Test
  public void multiRulesAndSimpleRules() {
    MultiRule<TestPatient> multiRule1 = MultiRule.<TestPatient>builder()
        .withRules("name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .withRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();
    MultiRule<TestPatient> multiRule2 = MultiRule.<TestPatient>builder()
        .withRules("phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .withRules("name", TestPatient::getName, stringExactLength(10))
        .build();

    TestPatient patient = new TestPatient(1L, "Name10long", "+48.123123123", true);
    Diagnosis diagnosis = validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2);
    assertOk(diagnosis);

    patient = new TestPatient(1L, "Name9long", "+48.123123123", true);
    diagnosis = validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(stringExactLength(10), diagnosis, "name");

    patient = null;
    diagnosis = validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(notNull(), diagnosis, null);
  }
}
