package com.miquido.validoctor

import com.miquido.validoctor.TestClasses.TestClass
import com.miquido.validoctor.TestClasses.TestInsideClass
import com.miquido.validoctor.definition.Rules.chained
import com.miquido.validoctor.definition.Rules.collectionMinSize
import com.miquido.validoctor.definition.Rules.collectionNotEmpty
import com.miquido.validoctor.definition.Rules.notNull
import com.miquido.validoctor.definition.Rules.numberPositive
import com.miquido.validoctor.definition.Rules.stringExactLength
import com.miquido.validoctor.definition.Rules.stringMaxLength
import com.miquido.validoctor.definition.Rules.stringMinLength
import com.miquido.validoctor.definition.Rules.stringNoSpacePadding
import com.miquido.validoctor.definition.Rules.stringNotEmpty
import com.miquido.validoctor.definition.Rules.stringTrimmedNotEmpty
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class ChainedTest {

  @Before
  fun setNonThrowing() {
    Validoctor.setThrowing(false)
  }

  @Test
  fun singleValueChained() {
    val diagnosis = Validoctor.examine("aaaa", chained(stringNotEmpty(), stringMinLength(5), stringMaxLength(10)))
    assertEquals(1, diagnosis.ailments[""]?.size)
    assertEquals("TOO_SHORT", diagnosis.ailments[""]?.first())
  }

  @Test
  fun chainedAlongWithNonchained() {
    val diagnosis = Validoctor.examine("aaa ", stringNoSpacePadding(),  chained(stringNotEmpty(), stringMinLength(5), stringMaxLength(10)))
    assertEquals(2, diagnosis.ailments[""]?.size)
    assertTrue(diagnosis.ailments[""]?.contains("TOO_SHORT")!!)
    assertTrue(diagnosis.ailments[""]?.contains("NO_WHITESPACE_PADDING_REQUIRED")!!)
  }

  @Test
  fun chainedFieldRules() {
    val rule = Validoctor.rulesFor(TestInsideClass::class.java)
      .field("name", chained(notNull(), stringNotEmpty(), stringTrimmedNotEmpty(), stringMinLength(3), stringExactLength(4)))
      .build()

    var patient = TestInsideClass(null, 0.0)
    var diagnosis = Validoctor.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size) // <- assert further batches were not executed
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("", 0.0)
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("NOT_EMPTY_REQUIRED", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("  ", 0.0)
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("aa", 0.0)
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("TOO_SHORT", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("aaa", 0.0)
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals(1, diagnosis.ailments["name"]?.size)
    assertEquals("INVALID_LENGTH", diagnosis.ailments["name"]!!.first())

    patient = TestInsideClass("aaaa", 0.0)
    diagnosis = Validoctor.examine(patient, rule)
    assertTrue(diagnosis.isValid)
  }

  @Test
  fun chainedCollectionRules() {
    val insideRule = Validoctor.rulesFor(TestInsideClass::class.java)
      .field("name", notNull(), stringTrimmedNotEmpty())
      .field("score", notNull(), numberPositive())
      .build()
    val rule = Validoctor.rulesFor(TestClass::class.java)
      .field("insideList", chained(notNull(), collectionNotEmpty(), collectionMinSize(2)))
      .elements("insideList", chained(notNull(), insideRule))
      .build()

    var patient = TestClass(null, null, null, null, -2.0f, null, null, null, null)
    var diagnosis = Validoctor.examine(patient, rule)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList"]?.first())
    assertEquals(1, diagnosis.ailments["insideList"]?.size)

    patient = TestClass(null, null, null, null, -2.0f, null, null, null, ArrayList())
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals("NOT_EMPTY_REQUIRED", diagnosis.ailments["insideList"]?.first())
    assertEquals(1, diagnosis.ailments["insideList"]?.size)

    val inside1 = TestInsideClass("n1", 0.0)
    patient = TestClass(null, null, null, null, -2.0f, null, null, null, listOf(inside1))
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals("SIZE_TOO_LITTLE", diagnosis.ailments["insideList"]?.first())
    assertEquals(1, diagnosis.ailments["insideList"]?.size)

    patient = TestClass(null, null, null, null, -2.0f, null, null, null, listOf(null, null, inside1))
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList[0]"]?.first())
    assertEquals(1, diagnosis.ailments["insideList[0]"]?.size)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList[1]"]?.first())
    assertEquals(1, diagnosis.ailments["insideList[1]"]?.size)
    assertEquals("POSITIVE_REQUIRED", diagnosis.ailments["insideList[2].score"]?.first())
    assertEquals(null, diagnosis.ailments["insideList[2]"])
    assertEquals(1, diagnosis.ailments["insideList[2].score"]?.size)

    val inside2 = TestInsideClass("", 2.0)
    patient = TestClass(null, null, null, null, -2.0f, null, null, null, listOf(inside1, inside2))
    diagnosis = Validoctor.examine(patient, rule)
    assertEquals("POSITIVE_REQUIRED", diagnosis.ailments["insideList[0].score"]?.first())
    assertEquals(1, diagnosis.ailments["insideList[0].score"]?.size)
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.ailments["insideList[1].name"]!!.first())
    assertEquals(1, diagnosis.ailments["insideList[1].name"]?.size)
    assertEquals(2, diagnosis.ailments.size)
  }
}