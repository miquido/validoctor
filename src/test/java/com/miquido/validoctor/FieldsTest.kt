package com.miquido.validoctor

import com.miquido.validoctor.TestClasses.TestClass
import com.miquido.validoctor.definition.Rules.chained
import com.miquido.validoctor.definition.Rules.collectionMinSize
import com.miquido.validoctor.definition.Rules.collectionNotEmpty
import com.miquido.validoctor.definition.Rules.conditional
import com.miquido.validoctor.definition.Rules.notNull
import com.miquido.validoctor.definition.Rules.numberInRange
import com.miquido.validoctor.definition.Rules.numberPositive
import com.miquido.validoctor.definition.Rules.stringAlphabetic
import com.miquido.validoctor.definition.Rules.stringLengthInRange
import com.miquido.validoctor.definition.Rules.stringMaxLength
import com.miquido.validoctor.definition.Rules.stringNoSpacePadding
import com.miquido.validoctor.definition.Rules.stringNotEmpty
import com.miquido.validoctor.definition.Rules.stringTrimmedNotEmpty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FieldsTest {

  @Before
  fun setNonThrowing() {
    Validoctor.setThrowing(false)
  }

  @Test
  fun fieldTest() {
    val rule = Validoctor.rulesFor(TestClass::class.java)
      .field("name", "NAME", stringNotEmpty(), stringTrimmedNotEmpty(), stringNoSpacePadding(), stringLengthInRange(4, 20))
      .field("kcal", notNull(), numberPositive(), numberInRange(0, 10000))
      .field("intSet", notNull(), collectionNotEmpty(), collectionMinSize(3))
      .build()
    val patient = TestClass(" abc ", null, null, null, 1f, 10001, null, setOf(1, 2), null)
    val diagnosis = Validoctor.examine(patient, "p", rule)
    assertFalse(diagnosis.isValid)
    assertEquals(1, diagnosis.ailments["p.NAME"]?.size)
    assertEquals("NO_WHITESPACE_PADDING_REQUIRED", diagnosis.ailments["p.NAME"]?.first())
    assertEquals(1, diagnosis.ailments["p.kcal"]?.size)
    assertEquals("TOO_LOW_OR_TOO_HIGH", diagnosis.ailments["p.kcal"]?.first())
    assertEquals(1, diagnosis.ailments["p.intSet"]?.size)
    assertEquals("SIZE_TOO_LITTLE", diagnosis.ailments["p.intSet"]?.first())
  }

  @Test
  fun fieldsTest() {
    val rule = Validoctor.rulesFor(TestClass::class.java)
      .fields(listOf("name", "description", "skuId"), stringTrimmedNotEmpty(), chained(stringAlphabetic(), stringMaxLength(10)))
      .fields(listOf("intSet", "insideList"), collectionNotEmpty(), conditional({ p -> p != null }, collectionMinSize(2)))
      .build()
    val patient = TestClass("123", " ", "abcde abcde", null, 1f, null, null, setOf(1), listOf(null))
    val diagnosis = Validoctor.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("ALPHABETIC_REQUIRED", diagnosis.ailments["name"]?.first())
    assertEquals(1, diagnosis.ailments["description"]?.size)
    assertEquals("ALPHABETIC_REQUIRED", diagnosis.ailments["description"]?.first())
    assertEquals(2, diagnosis.ailments["skuId"]?.size)
    assertTrue(diagnosis.ailments["skuId"]?.contains("ALPHABETIC_REQUIRED")!!)
    assertTrue(diagnosis.ailments["skuId"]?.contains("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED")!!)
    assertEquals(1, diagnosis.ailments["intSet"]?.size)
    assertTrue(diagnosis.ailments["intSet"]?.contains("SIZE_TOO_LITTLE")!!)
    assertEquals(1, diagnosis.ailments["insideList"]?.size)
    assertTrue(diagnosis.ailments["insideList"]?.contains("SIZE_TOO_LITTLE")!!)
  }
}