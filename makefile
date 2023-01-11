.PHONY: serve recommend

server/Main.class: server/Main.java
	javac server/Main.java

serve: server/Main.class
	cd server && java Main

recommend:
	python analyzer.py sampler/output/data.csv
