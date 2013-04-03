(ns cuma.core-test
  (:require [cuma.core      :refer :all]
            [clojure.test   :refer :all]
            [clojure.string :as str]))

(deftest render-test
  (testing "no variable"
    (are [x y] (= x y)
      ""    (render ""    {})
      "foo" (render "foo" {})
      "3"   (render "=(+ 1 2)=" {})

      ""    (render "")
      "foo" (render "foo")
      "3"   (render "=(+ 1 2)=")))

  (testing "simple variable"
    (are [x y] (= x y)
      "foo" (render "=(str x)="  {:x "foo"})
      "foo" (render "f=(str x)=" {:x "oo"})
      "foo" (render "=(str x)=o" {:x "fo"})))

  (testing "short simple variable"
    (are [x y] (= x y)
      "foo" (render "=(x)="  {:x "foo"})
      "foo" (render "f=(x)=" {:x "oo"})
      "foo" (render "=(x)=o" {:x "fo"})))

  (testing "condition"
    (are [x y] (= x y)
      "foo" (render "=(if (= 1 1) 'foo)="   {})
      "foo" (render "=(if flag 'foo 'bar)=" {:flag true})
      "bar" (render "=(if flag 'foo 'bar)=" {:flag false})))

  (testing "multiline condition"
    (are [x y] (= x (str/replace y #"\s" ""))
      "foo" (render "=(if (= 1 1) =\n foo \n=)="    {})

      "foo" (render "=(if flag =\n foo \n=)="       {:flag true})
      "foo" (render "=(if-not flag =\n foo \n=)="   {:flag false})

      "foo" (render "=(if flag =\n =(x)= \n=)="     {:flag true :x "foo"})
      "foo" (render "=(if-not flag =\n =(x)= \n=)=" {:flag false :x "foo"})))

  (testing "nested multiline condition"
    (are [x y] (= x (str/replace y #"\s" ""))
      "foobar" (render "=(if (= 1 1) =\n foo =(if (= 2 2) =\n bar \n= )= \n=)=" {})
      "foo"    (render "=(if (= 1 1) =\n foo =(if (= 2 3) =\n bar \n= )= \n=)=" {})
      ""       (render "=(if (= 1 2) =\n foo =(if (= 2 2) =\n bar \n= )= \n=)=" {})

      "foobar" (render "=(if flg1 =\n foo =(if flg2 =\n bar \n=)= \n=)=" {:flg1 true  :flg2 true})
      "foo"    (render "=(if flg1 =\n foo =(if flg2 =\n bar \n=)= \n=)=" {:flg1 true  :flg2 false})
      ""       (render "=(if flg1 =\n foo =(if flg2 =\n bar \n=)= \n=)=" {:flg1 false :flg2 true})
      ""       (render "=(if flg1 =\n foo =(if flg2 =\n bar \n=)= \n=)=" {:flg1 false :flg2 false})

      "foobar" (render "=(if flg1 =\n =(v1)= =(if flg2 =\n =(v2)= \n=)= \n=)=" {:flg1 true  :flg2 true :v1 "foo" :v2 "bar"})
      "foo"    (render "=(if flg1 =\n =(v1)= =(if flg2 =\n =(v2)= \n=)= \n=)=" {:flg1 true  :flg2 false :v1 "foo" :v2 "bar"})
      ""       (render "=(if flg1 =\n =(v1)= =(if flg2 =\n =(v2)= \n=)= \n=)=" {:flg1 false :flg2 true :v1 "foo" :v2 "bar"})
      ""       (render "=(if flg1 =\n =(v1)= =(if flg2 =\n =(v2)= \n=)= \n=)=" {:flg1 false :flg2 false :v1 "foo" :v2 "bar"})))

  (testing "loop"
    (are [x y] (= x y)
      "123" (render "=(for [x arr] x)=" {:arr [1 2 3]})))

  (testing "multiline loop"
    (are [x y] (= x (str/replace y #"\s" ""))
      "xxx" (render "=(for [x arr] =\n x \n=)="     {:arr [1 2 3]})
      "123" (render "=(for [x arr] =\n =(x)= \n=)=" {:arr [1 2 3]})))

  (testing "nested multiline loop"
    (are [x y] (= x (str/replace y #"\s" ""))
      "xyxyxyxy" (render "=(for [x arr1] =\n =(for [y arr2] =\n xy \n=)= \n=)="         {:arr1 [1 2] :arr2 [4 5]})
      "14152425" (render "=(for [x arr1] =\n =(for [y arr2] =\n =(x)==(y)= \n=)= \n=)=" {:arr1 [1 2] :arr2 [4 5]})))

  (testing "lambda"
    (are [x y] (= x y)
      "FOO" (render "=(f \"foo\")=" {:f #(.toUpperCase %)})))

  (testing "lambda for multiline"
    (are [x y] (= x (str/replace y #"\s" ""))
      "FOO" (render "=(f =\n foo \n=)=" {:f #(map (fn [s] (.toUpperCase s)) %)})))
  )
