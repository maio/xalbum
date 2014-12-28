(ns xalbum.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [xalbum.core.data :as data]))

(html/deftemplate main-template "templates/main.html"
  [albums]
  [:div.album] (html/clone-for [album albums]
                               [:h2] (html/content (:name album))
                               [:img] (html/set-attr :src (:teaser-url album))))

(defn render-main []
  (let [storage (data/local-storage (io/file (io/resource "test-albums")))
        albums (data/get-albums storage)]
    (main-template albums)))

(defroutes app-routes
  (GET "/" [] (render-main))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
