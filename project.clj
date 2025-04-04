(defproject rm-hull/infix "0.4.4"
  :description "A small Clojure library for expressing LISP expressions as infix rather than prefix notation"
  :url "https://github.com/rm-hull/infix"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [rm-hull/jasentaa "0.2.5"]]
  :scm {:url "git@github.com:rm-hull/infix.git"}
  :vcs :git
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :codox {
    :source-paths ["src"]
    :doc-files [
      "doc/basic-usage.md"
      "doc/references.md"
      "LICENSE.md"
    ]
    :output-path "doc/api"
    :source-uri "http://github.com/rm-hull/infix/blob/main/{filepath}#L{line}"
    :themes [:default [:google-analytics {:tracking-code "UA-39680853-6" }]]}
  :min-lein-version "2.8.1"
  :profiles {
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-codox "0.10.8"]
        [lein-cljfmt "0.9.2"]
        [lein-cloverage "1.2.4"]]
      :dependencies [
        [org.clojure/clojure "1.11.1"]
        [google-analytics-codox-theme "0.1.0"]]}})

