(ns christina.application.noughts-and-crosses.domain.rules
  (:require [christina.library.contract :as contract]
            [christina.application.noughts-and-crosses.domain.sign :as sign])
  (:import (clojure.lang Keyword)))

(defn create [turn terminal]
  {::.turn     turn
   ::.terminal terminal})

(defn turn [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::.turn this))

(defn terminal [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::.terminal this))

(derive ::terminal-state|draw ::terminal-state)
(derive ::terminal-state|winner ::terminal-state)
(derive ::terminal-state|none ::terminal-state)

(defn is-terminal-state? [^Keyword state]
  (isa? state ::terminal-state))

(defn terminal-state [this field]
  {:pre  [(contract/not-nil? this field)]
   :post [is-terminal-state?]}
  ((terminal this) field))

(defn valid-turn? [this field coordinates ^Keyword sign]
  {:pre [(contract/not-nil? this field sign)
         (sign/is? sign)]}
  (true? ((turn this) field coordinates sign)))