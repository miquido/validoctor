Validoctor is an all-purpose data validator for backend java and kotlin projects. It performs validation basing on rules 
passed along with object to be validated. It can operate depending on several traits and on various kinds of rules in an 
effort to cater to specific needs of all projects. Specially designed for simple validation of complex structures.

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
    jcenter()
}
dependencies {
  compile 'com.miquido.validoctor:validoctor:1.1.7'
}
```
Validoctor is a data validation library handling validations on complex data structures. It is designed to be 
used as a standalone complex validation solution, and not for cooperation with Bean Validation or any other 
validation solution.

Basic concepts and a production-grade example are shown below. For more specific use cases, please refer to tests. 
When in doubt about how something should be used, answer is usually easily found there.

# Vocabulary
* Ailment - like a single violation of a Rule. It carries a meaningful name and may have additional parameters.
* Diagnosis - validation report stating whether object is valid or not and containing all Ailments discovered in it. 
It is structured to be useful for any client code that may want to read it, so it may be used directly as, for example, 
a response body.

# Traits
Validoctor instance can be created using a builder:

```java
Validoctor validoctor = Validoctor.builder().pedantic(true).exceptional(false).build();
```

For now, there are two traits.

* Pedantic Validoctor will go through all rules to the end and return a complete Diagnosis with all violations. 
One who is not pedantic will stop on first violation encountered and return this one only.
* Exceptional Validoctor will throw an exception containing a Diagnosis instead of returning it, if it finds any violations.

Pedantic and non-exceptional Validoctor is the default obtained by calling build() without specifying any traits.

# Rules
Each rule provides two things: definition of how validity is tested and Ailment that should be stated upon violation of the rule.
There are 3 types of rules, calling validation with each type is the same:

```java
Diagnosis diagnosis = validoctor.examine(patient, RULE_1, RULE_2);
```

examine() method accepts any number of rules of given type. There also are examineCombo methods that allow passing some 
permutations of rules of several kinds:

```java
Diagnosis diagnosis = validoctor.examineCombo(patient, Rules.notNull(), multiRule1, multiRule2);
```

Types are:

* Just Rule - simple interface used to define validations of one value
* MultiRule - rule sets that allow defining validations for whole data structures on per property basis
* ReducerRules - rules that are applied to a set of properties of the same type that is reduced to one value

# Quick start examples
Create instance of Validoctor. Unless you need different reporting of errors for different cases 
(exceptions or just returned Diagnoses, all violations, or just the first one), you will only need one instance of Validoctor.
```java
Validoctor validoctor = Validoctor.builder().build();
```
For validating primitive or String values, just use SimpleRules. Most commonly used ones are supplied in Rules class.
```java
Diagnosis diagnosis = validoctor.examine(stringToValidate, Rules.notNull(), Rules.stringAlphabetic(), Rules.stringTrimmedNotEmpty());
```
This will check if stringToValidate is not null, not empty or whitespace only and only containing letters.

Validoctor's main strength lies in validating complex objects in one call using MultiRules or ReducerRules. Those need to 
be created with builders. For example this validates fields foo and bar of object Example with specified rules:
```java
MultiRule<Example> exampleMultiRule = MultiRule.<Example>builder()
        .reflexiveProperties(Example.class)
        .addRules("foo", notNull(), numberPositive())
        .addRules("bar", stringMinLength(3), stringMaxLength(20))
        .build();
```
It is then passed for Validoctor's examination the same way as SimpleRules:
```java
Diagnosis diagnosis = validoctor.examine(exampleObject, exampleMultiRule);
```
It can also accept several MultiRules just as it could accept several SimpleRules.

ReducerRules are useful when you need to validate some properties of an object as a group. For example when you have 
a firstName and a lastName, and want their concatenation to be no longer than 40 characters you can do:
```java
ReducerRule<TestPatient, String> reducerRule = ReducerRule.builder(TestPatient.class, String.class)
      .properties("firstName", "lastName")
      .reducer(String::concat)
      .rule(stringMaxLength(40))
      .nullIgnoring()
      .build();
