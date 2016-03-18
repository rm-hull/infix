(ns infix.macros-tests
  (:use [clojure.test]
        [infix.macros]))

(def ε 0.0000001)

(deftest basic-arithmetic
  (is (= (+ 3 4) (infix 3 + 4)))
  (is (= 43 (infix 3 + 5 * 8)))
  (is (= 64 (infix (3 + 5) * 8))))

(deftest check-aliasing
  (is (= 5.0 (infix √(5 * 5))))
  (let [t 0.324]
    (is (> ε (Math/abs (- (infix sin(2 * t) + 3 * cos(4 * t)) 1.4176457261295824))))))

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
