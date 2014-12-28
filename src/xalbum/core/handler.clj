(ns xalbum.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]
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

(defn resource-response [path]
  (response/file-response (.getPath (io/file (io/resource path)))))

(defn render-photo [album-id photo-filename]
  (resource-response (format "test-albums/%s/%s" album-id photo-filename)))

(defn render-teaser [album-id]
  (resource-response (format "test-albums/%s/teaser.jpg" album-id)))

(defroutes app-routes
  (GET "/" [] (render-main))
  (GET "/album/:album-id/teaser.jpg" [album-id] (render-teaser album-id))
  (GET "/album/:album-id/:photo-filename" [album-id photo-filename] (render-photo album-id photo-filename))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
