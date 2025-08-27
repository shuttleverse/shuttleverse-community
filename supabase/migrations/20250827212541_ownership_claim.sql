alter table "public"."ownership_claim"
    drop constraint "fk_ownership_claim_on_admin";

alter table "public"."ownership_claim"
    drop constraint "pk_ownership_claim";

drop index if exists "public"."pk_ownership_claim";

create table "public"."verification_file"
(
    "id"         uuid                        not null default gen_random_uuid(),
    "claim_id"   uuid                        not null,
    "file_name"  text                        not null,
    "file_url"   text                        not null,
    "created_at" timestamp with time zone    not null default now(),
    "updated_at" timestamp without time zone not null default now()
);


alter table "public"."verification_file"
    enable row level security;

alter table "public"."coach"
    drop column "is_verified";

alter table "public"."court"
    alter column "other_contacts" set not null;

alter table "public"."ownership_claim"
    drop column "admin_id";

alter table "public"."ownership_claim"
    drop column "admin_notes";

alter table "public"."ownership_claim"
    drop column "claim_id";

alter table "public"."ownership_claim"
    drop column "proof";

alter table "public"."ownership_claim"
    add column "creator_id" uuid not null;

alter table "public"."ownership_claim"
    add column "id" uuid not null default gen_random_uuid();

alter table "public"."ownership_claim"
    add column "status" text not null default '0'::text;

alter table "public"."ownership_claim"
    add column "user_notes" text;

alter table "public"."ownership_claim"
    alter column "created_at" set default now();

alter table "public"."ownership_claim"
    alter column "created_at" set data type timestamp with time zone using "created_at"::timestamp with time zone;

alter table "public"."ownership_claim"
    alter column "entity_type" set data type text using "entity_type"::text;

alter table "public"."ownership_claim"
    alter column "updated_at" set default now();

CREATE UNIQUE INDEX ownership_claim_pkey ON public.ownership_claim USING btree (id);

CREATE UNIQUE INDEX verification_file_pkey ON public.verification_file USING btree (id);

alter table "public"."ownership_claim"
    add constraint "ownership_claim_pkey" PRIMARY KEY using index "ownership_claim_pkey";

alter table "public"."verification_file"
    add constraint "verification_file_pkey" PRIMARY KEY using index "verification_file_pkey";

alter table "public"."ownership_claim"
    add constraint "ownership_claim_creator_id_fkey" FOREIGN KEY (creator_id) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE not valid;

alter table "public"."ownership_claim"
    validate constraint "ownership_claim_creator_id_fkey";

alter table "public"."verification_file"
    add constraint "verification_file_claim_id_fkey" FOREIGN KEY (claim_id) REFERENCES ownership_claim (id) ON UPDATE CASCADE ON DELETE CASCADE not valid;

alter table "public"."verification_file"
    validate constraint "verification_file_claim_id_fkey";

grant delete on table "public"."verification_file" to "anon";

grant insert on table "public"."verification_file" to "anon";

grant references on table "public"."verification_file" to "anon";

grant select on table "public"."verification_file" to "anon";

grant trigger on table "public"."verification_file" to "anon";

grant truncate on table "public"."verification_file" to "anon";

grant update on table "public"."verification_file" to "anon";

grant delete on table "public"."verification_file" to "authenticated";

grant insert on table "public"."verification_file" to "authenticated";

grant references on table "public"."verification_file" to "authenticated";

grant select on table "public"."verification_file" to "authenticated";

grant trigger on table "public"."verification_file" to "authenticated";

grant truncate on table "public"."verification_file" to "authenticated";

grant update on table "public"."verification_file" to "authenticated";

grant delete on table "public"."verification_file" to "service_role";

grant insert on table "public"."verification_file" to "service_role";

grant references on table "public"."verification_file" to "service_role";

grant select on table "public"."verification_file" to "service_role";

grant trigger on table "public"."verification_file" to "service_role";

grant truncate on table "public"."verification_file" to "service_role";

grant update on table "public"."verification_file" to "service_role";


