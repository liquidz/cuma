(ns cuma.core
  (:require
    [cuma.util.string :refer [index-of dotted-get get-paired-section-index]]
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
      (let [[a & b] (read-string* x)
            b?   (seq b)
            f    (if b? (dotted-get data (str a)))
            args (map #(if (symbol? %) (dotted-get data (str %)) %) (if b? b [a]))
            res (if b?
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
    (let [start-str   (str/trim (subs s (inc from) (inc body-start)))
          [f & args]  (read-string* start-str)
          end-len     6] ; "@(end)" => 6
      (if-let [body-end (get-paired-section-index s from)]
        {:f    f
         :args args
         :body (str/replace (subs s (inc body-start) body-end) #"^[\r\n]+" "")
         :all  (subs s from (+ body-end end-len))}
        s))
    s))

; =render-section
(defn- render-section
  [s data from]
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
    s))

; =render
(defn render
  [s data]
  {:pre  [(string? s) (map? data)] :post [(string? %)]}
  (let [m (merge {:render render}
                 (collect-extension-functions-memo)
                 data)]
    (-> s
      (render-section m 0)
      (render-variable m))))

