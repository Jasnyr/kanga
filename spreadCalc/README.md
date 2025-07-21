1. Launch instruction:

	- download the repo (https://github.com/Jasnyr/kanga.git) and open in in your favourite IDE.
	- (in case it is Eclipse run "maven eclipse:eclipse" to generate required eclipse files)
	- perform a "maven build"
	- Launch main class (SpreadCalcApp) as Java application

2. curls:

calculate spread-based ranking:
curl -X POST http://localhost:8080/api/spread/calculate -H "Authorization: Bearer ABC123"

fetch spread-based ranking:
curl -X GET http://localhost:8080/api/spread/ranking -H "Authorization: Bearer ABC123"
