(ns cuma.extension-test
  (:require [cuma [extension :refer :all]
                    [core      :refer [render]]]
            [clojure.test      :refer :all]
            [clojure.string    :as str]))

(deftest collect-extension-functions
  (let [m (#'cuma.extension/collect-extension-functions)]
    (are [x y] (= x (contains? m y))
      true  :escape
      true  :if
      true  :for
      false :collect-extension-functions-memo
      false :does-not-exists))

  (binding [*extension-ns-regexp* #"^cuma\."]
    (require 'cuma.core)
    (let [m (#'cuma.extension/collect-extension-functions)]
      (are [x y] (= x (contains? m y))
        true  :escape
        true  :if
        true  :for
        true  :collect-extension-functions-memo
        false :does-not-exists))))


(deftest core-functions-test
  (testing "if"
    (are [x y] (= x y)
      "foo" (render "@(if flag)foo@(/if)" {:flag true})
      "foo" (render "@(if flag)$(x)@(/if)" {:flag true :x "foo"})))

  (testing "for"
    (are [x y] (= x y)
      "xxx"      (render "@(for arr)x@(/for)" {:arr [1 2 3]})
      "123"      (render "@(for arr)$(.)@(/for)" {:arr [1 2 3]})
      "123"      (render "@(for arr)$(n)@(/for)" {:arr [{:n 1} {:n 2} {:n 3}]})
      "13142324" (render "@(for arr1)@(for arr2)$(a)$(b)@(/for)@(/for)"
                         {:arr1 [{:a 1} {:a 2}] :arr2 [{:b 3} {:b 4}]})))

  (testing "include"
    (are [x y] (= x y)
      "hello world" (render "hello $(include base)" {:base "$(x)" :x "world"})
      "1234"        (render "$(x)$(include base)"   {:base "@(for arr)$(.)@(/for)"
                                                     :x "12" :arr [3 4]})))

  (testing "escape"
    (are [x y] (= x y)
      "&lt;h1&gt;" (render "$(escape x)" {:x "<h1>"}))))
