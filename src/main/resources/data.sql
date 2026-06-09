insert into "TransactionTypes" ("id") values ('INCOME') on conflict do nothing;
insert into "TransactionTypes" ("id") values ('EXPENSE') on conflict do nothing;
insert into "Categories" ("id", "name", "budget_limit") values ('5123686d-0e89-4d1b-b162-ece1dc742413', 'Groceries', '2000') on conflict do nothing;
insert into "Categories" ("id", "name", "budget_limit") values ('98763ccb-bd76-404a-babb-bcf0f57c9254', 'Car', '5000') on conflict do nothing;
insert into "Categories" ("id", "name", "budget_limit") values ('1cee9612-6911-45d4-a1b5-1c651d61cbdb', 'Home', '3000') on conflict do nothing;