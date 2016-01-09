(defproject rm-hull/infix "0.0.1-SNAPSHOT"`
  :description "A small Clojure/ClojureScript library for expressing LISP expressions as infix rather than prefix notation"
  :url "https://github.com/rm-hull/infix"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.7.0"]
    [rm-hull/cljs-test "0.0.8-SNAPSHOT"]]
  :scm {:url "git@github.com:rm-hull/infix.git"}
  :plugins [
    [codox "0.9.1"] ]
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :codox {
    :sources ["src"]
    :output-dir "doc/api"
    :src-dir-uri "http://github.com/rm-hull/infix/blob/master/"
    :src-linenum-anchor-prefix "L" }
  :min-lein-version "2.5.3"
  :global-vars {*warn-on-reflection* true})
