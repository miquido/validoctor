package com.miquido.validoctor.definition;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class Rules {

  private Rules() {}

  private static final Rule<String> STRING_NOT_EMPTY =
      new SimpleRule<>("NOT_EMPTY_REQUIRED", str -> str == null || !str.isEmpty());

  private static final Rule<String> STRING_TRIMMED_NOT_EMPTY =
      new SimpleRule<>("NOT_EMPTY_NOR_WHITESPACE_ONLY_REQUIRED", str -> str == null || !str.trim().isEmpty());

  private static final Rule<String> STRING_ALPHANUMERIC =
      new SimpleRule<>("ALPHANUMERIC_REQUIRED", str -> str == null || str.chars().allMatch(Character::isLetterOrDigit));

  private static final Rule<String> STRING_ALPHABETIC =
      new SimpleRule<>("ALPHABETIC_REQUIRED", str -> str == null || str.chars().allMatch(Character::isLetter));

  private static final Rule<String> STRING_NO_SPACE_PADDING =
      new SimpleRule<>("NO_WHITESPACE_PADDING_REQUIRED",
          str -> str == null || str.trim().equals(str));

  private static final Rule<Object> NULL =
      new SimpleRule<>("NULL_REQUIRED", Objects::isNull);

  private static final SimpleRule<Object> NOT_NULL =
      new SimpleRule<>("NOT_NULL_REQUIRED", Objects::nonNull);

  private static final Rule<Boolean> FALSE =
      new SimpleRule<>("FALSE_REQUIRED", value -> value == null || !value);

  private static final Rule<Boolean> TRUE =
      new SimpleRule<>("TRUE_REQUIRED", value -> value == null || value);


  /**
   * Passed: patient is not null.<br>
   * Violated: patient is null.
   */
  public static <T> Rule<T> notNull() {
    return (Rule<T>) NOT_NULL;
  }

  /**
   * Passed: patient is null.<br>
   * Violated: patient is not null.
   */
  public static <T> Rule<T> isNull() {
    return (Rule<T>) NULL;
  }

  /**
   * Passed: patient is null or false.<br>
   * Violated: patient is true.
   */
  public static Rule<Boolean> isFalse() {
    return FALSE;
  }

  /**
   * Passed: patient is null of true.<br>
   * Violated: patient is false.
   */
  public static Rule<Boolean> isTrue() {
    return TRUE;
  }

  /**
   * Passed: patient is null or not empty string.<br>
   * Violated: patient is empty string.
   * @see Rules#stringTrimmedNotEmpty()
   */
  public static Rule<String> stringNotEmpty() {
    return STRING_NOT_EMPTY;
  }

  /**
   * Passed: patient is null, not empty and not all-whitespace string.<br>
   * Violated: patient is empty or all-whitespace string.
   * @see Rules#stringNotEmpty()
   */
  public static Rule<String> stringTrimmedNotEmpty() {
    return STRING_TRIMMED_NOT_EMPTY;
  }

  /**
   * Passed: patient is null or contains only letters and digits.<br>
   * Violated: patient contains any character that is not letter or digit.
   */
  public static Rule<String> stringAlphanumeric() {
    return STRING_ALPHANUMERIC;
  }

  /**
   * Passed: patient is null or contains only letters.<br>
   * Violated: patient contains any character that is not letter.
   */
  public static Rule<String> stringAlphabetic() {
    return STRING_ALPHABETIC;
  }

  /**
   * Passed: patient is null or does not have any leading or trailing spaces.<br>
   * Violated: patient contains leading or trailing space(s).
   */
  public static Rule<String> stringNoSpacePadding() {
    return STRING_NO_SPACE_PADDING;
  }

  /**
   * Passed: patient is null or number with value greater than or equal to 0.<br>
   * Violated: patient is number with value lesser than 0.
   */
  public static <T extends Number> Rule<T> numberNonNegative() {
    return new SimpleRule<>("NON_NEGATIVE_REQUIRED", value -> value == null || value.doubleValue() >= 0);
  }

  /**
   * Passed: patient is null or number with value greater than 0.<br>
   * Violated: patient is number with value lesser than or equal to 0.
   */
  public static <T extends Number> Rule<T> numberPositive() {
    return new SimpleRule<>("POSITIVE_REQUIRED", value -> value == null || value.doubleValue() > 0);
  }

  /**
   * Passed: patient is null or not empty collection.<br>
   * Violated: patient is empty collection.
   */
  public static Rule<Collection<?>> collectionNotEmpty() {
    return new SimpleRule<>("NOT_EMPTY_REQUIRED", collection -> collection == null || !collection.isEmpty());
  }
  /**
   * Passed: patient is null or a collection with size equal or greater than specified {@code minSize}.<br>
   * Violated: patient is a collection with size lesser than specified {@code minSize}.
   */
  public static Rule<Collection<?>> collectionMinSize(int minSize) {
    return new SimpleRule<>("SIZE_TOO_LITTLE", collection -> collection == null || collection.size() >= minSize);
  }

  /**
   * Passed: patient is null or a collection with size equal or lesser than specified {@code maxSize}.<br>
   * Violated: patient is a collection with size greater than specified {@code maxSize}.
   */
  public static Rule<Collection<?>> collectionMaxSize(int maxSize) {
    return new SimpleRule<>("SIZE_TOO_LARGE", collection -> collection == null || collection.size() <= maxSize);
  }

  /**
   * Passed: patient is null or a collection with size equal or greater than specified {@code minSize}
   * and equal or lesser than specified {@code maxSize} .<br>
   * Violated: patient is a collection with size greater than specified {@code maxSize} or lesser than specified {@code minSize}.
   */
  public static Rule<Collection<?>> collectionSizeIn(int minSize, int maxSize) {
    return new SimpleRule<>("INVALID_SIZE", collection -> collection == null
        || (collection.size() <= maxSize && collection.size() >= minSize));
  }

  /**
   * Passed: patient is null or a collection containing element equal to specified {@code element}.<br>
   * Violated: patient is a collection not containing element equal to specified {@code element}.
   */
  public static <T> Rule<Collection<T>> collectionContains(T element) {
    return new SimpleRule<>("MISSING_REQUIRED_ELEMENT", collection -> collection == null
        || collection.contains(element));
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength}.<br>
   * Violated: patient is string with length lesser than specified {@code minLength}.
   */
  public static Rule<String> stringMinLength(int minLength) {
    return new SimpleRule<>("TOO_SHORT", str -> str == null || str.length() >= minLength);
  }

  /**
   * Passed: patient is null or string with length lesser than or equal to specified {@code maxLength}.<br>
   * Violated: patient is string with length greater than specified {@code maxLength}.
   */
  public static Rule<String> stringMaxLength(int maxLength) {
    return new SimpleRule<>("TOO_LONG", str -> str == null || str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength} and
   * lesser than or equal to specified {@code maxLength}.<br>
   * Violated: patient is string with length lesser than or equal to specified {@code minLength} or
   * greater than specified {@code maxLength}.
   */
  public static Rule<String> stringLengthInRange(int minLength, int maxLength) {
    return new SimpleRule<>("TOO_SHORT_OR_TOO_LONG",
        str -> str == null || str.length() >= minLength && str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length equal to specified {@code exactLength}.<br>
   * Violated: patient is string with length other than specified {@code exactLength}.
   */
  public static Rule<String> stringExactLength(int exactLength) {
    return new SimpleRule<>("INVALID_LENGTH", str -> str == null || str.length() == exactLength);
  }

  /**
   * Passed: patient is null or string that contains specified {@code text}.<br>
   * Violated: patient is string not containing specified {@code text}.
   */
  public static Rule<String> stringContains(String text) {
    return new SimpleRule<>("LACKS_REQUIRED_TEXT", str -> str == null || str.contains(text));
  }

  /**
   * Passed: patient is null or string that matches specified {@code regex}.<br>
   * Violated: patient is string not matching specified {@code regex}.
   */
  public static Rule<String> stringMatches(String regex) {
    return new SimpleRule<>("MUST_MATCH_REGEX", str -> str == null || str.matches(regex));
  }
  /**
   * Passed: patient is null or number with value {@code >= minRange} and {@code <= maxRange}.<br>
   * Violated: patient is number with value {@code < minRange} or {@code > maxRange}.
   */
  public static <T extends Number> Rule<T> numberInRange(Number minRange, Number maxRange) {
    return new SimpleRule<>("TOO_LOW_OR_TOO_HIGH", value -> value == null
        || value.doubleValue() >= minRange.doubleValue() && value.doubleValue() <= maxRange.doubleValue());
  }

  /**
   * Passed: patient is equal to at least one of values passed in allowedValues argument.<br>
   * Violated: patient is not equal to any of the values passed in allowedValues.<br><br>
   * Note: {@code valueIn(null)} is not directly supported. To check if patient is null use {@link Rules#isNull()}.
   * To use valueIn with dynamic, nullable allowed value use {@code valueIn(listOf<TYPE>(null))} or
   * {@code valueIn(Arrays.asList(null))}.
   */
  @SafeVarargs
  public static <T> Rule<T> valueIn(T... allowedValues) {
    List<T> list = Arrays.asList(allowedValues);
    return new SimpleRule<>("VALUE_NOT_ALLOWED", list::contains);
  }

  /**
   * Passed: patient is equal to at least one element of collection passed in allowedValues argument.<br>
   * Violated: patient is not equal to any element of the collection passed in allowedValues.<br><br>
   * Note: {@code valueIn(null)} is not directly supported. To check if patient is null use {@link Rules#isNull()}.
   * To use valueIn with dynamic, nullable allowed value use {@code valueIn(listOf<TYPE>(null))} or
   * {@code valueIn(Arrays.asList(null))}.
   */
  public static <T> Rule<T> valueIn(Collection<T> allowedValues) {
    return new SimpleRule<>("VALUE_NOT_ALLOWED", allowedValues::contains);
  }

  /**
   * Passed: patient is not equal to any of the values passed in allowedValues.<br>
   * Violated: patient is equal to at least one of values passed in allowedValues argument.<br><br>
   */
  @SafeVarargs
  public static <T> Rule<T> valueNotIn(T... disallowedValues) {
    List<T> list = Arrays.asList(disallowedValues);
    return new SimpleRule<>("VALUE_NOT_ALLOWED", obj -> !list.contains(obj));
  }

  /**
   * Passed: patient is not equal to any element of the collection passed in disallowedValues.<br>
   * Violated: patient is equal to at least one element of collection passed in disallowedValues argument.<br><br>
   */
  public static <T> Rule<T> valueNotIn(Collection<T> disallowedValues) {
    return new SimpleRule<>("VALUE_NOT_ALLOWED", obj -> !disallowedValues.contains(obj));
  }

  /**
   * Passed: patient is null or equal to specified expectedValue.<br>
   * Violated: patient is not equal to specified expectedValue.
   */
  public static Rule<Object> equalTo(Object expectedValue) {
    return new SimpleRule<>("VALUE_NOT_ALLOWED", obj -> obj == null || obj.equals(expectedValue));
  }

  /**
   * Passed: patient is null or not equal to specified unexpectedValue.<br>
   * Violated: patient is equal to specified unexpectedValue.
   */
  public static Rule<Object> notEqualTo(Object unexpectedValue) {
    return new SimpleRule<>("VALUE_NOT_ALLOWED", obj -> obj == null || !obj.equals(unexpectedValue));
  }


  /**
   * Creates a new Rule identical to passed one, except it only tests its predicate if specified condition is true.
   * Always passes otherwise.
   * @param condition condition to meet for predicate test to occur
   * @param rule rule
   * @param <T> patient type
   * @return new Rule with conditional predicate test
   */
  public static <T> Rule<T> conditional(Predicate<T> condition, Rule<T> rule) {
    return rule.withCondition(condition);
  }

  /**
   * Creates new Rules identical to passed ones, except they only test their predicate if specified condition is true.
   * They always pass otherwise.
   * @param condition condition to meet for predicate test to occur
   * @param rules rules
   * @param <T> patient type
   * @return array of new Rules with conditional predicate test
   */
  @SafeVarargs
  public static <T> Rule<T>[] conditional(Predicate<T> condition, Rule<T>... rules) {
    return Arrays.stream(rules).map(rule -> rule.withCondition(condition)).toArray(Rule[]::new);
  }

  /**
   * Creates a new Rule that sequentially executes specified Rules. If any of the Rules fails,
   * none of the following ones will test their predicate.
   * @param rules rules to sequentially test during examination
   * @param <T> patient type
   * @return new Rule that will sequentially test all passed Rules
   */
  @SafeVarargs
  public static <T> Rule<T> chained(Rule<T>... rules) {
    return Arrays.stream(rules).reduce((rule1, rule2) -> rule2.withDependency(rule1)).orElse(null);
  }

  /**
   * Creates a new Rule that is identical to specified rule but has the specified violationMessage.
   * @param violationMessage violationMessage to use
   * @param rule rule
   * @param <T> patient type
   * @return new Rule with specified violationMessage
   */
  public static <T> Rule<T> named(String violationMessage, Rule<T> rule) {
    return rule.withViolationMessage(violationMessage);
  }

  /**
   * Creates a batch of Rules that will be tested as an atomic package - all Rules will always be executed.
   * <br>This may be useful for separating sets of Rules into steps of more complicated hierarchical validation scenarios.
   * <br>Any dependencies and conditions added with {@link Rules#chained(Rule[])} or
   * {@link Rules#conditional(Predicate, Rule)} will apply to whole batch.
   * <br>{@link Rules#named(String, Rule)} is not supported by BatchRules - rules passed as batch params can still
   * be named though.
   * @param rules Rules to join into a batch
   * @param <T> patient type
   * @return BatchRule including specified rules
   */
  public static <T> Rule<T> batch(Rule<T>... rules) {
    return new BatchRule<>(Arrays.asList(rules));
  }
}
