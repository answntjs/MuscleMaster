package routine;

import quest.Quest;
import quest.RoutineQuest;
import routine.rule.RoutineRule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * 루틴 - 특정 규칙에 따라 퀘스트를 부여하는 클래스
 * 상속 대신 RoutineRule 인터페이스를 합성(필드 보유)하여
 * 한 루틴 안에서도 규칙을 손쉽게 교체할 수 있다.
 * id는 파일 로드 및 매니저 통합 관리를 위해 RoutineManager 등록 시 부여된다.
 */
public class Routine {
    private int id;
    private String name;
    private LocalDate startDate;
    private boolean active;
    private final List<RoutineQuest> quests;
    private RoutineRule rule;
    private LocalDate lastGrantedDate;

    public Routine(String name, RoutineRule rule) {
        this(name, LocalDate.now(), rule);
    }
    public Routine(String name, LocalDate startDate, RoutineRule rule) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("루틴명은 비어 있을 수 없습니다.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("시작일은 null일 수 없습니다.");
        }
        if (rule == null) {
            throw new IllegalArgumentException("루틴 규칙은 null일 수 없습니다.");
        }
        this.id = 0;
        this.name = name;
        this.startDate = startDate;
        this.active = true;
        this.quests = new ArrayList<>();
        this.rule = rule;
    }



    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public boolean isActive() { return active; }
    public RoutineRule getRule() { return rule; }
    public Set<String> getBodyParts() {
        Set<String> allParts = new HashSet<>();
        for (RoutineQuest q : quests) {
            allParts.addAll(q.getParts());
        }
        return allParts;
    }
    public List<RoutineQuest> getQuests() {
        return quests;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("루틴명은 비어 있을 수 없습니다.");
        }
        this.name = name;
    }
    void setId(int id) { this.id = id; }
    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("시작일은 null일 수 없습니다.");
        }
        this.startDate = startDate;
        this.lastGrantedDate = null;
    }
    public void setActive(boolean active) { this.active = active; }
    public void setRule(RoutineRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("루틴 규칙은 null일 수 없습니다.");
        }
        this.rule = rule;
        this.lastGrantedDate = null;
    }

    public boolean isDue(LocalDate date) {

        return active && rule != null && rule.isDue(startDate, date);
    }

    public void addQuest(RoutineQuest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        this.quests.add(new RoutineQuest(quest));
    }

    public boolean removeQuest(RoutineQuest quest) {
        return this.quests.remove(quest);
    }

    public RoutineQuest removeQuest(int index) {
        return this.quests.remove(index);
    }



    public boolean hasBodyPart(String bodyPart) {
        if (bodyPart == null || bodyPart.isBlank()) {
            return false;
        }
        for (RoutineQuest q : quests) {
            if (q.getParts().contains(bodyPart)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesDayOfWeek(DayOfWeek dayOfWeek) {
        return rule != null && rule.matchesDayOfWeek(startDate, dayOfWeek);
    }


    public List<Quest> grantQuests(LocalDate date) {
        if (date == null || !isDue(date) || date.equals(lastGrantedDate)) {
            return Collections.emptyList();
        }
        lastGrantedDate = date;
        rule.onGranted(date);

        List<Quest> granted = new ArrayList<>(quests.size());
        for (RoutineQuest template : quests) {
            granted.add(template.createQuest());
        }
        return granted;
    }

    @Override
    public String toString() {
        String status = active ? "활성" : "비활성";
        return String.format("[ID:%d] %s (%s) | 시작일: %s | 규칙: %s | 부위: %s | 퀘스트 %d개",
                id, name, status, startDate, rule.describe(), getBodyParts(), quests.size());
    }
}
