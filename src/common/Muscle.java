package common;

import java.util.HashMap;

/*
* 근육 및 신체부위에 대한 정보 및 도구를 모아두는 class
* 객체 선언 없이도 쓸 수 있도록 할 것을 권장
*
* */

public class Muscle {
    public static final String[] MUSCLES = {
            "대흉근", "소흉근", "전거근",
            "광배근", "승모근", "능형근", "척추기립근",
            "삼각근", "회전근개",
            "상완이두근", "상완근", "상완삼두근", "전완근",
            "복직근", "외복사근", "내복사근", "복횡근",
            "대퇴사두근", "햄스트링", "둔근", "하퇴근"
    };

    public static final String[] BODYPARTS = {
            "가슴", "등", "어깨", "팔", "코어", "복부", "하체", "둔부"
    };

    public static final HashMap<String, String> MUSCLE_TO_BODY = createMuscleMap();
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
