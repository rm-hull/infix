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
    [infix.macros :refer [infix from-string]]))

(def ε 0.0000001)

(deftest basic-arithmetic
  (is (= (+ 3 4) (infix 3 + 4)))
  (is (= 43 (infix 3 + 5 * 8)))
  (is (= 64 (infix (3 + 5) * 8))))

(deftest check-aliasing
  (is (= 5.0 (infix √(5 * 5))))
  (let [t 0.324]
    (is (> ε (Math/abs (- (infix sin(2 * t) + 3 * cos(4 * t)) 1.4176457261295824))))))

(deftest check-nested-aliasing
  (is (= 729.0 (infix abs(3 ** 6)))))

(deftest check-unary-precedence
  (let [x 4
        y 3 ]
    (is (= 2.0  (infix √ x)))
    (is (= 5.0  (infix √ x + y)))
    (is (= -1.0 (infix cos π)))))

(deftest check-binary-precedence
  (let [x 4
        y 3 ]
    (is (= 12   (infix x . y)))
    (is (= 64.0 (infix x ** y)))))

(deftest check-from-string
  (is (= 7 ((from-string "5 + 2"))))
  (is (= 7 ((from-string [x] "x + 3") 4)))
  (is (= 1 ((from-string [x] {:+ -} "x + 3") 4)))
  (is (= 7 ((from-string [] {:x 6 :+ +} "x + 1"))))
  (is (thrown-with-msg? java.text.ParseException #"Failed to parse expression: 'x \+ '"
                        ((from-string [x] "x + ") 3)))
  (is (thrown-with-msg? clojure.lang.ArityException #"Wrong number of args \(2\) passed to: .*"
                        ((from-string [x] "x + 3") 2 3)))
  (is (thrown-with-msg? IllegalStateException #"x is not bound in environment"
                        ((from-string "x + 3")))))
