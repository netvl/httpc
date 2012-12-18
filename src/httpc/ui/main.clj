(ns httpc.ui.main
  (:use (seesaw core mig table))
  (:use httpc.utils
        httpc.ui.utils)
  (:require [httpc.ui.headers :as h]
            [httpc.ui.result :as r]
            [httpc.net.http :as http]))

(defn- create-main-form
  "Creates a panel containing main user interface."
  []
  (let [headers-model (table-model :columns [{:key :name :text "Name"}
                                             {:key :value :text "Value"}])
        request-type-group (button-group)]
    (with-widgets
      [(combobox :id :address-box :editable? true)
       (button :id :send-button :text "Send")
       (radio :id :request-get-type :text "GET" :selected? true :group request-type-group)
       (radio :id :request-post-type :text "POST" :group request-type-group)
       (radio :id :request-put-type :text "PUT" :group request-type-group)
       (radio :id :request-delete-type :text "DELETE" :group request-type-group)
       (button :id :add-header-button :text "Add")
       (button :id :delete-header-button :text "Delete")
       (button :id :clear-headers-button :text "Clear")
       (button :id :move-header-up-button :text "Move up")
       (button :id :move-header-down-button :text "Move down")
       (table :id :headers-table :model headers-model)
       (text :id :body-text-area :multi-line? true)]
;      (config! headers-table :minimum-size [(-> (config headers-table :minumum-size) .getWidth) :by 30])
      (request-focus! address-box)
      (let [form
            (mig-panel
              :id :main-panel
              :constraints ["height 500" "[][grow][]" "[][][][grow][][grow]"]
              :items [[address-box "spanx 2,growx"] [send-button "wrap"]
                      ["Method" ""] [request-get-type "split,sg methods"] [request-post-type "sg methods"]
                                    [request-put-type "sg methods"] [request-delete-type "sg methods,wrap"]
                      ["Headers" ""] [add-header-button "sg actions,split"] [delete-header-button "sg actions"]
                                     [clear-headers-button "sg actions"] [move-header-up-button "sg actions"]
                                     [move-header-down-button "sg actions,wrap"]
                      ; TODO: fix hack with height below
                      [(scrollable headers-table) "spanx 3,grow,height 0.1*pref,wrap"]
                      ["Body", "wrap"]
                      [(scrollable body-text-area) "spanx 3,grow"]])]
        form))))

(defn- get-request-method
  [w]
  (let [{:keys [request-get-type request-post-type request-put-type request-delete-type]} (group-by-id w)]
    (cond
      (selected? request-get-type) :get
      (selected? request-post-type) :post
      (selected? request-put-type) :put
      (selected? request-delete-type) :delete
      :else (throw (IllegalStateException. "None of known methods are selected")))))

(declare do-send)

(defn- install-handlers!
  "Sets up event handling over main form."
  [w]
  (let [{:keys [add-header-button delete-header-button clear-headers-button
                move-header-up-button move-header-down-button send-button

                address-box headers-table body-text-area]} (group-by-id w)
        headers-model (config headers-table :model)]

    ; Add reactions to header manipulation
    (listen-for add-header-button :action [_]
      (when-let [[header value] (h/ask-header-values :parent w)]
        (append! headers-model {:name header :value value})))

    (listen-for delete-header-button :action [_]
      (when-let [sel-idxs (selection headers-table {:multi? true})]
        (apply remove-at! headers-model sel-idxs)))

    (listen-for clear-headers-button :action [_]
      (clear! headers-model))

    (listen-for move-header-up-button :action [_]
      (let [sel-idxs (selection headers-table {:multi? true})
            new-sel-idxs (map dec sel-idxs)]
        (when (not-any? #(= 0 %) sel-idxs)
          (doseq [[oi ni] (zip sel-idxs new-sel-idxs)]
            (swap-at! headers-model oi ni))
          (selection! headers-table {:multi? true} new-sel-idxs))))

    (listen-for move-header-down-button :action [_]
      (let [sel-idxs (selection headers-table {:multi? true})
            new-sel-idxs (map inc sel-idxs)]
        (when (not-any? #(= (dec (model-size headers-model)) %) sel-idxs)
          (doseq [[oi ni] (zip sel-idxs new-sel-idxs)]
            (swap-at! headers-model oi ni))
          (selection! headers-table {:multi? true} new-sel-idxs))))

    (listen-for headers-table :mouse-clicked [e]
      (when (= (.getClickCount e) 2)  ; double click
        (when-let [sel-idx (selection headers-table)]
          (let [{:keys [name value]} (value-at headers-model sel-idx)]
            (when-let [[new-name new-value] (h/ask-header-values :parent w :default-name name :default-value value)]
              (update-at! headers-model sel-idx {:name new-name :value new-value}))))))

    ; Reaction to send button
    (listen-for send-button :action [_]
      (do-send w))
    )
  w)

(defn- collect-headers
  [headers-table]
  (table->seq headers-table))

(defn- do-send
  [w]
  (let [url (text (select w [:#address-box]))
        method (get-request-method w)
        headers (collect-headers (select w [:#headers-table]))
        body (text (select w [:#body-text-area]))
        result-window (r/create-result-window)]
    (http/send-request (r/create-events-listener result-window) url method headers body)))

(defn create-main-frame
  "Creates frame object with main user interface."
  []
  (->
    (frame
      :title "httpc"
      :content (create-main-form)
      :on-close :dispose)
    install-handlers!))

