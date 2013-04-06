(ns cuma.plugin.core
  (:require [clojure.string :as str]))

(defn include
  [s]
  (let [render (-> 'cuma.core/render resolve)
        arg    (-> 'cuma.core/*current-arg* resolve var-get)]
    (render s arg)))

(defn escape
  "Escape string."
  [s]
  (-> s (str/replace #"&"  "&amp;")
        (str/replace #"\"" "&quot;")
        (str/replace #"<"  "&lt;")
        (str/replace #">"  "&gt;")))

