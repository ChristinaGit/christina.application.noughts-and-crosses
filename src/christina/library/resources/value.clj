(ns christina.library.resources.value
  (:refer-clojure :exclude [type])
  (:require [christina.library.contract :as contract]
            [christina.library.resources.type :as type])
  (:import (clojure.lang Keyword)))


(defn create [^Keyword type value]
  {:pre  [(contract/not-nil? type type)
          (type/is? type)]
   :post [contract/not-nil?]}
  {::type  type
   ::value value})

(defn type [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::type this))

(defn value [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::value this))