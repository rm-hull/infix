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

(ns infix.grammar-test
  (:refer-clojure :exclude [boolean])
  (:require
   [clojure.test :refer :all]
   [jasentaa.parser :as p :refer [parse-all]]
   [infix.grammar :refer :all]
   [infix.core :refer [base-env]])
  (:import
   [java.text ParseException]))

(defn float=
  ([x y] (float= x y 0.00001))
  ([x y epsilon]
   (let [scale (if (or (zero? x) (zero? y)) 1 (Math/abs x))]
     (<= (Math/abs (- x y)) (* scale epsilon)))))

(deftest check-var
  (let [env {:x 32 :something_else 19}]
    (is (thrown? ParseException (parse-all var "54")))
    (is (= 32 ((parse-all var "x") env)))
    (is (= 19 ((parse-all var "something_else") env)))
    (is (thrown? IllegalStateException ((parse-all var "fred") env)))))

(deftest check-integer
  (is (thrown? ParseException (parse-all integer "f")))
  (is (thrown? ParseException (parse-all integer "1/2")))
  (is (thrown? ParseException (parse-all integer "1.2")))
  (is (thrown? ParseException (parse-all integer "0b01011")))
  (is (thrown? ParseException (parse-all integer "0xDEADCAFE")))
  (is (= 17 ((parse-all integer "17"))))
  (is (= -17 ((parse-all integer "-17"))))
  (is (= 443243242444234217 ((parse-all integer "443243242444234217")))))

(deftest check-binary
  (is (thrown? ParseException (parse-all binary "f")))
  (is (thrown? ParseException (parse-all binary "1/2")))
  (is (thrown? ParseException (parse-all binary "1.2")))
  (is (= 27 ((parse-all binary "0b011011"))))
  (is (= -27 ((parse-all binary "-0b011011"))))
  (is (= 4334871515 ((parse-all binary "0b100000010011000001110001111011011")))))

(deftest check-hex
  (is (thrown? ParseException (parse-all hex "f")))
  (is (thrown? ParseException (parse-all hex "1/2")))
  (is (thrown? ParseException (parse-all hex "1.2")))
  (is (= 3405691582 ((parse-all hex "0xCAFEBABE"))))
  (is (= 3735928559 ((parse-all hex "#DEADBEEF")))))

(deftest check-rational
  (is (thrown? ParseException (parse-all rational "f")))
  (is (thrown? ParseException (parse-all rational "12")))
  (is (thrown? ParseException (parse-all rational "1.2")))
  (is (thrown? ParseException (parse-all rational "0b01011")))
  (is (thrown? ParseException (parse-all rational "0xDEADCAFE")))
  (is (= 1/7 ((parse-all rational "1/7"))))
  (is (= -1/7 ((parse-all rational "-1/7")))))

(deftest check-decimal
  (is (thrown? ParseException (parse-all decimal "f")))
  (is (thrown? ParseException (parse-all decimal "12")))
  (is (thrown? ParseException (parse-all decimal "1/2")))
  (is (thrown? ParseException (parse-all decimal "0b01011")))
  (is (thrown? ParseException (parse-all decimal "0xDEADCAFE")))
  (is (= 1.7 ((parse-all decimal "1.7"))))
  (is (= -1.7 ((parse-all decimal "-1.7")))))

(deftest check-number
  (is (thrown? ParseException (parse-all number "f")))
  (is (= 1/12 ((parse-all number "6/72"))))
  (is (= 17 ((parse-all number "17"))))
  (is (= -1.7 ((parse-all number "-1.7"))))
  (is (= 240 ((parse-all number "0b11110000"))))
  (is (= 65534 ((parse-all number "#FFFE")))))

(deftest check-list
  (is (thrown? ParseException (parse-all (list-of digits) " ")))
  (is (thrown? ParseException (parse-all (list-of digits) "")))
  (is (= ["1"] (parse-all (list-of digits) "1")))
  (is (= ["6", "7", "8"] (parse-all (list-of digits) "6 , 7, 8 ")))
  (is (= ["1", "1", "2", "3", "5"] (parse-all (list-of digits) "1,1,2,3,5"))))

(deftest check-function
  (let [env {:x 81 :* *, :sqrt (fn [x] (Math/sqrt x))}]
    (is (= 9.0 ((parse-all function "sqrt x") env)))
    (is (= 5.0 ((parse-all function "sqrt(25)") env)))
    (is (= 7.0 ((parse-all function "sqrt(7*7)") env)))
    (is (thrown? IllegalStateException ((parse-all function "bargle(7)") env)))
    (is (thrown? clojure.lang.ArityException ((parse-all function "sqrt(7, 5)") env)))))

