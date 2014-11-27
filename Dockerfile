FROM cstpdk/java8-clojure-leiningen

RUN apt-get update
RUN apt-get -y install ssh

ADD . /app

WORKDIR /app

ADD keys /root/.ssh
ENV LEIN_ROOT true
RUN lein install
RUN lein repl

CMD ["lein","repl"]
