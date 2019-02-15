(ns christina.application.noughts-and-crosses.domain.player
  (:require
    [christina.library.contract :as contract]
    [christina.application.noughts-and-crosses.domain.sign :as sign]))

(defn create [user sign]
  {:pre  [(contract/not-nil? user sign)
          (sign/is? sign)]
   :post [contract/not-nil?]}
  {::user user
   ::sign sign})

(defn user [this]
  {:pre  [(contract/not-nil? user)]
   :post [contract/not-nil?]}
  (::user this))

(defn sign [this]
  {:pre  [(contract/not-nil? user)]
   :post [contract/not-nil?
          sign/is?]}
  (::sign this))