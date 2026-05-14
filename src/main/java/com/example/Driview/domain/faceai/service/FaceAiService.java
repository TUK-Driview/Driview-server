package com.example.Driview.domain.faceai.service;

import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.faceai.dto.FaceAiResponse;
import com.example.Driview.domain.faceai.entity.FaceAiEvent;
import com.example.Driview.domain.faceai.entity.FaceAiResult;
import com.example.Driview.domain.faceai.repository.FaceAiResultRepository;
import com.example.Driview.domain.user.entity.User;
import com.example.Driview.domain.user.repository.UserRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FaceAiService {

    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final WebClient faceAiWebClient;
    private final UserRepository userRepository;
    private final DrivingSessionRepository drivingSessionRepository;
    private final FaceAiResultRepository faceAiResultRepository;

    public CompletableFuture<FaceAiResponse> analyzeVideo(MultipartFile file, Long userId) {
        String filename = file.getOriginalFilename();
        LocalDateTime startedAt = parseStartedAt(filename);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 동일 시각 세션이 있으면 재사용, 없으면 생성
        DrivingSession session = drivingSessionRepository
                .findByUser_IdAndStartedAt(userId, startedAt)
                .orElseGet(() -> drivingSessionRepository.save(DrivingSession.create(user, startedAt)));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(filename != null ? filename : "video");

        return faceAiWebClient.post()
                .uri("/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(FaceAiResponse.class)
                .onErrorMap(WebClientResponseException.class,
                        e -> new CustomException(ErrorCode.FACEAI_SERVER_ERROR))
                .toFuture()
                .thenApply(result -> {
                    saveResult(result, session);
                    return result;
                });
    }

    private LocalDateTime parseStartedAt(String filename) {
        // 예: 20260513_143022_B.mp4 → 2026-05-13T14:30:22
        if (filename == null || filename.length() < 15) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
        try {
            String dateTimePart = filename.substring(0, 15); // "20260513_143022"
            return LocalDateTime.parse(dateTimePart, FILENAME_FORMATTER);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
    }

    private void saveResult(FaceAiResponse response, DrivingSession session) {
        List<FaceAiEvent> events = new ArrayList<>();
        if (response.getDrowsinessEvents() != null) {
            events = response.getDrowsinessEvents().stream()
                    .map(e -> FaceAiEvent.of(e.getTimestamp(), e.getType()))
                    .toList();
        }

        // AI 분석 결과로 세션 duration 업데이트
        if (response.getDuration_sec() != null) {
            session.updateDuration(response.getDuration_sec().intValue());
            drivingSessionRepository.save(session);
        }

        FaceAiResult result = FaceAiResult.create(
                session,
                response.getYawn_count(),
                response.getDuration_sec(),
                events
        );
        faceAiResultRepository.save(result);
    }
}
