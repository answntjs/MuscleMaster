package common;

import quest.RoutineQuest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 대중적인 운동 템플릿 상수 및 조회/복사 도구를 모아두는 유틸리티 클래스입니다.
 * {@link CharacterStatus}, {@link Muscle}과 동일하게 객체 선언 없이 상수 및 정적 메서드로 사용합니다.
 */
public class ExerciseTemplate {
    /** 팔굽혀펴기 운동 템플릿 상수 (3세트, EXP 60) */
    public static final RoutineQuest PUSH_UP = new RoutineQuest("팔굽혀펴기", 3, 60, "대흉근", "소흉근", "전거근", "삼각근", "상완삼두근");

    /** 벤치프레스 운동 템플릿 상수 (4세트, EXP 90) */
    public static final RoutineQuest BENCH_PRESS = new RoutineQuest("벤치프레스", 4, 90, "대흉근", "소흉근", "삼각근", "상완삼두근");

    /** 딥스 운동 템플릿 상수 (3세트, EXP 75) */
    public static final RoutineQuest DIPS = new RoutineQuest("딥스", 3, 75, "대흉근", "소흉근", "삼각근", "상완삼두근");

    /** 풀업 운동 템플릿 상수 (3세트, EXP 85) */
    public static final RoutineQuest PULL_UP = new RoutineQuest("풀업", 3, 85, "광배근", "승모근", "능형근", "상완이두근", "전완근");

    /** 랫풀다운 운동 템플릿 상수 (4세트, EXP 75) */
    public static final RoutineQuest LAT_PULLDOWN = new RoutineQuest("랫풀다운", 4, 75, "광배근", "승모근", "능형근", "상완이두근");

    /** 바벨 로우 운동 템플릿 상수 (4세트, EXP 85) */
    public static final RoutineQuest BARBELL_ROW = new RoutineQuest("바벨 로우", 4, 85, "광배근", "승모근", "능형근", "척추기립근", "상완이두근");

    /** 데드리프트 운동 템플릿 상수 (4세트, EXP 100) */
    public static final RoutineQuest DEADLIFT = new RoutineQuest("데드리프트", 4, 100, "척추기립근", "광배근", "승모근", "둔근", "햄스트링", "전완근");

    /** 오버헤드 프레스 운동 템플릿 상수 (4세트, EXP 80) */
    public static final RoutineQuest OVERHEAD_PRESS = new RoutineQuest("오버헤드 프레스", 4, 80, "삼각근", "회전근개", "상완삼두근", "복횡근");

    /** 사이드 레터럴 레이즈 운동 템플릿 상수 (3세트, EXP 50) */
    public static final RoutineQuest SIDE_LATERAL_RAISE = new RoutineQuest("사이드 레터럴 레이즈", 3, 50, "삼각근", "회전근개");

    /** 덤벨 컬 운동 템플릿 상수 (3세트, EXP 45) */
    public static final RoutineQuest DUMBBELL_CURL = new RoutineQuest("덤벨 컬", 3, 45, "상완이두근", "상완근", "전완근");

    /** 트라이셉스 익스텐션 운동 템플릿 상수 (3세트, EXP 45) */
    public static final RoutineQuest TRICEPS_EXTENSION = new RoutineQuest("트라이셉스 익스텐션", 3, 45, "상완삼두근");

    /** 윗몸일으키기 운동 템플릿 상수 (3세트, EXP 50) */
    public static final RoutineQuest SIT_UP = new RoutineQuest("윗몸일으키기", 3, 50, "복직근", "외복사근", "내복사근");

    /** 크런치 운동 템플릿 상수 (3세트, EXP 45) */
    public static final RoutineQuest CRUNCH = new RoutineQuest("크런치", 3, 45, "복직근", "외복사근");

    /** 레그 레이즈 운동 템플릿 상수 (3세트, EXP 55) */
    public static final RoutineQuest LEG_RAISE = new RoutineQuest("레그 레이즈", 3, 55, "복직근", "복횡근");

    /** 플랭크 운동 템플릿 상수 (3세트, EXP 55) */
    public static final RoutineQuest PLANK = new RoutineQuest("플랭크", 3, 55, "복횡근", "복직근", "외복사근", "척추기립근");

