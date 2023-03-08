-- auto-generated definition
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '用户密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 空用户 1 管理员',
    planetCode   varchar(512)                       null comment '星球编号'
)
    comment '用户';

create database user_center;

-- auto-generated definition
create table tag
(
    id           bigint auto_increment comment 'id' primary key,
    tagName     varchar(256)                       null comment '标签名称',
    userId      bigint                              null comment '用户Id',
    parentId    bigint                      null comment '父标签Id',
    isParent       tinyint                            null comment '0 不是父标签, 1 是父标签',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '标签';

alter table `user` add column tags varchar(1024) null comment '标签列表';