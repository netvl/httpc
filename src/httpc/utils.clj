(ns httpc.utils)

(defn zip
  "Given a number of sequences return new lazy sequence of elements from said sequences zipped together into a vector."
  [& more]
  (apply map vector more))
