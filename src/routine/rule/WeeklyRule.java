package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * 매주 지정 요일 반복 규칙
 */
public class WeeklyRule implements RoutineRule {
    private final EnumSet<DayOfWeek> days;

    public WeeklyRule(DayOfWeek... days) {
        if (days == null || days.length == 0) {
            throw new IllegalArgumentException("최소 1개 이상의 요일을 지정해야 합니다.");
        }
        this.days = EnumSet.noneOf(DayOfWeek.class);
        Collections.addAll(this.days, days);
    }

    public WeeklyRule(Collection<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 요일을 지정해야 합니다.");
        }
        this.days = EnumSet.copyOf(days);
    }

    public Set<DayOfWeek> getDays() {
        return Collections.unmodifiableSet(days);
    }

    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        return startDate != null
                && targetDate != null
                && !targetDate.isBefore(startDate)
                && days.contains(targetDate.getDayOfWeek());
    }

    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek) {
        return dayOfWeek != null && days.contains(dayOfWeek);
    }

    @Override
    public String describe() {
        String dayNames = days.stream()
                .map(d -> d.getDisplayName(TextStyle.SHORT, Locale.KOREAN))
                .collect(Collectors.joining(", "));
        return "매주 (" + dayNames + ")";
    }
}
