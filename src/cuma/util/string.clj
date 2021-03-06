(ns cuma.util.string
  (:require
    [clojure.string :as str]))

; =index-of
(defn index-of [^String s ^String target ^long from]
  (let [i (.indexOf s target from)]
    (if (not= -1 i) i)))

; =count-string
(defn count-string
  [s target-regexp]
  (count (re-seq target-regexp s)))

; =get-paired-section-index
(defn get-paired-section-index
  [s from]
  (let [len (count s)]
    (loop [from from, depth 0]
      (if-let [i (index-of s "@(" from)]
        (when (> len (+ i 5))
          (if (= (subs s (+ i 2) (+ i 5)) "end") ; check 3 chars "@(123"
            (if (= 1 depth)
              i
              (recur (+ i 4) (dec depth))) ; 4 => minimal sectin size "@(x)"
            (recur (+ i 4) (inc depth))))))))

; =dotted-get
(defn dotted-get
  [data dotted]
  (if (and (string? dotted) (not= "." dotted))
    (reduce #(get % (keyword %2)) data (str/split dotted #"\."))
    (get data (keyword dotted))))

