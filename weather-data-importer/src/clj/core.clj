(ns core
  (:require [honey.sql :as sql]
            [clojure.java.io :as io]
            [next.jdbc :as jdbc]
            [clojure.data.xml :as xml]))

;; command line args parsing


(defn- expiry-parser
  [parsed-xml]
  (->> parsed-xml
       first
       :content
       (reduce (fn [acc x]
                 (if (= (:tag x) :expiry-time)
                   (first (:content x))
                   acc)) "")))

(defmulti area (fn [x] (get-in x [:attrs :type])))

(defmethod area "region"
  [{:keys [attrs]}]
  {:region {:aac (:aac attrs)
            :description (:description attrs)}})

(defmethod area :default
  [args]
  )


(defn- forecast-parser
  [parsed-xml]
  (->> parsed-xml
       second
       :content
       (map area)
       (filter #(not (nil? %)))
       #_(map #(get-in % [:attrs :type] "notfound"))))

;; Transaction

;; Insert forecast and expiry

;; Insert

;; invoke main
