(ns httpc.core
  (:gen-class)
  (:require [httpc.ui.main :as main-ui]))

(defn -main [& args]
  (seesaw.core/native!)
  (-> (main-ui/create-main-frame) main-ui/present!))
