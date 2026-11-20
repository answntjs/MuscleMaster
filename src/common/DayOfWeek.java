package common;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

/**
 * 요일 상수(월요일=1 ~ 일요일=7) 및 요일 변환 유틸리티를 제공하는 클래스입니다.
 */
public class DayOfWeek {
    /** 월요일 상수 (1) */
    public static final int MONDAY = 1;

    /** 화요일 상수 (2) */
    public static final int TUESDAY = 2;

    /** 수요일 상수 (3) */
    public static final int WEDNESDAY = 3;

    /** 목요일 상수 (4) */
    public static final int THURSDAY = 4;

    /** 금요일 상수 (5) */
    public static final int FRIDAY = 5;

    /** 토요일 상수 (6) */
    public static final int SATURDAY = 6;

    /** 일요일 상수 (7) */
    public static final int SUNDAY = 7;

    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private DayOfWeek() {
    }

    /**
     * 전달된 정수가 유효한 요일 상수(1~7)인지 확인합니다.
     *
     * @param dayOfWeek 확인할 요일 정수 값
     * @return 1(월요일) 이상 7(일요일) 이하이면 true
     */
    public static boolean isValid(int dayOfWeek) {
        return dayOfWeek >= MONDAY && dayOfWeek <= SUNDAY;
    }

    /**
     * {@link LocalDate}에서 ISO-8601 기준 요일 상수(1~7)를 추출합니다.
     *
     * @param date 대상 날짜 (null 불가)
     * @return 해당 날짜의 요일 상수 (1~7)
     */
    public static int fromDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
        }
        return date.get(ChronoField.DAY_OF_WEEK);
    }

    /**
     * 요일 상수를 한국어 약칭({@code "월"}~{@code "일"})으로 변환합니다.
     *
     * @param dayOfWeek 요일 상수 (1~7)
     * @return 한국어 요일 한 글자 문자열
     */
    public static String toKoreanShort(int dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
            default -> throw new IllegalArgumentException("올바르지 않은 요일 값입니다: " + dayOfWeek);
        };
    }

    /**
     * 한국어 요일 약칭({@code "월"}~{@code "일"})을 요일 상수(1~7)로 변환합니다.
     *
     * @param token 한국어 요일 문자열
     * @return 해당하는 요일 상수 (1~7)
     */
    public static int parseKorean(String token) {
        if (token == null) {
            throw new IllegalArgumentException("요일 문자열은 null일 수 없습니다.");
        }
        return switch (token) {
            case "월" -> MONDAY;
            case "화" -> TUESDAY;
            case "수" -> WEDNESDAY;
            case "목" -> THURSDAY;
            case "금" -> FRIDAY;
            case "토" -> SATURDAY;
            case "일" -> SUNDAY;
            default -> throw new IllegalArgumentException("알 수 없는 요일입니다: " + token);
        };
    }

    /**
     * 요일 상수를 저장 파일용 영문 코드({@code "MONDAY"}~{@code "SUNDAY"})로 변환합니다.
     *
     * @param dayOfWeek 요일 상수 (1~7)
     * @return 영문 요일 코드
     */
    public static String toCode(int dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MONDAY";
            case TUESDAY -> "TUESDAY";
            case WEDNESDAY -> "WEDNESDAY";
            case THURSDAY -> "THURSDAY";
            case FRIDAY -> "FRIDAY";
            case SATURDAY -> "SATURDAY";
            case SUNDAY -> "SUNDAY";
            default -> throw new IllegalArgumentException("올바르지 않은 요일 값입니다: " + dayOfWeek);
        };
    }

    /**
     * 저장 파일용 영문 요일 코드 또는 숫자 문자열을 요일 상수(1~7)로 변환합니다.
     *
     * @param code 요일 코드 문자열
     * @return 해당하는 요일 상수 (1~7)
     */
    public static int fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("요일 코드가 비어 있습니다.");
        }
        return switch (code.trim().toUpperCase()) {
            case "MONDAY", "1" -> MONDAY;
            case "TUESDAY", "2" -> TUESDAY;
            case "WEDNESDAY", "3" -> WEDNESDAY;
            case "THURSDAY", "4" -> THURSDAY;
            case "FRIDAY", "5" -> FRIDAY;
            case "SATURDAY", "6" -> SATURDAY;
            case "SUNDAY", "7" -> SUNDAY;
            default -> throw new IllegalArgumentException("알 수 없는 요일 코드입니다: " + code);
        };
    }
}
