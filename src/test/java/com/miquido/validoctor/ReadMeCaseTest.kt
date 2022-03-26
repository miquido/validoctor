package com.miquido.validoctor

import com.miquido.validoctor.definition.Rules.chained
import com.miquido.validoctor.definition.Rules.conditional
import com.miquido.validoctor.definition.Rules.isNull
import com.miquido.validoctor.definition.Rules.notNull
import com.miquido.validoctor.definition.Rules.numberInRange
import com.miquido.validoctor.definition.Rules.numberPositive
import com.miquido.validoctor.definition.Rules.stringAlphanumeric
import com.miquido.validoctor.definition.Rules.stringExactLength
import com.miquido.validoctor.definition.Rules.stringMaxLength
import com.miquido.validoctor.definition.Rules.stringTrimmedNotEmpty
import com.miquido.validoctor.definition.SimpleRule
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadMeCaseTest {

  data class NutritionFacts(val kcal: Int?,
                            val fibre: Double?,
                            val protein: Double?,
                            val fat: Double?)

  data class Comment(val authorId: Long?,
                     val text: String?)

  data class Product(val name: String?,
                     val skuId: String?,
                     val description: String?,
                     val weightG: Float?,
                     val volumeMl: Float?,
                     val nutritionFacts: NutritionFacts?,
                     val glutenFree: Boolean?,
                     val vegan: Boolean?,
                     val comments: List<Comment>?,
                     val reviewScores: List<Int>?)

  val nutritionFactsRule =
    Validoctor.rulesFor(NutritionFacts::class.java)
      .allAssignable(Number::class.java, numberPositive())
      .build()

  val stringCensorRule: SimpleRule<String> =
    SimpleRule("CENSORED_WORD") { str -> str == null || !str.contains("fuck", true) }

  val commentRules =
    Validoctor.rulesFor(Comment::class.java)
      .field("authorId", notNull(), numberPositive())
      .field("text", notNull(), stringTrimmedNotEmpty(), stringMaxLength(500), stringCensorRule)
      .build()

  fun nullityRules(p: Product) =
    Validoctor.rulesFor(Product::class.java)
      .allTyped(Boolean::class.java, notNull())
      .allTyped(Float::class.java, notNull())
      .field("name", notNull<String>())
      .field("nutritionFacts", conditional({ p.skuId != null }, notNull<NutritionFacts>()))
      .field("nutritionFacts", conditional({ p.skuId == null }, isNull<NutritionFacts>()))
      .build()

  fun validityRules(p: Product) =
    Validoctor.rulesFor(Product::class.java)
      .field("name", stringTrimmedNotEmpty(), stringMaxLength(40))
      .field("skuId", stringExactLength(10), stringAlphanumeric())
      .field("description", stringMaxLength(200))
      .allTyped(Float::class.java, numberPositive())
      .field("nutritionFacts", conditional({ p.nutritionFacts != null }, nutritionFactsRule))
      .elements("comments", commentRules)
      .elements("reviewScores", chained(notNull(), numberInRange(1, 5)))
      .build()

  @Test
  fun test() {
    val product = Product("name", "sku1234567", "description", 1.0f, 1000f, NutritionFacts(150, 34.6, 12.7, 5.6), false, true,
      listOf(Comment(1, "ok")), listOf(1, 2, 3)
    )
    val diagnosis = Validoctor.examine(product, nullityRules(product), validityRules(product))
    assertTrue(diagnosis.isValid)
  }
}