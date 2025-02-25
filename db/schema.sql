


CREATE TABLE app_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    role VARCHAR(50) NOT NULL
);


CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    ticket_priority VARCHAR(50) NOT NULL CHECK (ticket_priority IN ('Low', 'Medium', 'High')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('NEW', 'IN_PROGRESS', 'RESOLVED')) DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    category_id INT NOT NULL,
    created_by UUID NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES app_users(id) ON DELETE CASCADE
);


CREATE TABLE ticket_comments (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL,
    commented_by UUID NOT NULL,
    comment_text TEXT NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (commented_by) REFERENCES app_users(id) ON DELETE CASCADE
);


CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL,
    changed_by UUID NOT NULL,
    action VARCHAR(255) NOT NULL,
    details TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES app_users(id) ON DELETE CASCADE
);


CREATE INDEX idx_user_email ON app_users(email);
CREATE INDEX idx_audit_ticket ON audit_log(ticket_id);
CREATE INDEX idx_audit_user ON audit_log(changed_by);
CREATE INDEX idx_audit_action ON audit_log(action);
