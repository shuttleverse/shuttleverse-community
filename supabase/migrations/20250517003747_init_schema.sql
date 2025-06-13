create table "public"."club"
(
    "club_id"     uuid                        not null default gen_random_uuid(),
    "name"        character varying(100)      not null,
    "location"    character varying(255),
    "description" character varying(1000),
    "website"     character varying(255),
    "phone"       character varying(20),
    "created_at"  timestamp without time zone not null,
    "updated_at"  timestamp without time zone not null,
    "creator_id"  uuid                        not null,
    "owner_id"    uuid                        not null,
    "is_verified" boolean                     not null
);


create table "public"."coach"
(
    "coach_id"         uuid                        not null default gen_random_uuid(),
    "club_id"          uuid                        not null,
    "name"             character varying(100)      not null,
    "location"         character varying(255),
    "description"      character varying(1000),
    "experience_years" integer,
    "other_contacts"   character varying(100),
    "phone_number"     character varying(20),
    "created_at"       timestamp without time zone not null,
    "updated_at"       timestamp without time zone not null,
    "owner_id"         uuid,
    "is_verified"      boolean                     not null,
    "creator_id"       uuid                        not null
);

create table "public"."coach_schedule"
(
    "schedule_id"  uuid                        not null default gen_random_uuid(),
    "coach_id"     uuid                        not null,
    "day_of_week"  integer                     not null,
    "start_time"   character varying(10)       not null,
    "end_time"     character varying(10)       not null,
    "created_at"   timestamp without time zone not null,
    "updated_at"   timestamp without time zone not null,
    "upvotes"      integer                     not null,
    "is_verified"  boolean                     not null,
    "submitted_by" uuid                        not null
);

create table "public"."coach_price"
(
    "price_id"    uuid                        not null default gen_random_uuid(),
    "coach_id"    uuid                        not null,
    "price"       double precision            not null,
    "duration"    integer                     not null,
    "created_at"  timestamp without time zone not null,
    "updated_at"  timestamp without time zone not null,
    "upvotes"     integer                     not null,
    "is_verified" boolean                     not null
);


create table "public"."court"
(
    "court_id"     uuid                        not null default gen_random_uuid(),
    "name"         character varying(255),
    "location"     character varying(255),
    "description"  character varying(255),
    "website"      character varying(255),
    "phone_number" character varying(255),
    "created_at"   timestamp without time zone not null,
    "updated_at"   timestamp without time zone not null,
    "creator_id"   uuid                        not null,
    "owner_id"     uuid
);


create table "public"."court_price"
(
    "price_id"    uuid                        not null default gen_random_uuid(),
    "court_id"    uuid                        not null,
    "price"       double precision            not null,
    "duration"    integer                     not null,
    "created_at"  timestamp without time zone not null,
    "updated_at"  timestamp without time zone not null,
    "upvotes"     integer                     not null,
    "is_verified" boolean                     not null
);


create table "public"."court_schedule"
(
    "schedule_id"  uuid                        not null default gen_random_uuid(),
    "court_id"     uuid                        not null,
    "day_of_week"  integer                     not null,
    "open_time"    character varying(255)      not null,
    "close_time"   character varying(255)      not null,
    "is_verified"  boolean                     not null,
    "upvotes"      integer                     not null,
    "submitted_by" uuid                        not null,
    "created_at"   timestamp without time zone not null,
    "updated_at"   timestamp without time zone not null
);


create table "public"."ownership_claim"
(
    "claim_id"    uuid                        not null default gen_random_uuid(),
    "entity_type" character varying(50)       not null,
    "entity_id"   uuid                        not null,
    "proof"       character varying(2000),
    "admin_id"    uuid,
    "admin_notes" character varying(2000),
    "created_at"  timestamp without time zone not null,
    "updated_at"  timestamp without time zone not null
);


create table "public"."stringer"
(
    "stringer_id"        uuid                        not null default gen_random_uuid(),
    "club_id"            uuid,
    "name"               character varying(255),
    "description"        character varying(255),
    "other_contacts"     character varying(255),
    "phone_number"       character varying(255),
    "additional_details" character varying(255),
    "created_at"         timestamp without time zone not null,
    "updated_at"         timestamp without time zone not null,
    "creator_id"         uuid                        not null,
    "owner_id"           uuid
);


create table "public"."stringer_price"
(
    "price_id"     uuid                        not null default gen_random_uuid(),
    "stringer_id"  uuid                        not null,
    "string_name"  character varying(100)      not null,
    "price"        double precision            not null,
    "created_at"   timestamp without time zone not null,
    "updated_at"   timestamp without time zone not null,
    "upvotes"      integer                     not null,
    "is_verified"  boolean                     not null,
    "submitted_by" uuid                        not null
);


