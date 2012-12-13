(ns httpc.ui.headers
  (:use (seesaw core mig))
  (:use httpc.ui.utils))

(defn create-header-dialog-content
  "Creates a panel with header dialog contents."
  [default-name default-value]
  (mig-panel
    :constraints ["" "[][grow]"]
    :items [["Name" ""]  [(text :id :header-name-field :text default-name) "growx,wrap"]
            ["Value" ""] [(text :id :header-value-field :text default-value) "growx,wrap"]]))

(defn header-dialog-ok-handler
  "A handler function reacting to dialog confirmation."
  [d]
  (let [{:keys [header-name-field header-value-field]} (group-by-id d)]
    [(text header-name-field) (text header-value-field)]))

(defn ask-header-values
  "Asks the user for header name-value pair in modal window. Returns [header value] when succeeded, nil otherwise."
  [& {:keys [parent default-name default-value]}]
  (-> (dialog
        :parent parent
        :title "Edit header"
        :content (create-header-dialog-content default-name default-value)
        :option-type :ok-cancel
        :success-fn header-dialog-ok-handler)
    pack! (relative-location! parent) show!))
