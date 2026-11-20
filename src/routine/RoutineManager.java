package routine;

import cli.RoutineMenu;
import common.DayOfWeek;
import quest.Quest;
import routine.rule.RoutineRule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 사용자의 루틴 목록을 통합 관리하는 매니저 클래스입니다.
 * 루틴 등록/조회/수정/삭제 및 이름, 요일, 부위별 검색과 일자별 퀘스트 일괄 발급을 담당합니다.
 */
public class RoutineManager {
    /** 루틴 ID를 키로 하여 등록 순서를 유지하며 보관하는 루틴 맵 */
    private final Map<Integer, Routine> routines = new LinkedHashMap<>();

    /** 다음 루틴 등록 시 할당할 자동 증가 ID */
    private int nextId = 1;

    /**
     * 비어 있는 루틴 매니저를 생성합니다.
     */
    public RoutineManager() {
    }

    /**
     * ID에 해당하는 루틴을 조회합니다.
     *
     * @param id 조회할 루틴 ID
     * @return 해당 ID의 {@link Routine}, 존재하지 않으면 null
     */
    public Routine getRoutine(int id) {
        return routines.get(id);
    }

    /**
     * 등록된 모든 루틴 목록을 반환합니다.
     *
     * @return 전체 {@link Routine} 목록
     */
    public List<Routine> getAllRoutines() {
        return new ArrayList<>(routines.values());
    }

    /**
     * 현재 활성 상태인 루틴만 필터링하여 반환합니다.
     *
     * @return 활성화된 {@link Routine} 목록
     */
    public List<Routine> getActiveRoutines() {
        List<Routine> result = new ArrayList<>();
        for (Routine routine : routines.values()) {
            if (routine.isActive()) {
                result.add(routine);
            }
        }
        return result;
    }

    /**
     * 새 루틴을 매니저에 등록하고 고유 ID를 부여합니다.
     *
     * @param routine 등록할 {@link Routine} (null 불가)
     * @return ID가 할당되어 등록된 {@link Routine}
     */
    public Routine addRoutine(Routine routine) {
        if (routine == null) {
            throw new IllegalArgumentException("루틴은 null일 수 없습니다.");
        }
        routine.setId(nextId++);
        routines.put(routine.getId(), routine);
        return routine;
    }

    /**
     * 지정한 ID의 루틴을 삭제합니다.
     *
     * @param id 삭제할 루틴 ID
     * @return 해당 루틴이 존재하여 삭제되었으면 true, 아니면 false
     */
    public boolean removeRoutine(int id) {
        return routines.remove(id) != null;
    }

