package customgame;

import java.awt.*;
import java.awt.event.InputEvent;

public class Main {
    public static final String process_name = "LeagueClientUx.exe";
    private static Rectangle cachedRect = null;
    private static long lastRectUpdate = 0;
    private static Robot robot;
    private static MSWindow window;

    private static final Coords PLAY_BTN = new Coords(150, 50);
    private static final Coords CREATE_CUSTOM = new Coords(505, 125);
    private static final Coords HOWLING_ABYSS = new Coords(610, 280);
    private static final Coords DRAFT_MODE = new Coords(930, 565);
    private static final Coords RANDOM_MODE = new Coords(1175, 570);
    private static final Coords FRIENDS_LIST_ONLY = new Coords(960, 715);
    private static final Coords CONFIRM_BTN = new Coords(665, 850);
    private static final Coords INVITE_BTN = new Coords(1180, 145);
    private static final Coords FRIEND1 = new Coords(690, 285);
    private static final Coords FRIEND2 = new Coords(690, 340);
    private static final Coords FRIEND3 = new Coords(690, 385);
    private static final Coords FRIEND4 = new Coords(690, 440);
    private static final Coords FRIEND5 = new Coords(690, 490);
    private static final Coords FRIEND6 = new Coords(690, 535);
    private static final Coords START_BTN = new Coords(800, 800);

    public static void main(String[] args) throws InterruptedException {
        try {
            robot = new Robot();
            robot.setAutoDelay(1);
        } catch (AWTException ex) {
            System.out.println("Could not create robot.");
        }
        window = MSWindow.findWindow(process_name);
        if (window == null) {
            System.out.println("League of Legends client window not found.");
            return;
        }

        int sleep = 50;
        int fadeSleep = 700;

        // PLAY
        click(PLAY_BTN);
        Thread.sleep(fadeSleep);

        // CREATE CUSTOM
        click(CREATE_CUSTOM);
        Thread.sleep(sleep);

        // HOWLING ABYSS
        click(HOWLING_ABYSS);
        Thread.sleep(sleep);

        // Draft Mode
//        click(DRAFT_MODE);
//        Thread.sleep(sleep);

        // Random Mode
        click(RANDOM_MODE);
        Thread.sleep(sleep);

        // Friends List Only
        click(FRIENDS_LIST_ONLY);
        Thread.sleep(sleep);

        // CONFIRM
        click(CONFIRM_BTN);
        Thread.sleep(fadeSleep);

        // INVITE
        click(INVITE_BTN);
        Thread.sleep(fadeSleep);

        // Pick friends
        click(FRIEND1);
        click(FRIEND2);
        click(FRIEND3);
        click(FRIEND4);
        click(FRIEND5);
        click(FRIEND6);

        // Start
//        click(START_BTN);
    }

    private static void click(Coords c) {
        if (performFocusActions()) {
            try {
                Rectangle rect = getCachedRect();
                robot.mouseMove(rect.x + c.x, rect.y + c.y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(30);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } catch (Exception ex) {
                System.out.println("Could not get LoL window");
                System.exit(1);
            }
        }
    }

    private static boolean performFocusActions() {
        if (window.isForeground()) {
            return true;
        }
        window.focus();
        int i = 5;
        while (i-- > 0) {
            if (window.isForeground()) {
                return true;
            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException ex) {
                return false;
            }
        }
        return false;
    }

    private static Rectangle getCachedRect() throws Exception {
        long now = System.currentTimeMillis();
        if (cachedRect == null || now - lastRectUpdate > 1000) {
            lastRectUpdate = now;
            return cachedRect = window.getRect();
        } else {
            return cachedRect;
        }
    }

    private static class Coords {
        int x;
        int y;

        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}