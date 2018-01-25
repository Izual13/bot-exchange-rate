FROM openjdk:alpine

RUN echo http://nl.alpinelinux.org/alpine/edge/testing >> /etc/apk/repositories && \
    apk add --no-cache curl bash rlwrap && \
    curl -O https://download.clojure.org/install/linux-install-1.9.0.297.sh && \
    chmod +x linux-install-1.9.0.297.sh && \
    ./linux-install-1.9.0.297.sh

EXPOSE 5000

CMD cd project && \
 sleep 1; clj -m clj-tools.nrepl

