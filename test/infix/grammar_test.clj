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
  (:require
    [clojure.test :refer :all]
    [infix.grammar :refer :all]
    [infix.parser :refer [parse-all]]))

(deftest check-var
  (let [env { :x 32 :something-else 19}]
    (is (nil? (parse-all var "54")))
    (is (= 32 ((parse-all var "x") env)))
    (is (= 19 ((parse-all var "something-else") env)))
    (is (thrown? Exception ((parse-all var "fred") env)))))

(deftest check-integer
  (is (nil? (parse-all integer "f")))
  (is (nil? (parse-all integer "1/2")))
  (is (nil? (parse-all integer "1.2")))
  (is (= 17 ((parse-all integer "17"))))
  (is (= -17 ((parse-all integer "-17")))))

(deftest check-rational
  (is (nil? (parse-all rational "f")))
  (is (nil? (parse-all rational "12")))
  (is (nil? (parse-all rational "1.2")))
  (is (= 1/7 ((parse-all rational "1/7"))))
  (is (= -1/7 ((parse-all rational "-1/7")))))

(deftest check-decimal
  (is (nil? (parse-all decimal "f")))
  (is (nil? (parse-all decimal "12")))
  (is (nil? (parse-all decimal "1/2")))
  (is (= 1.7 ((parse-all decimal "1.7"))))
  (is (= -1.7 ((parse-all decimal "-1.7")))))

(deftest check-number
  (is (nil? (parse-all number "f")))
  (is (= 1/12 ((parse-all number "6/72"))))
  (is (= 17 ((parse-all number "17"))))
  (is (= -1.7 ((parse-all number "-1.7")))))

(deftest check-list
  (is (nil? (parse-all (list-of digits) " ")))
  (is (= ["1", "1", "2", "3", "5"] (parse-all (list-of digits) "1,1,2,3,5"))))
