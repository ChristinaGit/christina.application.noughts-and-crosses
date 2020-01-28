(ns christina.application.noughts-and-crosses.domain.user
  (:refer-clojure :exclude [name])
  (:require [christina.library.contract :as contract]))

(defn create [id]
  {:pre  [(contract/not-nil? id)]
   :post [contract/not-nil?]}
  {::.id id})

(defn id [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::.id this))