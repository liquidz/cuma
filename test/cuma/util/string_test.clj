(ns cuma.util.string-test
  (:require
    [cuma.util.string :refer :all]
    [midje.sweet      :refer :all]
    [clojure.string   :as str]))

;; count-string
(facts "count-string function should work fine."
  (fact "Num of string should be correct."
    (count-string "foo" #"o") => 2
    (count-string "o" #"o") => 1)

  (fact "If string is empty or target string is not exists, 0 should be returned".
    (count-string "" #"o") => 0
    (count-string "x" #"o") => 0))

;; get-paired-section-index
(facts "get-paired-section-index function should work fine."
  (fact "Paired string index should be found."
    (get-paired-section-index "@(x)@(end)"            , 0) => 4
    (get-paired-section-index "@(x) @(end)"           , 0) => 5
    (get-paired-section-index "@(x)@@(end)"           , 0) => 5
    (get-paired-section-index "@(x)@(end)@(end)"      , 0) => 4
    (get-paired-section-index "@(x)@(y)@(end)@(end)"  , 0) => 14
    (get-paired-section-index "@(x)__@(y)@(end)@(end)", 0) => 16
    (get-paired-section-index "@(x)@(y)@(end)__@(end)", 0) => 16
    (get-paired-section-index "@(x)@(y)@(end)@(end)"  , 1) => 8
    (get-paired-section-index "@(x)@(y)@(end)__@(end)", 1) => 8
    (get-paired-section-index "@(x)@(end)@(y)@(end)"  , 0) => 4
    (get-paired-section-index "@(x)@(y)@(end)@(z)@(end)@(end)", 0)  => 24
    (get-paired-section-index "@(x)@(y)@(end)@(z)@(end)@(end)", 1)  => 8
    (get-paired-section-index "@(x)@(y)@(end)@(z)@(end)@(end)", 14) => 18)

  (fact "If paired string is not exists, nil should be returned."
    (get-paired-section-index ""              , 0) => nil
    (get-paired-section-index "@("            , 0) => nil
    (get-paired-section-index "@(x)@(end)"    , 1) => nil
    (get-paired-section-index "@(x)@(end)"    , 2) => nil
    (get-paired-section-index "@(x)@(end)"    , 3) => nil
    (get-paired-section-index "@(x)"          , 0) => nil
    (get-paired-section-index "@(end)"        , 0) => nil
    (get-paired-section-index "@(x)@(y)@(end)", 0) => nil
    (get-paired-section-index "@(x)@(y)@(end" , 0) => nil))

;; dotted-get
(facts "dotted-get function should work fine."
  (let [data {:a {:b {:c 1} :d 2} :e 3 :. 4 nil 5}]
    (fact "Dotted name should be accessible."
      (dotted-get data "a.b.c") => 1
      (dotted-get data "a.b")   => {:c 1}
      (dotted-get data "a")     => (contains {:b {:c 1}} {:d 2})
      (dotted-get data "a.d")   => 2
      (dotted-get data "e")     => 3
      (dotted-get data ".")     => 4
      (dotted-get data nil)     => 5)

    (fact "Not existing dotted name should return nil."
      (dotted-get data "x")       => nil
      (dotted-get data "a.b.c.d") => nil
      (dotted-get data "")        => nil)))
