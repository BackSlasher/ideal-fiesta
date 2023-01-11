.PHONY: serve

server/Main.class: server/Main.java
	javac server/Main.java

serve: server/Main.class
	cd server && java Main
