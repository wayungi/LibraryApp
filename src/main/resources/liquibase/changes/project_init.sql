--changeset project:create-yearly-number-sequence-table

CREATE TABLE yearly_number_sequence (
                                        registration_year INTEGER NOT NULL,
                                        sequence_type VARCHAR(50) NOT NULL,
                                        last_number BIGINT NOT NULL DEFAULT 0,

                                        CONSTRAINT pk_yearly_number_sequence
                                            PRIMARY KEY (registration_year, sequence_type)
);

--changeset project:create-next-yearly-sequence-function splitStatements:false

CREATE OR REPLACE FUNCTION get_next_yearly_sequence(
    p_year INTEGER,
    p_sequence_type VARCHAR
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
next_number BIGINT;
BEGIN

INSERT INTO yearly_number_sequence (
    registration_year,
    sequence_type,
    last_number
)
VALUES (
           p_year,
           p_sequence_type,
           1
       )
    ON CONFLICT (registration_year, sequence_type)
    DO UPDATE
               SET last_number =
               yearly_number_sequence.last_number + 1
               RETURNING last_number INTO next_number;

RETURN next_number;

END;
$$;

--changeset project:create-admission-number-function splitStatements:false

CREATE OR REPLACE FUNCTION generate_admission_number()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
current_year INTEGER;
    sequence_number BIGINT;
BEGIN

    IF NEW.admission_number IS NULL OR NEW.admission_number = '' THEN

        current_year := EXTRACT(YEAR FROM CURRENT_DATE);

        sequence_number := get_next_yearly_sequence(
            current_year,
            'STUDENT'
        );

        NEW.admission_number :=
            current_year::TEXT ||
            LPAD(
                sequence_number::TEXT,
                5,
                '0'
            );

END IF;

RETURN NEW;
END;
$$;

--changeset project:create-admission-number-trigger

CREATE TRIGGER trg_generate_admission_number
    BEFORE INSERT ON students
    FOR EACH ROW
    EXECUTE FUNCTION generate_admission_number();

--changeset project:create-employee-number-function splitStatements:false

CREATE OR REPLACE FUNCTION generate_employee_number()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
current_year INTEGER;
    sequence_number BIGINT;
BEGIN

    IF NEW.employee_number IS NULL OR NEW.employee_number = '' THEN

        current_year := EXTRACT(YEAR FROM CURRENT_DATE);

        sequence_number := get_next_yearly_sequence(
            current_year,
            'EMPLOYEE'
        );

        NEW.employee_number :=
            'EMP' ||
            current_year::TEXT ||
            LPAD(
                sequence_number::TEXT,
                5,
                '0'
            );

END IF;

RETURN NEW;
END;
$$;
--changeset project:create-employee-number-trigger

CREATE TRIGGER trg_generate_employee_number
    BEFORE INSERT ON librarians
    FOR EACH ROW
    EXECUTE FUNCTION generate_employee_number();