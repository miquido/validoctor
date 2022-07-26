![Tests](https://github.com/miquido/validoctor/workflows/Tests/badge.svg?branch=develop)

Validoctor is an all-purpose data validator for backend java and kotlin projects. It performs validation basing on rules 
passed along with object to be validated. Specially designed for simple validation of complex structures.

# Motivation
Validoctor is a tool that allows defining and performing validations in a clean, simple, deterministic, zero-magic way. 
Key features:
* validation rules clearly defined and well separated from model objects themselves, imposing no restrictions upon their 
structure and allowing you to keep them clean
* no generated code nor annotation processing involved for simple, understandable and readable validation process
* concise API and rich collection of predefined rules make even the most complex validations require little code
* flexible, configurable and extensible - its quick and easy to define custom validations and alter Validoctor's behavior
* readable and useful error reports, ready to be propagated back to clients as they are
* full control over validation process - call it when you want, run it on whatever number of threads, 
split it into several steps if you feel like it, process results however you want

# Getting started
To add validoctor to your project:
```groovy
repositories {
    mavenCentral()
}
dependencies {
  implementation "com.miquido:validoctor:2.0.0-rc2"
}
```
Validoctor is a data validation library handling validations on complex data structures. It is designed to be 
used as a standalone complex validation solution, and not for cooperation with Bean Validation or any other 
validation solution.

Basic concepts and a production-grade example are shown below. For more specific use cases, please refer to tests. 
When in doubt about how something should be used, answer is usually easily found there.

# Vocabulary
* Ailment - like a single violation of a Rule. It carries a violator's name (typically a field name) and a violation message.
* Diagnosis - validation report stating whether object is valid or not and containing all Ailments discovered in it. 
It is structured to be useful for any client code that may want to read it, so it may be used directly as, for example, 
a response body.

# Quick start examples
Main class to use is Validoctor. All its methods are static. You can set global behavior in cases of failed validations 
using `setThrowing(boolean)` and `setExceptionFactory(Function<Diagnosis, RuntimeException>)` methods.
```java
Validoctor.setThrowing(false); //will return Diagnosis objects after validating
Validoctor.setThrowing(true): //will throw DiagnosisExceptions on failed validations
Validoctor.setExceptionFactory(diagnosis -> new CustomException(diagnosis)); //will throw CustomExceptions instead, if setThrowing is true
```
For validating primitive or String values, just use `SimpleRule`s. Most commonly used ones are supplied in Rules class.
```java
Diagnosis diagnosis = Validoctor.examine(stringToValidate, notNull(), stringAlphabetic(), stringTrimmedNotEmpty());
```
This will check if `stringToValidate` is not null, not empty or whitespace only and only containing letters.

Validoctor's main strength lies in validating complex objects in one call. For this, a special `RuleBuilder` is used. 
For example this validates fields foo and bar of object `Example` with specified rules:
```java
Rule<Example> exampleRule = Validoctor.rulesFor(Example.class)
        .field("foo", notNull(), numberPositive())
        .field("bar", stringMinLength(3), stringMaxLength(20))
        .build();
```
It is then passed for Validoctor's examination the same way as `SimpleRule`s:
```java
Diagnosis diagnosis = Validoctor.examine(exampleObject, exampleRule);
```
It can also accept several rules just as it could accept several `SimpleRule`s.
You will find that builder returned by `rulesFor` class provides a multitude of other options for attaching rules to fields. 
It is possible to attach rule sets to multiple fields at once, to all fields of same type, to reductions of two fields,
to elements of collection that is a field in the object etc. Rules can also be made conditional and dependent on success 
of other rules.

All Rules are stateless and can be reused for multiple validations.

# Production grade example
Imagine you need to validate an instance of a `Product` defined like this:
```kotlin
//Those are Kotlin data classes, for pure Java people out there it is the same as annotating a class with Lombok's @Data
data class NutritionFacts(val kcal: Int?, 
                          val fibre: Double?, 
                          val protein: Double?, 
                          val fat: Double?)

data class Comment(val authorId: Long?, 
                   val text: String?)

data class Product(val name: String?, 
                   val skuId: String?, 
                   val description: String?, 
                   val weightG: Float?, 
                   val volumeMl: Float?,
                   val nutritionFacts: NutritionFacts?, 
                   val glutenFree: Boolean?, 
                   val vegan: Boolean?,
                   val comments: List<Comment>?, 
                   val reviewScores: List<Int>?)
```

Let's start with thinking about what we want to validate. Thinking on a per-class basis is the recommended approach 
for using Validoctor. So, starting with `NutritionFacts` class, we surely want all the values to be positive. 
We write a simple rule for that:
```kotlin
val nutritionFactsRule =
  Validoctor.rulesFor(NutritionFacts::class.java)
    .allAssignable(Number::class.java, numberPositive())
    .build()
```
`Validoctor.rulesFor` defines a list of rules that are applied to specified fields of the validated object. 
Validoctor uses reflection to find the values of the fields to apply the rules to. 
We used `allAssignable` to tell the rule that we want it to read all fields of type `Number` (or its subtypes) and apply 
the `numberPositive` rule to each of them. It is a predefined rule available in `Rules` class that just checks if number is 
larger than 0.

Ok, so that is what we want to validate in `NutritionFacts`. Let's move on to `Comment` class. We certainly need a valid, 
non-null `authorId`, and we want the `text` of the comment to be not empty, not longer that certain characters count and not 
contain any inappropriate words. For that, we will first need to define our own custom censor rule like this:
```kotlin
val stringCensorRule: SimpleRule<String> =
  SimpleRule("CENSORED_WORD") { str -> str == null || !str.contains("fuck", true) }
```
In case when there is no appropriate predefined rule available in `Rules` class, we can create our own rules as shown above. 
Using SimpleRule constructor should cover 99.99% cases, so if you find yourself wanting to do something more complicated 
it might be a sign that you are trying to over-engineer. What we did here is we specified a violation message to show when 
rule fails, and a predicate that will be applied to patients to determine whether they are valid or not.

Now, we are ready to create a rule for Comment object that will specify all validations we need:
```kotlin
val commentRules =
  Validoctor.rulesFor(Comment::class.java)
    .field("authorId", notNull(), numberPositive())
    .field("text", notNull(), stringTrimmedNotEmpty(), stringMaxLength(500), stringCensorRule)
    .build()
```
We used `Validoctor.rulesFor` just like for `NutritionFacts`. This time though, we specify fields we want the rules to be 
applied to one by one, by their name. Each `field` call will make resulting rule apply all the specified rules to given field. 
So here, `authorId` will be checked if it is not null and then if it is a positive number, and `text` will be checked for nullity, 
not emptiness, max allowed length and inappropriate words with our custom censor rule.

And now, for the biggest task: we need to apply a series of various validations to fields of `Product` class. For better 
readability, we can decide to split validations of such complex classes into a few separate rules. Let's do that here and 
first specify rules that deal exclusively with nullity of `Product`'s fields:
```kotlin
fun nullityRules(p: Product) =
  Validoctor.rulesFor(Product::class.java)
    .allTyped(Boolean::class.java, notNull())
    .allTyped(Float::class.java, notNull())
    .field("name", notNull<String>())
    .field("nutritionFacts", conditional({ p.skuId != null }, notNull<NutritionFacts>()))
    .field("nutritionFacts", conditional({ p.skuId == null }, isNull<NutritionFacts>()))
    .build()
```
What we did here is we required all `Booleans` (`glutenFree` and `vegan` fields) and all `Floats` (`weightG`, `volumeMl`)
to be not null. `allTyped` used here differs from `allAssignable` we have seen in `nutritionFactsRule` in that it will 
only match fields strictly of specified types, and not their subtypes.
Next, we specified that we need `name` field to not be null (`notNull` predefined rule needs a type 
parameter in Kotlin if there are no other rules passed that allow inferring the type of field). 
Last two `field` calls deal with `nutritionFacts` field, and differ from what we have seen so far in that the rules passed  
are wrapped in `conditional` accepting an additional `Predicate`. Those rules will only be applied if that predicate is 
fulfilled. This allows us to require the nutrition facts are present only if we also have the `skuId` of the product, 
and are null otherwise. This is also the reason why we defined this Rule in a function and not in a field - we read the 
`Product` argument to be able to dynamically determine `nutritionFacts` nullity rules.

Now, we also need to validate a bunch of other stuff on `Product` object. Let's look at the last rule we need:
```kotlin
fun validityRules(p: Product) =
  Validoctor.rulesFor(Product::class.java)
    .field("name", stringTrimmedNotEmpty(), stringMaxLength(40))
    .field("skuId", stringExactLength(10), stringAlphanumeric())
    .field("description", stringMaxLength(200))
    .allTyped(Float::class.java, numberPositive())
    .field("nutritionFacts", conditional({ p.nutritionFacts != null }, nutritionFactsRule))
    .elements("comments", commentRules)
    .elements("reviewScores", chained(notNull(), numberInRange(1, 5)))
    .build()
```
Top `field` and `allTyped` calls are nothing new at this point. New hot stuff is `nutritionFacts` rule definition. 
It specifies that `nutritionFacts` field will be validated with the Rule we created for `NutritionFacts` objects at the 
beginning of this example. You can nest Rules for fields of complex types like this, down to hierarchies of unlimited depth. 
In this case, validation of the `nutritionFactsRule` is also conditional, only applied if the `nutritionFacts` field is not null. 
Another important case here is `elements` method we used for `comments` and `reviewScores`. This method allows us to apply 
Rules to each element of the collection field instead of field itself. So we attached the Rule we defined above for `Comment` 
to each element of `comments` list. Similarly, we attached a Rule to each element in `reviewScores` collection, but this time, 
we also used a `chained` wrapper. It accepts any number of Rules that will be executed sequentially - if any of these Rules 
fails, none of the ones coming after it will be executed. `chained` is compatible with `conditional`, and you can mix and 
match any and all Rules in one call.

With that we have defined all the validation we need for given data structure. To perform the validation on an actual 
object, we just need one call:
```kotlin
val diagnosis = Validoctor.examine(product, nullityRules, validityRules)
```
And that's it. Diagnosis object returned by Validoctor contains the result and all the Ailments found in the object.

# Usage with Spring's @ExceptionHandler
Diagnosis objects are designed to be easily processable and readable by any client applications they are returned to. 
Using Validoctor with `setThrowing(true)` you can make it throw `DiagnosisException` on Rule violations. Then, if you are 
using Spring, you can intercept this exception and easily return `Diagnosis` as response body with whatever status code 
you desire. Example `ExceptionHandler` could be defined like this:

```kotlin
@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {

  @ExceptionHandler(DiagnosisException::class)
  fun handleDiagnosisException(ex: DiagnosisException): ResponseEntity<Any> {
    return ResponseEntity.unprocessableEntity().body(ex.diagnosis)
  }
}
```


# Dependencies
None.
