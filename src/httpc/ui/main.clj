(ns httpc.ui.main
  (:use (seesaw core mig table))
  (:require [httpc.ui.headers :as h]))

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
              :constraints ["height 500" "[][grow][]" ""]
              :items [[address-box "spanx 2,growx"] [send-button "wrap"]
                      ["Method" ""] [request-get-type "split,sg methods"] [request-post-type "sg methods"]
                                    [request-put-type "sg methods"] [request-delete-type "sg methods,wrap"]
                      ["Headers" ""] [add-header-button "sg actions,split"] [delete-header-button "sg actions"]
                                     [clear-headers-button "sg actions"] [move-header-up-button "sg actions"]
                                     [move-header-down-button "sg actions,wrap"]
                      ; TODO: fix hack with height below
                      [(scrollable headers-table) "spanx 3,grow,pushy,height 0.1*pref,wrap"]
                      ["Body", "wrap"]
                      [(scrollable body-text-area) "spanx 3,grow,pushy"]])]
        form))))

(defn selected?
  "Returns true when the component is selected, e.g. radio button is checked."
  [w]
  (config w :selected?))

(defn- get-request-type
  [w]
  (let [{:keys [request-get-type request-post-type request-put-type request-delete-type]} (group-by-id w)]
    (cond
      (selected? request-get-type) :get
      (selected? request-post-type) :post
      (selected? request-put-type) :put
      (selected? request-delete-type) :delete
      :else (throw (IllegalStateException. "None of known methods are selected")))))

(defn- install-handlers!
  "Sets up event handling over main form."
  [w]
  (let [{:keys [add-header-button delete-header-button clear-headers-button
                move-header-up-button move-header-down-button send-button

                address-box headers-table body-text-area]} (group-by-id w)
        request-type (get-request-type w)
        headers-model (config headers-table :model)]
    ; Add reactions to header manipulation buttons
    (listen add-header-button :action
      (fn [_]
        (when-let [[header value] (h/ask-header-values :parent w)]
          (insert-at! headers-model (.getRowCount headers-model) {:name header :value value})))))
  w)

(defn create-main-frame
  "Creates frame object with main user interface."
  []
  (->
    (frame
      :title "httpc"
      :content (create-main-form)
      :on-close :dispose)
    install-handlers!))

(defn present!
  "Shows a frame from Swing UI thread."
  [w]
  (invoke-later
    (-> w pack! show!)))
