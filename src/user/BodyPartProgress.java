package user;

/**
 * 신체 부위별 레벨, 현재 경험치, 누적 경험치를 관리하는 클래스입니다.
 */
public class BodyPartProgress {
    /** 1레벨에서 다음 레벨로 올라가기 위한 기본 요구 경험치 */
    public static final int BASE_REQUIRED_EXP = 50;

    /** 레벨 증가에 따라 선형으로 증가하는 요구 경험치 계수 */
    public static final int LINEAR_EXP_STEP = 30;

    /** 레벨 증가에 따라 이차항으로 증가하는 요구 경험치 계수 */
    public static final int QUADRATIC_EXP_STEP = 5;

    /** 신체 부위 이름 (예: "가슴", "등", "하체") */
    private final String partName;

    /** 해당 신체 부위의 현재 레벨 */
    private int level;

    /** 현재 레벨 내 보유 경험치 */
    private int exp;

    /** 지금까지 해당 신체 부위에 누적된 총 경험치 */
    private int totalExp;

    /**
     * 지정한 신체 부위 이름으로 초기 성장 정보를 생성합니다.
     *
     * @param partName 신체 부위 이름 (null 또는 공백 불가)
     */
    public BodyPartProgress(String partName) {
        if (partName == null || partName.isBlank()) {
            throw new IllegalArgumentException("부위명은 비어 있을 수 없습니다.");
        }
        this.partName = partName;
        this.level = 1;
        this.exp = 0;
        this.totalExp = 0;
    }

    /**
     * 신체 부위 이름을 반환합니다.
     *
     * @return 신체 부위 이름
     */
    public String getPartName() {
        return partName;
    }

    /**
     * 현재 부위 레벨을 반환합니다.
     *
     * @return 현재 레벨 (1 이상)
     */
    public int getLevel() {
        return level;
    }

    /**
     * 현재 레벨 내 보유 경험치를 반환합니다.
     *
     * @return 현재 경험치
     */
    public int getExp() {
        return exp;
    }

    /**
     * 지금까지 획득한 총 누적 경험치를 반환합니다.
     *
     * @return 총 누적 경험치
     */
    public int getTotalExp() {
        return totalExp;
    }

    /**
     * 현재 레벨에서 다음 레벨로 올라가기 위해 필요한 총 경험치를 계산하여 반환합니다.
     *
     * @return 다음 레벨업에 필요한 요구 경험치
     */
    public int getRequiredExp() {
        int offset = level - 1;
        return BASE_REQUIRED_EXP + LINEAR_EXP_STEP * offset + QUADRATIC_EXP_STEP * offset * offset;
    }

    /**
     * 경험치를 획득하고 요구 경험치를 초과할 경우 연속 레벨업을 처리합니다.
     *
     * @param amount 획득할 경험치 (0 이상)
     */
    public void gainExp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("획득 경험치는 0 이상이어야 합니다: " + amount);
        }
        if (amount == 0) {
            return;
        }
        this.totalExp += amount;
        this.exp += amount;
        while (this.exp >= getRequiredExp()) {
            this.exp -= getRequiredExp();
            this.level++;
        }
    }

    /**
     * 저장 파일에서 불러온 레벨과 경험치 상태로 부위 성장 정보를 복원합니다.
     *
     * @param level 복원할 레벨 (1 이상)
     * @param exp 복원할 현재 레벨 경험치 (0 이상)
     * @param totalExp 복원할 누적 총 경험치 (0 이상)
     */
    public void restore(int level, int exp, int totalExp) {
        if (level < 1 || exp < 0 || totalExp < 0) {
            throw new IllegalArgumentException("잘못된 부위 성장 복원 값입니다.");
        }
        this.level = level;
        this.exp = exp;
        this.totalExp = totalExp;
    }

    /**
     * 부위명, 레벨, 현재 레벨 경험치/요구 경험치 및 총 누적 경험치를 정형화된 문자열로 반환합니다.
     *
     * @return {@code "이름 | Lv.레벨 | EXP 현재경험치 / 요구경험치\n총 경험치: 총경험치"} 형식의 문자열
     */
    @Override
    public String toString() {
        return String.format("%s | Lv.%d | EXP %d / %d\n총 경험치: %d",
                partName, level, exp, getRequiredExp(), totalExp);
    }
}
