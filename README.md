# Quintor-intern

In deze git repository staat de code die ik tijdens mijn stage bij Quintor heb gemaakt als bewijsstuk voor bij mijn stageverslag.

#### load-generator
Load generetor is een springboot applicatie gemaakt om tijdens de demo bij J-Fall 2016 te laten zien hoe de meters op het managementpanel uitslaan.

#### statesaver
De statesaver applicaties zijn gemaakt om te onderzoeken hoe de sessiestaat het beste tijdelijk opgeslagen kan worden, zodat de sessiestaat nog beschikbaar is als de specifieke dokcercontainer omvalt. Van de drie onderzochtte manieren lijkt de state naar andere containers sturen met een multicastsocket, de huidige code van statesaver-multicast heeft het nadeel dat de sessiestaat verlogen gaat als alle docker containers omvallen. Dit is op te lossen door de code aan te passen zodat één of twee instanties de sessiestaat die ze binnenkrijgen in een database te zetten, zodat de sessiestaat zelfs nog beschikbaar is nadat alle dockercontainers omgevallen zijn.

De andere twee versies van statesaver slaan de sessiestaat in de database op of sturen de sessiestaat naar het activemq cluster. Hiervan is de versie die het op de database opslaat het meest interressant, zowel qua performance als veiligheid van de sessiestaat.

#### Compilen
alle applicaties zijn te compilen met maven:

    $ mvn clean package docker:build