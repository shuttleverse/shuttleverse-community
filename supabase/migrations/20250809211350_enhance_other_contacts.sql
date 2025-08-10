alter table "public"."court"
    add column "contacts" jsonb;

alter table "public"."coach"
    add column "contacts" jsonb;

alter table "public"."stringer"
    add column "contacts" jsonb;

update "public"."court"
set contacts =
        case
            when other_contacts is not null and other_contacts != '' then
                jsonb_build_object('email', other_contacts)
            else '{}'::jsonb
            end;

update "public"."coach"
set contacts =
        case
            when other_contacts is not null and other_contacts != '' then
                jsonb_build_object('email', other_contacts)
            else '{}'::jsonb
            end;

update "public"."stringer"
set contacts =
        case
            when other_contacts is not null and other_contacts != '' then
                jsonb_build_object('email', other_contacts)
            else '{}'::jsonb
            end;

alter table "public"."coach"
    alter column "contacts" set not null;

alter table "public"."stringer"
    alter column "contacts" set not null;

alter table "public"."court"
    drop column "other_contacts";
alter table "public"."coach"
    drop column "other_contacts";
alter table "public"."stringer"
    drop column "other_contacts";
alter table "public"."court"
    rename column "contacts" to "other_contacts";
alter table "public"."coach"
    rename column "contacts" to "other_contacts";
alter table "public"."stringer"
    rename column "contacts" to "other_contacts";