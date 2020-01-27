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
  (:import (clojure.lang Keyword)
           (com.sun.xml.internal.bind.v2 TODO)))

(derive ::state|none ::state)
(derive ::state|initialized ::state)
(derive ::state|game-in-progress ::state)
(derive ::state|game-ends ::state)
(derive ::state|closed ::state)

(defn is-state? [^Keyword state]
  (isa? state ::state))

(defn create []
  {:post [contract/not-nil?]}
  {::state ::state|none
   ::game  nil})

(defn state [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?
          is-state?]}
  (::state this))

(defn in-state? [this expected]
  {:pre [(contract/not-nil? this expected)
         (is-state? expected)]}
  (= (state this) expected))

(defn game [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil?]}
  (::game this))

(defn players-ids [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil?]}
  (map (comp user/id player/user) (game/players (game this))))

(defn active-player-id [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil?]}
  (user/id (player/user (game/active-player (game this)))))

(defn active-player-sign [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil? sign/is?]}
  (player/sign (game/active-player (game this))))

(defn initialize [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|none)]
   :post [contract/not-nil?]}
  (assoc this
    ::state ::state|initialized))

(defn- turn-rule [field sign coordinates] true)

(defn- sign->number [sign]
  {:pre [(sign/is? sign)]}
  (case sign
    ::sign/cross 1
    ::sign/nought -1))

(defn- signs-line-sum [signs-line]
  (math/abs (apply + (map sign->number signs-line))))

(defn- signs-lines [signs coordinates size]
  (list
    (filter #(not (nil? %)) (map #(get signs [(+ (first coordinates) %) (second coordinates)]) (range 0 size)))
    (filter #(not (nil? %)) (map #(get signs [(first coordinates) (+ (second coordinates) %)]) (range 0 size)))
    (filter #(not (nil? %)) (map #(get signs [(+ (first coordinates) %) (+ (second coordinates) %)]) (range 0 size)))))

(defn- signs-lines-sums [signs coordinates size]
  (map signs-line-sum (signs-lines signs coordinates size)))

(defn- contain-terminal-line? [signs coordinates size]
  (some #(= % size) (signs-lines-sums signs coordinates size)))

(defn- terminal-rule [field]
  (let [signs (field/signs field)
        coordinates (keys signs)]
    (cond
      (some #(= % true) (map #(contain-terminal-line? signs % 3) coordinates)) ::rules/terminal|winner
      (= (count coordinates) (* 3 3)) ::rules/terminal|draw
      nil)))

(defn start-game [this user-id-1 user-id-2]
  {:pre  [(contract/not-nil? this user-id-1 user-id-2)
          (in-state? this ::state|initialized)]
   :post [contract/not-nil?]}
  (assoc this
    ::state ::state|game-in-progress
    ::game (game/create
             (field/create [[0 3] [0 3]])
             (rules/create turn-rule terminal-rule)
             [(player/create (user/create user-id-1) ::sign/cross)
              (player/create (user/create user-id-2) ::sign/nought)])))

(defn perform-turn [this coordinates]
  {:pre  [(contract/not-nil? this coordinates)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil?]}
  (let [new-game (game/perform-turn (game this) coordinates)]
    (assoc this
      ::state (if (isa? (game/state new-game) ::game/state|done)
                ::state|game-ends
                (state this))
      ::game new-game)))

(defn close [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (assoc this
    ::state ::state|closed))