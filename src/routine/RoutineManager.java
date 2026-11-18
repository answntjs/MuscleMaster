package routine;

import common.Muscle;
import quest.Quest;
import quest.RoutineQuest;
import routine.rule.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
 * 루틴매니저 - 루틴들을 관리함
 * 루틴 등록/조회/수정(활성화여부포함)/삭제
 * 이름(포함)/요일별/부위별 검색
 * menu()는 핵심 로직 메서드를 호출하는 UI 전용 껍데기 역할을 한다.
 */
public class RoutineManager {
    private final Map<Integer, Routine> routines = new LinkedHashMap<>();
    private int nextId = 1;

    public Routine getRoutine(int id) {
        return routines.get(id);
    }

    public List<Routine> getAllRoutines() {
        return new ArrayList<>(routines.values());
    }

    public List<Routine> getActiveRoutines() {
        List<Routine> result = new ArrayList<>();
        for (Routine routine : routines.values()) {
            if (routine.isActive()) {
                result.add(routine);
            }
        }
        return result;
    }



    public Routine addRoutine(Routine routine) {
        if (routine == null) {
            throw new IllegalArgumentException("루틴은 null일 수 없습니다.");
        }
        routine.setId(nextId++);
        routines.put(routine.getId(), routine);
        return routine;
    }

    public boolean removeRoutine(int id) {
        return routines.remove(id) != null;
    }

    public boolean setRoutineActive(int id, boolean active) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setActive(active);
        return true;
    }

    public boolean updateRoutineName(int id, String newName) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setName(newName);
        return true;
    }

    public boolean updateRoutineStartDate(int id, LocalDate newStartDate) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setStartDate(newStartDate);
        return true;
    }

    public boolean updateRoutineRule(int id, RoutineRule newRule) {
        Routine routine = routines.get(id);
        if (routine == null) {
            return false;
        }
        routine.setRule(newRule);
        return true;
    }



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

    public List<Routine> searchByDayOfWeek(DayOfWeek dayOfWeek) {
        List<Routine> result = new ArrayList<>();
        if (dayOfWeek == null) {
            return result;
        }
        for (Routine routine : routines.values()) {
            if (routine.matchesDayOfWeek(dayOfWeek)) {
                result.add(routine);
            }
        }
        return result;
    }

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
}
