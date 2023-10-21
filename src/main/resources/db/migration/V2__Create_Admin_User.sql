INSERT INTO custom_user
(id, "version", address, email, first_name, gender, last_name, "password")
VALUES(1, 1, 'admin_name, admin_lastname, 123456, adres alani', 'admin@gmail.com', 'admin_name', 'MALE', 'admin_lastname', '{bcrypt}$2a$10$pFNL6pYgIupGrbIumR4OLelumKUP2sKmSOOHLGaZzPD/zFXsJogfm');


INSERT INTO "role"
(id, "version", "name")
VALUES(1, 0, 'ROLE_ADMIN');

INSERT INTO "role"
(id, "version", "name")
VALUES(2, 0, 'ROLE_ADMIN');

INSERT INTO user_role
(role_id, user_id)
VALUES(1, 1);

INSERT INTO user_role
(role_id, user_id)
VALUES(2, 1);
