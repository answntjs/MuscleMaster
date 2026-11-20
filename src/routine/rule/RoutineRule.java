package routine.rule;

import java.time.LocalDate;

/**
 * 루틴의 실행(퀘스트 부여) 여부를 결정하는 규칙 인터페이스입니다.
 * {@link routine.Routine}은 이 인터페이스를 필드로 보유하여 규칙을 런타임에 손쉽게 교체할 수 있습니다.
 */
public interface RoutineRule {
    /**
     * 시작일과 대상 날짜를 기준으로 해당 날짜에 루틴을 실행해야 하는지 판정합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param targetDate 실행 여부를 확인할 대상 날짜
     * @return 대상 날짜에 퀘스트를 부여해야 하면 true, 아니면 false
     */
    boolean isDue(LocalDate startDate, LocalDate targetDate);

    /**
     * 이 규칙이 지정한 요일에 실행될 수 있는지 확인합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param dayOfWeek 확인할 요일 ({@link common.DayOfWeek} 상수, 1~7)
     * @return 해당 요일 검색 조건과 일치하면 true, 아니면 false
     */
    boolean matchesDayOfWeek(LocalDate startDate, int dayOfWeek);

    /**
     * 특정 날짜에 실제로 퀘스트가 부여되었을 때 규칙 내부 상태를 갱신하기 위해 호출되는 콜백 메서드입니다.
     *
     * @param date 퀘스트가 부여된 날짜
     */
    default void onGranted(LocalDate date) {
    }

    /**
     * 정형화된 형태의 규칙 설명 문자열을 반환합니다.
     * 구현 클래스의 {@code toString()}도 동일한 문자열을 반환합니다.
     *
     * @return 정형화된 규칙 설명 문자열
     */
    String describe();
}
