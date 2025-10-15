package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.AnswerOption;
import com.example.entity.Question;
import com.example.entity.Test;
import com.example.entity.UserAnswer;
import com.example.repository.AnswerOptionRepository;
import com.example.repository.QuestionRepository;

@Service
public class QuestionGenerationService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    /**
     * Generate questions for a test based on its type
     * 
     * @param test           The test to generate questions for
     * @param vocabularyList List of vocabulary words to create questions from
     * @return List of generated questions
     */
    public List<Question> generateQuestionsForTest(Test test, List<String> vocabularyList) {
        List<Question> questions = new ArrayList<>();

        switch (test.getTestType()) {
            case multiple_choice:
                questions = generateMultipleChoiceQuestions(test, vocabularyList);
                break;
            case fill_in_the_blank:
                questions = generateFillInTheBlankQuestions(test, vocabularyList);
                break;
            case true_false:
                questions = generateTrueFalseQuestions(test, vocabularyList);
                break;
            default:
                throw new IllegalArgumentException("Unsupported test type: " + test.getTestType());
        }

        return questions;
    }

    /**
     * Generate multiple choice questions with 4 options each
     */
    private List<Question> generateMultipleChoiceQuestions(Test test, List<String> vocabularyList) {
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < test.getNumQuestions() && i < vocabularyList.size(); i++) {
            String word = vocabularyList.get(i);

            // Create question
            Question question = new Question();
            question.setTest(test);
            question.setQuestionText("What is the meaning of '" + word + "'?");
            question.setCorrectAnswerText(null);

            question = questionRepository.save(question);

            // Create 4 answer options
            List<AnswerOption> options = generateMultipleChoiceOptions(question, word, vocabularyList);
            answerOptionRepository.saveAll(options);

            questions.add(question);
        }

        return questions;
    }

    /**
     * Generate fill in the blank questions
     */
    private List<Question> generateFillInTheBlankQuestions(Test test, List<String> vocabularyList) {
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < test.getNumQuestions() && i < vocabularyList.size(); i++) {
            String word = vocabularyList.get(i);

            // Create question
            Question question = new Question();
            question.setTest(test);
            question.setQuestionText("Fill in the blank: The word '" + word + "' means __________");
            question.setCorrectAnswerText(word); // Store the correct answer

            question = questionRepository.save(question);

            // No answer options needed for fill in the blank
            questions.add(question);
        }

        return questions;
    }

    /**
     * Generate true/false questions
     */
    private List<Question> generateTrueFalseQuestions(Test test, List<String> vocabularyList) {
        List<Question> questions = new ArrayList<>();

        for (int i = 0; i < test.getNumQuestions() && i < vocabularyList.size(); i++) {
            String word = vocabularyList.get(i);

            // Create question
            Question question = new Question();
            question.setTest(test);
            question.setQuestionText("True or False: '" + word + "' is a valid English word.");
            question.setCorrectAnswerText(null); // Not used for T/F

            question = questionRepository.save(question);

            // Create True/False options
            List<AnswerOption> options = generateTrueFalseOptions(question, true); // Assuming true for now
            answerOptionRepository.saveAll(options);

            questions.add(question);
        }

        return questions;
    }

    /**
     * Generate 4 multiple choice options (1 correct, 3 incorrect)
     */
    private List<AnswerOption> generateMultipleChoiceOptions(Question question, String correctWord,
            List<String> vocabularyList) {
        List<AnswerOption> options = new ArrayList<>();

        // Add correct answer
        AnswerOption correctOption = new AnswerOption();
        correctOption.setQuestion(question);
        correctOption.setOptionText("Correct meaning of " + correctWord);
        correctOption.setCorrect(true);
        options.add(correctOption);

        // Add 3 incorrect options (always generate 3, regardless of vocabulary list
        // size)
        for (int i = 0; i < 3; i++) {
            AnswerOption incorrectOption = new AnswerOption();
            incorrectOption.setQuestion(question);
            incorrectOption.setOptionText("Incorrect meaning " + (i + 1));
            incorrectOption.setCorrect(false);
            options.add(incorrectOption);
        }

        return options;
    }

    /**
     * Generate True/False options
     */
    private List<AnswerOption> generateTrueFalseOptions(Question question, boolean correctAnswer) {
        List<AnswerOption> options = new ArrayList<>();

        // True option
        AnswerOption trueOption = new AnswerOption();
        trueOption.setQuestion(question);
        trueOption.setOptionText("True");
        trueOption.setCorrect(correctAnswer);
        options.add(trueOption);

        // False option
        AnswerOption falseOption = new AnswerOption();
        falseOption.setQuestion(question);
        falseOption.setOptionText("False");
        falseOption.setCorrect(!correctAnswer);
        options.add(falseOption);

        return options;
    }

    /**
     * Validate user answer based on question type
     */
    public boolean validateUserAnswer(Question question, UserAnswer userAnswer) {
        switch (question.getTest().getTestType()) {
            case multiple_choice:
            case true_false:
                return userAnswer.getSelectedOption() != null &&
                        userAnswer.getSelectedOption().isCorrect();

            case fill_in_the_blank:
                return userAnswer.getTextAnswer() != null &&
                        userAnswer.getTextAnswer().trim().equalsIgnoreCase(question.getCorrectAnswerText().trim());

            default:
                return false;
        }
    }

    /**
     * Create user answer based on question type
     */
    public UserAnswer createUserAnswer(Question question, String selectedOptionId, String textAnswer) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setQuestion(question);
        userAnswer.setTextAnswer(textAnswer);

        // Set selected option if provided (for MCQ and T/F)
        if (selectedOptionId != null && !selectedOptionId.isEmpty()) {
            AnswerOption selectedOption = answerOptionRepository.findById(UUID.fromString(selectedOptionId))
                    .orElse(null);
            userAnswer.setSelectedOption(selectedOption);
        }

        // Validate and set correctness
        boolean isCorrect = validateUserAnswer(question, userAnswer);
        userAnswer.setIsCorrect(isCorrect);

        return userAnswer;
    }
}
