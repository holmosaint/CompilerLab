---
    title: CompileLab
    notebook: MiniJava
---

# Structure of CompileLab

## What errors should we check?
- Using of undefined:
  - class
  - method
  - variable
- Duplicate definition of:
  - class
  - method
  - variable
- Type mismatch:
  - Expression in "if" or "while" should be boolean expression
  - Parameter in "println" should be an integer
  - The index of an array should be an integer
  - The left and right operands of an assignment must match the type
- Parameters mismatch:
  - Type, number, return type
  - Reloading is not allowed
- Operands of "+", "/" and "*" should be integers
- Circulation inheritance or multiple inheritance of class

## The phase of our work
### Phase 1
The program iterates through the syntax tree top-down, and constructs a symbol table.

## About MBlock and MMethod
> MScope
>> MBlock <br>
>> MMethod

## But, what's MBlock?
### It can be
- block
- assignment-statement
- arrayassignment-statement
- if-statement
- while-statement
- print-statement

## Structure of MExpr
- MExpr
  - expr_content:
  - which'
  - op
  - prim_exprl
  - prim_exprr
  - expr_list
  - var
- MPrimExpr
  - prim_content:
  - which
  - literal
  - var
  - expr

## Working flow
```flow
main=>start: main
Typecheck=>condition: Typecheck
output=>operation: Error
Piglet=>subroutine: Piglet
end=>end

main->Typecheck
Typecheck(yes, down)->Piglet
Typecheck(no, right)->output

```

## Back modelling
- method
  - Depends on the instance type
- property
  - Depends on the variable type

## What should be added to MVar/MMethod/MClass
```java
// MVar
MType type_;
String name_;
boolean allocated_ = false;
int addr_;      // base address
int length_;    // for array

MClass real_type_;  // for class instance
// 'method_tabel_' will be updated everytime the variable is assignmented,
// it stores the method accessible for this variable. Each pair 
// <method_name, class_name> in 'method_table' indicates a unique method
// which has name <method_name>_<class_name> in the object code 
HashMap<String, String> method_table_;  // <method_name : class_name>
HashMap<String, Integer> var_index_;    // <class_name : index>
// var_list_ is a concatenation of variable lists in ['real_type_', 
// 'real_type_' 's father, ... , 'type_', 'type_' 's father ...]
ArrayList<MVar> var_list_;

// relation between the 'var_index_' and 'var_list_'
//  class1         class2
//   ||             ||
//   \/             \/
//  var0 var1 var2 var3 var4

// When method visits one variable, it will inform 'method_table_' for its 
// current class name first, then uses it to inform var_index_ for index in 
// the var_list_. So the method can know that currently it access variabels 
// are in var_list_[index, :]
```
