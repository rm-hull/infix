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


(ns infix.macros
  (:require
   [jasentaa.parser :refer [parse-all]]
   [infix.core :refer [rewrite resolve-alias base-env]]
   [infix.grammar :refer [expression]]))

(defmacro infix
  "Takes an infix expression, resolves an aliases before rewriting the
   infix expressions into standard LISP prefix expressions."
  [& expr]
  (->>
   expr
   (map resolve-alias)
   rewrite))

(defn- binding-vars [bindings]
  (->>
   bindings
   (map #(vector (keyword %) (gensym "arg_")))
   (into {})))

(defmacro from-string
  ([expr]
   `(from-string [] ~expr))

  ([bindings expr]
   `(from-string ~bindings ~base-env ~expr))

  ([bindings env expr]
   (cond
     (not (vector? bindings))
     (throw (IllegalArgumentException. "Binding variables is not a vector"))

     :else
     (let [b# (binding-vars bindings)]
       `(if-let [f# (parse-all expression ~expr)]
          (with-meta
            (fn [~@(vals b#)]
              (f# (merge ~env ~b#)))
            {:doc ~expr}))))))
