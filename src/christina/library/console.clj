(ns christina.library.console
  (:require [christina.library.core :refer :all])
  (:import (java.util Scanner)))

(defn read-int
  ([] (read-int nil))
  ([default] (parse-int (read-line) default)))

(defn read-ints []
  (let [scanner (Scanner. ^String (read-line))]
    (loop [result []]
      (if (.hasNextInt scanner)
        (recur (conj result (.nextInt scanner)))
        result))))

(defn read-n-ints [n]
  (loop [result (read-ints)]
    (if (>= (count result) n)
      (into [] (take n result))
      (recur (into result (read-ints))))))
