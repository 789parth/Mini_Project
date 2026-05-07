package com.example.miniproject.domain;

public class FaqModel {
    private String question;
    private String answer;

    // Required for Firebase deserialization
    public FaqModel() {}

    public FaqModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() { return question; }
    public String getAnswer()   { return answer; }
    public void setQuestion(String question) { this.question = question; }
    public void setAnswer(String answer)     { this.answer = answer; }
}
