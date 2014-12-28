(ns xalbum.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.core :refer [html]]
            [clojure.java.io :as io]
            [xalbum.core.data :as data]))

(defn render-album [album]
  [:h1 (:name album)])

(defn render-main []
  (let [storage (data/local-storage (io/file (io/resource "test-albums")))
        albums (data/get-albums storage)]
    (html [:body
           [:h1 "Welcome to xalbum"]
           (map render-album albums)])))

(defroutes app-routes
  (GET "/" [] (render-main))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
