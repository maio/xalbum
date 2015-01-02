(defproject xalbum "0.4.0-SNAPSHOT"
  :description "photo gallery"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.3"]
                 [enlive "1.1.5"]
                 [tempfile "0.2.0"]
                 [digest "1.4.4"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler xalbum.core.handler/app}
  :java-source-paths ["vendor"]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [kerodon "0.6.0-SNAPSHOT"]]
         :plugins [[quickie "0.3.6"]]
         :resource-paths ["resources" "test-resources"]}})
