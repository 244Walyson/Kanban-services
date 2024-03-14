INSERT INTO tb_board (title, total_cards) VALUES ('board1', 1);

INSERT INTO tb_card (title, description, card_position, board_id, done) VALUES ('card1', 'description', 0, 1, false);

INSERT INTO tb_user( name, email, username, password, bio) VALUES ('user test', 'test@gmail.com', 'test1232', '', 'biografia');

INSERT INTO tb_user_card (user_id, card_id ) VALUES ( 1, 1 );

INSERT INTO tb_team (name, occupation_area, description ) VALUES ('team rocket', 'back end', 'projeto x');

INSERT INTO tb_user_team (user_id, team_id, is_admin ) VALUES ( 1, 1, true );