package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;

/*
 * 1회성 루틴 규칙
 * targetDate를 별도로 지정하지 않으면 루틴의 startDate 당일 1회만 실행된다.
 */
public class OneTimeRule implements RoutineRule {
    private final LocalDate targetDate;

    public OneTimeRule() {
        this.targetDate = null;
    }

    public OneTimeRule(LocalDate targetDate) {
        if (targetDate == null) {
            throw new IllegalArgumentException("지정 날짜는 null일 수 없습니다.");
        }
        this.targetDate = targetDate;
    }

    private LocalDate resolveDate(LocalDate startDate) {
        return targetDate != null ? targetDate : startDate;
    }

    @Override
    public boolean isDue(LocalDate startDate, LocalDate date) {
        LocalDate effectiveDate = resolveDate(startDate);
        return date != null && effectiveDate != null && date.equals(effectiveDate);
    }

    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek) {
        LocalDate effectiveDate = resolveDate(startDate);
        return effectiveDate != null && dayOfWeek != null && effectiveDate.getDayOfWeek() == dayOfWeek;
    }

    @Override
    public String describe() {
        return targetDate == null ? "1회성 (시작일)" : "1회성 (" + targetDate + ")";
    }
}
