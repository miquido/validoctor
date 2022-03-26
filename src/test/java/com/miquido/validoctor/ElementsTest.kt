package com.miquido.validoctor

import com.miquido.validoctor.TestClasses.TestClass
import com.miquido.validoctor.TestClasses.TestInsideClass
import com.miquido.validoctor.definition.Rules.chained
import com.miquido.validoctor.definition.Rules.collectionNotEmpty
import com.miquido.validoctor.definition.Rules.conditional
import com.miquido.validoctor.definition.Rules.notNull
import com.miquido.validoctor.definition.Rules.numberInRange
import com.miquido.validoctor.definition.Rules.numberPositive
import com.miquido.validoctor.definition.Rules.stringNotEmpty
import com.miquido.validoctor.definition.Rules.stringTrimmedNotEmpty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class ElementsTest {

  @Before
  fun setNonThrowing() {
    Validoctor.setThrowing(false)
  }

  @Test
  fun elementsTest() {
    val insideRule = Validoctor.rulesFor(TestInsideClass::class.java)
      .field("name", notNull(), stringTrimmedNotEmpty())
      .field("score", notNull(), numberPositive())
      .build()
    val rule = Validoctor.rulesFor(TestClass::class.java)
      .elements("intSet", numberPositive(), numberInRange(2, 5))
      .field("insideList", notNull(), collectionNotEmpty())
      .elements("insideList", notNull(), insideRule)
      .build()
    var patient = TestClass(null, null, null, null, 1f, null, null, setOf(1, 2),
      listOf(TestInsideClass(null, 1.0), TestInsideClass("abc", 1.0)))
    var diagnosis = Validoctor.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals(1, diagnosis.ailments["intSet[0]"]?.size)
    assertEquals("TOO_LOW_OR_TOO_HIGH", diagnosis.ailments["intSet[0]"]?.first())
    assertEquals(null, diagnosis.ailments["intSet[1]"])
    assertEquals(null, diagnosis.ailments["insideList"])
    assertEquals(1, diagnosis.ailments["insideList[0].name"]?.size)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList[0].name"]?.first())
    assertEquals(null, diagnosis.ailments["insideList[1]"])
    assertEquals(null, diagnosis.ailments["insideList[1].name"])

    patient = TestClass(null, null, null, null, 1f, null, null, setOf(2, 3), null)
    diagnosis = Validoctor.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals(null, diagnosis.ailments["intSet"])
    assertEquals(1, diagnosis.ailments["insideList"]?.size)
    assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["insideList"]?.first())
    assertEquals(null, diagnosis.ailments["insideList[0]"])
  }

  @Test
  fun elementsTestWithWrappedRules() {
    val insideRule = Validoctor.rulesFor(TestInsideClass::class.java)
      .field("name", chained(notNull(), stringNotEmpty(), stringTrimmedNotEmpty()))
      .field("score", conditional({ p -> p != 0 }, numberPositive()))
      .build()
    val rule = Validoctor.rulesFor(TestClass::class.java)
      .elements("intSet", chained(numberPositive(), numberInRange(2, 5)))
      .elements("insideList", conditional({ p -> p != null }, insideRule))
      .build()
    val patient = TestClass(null, null, null, null, 1f, null, null, setOf(0, 2, 6),
      listOf(TestInsideClass("  ", 1.0), null))
    val diagnosis = Validoctor.examine(patient, rule)
    assertFalse(diagnosis.isValid)
    assertEquals(1, diagnosis.ailments["intSet[0]"]?.size)
    assertEquals("POSITIVE_REQUIRED", diagnosis.ailments["intSet[0]"]?.first())
    assertEquals(null, diagnosis.ailments["intSet[1]"])
    assertEquals(1, diagnosis.ailments["intSet[2]"]?.size)
    assertEquals("TOO_LOW_OR_TOO_HIGH", diagnosis.ailments["intSet[2]"]?.first())
    assertEquals(null, diagnosis.ailments["insideList[0]"])
    assertEquals(null, diagnosis.ailments["insideList[1]"])
    assertEquals(1, diagnosis.ailments["insideList[0].name"]?.size)
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.ailments["insideList[0].name"]?.first())
    assertEquals(null, diagnosis.ailments["insideList[0].score"])
  }
}