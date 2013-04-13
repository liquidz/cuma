(ns cuma.util.string
  (:require
    [clojure.string :as str]))

; =index-of
(defn index-of [s target from]
  (let [i (.indexOf s target from)]
    (if (not= -1 i) i)))

; =indexes-of
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

; =get-paired-index
(defn get-paired-index
  ([s start end] (get-paired-index s start end 0))
  ([s start end from]
   (let [t (+ (count start) from)
         i (index-of s end from)]
     (if (and i (<= t i))
       (let [n (count (indexes-of (.substring s t i) start))]
         (if (zero? n)
           i
           (nth (indexes-of s end (inc i)) (dec n) nil)))))))

; =dotted-get
(defn dotted-get [data dotted]
  (let [arr (if (and (string? dotted) (not= "." dotted))
              (str/split dotted #"\.")
              [dotted])]
    (reduce #(get % (keyword %2)) data arr)))
