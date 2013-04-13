(ns cuma.extension-test
  (:require
    [cuma.extension :refer :all]
    [midje.sweet    :refer :all]
    [clojure.string :as str]))

;; collect-extension-functions
(facts "collect-extension-functions function should work fine."
  (fact "Extension.core should be loaded."
    (let [m (#'cuma.extension/collect-extension-functions)]
      (:escape m) => truthy
      (:if m)     => truthy
      (:for m)    => truthy
      (:dummy m)  => falsey
      (:collect-extension-functions-memo m) => falsey))

  (fact "Extension namespace should be changeable."
    (binding [*extension-ns-regexp* #"^cuma\."]
      (let [m (#'cuma.extension/collect-extension-functions)]
        (:escape m) => truthy
        (:if m)     => truthy
        (:for m)    => truthy
        (:dummy m)  => falsey
        (:collect-extension-functions-memo m) => truthy))))
