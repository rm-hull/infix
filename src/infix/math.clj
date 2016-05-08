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

(ns infix.math)

(defmacro ^:private defunary [func-name & [alias]]
  (let [arg (gensym "x__")]
    `(defn ~(or alias func-name) [^double ~arg]
       (~(symbol (str "Math/" func-name)) ~arg))))

(defmacro ^:private defbinary [func-name & [alias]]
  (let [arg1 (gensym "x__")
        arg2 (gensym "y__")]
    `(defn ~(or alias func-name) [^double ~arg1 ^double ~arg2]
       (~(symbol (str "Math/" func-name)) ~arg1 ~arg2))))

(defunary abs)
(defunary signum)
(defunary sqrt)
(defunary sqrt √)
(defunary exp)
(defunary log)

(defunary sin)
(defunary cos)
(defunary tan)

(defunary asin)
(defunary acos)
(defunary atan)
(defbinary atan2)

(defunary sinh)
(defunary cosh)
(defunary tanh)

(defbinary pow)
(defbinary pow **)

(def φ (/ (inc (√ 5)) 2))
(def e Math/E)
(def π Math/PI)
(def pi Math/PI)

(def product *)
(def sum +)

(defn divide [a b]
  (if (zero? b)
    (if (neg? a)
      Double/NEGATIVE_INFINITY
      Double/POSITIVE_INFINITY)
    (/ a b)))

(def ÷ divide)

(defn root [a b]
  (pow b (/ 1 a)))

(defn gcd [a b]
  (if (zero? b)
    a
    (recur b (rem a b))))

(defn lcm [a b]
  (/ (* a b) (gcd a b)))

(defn fact [n]
  (if (zero? n)
    1
    (apply * (range 1 (inc n)))))

;; Additional trig functions not found in JDK java.lang.Math
(defn sec
  "compute secant, given the angle in radians"
  [θ]
  (/ 1.0 (Math/cos θ)))

(defn csc
  "compute cosecant, given the angle in radians"
  [θ]
  (/ 1.0 (Math/sin θ)))

(defn cot
  "compute cotangent, given the angle in radians"
  [θ]
  (/ 1.0 (Math/tan θ)))

(defn asec
  "compute arcsecant, given the number, returns the arcsecant in radians"
  [value]
  (Math/acos (/ 1.0 value)))

(defn acsc
  "compute arccosecant, given the number, returns the arccosecant in radians"
  [value]
  (Math/asin (/ 1.0 value)))

(defn acot
  "compute arccotangent, given the number, returns the arccotangent in radians"
  [value]
  (Math/atan (/ 1.0 value)))

