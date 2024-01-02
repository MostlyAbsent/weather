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

(defn new-forecast-id!
  "Inserts the expiry as a new forecast and returns the ID generated."
  [db-conn expiry]
  (let [forecast-id (-> (sql/insert! db-conn
                                       :forecast {:expiry expiry} {:suffix "RETURNING *"})
                        :Forecast/id)]
    {:id forecast-id}))

