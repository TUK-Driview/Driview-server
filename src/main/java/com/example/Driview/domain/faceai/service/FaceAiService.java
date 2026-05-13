package com.example.Driview.domain.faceai.service;

import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.faceai.dto.FaceAiResponse;
import com.example.Driview.domain.faceai.entity.FaceAiEvent;
import com.example.Driview.domain.faceai.entity.FaceAiResult;
import com.example.Driview.domain.faceai.repository.FaceAiResultRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FaceAiService {

    private final WebClient faceAiWebClient;
    private final DrivingSessionRepository drivingSessionRepository;
    private final FaceAiResultRepository faceAiResultRepository;

    public CompletableFuture<FaceAiResponse> analyzeVideo(MultipartFile file, Long sessionId) {
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
                    saveResult(result, sessionId);
                    return result;
                });
    }

    private void saveResult(FaceAiResponse response, Long sessionId) {
        DrivingSession session = drivingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        List<FaceAiEvent> events = new ArrayList<>();
        if (response.getDrowsinessEvents() != null) {
            events = response.getDrowsinessEvents().stream()
                    .map(e -> FaceAiEvent.of(e.getTimestamp(), e.getType()))
                    .toList();
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
