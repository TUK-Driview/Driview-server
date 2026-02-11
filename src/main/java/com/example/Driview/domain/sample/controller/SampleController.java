package com.example.Driview.domain.sample.controller;

import com.example.Driview.domain.sample.dto.SampleCreateRequest;
import com.example.Driview.domain.sample.dto.SampleResponse;
import com.example.Driview.domain.sample.dto.SampleUpdateRequest;
import com.example.Driview.domain.sample.service.SampleService;
import com.example.Driview.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 샘플 API 컨트롤러
 */
@RestController
@RequestMapping("/api/samples")
@Tag(name = "Sample", description = "샘플 API")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Operation(
            summary = "샘플 생성",
            description = "샘플 데이터를 생성합니다."
    )
    @PostMapping
    public ApiResponse<SampleResponse> create(
            @Valid @RequestBody SampleCreateRequest request
    ) {
        return ApiResponse.success("샘플 생성 완료", sampleService.create(request));
    }

    @Operation(
            summary = "샘플 단건 조회",
            description = "샘플 ID를 이용해 단건 조회합니다."
    )
    @GetMapping("/{id}")
    public ApiResponse<SampleResponse> get(@PathVariable Long id) {
        return ApiResponse.success(sampleService.get(id));
    }

    @Operation(
            summary = "샘플 수정",
            description = "샘플 ID에 해당하는 데이터를 수정합니다."
    )
    @PutMapping("/{id}")
    public ApiResponse<SampleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SampleUpdateRequest request
    ) {
        return ApiResponse.success("샘플 수정 완료", sampleService.update(id, request));
    }

    @Operation(
            summary = "샘플 삭제",
            description = "샘플 ID에 해당하는 데이터를 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sampleService.delete(id);
        return ApiResponse.success("샘플 삭제 완료", null);
    }
}