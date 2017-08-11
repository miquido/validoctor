package com.miquido.validoctor.rule;

import java.util.Collection;
import java.util.Objects;

public final class Rules {

  private static final Rule<String> STRING_NOT_EMPTY =
      new SimpleRule<>("STRING_NOT_EMPTY", str -> str == null || !str.trim().isEmpty());

  private static final Rule<Object> NULL =
      new SimpleRule<>("OBJECT_NULL", Objects::isNull);

  private static final SimpleRule<Object> NOT_NULL =
      new SimpleRule<>("OBJECT_NOT_NULL", Objects::nonNull);

  private static final Rule<Boolean> FALSE =
      new SimpleRule<>("BOOLEAN_FALSE", value -> !value);

  private static final Rule<Number> NUMBER_POSITIVE =
      new SimpleRule<>("NUMBER_POSITIVE", value -> value == null || value.doubleValue() > 0);

  private static final Rule<Number> NUMBER_NON_NEGATIVE =
      new SimpleRule<>("NUMBER_NOT_NEGATIVE", value -> value == null || value.doubleValue() >= 0);

  private static final Rule<Collection> COLLECTION_NOT_EMPTY =
      new SimpleRule<>("COLLECTION_NOT_EMPTY", collection -> collection == null || !collection.isEmpty());


  private Rules() {
  }

  public static Rule<Object> notNull() {
    return NOT_NULL;
  }

  public static Rule<Object> isNull() {
    return NULL;
  }

  public static Rule<Boolean> isFalse() {
    return FALSE;
  }

  public static Rule<String> stringNotEmpty() {
    return STRING_NOT_EMPTY;
  }

  public static Rule<Number> numberPositive() {
    return NUMBER_POSITIVE;
  }

  public static Rule<Number> numberNonNegative() {
    return NUMBER_NON_NEGATIVE;
  }

  public static Rule<Collection> collectionNotEmpty() {
    return COLLECTION_NOT_EMPTY;
  }

  public static Rule<String> stringMinLength(int minLength) {
    return new SimpleRule<>("STRING_MIN_LENGTH", str -> str == null || str.length() >= minLength);
  }

  public static Rule<String> stringMaxLength(int maxLength) {
    return new SimpleRule<>("STRING_MAX_LENGTH", str -> str == null || str.length() <= maxLength);
  }

  public static Rule<Number> numberInRange(Number minRange, Number maxRange) {
    return new SimpleRule<>("NUMBER_IN_RANGE", value -> value == null
        || value.doubleValue() >= minRange.doubleValue() && value.doubleValue() <= maxRange.doubleValue());
  }
}
