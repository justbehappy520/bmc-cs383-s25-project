# Final project
Automated Bug Finding: Enhancing Test Generation for Real-World Java Bugs

## How to Start
Make new pool classes

Classes to make: DatePool, UnicodePool, HexPool, NegPool, UnderscorePool

DatePool: YYYY-MM-DD, mutation possibility -- insert/add non-number char (bug-triggering: non-number char)

UnicodePool: (bug-triggering: String of two Unicodes)

HexPool: eight-digit hex numbers, hex numbers w/ leading zeroes, hex numbers w/ leading chars "0x" "-0x" "0X" "-0X" (bug-triggering: leading zeroes, eight/sixteen digit hex w/ leading digit > 7, leading chars "0X" and "-0X")

NegPool: number as a String, neg/pos (bug-triggering: leading char is "-")

UnderscorePool: anything goes (bug-triggering: leading char is "_")

Need to calculate the probability in the method initDefaultProbabilities() in ConstantPoolManager

Need to make NAMEConstantVariableProbabilityPool for each new pool

Consider checking out/adding tests to TestVariableConstantPool in client/src/test/java/org/evosuite/seeding

client/src/main/java/org/evosuite/testcase/TestFactory.java is where it all starts.. in the insertRandomStatement method.
you can follow this to one of the lower level operations like the method createVariable. In attemptGeneration you will find methods to generate random variables. You are probably interested in the createPrimitive method. This calls:
client/src/main/java/org/evosuite/testcase/statements/PrimitiveStatement.java where the getPrimitivestatement method creates primtives of the specified type. Look at the   randomize method in each one you want to modify. For example, client/src/main/java/org/evosuite/testcase/statements/numeric/IntPrimitiveStatement. The randomize method either (a) generates a totally random int or (b) selects from the constant pool. 
