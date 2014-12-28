(ns xalbum.core.data
  (:require [clojure.java.io :as io]))

(defn get-albums [storage]
  (for [album-dir (.listFiles (:root storage))]
    {:name (.getName album-dir)}))
