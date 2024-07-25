package com.challenge.server;

public class Question {
    private int id;
    private int challengeId;
    private String questionText;
    private String answer;
    private int marks;

    public Question(String questionText, String answer, int marks) {
        this.questionText = questionText;
        this.answer = answer;
        this.marks = marks;
    }

    public Question(int id, int challengeId, String questionText, String answer, int marks) {
        this.id = id;
        this.challengeId = challengeId;
        this.questionText = questionText;
        this.answer = answer;
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }
}
