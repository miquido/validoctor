package com.miquido.validoctor2.rule;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class Rules2 {

  private Rules2() {}

  private static final Rule2<String> STRING_NOT_EMPTY =
      new SimpleRule2<>("NOT_EMPTY_REQUIRED", str -> str == null || !str.isEmpty());

  private static final Rule2<String> STRING_TRIMMED_NOT_EMPTY =
      new SimpleRule2<>("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", str -> str == null || !str.trim().isEmpty());

  private static final Rule2<String> STRING_ALPHANUMERIC =
      new SimpleRule2<>("ALPHANUMERIC_REQUIRED", str -> str == null || str.chars().allMatch(Character::isLetterOrDigit));

  private static final Rule2<String> STRING_ALPHABETIC =
      new SimpleRule2<>("ALPHABETIC_REQUIRED", str -> str == null || str.chars().allMatch(Character::isLetter));

  private static final Rule2<Object> NULL =
      new SimpleRule2<>("NULL_REQUIRED", Objects::isNull);

  private static final SimpleRule2<Object> NOT_NULL =
      new SimpleRule2<>("NOT_NULL_REQUIRED", Objects::nonNull);

  private static final Rule2<Boolean> FALSE =
      new SimpleRule2<>("FALSE_REQUIRED", value -> value == null || !value);

  private static final Rule2<Boolean> TRUE =
      new SimpleRule2<>("TRUE_REQUIRED", value -> value == null || value);

  private static final Rule2<Number> NUMBER_POSITIVE =
      new SimpleRule2<>("POSITIVE_REQUIRED", value -> value == null || value.doubleValue() > 0);

  private static final Rule2<Number> NUMBER_NON_NEGATIVE =
      new SimpleRule2<>("NON_NEGATIVE_REQUIRED", value -> value == null || value.doubleValue() >= 0);

  /**
   * Passed: patient is not null.<br/>
   * Violated: patient is null.
   */
  public static <T> Rule2<T> notNull() {
    return (Rule2<T>) NOT_NULL;
  }

  /**
   * Passed: patient is null.<br/>
   * Violated: patient is not null.
   */
  public static <T> Rule2<T> isNull() {
    return (Rule2<T>) NULL;
  }

  /**
   * Passed: patient is null or false.<br/>
   * Violated: patient is true.
   */
  public static Rule2<Boolean> isFalse() {
    return FALSE;
  }

  /**
   * Passed: patient is null of true.<br/>
   * Violated: patient is false.
   */
  public static Rule2<Boolean> isTrue() {
    return TRUE;
  }

  /**
   * Passed: patient is null or not empty string.<br/>
   * Violated: patient is empty string.
   * @see Rules2#stringTrimmedNotEmpty()
   */
  public static Rule2<String> stringNotEmpty() {
    return STRING_NOT_EMPTY;
  }

  /**
   * Passed: patient is null, not empty and not all-whitespace string.<br/>
   * Violated: patient is empty or all-whitespace string.
   * @see Rules2#stringNotEmpty()
   */
  public static Rule2<String> stringTrimmedNotEmpty() {
    return STRING_TRIMMED_NOT_EMPTY;
  }

  /**
   * Passed: patient is null or contains only letters and digits.<br/>
   * Violated: patient contains any character that is not letter or digit.
   */
  public static Rule2<String> stringAlphanumeric() {
    return STRING_ALPHANUMERIC;
  }

  /**
   * Passed: patient is null or contains only letters.<br/>
   * Violated: patient contains any character that is not letter.
   */
  public static Rule2<String> stringAlphabetic() {
    return STRING_ALPHABETIC;
  }

  /**
   * Passed: patient is null or number with value greater than or equal to 0.<br/>
   * Violated: patient is number with value lesser than 0.
   */
  public static Rule2<Number> numberNonNegative() {
    return NUMBER_NON_NEGATIVE;
  }

  /**
   * Passed: patient is null or number with value greater than 0.<br/>
   * Violated: patient is number with value lesser than or equal to 0.
   */
  public static Rule2<Number> numberPositive() {
    return NUMBER_POSITIVE;
  }

  /**
   * Passed: patient is null or not empty collection.<br/>
   * Violated: patient is empty collection.
   */
  public static <T> Rule2<Collection<T>> collectionNotEmpty() {
    return new SimpleRule2<>("NOT_EMPTY_REQUIRED", collection -> collection == null || !collection.isEmpty());
  }
  /**
   * Passed: patient is null or a collection with size equal or greater than specified {@code minSize}.<br/>
   * Violated: patient is a collection with size lesser than specified {@code minSize}.
   */
  public static <T> Rule2<Collection<T>> collectionMinSize(int minSize) {
    return new SimpleRule2<>("SIZE_TOO_LITTLE", collection -> collection == null || collection.size() >= minSize);
  }

  /**
   * Passed: patient is null or a collection with size equal or lesser than specified {@code maxSize}.<br/>
   * Violated: patient is a collection with size greater than specified {@code maxSize}.
   */
  public static <T> Rule2<Collection<T>> collectionMaxSize(int maxSize) {
    return new SimpleRule2<>("SIZE_TOO_LARGE", collection -> collection == null || collection.size() <= maxSize);
  }

  /**
   * Passed: patient is null or a collection with size equal or greater than specified {@code minSize}
   * and equal or lesser than specified {@code maxSize} .<br/>
   * Violated: patient is a collection with size greater than specified {@code maxSize} or lesser than specified {@code minSize}.
   */
  public static <T> Rule2<Collection<T>> collectionSizeIn(int minSize, int maxSize) {
    return new SimpleRule2<>("INVALID_SIZE", collection -> collection == null
        || (collection.size() <= maxSize && collection.size() >= minSize));
  }

  /**
   * Passed: patient is null or a collection containing element equal to specified {@code element}.<br/>
   * Violated: patient is a collection not containing element equal to specified {@code element}.
   */
  public static <T> Rule2<Collection<T>> collectionContains(T element) {
    return new SimpleRule2<>("MISSING_REQUIRED_ELEMENT", collection -> collection == null
        || collection.contains(element));
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength}.<br/>
   * Violated: patient is string with length lesser than specified {@code minLength}.
   */
  public static Rule2<String> stringMinLength(int minLength) {
    return new SimpleRule2<>("TOO_SHORT", str -> str == null || str.length() >= minLength);
  }

  /**
   * Passed: patient is null or string with length lesser than or equal to specified {@code maxLength}.<br/>
   * Violated: patient is string with length greater than specified {@code maxLength}.
   */
  public static Rule2<String> stringMaxLength(int maxLength) {
    return new SimpleRule2<>("TOO_LONG", str -> str == null || str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength} and
   * lesser than or equal to specified {@code maxLength}.<br/>
   * Violated: patient is string with length lesser than or equal to specified {@code minLength} or
   * greater than specified {@code maxLength}.
   */
  public static Rule2<String> stringLengthInRange(int minLength, int maxLength) {
    return new SimpleRule2<>("TOO_SHORT_OR_TOO_LONG",
        str -> str == null || str.length() >= minLength && str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length equal to specified {@code exactLength}.<br/>
   * Violated: patient is string with length other than specified {@code exactLength}.
   */
  public static Rule2<String> stringExactLength(int exactLength) {
    return new SimpleRule2<>("INVALID_LENGTH", str -> str == null || str.length() == exactLength);
  }

  /**
   * Passed: patient is null or string that contains specified {@code text}.<br/>
   * Violated: patient is string not containing specified {@code text}.
   */
  public static Rule2<String> stringContains(String text) {
    return new SimpleRule2<>("LACKS_REQUIRED_TEXT", str -> str == null || str.contains(text));
  }

  /**
   * Passed: patient is null or string that matches specified {@code regex}.<br/>
   * Violated: patient is string not matching specified {@code regex}.
   */
  public static Rule2<String> stringMatches(String regex) {
    return new SimpleRule2<>("MUST_MATCH_REGEX", str -> str == null || str.matches(regex));
  }
  /**
   * Passed: patient is null or number with value {@code >= minRange} and {@code <= maxRange}.<br/>
   * Violated: patient is number with value {@code < minRange} or {@code > maxRange}.
   */
  public static Rule2<Number> numberInRange(Number minRange, Number maxRange) {
    return new SimpleRule2<>("TOO_LOW_OR_TOO_HIGH", value -> value == null
        || value.doubleValue() >= minRange.doubleValue() && value.doubleValue() <= maxRange.doubleValue());
  }

  /**
   * Passed: patient is equal to at least one of values passed in allowedValues argument.<br/>
   * Violated: patient is not equal to any of the values passed in allowedValues.<br/><br/>
   * Note: {@code valueIn(null)} is not directly supported. To check if patient is null use {@link Rules2#isNull()}.
   * To use valueIn with dynamic, nullable allowed value use {@code valueIn(listOf<TYPE>(null))} or
   * {@code valueIn(Arrays.asList(null))}.
   */
  @SafeVarargs
  public static <T> Rule2<T> valueIn(T... allowedValues) {
    List<T> list = Arrays.asList(allowedValues);
    return new SimpleRule2<>("VALUE_NOT_ALLOWED", list::contains);
  }

  /**
   * Passed: patient is equal to at least one element of collection passed in allowedValues argument.<br/>
   * Violated: patient is not equal to any element of the collection passed in allowedValues.<br/><br/>
   * Note: {@code valueIn(null)} is not directly supported. To check if patient is null use {@link Rules2#isNull()}.
   * To use valueIn with dynamic, nullable allowed value use {@code valueIn(listOf<TYPE>(null))} or
   * {@code valueIn(Arrays.asList(null))}.
   */
  public static <T> Rule2<T> valueIn(Collection<T> allowedValues) {
    return new SimpleRule2<>("VALUE_NOT_ALLOWED", allowedValues::contains);
  }

  /**
   * Passed: patient is null or equal to specified expectedValue.<br/>
   * Violated: patient is not equal to specified expectedValue.
   */
  public static Rule2<Object> equalTo(Object expectedValue) {
    return new SimpleRule2<>("VALUE_NOT_ALLOWED", obj -> obj == null || obj.equals(expectedValue));
  }
}
