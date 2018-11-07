CREATE TABLE TASK (
    ID uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    title text NOT NULL,
    details text NULL,
    due_date timestamp,
    complete bool NOT NULL,
    created_at timestamp NOT NULL,
    modified_at timestamp NOT NULL
);

CREATE TABLE app_country(
  id UUID NOT NULL,
  name VARCHAR(45) NOT NULL,
  code VARCHAR(45) NOT NULL,
  currency VARCHAR NOT NULL,
  dollar_rate INT NULL,
  description VARCHAR(45) NULL,
  UNIQUE(name),
  PRIMARY KEY (id));

CREATE TABLE app_language (
  id UUID NOT NULL,
  name VARCHAR(45) NULL,
  locale VARCHAR(45) NULL,
  country_id UUID NOT NULL,
  UNIQUE(name),
  PRIMARY KEY (id),
  CONSTRAINT country_id_language
    FOREIGN KEY (country_id)
    REFERENCES app_country (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE app_country_state (
  id UUID NOT NULL ,
  name VARCHAR(45) NULL,
  country_id UUID NOT NULL,
  UNIQUE(name),
  PRIMARY KEY (id),
  CONSTRAINT country_id_state
    FOREIGN KEY (country_id)
    REFERENCES app_country (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TYPE address_type_enum AS ENUM ('Permanent', 'Current');
CREATE TABLE address (
  id UUID NOT NULL,
  line1 VARCHAR(200) NOT NULL,
  line2 VARCHAR(200) NULL,
  landmark VARCHAR(200) NULL,
  city VARCHAR(200) NOT NULL,
  country_id UUID NOT NULL,
  state_id UUID NULL,
  living_period INT NULL,
  latitude FLOAT NULL,
  longitude FLOAT NULL,
  zip_code VARCHAR(45) NOT NULL,
  address_type address_type_enum,
  PRIMARY KEY (id),
  CONSTRAINT country_id_address
    FOREIGN KEY (country_id)
    REFERENCES app_country (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT state_id_address
    FOREIGN KEY (state_id)
    REFERENCES app_country_state (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);