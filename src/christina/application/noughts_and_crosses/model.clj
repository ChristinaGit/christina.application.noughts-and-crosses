(ns christina.application.noughts-and-crosses.model
  (:require [clojure.tools.logging :as log]
            [clojure.math.numeric-tower :as math]
            [christina.library.contract :as contract]
            [christina.application.noughts-and-crosses.domain.sign :as sign]
            [christina.application.noughts-and-crosses.domain.player :as player]
            [christina.application.noughts-and-crosses.domain.user :as user]
            [christina.application.noughts-and-crosses.domain.rules :as rules]
            [christina.application.noughts-and-crosses.domain.field :as field]
            [christina.application.noughts-and-crosses.domain.game :as game])
  (:import (clojure.lang Keyword))
  (:use [christina.library.core]))

(derive ::state|none ::state)
(derive ::state|initialized ::state)

(derive ::state|game ::state)
(derive ::state|game|hold ::state|game)
(derive ::state|game|in-progress ::state|game)

(derive ::state|game|ends ::state|game)
(derive ::state|game|ends|winner ::state|game|ends)
(derive ::state|game|ends|draw ::state|game|ends)

(derive ::state|closed ::state)

(defn is-state? [^Keyword state]
  (isa? state ::state))

(defn create []
  {:post [contract/not-nil?]}
  {::.state ::state|none
   ::.game  nil})

(defn state [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?
          is-state?]}
  (::.state this))

(defn in-state? [this expected]
  {:pre [(contract/not-nil? this expected)
         (is-state? expected)]}
  (isa? (state this) expected))

(defn game [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game)]
   :post [contract/not-nil?]}
  (::.game this))

(defn players-ids [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game)]
   :post [contract/not-nil?]}
  (map (comp user/id player/user) (game/players (game this))))

(defn active-player-id [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game)]
   :post [contract/not-nil?]}
  (user/id (player/user (game/active-player (game this)))))

(defn active-player-sign [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game)]
   :post [contract/not-nil? sign/is?]}
  (player/sign (game/active-player (game this))))

(defn initialize [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|none)]
   :post [contract/not-nil?]}
  (assoc this
    ::.state ::state|initialized))

(defn- turn-rule [field coordinates ^Keyword sign]
  (field/empty-by-coordinates? field coordinates))

(defn- sign->number [sign]
  {:pre [(sign/is? sign)]}
  (case sign
    ::sign/cross 1
    ::sign/nought -1))

(defn- signs-line-sum [signs-line]
  (math/abs (apply + (map sign->number signs-line))))

(defn- signs-lines [signs coordinates field-size]
  (list
    (filter not-nil? (map #(get signs [(+ (first coordinates) %) (second coordinates)]) (range 0 field-size)))
    (filter not-nil? (map #(get signs [(first coordinates) (+ (second coordinates) %)]) (range 0 field-size)))
    (filter not-nil? (map #(get signs [(+ (first coordinates) %) (+ (second coordinates) %)]) (range 0 field-size)))))

(defn- signs-lines-sums [signs coordinates filed-size]
  (map signs-line-sum (signs-lines signs coordinates filed-size)))

(defn- contain-terminal-line? [signs coordinates target-size]
  (some #(= % target-size) (signs-lines-sums signs coordinates target-size)))

(defn- terminal-rule [target-length field]
  (let [signs (field/signs field)
        coordinates (keys signs)
        field-width (field/width field)
        field-height (field/height field)
        field-size (max field-width field-height)]
    (cond
      (some true? (map #(contain-terminal-line? signs % target-length) coordinates)) ::rules/terminal-state|winner
      (= (count coordinates) (* field-width field-height)) ::rules/terminal-state|draw
      :else ::rules/terminal-state|none)))

(defn start-game [this target-length field-size user-id-1 user-id-2]
  {:pre  [(contract/not-nil? this target-length user-id-1 user-id-2)
          (>= target-length 2)
          (in-state? this ::state|initialized)]
   :post [contract/not-nil?]}
  (assoc this
    ::.state ::state|game|in-progress
    ::.game (game/create
              (field/create [[0 field-size] [0 field-size]])
              (rules/create turn-rule (partial terminal-rule target-length))
              [(player/create (user/create user-id-1) ::sign/cross)
               (player/create (user/create user-id-2) ::sign/nought)])))

(defn perform-turn [this coordinates]
  {:pre  [(contract/not-nil? this coordinates)
          (in-state? this ::state|game|in-progress)]
   :post [contract/not-nil?]}
  (let [new-game (game/perform-turn (game this) coordinates)]
    (assoc this
      ::.state (case (game/state new-game)
                 ::game/state|hold ::state|game|hold
                 ::game/state|done|winner ::state|game|ends|winner
                 ::game/state|done|draw ::state|game|ends|draw
                 (state this))
      ::.game new-game)))

(defn rollback [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game|hold)]
   :post [contract/not-nil?]}
  (assoc this
    ::.state ::state|game|in-progress
    ::.game (game/rollback (game this))))

(defn close [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (assoc this
    ::.state ::state|closed))