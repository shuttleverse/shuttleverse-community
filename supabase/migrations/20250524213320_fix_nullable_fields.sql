alter table "public"."coach" alter column "club_id" drop not null;

alter table "public"."coach" alter column "other_contacts" set not null;

alter table "public"."court" add column "other_contacts" text;

alter table "public"."court" alter column "location" set not null;

alter table "public"."court" alter column "name" set not null;

alter table "public"."stringer" add column "location" text;

alter table "public"."stringer" alter column "name" set not null;

alter table "public"."stringer" alter column "other_contacts" set not null;


