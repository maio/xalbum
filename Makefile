.PHONY: test

build:
	lein ring uberjar

run:
	lein ring server

test:
	lein quickie
