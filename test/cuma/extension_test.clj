(ns cuma.extension-test
  (:require [cuma [extension :refer :all]
                  [core      :refer [render]]]
            [clojure.test    :refer :all]
            [clojure.string  :as str]))

(deftest collect-extension-functions
  (let [m (#'cuma.extension/collect-extension-functions)]
    (is (contains? m 'include))
    (is (contains? m 'escape))
    (is (not (contains? m 'collect-extension-functions-memo)))
    (is (not (contains? m 'does-not-exists))))

  (binding [*extension-ns-regexp* #"^cuma\."]
    (require 'cuma.core)
    (let [m (#'cuma.extension/collect-extension-functions)]
      (is (contains? m 'include))
      (is (contains? m 'escape))
      (is (contains? m 'render))
      (is (contains? m 'collect-extension-functions-memo)))))


(deftest core-functions-test
  (testing "include"
    (are [x y] (= x y)
      "hello world" (render "hello $(include base)" {:base "$(x)" :x "world"})
      "1234"        (render "$(x)$(include base)"   {:base "@(for [x arr])$(x)@(/for)"
                                                     :x "12" :arr [3 4]})))

  (testing "escape"
    (are [x y] (= x y)
      "&lt;h1&gt;" (render "$(escape x)" {:x "<h1>"}))))
