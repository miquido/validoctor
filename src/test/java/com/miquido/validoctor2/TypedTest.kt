package com.miquido.validoctor2

import com.miquido.validoctor2.TestClasses.TestClass
import com.miquido.validoctor2.definition.Rules2
import com.miquido.validoctor2.definition.Rules2.*
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class TypedTest {

  @Before
  fun setNonThrowing() {
    Validoctor2.setThrowing(false)
  }

  @Test
  fun all() {
    val rule = Validoctor2.rulesFor(TestClass::class.java)
      .all(notNull())
      .build()
    val patient = TestClass(null, null, null, null, -2.0f, null, null, null, null)
    val diagnosis = Validoctor2.examine(patient, rule)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["name"]?.first())
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["skuId"]?.first())
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["description"]?.first())
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["weightKg"]?.first())
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["kcal"]?.first())
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["inside"]?.first())
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["intSet"]?.first())
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList"]?.first())
  }

  @Test
  fun allTyped() {
    val rule = Validoctor2.rulesFor(TestClass::class.java)
      .allTyped(String::class.java, stringNotEmpty(), stringTrimmedNotEmpty())
      .allTyped(Float::class.java, numberPositive())
      .build()
    val patient = TestClass(" ", "sku", "desc", 1.0f, 0f, 1, null, null, null)
    val diagnosis = Validoctor2.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.ailments["name"]?.first())
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals(null, diagnosis.ailments["skuId"])
    assertEquals(null, diagnosis.ailments["description"])
    assertEquals(null, diagnosis.ailments["weightKg"])
//    assertEquals(null, diagnosis.ailments["volumeL"]) <- this would be the case in java since volumeL is primitive float
    assertEquals(1, diagnosis.ailments["volumeL"]?.size)
    assertEquals(null, diagnosis.ailments["kcal"])
  }

  @Test
  fun allAssignable() {
    val rule = Validoctor2.rulesFor(TestClass::class.java)
      .allAssignable(Float::class.java, numberPositive())
      .allAssignable(Number::class.java, notNull())
      .build()
    val patient = TestClass("name", "sku", "desc", 1.0f, 0f, null, null, null, null)
    val diagnosis = Validoctor2.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["kcal"]?.first())
    assertEquals(1, diagnosis.ailments["kcal"]?.size)
    assertEquals("POSITIVE_REQUIRED", diagnosis.ailments["volumeL"]?.first())
    assertEquals(1, diagnosis.ailments["volumeL"]?.size)
    assertEquals(null, diagnosis.ailments["weightKg"])
  }
}