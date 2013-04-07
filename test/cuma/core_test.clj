(ns cuma.core-test
  (:require
    [cuma.core :refer :all]
    [clojure.test :refer :all]
    [clojure.string :as str]))


(deftest render-variable-test
  (testing "no variable"
    (are [x y] (= x y)
      "foo"    (#'cuma.core/render-variable "foo" {})
      "$(x)"   (#'cuma.core/render-variable "$(x)" {})
      "$()"    (#'cuma.core/render-variable "$()" {})
      "$(x"    (#'cuma.core/render-variable "$(x" {:x "foo"})
      "$(f x)" (#'cuma.core/render-variable "$(f x)" {})
      "$(f x)" (#'cuma.core/render-variable "$(f x)" {:x "foo"})
      "$(f x)" (#'cuma.core/render-variable "$(f x)" {:f str/upper-case})))

  (testing "simple"
    (are [x y] (= x y)
      "foo"         (#'cuma.core/render-variable "$(x)" {:x "foo"})
      "1"           (#'cuma.core/render-variable "$(x)" {:x 1})
      "hello world" (#'cuma.core/render-variable "$(x) $(y)" {:x "hello" :y "world"})))

  (testing "function"
    (are [x y] (= x y)
      "FOO" (#'cuma.core/render-variable "$(upper x)" {:upper #(str/upper-case %2) :x "foo"})
      "3"   (#'cuma.core/render-variable "$(+ x y)"   {:+ (fn [_ & args] (apply + args)) :x 1 :y 2}))))


(deftest render-section-test
  (testing "no section"
    (are [x y] (= x y)
      "foo"               (#'cuma.core/render-section "foo" {})
      "@(x y) test @(/x)" (#'cuma.core/render-section "@(x y) test @(/x)" {})
      "@(x y) test @(/x)" (#'cuma.core/render-section "@(x y) test @(/x)" {:x (constantly "x")})
      "@(x y) test @(/x)" (#'cuma.core/render-section "@(x y) test @(/x)" {:y "foo"})))

  (testing "simple"
    (are [x y] (= x y)
      "bar" (#'cuma.core/render-section "@(foo x) bar @(/foo)" {:foo (fn [_ body _] body) :x "baz"})
      "baz" (#'cuma.core/render-section "@(foo x) bar @(/foo)" {:foo (fn [data _ _] (:x data)) :x "baz"})
      "baz" (#'cuma.core/render-section "@(foo x) bar @(/foo)" {:foo (fn [_ _ arg] arg) :x "baz"})
      "baz" (#'cuma.core/render-section "@(foo x y) bar @(/foo)" {:foo (fn [_ _ a1 a2] a1) :x "baz" :y "foo"})
      "foo" (#'cuma.core/render-section "@(foo x y) bar @(/foo)" {:foo (fn [_ _ a1 a2] a2) :x "baz" :y "foo"})))

  (testing "error"
    (are [x y] (= x y)
      "@(foo x"             (#'cuma.core/render-section "@(foo x" {})
      "@(foo x)"            (#'cuma.core/render-section "@(foo x)" {})
      "@(foo x) foo @(/fo)" (#'cuma.core/render-section "@(foo x) foo @(/fo)" {})
      "@(foo x) foo (/foo)" (#'cuma.core/render-section "@(foo x) foo (/foo)" {}))))


(deftest render-test
  (are [x y] (= x y)
    "foo"     (render "$(x)" {:x "foo"})
    "foo bar" (render "$(x) @(foo) bar @(/foo)" {:x "foo" :foo (fn [_ b] b)})))
