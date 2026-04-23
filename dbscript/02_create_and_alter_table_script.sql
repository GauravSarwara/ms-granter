BEGIN;

-- =========================================
-- 2. CREATE PROFESSION MASTER TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS public.profession (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Insert default professions (avoid duplicates)
INSERT INTO public.profession (name)
VALUES 
    ('STUDENT'),
    ('EMPLOYED'),
    ('SELF_EMPLOYED')
ON CONFLICT (name) DO NOTHING;

-- =========================================
-- 3. CREATE APPLICATION_PROFESSION MAPPING
-- =========================================

CREATE TABLE IF NOT EXISTS public.application_profession (
    id SERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    profession_id INT NOT NULL,
    
    CONSTRAINT fk_application
        FOREIGN KEY (application_id)
        REFERENCES public.granter_application(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_profession
        FOREIGN KEY (profession_id)
        REFERENCES public.profession(id)
        ON DELETE CASCADE
);

-- =========================================
-- 4. STUDENT DETAILS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS public.student_details (
    id SERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,

    university text,
    course text,
    course_start_date text,
    course_end_date text,

    CONSTRAINT fk_student_application
        FOREIGN KEY (application_id)
        REFERENCES public.granter_application(id)
        ON DELETE CASCADE
);

-- =========================================
-- 5. EMPLOYED DETAILS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS public.employed_details (
    id SERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,

    employer_name text,
    employer_email text,
    monthly_salary text,
    date_of_joining text,
    contract_type text,

    CONSTRAINT fk_employed_application
        FOREIGN KEY (application_id)
        REFERENCES public.granter_application(id)
        ON DELETE CASCADE
);

-- =========================================
-- 6. SELF-EMPLOYED DETAILS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS public.self_employed_details (
    id SERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,

    trade_name text,
    trade_type text,
    turnover text,
    profit text,
    years_of_experience text,

    CONSTRAINT fk_self_employed_application
        FOREIGN KEY (application_id)
        REFERENCES public.granter_application(id)
        ON DELETE CASCADE
);

-- =========================================
-- 7. OPTIONAL INDEXES (FOR PERFORMANCE)
-- =========================================

CREATE INDEX IF NOT EXISTS idx_app_prof_app_id 
    ON public.application_profession(application_id);

CREATE INDEX IF NOT EXISTS idx_student_app_id 
    ON public.student_details(application_id);

CREATE INDEX IF NOT EXISTS idx_employed_app_id 
    ON public.employed_details(application_id);

CREATE INDEX IF NOT EXISTS idx_self_employed_app_id 
    ON public.self_employed_details(application_id);

COMMIT;