(ns xalbum.core.data-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [xalbum.core.data :refer :all]))

(def root (io/file (io/resource "test-albums")))
;; test-albums/
;; ├── albumA
;; │   ├── 1.jpg
;; │   ├── 2.jpg
;; │   ├── 19.jpg
;; │   ├── 21.jpg
;; │   └── teaser.jpg
;; ├── albumB
;; │   └── teaser.jpg
;; └── albumC
;;     └── teaser.jpg

(deftest test-get-albums
  (is (= (get-albums (local-storage root))
         [{:name "albumA" :teaser-url "/album/albumA/teaser.jpg" :url "/album/albumA"}
          {:name "albumB" :teaser-url "/album/albumB/teaser.jpg" :url "/album/albumB"}
          {:name "albumC" :teaser-url "/album/albumC/teaser.jpg" :url "/album/albumC"}])))

(deftest test-get-album-photos
  (is (= (get-album-photos (local-storage root) "albumA")
         [{:url "/album/albumA/1.jpg"}
          {:url "/album/albumA/2.jpg"}
          {:url "/album/albumA/19.jpg"}
          {:url "/album/albumA/21.jpg"}])))

(deftest test-get-photo-location
  (is (= (get-photo-location (local-storage root) "albumA" "1.jpg")
         (io/file root "albumA" "1.jpg"))))

(deftest test-get-teaser-location
  (is (= (get-teaser-location (local-storage root) "albumA")
         (io/file root "albumA" "teaser.jpg"))))
