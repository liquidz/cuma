(ns cuma.util.string-test
  (:require
    [cuma.util.string :refer :all]
    [midje.sweet      :refer :all]
    [clojure.string   :as str]))

;; indexes-of
(facts "indexes-of function should work fine."
  (let [f (fn [& args] (take-while (comp not nil?) (apply indexes-of args)))]
    (fact "All indexes should be appered."
      (f "hello" "l" 0) => [2 3]
      (f "hello" "l" 3) => [3]
      (f "hello" "l" 4) => []
      (f "hello" "x" 0) => []
      (f "hello" "x" 3) => [])

    (fact "Empty string should be handleable."
      (f "" "" 0)  => [0]
      (f "" "" 3)  => [0]
      (f "" "x" 0) => []
      (f "" "x" 3) => [])))

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
    (get-paired-section-index "@(x)@(y)@(end)__@(end)", 1) => 8)

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

;; get-paired-char-index
;(facts "get-paired-char-index function should work fine."
;  (fact "Paired character index should be found."
;    (get-paired-char-index "()" \( \) 0) => 1
;    (get-paired-char-index "(a)" \( \) 0) => 2
;    (get-paired-char-index "(a (b))" \( \) 0) => 6
;    (get-paired-char-index "(a (b))" \( \) 3) => 5
;    (get-paired-char-index "((a) b)" \( \) 0) => 6
;    (get-paired-char-index "(())" \( \) 0) => 3
;    (get-paired-char-index "(()())" \( \) 0) => 5
;    (get-paired-char-index "(()())" \( \) 1) => 2)
;
;  (fact "If paired character is not exists, nil should be returned."
;    (get-paired-char-index ""   , \( \) 0)   => nil
;    (get-paired-char-index "()" , \( \) 1) => nil
;    (get-paired-char-index "()" , \( \) 2) => nil
;    (get-paired-char-index "("  , \( \) 0)   => nil
;    (get-paired-char-index ")"  , \( \) 0)   => nil
;    (get-paired-char-index "(()", \( \) 0)   => nil))

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
