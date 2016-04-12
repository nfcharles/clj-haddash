(defproject haddash "0.1.0-SNAPSHOT"
  :description "Lightweight hadoop dashboard"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "2.0.0"]]
  :repositories [["releases" "file:////home/ncharles/development/clojure/repos/local"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :main haddash.client
  :aot [haddash.client])