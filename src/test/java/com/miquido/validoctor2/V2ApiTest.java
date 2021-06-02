package com.miquido.validoctor2;

import com.miquido.validoctor2.definition.Rule2;
import com.miquido.validoctor2.result.Diagnosis2;
import com.miquido.validoctor2.result.DiagnosisException2;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.miquido.validoctor2.TestClasses.*;
import static com.miquido.validoctor2.definition.Rules2.*;
import static org.junit.jupiter.api.Assertions.*;

public class V2ApiTest {

  Rule2<TestInsideClass> insideRule = Validoctor2.rulesFor(TestInsideClass.class)
      .field("name", notNull(), stringTrimmedNotEmpty())
      .field("score", notNull(), numberPositive())
      .build();

  // Showcase of all options
  Rule2<TestClass> rule = Validoctor2.rulesFor(TestClass.class)
      .rule("DESCRIPTION_LONGER_THAN_SKU", "description", p -> p.description.length() < p.skuId.length())
      //^ Rule on whole enclosing object, not on specific field - allows defining rules that compare fields of object
      .field("name", stringTrimmedNotEmpty())
      .field("description", conditional(o -> LocalDate.now().isLeapYear(), notNull(), stringAlphabetic()))
      //^ Conditional rule(s) wrapper for external conditions
      .field("description", notNull(), stringAlphabetic())
      .field("description", notNull(), conditional(o -> LocalDate.now().isLeapYear(), stringAlphabetic()))
      .field("skuId", chained(stringExactLength(10), stringAlphanumeric(), stringAlphabetic()), stringMinLength(10))
      //^ Chained rules execute sequentially and the next one will not execute if any of previous ones failed
      .field("inside", notNull(), insideRule)
      //^ Adding object rule is same as adding simple rule, and they are interoperable
      .elements("intSet", notNull(), numberNonNegative())
      //^ Rules on elements of collection
      .elements("insideList", notNull(), insideRule)
      //^ Rules on elements of collection
      .allTyped(float.class, notNull(), numberPositive())
      //^ Rules for all floats (but not Floats)
      .allTyped(Float.class, notNull(), numberNonNegative())
      //^ Rules for all Floats (but not floats)
      .allAssignable(Number.class, numberNonNegative())
      //^ Rules for all fields assignable from Number
      .all(notNull())
      //^ Rules for all fields
      .fields(List.of("name", "description", "skuId"), stringNoSpacePadding(), stringTrimmedNotEmpty())
      //^ Rules for several fields of the same type in one go
      .reducedFields("name", "description", String::concat, stringMaxLength(10))
      //^ Rules for reduced value of two fields
      .build();

  @Before
  public void beforeEachTest() {
    //making sure to restore the default state of throwing property
    Validoctor2.setThrowing(false);
  }

  @Test
  public void singleValueExamination() {
    Diagnosis2 stringDiagnosis = Validoctor2.examine("  ", "string", notNull(), stringTrimmedNotEmpty());
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED",
        stringDiagnosis.getAilments().get("string").stream().findFirst().get());
  }

  @Test
  public void shouldNotThrowExceptionWithoutErrors() {
    Validoctor2.setThrowing(true);
    //create empty rules
    Rule2<TestClass> rule = Validoctor2.rulesFor(TestClass.class)
      .build();
    TestClass patient = new TestClass(null, null, null, null, 0, null, null, null, null);
    Validoctor2.examine(patient, rule);
  }

  @Test
  public void shouldThrowException() {
    Validoctor2.setThrowing(true);
    try {
      Rule2<TestClass> rule = Validoctor2.rulesFor(TestClass.class)
        .all(notNull())
        .build();
      TestClass patient = new TestClass(null, null, null, null, 0, null, null, null, null);
      Validoctor2.examine(patient, rule);
    } catch (DiagnosisException2 ex) {
      assertEquals("NOT_NULL_REQUIRED", ex.getDiagnosis().getAilments().get("name").stream().findFirst().get());
    }
  }
}
