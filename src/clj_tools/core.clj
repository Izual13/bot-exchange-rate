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
(defn get-updates-request [] (json/write-str {"offset" (+ last-message-id 1) "timeout" 50}))
(def chat-ids (atom #{}))


(def post-message-url (str "https://api.telegram.org/bot" token "/" "sendMessage"))
(defn post-message-request [chat-id text] (json/write-str {"chat_id" chat-id 
  "text" text
  "parse_mode" "Markdown"}))

;(swap! chat-ids conj 6)

;(println @chat-ids)


(println get-updates-url)

(defn get-updates []  
  (let [{:keys [status headers body error] :as resp} 
    @(http/post get-updates-url {:body (get-updates-request)})]
  (if error 
    (println "Failed, exception: " error)    
    (println "HTTP GET success: " body))
    body))



    (defn post-message [chat-id]
      (let [{:keys [status headers body error] :as resp} 
        @(http/post post-message-url {:body (post-message-request chat-id (get-usd))
                                      :headers {"Content-Type" "application/json"}})]
      (if error 
        (println "Failed, exception: " error)    
        (println "HTTP GET success: " body))
        body))

       (post-message 123)


(def bitcoin-url "https://api.coinmarketcap.com/v1/ticker/bitcoin/")


(defn get-usd []  
  (let [{:keys [status headers body error] :as resp} @(http/get bitcoin-url)]
    (if error 
      (println "Failed, exception: " error)    
      (parse body))))
  

(defn set-interval [callback ms] 
  (future (while true (do (Thread/sleep ms) (callback)))))
      
(def job (set-interval #(spit "log.txt" (str (get-usd) "\n") :append true) 6000))
      
(future-cancel job)
      
(get-usd)

; (clojure.set/union #{1 2} #{3 4})
(get-updates)
