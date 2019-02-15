(ns christina.application.noughts-and-crosses.resources
  (:require [christina.library.resources.container :as container]))

(def neutral (-> (container/create)))

(def ru (-> neutral
            (container/add-string :start-welcome "Добро божаловать!")
            (container/add-format :request-player-name-format "Введите имя %s игрока:")
            (container/add-format :request-player-turn-format "Ваш ход, %s!")
            (container/add-format :game-started-format "Игра начинается! Играет %s против %s!")))

(def en (-> neutral
            (container/add-string :start-welcome "Welcome!")
            (container/add-format :request-player-name-format "Input %s player name:")
            (container/add-format :request-player-turn-format "Your turn, %s!")
            (container/add-format :game-started-format "Game begins! %s vs. %s!")))
