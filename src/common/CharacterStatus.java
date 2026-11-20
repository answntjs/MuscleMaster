package common;

/**
 * 플레이어(캐릭터)의 상태 상수 및 상태별 경험치 배율을 제공하는 유틸리티 클래스입니다.
 */
public class CharacterStatus {
    /** 기본(보통) 상태 상수 */
    public static final String NORMAL = "보통";

    /** 연속 달성 조건을 만족한 열정 상태 상수 */
    public static final String ON_FIRE = "열정";

    /** 루틴 실패로 인한 슬럼프(페널티) 상태 상수 */
    public static final String PENALTY = "슬럼프";

    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private CharacterStatus() {
    }

    /**
     * 캐릭터 상태에 따른 경험치 획득 배율을 반환합니다.
     *
     * @param status 캐릭터의 현재 상태 문자열
     * @return 상태에 대응하는 경험치 배율 (열정: 1.15, 슬럼프: 0.8, 그 외: 1.0)
     */
    public static double getExpMultiplier(String status) {
        if (ON_FIRE.equals(status)) {
            return 1.15;
        }
        if (PENALTY.equals(status)) {
            return 0.8;
        }
        return 1.0;
    }
}
