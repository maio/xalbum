(ns xalbum.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [xalbum.core.data :as data]))

(defn resource-response [path]
  (response/file-response (.getPath (io/file (io/resource path)))))

(html/deftemplate main-template "templates/main.html"
  [albums]
  [:div.album] (html/clone-for [album albums]
                               [:h2] (html/content (:name album))
                               [:img] (html/set-attr :src (:teaser-url album))))

(html/deftemplate album-template "templates/album.html"
  [album-id photos]
  [:title] (html/content (format "%s - xalbum" album-id))
  [:h1] (html/content album-id)
  [:img.thumbnail] (html/clone-for [photo photos]
                                   [:img] (html/set-attr :src (:url photo))
                                   [:img] (html/set-attr :height 150)))

(defn render-photo [album-id photo-filename]
  (resource-response (format "test-albums/%s/%s" album-id photo-filename)))

(defn render-teaser [album-id]
  (resource-response (format "test-albums/%s/teaser.jpg" album-id)))

(defn render-album [album-id]
  (let [storage (data/local-storage (io/file (io/resource "test-albums")))
        photos (data/get-album-photos storage album-id)]
    (album-template album-id photos)))

(defn render-main []
  (let [storage (data/local-storage (io/file (io/resource "test-albums")))
        albums (data/get-albums storage)]
    (main-template albums)))

(defroutes app-routes
  (GET "/" [] (render-main))
  (GET "/album/:album-id" [album-id] (render-album album-id))

  (GET "/album/:album-id/teaser.jpg" [album-id] (render-teaser album-id))
  (GET "/album/:album-id/:photo-filename" [album-id photo-filename] (render-photo album-id photo-filename))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
