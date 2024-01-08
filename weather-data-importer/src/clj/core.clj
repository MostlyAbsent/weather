(ns core
  (:require
   [clojure.data.xml :as xml]
   [clojure.java.io :as io]
   [clojure.tools.cli :refer [parse-opts]]
   db
   [miner.ftp :as ftp]
   [next.jdbc :as jdbc]
   parsers)
  (:gen-class))


(def cli-options
  [["-d" "--dbname DATABASE" "Sqlite database file"
    :validate [#(.exists (io/file %))]]])

(defn read-forecast
  [file]
  (let [input (io/reader file)
        parsed (-> (xml/parse input)
                   :content)
        expiry (parsers/expiry-parser parsed)
        {:keys [locations forecastPeriods]} (parsers/forecast-parser parsed)]
    {:expiry expiry
     :forecast forecastPeriods
     :locations locations}))

(defn importer
  [parsed-db-name]
  (let [db-conn (jdbc/get-datasource {:dbtype "sqlite" :dbname parsed-db-name})
        filename (str (System/getProperty "java.io.tmpdir") (rand-int 9999) ".xml")
        forecast (if (ftp/with-ftp [client "ftp://ftp.bom.gov.au/anon/gen/fwo/"
                                    :username "anonymous"
                                    :password "guest"]
                       (ftp/client-get client "IDW14199.xml" filename))
                   filename
                   (throw (java.io.FileNotFoundException. "FTP failed to dowload file.")))
        {:keys [expiry forecast locations]} (read-forecast forecast)
        forecast-id (db/new-forecast-id! db-conn expiry)]
    (db/insert-locations! db-conn (vec locations))
    (db/insert-forecast! db-conn
                         (->> forecast
                              (map #(merge db/forecast-proto {:forecastID forecast-id} %))
                              vec))))

(defn -main [& args]
  (let [dbname (-> (parse-opts args cli-options)
                   :options
                   :dbname)]
    (importer dbname)))
