(ns clj-tools.core
  (:require [org.httpkit.client :as http]
            [clojure.tools.nrepl.server :as nrepl.server]
            [clojure.data.json :as json]
            ; [clojure.core.async :as async :refer :all]
            [cider.nrepl :as cider]))




(defn parse[str] 
  ;(println str)
  (get (first (json/read-str str :key-fn keyword)) :price_usd))

(def last-message-id (atom 1))
(def token (get (System/getenv) "TOKEN" "token..."))
(def get-updates-url (str "https://api.telegram.org/bot" token "/" "getUpdates"))
(defn get-updates-request [] (json/write-str {"offset" (+ @last-message-id 1) "timeout" 50}))
(def chat-ids (atom #{}))


(def post-message-url (str "https://api.telegram.org/bot" token "/" "sendMessage"))

(def bitcoin-url "https://api.coinmarketcap.com/v1/ticker/bitcoin/")
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

(add-chat-id 12)    
(add-chat-id 123)    
(remove-chat-id 12)    
(println @chat-ids)


(println get-updates-url)

(defn get-updates []  
  (let [{:keys [status headers body error] :as resp} 
    @(http/post get-updates-url {:body (get-updates-request)})]
  (if error 
    (println "Failed, exception: " error)    
    (println "HTTP GET success: " body))
    body))





(defn get-usd []  
  (let [{:keys [status headers body error] :as resp} @(http/get bitcoin-url)]
    (if error 
      (println "Failed, exception: " error)    
      (parse body))))

(defn post-message [chat-id]
  (let [{:keys [status headers body error] :as resp} 
    @(http/post post-message-url {:body (post-message-request chat-id (get-usd))
                                  :headers {"Content-Type" "application/json"}})]
  (if error 
    (println "Failed, exception: " error)    
    (println "HTTP GET success: " body))
    body))

(post-message 123)

 

(defn set-interval [callback ms] 
  (future (while true (do (Thread/sleep ms) (callback)))))
      
(def job (set-interval #(spit "log.txt" (str (get-usd) "\n") :append true) 6000))
      
(future-cancel job)
      
(get-usd)

; (clojure.set/union #{1 2} #{3 4})
(get-updates)


(def updates "{\"ok\":true,\"result\":
  [{\"update_id\":12,\n\"message\":{\"message_id\":1234,\"chat\":{\"id\":765,\"first_name\":\"m\",\"last_name\":\"q\",\"username\":\"mq\",\"type\":\"private\"},\"date\":154394,\"text\":\"\\u044e\"}},
  ,{\"update_id\":123,\n\"message\":{\"message_id\":123,\"chat\":{\"id\":987,\"first_name\":\"m\",\"last_name\":\"q\",\"username\":\"mq\",\"type\":\"private\"},\"date\":154394,\"text\":\"\\u044e\"}}
  ]}")

(defn parse-chat-ids[] 
  (json/read-str updates :key-fn keyword))

  (parse-chat-ids)

 (->> (parse-chat-ids) :result 
  (map (fn [{update-id :update_id {{chat-id :id} :chat text :text} :message}] (str update-id " " chat-id " " text))))


  
 
  (-> "a b c d" 
    ; .toUpperCase 
    (.replace "a" "X") 
    ; (.split " ") 
    first)


;; deeper destructuring
(def ds [ {:a 1 :b 2 :c [:foo :bar]}
  {:a 9 :b 8 :c [:baz :zoo]}
  {:a 1 :b 2 :c [:dog :cat]} ])

(->> ds 
(map (fn [{a :a, b :b, [lhs rhs] :c}] 
    [(str "a:" a " c2:" rhs)])))
;;=> (["a:1 c2::bar"] ["a:9 c2::zoo"] ["a:1 c2::cat"])