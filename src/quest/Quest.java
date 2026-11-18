package quest;

/*
* 퀘스트 - 실제 부여되어 진행되는 개별 운동
* 필드
*   운동명, 운동근육, 운동부위, 목표세트수, 경험치 (RoutineQuest 상속)
*   현재세트수
*   완료여부
* */

public class Quest extends RoutineQuest {
    private int currentSet;
    private boolean isCompleted;

    public Quest(String name, int goalSet, int exp, String... muscles) {
        super(name, goalSet, exp, muscles);
        this.currentSet = 0;
        this.isCompleted = false;
    }
    public Quest(RoutineQuest template) {
        super(template);
        this.currentSet = 0;
        this.isCompleted = false;
    }

    public int getCurrentSet() { return currentSet; }
    public boolean isCompleted() { return isCompleted; }

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


    public boolean doSet() { return doSet(1); }
    public boolean doSet(int set) {
        if (isCompleted) return false;

        currentSet += set;
        if (currentSet >= goalSet) {
            currentSet = goalSet;
            isCompleted = true;
        }
        return true;
    }
}