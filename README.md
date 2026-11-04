# Rezervace v ordinaci (Zadání A)
> **Předmět:** Pokročilé programování (PPRO) – ZS 2026/2027  
> **Fakulta:** Fakulta informatiky a managementu, Univerzita Hradec Králové (FIM UHK)  
> **Vyučující:** Dominik Palla (`dominik.palla@uhk.cz`)  
> **Klient:** MUDr. Jana Hrubá, *Ordinace Na Vyhlídce*  
> **Dokument:** Technická dokumentace, specifikace a Seznam rozhodnutí (Single Source of Truth)

---

## 1. Kontext a výchozí stav klienta

### O klientovi
Soukromá praxe *Ordinace Na Vyhlídce*. V ordinaci působí tři lékaři na částečné úvazky a recepční. V evidenci je přibližně 900 pacientů, z nichž zhruba polovina dochází pravidelně.

### Jak to v ordinaci funguje dnes
- Na recepci se používá papírový diář, do kterého se termíny zapisují obyčejnou tužkou.
- Když pacient volá, recepční zdlouhavě hledá volné okno listováním v sešitě.
- Pokud lékař náhle onemocní, celý seznam dotčených pacientů se musí obvolávat z poznámek.
- Na konci každého měsíce recepce ručně přepisuje provedené výkony do tabulky pro zdravotní pojišťovny – tato agenda zabere pokaždé spoustu času.
- Za poslední rok se dvakrát stalo, že byli na stejný čas omylem objednáni dva pacienti ke stejnému lékaři, což vedlo k nepříjemným situacím v čekárně.

---

## 2. Požadavky klienta

### 2.1 Co klient chce (Funkční požadavky)
1. **Evidence pacientů:** Jméno, příjmení, rok narození, telefon, identifikátor pojištěnce (rodné číslo / číslo pojištěnce), zdravotní pojišťovna. Rychlé vyhledávání podle příjmení nebo telefonního čísla (pacient obojí hlásí hned při zahájení hovoru).
2. **Lékaři a ordinační hodiny:** Každý lékař má specifické dny a ordinační hodiny. Systém musí reflektovat, že jeden z lékařů ordinuje v úterý odpoledne na jiné adrese, kde nelze přijímat objednávky přes tuto ordinaci.
3. **Rezervace na konkrétní termín u konkrétního lékaře:** Rezervaci zakládá recepce. Součástí je textová poznámka s důvodem návštěvy.
4. **Záznam o návštěvě a provedené výkony:** Po příchodu pacienta a proběhnutí vyšetření se zaeviduje průběh. Výkony se vybírají z centrálního číselníku (kód výkonu, název, bodové ohodnocení). U jedné návštěvy lze vykázat více výkonů současně, u vybraných výkonů se eviduje i počet opakování (např. 3 aplikace).
5. **Zrušení rezervace a sledování spolehlivosti:** Pacient může rezervaci odvolat. Lékař chce mít přehled o pacientech, kteří termíny ruší opakovaně.
6. **Přehled volných termínů:** Klíčová funkce pro recepci – zobrazení kalendářního týdne pro konkrétního lékaře s okamžitým vyznačením volných slotů.
7. **Měsíční výkaz pro pojišťovnu:** Generování souhrnu provedených výkonů a celkového součtu bodů za zvolený kalendářní měsíc a vybraného lékaře.

### 2.2 Na čem klient trvá (Obchodní pravidla a invarianty)
- **Zákaz překryvu rezervací:** Dvě rezervace na shodný časový úsek u stejného lékaře nesmí jít založit za žádných okolností (ani ručním zásahem, ani omylem). Vynuceno na úrovni databáze a doménových validačních pravidel.
- **Ordinační hodiny jako striktní mantinel:** Rezervaci nelze založit mimo platné ordinační hodiny daného lékaře.
- **Integrita vykázaných výkonů:** Zdravotní výkon nelze přiřadit k návštěvě/termínu, který se v reálu ještě neuskutečnil (vyloučení vykazování do budoucna).
- **Trvalá historie (nemazatelnost):** Historie pacienta a proběhlých návštěv se fyzicky nemaže kvůli právní doložitelnosti poskytnuté péče.

