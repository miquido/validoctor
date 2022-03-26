package com.miquido.validoctor;

import com.miquido.validoctor.definition.Rule;
import com.miquido.validoctor.result.Diagnosis;
import com.miquido.validoctor.result.DiagnosisException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static com.miquido.validoctor.TestClasses.*;
import static com.miquido.validoctor.definition.Rules.*;
import static org.junit.Assert.*;

public class V2ApiTest {

  Rule<TestInsideClass> insideRule = Validoctor.rulesFor(TestInsideClass.class)
      .field("name", notNull(), stringTrimmedNotEmpty())
      .field("score", notNull(), numberPositive())
      .build();

  // Showcase of all options
  Rule<TestClass> rule = Validoctor.rulesFor(TestClass.class)
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
    Validoctor.setThrowing(false);
  }

  @Test
  public void singleValueExamination() {
    Diagnosis stringDiagnosis = Validoctor.examine("  ", "string", notNull(), stringTrimmedNotEmpty());
    assertEquals("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED",
        stringDiagnosis.getAilments().get("string").stream().findFirst().get());
  }

  @Test
  public void shouldNotThrowExceptionWithoutErrors() {
    Validoctor.setThrowing(true);
    //create empty rules
    Rule<TestClass> rule = Validoctor.rulesFor(TestClass.class)
      .build();
    TestClass patient = new TestClass(null, null, null, null, 0, null, null, null, null);
    Validoctor.examine(patient, rule);
  }

  @Test
  public void shouldThrowException() {
    Validoctor.setThrowing(true);
    try {
      Rule<TestClass> rule = Validoctor.rulesFor(TestClass.class)
        .all(notNull())
        .build();
      TestClass patient = new TestClass(null, null, null, null, 0, null, null, null, null);
      Validoctor.examine(patient, rule);
    } catch (DiagnosisException ex) {
      assertEquals("NOT_NULL_REQUIRED", ex.getDiagnosis().getAilments().get("name").stream().findFirst().get());
    }
  }
}
