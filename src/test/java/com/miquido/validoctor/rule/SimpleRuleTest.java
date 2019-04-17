package com.miquido.validoctor.rule;

import com.miquido.validoctor.TestPatient;
import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.ailment.Ailment;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.miquido.validoctor.TestUtil.*;
import static com.miquido.validoctor.ailment.Ailment.*;
import static com.miquido.validoctor.rule.Rules.*;
import static org.junit.Assert.*;

public class SimpleRuleTest {

  private static final Validoctor validoctor = Validoctor.builder().build();

  @Test
  public void predefinedRule_notNull() {
    assertError(validoctor.examine(null, notNull()));
    assertOk(validoctor.examine(new Object(), notNull()));
  }

  @Test
  public void predefinedRule_isNull() {
    assertError(validoctor.examine(new Object(), isNull()));
    assertOk(validoctor.examine(null, isNull()));
  }

  @Test
  public void predefinedRule_isFalse() {
    assertError(validoctor.examine(true, isFalse()));
    assertError(validoctor.examine(Boolean.TRUE, isFalse()));
    assertOk(validoctor.examine(null, isFalse()));
    assertOk(validoctor.examine(false, isFalse()));
    assertOk(validoctor.examine(Boolean.FALSE, isFalse()));
  }

  @Test
  public void predefinedRule_isTrue() {
    assertError(validoctor.examine(false, isTrue()));
    assertError(validoctor.examine(Boolean.FALSE, isTrue()));
    assertOk(validoctor.examine(null, isTrue()));
    assertOk(validoctor.examine(true, isTrue()));
    assertOk(validoctor.examine(Boolean.TRUE, isTrue()));
  }

  @Test
  public void predefinedRule_collectionNotEmpty() {
    assertError(validoctor.examine(Collections.emptyList(), collectionNotEmpty()));
    assertError(validoctor.examine(Collections.emptyMap().values(), collectionNotEmpty()));
    assertError(validoctor.examine(Collections.emptySet(), collectionNotEmpty()));
    assertOk(validoctor.examine(Collections.singleton(1), collectionNotEmpty()));
    assertOk(validoctor.examine(Collections.singletonList(1), collectionNotEmpty()));
  }

  @Test
  public void predefinedRule_stringNotEmpty() {
    assertError(validoctor.examine("", stringNotEmpty()));
    assertOk(validoctor.examine("   ", stringNotEmpty()));
    assertOk(validoctor.examine(null, stringNotEmpty()));
    assertOk(validoctor.examine("aaaa", stringNotEmpty()));
  }

  @Test
  public void predefinedRule_stringTrimmedNotEmpty() {
    assertError(validoctor.examine("", stringTrimmedNotEmpty()));
    assertError(validoctor.examine("   ", stringTrimmedNotEmpty()));
    assertOk(validoctor.examine(null, stringTrimmedNotEmpty()));
    assertOk(validoctor.examine("aaaa", stringTrimmedNotEmpty()));
  }

  @Test
  public void predefinedRule_stringMinLength() {
    assertError(validoctor.examine("", stringMinLength(1)));
    assertError(validoctor.examine("   ", stringMinLength(4)));
    assertError(validoctor.examine("aa aa", stringMinLength(6)));
    assertOk(validoctor.examine(null, stringMinLength(15)));
    assertOk(validoctor.examine("aa aa", stringMinLength(5)));
  }

  @Test
  public void predefinedRule_stringMaxLength() {
    assertError(validoctor.examine("aa", stringMaxLength(1)));
    assertError(validoctor.examine("   ", stringMaxLength(2)));
    assertError(validoctor.examine("aa aa", stringMaxLength(4)));
    assertOk(validoctor.examine(null, stringMaxLength(1)));
    assertOk(validoctor.examine("aa aa", stringMaxLength(5)));
  }

