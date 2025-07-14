-- -- Inserisce un utente base nella tabella 'users'.
-- -- Gli assegniamo un ID fisso (es. 1) per poterlo referenziare facilmente.
INSERT INTO users (id, name, surname, email) VALUES (1, 'admin', 'admin', 'admin@siwbooks.com');
--
-- -- Inserisce le credenziali per l'utente appena creato.
-- -- Collega queste credenziali all'utente con id=1.
-- -- Imposta il ruolo a 'ADMIN'.
-- -- La password è 'password', ma memorizzata come hash BCrypt.
-- INSERT INTO credentials (id, username, password, user_role, user_id) VALUES (1, 'admin', '$2a$10$S.8qY1Y2sF6s/2y4zBvS..LhJg8fT0S9LIy5m4J2aD9a/y3b.Xm/a', 'ADMIN', 1);
INSERT INTO credentials (id, username, password, user_role, user_id) VALUES (1, 'admin', '$2a$10$aUpn8l.O6hWJcVPLtd5hDOiffp9MfieMqrXpi1Mu2sx/r2n5a4KRa', 'ADMIN', 1);
--
-- -- Puoi aggiungere altri dati se vuoi, per esempio un utente normale per i test
-- RICORDA NEW USER = fede/pupolo

ALTER SEQUENCE users_seq RESTART WITH 100;
ALTER SEQUENCE credentials_seq RESTART WITH 100;
ALTER SEQUENCE author_seq RESTART WITH 100;
ALTER SEQUENCE book_seq RESTART WITH 100;
ALTER SEQUENCE review_seq RESTART WITH 100;