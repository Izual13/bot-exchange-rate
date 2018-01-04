(ns clj-tools.core
  (:require [org.httpkit.client :as http]
            [clojure.tools.nrepl.server :as nrepl.server]
            [cider.nrepl :as cider]))


(http/get "https://google.com" {:as :text}
  (fn [{:keys [status headers body error opts]}]
       (println status)
       (println body)
    ))

    (println 12)

(http/get "http://site.com/string.txt" {:as :auto})



