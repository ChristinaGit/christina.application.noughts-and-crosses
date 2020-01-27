(ns christina.application.noughts-and-crosses.view.console
  (:require
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [christina.library.contract :as contract]
    [christina.library.resources.container :as r]
    [christina.library.console :as console]
    [christina.application.noughts-and-crosses.domain.game :as game]
    [christina.application.noughts-and-crosses.domain.sign :as sign]
    [christina.application.noughts-and-crosses.domain.field :as field]
    [christina.application.noughts-and-crosses.model :as model]
    [christina.application.noughts-and-crosses.services :as services]))

(defn- request-turn [model handler]
  {:pre [(contract/not-nil? model handler)]}
  (println (r/resolve-format* :request-player-turn-format (model/active-player-id model)))
  (let [coordinates (console/read-n-ints 2)]
    (handler #(model/perform-turn % coordinates))))

(defn- request-player-id [player-id]
  {:pre [(contract/not-nil? player-id)]}
  (println (r/resolve-format* :request-player-id-format player-id))
  (read-line))

(defn- sign->char [sign]
  {:post [contract/not-nil?]}
  (case sign
    ::sign/nought \O
    ::sign/cross \X
    \u00B7))

(defn field-map [model]
  {:pre  [(contract/not-nil? model)]
   :post [contract/not-nil?]}
  (let [game (model/game model)
        field (game/field game)
        [size-x size-y] (field/coordinate-ranges field)]
    (apply str (map
                 (fn [x]
                   (apply str (conj (map
                                      (fn [y]
                                        (sign->char (field/sign-by-coordinates field [x y])))
                                      (apply range size-y)) \newline)))
                 (apply range size-x)))))

(defn- on-state->initialized [handler]
  {:pre [(contract/not-nil? handler)]}
  (println (r/resolve-string* :start-welcome))
  (let [user-name-1 (request-player-id 1)
        user-name-2 (request-player-id 2)]
    (handler #(model/start-game % user-name-1 user-name-2))))

(defn- on-state->game-in-progress [model handler]
  {:pre [(contract/not-nil? model handler)]}
  (let [players-names (model/players-ids model)]
    (println (r/resolve-format* :game-started-format (first players-names) (second players-names))))
  (request-turn model handler))

(defn- on-state->game-ends [old-model handler]
  (println (r/resolve-format* :game-winner-congratulation (model/active-player-id old-model) (sign->char (model/active-player-sign old-model))))
  (handler #(model/close %)))

(defn- refresh-state|game-in-progress [model handler]
  {:pre [(contract/not-nil? model handler)]}
  (println (field-map model))
  (request-turn model handler))

(defn- render* [services old-model new-model handler]
  {:pre [(contract/not-nil? old-model new-model handler)]}
  (let [new-state (model/state new-model)]
    (if (not= new-state (model/state old-model))
      (case new-state
        ::model/state|initialized
        (on-state->initialized handler)
        ::model/state|game-in-progress
        (on-state->game-in-progress new-model handler)
        ::model/state|game-ends
        (on-state->game-ends old-model handler))
      (case new-state
        ::model/state|game-in-progress
        (refresh-state|game-in-progress new-model handler)))))

(defn render [services old new handler]
  (binding [r/*this* (services/resources services)]
    (render* services old new handler)))

