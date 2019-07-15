package com.miquido.validoctor.rule

import com.miquido.validoctor.TestPatient
import com.miquido.validoctor.TestUtil.*
import com.miquido.validoctor.Validoctor
import com.miquido.validoctor.ailment.Severity
import com.miquido.validoctor.ailment.SpecsKey
import com.miquido.validoctor.rule.Rules.*
import org.junit.Assert.*
import org.junit.Test

class SimpleRuleTest {

  companion object {
    private val validoctor = Validoctor.builder().build()
  }


  @Test
  fun predefinedRule_notNull() {
    assertError(validoctor.examine(null, notNull()))
    assertOk(validoctor.examine(Any(), notNull()))
  }

  @Test
  fun predefinedRule_isNull() {
    assertError(validoctor.examine(Any(), isNull()))
    assertOk(validoctor.examine(null, isNull()))
  }

  @Test
  fun predefinedRule_isFalse() {
    assertError(validoctor.examine(true, isFalse()))
    assertError(validoctor.examine(java.lang.Boolean.TRUE, isFalse()))
    assertOk(validoctor.examine(null, isFalse()))
    assertOk(validoctor.examine(false, isFalse()))
    assertOk(validoctor.examine(java.lang.Boolean.FALSE, isFalse()))
  }

  @Test
  fun predefinedRule_isTrue() {
    assertError(validoctor.examine(false, isTrue()))
    assertError(validoctor.examine(java.lang.Boolean.FALSE, isTrue()))
    assertOk(validoctor.examine(null, isTrue()))
    assertOk(validoctor.examine(true, isTrue()))
    assertOk(validoctor.examine(java.lang.Boolean.TRUE, isTrue()))
  }

  @Test
  fun predefinedRule_collectionNotEmpty() {
    assertError(validoctor.examine(emptyList<Any>(), collectionNotEmpty()))
    assertError(validoctor.examine(emptyMap<Any, Any>().values, collectionNotEmpty()))
    assertError(validoctor.examine(emptySet<Any>(), collectionNotEmpty()))
    assertOk(validoctor.examine(setOf(1), collectionNotEmpty()))
    assertOk(validoctor.examine(listOf(1), collectionNotEmpty()))
  }

  @Test
  fun predefinedRule_collectionMinSize() {
    assertError(validoctor.examine(emptyList<Any>(), collectionMinSize(1)))
    assertError(validoctor.examine(listOf(1, 2, 3, 4), collectionMinSize(5)))
    assertOk(validoctor.examine(listOf(1, 2, 3, 4), collectionMinSize(4)))
    assertOk(validoctor.examine(listOf(1, 2, 3, 4), collectionMinSize(2)))
    assertOk(validoctor.examine(null, collectionMinSize<Any>(4)))
  }

  @Test
  fun predefinedRule_collectionMaxSize() {
    assertError(validoctor.examine(listOf(1, 2, 3, 4), collectionMaxSize(1)))
    assertError(validoctor.examine(listOf(1, 2, 3, 4), collectionMaxSize(3)))
    assertOk(validoctor.examine(emptyList(), collectionMaxSize<Any>(4)))
    assertOk(validoctor.examine(listOf(1, 2, 3, 4), collectionMaxSize(6)))
    assertOk(validoctor.examine(null, collectionMaxSize<Any>(4)))
  }

  @Test
  fun predefinedRule_collectionSizeIn() {
    assertError(validoctor.examine(emptyList<Any>(), collectionSizeIn(1, 5)))
    assertError(validoctor.examine(listOf(1, 2, 3, 4), collectionSizeIn(1, 3)))
    assertError(validoctor.examine(listOf(1, 2, 3, 4), collectionSizeIn(5, 7)))
    assertOk(validoctor.examine(listOf(1, 2, 3, 4), collectionSizeIn(3, 7)))
    assertOk(validoctor.examine(emptyList<Any>(), collectionSizeIn(0, 2)))
    assertOk(validoctor.examine(null, collectionSizeIn<Any>(1, 4)))
  }

  @Test
  fun predefinedRule_stringNotEmpty() {
    assertError(validoctor.examine("", stringNotEmpty()))
    assertOk(validoctor.examine("   ", stringNotEmpty()))
    assertOk(validoctor.examine(null, stringNotEmpty()))
    assertOk(validoctor.examine("aaaa", stringNotEmpty()))
  }

