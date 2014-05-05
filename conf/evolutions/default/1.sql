# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `rides` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`client_id` BINARY(16) NOT NULL,`driver_id` BINARY(16) NOT NULL,`start_time` BIGINT NOT NULL,`lat` FLOAT NOT NULL,`lon` FLOAT NOT NULL,`fare` DOUBLE NOT NULL,`distance` DOUBLE NOT NULL,`rating` INTEGER NOT NULL);

# --- !Downs

drop table `rides`;

