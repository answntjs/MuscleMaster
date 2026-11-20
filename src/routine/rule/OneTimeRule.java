package routine.rule;

import common.DayOfWeek;

import java.time.LocalDate;

/**
 * 단 한 번만 실행되는 1회성 루틴 규칙 구현체입니다.
 * {@link #targetDate}를 별도로 지정하지 않으면 루틴의 시작일 당일 1회만 실행됩니다.
 */
public class OneTimeRule implements RoutineRule {
    /** 별도로 지정된 1회 실행 날짜 (null이면 루틴 시작일 사용) */
    private final LocalDate targetDate;

    /**
     * 루틴의 시작일 당일에 1회 실행되도록 규칙을 생성합니다.
     */
    public OneTimeRule() {
        this.targetDate = null;
    }

    /**
     * 지정된 날짜에 1회 실행되도록 규칙을 생성합니다.
     *
     * @param targetDate 실행할 특정 날짜 (null 불가)
     */
    public OneTimeRule(LocalDate targetDate) {
        if (targetDate == null) {
            throw new IllegalArgumentException("지정 날짜는 null일 수 없습니다.");
        }
        this.targetDate = targetDate;
    }

    /**
     * 별도로 지정된 1회 실행 날짜를 반환합니다.
     *
     * @return 지정된 실행 날짜 (루틴 시작일을 사용하는 경우 null)
     */
    public LocalDate getTargetDate() {
        return targetDate;
    }

    /**
     * 실제 적용될 1회 실행 기준 날짜를 계산합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @return targetDate가 지정되었으면 targetDate, 아니면 startDate
     */
    private LocalDate resolveDate(LocalDate startDate) {
        return targetDate != null ? targetDate : startDate;
    }

    /**
     * 대상 날짜가 실제 1회 실행 날짜와 일치하는지 판정합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param date 실행 여부를 확인할 대상 날짜
     * @return 실행 기준 날짜와 일치하면 true, 아니면 false
     */
    @Override
    public boolean isDue(LocalDate startDate, LocalDate date) {
        LocalDate effectiveDate = resolveDate(startDate);
        return date != null && effectiveDate != null && date.equals(effectiveDate);
    }

    /**
     * 1회 실행 날짜의 요일이 지정한 요일과 일치하는지 확인합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param dayOfWeek 확인할 요일 ({@link DayOfWeek} 상수, 1~7)
     * @return 실행 날짜의 요일이 일치하면 true, 아니면 false
     */
    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, int dayOfWeek) {
        LocalDate effectiveDate = resolveDate(startDate);
        return effectiveDate != null && DayOfWeek.isValid(dayOfWeek) && DayOfWeek.fromDate(effectiveDate) == dayOfWeek;
    }

    /**
     * 1회성 규칙의 정형화된 설명 문자열을 반환합니다.
     *
     * @return {@code "한번만"} 또는 지정 날짜가 포함된 {@code "한번만 (YYYY-MM-DD)"} 형태의 설명 문자열
     */
    @Override
    public String describe() {
        return targetDate == null ? "한번만" : "한번만 (" + targetDate + ")";
    }

    /**
     * 정형화된 1회성 규칙 문자열을 반환합니다.
     *
     * @return {@link #describe()} 결과 문자열
     */
    @Override
    public String toString() {
        return describe();
    }
}