  @Test
  fun predefinedRule_stringTrimmedNotEmpty() {
    assertError(validoctor.examine("", stringTrimmedNotEmpty()))
    assertError(validoctor.examine("   ", stringTrimmedNotEmpty()))
    assertOk(validoctor.examine(null, stringTrimmedNotEmpty()))
    assertOk(validoctor.examine("aaaa", stringTrimmedNotEmpty()))
  }

  @Test
  fun predefinedRule_stringMinLength() {
    assertError(validoctor.examine("", stringMinLength(1)))
    assertError(validoctor.examine("   ", stringMinLength(4)))
    assertError(validoctor.examine("aa aa", stringMinLength(6)))
    assertOk(validoctor.examine(null, stringMinLength(15)))
    assertOk(validoctor.examine("aa aa", stringMinLength(5)))
  }

  @Test
  fun predefinedRule_stringMaxLength() {
    assertError(validoctor.examine("aa", stringMaxLength(1)))
    assertError(validoctor.examine("   ", stringMaxLength(2)))
    assertError(validoctor.examine("aa aa", stringMaxLength(4)))
    assertOk(validoctor.examine(null, stringMaxLength(1)))
    assertOk(validoctor.examine("aa aa", stringMaxLength(5)))
  }

  @Test
  fun predefinedRule_stringLengthInRange() {
    assertError(validoctor.examine("aa", stringLengthInRange(3, 3)))
    assertError(validoctor.examine("   ", stringLengthInRange(0, 0)))
    assertError(validoctor.examine("aa aa", stringLengthInRange(1, 4)))
    assertOk(validoctor.examine(null, stringLengthInRange(1, 10)))
    assertOk(validoctor.examine("aa aa", stringLengthInRange(5, 6)))
  }

  @Test
  fun predefinedRule_stringExactLength() {
    assertError(validoctor.examine("aa", stringExactLength(5)))
    assertError(validoctor.examine("   ", stringExactLength(2)))
    assertError(validoctor.examine("aa aa", stringExactLength(4)))
    assertError(validoctor.examine("aa aa", stringExactLength(6)))
    assertOk(validoctor.examine(null, stringExactLength(1)))
    assertOk(validoctor.examine("aa aa", stringExactLength(5)))
  }

  @Test
  fun predefinedRule_stringAlphanumeric() {
    assertError(validoctor.examine("aa5 ", stringAlphanumeric()))
    assertError(validoctor.examine("aa-5", stringAlphanumeric()))
    assertError(validoctor.examine("&*(%$", stringAlphanumeric()))
    assertError(validoctor.examine("12^1", stringAlphanumeric()))
    assertOk(validoctor.examine(null, stringAlphanumeric()))
    assertOk(validoctor.examine("aa5aa", stringAlphanumeric()))
    assertOk(validoctor.examine("aaaa", stringAlphanumeric()))
    assertOk(validoctor.examine("555", stringAlphanumeric()))
  }

  @Test
  fun predefinedRule_stringAlphabetic() {
    assertError(validoctor.examine("aa5", stringAlphabetic()))
    assertError(validoctor.examine("a a", stringAlphabetic()))
    assertError(validoctor.examine("aa-aa", stringAlphabetic()))
    assertError(validoctor.examine("*7^%", stringAlphabetic()))
    assertOk(validoctor.examine(null, stringAlphabetic()))
    assertOk(validoctor.examine("aaaa", stringAlphabetic()))
  }

  @Test
  fun predefinedRule_stringContains() {
    assertError(validoctor.examine("aaaaa", stringContains("b")))
    assertError(validoctor.examine("", stringContains("b")))
    assertError(validoctor.examine("aa", stringContains("aaa")))
    assertOk(validoctor.examine("aaa", stringContains("aaa")))
    assertOk(validoctor.examine(null, stringContains("aaa")))
  }

