(ns clj-tools.nrepl
    (:require [clojure.tools.nrepl.server :as nrepl.server]
              [cider.nrepl :as cider]))
  
  (defn -main []
    (let [port 5000]
      (nrepl.server/start-server 
        :port port
        :handler cider/cider-nrepl-handler
        :bind "0.0.0.0")
        (spit ".nrepl-port" port)
        (.addShutdownHook (Runtime/getRuntime)
                          (Thread. #(clojure.java.io/delete-file ".nrepl-port")))
        (println "nrepl on " port)))
  


        