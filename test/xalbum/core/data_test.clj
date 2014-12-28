(ns xalbum.core.data-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [xalbum.core.data :refer :all]))

(defn local-storage [root]
  {:type :local
   :root root})

(deftest test-data
  (let [storage (local-storage (io/file (io/resource "test-albums")))]
    (is (= (get-albums storage)
           [{:name "albumA"}
            {:name "albumB"}
            {:name "albumC"}]))))
