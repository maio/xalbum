(ns xalbum.core.data
  (:require [clojure.java.io :as io])
  (:import NaturalOrderComparator))

(defn- is-photo-file? [file]
  (let [name (.getName file)
        is-teaser? #(boolean (re-seq #"teaser\.jpg$" %))
        is-jpeg? #(boolean (re-seq #"\.jpg$" %))]
    (and
     (is-jpeg? name)
     (not (is-teaser? name)))))

(defn- is-album-dir? [dir]
  (not (= (.getName dir) ".DS_Store")))

(defn- natural-sort [col]
  (sort (NaturalOrderComparator.) col))

(defn get-album-photos [storage album-id]
  (let [album-dir (io/file (:root storage) album-id)]
    (for [photo-file (natural-sort (filter is-photo-file? (.listFiles album-dir)))]
      {:url (format "/album/%s/%s" album-id (.getName photo-file))
       :thumb-url (format "/album/%s/thumb/%s" album-id (.getName photo-file))})))

(defn get-albums [storage]
  (for [album-dir (natural-sort (filter is-album-dir? (.listFiles (:root storage))))]
    (let [name (.getName album-dir)]
      {:name name
       :url (format "/album/%s" name)
       :teaser-url (format "/album/%s/teaser.jpg" name)})))

(defn get-teaser-location [storage album-id]
  (io/file (:root storage) album-id "teaser.jpg"))

(defn get-photo-location [storage album-id photo-filename]
  (io/file (:root storage) album-id photo-filename))

(defn create-album
  "Creates album."
  [storage album-name]
  (io/make-parents (io/file (:root storage) album-name "whatever")))

(defn local-storage [root]
  {:root root})
