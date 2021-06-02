package com.miquido.validoctor2

import com.miquido.validoctor2.TestClasses.TestClass
import com.miquido.validoctor2.definition.Rules2.chained
import com.miquido.validoctor2.definition.Rules2.conditional
import com.miquido.validoctor2.definition.Rules2.notNull
import com.miquido.validoctor2.definition.Rules2.stringAlphanumeric
import com.miquido.validoctor2.definition.Rules2.stringMaxLength
import com.miquido.validoctor2.definition.Rules2.stringMinLength
import com.miquido.validoctor2.definition.Rules2.stringNoSpacePadding
import com.miquido.validoctor2.definition.Rules2.stringNotEmpty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConditionalTest {

  @Before
  fun setNonThrowing() {
    Validoctor2.setThrowing(false)
  }

  @Test
  fun singleValueConditional() {
    var diagnosis = Validoctor2.examine("aaa", conditional({ p -> p != "aaa" }, stringMinLength(4)))
    assertTrue(diagnosis.isValid)

    diagnosis = Validoctor2.examine("aaa", conditional({ p -> p == "aaa" }, stringMinLength(4)))
    assertFalse(diagnosis.isValid)

    diagnosis = Validoctor2.examine("aaa", *conditional({ p -> p == "aaa" }, stringMaxLength(3), stringMinLength(4)))
    assertFalse(diagnosis.isValid)
    assertEquals(1, diagnosis.ailments[""]?.size)
    assertEquals("TOO_SHORT", diagnosis.ailments[""]?.first())
  }

  @Test
  fun complexConditionals() {
    val rule = Validoctor2.rulesFor(TestClass::class.java)
      .field("name", *conditional({ true }, stringMaxLength(3), stringAlphanumeric()))
      .field("description", conditional({ false }, stringMinLength(10)))
      .build()
    val patient = TestClass("abc#", "a", "abc", null, 1f, null, null, null, null)
    val diagnosis = Validoctor2.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals(2, diagnosis.ailments["name"]?.size)
    assertTrue(diagnosis.ailments["name"]?.contains("TOO_LONG")!!)
    assertTrue(diagnosis.ailments["name"]?.contains("ALPHANUMERIC_REQUIRED")!!)
    assertEquals(null, diagnosis.ailments["description"])
  }

  @Test
  fun chainedConditionals() {
    val d1 = Validoctor2.examine("abc", chained(notNull(), conditional({ false }, stringMinLength(5))))
    assertTrue(d1.isValid)
    val d2 = Validoctor2.examine("abc", chained(notNull(), conditional({ true }, stringMinLength(5))))
    assertFalse(d2.isValid)
    val d3 = Validoctor2.examine("abc", chained(*conditional({ false }, stringAlphanumeric(), stringMinLength(5))))
    assertTrue(d3.isValid)
    val d4 = Validoctor2.examine("abc", chained(*conditional({ true }, stringAlphanumeric(), stringMinLength(5))))
    assertFalse(d4.isValid)
    assertEquals(1, d4.ailments[""]?.size)
    assertEquals("TOO_SHORT", d4.ailments[""]?.first())
    val d5 = Validoctor2.examine(" abc", stringNoSpacePadding(),
      chained(*conditional({ true }, stringNotEmpty(), stringMinLength(5))))
    assertFalse(d5.isValid)
    assertEquals(2, d5.ailments[""]?.size)
    assertTrue(d5.ailments[""]?.contains("TOO_SHORT")!!)
    assertTrue(d5.ailments[""]?.contains("NO_WHITESPACE_PADDING_REQUIRED")!!)
    val d6 = Validoctor2.examine(" abc",
      chained(conditional({ true }, stringAlphanumeric()), conditional({ true }, stringNoSpacePadding())))
    assertFalse(d6.isValid)
    assertEquals(1, d6.ailments[""]?.size)
    assertTrue(d6.ailments[""]?.contains("ALPHANUMERIC_REQUIRED")!!)
  }

  @Test
  fun conditionalChained() {
    val d1 = Validoctor2.examine("a!bc", conditional({ false }, chained(stringMinLength(5), stringAlphanumeric())))
    assertTrue(d1.isValid)
    val d2 = Validoctor2.examine("a!bc", conditional({ true }, chained(stringMinLength(5), stringAlphanumeric())))
    assertFalse(d2.isValid)
    assertEquals(1, d2.ailments[""]?.size)
    assertEquals("TOO_SHORT", d2.ailments[""]?.first())
    val d3 = Validoctor2.examine("a!bcd", conditional({ true }, chained(stringMinLength(5), stringAlphanumeric())))
    assertFalse(d3.isValid)
    assertEquals(1, d3.ailments[""]?.size)
    assertEquals("ALPHANUMERIC_REQUIRED", d3.ailments[""]?.first())
  }
}