DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE persons (
    id INTEGER PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL
);

CREATE TABLE projects (
    id INTEGER PRIMARY KEY,
    url VARCHAR(120) NOT NULL,
    name VARCHAR(120),
    description VARCHAR(1024)
);

CREATE TABLE personsprojects (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES projects(id),
    person_id INTEGER REFERENCES persons(id)
);

CREATE TABLE actions (
    id INTEGER PRIMARY KEY,
    timestamp BIGINT,
    action VARCHAR(120) NOT NULL,
    project_id INTEGER REFERENCES projects(id),
    person_id INTEGER REFERENCES persons(id)
);


GRANT ALL PRIVILEGES ON DATABASE lumberghly to lumberghly_dev;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public to lumberghly_dev;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO lumberghly_dev;
GRANT ALL ON SCHEMA public to lumberghly_dev;
