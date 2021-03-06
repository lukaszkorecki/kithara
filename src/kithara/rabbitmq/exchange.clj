(ns kithara.rabbitmq.exchange
    (:refer-clojure :exclude [declare])
    (:require [kithara.utils :as u])
    (:import [com.rabbitmq.client Channel]))

;; ## Exchange Type (w/ Lookup)

(deftype Exchange [^Channel channel exchange-name exchange-type]
  clojure.lang.ILookup
  (valAt [_ k]
    (case k
      :channel       channel
      :exchange-name exchange-name
      :exchange-type exchange-type
      (throw
        (IllegalArgumentException.
          (str "cannot look up key " k " in exchange map."))))))

(alter-meta! #'->Exchange assoc :private true)

;; ## Declare

(defn declare
  "Declare a RabbitMQ exchange."
  [^Channel channel exchange-name exchange-type
   & [{:keys [durable?
              auto-delete?
              arguments]
       :or {durable?     false
            auto-delete? false}}]]
  (.exchangeDeclare
    channel
    (name exchange-name)
    (name exchange-type)
    (boolean durable?)
    (boolean auto-delete?)
    (u/stringify-keys1 arguments))
  (->Exchange channel exchange-name exchange-type))

(defn delete
  "Delete a RabbitMQ exchange based on the return value of `declare`."
  [{:keys [^Channel channel ^String exchange-name]}]
  (.exchangeDelete channel exchange-name))
