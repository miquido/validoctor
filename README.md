Validoctor is an all-purpose data validator for backend java projects. It performs validation basing on rules passed along with object to be validated. It can operate depending on several traits and on various kinds of rules in an effort to cater to specific needs of all projects.

####Vocabulary
* Ailment - like a single violation of a Rule. It carries a meaningful name and may have additional parameters.
* Diagnosis - validation report stating whether object is valid or not and containing all Ailments discovered in it.

####Traits
Validoctor instance can be created using a builder:
```java
Validoctor validoctor = Validoctor.builder().pedantic(true).exceptional(false).build();
```

For now, there are two traits.
* Pedantic Validoctor will go through all rules to the end and return a complete Diagnosis with all violations. One who is not pedantic will stop on first violation encountered and return this one only.
* Exceptional Validoctor will throw an exception containing a Diagnosis instead of returning it, if it finds any violations.

Pedantic and non-exceptional Validoctor is the default obtained by calling build() without specifying any traits.

####Rules
Each rule provides two things: definition of how validity is tested and Ailment that should be stated upon violation of the rule.
There are 4 types of rules, calling validation with each type is the same:
```java
Diagnosis diagnosis = validoctor.examine(patient, RULE_1, RULE_2);
```
examine() method accepts any number of rules of given type.

Types are:

* Just Rule - simple interface used to define validations of one value
* MultiRule - rule sets that allow defining validations for whole data structures on per property basis
* ComlexRule - define validations of multiple properties at once
* ExceptionalRule - rule that will cause exception to be thrown upon violation, even if Validoctor is not exceptional

Rules can be Exceptional and one of other types at once.