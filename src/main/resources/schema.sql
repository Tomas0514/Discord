CREATE TABLE users (
  username NVARCHAR(50) NOT NULL,
  password NVARCHAR(100) NOT NULL,
  enabled BIT NOT NULL DEFAULT 1,
  PRIMARY KEY (username)
);

CREATE TABLE authorities (
  username NVARCHAR(50) NOT NULL,
  authority NVARCHAR(50) NOT NULL,
  FOREIGN KEY (username) REFERENCES users(username)
);

CREATE UNIQUE INDEX ix_auth_username
  ON authorities (username, authority);