create table "public"."users"
(
    "user_id"    uuid                        not null,
    "username"   character varying(50)       not null,
    "email"      character varying(100)      not null,
    "bio"        character varying(255),
    "created_at" timestamp without time zone not null,
    "updated_at" timestamp without time zone not null,
    "is_admin"   boolean                     not null
);


CREATE UNIQUE INDEX pk_club ON public.club USING btree (club_id);

CREATE UNIQUE INDEX pk_coach ON public.coach USING btree (coach_id);

CREATE UNIQUE INDEX pk_coach_schedule ON public.coach_schedule USING btree (schedule_id);

CREATE UNIQUE INDEX pk_court ON public.court USING btree (court_id);

CREATE UNIQUE INDEX pk_court_price ON public.court_price USING btree (price_id);

CREATE UNIQUE INDEX pk_court_schedule ON public.court_schedule USING btree (schedule_id);

CREATE UNIQUE INDEX pk_ownership_claim ON public.ownership_claim USING btree (claim_id);

CREATE UNIQUE INDEX pk_stringer ON public.stringer USING btree (stringer_id);

CREATE UNIQUE INDEX pk_stringer_price ON public.stringer_price USING btree (price_id);

CREATE UNIQUE INDEX pk_users ON public.users USING btree (user_id);

CREATE UNIQUE INDEX uc_users_email ON public.users USING btree (email);

CREATE UNIQUE INDEX uc_users_username ON public.users USING btree (username);

alter table "public"."club"
    add constraint "pk_club" PRIMARY KEY using index "pk_club";

alter table "public"."coach"
    add constraint "pk_coach" PRIMARY KEY using index "pk_coach";

alter table "public"."coach_schedule"
    add constraint "pk_coach_schedule" PRIMARY KEY using index "pk_coach_schedule";

alter table "public"."court"
    add constraint "pk_court" PRIMARY KEY using index "pk_court";

alter table "public"."court_price"
    add constraint "pk_court_price" PRIMARY KEY using index "pk_court_price";

alter table "public"."court_schedule"
    add constraint "pk_court_schedule" PRIMARY KEY using index "pk_court_schedule";

alter table "public"."ownership_claim"
    add constraint "pk_ownership_claim" PRIMARY KEY using index "pk_ownership_claim";

alter table "public"."stringer"
    add constraint "pk_stringer" PRIMARY KEY using index "pk_stringer";

alter table "public"."stringer_price"
    add constraint "pk_stringer_price" PRIMARY KEY using index "pk_stringer_price";

alter table "public"."users"
    add constraint "pk_users" PRIMARY KEY using index "pk_users";

alter table "public"."coach"
    add constraint "fk_coach_on_club" FOREIGN KEY (club_id) REFERENCES club (club_id) not valid;

alter table "public"."coach"
    validate constraint "fk_coach_on_club";

alter table "public"."coach"
    add constraint "fk_coach_on_creator" FOREIGN KEY (creator_id) REFERENCES users (user_id) not valid;

alter table "public"."coach"
    validate constraint "fk_coach_on_creator";

alter table "public"."coach"
    add constraint "fk_coach_on_owner" FOREIGN KEY (owner_id) REFERENCES users (user_id) not valid;

alter table "public"."coach"
    validate constraint "fk_coach_on_owner";

alter table "public"."coach_schedule"
    add constraint "fk_coach_schedule_on_coach" FOREIGN KEY (coach_id) REFERENCES coach (coach_id) not valid;

alter table "public"."coach_schedule"
    validate constraint "fk_coach_schedule_on_coach";

alter table "public"."coach_schedule"
    add constraint "fk_coach_schedule_on_submitted_by" FOREIGN KEY (submitted_by) REFERENCES users (user_id) not valid;

alter table "public"."coach_schedule"
    validate constraint "fk_coach_schedule_on_submitted_by";

alter table "public"."court"
    add constraint "fk_court_on_creator" FOREIGN KEY (creator_id) REFERENCES users (user_id) not valid;

alter table "public"."court"
    validate constraint "fk_court_on_creator";

alter table "public"."court"
    add constraint "fk_court_on_owner" FOREIGN KEY (owner_id) REFERENCES users (user_id) not valid;

alter table "public"."court"
    validate constraint "fk_court_on_owner";

