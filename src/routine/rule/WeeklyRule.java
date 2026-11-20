package routine.rule;

import common.DayOfWeek;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 매주 지정된 요일마다 반복 실행되는 루틴 규칙 구현체입니다.
 */
public class WeeklyRule implements RoutineRule {
    /** 매주 실행할 요일 집합 ({@link DayOfWeek} 정수 상수 1~7) */
    private final Set<Integer> days;

    /**
     * 가변 인자로 전달된 요일 목록으로 매주 반복 규칙을 생성합니다.
     *
     * @param days 반복 실행할 요일 상수 목록 ({@link DayOfWeek}, 최소 1개 이상)
     */
    public WeeklyRule(int... days) {
        if (days == null || days.length == 0) {
            throw new IllegalArgumentException("최소 1개 이상의 요일을 지정해야 합니다.");
        }
        this.days = new TreeSet<>();
        for (int day : days) {
            if (!DayOfWeek.isValid(day)) {
                throw new IllegalArgumentException("올바르지 않은 요일 값입니다: " + day);
            }
            this.days.add(day);
        }
    }

    /**
     * 컬렉션으로 전달된 요일 목록으로 매주 반복 규칙을 생성합니다.
     *
     * @param days 반복 실행할 요일 상수 컬렉션 ({@link DayOfWeek}, 비어 있을 수 없음)
     */
    public WeeklyRule(Collection<Integer> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 요일을 지정해야 합니다.");
        }
        this.days = new TreeSet<>();
        for (Integer day : days) {
            if (day == null || !DayOfWeek.isValid(day)) {
                throw new IllegalArgumentException("올바르지 않은 요일 값입니다: " + day);
            }
            this.days.add(day);
        }
    }

    /**
     * 설정된 반복 요일 집합을 수정 불가능한 형태로 반환합니다.
     *
     * @return 수정 불가능한 요일 상수 집합
     */
    public Set<Integer> getDays() {
        return Collections.unmodifiableSet(days);
    }

    /**
     * 대상 날짜가 시작일 이후(당일 포함)이며 요일이 {@link #days}에 포함되는지 판정합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param targetDate 실행 여부를 확인할 대상 날짜
     * @return 지정 요일에 해당하면 true, 아니면 false
     */
    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        return startDate != null
                && targetDate != null
                && !targetDate.isBefore(startDate)
                && days.contains(DayOfWeek.fromDate(targetDate));
    }

    /**
     * 지정한 요일이 반복 대상 요일에 포함되는지 확인합니다.
     *
     * @param startDate 루틴 시작 기준일
     * @param dayOfWeek 확인할 요일 ({@link DayOfWeek} 상수, 1~7)
     * @return 해당 요일이 포함되어 있으면 true, 아니면 false
     */
    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, int dayOfWeek) {
        return DayOfWeek.isValid(dayOfWeek) && days.contains(dayOfWeek);
    }

    /**
     * 매주 반복 요일을 {@code "매주 ?요일"} 형식의 문자열로 반환합니다.
     *
     * @return 예: {@code "매주 월, 수, 금요일"} 또는 {@code "매주 월요일"} 형태의 설명 문자열
     */
    @Override
    public String describe() {
        String dayNames = days.stream()
                .map(DayOfWeek::toKoreanShort)
                .collect(Collectors.joining(", "));
        return "매주 " + dayNames + "요일";
    }

    /**
     * 정형화된 매주 반복 규칙 문자열을 반환합니다.
     *
     * @return {@link #describe()} 결과 문자열
     */
    @Override
    public String toString() {
        return describe();
    }
}
