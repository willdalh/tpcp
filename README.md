# Two-Phase Commit Protocol (TPCP)

## Innholdsfortegnelse

0. [Begrepsliste](#begrepsliste)
1. [Innledning](#innledning)
2. [Installasjonsinstruksjoner](#instruksjoner)
3. [API](#api)
4. [Continuous Integration](#ci)
5. [Beskrivelse](#beskrivelse)
6. [Diskusjon](#diskusjon)
7. [Gruppemedlemmer](#gruppemedlemmer)
8. [Referanser](#referanser)

## <a name=”begrepsliste”></a> Begrepsliste

Under er en liste over begreper som brukes i denne rapporten.

**Atomisk operasjon**:
En operasjon som endrer på en tilstand uten noe mellomstadie.

**Transaksjon**:
Ramme for en handling som sikrer atomisk utførelse.

**Koordinator (Coordinator)**:
Node som distribuerer meldinger til andre noder og leder utførelsen av en transaksjon.

**Deltaker (Participant)**:
Node som deltar i transaksjoner.

**Query**:
Strengen som skal føres opp i deltakernes logg.

**WireShark**:
Program som fanger pakker som går gjennom nettverksadapteren på maskinen.

**Socket**:
Et endepunkt for en toveis-kommunikasjon over et ip-basert nettverk.(Nettverkssocket, Wikipedia, 2020)

**Kontinuerlig integrasjon (Continous integration, CI)**:
Å sjekke inn kode jevnlig til et repository med tester som verifiserer koden.

**Versjonskontroll**:
Et system som holder orden på endringer og versjoner av filer.

## <a name="innledning"></a> Innledning

Vi har programmert en løsning som implementerer Two-Phase Commit Protocol (TPCP). Protokollen brukes i et nettverk av noder hvor alle skal utføre en handling hvis, og bare hvis alle andre gjør det. Nettverket består av en koordinator og en til flere deltakere. Løsningen er programmert i Java

## Installasjonsinstruksjoner <a name="instruksjoner"></a>

```
1: Clone the project
2: Make sure you have JDK 10 installed (We used 10.0.2)
3: Compile and run Server.java
4: Compile Client.java, and run as many instances with no more than *INSERT TIMEFRAME FOR CONNECTIOON* between each connection
```

## <a name=”api”></api> API

[Lenke til JavaDoc](http://williad.pages.stud.idi.ntnu.no/tpcp/overview-summary.html)

## <a name=”ci”></api> Continuous Integration

Vi brukte GitLabs verktøy for CI til å kjøre tester.
[Lenke til siste pipeline](https://gitlab.stud.idi.ntnu.no/williad/tpcp/pipelines/latest)

## <a name="beskrivelse"></a> Beskrivelse

### Two-phase commit protocol

Som navnet tilsier, deles Two-Phase Commit Protocol opp i to faser. Den generelle beskrivelsen av protokollen tar utgangspunkt i artikkelen Two-phase commit protocol på wikipedia.

Fase 1
I den første fasen mottar alle deltakerne melding fra koordinator om at en transaksjon er satt i gang. Alle klientene skal svare ja eller nei om de har inntruffet et problem. Fase 1 består da av to meldinger, en hver vei.

Fase 2:
Om alle klientene stemte ja i forrige fase blir endringen lagret. Om en klient stemte nei blir transaksjonen kansellert.
Koordinatoren informerer da alle klientene og sørger for at ingen endringer blir lagret. I fase 2 sendes det altså instruksjoner fra koordinator til deltakerne. Deltakerne svarer så med en kvittering på at instruksjonene er fullført.

Denne protokollen er atomisk, som betyr at alle endringene skal skje i én operasjon. Dette kan man se i fase 2 hvor koordinatoren instruerer alle til å enten lagre eller kansellere endringene. (Wikipedia, 2020)

### Teamets implementasjon av protokollen

I tillegg til de grunnleggende meldingene som sendes mellom koordinator og deltakerne ble det lagt til noen flere, som bygger på protokollen. I figuren under ser man hvordan meldingsflyten foregår gjennom en vellykket transaksjon. Figuren tar utgangspunkt i perspektivet til en individuell deltaker.

<figure class=”image”>
<img src="documentation/figures/tpcp.png" alt="Figur som viser flyten i en vellykket transaksjon" height="550">
<figcaption>Figur 1: Meldingsflyt i en vellykket transaksjon</figcaption>
</figure>

En deltager sender først `REQUESTING NEW TRANSACTION--query` til koordinatoren. Fase 1 begynner med at koordinatoren opplyser alle deltakere om at det er satt igang en ny transaksjon. Deltagerne får da meldingen `NEW TRANSACTION--query--READY TO COMMIT?`. Tilbake til koordinator sendes da meldingen `YES`. I fase 2 mottar deltagerne `TRANSACTION--query--COMMIT`. Deltageren sender tilbake `COMMITTED`, når instruksjonen er utført. Hvis deltageren hadde svart `NO` i fase en, ville den første meldingen i fase 2 blitt `TRANSACTION--query--ROLLBACK`, og deltagerne ville svart med `ROLLBACKED`.

I forklaringen ovenfor får man et overblikk over hvordan reglene i teamets implementasjon av protokollen er. Hver melding er en tekststreng, og må i noen tilfeller deles opp for å komme fram til komponentene den består av. Teamet løste dette ved å skille hver komponent med to bindestreker. Å bruke to bindestreker var løsningen på at vi trengte et vilkårlig sett med tegn som brukeren sjeldent ville ønske å skrive i en oppføring. Komponentene, med unntak av selve oppføringen, har alltid store bokstaver. Grunnen til dette er for å gjøre protokollens meldingsflyt mer oversiktlig, og å skille oppføringen fra de andre kodeordene.

Meldingene som kommer fra koordinator under en transaksjon er alltid på formen `STATUS--query--INSTRUCTIONS`. Hvis status er `NEW TRANSACTION`, vet deltakerne at det settes igang en ny transaksjon. Den siste komponenten inneholder instruksjonene som skal følges. `READY TO COMMIT?` sørger for at brukeren blir spurt om den er med på oppføringen. Figur 1 illustrerte meldingsflyten i en vellykket transaksjon, men det er også to andre tilfeller som kan oppstå. Figuren under illustrerer meldingsflyten når en annen deltager svarer `NO` i fase en.

<figure class=”image”>
<img src="documentation/figures/tpcpNoAnswer.png" alt="Figur som viser flyten når en annen deltager svarer NO" height="550">
<figcaption>Figur 2: Meldingsflyt i en transaksjon når en annen deltager svarer NO</figcaption>
</figure>

Figuren viser at så snart en deltager svarer `NO` i transaksjonen, sender koordinator ut en melding til alle deltagerne som instruerer dem i å rulle tilbake. Deltagerne mottar denne meldingen selv om de ikke har svart ennå. Et annet tilfelle er hvis en deltager ikke svarer under fase en. Koordinatoren vil etter en bestemt tid be den stumme deltageren om å koble seg fra tjeneren. Dette ser man i figuren under.

<figure class=”image”>
<img src="documentation/figures/tpcpTimeout.png" alt="Figur som viser flyten når deltageren ikke svarer i tide" height="550">
<figcaption>Figur 3: Meldingsflyt i en transaksjon deltakeren ikke svarer i tide</figcaption>
</figure>

### TCP

TCP er en nettverksprotokoll som befinner seg på transportlaget i den forenklede OSI-lagmodellen.

### Utdrag fra WireShark

<figure class=”image”>
<img src="documentation\WiresharkNettverk\Dekodet\Oppkobling.PNG" alt="Wireshark eksempel">
<figcaption>Figur 4: Oppkobling mellom server og klient i wireshark</figcaption>
</figure>

<figure class=”image”>
<img src="documentation\WiresharkNettverk\Dekodet\Suksess.PNG" alt="Wireshark eksempel">
<figcaption>Figur 4: Wireshark eksempel på en suksessfull transaksjon</figcaption>
</figure>

<figure class=”image”>
<img src="documentation\WiresharkNettverk\Dekodet\TCP-stream.PNG" alt="Wireshark eksempel">
<figcaption>Figur 4: TCP-streamdata output fra denne transaksjon</figcaption>
</figure>

<figure class=”image”>
<img src="documentation\WiresharkNettverk\Dekodet\Transaksjonrollback.PNG" alt="Wireshark eksempel">
<figcaption>Figur 4: Wireshark eksempel på rollback i en transaksjon</figcaption>
</figure>

<figure class=”image”>
<img src="documentation\WiresharkNettverk\Dekodet\Shutdown.PNG" alt="Wireshark eksempel">
<figcaption>Figur 4: Wireshark eksempel på shutdown i en transaksjon</figcaption>
</figure>

### Implementert funksjonalitet

Illustrasjonen er et aktivitetsdiagram for en klient:

<figure class=”image”>
<img src="documentation/figures/Aktivitetsdiagram.png" alt="Figuren viser et aktivitetsdiagram for klienten" height="550">
<figcaption>Aktivitetsdiagram for en klient</figcaption>
</figure>

### Eksempler med bruk av løsningen

Løsningen kan anvendes til ulike formål. Formålene har til felles at det er ønsket at alle som deltar i en aktivitet skal kunne ha tilgang til et eget dokument, hvor alle dokumenter til enhver tid skal være identiske. Eksemplene tar utgangspunkt i at løsningen distribueres på den måten at den passer spesifikke formålet. Altså at den ikke brukes ved å kjøre de kompilerte java-filene på datamaskinen, men for eksempel som en applikasjon på mobilen.

#### Oversikt over utlegg i en vennegjeng

Et eksempel på en anvendelse er hvis en person legger ut penger for andre. Vi tar utgangspunkt i en vennegjeng som er på ferie sammen og bruker et bankkort på alle varene og maten de kjøper. Gjennom ferien vil det være greit å skrive ned hva hver person skylder den som eier bankkortet. Bestemmer man at en skal ha ansvar for å føre opp dette kan man risikere at denne personen jukser med verdiene i sin favør, slik at den kan betale mindre.

Tar man i bruk løsningen må alle i vennegjengen godkjenne en hver oppføring før den lagres. Gjør de dette mens de sitter på restauranten har de gjerne prisene friskt i minne, slik at de er sikre på at oppføringen er riktig. Når ferien nærmer sin slutt, og de er klare til å gjøre opp, tar de frem sine kopier. Skal noen ha klart å endre sin egen kopi, vil dette bli avdekket når kopiene sammenlignes.

#### NOEN ANDRE EKSEMPLER???

### Hvordan teste løsningen

Instruksjonene som presenteres i dette avsnittet forutsetter at du har gjennomført [installasjonsinstruksjonene](#instruksjoner). Etter dette skal du ha en instans av klassen Server.java som kjøres, og minst en instans av Client.java. Tjeneren lytter i 10 sekunder etter hver tilkobling før den stopper. Dette er da tidsrammen det er mulig å kople opp det antallet klienter som er ønsket.

På hver av klientene skal du se følgende melding som indikerer at de er tilkoblet tjeneren:

```
COORDINATOR: You are connected with id ‘integer’
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
Person A owes Person B 150 NOK
Person A owes Person C 500 NOK
Person B owes Person A 40 NOK
-----------------------------
```

#### Sende forespørsel om en transaksjon og fullføre den

En transaksjon settes i gang av en av deltakerne. Dette gjøres med kommandoen `!request ‘query’`. Alle deltakerne vil da motta en melding fra koordinator som spør om man sier seg enig i å lagre oppføringen. Hver klient svarer enten `YES` eller `NO`. Hvis man svarer ‘YES’, vil man vente på videre instruksjoner fra koordinator. Dette er fase en av protokollen.

Når alle klientene har svart ja, begynner fase to. Koordinator vil da sende ut en ny melding hvor den instruerer deltakerne til å lagre oppføringen. Klientene sender tilbake kvittering om at instruksjonene er utført. Fase to skjer automatisk og krever ingen handlinger fra brukeren.

## <a name="diskusjon"></a> Diskusjon

### Teknologi- og arkitektur-/designvalg

#### Valg av programmeringsspråk og nettverksprotokoll

Til valg av programmeringsspråk hadde teamet flere muligheter. Teamet hadde tidligere brukt Java, JavaScript og C++ i øvingsarbeidet. Valg av programmeringsspråk hadde også konsekvenser for hvordan løsningen ville bli implementert, da med tanke på hvilke nettverksprotokoller man tar i bruk. En øving handlet om å utvikle en egen WebSocket-tjener ved hjelp av Node. C++ ble brukt tidlig i faget, men det ble ikke gjennomgått hvordan man setter opp tjenere med dette språket. Det ble til slutt bestemt at Java skulle brukes, da alle har erfaring med dette fra de begynte på universitetet.

I to av øvingene i faget ble det anbefalt å bruke Java. I den ene øvingen skulle man sette opp en TCP-tjener, og i den andre en UDP-tjener. Med TCP kunne man overføre rene tekststrenger mellom klient og tjener, men i UDP måtte man ta konvertere fra bytes. I TCP er man også sikker på at overføringen er pålitelig. I et system hvor en tjener skal koordinere flere klienter og sørge for at de utfører de samme instruksjonene er det positivt at eventuelle pakketap blir håndtert. Derfor ble det valgt å bruke TCP til systemet, med utgangspunkt i teamets kunnskaper fra Øving 4 hvor man skulle utvikle en kalkulatortjener.

#### Kode

Prosjektet består av følgende klasser:

##### Coordinator

Koordinere klientene som er koplet opp mot server. Klassen opprettes med en klientliste når server er ferdig å lytte etter klienter. Coordinator tolker data den mottar og sender riktig instruksjoner til riktig klient. Om transaksjonen blir kansellert sørger coordinator for å informere alle klientene at de må ruller tilbake transaksjonen. Klassen sørger også for at inaktive klienter blir terminert. Da rulles gjeldende transaksjon tilbake og gjenværende klienter kan initiere en ny.

##### ClientHandler

Klassen ClientHandler inneholder metoder for å sende meldinger til en deltager fra koordinator og lese meldinger fra en deltager. Klassen opprettes med en id for å identifisere hvilken klient som sender meldinger.

ClientHandler er en mellomklasse for Coordinator og participant klassen. Gruppen valgte å implementere denne klassen for å få en mer distribuert kode. Et alternativ hadde vært å ha Coordinator og Participant kommunisere direkte, men å skille ut denne koden i en ekstra klasse fører til en mer oversiktlig kodestruktur.

##### Participant

Klassen håndterer brukerinput og formaterer forespørsler før de sendes til koordinator. Hver instans av denne klassen vil ha en instans hos koordinatoren som den kommuniserer med. Denne klassen vil utføre alle instruksjoner som kommer fra koordinatoren.
Eksempler på sentrale instruksjoner:
Sende tilbakemelding om hvilke instruksjoner som skjer under hele transaksjonen
“Committe” dataen om alle klienter som er oppkoplet er med.
Sørge for at alle klientene “rollbacker” om en transaksjon feiler.
Sørge for å lagre transaksjonsdata om overføringen er en succsess

##### Server

Sørger for å ta imot klienter som ønsker å ta del i transaksjonen. Gruppen har satt en oppkoblingstimer på ti sekunder. Etter tiden er ute kan ikke flere klienter koble seg til denne instansen. Etter opprettes et koordinatorobjekt som inneholder en liste med alle klientene som er med i transaksjonen og start funksjonen i er initialisert.
Videre sørger serverklassen for at meldinger kan overføres mellom klassene.

Client:
Inneholder en main-metode som oppretter en instans av klassen Participant. Client kjøres for å kople en deltager opp mot tjeneren.

Client2 og Client3:
Disse klassene er kopier av Client. De ble laget for å bruke compound-funksjonen i IntelliJ, som lar deg starte flere program samtidig, men som ikke lar deg starte flere av det samme programmet.

Samarbeidet mellom klassene er illustrert i klassediagrammet under.
<img src=”documentation/figures/klassediagram.png” alt=”Klassediagram som viser samarbeid mellom klassene” height=”550”>

#### Begrensninger

##### Ulovlige tegn i forespørsler

I klientkoden har gruppen valgt at meldinger som koordinator sender til klient bruker “--” for å dele den opp. Om denne strengen da ble sendt ved en forespørsel av en transaksjon, for eksempel “!request --”, ville serveren krasjet med en feilmelding “index out of bounds”. I flere deler av koden splittes meldingene på “--”, da vil det også oppstå noen feiler hvor feile komponenter hentes ut. Dette løste teamet ved å hindre brukeren i å sende forespørsler som inneholder en eller flere sekvenser av “--”.

##### Lagring av logg

Oppføringene som skjer i transaksjonene lagres i en egen streng for hver instans av Participant. Når en klient avslutter, lagres ikke dette til noen fil. Dette betyr at tilstanden som opprettes under en sesjon ikke vil være tilgjengelig under en senere sesjon. Teamet tillot denne begrensningen, siden hvor man lagrer loggen vil avhenge av implementasjonen. Hadde det vært en nettside ville man heller ønsket å lagre loggen i en database. Teamet ønsket å gjøre løsningen så generell som mulig.

#### Biblioteker og verktøy

Til dette prosjektet har teamet tatt i bruk ulike verktøy og biblioteker. Disse er presentert og forklart i tabellen under.

| Biblioteker og verktøy | Bruk                                                                                                                                                      |
| ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| GitLab                 | Brukes til samskriving, versjonskontroll, generering av JavaDoc og kontinuerlig integrasjon                                                               |
| JUnit                  | Brukes til enhetstesting                                                                                                                                  |
| Maven                  | Håndterer avhengigheter til prosjektet. Hovedgrunnen til at det ble brukt i dette prosjektet var for å tillate enhetstester å kjøre i pipelinen på GitLab |
| java.util.ArrayList    | Arraylist ble brukt fordi den er enklere å jobbe med enn tabeller som er standard i Java                                                                  |
| java.util.Date         | Ble brukt for tidtagning                                                                                                                                  |
| java.util.scanner      | Ble brukt for brukerinput                                                                                                                                 |
| java.util.concurrent   | Ble brukt for koble klienter til tjeneren innen et gitt tidsrom                                                                                           |
| java.net               | Socket-objekter ble brukt for kommunikasjon mellom tjener og klient                                                                                       |
| java.io                | Nødvendig for bruk av Socket-objekter og for lesing og skriving mellom tjener og klient                                                                   |

#### Håndtering av feil i fase 2

Slik teamet tolket two-phase commit protocol ut fra wikipedia-artikkelen, kan ikke en deltager trekke seg etter fase 1. Da skal deltakerne bare committe endringene. Om det skulle skje noe som gjør at deltakeren ikke får gjort det, er det ikke protokollen sitt ansvar å håndtere dette. Å håndtere eventuelle feil som kan oppstå i fase 2, for eksempel at en deltaker mister forbindelsen, ville også krevd mye tid og ressurser å lage. Av disse to grunnene ble det derfor bestemt å ikke håndtere feil i fase 2.

#### Håndtering av timeout for klient

Under stemmeprosessen i fase en får klienten et valg om å stemme ja/nei om de vil bli med på transaksjonen som er satt i gang. I denne prosessen har gruppen valgt å implementere en timer mens koordinator venter på svar. Dette er for å løse problemet med en inaktiv klient, eller en klient som av en annen grunn ikke får svart. Etter satt timer blir klienten kastet ut, transaksjonen den da var en del av rulles tilbake. Videre kan gjenværende klienter fortsette ved å opprette en ny transaksjon.

### Fremtidig arbeid

Selv om teamet er fornøyde med produktet er det noen ting som kunne blitt gjort bedre med mer tid. I tillegg var det noe funksjonalitet som gruppen ønsket å implementere, men ikke hadde tid til. Under vil det bli listet mangler gruppen føler applikasjonen har og mulige forbedringer.

#### Mangler

En mangel applikasjon har er at det ikke er mulig for klient å avslutte seg selv. En klient kan bare bli avsluttet dersom den bruker for lang tid på svare koordinatoren i fase 1, og blir kastet ut, eller om prosessen blir terminert.

#### Mulige forbedringer

All interaksjon med applikasjonen er ved hjelp av tekst-kommandoer gjennom en terminal. Dersom prosjektet skulle jobbes videre med hadde en mulig forbedring vært å lage et grafisk brukergrensesnitt for applikasjonen. Dette kunne gjort det enklere å gangen i prosessen og å interagere med den, som hadde gjort hele applikasjonen mer brukervennlig.

Applikasjonen er mer eller mindre bare en demonstrasjon av two-phase commit protocol. Ved videre arbeid kunne det vært interessant å anvende protokollen i et system. Et eksempel på dette kunne vært å lage et banksystem hvor man kan overføre penger mellom kontoer.

Man kan bare legge til klienter i et gitt tidsrom etter at tjeneren er blitt startet. Om man ønsker å legge til flere må man starte tjeneren og alle klientene på nytt. Gruppen valgte å gjøre dette fordi det ikke nødvendigvis gir mening for en ny klient å godta en tidligere transaksjonslogg. Imidlertid ville det vært en nyttig egenskap for applikasjonen å ha, og kunne derfor kanskje blitt implementert ved videre arbeid.

## <a name="gruppemedlemmer"></a> Gruppemedlemmer

Magnus Baugerud  
William Dalheim  
Asbjørn Fiksdal Kallestad

## <a name=”referanser”></a> Referanser

Two-phase commit protocol. (n.d.). Wikipedia. hentet 20.april 2020, fra
https://en.wikipedia.org/wiki/Two-phase_commit_protocol

Nettverkssocket. (n.d) Wikipedia. Hentet 21.april 2020, fra https://no.wikipedia.org/wiki/Nettverkssocket
