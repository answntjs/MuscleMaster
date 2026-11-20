package cli;

import common.DayOfWeek;
import common.ExerciseTemplate;
import quest.RoutineQuest;
import routine.Routine;
import routine.RoutineManager;
import routine.rule.DailyRule;
import routine.rule.IntervalRule;
import routine.rule.OneTimeRule;
import routine.rule.RoutineRule;
import routine.rule.WeeklyRule;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * 콘솔 기반 루틴 및 루틴 퀘스트 관리 메뉴를 제공하는 CLI 클래스입니다.
 */
public class RoutineMenu {
    private RoutineMenu() {
    }

    /**
     * 콘솔 기반 루틴 관리 메뉴를 실행합니다.
     *
     * @param routineManager 대상 {@link RoutineManager} (null 불가)
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     */
    public static void show(RoutineManager routineManager, Scanner scanner) {
        show(routineManager, scanner, null);
    }

    /**
     * 콘솔 기반 루틴 관리 메뉴를 실행하며, 변경 사항 발생 시 저장 콜백을 즉시 호출합니다.
     *
     * @param routineManager 대상 {@link RoutineManager} (null 불가)
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     * @param onSave 루틴이나 퀘스트가 수정될 때마다 호출할 저장 콜백 (null 허용)
     */
    public static void show(RoutineManager routineManager, Scanner scanner, Runnable onSave) {
        if (routineManager == null) {
            throw new IllegalArgumentException("RoutineManager는 null일 수 없습니다.");
        }
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner는 null일 수 없습니다.");
        }

