# Infix
[![Build Status](https://travis-ci.org/rm-hull/infix.svg?branch=master)](http://travis-ci.org/rm-hull/infix)
[![Coverage Status](https://coveralls.io/repos/rm-hull/infix/badge.svg?branch=master)](https://coveralls.io/r/rm-hull/infix?branch=master)
[![Dependencies Status](https://jarkeeper.com/rm-hull/infix/status.svg)](https://jarkeeper.com/rm-hull/infix)
[![Downloads](https://jarkeeper.com/rm-hull/infix/downloads.svg)](https://jarkeeper.com/rm-hull/infix)
[![Clojars Project](https://img.shields.io/clojars/v/rm-hull/infix.svg)](https://clojars.org/rm-hull/infix)
[![Maintenance](https://img.shields.io/maintenance/yes/2017.svg?maxAge=2592000)]()

A small Clojure library for representing LISP expressions in infix
rather than prefix notation... sometimes it's easier to rely on operator precedence,
instead of LISP's insistence on parentheses – this is especially true when dealing
with mathematical equations.

An infix expression is rewritten as a prefix expression using a macro. The operator
precedence rules were taken from [Wikipedia](https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages).
Any function calls should _generally_ have parens around the arguments and not the function name. Aliased unary operators
(as outlined below) do not need parens however.

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.6.1 or above installed.

### Building

To build and install the library locally, run:

    $ cd infix
    $ lein test
    $ lein install

### Including in your project

There is a version hosted at [Clojars](https://clojars.org/rm-hull/infix).
For leiningen include a dependency:

```clojure
[rm-hull/infix "0.3.0"]
```

For maven-based projects, add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>rm-hull</groupId>
  <artifactId>infix</artifactId>
  <version>0.2.12</version>
</dependency>
```

## API Documentation

See [www.destructuring-bind.org/infix](http://www.destructuring-bind.org/infix/) for API details.

## Basic Usage

```clojure
(refer 'infix.macros :only '[infix from-string base-env])
; => nil

(infix 3 + 5 * 8)
; => 43

(infix (3 + 5) * 8)
; => 64
```

You can also use `$=` as a short alias for `infix`, like this for example:
```clojure
(refer 'infix.macros :only '[$=])
; => nil

($= 3 + 5 * 8)
; => 43
```

All of the examples below should work if you replace `infix` by `$=`.

Some `Math` functions have been aliased (see [below](#aliased-operators--functions)
for full list), so nullary and unary-argument functions can be used as follows:

```clojure
(infix √(5 * 5))
; => 5.0

(infix √ 121)
; => 11.0

(infix 2 ** 6)
; => 64.0

(def t 0.324)
; => #'user/t

(infix sin(2 * t) + 3 * cos(4 * t))
; => 1.4176457261295824

(infix rand() * 3)
; => 0.5544039436207262
```

#### Debugging

It may be the case that you encounter some esoteric errors emitted from the
library trying to rephrase expressions from infix to prefix. Use
`macroexpand-1` to show how the expression would be rewritten, and if necessary
file an [issue](https://github.com/rm-hull/infix/issues/new).

```clojure
(macroexpand-1 '(infix sin(2 * t) + 3 * cos(4 * t))
; => (+ (Math/sin (* 2 t)) (* 3 (Math/cos (* 4 t))))
```

### Usage in ClojureScript projects

The `infix` macro may be used to expand infix expressions in ClojureScript code
by adding the require-macros directive to a namespace, for example:

```clojure
(ns my.clojurescript.project
  (:require-macros [infix.macros :refer [infix]]))
```

### Evaluating infix expressions dynamically from a string

A function can created at runtime from an expression held in a string as
follows. When building from a string, a number of binding arguments should be
supplied, corresponding to any variable that may be used in the string
expression, for example:

```clojure
(def hypot
  (from-string [x y]
    "sqrt(x**2 + y**2)"))
; => #'user/hypot

(hypot 3 4)
; => 5
```

`from-string` is deliberately designed to _look_ like an anonymous function
definition, mainly because that is more-or-less what it is. In effect, this is
equivalent to creating the  following function:

```clojure
(def hypot
  (fn [x y]
    (infix sqrt(x ** 2 + y ** 2))))
```

However, it does so without recourse to `eval` and `read-string` - instead it is built using our
[old](https://github.com/rm-hull/ods-search-appliance/blob/master/src/odessa/parser.clj)
[friend](https://github.com/rm-hull/wam/blob/master/src/wam/parser.clj), the
monadic parser-combinator, with an EBNF grammar (implementing the infix notation)
and a restricted base environment of math functions, as outlined in the next
section.

The `base-env` may be extended with any number of key/value pairs (where keys
are keywords) and values may either be values or functions, to provide
the required extensions. When referenced in the string it is **not** necessary
to prefix the name with a colon.

```clojure
(def extended-env
  (merge
    base-env
    {:rad (fn [deg] (infix deg * π / 180))
     :hypot hypot}))
; => user/extended-env

(def rhs-triangle-height
  (from-string [base angle]
    extended-env
    "tan(rad(angle)) * base"))
; => user/rhs-triangle-height

(rhs-triangle-height 10 45)
; => 9.9999999999998
```

Obviously, a function that was previously created from a string can also
referenced in a subsequent function definition:

```clojure
(def hypot2
  (from-string [x y]
    extended-env
    "hypot(x, y) ** 2"))
; => user/hypot2

(hypot2 5 12)
; => 169.0
```

### Aliased Operators & Functions

| Alias   | Operator                |   | Alias  | Operator        |   | Alias  | Operator        |
|---------|-------------------------|---|--------|-----------------|---|--------|-----------------|
| &&      | and                     |   | abs    | Math/abs        |   | sin    | Math/sin        |
| \|\|    | or                      |   | signum | Math/signum     |   | cos    | Math/cos        |
| ==      | =                       |   | **     | Math/pow        |   | tan    | Math/tan        |
| !=      | not=                    |   | exp    | Math/exp        |   | asin   | Math/asin       |
| %       | mod                     |   | log    | Math/log        |   | acos   | Math/acos       |
| <<      | bit-shift-left          |   | e      | Math/E          |   | atan   | Math/atan       |
| >>      | bit-shift-right         |   | π      | Math/PI         |   | sinh   | Math/sinh       |
| !       | not                     |   | sqrt   | Math/sqrt       |   | cosh   | Math/cosh       |
| &       | bit-and                 |   | √      | Math/sqrt       |   | tanh   | Math/tanh       |
| \|      | bit-or                  |   | root   | b √ a           |   | sec    | Secant          |
|         |                         |   | φ      | Golden ratio    |   | csc    | Cosecant        |
| gcd     | Greatest common divsor  |   | fact   | Factorial       |   | cot    | Cotangent       |
| lcm     | Least common multiple   |   | ∑      | Sum             |   | asec   | Arcsecant       |
| rand    | Random number generator |   | ∏      | Product         |   | acsc   | Arccosecant     |
| randInt | Random int between 0..n |   |        |                 |   | acot   | Arccotangent    |

## EBNF Grammar Rules

The `from-string` macro parses infix expressions based on the EBNF
[grammar rules](https://github.com/rm-hull/infix/blob/master/src/infix/grammar.clj)
as follows:

* _**&lt;expression&gt;** ::= term { addop term }._

* _**&lt;term&gt;** ::= factor { mulop factor }._

* _**&lt;factor&gt;** ::= "(" expression ")" | var | number | function._

* _**&lt;addop&gt;** ::= "+" | "-" | "|" | "&"._

* _**&lt;mulop&gt;** ::= "\*" | "/" | "÷" | "\*\*" | "%" | ">>" | ">>>" | "<<"._

* _**&lt;function&gt;** ::= envref expression | envref "(" &lt;empty&gt; | expression { "," expression } ")"._

* _**&lt;envref&gt;** ::= letter { letter | digit | "_" }._

* _**&lt;var&gt;** ::= envref._

* _**&lt;number&gt;** ::= integer | decimal | rational | binary | hex_

* _**&lt;binary&gt;** :: = [ "-" ] "0b" { "0" | "1" }._

* _**&lt;hex&gt;** :: = [ "-" ] "0x" | "#" { "0" | ... | "9" | "A" | ... | "F" | "a" | ... | "f" }._

* _**&lt;integer&gt;** :: = [ "-" ] digits._

* _**&lt;decimal&gt;** :: = [ "-" ] digits "." digits._

* _**&lt;rational&gt;** :: = integer "/" digits._

* _**&lt;letter&gt;** ::= "A" | "B" | ... | "Z" | "a" | "b" | ... | "z"._

* _**&lt;digit&gt;** ::= "0" | "1" | ... | "8" | "9"._

* _**&lt;digits&gt;** ::= digit { digit }._

## References

* https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages

## License

The MIT License (MIT)

Copyright (c) 2016 Richard Hull

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
