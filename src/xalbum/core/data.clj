(ns xalbum.core.data
  (:require [clojure.java.io :as io])
  (:import NaturalOrderComparator))

(defn local-storage [root]
  {:type :local
   :root root})

(defn is-album-dir? [dir]
  (not (= (.getName dir) ".DS_Store")))

(defn get-albums [storage]
  (for [album-dir (filter is-album-dir? (.listFiles (:root storage)))]
    (let [name (.getName album-dir)]
      {:name name
       :url (format "/album/%s" name)
       :teaser-url (format "/album/%s/teaser.jpg" name)})))

(defn is-photo-file? [file]
  (let [name (.getName file)
        is-teaser? #(boolean (re-seq #"teaser\.jpg$" %))
        is-jpeg? #(boolean (re-seq #"\.jpg$" %))]
    (and
     (is-jpeg? name)
     (not (is-teaser? name)))))

(defn get-album-photos [storage album-id]
  (let [album-dir (io/file (:root storage) album-id)]
    (for [photo-file (sort (NaturalOrderComparator.)
                           (filter is-photo-file? (.listFiles album-dir)))]
      {:url (format "/album/%s/%s" album-id (.getName photo-file))})))

(defn get-teaser-location [storage album-id]
  (io/file (:root storage) album-id "teaser.jpg"))

(defn get-photo-location [storage album-id photo-filename]
  (io/file (:root storage) album-id photo-filename))

