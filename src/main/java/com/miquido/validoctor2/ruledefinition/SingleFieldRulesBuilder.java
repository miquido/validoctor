package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.ObjectRule.FieldRules;
import com.miquido.validoctor2.Rule2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class SingleFieldRulesBuilder<T> extends AbstractFieldsRulesBuilder<T> {

  protected final String field;
  protected int layer = 0;

  SingleFieldRulesBuilder(String field, ObjectRuleBuilder<T> objectRuleBuilder) {
    super(objectRuleBuilder);
    this.field = field;
  }

  @SafeVarargs
  public final <P> SingleFieldRulesBuilder<T> rules(Rule2<P>... rules) {
    Optional<FieldRules> existingFieldRule = objectRuleBuilder.rule.fieldsRules.stream()
        .filter(fr -> fr.fieldName.equals(field) && fr.layer == layer)
        .findFirst(); //keep one fieldRule per field and layer combination
    if (existingFieldRule.isPresent()) {
      existingFieldRule.get().rules.addAll(Arrays.asList(rules));
    } else {
      FieldRules fieldRules = new FieldRules(field, layer, new ArrayList<>(Arrays.asList(rules)));
      objectRuleBuilder.rule.fieldsRules.add(fieldRules);
    }
    layer++;
    return this;
  }
}
