(ns haddash.core
  (:require [haddash.api :as had-api])
  (:require [haddash.util :as had-util])
  (:require [haddash.visualizer :as had-viz])
  (:require [clojure.stacktrace :as strace])
  (:gen-class))


(defn scale [factor n]
  (Math/ceil (* (/ 1 factor) n)))

(defn milli->sec [x]
  (Math/ceil (/ x 60)))

(defn block-with-padding [scaler ref-stamp detail]
  (let [left-pad (milli->sec (- (detail "t0-stamp") ref-stamp))]
    (had-viz/block-with-padding scaler left-pad detail)))

(defn visualize [conf n scaler]
  (try
    (let [details (had-api/top-n conf n)
          ref-stamp ((first details) "t0-stamp")]
      (clojure.string/join 
        "\n"
        (map (partial block-with-padding scaler ref-stamp) details)))
     (catch Exception e
       (strace/print-stack-trace e))))

(defn visualize->file 
  ([conf file n scale-factor]
    (let [scaler (partial scale scale-factor)]
      (spit file (visualize conf n scaler))))
  ([conf file n]
    (visualize->file conf file n 1)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
