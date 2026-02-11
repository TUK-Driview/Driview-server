package com.example.Driview.domain.sample.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "sample")
public class SampleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제목
    @Column(nullable = false, length = 100)
    private String title;

    // 내용
    @Column(nullable = false, length = 500)
    private String content;

    // 값 변경용 메서드 (setter 대신)
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}