(ns christina.application.noughts-and-crosses.view.console
  (:require
    [clojure.string :as string]
    [clojure.tools.logging :as log]
    [christina.library.contract :as contract]
    [christina.library.resources.container :as res]
    [christina.library.console :as console]
    [christina.application.noughts-and-crosses.domain.game :as game]
    [christina.application.noughts-and-crosses.domain.sign :as sign]
    [christina.application.noughts-and-crosses.domain.field :as field]
    [christina.application.noughts-and-crosses.model :as model]
    [christina.application.noughts-and-crosses.services :as services]
    [christina.application.noughts-and-crosses.resources :as r]))

(defn- request-turn [model handler]
  {:pre [(contract/not-nil? model handler)]}
  (println (res/resolve-format* ::r/request-player-turn-format (model/active-player-id model)))
  (let [coordinates (console/read-n-ints 2)]
    (handler #(model/perform-turn % coordinates))))

(defn- request-player-id [player-id]
  {:pre [(contract/not-nil? player-id)]}
  (println (res/resolve-format* ::r/request-player-id-format player-id))
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
  (println (res/resolve-string* ::r/start-welcome))
  (let [user-name-1 (request-player-id 1)
        user-name-2 (request-player-id 2)]
    (handler #(model/start-game % user-name-1 user-name-2))))

(defn- on-state->game-in-progress [new-model handler]
  {:pre [(contract/not-nil? new-model handler)]}
  (let [players-names (model/players-ids new-model)]
    (println (res/resolve-format* ::r/game-started-format (first players-names) (second players-names))))
  (request-turn new-model handler))

(defn- on-state->game-hold [handler]
  (println (res/resolve-string* ::r/incorrect-turn))
  (handler #(model/rollback %)))

(defn- on-state->game-ends-winner [old-model new-model handler]
  (println (field-map new-model))
  (println (res/resolve-format* ::r/game-winner-congratulation (model/active-player-id old-model) (sign->char (model/active-player-sign old-model))))
  (handler #(model/close %)))

(defn- on-state->game-ends-draw [old-model new-model handler]
  (println (field-map new-model))
  (println (res/resolve-string* ::r/game-draw-congratulation))
  (println)
  (handler #(model/close %)))

(defn- refresh-state|game-in-progress [new-model handler]
  {:pre [(contract/not-nil? new-model handler)]}
  (println (field-map new-model))
  (request-turn new-model handler))

(defn- render* [services old-model new-model handler]
  {:pre [(contract/not-nil? old-model new-model handler)]}
  (let [new-state (model/state new-model)]
    (if (not= new-state (model/state old-model))
      (case new-state
        ::model/state|initialized
        (on-state->initialized handler)
        ::model/state|game|in-progress
        (on-state->game-in-progress new-model handler)
        ::model/state|game|hold
        (on-state->game-hold handler)
        ::model/state|game|ends|winner
        (on-state->game-ends-winner old-model new-model handler)
        ::model/state|game|ends|draw
        (on-state->game-ends-draw old-model new-model handler))
      (case new-state
        ::model/state|game|in-progress
        (refresh-state|game-in-progress new-model handler)))))

(defn render [services old new handler]
  (binding [res/*this* (services/resources services)]
    (render* services old new handler)))

