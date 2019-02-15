(ns christina.application.noughts-and-crosses.domain.game
  (:require
    [christina.library.contract :as contract]
    [christina.application.noughts-and-crosses.domain.field :as field]
    [christina.application.noughts-and-crosses.domain.player :as player]))

(defn can-play-together? [players]
  {:pre [(contract/not-nil? players)]}
  (distinct? (map player/sign players)))

(defn create [field players]
  {:pre  [(contract/not-nil? field players (first players) (second players))
          (can-play-together? players)]
   :post [contract/not-nil?]}
  {::field               field
   ::players             players
   ::active-player-index 0})

(defn field [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::field this))

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

(defn perform-turn [this coordinates]
  {:pre  [(contract/not-nil? this coordinates)
          (field/empty-by-coordinates? (field this) coordinates)]
   :post [contract/not-nil?]}
  (assoc this
    ::field (field/place-sign
              (field this)
              (player/sign (active-player this))
              coordinates)
    ::active-player-index (mod (inc (active-player-index this)) (count (players this)))))

