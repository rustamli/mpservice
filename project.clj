;; project.clj
(defproject mpservice "0.1.0-SNAPSHOT"
  :main ^:skip-aot mpservice.main

  :pom-plugins
  [[com.google.cloud.tools/jib-maven-plugin "2.1.0"
    (:configuration
      [:from [:image "gcr.io/distroless/java:11"]]
      [:to [:image "gcr.io/kalimantancloud/mpservice"]]
      [:container
       [:mainClass "mpservice.main"]
       [:creationTime "USE_CURRENT_TIMESTAMP"]])]]

  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [ring/ring-core "1.8.1"]
   [ring/ring-jetty-adapter "1.8.1"]
   [enlive-cljhtml "0.0.2"]])

