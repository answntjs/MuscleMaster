package quest;

import common.Muscle;
import java.util.Arrays;
import java.util.HashSet;

/*
 * 루틴에 저장되는 퀘스트
 * 완료 여부나 현재 세트 수 같은 진행 상태를 갖지 않으며,
 * 실제 부여 시 createQuest()를 통해 독립된 Quest 인스턴스를 생성한다.
 */
public class RoutineQuest {
    protected String name;
    protected HashSet<String> muscles;
    protected HashSet<String> parts;
    protected int goalSet;
    protected int exp;


    public RoutineQuest(String name, int goalSet, int exp, String... muscles) {
        this.name = name;
        this.muscles = new HashSet<>(Arrays.asList(muscles));
        this.goalSet = goalSet;
        this.exp = exp;
        this.parts = new HashSet<>();
        updateParts();
    }
    public RoutineQuest(RoutineQuest other) {
        this.name = other.name;
        this.muscles = new HashSet<>(other.muscles);
        this.parts = new HashSet<>(other.parts);
        this.goalSet = other.goalSet;
        this.exp = other.exp;
    }


    public String getName() { return name; }
    public HashSet<String> getMuscles() { return muscles; }
    public HashSet<String> getParts() { return parts; }
    public int getGoalSet() { return goalSet; }
    public int getExp() { return exp; }

    public void setName(String name) { this.name = name; }
    public void setGoalSet(int goalSet) { this.goalSet = goalSet; }
    public void setMuscles(String... muscles) {
        this.muscles = new HashSet<>(Arrays.asList(muscles));
        updateParts();
    }
    public void setExp(int exp) { this.exp = exp; }


    protected void updateParts() {
        parts.clear();
        for (String m : this.muscles) {
            String part = Muscle.MUSCLE_TO_BODY.get(m);
            if (part != null) {
                parts.add(part);
            }
        }
    }

    public Quest createQuest() {
        return new Quest(this);
    }

    @Override
    public String toString() {
        return name + " (" + goalSet + "세트, EXP " + exp + ", 부위 " + parts + ")";
    }
}
