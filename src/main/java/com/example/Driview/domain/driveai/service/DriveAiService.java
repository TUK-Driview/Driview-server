package com.example.Driview.domain.driveai.service;

import com.example.Driview.domain.driveai.dto.DriveAiAnalysisResponse;
import com.example.Driview.domain.driveai.dto.DriveAiResponse;
import com.example.Driview.domain.driveai.entity.DriveAiResult;
import com.example.Driview.domain.driveai.repository.DriveAiResultRepository;
import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.entity.ViolationEvent;
import com.example.Driview.domain.driving.enums.ViolationType;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.driving.repository.ViolationEventRepository;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DriveAiService {

    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final WebClient driveAiWebClient;
    private final UserRepository userRepository;
    private final DrivingSessionRepository drivingSessionRepository;
    private final DriveAiResultRepository driveAiResultRepository;
    private final ViolationEventRepository violationEventRepository;

    public CompletableFuture<DriveAiAnalysisResponse> analyzeVideo(MultipartFile file, Long userId) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "video");

        return driveAiWebClient.post()
                .uri("/api/driveai/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(DriveAiResponse.class)
                .onErrorMap(WebClientResponseException.class,
                        e -> new CustomException(ErrorCode.DRIVEAI_SERVER_ERROR))
                .toFuture()
                .thenApply(result -> {
                    DrivingSession session = createOrFindSession(result.getFilename(), userId);
                    saveResult(result, session);
                    saveLaneDepartureEvents(result.getLane_departure_timestamps(), session);
                    return new DriveAiAnalysisResponse(
                            session.getId(),
                            result.getLane_departure_count(),
                            result.getDuration_sec(),
                            result.getLane_departure_timestamps()
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
        if (filename == null || filename.length() < 15) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
        try {
            String dateTimePart = filename.substring(0, 15);
            return LocalDateTime.parse(dateTimePart, FILENAME_FORMATTER);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
    }

    private void saveLaneDepartureEvents(List<Double> timestamps, DrivingSession session) {
        if (timestamps == null) return;
        for (Double ts : timestamps) {
            ViolationEvent event = new ViolationEvent();
            violationEventRepository.save(ViolationEvent.ofLaneDeparture(session, ts.intValue()));
        }
    }

    private void saveResult(DriveAiResponse response, DrivingSession session) {
        if (response.getDuration_sec() != null) {
            session.updateDuration(response.getDuration_sec().intValue());
            drivingSessionRepository.save(session);
        }
        driveAiResultRepository.save(
                DriveAiResult.create(session, response.getLane_departure_count(), response.getDuration_sec())
        );
    }
}
