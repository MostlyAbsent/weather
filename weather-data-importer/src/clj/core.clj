(ns core
  (:require
   [clojure.java.io :as io]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [clojure.pprint :as pp]
   [clojure.data.xml :as xml]
   parsers)
  (:gen-class))

;; command line args parsing

;; Transaction

;; Insert forecast and expiry

;; Insert

;; invoke main

(defn read-forecast
  [file]
  (let [input (io/reader file)
        parsed (-> (xml/parse input)
                   :content)
        expiry (parsers/expiry-parser parsed)
        forecast (parsers/forecast-parser parsed)]
    {:expiry expiry
     :forecast forecast}))

(defn new-forecast-id!
  "Inserts the expiry as a new forecast and returns the ID generated."
  [db-conn expiry]
  (let [forecast-id (-> (sql/insert! db-conn
                                     :forecast {:expiry expiry} {:suffix "RETURNING *"})
                        :Forecast/id)]
    forecast-id))

(defn insert-forecast!
  [db-conn forecast]
  (sql/insert-multi! db-conn :ForecastPeriod forecast))

(def forecast-proto
  {:districtDescription ""
   :districtKey ""
   :endTime nil
   :forecastID nil
   :forecastIconCode nil
   :idx nil
   :locationDescription ""
   :locationKey ""
   :precipitationProbability nil
   :precipitationRange nil
   :precis ""
   :regionDescription ""
   :regionKey ""
   :startTime nil
   :tempMax nil
   :tempMin nil})

