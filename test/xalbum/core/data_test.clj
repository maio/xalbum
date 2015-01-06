(ns xalbum.core.data-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tempfile.core :refer [tempdir]]
            [xalbum.core.data :refer :all]))

(defn- touch-album-photo-file
  "Create fake photo file. Suitable for tests which don't care about file contents."
  [storage album-id photo-filename]
  (spit (get-photo-location storage album-id photo-filename) ""))

(defn- temp-storage
  "Create temporary local storage and initialize it with given list of albums."
  ([] (temp-storage {}))
  ([albums]
   (let [s (local-storage (tempdir))]
     (doseq [[album-name {photos :photos}] albums]
       (create-album s album-name)
       (doseq [photo-filename photos]
         (touch-album-photo-file s album-name photo-filename)))
     s)))

(deftest test-get-albums
  (let [s (temp-storage {"albumA" {} "albumB" {}})]
    (is (= (get-albums s)
           [{:name "albumA" :teaser-url "/album/albumA/teaser.jpg" :url "/album/albumA"}
            {:name "albumB" :teaser-url "/album/albumB/teaser.jpg" :url "/album/albumB"}]))))

(deftest test-get-album-photos
  (testing "photos should be ordered using natural sort"
    (let [s (temp-storage {"albumA" {:photos ["19.jpg" "2.jpg" "100.jpg"]}})]
      (is (= (get-album-photos s "albumA")
             [{:url "/album/albumA/2.jpg" :thumb-url "/album/albumA/thumb/2.jpg"}
              {:url "/album/albumA/19.jpg" :thumb-url "/album/albumA/thumb/19.jpg"}
              {:url "/album/albumA/100.jpg" :thumb-url "/album/albumA/thumb/100.jpg"}])))))

(deftest test-get-photo-location
  (let [s (temp-storage)]
    (is (= (get-photo-location s "albumA" "1.jpg")
           (io/file (:root s) "albumA" "1.jpg")))))

(deftest test-get-teaser-location
  (let [s (temp-storage)]
    (is (= (get-teaser-location s "albumA")
           (io/file (:root s) "albumA" "teaser.jpg")))))
