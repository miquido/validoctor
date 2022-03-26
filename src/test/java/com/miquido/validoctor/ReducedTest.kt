package com.miquido.validoctor

import com.miquido.validoctor.TestClasses.TestClass
import com.miquido.validoctor.definition.Rules.chained
import com.miquido.validoctor.definition.Rules.conditional
import com.miquido.validoctor.definition.Rules.numberInRange
import com.miquido.validoctor.definition.Rules.stringAlphanumeric
import com.miquido.validoctor.definition.Rules.stringMaxLength
import com.miquido.validoctor.definition.Rules.stringNoSpacePadding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class ReducedTest {

  @Before
  fun setNonThrowing() {
    Validoctor.setThrowing(false)
  }

  @Test
  fun reducedFields() {
    val rule = Validoctor.rulesFor(TestClass::class.java)
      .reducedFields("weightKg", "volumeL", { a: Float, b: Float -> a + b }, numberInRange(0, 10))
      .reducedFields("name", "skuId", String::plus, stringMaxLength(6), stringAlphanumeric(), stringNoSpacePadding())
      .build()
    val patient = TestClass("abcd!", "sku1 ", null, 7.0f, 4.0f, null, null, null, null)
    val diagnosis = Validoctor.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals(1, diagnosis.ailments["weightKg"]?.size)
    assertEquals(1, diagnosis.ailments["volumeL"]?.size)
    assertEquals("TOO_LOW_OR_TOO_HIGH", diagnosis.ailments["weightKg"]?.first())
    assertEquals("TOO_LOW_OR_TOO_HIGH", diagnosis.ailments["volumeL"]?.first())
    assertEquals(3, diagnosis.ailments["skuId"]?.size)
    assertEquals(3, diagnosis.ailments["name"]?.size)
  }

  @Test
  fun reducedFieldsWithWrappedRules() {
    val rule = Validoctor.rulesFor(TestClass::class.java)
      .reducedFields("weightKg", "volumeL", { a: Float, b: Float -> a + b }, conditional({ false }, numberInRange(0, 10)))
      .reducedFields("name", "skuId", String::plus, chained(stringMaxLength(6), stringAlphanumeric(), stringNoSpacePadding()))
      .build()
    val patient = TestClass("abcd!", "sku1 ", null, 7.0f, 4.0f, null, null, null, null)
    val diagnosis = Validoctor.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals(null, diagnosis.ailments["weightKg"])
    assertEquals(null, diagnosis.ailments["volumeL"])
    assertEquals(1, diagnosis.ailments["skuId"]?.size)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("TOO_LONG", diagnosis.ailments["skuId"]?.first())
    assertEquals("TOO_LONG", diagnosis.ailments["name"]?.first())
  }
}