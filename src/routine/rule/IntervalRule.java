package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*
 * N일 간격 반복 규칙
 */
public class IntervalRule implements RoutineRule {
    private final int intervalDays;

    public IntervalRule(int intervalDays) {
        if (intervalDays < 1) {
            throw new IllegalArgumentException("간격은 1일 이상이어야 합니다: " + intervalDays);
        }
        this.intervalDays = intervalDays;
    }

    public int getIntervalDays() {
        return intervalDays;
    }

    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        if (startDate == null || targetDate == null || targetDate.isBefore(startDate)) {
            return false;
        }
        long diff = ChronoUnit.DAYS.between(startDate, targetDate);
        return diff % intervalDays == 0;
    }

    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            return false;
        }
        if (intervalDays == 1) {
            return true;
        }
        return intervalDays % 7 == 0 && startDate != null && startDate.getDayOfWeek() == dayOfWeek;
    }

    @Override
    public String describe() {
        return intervalDays + "일 간격";
    }
}
