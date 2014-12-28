(ns xalbum.core.data
  (:require [clojure.java.io :as io]))

(defn local-storage [root]
  {:type :local
   :root root})

(defn get-albums [storage]
  (for [album-dir (.listFiles (:root storage))]
    (let [name (.getName album-dir)]
      {:name name
       :teaser-url (format "/album/%s/teaser.jpg" name)})))
