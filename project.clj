(defproject clj-tools "0.1.0-SNAPSHOT"
  :description "clj-tools"
  :dependencies [
    [org.clojure/clojure "1.9.0"]
    [cider/cider-nrepl "0.16.0"]
    [org.clojure/data.json "0.2.6"]
    [org.clojure/tools.namespace "0.2.11"]
    [http-kit "2.2.0"]]
  :main clj-tools.core
  :aot [clj-tools.core]
  :plugins [[lein-ancient "0.6.15"]])



  