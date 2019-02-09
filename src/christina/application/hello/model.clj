(ns christina.application.hello.model
  (:require
    [clojure.tools.logging :as log]))

(derive :state/authorized :state/initialized)
(derive :state/with-args :state/authorized)

(defn create []
  (agent {:state     nil
          :user-name nil
          :arg1      nil
          :arg2      nil}
         :error-handler #(log/error %2)))

(defn initialize [a]
  (send a assoc :state :state/initialized))

;; mutators

(defn change-user-name [a user-name]
  (send a assoc
        :state :state/authorized
        :user-name user-name))

(defn change-args [a arg1 arg2]
  (send a assoc
        :state :state/with-args
        :arg1 arg1
        :arg2 arg2))

;; getters

(defn get-state [model]
  (:state model))

(defn get-user-name [model]
  {:pre [(isa? (get-state model) :state/authorized)]}
  (:user-name model))

(defn get-result [model]
  {:pre [(isa? (get-state model) :state/with-args)]}
  (+ (:arg1 model) (:arg2 model)))

