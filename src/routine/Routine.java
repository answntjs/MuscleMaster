package routine;

import quest.Quest;
import quest.RoutineQuest;
import routine.rule.RoutineRule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 특정 실행 규칙({@link RoutineRule})에 따라 퀘스트를 부여하는 루틴 클래스입니다.
 * 상속 대신 {@link RoutineRule} 인터페이스를 합성하여 런타임에 규칙을 손쉽게 교체할 수 있으며,
 * 식별자({@link #id})는 {@link RoutineManager} 등록 시 부여됩니다.
 */
public class Routine {
    /** 매니저 등록 시 부여되는 루틴 고유 ID */
    private int id;

    /** 루틴 이름 */
    private String name;

    /** 루틴 시작 기준일 */
    private LocalDate startDate;

    /** 루틴 활성화 여부 */
    private boolean active;

    /** 루틴에 포함된 퀘스트 템플릿 목록 */
    private final List<RoutineQuest> quests;

    /** 루틴 실행 여부를 판정하는 반복 규칙 */
    private RoutineRule rule;

    /** 마지막으로 퀘스트가 부여된 날짜 (중복 부여 방지용) */
    private LocalDate lastGrantedDate;

    /**
     * 오늘 날짜를 시작일로 하여 새로운 루틴을 생성합니다.
     *
     * @param name 루틴 이름 (null 또는 공백 불가)
     * @param rule 적용할 루틴 반복 규칙 (null 불가)
     */
    public Routine(String name, RoutineRule rule) {
        this(name, LocalDate.now(), rule);
    }

    /**
     * 시작일과 규칙을 지정하여 새로운 루틴을 생성합니다.
     *
     * @param name 루틴 이름 (null 또는 공백 불가)
     * @param startDate 루틴 시작 기준일 (null 불가)
     * @param rule 적용할 루틴 반복 규칙 (null 불가)
     */
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

    /**
     * 루틴 ID를 반환합니다.
     *
     * @return 루틴 ID (매니저 등록 전이면 0)
     */
    public int getId() { return id; }

    /**
     * 루틴 이름을 반환합니다.
     *
     * @return 루틴 이름
     */
    public String getName() { return name; }

    /**
     * 루틴 시작 기준일을 반환합니다.
     *
     * @return 시작 기준일
     */
    public LocalDate getStartDate() { return startDate; }

    /**
     * 루틴 활성화 여부를 반환합니다.
     *
     * @return 활성화되어 있으면 true, 비활성이면 false
     */
    public boolean isActive() { return active; }

    /**
     * 현재 적용된 루틴 규칙을 반환합니다.
     *
     * @return 루틴 반복 규칙
     */
    public RoutineRule getRule() { return rule; }

    /**
     * 루틴에 등록된 모든 퀘스트가 자극하는 신체 부위 집합을 반환합니다.
     *
     * @return 중복이 제거된 신체 부위 이름 집합
     */
    public Set<String> getBodyParts() {
        Set<String> allParts = new HashSet<>();
        for (RoutineQuest q : quests) {
            allParts.addAll(q.getParts());
        }
        return allParts;
    }

    /**
     * 루틴에 포함된 퀘스트 템플릿 목록을 반환합니다.
     *
     * @return {@link RoutineQuest} 목록
     */
    public List<RoutineQuest> getQuests() {
        return quests;
    }

    /**
     * 루틴 이름을 변경합니다.
     *
     * @param name 새 루틴 이름 (null 또는 공백 불가)
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("루틴명은 비어 있을 수 없습니다.");
        }
        this.name = name;
    }

    /**
     * 매니저가 루틴에 고유 ID를 할당할 때 사용합니다.
     *
     * @param id 할당할 루틴 ID
     */
    void setId(int id) { this.id = id; }

    /**
     * 루틴 시작 기준일을 변경하고 마지막 부여 일자 기록을 초기화합니다.
     *
     * @param startDate 새 시작 기준일 (null 불가)
     */
    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("시작일은 null일 수 없습니다.");
        }
        this.startDate = startDate;
        this.lastGrantedDate = null;
    }

    /**
     * 루틴 활성화 여부를 설정합니다.
     *
     * @param active 활성화 여부 (true: 활성, false: 비활성)
     */
    public void setActive(boolean active) { this.active = active; }

    /**
     * 루틴 반복 규칙을 교체하고 마지막 부여 일자 기록을 초기화합니다.
     *
     * @param rule 새 루틴 반복 규칙 (null 불가)
     */
    public void setRule(RoutineRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("루틴 규칙은 null일 수 없습니다.");
        }
        this.rule = rule;
        this.lastGrantedDate = null;
    }

    /**
     * 지정한 날짜에 이 루틴이 실행 대상인지 확인합니다.
     *
     * @param date 확인할 날짜
     * @return 루틴이 활성화 상태이고 규칙상 해당 날짜에 실행 대상이면 true
     */
    public boolean isDue(LocalDate date) {
        return active && rule != null && rule.isDue(startDate, date);
    }

    /**
     * 루틴에 퀘스트 템플릿을 복사하여 추가합니다.
     *
     * @param quest 추가할 {@link RoutineQuest} (null 불가)
     */
    public void addQuest(RoutineQuest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        this.quests.add(new RoutineQuest(quest));
    }

    /**
     * 루틴에서 지정한 퀘스트 템플릿을 제거합니다.
     *
     * @param quest 제거할 {@link RoutineQuest}
     * @return 제거에 성공했으면 true, 목록에 없으면 false
     */
    public boolean removeQuest(RoutineQuest quest) {
        return this.quests.remove(quest);
    }

    /**
     * 루틴에서 지정한 인덱스의 퀘스트 템플릿을 제거합니다.
     *
     * @param index 제거할 퀘스트의 0 기반 인덱스
     * @return 제거된 {@link RoutineQuest}
     */
    public RoutineQuest removeQuest(int index) {
        return this.quests.remove(index);
    }

    /**
     * 마지막으로 퀘스트가 부여된 날짜를 반환합니다.
     *
     * @return 마지막 부여 날짜 (부여 이력이 없으면 null)
     */
    public LocalDate getLastGrantedDate() {
        return lastGrantedDate;
    }

    /**
     * 저장 파일에서 불러온 마지막 퀘스트 부여 날짜를 복원합니다.
     *
     * @param lastGrantedDate 복원할 마지막 부여 날짜 (null 허용)
     */
    public void restoreLastGrantedDate(LocalDate lastGrantedDate) {
        this.lastGrantedDate = lastGrantedDate;
    }

    /**
     * 루틴 내 퀘스트 중 지정한 신체 부위를 포함하는 항목이 있는지 확인합니다.
     *
     * @param bodyPart 확인할 신체 부위명
     * @return 해당 신체 부위를 포함하면 true, 아니면 false
     */
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

    /**
     * 루틴 내 퀘스트 중 지정한 근육을 포함하는 항목이 있는지 확인합니다.
     *
     * @param muscle 확인할 근육명
     * @return 해당 근육을 포함하면 true, 아니면 false
     */
    public boolean hasMuscle(String muscle) {
        if (muscle == null || muscle.isBlank()) {
            return false;
        }
        for (RoutineQuest q : quests) {
            if (q.getMuscles().contains(muscle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 루틴 규칙이 지정한 요일에 실행될 수 있는지 확인합니다.
     *
     * @param dayOfWeek 확인할 요일 ({@link common.DayOfWeek} 상수, 1~7)
     * @return 해당 요일과 일치하면 true, 아니면 false
     */
    public boolean matchesDayOfWeek(int dayOfWeek) {
        return rule != null && rule.matchesDayOfWeek(startDate, dayOfWeek);
    }

    /**
     * 지정한 날짜가 실행 대상이며 아직 부여되지 않았다면 루틴의 모든 퀘스트 인스턴스를 생성해 반환합니다.
     *
     * @param date 퀘스트를 부여할 날짜
     * @return 생성된 {@link Quest} 목록 (대상이 아니거나 이미 부여된 날짜이면 빈 목록)
     */
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

    /**
     * 루틴의 ID, 이름(비활성 시 {@code "(비활성)"} 표시), 규칙, 시작일, 마지막 부여일 및 퀘스트 목록을 정형화된 문자열로 반환합니다.
     *
     * @return {@code "id. 이름[(비활성)] | 규칙\n시작일: 시작일 | 마지막 부여: 마지막부여일\n퀘스트: 퀘스트목록"} 형식의 문자열
     */
    @Override
    public String toString() {
        String displayName = active ? name : name + "(비활성)";
        String lastGrantedText = lastGrantedDate != null ? lastGrantedDate.toString() : "없음";
        String questNames = quests.isEmpty()
                ? "없음"
                : quests.stream().map(RoutineQuest::getName).collect(Collectors.joining(", "));
        return String.format("%d. %s | %s\n시작일: %s | 마지막 부여: %s\n퀘스트: %s",
                id, displayName, rule.describe(), startDate, lastGrantedText, questNames);
    }
}
