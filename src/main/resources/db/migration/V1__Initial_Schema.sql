-- V1__Initial_Schema.sql
-- Flyway migration to create the production schema

CREATE TABLE accounts (
                          vpa VARCHAR(255) PRIMARY KEY,
                          holder_name VARCHAR(255) NOT NULL,
                          balance DECIMAL(19, 2) NOT NULL,
                          version BIGINT,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              packet_hash VARCHAR(64) NOT NULL UNIQUE,
                              sender_vpa VARCHAR(255) NOT NULL,
                              receiver_vpa VARCHAR(255) NOT NULL,
                              amount DECIMAL(19, 2) NOT NULL,
                              signed_at TIMESTAMP NOT NULL,
                              settled_at TIMESTAMP NOT NULL,
                              bridge_node_id VARCHAR(255) NOT NULL,
                              hop_count INT NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              db_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for fast dashboard lookups and deduplication
CREATE INDEX idx_packet_hash ON transactions(packet_hash);
CREATE INDEX idx_sender_vpa ON transactions(sender_vpa);
CREATE INDEX idx_receiver_vpa ON transactions(receiver_vpa);