(ns cuma.plugin-test
  (:require [cuma.plugin    :refer :all]
            [clojure.test   :refer :all]
            [clojure.string :as str]))

(deftest collect-plugin-functions
  (let [m (#'cuma.plugin/collect-plugin-functions)]
    (is (contains? m 'escape))
    (is (not (contains? m 'does-not-exists))))

  (binding [*plugin-ns-regexp* #"^cuma\."]
    (require 'cuma.core)
    (let [m (#'cuma.plugin/collect-plugin-functions)]
      (is (contains? m 'escape))
      (is (contains? m 'render))
      (is (contains? m 'collect-plugin-functions-memo)))))
