---
    title: CompileLab
    notebook: MiniJava
---

# Structure of CompileLab

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

```mermaid
graph LR
MExpr-->expr_content:
    subgraph Content
        expr_content:
        which
        op
        prim_exprl
        prim_exprr
        expr_list
        var
    end
MPrimExpr-->prim_content:
    subgraph Content
        prim_content:
        which
        literal
        var
        expr
    end
```