insert into client(nom, adresse, contact, mdp) values ('Mbola', '123 Main St', '555-1234', 'talenta');
insert into client(nom, adresse, contact, mdp) values ('Rasoa', '456 Elm St', '555-5678', 'Rabe');
-- Regions
insert into region(id, libelle) values (1, 'Analamanga');
insert into region(id, libelle) values (2, 'Vakinankaratra');
insert into region(id, libelle) values (3, 'Atsinanana');
insert into region(id, libelle) values (4, 'Diana');
insert into region(id, libelle) values (5, 'Atsimo-Andrefana');
insert into region(id, libelle) values (6, 'Sava');

-- Districts (id_region references region.id)
insert into district(id, libelle, id_region) values (1, 'Antananarivo-Renivohitra', 1);
insert into district(id, libelle, id_region) values (2, 'Antananarivo-Avaradrano', 1);
insert into district(id, libelle, id_region) values (3, 'Antsirabe I', 2);
insert into district(id, libelle, id_region) values (4, 'Faratsiho', 2);
insert into district(id, libelle, id_region) values (5, 'Toamasina I', 3);
insert into district(id, libelle, id_region) values (6, 'Vatomandry', 3);
insert into district(id, libelle, id_region) values (7, 'Antsiranana', 4);
insert into district(id, libelle, id_region) values (8, 'Ambanja', 4);
insert into district(id, libelle, id_region) values (9, 'Toliara I', 5);
insert into district(id, libelle, id_region) values (10, 'Betioky Atsimo', 5);
insert into district(id, libelle, id_region) values (11, 'Sambava', 6);
insert into district(id, libelle, id_region) values (12, 'Andapa', 6);

-- Communes (id_district references district.id)
insert into commune(id, libelle, id_district) values (1, 'Ambohijatovo', 1);
insert into commune(id, libelle, id_district) values (2, 'Ankadifotsy', 1);
insert into commune(id, libelle, id_district) values (3, 'Ambohimanambola', 2);
insert into commune(id, libelle, id_district) values (4, 'Ambohimangakely', 2);
insert into commune(id, libelle, id_district) values (5, 'Antsirabe', 3);
insert into commune(id, libelle, id_district) values (6, 'Antanambao', 3);
insert into commune(id, libelle, id_district) values (7, 'Faratsiho-ville', 4);
insert into commune(id, libelle, id_district) values (8, 'Anjoma', 4);
insert into commune(id, libelle, id_district) values (9, 'Toamasina', 5);
insert into commune(id, libelle, id_district) values (10, 'Ambodimasina', 5);
insert into commune(id, libelle, id_district) values (11, 'Vatomandry', 6);
insert into commune(id, libelle, id_district) values (12, 'Antanambe', 6);
insert into commune(id, libelle, id_district) values (13, 'Antsiranana-ville', 7);
insert into commune(id, libelle, id_district) values (14, 'Ramena', 7);
insert into commune(id, libelle, id_district) values (15, 'Ambanja-ville', 8);
insert into commune(id, libelle, id_district) values (16, 'Ankatafa', 8);
insert into commune(id, libelle, id_district) values (17, 'Toliara-ville', 9);
insert into commune(id, libelle, id_district) values (18, 'Mangily', 9);
insert into commune(id, libelle, id_district) values (19, 'Betioky-center', 10);
insert into commune(id, libelle, id_district) values (20, 'Manombo', 10);
insert into commune(id, libelle, id_district) values (21, 'Sambava-ville', 11);
insert into commune(id, libelle, id_district) values (22, 'Antohobe', 11);
insert into commune(id, libelle, id_district) values (23, 'Andapa-ville', 12);
insert into commune(id, libelle, id_district) values (24, 'Ambodivoahangy', 12);

TRUNCATE TABLE statut RESTART IDENTITY CASCADE;
insert into statut(id, libelle) values (1, 'dossier cree');
insert into statut(id, libelle) values (2, 'demande etude cree');
insert into statut(id, libelle) values (3, 'demande etude accepte');
insert into statut(id, libelle) values (4, 'demande forage cree');
insert into statut(id, libelle) values (5, 'demande forage accepte');
insert into statut(id, libelle) values (6, 'demande travail cree');
insert into statut(id, libelle) values (7, 'demande travail termine');


create table if not exists config (
    id bigserial primary key,
    id1 integer,
    id2 integer,
    dt1 integer,
    dt2 integer,
    code_couleur varchar(20)
);

alter table if exists config add column if not exists id bigserial;
alter table if exists config add column if not exists dt1 integer;
alter table if exists config add column if not exists dt2 integer;

do $$
declare
    constraint_to_drop text;
begin
    if exists (
        select 1
        from information_schema.columns
        where table_schema = 'public'
          and table_name = 'config'
          and column_name = 'dt'
    ) then
        for constraint_to_drop in
            select distinct tc.constraint_name
            from information_schema.table_constraints tc
            join information_schema.key_column_usage kcu
              on tc.constraint_schema = kcu.constraint_schema
             and tc.constraint_name = kcu.constraint_name
             and tc.table_name = kcu.table_name
            where tc.table_schema = 'public'
              and tc.table_name = 'config'
              and kcu.column_name = 'dt'
        loop
            execute format('alter table config drop constraint if exists %I', constraint_to_drop);
        end loop;

        alter table config drop column if exists dt;
    end if;
end $$;

TRUNCATE TABLE config RESTART IDENTITY CASCADE;
insert into config(id1, id2, dt1, dt2, code_couleur) values (1, 2, 480, 600, 'jaune');
insert into config(id1, id2, dt1, dt2, code_couleur) values (1, 2, 1440, 2880, 'rouge');
insert into config(id1, id2, dt1, dt2, code_couleur) values (2, 3, 240, 300, 'jaune');
insert into config(id1, id2, dt1, dt2, code_couleur) values (2, 3, 360, 480, 'rouge');
insert into config(id1, id2, dt1, dt2, code_couleur) values (3, 4, 60, 120, 'jaune');
insert into config(id1, id2, dt1, dt2, code_couleur) values (3, 4, 180, 240, 'rouge');
insert into config(id1, id2, dt1, dt2, code_couleur) values (4, 5, 240, 480, 'jaune');
insert into config(id1, id2, dt1, dt2, code_couleur) values (4, 5, 600, 720, 'rouge');
insert into config(id1, id2, dt1, dt2, code_couleur) values (5, 6, 1200, 1800, 'jaune');
insert into config(id1, id2, dt1, dt2, code_couleur) values (5, 6, 1801, 3600, 'rouge');
insert into config(id1, id2, dt1, dt2, code_couleur) values (6, 7, 3600, 4200, 'jaune');
insert into config(id1, id2, dt1, dt2, code_couleur) values (6, 7, 4800, 6000, 'rouge');

alter table if exists demande_statut add column if not exists duree_travaille_total double precision;

update demande_statut set date='2026-06-02 16:00:00' where id=2;

