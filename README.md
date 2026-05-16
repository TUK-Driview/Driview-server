# 🚗 Driview — AI 기반 운전 습관 분석 플랫폼

> 전방 카메라와 운전자 카메라 영상을 AI로 분석해 차선 이탈, 졸음운전을 감지하고 운전 습관 리포트를 제공합니다.

---

## 1. 프로젝트 소개

Driview는 두 가지 AI 모델을 결합한 운전 습관 분석 시스템입니다.  
운전 영상을 업로드하면 **차선 이탈**과 **졸음 운전**을 자동으로 감지하고, 세션별 리포트와 점수를 제공합니다.

---

## 2. 주요 기능

### 차선 이탈 감지 (DriveAI)
- **YOLOP** 딥러닝 모델 사용
- 전방 영상에서 차선 세그멘테이션 → 좌/우 차선 경계 추적
- 이탈 발생 시각(초) 및 횟수 자동 감지
- GPU 가속 추론 (AWS g4dn 인스턴스, NVIDIA T4)

### 졸음운전 감지 (FaceAI)
- **MediaPipe Face Mesh** 468개 얼굴 랜드마크 실시간 추적
- 입 개폐 비율(Mouth Aspect Ratio) 기반 하품 감지
- 2초 이상 입 벌림 → 하품 이벤트, 10분 내 3회 이상 → 졸음 경보
- 하품 발생 시각 및 횟수 분석

### 운전 리포트
- 세션별 종합 점수 및 등급 산출 (S / A / B / C / D)
- 항목별 세부 점수: 차선 준수, 집중도, 속도
- 위반 이벤트 타임라인 (발생 시각 기반)
- 월별 운전 세션 이력 조회

### 커뮤니티
- 카테고리별 게시글 작성 / 조회 / 좋아요 / 댓글
- 운전 관련 정보 공유 및 소통

### 배지 시스템
- 안전 운전 달성 시 배지 획득
- 사용자 통계 기반 성취 시스템

---

## 3. 시스템 아키텍처

```
┌─────────────────────────────────────────────────┐
│                  Client (Mobile/Web)            │
└──────────────────────┬──────────────────────────┘
                       │ REST API
┌──────────────────────▼──────────────────────────┐
│           Driview Backend (Spring Boot)         │
│                   Port 8080                     │
│  - JWT 인증/인가                                  │
│  - 세션 관리 / 리포트 / 커뮤니티                      │
└────────────┬─────────────────────┬──────────────┘
             │ WebClient (비동기)   │ WebClient (비동기)
┌────────────▼──────────┐ ┌───────▼──────────────┐
│   DriveAI (FastAPI)   │ │   FaceAI (FastAPI)   │
│      Port 8000        │ │      Port 8000       │
│  YOLOP 차선 이탈 감지    │ │  MediaPipe 졸음 감지    │
│  AWS g4dn (GPU)       │ │  AWS EC2             │
└───────────────────────┘ └──────────────────────┘
                         │
            ┌────────────▼──────────┐
            │     MySQL Database    │
            └───────────────────────┘
```

---

## 4. 기술 스택

### Backend
| 분류 | 기술 |
|------|------|
| Framework | Spring Boot 4.0, Spring Security |
| Language | Java 21 |
| Auth | JWT (Access 30분 / Refresh 14일) |
| HTTP Client | Spring WebFlux WebClient (비동기) |
| Database | MySQL + Spring Data JPA |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Deploy | Docker |

### AI Services
| 분류 | 기술 |
|------|------|
| Framework | FastAPI + Uvicorn |
| Language | Python 3.10 |
| Lane Detection | YOLOP (PyTorch, CUDA 11.8) |
| Face Detection | MediaPipe Face Mesh |
| Video Processing | OpenCV |
| Infra | AWS g4dn.xlarge (NVIDIA T4 GPU) |

---

## 5. API 명세

### 인증
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/reissue` | 토큰 재발급 |
| POST | `/api/auth/logout` | 로그아웃 |

### AI 분석
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/driveai/analyze` | 전방 영상 → 차선 이탈 분석 |
| POST | `/api/faceai/analyze` | 운전자 영상 → 졸음 감지 분석 |

### 운전 리포트
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/driving/session` | 월별 세션 목록 |
| GET | `/api/driving/{sessionId}/report` | 세션 리포트 상세 |
| GET | `/api/driving/{sessionId}/timeline` | 위반 이벤트 타임라인 |
| GET | `/api/driving/{sessionId}/status` | 분석 진행 상태 |

### 사용자
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/users/me` | 내 프로필 |
| GET | `/api/users/me/stats` | 운전 통계 |
| GET | `/api/users/me/badge` | 배지 현황 |

### 커뮤니티
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/posts` | 게시글 목록 |
| POST | `/api/v1/posts` | 게시글 작성 |
| POST | `/api/v1/posts/{postId}/likes` | 좋아요 토글 |
| POST | `/api/v1/posts/{postId}/comments` | 댓글 작성 |

---

## 6. AI 모델 상세

### DriveAI — 차선 이탈 감지
```
입력: 주행 영상 (.mp4 / .avi / .mov / .mkv)
  ↓
YOLOP 모델 추론 (640×640, 3프레임마다 1장 처리)
  ↓
차선 세그멘테이션 마스크 생성
  ↓
하단 10% ROI에서 좌/우 차선 중심 좌표 추출
  ↓
10프레임 기준선 설정 → 이탈 상태 머신 (NORMAL ↔ NOT_DETECTED)
  ↓
출력: { lane_departure_count, lane_departure_timestamps[] }
```

### FaceAI — 졸음 감지
```
입력: 운전자 영상 (.mp4 / .avi / .mov / .mkv)
  ↓
MediaPipe Face Mesh → 468개 랜드마크 추출
  ↓
Mouth Aspect Ratio = 입 높이 / 입 너비 (임계값 0.30)
  ↓
2초 이상 유지 → 하품 이벤트 기록
  ↓
출력: { yawn_count, yawn_timestamps[] }
```

---

## 7. 향후 개발 계획

### 기능 고도화
| 항목 | 설명 |
|------|------|
| GPS 연동 | 위반 이벤트 발생 위치(위도/경도) 기록 및 지도 시각화 |
| 과속 감지 | GPS 속도 데이터 기반 제한속도 초과 감지 |
| 급제동 / 급가속 감지 | 가속도 센서 데이터 연동 |
| 실시간 스트리밍 분석 | 영상 파일 업로드 방식 → 실시간 카메라 스트림 분석으로 전환 |
| 푸시 알림 | 졸음 경보 / 분석 완료 시 모바일 푸시 알림 |

### 🤖 AI 모델 개선
| 항목 | 설명 |
|------|------|
| 모델 경량화 | 추론 속도 최적화 (TensorRT / ONNX 변환) |
| 눈 깜빡임 감지 추가 | EAR(Eye Aspect Ratio) 기반 눈 감음 지속 시간 분석 |
| 커스텀 데이터셋 파인튜닝 | 국내 도로 환경 특화 학습 |
| 분석 진행률 실시간 전달 | WebSocket 기반 분석 진행 상태 스트리밍 |

---

## 팀원

| 역할 | 담당 |
|------|------|
| Backend, AI | 송현수 (팀장) |
| Backend | 이세훈 |
| Frontend | 박명남 |
| Frontend | 오유민 |

---

## License

본 프로젝트는 한국공학대학교 캡스톤디자인 과제로 제작되었습니다.
