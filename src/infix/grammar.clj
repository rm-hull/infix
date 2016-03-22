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

(ns infix.grammar
  (:require
    [infix.parser :refer :all]))

; expression ::= term { addop term }.
; term ::= factor { mulop factor }.
; factor ::= "(" expression ")" | var | number | function.
; addop ::= "+" | "-".
; mulop ::= "*" | "/".
;
; function ::= envref expression | envref "(" expression { "," expression } ")".
; ref ::= letter { letter | digit | "-" | "_" }.
; var ::= envref.
; number ::= integer | decimal | rational
; integer :: = [ "-" ] digits.
; decimal :: = [ "-" ] digits "." digits.
; rational :: = integer "/" digits.
; letter ::= "A" | "B" | ... | "Z" | "a" | "b" | ... | "z".
; digit ::= "0" | "1" | ... | "8" | "9".
; digits ::= digit { digit }.

(def digit (from-re #"[0-9]"))

(def letter (from-re #"[a-zA-Z]"))

(def alpha-num (any-of letter digit (match "_") (match "-")))

(def digits
  (do*
    (text <- (plus digit))
    (return (apply str text))))

(def envref
  (do*
   (fst <- letter)
   (rst <- (many alpha-num))
   (return (let [kw (keyword (apply str (cons fst rst)))]
             (fn [env]
               (if (contains? env kw)
                 (get env kw)
                 (throw (Exception.
                          (str (name kw) " is not bound in environment")))))))))

(def var envref)

(def integer
  (do*
    (sign <- (optional (match "-")))
    (value <- digits)
    (return (constantly (Integer/parseInt (str sign value))))))

(def rational
  (do*
    (dividend <- integer)
    (match "/")
    (divisor <- digits)
    (return (constantly (/ (dividend) (Integer/parseInt divisor))))))

(def decimal
  (do*
    (sign <- (optional (match "-")))
    (i <- digits)
    (p <- (match "."))
    (d <- digits)
    (return (constantly (Double/parseDouble (str sign i p d))))))

(def number
  (any-of integer decimal rational))

(defn list-of [parser]
  (do*
    (fst <- parser)
    (rst <- (optional
              (do*
                spaces
                (match ",")
                spaces
                (list-of parser))))
    (return (cons fst rst))))

(declare expression)

(def function
  (or-else
    (do*
      (f <- envref)
      (plus (match " "))
      (expr <- expression)
      (return (fn [env]
                ((f env) (expr env)))))
    (do*
      (f <- envref)
      (match "(")
      spaces
      (args <- (list-of expression))
      spaces
      (match ")")
      (return (fn [env]
                (apply
                  (f env)
                  (map #(% env) args)))))))

(def factor
  (any-of
    (do*
      (match "(")
      spaces
      (e <- expression)
      spaces
      (match ")")
      (return e))
    var
    number
    function))

(def mulop
  (do*
    (op <- (any-of (match "*") (match "/") (match ".") (string "**")))
    (return (fn [env]
              (get env (keyword (str op)))))))

(def addop
  (do*
    (op <- (any-of (match "+") (match "-")))
    (return (fn [env]
              (get env (keyword (str op)))))))

(def term
  (or-else
    factor
    (do*
      (f1 <- factor)
      spaces
      (op <- mulop)
      spaces
      (f2 <- term)
      (return
        (fn [env]
          ((op env) (f1 env) (f2 env)))))))

(def expression
  (or-else
    term
    (do*
      (t1 <- term)
      spaces
      (op <- addop)
      spaces
      (t2 <- expression)
      (return
        (fn [env]
          ((op env) (t1 env) (t2 env)))))))

(def base-env {
  :+ +
  :- -
  :* *
  :. *
  :/ /
  :% mod
  :** (fn [a b] (Math/pow a b))
  :pow (fn [a b] (Math/pow a b))
  :sqrt (fn [n] (Math/sqrt n))
  :e  Math/E
  :pi  Math/PI
})