alter table "public"."court_price"
    add constraint "fk_court_price_on_court" FOREIGN KEY (court_id) REFERENCES court (court_id) not valid;

alter table "public"."court_price"
    validate constraint "fk_court_price_on_court";

alter table "public"."court_schedule"
    add constraint "fk_court_schedule_on_court" FOREIGN KEY (court_id) REFERENCES court (court_id) not valid;

alter table "public"."court_schedule"
    validate constraint "fk_court_schedule_on_court";

alter table "public"."court_schedule"
    add constraint "fk_court_schedule_on_submitted_by" FOREIGN KEY (submitted_by) REFERENCES users (user_id) not valid;

alter table "public"."court_schedule"
    validate constraint "fk_court_schedule_on_submitted_by";

alter table "public"."ownership_claim"
    add constraint "fk_ownership_claim_on_admin" FOREIGN KEY (admin_id) REFERENCES users (user_id) not valid;

alter table "public"."ownership_claim"
    validate constraint "fk_ownership_claim_on_admin";

alter table "public"."stringer"
    add constraint "fk_stringer_on_club" FOREIGN KEY (club_id) REFERENCES club (club_id) not valid;

alter table "public"."stringer"
    validate constraint "fk_stringer_on_club";

alter table "public"."stringer"
    add constraint "fk_stringer_on_creator" FOREIGN KEY (creator_id) REFERENCES users (user_id) not valid;

alter table "public"."stringer"
    validate constraint "fk_stringer_on_creator";

alter table "public"."stringer"
    add constraint "fk_stringer_on_owner" FOREIGN KEY (owner_id) REFERENCES users (user_id) not valid;

alter table "public"."stringer"
    validate constraint "fk_stringer_on_owner";

alter table "public"."stringer_price"
    add constraint "fk_stringer_price_on_stringer" FOREIGN KEY (stringer_id) REFERENCES stringer (stringer_id) not valid;

alter table "public"."stringer_price"
    validate constraint "fk_stringer_price_on_stringer";

alter table "public"."stringer_price"
    add constraint "fk_stringer_price_on_submitted_by" FOREIGN KEY (submitted_by) REFERENCES users (user_id) not valid;

alter table "public"."stringer_price"
    validate constraint "fk_stringer_price_on_submitted_by";

alter table "public"."users"
    add constraint "uc_users_email" UNIQUE using index "uc_users_email";

alter table "public"."users"
    add constraint "uc_users_username" UNIQUE using index "uc_users_username";

grant delete on table "public"."club" to "anon";

grant insert on table "public"."club" to "anon";

grant references on table "public"."club" to "anon";

grant select on table "public"."club" to "anon";

grant trigger on table "public"."club" to "anon";

grant truncate on table "public"."club" to "anon";

grant update on table "public"."club" to "anon";

grant delete on table "public"."club" to "authenticated";

grant insert on table "public"."club" to "authenticated";

grant references on table "public"."club" to "authenticated";

grant select on table "public"."club" to "authenticated";

grant trigger on table "public"."club" to "authenticated";

grant truncate on table "public"."club" to "authenticated";

grant update on table "public"."club" to "authenticated";

grant delete on table "public"."club" to "service_role";

grant insert on table "public"."club" to "service_role";

grant references on table "public"."club" to "service_role";

grant select on table "public"."club" to "service_role";

grant trigger on table "public"."club" to "service_role";

grant truncate on table "public"."club" to "service_role";

grant update on table "public"."club" to "service_role";

grant delete on table "public"."coach" to "anon";

grant insert on table "public"."coach" to "anon";

grant references on table "public"."coach" to "anon";

grant select on table "public"."coach" to "anon";

grant trigger on table "public"."coach" to "anon";

grant truncate on table "public"."coach" to "anon";

grant update on table "public"."coach" to "anon";

grant delete on table "public"."coach" to "authenticated";

grant insert on table "public"."coach" to "authenticated";

grant references on table "public"."coach" to "authenticated";

grant select on table "public"."coach" to "authenticated";

grant trigger on table "public"."coach" to "authenticated";

grant truncate on table "public"."coach" to "authenticated";

grant update on table "public"."coach" to "authenticated";

grant delete on table "public"."coach" to "service_role";

grant insert on table "public"."coach" to "service_role";

grant references on table "public"."coach" to "service_role";

grant select on table "public"."coach" to "service_role";

grant trigger on table "public"."coach" to "service_role";

grant truncate on table "public"."coach" to "service_role";

grant update on table "public"."coach" to "service_role";

grant delete on table "public"."coach_schedule" to "anon";

