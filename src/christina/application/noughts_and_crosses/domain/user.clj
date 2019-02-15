(ns christina.application.noughts-and-crosses.domain.user
  (:refer-clojure :exclude [name])
  (:require [christina.library.contract :as contract]))

(defn create [name]
  {:pre  [(contract/not-nil? name)]
   :post [contract/not-nil?]}
  {::name name})

(defn name [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::name this))