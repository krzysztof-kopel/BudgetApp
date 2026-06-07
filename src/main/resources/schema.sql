CREATE TABLE IF NOT EXISTS "Accounts" (
                                          "id" UUID NOT NULL,
                                          "name" VARCHAR(255) NOT NULL,
    "balance" NUMERIC(12, 2) NOT NULL DEFAULT 0,
    PRIMARY KEY("id")
    );




CREATE TABLE IF NOT EXISTS "Transactions" (
                                              "id" UUID NOT NULL,
                                              "account_id" UUID NOT NULL,
                                              "amount" NUMERIC(12, 2) NOT NULL CHECK(amount>0),
    "type_id" VARCHAR(255) NOT NULL,
    "category_id" UUID NOT NULL,
    "description" VARCHAR(255),
    "created_at" DATE NOT NULL,
    PRIMARY KEY("id")
    );




CREATE TABLE IF NOT EXISTS "TransactionTypes" (
                                                  "id" VARCHAR(255) NOT NULL,
    PRIMARY KEY("id")
    );




CREATE TABLE IF NOT EXISTS "Categories" (
                                            "id" UUID NOT NULL,
                                            "name" VARCHAR(255) NOT NULL,
    "budget_limit" NUMERIC(12, 2) CHECK(budget_limit>0),
    PRIMARY KEY("id")
    );



ALTER TABLE "Transactions"
    ADD FOREIGN KEY("account_id") REFERENCES "Accounts"("id")
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "Transactions"
    ADD FOREIGN KEY("type_id") REFERENCES "TransactionTypes"("id")
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "Transactions"
    ADD FOREIGN KEY("category_id") REFERENCES "Categories"("id")
        ON UPDATE NO ACTION ON DELETE NO ACTION;