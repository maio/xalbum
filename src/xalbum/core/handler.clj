(ns xalbum.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [xalbum.core.data :as data]))

(defn file-response [file]
  (response/file-response (.getPath file)))

(html/deftemplate main-template "templates/main.html"
  [albums]
  [:div.album] (html/clone-for [album albums]
                               [:h2] (html/content (:name album))
                               [:a] (html/set-attr :href (:url album))
                               [:img] (html/set-attr :src (:teaser-url album))))

(html/deftemplate album-template "templates/album.html"
  [album-id photos]
  [:title] (html/content (format "%s - xalbum" album-id))
  [:h1] (html/content album-id)
  [:a.thumbnail] (html/clone-for [photo photos]
                                 [:a] (html/set-attr :href (:url photo))
                                 [:img] (html/set-attr :src (:url photo))))

(defn render-photo [photo-file]
  (file-response photo-file))

(defn render-teaser [teaser-file]
  (file-response teaser-file))

(defn render-album [storage album-id]
  (album-template album-id (data/get-album-photos storage album-id)))

(defn render-main [storage]
  (main-template (data/get-albums storage)))

(def root (io/file (or (System/getenv "XALBUM_ROOT") (io/resource "test-albums"))))

(let [storage (data/local-storage root)]
  (defroutes app-routes
    (GET "/" [] (render-main storage))
    (GET "/album/:album-id" [album-id] (render-album storage album-id))

    (GET "/album/:album-id/teaser.jpg" [album-id]
         (render-teaser (data/get-teaser-location storage album-id)))
    (GET "/album/:album-id/:photo-filename" [album-id photo-filename]
         (render-photo (data/get-photo-location storage album-id photo-filename)))
    (route/not-found "Not Found")))

(def app
  (wrap-defaults app-routes site-defaults))
