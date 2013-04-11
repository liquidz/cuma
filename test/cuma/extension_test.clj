(ns cuma.extension-test
  (:require [cuma [extension :refer :all]
                  [core      :refer [render]]]
            [clojure.test    :refer :all]
            [clojure.string  :as str]))

;; collect-extension-functions
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

;; if
(deftest if-test
  (testing "if"
    (are [x y] (= x y)
      ""    (render "@(if flag)foo@(/if)"  {})
      "foo" (render "@(if flag)foo@(/if)"  {:flag true})
      ""    (render "@(if flag)foo@(/if)"  {:flag false})
      "foo" (render "@(if flag)$(x)@(/if)" {:flag true :x "foo"})))

  (testing "if binding"
    (are [x y] (= x y)
      "foo" (render "@(if x)$(.)@(/if)" {:x "foo"})
      "foo" (render "@(if m)$(n)@(/if)" {:m {:n "foo"}}))))

;; if-not
(deftest if-not-test
  (testing "if-not"
    (are [x y] (= x y)
      "foo" (render "@(if-not flag)foo@(/if-not)"  {})
      ""    (render "@(if-not flag)foo@(/if-not)"  {:flag true})
      "foo" (render "@(if-not flag)foo@(/if-not)"  {:flag false})
      "foo" (render "@(if-not flag)$(x)@(/if-not)" {:flag false :x "foo"}))))

;; for
(deftest for-test
  (testing "for"
    (are [x y] (= x y)
      ""         (render "@(for x)$(.)@(/for)" {})
      ""         (render "@(for x)$(.)@(/for)" {:x nil})
      "xxx"      (render "@(for arr)x@(/for)" {:arr [1 2 3]})
      "123"      (render "@(for arr)$(.)@(/for)" {:arr [1 2 3]})
      "123"      (render "@(for arr)$(n)@(/for)" {:arr [{:n 1} {:n 2} {:n 3}]})))

  (testing "nested for"
    (are [x y] (= x y)
      "13142324" (render "@(for arr1)@(for arr2)$(a)$(b)@(/for)@(/for)"
                         {:arr1 [{:a 1} {:a 2}] :arr2 [{:b 3} {:b 4}]}))))


;; include
(deftest include-test
  (testing "include"
    (are [x y] (= x y)
      "hello world" (render "hello $(include base)" {:base "$(x)" :x "world"})
      "1234"        (render "$(x)$(include base)"   {:base "@(for arr)$(.)@(/for)"
                                                     :x "12" :arr [3 4]}))))

;; escape
(deftest escape-test
  (testing "escape"
    (are [x y] (= x y)
      "&lt;h1&gt;" (render "$(escape x)" {:x "<h1>"}))))
