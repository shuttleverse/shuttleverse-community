alter table "public"."coach_price" add column "submitted_by" uuid not null;

alter table "public"."court_price" add column "submitted_by" uuid not null;

alter table "public"."coach_price" add constraint "coach_price_coach_id_fkey" FOREIGN KEY (coach_id) REFERENCES coach(coach_id) ON UPDATE CASCADE ON DELETE CASCADE not valid;

alter table "public"."coach_price" validate constraint "coach_price_coach_id_fkey";

alter table "public"."coach_price" add constraint "coach_price_submitted_by_fkey" FOREIGN KEY (submitted_by) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE not valid;

alter table "public"."coach_price" validate constraint "coach_price_submitted_by_fkey";

alter table "public"."court_price" add constraint "court_price_submitted_by_fkey" FOREIGN KEY (submitted_by) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE not valid;

alter table "public"."court_price" validate constraint "court_price_submitted_by_fkey";


