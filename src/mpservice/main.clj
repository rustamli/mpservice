;; src/mpservice/main.clj
(ns mpservice.main
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [net.cgrand.enlive-html :as html])
  (:require [ring.middleware.params :refer [wrap-params]])
  (:require [clojure.data.json :as json]))

(def ^:dynamic *base-url* "https://members.parliament.uk/FindYourMP?SearchText=")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn fetch-mp-by-post-code [post-code]
  (fetch-url (str *base-url* (clojure.string/replace post-code #" " "+"))))

(defn mp-name [post-code]
  (map html/text (html/select (fetch-mp-by-post-code post-code) [:div.hero-banner :h1])))

(defn mp-image [page]
  (def url-attr 
    (first (filter (fn [attr] (clojure.string/starts-with? attr "url")) (html/attr-values (first (html/select page [:div.image-inner])) :style))))
  (subs url-attr 4 (- (count url-attr) 2)))

(def ^:dynamic *mp-info-selector*
     #{[:div.hero-banner :h1],
       [:div.contact-line :a]})

;; (mp-image (fetch-mp-by-post-code "SE1 6EJ"))

(defn mp-info [page]
  (map clojure.string/trim (map html/text (html/select page *mp-info-selector*))))

(defn mp-details [post-code]
  (def page (fetch-mp-by-post-code post-code))
  [(mp-image page) (mp-info page)])


(defn format-output [details]
  (json/write-str {
      :image (first details)
      :name (first (filter (fn[i] (clojure.string/includes? i "MP")) (get details 1)))
      :email (first (filter (fn[i] (clojure.string/includes? i "@")) (get details 1)))
      :phones (filter (fn[i] (clojure.string/starts-with? i "0")) (get details 1))}))

;;   (str (first details) "," (clojure.string/join "," (distinct (get details 1)))))
  
;;   (html/attr-values (html/select (fetch-mp-by-post-code post-code) [:div.image-inner]) :style))
;;   (map html/text (html/select (fetch-mp-by-post-code post-code) *mp-details-selector*)))


(defn- handler
  [{params :params}]
  {:status 200
   :body (format-output (mp-details (get params "postcode")))})

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
