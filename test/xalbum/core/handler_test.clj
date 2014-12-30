(ns xalbum.core.handler-test
  (:require [clojure.test :refer :all]
            [kerodon.core :refer :all]
            [kerodon.test :refer :all]
            [ring.mock.request :as mock]
            [xalbum.core.handler :refer :all]))

(deftest test-homepage
  (-> (session app)
      (visit "/")
      (has (status? 200))
      (has (heading? "Welcome to xalbum"))
      (has (heading? "albumA"))
      (has (link? "" "/album/albumA"))
      (has (heading? "albumB"))
      (has (link? "" "/album/albumB"))
      (has (heading? "albumC"))
      (has (link? "" "/album/albumC"))))

(deftest test-album
  (-> (session app)
      (visit "/album/albumA")
      (has (status? 200))
      (has (heading? "albumA"))))

(deftest test-photo
  (-> (session app)
      (visit "/album/albumA/1.jpg")
      (has (status? 200))))
