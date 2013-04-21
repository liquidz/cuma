(ns cuma.extension.date-test
  (:require
    [cuma.core      :refer [render]]
    [clj-time.core  :refer [date-time]]
    [midje.sweet    :refer :all]
    [clojure.string :as str]))

(facts "date extension should work fine."
  (let [d (date-time 2000 1 2 3 4 5)]
    (fact "Joda date should be converted by specified format."
      (render "$(date-format d \"yyyy\")" {:d d}) => "2000"
      (render "$(date-format d \"MM\")" {:d d}) => "01"
      (render "$(date-format d \"dd\")" {:d d}) => "02"
      (render "$(date-format d \"HH\")" {:d d}) => "03"
      (render "$(date-format d \"mm\")" {:d d}) => "04"
      (render "$(date-format d \"ss\")" {:d d}) => "05")))





