(ns christina.application.noughts-and-crosses.domain.game
  (:require
    [christina.library.contract :as contract]
    [christina.application.noughts-and-crosses.domain.rules :as rules]
    [christina.application.noughts-and-crosses.domain.field :as field]
    [christina.application.noughts-and-crosses.domain.player :as player]
    [christina.application.noughts-and-crosses.domain.user :as user]
    [clojure.tools.logging :as log])
  (:import (clojure.lang Keyword)))

(derive ::state|in-progress ::state)
(derive ::state|done ::state)
(derive ::state|done|winner ::state|done)
(derive ::state|done|draw ::state|done)

(defn is-state? [^Keyword state]
  (isa? state ::state))

(defn can-play-together? [players]
  {:pre  [(contract/not-nil? players)]
   :post [contract/not-nil?]}
  (and
    (apply distinct? (map player/sign players))
    (apply distinct? (map #(user/id (player/user %)) players))))

(defn create [field rules players]
  {:pre  [(contract/not-nil? field rules players (first players) (second players))
          (can-play-together? players)]
   :post [contract/not-nil?]}
  {::field               field
   ::rules               rules
   ::players             players
   ::active-player-index 0
   ::state               ::state|in-progress})

(defn field [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::field this))

(defn rules [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::rules this))

(defn players [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::players this))

(defn active-player-index [this]
  {:pre [(contract/not-nil? this)]}
  (::active-player-index this))

(defn active-player [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (nth (players this) (active-player-index this)))

(defn state [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?
          is-state?]}
  (::state this))

(defn perform-turn [this coordinates]
  {:pre  [(contract/not-nil? this coordinates)
          (field/empty-by-coordinates? (field this) coordinates)]
   :post [contract/not-nil?]}
  (let [new-field (field/place-sign
                    (field this)
                    (player/sign (active-player this))
                    coordinates)]
    (assoc this
      ::field new-field
      ::active-player-index (mod (inc (active-player-index this)) (count (players this)))
      ::state (case (field/terminal-state new-field (rules this))
                ::rules/terminal|draw ::state|done|draw
                ::rules/terminal|winner ::state|done|winner
                ::state|in-progress))))
