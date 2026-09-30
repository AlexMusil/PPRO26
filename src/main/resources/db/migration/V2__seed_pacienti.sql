-- V2__seed_pacienti.sql
-- Syntetická testovací data pacientů pro Ordinaci Na Vyhlídce

INSERT INTO pacient (id, jmeno, prijmeni, rok_narozeni, telefon, cislo_pojistence, pojistovna, poznamka) VALUES
('a0000000-0000-0000-0000-000000000001', 'Marie', 'Nováková', 1968, '+420 777 123 456', '685412/1234', '111', 'Pravidelná kontrola tlaku, alergie na penicilin'),
('a0000000-0000-0000-0000-000000000002', 'Jana', 'Krátká', 1982, '+420 732 987 654', '825910/2345', '207', 'Pozor na časté rušení termínů'),
('a0000000-0000-0000-0000-000000000003', 'Petr', 'Dvořák', 1975, '+420 603 456 789', '750315/3456', '211', 'Diabetik II. typu'),
('a0000000-0000-0000-0000-000000000004', 'Eva', 'Svobodová', 1991, '+420 721 112 233', '916201/4567', '111', 'Těhotenská poradna'),
('a0000000-0000-0000-0000-000000000005', 'Tomáš', 'Černý', 1988, '+420 608 554 433', '881123/5678', '205', 'Astma bronchiale'),
('a0000000-0000-0000-0000-000000000006', 'Lucie', 'Kučerová', 2001, '+420 774 889 900', '015505/6789', '111', 'Studentka, běžné preventivní prohlídky'),
('a0000000-0000-0000-0000-000000000007', 'Martin', 'Veselý', 1962, '+420 602 334 455', '620819/7890', '201', 'Bolesti zad, ortopedické doporučení'),
('a0000000-0000-0000-0000-000000000008', 'Hana', 'Němcová', 1954, '+420 731 223 344', '545930/8901', '111', 'Kardiak, vyžaduje bezbariérový přístup'),
('a0000000-0000-0000-0000-000000000009', 'Jaroslav', 'Horák', 1979, '+420 776 654 321', '790408/9012', '207', 'Pracovní lékařství'),
('a0000000-0000-0000-0000-000000000010', 'Zuzana', 'Pospíšilová', 1995, '+420 737 445 566', '955814/0123', '211', 'Očkování před cestou do zahraničí');
