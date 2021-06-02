package com.miquido.validoctor2

import com.miquido.validoctor.TestPatient
import com.miquido.validoctor2.definition.Rules2.collectionContains
import com.miquido.validoctor2.definition.Rules2.collectionMaxSize
import com.miquido.validoctor2.definition.Rules2.collectionMinSize
import com.miquido.validoctor2.definition.Rules2.collectionNotEmpty
import com.miquido.validoctor2.definition.Rules2.collectionSizeIn
import com.miquido.validoctor2.definition.Rules2.equalTo
import com.miquido.validoctor2.definition.Rules2.isFalse
import com.miquido.validoctor2.definition.Rules2.isNull
import com.miquido.validoctor2.definition.Rules2.isTrue
import com.miquido.validoctor2.definition.Rules2.named
import com.miquido.validoctor2.definition.Rules2.notNull
import com.miquido.validoctor2.definition.Rules2.numberInRange
import com.miquido.validoctor2.definition.Rules2.numberNonNegative
import com.miquido.validoctor2.definition.Rules2.numberPositive
import com.miquido.validoctor2.definition.Rules2.stringAlphabetic
import com.miquido.validoctor2.definition.Rules2.stringAlphanumeric
import com.miquido.validoctor2.definition.Rules2.stringContains
import com.miquido.validoctor2.definition.Rules2.stringExactLength
import com.miquido.validoctor2.definition.Rules2.stringLengthInRange
import com.miquido.validoctor2.definition.Rules2.stringMatches
import com.miquido.validoctor2.definition.Rules2.stringMaxLength
import com.miquido.validoctor2.definition.Rules2.stringMinLength
import com.miquido.validoctor2.definition.Rules2.stringNoSpacePadding
import com.miquido.validoctor2.definition.Rules2.stringNotEmpty
import com.miquido.validoctor2.definition.Rules2.stringTrimmedNotEmpty
import com.miquido.validoctor2.definition.Rules2.valueIn
import com.miquido.validoctor2.definition.SimpleRule2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.Boolean
import kotlin.Any
import kotlin.Long
import kotlin.String

class SimpleRulesTest2 {

  @Before
  fun setNonThrowing() {
    Validoctor2.setThrowing(false)
  }

  @Test
  fun predefinedRule_notNull() {
    assertFalse(Validoctor2.examine(null, notNull()).isValid)
    assertTrue(Validoctor2.examine(Any(), notNull()).isValid)
  }

  @Test
  fun predefinedRule_isNull() {
    assertFalse(Validoctor2.examine(Any(), isNull()).isValid)
    assertTrue(Validoctor2.examine(null, isNull()).isValid)
  }

  @Test
  fun predefinedRule_isFalse() {
    assertFalse(Validoctor2.examine(true, isFalse()).isValid)
    assertFalse(Validoctor2.examine(Boolean.TRUE, isFalse()).isValid)
    assertTrue(Validoctor2.examine(null, isFalse()).isValid)
    assertTrue(Validoctor2.examine(false, isFalse()).isValid)
    assertTrue(Validoctor2.examine(Boolean.FALSE, isFalse()).isValid)
  }

  @Test
  fun predefinedRule_isTrue() {
    assertFalse(Validoctor2.examine(false, isTrue()).isValid)
    assertFalse(Validoctor2.examine(Boolean.FALSE, isTrue()).isValid)
    assertTrue(Validoctor2.examine(null, isTrue()).isValid)
    assertTrue(Validoctor2.examine(true, isTrue()).isValid)
    assertTrue(Validoctor2.examine(Boolean.TRUE, isTrue()).isValid)
  }

