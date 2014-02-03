(defproject cuma "0.0.6"
  :description "Extensible micro template engine for Clojure"
  :url         "https://github.com/liquidz/cuma"

  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 ; cuma.extension.date
                 [clj-time "0.6.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.0"  :exclusions [org.clojure/clojure]]]}}
  :plugins [[lein-midje "3.1.3"]]

  ;:main cuma.core
  )
