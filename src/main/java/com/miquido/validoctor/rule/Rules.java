package com.miquido.validoctor.rule;

import com.miquido.validoctor.ailment.Ailment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.miquido.validoctor.ailment.SpecsKey.*;

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
   * Passed: patient is null or contains only letters and digits.<br/>
   * Violated: patient contains any character that is not letter or digit.
   */
  public static Rule<String> stringAlphanumeric() {
    return STRING_ALPHANUMERIC;
  }

  /**
   * Passed: patient is null or contains only letters.<br/>
   * Violated: patient contains any character that is not letter.
   */
  public static Rule<String> stringAlphabetic() {
    return STRING_ALPHABETIC;
  }

  /**
   * Passed: patient is null or not empty collection.<br/>
   * Violated: patient is empty collection.
   */
  public static <T> Rule<Collection<T>> collectionNotEmpty() {
    return new SimpleRule<>("COLLECTION_NOT_EMPTY", collection -> collection == null || !collection.isEmpty());
  }

  /**
   * Passed: patient is null or a collection with size equal or greater than specified {@code minSize}.<br/>
   * Violated: patient is a collection with size lesser than specified {@code minSize}.
   */
  public static <T> Rule<Collection<T>> collectionMinSize(int minSize) {
    return new SimpleRule<>("COLLECTION_MIN_SIZE:" + minSize, collection -> collection == null || collection.size() >= minSize);
  }

  /**
   * Passed: patient is null or a collection with size equal or lesser than specified {@code maxSize}.<br/>
   * Violated: patient is a collection with size greater than specified {@code maxSize}.
   */
  public static <T> Rule<Collection<T>> collectionMaxSize(int maxSize) {
    return new SimpleRule<>("COLLECTION_MAX_SIZE:" + maxSize, collection -> collection == null || collection.size() <= maxSize);
  }

  /**
   * Passed: patient is null or a collection with size equal or greater than specified {@code minSize}
   * and equal or lesser than specified {@code maxSize} .<br/>
   * Violated: patient is a collection with size greater than specified {@code maxSize} or lesser than specified {@code minSize}.
   */
  public static <T> Rule<Collection<T>> collectionSizeIn(int minSize, int maxSize) {
    return new SimpleRule<>("COLLECTION_MAX_SIZE:" + maxSize, collection -> collection == null
        || (collection.size() <= maxSize && collection.size() >= minSize));
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength}.<br/>
   * Violated: patient is string with length lesser than specified {@code minLength}.
   */
  public static Rule<String> stringMinLength(int minLength) {
    Map<String, Object> params = new HashMap<>();
    params.put(MIN_LENGTH, minLength);
    return new SimpleRule<>("STRING_MIN_LENGTH:" + minLength, params, str -> str == null || str.length() >= minLength);
  }

  /**
   * Passed: patient is null or string with length lesser than or equal to specified {@code maxLength}.<br/>
   * Violated: patient is string with length greater than specified {@code maxLength}.
   */
  public static Rule<String> stringMaxLength(int maxLength) {
    Map<String, Object> params = new HashMap<>();
    params.put(MAX_LENGTH, maxLength);
    return new SimpleRule<>("STRING_MAX_LENGTH:" + maxLength, params, str -> str == null || str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length greater than or equal to specified {@code minLength} and
   * lesser than or equal to specified {@code maxLength}.<br/>
   * Violated: patient is string with length lesser than or equal to specified {@code minLength} or
   * greater than specified {@code maxLength}.
   */
  public static Rule<String> stringLengthInRange(int minLength, int maxLength) {
    Map<String, Object> params = new HashMap<>();
    params.put(MIN_LENGTH, minLength);
    params.put(MAX_LENGTH, maxLength);
    return new SimpleRule<>("STRING_LENGTH_IN_RANGE:" + minLength + "-" + maxLength, params,
        str -> str == null || str.length() >= minLength && str.length() <= maxLength);
  }

  /**
   * Passed: patient is null or string with length equal to specified {@code exactLength}.<br/>
   * Violated: patient is string with length other than specified {@code exactLength}.
   */
  public static Rule<String> stringExactLength(int exactLength) {
    Map<String, Object> params = new HashMap<>();
    params.put(REQUIRED_LENGTH, exactLength);
    return new SimpleRule<>("STRING_EXACT_LENGTH:" + exactLength, params, str -> str == null || str.length() == exactLength);
  }

  /**
   * Passed: patient is null or string that contains specified {@code text}.<br/>
   * Violated: patient is string not containing specified {@code text}.
   */
  public static Rule<String> stringContains(String text) {
    Map<String, Object> params = new HashMap<>();
    params.put(TEXT, text);
    return new SimpleRule<>("STRING_CONTAINS:" + text, params, str -> str == null || str.contains(text));
  }

  /**
   * Passed: patient is null or string that matches specified {@code regex}.<br/>
   * Violated: patient is string not matching specified {@code regex}.
   */
  public static Rule<String> stringMatches(String regex) {
    Map<String, Object> params = new HashMap<>();
    params.put(REGEX, regex);
    return new SimpleRule<>("STRING_MATCHES:" + regex, params, str -> str == null || str.matches(regex));
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
    Map<String, Object> params = new HashMap<>();
    params.put(MIN_RANGE, minRange);
    params.put(MAX_RANGE, maxRange);
    return new SimpleRule<>("NUMBER_IN_RANGE:" + minRange + "-" + maxRange, params, value -> value == null
        || value.doubleValue() >= minRange.doubleValue() && value.doubleValue() <= maxRange.doubleValue());
  }

  /**
   * Passed: patient is equal to at least one of values passed in allowedValues argument.<br/>
   * Violated: patient is not equal to any of the values passed in allowedValues.<br/><br/>
   * Note: {@code valueIn(null)} is not directly supported. To check if patient is null use {@link Rules#isNull()}.
   * To use valueIn with dynamic, nullable allowed value use {@code valueIn(listOf<TYPE>(null))} or
   * {@code valueIn(Arrays.asList(null))}.
   */
  @SafeVarargs
  public static <T> Rule<T> valueIn(T... allowedValues) {
    Map<String, Object> params = new HashMap<>();
    List<T> list = Arrays.asList(allowedValues);
    params.put(ALLOWED_VALUES, list);
    return new SimpleRule<>("VALUE_IN", params, list::contains);
  }

  /**
   * Passed: patient is equal to at least one element of list passed in allowedValues argument.<br/>
   * Violated: patient is not equal to any element of the list passed in allowedValues.<br/><br/>
   * Note: {@code valueIn(null)} is not directly supported. To check if patient is null use {@link Rules#isNull()}.
   * To use valueIn with dynamic, nullable allowed value use {@code valueIn(listOf<TYPE>(null))} or
   * {@code valueIn(Arrays.asList(null))}.
   */
  public static <T> Rule<T> valueIn(List<T> allowedValues) {
    Map<String, Object> params = new HashMap<>();
    params.put(ALLOWED_VALUES, allowedValues);
    return new SimpleRule<>("VALUE_IN", params, allowedValues::contains);
  }

  /**
   * Passed: patient is null or equal to specified expectedValue.<br/>
   * Violated: patient is not equal to specified expectedValue.
   */
  public static Rule<Object> equalTo(Object expectedValue) {
    Map<String, Object> params = new HashMap<>();
    params.put(ALLOWED_VALUES, expectedValue);
    return new SimpleRule<>("EQUAL", params, obj -> obj == null || obj.equals(expectedValue));
  }

  /**
   * Creates a rule that negates the rule passed as argument. Result of applying this rule will be completely opposite
   * than that of the original rule. "NOT_" string is prepended to the name of {@link Ailment} resulting from violation
   * of this rule. Any {@link Rule#getParams() params} of the original Rule are copied untouched.
   */
  public static <T> Rule<T> not(Rule<T> rule) {
    return new SimpleRule<>("NOT_" + rule.peekAilment().getName(), rule.getParams(),
        obj -> rule.apply(obj) != null, rule.peekAilment().getSeverity());
  }

  /**
   * Builds a very simple wrapper rule for validating all elements of a collection.
   */
  public static <T> Rule<Collection<T>> each(Rule<T> rule) {
    return new SimpleRule<>(rule.peekAilment().getName(), rule.getParams(),
        col -> col == null || col.stream().allMatch(obj -> rule.apply(obj) == null), rule.peekAilment().getSeverity());
  }

  /**
   * Accepts any rule and creates an exactly the same rule with new name.
   * This new name will be used in Ailment stated in case of violation.
   * @param name name for the Rule and Ailment
   * @param rule
   * @return clone of the passed rule with the same predicate, params and severity, but new name
   */
  public static <T> Rule<T> named(String name, Rule<T> rule) {
    return new SimpleRule<>(name, rule.getParams(), obj -> rule.apply(obj) == null, rule.peekAilment().getSeverity());
  }

  /**
   * Accepts any rule and creates an exactly the same rule that will not return patients original value in Ailment
   * stated on violation.<br/><br/>
   * Note: when combining with {@link Rules#each(Rule)} use {@code each(confidential())}, not {@code confidential(each())}.
   * @param rule
   * @return passed rule altered to not put patient original value in Ailment.
   */
  public static <T> Rule<T> confidential(Rule<T> rule) {
    return new ConfidentialSimpleRule<>(rule);
  }
}
