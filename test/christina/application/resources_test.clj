(ns christina.application.resources-test
  (:require
    [clojure.test :refer :all]
    [clojure.tools.logging :as log]
    [christina.library.resources :as res]))

(deftest can-resolve-single-resource
  (let [tag "test-tag"
        resource "test-resource"
        container (as-> (res/container) it
                        (res/register it [tag] resource))]
    (is (= resource (res/resolve container [tag])))))

(deftest can-resolve-several-resources
  (let [trs (reduce
              #(conj %1 (format "test-tag-%d" %2) (format "test-value-%d" %2))
              []
              (range 0 10))
        container (as-> (res/container) it
                        (apply res/register it trs))]
    (is (let [tags (take-nth 2 trs)
              resources (take-nth 2 (rest trs))]
          (= resources
             (apply res/resolve container tags))))))