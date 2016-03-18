# Infix [![Build Status](https://travis-ci.org/rm-hull/infix.svg?branch=master)](http://travis-ci.org/rm-hull/infix) [![Coverage Status](https://coveralls.io/repos/rm-hull/infix/badge.svg?branch=master)](https://coveralls.io/r/rm-hull/infix?branch=master) [![Dependencies Status](https://jarkeeper.com/rm-hull/infix/status.svg)](https://jarkeeper.com/rm-hull/infix) [![Downloads](https://jarkeeper.com/rm-hull/infix/downloads.svg)](https://jarkeeper.com/rm-hull/infix) [![Clojars Project](https://img.shields.io/clojars/v/rm-hull/infix.svg)](https://clojars.org/rm-hull/infix)


A small Clojure/ClojureScript library for representing LISP expressions in infix
rather than prefix notation... sometimes it's easier to rely on operator precedence,
instead of LISP's insistence on parentheses – this is especially true when dealing
with mathematical equations.

An infix expression is rewritten as a prefix expression using a macro. The operator
precedence rules were taken from [Wikipedia](https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages).
Any function calls should have parens around the arguments and not the function name.

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.5.3 or above installed.

### Building

To build and install the library locally, run:

    $ cd infix
    $ lein test
    $ lein install

### Including in your project

There is a version hosted at [Clojars](https://clojars.org/rm-hull/infix).
For leiningen include a dependency:

```clojure
[rm-hull/infix "0.1.1"]
```

For maven-based projects, add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>rm-hull</groupId>
  <artifactId>infix</artifactId>
  <version>0.1.1</version>
</dependency>
```

## Basic Usage

```clojure
(refer 'infix.macros :only '[infix])
=> nil

(infix 3 + 5 * 8)
=> 43

(infix (3 + 5) * 8)
=> 64
```

Some `Math` functions have been aliased (see [below](#aliased-operators--functions) for full list), so single argument functions can be
used as follows:

```clojure
(infix √(5 * 5))
=> 5.0

(infix √ 121)
=> 11.0

(infix 2 ** 6)
=> 64.0

(def t 0.324)
=> #'user/t

(infix sin(2 * t) + 3 * cos(4 * t))
=> 1.4176457261295824

(macroexpand-1 '(infix sin(2 * t) + 3 * cos(4 * t))
=> (+ (Math/sin (* 2 t)) (* 3 (Math/cos (* 4 t))))
```

### Aliased Operators & Functions

| Alias  | Operator        |   | Alias  | Operator        |   | Alias  | Operator        |
|--------|-----------------|---|--------|-----------------|---|--------|-----------------|
| &&     | and             |   | abs    | Math/abs        |   | sin    | Math/sin        |
| \|\|   | or              |   | signum | Math/signum     |   | cos    | Math/cos        |
| ==     | =               |   | **     | Math/pow        |   | tan    | Math/tan        |
| !=     | not=            |   | exp    | Math/exp        |   | asin   | Math/asin       |
| %      | mod             |   | log    | Math/log        |   | acos   | Math/acos       |
| <<     | bit-shift-left  |   | e      | Math/E          |   | atan   | Math/atan       |
| >>     | bit-shift-right |   | π      | Math/PI         |   | sinh   | Math/sinh       |
| !      | not             |   | sqrt   | Math/sqrt       |   | cosh   | Math/cosh       |
| &      | bit-and         |   | √      | Math/sqrt       |   | tanh   | Math/tanh       |
| \|     | bit-or          |   | .      | *               |   |        |                 |

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
