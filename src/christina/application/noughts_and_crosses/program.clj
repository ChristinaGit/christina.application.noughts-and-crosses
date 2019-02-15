(ns christina.application.noughts-and-crosses.program
  (:require
    [christina.application.noughts-and-crosses.model :as model]
    [christina.application.noughts-and-crosses.view.console :as console]
    [christina.application.noughts-and-crosses.resources :as resources]
    [christina.application.noughts-and-crosses.services :as services]
    [clojure.tools.logging :as log]))

(defn -main []
  (let [services (services/create resources/ru)
        agent (agent
                (model/create)
                :error-handler (fn [_ error]
                                 (log/error error)
                                 (shutdown-agents)))]
    (add-watch agent ::view
               (fn [_ a old new]
                 (if (model/in-state? new ::model/state|closed)
                   (shutdown-agents)
                   (console/render services old new (partial send a)))))
    (send agent model/initialize)))