grant insert on table "public"."coach_schedule" to "anon";

grant references on table "public"."coach_schedule" to "anon";

grant select on table "public"."coach_schedule" to "anon";

grant trigger on table "public"."coach_schedule" to "anon";

grant truncate on table "public"."coach_schedule" to "anon";

grant update on table "public"."coach_schedule" to "anon";

grant delete on table "public"."coach_schedule" to "authenticated";

grant insert on table "public"."coach_schedule" to "authenticated";

grant references on table "public"."coach_schedule" to "authenticated";

grant select on table "public"."coach_schedule" to "authenticated";

grant trigger on table "public"."coach_schedule" to "authenticated";

grant truncate on table "public"."coach_schedule" to "authenticated";

grant update on table "public"."coach_schedule" to "authenticated";

grant delete on table "public"."coach_schedule" to "service_role";

grant insert on table "public"."coach_schedule" to "service_role";

grant references on table "public"."coach_schedule" to "service_role";

grant select on table "public"."coach_schedule" to "service_role";

grant trigger on table "public"."coach_schedule" to "service_role";

grant truncate on table "public"."coach_schedule" to "service_role";

grant update on table "public"."coach_schedule" to "service_role";

grant delete on table "public"."court" to "anon";

grant insert on table "public"."court" to "anon";

grant references on table "public"."court" to "anon";

grant select on table "public"."court" to "anon";

grant trigger on table "public"."court" to "anon";

grant truncate on table "public"."court" to "anon";

grant update on table "public"."court" to "anon";

grant delete on table "public"."court" to "authenticated";

grant insert on table "public"."court" to "authenticated";

grant references on table "public"."court" to "authenticated";

grant select on table "public"."court" to "authenticated";

grant trigger on table "public"."court" to "authenticated";

grant truncate on table "public"."court" to "authenticated";

grant update on table "public"."court" to "authenticated";

grant delete on table "public"."court" to "service_role";

grant insert on table "public"."court" to "service_role";

grant references on table "public"."court" to "service_role";

grant select on table "public"."court" to "service_role";

grant trigger on table "public"."court" to "service_role";

grant truncate on table "public"."court" to "service_role";

grant update on table "public"."court" to "service_role";

grant delete on table "public"."court_price" to "anon";

grant insert on table "public"."court_price" to "anon";

grant references on table "public"."court_price" to "anon";

grant select on table "public"."court_price" to "anon";

grant trigger on table "public"."court_price" to "anon";

grant truncate on table "public"."court_price" to "anon";

grant update on table "public"."court_price" to "anon";

grant delete on table "public"."court_price" to "authenticated";

grant insert on table "public"."court_price" to "authenticated";

grant references on table "public"."court_price" to "authenticated";

grant select on table "public"."court_price" to "authenticated";

grant trigger on table "public"."court_price" to "authenticated";

grant truncate on table "public"."court_price" to "authenticated";

grant update on table "public"."court_price" to "authenticated";

grant delete on table "public"."court_price" to "service_role";

grant insert on table "public"."court_price" to "service_role";

grant references on table "public"."court_price" to "service_role";

grant select on table "public"."court_price" to "service_role";

grant trigger on table "public"."court_price" to "service_role";

grant truncate on table "public"."court_price" to "service_role";

grant update on table "public"."court_price" to "service_role";

grant delete on table "public"."court_schedule" to "anon";

grant insert on table "public"."court_schedule" to "anon";

grant references on table "public"."court_schedule" to "anon";

grant select on table "public"."court_schedule" to "anon";

grant trigger on table "public"."court_schedule" to "anon";

grant truncate on table "public"."court_schedule" to "anon";

grant update on table "public"."court_schedule" to "anon";

grant delete on table "public"."court_schedule" to "authenticated";

grant insert on table "public"."court_schedule" to "authenticated";

grant references on table "public"."court_schedule" to "authenticated";

grant select on table "public"."court_schedule" to "authenticated";

grant trigger on table "public"."court_schedule" to "authenticated";

grant truncate on table "public"."court_schedule" to "authenticated";

grant update on table "public"."court_schedule" to "authenticated";

grant delete on table "public"."court_schedule" to "service_role";

grant insert on table "public"."court_schedule" to "service_role";

grant references on table "public"."court_schedule" to "service_role";

grant select on table "public"."court_schedule" to "service_role";

grant trigger on table "public"."court_schedule" to "service_role";

grant truncate on table "public"."court_schedule" to "service_role";

grant update on table "public"."court_schedule" to "service_role";

grant delete on table "public"."ownership_claim" to "anon";