  @Test
  public void predefinedRule_stringLengthInRange() {
    assertError(validoctor.examine("aa", stringLengthInRange(3, 3)));
    assertError(validoctor.examine("   ", stringLengthInRange(0, 0)));
    assertError(validoctor.examine("aa aa", stringLengthInRange(1, 4)));
    assertOk(validoctor.examine(null, stringLengthInRange(1, 10)));
    assertOk(validoctor.examine("aa aa", stringLengthInRange(5, 6)));
  }

  @Test
  public void predefinedRule_stringExactLength() {
    assertError(validoctor.examine("aa", stringExactLength(5)));
    assertError(validoctor.examine("   ", stringExactLength(2)));
    assertError(validoctor.examine("aa aa", stringExactLength(4)));
    assertError(validoctor.examine("aa aa", stringExactLength(6)));
    assertOk(validoctor.examine(null, stringExactLength(1)));
    assertOk(validoctor.examine("aa aa", stringExactLength(5)));
  }

  @Test
  public void predefinedRule_stringAlphanumeric() {
    assertError(validoctor.examine("aa5 ", stringAlphanumeric()));
    assertError(validoctor.examine("aa-5", stringAlphanumeric()));
    assertError(validoctor.examine("&*(%$", stringAlphanumeric()));
    assertError(validoctor.examine("12^1", stringAlphanumeric()));
    assertOk(validoctor.examine(null, stringAlphanumeric()));
    assertOk(validoctor.examine("aa5aa", stringAlphanumeric()));
    assertOk(validoctor.examine("aaaa", stringAlphanumeric()));
    assertOk(validoctor.examine("555", stringAlphanumeric()));
  }

  @Test
  public void predefinedRule_stringAlphabetic() {
    assertError(validoctor.examine("aa5", stringAlphabetic()));
    assertError(validoctor.examine("a a", stringAlphabetic()));
    assertError(validoctor.examine("aa-aa", stringAlphabetic()));
    assertError(validoctor.examine("*7^%", stringAlphabetic()));
    assertOk(validoctor.examine(null, stringAlphabetic()));
    assertOk(validoctor.examine("aaaa", stringAlphabetic()));
  }

  @Test
  public void predefinedRule_numberPositive() {
    assertError(validoctor.examine(-1, numberPositive()));
    assertError(validoctor.examine(-0.00001d, numberPositive()));
    assertError(validoctor.examine(-0.0001f, numberPositive()));
    assertError(validoctor.examine(0d, numberPositive()));
    assertError(validoctor.examine(0f, numberPositive()));
    assertError(validoctor.examine(0, numberPositive()));
    assertOk(validoctor.examine(null, numberPositive()));
    assertOk(validoctor.examine(5, numberPositive()));
    assertOk(validoctor.examine(125.8f, numberPositive()));
    assertOk(validoctor.examine(125.8d, numberPositive()));
  }

  @Test
  public void predefinedRule_numberNonNegative() {
    assertError(validoctor.examine(-1, numberNonNegative()));
    assertError(validoctor.examine(-0.00001d, numberNonNegative()));
    assertError(validoctor.examine(-0.0001f, numberNonNegative()));
    assertOk(validoctor.examine(0d, numberNonNegative()));
    assertOk(validoctor.examine(0f, numberNonNegative()));
    assertOk(validoctor.examine(0, numberNonNegative()));
    assertOk(validoctor.examine(null, numberNonNegative()));
    assertOk(validoctor.examine(5, numberNonNegative()));
    assertOk(validoctor.examine(125.8f, numberNonNegative()));
    assertOk(validoctor.examine(125.8d, numberNonNegative()));
  }

  @Test
  public void predefinedRule_numberInRange() {
    assertError(validoctor.examine(-1, numberInRange(-2, -1.0001f)));
    assertError(validoctor.examine(100, numberInRange(99, 99.999999d)));
    assertOk(validoctor.examine(-1, numberInRange(-2, -1)));
    assertOk(validoctor.examine(-0.00001d, numberInRange(-1, 0)));
    assertOk(validoctor.examine(-0.0001f, numberInRange(-0.0001f, -0.0001f)));
    assertOk(validoctor.examine(15, numberInRange(12, 16)));
    assertOk(validoctor.examine(15.000023d, numberInRange(15.000022d, 15.000024d)));
    assertOk(validoctor.examine(null, numberInRange(-100, 100)));
  }

