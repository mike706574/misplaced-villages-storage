(defproject org.clojars.mike706574/misplaced-villages-storage "0.0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [com.taoensso/timbre "4.8.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [org.clojars.mike706574/monger "3.2.0-SNAPSHOT"]
                 [org.clojars.mike706574/misplaced-villages "0.0.1-SNAPSHOT"]]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :profiles {:dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/test.check "0.9.0"]]}})
