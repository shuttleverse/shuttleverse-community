alter table "public"."coach_price"
    add column "min_price"     double precision,
    add column "max_price"     double precision,
    add column "description"   text,
    add column "duration_unit" text default 'minutes';

alter table "public"."court_price"
    add column "min_price"     double precision,
    add column "max_price"     double precision,
    add column "description"   text,
    add column "duration_unit" text default 'minutes';

update "public"."coach_price"
set min_price     = price,
    max_price     = price,
    duration_unit = 'minutes'
where min_price is null;

update "public"."court_price"
set min_price     = price,
    max_price     = price,
    duration_unit = 'minutes'
where min_price is null;

alter table "public"."coach_price"
    alter column "min_price" set not null,
    alter column "max_price" set not null,
    alter column "duration_unit" set not null;

alter table "public"."court_price"
    alter column "min_price" set not null,
    alter column "max_price" set not null,
    alter column "duration_unit" set not null;

alter table "public"."coach_price"
    drop column "price";
alter table "public"."court_price"
    drop column "price";

alter table "public"."coach_price"
    add constraint "coach_price_range_valid"
        check (min_price <= max_price);

alter table "public"."court_price"
    add constraint "court_price_range_valid"
        check (min_price <= max_price);