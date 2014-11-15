(defproject do-coreos "0.1"
  :description "Coreos on digital ocean"
  :plugins [[lein-exec "0.3.4"]]
  :dependencies [[digitalocean "1.2"] [org.clojure/clojure "1.5.1"]
                 [clj-http "1.0.1"]]
  :source-paths ["."]
  :clean-targets ^{:protect false} ["target"])
