(ns cuma.extension.layout
  (:require
    [clojure.java.io :as io]
    [filemodc.core :as fm])
  (:import
    [java.io FileNotFoundException]))

(def ^:private tmpl-cache (fm/init))

(defn- slurp-template
  [^java.io.File f & [default-value]]
  (try
    (slurp f)
    (catch FileNotFoundException e
      default-value)))

(defn- slurp-template*
  [^java.io.File f]
  (if (fm/modified? tmpl-cache f)
    (when-let [tmpl (slurp-template f)]
      (fm/register! tmpl-cache f :optional {:content tmpl})
      tmpl)
    (-> (fm/lookup tmpl-cache f) :optional :content)))

(defn- block
  [local-var data body block-name]
  (let [render  (:render data)
        content (render body data)]
    (swap! local-var assoc block-name content)
    ""))

(defn layout-file
  [data body filename]
  (let [render      (:render data)
        layout-body (slurp-template* (io/file filename))
        local-var   (atom {})
        data*       (merge {:block (partial block local-var)} data)
        content     (render body data*)]
    (if layout-body
      (render layout-body (merge data @local-var {:. content}))
      content)))

