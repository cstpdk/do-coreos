(defproject do-coreos "0.1"
  :description "Coreos on digital ocean"
  :dependencies [[digitalocean "1.2"] [org.clojure/clojure "1.5.1"]
                 [clj-http "1.0.1"] [clj-ssh "0.5.11"]]
  :source-paths ["."]
  :clean-targets ^{:protect false} ["target"])
