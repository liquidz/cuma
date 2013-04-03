(ns cuma.core
  "S-expression based micro template in clojure"
  (:require
    [evalive.core   :refer [evil]]
    [clojure.string :as    str]))

(defn- key-map->sym-map
  [m]
  (into {} (map (fn [[k v]] [(symbol (name k)) v]) m)))

(defn- read*
  [s]
  (-> (str/trim s)
      (str/replace #"\"" , "\\\"")
      (str/replace #"=\(", "\"(")
      (str/replace #"\)=", ")\"")
      (str/replace #"=[\r\n]+[ \t]*"      , "(list \"")
      (str/replace #"[ \t]*[\r\n]+[ \t]*=", "\n\")")
      (str/replace #"\(([^ \t,()]+)\)"    ,  (fn [[_ variable]] (str "(str " variable ")")))
      (as-> s (str "(\"" s "\")"))
      read-string))

(defn render
  ([s] (render s {}))
  ([s data]
   (let [m (key-map->sym-map data)]
     (->> (read* s)
          (map #(if (sequential? %) (evil m %) %))
          flatten
          (str/join "")))))
