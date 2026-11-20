package user;

import common.DayOfWeek;
import quest.Quest;
import quest.RoutineQuest;
import routine.Routine;
import routine.rule.DailyRule;
import routine.rule.IntervalRule;
import routine.rule.OneTimeRule;
import routine.rule.RoutineRule;
import routine.rule.WeeklyRule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 플레이어 데이터(캐릭터 정보, 부위별 성장도, 루틴 및 퀘스트 상태)를 UTF-8 텍스트 파일로 저장하고 불러오는 유틸리티 클래스입니다.
 * 루틴 ID 등 실행 시 재계산되는 요소는 저장하지 않으며, 불러올 때 자동으로 다시 할당됩니다.
 */
public class PlayerStorage {
    /** 기본 저장 파일 경로 */
    public static final Path DEFAULT_PATH = Path.of("save.dat");

    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private PlayerStorage() {
    }

    /**
     * 기본 저장 파일({@link #DEFAULT_PATH})이 존재하는지 확인합니다.
     *
     * @return 저장 파일이 존재하고 일반 파일이면 true
     */
    public static boolean exists() {
        return exists(DEFAULT_PATH);
    }

    /**
     * 지정한 경로에 저장 파일이 존재하는지 확인합니다.
     *
     * @param path 확인할 파일 경로
     * @return 파일이 존재하고 일반 파일이면 true
     */
    public static boolean exists(Path path) {
        return path != null && Files.isRegularFile(path);
    }

    /**
     * 플레이어 상태를 기본 저장 파일({@link #DEFAULT_PATH})에 저장합니다.
     *
     * @param player 저장할 {@link Player} 인스턴스 (null 불가)
     */
    public static void save(Player player) {
        save(player, DEFAULT_PATH);
    }

