package common;

import java.util.HashMap;

/**
 * 근육 및 신체 부위에 대한 상수와 매핑 정보를 제공하는 유틸리티 클래스입니다.
 * 객체 생성 없이 정적 필드와 도구를 사용할 수 있도록 설계되었습니다.
 */
public class Muscle {
    /** 지원하는 전체 근육 이름 목록 */
    public static final String[] MUSCLES = {
            "대흉근", "소흉근", "전거근",
            "광배근", "승모근", "능형근", "척추기립근",
            "삼각근", "회전근개",
            "상완이두근", "상완근", "상완삼두근", "전완근",
            "복직근", "외복사근", "내복사근", "복횡근",
            "대퇴사두근", "햄스트링", "둔근", "하퇴근"
    };

    /** 지원하는 전체 신체 부위 이름 목록 */
    public static final String[] BODYPARTS = {
            "가슴", "등", "어깨", "팔", "코어", "복부", "하체", "둔부"
    };

    /** 근육명을 해당 신체 부위명으로 매핑하는 맵 */
    public static final HashMap<String, String> MUSCLE_TO_BODY = createMuscleMap();

    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private Muscle() {
    }

    /**
     * 근육명과 신체 부위명을 연결한 초기 매핑 테이블을 생성합니다.
     *
     * @return 근육명(key)을 신체 부위명(value)으로 매핑한 HashMap
     */
    private static HashMap<String, String> createMuscleMap() {
        HashMap<String, String> map = new HashMap<>();
        // 가슴
        map.put("대흉근", "가슴");
        map.put("소흉근", "가슴");
        map.put("전거근", "가슴");

        // 등
        map.put("광배근", "등");
        map.put("승모근", "등");
        map.put("능형근", "등");
        map.put("척추기립근", "등");

        // 어깨
        map.put("삼각근", "어깨");
        map.put("회전근개", "어깨");

        // 팔
        map.put("상완이두근", "팔");
        map.put("상완근", "팔");
        map.put("상완삼두근", "팔");
        map.put("전완근", "팔");

        // 복부
        map.put("복직근", "복부");
        map.put("외복사근", "복부");
        map.put("내복사근", "복부");

        // 코어
        map.put("복횡근", "코어");

        // 하체
        map.put("대퇴사두근", "하체");
        map.put("햄스트링", "하체");
        map.put("하퇴근", "하체");

        // 둔부
        map.put("둔근", "둔부");

        return map;
    }
}
