create database IF NOT EXISTS tep;

use tep;

CREATE TABLE IF NOT EXISTS `tep`.`Topic` (
  `id` varchar(36) NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `title_UNIQUE` (`title` ASC) VISIBLE);


CREATE TABLE IF NOT EXISTS `tep`.`Slice` (
    `id` varchar(36) NOT NULL ,
    `note` VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `tep`.`Deck` (
  `id` varchar(36) NOT NULL ,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(1000) NOT NULL,
  `topicId` varchar(36) not null,
  foreign key (topicId) references Topic(id),
  PRIMARY KEY (`id`,`topicId`),
  UNIQUE INDEX `title_UNIQUE` (`title` ASC) VISIBLE);
  
  
CREATE TABLE IF NOT EXISTS `tep`.`Hashtag` (
    `name` VARCHAR(200) NOT NULL,
    PRIMARY KEY (`name`)
);

CREATE TABLE IF NOT EXISTS `tep`.`DeckSliceMap` (
    deckId varchar(36) NOT NULL,
    sliceId varchar(36) NOT NULL,
    sort INT NOT NULL,
    FOREIGN KEY (deckId)
        REFERENCES Deck (id),
    FOREIGN KEY (sliceId)
        REFERENCES Slice (id),
    PRIMARY KEY (deckId , sliceId)
);
 
CREATE TABLE IF NOT EXISTS `tep`.`SliceHashtagMap` (
    sliceId varchar(36) NOT NULL,
    hashtagName VARCHAR(200) NOT NULL,
    FOREIGN KEY (sliceId)
        REFERENCES Slice (id),
    FOREIGN KEY (hashtagName)
        REFERENCES Hashtag (name),
    PRIMARY KEY (sliceId , hashtagName)
);
 
 
CREATE TABLE IF NOT EXISTS `tep`.`TopicHashtagMap` (
    topicId varchar(36) NOT NULL,
    hashtagName VARCHAR(200) NOT NULL,
    FOREIGN KEY (topicId)
        REFERENCES Topic (id),
    FOREIGN KEY (hashtagName)
        REFERENCES Hashtag (name),
    PRIMARY KEY (topicID , hashtagName)
);
 
CREATE TABLE IF NOT EXISTS `tep`.`SliceTopicMap` (
    sliceId varchar(36) NOT NULL,
    topicId varchar(36) NOT NULL,
    FOREIGN KEY (sliceId)
        REFERENCES Slice (id),
    FOREIGN KEY (topicId)
        REFERENCES Topic (id),
    PRIMARY KEY (sliceId , topicId)
);

