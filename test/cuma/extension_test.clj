(ns cuma.extension-test
  (:require
    [cuma [extension :refer :all]
          [core      :refer [render]]]
    [midje.sweet     :refer :all]
    [clojure.string  :as str]))

;; collect-extension-functions
(facts "collect-extension-functions function should work fine."
  (fact "extension.coreが読み込めること"
    (let [m (#'cuma.extension/collect-extension-functions)]
      (:escape m) => truthy
      (:if m)     => truthy
      (:for m)    => truthy
      (:dummy m)  => falsey
      (:collect-extension-functions-memo m) => falsey))

  (fact "extensionのnamespace指定が変えられること"
    (binding [*extension-ns-regexp* #"^cuma\."]
      (let [m (#'cuma.extension/collect-extension-functions)]
        (:escape m) => truthy
        (:if m)     => truthy
        (:for m)    => truthy
        (:dummy m)  => falsey
        (:collect-extension-functions-memo m) => truthy))))

;; if
(facts "if extension should work fine."
  (fact "条件分岐できること"
    (render "@(if flag)foo@(/if)"  {})                    => ""
    (render "@(if flag)foo@(/if)"  {:flag true})          => "foo"
    (render "@(if flag)foo@(/if)"  {:flag false})         => ""
    (render "@(if flag)$(x)@(/if)" {:flag true :x "foo"}) => "foo")

  (fact "評価した値が暗黙知にバインドされていること"
    (render "@(if x)$(.)@(/if)" {:x "foo"})      => "foo"
    (render "@(if m)$(n)@(/if)" {:m {:n "foo"}}) => "foo"))

;; if-not
(fact "if-not extension should work fine."
  (render "@(if-not flag)foo@(/if-not)"  {})                     => "foo"
  (render "@(if-not flag)foo@(/if-not)"  {:flag true})           => ""
  (render "@(if-not flag)foo@(/if-not)"  {:flag false})          => "foo"
  (render "@(if-not flag)$(x)@(/if-not)" {:flag false :x "foo"}) => "foo")

;; for
(facts "for extension should work fine."
  (fact "ループできること"
    (render "@(for arr)x@(/for)"    {:arr [1 2 3]})                => "xxx"
    (render "@(for arr)$(.)@(/for)" {:arr [1 2 3]})                => "123"
    (render "@(for arr)$(n)@(/for)" {:arr [{:n 1} {:n 2} {:n 3}]}) => "123")

  (fact "ネストしてループできること"
    (render "@(for arr1)@(for arr2)$(a)$(b)@(/for)@(/for)"
            {:arr1 [{:a 1} {:a 2}] :arr2 [{:b 3} {:b 4}]})
    => "13142324")

  (fact "シーケンス以外はループできないこと"
    (render "@(for x)$(.)@(/for)" {})       => ""
    (render "@(for x)$(.)@(/for)" {:x nil}) => ""))


;; include
(fact "include extension should work fine."
  (render "$(include base)" {}) => ""

  (render "hello $(include base)" {:base "$(x)" :x "world"})
  => "hello world"

  (render "$(x)$(include base)"   {:base "@(for arr)$(.)@(/for)" :x "12" :arr [3 4]})
  => "1234")

;; escape
(fact "escape extension should work fine."
  (render "$(escape x)" {}) => ""
  (render "$(escape x)" {:x "<h1>"}) => "&lt;h1&gt;")
