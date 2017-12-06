package com.miquido.validoctor.nestedrules

enum class Tag {
  MEAT, VEGETABLE, SOY, FRUIT, DOUGH
}


data class NutritionFacts(var kcal: Int?, var fibre: Double?, var protein: Double?, var fat: Double?)


data class Comment(var authorId: Long?, var text: String?, var upVotedUsersIds: List<Long>?, var downVotedUsersIds: List<Long>?)


data class Product(var name: String?, var skuId: String?, var description: String?, var weightG: Float?, var volumeMl: Float?,
                   var nutritionFacts: NutritionFacts?, var glutenFree: Boolean?, var vegan: Boolean?, var tags: Set<Tag>?,
                   var comments: List<Comment>?)