grant insert on table "public"."ownership_claim" to "anon";

grant references on table "public"."ownership_claim" to "anon";

grant select on table "public"."ownership_claim" to "anon";

grant trigger on table "public"."ownership_claim" to "anon";

grant truncate on table "public"."ownership_claim" to "anon";

grant update on table "public"."ownership_claim" to "anon";

grant delete on table "public"."ownership_claim" to "authenticated";

grant insert on table "public"."ownership_claim" to "authenticated";

grant references on table "public"."ownership_claim" to "authenticated";

grant select on table "public"."ownership_claim" to "authenticated";

grant trigger on table "public"."ownership_claim" to "authenticated";

grant truncate on table "public"."ownership_claim" to "authenticated";

grant update on table "public"."ownership_claim" to "authenticated";

grant delete on table "public"."ownership_claim" to "service_role";

grant insert on table "public"."ownership_claim" to "service_role";

grant references on table "public"."ownership_claim" to "service_role";

grant select on table "public"."ownership_claim" to "service_role";

grant trigger on table "public"."ownership_claim" to "service_role";

grant truncate on table "public"."ownership_claim" to "service_role";

grant update on table "public"."ownership_claim" to "service_role";

grant delete on table "public"."stringer" to "anon";

grant insert on table "public"."stringer" to "anon";

grant references on table "public"."stringer" to "anon";

grant select on table "public"."stringer" to "anon";

grant trigger on table "public"."stringer" to "anon";

grant truncate on table "public"."stringer" to "anon";

grant update on table "public"."stringer" to "anon";

grant delete on table "public"."stringer" to "authenticated";

grant insert on table "public"."stringer" to "authenticated";

grant references on table "public"."stringer" to "authenticated";

grant select on table "public"."stringer" to "authenticated";

grant trigger on table "public"."stringer" to "authenticated";

grant truncate on table "public"."stringer" to "authenticated";

grant update on table "public"."stringer" to "authenticated";

grant delete on table "public"."stringer" to "service_role";

grant insert on table "public"."stringer" to "service_role";

grant references on table "public"."stringer" to "service_role";

grant select on table "public"."stringer" to "service_role";

grant trigger on table "public"."stringer" to "service_role";

grant truncate on table "public"."stringer" to "service_role";

grant update on table "public"."stringer" to "service_role";

grant delete on table "public"."stringer_price" to "anon";

grant insert on table "public"."stringer_price" to "anon";

grant references on table "public"."stringer_price" to "anon";

grant select on table "public"."stringer_price" to "anon";

grant trigger on table "public"."stringer_price" to "anon";

grant truncate on table "public"."stringer_price" to "anon";

grant update on table "public"."stringer_price" to "anon";

grant delete on table "public"."stringer_price" to "authenticated";

grant insert on table "public"."stringer_price" to "authenticated";

grant references on table "public"."stringer_price" to "authenticated";

grant select on table "public"."stringer_price" to "authenticated";

grant trigger on table "public"."stringer_price" to "authenticated";

grant truncate on table "public"."stringer_price" to "authenticated";

grant update on table "public"."stringer_price" to "authenticated";

grant delete on table "public"."stringer_price" to "service_role";

grant insert on table "public"."stringer_price" to "service_role";

grant references on table "public"."stringer_price" to "service_role";

grant select on table "public"."stringer_price" to "service_role";

grant trigger on table "public"."stringer_price" to "service_role";

grant truncate on table "public"."stringer_price" to "service_role";

grant update on table "public"."stringer_price" to "service_role";

grant delete on table "public"."users" to "anon";

grant insert on table "public"."users" to "anon";

grant references on table "public"."users" to "anon";

grant select on table "public"."users" to "anon";

grant trigger on table "public"."users" to "anon";

grant truncate on table "public"."users" to "anon";

grant update on table "public"."users" to "anon";

grant delete on table "public"."users" to "authenticated";

grant insert on table "public"."users" to "authenticated";

grant references on table "public"."users" to "authenticated";

grant select on table "public"."users" to "authenticated";

grant trigger on table "public"."users" to "authenticated";

grant truncate on table "public"."users" to "authenticated";

grant update on table "public"."users" to "authenticated";

grant delete on table "public"."users" to "service_role";

grant insert on table "public"."users" to "service_role";

grant references on table "public"."users" to "service_role";

grant select on table "public"."users" to "service_role";

grant trigger on table "public"."users" to "service_role";

grant truncate on table "public"."users" to "service_role";

grant update on table "public"."users" to "service_role";


