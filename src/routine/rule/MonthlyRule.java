package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/*
 * 매달 지정 일자 반복 규칙
 */
public class MonthlyRule implements RoutineRule {
    private final Set<Integer> daysOfMonth;

    public MonthlyRule(int... daysOfMonth) {
        if (daysOfMonth == null || daysOfMonth.length == 0) {
            throw new IllegalArgumentException("최소 1개 이상의 일자를 지정해야 합니다.");
        }
        this.daysOfMonth = new TreeSet<>();
        for (int day : daysOfMonth) {
            validateDay(day);
            this.daysOfMonth.add(day);
        }
    }

    public MonthlyRule(Collection<Integer> daysOfMonth) {
        if (daysOfMonth == null || daysOfMonth.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 일자를 지정해야 합니다.");
        }
        this.daysOfMonth = new TreeSet<>();
        for (int day : daysOfMonth) {
            validateDay(day);
            this.daysOfMonth.add(day);
        }
    }

    private void validateDay(int day) {
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("일자는 1~31 사이여야 합니다: " + day);
        }
    }

    public Set<Integer> getDaysOfMonth() {
        return Collections.unmodifiableSet(daysOfMonth);
    }

    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        return startDate != null
                && targetDate != null
                && !targetDate.isBefore(startDate)
                && daysOfMonth.contains(targetDate.getDayOfMonth());
    }

    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek) {
        return false;
    }

    @Override
    public String describe() {
        String dayText = daysOfMonth.stream()
                .map(d -> d + "일")
                .collect(Collectors.joining(", "));
        return "매달 (" + dayText + ")";
    }
}
