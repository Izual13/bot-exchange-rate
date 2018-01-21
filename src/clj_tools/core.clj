(ns clj-tools.core
  (:require [org.httpkit.client :as http]
            [clojure.tools.nrepl.server :as nrepl.server]
            [clojure.data.json :as json]
            ; [clojure.core.async :as async :refer :all]
            [cider.nrepl :as cider]))



(def post-message-url (str "https://api.telegram.org/bot" token "/" "sendMessage"))
(def bitcoin-url "https://api.coinmarketcap.com/v1/ticker/bitcoin/")
(def last-message-id (atom 1))
(def chat-ids (atom #{}))
(def is-updating (atom false))
(def token (get (System/getenv) "TOKEN" "token..."))
(def get-updates-url (str "https://api.telegram.org/bot" token "/" "getUpdates"))
(defn parse[str] (get (first (json/read-str str :key-fn keyword)) :price_usd))
(defn get-updates-request [] (json/write-str {"offset" (+ @last-message-id 1) "timeout" 10}))
(defn post-message-request [chat-id text] (json/write-str {"chat_id" chat-id
  "text" text
  "parse_mode" "Markdown"}))

;(swap! chat-ids conj 6)

(defn add-chat-id [id] (
  if (not (contains? @chat-ids id))
    (swap! chat-ids conj id)))


(defn remove-chat-id [id] (
  if (contains? @chat-ids id)
    (swap! chat-ids disj id)))


(defn update-last-message-id [id] (reset! last-message-id id))


(defn get-updates []  
  (if (not @is-updating) (do 
    (reset! is-updating true)
    (let [{:keys [status headers body error] :as resp} 
      @(http/post get-updates-url {:body (get-updates-request)
                                   :headers {"Content-Type" "application/json"}})]
    (if error 
      (println "Failed, exception: " error)    
      (println "HTTP GET success: " body))
    (reset! is-updating false)
    (println status)
    body))))
  
(get-updates)
(println @is-updating)
(reset! is-updating false)
(main-task)
(println @is-updating)


(defn get-usd []  
  (let [{:keys [status headers body error] :as resp} @(http/get bitcoin-url)]
    (if error 
      (println "Failed, exception: " error)    
      (parse body))))

(defn post-message [chat-id]
  (let [{:keys [status headers body error] :as resp} 
    @(http/post post-message-url {:body (post-message-request chat-id (str "Bitcoin price: " (get-usd)))
                                  :headers {"Content-Type" "application/json"}})]
  (if error 
    (println "Failed, exception: " error)    
    (println "HTTP GET success: " body))
    body))


      
    (get-usd)

    ; (clojure.set/union #{1 2} #{3 4})
    (get-updates)
    
    
   
    
    
    (defn parse-updates[updates] 
      (if (not (nil? updates)) (json/read-str updates :key-fn keyword)))
    
    
    (defn change-action [text chat-id update-id] 
      (case text
        "/start" (add-chat-id chat-id)
        "/stop" (remove-chat-id chat-id)
        "/getExchange" (post-message chat-id)
        (println text)
    ))

(defn main-task [] 
  (->> (parse-updates (get-updates)) :result 
  (map (fn [{update-id :update_id {{chat-id :id} :chat text :text} :message}] 
    (do (change-action text chat-id update-id) (update-last-message-id update-id))))))    
  
    
(nil? (get-updates))     
(main-task)

(defn set-interval [callback ms] 
  (future (while true (do (Thread/sleep ms) (callback)))))
      
(def job (set-interval #(main-task) 1000))

(main-task)

(future-cancel job)
