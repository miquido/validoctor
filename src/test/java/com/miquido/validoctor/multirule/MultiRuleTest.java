package com.miquido.validoctor.multirule;

import com.miquido.validoctor.TestPatient;
import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.rule.Rule;
import com.miquido.validoctor.rule.SimpleRule;
import org.junit.Test;

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
        .addRules("id", TestPatient::getId, notNull(), numberNonNegative())
        .addRules("name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .addRules("phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .addRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();

    testNonConditionalMultiRule(multiRule);
  }

  @Test
  public void nonConditional_reflexive() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .addRules("id", notNull(), numberNonNegative())
        .addRules("name", stringMinLength(3), stringMaxLength(20))
        .addRules("phone", stringExactLength(13), PHONE_FORMAT)
        .addRules("registered", notNull(), isTrue())
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
        .addRules(TestPatient::isIdSet, "id", TestPatient::getId, notNull(), numberNonNegative())
        .addRules(TestPatient::isNameSet, "name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .addRules(TestPatient::isPhoneSet, "phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .addRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();

    testConditionalMultiRule(multiRule);
  }

  @Test
  public void conditional_reflexive() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .addRules(TestPatient::isIdSet, "id", notNull(), numberNonNegative(), numberPositive())
        .addRules(TestPatient::isNameSet, "name", stringMinLength(3), stringMaxLength(20))
        .addRules(TestPatient::isPhoneSet, "phone", stringExactLength(13), PHONE_FORMAT)
        .addRules("registered", notNull(), isTrue())
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
  public void sameForAll_primitives() {
    MultiRule<TestPatient> multiRule = MultiRule.<TestPatient>builder().reflexiveProperties(TestPatient.class)
        .addRulesForAll(String.class, stringMinLength(3), stringMaxLength(20))
        .addRulesForAll(boolean.class, notNull(), isTrue())
        .addRulesForAll(long.class, numberPositive())
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
  public void sameForAll_complex() {
    MultiRule<Base1> base1MultiRule = MultiRule.<Base1>builder()
        .reflexiveProperties(Base1.class)
        .addRules("something", stringAlphabetic())
        .build();

    MultiRule<InheritanceTestPatient> multiRule = MultiRule.<InheritanceTestPatient>builder()
        .reflexiveProperties(InheritanceTestPatient.class)
        .addRulesForAll(Base1.class, base1MultiRule)
        .build();

    InheritanceTestPatient patient = new InheritanceTestPatient(new Inherited1("1"), new Inherited2("2"), new Base1("c"));
    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(2, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(stringAlphabetic(), diagnosis, "inherited1.something");
    assertOnlyViolationForProperty(stringAlphabetic(), diagnosis, "inherited2.something");
  }

  @Test
  public void sameForAll_strictClassMatch() {
    MultiRule<Base1> base1MultiRule = MultiRule.<Base1>builder()
        .reflexiveProperties(Base1.class)
        .addRules("something", stringAlphabetic())
        .build();

    MultiRule<InheritanceTestPatient> multiRule = MultiRule.<InheritanceTestPatient>builder()
        .reflexiveProperties(InheritanceTestPatient.class)
        .addRulesForAll(Inherited1.class, true, base1MultiRule)
        .build();

    InheritanceTestPatient patient = new InheritanceTestPatient(new Inherited1("1"), new Inherited2("2"), new Base1("c"));
    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(1, diagnosis.getAilments().size());
    assertOnlyViolationForProperty(stringAlphabetic(), diagnosis, "inherited1.something");
  }

  @Test
  public void multipleMultiRules() {
    MultiRule<TestPatient> multiRule1 = MultiRule.<TestPatient>builder()
        .addRules("name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .addRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();
    MultiRule<TestPatient> multiRule2 = MultiRule.<TestPatient>builder()
        .addRules("phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .addRules("name", TestPatient::getName, stringExactLength(10))
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
        .addRules("name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .addRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();
    MultiRule<TestPatient> multiRule2 = MultiRule.<TestPatient>builder()
        .addRules("phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .addRules("name", TestPatient::getName, stringExactLength(10))
        .build();

    TestPatient patient = new TestPatient(1L, "Name10long", "+48.123123123", true);
    Diagnosis diagnosis = validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2);
    assertOk(diagnosis);
    diagnosis = validoctor.examineChain(patient, notNull(), multiRule1, multiRule2);
    assertOk(diagnosis);
    diagnosis = validoctor.examineJoin(patient, notNull(), multiRule1, multiRule2);
    assertOk(diagnosis);

    patient = new TestPatient(1L, "Name9long", "+48.123123123", true);
    diagnosis = validoctor.examineCombo(patient, notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(stringExactLength(10), diagnosis, "name");
    diagnosis = validoctor.examineChain(patient, notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(stringExactLength(10), diagnosis, "name");
    diagnosis = validoctor.examineJoin(patient, notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(stringExactLength(10), diagnosis, "name");

    patient = new TestPatient(1L, "ab", "+48.123123123", true);
    diagnosis = validoctor.examineJoin(patient, notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertPropertyViolates(stringMinLength(3), diagnosis, "name");
    assertPropertyViolates(stringExactLength(10), diagnosis, "name");

    patient = null;
    diagnosis = validoctor.examineCombo(patient, "object", notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(notNull(), diagnosis, "object");
    diagnosis = validoctor.examineChain(patient, "object", notNull(), multiRule1, multiRule2);
    assertError(diagnosis);
    assertOnlyViolationForProperty(notNull(), diagnosis, "object");
  }

  @Test
  public void nestedMultirules() {
    Tier4 tier4 = new Tier4("something", 1);
    Tier3 tier3 = new Tier3(tier4, "ahoo");
    Tier2 tier2 = new Tier2(tier3, 2);
    Tier1 patient = new Tier1(tier2, true);

    MultiRule<Tier4> tier4Rule = MultiRule.<Tier4>builder()
        .reflexiveProperties(Tier4.class)
        .addRules("something", stringAlphabetic())
        .addRules("number", numberPositive())
        .build();

    MultiRule<Tier3> tier3Rule = MultiRule.<Tier3>builder()
        .reflexiveProperties(Tier3.class)
        .addMultiRule("tier4", tier4Rule)
        .addRules("ahoo", stringAlphanumeric())
        .build();

    MultiRule<Tier2> tier2Rule = MultiRule.<Tier2>builder()
        .reflexiveProperties(Tier2.class)
        .addMultiRule("tier3", tier3Rule)
        .addRules("count", numberNonNegative())
        .build();

    MultiRule<Tier1> tier1Rule = MultiRule.<Tier1>builder()
        .reflexiveProperties(Tier1.class)
        .addMultiRule("tier2", tier2Rule)
        .addRules("bleh", isTrue())
        .build();

    assertOk(validoctor.examine(patient, tier1Rule));

    tier4.setSomething("1234");
    Diagnosis diagnosis = validoctor.examine(patient, tier1Rule);
    assertError(diagnosis);
    assertOnlyViolationForProperty(stringAlphabetic(), diagnosis, "tier2.tier3.tier4.something");

    tier4.setSomething("something");
    tier3.setAhoo("#$%$");
    diagnosis = validoctor.examine(patient, tier1Rule);
    assertError(diagnosis);
    assertOnlyViolationForProperty(stringAlphanumeric(), diagnosis, "tier2.tier3.ahoo");

    tier3.setAhoo("ahoo");
    tier2.setCount(-2);
    diagnosis = validoctor.examine(patient, tier1Rule);
    assertError(diagnosis);
    assertOnlyViolationForProperty(numberNonNegative(), diagnosis, "tier2.count");
  }
}
