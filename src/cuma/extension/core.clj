(ns cuma.extension.core
  (:require [clojure.string :as str]))

(defn ^{:extension-name 'if}
  if* [data body arg]
  (if arg
    ((:render data) body (merge data (if (map? arg) arg {:. arg}))) ""))

(defn ^{:extension-name 'for}
  for* [data body arg]
  (if (sequential? arg)
    (->> (for [x arg :let [v (if (map? x) x {:. x})]]
           ((:render data) body (merge data v)))
         flatten
         (str/join ""))
    ""))

(defn include
  [data arg]
  ((:render data) arg data))

(defn escape
  "Escape string."
  [_ arg]
  (-> arg (str/replace #"&"  "&amp;")
          (str/replace #"\"" "&quot;")
          (str/replace #"<"  "&lt;")
          (str/replace #">"  "&gt;")))

