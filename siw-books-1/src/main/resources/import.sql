-- -- Inserts a user into the table 'users'.
-- -- We assign a fixed ID (ex. 1) to refer to it easily
INSERT INTO users (id, name, surname, email) VALUES (1, 'admin', 'admin', 'admin@siwbooks.com');

-- -- Inserting the credentials for the new user.
-- -- Link these credentials to the user with id = 1.
-- -- Set its role to 'ADMIN'.
-- -- The password has been crypted by the passwordEncoder as hash BCrypt. (password)
INSERT INTO credentials (id, username, password, user_role, user_id) VALUES (1, 'admin', '$2a$10$aUpn8l.O6hWJcVPLtd5hDOiffp9MfieMqrXpi1Mu2sx/r2n5a4KRa', 'ADMIN', 1);

-- REMEMBER NEW USER = fede/pupolo

ALTER SEQUENCE users_seq RESTART WITH 100;
ALTER SEQUENCE credentials_seq RESTART WITH 100;
ALTER SEQUENCE author_seq RESTART WITH 100;
ALTER SEQUENCE book_seq RESTART WITH 100;
ALTER SEQUENCE review_seq RESTART WITH 100;