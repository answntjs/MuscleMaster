package quest;

import common.Muscle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * 루틴에 저장되는 운동 퀘스트 템플릿 클래스입니다.
 * 완료 여부나 현재 세트 수 같은 진행 상태를 갖지 않으며,
 * 실제 부여 시 {@link #createQuest()}를 통해 독립된 {@link Quest} 인스턴스를 생성합니다.
 */
public class RoutineQuest {
    /** 운동 이름 */
    protected String name;

    /** 운동이 자극하는 근육 집합 */
    protected HashSet<String> muscles;

    /** 근육 정보를 기반으로 계산된 신체 부위 집합 */
    protected HashSet<String> parts;

    /** 퀘스트 완료를 위한 목표 세트 수 */
    protected int goalSet;

    /** 퀘스트 완료 시 지급되는 기본 경험치 */
    protected int exp;

    /**
     * 운동명, 목표 세트 수, 경험치, 자극 근육 목록으로 루틴 퀘스트를 생성합니다.
     *
     * @param name 운동 이름
     * @param goalSet 목표 세트 수
     * @param exp 완료 시 지급할 기본 경험치
     * @param muscles 자극하는 근육 이름 가변 인자
     */
    public RoutineQuest(String name, int goalSet, int exp, String... muscles) {
        this.name = name;
        this.muscles = new HashSet<>(Arrays.asList(muscles));
        this.goalSet = goalSet;
        this.exp = exp;
        this.parts = new HashSet<>();
        updateParts();
    }

    /**
     * 기존 {@link RoutineQuest}의 속성을 깊은 복사하여 새로운 인스턴스를 생성합니다.
     *
     * @param other 복사할 원본 {@link RoutineQuest}
     */
    public RoutineQuest(RoutineQuest other) {
        this.name = other.name;
        this.muscles = new HashSet<>(other.muscles);
        this.parts = new HashSet<>(other.parts);
        this.goalSet = other.goalSet;
        this.exp = other.exp;
    }

    /**
     * 운동 이름을 반환합니다.
     *
     * @return 운동 이름
     */
    public String getName() { return name; }

    /**
     * 자극하는 근육 이름 집합을 반환합니다.
     *
     * @return 근육 이름 집합
     */
    public HashSet<String> getMuscles() { return muscles; }

    /**
     * 자극하는 신체 부위 이름 집합을 반환합니다.
     *
     * @return 신체 부위 이름 집합
     */
    public HashSet<String> getParts() { return parts; }

    /**
     * 목표 세트 수를 반환합니다.
     *
     * @return 목표 세트 수
     */
    public int getGoalSet() { return goalSet; }

    /**
     * 완료 시 지급되는 기본 경험치를 반환합니다.
     *
     * @return 기본 경험치
     */
    public int getExp() { return exp; }

    /**
     * 운동 이름을 설정합니다.
     *
     * @param name 새로 설정할 운동 이름
     */
    public void setName(String name) { this.name = name; }

    /**
     * 목표 세트 수를 설정합니다.
     *
     * @param goalSet 새로 설정할 목표 세트 수
     */
    public void setGoalSet(int goalSet) { this.goalSet = goalSet; }

    /**
     * 자극하는 근육 목록을 변경하고 신체 부위 집합을 갱신합니다.
     *
     * @param muscles 새로 설정할 근육 이름 가변 인자
     */
    public void setMuscles(String... muscles) {
        this.muscles = new HashSet<>(Arrays.asList(muscles));
        updateParts();
    }

    /**
     * 완료 시 지급할 기본 경험치를 설정합니다.
     *
     * @param exp 새로 설정할 기본 경험치
     */
    public void setExp(int exp) { this.exp = exp; }

    /**
     * 현재 근육 집합을 기준으로 {@link Muscle#MUSCLE_TO_BODY}를 참조하여 신체 부위 집합을 재계산합니다.
     */
    protected void updateParts() {
        parts.clear();
        for (String m : this.muscles) {
            String part = Muscle.MUSCLE_TO_BODY.get(m);
            if (part != null) {
                parts.add(part);
            }
        }
    }

    /**
     * 이 템플릿을 기반으로 진행 가능한 독립 {@link Quest} 인스턴스를 생성합니다.
     *
     * @return 새로 생성된 {@link Quest} 인스턴스
     */
    public Quest createQuest() {
        return new Quest(this);
    }

    /**
     * 퀘스트 요약 문자열의 공통 포맷을 생성합니다.
     *
     * @param progressText 진행 현황 또는 목표 세트 문자열
     * @return 정형화된 퀘스트 요약 문자열
     */
    protected String formatQuestString(String progressText) {
        String partText = parts.isEmpty()
                ? "없음"
                : parts.stream().sorted().collect(Collectors.joining(", "));
        String muscleText = muscles.isEmpty()
                ? "없음"
                : muscles.stream().sorted().collect(Collectors.joining(", "));
        return String.format("%s | EXP %d | %s\n부위: %s\n근육: %s",
                name, exp, progressText, partText, muscleText);
    }

    /**
     * 운동명, 보상 경험치, 목표 세트 수, 자극 부위 및 근육을 정형화된 문자열로 반환합니다.
     *
     * @return {@code "이름 | EXP 보상경험치 | 목표세트\n부위: 부위목록\n근육: 근육목록"} 형식의 문자열
     */
    @Override
    public String toString() {
        return formatQuestString(goalSet + "세트");
    }
}
