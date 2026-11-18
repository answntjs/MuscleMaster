package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/*
 * 지정일 목록 반복 규칙
 */
public class SpecificDatesRule implements RoutineRule {
    private final Set<LocalDate> dates;

    public SpecificDatesRule(LocalDate... dates) {
        if (dates == null || dates.length == 0) {
            throw new IllegalArgumentException("최소 1개 이상의 날짜를 지정해야 합니다.");
        }
        this.dates = new TreeSet<>();
        for (LocalDate d : dates) {
            if (d == null) {
                throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
            }
            this.dates.add(d);
        }
    }

    public SpecificDatesRule(Collection<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 날짜를 지정해야 합니다.");
        }
        this.dates = new TreeSet<>();
        for (LocalDate d : dates) {
            if (d == null) {
                throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
            }
            this.dates.add(d);
        }
    }

    public Set<LocalDate> getDates() {
        return Collections.unmodifiableSet(dates);
    }

    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        if (targetDate == null) {
            return false;
        }
        if (startDate != null && targetDate.isBefore(startDate)) {
            return false;
        }
        return dates.contains(targetDate);
    }

    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            return false;
        }
        for (LocalDate d : dates) {
            if ((startDate == null || !d.isBefore(startDate)) && d.getDayOfWeek() == dayOfWeek) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String describe() {
        String dateText = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(", "));
        return "지정일 (" + dateText + ")";
    }
}
