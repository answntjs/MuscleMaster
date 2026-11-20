package user;

import common.CharacterStatus;
import common.Muscle;
import quest.Quest;
import routine.RoutineManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 유저(플레이어)의 RPG 캐릭터 상태, 부위별 성장, 루틴 매니저 및 퀘스트를 통합 관리하는 클래스입니다.
 */
public class Player {
    /** 캐릭터 1레벨의 기본 레벨업 요구 경험치 */
    public static final int CHAR_BASE_REQUIRED_EXP = 100;

    /** 캐릭터 레벨 증가에 따른 선형 요구 경험치 계수 */
    public static final int CHAR_LINEAR_EXP_STEP = 50;

    /** 캐릭터 레벨 증가에 따른 이차항 요구 경험치 계수 */
    public static final int CHAR_QUADRATIC_EXP_STEP = 10;

    /** 연속 달성(스트릭)으로 얻을 수 있는 최대 보너스 비율 (50%) */
    public static final double MAX_STREAK_BONUS = 0.50;

    /** 스트릭 로그 보너스 계산에 사용되는 계수 */
    public static final double STREAK_LOG_COEFFICIENT = 0.10;

    /** 열정(ON_FIRE) 상태로 전환되기 위한 최소 연속 달성 일수 */
    public static final int ON_FIRE_STREAK_THRESHOLD = 3;

    /** 퀘스트 실패 시 기본 경험치 차감 비율 */
    public static final double DEFAULT_FAILURE_PENALTY_RATE = 0.5;

    /** 플레이어 이름 */
    private String name;

    /** 캐릭터 현재 레벨 */
    private int level;

    /** 현재 레벨 내 보유 경험치 */
    private int exp;

    /** 지금까지 누적된 캐릭터 총 경험치 */
    private int totalExp;

    /** 연속 운동 달성 일수 */
    private int streakDays;

    /** 마지막으로 퀘스트를 완료한 날짜 */
    private LocalDate lastActiveDate;

    /** 캐릭터 현재 상태 ({@link CharacterStatus} 상수) */
    private String status;

    /** 신체 부위별 성장도 맵 */
    private final Map<String, BodyPartProgress> partProgressMap;

    /** 플레이어의 루틴들을 관리하는 매니저 */
    private final RoutineManager routineManager;

    /** 현재 진행 중인 퀘스트 목록 */
    private final List<Quest> activeQuests;

    /** 완료된 퀘스트 이력 목록 */
    private final List<Quest> completedQuests;

    /**
     * 지정한 이름으로 초기 상태의 플레이어 캐릭터를 생성합니다.
     *
     * @param name 플레이어 이름 (null 또는 공백 불가)
     */
    public Player(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("플레이어 이름은 비어 있을 수 없습니다.");
        }
        this.name = name.trim();
        this.level = 1;
        this.exp = 0;
        this.totalExp = 0;
        this.streakDays = 0;
        this.lastActiveDate = null;
        this.status = CharacterStatus.NORMAL;

        this.partProgressMap = new LinkedHashMap<>();
        for (String bodyPart : Muscle.BODYPARTS) {
            this.partProgressMap.put(bodyPart, new BodyPartProgress(bodyPart));
        }

