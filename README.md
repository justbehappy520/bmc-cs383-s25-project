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
