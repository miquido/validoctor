package com.miquido.validoctor2.ruledefinition;

import java.util.Comparator;
import java.util.List;

//TODO difficult because rules should be of comparator result type (int),
// and builder expects they are of fields' type
public class ComparableFieldsRulesBuilder<T, P> extends AbstractFieldsRulesBuilder<T> {

  protected ComparableFieldsRulesBuilder(RuleBuilder<T> ruleBuilder, List<String> fieldNames,
                                         Comparator<P> comparator) {
    super(ruleBuilder);
  }
}
