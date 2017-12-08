(ns lumberghly.cljs.xhr
    (:import [goog.net XhrIoPool EventType]
           [goog.net.XhrIo ResponseType])
  (:require [lumberghly.cljs.utils :as utils]))

(def min-pool-size 1)
(def max-pool-size 10)
(def pool (XhrIoPool. (js/Map.) min-pool-size max-pool-size))

(def string-type (.-TEXT ResponseType))
(def buffer-type (.-ARRAY_BUFFER ResponseType))
(def json-type (.-JSON ResponseType))

(def jwt-token (atom nil))

(defn response-types [t]
  (case t
    :string string-type
    :buffer buffer-type
    :json json-type))

(defn on-io-result [response on-success on-error on-finally]
  (fn [e]
    (let [io (.-target e)]
      (try
        (if-not (.isSuccess io)
          (on-error io)
          (if (= json-type (.getResponseType io))
            (on-success (js->clj (.getResponseJson io) :keywordize-keys true))
            (on-success (.getResponse io))))
        (finally
          (try
            (on-finally io)
            (finally
              (.releaseObject pool io))))))))

(defn default-error [io]
  (.error js/console
          (.getLastUri io)
          (.getLastError io)
          (.getResponse io)))

(defn ajax [method url & {:keys [body
                                 timeout-millis
                                 headers
                                 response-type
                                 on-success
                                 on-error
                                 on-finally]
                          :or {timeout-millis 0
                               headers {}
                               response-type :json
                               on-success utils/nop
                               on-error default-error
                               on-finally utils/nop}}]
  (.getObject pool
              (fn [io]
                (doto io
                  (.setTimeoutInterval timeout-millis)
                  (.setResponseType (response-types response-type))
                  (.send url
                         method
                         (.stringify js/JSON
                                     (clj->js body))
                         (merge {"Content-Type" "application/json"
                                 "Authorization" (when-let [t @jwt-token]
                                                   (str "Bearer " t))}
                                headers))
                  (.listenOnce (.-COMPLETE EventType)
                               (on-io-result response-type on-success on-error on-finally))))))
