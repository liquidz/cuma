(ns cuma.core-test
  (:require
    [midje.sweet    :refer :all]
    [cuma.core      :refer :all]
    [clojure.string :as str]))

;; custom variable converting function
(def upper #(if (string? %2) (str/upper-case %2) "err"))
(def plus  #(if (every? number? (rest %&)) (apply + (rest %&)) "err"))
;; custom section
(def section
  {:data  (fn [data _ key] (get data (keyword key)))
   :body  (fn [_ body & _] body)
   :arg1  (fn [_ _ & args] (first args))
   :arg2  (fn [_ _ & args] (second args))
   :quot  (fn [data body & _] (str "'" ((:render data) body data) "'"))
   :dquot (fn [data body & _] (str "\"" ((:render data) body data) "\""))})

;; render
(facts "render function should work fine."
  (fact "Argument type should be string and map."
    (render nil {}) => (throws AssertionError)
    (render "" nil) => (throws AssertionError))

  (fact "String should be rendered as string."
    (render ""    {}) => ""
    (render "foo" {}) => "foo"
    (render "$()" {}) => "$()"
    (render "$("  {}) => "$(")

  (fact "Variable should be replaced."
    (render "$(x)"     {:x "foo"})       => "foo"
    (render "$(x)"     {:x 1})           => "1"
    (render "$(x)$(y)" {:x "f" :y "oo"}) => "foo")

  (fact "Nil variable should be replaced as empty string."
    (render "$(x)"   {})       => ""
    (render "$(x)"   {:x nil}) => ""
    (render "[$(x)]" {})       => "[]"
    (render "[$(x)]" {:x nil}) => "[]")

  (fact "Custrom function should be applied to variable."
    (render "$(upper x)" {:upper upper :x "foo"}) => "FOO"
    (render "$(upper x)" {:upper upper :x ""})    => ""
    (render "$(upper x)" {:upper upper})          => "err"
    (render "$(+ x y)"   {:+ plus :x 1 :y 2})     => "3"
    (render "$(+ x y)"   {:+ plus})               => "err")

  (fact "Nil custom function should not be rendered."
    (render "$(f x)" {})     => "$(f x)"
    (render "$(f x)" {:x 1}) => "$(f x)")

  (fact "Dotted name variable should be replaced."
    (render "$(a.b)"     {:a {:b "foo"}})               => "foo"
    (render "$(a.b)"     {:a "foo"})                    => ""
    (render "$(f.g x)"   {:f {:g upper} :x "foo"})      => "FOO"
    (render "$(f.g x)"   {:f {:g upper}})               => "err"
    (render "$(f.g a.b)" {:f {:g upper} :a {:b "foo"}}) => "FOO"
    (render "$(f.g a.b)" {:f {:g upper} :a "foo"})      => "err")

  (fact "Section should be replaced."
    (let [{:keys [body data arg1 arg2]} section]
      (render "@(f)foo@(/f)"   {:f body})               => "foo"
      (render "@(f)@(/f)"      {:f body})               => ""
      (render "@(f x)_@(/f)"   {:f data :x "x"})        => "x"
      (render "@(f x y)_@(/f)" {:f arg1 :x "x" :y "y"}) => "x"
      (render "@(f x y)_@(/f)" {:f arg2 :x "x" :y "y"}) => "y"))

  (fact "Nested section should be replaced."
    (let [{f :quot, g :dquot} section]
      (render "@(f)x @(f)y@(/f)@(/f)"       {:f f})             => "'x 'y''"
      (render "@(f)$(x) @(f)$(x)@(/f)@(/f)" {:f f :x "a"})      => "'a 'a''"
      (render "@(f)x @(g)y@(/g)@(/f)"       {:f f :g g})        => "'x \"y\"'"
      (render "@(f)$(x) @(g)$(x)@(/g)@(/f)" {:f f :g g :x "a"}) => "'a \"a\"'"))

  (fact "Nil section should not be replaced."
    (render "@(f)foo@(/f)" {})       => "@(f)foo@(/f)"
    (render "@(f)foo@(/f)" {:f nil}) => "@(f)foo@(/f)")

  (fact "セクションに渡す値がnilの場合でも動作すること"
    (let [{:keys [body arg1 arg2]} section]
      (render "@(f x)foo@(/f)"   {:f body})        => "foo"
      (render "@(f x)foo@(/f)"   {:f arg1})        => ""
      (render "@(f x y)foo@(/f)" {:f arg2 :x "x"}) => ""
      (render "@(f x y)foo@(/f)" {:f arg2 :x "x"}) => ""))

  (fact "セクションでドット区切りの変数指定ができること"
    (let [{:keys [body arg1]} section]
      (render "@(f.g)foo@(/f.g)"     {:f {:g body}})               => "foo"
      (render "@(f a.b)foo@(/f)"     {:f arg1 :a {:b "bar"}})      => "bar"
      (render "@(f.g a.b)foo@(/f.g)" {:f {:g arg1} :a {:b "bar"}}) => "bar"))

  (fact "extension.coreの関数が使えること"
    (render "$(escape x)"      {:x "<h1>"}) => "&lt;h1&gt;"
    (render "@(if f)foo@(/if)" {:f true})   => "foo"
    (render "@(if f)foo@(/if)" {:f false})  => ""))

