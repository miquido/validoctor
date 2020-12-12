package com.miquido.validoctor.complexcases;

import com.miquido.validoctor2.Diagnosis2;
import com.miquido.validoctor2.Rule2;
import com.miquido.validoctor2.Validoctor2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.miquido.validoctor2.Rules2.*;

public class V2ApiTest {

  public static class TestClass {
    private String name;
    String skuId;
    public String description;
    private Float weightKg;
    private float volumeL;
    private Integer kcal;
    private TestInsideClass inside;
    private Set<Integer> intSet;
    private List<TestInsideClass> insideList;

    public TestClass(String name, String skuId, String description, Float weightKg,
                     float volumeL, Integer kcal, TestInsideClass inside, Set<Integer> intSet,
                     List<TestInsideClass> insideList) {
      this.name = name;
      this.skuId = skuId;
      this.description = description;
      this.weightKg = weightKg;
      this.volumeL = volumeL;
      this.kcal = kcal;
      this.inside = inside;
      this.intSet = intSet;
      this.insideList = insideList;
    }
  }

  public static class TestInsideClass {
    private String name;
    private double score;

    public TestInsideClass(String name, double score) {
      this.name = name;
      this.score = score;
    }
  }


  Rule2<TestInsideClass> insideRule = Validoctor2.rulesFor(TestInsideClass.class)
      .field("name").rules(notNull(), stringTrimmedNotEmpty())
      .field("score").rules(notNull(), numberPositive())
      .build();


  Rule2<TestClass> rule = Validoctor2.rulesFor(TestClass.class)
      .field("name").rules(stringTrimmedNotEmpty())
      .field("skuId").rules(notNull()).rules(stringExactLength(10))
      //^ Each consecutive rules() call adds a layer. Layer 0 and then 1. 1 will only execute if 0 is valid
      .field("description").rules(notNull())
      .field("description").rules(stringTrimmedNotEmpty())
      //^ Both layer 0, as they are defined separately
      .field("description").rules(stringMinLength(100)).rules(stringExactLength(100))
      //^ Another layer 0, and then layer 1. Layer 1 will only execute if all layer 0 are valid, including ones
      // defined separately above. Usage like this will be discouraged
      .field("inside").rules(notNull(), insideRule)
      //^ Adding object rule is same as adding simple rule and they are interoperable
//      .collectionField("intSet").rules(notNull(), collectionNotEmpty()).elementsRules(numberNonNegative())
      //^ Layer 0 on collection field, then layer 1 on its elements
//      .collectionField("intSet").elementsRules(notNull(), numberNonNegative())
      //^ Two layer 0 rules on elements of collection
//      .collectionField("insideList").elementsRules(notNull()).elementsRules(insideRule)
      //^ Layer 0 and then layer 1 on elements of collection
      .allTyped(Float.class).rules(notNull()).rules(numberNonNegative())
      //^ Layer 0 and then layer 1 for all Floats (but not floats)
      .allTyped(float.class).rules(notNull()).rules(numberPositive())
      //^ Layer 0 and then layer 1 for all floats (but not Floats)
      .allAssignable(Number.class).rules(numberNonNegative())
      //^ Layer 0 for all fields assignable from Number. This will add to layer 0 rules for Floats defined above
      .fields("weightKg", "volumeL")
      //^ Multifield validation for relations between fields or reductions of the fields
      .build();

  @Test
  public void test() {
    TestInsideClass insidePatient = new TestInsideClass("", 0.0);
    Set<Integer> intSet = new HashSet<>();
    intSet.add(1);
    intSet.add(-3);
    List<TestInsideClass> insideList = new ArrayList<>();
    insideList.add(insidePatient);
    TestClass patient = new TestClass("", null, "", null, -2.0f, 0, insidePatient, intSet, insideList);
    Diagnosis2 diagnosis2 = Validoctor2.examine(patient, rule);
    System.out.println(diagnosis2);
  }
}