    /** 러시안 트위스트 운동 템플릿 상수 (3세트, EXP 50) */
    public static final RoutineQuest RUSSIAN_TWIST = new RoutineQuest("러시안 트위스트", 3, 50, "외복사근", "내복사근", "복횡근");

    /** 스쿼트 운동 템플릿 상수 (4세트, EXP 90) */
    public static final RoutineQuest SQUAT = new RoutineQuest("스쿼트", 4, 90, "대퇴사두근", "햄스트링", "둔근", "척추기립근");

    /** 런지 운동 템플릿 상수 (3세트, EXP 70) */
    public static final RoutineQuest LUNGE = new RoutineQuest("런지", 3, 70, "대퇴사두근", "햄스트링", "둔근");

    /** 힙 쓰러스트 운동 템플릿 상수 (3세트, EXP 70) */
    public static final RoutineQuest HIP_THRUST = new RoutineQuest("힙 쓰러스트", 3, 70, "둔근", "햄스트링");

    /** 카프 레이즈 운동 템플릿 상수 (3세트, EXP 40) */
    public static final RoutineQuest CALF_RAISE = new RoutineQuest("카프 레이즈", 3, 40, "하퇴근");

    /** 버피 운동 템플릿 상수 (3세트, EXP 85) */
    public static final RoutineQuest BURPEE = new RoutineQuest("버피", 3, 85, "대흉근", "삼각근", "복횡근", "대퇴사두근", "둔근", "하퇴근");

    /** 마운틴 클라이머 운동 템플릿 상수 (3세트, EXP 65) */
    public static final RoutineQuest MOUNTAIN_CLIMBER = new RoutineQuest("마운틴 클라이머", 3, 65, "삼각근", "복직근", "복횡근", "대퇴사두근");

    /** 전체 기본 운동 템플릿 상수 배열 */
    public static final RoutineQuest[] TEMPLATES = {
            PUSH_UP, BENCH_PRESS, DIPS,
            PULL_UP, LAT_PULLDOWN, BARBELL_ROW, DEADLIFT,
            OVERHEAD_PRESS, SIDE_LATERAL_RAISE,
            DUMBBELL_CURL, TRICEPS_EXTENSION,
            SIT_UP, CRUNCH, LEG_RAISE, PLANK, RUSSIAN_TWIST,
            SQUAT, LUNGE, HIP_THRUST, CALF_RAISE,
            BURPEE, MOUNTAIN_CLIMBER
    };

    /** 운동명(key)을 해당 템플릿 {@link RoutineQuest}(value)로 매핑하는 맵 */
    public static final HashMap<String, RoutineQuest> NAME_TO_TEMPLATE = createTemplateMap();

    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private ExerciseTemplate() {
    }

    /**
     * 운동명으로 템플릿을 빠르게 조회할 수 있는 맵을 초기화합니다.
     *
     * @return 운동명(key)과 템플릿 {@link RoutineQuest}(value)를 담은 HashMap
     */
    private static HashMap<String, RoutineQuest> createTemplateMap() {
        HashMap<String, RoutineQuest> map = new HashMap<>();
        for (RoutineQuest template : TEMPLATES) {
            map.put(template.getName(), template);
        }
        return map;
    }

    /**
     * 지정한 운동 템플릿의 독립적인 복사본을 생성합니다.
     *
     * @param template 복사할 원본 {@link RoutineQuest} 템플릿
     * @return 원본과 동일한 속성을 가진 새로운 {@link RoutineQuest} 인스턴스
     * @throws IllegalArgumentException template이 null인 경우
     */
    public static RoutineQuest copy(RoutineQuest template) {
        if (template == null) {
            throw new IllegalArgumentException("운동 템플릿은 null일 수 없습니다.");
        }
        return new RoutineQuest(template);
    }

    /**
     * 지정한 운동 템플릿을 복사하면서 목표 세트 수를 변경한 인스턴스를 생성합니다.
     *
     * @param template 복사할 원본 {@link RoutineQuest} 템플릿
     * @param goalSet 새로 설정할 목표 세트 수 (1 이상)
     * @return 목표 세트 수가 변경된 새로운 {@link RoutineQuest} 인스턴스
     * @throws IllegalArgumentException template이 null이거나 goalSet이 1 미만인 경우
     */
    public static RoutineQuest copy(RoutineQuest template, int goalSet) {
        RoutineQuest copied = copy(template);
        if (goalSet <= 0) {
            throw new IllegalArgumentException("목표 세트 수는 1 이상이어야 합니다: " + goalSet);
        }
        copied.setGoalSet(goalSet);
        return copied;
    }

