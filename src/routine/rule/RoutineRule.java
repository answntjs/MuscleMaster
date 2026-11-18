package routine.rule;

import java.time.DayOfWeek;
import java.time.LocalDate;

/*
 * 루틴의 실행(퀘스트 부여) 규칙 인터페이스
 * Routine은 이 인터페이스를 필드로 보유하여 규칙을 런타임에 손쉽게 교체할 수 있다.
 */
public interface RoutineRule {
    boolean isDue(LocalDate startDate, LocalDate targetDate);

    boolean matchesDayOfWeek(LocalDate startDate, DayOfWeek dayOfWeek);

    default void onGranted(LocalDate date) {
    }

    String describe();
}
