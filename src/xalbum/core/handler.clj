(ns xalbum.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [xalbum.core.data :as data]
            [xalbum.core.photo :as photo]))

(defn file-response [file]
  (response/file-response (.getPath file)))

(html/deftemplate album-template "templates/album.html"
  [album-name photos]
  [:title] (html/content (format "%s - xalbum" album-name))
  [:h1] (html/content album-name)
  [:a.thumbnail] (html/clone-for [photo photos]
                                 [:a] (html/set-attr :href (:url photo))
                                 [:img] (html/set-attr :src (:thumb-url photo))))

(html/deftemplate main-template "templates/main.html"
  [albums]
  [:div.album] (html/clone-for [album albums]
                               [:h2] (html/content (:name album))
                               [:a] (html/set-attr :href (:url album))
                               [:img] (html/set-attr :src (:teaser-url album))))

(defn render-photo [photo-file]
  (file-response photo-file))

(defn render-photo-thumb [photo-file]
  (file-response (photo/resize-to-fit photo-file {:width nil :height 300})))

(defn render-teaser [teaser-file]
  (file-response teaser-file))

(defn render-album [storage album-id]
  (album-template album-id (data/get-album-photos storage album-id)))

(defn render-main [storage]
  (main-template (data/get-albums storage)))

(defn build-app [storage]
  (defroutes app-routes
    (GET "/" [] (render-main storage))

    (GET "/album/:album-id" [album-id] (render-album storage album-id))
    (GET "/album/:album-id/teaser.jpg" [album-id]
         (render-teaser (data/get-teaser-location storage album-id)))
    (GET "/album/:album-id/:photo-filename" [album-id photo-filename]
         (render-photo (data/get-photo-location storage album-id photo-filename)))
    ;; cache generated thumbs forever on client & proxy
    (photo/wrap-cache-forever
     (GET "/album/:album-id/thumb/:photo-filename" [album-id photo-filename]
          (render-photo-thumb (data/get-photo-location storage album-id photo-filename))))

    (route/not-found "Not Found"))
  (wrap-defaults app-routes site-defaults))

(def root (io/file (or (System/getenv "XALBUM_ROOT")
                       (io/resource "test-albums"))))

(def app
  (build-app (data/local-storage root)))
