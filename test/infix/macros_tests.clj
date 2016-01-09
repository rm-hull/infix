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

