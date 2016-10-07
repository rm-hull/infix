(defproject rm-hull/infix "0.2.10"
  :description "A small Clojure/ClojureScript library for expressing LISP expressions as infix rather than prefix notation"
  :url "https://github.com/rm-hull/infix"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [rm-hull/jasentaa "0.2.3"]]
  :scm {:url "git@github.com:rm-hull/infix.git"}
  :vcs :git
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
        [lein-codox "0.10.0"]
        [lein-cloverage "1.0.7"]]
      :dependencies [
        [org.clojure/clojure "1.8.0"]]}})

