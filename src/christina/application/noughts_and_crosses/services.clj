(ns christina.application.noughts-and-crosses.services
  (:require [christina.library.contract :as contract]))

(defn create [resources]
  {:pre [contract/not-nil? resources]}
  {::resources resources})

(defn resources [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::resources this))