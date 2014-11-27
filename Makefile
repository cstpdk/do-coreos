.PHONY: build update-discovery-token

default: build .coreos-token update-discovery-token

build: .built

.coreos-token:
	curl -w "\n" https://discovery.etcd.io/new > $@

update-discovery-token:
	sed -i.bak -r 's|discovery:(.*$$)|discovery: '$(shell cat .coreos-token)'|' cloud-config.yaml
	-rm cloud-config.yaml.bak

.built: .
	docker build -t do-coreos .
	touch .built