    /**
     * 지정한 운동 템플릿을 복사하면서 목표 세트 수와 경험치를 변경한 인스턴스를 생성합니다.
     *
     * @param template 복사할 원본 {@link RoutineQuest} 템플릿
     * @param goalSet 새로 설정할 목표 세트 수 (1 이상)
     * @param exp 새로 설정할 완료 보상 경험치 (0 이상)
     * @return 목표 세트 수와 경험치가 변경된 새로운 {@link RoutineQuest} 인스턴스
     * @throws IllegalArgumentException template이 null이거나 goalSet이 1 미만, 또는 exp가 0 미만인 경우
     */
    public static RoutineQuest copy(RoutineQuest template, int goalSet, int exp) {
        RoutineQuest copied = copy(template, goalSet);
        if (exp < 0) {
            throw new IllegalArgumentException("경험치는 0 이상이어야 합니다: " + exp);
        }
        copied.setExp(exp);
        return copied;
    }

    /**
     * 운동명과 정확히 일치하는 템플릿을 찾아 복사본을 반환합니다.
     *
     * @param name 조회할 운동명
     * @return 일치하는 템플릿의 {@link RoutineQuest} 복사본, 없거나 비어 있으면 null
     */
    public static RoutineQuest findByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        RoutineQuest found = NAME_TO_TEMPLATE.get(name.trim());
        return found != null ? new RoutineQuest(found) : null;
    }

    /**
     * 운동명, 자극 부위, 또는 자극 근육에 키워드가 포함된 템플릿 목록을 검색해 복사본 리스트로 반환합니다.
     *
     * @param keyword 검색할 키워드 (null이거나 공백이면 전체 템플릿 반환)
     * @return 검색 조건에 일치하는 {@link RoutineQuest} 복사본 목록
     */
    public static List<RoutineQuest> searchByKeyword(String keyword) {
        List<RoutineQuest> result = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            for (RoutineQuest template : TEMPLATES) {
                result.add(new RoutineQuest(template));
            }
            return result;
        }
        String normalized = keyword.trim().toLowerCase();
        for (RoutineQuest template : TEMPLATES) {
            if (template.getName().toLowerCase().contains(normalized)
                    || template.getParts().stream().anyMatch(p -> p.toLowerCase().contains(normalized))
                    || template.getMuscles().stream().anyMatch(m -> m.toLowerCase().contains(normalized))) {
                result.add(new RoutineQuest(template));
            }
        }
        return result;
    }

    /**
     * 특정 신체 부위를 자극하는 운동 템플릿 목록을 복사본 리스트로 반환합니다.
     *
     * @param bodyPart 조회할 신체 부위명 (예: "가슴", "하체")
     * @return 해당 부위를 포함하는 {@link RoutineQuest} 복사본 목록
     */
    public static List<RoutineQuest> getByBodyPart(String bodyPart) {
        List<RoutineQuest> result = new ArrayList<>();
        if (bodyPart == null || bodyPart.isBlank()) {
            return result;
        }
        String target = bodyPart.trim();
        for (RoutineQuest template : TEMPLATES) {
            if (template.getParts().contains(target)) {
                result.add(new RoutineQuest(template));
            }
        }
        return result;
    }

    /**
     * 특정 근육을 자극하는 운동 템플릿 목록을 복사본 리스트로 반환합니다.
     *
     * @param muscle 조회할 근육명 (예: "대흉근", "대퇴사두근")
     * @return 해당 근육을 포함하는 {@link RoutineQuest} 복사본 목록
     */
    public static List<RoutineQuest> getByMuscle(String muscle) {
        List<RoutineQuest> result = new ArrayList<>();
        if (muscle == null || muscle.isBlank()) {
            return result;
        }
        String target = muscle.trim();
        for (RoutineQuest template : TEMPLATES) {
            if (template.getMuscles().contains(target)) {
                result.add(new RoutineQuest(template));
            }
        }
        return result;
    }
}
