(ns httpc.ui.utils
  (:use (seesaw core table)))

(defn selected?
  "Returns true when the component is selected, e.g. radio button is checked."
  [w]
  (config w :selected?))

(defn model-size
  "Returns number of rows in the table model."
  [target]
  (.getRowCount target))

(defn append!
  "Appends a number of rows to the back of table model."
  [target & rows]
  (doseq [row rows]
    (insert-at! target (model-size target) row)))

(defn swap-at!
  "Swaps two rows of table model. i should be less than j."
  [target i j]
  (when (and (>= i 0) (< i (model-size target)) (>= j 0) (< j (model-size target)))
    (let [ri (value-at target i)
          rj (value-at target j)]
      (update-at! target i rj)
      (update-at! target j ri))))

(defn relative-location!
  "Applies Window#setPositionRelativeTo to target and parent and returns target."
  [target parent]
  (.setLocationRelativeTo target parent)
  target)

(defmacro listen-for
  "Simple wrapper for (seesaw.core/listen) for more fluent definition of listeners."
  [target event arglist & body]
  `(listen ~target ~event (fn ~arglist ~@body)))
