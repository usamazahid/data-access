package org.irs.dto;

public class GenerateInsightsRequestDTO {
    private String sql;
    private String question;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
} 