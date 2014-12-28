(ns xalbum.core.data-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [xalbum.core.data :refer :all]))

(deftest test-data
  (let [storage (local-storage (io/file (io/resource "test-albums")))]
    (is (= (get-albums storage)
           [{:name "albumA" :teaser-url "/album/albumA/teaser.jpg"}
            {:name "albumB" :teaser-url "/album/albumB/teaser.jpg"}
            {:name "albumC" :teaser-url "/album/albumC/teaser.jpg"}]))))
