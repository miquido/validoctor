package com.miquido.validoctor.complexrule;

import com.miquido.validoctor.Validoctor;
import com.miquido.validoctor.multirule.TestPatient;
import com.miquido.validoctor.rule.Rules;
import org.junit.Test;

import java.util.Arrays;

public class ComplexRuleTest {

  private static final Validoctor validoctor = Validoctor.builder().build();

  @Test
  public void multipleComplexRules() {
    ComplexRule<TestPatient> rule1 = new ComplexRule<>(Arrays.asList("id", "name"), Rules.stringMaxLength(20));
  }

}
