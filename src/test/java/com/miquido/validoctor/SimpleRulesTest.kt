package com.miquido.validoctor

import com.miquido.validoctor.TestClasses.SimpleTestClass
import com.miquido.validoctor.definition.Rules.collectionContains
import com.miquido.validoctor.definition.Rules.collectionMaxSize
import com.miquido.validoctor.definition.Rules.collectionMinSize
import com.miquido.validoctor.definition.Rules.collectionNotEmpty
import com.miquido.validoctor.definition.Rules.collectionSizeIn
import com.miquido.validoctor.definition.Rules.equalTo
import com.miquido.validoctor.definition.Rules.isFalse
import com.miquido.validoctor.definition.Rules.isNull
import com.miquido.validoctor.definition.Rules.isTrue
import com.miquido.validoctor.definition.Rules.named
import com.miquido.validoctor.definition.Rules.notEqualTo
import com.miquido.validoctor.definition.Rules.notNull
import com.miquido.validoctor.definition.Rules.numberInRange
import com.miquido.validoctor.definition.Rules.numberNonNegative
import com.miquido.validoctor.definition.Rules.numberPositive
import com.miquido.validoctor.definition.Rules.stringAlphabetic
import com.miquido.validoctor.definition.Rules.stringAlphanumeric
import com.miquido.validoctor.definition.Rules.stringContains
import com.miquido.validoctor.definition.Rules.stringExactLength
import com.miquido.validoctor.definition.Rules.stringLengthInRange
import com.miquido.validoctor.definition.Rules.stringMatches
import com.miquido.validoctor.definition.Rules.stringMaxLength
import com.miquido.validoctor.definition.Rules.stringMinLength
import com.miquido.validoctor.definition.Rules.stringNoSpacePadding
import com.miquido.validoctor.definition.Rules.stringNotEmpty
import com.miquido.validoctor.definition.Rules.stringTrimmedNotEmpty
import com.miquido.validoctor.definition.Rules.valueIn
import com.miquido.validoctor.definition.Rules.valueNotIn
import com.miquido.validoctor.definition.SimpleRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.Boolean
import java.time.DayOfWeek
import java.time.Month
import kotlin.Any
import kotlin.Long
import kotlin.String

class SimpleRulesTest {

  @Before
  fun setNonThrowing() {
    Validoctor.setThrowing(false)
  }

  @Test
  fun predefinedRule_notNull() {
    assertFalse(Validoctor.examine(null, notNull()).isValid)
    assertTrue(Validoctor.examine(Any(), notNull()).isValid)
  }

  @Test
  fun predefinedRule_isNull() {
    assertFalse(Validoctor.examine(Any(), isNull()).isValid)
    assertTrue(Validoctor.examine(null, isNull()).isValid)
  }

  @Test
  fun predefinedRule_isFalse() {
    assertFalse(Validoctor.examine(true, isFalse()).isValid)
    assertFalse(Validoctor.examine(Boolean.TRUE, isFalse()).isValid)
    assertTrue(Validoctor.examine(null, isFalse()).isValid)
    assertTrue(Validoctor.examine(false, isFalse()).isValid)
    assertTrue(Validoctor.examine(Boolean.FALSE, isFalse()).isValid)
  }

  @Test
  fun predefinedRule_isTrue() {
    assertFalse(Validoctor.examine(false, isTrue()).isValid)
    assertFalse(Validoctor.examine(Boolean.FALSE, isTrue()).isValid)
    assertTrue(Validoctor.examine(null, isTrue()).isValid)
    assertTrue(Validoctor.examine(true, isTrue()).isValid)
    assertTrue(Validoctor.examine(Boolean.TRUE, isTrue()).isValid)
  }

