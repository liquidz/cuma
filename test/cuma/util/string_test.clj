(ns cuma.util.string-test
  (:require
    [cuma.util.string :refer :all]
    [midje.sweet      :refer :all]
    [clojure.string   :as str]))

;; indexes-of
(facts "indexes-of function should work fine."
  (fact "All indexes should be appered."
    (indexes-of "hello" "l")   => [2 3]
    (indexes-of "hello" "l" 3) => [3]
    (indexes-of "hello" "l" 4) => []
    (indexes-of "hello" "x")   => []
    (indexes-of "hello" "x" 3) => [])

  (fact "Empty string should be handleable."
    (indexes-of "" "")    => [0]
    (indexes-of "" "" 3)  => [0]
    (indexes-of "" "x")   => []
    (indexes-of "" "x" 3) => []))

;; get-paired-index
(facts "get-paired-index function should work fine."
  (fact "Paired character index should be found."
    (get-paired-index "()"      , "(" ")")   => 1
    (get-paired-index "(a)"     , "(" ")")   => 2
    (get-paired-index "(a (b))" , "(" ")")   => 6
    (get-paired-index "(a (b)))", "(" ")")   => 6
    (get-paired-index "(a (b))" , "(" ")" 3) => 5
    (get-paired-index "((a) b)" , "(" ")")   => 6)

  (fact "If paired character is not exists, nil should be returned."
    (get-paired-index ""   , "(" ")")   => nil
    (get-paired-index "()" , "(" ")" 1) => nil
    (get-paired-index "()" , "(" ")" 2) => nil
    (get-paired-index "("  , "(" ")")   => nil
    (get-paired-index ")"  , "(" ")")   => nil
    (get-paired-index "(()", "(" ")")   => nil)

  (fact "Paired string index should be found."
    (get-paired-index "<%%>"    , "<%" "%>")   => 2
    (get-paired-index "<% %>"   , "<%" "%>")   => 3
    (get-paired-index "<%%_%>"  , "<%" "%>")   => 4
    (get-paired-index "<%<%%>%>", "<%" "%>")   => 6
    (get-paired-index "<%<%%>%>", "<%" "%>" 1) => 4)

  (fact "If paired string is not exists, nil should be returned."
    (get-paired-index ""      , "<%" "%>")   => nil
    (get-paired-index "<%%>"  , "<%" "%>" 1) => nil
    (get-paired-index "<%%>"  , "<%" "%>" 2) => nil
    (get-paired-index "<%%>"  , "<%" "%>" 3) => nil
    (get-paired-index "<%"    , "<%" "%>")   => nil
    (get-paired-index "<%<%%>", "<%" "%>")   => nil))

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
