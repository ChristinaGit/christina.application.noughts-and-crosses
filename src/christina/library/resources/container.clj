(ns christina.library.resources.container
  (:refer-clojure :exclude [resolve])
  (:require
    [christina.library.contract :as contract]
    [christina.library.resources.type :as type]
    [christina.library.resources.value :as value]
    [clojure.tools.logging :as log])
  (:import (clojure.lang Keyword)))

(def ^:dynamic *this* nil)

(defn create []
  {:post [contract/not-nil?]}
  {::values {}})

(defn- values [this]
  {:pre  [(contract/not-nil? this)]
   :post [contract/not-nil?]}
  (::values this))

(defn values* [] (values *this*))

(defn add [this ^Keyword id ^Keyword type value]
  {:pre  [(contract/not-nil? this id type)
          (type/is? type)]
   :post [contract/not-nil?]}
  (assoc this
    ::values (assoc (values this) id (value/create type value))))

(defn add* [^Keyword id ^Keyword type value]
  (add *this* id type value))

(defn- throw->wrong-type [actual expected]
  (throw (ex-info "Wrong resource type." {:actual actual :expected expected})))

(defn resolve [this ^Keyword id ^Keyword type]
  {:pre [(contract/not-nil? this id type)
         (type/is? type)]}
  (let [value (get (values this) id nil)
        actual-type (value/type value)]
    (if (isa? actual-type type)
      (value/value value)
      (throw->wrong-type actual-type type))))

(defn resolve* [^Keyword id ^Keyword type]
  (resolve *this* id type))

(defn add-string [this ^Keyword id ^String string]
  {:pre  [(contract/not-nil? this id)]
   :post [contract/not-nil?]}
  (add this id ::type/string string))

(defn resolve-string [this ^Keyword id]
  {:pre [(contract/not-nil? this id)]}
  (resolve this id ::type/string))

(defn resolve-string* [^Keyword id]
  (resolve-string *this* id))

(defn add-format [this ^Keyword id ^String format]
  {:pre  [(contract/not-nil? this id)]
   :post [contract/not-nil?]}
  (add this id ::type/format format))

(defn resolve-format [this ^Keyword id & args]
  {:pre [(contract/not-nil? this id)]}
  (apply format (resolve this id ::type/format) args))

(defn resolve-format* [^Keyword id & args]
  (apply resolve-format *this* id args))