  @Test
  public void predefinedRule_valueIn() {
    assertOk(validoctor.examine("a", valueIn("a", "b", "c")));
    assertOk(validoctor.examine(null, valueIn(null, "a", "b", "c")));
    assertOk(validoctor.examine(765, valueIn(5, 23, 765, 43)));
    assertOk(validoctor.examine(new TestPatient(1, "a", "1", true),
        valueIn(new TestPatient(2, "b", "2", false), new TestPatient(1, "a", "1", true))));
    assertError(validoctor.examine("a", valueIn("b", "c", "d")));
    assertError(validoctor.examine(null, valueIn()));
    assertError(validoctor.examine(new TestPatient(1, "a", "1", true),
        valueIn(new TestPatient(2, "a", "1", true), new TestPatient(1, "a", "1", false))));
  }

  @Test
  public void predefinedRule_valueIn_list() {
    assertOk(validoctor.examine("a", valueIn(Arrays.asList("a", "b", "c"))));
    assertOk(validoctor.examine(null, valueIn(Arrays.asList(null, "a", "b", "c"))));
    assertOk(validoctor.examine(765, valueIn(Arrays.asList(5, 23, 765, 43))));
    assertOk(validoctor.examine(new TestPatient(1, "a", "1", true),
        valueIn(Arrays.asList(new TestPatient(2, "b", "2", false), new TestPatient(1, "a", "1", true)))));
    assertError(validoctor.examine("a", valueIn(Arrays.asList("b", "c", "d"))));
    assertError(validoctor.examine(null, valueIn(Collections.emptyList())));
    assertError(validoctor.examine(new TestPatient(1, "a", "1", true),
        valueIn(Arrays.asList(new TestPatient(2, "a", "1", true), new TestPatient(1, "a", "1", false)))));
  }

  @Test
  public void predefinedRule_each() {
    assertOk(validoctor.examine(Collections.singleton(1), each(notNull())));
    assertOk(validoctor.examine(Arrays.asList("a", "b", "c"), each(stringAlphabetic())));
    assertError(validoctor.examine(Arrays.asList("a", "b", "c", "2"), each(stringAlphabetic())));
  }

  @Test
  public void predefinedRule_each_collectionNotEmpty() {
    List<String> strings = Arrays.asList("a", "b", "c");
    assertOk(validoctor.examine(strings, collectionNotEmpty(), each(stringAlphabetic())));
    assertError(validoctor.examine(strings, collectionNotEmpty(), each(stringAlphabetic()), each(stringMinLength(2))));
  }

  @Test
  public void customRule() {
    SimpleRule<Long> is5Rule = new SimpleRule<>("IS_5", v -> v == 5);
    assertOk(validoctor.examine(5L, is5Rule));
    assertError(validoctor.examine(6L, is5Rule));

    is5Rule = new SimpleRule<>("IS_5", v -> v == 5, Severity.WARN);
    assertEquals(Severity.WARN, validoctor.examine(6L, is5Rule).getSeverity());
  }

  @Test
  public void multipleRules() {
    assertOk(validoctor.examine(10, numberPositive(), numberInRange(-10, 10)));
    assertOk(validoctor.examine("10", stringAlphanumeric(), valueIn("9", "10", "11")));

    Diagnosis diagnosis = validoctor.examine(-11, "patient", numberNonNegative(), numberInRange(-10, 10));
    assertError(diagnosis);
    Set<Ailment> objectAilments = diagnosis.getAilments().get("patient");
    assertEquals(2, objectAilments.size());
    assertTrue(objectAilments.stream().anyMatch(ailment -> ailment.getName().equals(numberNonNegative().peekAilment().getName())));
    assertTrue(objectAilments.stream().anyMatch(ailment -> ailment.getName().equals(numberInRange(-10, 10).peekAilment().getName())));
  }

}
