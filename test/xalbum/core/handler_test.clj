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
      (within [:h1]
              (has (text? "Welcome to xalbum")))))
