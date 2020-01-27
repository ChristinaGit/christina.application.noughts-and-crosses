(ns christina.application.noughts-and-crosses.domain.field
  (:require
    [christina.library.contract :as contract]
    [christina.application.noughts-and-crosses.domain.sign :as sign]
    [clojure.tools.logging :as log]
    [christina.application.noughts-and-crosses.domain.rules :as rules])
  (:import (clojure.lang Keyword)))

(defn create
  ([coordinate-ranges]
   {:pre [(contract/not-nil? coordinate-ranges)]}
   (create {} coordinate-ranges))
  ([signs coordinate-ranges]
   {:pre  [(contract/not-nil? signs coordinate-ranges)]
    :post [contract/not-nil?]}
   {::signs             signs
    ::coordinate-ranges coordinate-ranges}))

(defn signs [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::signs this))

(defn coordinate-ranges [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::coordinate-ranges this))

(defn valid-coordinates? [this coordinates]
  {:pre [(contract/not-nil? this coordinates)]}
  (let [coordinate-ranges (coordinate-ranges this)]
    (every? (fn [[coordinate range]]
              (and
                (>= coordinate (first range))
                (< coordinate (last range))))
            (map vector coordinates coordinate-ranges))))

(defn sign-by-coordinates [this coordinates]
  {:pre [(contract/not-nil? this coordinates)
         (valid-coordinates? this coordinates)]}
  (get (signs this) coordinates nil))

(defn empty-by-coordinates? [this coordinates]
  {:pre [(contract/not-nil? this coordinates)
         (valid-coordinates? this coordinates)]}
  (nil? (sign-by-coordinates this coordinates)))

(defn place-sign [this ^Keyword sign coordinates]
  {:pre  [(contract/not-nil? this sign coordinates)
          (sign/is? sign)
          (valid-coordinates? this coordinates)]
   :post [contract/not-nil?]}
  (assoc-in this [::signs coordinates] sign))

(defn terminal-state [this rules]
  {:pre  [(contract/not-nil? this rules)]
   :post [rules/is-terminal-state?]}
  ((rules/terminal rules) this))