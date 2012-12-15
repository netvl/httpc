(ns httpc.ui.result
  (:use (seesaw core mig table border))
  (:use httpc.ui.utils
        httpc.utils))

(def ^:private result-number (atom 0))

(defn next-result-number
  "Bumps the result number in the atom and returns new value."
  []
  (swap! result-number inc))

(defn create-raw-tab-content
  "Creates the contents of 'Raw' tab."
  []
  (with-widgets
    [(text
       :id :raw-response-area
       :multi-line? true
       :editable? false)]
    (mig-panel
      :id :raw-tab-panel
      :items [[(scrollable raw-response-area) "grow,push"]])))

(defn create-headers-tab-content
  "Creates the contents of 'Headers' tab."
  []
  (let [headers-model (table-model :columns [{:key :name :text "Name"}
                                             {:key :value :text "Value"}])]
    (with-widgets
      [(text :id :status-code-field :editable? false)
       (text :id :status-message-field :editable? false)
       (table :id :headers-table :model headers-model)]
      (mig-panel
        :id :headers-tab-panel
        :constraints ["" "[][grow 3][][grow 10]" "[][grow]"]
        :items [["Status code" ""] [status-code-field "growx"]
                ["Status message" ""] [status-message-field "growx,wrap"]
                [(scrollable headers-table) "grow,spanx 4"]]))))

(defn create-body-tab-content
  "Creates the contents of 'Body' tab."
  []
  (with-widgets
    [(text :id :body-size-field :editable? false)
     (text :id :content-type-field :editable? false)
     (text :id :body-area :multi-line? true :editable? false)]
    (mig-panel
      :id :body-tab-panel
      :constraints ["" "[][grow]" "[][][grow]"]
      :items [["Body size" ""] [body-size-field "growx,wrap"]
              ["Content type" ""] [content-type-field "growx,wrap"]
              [(scrollable body-area) "grow,spanx 2"]])))

(defn create-result-panel
  "Creates the main panel."
  []
  (with-widgets
    [(text :id :address-box :editable? false)
     (tabbed-panel
       :id :tabs-panel :placement :top
       :tabs [{:title "Raw" :content (create-raw-tab-content)}
              {:title "Headers" :content (create-headers-tab-content)}
              {:title "Body" :content (create-body-tab-content)}])
     (button :id :close-button :text "Close")
     (label :id :progress-label :text "Working...")]
    (mig-panel
      :id :main-panel
      :constraints ["" "[][grow][]" "[][grow][]"]
      :items [["Request URL" ""] [address-box "spanx 2,growx,wrap"]
              [tabs-panel "spanx 3,grow,wrap"]
              [(horizontal-panel :items [progress-label] :border (line-border)) "spanx 2,grow"]
              [close-button ""]])))

(defn create-result-window
  "Create a window for displaying HTTP request results."
  []
  (let [w
        (frame
          :title (str "Result " (next-result-number))
          :on-close :dispose
          :content (create-result-panel))]
    (listen-for (select w [:#close-button]) :action [_]
      (dispose! w))
    w))

(defn show-result-window
  "Creates and shows a window for displaying HTTP request results."
  []
  (-> (create-result-window)
    pack!
    (relative-location! nil)
    show!))

(defn create-events-listener
  "Creates a function which will handle different events sent from other places, changing the window state."
  [w]
  (let [{:keys [progress-label
                address-box
                status-code-field status-message-field headers-table
                body-size-field content-type-field body-area]} (group-by-id w)]
    (fn [e]
      (invoke-later
        (process-events e
          :present
          ([]
            (-> w pack! (relative-location! nil) show!))
          :set-url
          ([url]
            (text! address-box url))
          :set-status
          ([status]
            (text! status-code-field (:code status))
            (text! status-message-field (:message status)))
          :set-headers
          ([headers]
            (doseq [header headers]
              (append! headers-table header)))
          :set-body
          ([body content-type]
            (text! body-size-field (count body))
            (text! content-type-field content-type)
            (text! body-area body))
          :set-progress
          ([progress]
            (text! progress-label progress)))))))
