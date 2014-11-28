(ns user
    (:require [digitalocean.v2.core :as do]
              [clj-http.client :as client]))

(use 'clj-ssh.cli)
(default-session-options {:strict-host-key-checking :no})

(defonce token (System/getenv "DO_TOKEN"))
(defonce key-id (System/getenv "DO_KEY_ID"))

(declare set-metadata ip)

(def common-core-args { :region "lon1"
                        :size "512mb"
                        :private_networking "true"
                        :image "coreos-stable"
                        :user_data (slurp "cloud-config.yaml")
                        :ssh_keys [key-id]})
(defn create-core
  "Creates a new coreos host with the given name"
  [name]
  (do/create-droplet token nil (assoc common-core-args :name name))
  (let [set-meta (delay (set-metadata
                          name "persistent=no,purpose=app,tier=green"))]
        (future (do (ip name) (force set-meta)))))

(defn create-loghost
  "Creates a new coreos host used for log hosting"
  []
  (do/create-droplet token (assoc common-core-args :name "loghost"))
  (let [set-meta (delay (set-metadata
                          name "persistent=yes,purpose=logs,tier=green"))]
        (future (do (ip name) (force set-meta)))))

(defn droplets [] (:droplets (do/droplets token)))

(defn coreos-hosts [] (filter #(= (:distribution (:image %)) "CoreOS")
                              (droplets)))

(defn ips [] (map #(:ip_address %) (filter #(= "public" (:type %))
                                           (flatten (map #(:v4 (:networks %))
                                                         (coreos-hosts))))))

(defn ip
  "Returns a future for the ip of the instance by name"
  [name]
  (defn extract-ip [name]
    (:ip_address
      (first (filter #(= "public" (:type %))
        (:v4 (:networks (first (filter #(= name (:name %))
                                       (droplets)))))))))
  (loop [i (extract-ip name)]
    (if i
      i
      (recur (extract-ip name)))))

(defn save-entry-node [] (spit ".entry-node" (first (ips))))

(defn destroy-droplet [name] ((ssh (ip name)
                                   (str "curl -L http://127.0.0.1:7001/v2/admin/machines/"
                                        "`cat /etc/machine-id`"
                                        "-XDELETE")
                                   :username "core")
                              ((do/generic :delete :droplets) token
                                         (:id (first (filter #(= name (:name %))
                                                 (droplets)))))))

(defn set-metadata
  "set fleet metadata on a coreos droplet"
  [name metadatastring]
  (with-default-session-options {:connect-timeout 1000
                                 :strict-host-key-checking :no}
    (ssh (ip name)
         (str "sudo mkdir /etc/fleet ; echo 'metadata=\""
              metadatastring
              "\"' | sudo tee /etc/fleet/fleet.conf && "
              "sudo systemctl restart fleet")
         :username "core")))
