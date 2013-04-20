(ns cuma.core
  (:require
    [cuma.util.string :refer [index-of get-paired-index dotted-get]]
    [cuma.extension   :refer [collect-extension-functions-memo]]
    [clojure.string   :as    str]))

(def read-string* (memoize read-string))

; =escape
(defn escape
  "Escape string."
  [s]
  (if (string? s)
    (-> s (str/replace #"&"  "&amp;")
            (str/replace #"\"" "&quot;")
            (str/replace #"<"  "&lt;")
            (str/replace #">"  "&gt;"))))

; =render-variable
(defn- render-variable
  [s data]
  (str/replace
    s #"\$(\(\s*.+?\s*\))"
    (fn [[all x]]
      (let [[a & b :as ls] (read-string* x)
            f    (dotted-get data (str (if-not (empty? b) a)))
            args (map #(if (symbol? %) (dotted-get data (str %)) %) (if (empty? b) [a] b))
            res (if (> (count ls) 1)
                  (if f (apply f data args) all)
                  (first args))]
        (if (and (map? res) (contains? res :body))
          (if (:raw? res)
            (-> res :body str)
            (-> res :body str escape))
          (-> res str escape))))))

; =parse-section
(defn- parse-section
  [s data from]
  (if-let [body-start (index-of s ")" from)]
    (let [start-str   (str/trim (.substring s (inc from) (inc body-start)))
          [f & args]  (read-string* start-str)
          end-str     "@(end)"
          end-len     6]
      (if-let [body-end (get-paired-index s "@(" end-str from)]
        {:f    f
         :args args
         :body (str/replace (.substring s (inc body-start) body-end) #"^[\r\n]+" "")
         :all  (.substring s from (+ body-end end-len))}
        s))
    s))

; =render-section
(defn- render-section
  ([s data] (render-section s data 0))
  ([s data from]
   (if-let [sec-start (index-of s "@(" from)]
     (let [{:keys [f args body all]} (parse-section s data sec-start)
           f    (dotted-get data (str f))
           args (map #(if (symbol? %) (dotted-get data (str %)) %) args)]
       (if f
         (let [res (str (apply f (concat (list data body) args)))]
           (recur
             (str/replace-first s all res)
             data
             (+ sec-start (count res))))
         s))
     s)))

; =render
(defn render
  [s data]
  {:pre  [(string? s) (map? data)] :post [(string? %)]}
  (let [m (merge {:render render}
                 (collect-extension-functions-memo)
                 data)]
    (-> s
      (render-section m)
      (render-variable m))))
