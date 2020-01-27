(ns christina.application.noughts-and-crosses.domain.rules
  (:require [christina.library.contract :as contract]
            [christina.application.noughts-and-crosses.domain.sign :as sign])
  (:import (clojure.lang Keyword)))

(defn create [turn terminal]
  {::turn     turn
   ::terminal terminal})

(defn turn [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::turn this))

(defn terminal [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::terminal this))

(defn valid-turn? [this field ^Keyword sign coordinates]
  {:pre [(contract/not-nil? this field sign)
         (sign/is? sign)]}
  (true? ((turn this) field sign coordinates)))

(derive ::terminal|draw ::terminal)
(derive ::terminal|winner ::terminal)

(defn is-terminal-state? [^Keyword state]
  (isa? state ::terminal))