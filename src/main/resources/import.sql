INSERT INTO tb_user( name, email, nickname, password, bio, img_url) VALUES ('user test', 'test@gmail.com', 'test1232', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', 'biografia', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Maria Brown', 'maria@gmail.com', 'maria2543', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Joao Grey', 'joao@gmail.com', 'joao123', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Alex Grenn', 'alex@gmail.com', 'alex321', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
INSERT INTO tb_role (authority) VALUES ('ROLE_SYS_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_MEMBER');
--
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO tb_user_connection (status, user_id1, user_id2) VALUES (false , 1, 2);
INSERT INTO tb_user_connection (status, user_id1, user_id2) VALUES (true , 2, 3);