        this.routineManager = new RoutineManager();
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
    }

    /**
     * 플레이어 이름을 반환합니다.
     *
     * @return 플레이어 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 플레이어 이름을 변경합니다.
     *
     * @param name 새 플레이어 이름 (null 또는 공백 불가)
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("플레이어 이름은 비어 있을 수 없습니다.");
        }
        this.name = name.trim();
    }

    /**
     * 캐릭터의 현재 레벨을 반환합니다.
     *
     * @return 캐릭터 레벨
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
     * 지금까지 누적된 캐릭터 총 경험치를 반환합니다.
     *
     * @return 총 누적 경험치
     */
    public int getTotalExp() {
        return totalExp;
    }

    /**
     * 현재 연속 운동 달성 일수를 반환합니다.
     *
     * @return 연속 달성 일수
     */
    public int getStreakDays() {
        return streakDays;
    }

    /**
     * 마지막으로 퀘스트를 완료한 날짜를 반환합니다.
     *
     * @return 마지막 활동 날짜 (기록이 없으면 null)
     */
    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    /**
     * 캐릭터의 현재 상태 문자열을 반환합니다.
     *
     * @return 캐릭터 상태 ({@link CharacterStatus} 값)
     */
    public String getStatus() {
        return status;
    }

    /**
     * 플레이어의 루틴 매니저를 반환합니다.
     *
     * @return {@link RoutineManager} 인스턴스
     */
    public RoutineManager getRoutineManager() {
        return routineManager;
    }

    /**
     * 모든 신체 부위의 성장 정보 맵을 수정 불가능한 형태로 반환합니다.
     *
     * @return 부위명(key)과 {@link BodyPartProgress}(value)로 구성된 수정 불가능한 맵
     */
    public Map<String, BodyPartProgress> getPartProgressMap() {
        return Collections.unmodifiableMap(partProgressMap);
    }

    /**
     * 특정 신체 부위의 성장 정보를 조회합니다.
     *
     * @param bodyPart 조회할 신체 부위명
     * @return 해당 부위의 {@link BodyPartProgress}, 존재하지 않으면 null
     */
    public BodyPartProgress getPartProgress(String bodyPart) {
        return partProgressMap.get(bodyPart);
    }

    /**
     * 현재 진행 중인 퀘스트 목록을 수정 불가능한 리스트로 반환합니다.
     *
     * @return 진행 중인 {@link Quest} 목록
     */
    public List<Quest> getActiveQuests() {
        return Collections.unmodifiableList(activeQuests);
    }

    /**
     * 완료된 퀘스트 목록을 수정 불가능한 리스트로 반환합니다.
     *
     * @return 완료된 {@link Quest} 목록
     */
    public List<Quest> getCompletedQuests() {
        return Collections.unmodifiableList(completedQuests);
    }

    /**
     * 현재 캐릭터 레벨에서 다음 레벨로 올라가기 위해 필요한 총 경험치를 계산합니다.
     *
     * @return 다음 레벨업 요구 경험치
     */
    public int getRequiredExp() {
        int offset = level - 1;
        return CHAR_BASE_REQUIRED_EXP + CHAR_LINEAR_EXP_STEP * offset + CHAR_QUADRATIC_EXP_STEP * offset * offset;
    }

    /**
     * 현재 연속 달성 일수({@link #streakDays})에 따른 경험치 배율을 계산합니다.
     *
     * @return 1.0 이상의 연속 달성 경험치 배율 (최대 1.0 + {@link #MAX_STREAK_BONUS})
     */
    public double getStreakMultiplier() {
        if (streakDays <= 1) {
            return 1.0;
        }
        double bonus = Math.min(MAX_STREAK_BONUS, STREAK_LOG_COEFFICIENT * Math.log(streakDays));
        return 1.0 + bonus;
    }

    /**
     * 지정한 날짜에 해당하는 루틴 퀘스트들을 발급받아 진행 중 목록에 추가합니다.
     *
     * @param date 퀘스트를 발급할 기준 날짜 (null 불가)
     * @return 해당 날짜에 새로 발급되어 추가된 수정 불가능한 {@link Quest} 목록
     */
    public List<Quest> assignQuestsForDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("기준 날짜는 null일 수 없습니다.");
        }
        List<Quest> granted = routineManager.grantQuestsForDate(date);
        activeQuests.addAll(granted);
        return Collections.unmodifiableList(granted);
    }

    /**
     * 진행 중인 퀘스트의 세트를 수행하고, 목표 세트 달성 시 자동으로 완료 정산을 처리합니다.
     *
     * @param quest 진행할 {@link Quest} (null 불가, 진행 중 목록에 포함되어야 함)
     * @param sets 수행한 세트 수 (1 이상)
     * @param date 수행 날짜 (null 불가)
     * @return 이번 진행으로 퀘스트가 완료되었으면 true, 아직 미완료이면 false
     */
    public boolean progressQuest(Quest quest, int sets, LocalDate date) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("수행 날짜는 null일 수 없습니다.");
        }
        if (sets <= 0) {
            throw new IllegalArgumentException("수행 세트 수는 1 이상이어야 합니다: " + sets);
        }
        if (!activeQuests.contains(quest)) {
            throw new IllegalArgumentException("진행 중인 퀘스트 목록에 존재하지 않는 퀘스트입니다: " + quest.getName());
        }
        quest.doSet(sets);
        if (quest.isCompleted()) {
            completeQuest(quest, date);
            return true;
        }
        return false;
    }

    /**
     * 목표 세트를 달성한 퀘스트를 완료 목록으로 이동하고 스트릭, 캐릭터 경험치, 부위별 경험치를 정산합니다.
     *
     * @param quest 완료 처리할 {@link Quest} (null 불가, 진행 중이며 완료 상태여야 함)
     * @param date 완료 날짜 (null 불가)
     */
    public void completeQuest(Quest quest, LocalDate date) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("완료 날짜는 null일 수 없습니다.");
        }
        if (!activeQuests.contains(quest)) {
            throw new IllegalArgumentException("진행 중인 퀘스트 목록에 존재하지 않는 퀘스트입니다: " + quest.getName());
        }
        if (!quest.isCompleted()) {
            throw new IllegalStateException("아직 완료되지 않은 퀘스트입니다: " + quest.getName());
        }

        boolean wasPenalty = CharacterStatus.PENALTY.equals(this.status);
        updateStreak(date);
        activeQuests.remove(quest);
        completedQuests.add(quest);

        double statusMultiplier = wasPenalty
                ? CharacterStatus.getExpMultiplier(CharacterStatus.PENALTY)
                : CharacterStatus.getExpMultiplier(this.status);
        int baseExp = Math.max(0, quest.getExp());
        int finalExp = (int) Math.round(baseExp * getStreakMultiplier() * statusMultiplier);

        gainCharacterExp(finalExp);
        distributePartExp(quest, finalExp);
    }

    /**
     * 진행 중인 퀘스트를 실패 처리하여 목록에서 제거하고, 경험치 차감 및 슬럼프 상태 전환을 적용합니다.
     *
     * @param quest 실패 처리할 {@link Quest} (null 불가, 진행 중 목록에 포함되어야 함)
     */
    public void failQuest(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        if (!activeQuests.remove(quest)) {
            throw new IllegalArgumentException("진행 중인 퀘스트 목록에 존재하지 않는 퀘스트입니다: " + quest.getName());
        }
        int penaltyExp = (int) Math.round(Math.max(0, quest.getExp()) * DEFAULT_FAILURE_PENALTY_RATE);
        penalizeCharacterExp(penaltyExp);
        this.streakDays = 0;
        this.status = CharacterStatus.PENALTY;
    }

    /**
     * 현재 진행 중인 모든 퀘스트를 일괄 실패 처리합니다.
     */
    public void failAllActiveQuests() {
        if (activeQuests.isEmpty()) {
            return;
        }
        for (Quest quest : new ArrayList<>(activeQuests)) {
            failQuest(quest);
        }
    }

    /**
     * 활동 날짜를 기준으로 연속 달성 일수({@link #streakDays})와 캐릭터 상태({@link #status})를 갱신합니다.
     *
     * @param date 퀘스트를 완료한 날짜 (null 불가, 마지막 활동일 이전 날짜 불가)
     */
    protected void updateStreak(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
        }
        if (lastActiveDate == null) {
            streakDays = 1;
            lastActiveDate = date;
        } else if (date.equals(lastActiveDate)) {
            if (streakDays == 0) {
                streakDays = 1;
            }
        } else if (date.equals(lastActiveDate.plusDays(1))) {
            streakDays++;
            lastActiveDate = date;
        } else if (date.isAfter(lastActiveDate.plusDays(1))) {
            streakDays = 1;
            lastActiveDate = date;
        } else {
            throw new IllegalArgumentException("마지막 활동일(" + lastActiveDate + ")보다 이전 날짜(" + date + ")는 처리할 수 없습니다.");
        }

        this.status = (streakDays >= ON_FIRE_STREAK_THRESHOLD)
                ? CharacterStatus.ON_FIRE
                : CharacterStatus.NORMAL;
    }

    /**
     * 퀘스트에 포함된 근육 비율을 바탕으로 신체 부위별 경험치 분배 비율을 계산합니다.
     *
     * @param quest 비율을 계산할 {@link Quest} (null 불가)
     * @return 신체 부위명(key)과 0.0~1.0 사이의 분배 비율(value) 맵
     */
    protected Map<String, Double> calculatePartRatios(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        Map<String, Integer> counts = new LinkedHashMap<>();
        int totalCount = 0;

        for (String muscle : quest.getMuscles()) {
            String bodyPart = Muscle.MUSCLE_TO_BODY.get(muscle);
            if (bodyPart != null) {
                counts.merge(bodyPart, 1, Integer::sum);
                totalCount++;
            }
        }

        Map<String, Double> ratios = new LinkedHashMap<>();
        if (totalCount > 0) {
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                ratios.put(entry.getKey(), (double) entry.getValue() / totalCount);
            }
            return ratios;
        }

        Set<String> parts = quest.getParts();
        if (!parts.isEmpty()) {
            double equalRatio = 1.0 / parts.size();
            for (String part : parts) {
                ratios.put(part, equalRatio);
            }
        }
        return ratios;
    }

    /**
     * 획득한 총 경험치를 퀘스트의 부위별 비율에 따라 각 {@link BodyPartProgress}에 분배합니다.
     *
     * @param quest 완료된 {@link Quest}
     * @param totalGainedExp 분배할 기준 총 경험치
     */
    protected void distributePartExp(Quest quest, int totalGainedExp) {
        if (totalGainedExp <= 0) {
            return;
        }
        Map<String, Double> ratios = calculatePartRatios(quest);
        for (Map.Entry<String, Double> entry : ratios.entrySet()) {
            BodyPartProgress progress = partProgressMap.get(entry.getKey());
            if (progress != null) {
                int partExp = (int) Math.round(totalGainedExp * entry.getValue());
                if (partExp == 0 && entry.getValue() > 0.0) {
                    partExp = 1;
                }
                progress.gainExp(partExp);
            }
        }
    }

    /**
     * 캐릭터 현재 경험치와 누적 총 경험치를 추가하고, 요구 경험치를 초과할 경우 연속 레벨업을 처리합니다.
     *
     * @param amount 획득할 경험치 (0 이상)
     */
    protected void gainCharacterExp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("획득 경험치는 0 이상이어야 합니다: " + amount);
        }
        this.totalExp += amount;
        this.exp += amount;
        while (this.exp >= getRequiredExp()) {
            this.exp -= getRequiredExp();
            this.level++;
        }
    }

    /**
     * 실패 페널티로 현재 레벨 내 캐릭터 경험치와 총 누적 경험치를 차감합니다 (레벨 다운 없음, 음수 하한 0 유지).
     *
     * @param amount 차감할 경험치 (0 이상)
     */
    protected void penalizeCharacterExp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("차감 경험치는 0 이상이어야 합니다: " + amount);
        }
        this.exp = Math.max(0, this.exp - amount);
        this.totalExp = Math.max(0, this.totalExp - amount);
    }

    /**
     * 저장 파일에서 불러온 캐릭터 상태와 성장 수치를 복원합니다.
     *
     * @param level 복원할 레벨 (1 이상)
     * @param exp 복원할 현재 레벨 경험치 (0 이상)
     * @param totalExp 복원할 누적 총 경험치 (0 이상)
     * @param streakDays 복원할 연속 달성 일수 (0 이상)
     * @param lastActiveDate 복원할 마지막 활동 날짜 (null 허용)
     * @param status 복원할 캐릭터 상태 문자열
     */
    public void restoreState(int level, int exp, int totalExp, int streakDays,
                             LocalDate lastActiveDate, String status) {
        if (level < 1 || exp < 0 || totalExp < 0 || streakDays < 0) {
            throw new IllegalArgumentException("잘못된 플레이어 복원 수치입니다.");
        }
        this.level = level;
        this.exp = exp;
        this.totalExp = totalExp;
        this.streakDays = streakDays;
        this.lastActiveDate = lastActiveDate;
        this.status = (status == null || status.isBlank()) ? CharacterStatus.NORMAL : status;
    }

    /**
     * 저장 파일에서 불러온 진행 중 퀘스트를 목록에 복원합니다.
     *
     * @param quest 복원할 진행 중 {@link Quest} (null 불가)
     */
    public void restoreActiveQuest(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        this.activeQuests.add(quest);
    }

    /**
     * 저장 파일에서 불러온 완료 퀘스트를 목록에 복원합니다.
     *
     * @param quest 복원할 완료 {@link Quest} (null 불가)
     */
    public void restoreCompletedQuest(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("퀘스트는 null일 수 없습니다.");
        }
        this.completedQuests.add(quest);
    }

    /**
     * 플레이어의 이름, 레벨, 총 경험치, 연속 달성 일수, 상태 및 퀘스트 진행 현황을 정형화된 문자열로 반환합니다.
     *
     * @return {@code "이름 | Lv.레벨 | EXP 총경험치\n연속 ?일 | 상태: ? | 퀘스트: 남은수/전체수"} 형식의 문자열
     */
    @Override
    public String toString() {
        return String.format("%s | Lv.%d | EXP %d\n연속 %d일 | 상태: %s | 퀘스트: %d/%d",
                name, level, totalExp, streakDays, status,
                activeQuests.size(), completedQuests.size() + activeQuests.size());
    }
}
