(ns christina.application.noughts-and-crosses.domain.sign
  (:require [christina.library.contract :as contract])
  (:import (clojure.lang Keyword)))

(derive ::nought ::type)
(derive ::cross ::type)

(defn is? [^Keyword this]
  {:pre [(contract/not-nil? this)]}
  (isa? this ::type))