### 2.3 Co klient prohodil mimochodem (Nároky na UX a rozšiřitelnost)
- *„Musí to zvládnout běžná obsluha.“* $\to$ Rozhraní musí být maximálně přehledné, bez zbytečných kliků navíc, s velkými a srozumitelnými ovládacími prvky.
- *„Když zvoní telefon, nemůžu čekat, až se to načte.“* $\to$ Okamžitá odezva vyhledávání pacientů (debounce vyhledávání, indexy v DB).
- *„Bylo by hezké, kdybych to viděla i na tabletu, ale to nespěchá.“* $\to$ Responzivní webové rozhraní přizpůsobené i pro dotykové obrazovky.
- *„Pacienti by se možná mohli objednávat sami přes internet, ale bojím se toho.“* $\to$ Architektura a API musí být navrženy tak, aby umožnily budoucí napojení pacientského samoobslužného portálu bez nutnosti refaktoringu jádra systému.

---

## 3. Seznam rozhodnutí (Otevřené body klienta)

Následující tabulka dokumentuje rozhodnutí ke všem otevřeným bodům specifikovaným v zadání:

| # | Otevřený bod klienta | Přijaté rozhodnutí a technické řešení | Zdůvodnění |
|---|----------------------|---------------------------------------|------------|
| **1** | **Délka jednoho termínu** (*„podle toho, co se dělá“ vs. mřížka*) | Zavedena pevná časová mřížka s krokem **15 minut** (základní slot). Rezervace má počáteční čas a délku danou celočíselným násobkem základního slotu (15, 30, 45, 60 minut). Typ vyšetření předvyplní doporučenou délku. | Umožňuje zachovat přehledný vizuální kalendář pro recepci (mřížku) a zároveň flexibilitu pro komplexnější zákroky. |
| **2** | **Kdo zakládá rezervaci** (*recepce vs. pacienti přes internet*) | V 1. fázi je zápis umožněn **výhradně recepci**. Systém je však postaven na modulární třívrstvé architektuře s autorizovaným REST API. | Recepce neztratí kontrolu nad diářem a eliminují se obavy lékařky. Budoucí zapojení pacientského portálu bude pouze novým klientem téhož API. |
| **3** | **Co se zrušenou rezervací** (*odstranění z diáře vs. statistika nespolehlivých*) | Implementován stavový automat rezervace (`PLANOVANA`, `PROBEHLA`, `ZRUSENA_PACIENTEM`, `ZRUSENA_LEKAREM`). Zrušená rezervace **uvolní časový slot v kalendáři**, ale její záznam zůstává v DB s časovým razítkem a důvodem. | Kalendář zůstává čistý a volný pro nové pacienty, zatímco systém dokáže okamžitě spočítat a zobrazit statistiku častých rušení pacienta. |
| **4** | **Totožnost pacienta a duplicity** (*paní Nováková je v diáři 3x*) | Primárním unikátním identifikátorem pacienta je **číslo pojištěnce (rodné číslo)**. Při zadávání jména a telefonu systém provádí našeptávání a varuje před vznikem duplicit. | Zamezí se vícenásobné registraci stejného člověka pod mírně odlišným zápisem jména, což je klíčové pro správné vykazování pojišťovnám. |
| **5** | **Nemoc lékaře** (*jak postupovat při výpadku*) | Funkce **„Nahlásit nepřítomnost lékaře“** pro vybraný interval. Systém automaticky převede všechny kolidující rezervace do stavu `K_PREOBJEDNANI` a vytvoří přehledný pracovní frontový seznam (Call-list) pro recepci. | Nemoc nelze řešit plnou automatizací, protože pacienti vyžadují lidský přístup a domluvu náhradního termínu; obsluha však dostane přesný seznam koho a kdy kontaktovat. |


---

## 4. Povinné minimum a technická architektura

Systém striktně naplňuje veškeré požadavky semestrálního projektu předmětu PPRO:

### 4.1 Třívrstvá architektura se závislostmi jedním směrem
Závislosti směřují výhradně shora dolů:
1. **Prezentační vrstva (Web UI / Controller / API)**: Zpracování HTTP požadavků, validace vstupních DTO, formátování odpovědí pro recepci.
2. **Aplikační a doménová vrstva (Services & Domain Model)**: Obchodní logika, validace překryvů rezervací, správa stavů, kalkulace měsíčních bodových výkazů. Nezávisí na prezentační vrstvě.
3. **Infrastrukturní a datová vrstva (Repositories & Database Context)**: Mapování entit na relační databázi, transakční zpracování, migrační skripty.

### 4.2 Relační databáze a migrace
- Provozována v kontejneru Docker.
- Všechny změny schématu jsou řízeny verzovanými migracemi.
- Použití cizích klíčů, unikátních indexů a kontrolních omezení (CHECK constraints) pro zajištění integrity dat.

