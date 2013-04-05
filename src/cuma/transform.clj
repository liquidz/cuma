(ns cuma.transform
  (:require
    [clojure.string :as str]))

(defn- transform-expression
  [sexp]
  (let [x (if (= -1 (.indexOf sexp " ")) "str ")]
    (str "\" (" x sexp ") \"")))

(defn- transform-syntax
  [sexp]
  (if (.startsWith sexp "/")
    (str "\")) \"")
    (str "\" (" sexp " (list \"")))

(defn transform-sexp
  [s]
  (let [[[_ type sexp]] (re-seq #"^(.)\((.+)\)$" s)
        sexp (str/trim sexp)]
    (case type
      "$" (transform-expression sexp)
      "@" (transform-syntax sexp))))

