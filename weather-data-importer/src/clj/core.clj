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

(def cli-options
  [["-d" "--database DATABASE" "Sqlite database file"
    :validate [#(.exists (io/file %))]]])

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

(defn importer
  []
  (let [db-conn (jdbc/get-datasource {:dbtype "sqlite" :dbname #_parsed-db-name "db.sqlite"})
        filename (str (System/getProperty "java.io.tmpdir") (rand-int 9999) ".xml")
        forecast (if (ftp/with-ftp [client "ftp://ftp.bom.gov.au/anon/gen/fwo/"
                                    :username "anonymous"
                                    :password "guest"]
                       (ftp/client-get client "IDW14199.xml" filename))
                   filename
                   (throw (java.io.FileNotFoundException. "FTP failed to dowload file.")))
        {:keys [expiry forecast]} (read-forecast forecast)
        forecast-id (db/new-forecast-id! db-conn expiry)]
    (db/insert-forecast! db-conn
                         (->> forecast
                              (map #(merge db/forecast-proto {:forecastID forecast-id} %))
                              vec))))

(defn -main []
  (importer))

(comment

  (importer)

  )
