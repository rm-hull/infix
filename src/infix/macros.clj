(ns infix.macros)

(def operator-alias
  {'&& 'and
   '|| 'or
   '==  '=
   '!=  'not=
   '%   'mod
   '<<  'bit-shift-left
   '>>  'bit-shift-right
   '!   'not
   '&   'bit-and
   '|   'bit-or
   'sin 'Math/sin
   'cos 'Math/cos
   'tan 'Math/tan
   'sqrt 'Math/sqrt
   'asin 'Math/asin
   'acos 'Math/acos
   'atan 'Math/atan
   'π    'Math/PI
   '√    'Math/sqrt
   }
  )

(def operator-precedence
  ; From https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages
  ; Lowest precedence first
  ['or 'and 'bit-or 'bit-xor 'bit-and 'not= '= '>= '> '<= '<
   'bit-shift-right 'bit-shift-left '- '+ '/ '* 'not])

(defn- resolve-aliases
  "Attempt to resolve any aliases: if not found just return the original term"
  [expr]
  (map #(get operator-alias % %) expr))

(defn- rewrite
  "Recursively rewrites the infix-expr as a prefix expression, according to
   the operator precedence rules"
  [infix-expr]
  (cond
    (not (seq? infix-expr))
    infix-expr

    (and (seq? (first infix-expr)) (= (count infix-expr) 1))
    (rewrite (first infix-expr))

    (empty? (rest infix-expr))
    (first infix-expr)

    :else
    (loop [ops operator-precedence]
      (if-let [op (first ops)]
        (let [idx (.indexOf infix-expr op)]
          (if (pos? idx)
            (let [[expr1 [op & expr2]] (split-at idx infix-expr)]
              (list op (rewrite expr1) (rewrite expr2)))
            (recur (next ops))))
        (list
          (rewrite (first infix-expr))
          (rewrite (rest infix-expr)))))))

(defmacro infix
  "Takes an infix expression, resolves an aliases before rewriting the
   infix expressions into standard LISP prefix expressions."
  [& expr]
  (-> expr resolve-aliases rewrite))
