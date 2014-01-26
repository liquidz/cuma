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

  (fact "Custom function should be applied to variable."
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

  (fact "In replacing variable, normal value should be handled as itself."
    (render "$(\"foo\")"   {})             => "foo"
    (render "$(:foo)"      {})             => ":foo"
    (render "$(123)"       {})             => "123"
    (render "$({1 1})"     {})             => "{1 1}"
    (render "$([1 2])"     {})             => "[1 2]"
    (render "$(f 1)"       {:f #(inc %2)}) => "2"
    (render "$(f \"a b\")" {:f upper})     => "A B")

  (fact "Section should be replaced."
    (let [{:keys [body data arg1 arg2 quot dquot]} section]
      (render "@(f)foo@(end)"   {:f body})               => "foo"
      (render "@(f)@(end)"      {:f body})               => ""
      (render "@(f x)_@(end)"   {:f data :x "x"})        => "x"
      (render "@(f x y)_@(end)" {:f arg1 :x "x" :y "y"}) => "x"
      (render "@(f x y)_@(end)" {:f arg2 :x "x" :y "y"}) => "y"
      (render "@(f)$(x)@(end)@(g)$(y)@(end)" {:f quot :g dquot :x "x" :y "y"}) => "'x'\"y\""))

  (fact "Nested section should be replaced."
    (let [{f :quot, g :dquot} section]
      (render "@(f)x @(f)y@(end)@(end)"                {:f f})             => "'x 'y''"
      (render "@(f)$(x) @(f)$(x)@(end)@(end)"          {:f f :x "a"})      => "'a 'a''"
      (render "@(f)x @(g)y@(end)@(end)"                {:f f :g g})        => "'x \"y\"'"
      (render "@(f)$(x) @(g)$(x)@(end)@(end)"          {:f f :g g :x "a"}) => "'a \"a\"'"
      (render "@(f)@(g)$(x)@(end)@(g)$(x)@(end)@(end)" {:f f :g g :x "a"}) => "'\"a\"\"a\"'"))

  (fact "Nil section should not be replaced."
    (render "@(f)foo@(end)" {})       => "@(f)foo@(end)"
    (render "@(f)foo@(end)" {:f nil}) => "@(f)foo@(end)")

  (fact "Section should work fine when nil variable passed."
    (let [{:keys [body arg1 arg2]} section]
      (render "@(f x)foo@(end)"   {:f body})        => "foo"
      (render "@(f x)foo@(end)"   {:f arg1})        => ""
      (render "@(f x y)foo@(end)" {:f arg2 :x "x"}) => ""
      (render "@(f x y)foo@(end)" {:f arg2 :x "x"}) => ""))

  (fact "Dotted name section should be replaced."
    (let [{:keys [body arg1]} section]
      (render "@(f.g)foo@(end)"     {:f {:g body}})               => "foo"
      (render "@(f a.b)foo@(end)"     {:f arg1 :a {:b "bar"}})    => "bar"
      (render "@(f.g a.b)foo@(end)" {:f {:g arg1} :a {:b "bar"}}) => "bar"))

  (fact "In replacing section, normal value should be handled as itself."
    (let [{:keys [body arg1 arg2]} section]
      (render "@(f \"foo\")bar@(end)" {:f arg1}) => "foo"
      (render "@(f 123)bar@(end)"     {:f arg1}) => "123"
      (render "@(f :foo)bar@(end)"    {:f arg1}) => ":foo"
      (render "@(f {1 1})bar@(end)"   {:f arg1}) => "{1 1}"
      (render "@(f [1 2])bar@(end)"   {:f arg1}) => "[1 2]"))

  (fact "Complex pattern should be rendered correctly."
    (render "$(a)-$(b)" {:a "$(b)" :b "c"})       => "$(b)-c"
    (render "$(a) @(if b)$(.)@(end)" {:a 1 :b 2}) => "1 2"
    (render "@(if b)$(.)@(end) $(a)" {:a 1 :b 2}) => "2 1")

  (fact "Escaping and unescaping should work correctly."
    (render "$(x)" {:x "<h1>"})                               => "&lt;h1&gt;"
    (render "$(raw x)" {:x "<h1>"})                           => "<h1>"
    (render "@(if x)$(.)@(end)" {:x "<h1>"})                  => "&lt;h1&gt;"
    (render "@(if x)$(raw .)@(end)" {:x "<h1>"})              => "<h1>"
    (render "$(f x)" {:x "h1" :f (fn [_ x] (str "<" x ">"))}) => "&lt;h1&gt;")

  (fact "Functions in extension.core should be accessible."
    (render "@(if f)foo@(end)" {:f true})   => "foo"
    (render "@(if f)foo@(end)" {:f false})  => ""))

