(ns haddash.visualizer)

(defn block-tag [detail]
 (format "(%d %s)" 
   (detail "duration")
   (subs (detail "user") 0 3)))

(defn gen-block [block-len anchor]
  (str "|" (apply str (repeat block-len "-")) anchor))

(defn block-with-padding [scaler pad-len detail]
  (let [id (detail "id")
        pad (apply str (repeat (scaler pad-len) " "))
        anchor (if (= (detail "status") "Running") ">" "|")
        block (gen-block (scaler (detail "duration")) anchor)
        tag (block-tag detail)]
    (format "%s%10s: %s%s" id tag pad block)))