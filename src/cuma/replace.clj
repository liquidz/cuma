(ns cuma.replace
  (:require
    [clojure.string :as str]))

(defn- find-paired-parenthesis-index
  ([s index] (find-paired-parenthesis-index s index 0 true))
  ([s index depth first?]
   (cond
     (and (not first?) (zero? depth)) index
     (>= index (count s)) nil
     :else
     (let [n (inc index)]
       (case (.charAt s index)
         \( (recur s n (inc depth) false)
         \) (recur s n (dec depth) first?)
         (recur s n depth first?))))))

(defn replace-sexp
  [s index-fn replace-fn]
  (loop [res s, from-index 0]
    (if-let [start (index-fn res from-index)]
      (if-let [end (find-paired-parenthesis-index res start)]
        (let [before (.substring res start end)
              after  (replace-fn before)]
          (recur (str/replace res before after)
                 (+ start (count after))))
        res)
      res)))
