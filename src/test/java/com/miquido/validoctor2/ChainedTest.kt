package com.miquido.validoctor2

import com.miquido.validoctor2.TestClasses.TestClass
import com.miquido.validoctor2.TestClasses.TestInsideClass
import com.miquido.validoctor2.definition.Rules2.chained
import com.miquido.validoctor2.definition.Rules2.collectionMinSize
import com.miquido.validoctor2.definition.Rules2.collectionNotEmpty
import com.miquido.validoctor2.definition.Rules2.notNull
import com.miquido.validoctor2.definition.Rules2.numberPositive
import com.miquido.validoctor2.definition.Rules2.stringExactLength
import com.miquido.validoctor2.definition.Rules2.stringMaxLength
import com.miquido.validoctor2.definition.Rules2.stringMinLength
import com.miquido.validoctor2.definition.Rules2.stringNoSpacePadding
import com.miquido.validoctor2.definition.Rules2.stringNotEmpty
import com.miquido.validoctor2.definition.Rules2.stringTrimmedNotEmpty
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class ChainedTest {

  @Before
  fun setNonThrowing() {
    Validoctor2.setThrowing(false)
  }

  @Test
  fun singleValueChained() {
    val diagnosis = Validoctor2.examine("aaaa", chained(stringNotEmpty(), stringMinLength(5), stringMaxLength(10)))
    assertEquals(1, diagnosis.ailments[""]?.size)
    assertEquals("TOO_SHORT", diagnosis.ailments[""]?.first())
  }

  @Test
  fun chainedAlongWithNonchained() {
    val diagnosis = Validoctor2.examine("aaa ", stringNoSpacePadding(),  chained(stringNotEmpty(), stringMinLength(5), stringMaxLength(10)))
    assertEquals(2, diagnosis.ailments[""]?.size)
    assertTrue(diagnosis.ailments[""]?.contains("TOO_SHORT")!!)
    assertTrue(diagnosis.ailments[""]?.contains("NO_WHITESPACE_PADDING_REQUIRED")!!)
  }

  @Test
  fun chainedFieldRules() {
    val rule = Validoctor2.rulesFor(TestInsideClass::class.java)
      .field("name", chained(notNull(), stringNotEmpty(), stringTrimmedNotEmpty(), stringMinLength(3), stringExactLength(4)))
      .build()

    var patient = TestInsideClass(null, 0.0)
    var diagnosis = Validoctor2.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size) // <- assert further batches were not executed
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("", 0.0)
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("NOT_EMPTY_REQUIRED", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("  ", 0.0)
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("aa", 0.0)
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("TOO_SHORT", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("aaa", 0.0)
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("INVALID_LENGTH", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("aaaa", 0.0)
    diagnosis = Validoctor2.examine(patient, rule)
    assertTrue(diagnosis.isValid)
  }

  @Test
  fun chainedCollectionRules() {
    val insideRule = Validoctor2.rulesFor(TestInsideClass::class.java)
      .field("name", notNull(), stringTrimmedNotEmpty())
      .field("score", notNull(), numberPositive())
      .build()
    val rule = Validoctor2.rulesFor(TestClass::class.java)
      .field("insideList", chained(notNull(), collectionNotEmpty(), collectionMinSize(2)))
      .elements("insideList", chained(notNull(), insideRule))
      .build()

    var patient = TestClass(null, null, null, null, -2.0f, null, null, null, null)
    var diagnosis = Validoctor2.examine(patient, rule)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList"]?.first())
    assertEquals(1, diagnosis.ailments["insideList"]?.size)

    patient = TestClass(null, null, null, null, -2.0f, null, null, null, ArrayList())
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals("NOT_EMPTY_REQUIRED", diagnosis.ailments["insideList"]?.first())
    assertEquals(1, diagnosis.ailments["insideList"]?.size)

    val inside1 = TestInsideClass("n1", 0.0)
    patient = TestClass(null, null, null, null, -2.0f, null, null, null, listOf(inside1))
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals("SIZE_TOO_LITTLE", diagnosis.ailments["insideList"]?.first())
    assertEquals(1, diagnosis.ailments["insideList"]?.size)

    patient = TestClass(null, null, null, null, -2.0f, null, null, null, listOf(null, null, inside1))
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList[0]"]?.first())
    assertEquals(1, diagnosis.ailments["insideList[0]"]?.size)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList[1]"]?.first())
    assertEquals(1, diagnosis.ailments["insideList[1]"]?.size)
    assertEquals("POSITIVE_REQUIRED", diagnosis.ailments["insideList[2].score"]?.first())
    assertEquals(null, diagnosis.ailments["insideList[2]"])
    assertEquals(1, diagnosis.ailments["insideList[2].score"]?.size)

    val inside2 = TestInsideClass("", 2.0)
    patient = TestClass(null, null, null, null, -2.0f, null, null, null, listOf(inside1, inside2))
    diagnosis = Validoctor2.examine(patient, rule)
    assertEquals("POSITIVE_REQUIRED", diagnosis.ailments["insideList[0].score"]?.first())
    assertEquals(1, diagnosis.ailments["insideList[0].score"]?.size)
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.ailments["insideList[1].name"]!!.first())
    assertEquals(1, diagnosis.ailments["insideList[1].name"]?.size)
    assertEquals(2, diagnosis.ailments.size)
  }
}