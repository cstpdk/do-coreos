FROM cstpdk/java8-clojure-leiningen

ADD . /app

WORKDIR /app

ENV LEIN_ROOT true
RUN lein install
RUN lein repl

CMD ["lein","repl"]
