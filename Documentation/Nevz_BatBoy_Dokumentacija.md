# RAZŠIRJENI DEL
## Razvojni proces

Načrtovanje igre je potekalo na sprehodu od faksa do študentskega doma ter naprej do Lidla. Potekalo je verbalno, malo telepatsko ter s skicami na majhne post-it lističe.
Delo je bilo razdeljeno enakomerno, vsak član je poprijel po svojih najboljših zmožnostih! 💪

## Tehnike in orodja:
- Git in GitHub - VCS (verzioniranje)
- Android studio (Ozva) in IntelliJIDEA (Copoy)
- LibGdx in Scene2d
- Discord ter mobilni telefon za komunikacijo

## Podrobnosti o mehanikah
- Voditi netopirčka z imenom BatBoy po neskončnem svetu težav in sladkosti. Netopir avtomatsko sledi miškinemu kurzorju. 
- S pritiskom na preslednico [SPACE], netopir v smer miške izstreli zvočni sonar. Če sonar zazna entiteto (drevo, čarovnica, buča ali žužek), jo za par sekund osvetli.
- Netopir pasivno izgublja energijo (stamina). 
- Ob kliku na drevo, netopir poleti k drevesu in si odpočije (nabira stamino).
- Ob pritisku na tipko A [A], netopir izstreli ognjeno kroglo. Ognjena krogla razstreli čarovnico in ta izpusti bučo.
- Ob koliziji s čarovnico, netopir izgubi življenje. 
- Ob koliziji z žužkom, netopir pridobi življenje. 
- Ob koliziji z bučo, netopir dobi točke.
- Čarovnice se pojavijo izven vidnega polja igralca, vedno pogosteje glede na netopirjevo višino. Sledijo naključni predeterminirani trajektoriji.
- Žužki se pojavijo v okolici drevesa v naključnih intervalih. Sledijo naključni smeri določeni vsak frame.
- Tema HALLOWEEN je vključena z BUČAMI, NETOPIRJEM in ČAROVNICO.
- Tema IT’S NOT A BUG, IT’S A FEATURE je vključena s HROŠČI, ČRNIM EKRANOM IN UI-jem, ter potencialno NEOMEJENIM SCORE-om.

## Tehnična izvedba
Najpomembnejši element je SONAR, ki razkrije vse ostale elemente. Največji izziv je bilo usklajevanje koordinat sveta, ekrana in posameznega elementa, ter manualno nastavljanje točk, kjer lahko netopir počiva. Za implementacijo tega sva ustvarila svoj editor, kamor lahko naložimo vsa različna drevesa ter shranimo njihove točke počivanja.

## Vizualni in zvočni del
- Pixel art
- Asseti vzeti s spleta, royalty free strani (ali ustvarjeni ročno)
- AI ni bil uporabljen za pridobivanje assetov

## Namestitev in zagon igre
- exe datoteka se nahaja v mapi /Release/BatBoy.zip
- poleg exe datoteke so potrebni tudi asseti v mapi assets, ter trees.json za nalaganje podatkov o drevesih

## Uporaba AI in zunanjih virov
- Po potrebi zastonj verzija ChatGPT, brez agentov in autocompleta
- Priporočanje funkcij
- Na manj zahtevnih funkcionalnostih zelo malo pomoči, na zahtevnejših (sonar) do 50%
- Zunanji viri - asseti:
  - itch.io
  - pixabay

## Testiranje
Ob preigravanju sva ugotovila, da ni najboljše disposati assetov, ki jih še vedno potrebuješ med igranjem...

## Načrti za naprej
- Spin attack!
- Žužki bežijo!
- Toliko žužkov, da igra crasha!
