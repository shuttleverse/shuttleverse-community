create table "public"."upvote"
(
    "upvote_id"   uuid                     not null default gen_random_uuid(),
    "entity_type" text                     not null,
    "user_id"     uuid                     not null,
    "entity_id"   uuid                     not null,
    "created_at"  timestamp with time zone not null default now(),
    "info_type"   text                     not null
);


CREATE UNIQUE INDEX upvote_pkey ON public.upvote USING btree (upvote_id);

alter table "public"."upvote"
    add constraint "upvote_pkey" PRIMARY KEY using index "upvote_pkey";

alter table "public"."upvote"
    add constraint "upvote_user_id_fkey" FOREIGN KEY (user_id) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE not valid;

alter table "public"."upvote"
    validate constraint "upvote_user_id_fkey";

grant delete on table "public"."upvote" to "anon";

grant insert on table "public"."upvote" to "anon";

grant references on table "public"."upvote" to "anon";

grant select on table "public"."upvote" to "anon";

grant trigger on table "public"."upvote" to "anon";

grant truncate on table "public"."upvote" to "anon";

grant update on table "public"."upvote" to "anon";

grant delete on table "public"."upvote" to "authenticated";

grant insert on table "public"."upvote" to "authenticated";

grant references on table "public"."upvote" to "authenticated";

grant select on table "public"."upvote" to "authenticated";

grant trigger on table "public"."upvote" to "authenticated";

grant truncate on table "public"."upvote" to "authenticated";

grant update on table "public"."upvote" to "authenticated";

grant delete on table "public"."upvote" to "service_role";

grant insert on table "public"."upvote" to "service_role";

grant references on table "public"."upvote" to "service_role";

grant select on table "public"."upvote" to "service_role";

grant trigger on table "public"."upvote" to "service_role";

grant truncate on table "public"."upvote" to "service_role";

grant update on table "public"."upvote" to "service_role";


