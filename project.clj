(defproject dpx-infinity/httpc "0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [http.async.client "0.5.0"]
                 [seesaw "1.4.2"]
                 [clj-yaml "0.4.0"]]
  :exclusions [org.clojure/clojure]

  :aot :all
  :warn-on-reflection true
  :main httpc.core

  :source-paths ["src"]
  :resources-paths ["resources"]
  :test-paths ["test"]

  :omit-source true
  :target-path "out"
  :compile-path "out/classes"
  :library-path "out/lib"

  :jar-name "httpc-stripped.jar"
  :uberjar-name "httpc.jar")
