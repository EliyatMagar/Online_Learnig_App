package com.EliyatMagar.LearningWebApp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String answerText; // For subjective answers

    @ManyToOne
    @JoinColumn(name = "selected_option")
    private Option selectedOption; // For MCQ answers

    private int marksObtained;
}