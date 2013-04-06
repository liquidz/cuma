(ns cuma.transform-test
  (:require [cuma.transform :refer :all]
            [clojure.test   :refer :all]
            [clojure.string :as str]))

(deftest transform-sexp-test
  (testing "text"
    (are [x y] (= x (transform-sexp y))
      "foo" , "foo"
      "()"  , "()"
      "$("  , "$("
      "@("  , "@("
      "$()" , "$()"
      "$( )", "$( )"
      "@()" , "@()"
      "@( )", "@( )"))

  (testing "expression"
    (are [x y] (= x (transform-sexp y))
      "\" (str x) \""    , "$(x)"
      "\" (str x) \""    , "$( x )"
      "\" (str x) \""    , "$(str x)"
      "\" (a (b (c))) \"", "$(a (b (c)))"
      "\" (((a) b) c) \"", "$(((a) b) c)"))

  (testing "syntax"
    (are [x y] (= x (str/replace (transform-sexp y) #"[\r\n]" ""))
      "\" (x (list \""          , "@(x)"
      "\" (x (list \""          , "@( x )"
      "\" (if x (list \""       , "@(if x)"
      "\" (for [x arr] (list \"", "@(for [x arr])"
      "\")) \""                 , "@(/)"
      "\")) \""                 , "@(/foo)"
      "\")) \""                 , "@( /foo )")))


