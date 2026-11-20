package quest;

/**
 * 실제 사용자에게 부여되어 세트 진행 및 완료 여부를 추적하는 개별 운동 퀘스트 클래스입니다.
 * {@link RoutineQuest}를 상속하여 진행 상태({@link #currentSet}, {@link #isCompleted})를 관리합니다.
 */
public class Quest extends RoutineQuest {
    /** 현재까지 수행한 세트 수 */
    private int currentSet;

    /** 퀘스트 완료 여부 */
    private boolean isCompleted;

    /**
     * 운동 정보로 새로운 진행형 퀘스트를 생성합니다.
     *
     * @param name 운동 이름
     * @param goalSet 목표 세트 수
     * @param exp 완료 시 지급할 기본 경험치
     * @param muscles 자극하는 근육 이름 가변 인자
     */
    public Quest(String name, int goalSet, int exp, String... muscles) {
        super(name, goalSet, exp, muscles);
        this.currentSet = 0;
        this.isCompleted = false;
    }

    /**
     * {@link RoutineQuest} 템플릿을 복사하여 진행형 퀘스트를 생성합니다.
     *
     * @param template 기반이 될 {@link RoutineQuest} 템플릿
     */
    public Quest(RoutineQuest template) {
        super(template);
        this.currentSet = 0;
        this.isCompleted = false;
    }

    /**
     * 현재까지 수행한 세트 수를 반환합니다.
     *
     * @return 현재 세트 수
     */
    public int getCurrentSet() { return currentSet; }

    /**
     * 퀘스트 완료 여부를 반환합니다.
     *
     * @return 목표 세트 수를 달성하여 완료되었으면 true, 아니면 false
     */
    public boolean isCompleted() { return isCompleted; }

    /**
     * 목표 세트 수를 변경하고 현재 진행 세트와 비교해 완료 여부를 갱신합니다.
     *
     * @param goalSet 새로 설정할 목표 세트 수
     */
    @Override
    public void setGoalSet(int goalSet) {
        super.setGoalSet(goalSet);
        if (currentSet >= goalSet) {
            currentSet = goalSet;
            isCompleted = true;
        } else {
            isCompleted = false;
        }
    }

    /**
     * 운동을 1세트 수행합니다.
     *
     * @return 세트가 정상 반영되었으면 true, 이미 완료된 상태면 false
     */
    public boolean doSet() { return doSet(1); }

    /**
     * 지정한 세트 수만큼 운동을 수행하고, 목표 세트에 도달하면 완료 처리합니다.
     *
     * @param set 이번에 수행한 세트 수
     * @return 세트가 정상 반영되었으면 true, 이미 완료된 상태면 false
     */
    public boolean doSet(int set) {
        if (isCompleted) return false;

        currentSet += set;
        if (currentSet >= goalSet) {
            currentSet = goalSet;
            isCompleted = true;
        }
        return true;
    }

    /**
     * 저장 파일에서 불러온 현재 세트 수와 완료 상태로 퀘스트 진행도를 복원합니다.
     *
     * @param currentSet 복원할 현재 세트 수 (0 이상)
     * @param completed 복원할 완료 여부
     */
    public void restoreProgress(int currentSet, boolean completed) {
        if (currentSet < 0) {
            throw new IllegalArgumentException("현재 세트 수는 0 이상이어야 합니다: " + currentSet);
        }
        this.currentSet = Math.min(currentSet, goalSet);
        this.isCompleted = completed || this.currentSet >= goalSet;
    }

    /**
     * 운동명, 보상 경험치, 세트 진행 현황(완료 시 {@code "Completed!"}), 자극 부위 및 근육을 정형화된 문자열로 반환합니다.
     *
     * @return {@code "이름 | EXP 보상경험치 | 현재/목표(또는 Completed!)\n부위: 부위목록\n근육: 근육목록"} 형식의 문자열
     */
    @Override
    public String toString() {
        String progressText = isCompleted ? "Completed!" : String.format("%d/%d", currentSet, goalSet);
        return formatQuestString(progressText);
    }
}