(ns clj-tools.nrepl
    (:require [clojure.tools.nrepl.server :as nrepl.server]
              [cider.nrepl :as cider]))
  
  (defn -main []
    (let [port 8000]
      (nrepl.server/start-server 
        :port port
        :handler cider/cider-nrepl-handler)
        (spit ".nrepl-port" port)
        (.addShutdownHook (Runtime/getRuntime)
                          (Thread. #(clojure.java.io/delete-file ".nrepl-port")))
        (println "nrepl on " port)))
  


        