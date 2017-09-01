package com.miquido.validoctor.multirule;

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
  public void nonReflexive_nonConditional() {
    MultiRule<TestPatient> multiRule = MultiRuleBuilder.forClass(TestPatient.class)
        .withRules("id", TestPatient::getId, notNull(), numberNonNegative())
        .withRules("name", TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .withRules("phone", TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .withRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();

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
    assertEquals(notNull().getAilment().getName(), diagnosis.getFieldAilments().get("id").iterator().next().getName());

    patient = new TestPatient();
    patient.setId(1L);
    patient.setName("Name");
    patient.setPhone("?48.123123123");
    patient.setRegistered(true);

    diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(PHONE_FORMAT.getAilment().getName(), diagnosis.getFieldAilments().get("phone").iterator().next().getName());
  }

  @Test
  public void nonReflexive_conditional() {
    MultiRule<TestPatient> multiRule = MultiRuleBuilder.forClass(TestPatient.class)
        .withConditionalRules("id", TestPatient::isIdSet, TestPatient::getId, notNull(), numberNonNegative())
        .withConditionalRules("name", TestPatient::isNameSet, TestPatient::getName, stringMinLength(3), stringMaxLength(20))
        .withConditionalRules("phone", TestPatient::isPhoneSet, TestPatient::getPhone, stringExactLength(13), PHONE_FORMAT)
        .withRules("registered", TestPatient::isRegistered, notNull(), isTrue())
        .build();

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
    assertEquals(stringMinLength(3).getAilment().getName(), diagnosis.getFieldAilments().get("name").iterator().next().getName());
    assertEquals(isTrue().getAilment().getName(), diagnosis.getFieldAilments().get("registered").iterator().next().getName());
  }

  @Test
  public void reflexive_conditional() {
    MultiRule<TestPatient> multiRule = MultiRuleBuilder.forClass(TestPatient.class).reflexiveProperties()
        .withIsSetDependentRules("id", notNull(), numberNonNegative())
        .withIsSetDependentRules("name", stringMinLength(3), stringMaxLength(20))
        .withIsSetDependentRules("phone", stringExactLength(13), PHONE_FORMAT)
        .withRules("registered", notNull(), isTrue())
        .build();

    TestPatient patient = new TestPatient();
    patient.setId(1L);
    patient.setName("Name");
    patient.setPhone("+48.123123123");
    patient.setRegistered(true);

    assertOk(validoctor.examine(patient, multiRule));

    patient = new TestPatient();
    patient.setRegistered(true);

    assertOk(validoctor.examine(patient, multiRule));

    patient = new TestPatient();
    patient.setId(-1L);
    patient.setRegistered(true);

    Diagnosis diagnosis = validoctor.examine(patient, multiRule);
    assertError(diagnosis);
    assertEquals(1, diagnosis.getAilments().size());
    assertEquals(numberNonNegative().getAilment().getName(), diagnosis.getFieldAilments().get("id").iterator().next().getName());
  }
}