### 4.3 Datový model (minimálně 5 entit + M:N vazba)
Model obsahuje tyto klíčové doménové entity:
1. `Pacient` – osobní a kontaktní údaje, číslo pojištěnce, kód pojišťovny.
2. `Lekar` – identifikační údaje lékaře, specializace, příznak aktivity.
3. `OrdinacniDoba` – ordinační okna lékaře (den v týdnu, platnost od-do, ordinace/adresa).
4. `Rezervace` – vazba na pacienta a lékaře, plánovaný čas začátku a konce, stav, důvod návštěvy.
5. `Navsteva` – záznam o reálně proběhlé návštěvě vázaný na rezervaci, datum a čas realizace, poznámka lékaře.
6. `Vykon` – číselník zdravotních výkonů (kód výkonu, popis, bodová hodnota).
7. `NavstevaVykon` – **asociační entita vazby M:N** spojující `Navsteva` a `Vykon` s dodatečným atributem `pocet` (počet aplikací/provedení daného výkonu).

```mermaid
erDiagram
    PACIENT ||--o{ REZERVACE : "má"
    LEKAR ||--o{ ORDINACNI_DOBA : "má rozvrh"
    LEKAR ||--o{ REZERVACE : "přijímá"
    REZERVACE ||--o| NAVSTEVA : "je realizována jako"
    NAVSTEVA ||--|{ NAVSTEVA_VYKON : "obsahuje"
    VYKON ||--|{ NAVSTEVA_VYKON : "je vykázán v"

    PACIENT {
        uuid id PK
        string jmeno
        string prijmeni
        int rok_narozeni
        string telefon
        string cislo_pojistence UK
        string pojistovna
    }

    LEKAR {
        uuid id PK
        string titul_jmeno_prijmeni
        string specializace
    }

    ORDINACNI_DOBA {
        uuid id PK
        uuid lekar_id FK
        int den_v_tydnu
        time cas_od
        time cas_do
        string pracoviste
        boolean lze_objednavat
    }

    REZERVACE {
        uuid id PK
        uuid pacient_id FK
        uuid lekar_id FK
        datetime cas_od
        datetime cas_do
        string stav
        string poznamka
    }

    NAVSTEVA {
        uuid id PK
        uuid rezervace_id FK
        datetime datum_cas
        string lekarska_zprava
    }

    VYKON {
        string kod PK
        string nazev
        int body
    }

    NAVSTEVA_VYKON {
        uuid id PK
        uuid navsteva_id FK
        string vykon_kod FK
        int pocet
    }
```

---

## 5. Technologický stack a struktura projektu

### Použité technologie
- **Jazyk & Platforma:** Java 21 LTS (OpenJDK)
- **Framework:** Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Bean Validation, Thymeleaf)
- **Relační databáze:** PostgreSQL 16 (v Docker kontejneru)
- **Databázové migrace:** Flyway (verzované SQL migrace v `src/main/resources/db/migration`)
- **Prezentační vrstva:** Thymeleaf + Vanilla CSS & REST API
- **Testování:** JUnit 5, Mockito (unit testy byznys logiky), MockMvc & H2 (integrační testy kontrolerů)
- **Kontejnerizace:** Multi-stage Dockerfile a Docker Compose

### Adresářová struktura třívrstvé architektury
```
src/main/java/cz/uhk/fim/ppro/ordinace/
├── OrdinaceApplication.java        # Vstupní bod Spring Boot aplikace
├── presentation/                   # 1. Prezentační vrstva
│   ├── controller/                 # Web kontrolery (UI Thymeleaf) & REST kontrolery (/api/*)
│   │   ├── PacientWebController.java / PacientRestController.java
│   │   ├── KalendarWebController.java / RezervaceRestController.java
│   │   ├── LekarWebController.java / LekarRestController.java
│   │   ├── NavstevaWebController.java / NavstevaRestController.java
│   │   ├── VykazWebController.java / VykazRestController.java
│   │   ├── CallListWebController.java
│   │   └── GlobalExceptionHandler.java # Centralizované ošetření chyb napříč REST API
│   ├── dto/                        # Datové přenosové objekty (DTOs pro pacienty, lékaře, rezervace, návštěvy, výkazy)
│   └── validation/                 # Validační anotace a validátory (rodné číslo, formát formuláře)
├── application/                    # 2. Aplikační a doménová vrstva
│   ├── model/                      # Doménové entity (Pacient, Lekar, OrdinacniDoba, Rezervace, Navsteva, Vykon, NavstevaVykon)
│   ├── service/                    # Byznys logika (PacientService, LekarService, RezervaceService, NavstevaService, VykazService, VykonService)
│   ├── validation/                 # Doménový validátor rodného čísla
│   └── exception/                  # Doménové výjimky (BusinessRuleException, DuplicateEntityException, EntityNotFoundException)
└── infrastructure/                 # 3. Infrastrukturní a datová vrstva
    └── repository/                 # Spring Data JPA repozitáře (Pacient, Lekar, OrdinacniDoba, Rezervace, Navsteva, Vykon, NavstevaVykon)
```

