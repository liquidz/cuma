(ns cuma.util.string-test
  (:require [cuma.util.string :refer :all]
            [clojure.test      :refer :all]
            [clojure.string    :as str]))

(deftest indexes-of-test
  (are [x y] (= x y)
    [2 3] (indexes-of "hello" "l")
    [3]   (indexes-of "hello" "l" 3)
    []    (indexes-of "hello" "l" 4)
    []    (indexes-of "hello" "x")
    []    (indexes-of "" "x")
    [0]   (indexes-of "" "")
    [0]   (indexes-of "" "" 3)
    [3]   (indexes-of "hello" "" 3)))

(deftest get-paired-index-test
  (testing "valid"
    (are [x y] (= x y)
      1 (get-paired-index "()"      , "(" ")")
      2 (get-paired-index "(a)"     , "(" ")")
      6 (get-paired-index "(a (b))" , "(" ")")
      6 (get-paired-index "(a (b)))", "(" ")")
      5 (get-paired-index "(a (b))" , "(" ")" 3)))

  (testing "invalid"
    (are [x y] (= x y)
      nil (get-paired-index ""   , "(" ")")
      nil (get-paired-index "()" , "(" ")" 2)
      nil (get-paired-index "("  , "(" ")")
      nil (get-paired-index "(()", "(" ")"))))

(deftest dotted-get-test
  (let [data {:a {:b {:c 1} :d 2} :e 3 :. 4}]
    (testing "valid"
      (are [x y] (= x (dotted-get data y))
        1 "a.b.c"
        2 "a.d"
        3 "e"
        4 "."))

    (testing "invalid"
      (are [x y] (= x (dotted-get data y))
        nil "x"
        nil "a.b.c.d"
        nil nil)
      (is (= 1 (dotted-get {nil 1} nil))))))
