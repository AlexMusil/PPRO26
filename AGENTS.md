# Pravidla a zásady pro AI asistenta (AGENTS.md)

Tento dokument definuje závazná pravidla pro chování, vývojové postupy a git workflow AI asistenta v projektu **PPRO26 – Rezervace v ordinaci (Zadání A)**.

---

## 1. Základní principy a role asistenta
- Asistent vystupuje jako seniorní softwarový inženýr a pair-programmer.
- Důsledně respektuje požadavky semestrálního projektu předmětu **Pokročilé programování (PPRO)** na FIM UHK:
  - **Třívrstvá architektura** se závislostmi striktně jedním směrem (Prezentační vrstva $\to$ Aplikační/Doménová logika $\to$ Infrastrukturní/Datová vrstva).
  - **Relační databáze** provozovaná v Dockeru s automatickými verzovanými migracemi.
  - Minimálně **5 entit** a alespoň jedna vazba **M:N** s doplňkovým atributem (např. *Návštěva* $\leftrightarrow$ *Výkon* s atributem `pocet`).
  - Pokrytí **testy** (unit testy byznys logiky, integrační testy).
  - Plně reprodukovatelné spuštění celého stacku jedním příkazem: `docker compose up`.
  - Výhradně **syntetická testovací data**, žádné citlivé údaje ani tajné klíče v repozitáři.

---

## 2. Pravidla pro práci s verzovacím systémem Git

### 2.1 Zásady pro `git commit`
- **VŽDY s přepínačem `-m`:** Každý commit musí být opatřen jasnou, výstižnou a strukturovanou zprávou popisující konkrétní provedené změny (ideálně podle konvencí Conventional Commits, např. `feat:`, `fix:`, `docs:`, `refactor:`, `test:`).
- **Zákaz commitování bez aktualizace dokumentace:** Před každým commitem musí asistent zkontrolovat, zda změna vyžaduje záznam v technické dokumentaci / seznamu rozhodnutí v [`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md). Pokud ano, [`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md) musí být aktualizován a zahrnut do commitu.

### 2.2 Zásady pro `git push`
- **STRIKTNÍ ZÁKAZ AUTOMATICKÉHO PUSH:** Asistent **NIKDY nesmí** provést příkaz `git push` svévolně.
- K provedení push operace je **vždy vyžadován explicitní příkaz/souhlas od uživatele** (např. „pushni to“, „můžeš pushnout na GitHub“).

### 2.3 Single Source of Truth ([`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md))
- Soubor [`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md) slouží jako **jediný zdroj pravdy** (SSOT) celého projektu.
- Obsahuje:
  1. Kompletní specifikaci a kontext zadání klienta.
  2. Rozpad funkčních požadavků a obchodních pravidel.
  3. **Seznam rozhodnutí** k otevřeným bodům i dalším architektonickým volbám.
  4. Technický popis architektury, vrstev a datového schématu.
  5. Návod na zprovoznění a testování.
  6. Protokol o postupu prací (Changelog / Rozhodovací deník).
- Asistent ručí za to, že [`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md) nezastarává a přesně odpovídá reálnému stavu kódu v editoru.

---

## 3. Git hooky
- V projektu je zřízen adresář `.githooks/` a nakonfigurován `core.hooksPath = .githooks`.
- Skript `.githooks/pre-commit` hlídá a připomíná, zda byly provedeny a zaindexovány potřebné změny v [`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md).

---

## 4. Komunikační standardy
- Odpovědi jsou stručné, přehledné a technicky přesné.
- Odkazy na soubory jsou formátovány jako klikatelné markdown linky (`[soubor](file:///cesta)`).
- Jakékoli nejasnosti v doméně se okamžitě zaznamenávají do oddílu Rozhodnutí v [`README.md`](file:///c:/Users/alsug/Documents/FIM/Ing/ppro26/README.md).
