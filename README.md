# BatBoy
_~ Because **BatMan** was already taken ~_

# Installation
Extract the `BatBoy.zip` in `/Release` directory.
Run the exe or the jar.

## Controls:
- `mouse` to move
- `A` to attack (shoot fireball)
- `SPACE` to use sonar
- `LMB` to hang/unhang from a tree


# Dokumentacija
# BAT BOY!!
“Težko dojemaš svet okrog sebe, ko si bad Bat Boy!”

## Ime ekipe: Nevž

## Člani in vloge:
• Matevž Ozvatič (AKA OZVA) (the “vž” in “Nevž”): Programiranje in oblikovanje in zvok
• Nela Copot (AKA COPOY) (the “Ne” in “Nevž”): Programiranje in zvok in oblikovanje

## Povzetek koncepta:
- **Cilj igre**: Se naučiti več, ko vidiš manj
- **Glavna igralna mehanika**: Sistem biosonarja, ki ga netopirji uporabljajo za orientacijo v
  prostoru, izogibanje oviram in lov na plen v temi.
- **Kje se skriva tema**?
  **Halloween**: Netopirji, ki jih poškudujejo čarovnice, ki letijo na metlah, le-te pa ob zadetku
  spustijo bučo, ki ti ob stiku z njimi dajo točke. Noč čarovnic “in a nutshell”
  **“It’s not a bug, it’s a feature”**: Dejstvo, da igralec ne vidi, kaj se dogaja na zaslonu večino
  časa, torej črn zaslon; muhe, ki jih netopir lovi, ki niso le “bug”, temveč tudi “feature” in
  navsezadnje tudi beleženje točk, ki se igralcu dodajajo vedno ko igra igro. Ob ponovnem igranju
  se točke ne resetirajo, se samo seštevajo in seštevajo ... Ampak to je totalno feature! Ker je bil
  namen, da lahko s prijatelji tekmuješ, kdo bo imel do konca meseca več točk!


## **Ključne mehanike**:

- Netopirjev biosonar
- Spanje na drevesu
- Napad čarovnic
- Hranjenje z muhami
- Pobiranje buč

## Tehnologije in orodja:
**LibGDX (IntelliJ IDEA, Android Studio)**

## Največja unikatnost igre:
**Dejstvo, da igralec večino časa ne vidi, kaj se dogaja na zaslonu, niti Health in Progress Bar-a!**

##  Največja tehnična zahtevnost:
**Implementacija biosonarja in dejstvo, da sva delala v LibGDX.**

## Ocenjeni delež uporabe AI:
15 % pri osnovnih funkcionalnostih, ki jih poznava in sva spoznala na faksu, 50% pa je bilo pomoči pri zahtevnejših funkcionalnostih (npr. biosonar). Za to pa sva si kvečjemu pomagala le z ChatGPT zastonjsko verzijo.

- AI generiranega besedila, grafik in zvoka ni. Vizualni elementi so bili prevzeti iz itch.io, zvoki pa
  iz Pixabay.

## Status izdelka:
- Implementirane so vse funkcionalnosti, ki sva jih planirala narediti v tem kratkem času.
- Nedokončanih stvari ni, je pa marsikaj, kar bi si želela nadgraditi, popraviti, dodati!

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
