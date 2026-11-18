package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;

/*
 * 횟수가 정해진 반복 규칙 (데코레이터)
 * 어떤 RoutineRule이든 감싸서 최대 maxCount회까지만 퀘스트를 부여하도록 제한한다.
 */
public class LimitedRule implements RoutineRule {
    private final RoutineRule baseRule;
    private final int maxCount;
    private int grantedCount;

    public LimitedRule(RoutineRule baseRule, int maxCount) {
        if (baseRule == null) {
            throw new IllegalArgumentException("기본 규칙은 null일 수 없습니다.");
        }
        if (maxCount < 1) {
            throw new IllegalArgumentException("최대 반복 횟수는 1회 이상이어야 합니다: " + maxCount);
        }
        this.baseRule = baseRule;
        this.maxCount = maxCount;
        this.grantedCount = 0;
    }

    public RoutineRule getBaseRule() {
        return baseRule;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getGrantedCount() {
        return grantedCount;
    }

    public int getRemainingCount() {
        return Math.max(0, maxCount - grantedCount);
    }

    public boolean isExhausted() {
        return grantedCount >= maxCount;
    }

    public void resetCount() {
        this.grantedCount = 0;
    }

    @Override
    public boolean isDue(LocalDate startDate, LocalDate targetDate) {
        return !isExhausted() && baseRule.isDue(startDate, targetDate);
    }

    @Override
    public boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek) {
        return !isExhausted() && baseRule.matchesDayOfWeek(startDate, dayOfWeek);
    }

    @Override
    public void onGranted(LocalDate date) {
        if (!isExhausted()) {
            grantedCount++;
            baseRule.onGranted(date);
        }
    }

    @Override
    public String describe() {
        return baseRule.describe() + " [최대 " + maxCount + "회, 남은 횟수 " + getRemainingCount() + "회]";
    }
}
