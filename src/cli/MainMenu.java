package cli;

import user.Player;
import user.PlayerStorage;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * MuscleMaster 콘솔 애플리케이션의 메인 메뉴 흐름을 담당하는 CLI 클래스입니다.
 */
public class MainMenu {
    private MainMenu() {
    }

    /**
     * 기본 표준 입력과 기본 저장 파일을 사용하여 콘솔 애플리케이션 메뉴를 시작합니다.
     */
    public static void start() {
        start(new Scanner(System.in), PlayerStorage.DEFAULT_PATH);
    }

    /**
     * 지정한 입력 스캐너와 기본 저장 파일을 사용하여 콘솔 애플리케이션 메뉴를 시작합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     */
    public static void start(Scanner scanner) {
        start(scanner, PlayerStorage.DEFAULT_PATH);
    }

    /**
     * 지정한 입력 스캐너와 저장 파일 경로를 사용하여 콘솔 애플리케이션 메뉴를 시작합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     * @param savePath 플레이어 데이터를 저장/불러올 파일 경로 (null 불가)
     */
    public static void start(Scanner scanner, Path savePath) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner는 null일 수 없습니다.");
        }
        if (savePath == null) {
            throw new IllegalArgumentException("저장 경로는 null일 수 없습니다.");
        }

        Player player = loadOrCreatePlayer(scanner, savePath);
        Runnable onSave = () -> PlayerStorage.save(player, savePath);

        while (true) {
            System.out.println("\n=== 메인 메뉴 ===");
            System.out.println("1. 캐릭터 정보");
            System.out.println("2. 루틴 관리");
            System.out.println("3. 퀘스트");
            System.out.println("4. 도움말");
            System.out.println("5. 크레딧");
            System.out.println("6. 종료");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> CharacterMenu.show(scanner, player, onSave);
                case "2" -> RoutineMenu.show(player.getRoutineManager(), scanner, onSave);
                case "3" -> QuestMenu.show(scanner, player, onSave);
                case "4" -> System.out.println("미완성입니다");
                case "5" -> System.out.println("김용현, 김지형, 문주선, 박진용");
                case "6" -> {
                    System.out.println("프로그램을 종료합니다.");
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }

    private static Player loadOrCreatePlayer(Scanner scanner, Path savePath) {
        if (PlayerStorage.exists(savePath)) {
            Player loaded = PlayerStorage.load(savePath);
            System.out.println(loaded.getName() + "님의 데이터를 불러왔습니다.");
            return loaded;
        }
        System.out.println("저장된 유저 파일이 없습니다. 새 유저를 생성합니다.");
        while (true) {
            System.out.print("이름: ");
            String name = scanner.nextLine().trim();
            if (!name.isBlank()) {
                Player created = new Player(name);
                PlayerStorage.save(created, savePath);
                System.out.println(created.getName() + "님이 생성되었습니다.");
                return created;
            }
            System.out.println("이름은 비어 있을 수 없습니다.");
        }
    }
}
