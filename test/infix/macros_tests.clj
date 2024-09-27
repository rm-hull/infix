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

(ns infix.macros-tests
  (:require
   [clojure.test :refer :all]
   [infix.macros :refer [infix $= from-string]]))

(def ε 0.0000001)

(deftest basic-arithmetic
  (is (= (+ 3 4) (infix 3 + 4)))
  (is (= 43 (infix 3 + 5 * 8)))
  (is (= 64 (infix (3 + 5) * 8)))
  (is (= 0 (infix (3 - 2) - 1)))
  (is (= 0 (infix 3 - 2 - 1)))
  (is (= 5 (infix 3 + 2 % 3)))
  (is (= 2 (infix (3 + 2) % 3)))
  (is (= 1 (infix 1 - 1 + 1)))
  (is (= 2 (infix 1 - 2 + 3))))

(deftest basic-arithmetic-$=
  (is (= (+ 3 4) ($= 3 + 4)))
  (is (= 43 ($= 3 + 5 * 8)))
  (is (= 64 ($= (3 + 5) * 8)))
  (is (= 0 ($= (3 - 2) - 1)))
  (is (= 0 ($= 3 - 2 - 1)))
  (is (= 5 ($= 3 + 2 % 3)))
  (is (= 2 ($= (3 + 2) % 3)))
  (is (= 1 ($= 1 - 1 + 1)))
  (is (= 2 ($= 1 - 2 + 3))))

(deftest check-aliasing
  (is (= 5.0 (infix √ (5 * 5))))
  (is (= 2 (infix 5 % 3)))
  (let [t 0.324]
    (is (> ε (Math/abs (- (infix sin (2 * t) + 3 * cos (4 * t)) 1.4176457261295824))))))

(deftest check-aliasing-$=
  (is (= 5.0 (infix √ (5 * 5))))
  (is (= 2 (infix 5 % 3)))
  (let [t 0.324]
    (is (> ε (Math/abs (- (infix sin (2 * t) + 3 * cos (4 * t)) 1.4176457261295824))))))

(deftest check-nested-aliasing
  (is (= 729.0 (infix abs (3 ** 6)))))

