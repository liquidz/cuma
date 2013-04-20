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

;; get-paired-string-index
(facts "get-paired-string-index function should work fine."
  (fact "Paired character index should be found."
    (get-paired-string-index "()"      , "(" ")")   => 1
    (get-paired-string-index "(a)"     , "(" ")")   => 2
    (get-paired-string-index "(a (b))" , "(" ")")   => 6
    (get-paired-string-index "(a (b)))", "(" ")")   => 6
    (get-paired-string-index "(a (b))" , "(" ")" 3) => 5
    (get-paired-string-index "((a) b)" , "(" ")")   => 6)

  (fact "If paired character is not exists, nil should be returned."
    (get-paired-string-index ""   , "(" ")")   => nil
    (get-paired-string-index "()" , "(" ")" 1) => nil
    (get-paired-string-index "()" , "(" ")" 2) => nil
    (get-paired-string-index "("  , "(" ")")   => nil
    (get-paired-string-index ")"  , "(" ")")   => nil
    (get-paired-string-index "(()", "(" ")")   => nil)

  (fact "Paired string index should be found."
    (get-paired-string-index "<%%>"    , "<%" "%>")   => 2
    (get-paired-string-index "<% %>"   , "<%" "%>")   => 3
    (get-paired-string-index "<%%_%>"  , "<%" "%>")   => 4
    (get-paired-string-index "<%<%%>%>", "<%" "%>")   => 6
    (get-paired-string-index "<%<%%>%>", "<%" "%>" 1) => 4)

  (fact "If paired string is not exists, nil should be returned."
    (get-paired-string-index ""      , "<%" "%>")   => nil
    (get-paired-string-index "<%%>"  , "<%" "%>" 1) => nil
    (get-paired-string-index "<%%>"  , "<%" "%>" 2) => nil
    (get-paired-string-index "<%%>"  , "<%" "%>" 3) => nil
    (get-paired-string-index "<%"    , "<%" "%>")   => nil
    (get-paired-string-index "<%<%%>", "<%" "%>")   => nil))

;; get-paired-char-index
(facts "get-paired-char-index function should work fine."
  (fact "Paired character index should be found."
    (get-paired-char-index "()" \( \) 0) => 1
    (get-paired-char-index "(a)" \( \) 0) => 2
    (get-paired-char-index "(a (b))" \( \) 0) => 6
    (get-paired-char-index "(a (b))" \( \) 3) => 5
    (get-paired-char-index "((a) b)" \( \) 0) => 6
    (get-paired-char-index "(())" \( \) 0) => 3
    (get-paired-char-index "(()())" \( \) 0) => 5
    (get-paired-char-index "(()())" \( \) 1) => 2)

  (fact "If paired character is not exists, nil should be returned."
    (get-paired-char-index ""   , \( \) 0)   => nil
    (get-paired-char-index "()" , \( \) 1) => nil
    (get-paired-char-index "()" , \( \) 2) => nil
    (get-paired-char-index "("  , \( \) 0)   => nil
    (get-paired-char-index ")"  , \( \) 0)   => nil
    (get-paired-char-index "(()", \( \) 0)   => nil))

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

(facts "replace-first-from should work fine."
  (let [s "foofoo"]
    (fact "指定index以降の最初の文字列を置換できること"
      (replace-first-from s "f" "x" 0) => "xoofoo"
      (replace-first-from s "f" "x" 1) => "fooxoo"
      (replace-first-from s "f" "x" 9) => "foofoo"
      (replace-first-from s "fo" "" 0) => "ofoo"
      (replace-first-from s "fo" "" 1) => "fooo")

    (fact "存在しない文字列が指定された場合には置換されないこと"
      (replace-first-from s "z" "x" 0) => "foofoo")))
