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

(defn main
  [forecast db-conn]
  (let [{:keys [expiry forecast]} (read-forecast forecast)
        forecast-id (db/new-forecast-id! db-conn expiry)]
    (db/insert-forecast! db-conn
                      (->> forecast
                           (map #(merge db/forecast-proto {:forecastID forecast-id} %))
                           vec))))

(comment

  (def dev-db-def {:dbtype "sqlite" :dbname "db.sqlite"})

  (def dev-db-conn (jdbc/get-datasource dev-db-def))

  (main "IDW14199.xml" dev-db-conn)

  (ftp/with-ftp [client "ftp://ftp.bom.gov.au/anon/gen/fwo/"
                 :username "anonymous"
                 :password "guest"]
    (ftp/client-get client "IDW14199.xml" "downloaded.xml"))
)
