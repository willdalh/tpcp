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
2: Make sure you have JDK 10 installed (We used 10.0.2)
3: Compile and run Server.java
4: Compile Client.java, and run as many instances within *INSERT TIMEFRAME FOR CONNECTIOON* after starting Server.java
```

## API

[Lenke til JavaDoc](http://williad.pages.stud.idi.ntnu.no/tpcp/overview-summary.html)

## Innledning/Introduksjon

Dette avsnittet kan også være rett under tittelen


Vi har programmert en Two-Phase Commit Protocol (TPCP). Protokollen brukes i et nettverk av noder hvor alle skal utføre en handling hvis, og bare hvis alle andre gjør det. Nettverket består av en Coordinator og en til flere Participants. Protokollen sikrer atomiske transaksjoner.


## Beskrivelse
Fase 1:
I den første fasen forberedes alle transaksjonene. Alle klientene må også svare ja/nei om de har intruffet et problem.

Fase 2:
Om alle klientene stemte ja i forrige fase blir endringen lagret. Om en klient stemte nei blir tansaksjonen kanselert.
Kordinatoren informerer da alle klientene og sørger for at ingen endringer blir lagret.

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
