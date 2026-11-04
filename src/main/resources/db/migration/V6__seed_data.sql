-- V6__seed_data.sql
-- Syntetická testovací data pro lékaře, ordinační doby, výkony, rezervace a návštěvy

-- 1. Lékaři Ordinace Na Vyhlídce
INSERT INTO lekar (id, titul_pred, jmeno, prijmeni, titul_za, specializace, telefon, email, aktivni) VALUES
('b0000000-0000-0000-0000-000000000001', 'MUDr.', 'Jana', 'Hrubá', NULL, 'Všeobecné praktické lékařství', '+420 495 123 451', 'hruba@ordinace-vyhlidka.cz', TRUE),
('b0000000-0000-0000-0000-000000000002', 'MUDr.', 'Petr', 'Svoboda', NULL, 'Vnitřní lékařství a kardiologie', '+420 495 123 452', 'svoboda@ordinace-vyhlidka.cz', TRUE),
('b0000000-0000-0000-0000-000000000003', 'MUDr.', 'Eva', 'Černá', 'Ph.D.', 'Diabetologie a endokrinologie', '+420 495 123 453', 'cerna@ordinace-vyhlidka.cz', TRUE);

-- 2. Ordinační hodiny lékařů
-- MUDr. Jana Hrubá: Po 8-13, St 8-14, Pá 8-12 (Ordinace Na Vyhlídce)
INSERT INTO ordinacni_doba (id, lekar_id, den_v_tydnu, cas_od, cas_do, pracoviste, lze_objednavat, poznamka) VALUES
('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 1, '08:00:00', '13:00:00', 'Ordinace Na Vyhlídce - ordinace 1', TRUE, 'Běžná ordinace a prevence'),
('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 3, '08:00:00', '14:00:00', 'Ordinace Na Vyhlídce - ordinace 1', TRUE, 'Objednaní pacienti'),
('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 5, '08:00:00', '12:00:00', 'Ordinace Na Vyhlídce - ordinace 1', TRUE, 'Akutní případy a kontroly');

-- MUDr. Petr Svoboda: Út 8-12 (Vyhlídka), Út 13-17 (externí pracoviště Poliklinika Jih - nelze objednávat!), Čt 8-14 (Vyhlídka)
INSERT INTO ordinacni_doba (id, lekar_id, den_v_tydnu, cas_od, cas_do, pracoviste, lze_objednavat, poznamka) VALUES
('c0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000002', 2, '08:00:00', '12:00:00', 'Ordinace Na Vyhlídce - ordinace 2', TRUE, 'Kardiologická poradna'),
('c0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000002', 2, '13:00:00', '17:00:00', 'Poliklinika Jih - externí kardiologie', FALSE, 'Externí pracoviště, nelze objednávat přes recepci Na Vyhlídce'),
('c0000000-0000-0000-0000-000000000006', 'b0000000-0000-0000-0000-000000000002', 4, '08:00:00', '14:00:00', 'Ordinace Na Vyhlídce - ordinace 2', TRUE, 'Interní ambulance a EKG');

-- MUDr. Eva Černá: St 12-17, Čt 13-18 (Vyhlídka)
INSERT INTO ordinacni_doba (id, lekar_id, den_v_tydnu, cas_od, cas_do, pracoviste, lze_objednavat, poznamka) VALUES
('c0000000-0000-0000-0000-000000000007', 'b0000000-0000-0000-0000-000000000003', 3, '12:00:00', '17:00:00', 'Ordinace Na Vyhlídce - ordinace 3', TRUE, 'Diabetologická ambulance'),
('c0000000-0000-0000-0000-000000000008', 'b0000000-0000-0000-0000-000000000003', 4, '13:00:00', '18:00:00', 'Ordinace Na Vyhlídce - ordinace 3', TRUE, 'Endokrinologické konzultace');

-- 3. Číselník zdravotních výkonů VZP
INSERT INTO vykon (kod, nazev, body, popis, aktivni) VALUES
('01543', 'Komplexní vyšetření praktickým lékařem', 510, 'Vstupní nebo komplexní vyšetření praktickým lékařem pro dospělé', TRUE),
('09513', 'Cílené vyšetření lékařem', 280, 'Cílené klinické vyšetření a zhodnocení stavu pacienta', TRUE),
('09511', 'Kontrolní vyšetření lékařem', 155, 'Běžná kontrola a předpis dlouhodobé medikace', TRUE),
('09115', 'Injekce i.m., i.v., s.c.', 65, 'Aplikace intramuskulární, intravenózní nebo subkutánní injekce', TRUE),
('09123', 'Odběr krve ze žíly', 45, 'Venepunkce pro biochemické nebo hematologické vyšetření', TRUE),
('01520', 'Preventivní prohlídka', 620, 'Všeobecná periodická preventivní prohlídka', TRUE),
('09127', 'Aplikace očkovací látky', 90, 'Očkování proti tetanu, chřipce nebo COVID-19', TRUE),
('17111', 'Elektrokardiografické vyšetření (EKG)', 215, 'Dvanáctisvodové EKG včetně popisu a vyhodnocení křivky', TRUE);

-- 4. Ukázkové rezervace
-- 4.1 Proběhlá rezervace pro Marii Novákovou (a0...1) u MUDr. Hrubé (b0...1)
INSERT INTO rezervace (id, pacient_id, lekar_id, cas_od, cas_do, stav, duvod_navstevy) VALUES
('d0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', '2026-10-12 08:30:00', '2026-10-12 09:00:00', 'PROBEHLA', 'Pravidelná kontrola tlaku');

-- 4.2 Zrušené rezervace paní Krátké (a0...2) - demonstrace statistiky nespolehlivých pacientů z příběhu
INSERT INTO rezervace (id, pacient_id, lekar_id, cas_od, cas_do, stav, duvod_navstevy, poznamka_zruseni) VALUES
('d0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', '2026-10-05 09:00:00', '2026-10-05 09:30:00', 'ZRUSENA_PACIENTEM', 'Kontrola cholesterolu', 'Pacientka zrušila telefonicky 1 hodinu předem'),
('d0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', '2026-10-19 10:00:00', '2026-10-19 10:30:00', 'ZRUSENA_PACIENTEM', 'Náhradní kontrola', 'Omluva z důvodu služební cesty');

-- 4.3 Další proběhlá rezervace pro Petra Dvořáka (a0...3) u MUDr. Svobody (b0...2)
INSERT INTO rezervace (id, pacient_id, lekar_id, cas_od, cas_do, stav, duvod_navstevy) VALUES
('d0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', '2026-10-13 09:00:00', '2026-10-13 09:30:00', 'PROBEHLA', 'Kardiologická kontrola a EKG');

-- 5. Realizované návštěvy (vázané na proběhlé rezervace)
INSERT INTO navsteva (id, rezervace_id, datum_cas, lekarska_zprava, doporuceni) VALUES
('e0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', '2026-10-12 08:35:00', 'Tlak 135/85 mmHg, kompenzováno. Plíce a srdce bez patologického nálezu. Aplikována 1x injekce.', 'Pokračovat v zavedené medikaci, kontrola za 6 měsíců.'),
('e0000000-0000-0000-0000-000000000002', 'd0000000-0000-0000-0000-000000000004', '2026-10-13 09:05:00', 'EKG s normálním sinusovým rytmem bez známek ischémie. Subjektivně bez stenokardií.', 'Laboratoř lipidového spektra za 3 měsíce.');

-- 6. Vazba M:N mezi návštěvou a výkony s atributem pocet
-- Návštěva 1 (Nováková u Hrubé): 1x Cílené vyšetření (09513) + 2x Injekce (09115)
INSERT INTO navsteva_vykon (id, navsteva_id, vykon_kod, pocet, poznamka) VALUES
('f0000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001', '09513', 1, 'Cílené vyšetření tlaku'),
('f0000000-0000-0000-0000-000000000002', 'e0000000-0000-0000-0000-000000000001', '09115', 2, 'Aplikace 2 injekčních dávek');

-- Návštěva 2 (Dvořák u Svobody): 1x Cílené vyšetření (09513) + 1x EKG (17111) + 1x Odběr krve (09123)
INSERT INTO navsteva_vykon (id, navsteva_id, vykon_kod, pocet, poznamka) VALUES
('f0000000-0000-0000-0000-000000000003', 'e0000000-0000-0000-0000-000000000002', '09513', 1, 'Kardiologické zhodnocení'),
('f0000000-0000-0000-0000-000000000004', 'e0000000-0000-0000-0000-000000000002', '17111', 1, '12svodové klidové EKG'),
('f0000000-0000-0000-0000-000000000005', 'e0000000-0000-0000-0000-000000000002', '09123', 1, 'Odběr žilní krve na lipidy');
