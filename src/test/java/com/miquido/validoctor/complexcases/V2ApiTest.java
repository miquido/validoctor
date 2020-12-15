package com.miquido.validoctor.complexcases;

import com.miquido.validoctor2.Diagnosis2;
import com.miquido.validoctor2.Rule2;
import com.miquido.validoctor2.Rules2;
import com.miquido.validoctor2.Validoctor2;
import com.miquido.validoctor2.ruledefinition.ExaminationDefinition;
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
      .<String>field("name").rules(notNull(), stringTrimmedNotEmpty())
      .<Number>field("score").rules(notNull(), numberPositive())
      .build();


  Rule2<TestClass> rule = Validoctor2.rulesFor(TestClass.class)
      .<String>field("name").rules(stringTrimmedNotEmpty())
      .<String>field("skuId").rules(notNull()).rules(stringExactLength(10))
      //^ Each consecutive rules() call adds a branch. Further branches only execute if their parent branches succeed
      .<String>field("description").rules(notNull())
      .<String>field("description").rules(stringTrimmedNotEmpty())
      //^ Both independent root branches, as they are defined separately
      .<String>field("description").rules(stringMinLength(100)).rules(stringExactLength(100))
      //^ Root branch and then child branch that will only execute if this root branch succeeds
      .<TestInsideClass>field("inside").rules(notNull(), insideRule)
      //^ Adding object rule is same as adding simple rule and they are interoperable
      .<Number>collectionField("intSet").rules(notNull(), collectionNotEmpty()).elementsRules(numberNonNegative())
      //^ Collection fields also use a single root branch, so rules on elements will only be executed if
      //root rules() on field succeed
      .<Number>collectionField("intSet").elementsRules(notNull(), numberNonNegative())
      //^ Root branch on elements of collection
      .<TestInsideClass>collectionField("insideList").elementsRules(notNull()).elementsRules(insideRule)
      //^ Root branch and then child branch on elements of collection
      .allTyped(Float.class).rules(notNull()).rules(numberNonNegative())
      //^ Root branch for all Floats (but not floats)
      .allTyped(float.class).rules(notNull()).rules(numberPositive())
      //^ Root and child branch for all floats (but not Floats)
      .allAssignable(Number.class).rules(numberNonNegative())
      //^ Root branch all fields assignable from Number
      .fields("weightKg", "volumeL")
      //^ Multifield validation for relations between fields or reductions of the fields
      .build();

  @Test
  public void test() {
    Diagnosis2 stringDiagnosis = Validoctor2.examine("  ", "string", notNull(), stringTrimmedNotEmpty());

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
