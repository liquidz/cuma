(ns cuma.extension.core-test
  (:require
    [cuma.core      :refer [render]]
    [midje.sweet    :refer :all]
    [clojure.string :as str]))

;; if
(facts "if extension should work fine."
  (fact "`if` should work."
    (render "@(if flag)foo@(/if)"  {})                    => ""
    (render "@(if flag)foo@(/if)"  {:flag true})          => "foo"
    (render "@(if flag)foo@(/if)"  {:flag false})         => ""
    (render "@(if flag)$(x)@(/if)" {:flag true :x "foo"}) => "foo")

  (fact "Evaluated value should bind implicit variable."
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
  (fact "`for` should work."
    (render "@(for arr)x@(/for)"    {:arr [1 2 3]})                => "xxx"
    (render "@(for arr)$(.)@(/for)" {:arr [1 2 3]})                => "123"
    (render "@(for arr)$(n)@(/for)" {:arr [{:n 1} {:n 2} {:n 3}]}) => "123")

  (fact "Nested `for` should work."
    (render "@(for arr1)@(for arr2)$(a)$(b)@(/for)@(/for)"
            {:arr1 [{:a 1} {:a 2}] :arr2 [{:b 3} {:b 4}]})
    => "13142324")

  (fact "Non-sequential value should not be looped."
    (render "@(for x)$(.)@(/for)" {})       => ""
    (render "@(for x)$(.)@(/for)" {:x nil}) => ""))


;; include
(fact "include extension should work fine."
  (render "$(include base)" {}) => ""

  (render "hello $(include base)" {:base "$(x)" :x "world"})
  => "hello world"

  (render "$(x)$(include base)"   {:base "@(for arr)$(.)@(/for)" :x "12" :arr [3 4]})
  => "1234")

;; raw
(fact "raw extension should work fine."
  (render "$(raw x)" {}) => ""
  (render "$(raw x)" {:x "<h1>"}) => "<h1>")

;; ->
(fact "-> extension should work fine."
  (let [f (fn [_ x] (str "foo " x))
        g (fn [_ x] (str "bar " x))]
    (render "$(-> x)" {:x "baz"})         => "baz"
    (render "$(-> x)" {:x "<h1>"})        => "&lt;h1&gt;"
    (render "$(-> x raw)" {:x "<h1>"})    => "<h1>"
    (render "$(-> x f)" {:f f :x "baz"})  => "foo baz"
    (render "$(-> x f)" {:f f :x "<h1>"}) => "foo &lt;h1&gt;"
    (render "$(-> x f g)" {:f f :g g :x "baz"}) => "bar foo baz"
    (render "$(-> x f raw)" {:f f :x "<h1>"})   => "foo <h1>"))
