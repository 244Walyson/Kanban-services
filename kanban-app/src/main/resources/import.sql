-- INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe terrorista', 'conserta bug', 'projeto x', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
-- INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe sinistra', 'faz bug', 'projeto x', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
-- INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe sei la oque', 'analista de dados', 'projeto y', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
-- INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe não sei oque la', 'front-end', 'projeto x', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
-- INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('so equipe mesmo', 'back-end', 'projeto y', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');



-- INSERT INTO tb_board (title, total_cards, team_id) VALUES ('board1', 1, 1);

-- INSERT INTO tb_card (title, description, card_position, board_id, done) VALUES ('card1', 'description', 0, 1, false);

-- INSERT INTO tb_user( name, email, nickname, password, bio, img_url) VALUES ('user test', 'test@gmail.com', 'test1232', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', 'biografia', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
-- INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Maria Brown', 'maria@gmail.com', 'maria2543', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
-- INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Joao Grey', 'joao@gmail.com', 'joao123', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');
-- INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Alex Grenn', 'alex@gmail.com', 'alex321', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://chat-kanban.s3.amazonaws.com/1715623715.jpeg');

-- INSERT INTO tb_role (authority) VALUES ('ROLE_SYS_ADMIN');
-- INSERT INTO tb_role (authority) VALUES ('ROLE_MEMBER');

-- INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
-- INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
-- INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);



-- INSERT INTO tb_user_card (user_id, card_id ) VALUES ( 1, 1 );

-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 1, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 1, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 1, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 1, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 1, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 2, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 2, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 2, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 2, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 2, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 3, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 3, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 3, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 3, true );
-- INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 3, true );