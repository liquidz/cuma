(ns cuma.extension
  (:require
    cuma.extension.core
    cuma.extension.date))

(def ^:dynamic *extension-ns-regexp*
  #"^cuma\.extension\.")

; =find-extension-namespaces
(defn- find-extension-namespaces
  []
  (filter #(re-seq *extension-ns-regexp* (str (ns-name %))) (all-ns)))

; =extension-publics
(defn- extension-publics
  [ns-sym]
  (into {} (map #(let [{name :name, ename :extension-name} (meta %)]
                   [(keyword (if ename ename name)) %])
                (vals (ns-publics ns-sym)))))

; =collect-extension-functions
(defn- collect-extension-functions
  []
  (reduce
    #(merge % (extension-publics %2))
    {} (find-extension-namespaces)))

; =collect-extension-functions-memo
(def collect-extension-functions-memo
  (memoize collect-extension-functions))

