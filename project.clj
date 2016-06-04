(defproject rm-hull/infix "0.2.8-SNAPSHOT"
  :description "A small Clojure/ClojureScript library for expressing LISP expressions as infix rather than prefix notation"
  :url "https://github.com/rm-hull/infix"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [rm-hull/jasentaa "0.2.1"]]
  :scm {:url "git@github.com:rm-hull/infix.git"}
  :plugins [
    [lein-codox "0.9.5"] ]
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :codox {
    :source-paths ["src"]
    :output-path "doc/api"
    :source-uri "http://github.com/rm-hull/infix/blob/master/{filepath}#L{line}"  }
  :min-lein-version "2.6.1"
  :profiles {
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-cloverage "1.0.6"]]}})
