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
        if (gatePanel.timer.getDelay() != GamePanel.FRAME_MS) {
            throw new IllegalStateException("Smoke test failed: render timer is not running at the frame cadence.");
        }
        gatePanel.renderAlpha = 0.5;
        if (Math.abs(gatePanel.renderX(100.0, 80.0) - 90.0) > 0.001) {
            throw new IllegalStateException("Smoke test failed: render interpolation is not blending positions.");
        }
        gatePanel.renderAlpha = 1.0;
        GamePanel frameClockPanel = new GamePanel(false);
        frameClockPanel.started = true;
        frameClockPanel.lastFrameNanos = System.nanoTime() - 40_000_000L;
        frameClockPanel.advanceFrameClock();
        if (frameClockPanel.tick != 1) {
            throw new IllegalStateException("Smoke test failed: frame clock did not preserve the fixed update cadence.");
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
        lateSpawnPanel.tick = 430 * 12;
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
                + sudoPanel.gameplayPoolFor(UpgradeRarity.HIGH).length;
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
        if (!"多发射一次子弹，但每颗弹丸伤害减少。".equals(sudoPanel.upgradeChoiceDescription(localizedTriggerCard))) {
            throw new IllegalStateException("Smoke test failed: single-shot trigger tuning description was not dynamic.");
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
        highPressureWordPanel.tick = 430 * 12;
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
        highPressureSpawnPanel.tick = 430 * 12;
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
        if (rangeProbe.lifeTicks * rangeProbe.speed < GamePanel.LANE_RIGHT_X - rangeProbe.x) {
            throw new IllegalStateException("Smoke test failed: bullet lifetime does not cover the full lane.");
        }

        Target frozen = Target.enemy(0, TargetKind.NORMAL);
        frozen.x = GamePanel.PLAYER_X + 200;
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
        basicTarget.x = GamePanel.PLAYER_X + 150;
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
        for (int i = 0; i < 16; i++) {
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
        for (int i = 0; i < 120; i++) {
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
        XpOrb attractOrb = new XpOrb(GamePanel.PLAYER_X + 240, 0, 1);
        attractPanel.xpOrbs.add(attractOrb);
        double attractStart = attractOrb.x;
        attractPanel.step();
        if (attractOrb.x >= attractStart - 2.0) {
            throw new IllegalStateException("Smoke test failed: nearby same-lane XP ball did not attract.");
        }
        if (!attractOrb.attracted) {
            throw new IllegalStateException("Smoke test failed: attracted XP ball did not lock its magnet state.");
        }
        double switchedLaneStart = attractOrb.x;
        double switchedLaneStartY = attractOrb.y;
        attractPanel.lane = 1;
        attractPanel.step();
        if (attractOrb.x >= switchedLaneStart - attractOrb.speed - 1.0) {
            throw new IllegalStateException("Smoke test failed: lane switch interrupted XP ball attraction.");
        }
        if (attractOrb.y <= switchedLaneStartY) {
            throw new IllegalStateException("Smoke test failed: attracted XP ball did not move toward the player lane.");
        }
        GamePanel farOrbPanel = new GamePanel(false);
        farOrbPanel.started = true;
        farOrbPanel.lane = 0;
        XpOrb farOrb = new XpOrb(GamePanel.PLAYER_X + 340, 0, 1);
        farOrbPanel.xpOrbs.add(farOrb);
        double farStart = farOrb.x;
        farOrbPanel.step();
        if (farOrb.x < farStart - farOrb.speed - 0.01) {
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
        panel.keyPressed(key(panel, KeyEvent.VK_RIGHT));
        if (panel.selectedUpgradeIndex != 1) {
            throw new IllegalStateException("Smoke test failed: upgrade menu selection did not move right.");
        }
        int pendingBeforeUpgrade = panel.pendingUpgradeChoices;
        panel.keyPressed(key(panel, KeyEvent.VK_ENTER));
        if (panel.choiceMode != ChoiceMode.NONE || panel.pendingUpgradeChoices >= pendingBeforeUpgrade) {
            throw new IllegalStateException("Smoke test failed: upgrade card was not applied from the menu.");
        }

        GamePanel bossRewardPanel = new GamePanel(false);
        bossRewardPanel.started = true;
        bossRewardPanel.bossLevel = 2;
        Target rewardBoss = Target.boss(0, 2);
        bossRewardPanel.rewardFor(rewardBoss);
        if (bossRewardPanel.choiceMode != ChoiceMode.NONE || bossRewardPanel.xpOrbs.size() != 0) {
            throw new IllegalStateException("Smoke test failed: boss reward still opened a special menu or dropped an orb.");
        }
        if (bossRewardPanel.pendingUpgradeChoices <= 0 && bossRewardPanel.xp <= 0) {
            throw new IllegalStateException("Smoke test failed: boss reward did not grant large XP.");
        }

        panel.targets.clear();
        panel.xpOrbs.clear();
        panel.bullets.clear();
        panel.impacts.clear();
        panel.lane = 0;
        panel.laneWords[0] = "code";
        panel.laneWords[1] = "java";
        Target downTarget = Target.enemy(1, TargetKind.NORMAL);
        downTarget.x = GamePanel.PLAYER_X + 130;
        panel.targets.add(downTarget);
        int downHpBefore = downTarget.hp;
        panel.handleLetter('j');
        panel.handleLetter('a');
        panel.handleLetter('v');
        panel.handleLetter('a');
        for (int i = 0; i < 12; i++) {
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
        streamTarget.x = GamePanel.PLAYER_X + 170;
        streamTarget.hp = 60;
        streamTarget.maxHp = 60;
        panel.targets.add(streamTarget);
        panel.step();
        if (!hasBulletKind(panel, BulletKind.CONTINUOUS)) {
            throw new IllegalStateException("Smoke test failed: autocannon did not fire without typing.");
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

        GamePanel piercePanel = new GamePanel(false);
        piercePanel.highTalents[0] = UpgradeEffect.FROST_FIELD;
        piercePanel.laneWords[0] = "loop";
        piercePanel.laneWords[1] = "java";
        Target first = Target.enemy(0, TargetKind.NORMAL);
        Target second = Target.enemy(0, TargetKind.NORMAL);
        first.x = GamePanel.PLAYER_X + 150;
        second.x = GamePanel.PLAYER_X + 230;
        piercePanel.targets.add(first);
        piercePanel.targets.add(second);
        typeWord(piercePanel, "loop");
        if (!hasBulletKind(piercePanel, BulletKind.PIERCE)) {
            throw new IllegalStateException("Smoke test failed: piercing rail did not fire a pierce bullet.");
        }
        Bullet decayProbe = new Bullet(GamePanel.PLAYER_X, 0, 10, 5, BulletKind.PIERCE);
        if (piercePanel.bulletDamageForHit(decayProbe) != 10) {
            throw new IllegalStateException("Smoke test failed: first pierce hit did not keep full damage.");
        }
        decayProbe.hits = 1;
        if (piercePanel.bulletDamageForHit(decayProbe) != 8) {
            throw new IllegalStateException("Smoke test failed: pierce damage did not decay by 20 percent.");
        }
        for (int i = 0; i < 14; i++) {
            piercePanel.step();
        }
        if (piercePanel.impacts.size() == 0 || first.hp >= first.maxHp || second.hp >= second.maxHp) {
            throw new IllegalStateException("Smoke test failed: piercing rail did not hit multiple lane targets.");
        }
        for (Bullet bullet : piercePanel.bullets) {
            if (bullet.kind != BulletKind.PIERCE && bullet.hitTargets != null) {
                throw new IllegalStateException("Smoke test failed: non-pierce bullets allocated hit target lists.");
            }
        }

        panel.tick = 430 * 6;
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
        panel.tick = 430 * 8;
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

        panel.applyUpgradeCard(new UpgradeCard("Rhythm Cannon", "test", UpgradeRarity.HIGH,
                UpgradeEffect.RHYTHM_CANNON));
        if (!panel.hasHighTalent(UpgradeEffect.RHYTHM_CANNON)) {
            throw new IllegalStateException("Smoke test failed: high rarity talent was not recorded.");
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
            burstCenter.x = GamePanel.PLAYER_X + 140;
            burstSide.x = burstCenter.x + 42;
            burstPanel.targets.add(burstCenter);
            burstPanel.targets.add(burstSide);
            burstPanel.laneWords[0] = "code";
            burstPanel.laneWords[1] = "loop";
            typeWord(burstPanel, "code");
            if (!hasBulletKind(burstPanel, BulletKind.BURST)) {
                throw new IllegalStateException("Smoke test failed: Final Letter Burst did not spawn a burst bullet.");
            }
            for (int i = 0; i < 9; i++) {
                burstPanel.step();
            }
            if (burstSide.hp >= burstSide.maxHp) {
                throw new IllegalStateException("Smoke test failed: Final Letter Burst did not splash nearby targets.");
            }

            GamePanel vowelPanel = new GamePanel(false);
            grantForSmoke(vowelPanel, UpgradeEffect.VOWEL_CONVERGENCE);
            XpOrb vowelOrb = new XpOrb(GamePanel.PLAYER_X + 260, 0, 1);
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
            hardBreakTarget.x = GamePanel.PLAYER_X + 160;
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
            sameLaneTarget.x = GamePanel.PLAYER_X + 170;
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

            GamePanel xpPullPanel = new GamePanel(false);
            grantForSmoke(xpPullPanel, UpgradeEffect.PRECISE_PICKUP);
            XpOrb pullOrb = new XpOrb(GamePanel.PLAYER_X + 260, 0, 1);
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
            dangerTarget.x = GamePanel.PLAYER_X + 230;
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
            valveTarget.x = GamePanel.PLAYER_X + 250;
            valvePanel.targets.add(valveTarget);
            valvePanel.laneWords[0] = "code";
            valvePanel.laneWords[1] = "loop";
            typeWord(valvePanel, "code");
            if (valvePanel.laneSlowTicks[0] <= 0) {
                throw new IllegalStateException("Smoke test failed: Pressure Valve did not slow the lane.");
            }
        }

        System.out.println("Smoke test passed: typing, XP, choices, high talents, and update loop are alive.");
    }

    static void runRenderBenchmark() {
        GamePanel panel = new GamePanel(false);
        panel.setSize(3840, 2160);
        panel.started = true;
        panel.highTalents[0] = UpgradeEffect.RHYTHM_CANNON;
        panel.continuousSurgeTicks = 15;
        panel.laneWords[0] = "graphics";
        panel.laneWords[1] = "keyboard";
        for (int i = 0; i < 18; i++) {
            Target target = Target.enemy(i % 2, i % 3 == 0 ? TargetKind.TANK : TargetKind.NORMAL);
            target.x = GamePanel.PLAYER_X + 120 + i * 42;
            panel.targets.add(target);
        }
        for (int i = 0; i < 36; i++) {
            panel.bullets.add(new Bullet(GamePanel.PLAYER_X + 80 + i * 19, i % 2, 2, 1,
                    i % 5 == 0 ? BulletKind.CONTINUOUS_SURGE : BulletKind.CONTINUOUS));
        }
        for (int i = 0; i < 12; i++) {
            panel.impacts.add(new Impact(GamePanel.PLAYER_X + 120 + i * 60, GamePanel.LANE_Y[i % 2], 3));
        }
        for (int i = 0; i < 8; i++) {
            panel.xpOrbs.add(new XpOrb(GamePanel.PLAYER_X + 170 + i * 70, i % 2, 1 + i % 4));
        }

        BufferedImage frame = new BufferedImage(3840, 2160, BufferedImage.TYPE_INT_ARGB);
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
        System.out.printf("Render benchmark 3840x2160: %.2f ms/frame, %.1f FPS equivalent%n", averageMs, fps);
    }

    private static KeyEvent key(GamePanel panel, int keyCode) {
        return new KeyEvent(panel, KeyEvent.KEY_PRESSED, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    private static KeyEvent typedKey(GamePanel panel, char keyChar) {
        return new KeyEvent(panel, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, keyChar);
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

}
