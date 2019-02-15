(ns christina.library.contract)

(defn not-nil?
  ([x]
   (not (nil? x)))
  ([x & xs]
   (and
     (not-nil? x)
     (every? not-nil? xs))))