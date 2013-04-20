(ns cuma.extension.core-test
  (:require
    [cuma.core      :refer [render]]
    [midje.sweet    :refer :all]
    [clojure.string :as str]))

;; if
(facts "if extension should work fine."
  (fact "`if` should work."
    (render "@(if flag)foo@(end)"  {})                    => ""
    (render "@(if flag)foo@(end)"  {:flag true})          => "foo"
    (render "@(if flag)foo@(end)"  {:flag false})         => ""
    (render "@(if flag)$(x)@(end)" {:flag true :x "foo"}) => "foo")

  (fact "Evaluated value should bind implicit variable."
    (render "@(if x)$(.)@(end)" {:x "foo"})      => "foo"
    (render "@(if m)$(n)@(end)" {:m {:n "foo"}}) => "foo"))

;; if-not
(fact "if-not extension should work fine."
  (render "@(if-not flag)foo@(end)"  {})                     => "foo"
  (render "@(if-not flag)foo@(end)"  {:flag true})           => ""
  (render "@(if-not flag)foo@(end)"  {:flag false})          => "foo"
  (render "@(if-not flag)$(x)@(end)" {:flag false :x "foo"}) => "foo")

;; for
(facts "for extension should work fine."
  (fact "`for` should work."
    (render "@(for arr)x@(end)"    {:arr [1 2 3]})                => "xxx"
    (render "@(for arr)$(.)@(end)" {:arr [1 2 3]})                => "123"
    (render "@(for arr)$(n)@(end)" {:arr [{:n 1} {:n 2} {:n 3}]}) => "123")

  (fact "Nested `for` should work."
    (render "@(for arr1)@(for arr2)$(a)$(b)@(end)@(end)"
            {:arr1 [{:a 1} {:a 2}] :arr2 [{:b 3} {:b 4}]})
    => "13142324")

  (fact "Non-sequential value should not be looped."
    (render "@(for x)$(.)@(end)" {})       => ""
    (render "@(for x)$(.)@(end)" {:x nil}) => ""))


;; include
(fact "include extension should work fine."
  (render "$(include base)" {}) => ""

  (render "hello $(include base)" {:base "$(x)" :x "world"})
  => "hello world"

  (render "$(x)$(include base)"   {:base "@(for arr)$(.)@(end)" :x "12" :arr [3 4]})
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

;; comment
(fact "comment extension should work fine."
  (render "@(comment)foo@(end)" {})                      => ""
  (render "@(comment)$(x)@(end)" {})                     => ""
  (render "@(comment)$(x)@(end)" {:x "foo"})             => ""
  (render "@(comment)@(if x)foo@(end)@(end)" {:x "foo"}) => "")

;; let
(fact "let extension should work fine."
  (render "@(let :x \"foo\")$(x)@(end)"       {})        => "foo"
  (render "@(let :x \"foo\")$(x)@(end)"       {:x "x"})  => "foo"
  (render "@(let :x 123)$(x)@(end)"           {})        => "123"
  (render "@(let :x 1 :y 2)$(x)-$(y)@(end)"   {})        => "1-2"
  (render "@(let :x \"f$(x)\")$(x)@(end)"     {:x "oo"}) => "foo"
  (render "@(let :x \"f$(x)$(y)\")$(x)@(end)" {:x "o" :y "o"}) => "foo"
  (render "@(let :x \"b$(x)$(y)\")$(x)@(end)" {:x "a" :y "r"}) => "bar"
  (render "@(let :x \"@(let :x 1)$(x)@(end)\")$(x)@(end)" {:x 0})       => "1"
  (render "@(for arr)@(let :x \"n$(.)\")$(x)@(end)@(end)" {:arr [1 2]}) => "n1n2")
