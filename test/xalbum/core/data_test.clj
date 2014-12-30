(ns xalbum.core.data-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [xalbum.core.data :refer :all]))

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
  (let [storage (local-storage (io/file (io/resource "test-albums")))]
    (is (= (get-albums storage)
           [{:name "albumA" :teaser-url "/album/albumA/teaser.jpg" :url "/album/albumA"}
            {:name "albumB" :teaser-url "/album/albumB/teaser.jpg" :url "/album/albumB"}
            {:name "albumC" :teaser-url "/album/albumC/teaser.jpg" :url "/album/albumC"}]))))

(deftest test-get-album-photos
  (let [storage (local-storage (io/file (io/resource "test-albums")))]
    (is (= (get-album-photos storage "albumA")
           [{:url "/album/albumA/1.jpg"}
            {:url "/album/albumA/2.jpg"}
            {:url "/album/albumA/19.jpg"}
            {:url "/album/albumA/21.jpg"}]))))
