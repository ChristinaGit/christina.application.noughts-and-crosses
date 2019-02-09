(ns christina.application.hello.view
  (:require
    [christina.application.resources :refer :all]
    [christina.library.resources :as r]))

(defn show-empty-state [on-input-user-name]
  (println (r/resolve strings :user-name-promt))
  (on-input-user-name (read-line)))

(defn- parse-number [number-string]
  (try (Double/parseDouble number-string)
       (catch Exception _ nil)))

(defn- read-number []
  (parse-number (read-line)))

(defn show-numbers-input [user-name on-input-numbers]
  (println (r/resolve-format strings :user-greeting-format user-name))
  (println (r/resolve strings :number-promt))
  (loop [arg1 (read-number)
         arg2 (read-number)]
    (if (or (nil? arg1) (nil? arg2))
      (do
        (println (r/resolve strings :error-number))
        (recur (read-number) (read-number)))
      (on-input-numbers arg1 arg2))))

(defn show-result [user-name result]
  (println (r/resolve-format strings :result-format user-name result))
  (shutdown-agents))