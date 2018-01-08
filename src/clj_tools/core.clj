(ns clj-tools.core
  (:require [org.httpkit.client :as http]
            [clojure.tools.nrepl.server :as nrepl.server]
            [clojure.data.json :as json]
            [clojure.core.async :as async :refer :all]
            [cider.nrepl :as cider]))




(defn parse[str] 
  ;(println str)
  (get (first (json/read-str str :key-fn keyword)) :price_usd))

(def last-message-id 0)
(def token (get (System/getenv) "TOKEN" "token..."))
(def get-updates-url (str "https://api.telegram.org/bot" token "/" "getUpdates"))
(defn get-updates-request [] (json/write-str {"offset" (+ last-message-id 0) "timeout" 50}))

(println get-updates-url)

(defn get-updates []  
  (let [{:keys [status headers body error] :as resp} 
    @(http/post get-updates-url {:body (get-updates-request)})]
  (if error 
    (println "Failed, exception: " error)    
    (println "HTTP GET success: " body))))


(def bitcoin-url "https://api.coinmarketcap.com/v1/ticker/bitcoin/")


(defn get-usd []  
  (let [{:keys [status headers body error] :as resp} @(http/get bitcoin-url)]
    (if error 
      (println "Failed, exception: " error)    
      (parse body))))
  

(defn set-interval [callback ms] 
  (future (while true (do (Thread/sleep ms) (callback)))))
      
; (def job (set-interval #(spit "log.txt" (str (get-usd) "\n") :append true) 1000))
      
(future-cancel job)
      
(get-usd)
(get-updates)
