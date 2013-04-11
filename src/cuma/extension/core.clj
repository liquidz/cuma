(ns cuma.extension.core
  (:require [clojure.string :as str]))

; =if
(defn ^{:extension-name 'if}
  if* [data body arg]
  (if arg
    ((:render data) body (merge data (if (map? arg) arg {:. arg}))) ""))

; =if-not
(defn ^{:extension-name 'if-not}
  if-not* [data body arg]
  (if-not arg
    ((:render data) body (merge data (if (map? arg) arg {:. arg}))) ""))

; =for
(defn ^{:extension-name 'for}
  for* [data body arg]
  (if (sequential? arg)
    (->> (for [x arg :let [v (if (map? x) x {:. x})]]
           ((:render data) body (merge data v)))
         flatten
         (str/join ""))
    ""))

; =include
(defn include
  [data arg]
  ((:render data) arg data))

; =escape
(defn escape
  "Escape string."
  [_ arg]
  (-> arg (str/replace #"&"  "&amp;")
          (str/replace #"\"" "&quot;")
          (str/replace #"<"  "&lt;")
          (str/replace #">"  "&gt;")))

