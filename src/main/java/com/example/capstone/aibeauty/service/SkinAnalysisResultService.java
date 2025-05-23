package com.example.capstone.aibeauty.service;

import com.example.capstone.aibeauty.domain.SkinAnalysisResult;
import com.example.capstone.aibeauty.dto.SkinAnalysisResponse;
import com.example.capstone.aibeauty.repository.SkinAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkinAnalysisResultService {

    private final SkinAnalysisResultRepository repository;

    // 분석 결과 id를 기반으로 DB에서 데이터를 조회하고 이를 응답 DTO로 변환하여 반환
    public SkinAnalysisResponse getAnalysisResult(String analysisId) {
        try {
            SkinAnalysisResult result = repository.findByAnalysisId(analysisId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 id의 분석 결과를 찾을 수 없습니다."));

            return fromEntity(result);
        } catch (DataAccessException e) {
            throw new RuntimeException("분석 결과 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 사용자 id를 기반으로 피부 분석 결과의 날짜 목록을 반환
    public List<LocalDate> getAnalysisDates(String userId) {
        List<SkinAnalysisResult> results = repository.findAllByUserId(userId);

        return results.stream()
                .map(r -> r.getCreatedAt().toLocalDate())  // LocalDate로 변환
                .distinct()  // 중복 제거
                .sorted(Comparator.reverseOrder())    // 오름차순 정렬
                .toList();
    }

    // 사용자 id와 특정 날짜를 기반으로 해당 날짜에 수행된 피부 분석 결과를 조회하고 응답 DTO로 반환
    public SkinAnalysisResponse getAnalysisResultByDate(String userId, LocalDate date) {
        // 특정 날짜 범위 설정
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        SkinAnalysisResult result = repository.findByUserIdAndCreatedAtBetween(userId, start, end)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 분석 결과를 찾을 수 없습니다."));

        return fromEntity(result);
    }

    // 엔티티 객체를 SkinAnalysisResponse DTO로 변환 (build 하는 거 위에서 중복 코드라 따로 뺌)
    private SkinAnalysisResponse fromEntity(SkinAnalysisResult result) {
        return SkinAnalysisResponse.builder()
                .imageUrls(result.getImageUrls())
                .skinAge(result.getSkinAge())
                .foreheadWrinkle(result.getForeheadWrinkle())
                .foreheadPigmentation(result.getForeheadPigmentation())
                .glabellaWrinkle(result.getGlabellaWrinkle())
                .lefteyeWrinkle(result.getLefteyeWrinkle())
                .righteyeWrinkle(result.getRighteyeWrinkle())
                .leftcheekPigmentation(result.getLeftcheekPigmentation())
                .leftcheekPore(result.getLeftcheekPore())
                .rightcheekPigmentation(result.getRightcheekPigmentation())
                .rightcheekPore(result.getRightcheekPore())
                .lipDryness(result.getLipDryness())
                .jawlineSagging(result.getJawlineSagging())
                .totalWrinkle(result.getTotalWrinkle())
                .totalPigmentation(result.getTotalPigmentation())
                .totalPore(result.getTotalPore())
                .createdAt(result.getCreatedAt())
                .build();
    }
}
