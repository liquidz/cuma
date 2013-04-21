(ns cuma.util.string
  (:require
    [clojure.string :as str]))

; =index-of
(defn index-of [s target from]
  (let [i (.indexOf s target from)]
    (if (not= -1 i) i)))

; =indexes-of
(defn indexes-of
  [s target from]
  (if (str/blank? target)
    [(min (count s) from)]
    (->> (iterate
           #(if % (if-let [i (index-of s target (second %))]
                    [i (inc i)]))
           [-1 from])
         rest
         (map first))))

; =count-string
(defn count-string
  [s target-regexp]
  (count (re-seq target-regexp s)))

; =get-paired-section-index
(defn get-paired-section-index
  [s from]
  (if-let [i (index-of s "@(end)" from)]
    (if (> i from)
      (let [n (dec (count-string (.substring s from i) #"@\("))]
        (cond
          (neg? n)  nil
          (zero? n) i
          :else     (nth (indexes-of s "@(end)" i) n nil))))))

; =get-paired-char-index
;(defn get-paired-char-index
;  [s start end from]
;   (let [len (count s)]
;     (loop [i from, level 0, start? false]
;       (cond
;         (and start? (zero? level)) (dec i)
;         (>= i len) nil
;         :else (let [c (.charAt s i), i* (inc i)]
;                 (cond
;                   (= start c) (recur i* (inc level) true)
;                   (= end c)   (recur i* (dec level) start?)
;                   :else       (recur i* level start?)))))))

; =dotted-get
(defn dotted-get
  [data dotted]
  (if (and (string? dotted) (not= "." dotted))
    (reduce #(get % (keyword %2)) data (str/split dotted #"\."))
    (get data (keyword dotted))))

