create table tb_board (total_cards integer, id bigint not null auto_increment, team_id bigint, title varchar(255), primary key (id)) engine=InnoDB;
create table tb_card (card_position integer, done bit not null, board_id bigint, id bigint not null auto_increment, description TEXT, title varchar(255), primary key (id)) engine=InnoDB;
create table tb_role (id bigint not null auto_increment, authority varchar(255), primary key (id)) engine=InnoDB;
create table tb_team (total_boards integer, total_collaborators integer, id bigint not null auto_increment, description varchar(255), img_url varchar(255), name varchar(255), occupation_area varchar(255), primary key (id)) engine=InnoDB;
create table tb_user (id bigint not null auto_increment, bio TEXT, email varchar(255), img_url varchar(255), name varchar(255), nickname varchar(255), password varchar(255), primary key (id)) engine=InnoDB;
create table tb_user_card (card_id bigint not null, user_id bigint not null, primary key (card_id, user_id)) engine=InnoDB;
create table tb_user_role (role_id bigint not null, user_id bigint not null, primary key (role_id, user_id)) engine=InnoDB;
create table tb_user_team (is_admin bit not null, team_id bigint not null, user_id bigint not null, primary key (team_id, user_id)) engine=InnoDB;
alter table tb_user add constraint UK_4vih17mube9j7cqyjlfbcrk4m unique (email);
alter table tb_user add constraint UK_ig0bbysxr6nnpxo4qn2btdcc8 unique (nickname);
alter table tb_board add constraint FK60rh3hn0qa10jnh8vqxugevt foreign key (team_id) references tb_team (id);
alter table tb_card add constraint FKbr17kpffrkple740f8gbb7f01 foreign key (board_id) references tb_board (id);
alter table tb_user_card add constraint FKh94qk144ntaxugc73vme836wi foreign key (card_id) references tb_user (id);
alter table tb_user_card add constraint FK2mla7r89ym5cqeb0lgr6uiknf foreign key (user_id) references tb_card (id);
alter table tb_user_role add constraint FKd7bi3kepoo7ca1oj0jn4dr10a foreign key (role_id) references tb_user (id);
alter table tb_user_role add constraint FK6wbd7emdx87eyul35mkr5unpq foreign key (user_id) references tb_role (id);
alter table tb_user_team add constraint FKlq985nyir3httyf1dr37awois foreign key (user_id) references tb_user (id);
alter table tb_user_team add constraint FKflrwkx9ntub81qj76freu6v5 foreign key (team_id) references tb_team (id);
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe terrorista', 'conserta bug', 'projeto x', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe sinistra', 'faz bug', 'projeto x', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe sei la oque', 'analista de dados', 'projeto y', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe não sei oque la', 'front-end', 'projeto x', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('so equipe mesmo', 'back-end', 'projeto y', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_board (title, total_cards, team_id) VALUES ('board1', 1, 1);
INSERT INTO tb_card (title, description, card_position, board_id, done) VALUES ('card1', 'description', 0, 1, false);
INSERT INTO tb_user( name, email, nickname, password, bio, img_url) VALUES ('user test', 'test@gmail.com', 'test1232', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', 'biografia', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Maria Brown', 'maria@gmail.com', 'maria2543', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Alex Green', 'alex@gmail.com', 'alex244', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '1987-12-13', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_role (authority) VALUES ('ROLE_SYS_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_MEMBER');
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_card (user_id, card_id ) VALUES ( 1, 1 );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 3, true );

    create table tb_board (
        total_cards integer,
        id bigint not null auto_increment,
        team_id bigint,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_card (
        card_position integer,
        done bit not null,
        board_id bigint,
        id bigint not null auto_increment,
        description TEXT,
        title varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_role (
        id bigint not null auto_increment,
        authority varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_team (
        total_boards integer,
        total_collaborators integer,
        id bigint not null auto_increment,
        description TEXT,
        img_url varchar(255),
        name varchar(255),
        occupation_area varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_team_outbox (
        total_collaborators integer,
        id bigint not null,
        description TEXT,
        img_url varchar(255),
        name varchar(255),
        occupation_area varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_user (
        id bigint not null auto_increment,
        bio TEXT,
        email varchar(255),
        img_url varchar(255),
        name varchar(255),
        nickname varchar(255),
        password varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_user_card (
        card_id bigint not null,
        user_id bigint not null,
        primary key (card_id, user_id)
    ) engine=InnoDB;

    create table tb_user_outbox (
        id bigint not null,
        team_id bigint,
        email varchar(255),
        img_url varchar(255),
        name varchar(255),
        nickname varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_user_role (
        role_id bigint not null,
        user_id bigint not null,
        primary key (role_id, user_id)
    ) engine=InnoDB;

    create table tb_user_team (
        is_admin bit not null,
        team_id bigint not null,
        user_id bigint not null,
        primary key (team_id, user_id)
    ) engine=InnoDB;

    alter table tb_user 
       add constraint UK_4vih17mube9j7cqyjlfbcrk4m unique (email);

    alter table tb_user 
       add constraint UK_ig0bbysxr6nnpxo4qn2btdcc8 unique (nickname);

    alter table tb_user_outbox 
       add constraint UK_f7wf0if2c20nh0cfpgblxespj unique (email);

    alter table tb_user_outbox 
       add constraint UK_e3rg3983tmgnmqyrv913x6hfm unique (nickname);

    alter table tb_board 
       add constraint FK60rh3hn0qa10jnh8vqxugevt 
       foreign key (team_id) 
       references tb_team (id);

    alter table tb_card 
       add constraint FKbr17kpffrkple740f8gbb7f01 
       foreign key (board_id) 
       references tb_board (id);

    alter table tb_user_card 
       add constraint FKh94qk144ntaxugc73vme836wi 
       foreign key (card_id) 
       references tb_user (id);

    alter table tb_user_card 
       add constraint FK2mla7r89ym5cqeb0lgr6uiknf 
       foreign key (user_id) 
       references tb_card (id);

    alter table tb_user_role 
       add constraint FKd7bi3kepoo7ca1oj0jn4dr10a 
       foreign key (role_id) 
       references tb_user (id);

    alter table tb_user_role 
       add constraint FK6wbd7emdx87eyul35mkr5unpq 
       foreign key (user_id) 
       references tb_role (id);

    alter table tb_user_team 
       add constraint FKlq985nyir3httyf1dr37awois 
       foreign key (user_id) 
       references tb_user (id);

    alter table tb_user_team 
       add constraint FKflrwkx9ntub81qj76freu6v5 
       foreign key (team_id) 
       references tb_team (id);
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe terrorista', 'conserta bug', 'projeto x', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe sinistra', 'faz bug', 'projeto x', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe sei la oque', 'analista de dados', 'projeto y', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('equipe não sei oque la', 'front-end', 'projeto x', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_team (name, occupation_area, description, img_url) VALUES ('so equipe mesmo', 'back-end', 'projeto y', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_board (title, total_cards, team_id) VALUES ('board1', 1, 1);
INSERT INTO tb_card (title, description, card_position, board_id, done) VALUES ('card1', 'description', 0, 1, false);
INSERT INTO tb_user( name, email, nickname, password, bio, img_url) VALUES ('user test', 'test@gmail.com', 'test1232', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', 'biografia', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Maria Brown', 'maria@gmail.com', 'maria2543', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Joao Grey', 'joao@gmail.com', 'joao123', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_user (name, email, nickname, password, bio, img_url) VALUES ('Alex Grenn', 'alex@gmail.com', 'alex321', '$2a$10$4vet6vWuI78kQf9HcbTMYeuN1eM6yKeBwcpa7dsYdc0ARQypKbVhm', '2001-07-25', 'https://www.cambe.pr.leg.br/anonimo.jpg/image_view_fullscreen');
INSERT INTO tb_role (authority) VALUES ('ROLE_SYS_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_MEMBER');
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_card (user_id, card_id ) VALUES ( 1, 1 );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 1, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 2, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 1, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 2, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 3, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 4, 3, true );
INSERT INTO tb_user_team (team_id, user_id, is_admin ) VALUES ( 5, 3, true );