    /**
     * 플레이어 상태를 지정한 경로의 파일에 UTF-8 텍스트 형식으로 저장합니다.
     *
     * @param player 저장할 {@link Player} 인스턴스 (null 불가)
     * @param path 저장할 파일 경로 (null 불가)
     */
    public static void save(Player player, Path path) {
        if (player == null) {
            throw new IllegalArgumentException("플레이어는 null일 수 없습니다.");
        }
        if (path == null) {
            throw new IllegalArgumentException("저장 경로는 null일 수 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("# MuscleMaster Save File\n");
        sb.append("[PLAYER]\n");
        sb.append("name=").append(escape(player.getName())).append('\n');
        sb.append("level=").append(player.getLevel()).append('\n');
        sb.append("exp=").append(player.getExp()).append('\n');
        sb.append("totalExp=").append(player.getTotalExp()).append('\n');
        sb.append("streakDays=").append(player.getStreakDays()).append('\n');
        sb.append("lastActiveDate=").append(player.getLastActiveDate() != null ? player.getLastActiveDate() : "").append('\n');
        sb.append("status=").append(escape(player.getStatus())).append('\n');

        for (BodyPartProgress part : player.getPartProgressMap().values()) {
            sb.append("\n[BODY_PART]\n");
            sb.append("partName=").append(escape(part.getPartName())).append('\n');
            sb.append("level=").append(part.getLevel()).append('\n');
            sb.append("exp=").append(part.getExp()).append('\n');
            sb.append("totalExp=").append(part.getTotalExp()).append('\n');
        }

        for (Routine routine : player.getRoutineManager().getAllRoutines()) {
            sb.append("\n[ROUTINE]\n");
            sb.append("name=").append(escape(routine.getName())).append('\n');
            sb.append("startDate=").append(routine.getStartDate()).append('\n');
            sb.append("active=").append(routine.isActive()).append('\n');
            sb.append("lastGrantedDate=").append(routine.getLastGrantedDate() != null ? routine.getLastGrantedDate() : "").append('\n');
            sb.append("rule=").append(serializeRule(routine.getRule())).append('\n');

            for (RoutineQuest rq : routine.getQuests()) {
                sb.append("[ROUTINE_QUEST]\n");
                sb.append("name=").append(escape(rq.getName())).append('\n');
                sb.append("goalSet=").append(rq.getGoalSet()).append('\n');
                sb.append("exp=").append(rq.getExp()).append('\n');
                sb.append("muscles=").append(joinEscapedList(rq.getMuscles())).append('\n');
            }
        }

        for (Quest quest : player.getActiveQuests()) {
            appendQuestSection(sb, "[ACTIVE_QUEST]", quest);
        }

        for (Quest quest : player.getCompletedQuests()) {
            appendQuestSection(sb, "[COMPLETED_QUEST]", quest);
        }

        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("저장 파일 쓰기에 실패했습니다: " + path, e);
        }
    }

    /**
     * 기본 저장 파일({@link #DEFAULT_PATH})에서 플레이어 상태를 불러옵니다.
     *
     * @return 복원된 {@link Player} 인스턴스
     */
    public static Player load() {
        return load(DEFAULT_PATH);
    }

    /**
     * 지정한 경로의 저장 파일에서 플레이어 상태를 불러옵니다.
     *
     * @param path 불러올 파일 경로 (null 불가)
     * @return 복원된 {@link Player} 인스턴스
     */
    public static Player load(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("불러올 경로는 null일 수 없습니다.");
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("저장 파일 읽기에 실패했습니다: " + path, e);
        }

        List<Section> sections = parseSections(lines);
        Section playerSection = null;
        for (Section section : sections) {
            if ("[PLAYER]".equals(section.type)) {
                playerSection = section;
                break;
            }
        }
        if (playerSection == null) {
            throw new IllegalStateException("저장 파일에 [PLAYER] 섹션이 없습니다.");
        }

        String playerName = unescape(playerSection.getRequired("name"));
        Player player = new Player(playerName);
        player.restoreState(
                Integer.parseInt(playerSection.getOrDefault("level", "1")),
                Integer.parseInt(playerSection.getOrDefault("exp", "0")),
                Integer.parseInt(playerSection.getOrDefault("totalExp", "0")),
                Integer.parseInt(playerSection.getOrDefault("streakDays", "0")),
                parseNullableDate(playerSection.getOrDefault("lastActiveDate", "")),
                unescape(playerSection.getOrDefault("status", common.CharacterStatus.NORMAL))
        );

        Routine currentRoutine = null;
        for (Section section : sections) {
            switch (section.type) {
                case "[PLAYER]":
                    break;
                case "[BODY_PART]": {
                    String partName = unescape(section.getRequired("partName"));
                    BodyPartProgress progress = player.getPartProgress(partName);
                    if (progress != null) {
                        progress.restore(
                                Integer.parseInt(section.getOrDefault("level", "1")),
                                Integer.parseInt(section.getOrDefault("exp", "0")),
                                Integer.parseInt(section.getOrDefault("totalExp", "0"))
                        );
                    }
                    break;
                }
                case "[ROUTINE]": {
                    String routineName = unescape(section.getRequired("name"));
                    LocalDate startDate = LocalDate.parse(section.getRequired("startDate"));
                    boolean active = Boolean.parseBoolean(section.getOrDefault("active", "true"));
                    LocalDate lastGranted = parseNullableDate(section.getOrDefault("lastGrantedDate", ""));
                    RoutineRule rule = deserializeRule(section.getRequired("rule"));

                    currentRoutine = new Routine(routineName, startDate, rule);
                    currentRoutine.setActive(active);
                    currentRoutine.restoreLastGrantedDate(lastGranted);
                    player.getRoutineManager().addRoutine(currentRoutine);
                    break;
                }
                case "[ROUTINE_QUEST]": {
                    if (currentRoutine == null) {
                        throw new IllegalStateException("[ROUTINE] 없이 [ROUTINE_QUEST]가 존재합니다.");
                    }
                    RoutineQuest rq = buildRoutineQuest(section);
                    currentRoutine.addQuest(rq);
                    break;
                }
                case "[ACTIVE_QUEST]": {
                    player.restoreActiveQuest(buildQuest(section));
                    break;
                }
                case "[COMPLETED_QUEST]": {
                    player.restoreCompletedQuest(buildQuest(section));
                    break;
                }
                default:
                    throw new IllegalStateException("알 수 없는 저장 섹션입니다: " + section.type);
            }
        }
        return player;
    }

    private static void appendQuestSection(StringBuilder sb, String header, Quest quest) {
        sb.append('\n').append(header).append('\n');
        sb.append("name=").append(escape(quest.getName())).append('\n');
        sb.append("goalSet=").append(quest.getGoalSet()).append('\n');
        sb.append("exp=").append(quest.getExp()).append('\n');
        sb.append("currentSet=").append(quest.getCurrentSet()).append('\n');
        sb.append("completed=").append(quest.isCompleted()).append('\n');
        sb.append("muscles=").append(joinEscapedList(quest.getMuscles())).append('\n');
    }

