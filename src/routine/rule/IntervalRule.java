package routine.rule;

import common.DayOfWeek;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 시작일 기준으로 지정된 일수 간격마다 반복 실행되는 루틴 규칙 구현체입니다.
 */
public class IntervalRule implements RoutineRule {
    /** 반복 간격 일수 (1 이상) */
    private final int intervalDays;

    /**
     * 지정된 일수 간격으로 반복 규칙을 생성합니다.
     *
     * @param intervalDays 반복 간격 일수 (1 이상)
     */
    public IntervalRule(int intervalDays) {
        if (intervalDays < 1) {
            throw new IllegalArgumentException("간격은 1일 이상이어야 합니다: " + intervalDays);
        }
        this.intervalDays = intervalDays;
    }

    /**
     * 설정된 반복 간격 일수를 반환합니다.
     *
     * @return 반복 간격 일수
     */
    public int getIntervalDays() {
        return intervalDays;
    }

    /**
     * 시작일로부터 대상 날짜까지의 일수 차이가 {@link #intervalDays}의 배수인지 판정합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param targetDate 실행 여부를 확인할 대상 날짜
     * @return 시작일 이후(당일 포함)이며 간격 주기에 해당하면 true, 아니면 false
     */
    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        if (startDate == null || targetDate == null || targetDate.isBefore(startDate)) {
            return false;
        }
        long diff = ChronoUnit.DAYS.between(startDate, targetDate);
        return diff % intervalDays == 0;
    }

    /**
     * 간격이 1일이거나 7의 배수인 경우 지정한 요일에 고정 실행되는지 확인합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param dayOfWeek 확인할 요일 ({@link DayOfWeek} 상수, 1~7)
     * @return 고정 요일 조건과 일치하면 true, 아니면 false
     */
    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, int dayOfWeek) {
        if (!DayOfWeek.isValid(dayOfWeek)) {
            return false;
        }
        if (intervalDays == 1) {
            return true;
        }
        return intervalDays % 7 == 0 && startDate != null && DayOfWeek.fromDate(startDate) == dayOfWeek;
    }

    /**
     * 반복 간격을 {@code "?일마다"} 형식의 문자열로 반환합니다.
     *
     * @return 예: {@code "3일마다"} 형태의 설명 문자열
     */
    @Override
    public String describe() {
        return intervalDays + "일마다";
    }

    /**
     * 정형화된 간격 반복 규칙 문자열을 반환합니다.
     *
     * @return {@link #describe()} 결과 문자열
     */
    @Override
    public String toString() {
        return describe();
    }
}
