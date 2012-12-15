(ns httpc.utils)

(defn zip
  "Given a number of sequences return new lazy sequence of elements from said sequences zipped together into a vector."
  [& more]
  (apply map vector more))

(defn new-event
  "Creates a new event with given name and parameters. Parameters should be a number of key-value pairs.
  An event is a plain map with name stored under :name key and parameters stored as-is."
  [name & params]
  (merge {:name name} (apply hash-map params)))

(defn send-event
  "A wrapper using an agent which emulates message passing. listener is some function representing message handler,
  and event is sequence of arguments which will be supplied to new-event. event is wrapped into an agent,
  and then listener is sent to this agent."
  [listener & event-args]
  (send (agent (apply new-event event-args)) listener))

(defmacro process-events
  "Generates an event processing structure which compares :name key of event with a set of names and calls
  corresponding handler. The handler is a list where the first element is a vector with desired event parameters,
  and other elements constitute the body of handler. Example transformation:

  (process-events evt
    :event-1 ([param-1 param-2]
                (do-smth param-1 param-2))
    :event-2 ([param-3]
                (println param-3)))

  ===>

  (case (:name evt)
    :event-1 (let [{:keys [param-1 param-2]} evt}]
               (do-smth param-1 param-2))
    :event-2 (let [{:keys [param-3]} evt}]
               (println param-3)))
  "
  [event & pairs]
  `(case (:name ~event)
     ~@(apply concat
         (for [[name [params & body]] (partition-all 2 pairs)]
           [name `(let [{:keys ~params} ~event] ~@body)]))))
