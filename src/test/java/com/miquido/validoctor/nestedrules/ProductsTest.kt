package com.miquido.validoctor.nestedrules

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

class ProductsTest {

  val validoctor: Validoctor = Validoctor.builder().build()
  val product: Product by lazy { makeProduct() }

  private fun makeProduct() : Product {
    val tags = setOf(Tag.DOUGH, Tag.FRUIT, Tag.VEGETABLE)
    val comments = listOf(Comment(1, "text1", listOf(2, 3, 4), listOf(5, 6)),
        Comment(2, "text2", listOf(1, 2), listOf(3)), Comment(1, "text3", listOf(4, 5), listOf(2, 3)))
    val nutritionFacts = NutritionFacts(287, 17.0, 32.5, 0.04)
    return Product("name1", "sku1234567", "desc1", 200f, 312.56f, nutritionFacts, true, false, tags, comments)
  }


  @Test
  fun validation1() {
    val censorRule: SimpleRule<Comment> =
        SimpleRule("CENSORED_WORD", Predicate { c -> c == null || !c.text!!.contains("fuck", true) }, Severity.WARN)

    val nutritionFactsRules: MultiRule<NutritionFacts> =
        MultiRule.builder<NutritionFacts>().reflexiveProperties(NutritionFacts::class.java)
            .addRulesForAll(Number::class.java, numberPositive())
            .build()

    //TODO allow multirule validation on collection elements
//    val commentRules: MultiRule<Comment> = MultiRule.builder<Comment>().reflexiveProperties(Comment::class.java)
//        .addRules("authorId", Rules.notNull(), Rules.numberPositive())
//        .addRules("text", Rules.notNull(), Rules.stringTrimmedNotEmpty(), Rules.stringMaxLength(500), censorRule)
//        .build()

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
        .addRules("comments", each(censorRule))
        .build()

    assertOk(validoctor.examine(product, nullityRules, validityRules))


    product.comments?.get(0)?.text = "text fucktext"
    val diagnosis1 = validoctor.examine(product, nullityRules, validityRules)
    assertWarn(diagnosis1)
    assertTrue(diagnosis1.ailments["comments"]!!.any({ a -> a.name == "CENSORED_WORD" }))

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
  }

}