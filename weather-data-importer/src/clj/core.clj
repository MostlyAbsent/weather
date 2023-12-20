(ns core
  (:require [honey.sql :as sql]
            [clojure.java.io :as io]
            [next.jdbc :as jdbc]
            [clojure.data.xml :as xml]
            [java-time.api :as jt])

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

(defn unixtime [x] (-> (jt/offset-date-time x)
                       (jt/to-millis-from-epoch)))

(defmulti forecast-content (fn [x] (get-in x [:attrs :type])))

(defmethod forecast-content "forecast_icon_code"
  [{:keys [content]}]
  {:forecastIconCode (first content)})

(defmethod forecast-content "probability_of_precipitation"
  [{:keys [content]}]
  {:precipitiationProbability (first content)})

(defmethod forecast-content "air_temperature_minimum"
  [{:keys [content]}]
  {:tempMin (first content)})

(defmethod forecast-content "air_temperature_maximum"
  [{:keys [content]}]
  {:tempMax (first content)})

(defmethod forecast-content "precipitation_range"
  [{:keys [content]}]
  {:precipitationRange (first content)})

(defmethod forecast-content "precis"
  [{:keys [content]}]
  {:precis (first content)})

(defmethod forecast-content :default
  [args]
  {:unhandled-key true})

(defn forecast-period-parser
  [{:keys [attrs content]}]

  (merge {:index (:index attrs)
          :startTime (unixtime (:start-time-utc attrs))
          :endTime (unixtime (:end-time-utc attrs))}
         (reduce #(conj %1  (forecast-content %2)) {} content)))

(defmulti area (fn [x] (get-in x [:attrs :type])))

(defmethod area "region"
  [{:keys [attrs]}]
  {:region {:aac (:aac attrs)
            :description (:description attrs)}})

(defmethod area "public-district"
  [{:keys [attrs]}]
  {:district {:aac (:aac attrs)
              :parent (:parent-aac attrs)
              :description (:description attrs)}})

(defmethod area "location"
  [{:keys [attrs content]}]
  {:location {:aac (:aac attrs)
              :parent (:parent-aac attrs)
              :description (:description attrs)
              :content (reduce #(conj %1 (forecast-period-parser %2)) [] content)}})

(defmethod area :default
  [_])

(defn- forecast-parser
  [parsed-xml]
  (->> parsed-xml
       second
       :content
       (reduce #(conj %1 (area %2)) [])
       (filter #(not (nil? %)))))

;; Transaction

;; Insert forecast and expiry

;; Insert

;; invoke main
