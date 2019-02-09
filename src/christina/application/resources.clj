(ns christina.application.resources
  (:require
    [christina.library.resources :as r]))

(def strings
  (-> (r/container)
      (r/register
        :user-name-promt "Введите ваше имя:"
        :user-greeting-format "Привет, %s!"
        :error-number "Неверный вормат числа."
        :number-promt "Введите два числа:"
        :result-format "%s, сумма чисел равна: %f")))