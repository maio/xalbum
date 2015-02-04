(ns xalbum.core.data
  (:require [clojure.java.io :as io]
            [tempfile.core :refer [tempdir]])
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
  (let [album-dir (io/file storage album-id)]
    (for [photo-file (natural-sort (filter is-photo-file? (.listFiles album-dir)))]
      {:url (format "/album/%s/%s" album-id (.getName photo-file))
       :thumb-url (format "/album/%s/thumb/%s" album-id (.getName photo-file))})))

(defn get-albums [storage]
  (for [album-dir (natural-sort (filter is-album-dir? (.listFiles storage)))]
    (let [id (.getName album-dir)]
      {:name id  ;; for local storage album name is it's id (dir name)
       :url (format "/album/%s" id)
       :teaser-url (format "/album/%s/teaser.jpg" id)})))

(defn get-teaser-location [storage album-id]
  (io/file storage album-id "teaser.jpg"))

(defn get-photo-location [storage album-id photo-filename]
  (io/file storage album-id photo-filename))

;; utility functions for tests
(defn create-album
  "Creates album."
  [storage album-name]
  (io/make-parents (io/file storage album-name "whatever")))

(defn- touch-album-photo-file
  "Create fake photo file. Suitable for tests which don't care about file contents."
  [storage album-id photo-filename]
  (spit (get-photo-location storage album-id photo-filename) ""))

(defn temp-storage
  "Create temporary local storage and initialize it with given list of albums."
  ([] (temp-storage {}))
  ([albums]
   (let [s (tempdir)]
     (doseq [[album-name {photos :photos}] albums]
       (create-album s album-name)
       (doseq [photo-filename photos]
         (touch-album-photo-file s album-name photo-filename)))
     s)))
