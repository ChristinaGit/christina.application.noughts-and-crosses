(ns christina.application.noughts-and-crosses.resources
  (:require [christina.library.resources.container :as container]))

(def neutral (-> (container/create)))

(def ru (-> neutral
            (container/add-string :start-welcome "Добро божаловать!")
            (container/add-format :request-player-id-format "Введите имя %s игрока:")
            (container/add-format :request-player-turn-format "Ваш ход, %s!")
            (container/add-format :game-started-format "Игра начинается! Играет %s против %s!")
            (container/add-format :game-winner-congratulation "Игра окончена! Победил игрок %s, игравший '%s'!")))

(def en (-> neutral
            (container/add-string :start-welcome "Welcome!")
            (container/add-format :request-player-id-format "Input %s player name:")
            (container/add-format :request-player-turn-format "Your turn, %s!")
            (container/add-format :game-started-format "Game begins! %s vs. %s!")
            (container/add-format :game-winner-congratulation "The game is over! The player %s who played '%s' won!")))
