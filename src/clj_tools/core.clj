(ns clj-tools.core
  (:require [org.httpkit.client :as http]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;;(foo "123")




(http/get "https://google.com" {:as :text}
  (fn [{:keys [status headers body error opts]}]
       (println status)
    ))


    (println 12)

(http/get "http://site.com/string.txt" {:as :auto})



