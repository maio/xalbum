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
      (has (heading? "albumB"))
      (has (heading? "albumC"))))
