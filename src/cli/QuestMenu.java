package cli;

import quest.Quest;
import user.Player;
import user.PlayerStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * 콘솔 기반 퀘스트 조회 및 진행 메뉴를 제공하는 CLI 클래스입니다.
 */
public class QuestMenu {
    private QuestMenu() {
    }

    /**
     * 퀘스트 메뉴를 실행하고 변경 사항을 기본 저장 파일에 저장합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     * @param player 대상 {@link Player} (null 불가)
     */
    public static void show(Scanner scanner, Player player) {
        show(scanner, player, () -> PlayerStorage.save(player));
    }

    /**
     * 퀘스트 메뉴를 실행하며, 퀘스트 발급 또는 진행 시 저장 콜백을 호출합니다.
     *
     * @param scanner 사용자 입력을 받을 {@link Scanner} (null 불가)
     * @param player 대상 {@link Player} (null 불가)
     * @param onSave 상태 변경 시 호출할 저장 콜백 (null 허용)
     */
    public static void show(Scanner scanner, Player player, Runnable onSave) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner는 null일 수 없습니다.");
        }
        if (player == null) {
            throw new IllegalArgumentException("플레이어는 null일 수 없습니다.");
        }

        List<Quest> granted = player.assignQuestsForDate(LocalDate.now());
        if (!granted.isEmpty()) {
            notifySaved(onSave);
            System.out.println("\n오늘의 루틴 퀘스트 " + granted.size() + "개가 발급되었습니다.");
        }

        while (true) {
            List<Quest> active = player.getActiveQuests();
            System.out.println("\n=== 퀘스트 ===");
            if (active.isEmpty()) {
                System.out.println("진행 중인 퀘스트가 없습니다.");
            } else {
                for (int i = 0; i < active.size(); i++) {
                    System.out.println((i + 1) + ". " + active.get(i));
                }
            }

            int completedOption = active.size() + 1;
            int backOption = active.size() + 2;
            System.out.println();
            if (!active.isEmpty()) {
                System.out.println("1~" + active.size() + ". 진행하기");
            }
            System.out.println(completedOption + ". 완료된 퀘스트");
            System.out.println(backOption + ". 뒤로");
            System.out.print("> ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("올바른 번호를 입력하세요.");
                continue;
            }

            if (choice >= 1 && choice <= active.size()) {
                Quest target = active.get(choice - 1);
                boolean finished = player.progressQuest(target, 1, LocalDate.now());
                notifySaved(onSave);
                if (finished) {
                    System.out.println(target.getName() + " 퀘스트를 완료했습니다!");
                } else {
                    System.out.println(target.getName() + " 1세트 진행 ("
                            + target.getCurrentSet() + "/" + target.getGoalSet() + ")");
                }
            } else if (choice == completedOption) {
                System.out.println("\n=== 완료된 퀘스트 ===");
                List<Quest> completed = player.getCompletedQuests();
                if (completed.isEmpty()) {
                    System.out.println("완료된 퀘스트가 없습니다.");
                } else {
                    for (int i = 0; i < completed.size(); i++) {
                        System.out.println((i + 1) + ". " + completed.get(i));
                    }
                }
            } else if (choice == backOption) {
                return;
            } else {
                System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }

    private static void notifySaved(Runnable onSave) {
        if (onSave != null) {
            onSave.run();
        }
    }
}
