(ns cuma.extension
  (:require [cuma.extension.core]))

(def ^:dynamic *extension-ns-regexp*
  #"^cuma\.extension\.")

(defn- find-extension-namespaces
  []
  (filter #(re-seq *extension-ns-regexp* (str (ns-name %))) (all-ns)))

(defn- collect-extension-functions
  []
  (reduce
    #(merge % (ns-publics %2))
    {} (find-extension-namespaces)))

(def collect-extension-functions-memo
  (memoize collect-extension-functions))

