package com.miquido.validoctor

import com.miquido.validoctor.definition.Rules
import com.miquido.validoctor.definition.Rules.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.function.BinaryOperator

class DisplayNamesTest {

    @Before
    fun setNonThrowing() {
        Validoctor.setThrowing(false)
    }

    @Test
    fun emptyTopLevelDisplayName() {
        val insideRule = Validoctor.rulesFor(TestClasses.TestInsideClass::class.java)
            .field("name", "specificName1", stringMinLength(5))
            .elements("list", "specificName2", stringNotEmpty())
            .reducedFields("score", "optScore", { a: Double, b: Double -> a + b }, numberInRange(0, 10))
            .fields(listOf("score", "optScore"), numberPositive())
            .build()
        val rule = Validoctor.rulesFor(TestClasses.TestClass::class.java)
            .field("inside", "", insideRule)
            .build()
        val patient = TestClasses.TestClass(
            "abc", null, null, null, 1f, null, TestClasses.TestInsideClass("xdxd", 14.3, 0.0, listOf("", "")), null, null
        )
        val diagnosis1 = Validoctor.examine(patient, "p", rule)
        assertFalse(diagnosis1.isValid)
        assertEquals(1, diagnosis1.ailments["p.specificName1"]?.size)
        assertEquals("TOO_SHORT", diagnosis1.ailments["p.specificName1"]?.first())
        assertEquals(1, diagnosis1.ailments["p.specificName2[0]"]?.size)
        assertEquals("NOT_EMPTY_REQUIRED", diagnosis1.ailments["p.specificName2[0]"]?.first())
        assertEquals(1, diagnosis1.ailments["p.specificName2[1]"]?.size)
        assertEquals("NOT_EMPTY_REQUIRED", diagnosis1.ailments["p.specificName2[1]"]?.first())
        assertEquals(2, diagnosis1.ailments["p.optScore"]?.size)
        assertEquals(1, diagnosis1.ailments["p.score"]?.size)
        val diagnosis2 = Validoctor.examine(patient, rule)
        assertFalse(diagnosis1.isValid)
        assertEquals(1, diagnosis2.ailments["specificName1"]?.size)
        assertEquals("TOO_SHORT", diagnosis2.ailments["specificName1"]?.first())
        assertEquals(1, diagnosis2.ailments["specificName2[0]"]?.size)
        assertEquals("NOT_EMPTY_REQUIRED", diagnosis2.ailments["specificName2[0]"]?.first())
        assertEquals(1, diagnosis2.ailments["specificName2[1]"]?.size)
        assertEquals("NOT_EMPTY_REQUIRED", diagnosis2.ailments["specificName2[1]"]?.first())
        assertEquals(2, diagnosis2.ailments["optScore"]?.size)
        assertEquals(1, diagnosis2.ailments["score"]?.size)
    }
}