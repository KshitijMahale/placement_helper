CREATE TABLE IF NOT EXISTS internship_submissions (
    id SERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    course TEXT NOT NULL,
    company TEXT NOT NULL,
    other_company TEXT,
    job_profile TEXT NOT NULL,
    other_job_profile TEXT,
    offer_type TEXT NOT NULL,
    internship_stipend INT,
    ctc INT,
    location TEXT NOT NULL,
    process_date DATE NOT NULL,
    linkedin TEXT,
    comments TEXT,
    rounds VARCHAR(1000),
    submission_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_details (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    degree VARCHAR(255),
    academic_year VARCHAR(255),
    department VARCHAR(255),
    passout_year VARCHAR(255),
    first_name VARCHAR(255)
); 