```
This will get listed properties of specified type and reduce them to one value with given function, then validate it 
with passed rules. nullIgnoring() lifts responsibility of handling nulls form reducer function. Validation is called 
exactly the same as with MultiRules and SimpleRules. 

All Rules are stateless and can be reused for multiple validations.

# Production grade example
This is a description of slightly simplified ComplexCase1Test class as it shows and tests validation of 
production-grade complexity. This class exists in tests code in both Kotlin and Java version. Code snippets below are 
from Kotlin one. For Java counterparts, look at ComplexCase1JavaTest class.

Imagine you need to validate an instance of a Product defined like this:
```kotlin
//Those are Kotlin data classes, for pure Java people out there it is the same as annotating a class with Lombok's @Data
data class NutritionFacts(var kcal: Int?, 
                          var fibre: Double?, 
                          var protein: Double?, 
                          var fat: Double?)

data class Comment(var authorId: Long?, 
                   var text: String?, 
                   var upVotedUsersIds: List<Long>?, 
                   var downVotedUsersIds: List<Long>?)

data class Product(var name: String?, 
                   var skuId: String?, 
                   var description: String?, 
                   var weightG: Float?, 
                   var volumeMl: Float?,
                   var nutritionFacts: NutritionFacts?, 
                   var glutenFree: Boolean?, 
                   var vegan: Boolean?,
                   var comments: List<Comment>?, 
                   var reviewScores: MutableList<Int>?)
```

Let's start with thinking about what we want to validate. Thinking on a per-class basis is the recommended approach 
for using Validoctor. So, starting with NutritionFacts class, we surely want all of the values to be positive. 
We write a simple MultiRule for that:
```kotlin
val nutritionFactsRules: MultiRule<NutritionFacts> =
        MultiRule.builder<NutritionFacts>().reflexiveProperties(NutritionFacts::class.java)
            .addRulesForAll(Number::class.java, numberPositive())
            .build()
```
What we did here? MultiRule is a list of rules that are applied to specified fields of the validated object. 
By using reflexiveProperties method on MultiRuleBuilder we allowed the resulting MultiRule to use reflection to find the 
values of the fields to apply the rules to. Then, we used addRulesForAll to tell the rule that we want it to read all 
fields of type Number (or its subtypes) and apply the numberPositive rule to each of them. It is a predefined rule 
available in Rules class that just checks if number is larger than 0.

Ok, so that is what we want to validate in NutritionFacts. Let's move on to Comment class. We certainly need a valid, 
non-null authorId and we want the text of the comment to be not empty, not longer that certain characters count and not 
contain any inappropriate words. For that, we will first need to define our own custom censor rule like this:
```kotlin
val stringCensorRule: SimpleRule<String> =
        SimpleRule("CENSORED_WORD", Predicate { str -> str == null || !str.contains("fuck", true) }, Severity.WARN)
```
In case when there is no appropriate predefined rule available in Rules class, we can create our own rules as shown above. 
Using SimpleRule constructor should cover 99.99% cases, so if you find yourself wanting to do something more complicated 
it might be a sign that you are trying to over-engineer. What we did here is we specified a name of our custom rule, 
passed a predicate that will be applied to patients to determine whether they are valid or not, and specified severity 
of violation of this rule. We decided on just WARN and not ERROR as we assume Comments reported to violate this rule 
will need to be reviewed by moderators and not just plain rejected.

Now, we are ready to create a MultiRule for Comment object that will specify all validations we need:
```kotlin
val commentRules: MultiRule<Comment> = 
        MultiRule.builder<Comment>().reflexiveProperties(Comment::class.java)
            .addRules("authorId", notNull(), numberPositive())
            .addRules("text", notNull(), stringTrimmedNotEmpty(), stringMaxLength(500), stringCensorRule)
            .build()
