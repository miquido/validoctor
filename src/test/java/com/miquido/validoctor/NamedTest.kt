package com.miquido.validoctor

import com.miquido.validoctor.definition.Rules.collectionMinSize
import com.miquido.validoctor.definition.Rules.collectionNotEmpty
import com.miquido.validoctor.definition.Rules.named
import com.miquido.validoctor.definition.Rules.notNull
import com.miquido.validoctor.definition.Rules.numberInRange
import com.miquido.validoctor.definition.Rules.numberPositive
import com.miquido.validoctor.definition.Rules.stringMaxLength
import com.miquido.validoctor.definition.Rules.stringMinLength
import com.miquido.validoctor.definition.Rules.stringNoSpacePadding
import com.miquido.validoctor.definition.Rules.stringNotEmpty
import com.miquido.validoctor.definition.Rules.stringTrimmedNotEmpty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NamedTest {

    @Before
    fun setNonThrowing() {
        Validoctor.setThrowing(false)
    }

    @Test
    fun namedSimpleRule() {
        val name = "custom_name"
        val diagnosis = Validoctor.examine("a", "patient", named(name, stringMinLength(2)))
        val ailments = diagnosis.ailments["patient"]
        assertTrue(ailments!!.any { ailment -> ailment == name })
        assertTrue(Validoctor.examine("a", "patient", named(name, stringMaxLength(2))).isValid)
    }

    @Test
    fun namedRules() {
        val rule = Validoctor.rulesFor(TestClasses.TestClass::class.java)
            .field("name", "NAME", stringNotEmpty(), stringTrimmedNotEmpty(), stringNoSpacePadding())
            .field("kcal", notNull(), numberPositive(), numberInRange(0, 10000))
            .field("intSet", notNull(), collectionNotEmpty(), collectionMinSize(3))
            .build()
        val patient = TestClasses.TestClass(" abc ", null, null, null, 1f, 10001, null, setOf(1, 2), null)
        val diagnosis = Validoctor.examine(patient, "p", named("OVERRIDE", rule))
        assertFalse(diagnosis.isValid)
        assertEquals(1, diagnosis.ailments["p.NAME"]?.size)
        assertEquals("OVERRIDE", diagnosis.ailments["p.NAME"]?.first())
        assertEquals(1, diagnosis.ailments["p.kcal"]?.size)
        assertEquals("OVERRIDE", diagnosis.ailments["p.kcal"]?.first())
        assertEquals(1, diagnosis.ailments["p.intSet"]?.size)
        assertEquals("OVERRIDE", diagnosis.ailments["p.intSet"]?.first())
    }
}