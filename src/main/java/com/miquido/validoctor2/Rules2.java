package com.miquido.validoctor2;

import java.util.Collection;
import java.util.Objects;

public class Rules2 {

  public static Rule2<String> stringTrimmedNotEmpty() {
    return new SimpleRule2<>("TRiMMED_STRING_IS_EMPTY", str -> str == null || !str.trim().isEmpty());
  }

  public static Rule2<String> stringExactLength(int length) {
    return new SimpleRule2<>("STRING_WRONG_LENGTH", str -> str == null || str.length() == length);
  }

  public static Rule2<String> stringMinLength(int length) {
    return new SimpleRule2<>("STRING_WRONG_LENGTH", str -> str == null || str.length() > length);
  }

  public static <T> Rule2<T> notNull() {
    return new SimpleRule2<>("NULL", Objects::nonNull);
  }

  public static Rule2<Number> numberNonNegative() {
    return new SimpleRule2<>("NUMBER_NEGATIVE", value -> value == null || value.doubleValue() >= 0);
  }

  public static Rule2<Number> numberPositive() {
    return new SimpleRule2<>("NUMBER_NON_POSITIVE", value -> value == null || value.doubleValue() > 0);
  }

  public static <T> Rule2<Collection<T>> collectionNotEmpty() {
    return new SimpleRule2<>("COLLECTION_EMPTY", collection -> collection == null || !collection.isEmpty());
  }

  public static <T> Rule2<Collection<T>> each(Rule2<T> rule) {
    return new SimpleRule2<>(rule.getViolationMessage(),
        col -> col == null || col.stream().allMatch(obj -> rule.test(obj).isEmpty()));
  }

}
