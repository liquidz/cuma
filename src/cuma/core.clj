(ns cuma.core
  "S-expression based micro template in clojure"
  (:require
    [cuma [replace   :refer [replace-sexp]]
          [transform :refer [transform-sexp]]
          [plugin    :refer [collect-plugin-functions-memo]]]
    [evalive.core    :refer [evil]]
    [clojure.string  :as    str]))

(defn- key-map->sym-map [m] (into {} (map (fn [[k v]] [(symbol (name k)) v]) m)))
(defn- escape-quote     [s] (str/replace s "\"" "\\\""))
(defn- unescape-quote   [s] (str/replace s "\\\"" "\""))

(defn- index-fn
  [& targets]
  (fn [s from-index]
    (let [indexes (remove neg? (map #(.indexOf s % from-index) targets))]
      (if-not (empty? indexes)
        (apply min indexes)))))

(defn- read*
  [s]
  (-> s
      escape-quote
      (as-> x (str "(list \"" x "\")"))
      (replace-sexp (index-fn "$(" "@(") (comp transform-sexp unescape-quote))
      read-string))

(defn render
  ([s] (render s {}))
  ([s data]
   (let [m (merge (collect-plugin-functions-memo) (key-map->sym-map data))]
     (->> (read* s)
          (evil m)
          flatten
          (str/join "")))))

