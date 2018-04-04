CREATE DATABASE IF NOT EXISTS mamute_development DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

CREATE FULLTEXT INDEX title_text ON QuestionInformation (title,description)
