-- Users
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Topic
CREATE TRIGGER trg_topic_updated_at
BEFORE UPDATE ON topic
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Vocabulary
CREATE TRIGGER trg_vocabulary_updated_at
BEFORE UPDATE ON vocabulary
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Tests
CREATE TRIGGER trg_tests_updated_at
BEFORE UPDATE ON tests
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- User Learning Vocab
CREATE TRIGGER trg_user_learning_vocab_updated_at
BEFORE UPDATE ON user_learning_vocab
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Questions
CREATE TRIGGER trg_questions_updated_at
BEFORE UPDATE ON questions
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Answer Options
CREATE TRIGGER trg_answer_options_updated_at
BEFORE UPDATE ON answer_options
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Test Result
CREATE TRIGGER trg_test_result_updated_at
BEFORE UPDATE ON test_result
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- User Answer
CREATE TRIGGER trg_user_answer_updated_at
BEFORE UPDATE ON user_answer
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
