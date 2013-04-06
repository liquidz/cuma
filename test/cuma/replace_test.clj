(ns cuma.replace-test
  (:require [cuma.replace   :refer :all]
            [cuma.core]
            [clojure.test   :refer :all]
            [clojure.string :as str]))

(deftest replace-sexp-test
  (let [f (#'cuma.core/index-fn "(")
        c #(str "<" % ">")]
    (testing "simple"
      (are [x y] (= x (replace-sexp y f c))
        "<()>"       , "()"
        "<(a)>"      , "(a)"
        "<(a)><(b)>" , "(a)(b)"
        "<(a)> <(b)>", "(a) (b)"))

    (testing "nested"
      (are [x y] (= x (replace-sexp y f c))
        "<(())>"         , "(())"
        "<((a))>"        , "((a))"
        "<(a (b))>"      , "(a (b))"
        "<(a (b (c)))>"  , "(a (b (c)))"
        "<(a (b))> <(c)>", "(a (b)) (c)"
        "<(((a) b) c)>"  , "(((a) b) c)"))

    (testing "replace another sexp"
      (are [x y] (= x (replace-sexp y f (constantly "(x)")))
        "(x)" "(a)"
        "(x)" "(a (b))"))

    (testing "error"
      (are [x y] (= x (replace-sexp y f c))
        "("  , "("
        "(()", "(()"))))


