.PHONY: up recommend watch_mem

up:
	docker compose up

recommend:
	python analyzer.py sampler/output/data.csv

watch_mem:
	watch 'ps aux | head -n1 ; ps aux | grep java'

