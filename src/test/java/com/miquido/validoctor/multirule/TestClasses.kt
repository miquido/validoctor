package com.miquido.validoctor.multirule

data class Tier4(var something: String, var number: Int)
data class Tier3(var tier4: Tier4, var ahoo: String)
data class Tier2(var tier3: Tier3, var count: Int)
data class Tier1(var tier2: Tier2, var bleh: Boolean?)

open class Base1(open var something: String)
data class Inherited1(override var something: String) : Base1(something)
data class Inherited2(override var something: String) : Base1(something)
data class InheritanceTestPatient(var inherited1: Inherited1,
                                  var inherited2: Inherited2,
                                  var base1: Base1)