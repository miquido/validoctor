package com.miquido.validoctor.complexcases

import com.miquido.validoctor.TestUtil.*
import com.miquido.validoctor.Validoctor
import com.miquido.validoctor.ailment.Severity
import com.miquido.validoctor.multirule.MultiRule
import com.miquido.validoctor.rule.Rules.*
import com.miquido.validoctor.rule.SimpleRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.function.Predicate

class ComplexCasesTest {

  val validoctor: Validoctor = Validoctor.builder().build()
  val product: Product by lazy { makeProduct() }

  private fun makeProduct() : Product {
    val tags = setOf(Tag.DOUGH, Tag.FRUIT, Tag.VEGETABLE)
    val comments = listOf(Comment(1, "text1", listOf(2, 3, 4), listOf(5, 6)),
        Comment(2, "text2", listOf(1, 2), listOf(3)), Comment(1, "text3", listOf(4, 5), listOf(2, 3)))
    val nutritionFacts = NutritionFacts(287, 17.0, 32.5, 0.04)
    val reviewScores = mutableListOf(2, 3, 3, 4, 4, 4, 5, 5)
    return Product("name1", "sku1234567", "desc1", 200f, 312.56f, nutritionFacts, true, false, tags, comments, reviewScores)
  }


  @Test
  fun validation1() {
    val stringCensorRule: SimpleRule<String> =
        SimpleRule("CENSORED_WORD", Predicate { str -> str == null || !str.contains("fuck", true) }, Severity.WARN)

    val nutritionFactsRules: MultiRule<NutritionFacts> =
        MultiRule.builder<NutritionFacts>().reflexiveProperties(NutritionFacts::class.java)
            .addRulesForAll(Number::class.java, numberPositive())
            .build()

    val commentRules: MultiRule<Comment> = MultiRule.builder<Comment>().reflexiveProperties(Comment::class.java)
        .addRules("authorId", notNull(), numberPositive())
        .addRules("text", notNull(), stringTrimmedNotEmpty(), stringMaxLength(500), stringCensorRule)
        .build()

    val nullityRules: MultiRule<Product> = MultiRule.builder<Product>().reflexiveProperties(Product::class.java)
        .addRulesForAll(Boolean::class.java, notNull())
        .addRulesForAll(Float::class.java, notNull())
        .addRules("name", notNull<String>())
        .addRules(Predicate { p -> p.skuId != null }, "nutritionFacts", notNull<NutritionFacts>())
        .addRules(Predicate { p -> p.skuId == null }, "nutritionFacts", isNull<NutritionFacts>())
        .build()

    val validityRules: MultiRule<Product> = MultiRule.builder<Product>().reflexiveProperties(Product::class.java)
        .addRules("name", stringTrimmedNotEmpty(), stringMaxLength(40))
        .addRules("skuId", stringExactLength(10), stringAlphanumeric())
        .addRules("description", stringMaxLength(200))
        .addRulesForAll(Float::class.java, numberPositive())
        .addMultiRule({ p -> p.nutritionFacts != null }, "nutritionFacts", nutritionFactsRules)
        .addRules("tags", collectionNotEmpty())
        .addMultiRuleForElements("comments", commentRules)
        .addRules("reviewScores", each(numberInRange(1, 5)))
        .build()

    assertOk(validoctor.examine(product, nullityRules, validityRules))


    product.comments?.get(0)?.text = "text fucktext"
    val diagnosis1 = validoctor.examine(product, nullityRules, validityRules)
    assertWarn(diagnosis1)
    assertTrue(diagnosis1.ailments["comments_element.text"]!!.any({ a -> a.name == "CENSORED_WORD" }))

    product.nutritionFacts?.fibre = 0.0
    val diagnosis2 = validoctor.examine(product, nullityRules, validityRules)
    assertError(diagnosis2)
    assertEquals(2, diagnosis2.ailments.size)
    assertTrue(diagnosis2.ailments["nutritionFacts.fibre"]!!.any { a -> a.name == numberPositive().ailment.name })

    product.name = null
    val diagnosis3 = validoctor.examine(product, nullityRules, validityRules)
    assertError(diagnosis3)
    assertEquals(3, diagnosis3.ailments.size)
    assertTrue(diagnosis3.ailments["name"]!!.any { a -> a.name == notNull<Any>().ailment.name })

    product.reviewScores!!.add(6)
    val diagnosis4 = validoctor.examine(product, nullityRules, validityRules)
    assertError(diagnosis4)
    assertEquals(4, diagnosis4.ailments.size)
    assertTrue(diagnosis4.ailments["reviewScores"]!!.any { a -> a.name == numberInRange(1, 5).ailment.name })
  }

}