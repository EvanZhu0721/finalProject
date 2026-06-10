import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


final class SmokeTest {
    static void run() {
        if (!"1.0.1".equals(TypingLaneDemo.VERSION)) {
            throw new IllegalStateException("Smoke test failed: release version is not 1.0.1.");
        }
        validateWordBank();
        if (Target.enemy(0, TargetKind.NORMAL).hp != GamePanel.scaleEnemyHp(4)
                || Target.boss(0, 1).hp != GamePanel.scaleBossHp(18 + 8)) {
            throw new IllegalStateException("Smoke test failed: enemy HP multipliers are not applied.");
        }
        GamePanel endurancePanel = new GamePanel(false);
        Target standardCollision = Target.enemy(0, TargetKind.NORMAL);
        int standardCollisionCost = endurancePanel.collisionCost(standardCollision);
        if (standardCollisionCost != standardCollision.hp || endurancePanel.maxHp / standardCollisionCost != 6) {
            throw new IllegalStateException("Smoke test failed: collision cost no longer matches real target HP.");
        }
        standardCollision.hp = 3;
        if (endurancePanel.collisionCost(standardCollision) != 3) {
            throw new IllegalStateException("Smoke test failed: damaged targets did not reduce collision cost.");
        }
        int[] expectedXpCurve = {6, 8, 12, 16, 22, 30, 40};
        GamePanel xpCurvePanel = new GamePanel(false);
        if (xpCurvePanel.xpToNext != expectedXpCurve[0]) {
            throw new IllegalStateException("Smoke test failed: first upgrade XP requirement is wrong.");
        }
        for (int i = 1; i < expectedXpCurve.length; i++) {
            xpCurvePanel.upgradeLevel = i;
            if (xpCurvePanel.nextXpRequirement() != expectedXpCurve[i]) {
                throw new IllegalStateException("Smoke test failed: early XP curve did not ramp correctly.");
            }
        }

        GamePanel gatePanel = new GamePanel(false);
        if (GamePanel.LOGIC_FPS != 120 || Math.abs(GamePanel.TICK_MS - (1000.0 / 120.0)) > 0.001) {
            throw new IllegalStateException("Smoke test failed: logic frame rate is not 120 FPS.");
        }
        if (GamePanel.WIDTH != 2560 || GamePanel.HEIGHT != 1440
                || GamePanel.RENDER_WIDTH != 2560 || GamePanel.RENDER_HEIGHT != 1440) {
            throw new IllegalStateException("Smoke test failed: render resolution changed the gameplay coordinate space.");
        }
        if (gatePanel.timer.getDelay() != GamePanel.FRAME_MS) {
            throw new IllegalStateException("Smoke test failed: render timer is not running at the frame cadence.");
        }
        if (GamePanel.FRAME_MS != 8) {
            throw new IllegalStateException("Smoke test failed: render timer is not running at 8 ms.");
        }
        if (gatePanel.getPreferredSize().width != GamePanel.DEFAULT_WINDOW_WIDTH
                || gatePanel.getPreferredSize().height != GamePanel.DEFAULT_WINDOW_HEIGHT
                || gatePanel.logicalFrameBuffer().getWidth() != GamePanel.RENDER_WIDTH
                || gatePanel.logicalFrameBuffer().getHeight() != GamePanel.RENDER_HEIGHT) {
            throw new IllegalStateException("Smoke test failed: default window size or render buffer is wrong.");
        }
        if (GamePanel.MIN_WINDOW_WIDTH >= GamePanel.WIDTH || GamePanel.MIN_WINDOW_HEIGHT >= GamePanel.HEIGHT) {
            throw new IllegalStateException("Smoke test failed: minimum window size is still bound to render size.");
        }
        gatePanel.renderAlpha = 0.5;
        if (Math.abs(gatePanel.renderX(100.0, 80.0) - 90.0) > 0.001) {
            throw new IllegalStateException("Smoke test failed: render interpolation is not blending positions.");
        }
        gatePanel.renderAlpha = 1.0;
        GamePanel frameClockPanel = new GamePanel(false);
        frameClockPanel.started = true;
        frameClockPanel.lastFrameNanos = System.nanoTime()
                - (long) (GamePanel.TICK_MS * 2.25 * 1_000_000.0);
        frameClockPanel.advanceFrameClock();
        if (frameClockPanel.tick != 2) {
            throw new IllegalStateException("Smoke test failed: frame clock did not preserve the fixed update cadence.");
        }
        GamePanel speedScalePanel = new GamePanel(false);
        speedScalePanel.started = true;
        speedScalePanel.spawnCooldown = 9999;
        Target speedProbeTarget = Target.enemy(0, TargetKind.NORMAL);
        double targetStartX = speedProbeTarget.x;
        speedScalePanel.targets.add(speedProbeTarget);
        Bullet speedProbeBullet = new Bullet(GamePanel.PLAYER_X + GamePanel.worldAmount(28), 0, 1, 1,
                BulletKind.BASIC);
        double bulletStartX = speedProbeBullet.x;
        speedScalePanel.bullets.add(speedProbeBullet);
        speedScalePanel.step();
        double expectedTargetX = targetStartX - GamePanel.gameplayStep(speedProbeTarget.speed);
        double expectedBulletX = bulletStartX + GamePanel.gameplayStep(speedProbeBullet.speed);
        if (Math.abs(speedProbeTarget.x - expectedTargetX) > 0.001
                || Math.abs(speedProbeBullet.x - expectedBulletX) > 0.001) {
            throw new IllegalStateException("Smoke test failed: 120 FPS logic changed movement speed.");
        }
        gatePanel.started = false;
        gatePanel.laneWords[0] = "code";
        gatePanel.laneWords[1] = "loop";
        gatePanel.handleLetter('c');
        gatePanel.step();
        if (gatePanel.typed.length() != 0 || gatePanel.tick != 0) {
            throw new IllegalStateException("Smoke test failed: start gate accepted input or advanced time.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_F2));
        if (gatePanel.language != Language.CHINESE || gatePanel.started) {
            throw new IllegalStateException("Smoke test failed: F2 did not switch language before start.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_3));
        if (gatePanel.difficulty != Difficulty.HARD) {
            throw new IllegalStateException("Smoke test failed: difficulty selection did not apply before start.");
        }
        if (gatePanel.nextSpawnCooldown() != new GamePanel(false).nextSpawnCooldown()) {
            throw new IllegalStateException("Smoke test failed: difficulty changed spawn pacing.");
        }
        GamePanel freshSpawnPanel = new GamePanel(false);
        GamePanel lateSpawnPanel = new GamePanel(false);
        lateSpawnPanel.tick = GamePanel.logicTicks(430 * 12);
        lateSpawnPanel.kills = 108;
        lateSpawnPanel.bossLevel = 3;
        if (lateSpawnPanel.nextSpawnCooldown() != freshSpawnPanel.nextSpawnCooldown()) {
            throw new IllegalStateException("Smoke test failed: progress still changed enemy spawn density.");
        }
        GamePanel easyScalePanel = new GamePanel(false);
        easyScalePanel.difficulty = Difficulty.EASY;
        GamePanel hardScalePanel = new GamePanel(false);
        hardScalePanel.difficulty = Difficulty.HARD;
        Target easyTarget = Target.enemy(0, TargetKind.NORMAL);
        Target hardTarget = Target.enemy(0, TargetKind.NORMAL);
        easyScalePanel.scaleTarget(easyTarget);
        hardScalePanel.scaleTarget(hardTarget);
        if (easyTarget.hp != hardTarget.hp || easyTarget.maxHp != hardTarget.maxHp) {
            throw new IllegalStateException("Smoke test failed: difficulty changed enemy HP.");
        }
        if (easyTarget.speed >= hardTarget.speed) {
            throw new IllegalStateException("Smoke test failed: difficulty did not change enemy movement speed.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_ENTER));
        if (!gatePanel.started) {
            throw new IllegalStateException("Smoke test failed: Enter did not start the run.");
        }
        gatePanel.keyTyped(typedKey(gatePanel, 'c'));
        if (gatePanel.typed.length() != 0) {
            throw new IllegalStateException("Smoke test failed: keyTyped still changed typing progress.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_C));
        if (!"c".equals(gatePanel.typed)) {
            throw new IllegalStateException("Smoke test failed: keyboard key codes were not accepted.");
        }
        gatePanel.handleLetter('1');
        if (!"c".equals(gatePanel.typed)) {
            throw new IllegalStateException("Smoke test failed: non-letter input changed typing progress.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_BACK_SPACE));
        if (gatePanel.typed.length() != 0 || gatePanel.typingLane != -1) {
            throw new IllegalStateException("Smoke test failed: Backspace did not clear the typing lane.");
        }
        gatePanel.correctTypedChars = 25;
        gatePanel.runStartTick = 0;
        gatePanel.tick = 60;
        if (gatePanel.currentTypingSpeedWpm() <= 0) {
            throw new IllegalStateException("Smoke test failed: current typing speed was not calculated.");
        }
        GamePanel sudoPanel = new GamePanel(false);
        sudoPanel.started = false;
        sudoPanel.handleLetter('s');
        sudoPanel.handleLetter('u');
        sudoPanel.handleLetter('d');
        sudoPanel.handleLetter('o');
        if (!sudoPanel.started || sudoPanel.choiceMode != ChoiceMode.TEST_BACKEND) {
            throw new IllegalStateException("Smoke test failed: sudo did not open the test backend.");
        }
        selectTestEffect(sudoPanel, UpgradeEffect.TEST_INVINCIBLE);
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ENTER));
        if (!sudoPanel.testInvincible || sudoPanel.collisionCost(Target.enemy(0, TargetKind.NORMAL)) != 0) {
            throw new IllegalStateException("Smoke test failed: sudo invincibility did not remove HP loss.");
        }
        int pendingBeforeBigXp = sudoPanel.pendingUpgradeChoices;
        selectTestEffect(sudoPanel, UpgradeEffect.TEST_BIG_XP);
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ENTER));
        if (sudoPanel.pendingUpgradeChoices <= pendingBeforeBigXp) {
            throw new IllegalStateException("Smoke test failed: sudo XP cache did not grant upgrade progress.");
        }
        int pendingBeforeSudoGrant = sudoPanel.pendingUpgradeChoices;
        selectTestEffect(sudoPanel, UpgradeEffect.CALIBRATED_DAMAGE);
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ENTER));
        if (sudoPanel.damageBonusPercent <= 0 || sudoPanel.choiceMode != ChoiceMode.TEST_BACKEND
                || sudoPanel.pendingUpgradeChoices != pendingBeforeSudoGrant) {
            throw new IllegalStateException("Smoke test failed: test backend did not grant upgrades cleanly.");
        }
        int expectedSudoEffects = sudoPanel.sudoToolEffects().length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.COMMON).length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.UNCOMMON).length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.HIGH).length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.RED).length;
        if (sudoPanel.allTestUpgradeEffects().length != expectedSudoEffects) {
            throw new IllegalStateException("Smoke test failed: sudo backend is not synchronized with gameplay pools.");
        }
        if (GamePanel.ENABLE_GROUP_ONE_UPGRADES) {
            UpgradeEffect[] commonPool = sudoPanel.gameplayPoolFor(UpgradeRarity.COMMON);
            UpgradeEffect[] sudoEffects = sudoPanel.allTestUpgradeEffects();
            UpgradeEffect[] groupOneEffects = {
                    UpgradeEffect.LONG_WORD_REWARD,
                    UpgradeEffect.SHORT_WORD_QUICKSHOT,
                    UpgradeEffect.FINAL_LETTER_BURST,
                    UpgradeEffect.VOWEL_CONVERGENCE,
                    UpgradeEffect.HARD_CONSONANT_BREAK
            };
            for (UpgradeEffect effect : groupOneEffects) {
                if (!containsEffect(commonPool, effect) || !containsEffect(sudoEffects, effect)) {
                    throw new IllegalStateException("Smoke test failed: group-one upgrade is not visible in pools.");
                }
            }
        }
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ESCAPE));
        if (sudoPanel.choiceMode != ChoiceMode.NONE) {
            throw new IllegalStateException("Smoke test failed: test backend did not close with Esc.");
        }
        sudoPanel.language = Language.CHINESE;
        UpgradeCard localizedSudoCard = sudoPanel.createCard(UpgradeEffect.CALIBRATED_DAMAGE, UpgradeRarity.COMMON);
        if (!"校准伤害".equals(sudoPanel.cardTitle(localizedSudoCard))
                || !"+12% 加算伤害".equals(sudoPanel.cardDescription(localizedSudoCard))
                || !"普通".equals(sudoPanel.rarityLabel(UpgradeRarity.COMMON))) {
            throw new IllegalStateException("Smoke test failed: test backend labels did not localize.");
        }
        UpgradeCard localizedTriggerCard = sudoPanel.createCard(UpgradeEffect.TRIGGER_TUNING, UpgradeRarity.COMMON);
        if (!"增加一发子弹或一道激光；齐射总伤害提高后再分摊。".equals(
                sudoPanel.upgradeChoiceDescription(localizedTriggerCard))) {
            throw new IllegalStateException("Smoke test failed: trigger tuning description was not dynamic.");
        }
        sudoPanel.choiceMode = ChoiceMode.TEST_BACKEND;
        selectTestEffect(sudoPanel, UpgradeEffect.CALIBRATED_DAMAGE);
        sudoPanel.setSize(GamePanel.WIDTH, GamePanel.HEIGHT);
        BufferedImage sudoBackendFrame = new BufferedImage(
                GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sudoBackendGraphics = sudoBackendFrame.createGraphics();
        sudoPanel.paint(sudoBackendGraphics);
        sudoBackendGraphics.dispose();

        gatePanel.laneWords[0] = "code";
        gatePanel.laneWords[1] = "loop";
        for (int i = 0; i < 25; i++) {
            String previous = gatePanel.laneWords[0];
            gatePanel.refreshLaneWord(0);
            if (gatePanel.laneWords[0].equals(previous) || gatePanel.laneWords[0].equals(gatePanel.laneWords[1])) {
                throw new IllegalStateException("Smoke test failed: lane word repeated after refresh.");
            }
            if (gatePanel.laneWords[0].charAt(0) == gatePanel.laneWords[1].charAt(0)) {
                throw new IllegalStateException("Smoke test failed: lane words share an ambiguous first letter.");
            }
            if (gatePanel.laneWords[0].length() < GamePanel.LANE_WORD_MIN_LENGTH
                    || gatePanel.laneWords[0].length() > GamePanel.LANE_WORD_MAX_LENGTH) {
                throw new IllegalStateException("Smoke test failed: lane word length changed with progress.");
            }
        }
        GamePanel highPressureWordPanel = new GamePanel(false);
        highPressureWordPanel.tick = GamePanel.logicTicks(430 * 12);
        highPressureWordPanel.kills = 108;
        highPressureWordPanel.bossLevel = 3;
        highPressureWordPanel.laneWords[0] = "code";
        highPressureWordPanel.laneWords[1] = "loop";
        highPressureWordPanel.refreshLaneWord(0);
        if (highPressureWordPanel.laneWords[0].length() < GamePanel.LANE_WORD_MIN_LENGTH
                || highPressureWordPanel.laneWords[0].length() > GamePanel.LANE_WORD_MAX_LENGTH) {
            throw new IllegalStateException("Smoke test failed: high pressure still changed lane word length.");
        }
        GamePanel highPressureSpawnPanel = new GamePanel(false);
        highPressureSpawnPanel.tick = GamePanel.logicTicks(430 * 12);
        highPressureSpawnPanel.kills = 108;
        highPressureSpawnPanel.bossLevel = 3;
        highPressureSpawnPanel.spawnRandomTarget();
        if (highPressureSpawnPanel.targets.size() != 1) {
            throw new IllegalStateException("Smoke test failed: high pressure still created extra enemy density.");
        }
        Target spawnedAtLaneEnd = highPressureSpawnPanel.targets.get(0);
        double spawnedRightEdge = spawnedAtLaneEnd.x + GamePanel.targetHalfWidth(spawnedAtLaneEnd.kind);
        if (Math.abs(spawnedRightEdge - GamePanel.LANE_RIGHT_X) > 0.001) {
            throw new IllegalStateException("Smoke test failed: enemy did not spawn at the lane end.");
        }
        Bullet rangeProbe = new Bullet(GamePanel.PLAYER_X, 0, 1, 1, BulletKind.BASIC);
        if (Math.abs(highPressureSpawnPanel.maxBulletX(rangeProbe) - GamePanel.LANE_RIGHT_X) > 0.001) {
            throw new IllegalStateException("Smoke test failed: bullet range does not cover the full lane.");
        }
        if (rangeProbe.lifeTicks * GamePanel.gameplayStep(rangeProbe.speed) < GamePanel.LANE_RIGHT_X - rangeProbe.x) {
            throw new IllegalStateException("Smoke test failed: bullet lifetime does not cover the full lane.");
        }

        Target frozen = Target.enemy(0, TargetKind.NORMAL);
        frozen.x = GamePanel.PLAYER_X + GamePanel.worldAmount(200);
        gatePanel.targets.add(frozen);
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_ESCAPE));
        int pausedTick = gatePanel.tick;
        double pausedX = frozen.x;
        gatePanel.step();
        if (!gatePanel.paused || gatePanel.tick != pausedTick || frozen.x != pausedX) {
            throw new IllegalStateException("Smoke test failed: pause did not freeze the update loop.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_ESCAPE));
        if (gatePanel.paused) {
            throw new IllegalStateException("Smoke test failed: Esc did not resume from pause.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_P));
        if (gatePanel.paused) {
            throw new IllegalStateException("Smoke test failed: letter P still paused the game.");
        }
        gatePanel.paused = true;
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_R));
        if (!gatePanel.paused || gatePanel.targets.size() == 0) {
            throw new IllegalStateException("Smoke test failed: letter R still restarted from pause.");
        }
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_F5));
        if (gatePanel.paused || gatePanel.score != 0 || gatePanel.targets.size() != 0) {
            throw new IllegalStateException("Smoke test failed: F5 did not reset from pause.");
        }
        gatePanel.setFullscreenState(true);
        if (!gatePanel.fullscreen) {
            throw new IllegalStateException("Smoke test failed: fullscreen state flag was not applied.");
        }
        gatePanel.setFullscreenState(false);

        GamePanel panel = new GamePanel(false);
        panel.setSize(GamePanel.WIDTH * 2, GamePanel.HEIGHT * 2);
        BufferedImage highResFrame = new BufferedImage(
                GamePanel.WIDTH * 2, GamePanel.HEIGHT * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D highResGraphics = highResFrame.createGraphics();
        panel.paint(highResGraphics);
        highResGraphics.dispose();

        Target basicTarget = Target.enemy(0, TargetKind.NORMAL);
        basicTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(150);
        panel.targets.add(basicTarget);
        panel.laneWords[0] = "code";
        panel.laneWords[1] = "loop";
        panel.handleLetter('c');
        panel.handleLetter('x');
        if (panel.typed.length() != 0 || panel.typingLane != -1) {
            throw new IllegalStateException("Smoke test failed: typo did not clear lane-word input progress.");
        }
        panel.laneWords[0] = "code";
        panel.laneWords[1] = "java";
        panel.handleLetter('c');
        if (panel.inputPulseTicks <= 0) {
            throw new IllegalStateException("Smoke test failed: correct typing did not trigger input feedback.");
        }
        panel.handleLetter('j');
        if (!"j".equals(panel.typed) || panel.typingLane != 1) {
            throw new IllegalStateException("Smoke test failed: mismatch did not restart with the new letter.");
        }
        if (!panel.laneHasInputPulse(1) || panel.laneHasInputPulse(0)) {
            throw new IllegalStateException("Smoke test failed: input pulse was not strictly bound to the typing lane.");
        }
        if (panel.wrongFlashTicks <= 0 || panel.wrongFlashLane != 0) {
            throw new IllegalStateException("Smoke test failed: mismatch did not mark the broken lane.");
        }
        panel.typed = "";
        panel.typingLane = -1;
        panel.laneWords[1] = "loop";
        panel.handleLetter('c');
        panel.handleLetter('o');
        panel.handleLetter('d');
        panel.handleLetter('e');
        panel.laneWords[0] = "code";
        panel.handleLetter('c');
        panel.handleLetter('o');
        panel.handleLetter('d');
        panel.handleLetter('e');
        if (panel.completePulseTicks <= 0 || panel.completePulseLane != 0) {
            throw new IllegalStateException("Smoke test failed: completed lane word did not trigger finish feedback.");
        }
        if (!panel.laneWordShowsActiveHighlight(0)) {
            throw new IllegalStateException("Smoke test failed: completed lane lost its highlight before the green pulse ended.");
        }
        if (!hasBulletKind(panel, BulletKind.BASIC, false)) {
            throw new IllegalStateException("Smoke test failed: basic bullets did not use the line trail style.");
        }
        GamePanel highlightPanel = new GamePanel(false);
        highlightPanel.laneWords[0] = "code";
        highlightPanel.laneWords[1] = "loop";
        typeWord(highlightPanel, "code");
        for (int i = 0; i < GamePanel.WORD_COMPLETE_PULSE_TICKS; i++) {
            highlightPanel.step();
        }
        if (highlightPanel.completePulseTicks != 0 || highlightPanel.laneWordShowsActiveHighlight(0)) {
            throw new IllegalStateException("Smoke test failed: completed lane stayed highlighted after the green pulse.");
        }
        highlightPanel.handleLetter(highlightPanel.laneWords[0].charAt(0));
        if (!highlightPanel.laneWordShowsActiveHighlight(0)) {
            throw new IllegalStateException("Smoke test failed: completed lane highlight did not return when typing restarted.");
        }
        for (int i = 0; i < GamePanel.logicTicks(16); i++) {
            panel.step();
        }
        if (panel.score <= 0 && panel.targets.size() > 0) {
            throw new IllegalStateException("Smoke test failed: typing did not damage or remove target.");
        }
        if (panel.breakParticles.size() == 0) {
            throw new IllegalStateException("Smoke test failed: defeated target did not create break particles.");
        }
        if (panel.xpOrbs.size() == 0 && panel.xp == 0) {
            throw new IllegalStateException("Smoke test failed: defeated hostile unit did not produce XP.");
        }
        GamePanel fullRangePanel = new GamePanel(false);
        fullRangePanel.started = true;
        fullRangePanel.laneWords[0] = "code";
        fullRangePanel.laneWords[1] = "loop";
        Target laneEndTarget = Target.enemy(0, TargetKind.NORMAL);
        fullRangePanel.targets.add(laneEndTarget);
        int laneEndHpBefore = laneEndTarget.hp;
        typeWord(fullRangePanel, "code");
        for (int i = 0; i < GamePanel.logicTicks(120); i++) {
            fullRangePanel.step();
        }
        if (laneEndTarget.hp >= laneEndHpBefore) {
            throw new IllegalStateException("Smoke test failed: full-lane attack did not reach the lane-end target.");
        }
        int xpBeforeOrb = panel.xp;
        panel.xpOrbs.clear();
        panel.xpOrbs.add(new XpOrb(GamePanel.PLAYER_X, panel.lane, 1));
        panel.step();
        if (panel.xpOrbs.size() != 0 || panel.xp <= xpBeforeOrb) {
            throw new IllegalStateException("Smoke test failed: XP ball was not collected by approaching it.");
        }

        GamePanel attractPanel = new GamePanel(false);
        attractPanel.started = true;
        attractPanel.lane = 0;
        XpOrb attractOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(240), 0, 1);
        attractPanel.xpOrbs.add(attractOrb);
        double attractStart = attractOrb.x;
        for (int i = 0; i < GamePanel.logicTicks(1); i++) {
            attractPanel.step();
        }
        if (attractOrb.x >= attractStart - 2.0) {
            throw new IllegalStateException("Smoke test failed: nearby same-lane XP ball did not attract.");
        }
        if (!attractOrb.attracted) {
            throw new IllegalStateException("Smoke test failed: attracted XP ball did not lock its magnet state.");
        }
        double switchedLaneStart = attractOrb.x;
        double switchedLaneStartY = attractOrb.y;
        attractPanel.lane = 1;
        for (int i = 0; i < GamePanel.logicTicks(1); i++) {
            attractPanel.step();
        }
        if (attractOrb.x >= switchedLaneStart - attractOrb.speed - 1.0) {
            throw new IllegalStateException("Smoke test failed: lane switch interrupted XP ball attraction.");
        }
        if (attractOrb.y <= switchedLaneStartY) {
            throw new IllegalStateException("Smoke test failed: attracted XP ball did not move toward the player lane.");
        }
        GamePanel farOrbPanel = new GamePanel(false);
        farOrbPanel.started = true;
        farOrbPanel.lane = 0;
        XpOrb farOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(340), 0, 1);
        farOrbPanel.xpOrbs.add(farOrb);
        double farStart = farOrb.x;
        farOrbPanel.step();
        if (farOrb.x < farStart - GamePanel.gameplayStep(farOrb.speed) - 0.01) {
            throw new IllegalStateException("Smoke test failed: far XP ball attracted from too far away.");
        }

        panel.xp = panel.xpToNext - 1;
        panel.xpOrbs.add(new XpOrb(GamePanel.PLAYER_X, panel.lane, 1));
        panel.step();
        if (panel.pendingUpgradeChoices <= 0 || panel.choiceMode != ChoiceMode.NONE) {
            throw new IllegalStateException("Smoke test failed: full XP did not create a pending upgrade without opening it.");
        }
        panel.keyPressed(key(panel, KeyEvent.VK_SPACE));
        if (panel.choiceMode != ChoiceMode.UPGRADE || panel.upgradeChoices[0] == null
                || panel.upgradeChoices[1] == null || panel.upgradeChoices[2] == null) {
            throw new IllegalStateException("Smoke test failed: Space did not open the three-slot upgrade menu.");
        }
        paintSmokeFrame(panel);
        for (UpgradeCard card : panel.upgradeChoices) {
            if (card.rarity == UpgradeRarity.HIGH || card.rarity == UpgradeRarity.RED) {
                throw new IllegalStateException("Smoke test failed: normal upgrade menu offered boss-only rarities.");
            }
        }
        panel.keyPressed(key(panel, KeyEvent.VK_RIGHT));
        if (panel.selectedUpgradeIndex != 1) {
            throw new IllegalStateException("Smoke test failed: upgrade menu selection did not move right.");
        }
        int pendingBeforeUpgrade = panel.pendingUpgradeChoices;
        panel.keyPressed(key(panel, KeyEvent.VK_ENTER));
        if (panel.choiceMode != ChoiceMode.NONE || panel.pendingUpgradeChoices >= pendingBeforeUpgrade) {
            throw new IllegalStateException("Smoke test failed: upgrade card was not applied from the menu.");
        }
        GamePanel multiUpgradePanel = new GamePanel(false);
        multiUpgradePanel.started = true;
        multiUpgradePanel.pendingUpgradeChoices = 2;
        multiUpgradePanel.openUpgradeMenu();
        multiUpgradePanel.keyPressed(key(multiUpgradePanel, KeyEvent.VK_ENTER));
        if (multiUpgradePanel.choiceMode != ChoiceMode.UPGRADE || multiUpgradePanel.pendingUpgradeChoices != 1
                || multiUpgradePanel.upgradeChoices[0] == null || multiUpgradePanel.selectedUpgradeIndex != 0) {
            throw new IllegalStateException("Smoke test failed: queued upgrade choices did not keep the menu open.");
        }
        multiUpgradePanel.keyPressed(key(multiUpgradePanel, KeyEvent.VK_ENTER));
        if (multiUpgradePanel.choiceMode != ChoiceMode.NONE || multiUpgradePanel.pendingUpgradeChoices != 0) {
            throw new IllegalStateException("Smoke test failed: final queued upgrade choice did not close the menu.");
        }
        GamePanel abandonPanel = new GamePanel(false);
        abandonPanel.started = true;
        abandonPanel.pendingUpgradeChoices = 1;
        abandonPanel.keyPressed(key(abandonPanel, KeyEvent.VK_SPACE));
        abandonPanel.keyPressed(key(abandonPanel, KeyEvent.VK_ESCAPE));
        if (abandonPanel.choiceMode != ChoiceMode.NONE || abandonPanel.pendingUpgradeChoices != 0) {
            throw new IllegalStateException("Smoke test failed: upgrade abandon option did not consume a pending choice.");
        }
        GamePanel overviewPanel = new GamePanel(false);
        overviewPanel.started = true;
        overviewPanel.xpToNext = 12;
        overviewPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT;
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_SPACE));
        if (overviewPanel.choiceMode != ChoiceMode.OVERVIEW || !overviewPanel.overviewSelectionActive
                || overviewPanel.pendingUpgradeChoices != 0 || overviewPanel.upgradeChoices[0] != null
                || overviewPanel.upgradeChoices[1] != null || overviewPanel.upgradeChoices[2] != null) {
            throw new IllegalStateException("Smoke test failed: Space did not open the standalone upgrade overview.");
        }
        paintSmokeFrame(overviewPanel);
        int overviewTickBefore = overviewPanel.tick;
        overviewPanel.step();
        if (overviewPanel.tick != overviewTickBefore) {
            throw new IllegalStateException("Smoke test failed: standalone upgrade overview did not freeze logic.");
        }
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_ENTER));
        if (overviewPanel.fireRateBonusPercent != 0 || overviewPanel.xp != 4
                || overviewPanel.choiceMode != ChoiceMode.OVERVIEW) {
            throw new IllegalStateException("Smoke test failed: standalone overview could not sell an owned card.");
        }
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_SPACE));
        if (overviewPanel.choiceMode != ChoiceMode.NONE) {
            throw new IllegalStateException("Smoke test failed: Space did not close the standalone overview.");
        }
        GamePanel pausedOverviewPanel = new GamePanel(false);
        pausedOverviewPanel.started = true;
        pausedOverviewPanel.paused = true;
        pausedOverviewPanel.keyPressed(key(pausedOverviewPanel, KeyEvent.VK_SPACE));
        if (pausedOverviewPanel.choiceMode != ChoiceMode.OVERVIEW || !pausedOverviewPanel.paused) {
            throw new IllegalStateException("Smoke test failed: Space did not open overview while paused.");
        }
        pausedOverviewPanel.keyPressed(key(pausedOverviewPanel, KeyEvent.VK_ESCAPE));
        if (pausedOverviewPanel.choiceMode != ChoiceMode.NONE || !pausedOverviewPanel.paused) {
            throw new IllegalStateException("Smoke test failed: closing paused overview changed pause state.");
        }

        GamePanel bossRewardPanel = new GamePanel(false);
        bossRewardPanel.started = true;
        bossRewardPanel.bossLevel = 2;
        Target rewardBoss = Target.boss(0, 2);
        bossRewardPanel.rewardFor(rewardBoss);
        if (bossRewardPanel.choiceMode != ChoiceMode.UPGRADE || !bossRewardPanel.bossRewardChoice
                || bossRewardPanel.xpOrbs.size() != 0 || bossRewardPanel.pendingUpgradeChoices != 0) {
            throw new IllegalStateException("Smoke test failed: boss reward did not open the boss-only upgrade menu.");
        }
        if (bossRewardPanel.upgradeChoices[0].rarity != UpgradeRarity.RED
                || bossRewardPanel.upgradeChoices[1].rarity != UpgradeRarity.HIGH
                || bossRewardPanel.upgradeChoices[2].rarity != UpgradeRarity.HIGH
                || bossRewardPanel.upgradeChoices[1].effect != UpgradeEffect.GOLD_TALENT_PLACEHOLDER
                || bossRewardPanel.upgradeChoices[2].effect != UpgradeEffect.GOLD_TALENT_PLACEHOLDER) {
            throw new IllegalStateException("Smoke test failed: boss reward slots are not red plus reserved gold talents.");
        }
        UpgradeEffect bossWeapon = bossRewardPanel.upgradeChoices[0].effect;
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_1));
        if (bossRewardPanel.choiceMode != ChoiceMode.NONE || bossRewardPanel.bossRewardChoice
                || !bossRewardPanel.hasHighTalent(bossWeapon)) {
            throw new IllegalStateException("Smoke test failed: boss red weapon choice was not installed cleanly.");
        }
        if (bossRewardPanel.highTalents.length != GamePanel.MAX_RED_WEAPONS
                || GamePanel.MAX_RED_WEAPONS != 1) {
            throw new IllegalStateException("Smoke test failed: red weapons are not limited to one slot.");
        }
        GamePanel singleWeaponPanel = new GamePanel(false);
        singleWeaponPanel.started = true;
        singleWeaponPanel.highTalents[0] = UpgradeEffect.RHYTHM_CANNON;
        singleWeaponPanel.applyUpgradeCard(singleWeaponPanel.createCard(UpgradeEffect.FROST_FIELD,
                UpgradeRarity.RED), false, true);
        if (singleWeaponPanel.choiceMode != ChoiceMode.HIGH_REPLACE
                || singleWeaponPanel.highTalents[0] != UpgradeEffect.RHYTHM_CANNON) {
            throw new IllegalStateException("Smoke test failed: second red weapon did not enter replacement flow.");
        }
        singleWeaponPanel.keyPressed(key(singleWeaponPanel, KeyEvent.VK_2));
        if (singleWeaponPanel.choiceMode != ChoiceMode.HIGH_REPLACE
                || singleWeaponPanel.highTalents[0] != UpgradeEffect.RHYTHM_CANNON) {
            throw new IllegalStateException("Smoke test failed: invalid red weapon replacement slot was accepted.");
        }
        singleWeaponPanel.keyPressed(key(singleWeaponPanel, KeyEvent.VK_1));
        if (singleWeaponPanel.choiceMode != ChoiceMode.NONE
                || singleWeaponPanel.highTalents[0] != UpgradeEffect.FROST_FIELD) {
            throw new IllegalStateException("Smoke test failed: one-slot red weapon replacement did not install.");
        }

        panel.targets.clear();
        panel.xpOrbs.clear();
        panel.bullets.clear();
        panel.impacts.clear();
        panel.lane = 0;
        panel.laneWords[0] = "code";
        panel.laneWords[1] = "java";
        Target downTarget = Target.enemy(1, TargetKind.NORMAL);
        downTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(130);
        panel.targets.add(downTarget);
        int downHpBefore = downTarget.hp;
        panel.handleLetter('j');
        panel.handleLetter('a');
        panel.handleLetter('v');
        panel.handleLetter('a');
        for (int i = 0; i < GamePanel.logicTicks(12); i++) {
            panel.step();
        }
        if (panel.lane != 1 || downTarget.hp >= downHpBefore) {
            throw new IllegalStateException("Smoke test failed: DOWN lane word did not move and attack that lane.");
        }

        panel.targets.clear();
        panel.xpOrbs.clear();
        panel.bullets.clear();
        panel.impacts.clear();
        panel.lane = 0;
        panel.highTalents[0] = UpgradeEffect.RHYTHM_CANNON;
        Target streamTarget = Target.enemy(0, TargetKind.TANK);
        streamTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(170);
        streamTarget.hp = 60;
        streamTarget.maxHp = 60;
        panel.targets.add(streamTarget);
        panel.step();
        if (!hasBulletKind(panel, BulletKind.CONTINUOUS)) {
            throw new IllegalStateException("Smoke test failed: autocannon did not fire without typing.");
        }
        if (!hasBulletKind(panel, BulletKind.CONTINUOUS, false)) {
            throw new IllegalStateException("Smoke test failed: autocannon bullets should keep the old trail style.");
        }
        panel.laneWords[0] = "stream";
        panel.laneWords[1] = "java";
        typeWord(panel, "stream");
        if (panel.continuousSurgeTicks <= 0) {
            throw new IllegalStateException("Smoke test failed: typing did not empower the autocannon.");
        }
        panel.bullets.clear();
        panel.step();
        if (!hasBulletKind(panel, BulletKind.CONTINUOUS_SURGE)) {
            throw new IllegalStateException("Smoke test failed: empowered autocannon did not fire surge rounds.");
        }
        if (!hasBulletKind(panel, BulletKind.CONTINUOUS_SURGE, false)) {
            throw new IllegalStateException("Smoke test failed: empowered autocannon bullets should keep the old trail style.");
        }

        GamePanel piercePanel = new GamePanel(false);
        piercePanel.highTalents[0] = UpgradeEffect.FROST_FIELD;
        piercePanel.laneWords[0] = "loop";
        piercePanel.laneWords[1] = "java";
        Target first = Target.enemy(0, TargetKind.NORMAL);
        Target second = Target.enemy(0, TargetKind.NORMAL);
        first.x = GamePanel.PLAYER_X + GamePanel.worldAmount(150);
        second.x = GamePanel.PLAYER_X + GamePanel.worldAmount(230);
        piercePanel.targets.add(first);
        piercePanel.targets.add(second);
        typeWord(piercePanel, "loop");
        if (!hasBulletKind(piercePanel, BulletKind.PIERCE)) {
            throw new IllegalStateException("Smoke test failed: Laser Gun did not fire a laser beam.");
        }
        if (hasBulletKind(piercePanel, BulletKind.BASIC)) {
            throw new IllegalStateException("Smoke test failed: Laser Gun should replace the basic shot.");
        }
        Bullet laserBeam = firstBulletOfKind(piercePanel, BulletKind.PIERCE);
        if (!laserBeam.beamResolved || laserBeam.x < GamePanel.LANE_RIGHT_X) {
            throw new IllegalStateException("Smoke test failed: Laser Gun did not resolve as an instant beam.");
        }
        Bullet decayProbe = new Bullet(GamePanel.PLAYER_X, 0, 10, 5, BulletKind.PIERCE);
        if (piercePanel.bulletDamageForHit(decayProbe) != 10) {
            throw new IllegalStateException("Smoke test failed: first pierce hit did not keep full damage.");
        }
        decayProbe.hits = 1;
        if (piercePanel.bulletDamageForHit(decayProbe) != 8) {
            throw new IllegalStateException("Smoke test failed: pierce damage did not decay by 20 percent.");
        }
        for (int i = 0; i < GamePanel.logicTicks(14); i++) {
            piercePanel.step();
        }
        if (piercePanel.impacts.size() == 0 || first.hp >= first.maxHp || second.hp >= second.maxHp) {
            throw new IllegalStateException("Smoke test failed: Laser Gun did not hit multiple lane targets.");
        }
        for (Bullet bullet : piercePanel.bullets) {
            if (bullet.kind != BulletKind.PIERCE && bullet.hitTargets != null) {
                throw new IllegalStateException("Smoke test failed: non-pierce bullets allocated hit target lists.");
            }
        }
        GamePanel tunedLaserPanel = new GamePanel(false);
        tunedLaserPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT;
        Target tunedLaserTarget = Target.enemy(0, TargetKind.TANK);
        tunedLaserTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(180);
        tunedLaserTarget.hp = 100;
        tunedLaserTarget.maxHp = 100;
        tunedLaserPanel.targets.add(tunedLaserTarget);
        tunedLaserPanel.fireLaserVolley(0, 10);
        int tunedLaserDamage = 0;
        double firstLaserY = Double.NaN;
        boolean laserOffsetsDiffer = false;
        for (Bullet bullet : tunedLaserPanel.bullets) {
            if (bullet.kind == BulletKind.PIERCE) {
                tunedLaserDamage += bullet.damage;
                if (Double.isNaN(firstLaserY)) {
                    firstLaserY = bullet.y;
                } else if (Math.abs(firstLaserY - bullet.y) > 0.001) {
                    laserOffsetsDiffer = true;
                }
            }
        }
        if (countBulletKind(tunedLaserPanel, BulletKind.PIERCE) != 2 || tunedLaserDamage != 12
                || tunedLaserTarget.hp != tunedLaserTarget.maxHp - 12 || !laserOffsetsDiffer) {
            throw new IllegalStateException("Smoke test failed: Trigger Tuning did not split Laser Gun beams.");
        }

        GamePanel homingPanel = new GamePanel(false);
        homingPanel.highTalents[0] = UpgradeEffect.HOMING_SHOTGUN;
        homingPanel.laneWords[0] = "code";
        homingPanel.laneWords[1] = "loop";
        Target homingTarget = Target.enemy(1, TargetKind.TANK);
        homingTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(760);
        homingTarget.hp = 999;
        homingTarget.maxHp = 999;
        homingPanel.targets.add(homingTarget);
        typeWord(homingPanel, "code");
        if (countBulletKind(homingPanel, BulletKind.HOMING_SHOT) < GamePanel.HOMING_SHOTGUN_BASE_PELLETS) {
            throw new IllegalStateException("Smoke test failed: Homing Shotgun did not fire a large pellet fan.");
        }
        if (hasBulletKind(homingPanel, BulletKind.BASIC)) {
            throw new IllegalStateException("Smoke test failed: Homing Shotgun should replace the basic shot.");
        }
        if (!hasBulletKind(homingPanel, BulletKind.HOMING_SHOT, true)) {
            throw new IllegalStateException("Smoke test failed: Homing Shotgun pellets need particle trails.");
        }
        Bullet topFanPellet = firstBulletOfKind(homingPanel, BulletKind.HOMING_SHOT);
        double initialVy = topFanPellet.vy;
        for (int i = 0; i < GamePanel.logicTicks(18); i++) {
            homingPanel.step();
        }
        if (initialVy >= 0.0 || topFanPellet.vy <= 0.0 || homingPanel.bulletTrailParticles.size() == 0) {
            throw new IllegalStateException("Smoke test failed: Homing Shotgun pellets did not arm, steer, and trail.");
        }

        GamePanel dryIcePanel = new GamePanel(false);
        dryIcePanel.highTalents[0] = UpgradeEffect.DRY_ICE_BULLET;
        dryIcePanel.laneWords[0] = "code";
        dryIcePanel.laneWords[1] = "loop";
        Target dryCenter = Target.enemy(0, TargetKind.TANK);
        Target drySide = Target.enemy(0, TargetKind.NORMAL);
        dryCenter.x = GamePanel.PLAYER_X + GamePanel.worldAmount(145);
        drySide.x = dryCenter.x + GamePanel.worldAmount(48);
        dryCenter.hp = 40;
        dryCenter.maxHp = 40;
        drySide.hp = 20;
        drySide.maxHp = 20;
        dryIcePanel.targets.add(dryCenter);
        dryIcePanel.targets.add(drySide);
        typeWord(dryIcePanel, "code");
        if (!hasBulletKind(dryIcePanel, BulletKind.DRY_ICE, true)) {
            throw new IllegalStateException("Smoke test failed: Dry-Ice Bullet did not fire the icy particle-trail round.");
        }
        if (hasBulletKind(dryIcePanel, BulletKind.BASIC)) {
            throw new IllegalStateException("Smoke test failed: Dry-Ice Bullet should replace the basic shot.");
        }
        Bullet rotatingDryIce = firstBulletOfKind(dryIcePanel, BulletKind.DRY_ICE);
        double dryIceRotationBefore = rotatingDryIce.shapeRotation;
        dryIcePanel.step();
        if (rotatingDryIce.shapeRotation == dryIceRotationBefore || rotatingDryIce.shapeSpin == 0.0) {
            throw new IllegalStateException("Smoke test failed: Dry-Ice Bullet did not rotate in flight.");
        }
        for (int i = 0; i < GamePanel.logicTicks(8); i++) {
            dryIcePanel.step();
        }
        if (drySide.hp >= drySide.maxHp || drySide.slowTicks <= 0 || dryIcePanel.icePulses.size() == 0) {
            throw new IllegalStateException("Smoke test failed: Dry-Ice Bullet did not splash, slow, and pulse.");
        }
        dryCenter.dryIceHitStreak = 2;
        dryIcePanel.applyDryIceHit(new Bullet(dryCenter.x, 0, 2, 1, BulletKind.DRY_ICE), dryCenter, 2);
        if (dryCenter.freezeTicks <= 0) {
            throw new IllegalStateException("Smoke test failed: Dry-Ice Bullet did not freeze on the third same-target hit.");
        }

        panel.tick = GamePanel.logicTicks(430 * 6);
        panel.kills = 54;
        panel.bossLevel = 1;
        Target scaled = Target.enemy(0, TargetKind.TANK);
        int baseHp = scaled.hp;
        double baseSpeed = scaled.speed;
        panel.scaleTarget(scaled);
        if (scaled.hp <= baseHp || scaled.speed <= baseSpeed || panel.pressureLevel() <= 0) {
            throw new IllegalStateException("Smoke test failed: pressure scaling did not strengthen enemies.");
        }
        Target earlyNormal = Target.enemy(0, TargetKind.NORMAL);
        Target lateNormal = Target.enemy(0, TargetKind.NORMAL);
        panel.tick = 0;
        panel.kills = 0;
        panel.bossLevel = 0;
        panel.scaleTarget(earlyNormal);
        panel.tick = GamePanel.logicTicks(430 * 8);
        panel.scaleTarget(lateNormal);
        if (lateNormal.hp - earlyNormal.hp < 6) {
            throw new IllegalStateException("Smoke test failed: enemy HP did not ramp fast enough over time.");
        }

        panel.choiceMode = ChoiceMode.UPGRADE;
        int choiceTick = panel.tick;
        panel.step();
        if (panel.tick != choiceTick) {
            throw new IllegalStateException("Smoke test failed: upgrade choice did not freeze pressure time.");
        }

        panel.damageBonusPercent = 0;
        int unscaledDamage = panel.scaledDamage(10, null);
        panel.applyUpgradeCard(new UpgradeCard("Test Damage", "test", UpgradeRarity.COMMON,
                UpgradeEffect.CALIBRATED_DAMAGE));
        if (panel.scaledDamage(10, null) <= unscaledDamage) {
            throw new IllegalStateException("Smoke test failed: percentage damage upgrade did not scale attacks.");
        }
        if (panel.scaledDamage(5, null) <= 5) {
            throw new IllegalStateException("Smoke test failed: low base damage did not receive a visible damage upgrade.");
        }
        panel.damageBonusPercent = GamePanel.MAX_DAMAGE_BONUS_PERCENT;
        if (panel.canOfferEffect(UpgradeEffect.CALIBRATED_DAMAGE, UpgradeRarity.COMMON)) {
            throw new IllegalStateException("Smoke test failed: capped damage upgrade was still offerable.");
        }
        panel.fireRateBonusPercent = GamePanel.MAX_TRIGGER_TUNING_BONUS_PERCENT;
        if (panel.canOfferEffect(UpgradeEffect.TRIGGER_TUNING, UpgradeRarity.COMMON)) {
            throw new IllegalStateException("Smoke test failed: capped trigger tuning was still offerable.");
        }
        panel.maxHpUpgradeBonus = GamePanel.MAX_HP_UPGRADE_BONUS;
        panel.hp = panel.maxHp;
        if (panel.canOfferEffect(UpgradeEffect.FIELD_PATCH, UpgradeRarity.COMMON)
                || panel.canOfferEffect(UpgradeEffect.REINFORCED_CORE, UpgradeRarity.COMMON)) {
            throw new IllegalStateException("Smoke test failed: capped HP upgrades were still offerable.");
        }

        GamePanel volleyPanel = new GamePanel(false);
        volleyPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT;
        volleyPanel.fireSingleShotVolley(0, 10, 1, BulletKind.BASIC);
        int volleyDamage = 0;
        for (Bullet bullet : volleyPanel.bullets) {
            volleyDamage += bullet.damage;
            if (bullet.damage >= 10) {
                throw new IllegalStateException("Smoke test failed: trigger tuning pellet damage was not reduced.");
            }
        }
        if (volleyPanel.bullets.size() != 2 || volleyDamage != 12) {
            throw new IllegalStateException("Smoke test failed: trigger tuning did not split +20% total damage.");
        }
        volleyPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT * 2;
        if (volleyPanel.singleShotPelletCount() != 3) {
            throw new IllegalStateException("Smoke test failed: trigger tuning did not add one pellet per level.");
        }
        GamePanel sellPanel = new GamePanel(false);
        sellPanel.started = true;
        sellPanel.pendingUpgradeChoices = 1;
        sellPanel.xpToNext = 12;
        sellPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT;
        sellPanel.phaseSwitchLevel = 1;
        sellPanel.blueUpgradeCount = 1;
        sellPanel.openUpgradeMenu();
        sellPanel.selectedOverviewCardIndex = inventoryIndexFor(sellPanel, UpgradeEffect.TRIGGER_TUNING);
        sellPanel.overviewSelectionActive = true;
        sellPanel.keyPressed(key(sellPanel, KeyEvent.VK_ENTER));
        if (sellPanel.fireRateBonusPercent != 0 || sellPanel.xp != 4 || sellPanel.choiceMode != ChoiceMode.UPGRADE) {
            throw new IllegalStateException("Smoke test failed: selling an upgrade did not refund one-third level XP.");
        }
        sellPanel.selectedOverviewCardIndex = inventoryIndexFor(sellPanel, UpgradeEffect.PHASE_SWITCH);
        sellPanel.overviewSelectionActive = true;
        sellPanel.keyPressed(key(sellPanel, KeyEvent.VK_ENTER));
        if (sellPanel.phaseSwitchLevel != 0 || sellPanel.blueUpgradeCount != 0 || sellPanel.xp != 8) {
            throw new IllegalStateException("Smoke test failed: selling a blue upgrade did not free the blue cap.");
        }
        GamePanel goldSellPanel = new GamePanel(false);
        goldSellPanel.started = true;
        goldSellPanel.xpToNext = 12;
        goldSellPanel.goldTalents[0] = UpgradeEffect.CALIBRATED_DAMAGE;
        goldSellPanel.openUpgradeOverview();
        goldSellPanel.selectedOverviewCardIndex = inventoryIndexFor(goldSellPanel, UpgradeEffect.CALIBRATED_DAMAGE);
        goldSellPanel.overviewSelectionActive = true;
        goldSellPanel.keyPressed(key(goldSellPanel, KeyEvent.VK_ENTER));
        if (goldSellPanel.goldTalents[0] != null || goldSellPanel.xp != 8) {
            throw new IllegalStateException("Smoke test failed: selling a gold upgrade did not refund double blue XP.");
        }
        GamePanel blueCapPanel = new GamePanel(false);
        blueCapPanel.applyUpgradeCard(blueCapPanel.createCard(UpgradeEffect.PHASE_SWITCH, UpgradeRarity.UNCOMMON),
                true, true);
        blueCapPanel.applyUpgradeCard(blueCapPanel.createCard(UpgradeEffect.BOSS_BREAKER, UpgradeRarity.UNCOMMON),
                true, true);
        blueCapPanel.applyUpgradeCard(blueCapPanel.createCard(UpgradeEffect.CROSSFEED, UpgradeRarity.UNCOMMON),
                true, true);
        if (blueCapPanel.blueUpgradeCount != GamePanel.MAX_BLUE_UPGRADES
                || blueCapPanel.canOfferEffect(UpgradeEffect.PRESSURE_VALVE, UpgradeRarity.UNCOMMON)) {
            throw new IllegalStateException("Smoke test failed: blue upgrade cap was not enforced.");
        }
        blueCapPanel.buildUpgradeChoices();
        for (UpgradeCard card : blueCapPanel.upgradeChoices) {
            if (card.rarity != UpgradeRarity.COMMON) {
                throw new IllegalStateException("Smoke test failed: capped blue upgrades still appeared in choices.");
            }
        }

        panel.applyUpgradeCard(new UpgradeCard("Rhythm Cannon", "test", UpgradeRarity.RED,
                UpgradeEffect.RHYTHM_CANNON));
        if (!panel.hasHighTalent(UpgradeEffect.RHYTHM_CANNON)) {
            throw new IllegalStateException("Smoke test failed: red weapon was not recorded.");
        }

        if (GamePanel.ENABLE_GROUP_ONE_UPGRADES) {
            GamePanel longRewardPanel = new GamePanel(false);
            longRewardPanel.hp = longRewardPanel.maxHp - 3;
            grantForSmoke(longRewardPanel, UpgradeEffect.LONG_WORD_REWARD);
            longRewardPanel.laneWords[0] = "random";
            longRewardPanel.laneWords[1] = "code";
            typeWord(longRewardPanel, "random");
            if (longRewardPanel.hp <= longRewardPanel.maxHp - 3) {
                throw new IllegalStateException("Smoke test failed: Longword Reward did not repair HP.");
            }

            GamePanel shortShotPanel = new GamePanel(false);
            grantForSmoke(shortShotPanel, UpgradeEffect.SHORT_WORD_QUICKSHOT);
            shortShotPanel.laneWords[0] = "code";
            shortShotPanel.laneWords[1] = "loop";
            typeWord(shortShotPanel, "code");
            if (!hasBulletKind(shortShotPanel, BulletKind.CONTINUOUS) || shortShotPanel.bullets.size() < 2) {
                throw new IllegalStateException("Smoke test failed: Shortword Quickshot did not add a fast shot.");
            }

            GamePanel burstPanel = new GamePanel(false);
            grantForSmoke(burstPanel, UpgradeEffect.FINAL_LETTER_BURST);
            Target burstCenter = Target.enemy(0, TargetKind.NORMAL);
            Target burstSide = Target.enemy(0, TargetKind.NORMAL);
            burstCenter.x = GamePanel.PLAYER_X + GamePanel.worldAmount(140);
            burstSide.x = burstCenter.x + GamePanel.worldAmount(42);
            burstPanel.targets.add(burstCenter);
            burstPanel.targets.add(burstSide);
            burstPanel.laneWords[0] = "code";
            burstPanel.laneWords[1] = "loop";
            typeWord(burstPanel, "code");
            if (!hasBulletKind(burstPanel, BulletKind.BURST)) {
                throw new IllegalStateException("Smoke test failed: Final Letter Burst did not spawn a burst bullet.");
            }
            for (int i = 0; i < GamePanel.logicTicks(9); i++) {
                burstPanel.step();
            }
            if (burstSide.hp >= burstSide.maxHp) {
                throw new IllegalStateException("Smoke test failed: Final Letter Burst did not splash nearby targets.");
            }

            GamePanel vowelPanel = new GamePanel(false);
            grantForSmoke(vowelPanel, UpgradeEffect.VOWEL_CONVERGENCE);
            XpOrb vowelOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(260), 0, 1);
            vowelPanel.xpOrbs.add(vowelOrb);
            vowelPanel.laneWords[0] = "code";
            vowelPanel.laneWords[1] = "loop";
            double vowelOrbStart = vowelOrb.x;
            typeWord(vowelPanel, "code");
            if (!vowelOrb.attracted || vowelOrb.x >= vowelOrbStart) {
                throw new IllegalStateException("Smoke test failed: Vowel Convergence did not pull XP.");
            }

            GamePanel hardPanel = new GamePanel(false);
            grantForSmoke(hardPanel, UpgradeEffect.HARD_CONSONANT_BREAK);
            Target hardBreakTarget = Target.enemy(0, TargetKind.TANK);
            hardBreakTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(160);
            hardPanel.targets.add(hardBreakTarget);
            hardPanel.laneWords[0] = "target";
            hardPanel.laneWords[1] = "code";
            typeWord(hardPanel, "target");
            if (hardPanel.bullets.size() == 0 || hardPanel.bullets.get(0).damage <= hardPanel.baseDamage) {
                throw new IllegalStateException("Smoke test failed: Hard Consonant Break did not improve thick-target damage.");
            }
        }

        if (GamePanel.ENABLE_GROUP_TWO_TO_FIVE_UPGRADES) {
            UpgradeEffect[] sudoEffects = sudoPanel.allTestUpgradeEffects();
            UpgradeEffect[] groupTwoToFiveEffects = {
                    UpgradeEffect.SAME_LANE_SUPPRESSION,
                    UpgradeEffect.LANE_SWAP_BEAT,
                    UpgradeEffect.SINGLE_LANE_BASTION,
                    UpgradeEffect.ALTERNATING_GUARD,
                    UpgradeEffect.COMBO_CALIBRATOR,
                    UpgradeEffect.FIRST_LETTER_LOCK,
                    UpgradeEffect.ACCURATE_OPENER,
                    UpgradeEffect.PREFIX_ILLUMINATION,
                    UpgradeEffect.FIRST_LETTER_TICKET,
                    UpgradeEffect.DUAL_PREFIX_SCAN,
                    UpgradeEffect.CLEAN_FINISH,
                    UpgradeEffect.ERROR_RESET,
                    UpgradeEffect.BACKSPACE_FIX,
                    UpgradeEffect.CALM_AFTER_ERROR,
                    UpgradeEffect.BACKSPACE_COUNTER,
                    UpgradeEffect.PRECISE_PICKUP,
                    UpgradeEffect.FINAL_LETTER_PULL,
                    UpgradeEffect.LONG_WORD_FOCUS,
                    UpgradeEffect.DANGER_WORD,
                    UpgradeEffect.PRESSURE_VALVE
            };
            for (UpgradeEffect effect : groupTwoToFiveEffects) {
                if (!containsEffect(sudoEffects, effect)) {
                    throw new IllegalStateException("Smoke test failed: group 2-5 upgrade is missing from sudo.");
                }
            }

            GamePanel switchPanel = new GamePanel(false);
            switchPanel.laneWords[0] = "code";
            switchPanel.laneWords[1] = "loop";
            typeWord(switchPanel, "loop");
            double switchY = switchPanel.playerRenderY();
            if (switchPanel.lane != 1 || switchPanel.laneSwitchAnimationTicks <= 0
                    || switchY <= GamePanel.LANE_Y[0] || switchY >= GamePanel.LANE_Y[1]) {
                throw new IllegalStateException("Smoke test failed: lane switch animation did not start smoothly.");
            }
            if (!switchPanel.pendingLaneAttack || switchPanel.bullets.size() != 0) {
                throw new IllegalStateException("Smoke test failed: lane switch attack did not wait for the animation.");
            }
            for (int i = 0; i < GamePanel.LANE_SWITCH_ANIMATION_TICKS; i++) {
                switchPanel.step();
            }
            if (switchPanel.pendingLaneAttack || switchPanel.bullets.size() == 0) {
                throw new IllegalStateException("Smoke test failed: lane switch attack did not fire after the animation.");
            }

            GamePanel sameLanePanel = new GamePanel(false);
            grantForSmoke(sameLanePanel, UpgradeEffect.SAME_LANE_SUPPRESSION);
            Target sameLaneTarget = Target.enemy(0, TargetKind.TANK);
            sameLaneTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(170);
            sameLanePanel.targets.add(sameLaneTarget);
            sameLanePanel.laneWords[0] = "code";
            sameLanePanel.laneWords[1] = "loop";
            typeWord(sameLanePanel, "code");
            sameLanePanel.laneWords[0] = "java";
            sameLanePanel.laneWords[1] = "loop";
            typeWord(sameLanePanel, "java");
            if (sameLanePanel.sameLaneStreak < 2 || sameLaneTarget.slowTicks <= 0) {
                throw new IllegalStateException("Smoke test failed: Same-Lane Suppression did not slow the target.");
            }

            GamePanel swapPanel = new GamePanel(false);
            grantForSmoke(swapPanel, UpgradeEffect.LANE_SWAP_BEAT);
            swapPanel.laneWords[0] = "code";
            swapPanel.laneWords[1] = "loop";
            typeWord(swapPanel, "code");
            int bulletsBeforeSwap = swapPanel.bullets.size();
            swapPanel.laneWords[0] = "code";
            swapPanel.laneWords[1] = "loop";
            typeWord(swapPanel, "loop");
            finishLaneSwitch(swapPanel);
            if (!swapPanel.completedWordAlternated || swapPanel.bullets.size() < bulletsBeforeSwap + 3) {
                throw new IllegalStateException("Smoke test failed: Lane Swap Beat did not fire split shots.");
            }

            GamePanel firstLetterPanel = new GamePanel(false);
            grantForSmoke(firstLetterPanel, UpgradeEffect.FIRST_LETTER_TICKET);
            firstLetterPanel.laneWords[1] = "loop";
            firstLetterPanel.laneWords[0] = "code";
            typeWord(firstLetterPanel, "code");
            firstLetterPanel.laneWords[0] = "class";
            typeWord(firstLetterPanel, "class");
            int xpBeforeTicket = firstLetterPanel.xpOrbs.size();
            firstLetterPanel.laneWords[0] = "cursor";
            typeWord(firstLetterPanel, "cursor");
            if (firstLetterPanel.sameFirstLetterStreak < 3 || firstLetterPanel.xpOrbs.size() <= xpBeforeTicket) {
                throw new IllegalStateException("Smoke test failed: First-Letter Ticket did not create bonus XP.");
            }

            GamePanel cleanPanel = new GamePanel(false);
            grantForSmoke(cleanPanel, UpgradeEffect.CLEAN_FINISH);
            cleanPanel.laneWords[0] = "code";
            cleanPanel.laneWords[1] = "loop";
            typeWord(cleanPanel, "code");
            if (!cleanPanel.completedWordWasClean || !hasBulletKind(cleanPanel, BulletKind.CONTINUOUS)) {
                throw new IllegalStateException("Smoke test failed: Clean Finish did not add a light shot.");
            }

            GamePanel backspacePanel = new GamePanel(false);
            grantForSmoke(backspacePanel, UpgradeEffect.BACKSPACE_COUNTER);
            backspacePanel.laneWords[0] = "code";
            backspacePanel.laneWords[1] = "loop";
            backspacePanel.handleLetter('c');
            backspacePanel.handleLetter('o');
            backspacePanel.keyPressed(key(backspacePanel, KeyEvent.VK_BACK_SPACE));
            backspacePanel.handleLetter('o');
            backspacePanel.handleLetter('d');
            backspacePanel.handleLetter('e');
            if (!backspacePanel.completedWordUsedBackspace || !hasBulletKind(backspacePanel, BulletKind.BURST)) {
                throw new IllegalStateException("Smoke test failed: Backspace Counter did not fire.");
            }

            GamePanel immediateResetPanel = new GamePanel(false);
            grantForSmoke(immediateResetPanel, UpgradeEffect.ERROR_RESET);
            immediateResetPanel.laneWords[0] = "code";
            immediateResetPanel.laneWords[1] = "loop";
            immediateResetPanel.handleLetter('c');
            immediateResetPanel.handleLetter('x');
            typeWord(immediateResetPanel, "code");
            if (!hasBulletKind(immediateResetPanel, BulletKind.BURST)) {
                throw new IllegalStateException("Smoke test failed: Error Reset did not fire after recompleting the same word.");
            }

            GamePanel wrongWordResetPanel = new GamePanel(false);
            grantForSmoke(wrongWordResetPanel, UpgradeEffect.ERROR_RESET);
            wrongWordResetPanel.laneWords[0] = "code";
            wrongWordResetPanel.laneWords[1] = "loop";
            wrongWordResetPanel.handleLetter('c');
            wrongWordResetPanel.handleLetter('x');
            typeWord(wrongWordResetPanel, "loop");
            finishLaneSwitch(wrongWordResetPanel);
            if (hasBulletKind(wrongWordResetPanel, BulletKind.BURST)) {
                throw new IllegalStateException("Smoke test failed: Error Reset fired after completing a different word.");
            }

            GamePanel comboCalibratorPanel = new GamePanel(false);
            grantForSmoke(comboCalibratorPanel, UpgradeEffect.COMBO_CALIBRATOR);
            comboCalibratorPanel.combo = 2;
            comboCalibratorPanel.completeLaneWord(0, "code");
            if (comboCalibratorPanel.bullets.size() != 1 || comboCalibratorPanel.bullets.get(0).damage <= 5) {
                throw new IllegalStateException("Smoke test failed: Combo Calibrator did not add damage at combo 3.");
            }
            comboCalibratorPanel.bullets.clear();
            comboCalibratorPanel.combo = 9;
            comboCalibratorPanel.completeLaneWord(0, "code");
            if (comboCalibratorPanel.bullets.size() != 1 || comboCalibratorPanel.bullets.get(0).damage <= 8) {
                throw new IllegalStateException("Smoke test failed: Combo Calibrator did not add stronger damage at combo 10.");
            }
            comboCalibratorPanel.bullets.clear();
            comboCalibratorPanel.combo = 10;
            comboCalibratorPanel.completeLaneWord(0, "code");
            if (comboCalibratorPanel.bullets.size() != 1 || comboCalibratorPanel.bullets.get(0).damage != 6) {
                throw new IllegalStateException("Smoke test failed: Combo Calibrator added damage outside combo 3/5/10.");
            }

            GamePanel crossfeedPanel = new GamePanel(false);
            grantForSmoke(crossfeedPanel, UpgradeEffect.CROSSFEED);
            crossfeedPanel.xpToNext = 999;
            crossfeedPanel.laneWords[0] = "flow";
            crossfeedPanel.laneWords[1] = "loop";
            typeWord(crossfeedPanel, "flow");
            if (crossfeedPanel.crossfeedBonusTicks <= 0) {
                throw new IllegalStateException("Smoke test failed: Crossfeed did not prime after an f/t/k word.");
            }
            crossfeedPanel.addExperience(5);
            if (crossfeedPanel.xp <= 5 || crossfeedPanel.crossfeedBonusTicks != 0) {
                throw new IllegalStateException("Smoke test failed: Crossfeed did not boost exactly one XP pickup.");
            }

            GamePanel expiredCrossfeedPanel = new GamePanel(false);
            grantForSmoke(expiredCrossfeedPanel, UpgradeEffect.CROSSFEED);
            expiredCrossfeedPanel.xpToNext = 999;
            expiredCrossfeedPanel.laneWords[0] = "flow";
            expiredCrossfeedPanel.laneWords[1] = "loop";
            typeWord(expiredCrossfeedPanel, "flow");
            for (int i = 0; i < GamePanel.logicTicks(151); i++) {
                expiredCrossfeedPanel.step();
            }
            expiredCrossfeedPanel.addExperience(5);
            if (expiredCrossfeedPanel.xp != 5) {
                throw new IllegalStateException("Smoke test failed: Crossfeed boost did not expire.");
            }

            GamePanel xpPullPanel = new GamePanel(false);
            grantForSmoke(xpPullPanel, UpgradeEffect.PRECISE_PICKUP);
            XpOrb pullOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(260), 0, 1);
            xpPullPanel.xpOrbs.add(pullOrb);
            xpPullPanel.laneWords[0] = "code";
            xpPullPanel.laneWords[1] = "loop";
            double pullStart = pullOrb.x;
            typeWord(xpPullPanel, "code");
            if (!pullOrb.attracted || pullOrb.x >= pullStart) {
                throw new IllegalStateException("Smoke test failed: Precise Pickup did not pull XP.");
            }

            GamePanel dangerPanel = new GamePanel(false);
            grantForSmoke(dangerPanel, UpgradeEffect.DANGER_WORD);
            Target dangerTarget = Target.enemy(0, TargetKind.NORMAL);
            dangerTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(230);
            dangerPanel.targets.add(dangerTarget);
            dangerPanel.laneWords[0] = "code";
            dangerPanel.laneWords[1] = "loop";
            double dangerStart = dangerTarget.x;
            typeWord(dangerPanel, "code");
            if (dangerTarget.x <= dangerStart) {
                throw new IllegalStateException("Smoke test failed: Danger Word did not knock back the target.");
            }

            GamePanel valvePanel = new GamePanel(false);
            grantForSmoke(valvePanel, UpgradeEffect.PRESSURE_VALVE);
            Target valveTarget = Target.enemy(0, TargetKind.NORMAL);
            valveTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(250);
            valvePanel.targets.add(valveTarget);
            valvePanel.laneWords[0] = "code";
            valvePanel.laneWords[1] = "loop";
            typeWord(valvePanel, "code");
            if (valvePanel.laneSlowTicks[0] <= 0) {
                throw new IllegalStateException("Smoke test failed: Pressure Valve did not slow the lane.");
            }
        }

        System.out.println("Smoke test passed: typing, XP, choices, red weapons, and update loop are alive.");
    }

    static void runRenderBenchmark() {
        GamePanel panel = new GamePanel(false);
        panel.setSize(GamePanel.RENDER_WIDTH, GamePanel.RENDER_HEIGHT);
        panel.started = true;
        panel.highTalents[0] = UpgradeEffect.RHYTHM_CANNON;
        panel.continuousSurgeTicks = GamePanel.logicTicks(15);
        panel.laneWords[0] = "graphics";
        panel.laneWords[1] = "keyboard";
        for (int i = 0; i < 18; i++) {
            Target target = Target.enemy(i % 2, i % 3 == 0 ? TargetKind.TANK : TargetKind.NORMAL);
            target.x = GamePanel.PLAYER_X + GamePanel.worldAmount(120 + i * 42);
            panel.targets.add(target);
        }
        for (int i = 0; i < 36; i++) {
            panel.bullets.add(new Bullet(GamePanel.PLAYER_X + GamePanel.worldAmount(80 + i * 19), i % 2, 2, 1,
                    i % 5 == 0 ? BulletKind.CONTINUOUS_SURGE : BulletKind.CONTINUOUS));
        }
        for (int i = 0; i < 12; i++) {
            panel.impacts.add(new Impact(GamePanel.PLAYER_X + GamePanel.worldAmount(120 + i * 60),
                    GamePanel.LANE_Y[i % 2], 3));
        }
        for (int i = 0; i < 8; i++) {
            panel.xpOrbs.add(new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(170 + i * 70), i % 2,
                    1 + i % 4));
        }

        BufferedImage frame = new BufferedImage(GamePanel.RENDER_WIDTH, GamePanel.RENDER_HEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = frame.createGraphics();
        int warmupFrames = 20;
        int measuredFrames = 120;
        for (int i = 0; i < warmupFrames; i++) {
            panel.tick++;
            panel.paint(graphics);
        }
        long startNanos = System.nanoTime();
        for (int i = 0; i < measuredFrames; i++) {
            panel.tick++;
            panel.paint(graphics);
        }
        long elapsedNanos = System.nanoTime() - startNanos;
        graphics.dispose();

        double averageMs = elapsedNanos / 1_000_000.0 / measuredFrames;
        double fps = 1000.0 / Math.max(0.01, averageMs);
        System.out.printf("Render benchmark %dx%d: %.2f ms/frame, %.1f FPS equivalent%n",
                GamePanel.RENDER_WIDTH, GamePanel.RENDER_HEIGHT, averageMs, fps);
    }

    private static KeyEvent key(GamePanel panel, int keyCode) {
        return new KeyEvent(panel, KeyEvent.KEY_PRESSED, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    private static KeyEvent typedKey(GamePanel panel, char keyChar) {
        return new KeyEvent(panel, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, keyChar);
    }

    private static void paintSmokeFrame(GamePanel panel) {
        BufferedImage frame = new BufferedImage(GamePanel.RENDER_WIDTH, GamePanel.RENDER_HEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = frame.createGraphics();
        panel.setSize(GamePanel.RENDER_WIDTH, GamePanel.RENDER_HEIGHT);
        panel.paint(graphics);
        graphics.dispose();
    }

    private static void validateWordBank() {
        for (int i = 0; i < GamePanel.WORDS.length; i++) {
            String word = GamePanel.WORDS[i];
            if (word.length() < GamePanel.LANE_WORD_MIN_LENGTH || word.length() > GamePanel.LANE_WORD_MAX_LENGTH) {
                throw new IllegalStateException("Smoke test failed: word length is outside lane constraints: " + word);
            }
            for (int j = i + 1; j < GamePanel.WORDS.length; j++) {
                if (word.equals(GamePanel.WORDS[j])) {
                    throw new IllegalStateException("Smoke test failed: duplicate word in bank: " + word);
                }
            }
        }
    }

    private static void selectTestEffect(GamePanel panel, UpgradeEffect effect) {
        UpgradeEffect[] effects = panel.allTestUpgradeEffects();
        for (int i = 0; i < effects.length; i++) {
            if (effects[i] == effect) {
                panel.selectedTestUpgradeIndex = i;
                return;
            }
        }
        throw new IllegalStateException("Smoke test failed: test backend is missing " + effect + ".");
    }

    private static boolean containsEffect(UpgradeEffect[] effects, UpgradeEffect wanted) {
        for (UpgradeEffect effect : effects) {
            if (effect == wanted) {
                return true;
            }
        }
        return false;
    }

    private static int inventoryIndexFor(GamePanel panel, UpgradeEffect wanted) {
        List<UpgradeInventoryCard> cards = panel.currentUpgradeCards();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).effect == wanted) {
                return i;
            }
        }
        throw new IllegalStateException("Smoke test failed: upgrade inventory is missing " + wanted + ".");
    }

    private static void grantForSmoke(GamePanel panel, UpgradeEffect effect) {
        panel.applyUpgradeCard(panel.createCard(effect, panel.rarityForEffect(effect)), false, false);
        panel.choiceMode = ChoiceMode.NONE;
    }

    private static void typeWord(GamePanel panel, String word) {
        for (int i = 0; i < word.length(); i++) {
            panel.handleLetter(word.charAt(i));
        }
    }

    private static void finishLaneSwitch(GamePanel panel) {
        for (int i = 0; i < GamePanel.LANE_SWITCH_ANIMATION_TICKS && panel.pendingLaneAttack; i++) {
            panel.step();
        }
    }

    private static boolean hasBulletKind(GamePanel panel, BulletKind kind) {
        for (Bullet bullet : panel.bullets) {
            if (bullet.kind == kind) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasBulletKind(GamePanel panel, BulletKind kind, boolean particleTrail) {
        for (Bullet bullet : panel.bullets) {
            if (bullet.kind == kind && bullet.particleTrail == particleTrail) {
                return true;
            }
        }
        return false;
    }

    private static int countBulletKind(GamePanel panel, BulletKind kind) {
        int count = 0;
        for (Bullet bullet : panel.bullets) {
            if (bullet.kind == kind) {
                count++;
            }
        }
        return count;
    }

    private static Bullet firstBulletOfKind(GamePanel panel, BulletKind kind) {
        for (Bullet bullet : panel.bullets) {
            if (bullet.kind == kind) {
                return bullet;
            }
        }
        throw new IllegalStateException("Smoke test failed: expected bullet kind was missing: " + kind);
    }

}

