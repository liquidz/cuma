(ns cuma.core
  (:require
    [cuma.util.string :refer [index-of get-paired-index]]
    [cuma.extension   :refer [collect-extension-functions-memo]]
    [clojure.string   :as str]))

(defn- render-variable
  [s data]
  (str/replace
    s #"\$\(\s*(.+?)\s*\)"
    (fn [[all x]]
      (let [[a & b :as ls] (str/split x #"\s+")
            f    (get data (keyword (if-not (empty? b) a)))
            args (map #(get data (keyword %)) (if-not (empty? b) b [a]))]
        (if (> (count ls) 1)
          (if f (str (apply f data args)) all)
          (str (first args)))))))


(defn- parse-section
  [s data from]
  (if-let [body-start (index-of s ")" from)]
    (let [start-str   (str/trim (.substring s (+ 2 from) body-start))
          [f & args]  (str/split start-str #"\s")
          end-str     (str "@(/" f ")")]
      (if-let [body-end (get-paired-index s (str "@(" f) end-str from)]
        {:f    f
         :args args
         :body (str/triml (.substring s (inc body-start) body-end))
         :all  (.substring s from (+ body-end (count end-str)))}
        s))
    s))


(defn- render-section
  ([s data] (render-section s data 0))
  ([s data from]
   (if-let [sec-start (index-of s "@(" from)]
     (let [{:keys [f args body all]} (parse-section s data sec-start)
           f    (get data (keyword f))
           args (map #(get data (keyword %)) args)]
       (if f
         (let [res (apply f (concat (list data body) args))]
           (recur
             (str/replace-first s all res)
             data
             (+ sec-start (count res))))
         s))
     s)))


(defn render
  [s data]
  (let [m (merge {:render render}
                 (collect-extension-functions-memo)
                 data)]
    (-> s
      (render-section m)
      (render-variable m))))
