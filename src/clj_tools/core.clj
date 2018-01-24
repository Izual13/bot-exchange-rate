(ns clj-tools.core
  (:require [org.httpkit.client :as http]
            [clojure.tools.nrepl.server :as nrepl.server]
            [clojure.data.json :as json]
            [cider.nrepl :as cider])
  (:gen-class))



(def bitcoin-url "https://api.coinmarketcap.com/v1/ticker/bitcoin/")
(def last-message-id (atom 1))
(def chat-ids (atom #{}))
(def is-updating (atom false))
(def token (get (System/getenv) "TOKEN" "token..."))

(def post-message-url (str "https://api.telegram.org/bot" token "/" "sendMessage"))
(def get-updates-url (str "https://api.telegram.org/bot" token "/" "getUpdates"))
(defn parse[str] (get (first (json/read-str str :key-fn keyword)) :price_usd))
(defn get-updates-request [] (json/write-str {"offset" (+ @last-message-id 1) "timeout" 60}))
(defn post-message-request [chat-id text] (json/write-str {"chat_id" chat-id
  "text" text
  "parse_mode" "Markdown"}))

(defn add-chat-id [id] (
  if (not (contains? @chat-ids id))
    (swap! chat-ids conj id)))


(defn remove-chat-id [id] (
  if (contains? @chat-ids id)
    (swap! chat-ids disj id)))

(defn update-last-message-id [id] (reset! last-message-id id))


(defn get-updates []  
  (if (not @is-updating) 
    (do (reset! is-updating true)
    (let [{:keys [status headers body error] :as resp} 
        @(http/post get-updates-url {:body (get-updates-request)
                                     :headers {"Content-Type" "application/json"}})]
        (if error 
          (println "Failed, exception: " error)    
          (println "HTTP GET success: " body))
        (reset! is-updating false)
        body))))
  
(defn get-usd []  
  (let [{:keys [status headers body error] :as resp} @(http/get bitcoin-url)]
    (if error 
      (println "Failed, exception: " error)    
      (parse body))))

(defn post-message [chat-id]
  (let [{:keys [status headers body error] :as resp} 
    @(http/post post-message-url {:body (post-message-request chat-id (str "*Bitcoin price*: _" (get-usd) "_"))
                                  :headers {"Content-Type" "application/json"}})]
  (if error 
    (println "Failed, exception: " error)    
    (println "HTTP GET success: " status))
    body))

    
(defn parse-updates[updates] 
  (if (not (nil? updates)) (:result (json/read-str updates :key-fn keyword))))

(defn change-action [text chat-id update-id] 
  (case text
    "/start" (add-chat-id chat-id)
    "/stop" (remove-chat-id chat-id)
    "/getExchange" (post-message chat-id)
    (println "Message: " text " chatId: " chat-id " updateId:" update-id)))

        
(defn message-handler [{update-id :update_id {{chat-id :id} :chat text :text} :message}] 
  (do (change-action text chat-id update-id) (update-last-message-id update-id)))        

(defn main-task [] (doseq [message (parse-updates (get-updates))] (message-handler message)))


(defn set-interval [callback ms] 
  (future (while true (do (callback) (Thread/sleep ms)))))

(defn -main [] (def job (set-interval #(main-task) 1000)))
  
  