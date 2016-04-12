(ns haddash.util)

(def month-map (hash-map
 "Jan" "01"
 "Feb" "02"
 "Mar" "03"
 "Apr" "04"
 "May" "05"
 "Jun" "06"
 "Jul" "07"
 "Aug" "08"
 "Sep" "09"
 "Oct" "10"
 "Nov" "11"
 "Dec" "12"))


;; WARNING: THIS SHIT AIN'T THREAD SAFE!!

(defn str->date 
  ([datetime format]
    (let [tz (. java.util.TimeZone getTimeZone "GMT")
          sdf (java.text.SimpleDateFormat. format)]
      (. sdf setTimeZone tz)
      (.parse sdf datetime)))
  ([datetime]
    (str->date datetime "yyyy-MM-dd'T'HH:mm:ss'Z'")))

(defn date->timestamp [date]
  (let [c (. java.util.Calendar getInstance)]
    (. c setTime date)
    (/ (.getTimeInMillis c) 1000)))

(defn str->timestamp [datetime]
  (date->timestamp (str->date datetime)))

(defn iso-format [datetime]
  (let [parts (clojure.string/split datetime #"\s+")
        year (last parts)
        mon (month-map (nth parts 1))
        day (nth parts 2)
        time (nth parts 3)]
    (str year "-" mon "-" day "T" time "Z")))