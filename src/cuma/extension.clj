(ns cuma.extension
  (:require [cuma.extension.core]))

(def ^:dynamic *extension-ns-regexp*
  #"^cuma\.extension\.")

(defn- find-extension-namespaces
  []
  (filter #(re-seq *extension-ns-regexp* (str (ns-name %))) (all-ns)))


(defn- extension-publics
  [ns-sym]
  (into {} (map #(let [{name :name, ename :extension-name} (meta %)]
                   [(keyword (if ename ename name)) %])
                (vals (ns-publics ns-sym)))))

(defn- collect-extension-functions
  []
  (reduce
    #(merge % (extension-publics %2))
    {} (find-extension-namespaces)))

(def collect-extension-functions-memo
  (memoize collect-extension-functions))

