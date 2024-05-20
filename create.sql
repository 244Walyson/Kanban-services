
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

    create table tb_user_connection (
        status bit not null,
        user_id1 bigint not null,
        user_id2 bigint not null,
        primary key (user_id1, user_id2)
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

    alter table tb_user_connection 
       add constraint FKifrybjed8m4450vu0109wnbs1 
       foreign key (user_id2) 
       references tb_user (id);

    alter table tb_user_connection 
       add constraint FKamnhss5gxhee3q0af3m8f4gip 
       foreign key (user_id1) 
       references tb_user (id);

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


    create table tb_notification (
        accepted bit,
        status tinyint check (status between 0 and 1),
        created_at datetime(6),
        id bigint not null auto_increment,
        receiver_id bigint,
        sender_id bigint,
        message varchar(255),
        title varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table tb_user (
        id bigint not null,
        email varchar(255),
        img_url varchar(255),
        name varchar(255),
        nickname varchar(255),
        token varchar(255),
        primary key (id)
    ) engine=InnoDB;

    alter table tb_notification 
       add constraint FKsyyivii8sc13nbwihfo7ejvfh 
       foreign key (receiver_id) 
       references tb_user (id);

    alter table tb_notification 
       add constraint FKb0ppwa6ysniupj0y3oquhrh4f 
       foreign key (sender_id) 
       references tb_user (id);
