;; The MIT License (MIT)
;;
;; Copyright (c) 2016 Richard Hull
;;
;; Permission is hereby granted, free of charge, to any person obtaining a copy
;; of this software and associated documentation files (the "Software"), to deal
;; in the Software without restriction, including without limitation the rights
;; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;; copies of the Software, and to permit persons to whom the Software is
;; furnished to do so, subject to the following conditions:
;;
;; The above copyright notice and this permission notice shall be included in all
;; copies or substantial portions of the Software.
;;
;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
;; SOFTWARE.


(ns infix.core
  (:require [infix.math]))

(def operator-alias
  {'&&     'and
   '||     'or
   '==     '=
   '!=     'not=
   '%      'mod
   '<<     'bit-shift-left
   '>>     'bit-shift-right
   '!      'not
   '&      'bit-and
   '|      'bit-or
   '.      '*
   'abs    'Math/abs
   'signum 'Math/signum
   '**     'Math/pow
   'sin    'Math/sin
   'cos    'Math/cos
   'tan    'Math/tan
   'asin   'Math/asin
   'acos   'Math/acos
   'atan   'Math/atan
   'sinh   'Math/sinh
   'cosh   'Math/cosh
   'tanh   'Math/tanh
   'sec    'infix.math/sec
   'csc    'infix.math/csc
   'cot    'infix.math/cot
   'asec   'infix.math/asec
   'acsc   'infix.math/acsc
   'acot   'infix.math/acot
   'exp    'Math/exp
   'log    'Math/log
   'e      'Math/E
   'π      'Math/PI
   'φ      'infix.math/φ
   'sqrt   'Math/sqrt
   '√      'Math/sqrt
   'root   'infix.math/root
   'gcd    'infix.math/gcd
   'lcm    'infix.math/lcm
   'fact   'infix.math/fact
   'sum    'infix.math/sum
   '∑      'infix.math/sum
   'product 'infix.math/product
   '∏      'infix.math/product })

(def operator-precedence
  ; From https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages
  ; Lowest precedence first
  [
   ;; binary operators
   'or 'and 'bit-or 'bit-xor 'bit-and 'not= '= '>= '> '<= '<
   'bit-shift-right 'bit-shift-left '- '+ '/ '* 'Math/pow 'mod

   ;; unary operators
   'not
   'Math/sin  'Math/cos  'Math/tan
   'Math/asin 'Math/acos 'Math/atan
   'Math/sinh 'Math/cosh 'Math/tanh
   'Math/sqrt 'Math/exp  'Math/log
   'Math/abs  'Math/signum ])

(defn- resolve-alias [op]
  (get operator-alias op op))

(defn resolve-aliases
  "Attempt to resolve any aliases: if not found just return the original term"
  [expr]
  (map resolve-alias expr))

(defn rewrite
  "Recursively rewrites the infix-expr as a prefix expression, according to
   the operator precedence rules"
  [infix-expr]
  (cond
    (not (seq? infix-expr))
    (resolve-alias infix-expr)

    (and (seq? (first infix-expr)) (= (count infix-expr) 1))
    (rewrite (first infix-expr))

    (empty? (rest infix-expr))
    (first infix-expr)

    :else
    (loop [ops operator-precedence]
      (if-let [op (first ops)]
        (let [infix-expr (resolve-aliases infix-expr)
              idx        (.lastIndexOf ^java.util.List infix-expr op)]
          (if (pos? idx)
            (let [[expr1 [op & expr2]] (split-at idx infix-expr)]
              (list op (rewrite expr1) (rewrite expr2)))
            (recur (next ops))))
        (list
          (rewrite (first infix-expr))
          (rewrite (rest infix-expr)))))))

(def base-env
  (merge
    ; wrapped java.lang.Math constants & functions
    (->>
      (ns-publics 'infix.math)
      (map (fn [[k v]] (vector (keyword k) v)))
      (into {}))

    ; Basic ops
    {
      :+ +
      :- -
      :* *
      :/ /
      :% mod
    }))
