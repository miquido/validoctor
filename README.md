Validoctor is an all-purpose data validator for backend java projects. It performs validation basing on rules passed along with object to be validated. It can operate depending on several traits and on various kinds of rules in an effort to cater to specific needs of all projects.

# Getting started
To add validoctor to your project:
```groovy
repositories {
    jcenter()
}
dependencies {
  compile 'com.miquido.validoctor:validoctor:1.0.0'
}
```

# Vocabulary
* Ailment - like a single violation of a Rule. It carries a meaningful name and may have additional parameters.
* Diagnosis - validation report stating whether object is valid or not and containing all Ailments discovered in it.

# Traits
Validoctor instance can be created using a builder:

```java
Validoctor validoctor = Validoctor.builder().pedantic(true).exceptional(false).build();
```

For now, there are two traits.

* Pedantic Validoctor will go through all rules to the end and return a complete Diagnosis with all violations. One who is not pedantic will stop on first violation encountered and return this one only.
* Exceptional Validoctor will throw an exception containing a Diagnosis instead of returning it, if it finds any violations.

Pedantic and non-exceptional Validoctor is the default obtained by calling build() without specifying any traits.

# Rules
Each rule provides two things: definition of how validity is tested and Ailment that should be stated upon violation of the rule.
There are 3 types of rules, calling validation with each type is the same:

```java
Diagnosis diagnosis = validoctor.examine(patient, RULE_1, RULE_2);
```

examine() method accepts any number of rules of given type. There also are examineCombo methods that allow passing some permutations of rules of several kinds:

```java
Diagnosis diagnosis = validoctor.examineCombo(patient, Rules.notNull(), multiRule1, multiRule2);
```

Types are:

* Just Rule - simple interface used to define validations of one value
* MultiRule - rule sets that allow defining validations for whole data structures on per property basis
* ReducerRules - rules that are applied to a set of properties of the same type that is reduced to one value


# Dependencies
None.

# In next release
* Add missing toString and equals/hashCode methods - DONE.
* Return rule parameters and actual patient value in Ailment for easier result handling by clients - DONE.
* Add trait for multitheaded validation.
* Write quick start guide.
* Create develop branch.
* Building snapshots to bintray from master.
