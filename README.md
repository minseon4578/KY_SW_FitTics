# BodySync

AI 기반 개인 맞춤 건강관리 애플리케이션

## 프로젝트 소개

BodySync는 사용자의 신체 정보를 바탕으로 건강 상태를 분석하고, 맞춤형 식단 및 운동 계획을 제공하는 Android 애플리케이션입니다.

BMI, BMR, 체지방률 계산을 통해 현재 건강 상태를 분석하며, AI 기반 운동 추천 기능과 개인 맞춤 식단 추천 기능을 통해 사용자의 건강 관리를 지원합니다.

---

## 개발 목적

현대인들은 자신의 건강 상태를 정확하게 파악하기 어렵고, 인터넷에 존재하는 획일적인 운동 및 식단 정보를 그대로 따라가는 경우가 많습니다.

BodySync는 사용자의 신체 데이터를 기반으로 개인화된 건강 관리 정보를 제공하여 보다 효율적이고 지속 가능한 건강 관리를 지원하는 것을 목표로 합니다.

---

## 주요 기능

### 1. 신체 정보 분석

* 키, 몸무게, 나이, 성별 입력
* BMI 계산
* BMR(기초대사량) 계산
* 체지방률 추정
* 건강 상태 분류

### 2. 활동량 기반 칼로리 분석

* 활동 수준 선택
* TDEE 계산
* 감량/유지/증량 목표 칼로리 제공

### 3. 맞춤형 식단 추천

* BMI 기반 목표 자동 설정
* 감량/유지/증량 식단 추천
* 밥 양 조절 기능
* 단백질 보충 옵션
* 칼로리 및 단백질 목표 제공

### 4. AI 운동 추천

* Gemini AI 활용
* 사용자 신체 데이터 기반 운동 추천
* 난이도별 운동 계획 제공
* 운동 소모 칼로리 계산

### 5. 건강 기록 관리

* 식사 기록
* 운동 기록
* BMI 변화 추적
* 건강 리포트 제공

### 6. 데이터 시각화

* BMI 변화 그래프
* 건강 상태 변화 확인
* 기록 기반 통계 제공

---

## 기술 스택

### Frontend

* Kotlin
* Jetpack Compose
* Material Design 3

### AI

* Google Gemini API

### Architecture

* Compose UI
* State Management
* Domain Layer 분리 구조

---

## 프로젝트 구조

app
┣ component
┃ ┣ HealthCard
┃ ┣ AppHeader
┃ ┣ BottomNavBar
┃ ┗ BmiLineChart
┣ domain
┃ ┣ BmiCalculator
┃ ┣ BmiRecord
┃ ┗ ActivityLevel
┗ screen
┣ CalculatorTab
┣ DietTab
┣ ExerciseTab
┣ HistoryTab
┗ HealthFitnessScreen

---

## 기대 효과

* 개인 맞춤형 건강 관리 지원
* 건강 데이터 기반 의사결정
* 식습관 및 운동 습관 개선
* 지속적인 건강 상태 추적

---

## 향후 개선 계획

* Firebase 연동
* 사용자 계정 기능
* 건강 데이터 클라우드 저장
* AI 식단 생성 기능 고도화
* 운동 영상 추천 기능
* 체성분 분석 연동
* Wear OS 및 스마트워치 연동

---

## 개발자

창의설계 1분반 5조
