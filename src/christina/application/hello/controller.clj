(ns christina.application.hello.controller
  (:require
    [christina.application.hello.model :as m]
    [christina.application.hello.view :as v]))

(defn- render [a old new]
  (let [old-state (m/get-state old)
        new-state (m/get-state new)]
    (if (not= old-state new-state)
      (case new-state
        :state/initialized (v/show-empty-state
                             (partial m/change-user-name a))
        :state/authorized (v/show-numbers-input
                            (m/get-user-name new)
                            (partial m/change-args a))
        :state/with-args (v/show-result (m/get-user-name new) (m/get-result new)))
      (throw (RuntimeException.)))))

(defn bind [a]
  (add-watch a ::controller #(render %2 %3 %4)))