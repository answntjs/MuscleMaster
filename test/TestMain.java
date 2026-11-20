import cli.MainMenu;
import quest.Quest;
import quest.RoutineQuest;
import routine.Routine;
import routine.rule.DailyRule;
import routine.rule.IntervalRule;
import routine.rule.OneTimeRule;
import routine.rule.WeeklyRule;
import user.BodyPartProgress;
import common.CharacterStatus;
import user.Player;
import user.PlayerStorage;

import common.DayOfWeek;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * 루틴, 퀘스트, 플레이어 성장 및 운동 템플릿 기능을 검증하는 테스트 실행 클래스입니다.
 */
public class TestMain {
    /**
     * 테스트 실행 클래스의 기본 인스턴스를 생성합니다.
     */
    public TestMain() {
    }

    /**
     * 전체 통합 검증 시나리오를 실행합니다.
     *
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        Player player = new Player("테스트유저");

        // 1. 루틴 등록 및 날짜별 퀘스트 발급 테스트
        Routine upperRoutine = new Routine("상체 루틴", LocalDate.of(2026, 11, 1), new DailyRule());
        // 등 1개(광배근), 어깨 2개(삼각근, 회전근개) -> 등 1/3, 어깨 2/3 비율 테스트용 (EXP 90)
        upperRoutine.addQuest(new RoutineQuest("풀업 & 페이스풀", 3, 90, "광배근", "삼각근", "회전근개"));
        player.getRoutineManager().addRoutine(upperRoutine);

        LocalDate day1 = LocalDate.of(2026, 11, 1);
        List<Quest> day1Quests = player.assignQuestsForDate(day1);
        assertEquals(1, day1Quests.size(), "Day 1 퀘스트 발급 개수");
        assertEquals(1, player.getActiveQuests().size(), "진행중 퀘스트 개수");

        // 2. 부분 세트 진행 후 완료 (부위별 비율 EXP & 부위/캐릭터 레벨업 검증)
        Quest q1 = day1Quests.get(0);
        assertFalse(player.progressQuest(q1, 2, day1), "2세트 수행 시 미완료");
        assertTrue(player.progressQuest(q1, 1, day1), "3세트 완료 시 완료");
        assertEquals(0, player.getActiveQuests().size(), "완료 후 진행중 퀘스트 비움");
        assertEquals(1, player.getCompletedQuests().size(), "완료된 퀘스트 이동");
        assertEquals(1, player.getStreakDays(), "첫날 streakDays");
        assertEquals(CharacterStatus.NORMAL, player.getStatus(), "첫날 상태 NORMAL");

        // EXP 90 획득: 캐릭터 Lv.1(요구 100) -> Lv.1, exp 90
        assertEquals(1, player.getLevel(), "Day 1 캐릭터 레벨");
        assertEquals(90, player.getExp(), "Day 1 캐릭터 EXP");

        // 부위별: 등 30 (Lv.1, 30/50), 어깨 60 (Lv.2, 10/85)
        BodyPartProgress back = player.getPartProgress("등");
        BodyPartProgress shoulder = player.getPartProgress("어깨");
        assertEquals(1, back.getLevel(), "등 부위 레벨");
        assertEquals(30, back.getExp(), "등 부위 EXP (1/3)");
        assertEquals(30, back.getTotalExp(), "등 부위 누적 EXP");
        assertEquals(2, shoulder.getLevel(), "어깨 부위 레벨업 (60 >= 50)");
        assertEquals(10, shoulder.getExp(), "어깨 부위 잔여 EXP (60 - 50)");
        assertEquals(60, shoulder.getTotalExp(), "어깨 부위 누적 EXP (2/3)");

        // 3. 연속 3일 달성 -> ON_FIRE 보너스 & 캐릭터 레벨업
        LocalDate day2 = day1.plusDays(1);
        Quest q2 = player.assignQuestsForDate(day2).get(0);
        assertTrue(player.progressQuest(q2, 3, day2), "Day 2 완료");
        assertEquals(2, player.getStreakDays(), "Day 2 streakDays");
        assertTrue(player.getLevel() >= 2, "Day 2 캐릭터 레벨업");

        LocalDate day3 = day2.plusDays(1);
        Quest q3 = player.assignQuestsForDate(day3).get(0);
        assertTrue(player.progressQuest(q3, 3, day3), "Day 3 완료");
        assertEquals(3, player.getStreakDays(), "Day 3 streakDays");
        assertEquals(CharacterStatus.ON_FIRE, player.getStatus(), "3일 연속 달성 시 ON_FIRE");

        // 4. 루틴 실패 -> EXP 차감(레벨 유지), streak 초기화, PENALTY 상태
        LocalDate day4 = day3.plusDays(1);
        player.assignQuestsForDate(day4);
        int levelBeforeFail = player.getLevel();
        int expBeforeFail = player.getExp();
        player.failAllActiveQuests();

        assertEquals(0, player.getActiveQuests().size(), "실패 정산 후 activeQuests 비움");
        assertEquals(levelBeforeFail, player.getLevel(), "경험치 차감 시 레벨 차감 없음");
        assertEquals(Math.max(0, expBeforeFail - 45), player.getExp(), "미완료 퀘스트 EXP의 50% 차감");
        assertEquals(0, player.getStreakDays(), "실패 시 streakDays 초기화");
        assertEquals(CharacterStatus.PENALTY, player.getStatus(), "실패 시 PENALTY 상태");

        // 5. 레벨 1 exp=0에서 실패해도 음수나 레벨 다운 없음
        Player newbie = new Player("초보자");
        Routine r2 = new Routine("하체", day1, new DailyRule());
        r2.addQuest(new RoutineQuest("스쿼트", 3, 100, "대퇴사두근"));
        newbie.getRoutineManager().addRoutine(r2);
        newbie.assignQuestsForDate(day1);
        newbie.failAllActiveQuests();
        assertEquals(1, newbie.getLevel(), "Lv.1 유지");
        assertEquals(0, newbie.getExp(), "EXP 하한 0 유지");

        // 6. ExerciseTemplate 검증 (모든 근육/부위 커버리지, 상수 직접 사용, 복사/조회, 루틴 연동)
        assertTrue(common.ExerciseTemplate.TEMPLATES.length >= 20, "대중적 운동 템플릿 개수");
        for (String muscle : common.Muscle.MUSCLES) {
            assertFalse(common.ExerciseTemplate.getByMuscle(muscle).isEmpty(), "근육 커버리지: " + muscle);
        }
        for (String bodyPart : common.Muscle.BODYPARTS) {
            assertFalse(common.ExerciseTemplate.getByBodyPart(bodyPart).isEmpty(), "부위 커버리지: " + bodyPart);
        }

        RoutineQuest pushUp = common.ExerciseTemplate.findByName("팔굽혀펴기");
        assertEquals(common.ExerciseTemplate.PUSH_UP.getName(), pushUp.getName(), "이름으로 팔굽혀펴기 조회");
        assertEquals("윗몸일으키기", common.ExerciseTemplate.SIT_UP.getName(), "상수 SIT_UP 이름");
        assertEquals("스쿼트", common.ExerciseTemplate.SQUAT.getName(), "상수 SQUAT 이름");
        assertFalse(common.ExerciseTemplate.searchByKeyword("팔굽").isEmpty(), "키워드 검색");

        pushUp.setGoalSet(99);
        pushUp.getMuscles().clear();
        assertEquals(3, common.ExerciseTemplate.PUSH_UP.getGoalSet(), "상수 기본 세트 수 유지");
        assertFalse(common.ExerciseTemplate.PUSH_UP.getMuscles().isEmpty(), "상수 근육 목록 유지");

        Player templateUser = new Player("템플릿유저");
        Routine homeRoutine = new Routine("맨몸 3대 루틴", day1, new DailyRule());
        homeRoutine.addQuest(common.ExerciseTemplate.PUSH_UP);
        homeRoutine.addQuest(common.ExerciseTemplate.copy(common.ExerciseTemplate.SIT_UP, 5));
        homeRoutine.addQuest(common.ExerciseTemplate.copy(common.ExerciseTemplate.SQUAT, 4, 120));
        templateUser.getRoutineManager().addRoutine(homeRoutine);

        List<Quest> granted = templateUser.assignQuestsForDate(day1);
        assertEquals(3, granted.size(), "템플릿 기반 루틴 퀘스트 3개 발급");
        assertEquals(3, granted.get(0).getGoalSet(), "팔굽혀펴기 기본 세트");
        assertEquals(5, granted.get(1).getGoalSet(), "윗몸일으키기 커스텀 세트");
        assertEquals(120, granted.get(2).getExp(), "스쿼트 커스텀 EXP");

        // 7. 정형화된 toString() 및 totalExp 검증
        assertEquals("매일", new DailyRule().toString(), "DailyRule toString");
        assertEquals("매주 월, 수, 금요일",
                new WeeklyRule(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY).toString(),
                "WeeklyRule toString");
        assertEquals("3일마다", new IntervalRule(3).toString(), "IntervalRule toString");
        assertEquals("한번만", new OneTimeRule().toString(), "OneTimeRule(기본) toString");
        assertEquals("한번만 (2026-11-10)",
                new OneTimeRule(LocalDate.of(2026, 11, 10)).toString(),
                "OneTimeRule(날짜지정) toString");

        RoutineQuest rqTest = new RoutineQuest("벤치프레스", 4, 90, "대흉근", "삼각근");
        assertEquals("벤치프레스 | EXP 90 | 4세트\n부위: 가슴, 어깨\n근육: 대흉근, 삼각근",
                rqTest.toString(), "RoutineQuest toString");

        Quest qTest = rqTest.createQuest();
        assertEquals("벤치프레스 | EXP 90 | 0/4\n부위: 가슴, 어깨\n근육: 대흉근, 삼각근",
                qTest.toString(), "Quest 진행전 toString");
        qTest.doSet(2);
        assertEquals("벤치프레스 | EXP 90 | 2/4\n부위: 가슴, 어깨\n근육: 대흉근, 삼각근",
                qTest.toString(), "Quest 진행중 toString");
        qTest.doSet(2);
        assertEquals("벤치프레스 | EXP 90 | Completed!\n부위: 가슴, 어깨\n근육: 대흉근, 삼각근",
                qTest.toString(), "Quest 완료 toString");

        assertEquals("맨몸 3대 루틴", homeRoutine.getName(), "루틴 이름 유지");
        assertEquals("1. 맨몸 3대 루틴 | 매일\n시작일: 2026-11-01 | 마지막 부여: 2026-11-01\n퀘스트: 팔굽혀펴기, 윗몸일으키기, 스쿼트",
                homeRoutine.toString(), "활성 Routine toString");
        homeRoutine.setActive(false);
        assertEquals("1. 맨몸 3대 루틴(비활성) | 매일\n시작일: 2026-11-01 | 마지막 부여: 2026-11-01\n퀘스트: 팔굽혀펴기, 윗몸일으키기, 스쿼트",
                homeRoutine.toString(), "비활성 Routine toString");
        Routine emptyRoutine = new Routine("빈 루틴", day1, new IntervalRule(2));
        assertEquals("0. 빈 루틴 | 2일마다\n시작일: 2026-11-01 | 마지막 부여: 없음\n퀘스트: 없음",
                emptyRoutine.toString(), "미부여 빈 Routine toString");

        BodyPartProgress chestPart = new BodyPartProgress("가슴");
        chestPart.gainExp(30);
        assertEquals("가슴 | Lv.1 | EXP 30 / 50\n총 경험치: 30",
                chestPart.toString(), "BodyPartProgress toString");

        assertEquals(
                String.format("%s | Lv.%d | EXP %d\n연속 %d일 | 상태: %s | 퀘스트: %d/%d",
                        player.getName(), player.getLevel(), player.getTotalExp(),
                        player.getStreakDays(), player.getStatus(),
                        player.getActiveQuests().size(),
                        player.getCompletedQuests().size() + player.getActiveQuests().size()),
                player.toString(), "Player toString");

        // 8. PlayerStorage 저장/불러오기 및 루틴 ID 미저장 검증
        try {
            Player saveSource = new Player("저장테스트");
            saveSource.setName("이름변경유저");
            Routine rDaily = new Routine("매일 루틴", day1, new DailyRule());
            rDaily.addQuest(new RoutineQuest("푸시업", 3, 60, "대흉근"));
            Routine rWeekly = new Routine("주간 루틴", day1, new WeeklyRule(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
            rWeekly.addQuest(new RoutineQuest("풀업", 3, 85, "광배근"));
            Routine rInterval = new Routine("간격 루틴", day1, new IntervalRule(3));
            Routine rOneTime = new Routine("단발 루틴", day1, new OneTimeRule(LocalDate.of(2026, 12, 1)));

            saveSource.getRoutineManager().addRoutine(rDaily);
            saveSource.getRoutineManager().addRoutine(rWeekly);
            saveSource.getRoutineManager().addRoutine(rInterval);
            saveSource.getRoutineManager().addRoutine(rOneTime);
            // ID=2 제거 후 저장 시 재시작하면 1, 2, 3으로 재할당되는지 검증
            saveSource.getRoutineManager().removeRoutine(2);

            List<Quest> grantedToSave = saveSource.assignQuestsForDate(day1);
            assertEquals(1, grantedToSave.size(), "저장 전 발급 퀘스트 수");
            saveSource.progressQuest(grantedToSave.get(0), 2, day1);

            Path tempSave = Files.createTempFile("muscle_master_test_", ".dat");
            try {
                PlayerStorage.save(saveSource, tempSave);
                String rawContent = Files.readString(tempSave, StandardCharsets.UTF_8);
                assertFalse(rawContent.contains("\nid="), "저장 파일에 루틴 id가 포함되지 않음");

                Player loaded = PlayerStorage.load(tempSave);
                assertEquals("이름변경유저", loaded.getName(), "로드된 이름");
                assertEquals(saveSource.getLevel(), loaded.getLevel(), "로드된 레벨");
                assertEquals(saveSource.getExp(), loaded.getExp(), "로드된 현재 EXP");
                assertEquals(saveSource.getTotalExp(), loaded.getTotalExp(), "로드된 총 EXP");
                assertEquals(3, loaded.getRoutineManager().getAllRoutines().size(), "로드된 루틴 개수");
                assertEquals(1, loaded.getRoutineManager().getAllRoutines().get(0).getId(), "재할당 ID 1");
                assertEquals(2, loaded.getRoutineManager().getAllRoutines().get(1).getId(), "재할당 ID 2");
                assertEquals(3, loaded.getRoutineManager().getAllRoutines().get(2).getId(), "재할당 ID 3");
                assertEquals("3일마다", loaded.getRoutineManager().getRoutine(2).getRule().describe(), "간격 규칙 복원");
                assertEquals("한번만 (2026-12-01)", loaded.getRoutineManager().getRoutine(3).getRule().describe(), "1회 규칙 복원");
                assertEquals(1, loaded.getActiveQuests().size(), "진행중 퀘스트 복원");
                assertEquals(2, loaded.getActiveQuests().get(0).getCurrentSet(), "진행중 퀘스트 세트 복원");
                assertEquals(1, loaded.getRoutineManager().searchByMuscle("대흉근").size(), "근육으로 루틴 검색");
            } finally {
                Files.deleteIfExists(tempSave);
            }
        } catch (Exception e) {
            throw new AssertionError("PlayerStorage 테스트 실패", e);
        }

        // 9. RoutineManager.menu() 콘솔 흐름 및 즉시 저장 콜백 검증
        Player menuUser = new Player("메뉴유저");
        int[] saveCount = {0};
        String todayStr = LocalDate.now().toString();
        String simulatedInput = String.join("\n",
                "3", "상체", todayStr,       // 루틴 추가 (ID=1)
                "4", "1",                    // 루틴 수정 ID=1
                "1", "상체 집중",            // 이름 수정
                "2", "월, 수, 금요일",       // 규칙 수정
                "4",                         // 퀘스트 관리 진입
                "3", "2", "벤치", "4", "80", "대흉근, 삼각근", // 직접 퀘스트 추가
                "4", "1", "2", "5", "6",     // 퀘스트 목표 세트 5로 수정 후 뒤로
                "5",                         // 퀘스트 관리 뒤로
                "5",                         // 루틴 수정 뒤로
                "5"                          // 루틴 관리 뒤로
        ) + "\n";
        menuUser.getRoutineManager().menu(new Scanner(simulatedInput), () -> saveCount[0]++);
        Routine edited = menuUser.getRoutineManager().getRoutine(1);
        assertEquals("상체 집중", edited.getName(), "메뉴로 수정된 루틴 이름");
        assertEquals("매주 월, 수, 금요일", edited.getRule().describe(), "메뉴로 수정된 루틴 규칙");
        assertEquals(1, edited.getQuests().size(), "메뉴로 추가된 루틴 퀘스트 수");
        assertEquals(5, edited.getQuests().get(0).getGoalSet(), "메뉴로 수정된 퀘스트 목표 세트");
        assertEquals(5, saveCount[0], "수정마다 즉시 저장 콜백 호출 횟수");

        // 10. cli 패키지(MainMenu, CharacterMenu, RoutineMenu, QuestMenu) 통합 흐름 검증
        try {
            Path cliSave = Files.createTempFile("muscle_master_cli_test_", ".dat");
            Files.deleteIfExists(cliSave); // 새 유저 생성 흐름 테스트를 위해 삭제
            try {
                String cliInput = String.join("\n",
                        "헬린이",                     // 새 유저 생성
                        "1", "1", "2", "헬스왕", "3", // 캐릭터 정보 -> 신체정보 -> 이름 바꾸기 -> 뒤로
                        "2", "3", "오늘루틴", todayStr, // 루틴 관리 -> 루틴 추가 (매일, ID=1)
                        "4", "1", "4", "3", "2", "스쿼트", "1", "50", "대퇴사두근", "5", "5", "5", // 1세트 퀘스트 추가 후 뒤로
                        "3", "1", "1", "2",           // 퀘스트 -> 1번 퀘스트 1세트 진행(완료) -> 완료된 퀘스트(1) -> 뒤로(2)
                        "4", "5", "6"                 // 도움말 -> 크레딧 -> 종료
                ) + "\n";
                MainMenu.start(new Scanner(cliInput), cliSave);

                Player savedCliUser = PlayerStorage.load(cliSave);
                assertEquals("헬스왕", savedCliUser.getName(), "CLI 캐릭터 메뉴 이름 변경 저장");
                assertEquals(1, savedCliUser.getRoutineManager().getAllRoutines().size(), "CLI 루틴 메뉴 루틴 저장");
                assertEquals(1, savedCliUser.getCompletedQuests().size(), "CLI 퀘스트 메뉴 완료 저장");
                assertEquals(50, savedCliUser.getTotalExp(), "CLI 퀘스트 완료 경험치 저장");
            } finally {
                Files.deleteIfExists(cliSave);
            }
        } catch (Exception e) {
            throw new AssertionError("CLI 통합 테스트 실패", e);
        }

        System.out.println("ALL TESTS PASSED!");
        System.out.println(player);
        System.out.println("  -> " + player.getPartProgress("등"));
        System.out.println("  -> " + player.getPartProgress("어깨"));
    }

    /**
     * 두 객체가 동등한지 검증하고, 다르면 {@link AssertionError}를 발생시킵니다.
     *
     * @param expected 기대값
     * @param actual 실제값
     * @param message 실패 시 출력할 검증 설명 메시지
     */
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - expected: " + expected + ", actual: " + actual);
        }
    }

    /**
     * 조건식이 참인지 검증하고, 거짓이면 {@link AssertionError}를 발생시킵니다.
     *
     * @param condition 검증할 boolean 조건식
     * @param message 실패 시 출력할 검증 설명 메시지
     */
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + " - expected true");
        }
    }

    /**
     * 조건식이 거짓인지 검증하고, 참이면 {@link AssertionError}를 발생시킵니다.
     *
     * @param condition 검증할 boolean 조건식
     * @param message 실패 시 출력할 검증 설명 메시지
     */
    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message + " - expected false");
        }
    }
}
