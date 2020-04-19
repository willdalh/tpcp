# Two-Phase Commit Protocol (TPCP)

## Innholdsfortegnelse

1. [Installasjonsinstruksjoner](#instruksjoner)
2. [Innledning](#innledning)
3. [Beskrivelse](#beskrivelse)
4. [Diskusjon](#diskusjon)
5. [Gruppemedlemmer](#gruppemedlemmer)

## Installasjonsinstruksjoner <a name="instruksjoner"></a>

```
1: Clone the project
2: Make sure you have JDK 10 installed (We used 10.0.2)
3: Compile and run Server.java
4: Compile Client.java, and run as many instances within *INSERT TIMEFRAME FOR CONNECTIOON* after starting Server.java
```

## API

[Lenke til JavaDoc](http://williad.pages.stud.idi.ntnu.no/tpcp/overview-summary.html)

## <a name="innledning"></a> Innledning

Dette avsnittet kan også være rett under tittelen

Vi har programmert en Two-Phase Commit Protocol (TPCP). Protokollen brukes i et nettverk av noder hvor alle skal utføre en handling hvis, og bare hvis alle andre gjør det. Nettverket består av en Coordinator og en til flere Participants. Protokollen sikrer atomiske transaksjoner.

## <a name="beskrivelse"></a> Beskrivelse

Fase 1:
I den første fasen forberedes alle transaksjonene. Alle klientene må også svare ja/nei om de har intruffet et problem.

Fase 2:
Om alle klientene stemte ja i forrige fase blir endringen lagret. Om en klient stemte nei blir tansaksjonen kanselert.
Kordinatoren informerer da alle klientene og sørger for at ingen endringer blir lagret.

### Implementert funksjonalitet

### Teknologi- og arkitektur-/designvalg

### Eksempler med bruk av løsningen

### Hvordan teste løsningen

Instruksjonene som presenteres i dette avsnittet forutsetter at du har

## <a name="diskusjon"></a> Diskusjon

### Fremtidig arbeid

#### Mangler

#### Mulige forbedringer

    -En GUI interface

## <a name="gruppemedlemmer"></a> Gruppemedlemmer

Magnus Baugerud  
 William Dalheim  
 Asbjørn Fiksdal Kallestad
