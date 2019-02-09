(ns christina.application.hello.main
  (:require
    [christina.application.hello.model :as m]
    [christina.application.hello.controller :as c]))

(defn -main []
  (let [a (m/create)]
    (c/bind a)
    (m/initialize a)))