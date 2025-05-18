CREATE TABLE IF NOT EXISTS internship_experiences (
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
    rounds JSONB,
    submission_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 