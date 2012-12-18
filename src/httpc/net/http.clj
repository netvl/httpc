(ns httpc.net.http
  (:use httpc.utils)
  (:require [http.async.client :as h]
            [http.async.client.request :as r]))

(defn handle-status
  [events-listener response status]
  (send-event events-listener :set-progress
    :progress "Received status line")
  (send-event events-listener :set-status
    :status {:code (:code status) :message (:msg status)})
  [status :continue])

(defn handle-headers
  [events-listener response headers]
  (send-event events-listener :set-progress
    :progress "Received headers")
  (send-event events-listener :set-headers
    :headers (into [] (for [[name value] headers] {:name (kwname name) :value value})))
  [headers (if headers :continue :abort)])

(defn handle-completed
  [events-listener client response]
  (if (realized? (:body response))
    (do
      (send-event events-listener :set-progress
        :progress "Received body")
      (send-event events-listener :set-body
        :body (h/string response)
        :content-type (get (h/headers response) "Content-Type")))
    (do
      (send-event events-listener :set-progress
        :progress "Failed to receive body")))
  (h/close client)
  [true :continue])

(defn handle-error
  [events-listener client response ^java.lang.Throwable throwable]
  (send-event events-listener :set-progress
    :progress "An error occured")
  (.printStackTrace throwable)
  (h/close client))

(defn send-request
  [events-listener url method headers body]
  (send-event events-listener :present)
  (send-event events-listener :set-progress :progress "Sending a request to server...")
  (send-event events-listener :set-url :url url)
  (let [client (h/create-client :keep-alive false)]
    (let [headers (into {} (for [{:keys [name value]} headers] [name value]))
          request
          (if (#{:get :head} method)
            (r/prepare-request method url :headers headers)   ; Do not set body if the request method is GET or HEAD
            (r/prepare-request method url :headers headers :body body))]
      (r/execute-request client request
        :status (partial handle-status events-listener)
        :headers (partial handle-headers events-listener)
        :completed (partial handle-completed events-listener client)
        :error (partial handle-error events-listener client)))))
