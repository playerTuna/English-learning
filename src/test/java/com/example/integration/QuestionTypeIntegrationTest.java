package com.example.integration;

import com.example.config.TestApplication;
import com.example.dto.UserAnswerDTO;
import com.example.entity.Topic;
import com.example.entity.User;
import com.example.repository.*;
import com.example.service.QuestionGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test") 
class QuestionTypeIntegrationTest {

@Autowired
        private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuestionGenerationService questionGenerationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    private User user;
    private Topic topic;
    private com.example.entity.Test mcqTest;
    private com.example.entity.Test fillInBlankTest;
    private com.example.entity.Test trueFalseTest;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("hashedpassword");
        user.setRole(User.UserRole.user);
        user.setName("Test User");
        user = userRepository.save(user);

        // Create test topic
        topic = new Topic();
        topic.setName("Test Topic");
        topic.setDescription("Test Description");
        topic = topicRepository.save(topic);

        // Create MCQ test
        mcqTest = new com.example.entity.Test();
        mcqTest.setTopic(topic);
        mcqTest.setTestType(com.example.entity.Test.TestType.multiple_choice);
        mcqTest.setNumQuestions(2);
        mcqTest = testRepository.save(mcqTest);

        // Create Fill in the Blank test
        fillInBlankTest = new com.example.entity.Test();
        fillInBlankTest.setTopic(topic);
        fillInBlankTest.setTestType(com.example.entity.Test.TestType.fill_in_the_blank);
        fillInBlankTest.setNumQuestions(2);
        fillInBlankTest = testRepository.save(fillInBlankTest);

