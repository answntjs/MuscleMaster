package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;

/*
 * 매일 반복 규칙 (시작일 이후 매일)
 */
public class DailyRule implements RoutineRule {
    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        return startDate != null && targetDate != null && !targetDate.isBefore(startDate);
    }

    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek) {
        return dayOfWeek != null;
    }

    @Override
    public String describe() {
        return "매일";
    }
}
