(ns christina.library.resources
  (:refer-clojure :exclude [resolve]))

(defn config
  ([] (config :no-tag-defaults 1))
  ([tag-defaults tag-size]
   {:tag-defaults tag-defaults
    :tag-size     tag-size}))

(defn container
  ([]
   (container (config)))
  ([config]
   {:config    config
    :resources {}}))

(defn register
  ([container tag resource & trs]
   (assoc container :resources
                    (apply assoc (:resources container) tag resource trs))))

(defn- apply-tag-defaults [tag config]
  (let [tag-size (:tag-size config)
        tag-defaults (:tag-defaults config)]
    (if (= tag-defaults :no-tag-defaults)
      tag
      (vec (dotimes [index tag-size]
             (let [tag-part (nth tag index)]
               (if (= tag-part :tag-default)
                 (nth tag-defaults index)
                 tag-part)))))))

(defn resolve
  ([container tag]
   (let [config (:config container)
         resources (:resources container)]
     (get resources (apply-tag-defaults tag config) :resource-not-found)))
  ([container tag & tags]
   (map #(resolve container %) (cons tag tags))))

(defn resolve-format
  [container tag & args]
  (apply format (resolve container tag) args))