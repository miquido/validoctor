package com.miquido.validoctor.complexcases;

import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.ailment.Severity;
import com.miquido.validoctor.diagnosis.Diagnosis;
import com.miquido.validoctor.multirule.MultiRule;
import com.miquido.validoctor.rule.SimpleRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.miquido.validoctor.TestUtil.*;
import static com.miquido.validoctor.rule.Rules.*;
import static org.junit.Assert.*;

public class ComplexCase1JavaTest {

  private Validoctor validoctor = Validoctor.builder().build();
  private Product product = makeProduct();

  private Product makeProduct() {
    Set<Tag> tags = new HashSet<>(3);
    tags.add(Tag.DOUGH);
    tags.add(Tag.FRUIT);
    tags.add(Tag.VEGETABLE);
    List<Comment> comments = new ArrayList<>(3);
    comments.add(new Comment(1L, "text1"));
    comments.add(new Comment(2L, "text2"));
    comments.add(new Comment(1L, "text3"));
    NutritionFacts nutritionFacts = new NutritionFacts(287, 17.0, 32.5, 0.04);
    List<Integer> reviewScores = Arrays.asList(2, 3, 3, 4, 4, 4, 5, 5);
    return new Product("name1", "sku1234567", "desc1", 200f, 312.56f, nutritionFacts, true, false, tags, comments, reviewScores);
  }

  @Test
  public void validation1() {
    SimpleRule<String> stringCensorRule = new SimpleRule<>("CENSORED_WORD",
        str -> str == null || !str.contains("fuck"), Severity.WARN);

    MultiRule<NutritionFacts> nutritionFactsRules = MultiRule.<NutritionFacts>builder().reflexiveProperties(NutritionFacts.class)
            .addRulesForAll(Number.class, numberPositive())
            .build();

    MultiRule<Comment> commentRules = MultiRule.<Comment>builder().reflexiveProperties(Comment.class)
        .addRules("authorId", notNull(), numberPositive())
        .addRules("text", notNull(), stringTrimmedNotEmpty(), stringMaxLength(500), stringCensorRule)
        .build();

    MultiRule<Product> nullityRules = MultiRule.<Product>builder().reflexiveProperties(Product.class)
        .addRulesForAll(Boolean.class, notNull())
        .addRulesForAll(Float.class, notNull())
        .addRules("name", notNull())
        .addRules(p -> p.getSkuId() != null, "nutritionFacts", notNull())
        .addRules(p -> p.getSkuId() == null, "nutritionFacts", isNull())
        .build();

    MultiRule<Product> validityRules = MultiRule.<Product>builder().reflexiveProperties(Product.class)
        .addRules("name", stringTrimmedNotEmpty(), stringMaxLength(40))
        .addRules("skuId", stringExactLength(10), stringAlphanumeric())
        .addRules("description", stringMaxLength(200))
        .addRulesForAll(Float.class, numberPositive())
        .addMultiRule(p -> p.getNutritionFacts() != null, "nutritionFacts", nutritionFactsRules)
        .addRules("tags", collectionNotEmpty())
        .addMultiRuleForElements("comments", commentRules)
        .addRules("reviewScores", each(numberInRange(1, 5)))
        .build();

    assertOk(validoctor.examine(product, nullityRules, validityRules));


    product.getComments().get(0).setText("text fucktext");
    Diagnosis diagnosis1 = validoctor.examine(product, nullityRules, validityRules);
    assertWarn(diagnosis1);
    assertTrue(diagnosis1.getAilments().get("comments_element.text").stream().anyMatch(a -> a.getName().equals("CENSORED_WORD")));

    product.getNutritionFacts().setFibre(0.0);
    Diagnosis diagnosis2 = validoctor.examine(product, nullityRules, validityRules);
    assertError(diagnosis2);
    assertEquals(2, diagnosis2.getAilments().size());
    assertTrue(diagnosis2.getAilments().get("nutritionFacts.fibre").stream().anyMatch(a -> a.getName().equals(numberPositive().peekAilment().getName())));

    product.setName(null);
    Diagnosis diagnosis3 = validoctor.examine(product, nullityRules, validityRules);
    assertError(diagnosis3);
    assertEquals(3, diagnosis3.getAilments().size());
    assertTrue(diagnosis3.getAilments().get("name").stream().anyMatch(a -> a.getName().equals(notNull().peekAilment().getName())));

    product.getReviewScores().set(0, 6);
    Diagnosis diagnosis4 = validoctor.examine(product, nullityRules, validityRules);
    assertError(diagnosis4);
    assertEquals(4, diagnosis4.getAilments().size());
    assertTrue(diagnosis4.getAilments().get("reviewScores").stream().anyMatch(a -> a.getName().equals(numberInRange(1, 5).peekAilment().getName())));
  }
}
