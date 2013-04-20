(ns cuma.extension.core
  (:require [clojure.string :as str]))

; =if
(defn ^{:extension-name 'if}
  if* [data body arg]
  (if arg
    (if (map? arg)
      ((:render data) body (merge data arg))
      ((:render data) body (assoc data :. arg)))))

; =if-not
(defn ^{:extension-name 'if-not}
  if-not* [data body arg]
  (if-not arg
    (if (map? arg)
      ((:render data) body (merge data arg))
      ((:render data) body (assoc data :. arg)))))

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
  (if (string? arg)
    ((:render data) arg data)))

; =raw
(defn raw
  [_ arg]
  {:raw? true :body arg})

; =->
(defn ^{:extension-name '->}
  arrow*
  [data arg & fns]
  (reduce (fn [res f] (f data res)) arg fns))

; =comment
(defn ^{:extension-name 'comment}
  comment* [& _] "")

; =let
(defn ^{:extension-name 'let}
  let* [{render :render :as data} body & args]
  (->> args
       (partition 2)
       (map (fn [[k v]] [k (if (string? v) (render v data) v)]))
       (into {})
       (merge data)
       (render body)))
