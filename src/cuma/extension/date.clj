(ns cuma.extension.date
  (:require
    [clj-time.format :refer [formatter unparse]]
    [clojure.string  :as str]))

(defn date-format
  [data date fmt]
  (unparse (formatter fmt) date))

(defn date->xml-schema
  [data date]
  (date-format data date "yyyy-MM-dd'T'HH:mm:ss"))
