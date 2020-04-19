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

Som navnet tilsier, så deles Two-Phase Commit Protocol opp i to faser.

### Fase 1

I den første fasen mottar alle deltakerne melding fra koordinator om at en transaksjon er satt igang. Alle klientene skal svare ja eller nei om de har intruffet et problem. Fase 1 består da av to meldinger, en hver vei.

### Fase 2

Om alle klientene stemte ja i forrige fase blir endringen lagret. Om en klient stemte nei blir tansaksjonen kanselert.
Kordinatoren informerer da alle klientene og sørger for at ingen endringer blir lagret. I fase 2 sendes det altså instruksjoner fra koordinator til deltakerne. Deltakerne svarer så med en kvittering på at instruksjonene er fullført.

I tillegg til de grunnleggende meldingene som sendes mellom koordinator og deltakerne har vi lagt til noen flere som bygger på protokollen. I figuren under ser man hvordan meldingsflyten foregår gjennom en vellykket transaksjon. Figuren tar utgangspunkt i synspunktet fra en individuell deltaker.

<img src="documentation/figures/tpcp.png" alt="Figur som viser flyten i en vellykket transaksjon" height="550">

### Implementert funksjonalitet

### Eksempler med bruk av løsningen

### Hvordan teste løsningen

Instruksjonene som presenteres i dette avsnittet forutsetter at du har gjennomført [installasjonsinstruksjonene](#instruksjoner). Etter dette skal du ha en instans av klassen Server.java som kjøres, og minst en instans av Client.java.

På hver av klientene skal du se følgende melding som indikerer at de er tilkoblet tjeneren:

```
COORDINATOR: You are connected with id {integer}
```

Påfølgende meldinger er instruksjoner over ulike kommandoer du kan utføre. Disse presenteres under.

#### Vise loggen

Utenfor en transaksjon kan man skrive `!showlog` for å skrive ut loggen. Rett etter man har koblet seg opp vil loggen være tom og se slik ut:

```
-------------LOG-------------

-----------------------------
```

Fullfører man noen vellykkede transaksjoner vil man kunne se alle oppføringer i loggen.

```
-------------LOG-------------
Person A pays Person B 150NOK
Person A pays Person C 500NOK
Person B pays Person A 40NOK

-----------------------------
```

#### Sende forespørsel om en transaksjon og fullføre den

En transaksjon settes igang av en av deltakerne. Dette gjøres med kommandoen `!request {oppføring}`. Alle deltakerne vil da motta en melding fra koordinator som spør om man sier seg enig i å lagre oppføringen. Hver klient svarer enten `YES` eller `NO`. Hvis man svarer ja, vil man vente på videre instruksjoner fra koordinator. Dette er fase en av protokollen.

Når alle klientene har svart ja, begynner fase to. Koordinator vil da sende ut en ny melding hvor den instruerer deltakerne til å lagre oppføringen. Klientene sender tilbake kvittering om at instruksjonene er utført. Fase to skjer automatisk og krever ingen handlinger fra brukeren.

## <a name="diskusjon"></a> Diskusjon

### Teknologi- og arkitektur-/designvalg

Vi kan skrive om

1. TCP
2. Arbeidsoppgavene hver klasse har
3. At brukeren ikke kan skrive "--" i en request
4. Hvorfor vi valgte java
5. Vår tolkning av protokollen ut fra wikipedia siden
6. Testbiblioteker og andre biblioteker vi brukte

### Fremtidig arbeid

#### Mangler

#### Mulige forbedringer

    -En GUI interface

## <a name="gruppemedlemmer"></a> Gruppemedlemmer

Magnus Baugerud  
 William Dalheim  
 Asbjørn Fiksdal Kallestad