(deftest check-nullary-operators
  (let [f (fn [] (infix 3 + 7))
        g #(infix 7 + 21)]
    (is (= 14 (infix f () + 4)))
    (is (= 7  (infix g () / 4)))
    (is (true? (<= 0 (infix rand () * 3) 3)))))

(deftest check-unary-precedence
  (let [x 4 y 3]
    (is (= 2.0  (infix √ x)))
    (is (= 5.0  (infix √ x + y)))
    (is (= -1.0 (infix cos π)))))

(deftest check-binary-precedence
  (let [x 4 y 3]
    (is (= 12   (infix x . y)))
    (is (= 64.0 (infix x ** y)))
    (is (= 65536.0 (infix 2 ** 2 ** 2 ** 2)))))

(deftest check-from-string
  (is (= 5 ((from-string "5"))))
  (is (= 10 ((from-string "5 * 2"))))
  (is (= 7 ((from-string "5 + 2"))))
  (is (= 1 ((from-string "1 - 1 + 1"))))
  (is (= 2 ((from-string "1 - 2 + 3"))))
  (is (= 7 ((from-string [x] "x + 3") 4)))
  (is (= 7 ((from-string [_x] "_x + 3") 4)))
  (is (= 1 ((from-string [x] {:+ -} "x + 3") 4)))
  (is (= 7 ((from-string [] {:x 6 :+ +} "x + 1"))))
  (is (= 28.0 ((from-string [] "3 + 5**2"))))
  (is (= 75.0 ((from-string [] "3 * 5**2"))))
  (is (= 380175 ((from-string [t] "(t*(t>>5|t>>8))>>(t>>16)") 3425)))
  (is (= 380175 ((from-string [t] "( t * (  t  >> 5 | t >>  8 ) ) >> ( t >> 16  )") 3425)))
  (is (= 0 ((from-string "(3-2)-1"))))
  (is (= 0 ((from-string "3 - 2 - 1"))))
  (is (= 65536.0 ((from-string "2 ** 2 ** 2 ** 2"))))
  (is (= Double/POSITIVE_INFINITY ((from-string "divide(3, 0)"))))
  (is (= Double/NEGATIVE_INFINITY ((from-string "-3 ÷ 0"))))
  (is (= 5 ((from-string [t] "t - 2") 7)))
  (is (= 5 ((from-string [t] "t-2") 7)))
  (is (= 5 ((from-string [t] "t- 2") 7)))
  (is (= 5 ((from-string [t] "t+2") 3)))
  (is (= 5 ((from-string [f] "f() + 2") (fn [] 3))))
  (is (true? (<= 0 ((from-string "rand() * 10")) 10)))
  (is (true? (<= 0 ((from-string "randInt(10)")) 10)))
  (is (true? (<= 0 ((from-string [n] "randInt(n)") 5) 5)))
  (is (= 25.0 ((from-string "pow(5,2)"))))
  (is (= 0.1411200080598672 ((from-string "sin(3)"))))
  (is (= 0.1411200080598672 ((from-string "sin 3"))))
  (is (= 3.1415926535897932 ((from-string "pi"))))
  (is (= 2.718281828459045 ((from-string "e"))))
  (is (= 15 ((from-string [e] "e * 3") 5)))
  (is (= 19624.068163608234 ((from-string [e] "e ** (3 + pi)") 5)))
  (is (= 384.2880400203104 ((from-string [x] "product(e, pi, 3 * 3, x)") 5)))
  (is (= true ((from-string "16 = 16"))))
  (is (= true ((from-string "16 == 16"))))
  (is (= true ((from-string "16 != 17"))))
  (is (= 48 ((from-string "16 | 32"))))
  (is (= true ((from-string "false || true"))))
  (is (= false ((from-string "false || false"))))
  (is (= 16 ((from-string "16 & 48"))))
  (is (= false ((from-string "true && false"))))
  (is (= true ((from-string "true && true"))))
  (is (thrown-with-msg? java.text.ParseException #"Failed to parse text at line: 1, col: 3\nx \+ \n  \^"
                        ((from-string [x] "x + ") 3)))
  (is (thrown-with-msg? java.text.ParseException #"Failed to parse text at line: 1, col: 8\nx\+\(y\*7\)\)\n       \^"
                        ((from-string [x y] "x+(y*7))") 3 2)))
  (is (thrown-with-msg? clojure.lang.ArityException #"Wrong number of args \(2\) passed to: .*"
                        ((from-string [x] "x + 3") 2 3)))
  (is (thrown-with-msg? IllegalStateException #"x is not bound in environment"
                        ((from-string "x + 3")))))

(deftest check-math-namespace-aliases
  (is (= 1.813477718829676 (infix csc (32))))
  (is (= -1.5298856564663974 (infix sec (4)))))

(deftest check-alias-expansion
  (let [x 4 y 3]
    (is (= 0.389947492069644965 (infix exp (sin x + cos y) - sin (exp (x + y)))))))

(deftest check-equivalence
  (let [f (from-string [t] "t>>5 | t>>8")]
    (doseq [t (repeatedly 50 #(rand-int 1000000))]
      (is (= (f t) (infix (t >> 5 | t >> 8)))
          (str "Incorrect evaluation for t=" t)))))

(deftest check-division-precedence
  (let [x 0 y 1]
    (is (= 0.0 (infix sin (x ÷ y ** 2))))
    (is (= 2 (infix 4 / 4 * 2)))
    (is (= 2 (infix (4 / 4) * 2)))
    (is (= 1/2 (infix 4 / (4 * 2))))
    (is (= 0.5 (infix 4 / (4 * 2.0))))
    (is (= 8 (infix 4 * 4 / 2)))
    (is (= 8 (infix (4 * 4) / 2)))
    (is (= 8 (infix 4 * (4 / 2))))))

(deftest check-equality
  (is (true? (infix 5 = 5)))
  (is (false? (infix 5 = 5.0)))
  (is (true? (infix 5 == 5)))
  (is (true? (infix 5 == 5.0)))
  (is (true? (infix 5 != 3)))
  (is (true? (infix 5 not= 4))))

(deftest check-comparison
  (is (true? (infix 3 < 5)))
  (is (false? (infix 3 > 5)))
  (is (true? (infix 3 <= 5)))
  (is (false? (infix 3 >= 5)))
  (is (true? (infix 3 >= 3)))
  (is (true? (infix 3 <= 3))))

(deftest check-meta
  (let [hypot (from-string [x y] "sqrt(x**2 + y**2)")
        no-params (from-string "1 + 2")]
    (is (= {:params [:x :y] :doc "sqrt(x**2 + y**2)"} (meta hypot))
        (is (= {:params [] :doc "1 + 2"} (meta no-params))))))
