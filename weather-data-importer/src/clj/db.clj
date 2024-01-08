(ns db
  (:require
   [next.jdbc.sql :as sql]))

(defn new-forecast-id!
  "Inserts the expiry as a new forecast and returns the ID generated."
  [db-conn expiry]
  (let [forecast-id (-> (sql/insert! db-conn
                                     :forecast {:expiry expiry} {:suffix "RETURNING *"})
                        :Forecast/id)]
    forecast-id))

(defn insert-locations!
  [db-conn locations]
  (sql/insert-multi! db-conn :Locations locations))

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
