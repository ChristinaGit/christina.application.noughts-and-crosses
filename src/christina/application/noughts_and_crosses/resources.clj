(ns christina.application.noughts-and-crosses.resources
  (:require [christina.library.resources.container :as container]))

(def neutral (-> (container/create)))

(def ru (-> neutral
            (container/add-string ::start-welcome "Добро божаловать!")
            (container/add-string ::request-rules "Введите длину победной цепочки и размер поля:")
            (container/add-format ::request-player-id-format "Введите имя %s игрока:")
            (container/add-format ::request-player-turn-format "Ваш ход, %s!")
            (container/add-format ::game-started-format "Игра начинается! Играет %s против %s!")
            (container/add-format ::game-winner-congratulation "Игра окончена! Победил игрок %s, игравший '%s'!")
            (container/add-format ::game-draw-congratulation "Игра окончена! Ничья!")
            (container/add-format ::incorrect-turn "Неверный ход!")))

(def en (-> neutral
            (container/add-string ::start-welcome "Welcome!")
            (container/add-string ::request-rules "Input length of winning line and field size:")
            (container/add-format ::request-player-id-format "Input %s player name:")
            (container/add-format ::request-player-turn-format "Your turn, %s!")
            (container/add-format ::game-started-format "Game begins! %s vs. %s!")
            (container/add-format ::game-winner-congratulation "The game is over! The player %s who played '%s' won!")
            (container/add-format ::game-draw-congratulation "The game is over! Draw!")
            (container/add-format ::incorrect-turn "The turn is incorrect!")))
