(defproject dpx-infinity/httpc "0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [http.async.client "0.5.0"]
                 [seesaw "1.4.2"]]
  :exclusions [org.clojure/clojure]

  :aot :all

  :source-paths ["src"]
  :resources-paths ["resources"]
  :test-paths ["test"]

  :omit-source true
  :target-path "out"
  :compile-path "out/classes"
  :library-path "out/lib")