        while (true) {
            System.out.println("\n=== 루틴 관리 ===");
            System.out.println("1. 루틴 조회");
            System.out.println("2. 루틴 검색");
            System.out.println("3. 루틴 추가");
            System.out.println("4. 루틴 수정");
            System.out.println("5. 뒤로");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listRoutines(routineManager);
                case "2" -> searchRoutineMenu(routineManager, scanner);
                case "3" -> addRoutineMenu(routineManager, scanner, onSave);
                case "4" -> editRoutineMenu(routineManager, scanner, onSave);
                case "5" -> {
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }

    private static void listRoutines(RoutineManager routineManager) {
        List<Routine> all = routineManager.getAllRoutines();
        if (all.isEmpty()) {
            System.out.println("등록된 루틴이 없습니다.");
            return;
        }
        for (Routine routine : all) {
            System.out.println(routine.getId() + ". " + routine.getName());
        }
    }

    private static void searchRoutineMenu(RoutineManager routineManager, Scanner scanner) {
        System.out.println("\n[루틴 검색]");
        System.out.println("1. 이름 검색");
        System.out.println("2. 부위로 검색");
        System.out.println("3. 근육으로 검색");
        System.out.println("4. 뒤로");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();

        if ("4".equals(choice)) {
            return;
        }
        if (!"1".equals(choice) && !"2".equals(choice) && !"3".equals(choice)) {
            System.out.println("올바른 번호를 입력하세요.");
            return;
        }

        System.out.print("검색어: ");
        String keyword = scanner.nextLine().trim();
        List<Routine> results = switch (choice) {
            case "1" -> routineManager.searchByName(keyword);
            case "2" -> routineManager.searchByBodyPart(keyword);
            case "3" -> routineManager.searchByMuscle(keyword);
            default -> List.of();
        };

        if (results.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        } else if (results.size() == 1) {
            System.out.println(results.get(0));
        } else {
            for (Routine r : results) {
                System.out.println(r.getId() + ". " + r.getName());
            }
        }
    }

    private static void addRoutineMenu(RoutineManager routineManager, Scanner scanner, Runnable onSave) {
        System.out.print("이름: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            System.out.println("루틴 이름은 비어 있을 수 없습니다.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate;
        while (true) {
            System.out.print("시작일(YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                startDate = LocalDate.parse(input);
                if (startDate.isBefore(today)) {
                    System.out.println("시작일은 과거일 수 없습니다. (" + today + " 이후 입력)");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("날짜 형식이 올바르지 않습니다. (예: " + today + ")");
            }
        }

        Routine routine = routineManager.addRoutine(new Routine(name, startDate, new DailyRule()));
        notifySaved(onSave);
        System.out.println("루틴이 추가되었습니다. (ID: " + routine.getId() + ")");
    }

    private static void editRoutineMenu(RoutineManager routineManager, Scanner scanner, Runnable onSave) {
        System.out.print("id: ");
        String idInput = scanner.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자 ID를 입력하세요.");
            return;
        }

        Routine routine = routineManager.getRoutine(id);
        if (routine == null) {
            System.out.println("해당 ID의 루틴을 찾을 수 없습니다.");
            return;
        }

        while (true) {
            System.out.println("\n" + routine);
            System.out.println("\n[루틴 수정]");
            System.out.println("1. 이름 수정");
            System.out.println("2. 규칙 수정");
            System.out.println("3. 루틴 삭제");
            System.out.println("4. 퀘스트 관리");
            System.out.println("5. 뒤로");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("이름: ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isBlank()) {
                        System.out.println("루틴 이름은 비어 있을 수 없습니다.");
                    } else {
                        routineManager.updateRoutineName(id, newName);
                        notifySaved(onSave);
                        System.out.println("루틴 이름이 수정되었습니다.");
                    }
                }
                case "2" -> {
                    System.out.println("매일 / ?일마다 / ?요일 / 한번만");
                    System.out.print("규칙: ");
                    String ruleInput = scanner.nextLine().trim();
                    try {
                        RoutineRule newRule = parseRuleInput(ruleInput);
                        routineManager.updateRoutineRule(id, newRule);
                        notifySaved(onSave);
                        System.out.println("규칙이 변경되었습니다: " + newRule.describe());
                    } catch (IllegalArgumentException e) {
                        System.out.println("규칙 입력 오류: " + e.getMessage());
                    }
                }
                case "3" -> {
                    System.out.print(routine.getName() + " 루틴을 삭제하시겠습니까?(y/n): ");
                    String confirm = scanner.nextLine().trim();
                    if ("y".equalsIgnoreCase(confirm)) {
                        routineManager.removeRoutine(id);
                        notifySaved(onSave);
                        System.out.println("루틴이 삭제되었습니다.");
                        return;
                    }
                }
                case "4" -> questManageMenu(scanner, routine, onSave);
                case "5" -> {
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }

    private static void questManageMenu(Scanner scanner, Routine routine, Runnable onSave) {
        while (true) {
            System.out.println("\n=== 퀘스트 관리 (" + routine.getName() + ") ===");
            System.out.println("1. 퀘스트 조회");
            System.out.println("2. 퀘스트 검색");
            System.out.println("3. 퀘스트 추가");
            System.out.println("4. 퀘스트 수정");
            System.out.println("5. 뒤로");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listRoutineQuests(routine);
                case "2" -> searchRoutineQuestMenu(scanner, routine);
                case "3" -> addRoutineQuestMenu(scanner, routine, onSave);
                case "4" -> editRoutineQuestMenu(scanner, routine, onSave);
                case "5" -> {
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }

    private static void listRoutineQuests(Routine routine) {
        List<RoutineQuest> quests = routine.getQuests();
        if (quests.isEmpty()) {
            System.out.println("등록된 퀘스트가 없습니다.");
            return;
        }
        for (int i = 0; i < quests.size(); i++) {
            System.out.println((i + 1) + ". " + quests.get(i).getName());
        }
    }

    private static void searchRoutineQuestMenu(Scanner scanner, Routine routine) {
        System.out.println("\n[퀘스트 검색]");
        System.out.println("1. 이름 검색");
        System.out.println("2. 부위로 검색");
        System.out.println("3. 근육으로 검색");
        System.out.println("4. 뒤로");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();

        if ("4".equals(choice)) {
            return;
        }
        if (!"1".equals(choice) && !"2".equals(choice) && !"3".equals(choice)) {
            System.out.println("올바른 번호를 입력하세요.");
            return;
        }

        System.out.print("검색어: ");
        String keyword = scanner.nextLine().trim();
        String lower = keyword.toLowerCase();
        List<RoutineQuest> results = new ArrayList<>();
        for (RoutineQuest q : routine.getQuests()) {
            boolean match = switch (choice) {
                case "1" -> !keyword.isBlank() && q.getName().toLowerCase().contains(lower);
                case "2" -> q.getParts().contains(keyword);
                case "3" -> q.getMuscles().contains(keyword);
                default -> false;
            };
            if (match) {
                results.add(q);
            }
        }

        if (results.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        } else if (results.size() == 1) {
            System.out.println(results.get(0));
        } else {
            for (RoutineQuest q : results) {
                System.out.println(q.getName());
            }
        }
    }

    private static void addRoutineQuestMenu(Scanner scanner, Routine routine, Runnable onSave) {
        System.out.println("\n[퀘스트 추가]");
        System.out.println("1. 운동 템플릿에서 선택");
        System.out.println("2. 직접 입력");
        System.out.println("3. 뒤로");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("템플릿 검색어(엔터 시 전체): ");
                String keyword = scanner.nextLine().trim();
                List<RoutineQuest> templates = ExerciseTemplate.searchByKeyword(keyword);
                if (templates.isEmpty()) {
                    System.out.println("일치하는 운동 템플릿이 없습니다.");
                    return;
                }
                for (int i = 0; i < templates.size(); i++) {
                    RoutineQuest t = templates.get(i);
                    System.out.println((i + 1) + ". " + t.getName() + " (" + t.getGoalSet() + "세트, EXP " + t.getExp() + ")");
                }
                System.out.print("번호 선택: ");
                int idx;
                try {
                    idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                } catch (NumberFormatException e) {
                    System.out.println("올바른 번호를 입력하세요.");
                    return;
                }
                if (idx < 0 || idx >= templates.size()) {
                    System.out.println("범위를 벗어난 번호입니다.");
                    return;
                }
                RoutineQuest selected = templates.get(idx);
                routine.addQuest(selected);
                notifySaved(onSave);
                System.out.println("퀘스트가 추가되었습니다: " + selected.getName());
            }
            case "2" -> {
                System.out.print("이름: ");
                String name = scanner.nextLine().trim();
                if (name.isBlank()) {
                    System.out.println("퀘스트 이름은 비어 있을 수 없습니다.");
                    return;
                }
                int goalSet = readPositiveInt(scanner, "목표 세트: ");
                if (goalSet <= 0) {
                    return;
                }
                int exp = readNonNegativeInt(scanner, "보상 경험치: ");
                if (exp < 0) {
                    return;
                }
                System.out.print("근육(쉼표 구분): ");
                String musclesRaw = scanner.nextLine().trim();
                String[] muscles = parseCommaList(musclesRaw);
                routine.addQuest(new RoutineQuest(name, goalSet, exp, muscles));
                notifySaved(onSave);
                System.out.println("퀘스트가 추가되었습니다: " + name);
            }
            case "3" -> {
                // 뒤로
            }
            default -> System.out.println("올바른 번호를 입력하세요.");
        }
    }

    private static void editRoutineQuestMenu(Scanner scanner, Routine routine, Runnable onSave) {
        List<RoutineQuest> quests = routine.getQuests();
        if (quests.isEmpty()) {
            System.out.println("수정할 퀘스트가 없습니다.");
            return;
        }
        listRoutineQuests(routine);
        System.out.print("번호: ");
        int idx;
        try {
            idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("올바른 번호를 입력하세요.");
            return;
        }
        if (idx < 0 || idx >= quests.size()) {
            System.out.println("해당 번호의 퀘스트를 찾을 수 없습니다.");
            return;
        }

        RoutineQuest quest = quests.get(idx);
        while (true) {
            System.out.println("\n" + quest);
            System.out.println("\n[퀘스트 수정]");
            System.out.println("1. 이름 수정");
            System.out.println("2. 목표 세트 수정");
            System.out.println("3. 보상 경험치 수정");
            System.out.println("4. 근육 수정");
            System.out.println("5. 퀘스트 삭제");
            System.out.println("6. 뒤로");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("이름: ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isBlank()) {
                        System.out.println("퀘스트 이름은 비어 있을 수 없습니다.");
                    } else {
                        quest.setName(newName);
                        notifySaved(onSave);
                        System.out.println("퀘스트 이름이 수정되었습니다.");
                    }
                }
                case "2" -> {
                    int goalSet = readPositiveInt(scanner, "목표 세트: ");
                    if (goalSet > 0) {
                        quest.setGoalSet(goalSet);
                        notifySaved(onSave);
                        System.out.println("목표 세트가 수정되었습니다.");
                    }
                }
                case "3" -> {
                    int exp = readNonNegativeInt(scanner, "보상 경험치: ");
                    if (exp >= 0) {
                        quest.setExp(exp);
                        notifySaved(onSave);
                        System.out.println("보상 경험치가 수정되었습니다.");
                    }
                }
                case "4" -> {
                    System.out.print("근육(쉼표 구분): ");
                    String[] muscles = parseCommaList(scanner.nextLine().trim());
                    quest.setMuscles(muscles);
                    notifySaved(onSave);
                    System.out.println("자극 근육이 수정되었습니다.");
                }
                case "5" -> {
                    System.out.print(quest.getName() + " 퀘스트를 삭제하시겠습니까?(y/n): ");
                    if ("y".equalsIgnoreCase(scanner.nextLine().trim())) {
                        routine.removeQuest(idx);
                        notifySaved(onSave);
                        System.out.println("퀘스트가 삭제되었습니다.");
                        return;
                    }
                }
                case "6" -> {
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }

    /**
     * 사용자 입력 문자열을 {@link RoutineRule} 구현체로 파싱합니다.
     *
     * @param input {@code "매일"}, {@code "?일마다"}, {@code "?요일"}, {@code "한번만"} 형식의 문자열
     * @return 파싱된 {@link RoutineRule}
     */
    public static RoutineRule parseRuleInput(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("규칙이 비어 있습니다.");
        }
        String trimmed = input.trim();
        if ("매일".equals(trimmed) || "1일마다".equals(trimmed)) {
            return new DailyRule();
        }
        if (trimmed.endsWith("일마다")) {
            String numPart = trimmed.substring(0, trimmed.length() - "일마다".length()).trim();
            try {
                int days = Integer.parseInt(numPart);
                if (days == 1) {
                    return new DailyRule();
                }
                return new IntervalRule(days);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("간격 일수가 올바르지 않습니다: " + trimmed);
            }
        }
        if ("한번만".equals(trimmed) || "한 번만".equals(trimmed)) {
            return new OneTimeRule();
        }
        if (trimmed.startsWith("한번만 ") || trimmed.startsWith("한 번만 ")) {
            String datePart = trimmed.replaceFirst("^한\\s?번만\\s+", "").trim();
            try {
                return new OneTimeRule(LocalDate.parse(datePart));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다(YYYY-MM-DD): " + datePart);
            }
        }

        String normalized = trimmed;
        if (normalized.startsWith("매주")) {
            normalized = normalized.substring("매주".length()).trim();
        }
        if (normalized.endsWith("요일") && !normalized.contains(",") && !normalized.contains(" ")) {
            normalized = normalized.substring(0, normalized.length() - "요일".length()).trim();
        }
        String[] tokens = normalized.split("[,\\s]+");
        Set<Integer> days = new TreeSet<>();
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            String dayToken = token.endsWith("요일")
                    ? token.substring(0, token.length() - "요일".length())
                    : token;
            days.add(DayOfWeek.parseKorean(dayToken));
        }
        if (days.isEmpty()) {
            throw new IllegalArgumentException("인식할 수 없는 규칙입니다: " + trimmed);
        }
        return new WeeklyRule(days);
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            if (val <= 0) {
                System.out.println("1 이상의 숫자를 입력하세요.");
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자를 입력하세요.");
            return -1;
        }
    }

    private static int readNonNegativeInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            if (val < 0) {
                System.out.println("0 이상의 숫자를 입력하세요.");
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자를 입력하세요.");
            return -1;
        }
    }

    private static String[] parseCommaList(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[0];
        }
        String[] parts = raw.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            if (!p.isBlank()) {
                list.add(p.trim());
            }
        }
        return list.toArray(new String[0]);
    }

    private static void notifySaved(Runnable onSave) {
        if (onSave != null) {
            onSave.run();
        }
    }
}
