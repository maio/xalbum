(ns xalbum.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.core :refer [html]]))

(defroutes app-routes
  (GET "/" []
       (html [:body
              [:h1 "Welcome to xalbum"]
              [:p "album"]]))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
