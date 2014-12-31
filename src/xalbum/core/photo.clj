(ns xalbum.core.photo
  (:require [tempfile.core :refer [tempdir]]
            [ring.util.response :refer [header status file-response]]
            [compojure.core :refer [defroutes context GET]]
            [clojure.java.shell :refer [sh]]
            [digest :refer [md5]]
            [clojure.java.io :as io]))

;; store resized photos here
(def photo-temp (tempdir))

;; cache photos forever
(def cache-control "max-age=31556926, public")

;; http://www.mnot.net/cache_docs/
(defn wrap-cache-forever
  "Wrap an app such that all request will be cached forever. This is
  useful for immutable content.

  Any request which contains If-Modified-Since or If-None-Match
  headers will get HTTP 304 Not-Modified response.

  All responses will get Cache-Control max-age set to 3167356 seconds
  so that browser will keep the cached file around for some time."
  [handler]
  (fn [{headers :headers :as req}]
    (if (or (headers "if-modified-since")
            (headers "if-none-match"))
      (status {} 304)
      (when-let [res (handler req)]
        (header res "Cache-Control" cache-control)))))

(defn fit-geometry [{width :width height :height}]
  (str width "x" height ">"))

(defn resize-to-fit [src size]
  (let [geometry (fit-geometry size)
        resized-hash (md5 (format "%s/%s" (.getAbsolutePath src) geometry))
        resized (io/file photo-temp (format "%s.jpg" resized-hash))]
    ;; TODO: strip useless EXIF fields
    ;;       keep some useful ones (e.g. aperture) + color profile setting
    (sh "convert" "-auto-orient" "-quality" "80"
        "-resize" geometry (str src) (str resized))
    resized))
