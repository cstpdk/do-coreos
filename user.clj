(ns user
    (:require [digitalocean.v2.core :as do]
              [clj-http.client :as client]))

(defonce token (System/getenv "DO_TOKEN"))
(defonce key-id (System/getenv "DO_KEY_ID"))

(defn create-core
  "Creates a new coreos host with the given host"
  [name]
  (do/create-droplet token nil {
    :name name
    :region "lon1"
    :size "512mb"
    :private_networking "true"
    :image "coreos-stable"
    :user_data (slurp "cloud-config.yaml")
    :ssh_keys [key-id]}))

(defn droplets [] (:droplets (do/droplets token)))

(defn coreos-hosts [] (filter #(= (:distribution (:image %)) "CoreOS")
                              (droplets)))

(defn ips [] (map #(:ip_address %) (filter #(= "public" (:type %))
                                           (flatten (map #(:v4 (:networks %))
                                                         (coreos-hosts))))))

(defn save-entry-node [] (spit ".entry-node" (first (ips))))
