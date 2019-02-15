(ns christina.application.noughts-and-crosses.program
  (:require
    [christina.application.noughts-and-crosses.model :as model]
    [christina.application.noughts-and-crosses.view :as view]
    [clojure.tools.logging :as log]))

(defn -main []
  #_(println (apply str (map #(char (+ (int \a) %)) (range 1 10))))
  (let [a (agent (model/create)
                 :error-handler #(log/error %2))]
    (add-watch a ::view
               (fn [_ a old new]
                 (if (model/in-state? new ::model/state|closed)
                   (shutdown-agents)
                   (view/render old new (partial send a)))))
    (send a model/initialize)))