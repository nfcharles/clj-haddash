(ns haddash.api
  (:require [clj-http.client :as client])
  (:require [haddash.util :as had-util])
  (:gen-class))

(def run-fields 
   [["User"        "user"] 
    ["Job Name"    "job_name"]
    ["Submit Host" "host"] 
    ["Status"      "status"] 
    ["Started at"  "t0"]
    ["Running for" "duration"]])

(def comp-fields 
   [["User"        "user"] 
    ["Job Name"    "job_name"]
    ["Submit Host" "host"] 
    ["Status"      "status"] 
    ["Started at"  "t0"]
    ["Finished at" "t1"]
    ["Finished in" "duration"]]) 

(def fail-fields 
   [["User"        "user"] 
    ["Job Name"    "job_name"]
    ["Submit Host" "host"] 
    ["Status"      "status"] 
    ["Started at"  "t0"]
    ["Failed at"   "t1"]
    ["Failed in"   "duration"]])

(def job-fields-map (hash-map
  "Running"   run-fields
  "Succeeded" comp-fields
  "Failed"    fail-fields))

;;;
;;; JOB DETAILS
;;;

(defn field-value [name txt]
  ;; Parse field value from job details html
  (if-let [match (re-find (re-pattern (format "<b>%s:</b>(.+)<br>" name)) txt)]
    (clojure.string/trim (nth match 1))))

(defn details [in-fields txt]
  ;; Return hash of field name -> value mappings parsed from job details html
  (loop [fields in-fields
         acc {}]
    (if-let [[name tag] (first fields)]
      (recur (rest fields) (assoc acc tag (field-value name txt)))
      acc)))

(defn split-time [in]
  ;; Parse time txt into sematic components, e.g.
  ;;   "23mins" -> (24, mins)
  (map read-string (rest (re-find #"([\d\.]+)(\w+)" in))))

(defn parse-duration [duration]
  ;; Deserialize job duration message into minutes
  (loop [time-parts (clojure.string/split duration, #",")
         acc 0]
    (if-let [part (first time-parts)]
      (let [[v k] (split-time part)]
        (recur (rest time-parts) 
               (+ acc (condp = k
                        (symbol "hrs")  (* v 60)
                        (symbol "mins") v
                        (symbol "sec")  1
                        -1))))
      acc)))

(defn job-details-url [conf job-id]
  (let [url (format "http://%s:%s/jobdetails.jsp?jobid=%s" (conf :host) (conf :port) job-id)]
    url))

(defn job-details [conf job-id]
  ;; Returns hadoop job details hash
  (let [html (clojure.string/trim ((client/get (job-details-url conf job-id) {:accept :json}) :body))
        status (field-value "Status" html)]
    (let [ret (details (job-fields-map status) html)]
      (if-let [duration (ret "duration")]
        (assoc ret "duration" (parse-duration duration))
        ret))))

(defn show-job-details [conf job-id]
  (clojure.pprint/pprint (job-details conf job-id)))

;;;
;;; JOB TRACKER 
;;;

(defn jobtracker-url [conf]
  (format "http://%s:%s/jobtracker.jsp" (conf :host) (conf :port)))

(defn active-job-ids [html]
  ;; Returns list of active hadoop jobs
  (map #(nth % 1) (re-seq #"jobdetails.jsp\?jobid=(job_[0-9_]+)&refresh=30" html)))

(defn inactive-job-ids [html]
  ;; Returns list of inactive (completed) hadoop jobs
  (map #(nth % 1) (re-seq #"jobdetails.jsp\?jobid=(job_[0-9_]+)&refresh=0" html)))

(defn all-jobs [conf]
  ;; Returns complete list of hadoop jobs
  (let [html (clojure.string/trim ((client/get (jobtracker-url conf) {:accept :json}) :body))]
    { :active (active-job-ids html) :inactive (inactive-job-ids html) }))

(defn txt->timestamp [txt-date]
  ;; Converts "Mon Apr 04 03:13:06 UTC 2016" date to timestamp format
  (had-util/str->timestamp (had-util/iso-format txt-date)))

(defn- add-stamps [details]
  ;; Adds unix timestamp to job details data structure
  (let [ret (assoc details "t0-stamp" (txt->timestamp (details "t0")))]
    (if (= (ret "status") "Running")
      ret
      (assoc ret "t1-stamp" (txt->timestamp (details "t1"))))))
      
(defn show-job-ids [conf ]
  ;; Prints all hadoop jobs
  (clojure.pprint/pprint (all-jobs conf)))


 
;;;
;;; Putting it all together
;;;

(defn sort-key [job-id]
  ;; Returns sort key for chornological hadoop job sorting
  (nth (re-find #"_(\d+)$" job-id) 1))

(defn process [conf id]
  ;; Returns info for hadoop job with id
  (let [job (job-details conf id)] ;; websevice call
    (assoc (add-stamps job) "id" id)))

(defn top-n [conf n]
  ;; Retrieves last n (active and inactive) hadoop jobs.
  (let [jobs (all-jobs conf)
        active (jobs :active)
        inactive (reverse (sort-by sort-key (jobs :inactive))) ;; descending time order
        inact-len (- n (count active))
        all (sort-by sort-key (concat active (take inact-len inactive)))] ;; ascending order
    (println (count active) (count inactive))
    (map (partial process conf) all)))