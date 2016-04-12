(ns haddash.client
  (:require [haddash.core :as had-core])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (let [conf {:host (nth args 0) :port (nth args 1)}
        n (read-string (nth args 2))
        scaler (partial had-core/scale (read-string (nth args 3)))]
  (println (had-core/visualize conf n scaler))))
