(ns christina.application.noughts-and-crosses.view
  (:require
    [clojure.string :as string]
    [christina.library.contract :as contract]
    [christina.application.noughts-and-crosses.model :as model]
    [clojure.tools.logging :as log]))

(defn- parse-number [number-string]
  (try (int (Integer/parseInt number-string))
       (catch Exception _ nil)))

(defn- read-number []
  (parse-number (read-line)))

(defn- request-turn [new handler]
  (println "Your turn," (model/active-player-name new))
  (let [coordinates [(read-number) (read-number)]]
    (handler (fn [m]
               (model/perform-turn m coordinates)))))

(defn render [old new handler]
  {:pre [(contract/not-nil? old new handler)]}
  (let [old-state (model/state old)
        new-state (model/state new)]
    (if (not= old-state new-state)
      (case new-state
        ::model/state|initialized
        (do
          (println "Welcome!")
          (println "Input user names:")
          (let [user-name-1 (read-line)
                user-name-2 (read-line)]
            (handler (fn [m]
                       (model/start-game m user-name-1 user-name-2)))))
        ::model/state|game-in-progress
        (do (println "Game started:" (string/join " VS " (model/players-names new)))
            (request-turn new handler)))
      (case new-state
        ::model/state|game-in-progress
        (do
          (println (model/field-map new))
          (request-turn new handler))))))
