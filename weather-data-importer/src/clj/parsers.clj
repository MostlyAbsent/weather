(ns parsers
  (:require
   [java-time.api :as jt]))

(defn expiry-parser
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
  [_]
  {:unhandled-key true})

(defn forecast-period-parser
  [{:keys [attrs content]} aac parent description districts regions]
  (let [district (->> districts
                      (filter #(= parent (get %1 :aac)))
                      first)
        parentDescription (:description district)
        regionKey (:parent district)
        regionDescription (->> regions
                               (filter #(= regionKey (get %1 :aac)))
                               first
                               :description)]
    (merge {:index (:index attrs)
            :startTime (unixtime (:start-time-utc attrs))
            :endTime (unixtime (:end-time-utc attrs))
            :locationKey aac
            :locationDescription description
            :districtDescription parentDescription
            :districtKey parent
            :regionKey regionKey
            :regionDescription regionDescription}
           (reduce #(conj %1  (forecast-content %2)) {} content))))

(defmulti area (fn [x _] (get-in x [:attrs :type])))

(defmethod area "region"
  [{:keys [attrs]} _]
  {:region {:aac (:aac attrs)
            :description (:description attrs)}})

(defmethod area "public-district"
  [{:keys [attrs]} _]
  {:district {:aac (:aac attrs)
              :parent (:parent-aac attrs)
              :description (:description attrs)}})

(defmethod area "location"
  [{:keys [attrs content]} acc]
  (let [aac (:aac attrs)
        parent (:parent-aac attrs)
        description (:description attrs)
        districts (:districts acc)
        regions (:regions acc)]
    {:location (reduce #(conj %1 (forecast-period-parser %2 aac parent description districts regions)) [] content)}))

(defn spread-areas
  [acc x]
  (let [a (area x acc)
        k (first (keys a))
        v (first (vals a))]
    (cond
      (= :region k) (merge acc {:regions (conj (:regions acc) v)})
      (= :district k) (merge acc {:districts (conj (:districts acc) v)})
      (= :location k) (merge acc {:locations (flatten (conj (:locations acc) v))}))))

(defn forecast-parser
  [parsed-xml]
  (->> parsed-xml
       second
       :content
       (reduce spread-areas {:districts [] :regions [] :locations []})
       :locations))
