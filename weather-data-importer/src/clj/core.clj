(ns core
  (:require
   [clojure.data.xml :as xml]
   [clojure.java.io :as io]
   [clojure.pprint :as pp]
   db
   [miner.ftp :as ftp]
   [next.jdbc :as jdbc]
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

(defn main
  [forecast db-conn]
  (let [{:keys [expiry forecast]} (read-forecast forecast)
        forecast-id (new-forecast-id! db-conn expiry)]
    (insert-forecast! db-conn
                      (->> forecast
                           (map #(merge forecast-proto {:forecastID forecast-id} %))
                           vec))))

