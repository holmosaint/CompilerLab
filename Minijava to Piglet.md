# Minijava to Piglet

## Piglet Grammar

- `[function name] [number of parameters]`
- `NOOP`
- `ERROR`
- `CJUMP Exp Label`
  - If `Exp` is 1, go the next statement; or go to label
- `LT Exp1 Exp2`
  - Return 1 if `Exp1 < Exp2` else 0
- `HALLOCATE Exp`
  - Allocate number(`Exp`) of bytes and return the base address
  - The integers and pointers both have a size of 4 bytes.
- `HSTORE Exp1 IntegerLiteral Exp2`
  - `Ex1` evaluates the base address, `IntegerLiteral` is the offset and `Exp2` is the value to be stored
- `HLOAD Temp Exp IntegerLiteral`
  - `Temp` is the teporary where the value should be loaded, `Exp` and `IntegerLiteral` refers to the address



## Transition

- Main function: `class Identifier {public static void main(String [] Identifier){...}}`

  > MAIN
  >
  > ​	...
  >
  > END

- Method declaration: `public Type Identifier (parameters) {... return ...;}`

  > classname_methodname [len(parameters)]
  >
  > BEGIN
  >
  > ​	......
  >
  > RETURN ...
  >
  > END

- Assignment: `Identifier = Expression`

  > MOVE TEMP Exp

- Array assignment: `Identifier[Expression1] = Expression2`

  > HSTORE Identifier_address expression1 expression2

- If statement: `if (expression) statement1 else statement2`

  > CJUMP expression LABEL
  >
  > ​	statement1
  >
  > LABEL	statement2

- While statement: `while (expression) statement`

  > L1 CJUMP expression L2
  >
  > ​	statement
  >
  > JUMP L1
  >
  > L2 NOOP

- Print statement: `System.out.println(expression)`

  > PRINT statement

- 各种算术expression（括号优先级？）

  > PLUS/TIMES/MINUS TEMP TEMP
  >
  > 优先级！括号！

- not expression `! expression`

  > MOVE TEMP expression
  >
  > CJUMP TEMP L2
  >
  > L1	MOVE TEMP '1'
  >
  > ​		JUMP L3
  >
  > L2	MOVE TEMP '0'
  >
  > L3	NOOP

- Array allocation expression: `new int [expression]`

  > HSTORE TEMP HALLOCATE TIMES expression 4

- Allocation expression: `new Identifier ()`

  > HSTORE TEMP HALLOCATE TIMES size 4

- Message send: `PrimaryExpression.Identifier(parameters)`

  > CALL classname_identifier (VTable, Dtable, parameterList)