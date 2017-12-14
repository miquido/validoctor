package com.miquido.validoctor.rule;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class Rules {

  private static final Rule<String> STRING_NOT_EMPTY =
      new SimpleRule<>("STRING_NOT_EMPTY", str -> str == null || !str.isEmpty());

  private static final Rule<String> STRING_TRIMMED_NOT_EMPTY =
      new SimpleRule<>("STRING_TRIMMED_NOT_EMPTY", str -> str == null || !str.trim().isEmpty());

  private static final Rule<String> STRING_ALPHANUMERIC =
      new SimpleRule<>("STRING_ALPHANUMERIC", str -> str == null || str.chars().allMatch(Character::isLetterOrDigit));

  private static final Rule<String> STRING_ALPHABETIC =
      new SimpleRule<>("STRING_ALPHABETIC", str -> str == null || str.chars().allMatch(Character::isLetter));

  private static final Rule<Object> NULL =
      new SimpleRule<>("OBJECT_NULL", Objects::isNull);

  private static final SimpleRule<Object> NOT_NULL =
      new SimpleRule<>("OBJECT_NOT_NULL", Objects::nonNull);

  private static final Rule<Boolean> FALSE =
      new SimpleRule<>("BOOLEAN_FALSE", value -> value == null || !value);

  private static final Rule<Boolean> TRUE =
      new SimpleRule<>("BOOLEAN_TRUE", value -> value == null || value);

  private static final Rule<Number> NUMBER_POSITIVE =
      new SimpleRule<>("NUMBER_POSITIVE", value -> value == null || value.doubleValue() > 0);

  private static final Rule<Number> NUMBER_NON_NEGATIVE =
      new SimpleRule<>("NUMBER_NOT_NEGATIVE", value -> value == null || value.doubleValue() >= 0);

  private static final Rule<Collection> COLLECTION_NOT_EMPTY =
      new SimpleRule<>("COLLECTION_NOT_EMPTY", collection -> collection == null || !collection.isEmpty());


  private Rules() {
  }

  /**
   * Passed: patient is not null.<br/>
   * Violated: patient is null.
   */
  public static <T> Rule<T> notNull() {
    return (Rule<T>) NOT_NULL;
  }

  /**
   * Passed: patient is null.<br/>
   * Violated: patient is not null.
   */
  public static <T> Rule<T> isNull() {
    return (Rule<T>) NULL;
  }

  /**
   * Passed: patient is null or false.<br/>
   * Violated: patient is true.
   */
  public static Rule<Boolean> isFalse() {
    return FALSE;
  }

  /**
   * Passed: patient is null of true.<br/>
   * Violated: patient is false.
   */
  public static Rule<Boolean> isTrue() {
    return TRUE;
  }

  /**
   * Passed: patient is null or not empty collection.<br/>
   * Violated: patient is empty collection.
   */
  public static Rule<Collection> collectionNotEmpty() {
    return COLLECTION_NOT_EMPTY;
  }

  /**
   * Passed: patient is null or not empty string.<br/>
   * Violated: patient is empty string.
   * @see Rules#stringTrimmedNotEmpty()
   */
  public static Rule<String> stringNotEmpty() {
    return STRING_NOT_EMPTY;
  }

  /**
   * Passed: patient is null, not empty and not all-whitespace string.<br/>
   * Violated: patient is empty or all-whitespace string.
   * @see Rules#stringNotEmpty()
   */
  public static Rule<String> stringTrimmedNotEmpty() {
    return STRING_TRIMMED_NOT_EMPTY;
  }

  /**
   * Passed: patient is null or contains only letters and digits.
   * Violated: patient contains any character that is not letter or digit.
   */
  public static Rule<String> stringAlphanumeric() {
    return STRING_ALPHANUMERIC;
  }

  /**
   * Passed: patient is null or contains only letters.
   * Violated: patient contains any character that is not letter.
   */
  public static Rule<String> stringAlphabetic() {
    return STRING_ALPHABETIC;
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength}.<br/>
   * Violated: patient is string with length lesser than specified {@code minLength}.
   */
  public static Rule<String> stringMinLength(int minLength) {
    return new SimpleRule<>("STRING_MIN_LENGTH:" + minLength, str -> str == null || str.length() >= minLength);
  }

  /**
   * Passed: patient is null or string with length lesser than or equal to specified {@code maxLength}.<br/>
   * Violated: patient is string with length greater than specified {@code maxLength}.
   */
  public static Rule<String> stringMaxLength(int maxLength) {
    return new SimpleRule<>("STRING_MAX_LENGTH:" + maxLength, str -> str == null || str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength} and
   * lesser than or equal to specified {@code maxLength}.<br/>
   * Violated: patient is string with length lesser than or equal to specified {@code minLength} or
   * greater than specified {@code maxLength}.
   */
  public static Rule<String> stringLengthInRange(int minLength, int maxLength) {
    return new SimpleRule<>("STRING_LENGTH_IN_RANGE:" + minLength + "-" + maxLength, str -> str == null
        || str.length() >= minLength && str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length equal to specified {@code exactLength}.<br/>
   * Violated: patient is string with length other than specified {@code exactLength}.
   */
  public static Rule<String> stringExactLength(int exactLength) {
    return new SimpleRule<>("STRING_EXACT_LENGTH:" + exactLength, str -> str == null || str.length() == exactLength);
  }

  /**
   * Passed: patient is null or number with value greater than 0.<br/>
   * Violated: patient is number with value lesser than or equal to 0.
   */
  public static Rule<Number> numberPositive() {
    return NUMBER_POSITIVE;
  }

  /**
   * Passed: patient is null or number with value greater than or equal to 0.<br/>
   * Violated: patient is number with value lesser than 0.
   */
  public static Rule<Number> numberNonNegative() {
    return NUMBER_NON_NEGATIVE;
  }

  /**
   * Passed: patient is null or number with value {@code >= minRange} and {@code <= maxRange}.<br/>
   * Violated: patient is number with value {@code < minRange} or {@code > maxRange}.
   */
  public static Rule<Number> numberInRange(Number minRange, Number maxRange) {
    return new SimpleRule<>("NUMBER_IN_RANGE:" + minRange + "-" + maxRange, value -> value == null
        || value.doubleValue() >= minRange.doubleValue() && value.doubleValue() <= maxRange.doubleValue());
  }

  /**
   * Builds a very simple wrapper rule for validating collections.
   */
  public static <T> Rule<Collection<T>> each(Rule<T> rule) {
    return new SimpleRule<>(rule.getAilment().getName(),
        col -> col == null || col.stream().allMatch(rule::test), rule.getAilment().getSeverity());
  }
}
