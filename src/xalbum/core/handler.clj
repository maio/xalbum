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

(defn build-app [storage]
  (defroutes app-routes
    (GET "/" [] (main-template (data/get-albums storage)))

    (GET "/album/:album-id" [album-id]
         (album-template album-id (data/get-album-photos storage album-id)))
    (GET "/album/:album-id/teaser.jpg" [album-id]
         (file-response (data/get-teaser-location storage album-id)))
    (GET "/album/:album-id/:photo-filename" [album-id photo-filename]
         (file-response (data/get-photo-location storage album-id photo-filename)))
    ;; cache generated thumbs forever on client & proxy
    (photo/wrap-cache-forever
     (GET "/album/:album-id/thumb/:photo-filename" [album-id photo-filename]
          (let [photo-file (data/get-photo-location storage album-id photo-filename)]
            (file-response (photo/resize-to-fit photo-file {:width nil :height 300})))))

    (route/not-found "Not Found"))
  (wrap-defaults app-routes site-defaults))

(def storage (io/file (or (System/getenv "XALBUM_ROOT")
                          (io/resource "test-albums"))))

(def app
  (build-app storage))
