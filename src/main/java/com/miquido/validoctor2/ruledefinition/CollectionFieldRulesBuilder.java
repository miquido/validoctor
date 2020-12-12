package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.ObjectRule;
import com.miquido.validoctor2.Rule2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class CollectionFieldRulesBuilder<T> extends AbstractFieldsRulesBuilder<T> {

  private String field;
  private int layer = 0;

  CollectionFieldRulesBuilder(String field, ObjectRuleBuilder<T> objectRuleBuilder) {
    super(objectRuleBuilder);
    this.field = field;
  }

  @SafeVarargs
  public final <P> CollectionFieldRulesBuilder<T> rules(Rule2<P>... rules) {
    Optional<ObjectRule.FieldRules> existingFieldRule = objectRuleBuilder.rule.fieldsRules.stream()
        .filter(fr -> fr.fieldName.equals(field) && fr.layer == layer)
        .findFirst(); //keep one fieldRule per field and layer combination
    if (existingFieldRule.isPresent()) {
      existingFieldRule.get().rules.addAll(Arrays.asList(rules));
    } else {
      ObjectRule.FieldRules fieldRules = new ObjectRule.FieldRules(field, layer, new ArrayList<>(Arrays.asList(rules)));
      objectRuleBuilder.rule.fieldsRules.add(fieldRules);
    }
    layer++;
    return this;
  }

  @SafeVarargs
  public final <P> CollectionFieldRulesBuilder<T> elementsRules(Rule2<P>... rules) {
    //TODO
    layer++;
    return this;
  }
}
