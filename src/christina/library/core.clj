(ns christina.library.core)

(defn parse-int
  ([string]
   (parse-int string nil))
  ([string default]
   (try (int (Integer/parseInt string))
        (catch Throwable _ default))))

(defn parse-double
  ([string]
   (parse-int string nil))
  ([string default]
   (try (int (Double/parseDouble string))
        (catch Throwable _ default))))