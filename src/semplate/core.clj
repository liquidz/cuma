(ns semplate.core
  (:require
    [evalive.core   :refer [evil]]
    [clojure.string :as    str]))

(defn- join-strs [arr] (str/join "" (flatten arr)))
(defn- remove-double-newline [s] (str/replace s #"[\r\n]{2,}" "\n"))
(defn- eval*
  [sexp data]
  (let [res (evil data sexp)]
    (if (sequential? res)
      (join-strs res) res)))

(defn key-map->sym-map
  [m]
  (into {} (map (fn [[k v]] [(symbol (name k)) v]) m)))

(defn read*
  [s]
  (-> (str/trim s)
    (str/replace #"\#"  , "__")
    (str/replace #"\""  , "\\\"")
    (str/replace #"__\(", "\"(")
    (str/replace #"\)__", ")\"")
    (str/replace #"__[\r\n]+[ \t]*"      , "(list \"")
    (str/replace #"[ \t]*[\r\n]+[ \t]*__", "\n\")")
    (str/replace #"\(([^ \t,()]+)\)"     ,  (fn [[_ variable]] (str "(str " variable ")")))
    (as-> x (str "(\"" x "\")"))
    read-string))

(defn render [s data]
  (let [m (key-map->sym-map data)]
    (->> (read* s)
         (map #(if (sequential? %) (eval* % m) %))
         join-strs
         remove-double-newline)))

