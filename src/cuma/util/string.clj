(ns cuma.util.string
  (:require
    [clojure.string :as str]))

(defn index-of [s target from]
  (let [i (.indexOf s target from)]
    (if (not= -1 i) i)))

(defn indexes-of
  ([s target] (indexes-of s target 0))
  ([s target from]
   (if (str/blank? target)
     [(min (count s) from)]
     (->> (iterate
            #(if % (if-let [i (index-of s target (second %))]

                     [i (inc i)]))
            [-1 from])
          rest
          (map first)
          (take-while (comp not nil?))))))

(defn get-paired-index
  ([s start end] (get-paired-index s start end 0))
  ([s start end from]
   (if-let [i (index-of s end from)]
     (let [x (.substring s (inc from) i)
           n (count (take-while (comp not nil?) (indexes-of x start)))]
       (if (zero? n)
         i
         (nth (indexes-of s end (inc i)) (dec n) nil)
         )
       ))))


