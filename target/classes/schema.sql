CREATE TABLE IF NOT EXISTS users(
id SERIAL PRIMARY KEY,
first_name VARCHAR(100),
last_name VARCHAR(100),
mobile_no VARCHAR(20),
email VARCHAR(150) UNIQUE,
password VARCHAR(200),
profession_type VARCHAR(50),
nationality VARCHAR(100),
email_verified BOOLEAN DEFAULT FALSE
);