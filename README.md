# Two-Phase Commit Protocol (TPCP)

## Innholdsfortegnelse

1. [Installasjonsinstruksjoner](##Installasjonsinstruksjoner)
2. [Innledning/Introduksjon](##Innledning/Introduksjon)
3. [Beskrivelse](##Beskrivelse)
4. [Diskusjon](##Diskusjon)
5. [Gruppemedlemmer](##Gruppemedlemmer)

## Installasjonsinstruksjoner

```
1: Clone the project
2: Make sure you have java version 10
3: Run server.java
4: Run as many instances of Client.java or Client2.java within *INSERT TIMEFRAME FOR CONNECTIOON* after starting server.java
```

## API

Lenke til API her

## Innledning/Introduksjon

Dette avsnittet kan også være rett under tittelen


Vi har programert en two-phase commit protokoll. Denne protokollen sikrer atomiske transaktsjoner.

Fase en:
I den første fasen forberedes alle transaksjonene. Alle klientene må også svare ja/nei om de har intruffet et problem.

Fase to:
Om alle klientene stemte ja i forrige fase blir endringen lagret. Om en klient stemte nei blir tansaksjonen kanselert.
Kordinatoren informerer da alle klientene og sørger for at ingen endringer blir lagret.


## Beskrivelse

### Implementert funksjonalitet

### Teknologi- og arkitektur-/designvalg

### Eksempler med bruk av løsningen

### Hvordan teste løsningen

## Diskusjon

### Fremtidig arbeid

#### Mangler

#### Mulige forbedringer
	-En GUI interface

## Gruppemedlemmer

Magnus Baugerud  
 William Dalheim  
 Asbjørn Fiksdal Kallestad
