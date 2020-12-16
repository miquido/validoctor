package com.miquido.validoctor2;

import com.miquido.validoctor2.result.Diagnosis2;
import com.miquido.validoctor2.rule.Rule2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.miquido.validoctor2.TestClasses.*;
import static com.miquido.validoctor2.rule.Rules2.*;
import static org.junit.jupiter.api.Assertions.*;

public class V2ApiTest {

  Rule2<TestInsideClass> insideRule = Validoctor2.rulesFor(TestInsideClass.class)
      .<String>field("name").rules(notNull(), stringTrimmedNotEmpty())
      .<Number>field("score").rules(notNull(), numberPositive())
      .build();

  // Showcase of all options
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
      .all().rules(notNull())
      //^ All fields
      .fields("name", "description", String::concat).rules(stringMaxLength(10))
      //^ Root branch for reduced value of two fields
      .build();

  @Test
  public void singleValueExamination() {
    Diagnosis2 stringDiagnosis = Validoctor2.examine("  ", "string", notNull(), stringTrimmedNotEmpty());
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED",
        stringDiagnosis.getAilments().get("string").stream().findFirst().get());
  }

  @Test
  public void allNotNull() {
    Rule2<TestClass> rule = Validoctor2.rulesFor(TestClass.class)
        .all().rules(notNull())
        .build();
    TestClass patient = new TestClass(null, null, null, null, -2.0f, null, null, null, null);
    Diagnosis2 diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("name").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("skuId").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("description").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("weightKg").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("kcal").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("inside").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("intSet").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("insideList").stream().findFirst().get());
  }

  @Test
  public void multiBranch() {
    Rule2<TestInsideClass> rule = Validoctor2.rulesFor(TestInsideClass.class)
        .<String>field("name")
          .rules(notNull())
          .rules(stringNotEmpty())
          .rules(stringTrimmedNotEmpty())
          .rules(stringMinLength(3))
          .rules(stringExactLength(4))
        .build();
    TestInsideClass patient = new TestInsideClass(null, 0);
    Diagnosis2 diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("name").stream().findFirst().get());
    patient = new TestInsideClass("", 0);
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("NOT_EMPTY_REQUIRED", diagnosis.getAilments().get("name").stream().findFirst().get());
    patient = new TestInsideClass("  ", 0);
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.getAilments().get("name").stream().findFirst().get());
    patient = new TestInsideClass("aa", 0);
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("TOO_SHORT", diagnosis.getAilments().get("name").stream().findFirst().get());
    patient = new TestInsideClass("aaa", 0);
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("INVALID_LENGTH", diagnosis.getAilments().get("name").stream().findFirst().get());
    patient = new TestInsideClass("aaaa", 0);
    diagnosis = Validoctor2.examine(patient, rule);
    assertTrue(diagnosis.isValid());
  }

  @Test
  public void collectionBranches() {
    Rule2<TestClass> rule = Validoctor2.rulesFor(TestClass.class)
        .<TestInsideClass>collectionField("insideList")
          .rules(notNull())
          .rules(collectionNotEmpty())
          .rules(collectionMinSize(2))
          .elementsRules(notNull())
          .elementsRules(insideRule)
        .build();
    TestClass patient = new TestClass(null, null, null, null, -2.0f, null, null, null, null);
    Diagnosis2 diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("insideList").stream().findFirst().get());
    patient = new TestClass(null, null, null, null, -2.0f, null, null, null, new ArrayList<>());
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("NOT_EMPTY_REQUIRED", diagnosis.getAilments().get("insideList").stream().findFirst().get());
    TestInsideClass inside1 = new TestInsideClass("n1", 0);
    patient = new TestClass(null, null, null, null, -2.0f, null, null, null, List.of(inside1));
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("SIZE_TOO_LITTLE", diagnosis.getAilments().get("insideList").stream().findFirst().get());
    patient = new TestClass(null, null, null, null, -2.0f, null, null, null, Arrays.asList(null, null));
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("insideList[0]").stream().findFirst().get());
    assertEquals("NOT_NULL_REQUIRED", diagnosis.getAilments().get("insideList[1]").stream().findFirst().get());
    TestInsideClass inside2 = new TestInsideClass("", 2);
    patient = new TestClass(null, null, null, null, -2.0f, null, null, null, List.of(inside1, inside2));
    diagnosis = Validoctor2.examine(patient, rule);
    assertEquals("POSITIVE_REQUIRED", diagnosis.getAilments().get("insideList[0].score").stream().findFirst().get());
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", diagnosis.getAilments().get("insideList[1].name").stream().findFirst().get());
  }
}