  @Test
  fun predefinedRule_collectionNotEmpty() {
    assertFalse(Validoctor2.examine(emptyList<Any>(), collectionNotEmpty()).isValid)
    assertFalse(Validoctor2.examine(emptyMap<Any, Any>().values, collectionNotEmpty()).isValid)
    assertFalse(Validoctor2.examine(emptySet<Any>(), collectionNotEmpty()).isValid)
    assertTrue(Validoctor2.examine(setOf(1), collectionNotEmpty()).isValid)
    assertTrue(Validoctor2.examine(listOf(1), collectionNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_collectionMinSize() {
    assertFalse(Validoctor2.examine(emptyList<Any>(), collectionMinSize(1)).isValid)
    assertFalse(Validoctor2.examine(listOf(1, 2, 3, 4), collectionMinSize(5)).isValid)
    assertTrue(Validoctor2.examine(listOf(1, 2, 3, 4), collectionMinSize(4)).isValid)
    assertTrue(Validoctor2.examine(listOf(1, 2, 3, 4), collectionMinSize(2)).isValid)
    assertTrue(Validoctor2.examine(null, collectionMinSize(4)).isValid)
  }

  @Test
  fun predefinedRule_collectionMaxSize() {
    assertFalse(Validoctor2.examine(listOf(1, 2, 3, 4), collectionMaxSize(1)).isValid)
    assertFalse(Validoctor2.examine(listOf(1, 2, 3, 4), collectionMaxSize(3)).isValid)
    assertTrue(Validoctor2.examine(emptyList<String>(), collectionMaxSize(4)).isValid)
    assertTrue(Validoctor2.examine(listOf(1, 2, 3, 4), collectionMaxSize(6)).isValid)
    assertTrue(Validoctor2.examine(null, collectionMaxSize(4)).isValid)
  }

  @Test
  fun predefinedRule_collectionSizeIn() {
    assertFalse(Validoctor2.examine(emptyList<Any>(), collectionSizeIn(1, 5)).isValid)
    assertFalse(Validoctor2.examine(listOf(1, 2, 3, 4), collectionSizeIn(1, 3)).isValid)
    assertFalse(Validoctor2.examine(listOf(1, 2, 3, 4), collectionSizeIn(5, 7)).isValid)
    assertTrue(Validoctor2.examine(listOf(1, 2, 3, 4), collectionSizeIn(3, 7)).isValid)
    assertTrue(Validoctor2.examine(emptyList<Any>(), collectionSizeIn(0, 2)).isValid)
    assertTrue(Validoctor2.examine(null, collectionSizeIn(1, 4)).isValid)
  }

  @Test
  fun predefinedRule_collectionContains() {
    assertFalse(Validoctor2.examine(emptyList<String>(), collectionContains("")).isValid)
    assertFalse(Validoctor2.examine(listOf(1, 2, 3, 4), collectionContains(5)).isValid)
    assertFalse(Validoctor2.examine(listOf("a", "b", "c", "d"), collectionContains("e")).isValid)
    assertFalse(Validoctor2.examine(listOf("a", "b", "c", "d"), collectionContains("e")).isValid)
    assertFalse(Validoctor2.examine(listOf(TestPatient(1, "a", "1", false)),
      collectionContains(TestPatient(1, "b", "1", false))).isValid)
    assertTrue(Validoctor2.examine(listOf(TestPatient(1, "a", "1", false)),
      collectionContains(TestPatient(1, "a", "1", false))).isValid)
    assertTrue(Validoctor2.examine(listOf("a", "b", "c", "d"), collectionContains("d")).isValid)
    assertTrue(Validoctor2.examine(null, collectionContains(1)).isValid)
  }

  @Test
  fun predefinedRule_stringNotEmpty() {
    assertFalse(Validoctor2.examine("", stringNotEmpty()).isValid)
    assertTrue(Validoctor2.examine("   ", stringNotEmpty()).isValid)
    assertTrue(Validoctor2.examine(null, stringNotEmpty()).isValid)
    assertTrue(Validoctor2.examine("aaaa", stringNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_stringTrimmedNotEmpty() {
    assertFalse(Validoctor2.examine("", stringTrimmedNotEmpty()).isValid)
    assertFalse(Validoctor2.examine("   ", stringTrimmedNotEmpty()).isValid)
    assertTrue(Validoctor2.examine(null, stringTrimmedNotEmpty()).isValid)
    assertTrue(Validoctor2.examine("aaaa", stringTrimmedNotEmpty()).isValid)
  }

  @Test
  fun predefinedRule_stringMinLength() {
    assertFalse(Validoctor2.examine("", stringMinLength(1)).isValid)
    assertFalse(Validoctor2.examine("   ", stringMinLength(4)).isValid)
    assertFalse(Validoctor2.examine("aa aa", stringMinLength(6)).isValid)
    assertTrue(Validoctor2.examine(null, stringMinLength(15)).isValid)
    assertTrue(Validoctor2.examine("aa aa", stringMinLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringMaxLength() {
    assertFalse(Validoctor2.examine("aa", stringMaxLength(1)).isValid)
    assertFalse(Validoctor2.examine("   ", stringMaxLength(2)).isValid)
    assertFalse(Validoctor2.examine("aa aa", stringMaxLength(4)).isValid)
    assertTrue(Validoctor2.examine(null, stringMaxLength(1)).isValid)
    assertTrue(Validoctor2.examine("aa aa", stringMaxLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringLengthInRange() {
    assertFalse(Validoctor2.examine("aa", stringLengthInRange(3, 3)).isValid)
    assertFalse(Validoctor2.examine("   ", stringLengthInRange(0, 0)).isValid)
    assertFalse(Validoctor2.examine("aa aa", stringLengthInRange(1, 4)).isValid)
    assertTrue(Validoctor2.examine(null, stringLengthInRange(1, 10)).isValid)
    assertTrue(Validoctor2.examine("aa aa", stringLengthInRange(5, 6)).isValid)
  }

  @Test
  fun predefinedRule_stringExactLength() {
    assertFalse(Validoctor2.examine("aa", stringExactLength(5)).isValid)
    assertFalse(Validoctor2.examine("   ", stringExactLength(2)).isValid)
    assertFalse(Validoctor2.examine("aa aa", stringExactLength(4)).isValid)
    assertFalse(Validoctor2.examine("aa aa", stringExactLength(6)).isValid)
    assertTrue(Validoctor2.examine(null, stringExactLength(1)).isValid)
    assertTrue(Validoctor2.examine("aa aa", stringExactLength(5)).isValid)
  }

  @Test
  fun predefinedRule_stringAlphanumeric() {
    assertFalse(Validoctor2.examine("aa5 ", stringAlphanumeric()).isValid)
    assertFalse(Validoctor2.examine("aa-5", stringAlphanumeric()).isValid)
    assertFalse(Validoctor2.examine("&*(%$", stringAlphanumeric()).isValid)
    assertFalse(Validoctor2.examine("12^1", stringAlphanumeric()).isValid)
    assertTrue(Validoctor2.examine(null, stringAlphanumeric()).isValid)
    assertTrue(Validoctor2.examine("aa5aa", stringAlphanumeric()).isValid)
    assertTrue(Validoctor2.examine("aaaa", stringAlphanumeric()).isValid)
    assertTrue(Validoctor2.examine("555", stringAlphanumeric()).isValid)
  }

  @Test
  fun predefinedRule_stringAlphabetic() {
    assertFalse(Validoctor2.examine("aa5", stringAlphabetic()).isValid)
    assertFalse(Validoctor2.examine("a a", stringAlphabetic()).isValid)
    assertFalse(Validoctor2.examine("aa-aa", stringAlphabetic()).isValid)
    assertFalse(Validoctor2.examine("*7^%", stringAlphabetic()).isValid)
    assertTrue(Validoctor2.examine(null, stringAlphabetic()).isValid)
    assertTrue(Validoctor2.examine("aaaa", stringAlphabetic()).isValid)
  }

  @Test
  fun predefinedRule_stringContains() {
    assertFalse(Validoctor2.examine("aaaaa", stringContains("b")).isValid)
    assertFalse(Validoctor2.examine("", stringContains("b")).isValid)
    assertFalse(Validoctor2.examine("aa", stringContains("aaa")).isValid)
    assertTrue(Validoctor2.examine("aaa", stringContains("aaa")).isValid)
    assertTrue(Validoctor2.examine(null, stringContains("aaa")).isValid)
  }

  @Test
  fun predefinedRule_stringMatches() {
    assertFalse(Validoctor2.examine("abc1", stringMatches("[a-z]*")).isValid)
    assertFalse(Validoctor2.examine("9-09-2009", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")).isValid)
    assertTrue(Validoctor2.examine("abc", stringMatches("[a-z]*")).isValid)
    assertTrue(Validoctor2.examine("10-10-2010", stringMatches("[0-9]{2}-[0-9]{2}-[0-9]{4}")).isValid)
  }

  @Test
  fun predefinedRule_stringNoSpacePadding() {
    assertFalse(Validoctor2.examine(" c ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor2.examine("  ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor2.examine(" ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor2.examine("abc ", stringNoSpacePadding()).isValid)
    assertFalse(Validoctor2.examine(" abc", stringNoSpacePadding()).isValid)
    assertTrue(Validoctor2.examine("abc", stringNoSpacePadding()).isValid)
  }

  @Test
  fun predefinedRule_numberPositive() {
    assertFalse(Validoctor2.examine(-1, numberPositive()).isValid)
    assertFalse(Validoctor2.examine(-0.00001, numberPositive()).isValid)
    assertFalse(Validoctor2.examine(-0.0001f, numberPositive()).isValid)
    assertFalse(Validoctor2.examine(0.0, numberPositive()).isValid)
    assertFalse(Validoctor2.examine(0f, numberPositive()).isValid)
    assertFalse(Validoctor2.examine(0, numberPositive()).isValid)
    assertTrue(Validoctor2.examine(null, numberPositive()).isValid)
    assertTrue(Validoctor2.examine(5, numberPositive()).isValid)
    assertTrue(Validoctor2.examine(125.8f, numberPositive()).isValid)
    assertTrue(Validoctor2.examine(125.8, numberPositive()).isValid)
  }

  @Test
  fun predefinedRule_numberNonNegative() {
    assertFalse(Validoctor2.examine(-1, numberNonNegative()).isValid)
    assertFalse(Validoctor2.examine(-0.00001, numberNonNegative()).isValid)
    assertFalse(Validoctor2.examine(-0.0001f, numberNonNegative()).isValid)
    assertTrue(Validoctor2.examine(0.0, numberNonNegative()).isValid)
    assertTrue(Validoctor2.examine(0f, numberNonNegative()).isValid)
    assertTrue(Validoctor2.examine(0, numberNonNegative()).isValid)
    assertTrue(Validoctor2.examine(null, numberNonNegative()).isValid)
    assertTrue(Validoctor2.examine(5, numberNonNegative()).isValid)
    assertTrue(Validoctor2.examine(125.8f, numberNonNegative()).isValid)
    assertTrue(Validoctor2.examine(125.8, numberNonNegative()).isValid)
  }

  @Test
  fun predefinedRule_numberInRange() {
    assertFalse(Validoctor2.examine(-1, numberInRange(-2, -1.0001f)).isValid)
    assertFalse(Validoctor2.examine(100, numberInRange(99, 99.999999)).isValid)
    assertTrue(Validoctor2.examine(-1, numberInRange(-2, -1)).isValid)
    assertTrue(Validoctor2.examine(-0.00001, numberInRange(-1, 0)).isValid)
    assertTrue(Validoctor2.examine(-0.0001f, numberInRange(-0.0001f, -0.0001f)).isValid)
    assertTrue(Validoctor2.examine(15, numberInRange(12, 16)).isValid)
    assertTrue(Validoctor2.examine(15.000023, numberInRange(15.000022, 15.000024)).isValid)
    assertTrue(Validoctor2.examine(null, numberInRange(-100, 100)).isValid)
  }

  @Test
  fun predefinedRule_valueIn() {
    assertTrue(Validoctor2.examine("a", valueIn("a", "b", "c")).isValid)
    assertTrue(Validoctor2.examine(null, valueIn(null, "a", "b", "c")).isValid)
    assertTrue(Validoctor2.examine(765, valueIn(5, 23, 765, 43)).isValid)
    assertTrue(Validoctor2.examine(TestPatient(1, "a", "1", true),
        valueIn(TestPatient(2, "b", "2", false), TestPatient(1, "a", "1", true))).isValid)
    assertFalse(Validoctor2.examine("a", valueIn("b", "c", "d")).isValid)
    assertFalse(Validoctor2.examine(null, valueIn()).isValid)
    assertFalse(Validoctor2.examine(TestPatient(1, "a", "1", true),
        valueIn(TestPatient(2, "a", "1", true), TestPatient(1, "a", "1", false))).isValid)
  }

  @Test
  fun predefinedRule_valueIn_collection() {
    assertTrue(Validoctor2.examine("a", valueIn(listOf("a", "b", "c"))).isValid)
    assertTrue(Validoctor2.examine("a", valueIn(setOf("a", "b", "c"))).isValid)
    assertTrue(Validoctor2.examine(null, valueIn(listOf(null, "a", "b", "c"))).isValid)
    assertTrue(Validoctor2.examine(765, valueIn(listOf(5, 23, 765, 43))).isValid)
    assertTrue(Validoctor2.examine(TestPatient(1, "a", "1", true),
      valueIn(listOf(TestPatient(2, "b", "2", false), TestPatient(1, "a", "1", true)))).isValid)
    assertTrue(Validoctor2.examine(null, valueIn(listOf<String?>(null))).isValid)
    assertFalse(Validoctor2.examine("a", valueIn(listOf("b", "c", "d"))).isValid)
    assertFalse(Validoctor2.examine("a", valueIn(setOf("b", "c", "d"))).isValid)
    assertFalse(Validoctor2.examine(null, valueIn(emptyList())).isValid)
    assertFalse(Validoctor2.examine("a", valueIn(listOf<String?>(null))).isValid)
    assertFalse(Validoctor2.examine(TestPatient(1, "a", "1", true),
      valueIn(listOf(TestPatient(2, "a", "1", true), TestPatient(1, "a", "1", false)))).isValid)
  }

  @Test
  fun predefinedRule_equalTo() {
    assertTrue(Validoctor2.examine("a", equalTo("a")).isValid)
    assertTrue(Validoctor2.examine(7, equalTo(7)).isValid)
    assertTrue(Validoctor2.examine(null, equalTo(null)).isValid)
    assertTrue(Validoctor2.examine(TestPatient(1, "a", "1", false), equalTo(TestPatient(1, "a", "1", false))).isValid)
    assertFalse(Validoctor2.examine(7.0, equalTo(7.000001)).isValid)
    assertFalse(Validoctor2.examine("", equalTo(null)).isValid)
    assertFalse(Validoctor2.examine("123", equalTo("12")).isValid)
    assertFalse(Validoctor2.examine(TestPatient(1, "a", "1", false), equalTo(TestPatient(1, "a", "2", false))).isValid)
  }

  @Test
  fun namedRule() {
    val name = "custom_name"
    val diagnosis = Validoctor2.examine("a", "patient", named(name, stringMinLength(2)))
    val ailments = diagnosis.ailments["patient"]
    assertTrue(ailments!!.any { ailment -> ailment == name })
    assertTrue(Validoctor2.examine("a", "patient", named(name, stringMaxLength(2))).isValid)
  }

  @Test
  fun customRule() {
    val is5Rule = SimpleRule2<Long>("IS_5") { v -> v == 5L }
    assertTrue(Validoctor2.examine(5L, is5Rule).isValid)
    assertFalse(Validoctor2.examine(6L, is5Rule).isValid)
  }

  @Test
  fun multipleRules() {
    assertTrue(Validoctor2.examine(10, numberPositive(), numberInRange(-10, 10)).isValid)
    assertTrue(Validoctor2.examine("10", stringAlphanumeric(), valueIn("9", "10", "11")).isValid)

    val diagnosis = Validoctor2.examine(-11, "patient", numberNonNegative(), numberInRange(-10, 10))
    assertFalse(diagnosis.isValid)
    val objectAilments = diagnosis.ailments["patient"]
    assertEquals(2, objectAilments!!.size.toLong())
  }
}