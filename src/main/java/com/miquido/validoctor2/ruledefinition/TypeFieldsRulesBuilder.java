package com.miquido.validoctor2.ruledefinition;

import com.miquido.validoctor2.Rule2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static com.miquido.validoctor2.ObjectRule.*;

public class TypeFieldsRulesBuilder<T, P> extends AbstractFieldsRulesBuilder<T> {

  private final Class<T> enclosingClass;
  private final Class<? extends P> fieldsClass;
  private final boolean strictMatch;
  protected int layer = 0;

  TypeFieldsRulesBuilder(Class<T> enclosingClass, Class<? extends P> fieldsClass, boolean strictMatch,
                         ObjectRuleBuilder<T> objectRuleBuilder) {
    super(objectRuleBuilder);
    this.enclosingClass = enclosingClass;
    this.fieldsClass = fieldsClass;
    this.strictMatch = strictMatch;
  }

  @SafeVarargs
  public final TypeFieldsRulesBuilder<T, P> rules(Rule2<? super P>... rules) {
    findFields().forEach(field -> {
      Optional<FieldRules> existingFieldRule = objectRuleBuilder.rule.fieldsRules.stream()
          .filter(fr -> fr.fieldName.equals(field.getName()) && fr.layer == layer)
          .findFirst(); //keep one fieldRule per field and layer combination
      if (existingFieldRule.isPresent()) {
        existingFieldRule.get().rules.addAll(Arrays.asList(rules));
      } else {
        FieldRules fieldRules = new FieldRules(field.getName(), layer, new ArrayList<>(Arrays.asList(rules)));
        objectRuleBuilder.rule.fieldsRules.add(fieldRules);
      }
    });
    layer++;
    return this;
  }

  private Stream<Field> findFields() {
    return Arrays.stream(enclosingClass.getDeclaredFields())
        .filter(field -> strictMatch
            ? field.getType().equals(fieldsClass)
            : fieldsClass.isAssignableFrom(field.getType())
        );
  }
}
