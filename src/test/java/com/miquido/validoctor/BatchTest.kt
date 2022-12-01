package com.miquido.validoctor

import com.miquido.validoctor.TestClasses.TestClass
import com.miquido.validoctor.TestClasses.TestInsideClass
import com.miquido.validoctor.definition.Rules.batch
import com.miquido.validoctor.definition.Rules.chained
import com.miquido.validoctor.definition.Rules.collectionNotEmpty
import com.miquido.validoctor.definition.Rules.conditional
import com.miquido.validoctor.definition.Rules.notNull
import com.miquido.validoctor.definition.Rules.numberNonNegative
import com.miquido.validoctor.definition.Rules.numberPositive
import com.miquido.validoctor.definition.Rules.stringAlphabetic
import com.miquido.validoctor.definition.Rules.stringAlphanumeric
import com.miquido.validoctor.definition.Rules.stringMatches
import com.miquido.validoctor.definition.Rules.stringMaxLength
import com.miquido.validoctor.definition.Rules.stringNoSpacePadding
import com.miquido.validoctor.definition.Rules.stringTrimmedNotEmpty
import com.miquido.validoctor.definition.SimpleRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BatchTest {

    val insideRule = Validoctor.rulesFor(TestInsideClass::class.java)
        .field("name", stringAlphabetic(), stringTrimmedNotEmpty())
        .field("score", numberPositive())
        .build()
    val rule1 = Validoctor.rulesFor(TestClass::class.java)
        .allTyped(String::class.java, stringTrimmedNotEmpty())
        .field("inside", notNull<TestInsideClass>())
        .build()
    val rule2 = Validoctor.rulesFor(TestClass::class.java)
        .field("description", stringNoSpacePadding(), stringMaxLength(255))
        .field("intSet", collectionNotEmpty())
        .elements("intSet", numberPositive())
        .build()
    val rule3 = Validoctor.rulesFor(TestClass::class.java)
        .elements("insideList", notNull(), insideRule)
        .field("kcal", numberNonNegative())
        .build()
    val rule4 = Validoctor.rulesFor(TestClass::class.java)
        .field("weightKg", numberPositive())
        .field("skuId", stringNoSpacePadding(), stringAlphanumeric())
        .build()
    val batch1 = batch(rule1, rule2)
    val batch2 = batch(rule3, rule4)

    @Before
    fun setNonThrowing() {
        Validoctor.setThrowing(false)
    }

    @Test
    fun batchSingleValue() {
        val batch = batch(stringTrimmedNotEmpty(), stringNoSpacePadding(), stringAlphanumeric(), stringMaxLength(5))
        var diagnosis = Validoctor.examine("", "test", batch)
        assertEquals(1, diagnosis.ailments["test"]?.size)
        assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.ailments["test"]?.first())
        diagnosis = Validoctor.examine(" ", "test", batch)
        assertEquals(3, diagnosis.ailments["test"]?.size)
        assertTrue(diagnosis.ailments["test"]?.contains("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED") ?: false)
        assertTrue(diagnosis.ailments["test"]?.contains("NO_WHITESPACE_PADDING_REQUIRED") ?: false)
        assertTrue(diagnosis.ailments["test"]?.contains("ALPHANUMERIC_REQUIRED") ?: false)
        diagnosis = Validoctor.examine(" 123#$ ", "test", batch)
        assertEquals(3, diagnosis.ailments["test"]?.size)
        assertTrue(diagnosis.ailments["test"]?.contains("TOO_LONG") ?: false)
        assertTrue(diagnosis.ailments["test"]?.contains("NO_WHITESPACE_PADDING_REQUIRED") ?: false)
        assertTrue(diagnosis.ailments["test"]?.contains("ALPHANUMERIC_REQUIRED") ?: false)
    }

    @Test
    fun batchEmailCase() {
        val batch = batch(stringNoSpacePadding(), stringMatches("\\S+@\\S+\\.\\S+"), stringMaxLength(255))
        val emailNotExistsRule = SimpleRule<String>("EMAIL_AVAILABILITY") { e -> e.startsWith("a") }
        var diagnosis = Validoctor.examine("ffasf.com", "email", chained(batch, emailNotExistsRule))
        assertEquals(1, diagnosis.ailments["email"]?.size)
        assertEquals("MUST_MATCH_REGEX", diagnosis.ailments["email"]?.first())

        diagnosis = Validoctor.examine("ffa@sf.com", "email", chained(batch, emailNotExistsRule))
        assertEquals(1, diagnosis.ailments["email"]?.size)
        assertEquals("EMAIL_AVAILABILITY", diagnosis.ailments["email"]?.first())

        diagnosis = Validoctor.examine("affa@sf.com", "email", chained(batch, emailNotExistsRule))
        assertTrue(diagnosis.isValid)
    }

    @Test
    fun batchRulesForChained() {
        val ins = TestInsideClass("a", 1.0)
        var patient = TestClass(null, null, null, null, -2.0f, null, ins, null, null)
        var diagnosis = Validoctor.examine(patient, chained(batch1, batch2))
        assertTrue(diagnosis.isValid)

        patient = TestClass(null, null, null, null, -2.0f, 1, null, setOf(), ArrayList())
        diagnosis = Validoctor.examine(patient, chained(batch1, batch2))
        assertEquals(2, diagnosis.ailments.size)
        assertEquals("NOT_EMPTY_REQUIRED", diagnosis.ailments["intSet"]?.first())
        assertEquals("NOT_NULL_REQUIRED", diagnosis.ailments["inside"]?.first())

        patient = TestClass(null, null, null, null, -2.0f, -1, ins, setOf(), ArrayList())
        diagnosis = Validoctor.examine(patient, chained(batch1, batch2))
        assertEquals(1, diagnosis.ailments.size)
        assertEquals("NOT_EMPTY_REQUIRED", diagnosis.ailments["intSet"]?.first())
    }

    @Test
    fun batchRulesForConditional() {
        val ins = TestInsideClass("a", 1.0)
        var patient = TestClass(null, null, null, null, -2.0f, null, ins, null, null)
        var diagnosis = Validoctor.examine(patient,
            conditional({ p -> p != null}, batch1),
            conditional({ p -> p.skuId != null }, batch2)
        )
        assertTrue(diagnosis.isValid)

        patient = TestClass(null, "sku", null, 0f, -2.0f, null, ins, setOf(), ArrayList())
        diagnosis = Validoctor.examine(patient,
            conditional({ p -> p != null}, batch1),
            conditional({ p -> p.skuId != null }, batch2)
        )
        assertEquals(2, diagnosis.ailments.size)
        assertEquals("NOT_EMPTY_REQUIRED", diagnosis.ailments["intSet"]?.first())
        assertEquals("POSITIVE_REQUIRED", diagnosis.ailments["weightKg"]?.first())

        patient = TestClass(null, null, null, 0f, -2.0f, -1, ins, null, ArrayList())
        diagnosis = Validoctor.examine(patient,
            conditional({ p -> p != null}, batch1),
            conditional({ p -> p.skuId != null }, batch2)
        )
        assertTrue(diagnosis.isValid)
    }
}