---

## 6. Spuštění a provoz systému

Systém je plně kontejnerizován pro snadné lokální spuštění bez nutnosti instalace lokálních závislostí:

```bash
# Spuštění kompletního prostředí (aplikace + relační databáze PostgreSQL + automatické Flyway migrace)
docker compose up --build
```

Po spuštění je systém dostupný na adresách:
- **Webové rozhraní (Recepce & Lékaři):**
  - **Pacienti:** [http://localhost:8080/pacienti](http://localhost:8080/pacienti) (nebo [http://localhost:8080](http://localhost:8080))
  - **Týdenní kalendář & Rezervace:** [http://localhost:8080/kalendar](http://localhost:8080/kalendar)
  - **Lékaři & Ordinační hodiny:** [http://localhost:8080/lekari](http://localhost:8080/lekari)
  - **Návštěvy & Zdravotní výkony:** [http://localhost:8080/navstevy](http://localhost:8080/navstevy)
  - **Měsíční výkaz pro pojišťovnu:** [http://localhost:8080/vykazy](http://localhost:8080/vykazy)
  - **Nemoc lékaře & Call-list:** [http://localhost:8080/call-list](http://localhost:8080/call-list)
- **REST API:**
  - `GET /api/pacienti`, `POST /api/pacienti`
  - `GET /api/lekari`, `GET /api/lekari/{id}/ordinacni-doby`
  - `POST /api/rezervace`, `GET /api/rezervace/{id}`, `POST /api/rezervace/{id}/zrusit`, `GET /api/rezervace/call-list`
  - `GET /api/navstevy`, `POST /api/navstevy` (M:N vazba na výkony s počtem)
  - `GET /api/vykony`
  - `GET /api/vykazy/mesicni?rok=2026&mesic=10&lekarId=...`

### Spuštění testů
Unit a integrační testy ověřují správné chování vyhledávání, detekci kolizí, ordinační mantinely, zákaz vykazování do budoucna, Call-list i výkaznictví pojišťovnám:
```bash
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-21-alpine mvn test
```

---

## 7. Protokol o postupu a změnách (Decision & Change Log)

- **2026-09-30:**
  - Inicializace git repozitáře a propojení s GitHub (`AlexMusil/PPRO26`).
  - Vytvoření pravidel asistenta v [`AGENTS.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/AGENTS.md) (commit s `-m`, zákaz push bez explicitního souhlasu, udržování dokumentace).
  - Vytvoření výchozí technické dokumentace a Seznamu rozhodnutí v [`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md) pro Zadání A (Rezervace v ordinaci).
  - Nastavení git pre-commit hooku pro kontrolu aktuálnosti dokumentace.
  - **Pilotní demo – Entita Pacient:**
    - Zvolen technologický stack Java 21, Spring Boot 3.3.4, PostgreSQL 16 a Flyway.
    - Implementována třívrstvá architektura (`presentation` $\to$ `application` $\to$ `infrastructure`).
    - Vytvořena migrace `V1__create_pacient_table.sql` a seed skript `V2__seed_pacienti.sql` s 10 syntetickými záznamy pacientů (včetně případů z příběhu klienta: paní Nováková, paní Krátká).
    - Implementována doménová pravidla v `PacientService`: ochrana před duplicitami (unikátní rodné číslo / číslo pojištěnce, shoda jméno + telefon), striktní zákaz mazání historie pacienta (`BusinessRuleException`).
    - Vytvořeno webové UI (`pacienti.html`, `style.css`) s vyhledávačem a zápisem nového pacienta.
    - Připraven `Dockerfile` (multi-stage) a `docker-compose.yml` s healthcheckem pro PostgreSQL.
    - Implementována sada unit a integračních testů (`PacientServiceTest`, `PacientRestControllerTest`).

- **2026-10-07:**
  - **Zabezpečení a validace formuláře pacienta (klientská i serverová vrstva):**
    - Implementována doménová a prezentační validace českého rodného čísla / čísla pojištěnce (`RodneCisloValidator`, `@ValidRodneCislo`, `@ValidPacient`).
    - Zahrnuta kontrola formátu (9 číslic do 1953, 10 číslic od 1954), platnosti kalendářního data (měsíc pro muže 1–12, ženy 51–62, přestupné roky) a kontrolního součtu modulo 11.
    - Zavedena křížová validace souladu roku narození s rodným číslem (zamezení rozporů v kartě pacienta).
    - Doplněna validační pravidla pro jméno, příjmení (pouze písmena, min. 2 znaky), telefon a kód zdravotní pojišťovny.
    - Vytvořen klientský skript `patient-form.js` (automatické doplňování lomítka, okamžitá vizuální indikace chyb a blokace odeslání nevalidního formuláře).
    - Aktualizovány testy `PacientServiceTest` a `PacientRestControllerTest` o testovací scénáře odmítnutí chybných údajů (HTTP 400 Bad Request, BusinessRuleException).
    - Vyčištění uživatelského rozhraní a zdrojových kódů od vývojových a meta poznámek pro čisté produkční zobrazení.
    - Nahrazení emotikonů v uživatelském rozhraní čistými vektorovými SVG ikonami a zjednodušení záhlaví.

- **2026-11-04:**
  - **Implementace kompletního doménového modelu a splnění všech požadavků předmětu PPRO:**
    - **Nové entity třívrstvé architektury:**
      - `Lekar` – identifikační údaje lékařů ordinace, specializace, aktivní stav.
      - `OrdinacniDoba` – ordinační hodiny pro jednotlivé dny v týdnu, adresa pracoviště, příznak `lzeObjednavat` (zohlednění externí praxe v úterý odpoledne na jiné adrese).
      - `Vykon` – centrální číselník zdravotních výkonů VZP s bodovým ohodnocením.
      - `Rezervace` – časové okno rezervace, stavový automat (`PLANOVANA`, `PROBEHLA`, `ZRUSENA_PACIENTEM`, `ZRUSENA_LEKAREM`, `K_PREOBJEDNANI`), důvod návštěvy.
      - `Navsteva` – záznam o reálně uskutečněném vyšetření s lékařskou zprávou a doporučením.
      - `NavstevaVykon` – **povinná asociační entita vazby M:N** mezi `Navsteva` a `Vykon` s dodatečným atributem `pocet` (počet provedení výkonu u jedné návštěvy).
    - **Verzované databázové migrace Flyway a syntetická data:**
      - `V3__create_lekar_and_ordinacni_doba.sql` – struktura pro lékaře a ordinační hodiny.
      - `V4__create_vykon_table.sql` – číselník výkonů s body.
      - `V5__create_rezervace_navsteva_and_navsteva_vykon.sql` – tabulky rezervací, návštěv a asociační vazby M:N.
      - `V6__seed_data.sql` – syntetická data 3 lékařů ordinace, jejich ordinační rozvrhy, 8 zdravotních výkonů VZP, vzorové proběhlé i zrušené rezervace a návštěvy s vykázanými výkony.
    - **Aplikační servisní vrstva a obchodní pravidla:**
      - `RezervaceService`: striktní zákaz překryvu rezervací stejného lékaře, validace 15minutové mřížky, kontrola platných ordinačních hodin a pracoviště, uvolnění slotu při zrušení termínu, výpočet statistiky rušení pacienta.
      - `NavstevaService`: zaevidování proběhlé návštěvy, přiřazení výkonů s počtem, ochrana proti vykazování výkonů do budoucna (`casOd.isAfter(now)`).
      - `VykazService`: agregace provedených výkonů a výpočet celkového počtu bodů pro pojišťovny za vybraný měsíc a lékaře.
      - Řešení nemoci lékaře (Otevřený bod 5 klienta): nahlášení nepřítomnosti lékaře $\to$ automatický přesun kolidujících rezervací do `K_PREOBJEDNANI` a vygenerování pracovního Call-listu pro recepci.
    - **Prezentační vrstva (Web UI Thymeleaf & REST API):**
      - Vytvoření jednotné navigace a záhlaví (`templates/fragments/layout.html`).
      - Nové webové stránky: `/kalendar` (týdenní interaktivní přehled slotů), `/lekari` (seznam lékařů a rozvrhů), `/navstevy` (evidence návštěv a vykazování výkonů), `/vykazy` (měsíční výkaz pro pojišťovnu), `/call-list` (seznam pacientů k obvolání).
      - REST API kontrolery: `LekarRestController`, `VykonRestController`, `RezervaceRestController`, `NavstevaRestController`, `VykazRestController` se sjednoceným `GlobalExceptionHandler`.
    - **Testování:**
      - Implementována sada unit testů pro doménovou byznys logiku (`RezervaceServiceTest`, `NavstevaServiceTest`, `VykazServiceTest`).
      - Implementovány integrační testy REST kontrolerů (`RezervaceRestControllerTest`, `VykonRestControllerTest`).

