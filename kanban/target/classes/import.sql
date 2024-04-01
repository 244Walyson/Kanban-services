INSERT INTO tb_team (name, occupation_area, description ) VALUES ('team rocket', 'back end', 'projeto x');

INSERT INTO tb_board (title, total_cards, team_id) VALUES ('board1', 1, 1);

INSERT INTO tb_card (title, description, card_position, board_id, done) VALUES ('card1', 'description', 0, 1, false);

INSERT INTO tb_user( name, email, nickname, password, bio) VALUES ('user test', 'test@gmail.com', 'test1232', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', 'biografia');
INSERT INTO tb_user (name, email, nickname, password, bio) VALUES ('Maria Brown', 'maria@gmail.com', 'maria2543', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25');
INSERT INTO tb_user (name, email, nickname, password, bio) VALUES ('Alex Green', 'alex@gmail.com', 'alex244', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '1987-12-13');

INSERT INTO tb_role (authority) VALUES ('ROLE_SYS_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_MEMBER');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);



INSERT INTO tb_user_card (user_id, card_id ) VALUES ( 1, 1 );

INSERT INTO tb_user_team (user_id, team_id, is_admin ) VALUES ( 1, 1, true );