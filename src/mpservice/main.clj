;; src/mpservice/main.clj
(ns mpservice.main
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn- handler
  [_req]
  {:status 200
   :body   "Hello world!"})

(defn serve
  [port]
  (run-jetty
    handler
    {:host  "0.0.0.0"
     :port  port
     ;; run-jetty takes over the thread by default, which is bad in a REPL
     :join? false}))

(defn -main
  [& _args]
  (serve (Long/parseLong (System/getenv "PORT"))))