    private static RoutineQuest buildRoutineQuest(Section section) {
        String name = unescape(section.getRequired("name"));
        int goalSet = Integer.parseInt(section.getRequired("goalSet"));
        int exp = Integer.parseInt(section.getRequired("exp"));
        String[] muscles = splitEscapedList(section.getOrDefault("muscles", ""));
        return new RoutineQuest(name, goalSet, exp, muscles);
    }

    private static Quest buildQuest(Section section) {
        String name = unescape(section.getRequired("name"));
        int goalSet = Integer.parseInt(section.getRequired("goalSet"));
        int exp = Integer.parseInt(section.getRequired("exp"));
        int currentSet = Integer.parseInt(section.getOrDefault("currentSet", "0"));
        boolean completed = Boolean.parseBoolean(section.getOrDefault("completed", "false"));
        String[] muscles = splitEscapedList(section.getOrDefault("muscles", ""));
        Quest quest = new Quest(name, goalSet, exp, muscles);
        quest.restoreProgress(currentSet, completed);
        return quest;
    }

    private static String serializeRule(RoutineRule rule) {
        if (rule instanceof DailyRule) {
            return "DAILY";
        }
        if (rule instanceof WeeklyRule weekly) {
            String dayCodes = weekly.getDays().stream()
                    .map(DayOfWeek::toCode)
                    .collect(Collectors.joining(","));
            return "WEEKLY:" + dayCodes;
        }
        if (rule instanceof IntervalRule interval) {
            return "INTERVAL:" + interval.getIntervalDays();
        }
        if (rule instanceof OneTimeRule oneTime) {
            LocalDate target = oneTime.getTargetDate();
            return target == null ? "ONETIME" : "ONETIME:" + target;
        }
        throw new IllegalArgumentException("지원하지 않는 루틴 규칙입니다: " + rule.getClass().getName());
    }

    private static RoutineRule deserializeRule(String raw) {
        if ("DAILY".equals(raw)) {
            return new DailyRule();
        }
        if (raw.startsWith("WEEKLY:")) {
            String[] tokens = raw.substring("WEEKLY:".length()).split(",");
            Set<Integer> days = new TreeSet<>();
            for (String token : tokens) {
                if (!token.isBlank()) {
                    days.add(DayOfWeek.fromCode(token));
                }
            }
            return new WeeklyRule(days);
        }
        if (raw.startsWith("INTERVAL:")) {
            int interval = Integer.parseInt(raw.substring("INTERVAL:".length()));
            return new IntervalRule(interval);
        }
        if ("ONETIME".equals(raw)) {
            return new OneTimeRule();
        }
        if (raw.startsWith("ONETIME:")) {
            LocalDate date = LocalDate.parse(raw.substring("ONETIME:".length()));
            return new OneTimeRule(date);
        }
        throw new IllegalStateException("알 수 없는 규칙 데이터입니다: " + raw);
    }

    private static LocalDate parseNullableDate(String text) {
        return (text == null || text.isBlank()) ? null : LocalDate.parse(text.trim());
    }

    private static String joinEscapedList(Iterable<String> items) {
        List<String> list = new ArrayList<>();
        for (String item : items) {
            list.add(escape(item).replace(",", "\\c"));
        }
        list.sort(String::compareTo);
        return String.join(",", list);
    }

    private static String[] splitEscapedList(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[0];
        }
        String[] parts = raw.split(",");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            if (!part.isBlank()) {
                result.add(unescape(part.replace("\\c", ",")));
            }
        }
        return result.toArray(new String[0]);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private static String unescape(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);
                switch (next) {
                    case '\\' -> sb.append('\\');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    default -> {
                        sb.append('\\').append(next);
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static List<Section> parseSections(List<String> lines) {
        List<Section> sections = new ArrayList<>();
        Section current = null;
        for (String rawLine : lines) {
            String line = rawLine.stripTrailing();
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                current = new Section(line);
                sections.add(current);
            } else if (current != null) {
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String key = line.substring(0, eq);
                    String val = line.substring(eq + 1);
                    current.fields.put(key, val);
                }
            }
        }
        return sections;
    }

    private static final class Section {
        private final String type;
        private final Map<String, String> fields = new LinkedHashMap<>();

        private Section(String type) {
            this.type = type;
        }

        private String getRequired(String key) {
            String val = fields.get(key);
            if (val == null) {
                throw new IllegalStateException(type + " 섹션에 필수 키가 없습니다: " + key);
            }
            return val;
        }

        private String getOrDefault(String key, String defaultValue) {
            return fields.getOrDefault(key, defaultValue);
        }
    }
}