```
We used MultiRuleBuilder just like for NutritionFacts. This time though, we specify fields we want the rules to be 
applied to one by one, by their name. If we did not use reflexiveProperties, we would need to also specify the getters 
for these fields. Each addRules call will make resulting MultiRule apply all the specified rules to given field. So here, 
authorId will be checked if it is not null and then if it is a positive number, and text will be checked for nullity, 
not emptiness, max allowed length and inappropriate words with our custom censor rule.

And now, for the biggest task: we need to apply a series of various validations to fields of Product class. For better 
readability, we can decide to split validations of such complex classes into a few MultiRules. Let's do that here and 
first specify rules that deal exclusively with nullity of Product's fields:
```kotlin
val nullityRules: MultiRule<Product> = 
        MultiRule.builder<Product>().reflexiveProperties(Product::class.java)
            .addRulesForAll(Boolean::class.java, notNull())
            .addRulesForAll(Float::class.java, notNull())
            .addRules("name", notNull<String>())
            .addRules(Predicate { p -> p.skuId != null }, "nutritionFacts", notNull<NutritionFacts>())
            .addRules(Predicate { p -> p.skuId == null }, "nutritionFacts", isNull<NutritionFacts>())
            .build()
```
What we did here is we required all Booleans (glutenFree and vegan fields) and all Floats (weightG, volumeMl) to be not 
null. Then, we also specified that we need name field to not be null (notNull predefined rule needs a type parameter 
in Kotlin if there are no other rules passed that allow inferring the type of field). Last two calls to addRules deal 
with nutritionFacts fields, and differ from what we have seen so far in that they accept an additional Predicate as 
first argument. Those are conditional rules, that will only be applied if that predicate is fulfilled. This allows us 
to require the nutrition facts are present only if we also have the skuId of the product, and are null otherwise.

Now, we also need to validate a bunch of other stuff on Product object. Let's look at the last MultiRule we need:
```kotlin
val validityRules: MultiRule<Product> = 
        MultiRule.builder<Product>().reflexiveProperties(Product::class.java)
            .addRules("name", stringTrimmedNotEmpty(), stringMaxLength(40))
            .addRules("skuId", stringExactLength(10), stringAlphanumeric())
            .addRules("description", stringMaxLength(200))
            .addRulesForAll(Float::class.java, numberPositive())
            .addMultiRule({ p -> p.nutritionFacts != null }, "nutritionFacts", nutritionFactsRules)
            .addRules("tags", collectionNotEmpty())
            .addMultiRuleForElements("comments", commentRules)
            .addRules("reviewScores", each(numberInRange(1, 5)))
            .build()
```
AddRules and addRulesForAll usages here are nothing new at this point, they just use some more predefined rules that can 
be found in Rules class. New hot stuff is addMultiRule method. It specifies that nutritionFacts field will be validated 
with the MultiRule we created for NutritionFacts objects at the beginning of this example. You can nest MultiRules for 
fields of complex types like this, down to hierarchies of unlimited depth. In this case, validation of the 
nutritionFactsRule is also conditional, only applied if the nutritionFacts field is not null. Another important case here 
is addMultiRuleForElements we used for list of Comment objects in Product. This method allows us to apply the MultiRule 
we defined above for Comment to each element of the list. The final thing to note is the last addRules call that has a 
Rule built using each() passed. Rules.each() allows us to achieve validation for individual elements of collection same 
as addMultiRuleForElements, it is just easier to use when we do not need a MultiRule for that, and just SimpleRules are 
enough - typically when collection elements are of primitive or String type.

With that we have defined all the validation we need for given data structure. To perform the validation on an actual 
object, we just need one call:
```kotlin
val diagnosis = validoctor.examine(product, nullityRules, validityRules)
```
And that's it. Diagnosis object returned by our validoctor instance contains the result and all the Ailments found in 
the object.

# Usage with Spring's @ExceptionHandler
Diagnosis objects are designed to be easily processable and readable by any client applications they are returned to. 
Using Validoctor with exceptional trait you can make it throw DiagnosisException on Rule violations. Then, if you are 
using Spring, you can intercept this exception and easily return Diagnosis as response body with whatever status code 
you desire. Example ExceptionHandler could be defined like this:

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

# In next releases
* Add convenient overloads to all rule creating methods in Rules for creating named Rules - shortcuts for using Rules.named().
* Make Ailments found in elements of collection be mapped under jsonpath-compliant keys.
* Add trait for multithreaded validation.
* Building snapshots to bintray from master.
* Convert to Kotlin.
