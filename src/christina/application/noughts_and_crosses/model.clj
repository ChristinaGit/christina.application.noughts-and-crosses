(ns christina.application.noughts-and-crosses.model
  (:require [clojure.tools.logging :as log]
            [christina.library.contract :as contract]
            [christina.application.noughts-and-crosses.domain.sign :as sign]
            [christina.application.noughts-and-crosses.domain.player :as player]
            [christina.application.noughts-and-crosses.domain.user :as user]
            [christina.application.noughts-and-crosses.domain.field :as field]
            [christina.application.noughts-and-crosses.domain.game :as game])
  (:import (clojure.lang Keyword)))

(derive ::state|none ::state)
(derive ::state|initialized ::state)
(derive ::state|game-in-progress ::state)
(derive ::state|closed ::state)

(defn is-state? [^Keyword state]
  (isa? state ::state))

(defn create []
  {:post [contract/not-nil?]}
  {::state     ::state|none
   ::game      nil})

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

(defn players-names [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil?]}
  (map (comp user/name player/user) (game/players (game this))))

(defn active-player-name [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil?]}
  (user/name (player/user (game/active-player (game this)))))

(defn initialize [this]
  {:pre  [(contract/not-nil? this)
          (in-state? this ::state|none)]
   :post [contract/not-nil?]}
  (assoc this
    ::state ::state|initialized))

(defn start-game [this user-name-1 user-name-2]
  {:pre  [(contract/not-nil? this user-name-1 user-name-2)
          (in-state? this ::state|initialized)]
   :post [contract/not-nil?]}
  (assoc this
    ::state ::state|game-in-progress
    ::game (game/create
             (field/create [[0 3] [0 3]])
             [(player/create (user/create user-name-1) ::sign/cross)
              (player/create (user/create user-name-2) ::sign/nought)])))

(defn perform-turn [this coordinates]
  {:pre  [(contract/not-nil? this coordinates)
          (in-state? this ::state|game-in-progress)]
   :post [contract/not-nil?]}
  (assoc this
    ::game (game/perform-turn (game this) coordinates)))

(defn close [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (assoc this
    ::state ::state|closed))