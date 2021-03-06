(ns xalbum.core.photo
  (:require [tempfile.core :refer [tempdir]]
            [ring.util.response :refer [header status]]
            [clojure.java.shell :refer [sh]]
            [digest :refer [md5]]
            [clojure.java.io :as io]))

;; store resized photos here
(def photo-temp (tempdir))

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
        (header res "Cache-Control" "max-age=31556926, public")))))

(defn fit-geometry
  "Imagick fit geometry."
  [{width :width height :height}]
  (str width "x" height ">"))

(defn resize-to-fit [src size]
  (let [resized (io/file photo-temp (format "%s.jpg" (md5 (str src (sort size)))))]
    (when-not (.exists resized)
      (sh "convert" "-auto-orient" "-quality" "75"
          "-resize" (fit-geometry size) (str src) (str resized)))
    resized))
