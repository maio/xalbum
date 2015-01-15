(ns xalbum.core.handler-test
  (:require [clojure.test :refer :all]
            [kerodon.core :refer :all]
            [kerodon.test :refer :all]
            [ring.mock.request :as mock]
            [xalbum.core.handler :refer :all]
            [xalbum.core.data :as data]
            [clojure.java.io :as io]))

(deftest test-homepage
  (-> (session (build-app (data/temp-storage {"albumA" {} "albumB" {}})))
      (visit "/")
      (has (status? 200))
      (has (heading? "Welcome to xalbum"))
      (has (heading? "albumA"))
      (has (link? "" "/album/albumA"))
      (has (heading? "albumB"))
      (has (link? "" "/album/albumB"))))

(deftest test-album
  (-> (session (build-app (data/temp-storage {"albumA" {}})))
      (visit "/album/albumA")
      (has (status? 200))
      (has (heading? "albumA"))))

(deftest test-photo
  (-> (session (build-app (data/temp-storage {"albumA" {:photos ["1.jpg"]}})))
      (visit "/album/albumA/1.jpg")
      (has (status? 200))))
