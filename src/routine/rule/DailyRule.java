package routine.rule;

import common.DayOfWeek;

import java.time.LocalDate;

/**
 * 시작일 이후 매일 반복 실행되는 루틴 규칙 구현체입니다.
 */
public class DailyRule implements RoutineRule {
    /**
     * 매일 반복 규칙 인스턴스를 생성합니다.
     */
    public DailyRule() {
    }

    /**
     * 대상 날짜가 시작일과 같거나 그 이후인지 확인합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param targetDate 실행 여부를 확인할 대상 날짜
     * @return 시작일과 대상 날짜가 null이 아니고 대상 날짜가 시작일 이후(당일 포함)이면 true
     */
    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        return startDate != null && targetDate != null && !targetDate.isBefore(startDate);
    }

    /**
     * 매일 반복 규칙이므로 유효한 모든 요일(1~7)과 일치합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param dayOfWeek 확인할 요일 ({@link DayOfWeek} 상수, 1~7)
     * @return 유효한 요일 상수이면 true
     */
    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, int dayOfWeek) {
        return DayOfWeek.isValid(dayOfWeek);
    }

    /**
     * 규칙 설명 문자열({@code "매일"})을 반환합니다.
     *
     * @return {@code "매일"} 문자열
     */
    @Override
    public String describe() {
        return "매일";
    }

    /**
     * 정형화된 규칙 문자열({@code "매일"})을 반환합니다.
     *
     * @return {@link #describe()} 결과 문자열
     */
    @Override
    public String toString() {
        return describe();
    }
}
