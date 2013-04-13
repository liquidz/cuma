(defproject cuma "0.0.1-SNAPSHOT"
  :description "S-expression based micro template in clojure"
  :url         "https://github.com/liquidz/cuma"

  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"  :exclusions [org.clojure/clojure]]]}}
  :plugins [[lein-midje "3.0.0"]]

  :main cuma.core)