  @Test
  fun predefinedRule_collectionNotEmpty() {
    assertFalse(Validoctor.examine(emptyList<Any>(), collectionNotEmpty()).isValid)
    assertFalse(Validoctor.examine(emptyMap<Any, Any>().values, collectionNotEmpty()).isValid)
    assertFalse(Validoctor.examine(emptySet<Any>(), collectionNotEmpty()).isValid)
    assertTrue(Validoctor.examine(setOf(1), collectionNotEmpty()).isValid)
    assertTrue(Validoctor.examine(listOf(1), collectionNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_collectionMinSize() {
    assertFalse(Validoctor.examine(emptyList<Any>(), collectionMinSize(1)).isValid)
    assertFalse(Validoctor.examine(listOf(1, 2, 3, 4), collectionMinSize(5)).isValid)
    assertTrue(Validoctor.examine(listOf(1, 2, 3, 4), collectionMinSize(4)).isValid)
    assertTrue(Validoctor.examine(listOf(1, 2, 3, 4), collectionMinSize(2)).isValid)
    assertTrue(Validoctor.examine(null, collectionMinSize(4)).isValid)
  }

  @Test
  fun predefinedRule_collectionMaxSize() {
    assertFalse(Validoctor.examine(listOf(1, 2, 3, 4), collectionMaxSize(1)).isValid)
    assertFalse(Validoctor.examine(listOf(1, 2, 3, 4), collectionMaxSize(3)).isValid)
    assertTrue(Validoctor.examine(emptyList<String>(), collectionMaxSize(4)).isValid)
    assertTrue(Validoctor.examine(listOf(1, 2, 3, 4), collectionMaxSize(6)).isValid)
    assertTrue(Validoctor.examine(null, collectionMaxSize(4)).isValid)
  }

  @Test
  fun predefinedRule_collectionSizeIn() {
    assertFalse(Validoctor.examine(emptyList<Any>(), collectionSizeIn(1, 5)).isValid)
    assertFalse(Validoctor.examine(listOf(1, 2, 3, 4), collectionSizeIn(1, 3)).isValid)
    assertFalse(Validoctor.examine(listOf(1, 2, 3, 4), collectionSizeIn(5, 7)).isValid)
    assertTrue(Validoctor.examine(listOf(1, 2, 3, 4), collectionSizeIn(3, 7)).isValid)
    assertTrue(Validoctor.examine(emptyList<Any>(), collectionSizeIn(0, 2)).isValid)
    assertTrue(Validoctor.examine(null, collectionSizeIn(1, 4)).isValid)
  }

  @Test
  fun predefinedRule_collectionContains() {
    assertFalse(Validoctor.examine(emptyList<String>(), collectionContains("")).isValid)
    assertFalse(Validoctor.examine(listOf(1, 2, 3, 4), collectionContains(5)).isValid)
    assertFalse(Validoctor.examine(listOf("a", "b", "c", "d"), collectionContains("e")).isValid)
    assertFalse(Validoctor.examine(listOf("a", "b", "c", "d"), collectionContains("e")).isValid)
    assertFalse(
      Validoctor.examine(listOf(SimpleTestClass(1, "a", "1", false)),
      collectionContains(SimpleTestClass(1, "b", "1", false))).isValid)
    assertTrue(
      Validoctor.examine(listOf(SimpleTestClass(1, "a", "1", false)),
      collectionContains(SimpleTestClass(1, "a", "1", false))).isValid)
    assertTrue(Validoctor.examine(listOf("a", "b", "c", "d"), collectionContains("d")).isValid)
    assertTrue(Validoctor.examine(null, collectionContains(1)).isValid)
  }

  @Test
  fun predefinedRule_stringNotEmpty() {
    assertFalse(Validoctor.examine("", stringNotEmpty()).isValid)
    assertTrue(Validoctor.examine("   ", stringNotEmpty()).isValid)
    assertTrue(Validoctor.examine(null, stringNotEmpty()).isValid)
    assertTrue(Validoctor.examine("aaaa", stringNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_stringTrimmedNotEmpty() {
    assertFalse(Validoctor.examine("", stringTrimmedNotEmpty()).isValid)
    assertFalse(Validoctor.examine("   ", stringTrimmedNotEmpty()).isValid)
    assertTrue(Validoctor.examine(null, stringTrimmedNotEmpty()).isValid)
    assertTrue(Validoctor.examine("aaaa", stringTrimmedNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_stringMinLength() {
    assertFalse(Validoctor.examine("", stringMinLength(1)).isValid)
    assertFalse(Validoctor.examine("   ", stringMinLength(4)).isValid)
    assertFalse(Validoctor.examine("aa aa", stringMinLength(6)).isValid)
    assertTrue(Validoctor.examine(null, stringMinLength(15)).isValid)
    assertTrue(Validoctor.examine("aa aa", stringMinLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringMaxLength() {
    assertFalse(Validoctor.examine("aa", stringMaxLength(1)).isValid)
    assertFalse(Validoctor.examine("   ", stringMaxLength(2)).isValid)
    assertFalse(Validoctor.examine("aa aa", stringMaxLength(4)).isValid)
    assertTrue(Validoctor.examine(null, stringMaxLength(1)).isValid)
    assertTrue(Validoctor.examine("aa aa", stringMaxLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringLengthInRange() {
    assertFalse(Validoctor.examine("aa", stringLengthInRange(3, 3)).isValid)
    assertFalse(Validoctor.examine("   ", stringLengthInRange(0, 0)).isValid)
    assertFalse(Validoctor.examine("aa aa", stringLengthInRange(1, 4)).isValid)
    assertTrue(Validoctor.examine(null, stringLengthInRange(1, 10)).isValid)
    assertTrue(Validoctor.examine("aa aa", stringLengthInRange(5, 6)).isValid)
  }

  @Test
  fun predefinedRule_stringExactLength() {
    assertFalse(Validoctor.examine("aa", stringExactLength(5)).isValid)
    assertFalse(Validoctor.examine("   ", stringExactLength(2)).isValid)
    assertFalse(Validoctor.examine("aa aa", stringExactLength(4)).isValid)
    assertFalse(Validoctor.examine("aa aa", stringExactLength(6)).isValid)
    assertTrue(Validoctor.examine(null, stringExactLength(1)).isValid)
    assertTrue(Validoctor.examine("aa aa", stringExactLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringAlphanumeric() {
    assertFalse(Validoctor.examine("aa5 ", stringAlphanumeric()).isValid)
    assertFalse(Validoctor.examine("aa-5", stringAlphanumeric()).isValid)
    assertFalse(Validoctor.examine("&*(%$", stringAlphanumeric()).isValid)
    assertFalse(Validoctor.examine("12^1", stringAlphanumeric()).isValid)
    assertTrue(Validoctor.examine(null, stringAlphanumeric()).isValid)
    assertTrue(Validoctor.examine("aa5aa", stringAlphanumeric()).isValid)
    assertTrue(Validoctor.examine("aaaa", stringAlphanumeric()).isValid)
    assertTrue(Validoctor.examine("555", stringAlphanumeric()).isValid)
  }

  @Test
  fun predefinedRule_stringAlphabetic() {
    assertFalse(Validoctor.examine("aa5", stringAlphabetic()).isValid)
    assertFalse(Validoctor.examine("a a", stringAlphabetic()).isValid)
    assertFalse(Validoctor.examine("aa-aa", stringAlphabetic()).isValid)
    assertFalse(Validoctor.examine("*7^%", stringAlphabetic()).isValid)
    assertTrue(Validoctor.examine(null, stringAlphabetic()).isValid)
    assertTrue(Validoctor.examine("aaaa", stringAlphabetic()).isValid)
  }

  @Test
  fun predefinedRule_stringContains() {
    assertFalse(Validoctor.examine("aaaaa", stringContains("b")).isValid)
    assertFalse(Validoctor.examine("", stringContains("b")).isValid)
    assertFalse(Validoctor.examine("aa", stringContains("aaa")).isValid)
    assertTrue(Validoctor.examine("aaa", stringContains("aaa")).isValid)
    assertTrue(Validoctor.examine(null, stringContains("aaa")).isValid)
  }

  @Test
  fun predefinedRule_stringMatches() {
    assertFalse(Validoctor.examine("abc1", stringMatches("[a-z]*")).isValid)
    assertFalse(Validoctor.examine("9-09-2009", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")).isValid)
    assertTrue(Validoctor.examine("abc", stringMatches("[a-z]*")).isValid)
    assertTrue(Validoctor.examine("10-10-2010", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")).isValid)
  }

  @Test
  fun predefinedRule_stringNoSpacePadding() {
    assertFalse(Validoctor.examine(" c ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor.examine("  ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor.examine(" ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor.examine("abc ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor.examine(" abc", stringNoSpacePadding()).isValid)
    assertTrue(Validoctor.examine("abc", stringNoSpacePadding()).isValid)
  }

  @Test
  fun predefinedRule_numberPositive() {
    assertFalse(Validoctor.examine(-1, numberPositive()).isValid)
    assertFalse(Validoctor.examine(-0.00001, numberPositive()).isValid)
    assertFalse(Validoctor.examine(-0.0001f, numberPositive()).isValid)
    assertFalse(Validoctor.examine(0.0, numberPositive()).isValid)
    assertFalse(Validoctor.examine(0f, numberPositive()).isValid)
    assertFalse(Validoctor.examine(0, numberPositive()).isValid)
    assertTrue(Validoctor.examine(null, numberPositive()).isValid)
    assertTrue(Validoctor.examine(5, numberPositive()).isValid)
    assertTrue(Validoctor.examine(125.8f, numberPositive()).isValid)
    assertTrue(Validoctor.examine(125.8, numberPositive()).isValid)
  }

  @Test
  fun predefinedRule_numberNonNegative() {
    assertFalse(Validoctor.examine(-1, numberNonNegative()).isValid)
    assertFalse(Validoctor.examine(-0.00001, numberNonNegative()).isValid)
    assertFalse(Validoctor.examine(-0.0001f, numberNonNegative()).isValid)
    assertTrue(Validoctor.examine(0.0, numberNonNegative()).isValid)
    assertTrue(Validoctor.examine(0f, numberNonNegative()).isValid)
    assertTrue(Validoctor.examine(0, numberNonNegative()).isValid)
    assertTrue(Validoctor.examine(null, numberNonNegative()).isValid)
    assertTrue(Validoctor.examine(5, numberNonNegative()).isValid)
    assertTrue(Validoctor.examine(125.8f, numberNonNegative()).isValid)
    assertTrue(Validoctor.examine(125.8, numberNonNegative()).isValid)
  }

  @Test
  fun predefinedRule_numberInRange() {
    assertFalse(Validoctor.examine(-1, numberInRange(-2, -1.0001f)).isValid)
    assertFalse(Validoctor.examine(100, numberInRange(99, 99.999999)).isValid)
    assertTrue(Validoctor.examine(-1, numberInRange(-2, -1)).isValid)
    assertTrue(Validoctor.examine(-0.00001, numberInRange(-1, 0)).isValid)
    assertTrue(Validoctor.examine(-0.0001f, numberInRange(-0.0001f, -0.0001f)).isValid)
    assertTrue(Validoctor.examine(15, numberInRange(12, 16)).isValid)
    assertTrue(Validoctor.examine(15.000023, numberInRange(15.000022, 15.000024)).isValid)
    assertTrue(Validoctor.examine(null, numberInRange(-100, 100)).isValid)
  }

  @Test
  fun predefinedRule_valueIn() {
    assertTrue(Validoctor.examine("a", valueIn("a", "b", "c")).isValid)
    assertTrue(Validoctor.examine(null, valueIn(null, "a", "b", "c")).isValid)
    assertTrue(Validoctor.examine(765, valueIn(5, 23, 765, 43)).isValid)
    assertTrue(
      Validoctor.examine(
        SimpleTestClass(1, "a", "1", true),
        valueIn(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true))).isValid)
    assertTrue(Validoctor.examine(DayOfWeek.FRIDAY, valueIn(*DayOfWeek.values())).isValid)
    assertFalse(Validoctor.examine("a", valueIn("b", "c", "d")).isValid)
    assertFalse(Validoctor.examine(null, valueIn()).isValid)
    assertFalse(
      Validoctor.examine(SimpleTestClass(1, "a", "1", true),
        valueIn(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false))).isValid)
    assertFalse(Validoctor.examine(Month.APRIL, valueIn(*DayOfWeek.values())).isValid)
  }

  @Test
  fun predefinedRule_valueIn_collection() {
    assertTrue(Validoctor.examine("a", valueIn(listOf("a", "b", "c"))).isValid)
    assertTrue(Validoctor.examine("a", valueIn(setOf("a", "b", "c"))).isValid)
    assertTrue(Validoctor.examine(null, valueIn(listOf(null, "a", "b", "c"))).isValid)
    assertTrue(Validoctor.examine(765, valueIn(listOf(5, 23, 765, 43))).isValid)
    assertTrue(
      Validoctor.examine(SimpleTestClass(1, "a", "1", true),
      valueIn(listOf(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true)))).isValid)
    assertTrue(Validoctor.examine(null, valueIn(listOf<String?>(null))).isValid)
    assertFalse(Validoctor.examine("a", valueIn(listOf("b", "c", "d"))).isValid)
    assertFalse(Validoctor.examine("a", valueIn(setOf("b", "c", "d"))).isValid)
    assertFalse(Validoctor.examine(null, valueIn(emptyList())).isValid)
    assertFalse(Validoctor.examine("a", valueIn(listOf<String?>(null))).isValid)
    assertFalse(
      Validoctor.examine(SimpleTestClass(1, "a", "1", true),
      valueIn(listOf(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false)))).isValid)
  }

  @Test
  fun predefinedRule_valueNotIn() {
    assertTrue(Validoctor.examine("a", valueNotIn("b", "c", "d")).isValid)
    assertTrue(Validoctor.examine(null, valueNotIn()).isValid)
    assertTrue(
      Validoctor.examine(SimpleTestClass(1, "a", "1", true),
        valueNotIn(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false))).isValid)
    assertTrue(Validoctor.examine(Month.APRIL, valueNotIn(*DayOfWeek.values())).isValid)
    assertFalse(Validoctor.examine("a", valueNotIn("a", "b", "c")).isValid)
    assertFalse(Validoctor.examine(null, valueNotIn(null, "a", "b", "c")).isValid)
    assertFalse(Validoctor.examine(765, valueNotIn(5, 23, 765, 43)).isValid)
    assertFalse(
      Validoctor.examine(
        SimpleTestClass(1, "a", "1", true),
        valueNotIn(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true))).isValid)
    assertFalse(Validoctor.examine(DayOfWeek.FRIDAY, valueNotIn(*DayOfWeek.values())).isValid)
  }

  @Test
  fun predefinedRule_valueNotIn_collection() {
    assertTrue(Validoctor.examine("a", valueNotIn(listOf("b", "c", "d"))).isValid)
    assertTrue(Validoctor.examine("a", valueNotIn(setOf("b", "c", "d"))).isValid)
    assertTrue(Validoctor.examine(null, valueNotIn(emptyList())).isValid)
    assertTrue(Validoctor.examine("a", valueNotIn(listOf<String?>(null))).isValid)
    assertTrue(
      Validoctor.examine(SimpleTestClass(1, "a", "1", true),
        valueNotIn(listOf(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false)))).isValid)
    assertFalse(Validoctor.examine("a", valueNotIn(listOf("a", "b", "c"))).isValid)
    assertFalse(Validoctor.examine("a", valueNotIn(setOf("a", "b", "c"))).isValid)
    assertFalse(Validoctor.examine(null, valueNotIn(listOf(null, "a", "b", "c"))).isValid)
    assertFalse(Validoctor.examine(765, valueNotIn(listOf(5, 23, 765, 43))).isValid)
    assertFalse(
      Validoctor.examine(SimpleTestClass(1, "a", "1", true),
        valueNotIn(listOf(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true)))).isValid)
    assertFalse(Validoctor.examine(null, valueNotIn(listOf<String?>(null))).isValid)
  }

  @Test
  fun predefinedRule_equalTo() {
    assertTrue(Validoctor.examine("a", equalTo("a")).isValid)
    assertTrue(Validoctor.examine(7, equalTo(7)).isValid)
    assertTrue(Validoctor.examine(null, equalTo(null)).isValid)
    assertTrue(Validoctor.examine(SimpleTestClass(1, "a", "1", false), equalTo(SimpleTestClass(1, "a", "1", false))).isValid)
    assertFalse(Validoctor.examine(7.0, equalTo(7.000001)).isValid)
    assertFalse(Validoctor.examine("", equalTo(null)).isValid)
    assertFalse(Validoctor.examine("123", equalTo("12")).isValid)
    assertFalse(Validoctor.examine(SimpleTestClass(1, "a", "1", false), equalTo(SimpleTestClass(1, "a", "2", false))).isValid)
  }

  @Test
  fun predefinedRule_notEqualTo() {
    assertTrue(Validoctor.examine(7.0, notEqualTo(7.000001)).isValid)
    assertTrue(Validoctor.examine("", notEqualTo(null)).isValid)
    assertTrue(Validoctor.examine("123", notEqualTo("12")).isValid)
    assertTrue(Validoctor.examine(null, notEqualTo(null)).isValid)
    assertTrue(Validoctor.examine(SimpleTestClass(1, "a", "1", false), notEqualTo(SimpleTestClass(1, "a", "2", false))).isValid)
    assertFalse(Validoctor.examine("a", notEqualTo("a")).isValid)
    assertFalse(Validoctor.examine(7, notEqualTo(7)).isValid)
    assertFalse(Validoctor.examine(SimpleTestClass(1, "a", "1", false), notEqualTo(SimpleTestClass(1, "a", "1", false))).isValid)
  }

  @Test
  fun namedRule() {
    val name = "custom_name"
    val diagnosis = Validoctor.examine("a", "patient", named(name, stringMinLength(2)))
    val ailments = diagnosis.ailments["patient"]
    assertTrue(ailments!!.any { ailment -> ailment == name })
    assertTrue(Validoctor.examine("a", "patient", named(name, stringMaxLength(2))).isValid)
  }

  @Test
  fun customRule() {
    val is5Rule = SimpleRule<Long>("IS_5") { v -> v == 5L }
    assertTrue(Validoctor.examine(5L, is5Rule).isValid)
    assertFalse(Validoctor.examine(6L, is5Rule).isValid)
  }

  @Test
  fun multipleRules() {
    assertTrue(Validoctor.examine(10, numberPositive(), numberInRange(-10, 10)).isValid)
    assertTrue(Validoctor.examine("10", stringAlphanumeric(), valueIn("9", "10", "11")).isValid)

    val diagnosis = Validoctor.examine(-11, "patient", numberNonNegative(), numberInRange(-10, 10))
    assertFalse(diagnosis.isValid)
    val objectAilments = diagnosis.ailments["patient"]
    assertEquals(2, objectAilments!!.size.toLong())
  }
}