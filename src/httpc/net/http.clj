(ns httpc.net.http
  (:use httpc.utils))

(defn send-request
  [events-listener url headers body]
  (send-event events-listener :present)
  (send-event events-listener :set-progress :progress "Connecting to server..."))
