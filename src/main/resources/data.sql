insert into "TransactionTypes" ("id") values ('INCOME') on conflict do nothing;
insert into "TransactionTypes" ("id") values ('EXPENSE') on conflict do nothing;