(deftest check-expression
  (let [env (merge base-env {:t 0.324})]
    (is (float= 1.4176457261295824 ((parse-all expression "sin(2 * t) + 3 * cos(4 * t)") env)))
    (is (thrown? IllegalStateException ((parse-all expression "3 + 4") {})))
    (is (= 43 ((parse-all expression "3 + 5 * 8") env)))
    (is (= 64 ((parse-all expression "(3 + 5) * 8") env)))))

(deftest check-baseenv-functions
  (is (= 16 ((parse-all expression "9 + 7") base-env)))
  (is (= 12 ((parse-all expression "19 - 7") base-env)))
  (is (= 63 ((parse-all expression "9 * 7") base-env)))
  (is (= 19/75 ((parse-all expression "19 / 75") base-env)))
  (is (= 28.0 ((parse-all expression "3 + 5 ** 2") base-env)))
  (is (= 75.0 ((parse-all expression "3 * 5 ** 2") base-env)))
  (is (float= (Math/pow 2.53 3.1) ((parse-all expression "pow(2.53, 3.1)") base-env)))
  (is (float= (Math/pow 7.01 1.9) ((parse-all expression "7.01 ** 1.9") base-env)))
  (is (float= (Math/abs -9.213) ((parse-all expression "abs(-9.213)") base-env)))
  (is (float= (Math/signum -9.213) ((parse-all expression "signum(-9.213)") base-env)))
  (is (float= (Math/sqrt 24353) ((parse-all expression "sqrt(24353)") base-env)))
  (is (float= 3 ((parse-all expression "root(3, 27)") base-env)))
  (is (float= (Math/exp 93) ((parse-all expression "exp(93)") base-env)))
  (is (float= (Math/log 23.1) ((parse-all expression "log(23.1)") base-env)))
  (is (float= (Math/sin 1.91) ((parse-all expression "sin(1.91)") base-env)))
  (is (float= (Math/cos 2.791) ((parse-all expression "cos(2.791)") base-env)))
  (is (float= (Math/tan 44.3) ((parse-all expression "tan(44.3)") base-env)))
  (is (float= (Math/asin 0.99) ((parse-all expression "asin(0.99)") base-env)))
  (is (float= (Math/acos 0.04) ((parse-all expression "acos(0.04)") base-env)))
  (is (float= (Math/atan 0.11) ((parse-all expression "atan(0.11)") base-env)))
  (is (float= (Math/atan2 0.1 2) ((parse-all expression "atan2(0.1, 2)") base-env)))
  (is (float= (Math/sinh 60) ((parse-all expression "sinh(60)") base-env)))
  (is (float= (Math/cosh 4) ((parse-all expression "cosh(4)") base-env)))
  (is (float= (Math/tanh 1.9) ((parse-all expression "tanh(1.9)") base-env)))
  (is (float= (/ 1 (Math/cos 3)) ((parse-all expression "sec(3.0)") base-env)))
  (is (float= (/ 1 (Math/sin 2)) ((parse-all expression "csc(2)") base-env)))
  (is (float= (/ 1 (Math/tan 1.322)) ((parse-all expression "cot(1.322)") base-env)))
  (is (float= (Math/acos (/ 1 3)) ((parse-all expression "asec(3.0)") base-env)))
  (is (float= (Math/asin (/ 1 33)) ((parse-all expression "acsc(33)") base-env)))
  (is (float= (Math/atan (/ 1 0.21)) ((parse-all expression "acot(0.21)") base-env)))
  (is (float= (+ 1 2 5.7 4) ((parse-all expression "sum(1, 2, 5.7, 4)") base-env)))
  (is (float= (* 1 2 5.7 4) ((parse-all expression "product(1, 2, 5.7, 4)") base-env)))
  (is (= 1 ((parse-all expression "fact 0") base-env)))
  (is (= 120 ((parse-all expression "fact 5") base-env)))
  (is (= 4 ((parse-all expression "gcd(8, 12)") base-env)))
  (is (= 24 ((parse-all expression "lcm(8, 12)") base-env)))
  (is (= true ((parse-all expression "3 = 3") base-env)))
  (is (= false ((parse-all expression "3 = (5 - 1)") base-env)))
  (is (= true ((parse-all expression "3 != 5") base-env)))
  (is (= false ((parse-all expression "0 = 0.0") base-env)))
  (is (= true ((parse-all expression "0 == 0.0") base-env))))

