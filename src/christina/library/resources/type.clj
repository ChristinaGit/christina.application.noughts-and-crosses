(ns christina.library.resources.type
  (:require [christina.library.contract :as contract])
  (:import (clojure.lang Keyword)))

(derive ::number ::type)

(derive ::string ::type)

(derive ::format ::string)

(derive ::plural ::string)

(derive ::plural-format ::format)
(derive ::plural-format ::plural)

(defn is? [^Keyword type]
  {:pre [(contract/not-nil? type)]}
  (isa? type ::type))
