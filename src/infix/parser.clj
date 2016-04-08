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


;; Adapted from https://gist.github.com/kachayev/b5887f66e2985a21a466
(ns infix.parser)

(defn failure [_] '())

(defn any [input]
  (if (empty? input)
    (failure input)
    (list [(first input) (apply str (rest input))])))

(defn parse [parser input]
  (parser input))

(defn parse-all [parser input]
  (->>
    input
    (parse parser)
    (filter #(= "" (second %)))
    ffirst))

(defn return [v]
  (fn [input] (list [v input])))

(defn >>= [m f]
  (fn [input]
    (->>
      input
      (parse m)
      (mapcat (fn [[v tail]] (parse (f v) tail))))))

(defn merge-bind [body bind]
  (if (and (not= clojure.lang.Symbol (type bind))
           (= 3 (count bind))
           (= '<- (second bind)))
    `(>>= ~(last bind) (fn [~(first bind)] ~body))
    `(>>= ~bind (fn [~'_] ~body))))

(defmacro do* [& forms]
  (reduce merge-bind (last forms) (reverse (butlast forms))))

; Basic parsers

(defn sat
  "Satisfies a given predicate"
  [pred]
  (>>= any (fn [v] (if (pred v) (return v) failure))))

(defn char-cmp
  "Does a character comparison using a specific function"
  [f]
  (fn [c] (sat (partial f (first c)))))

(def match
  "Recognises a given char"
  (char-cmp =))

(def none-of
  "Rejects a given char"
  (char-cmp not=))

(defn from-re [re]
  (sat (fn [v] (not (nil? (re-find re (str v)))))))

; Combinators

(defn and-then
  "(ab)"
  [p1 p2]
  (do*
    (r1 <- p1)
    (r2 <- p2)
    (return (str r1 r2))))

(defn or-else
  "(a|b)"
  [p1 p2]
  (fn [input]
    (lazy-cat (parse p1 input) (parse p2 input))))

(declare plus)
(declare optional)

(defn many
  "(a*)"
  [parser]
  (optional (plus parser)))

(defn plus
  "(a+) equals to (aa*)"
  [parser]
  (do*
    (a <- parser)
    (as <- (many parser))
    (return (cons a as))))

(defn optional
  "(a?)"
  [parser]
  (or-else parser (return "")))

(def space (or-else (match " ") (match "\t")))

(def spaces (many space))

(defn any-of [& parsers]
  (reduce or-else parsers))

(defn string [s]
  (reduce and-then (map #(match (str %)) s)))

(defmacro fwd
  "Delays the evaluation of a parser that was forward (declare)d and
   it has not been defined yet. For use in (def)s of no-arg parsers,
   since the parser expression evaluates immediately."
  [p]
  (let [x (gensym)]
    `(fn [~x] (~p ~x))))
