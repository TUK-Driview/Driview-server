package com.example.Driview.domain.faceai.service;

import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.entity.ViolationEvent;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.driving.repository.ViolationEventRepository;
import com.example.Driview.domain.faceai.dto.FaceAiAnalysisResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FaceAiService {

    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private static final int DROWSY_WINDOW_SEC = 600;
    private static final int DROWSY_YAWN_THRESHOLD = 3;

    private final WebClient faceAiWebClient;
    private final UserRepository userRepository;
    private final DrivingSessionRepository drivingSessionRepository;
    private final FaceAiResultRepository faceAiResultRepository;
    private final ViolationEventRepository violationEventRepository;

    public CompletableFuture<FaceAiAnalysisResponse> analyzeVideo(MultipartFile file, Long userId) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "video");

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
                    DrivingSession session = createOrFindSession(result.getFilename(), userId);
                    saveResult(result, session);
                    saveDrowsyEvents(result.getYawn_timestamps(), session);
                    return new FaceAiAnalysisResponse(
                            session.getId(),
                            result.getYawn_count(),
                            result.getDuration_sec(),
                            result.getDrowsinessEvents()
                    );
                });
    }

    private DrivingSession createOrFindSession(String filename, Long userId) {
        LocalDateTime startedAt = parseStartedAt(filename);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return drivingSessionRepository
                .findByUser_IdAndStartedAt(userId, startedAt)
                .orElseGet(() -> drivingSessionRepository.save(DrivingSession.create(user, startedAt)));
    }

    private LocalDateTime parseStartedAt(String filename) {
        // 예: 20260314_174454_B.mp4 → 2026-03-14 17:44:54
        if (filename == null || filename.length() < 15) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
        try {
            String dateTimePart = filename.substring(0, 15); // "20260314_174454"
            return LocalDateTime.parse(dateTimePart, FILENAME_FORMATTER);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
    }

    private void saveDrowsyEvents(List<Double> yawnTimestamps, DrivingSession session) {
        if (yawnTimestamps == null || yawnTimestamps.size() < DROWSY_YAWN_THRESHOLD) return;

        List<Double> sorted = new ArrayList<>(yawnTimestamps);
        Collections.sort(sorted);

        int i = 0;
        while (i <= sorted.size() - DROWSY_YAWN_THRESHOLD) {
            double windowStart = sorted.get(i);
            double thirdYawn = sorted.get(i + DROWSY_YAWN_THRESHOLD - 1);

            if (thirdYawn - windowStart <= DROWSY_WINDOW_SEC) {
                violationEventRepository.save(
                        ViolationEvent.ofDrowsy(session, (int) thirdYawn)
                );
                i += DROWSY_YAWN_THRESHOLD;
            } else {
                i++;
            }
        }
    }

    private void saveResult(FaceAiResponse response, DrivingSession session) {
        List<FaceAiEvent> events = new ArrayList<>();
        if (response.getDrowsinessEvents() != null) {
            events = response.getDrowsinessEvents().stream()
                    .map(e -> FaceAiEvent.of(e.getTimestamp(), e.getType()))
                    .toList();
        }

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