    /**
     * 지정한 ID의 루틴 활성화 여부를 변경합니다.
     *
     * @param id 대상 루틴 ID
     * @param active 변경할 활성화 상태
     * @return 루틴을 찾아 변경했으면 true, 존재하지 않으면 false
     */
    public boolean setRoutineActive(int id, boolean active) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setActive(active);
        return true;
    }

    /**
     * 지정한 ID의 루틴 이름을 수정합니다.
     *
     * @param id 대상 루틴 ID
     * @param newName 새 루틴 이름
     * @return 루틴을 찾아 수정했으면 true, 존재하지 않으면 false
     */
    public boolean updateRoutineName(int id, String newName) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setName(newName);
        return true;
    }

    /**
     * 지정한 ID의 루틴 시작 기준일을 수정합니다.
     *
     * @param id 대상 루틴 ID
     * @param newStartDate 새 시작 기준일
     * @return 루틴을 찾아 수정했으면 true, 존재하지 않으면 false
     */
    public boolean updateRoutineStartDate(int id, LocalDate newStartDate) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setStartDate(newStartDate);
        return true;
    }

    /**
     * 지정한 ID의 루틴 반복 규칙을 변경합니다.
     *
     * @param id 대상 루틴 ID
     * @param newRule 새 {@link RoutineRule}
     * @return 루틴을 찾아 변경했으면 true, 존재하지 않으면 false
     */
    public boolean updateRoutineRule(int id, RoutineRule newRule) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setRule(newRule);
        return true;
    }

    /**
     * 루틴 이름에 키워드가 포함된 루틴들을 대소문자 구분 없이 검색합니다.
     *
     * @param keyword 검색할 이름 키워드 (null 또는 공백이면 전체 루틴 반환)
     * @return 검색 조건과 일치하는 {@link Routine} 목록
     */
    public List<Routine> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllRoutines();
        }
        String normalized = keyword.trim().toLowerCase();
        List<Routine> result = new ArrayList<>();
        for (Routine routine : routines.values()) {
            if (routine.getName().toLowerCase().contains(normalized)) {
                result.add(routine);
            }
        }
        return result;
    }

    /**
     * 특정 요일에 실행되는 루틴 목록을 검색합니다.
     *
     * @param dayOfWeek 검색할 요일 ({@link DayOfWeek} 상수, 1~7)
     * @return 해당 요일과 일치하는 {@link Routine} 목록
     */
    public List<Routine> searchByDayOfWeek(int dayOfWeek) {
        List<Routine> result = new ArrayList<>();
        if (!DayOfWeek.isValid(dayOfWeek)) {
            return result;
        }
        for (Routine routine : routines.values()) {
            if (routine.matchesDayOfWeek(dayOfWeek)) {
                result.add(routine);
            }
        }
        return result;
    }

    /**
     * 특정 신체 부위를 자극하는 퀘스트를 포함한 루틴 목록을 검색합니다.
     *
     * @param bodyPart 검색할 신체 부위명
     * @return 해당 부위를 포함하는 {@link Routine} 목록
     */
    public List<Routine> searchByBodyPart(String bodyPart) {
        List<Routine> result = new ArrayList<>();
        if (bodyPart == null || bodyPart.isBlank()) {
            return result;
        }
        String target = bodyPart.trim();
        for (Routine routine : routines.values()) {
            if (routine.hasBodyPart(target)) {
                result.add(routine);
            }
        }
        return result;
    }

    /**
     * 특정 근육을 자극하는 퀘스트를 포함한 루틴 목록을 검색합니다.
     *
     * @param muscle 검색할 근육명
     * @return 해당 근육을 포함하는 {@link Routine} 목록
     */
    public List<Routine> searchByMuscle(String muscle) {
        List<Routine> result = new ArrayList<>();
        if (muscle == null || muscle.isBlank()) {
            return result;
        }
        String target = muscle.trim();
        for (Routine routine : routines.values()) {
            if (routine.hasMuscle(target)) {
                result.add(routine);
            }
        }
        return result;
    }

    /**
     * 지정한 날짜에 실행 대상인 모든 활성 루틴으로부터 퀘스트를 발급해 모아서 반환합니다.
     *
     * @param date 퀘스트를 발급할 기준 날짜
     * @return 해당 날짜에 새로 발급된 {@link Quest} 목록
     */
    public List<Quest> grantQuestsForDate(LocalDate date) {
        List<Quest> grantedQuests = new ArrayList<>();
        if (date == null) {
            return grantedQuests;
        }
        for (Routine routine : routines.values()) {
            if (routine.isActive()) {
                grantedQuests.addAll(routine.grantQuests(date));
            }
        }
        return grantedQuests;
    }

    /**
     * 콘솔 기반 루틴 관리 메뉴를 실행합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     */
    public void menu(Scanner scanner) {
        menu(scanner, null);
    }

    /**
     * 콘솔 기반 루틴 관리 메뉴를 실행하며, 변경 사항 발생 시 저장 콜백을 즉시 호출합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     * @param onSave 루틴이나 퀘스트가 수정될 때마다 호출할 저장 콜백 (null 허용)
     */
    public void menu(Scanner scanner, Runnable onSave) {
        RoutineMenu.show(this, scanner, onSave);
    }
}
