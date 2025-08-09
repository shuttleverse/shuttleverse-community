alter table public.coach
    add column id uuid;
alter table public.court
    add column id uuid;
alter table public.stringer
    add column id uuid;
alter table public.coach_price
    rename column price_id to id;
alter table public.coach_schedule
    rename column schedule_id to id;
alter table public.court_price
    rename column price_id to id;
alter table public.court_schedule
    rename column schedule_id to id;
alter table public.stringer_price
    rename column price_id to id;

alter table public.court_price
    drop constraint if exists fk_court_price_on_court;
alter table public.court_schedule
    drop constraint if exists fk_court_schedule_on_court;
alter table public.stringer_price
    drop constraint if exists fk_stringer_price_on_stringer;
alter table public.coach_price
    drop constraint if exists coach_price_coach_id_fkey;
alter table public.coach_schedule
    drop constraint if exists coach_schedule_coach_id_fkey;

alter table public.coach
    drop constraint if exists pk_coach;
alter table public.court
    drop constraint if exists pk_court;
alter table public.stringer
    drop constraint if exists pk_stringer;
alter table public.coach_schedule
    drop constraint if exists pk_coach_schedule;
alter table public.coach_price
    drop constraint if exists pk_coach_price;
alter table public.court_schedule
    drop constraint if exists pk_court_schedule;
alter table public.court_price
    drop constraint if exists pk_court_price;
alter table public.stringer_price
    drop constraint if exists pk_stringer_price;

update public.coach
set id = gen_random_uuid();
update public.court
set id = gen_random_uuid();
update public.stringer
set id = gen_random_uuid();

update public.court_price cp
set court_id = c.id
from public.court c
where cp.court_id = c.court_id;

update public.court_schedule cs
set court_id = c.id
from public.court c
where cs.court_id = c.court_id;

update public.stringer_price sp
set stringer_id = s.id
from public.stringer s
where sp.stringer_id = s.stringer_id;

update public.coach_price cp
set coach_id = c.id
from public.coach c
where cp.coach_id = c.coach_id;

update public.coach_schedule cs
set coach_id = c.id
from public.coach c
where cs.coach_id = c.coach_id;

drop index if exists public.pk_coach;
drop index if exists public.pk_court;
drop index if exists public.pk_stringer;
drop index if exists public.pk_court_price;
drop index if exists public.pk_court_schedule;
drop index if exists public.pk_stringer_price;
drop index if exists public.pk_coach_schedule;
drop index if exists public.pk_coach_price;

alter table public.coach
    drop column coach_id;
alter table public.court
    drop column court_id;
alter table public.stringer
    drop column stringer_id;

alter table public.coach
    alter column id set not null;
alter table public.court
    alter column id set not null;
alter table public.stringer
    alter column id set not null;

create unique index pk_coach on public.coach using btree (id);
create unique index pk_court on public.court using btree (id);
create unique index pk_stringer on public.stringer using btree (id);

create unique index pk_court_price on public.court_price using btree (id);
create unique index pk_court_schedule on public.court_schedule using btree (id);
create unique index pk_stringer_price on public.stringer_price using btree (id);
create unique index pk_coach_price on public.coach_price using btree (id);
create unique index pk_coach_schedule on public.coach_schedule using btree (id);

alter table public.coach
    add constraint pk_coach primary key using index pk_coach;
alter table public.court
    add constraint pk_court primary key using index pk_court;
alter table public.stringer
    add constraint pk_stringer primary key using index pk_stringer;
alter table public.coach_price

    add constraint pk_coach_price primary key using index pk_coach_price;
alter table public.coach_schedule
    add constraint pk_coach_schedule primary key using index pk_coach_schedule;
alter table public.court_price
    add constraint pk_court_price primary key using index pk_court_price;
alter table public.court_schedule
    add constraint pk_court_schedule primary key using index pk_court_schedule;
alter table public.stringer_price
    add constraint pk_stringer_price primary key using index pk_stringer_price;

alter table public.court_price
    add constraint fk_court_price_on_court
        foreign key (court_id) references public.court (id) on update cascade on delete cascade;

alter table public.court_schedule
    add constraint fk_court_schedule_on_court
        foreign key (court_id) references public.court (id) on update cascade on delete cascade;

alter table public.stringer_price
    add constraint fk_stringer_price_on_stringer
        foreign key (stringer_id) references public.stringer (id) on update cascade on delete cascade;

alter table public.coach_price
    add constraint fk_coach_price_on_coach
        foreign key (coach_id) references public.coach (id) on update cascade on delete cascade;

alter table public.coach_schedule
    add constraint fk_coach_schedule_coach
        foreign key (coach_id) references public.coach (id) on update cascade on delete cascade;
