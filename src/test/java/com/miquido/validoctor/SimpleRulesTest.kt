package com.miquido.validoctor

import com.miquido.validoctor.TestClasses.SimpleTestClass
import com.miquido.validoctor.Validoctor.examine
import com.miquido.validoctor.Validoctor.setThrowing
import com.miquido.validoctor.definition.Rules.collectionContains
import com.miquido.validoctor.definition.Rules.collectionMaxSize
import com.miquido.validoctor.definition.Rules.collectionMinSize
import com.miquido.validoctor.definition.Rules.collectionNotEmpty
import com.miquido.validoctor.definition.Rules.collectionSizeIn
import com.miquido.validoctor.definition.Rules.equalTo
import com.miquido.validoctor.definition.Rules.isFalse
import com.miquido.validoctor.definition.Rules.isNull
import com.miquido.validoctor.definition.Rules.isTrue
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
    setThrowing(false)
  }

  @Test
  fun predefinedRule_notNull() {
    assertFalse(examine(null, notNull()).isValid)
    assertTrue(examine(Any(), notNull()).isValid)
  }

  @Test
  fun predefinedRule_isNull() {
    assertFalse(examine(Any(), isNull()).isValid)
    assertTrue(examine(null, isNull()).isValid)
  }

  @Test
  fun predefinedRule_isFalse() {
    assertFalse(examine(true, isFalse()).isValid)
    assertFalse(examine(Boolean.TRUE, isFalse()).isValid)
    assertTrue(examine(null, isFalse()).isValid)
    assertTrue(examine(false, isFalse()).isValid)
    assertTrue(examine(Boolean.FALSE, isFalse()).isValid)
  }

  @Test
  fun predefinedRule_isTrue() {
    assertFalse(examine(false, isTrue()).isValid)
    assertFalse(examine(Boolean.FALSE, isTrue()).isValid)
    assertTrue(examine(null, isTrue()).isValid)
    assertTrue(examine(true, isTrue()).isValid)
    assertTrue(examine(Boolean.TRUE, isTrue()).isValid)
  }

  @Test
  fun predefinedRule_collectionNotEmpty() {
    assertFalse(examine(emptyList<Any>(), collectionNotEmpty()).isValid)
    assertFalse(examine(emptyMap<Any, Any>().values, collectionNotEmpty()).isValid)
    assertFalse(examine(emptySet<Any>(), collectionNotEmpty()).isValid)
    assertTrue(examine(setOf(1), collectionNotEmpty()).isValid)
    assertTrue(examine(listOf(1), collectionNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_collectionMinSize() {
    assertFalse(examine(emptyList<Any>(), collectionMinSize(1)).isValid)
    assertFalse(examine(listOf(1, 2, 3, 4), collectionMinSize(5)).isValid)
    assertTrue(examine(listOf(1, 2, 3, 4), collectionMinSize(4)).isValid)
    assertTrue(examine(listOf(1, 2, 3, 4), collectionMinSize(2)).isValid)
    assertTrue(examine(null, collectionMinSize(4)).isValid)
  }

  @Test
  fun predefinedRule_collectionMaxSize() {
    assertFalse(examine(listOf(1, 2, 3, 4), collectionMaxSize(1)).isValid)
    assertFalse(examine(listOf(1, 2, 3, 4), collectionMaxSize(3)).isValid)
    assertTrue(examine(emptyList<String>(), collectionMaxSize(4)).isValid)
    assertTrue(examine(listOf(1, 2, 3, 4), collectionMaxSize(6)).isValid)
    assertTrue(examine(null, collectionMaxSize(4)).isValid)
  }

  @Test
  fun predefinedRule_collectionSizeIn() {
    assertFalse(examine(emptyList<Any>(), collectionSizeIn(1, 5)).isValid)
    assertFalse(examine(listOf(1, 2, 3, 4), collectionSizeIn(1, 3)).isValid)
    assertFalse(examine(listOf(1, 2, 3, 4), collectionSizeIn(5, 7)).isValid)
    assertTrue(examine(listOf(1, 2, 3, 4), collectionSizeIn(3, 7)).isValid)
    assertTrue(examine(emptyList<Any>(), collectionSizeIn(0, 2)).isValid)
    assertTrue(examine(null, collectionSizeIn(1, 4)).isValid)
  }

  @Test
  fun predefinedRule_collectionContains() {
    assertFalse(examine(emptyList<String>(), collectionContains("")).isValid)
    assertFalse(examine(listOf(1, 2, 3, 4), collectionContains(5)).isValid)
    assertFalse(examine(listOf("a", "b", "c", "d"), collectionContains("e")).isValid)
    assertFalse(examine(listOf("a", "b", "c", "d"), collectionContains("e")).isValid)
    assertFalse(
      examine(listOf(SimpleTestClass(1, "a", "1", false)),
      collectionContains(SimpleTestClass(1, "b", "1", false))).isValid)
    assertTrue(
      examine(listOf(SimpleTestClass(1, "a", "1", false)),
      collectionContains(SimpleTestClass(1, "a", "1", false))).isValid)
    assertTrue(examine(listOf("a", "b", "c", "d"), collectionContains("d")).isValid)
    assertTrue(examine(null, collectionContains(1)).isValid)
  }

  @Test
  fun predefinedRule_stringNotEmpty() {
    assertFalse(examine("", stringNotEmpty()).isValid)
    assertTrue(examine("   ", stringNotEmpty()).isValid)
    assertTrue(examine(null, stringNotEmpty()).isValid)
    assertTrue(examine("aaaa", stringNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_stringTrimmedNotEmpty() {
    assertFalse(examine("", stringTrimmedNotEmpty()).isValid)
    assertFalse(examine("   ", stringTrimmedNotEmpty()).isValid)
    assertTrue(examine(null, stringTrimmedNotEmpty()).isValid)
    assertTrue(examine("aaaa", stringTrimmedNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_stringMinLength() {
    assertFalse(examine("", stringMinLength(1)).isValid)
    assertFalse(examine("   ", stringMinLength(4)).isValid)
    assertFalse(examine("aa aa", stringMinLength(6)).isValid)
    assertTrue(examine(null, stringMinLength(15)).isValid)
    assertTrue(examine("aa aa", stringMinLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringMaxLength() {
    assertFalse(examine("aa", stringMaxLength(1)).isValid)
    assertFalse(examine("   ", stringMaxLength(2)).isValid)
    assertFalse(examine("aa aa", stringMaxLength(4)).isValid)
    assertTrue(examine(null, stringMaxLength(1)).isValid)
    assertTrue(examine("aa aa", stringMaxLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringLengthInRange() {
    assertFalse(examine("aa", stringLengthInRange(3, 3)).isValid)
    assertFalse(examine("   ", stringLengthInRange(0, 0)).isValid)
    assertFalse(examine("aa aa", stringLengthInRange(1, 4)).isValid)
    assertTrue(examine(null, stringLengthInRange(1, 10)).isValid)
    assertTrue(examine("aa aa", stringLengthInRange(5, 6)).isValid)
  }

  @Test
  fun predefinedRule_stringExactLength() {
    assertFalse(examine("aa", stringExactLength(5)).isValid)
    assertFalse(examine("   ", stringExactLength(2)).isValid)
    assertFalse(examine("aa aa", stringExactLength(4)).isValid)
    assertFalse(examine("aa aa", stringExactLength(6)).isValid)
    assertTrue(examine(null, stringExactLength(1)).isValid)
    assertTrue(examine("aa aa", stringExactLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringAlphanumeric() {
    assertFalse(examine("aa5 ", stringAlphanumeric()).isValid)
    assertFalse(examine("aa-5", stringAlphanumeric()).isValid)
    assertFalse(examine("&*(%$", stringAlphanumeric()).isValid)
    assertFalse(examine("12^1", stringAlphanumeric()).isValid)
    assertTrue(examine(null, stringAlphanumeric()).isValid)
    assertTrue(examine("aa5aa", stringAlphanumeric()).isValid)
    assertTrue(examine("aaaa", stringAlphanumeric()).isValid)
    assertTrue(examine("555", stringAlphanumeric()).isValid)
  }

  @Test
  fun predefinedRule_stringAlphabetic() {
    assertFalse(examine("aa5", stringAlphabetic()).isValid)
    assertFalse(examine("a a", stringAlphabetic()).isValid)
    assertFalse(examine("aa-aa", stringAlphabetic()).isValid)
    assertFalse(examine("*7^%", stringAlphabetic()).isValid)
    assertTrue(examine(null, stringAlphabetic()).isValid)
    assertTrue(examine("aaaa", stringAlphabetic()).isValid)
  }

  @Test
  fun predefinedRule_stringContains() {
    assertFalse(examine("aaaaa", stringContains("b")).isValid)
    assertFalse(examine("", stringContains("b")).isValid)
    assertFalse(examine("aa", stringContains("aaa")).isValid)
    assertTrue(examine("aaa", stringContains("aaa")).isValid)
    assertTrue(examine(null, stringContains("aaa")).isValid)
  }

  @Test
  fun predefinedRule_stringMatches() {
    assertFalse(examine("abc1", stringMatches("[a-z]*")).isValid)
    assertFalse(examine("9-09-2009", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")).isValid)
    assertTrue(examine("abc", stringMatches("[a-z]*")).isValid)
    assertTrue(examine("10-10-2010", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")).isValid)
  }

  @Test
  fun predefinedRule_stringNoSpacePadding() {
    assertFalse(examine(" c ", stringNoSpacePadding()).isValid)
    assertFalse(examine("  ", stringNoSpacePadding()).isValid)
    assertFalse(examine(" ", stringNoSpacePadding()).isValid)
    assertFalse(examine("abc ", stringNoSpacePadding()).isValid)
    assertFalse(examine(" abc", stringNoSpacePadding()).isValid)
    assertTrue(examine("abc", stringNoSpacePadding()).isValid)
  }

  @Test
  fun predefinedRule_numberPositive() {
    assertFalse(examine(-1, numberPositive()).isValid)
    assertFalse(examine(-0.00001, numberPositive()).isValid)
    assertFalse(examine(-0.0001f, numberPositive()).isValid)
    assertFalse(examine(0.0, numberPositive()).isValid)
    assertFalse(examine(0f, numberPositive()).isValid)
    assertFalse(examine(0, numberPositive()).isValid)
    assertTrue(examine(null, numberPositive()).isValid)
    assertTrue(examine(5, numberPositive()).isValid)
    assertTrue(examine(125.8f, numberPositive()).isValid)
    assertTrue(examine(125.8, numberPositive()).isValid)
  }

  @Test
  fun predefinedRule_numberNonNegative() {
    assertFalse(examine(-1, numberNonNegative()).isValid)
    assertFalse(examine(-0.00001, numberNonNegative()).isValid)
    assertFalse(examine(-0.0001f, numberNonNegative()).isValid)
    assertTrue(examine(0.0, numberNonNegative()).isValid)
    assertTrue(examine(0f, numberNonNegative()).isValid)
    assertTrue(examine(0, numberNonNegative()).isValid)
    assertTrue(examine(null, numberNonNegative()).isValid)
    assertTrue(examine(5, numberNonNegative()).isValid)
    assertTrue(examine(125.8f, numberNonNegative()).isValid)
    assertTrue(examine(125.8, numberNonNegative()).isValid)
  }

  @Test
  fun predefinedRule_numberInRange() {
    assertFalse(examine(-1, numberInRange(-2, -1.0001f)).isValid)
    assertFalse(examine(100, numberInRange(99, 99.999999)).isValid)
    assertTrue(examine(-1, numberInRange(-2, -1)).isValid)
    assertTrue(examine(-0.00001, numberInRange(-1, 0)).isValid)
    assertTrue(examine(-0.0001f, numberInRange(-0.0001f, -0.0001f)).isValid)
    assertTrue(examine(15, numberInRange(12, 16)).isValid)
    assertTrue(examine(15.000023, numberInRange(15.000022, 15.000024)).isValid)
    assertTrue(examine(null, numberInRange(-100, 100)).isValid)
  }

  @Test
  fun predefinedRule_valueIn() {
    assertTrue(examine("a", valueIn("a", "b", "c")).isValid)
    assertTrue(examine(null, valueIn(null, "a", "b", "c")).isValid)
    assertTrue(examine(765, valueIn(5, 23, 765, 43)).isValid)
    assertTrue(
      examine(
        SimpleTestClass(1, "a", "1", true),
        valueIn(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true))).isValid)
    assertTrue(examine(DayOfWeek.FRIDAY, valueIn(*DayOfWeek.values())).isValid)
    assertFalse(examine("a", valueIn("b", "c", "d")).isValid)
    assertFalse(examine(null, valueIn()).isValid)
    assertFalse(
      examine(SimpleTestClass(1, "a", "1", true),
        valueIn(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false))).isValid)
    assertFalse(examine(Month.APRIL, valueIn(*DayOfWeek.values())).isValid)
  }

  @Test
  fun predefinedRule_valueIn_collection() {
    assertTrue(examine("a", valueIn(listOf("a", "b", "c"))).isValid)
    assertTrue(examine("a", valueIn(setOf("a", "b", "c"))).isValid)
    assertTrue(examine(null, valueIn(listOf(null, "a", "b", "c"))).isValid)
    assertTrue(examine(765, valueIn(listOf(5, 23, 765, 43))).isValid)
    assertTrue(
      examine(SimpleTestClass(1, "a", "1", true),
      valueIn(listOf(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true)))).isValid)
    assertTrue(examine(null, valueIn(listOf<String?>(null))).isValid)
    assertFalse(examine("a", valueIn(listOf("b", "c", "d"))).isValid)
    assertFalse(examine("a", valueIn(setOf("b", "c", "d"))).isValid)
    assertFalse(examine(null, valueIn(emptyList())).isValid)
    assertFalse(examine("a", valueIn(listOf<String?>(null))).isValid)
    assertFalse(
      examine(SimpleTestClass(1, "a", "1", true),
      valueIn(listOf(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false)))).isValid)
  }

  @Test
  fun predefinedRule_valueNotIn() {
    assertTrue(examine("a", valueNotIn("b", "c", "d")).isValid)
    assertTrue(examine(null, valueNotIn()).isValid)
    assertTrue(
      examine(SimpleTestClass(1, "a", "1", true),
        valueNotIn(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false))).isValid)
    assertTrue(examine(Month.APRIL, valueNotIn(*DayOfWeek.values())).isValid)
    assertFalse(examine("a", valueNotIn("a", "b", "c")).isValid)
    assertFalse(examine(null, valueNotIn(null, "a", "b", "c")).isValid)
    assertFalse(examine(765, valueNotIn(5, 23, 765, 43)).isValid)
    assertFalse(
      examine(
        SimpleTestClass(1, "a", "1", true),
        valueNotIn(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true))).isValid)
    assertFalse(examine(DayOfWeek.FRIDAY, valueNotIn(*DayOfWeek.values())).isValid)
  }

  @Test
  fun predefinedRule_valueNotIn_collection() {
    assertTrue(examine("a", valueNotIn(listOf("b", "c", "d"))).isValid)
    assertTrue(examine("a", valueNotIn(setOf("b", "c", "d"))).isValid)
    assertTrue(examine(null, valueNotIn(emptyList())).isValid)
    assertTrue(examine("a", valueNotIn(listOf<String?>(null))).isValid)
    assertTrue(
      examine(SimpleTestClass(1, "a", "1", true),
        valueNotIn(listOf(SimpleTestClass(2, "a", "1", true), SimpleTestClass(1, "a", "1", false)))).isValid)
    assertFalse(examine("a", valueNotIn(listOf("a", "b", "c"))).isValid)
    assertFalse(examine("a", valueNotIn(setOf("a", "b", "c"))).isValid)
    assertFalse(examine(null, valueNotIn(listOf(null, "a", "b", "c"))).isValid)
    assertFalse(examine(765, valueNotIn(listOf(5, 23, 765, 43))).isValid)
    assertFalse(
      examine(SimpleTestClass(1, "a", "1", true),
        valueNotIn(listOf(SimpleTestClass(2, "b", "2", false), SimpleTestClass(1, "a", "1", true)))).isValid)
    assertFalse(examine(null, valueNotIn(listOf<String?>(null))).isValid)
  }

  @Test
  fun predefinedRule_equalTo() {
    assertTrue(examine("a", equalTo("a")).isValid)
    assertTrue(examine(7, equalTo(7)).isValid)
    assertTrue(examine(null, equalTo(null)).isValid)
    assertTrue(examine(SimpleTestClass(1, "a", "1", false), equalTo(SimpleTestClass(1, "a", "1", false))).isValid)
    assertTrue(examine("test", stringAlphanumeric(), notEqualTo("other")).isValid)
    assertFalse(examine(7.0, equalTo(7.000001)).isValid)
    assertFalse(examine("", equalTo(null)).isValid)
    assertFalse(examine("123", equalTo("12")).isValid)
    assertFalse(examine(SimpleTestClass(1, "a", "1", false), equalTo(SimpleTestClass(1, "a", "2", false))).isValid)
  }

  @Test
  fun predefinedRule_notEqualTo() {
    assertTrue(examine(7.0, notEqualTo(7.000001)).isValid)
    assertTrue(examine("", notEqualTo(null)).isValid)
    assertTrue(examine("123", notEqualTo("12")).isValid)
    assertTrue(examine(null, notEqualTo(null)).isValid)
    assertTrue(examine(SimpleTestClass(1, "a", "1", false), notEqualTo(SimpleTestClass(1, "a", "2", false))).isValid)
    assertFalse(examine("a", notEqualTo("a")).isValid)
    assertFalse(examine(7, notEqualTo(7)).isValid)
    assertFalse(examine(SimpleTestClass(1, "a", "1", false), notEqualTo(SimpleTestClass(1, "a", "1", false))).isValid)
  }

  @Test
  fun customRule() {
    val is5Rule = SimpleRule<Long>("IS_5") { v -> v == 5L }
    assertTrue(examine(5L, is5Rule).isValid)
    assertFalse(examine(6L, is5Rule).isValid)
  }

  @Test
  fun multipleRules() {
    assertTrue(examine(10, numberPositive(), numberInRange(-10, 10)).isValid)
    assertTrue(examine("10", stringAlphanumeric(), valueIn("9", "10", "11")).isValid)

    val diagnosis = examine(-11, "patient", numberNonNegative(), numberInRange(-10, 10))
    assertFalse(diagnosis.isValid)
    val objectAilments = diagnosis.ailments["patient"]
    assertEquals(2, objectAilments!!.size.toLong())
  }
}