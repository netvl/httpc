(ns httpc.core
  (:gen-class)
  (:use httpc.ui.utils)
  (:require [httpc.ui.main :as main-ui]))

(defn -main [& args]
  (seesaw.core/native!)
  (-> (main-ui/create-main-frame) present!))
