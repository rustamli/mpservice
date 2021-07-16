;; src/mpservice/main.clj
(ns mpservice.main
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [net.cgrand.enlive-html :as html])
  (:require [ring.middleware.params :refer [wrap-params]]))

(def ^:dynamic *base-url* "https://members.parliament.uk/FindYourMP?SearchText=")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn mp-name [post-code]
  (map html/text (html/select (fetch-url (str *base-url* (clojure.string/replace post-code #" " "+"))) [:div.hero-banner :h1])))

(defn- handler
  [{params :params}]
  {:status 200
   :body (mp-name (get params "postcode"))})

(def app-handler
  (-> handler
      (wrap-params)
  ))

(defn serve
  [port]
  (run-jetty
    app-handler
    {:host  "0.0.0.0"
     :port  port
     ;; run-jetty takes over the thread by default, which is bad in a REPL
     :join? false}))

(defn -main
  [& _args]
  (serve (Long/parseLong (System/getenv "PORT"))))
