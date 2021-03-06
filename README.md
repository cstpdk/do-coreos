# do-coreos

Run coreos on digital ocean, without dependencies other than docker.

## Prerequisite

1. Docker
1. Digital Ocean account
1. SSH Key added to account

## Getting started

1. Add Digital Ocean user token to .do-token

1. Add Digital Ocean ssh key id to .do-key

1. If you already have a cluster, then put your coreos token in
.coreos-token, otherwise make will create it

1. Run
	> make

1. Run

	> ./run

	To start a clojure repl. From here, the excellent digital-ocean
	library can be used to manage droplets. For convenience a function with
	default values exists "create-core"

	For instance to create 3 machines named core{1..3}:
	``
	(doseq [i [1 2 3]] (create-core (str "core" i)))
	``

1. To use fleetctl locally an ip to one of the coreos machines have to
exist in .entry-node for convenience the method "save-entry-node"
exists
