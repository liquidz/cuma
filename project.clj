(defproject cuma "0.1.0"
  :description "Extensible micro template engine for Clojure"
  :url         "https://github.com/liquidz/cuma"

  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 ; cuma.extension.date
                 [clj-time "0.8.0"]
                 ; cuma.extension.layout
                 [filemodc "0.0.2"]]

  :profiles {:dev {:global-vars {*warn-on-reflection* true}
                   :dependencies [[midje "1.6.3"  :exclusions [org.clojure/clojure]]
                                  [org.clojars.runa/conjure "2.1.3"]]}}
  :plugins [[lein-midje "3.1.3"]])
