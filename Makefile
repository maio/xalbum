.PHONY: test

run:
	lein ring server

test:
	lein quickie
