(ns clj-tools.core-test
  (:require [clojure.test :refer :all]
            [clj-tools.core :refer :all]))


(def json-updates "{\"ok\":true,\"result\":
  [{\"update_id\":12,\n\"message\":{\"message_id\":1234,\"chat\":{\"id\":142657573,\"first_name\":\"m\",\"last_name\":\"q\",\"username\":\"mq\",\"type\":\"private\"},\"date\":154394,\"text\":\"/getExchange\"}},
  ,{\"update_id\":123,\n\"message\":{\"message_id\":123,\"chat\":{\"id\":987,\"first_name\":\"m\",\"last_name\":\"q\",\"username\":\"mq\",\"type\":\"private\"},\"date\":154394,\"text\":\"\\u044e\"}}
  ]}")

  (parse-updates json-updates)

(deftest parse-updates-test
  (testing "parse updates"
    (is (=  (parse-updates json-updates) {:ok true, :result [{:update_id 12, :message {:message_id 1234, :chat {:id 142657573, :first_name "m", :last_name "q", :username "mq", :type "private"}, :date 154394, :text "/getExchange"}} {:update_id 123, :message {:message_id 123, :chat {:id 987, :first_name "m", :last_name "q", :username "mq", :type "private"}, :date 154394, :text "ю"}}]}))))
    