        // Create True/False test
        trueFalseTest = new com.example.entity.Test();
        trueFalseTest.setTopic(topic);
        trueFalseTest.setTestType(com.example.entity.Test.TestType.true_false);
        trueFalseTest.setNumQuestions(2);
        trueFalseTest = testRepository.save(trueFalseTest);
    }

    @Test
    void testGenerateMultipleChoiceQuestions() throws Exception {
        // Test MCQ question generation
        List<String> vocabulary = Arrays.asList("apple", "banana");
        List<com.example.entity.Question> questions = questionGenerationService.generateQuestionsForTest(mcqTest, vocabulary);

        assertEquals(2, questions.size());
        
        // Check first question
        com.example.entity.Question question1 = questions.get(0);
        assertTrue(question1.getQuestionText().contains("apple"));
        assertNull(question1.getCorrectAnswerText()); // MCQ doesn't use correctAnswerText

        // Check answer options for MCQ (should have 4 options)
        List<com.example.entity.AnswerOption> options = answerOptionRepository.findByQuestionQuestionId(question1.getQuestionId());
        assertEquals(4, options.size());
        
        // Exactly one option should be correct
        long correctOptions = options.stream().filter(com.example.entity.AnswerOption::isCorrect).count();
        assertEquals(1, correctOptions);
    }

    @Test
    void testGenerateFillInTheBlankQuestions() throws Exception {
        // Test Fill in the Blank question generation
        List<String> vocabulary = Arrays.asList("apple", "banana");
        List<com.example.entity.Question> questions = questionGenerationService.generateQuestionsForTest(fillInBlankTest, vocabulary);

        assertEquals(2, questions.size());
        
        // Check first question
        com.example.entity.Question question1 = questions.get(0);
        assertTrue(question1.getQuestionText().contains("apple"));
        assertEquals("apple", question1.getCorrectAnswerText()); // Fill in the blank stores correct answer

        // Check answer options for Fill in the Blank (should have 0 options)
        List<com.example.entity.AnswerOption> options = answerOptionRepository.findByQuestionQuestionId(question1.getQuestionId());
        assertEquals(0, options.size());
    }

    @Test
    void testGenerateTrueFalseQuestions() throws Exception {
        // Test True/False question generation
        List<String> vocabulary = Arrays.asList("apple", "banana");
        List<com.example.entity.Question> questions = questionGenerationService.generateQuestionsForTest(trueFalseTest, vocabulary);

        assertEquals(2, questions.size());
        
        // Check first question
        com.example.entity.Question question1 = questions.get(0);
        assertTrue(question1.getQuestionText().contains("apple"));
        assertNull(question1.getCorrectAnswerText()); // T/F doesn't use correctAnswerText

        // Check answer options for True/False (should have 2 options: True and False)
        List<com.example.entity.AnswerOption> options = answerOptionRepository.findByQuestionQuestionId(question1.getQuestionId());
        assertEquals(2, options.size());
        
        // Exactly one option should be correct
        long correctOptions = options.stream().filter(com.example.entity.AnswerOption::isCorrect).count();
        assertEquals(1, correctOptions);
        
        // Check that we have both True and False options
        List<String> optionTexts = options.stream()
                .map(com.example.entity.AnswerOption::getOptionText)
                .toList();
        assertTrue(optionTexts.contains("True"));
        assertTrue(optionTexts.contains("False"));
    }

    @Test
    void testUserAnswerValidationForMCQ() throws Exception {
        // Generate MCQ question
        List<String> vocabulary = Arrays.asList("apple");
        List<com.example.entity.Question> questions = questionGenerationService.generateQuestionsForTest(mcqTest, vocabulary);
        com.example.entity.Question question = questions.get(0);
        
        // Create test result
        com.example.entity.TestResult testResult = new com.example.entity.TestResult();
        testResult.setUser(user);
        testResult.setTest(mcqTest);
        testResult.setCorrectAnswers(0);
        testResult.setTotalQuestions(1);
        testResult.setTakenAt(OffsetDateTime.now());
        testResult = testResultRepository.save(testResult);

        // Test correct answer
        com.example.entity.AnswerOption correctOption = answerOptionRepository.findByQuestionQuestionId(question.getQuestionId())
                .stream()
                .filter(com.example.entity.AnswerOption::isCorrect)
                .findFirst()
                .orElseThrow();

        UserAnswerDTO correctUserAnswer = new UserAnswerDTO();
        correctUserAnswer.setResultId(testResult.getResultId().toString());
        correctUserAnswer.setQuestionId(question.getQuestionId().toString());
        correctUserAnswer.setSelectedOptionId(correctOption.getOptionId().toString());
        correctUserAnswer.setIsCorrect(true);

        // Validate answer
        boolean isValid = questionGenerationService.validateUserAnswer(question, 
                questionGenerationService.createUserAnswer(question, correctOption.getOptionId().toString(), null));
        assertTrue(isValid);

        // Test incorrect answer
        com.example.entity.AnswerOption incorrectOption = answerOptionRepository.findByQuestionQuestionId(question.getQuestionId())
                .stream()
                .filter(option -> !option.isCorrect())
                .findFirst()
                .orElseThrow();

        boolean isInvalid = questionGenerationService.validateUserAnswer(question, 
                questionGenerationService.createUserAnswer(question, incorrectOption.getOptionId().toString(), null));
        assertFalse(isInvalid);
    }

    @Test
    void testUserAnswerValidationForFillInTheBlank() throws Exception {
        // Generate Fill in the Blank question
        List<String> vocabulary = Arrays.asList("apple");
        List<com.example.entity.Question> questions = questionGenerationService.generateQuestionsForTest(fillInBlankTest, vocabulary);
        com.example.entity.Question question = questions.get(0);

        // Test correct answer
        boolean isValid = questionGenerationService.validateUserAnswer(question, 
                questionGenerationService.createUserAnswer(question, null, "apple"));
        assertTrue(isValid);

        // Test incorrect answer
        boolean isInvalid = questionGenerationService.validateUserAnswer(question, 
                questionGenerationService.createUserAnswer(question, null, "orange"));
        assertFalse(isInvalid);

        // Test case insensitive matching
        boolean isCaseInsensitiveValid = questionGenerationService.validateUserAnswer(question, 
                questionGenerationService.createUserAnswer(question, null, "APPLE"));
        assertTrue(isCaseInsensitiveValid);
    }

    @Test
    void testUserAnswerValidationForTrueFalse() throws Exception {
        // Generate True/False question
        List<String> vocabulary = Arrays.asList("apple");
        List<com.example.entity.Question> questions = questionGenerationService.generateQuestionsForTest(trueFalseTest, vocabulary);
        com.example.entity.Question question = questions.get(0);

        // Test correct answer
        com.example.entity.AnswerOption correctOption = answerOptionRepository.findByQuestionQuestionId(question.getQuestionId())
                .stream()
                .filter(com.example.entity.AnswerOption::isCorrect)
                .findFirst()
                .orElseThrow();

        boolean isValid = questionGenerationService.validateUserAnswer(question, 
                questionGenerationService.createUserAnswer(question, correctOption.getOptionId().toString(), null));
        assertTrue(isValid);

        // Test incorrect answer
        com.example.entity.AnswerOption incorrectOption = answerOptionRepository.findByQuestionQuestionId(question.getQuestionId())
                .stream()
                .filter(option -> !option.isCorrect())
                .findFirst()
                .orElseThrow();

        boolean isInvalid = questionGenerationService.validateUserAnswer(question, 
                questionGenerationService.createUserAnswer(question, incorrectOption.getOptionId().toString(), null));
        assertFalse(isInvalid);
    }

    @Test
    void testUserAnswerAPIEndpoint() throws Exception {
        // Generate questions
        List<String> vocabulary = Arrays.asList("apple");
        List<com.example.entity.Question> questions = questionGenerationService.generateQuestionsForTest(mcqTest, vocabulary);
        com.example.entity.Question question = questions.get(0);

        // Create test result
        com.example.entity.TestResult testResult = new com.example.entity.TestResult();
        testResult.setUser(user);
        testResult.setTest(mcqTest);
        testResult.setCorrectAnswers(0);
        testResult.setTotalQuestions(1);
        testResult.setTakenAt(OffsetDateTime.now());
        testResult = testResultRepository.save(testResult);

        // Get correct answer option
        com.example.entity.AnswerOption correctOption = answerOptionRepository.findByQuestionQuestionId(question.getQuestionId())
                .stream()
                .filter(com.example.entity.AnswerOption::isCorrect)
                .findFirst()
                .orElseThrow();

        // Create user answer via API
        UserAnswerDTO userAnswerDTO = new UserAnswerDTO();
        userAnswerDTO.setResultId(testResult.getResultId().toString());
        userAnswerDTO.setQuestionId(question.getQuestionId().toString());
        userAnswerDTO.setSelectedOptionId(correctOption.getOptionId().toString());

        mockMvc.perform(post("/api/user_answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAnswerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCorrect").value(true));
    }
}
