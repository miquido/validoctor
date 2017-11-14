package com.miquido.validoctor.multirule;

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
    TestPatient patient = new TestPatient();
    patient.setId(1L);
    patient.setName("Name");
    patient.setPhone("+48.123123123");
    patient.setRegistered(true);

    assertOk(validoctor.examine(patient, multiRule));

    patient = new TestPatient();
    patient.setName("Name");
    patient.setPhone("+48.123123");
    patient.setRegistered(true);

    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(notNull().getAilment().getName(), diagnosis.getAilments().get("id").iterator().next().getName());

    patient = new TestPatient();
    patient.setId(1L);
    patient.setName("Name");
    patient.setPhone("?48.123123123");
    patient.setRegistered(true);

    diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(PHONE_FORMAT.getAilment().getName(), diagnosis.getAilments().get("phone").iterator().next().getName());
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
    assertEquals(stringMinLength(3).getAilment().getName(), diagnosis.getAilments().get("name").iterator().next().getName());
    assertEquals(isTrue().getAilment().getName(), diagnosis.getAilments().get("registered").iterator().next().getName());
  }


  @Test
  public void sameForAll() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .withRulesForAll(String.class, stringMinLength(3), stringMaxLength(20))
        .withRulesForAll(boolean.class, notNull(), isTrue())
        .withRulesForAll(long.class, numberPositive())
        .build();

    TestPatient patient = new TestPatient();
    patient.setId(1L);
    patient.setName("Name");
    patient.setPhone("+48.123123123");
    patient.setRegistered(true);

    assertOk(validoctor.examine(patient, multiRule));

    patient.setPhone("12");

    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(1, diagnosis.getAilments().size());
    assertEquals(stringMinLength(3).getAilment().getName(), diagnosis.getAilments().get("phone").iterator().next().getName());
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

    TestPatient patient = new TestPatient();
    patient.setId(1L);
    patient.setName("Name10long");
    patient.setPhone("+48.123123123");
    patient.setRegistered(true);

    Diagnosis diagnosis1 = validoctor.examine(patient, multiRule1, multiRule2);
    assertOk(diagnosis1);

    patient.setName("name9long");

    Diagnosis diagnosis2 = validoctor.examine(patient, multiRule1, multiRule2);
    assertError(diagnosis2);
    assertEquals(1, diagnosis2.getAilments().size());
    assertEquals(stringExactLength(10).getAilment().getName(), diagnosis2.getAilments().get("name").iterator().next().getName());

    patient.setName("a");

    Diagnosis diagnosis3 = validoctor.examine(patient, multiRule1, multiRule2);
    assertError(diagnosis3);
    Set<Ailment> nameAilments = diagnosis3.getAilments().get("name");
    assertEquals(2, nameAilments.size());
    assertTrue(nameAilments.stream().anyMatch(ailment -> ailment.getName().equals(stringMinLength(3).getAilment().getName())));
    assertTrue(nameAilments.stream().anyMatch(ailment -> ailment.getName().equals(stringExactLength(10).getAilment().getName())));
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

    TestPatient patient = new TestPatient();
    patient.setId(1L);
    patient.setName("Name10long");
    patient.setPhone("+48.123123123");
    patient.setRegistered(true);

    Diagnosis diagnosis1 = validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2);
    assertOk(diagnosis1);

    SimpleRule<TestPatient> rule1 = new SimpleRule<>("IS_REGISTERED", TestPatient::isRegistered);
    Diagnosis diagnosis2 = validoctor.examine(patient, MultiRule.of(notNull(), rule1), multiRule1, multiRule2);
    assertOk(diagnosis2);

    patient.setRegistered(false);
    Diagnosis diagnosis3 = validoctor.examine(patient, MultiRule.of(notNull(), rule1), multiRule1, multiRule2);
    assertError(diagnosis3);
    Set<Ailment> objectAilments = diagnosis3.getAilments().get(null);
    assertEquals(2, diagnosis3.getAilments().size());
    assertEquals(1, objectAilments.size());
    assertTrue(objectAilments.stream().anyMatch(ailment -> ailment.getName().equals(rule1.getAilment().getName())));
  }
}
