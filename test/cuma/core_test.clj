(ns cuma.core-test
  (:require
    [cuma.core :refer :all]
    [clojure.test :refer :all]
    [clojure.string :as str]))


(deftest render-variable-test
  (testing "no variable"
    (are [x y] (= x y)
      "foo"    (#'cuma.core/render-variable "foo" {})
      ""       (#'cuma.core/render-variable "$(x)" {})
      "$()"    (#'cuma.core/render-variable "$()" {})
      "$(x"    (#'cuma.core/render-variable "$(x" {:x "foo"})
      "$(f x)" (#'cuma.core/render-variable "$(f x)" {})
      "$(f x)" (#'cuma.core/render-variable "$(f x)" {:x "foo"})))

  (testing "simple"
    (are [x y] (= x y)
      "foo"         (#'cuma.core/render-variable "$(x)" {:x "foo"})
      "1"           (#'cuma.core/render-variable "$(x)" {:x 1})
      "hello world" (#'cuma.core/render-variable "$(x) $(y)" {:x "hello" :y "world"})))

  (testing "dotted"
    (are [x y] (= x y)
      "foo" (#'cuma.core/render-variable "$(x.y)" {:x {:y "foo"}})
      ""    (#'cuma.core/render-variable "$(x.z)" {:x {:y "foo"}})))

  (testing "no function"
    (are [x y] (= x y)
      "$(upper x)" (#'cuma.core/render-variable "$(upper x)" {})
      "$(upper x)" (#'cuma.core/render-variable "$(upper x)" {:x "foo"})))

  (testing "function"
    (let [upper #(if (string? %2) (str/upper-case %2))
          plus  (fn [_ & args] (if (every? number? args) (apply + args)))]
      (are [x y] (= x y)
        "FOO" (#'cuma.core/render-variable "$(upper x)" {:upper upper :x "foo"})
        "FOO" (#'cuma.core/render-variable "$(upper x.y)" {:upper upper :x {:y "foo"}})
        "FOO" (#'cuma.core/render-variable "$(f.upper x)" {:f {:upper upper} :x "foo"})
        ""    (#'cuma.core/render-variable "$(upper x)" {:upper upper})
        "3"   (#'cuma.core/render-variable "$(+ x y)"   {:+ plus :x 1 :y 2})
        ""    (#'cuma.core/render-variable "$(+ x y)"   {:+ plus})))))

(deftest render-section-test
  (testing "no section"
    (are [x y] (= x y)
      "foo"               (#'cuma.core/render-section "foo" {})
      "@(x y) test @(/x)" (#'cuma.core/render-section "@(x y) test @(/x)" {})
      "@(x y) test @(/x)" (#'cuma.core/render-section "@(x y) test @(/x)" {:y "foo"})))

  (testing "simple"
    (are [x y] (= x y)
      "bar" (#'cuma.core/render-section "@(foo x)bar@(/foo)" {:foo (fn [_ body _] body) :x "baz"})
      "baz" (#'cuma.core/render-section "@(foo x)bar@(/foo)" {:foo (fn [data _ _] (:x data)) :x "baz"})
      "baz" (#'cuma.core/render-section "@(foo x)bar@(/foo)" {:foo (fn [_ _ arg] arg) :x "baz"})
      "baz" (#'cuma.core/render-section "@(foo x y)bar@(/foo)" {:foo (fn [_ _ a1 a2] a1) :x "baz" :y "foo"})
      "foo" (#'cuma.core/render-section "@(foo x y)bar@(/foo)" {:foo (fn [_ _ a1 a2] a2) :x "baz" :y "foo"})

      "foo" (#'cuma.core/render-section "@(x y)bar@(/x)" {:x (constantly "foo")})
      "foo" (#'cuma.core/render-section "@(x y)bar@(/x)" {:x #(if (nil? %3) "foo" %2)})
      "bar" (#'cuma.core/render-section "@(x y)bar@(/x)" {:x #(if (nil? %3) "foo" %2) :y 1})))

  (testing "dotted"
    (let [f (fn [data body arg] (str body " " arg))]
      (are [x y] (= x y)
        "hello world" (#'cuma.core/render-section "@(f.foo x)hello@(/f.foo)" {:f {:foo f} :x "world"})
        "hello world" (#'cuma.core/render-section "@(foo x.y)hello@(/foo)" {:foo f :x {:y "world"}})
        "hello world" (#'cuma.core/render-section "@(f.foo x.y)hello@(/f.foo)" {:f {:foo f} :x {:y "world"}}))))

  (testing "nested"
    (are [x y] (= x y)
      "[a[b]c]" (#'cuma.core/render-section "@(x)a@(x)b@(/x)c@(/x)"
                    {:x (fn [data body] (str "[" ((:render data) body data) "]"))
                     :render render })
      "[a[b<c>]d]" (#'cuma.core/render-section "@(x)a@(x)b@(y)c@(/y)@(/x)d@(/x)"
                       {:x (fn [data body] (str "[" ((:render data) body data) "]"))
                        :y (fn [_ body] (str "<" body ">"))
                        :render render })))

  (testing "error"
    (are [x y] (= x y)
      "@(foo x"             (#'cuma.core/render-section "@(foo x" {})
      "@(foo x)"            (#'cuma.core/render-section "@(foo x)" {})
      "@(foo x) foo @(/fo)" (#'cuma.core/render-section "@(foo x) foo @(/fo)" {})
      "@(foo x) foo (/foo)" (#'cuma.core/render-section "@(foo x) foo (/foo)" {}))))


(deftest render-test
  (testing "valid"
    (are [x y] (= x y)
      "foo"     (render "$(x)" {:x "foo"})
      "foo bar" (render "$(x) @(foo)bar@(/foo)" {:x "foo" :foo (fn [_ b] b)})))

  (testing "invalid"
    (are [x y] (= x y)
      ""         (render "$(x)" {})
      "$(f x)"   (render "$(f x)" {})
      "[]"       (render "[$(x)]" {})
      "[$(f x)]" (render "[$(f x)]" {}))))
