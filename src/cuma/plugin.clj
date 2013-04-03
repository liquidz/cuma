(ns cuma.plugin
  (:require [cuma.plugin.core]))

(def ^:dynamic *plugin-ns-regexp*
  #"^cuma\.plugin\.")

(defn- find-plugin-namespaces
  []
  (filter #(re-seq *plugin-ns-regexp* (str (ns-name %))) (all-ns)))

(defn- collect-plugin-functions
  []
  (reduce
    #(merge % (ns-publics %2))
    {} (find-plugin-namespaces)))

(def collect-plugin-functions-memo
  (memoize collect-plugin-functions))

