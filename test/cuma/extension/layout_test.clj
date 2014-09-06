(ns cuma.extension.layout-test
  (:require
    [cuma.core    :refer [render]]
    [midje.sweet  :refer :all]
    [conjure.core :refer :all]))

(defn- test-layout
  [s]
  (str "@(layout-file \"" (gensym) ".tpl\")"
       s
       "@(end)"))

(defn- render-layout
  [s data]
  (render (test-layout s) data))

(defn slurp-stub
  [results]
  (let [a (atom results)]
    (fn [& _]
      (let [[x] @a]
        (swap! a rest)
        x))))

(facts "layout-file extension should work fine."
  (fact "file exist"
    (stubbing [slurp "foo$(.)"]
      (render-layout "bar" {}) => "foobar"))

  (fact "file does not exist"
    (render-layout "bar" {}) => "bar")

  (fact "variable in layout"
    (stubbing [slurp "$(x)$(.)"]
      (render-layout "$(y)" {:x "a" :y "b"}) => "ab"))

  (fact "no variable in layout"
    (stubbing [slurp "foo"]
      (render-layout "bar" {}) => "foo"))

  (fact "layout in layout"
    (stubbing [slurp (slurp-stub [(test-layout "y$(.)") "x$(.)"])]
      (render-layout "z" {}) => "xyz")
    (stubbing [slurp (slurp-stub [(test-layout "$(y)$(.)") "$(x)$(.)"])]
      (render-layout "$(z)" {:x "a" :y "b" :z "c"}) => "abc")))

(facts "defining block should work fine."
  (fact "normal use case"
    (stubbing [slurp "$(x)-$(y)"]
      (render-layout "@(block :x)a@(end)@(block :y)b@(end)" {}) => "a-b")
    (stubbing [slurp "$(.)$(x)"]
      (render-layout "@(block :x)world@(end)hello" {}) => "helloworld")
    (stubbing [slurp "$(.)"]
      (render-layout "@(block :x)foo@(end)bar" {}) => "bar")
    (stubbing [slurp "$(x)$(.)"]
      (render-layout "@(block :x)a@(end)$(x)" {:x "b"}) => "ab"))

  (fact "block scope should be closed in layout-file"
    (stubbing [slurp (slurp-stub ["$(x)" "$(x)$(y)"])]
      (render (str (test-layout "@(block :x)a@(end)")
                   (test-layout "@(block :y)b@(end)"))
              {}) => "ab"))

  (fact "block should work only in layout-file"
    (let [s "@(block :a)hello@(end)"]
      (render s {}) => s)))
