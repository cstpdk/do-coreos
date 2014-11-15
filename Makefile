.PHONY: build run update-discovery-token coreos-token

default: build coreos-token update-discovery-token

run:
	./run

build: .built

coreos-token:
	curl -w "\n" https://discovery.etcd.io/new > $@

update-discovery-token:
	sed -i.bak -r 's|discovery:(.*$$)|discovery: '$(shell cat coreos-token)'|' cloud-config.yaml

.built: .
	docker build -t do-coreos .
	touch .built
