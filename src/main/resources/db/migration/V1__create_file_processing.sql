CREATE TABLE file_processing (
                                 id UUID PRIMARY KEY,
                                 original_file_name VARCHAR(255) NOT NULL,
                                 source_bucket VARCHAR(255) NOT NULL,
                                 source_object_key VARCHAR(255) NOT NULL,
                                 result_bucket VARCHAR(255),
                                 result_object_key VARCHAR(255),
                                 status VARCHAR(32) NOT NULL,
                                 error_message VARCHAR(255),
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);