  @Test
  fun predefinedRule_stringMatches() {
    assertError(validoctor.examine("abc1", stringMatches("[a-z]*")))
    assertError(validoctor.examine("9-09-2009", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")))
    assertOk(validoctor.examine("abc", stringMatches("[a-z]*")))
    assertOk(validoctor.examine("10-10-2010", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")))
  }

  @Test
  fun predefinedRule_numberPositive() {
    assertError(validoctor.examine(-1, numberPositive()))
    assertError(validoctor.examine(-0.00001, numberPositive()))
    assertError(validoctor.examine(-0.0001f, numberPositive()))
    assertError(validoctor.examine(0.0, numberPositive()))
    assertError(validoctor.examine(0f, numberPositive()))
    assertError(validoctor.examine(0, numberPositive()))
    assertOk(validoctor.examine(null, numberPositive()))
    assertOk(validoctor.examine(5, numberPositive()))
    assertOk(validoctor.examine(125.8f, numberPositive()))
    assertOk(validoctor.examine(125.8, numberPositive()))
  }

  @Test
  fun predefinedRule_numberNonNegative() {
    assertError(validoctor.examine(-1, numberNonNegative()))
    assertError(validoctor.examine(-0.00001, numberNonNegative()))
    assertError(validoctor.examine(-0.0001f, numberNonNegative()))
    assertOk(validoctor.examine(0.0, numberNonNegative()))
    assertOk(validoctor.examine(0f, numberNonNegative()))
    assertOk(validoctor.examine(0, numberNonNegative()))
    assertOk(validoctor.examine(null, numberNonNegative()))
    assertOk(validoctor.examine(5, numberNonNegative()))
    assertOk(validoctor.examine(125.8f, numberNonNegative()))
    assertOk(validoctor.examine(125.8, numberNonNegative()))
  }

  @Test
  fun predefinedRule_numberInRange() {
    assertError(validoctor.examine(-1, numberInRange(-2, -1.0001f)))
    assertError(validoctor.examine(100, numberInRange(99, 99.999999)))
    assertOk(validoctor.examine(-1, numberInRange(-2, -1)))
    assertOk(validoctor.examine(-0.00001, numberInRange(-1, 0)))
    assertOk(validoctor.examine(-0.0001f, numberInRange(-0.0001f, -0.0001f)))
    assertOk(validoctor.examine(15, numberInRange(12, 16)))
    assertOk(validoctor.examine(15.000023, numberInRange(15.000022, 15.000024)))
    assertOk(validoctor.examine(null, numberInRange(-100, 100)))
  }

  @Test
  fun predefinedRule_valueIn() {
    assertOk(validoctor.examine("a", valueIn("a", "b", "c")))
    assertOk(validoctor.examine(null, valueIn(null, "a", "b", "c")))
    assertOk(validoctor.examine(765, valueIn(5, 23, 765, 43)))
    assertOk(validoctor.examine(TestPatient(1, "a", "1", true),
        valueIn(TestPatient(2, "b", "2", false), TestPatient(1, "a", "1", true))))
    assertError(validoctor.examine("a", valueIn("b", "c", "d")))
    assertError(validoctor.examine(null, valueIn()))
    assertError(validoctor.examine(TestPatient(1, "a", "1", true),
        valueIn(TestPatient(2, "a", "1", true), TestPatient(1, "a", "1", false))))
  }

  @Test
  fun predefinedRule_valueIn_list() {
    assertOk(validoctor.examine("a", valueIn(listOf("a", "b", "c"))))
    assertOk(validoctor.examine(null, valueIn(listOf(null, "a", "b", "c"))))
    assertOk(validoctor.examine(765, valueIn(listOf(5, 23, 765, 43))))
    assertOk(validoctor.examine(TestPatient(1, "a", "1", true),
        valueIn(listOf(TestPatient(2, "b", "2", false), TestPatient(1, "a", "1", true)))))
    assertOk(validoctor.examine(null, valueIn(listOf<String?>(null))))
    assertError(validoctor.examine("a", valueIn(listOf("b", "c", "d"))))
    assertError(validoctor.examine(null, valueIn(emptyList())))
    assertError(validoctor.examine("a", valueIn(listOf<String?>(null))))
    assertError(validoctor.examine(TestPatient(1, "a", "1", true),
        valueIn(listOf(TestPatient(2, "a", "1", true), TestPatient(1, "a", "1", false)))))
  }

  @Test
  fun predefinedRule_equalTo() {
    assertOk(validoctor.examine("a", equalTo("a")))
    assertOk(validoctor.examine(7, equalTo(7)))
    assertOk(validoctor.examine(null, equalTo(null)))
    assertOk(validoctor.examine(TestPatient(1, "a", "1", false), equalTo(TestPatient(1, "a", "1", false))))
    assertError(validoctor.examine(7.0, equalTo(7.000001)))
    assertError(validoctor.examine("", equalTo(null)))
    assertError(validoctor.examine("123", equalTo("12")))
    assertError(validoctor.examine(TestPatient(1, "a", "1", false), equalTo(TestPatient(1, "a", "2", false))))
  }

  @Test
  fun predefinedRule_each() {
    assertOk(validoctor.examine(setOf(1), each(notNull<Int>())))
    assertOk(validoctor.examine(listOf("a", "b", "c"), each(stringAlphabetic())))
    assertError(validoctor.examine(listOf("a", "b", "c", "2"), each(stringAlphabetic())))
  }

  @Test
  fun predefinedRule_each_collectionNotEmpty() {
    val strings = listOf("a", "b", "c")
    assertOk(validoctor.examine(strings, collectionNotEmpty(), each(stringAlphabetic())))
    assertError(validoctor.examine(strings, collectionNotEmpty(), each(stringAlphabetic()), each(stringMinLength(2))))
  }

  @Test
  fun predefinedRule_each_collectionSizes() {
    val strings = listOf("a", "b", "c")
    assertOk(validoctor.examine(strings, collectionSizeIn(2, 4), each(stringAlphabetic())))
    assertOk(validoctor.examine(strings, collectionMinSize(2), collectionMaxSize(4), each(stringAlphabetic())))
    assertError(validoctor.examine(strings, collectionSizeIn(2, 4), each(stringAlphabetic()), each(stringMinLength(2))))
  }

  @Test
  fun namedRule() {
    val name = "custom_name"
    val diagnosis = validoctor.examine("a", "patient", named(name, stringMinLength(2)))
    val ailments = diagnosis.ailments["patient"]
    assertTrue(ailments!!.any { ailment -> ailment.name == name })

    assertOk(validoctor.examine("a", "patient", named(name, stringMaxLength(2))))
  }

  @Test
  fun confidentialRule() {
    var diagnosis = validoctor.examine(7, "number", confidential(numberInRange(1, 5)))
    assertError(diagnosis)
    diagnosis.ailments["number"]!!.none { it.specs.containsKey(SpecsKey.PATIENT_VALUE) }

    diagnosis = validoctor.examine(7, "number", confidential(not(numberPositive())))
    assertError(diagnosis)
    diagnosis.ailments["number"]!!.none { it.specs.containsKey(SpecsKey.PATIENT_VALUE) }

    diagnosis = validoctor.examine(listOf("a", "b", null), "strings", each(confidential(notNull())))
    assertError(diagnosis)
    diagnosis.ailments["strings"]!!.none { it.specs.containsKey(SpecsKey.PATIENT_VALUE) }
  }

  @Test
  fun predefinedRule_not() {
    assertOk(validoctor.examine(emptyList(), not(collectionNotEmpty<Any>())))
    assertOk(validoctor.examine(listOf("a", "b", "c"), collectionNotEmpty(), not(each(stringMinLength(2)))))
    assertOk(validoctor.examine(listOf("a", "b", "c"), collectionNotEmpty(), each(not(stringMinLength(2)))))
    assertOk(validoctor.examine(-5, not(numberPositive()), not(numberNonNegative())))
    assertOk(validoctor.examine(6, not(numberInRange(7, 10)), not(valueIn<Number>(7, 8, 9, 10))))
    assertOk(validoctor.examine(null, not(notNull<Any>())))
    assertError(validoctor.examine(null, not(isNull<Any>())))
    assertError(validoctor.examine(false, not(isFalse())))
    assertError(validoctor.examine(true, not(isTrue())))
    assertError(validoctor.examine("  a  ", not(stringTrimmedNotEmpty()), not(stringAlphanumeric()), not(stringMaxLength(2))))
  }

  @Test
  fun customRule() {
    var is5Rule = SimpleRule<Long>("IS_5") { v -> v == 5L }
    assertOk(validoctor.examine(5L, is5Rule))
    assertError(validoctor.examine(6L, is5Rule))

    is5Rule = SimpleRule("IS_5", { v -> v == 5L }, Severity.WARN)
    assertEquals(Severity.WARN, validoctor.examine(6L, is5Rule).severity)
  }

  @Test
  fun multipleRules() {
    assertOk(validoctor.examine(10, numberPositive(), numberInRange(-10, 10)))
    assertOk(validoctor.examine("10", stringAlphanumeric(), valueIn("9", "10", "11")))

    val diagnosis = validoctor.examine(-11, "patient", numberNonNegative(), numberInRange(-10, 10))
    assertError(diagnosis)
    val objectAilments = diagnosis.ailments["patient"]
    assertEquals(2, objectAilments!!.size.toLong())
    assertTrue(objectAilments.any { ailment -> ailment.name == numberNonNegative().peekAilment().name })
    assertTrue(objectAilments.any { ailment -> ailment.name == numberInRange(-10, 10).peekAilment().name })
  }
}
