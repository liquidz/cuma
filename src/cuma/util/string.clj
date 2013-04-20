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

; =get-paired-string-index
(defn get-paired-string-index
  ([s start end] (get-paired-string-index s start end 0))
  ([s start end from]
   (let [t (+ (count start) from)
         i (index-of s end from)]
     (if (and i (<= t i))
       (let [n (count (indexes-of (.substring s t i) start))]
         (if (zero? n)
           i
           (nth (indexes-of s end (inc i)) (dec n) nil)))))))

; =dotted-get
(defn dotted-get
  [data dotted]
  (if (and (string? dotted) (not= "." dotted))
    (reduce #(get % (keyword %2)) data (str/split dotted #"\."))
    (get data (keyword dotted))))

(defn replace-first-from*
  [s match replacement from]
  (if-let [i (index-of s match from)]
    (str
      (.substring s 0 i)
      replacement
      (.substring s (+ i (count match))))
    s
    )
  )

(defn replace-first-from
  [s match replacement from]
  (if (or (< from 0) (> from (count s)))
    s
    (str
      (.substring s 0 from)
      (str/replace-first (.substring s from) match replacement))
  )
  )
