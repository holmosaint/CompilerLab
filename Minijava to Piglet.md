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

  > CALL classname_identifier (VTable, parameterList)



## 数组翻译

- 数组长度：`length + 1`
- 长度信息存储：存储在第一个元素的位置
- 获取长度：返回第一个元素的值；如果没有初始化，返回0



## 类的翻译

- `Table Size`：给类的`VTable`表的大小。
- `DTable`: 方法表。自顶向下存储父类、自己的方法，覆盖！！有同名方法直接用子类的函数进行替换。
- `VTable`：第一个变量是方法表的指针，剩下是自顶向下存储父类、自己的成员变量，不覆盖！！！
- 搜索成员全部**从下往上**找。



## 方法翻译

- 多于20个参数传入地址，规定`TEMP 20`存储所有变量的地址，可以看作开辟一个新的数组，中间存储各种变量的地址，基地址在`TEMP20`中。
- 规定第一个变量`TEMP 0`传入的是类的`VTable`指针。



