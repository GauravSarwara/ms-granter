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



CREATE TABLE konfir_api_transaction (

    id BIGSERIAL PRIMARY KEY,
	user_id INTEGER not null ,
    candidate_id  text default null,
    employment_activity_id  text default null,

    education_activity_id text default null,

    self_employment_activity_id  text default null,

    request_json text default null,

    response_json text default null,

    status VARCHAR(50) default null,

    created_by VARCHAR(100) default null,

    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    modified_by VARCHAR(100) default null,

    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);