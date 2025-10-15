CREATE EXTENSION IF NOT EXISTS "citext";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE user_role AS ENUM ('admin', 'user');
CREATE TYPE word_type_enum AS ENUM ('noun', 'verb', 'adjective', 'adverb', 'phrase', 'other');
CREATE TYPE test_type_enum AS ENUM ('multiple_choice', 'fill_in_the_blank', 'true_false');

CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username CITEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role user_role DEFAULT 'user',
    name TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE topic (
    topic_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE vocabulary (
    vocab_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic_id UUID REFERENCES topic(topic_id) ON DELETE SET NULL,
    word TEXT NOT NULL,
    meaning TEXT,
    word_type word_type_enum NOT NULL,
    example_sentence TEXT,
    audio_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(topic_id, word, word_type)
);

CREATE TABLE tests (
    test_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic_id UUID REFERENCES topic(topic_id) ON DELETE SET NULL,
    test_type test_type_enum NOT NULL,
    num_questions INT CHECK (num_questions > 0) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE user_learning_vocab (
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    vocab_id UUID REFERENCES vocabulary(vocab_id) ON DELETE CASCADE,
    last_review_at TIMESTAMPTZ DEFAULT NOW(),
    next_review_at TIMESTAMPTZ,
    ease_factor REAL DEFAULT 2.5 CHECK (ease_factor >= 1.3),
    repetition_count BIGINT DEFAULT 0 CHECK (repetition_count >= 0),
    success_rate REAL DEFAULT 0 CHECK (success_rate >= 0 AND success_rate <= 1),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (user_id, vocab_id)
);

CREATE TABLE questions (
    question_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_id UUID REFERENCES tests(test_id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    image_url TEXT,
    audio_url TEXT,
    correct_answer_text TEXT, -- For fill_in_the_blank questions
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(test_id, question_text)
);

CREATE TABLE answer_options (
    option_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID REFERENCES questions(question_id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(question_id, option_text)
);

CREATE TABLE test_result (
    result_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    test_id UUID REFERENCES tests(test_id) ON DELETE CASCADE,
    score REAL CHECK (score >= 0 AND score <= 100),
    total_questions INT CHECK (total_questions > 0),
    correct_answers INT CHECK (correct_answers >= 0),
    taken_at TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, test_id, taken_at)
);

CREATE TABLE user_answer (
    answer_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_id UUID REFERENCES test_result(result_id) ON DELETE CASCADE,
    question_id UUID REFERENCES questions(question_id) ON DELETE CASCADE,
    selected_option_id UUID REFERENCES answer_options(option_id) ON DELETE SET NULL,
    text_answer TEXT, -- For fill_in_the_blank questions
    is_correct BOOLEAN,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE users ADD COLUMN banStatus BOOLEAN DEFAULT FALSE, ADD COLUMN banUtil TIMESTAMP NULL, ADD COLUMN reason VARCHAR(255) DEFAULT NULL;