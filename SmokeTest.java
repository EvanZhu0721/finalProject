import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;


final class SmokeTest {
    static void run() {
        failIf(!"1.2.0".equals(TypingLane.VERSION), "Smoke test failed: release version is not 1.2.0.");
        validateWordBank();
        Target sampleBoss = Target.boss(0, 1);
        failIf(Target.enemy(0, TargetKind.NORMAL).hp != GamePanel.scaleEnemyHp(4)
                || sampleBoss.hp != GamePanel.scaleBossHp(18 + 8)
                || Math.abs(sampleBoss.speed - 0.275) > 0.0001
                || GamePanel.targetHalfHeight(TargetKind.BOSS) <= (GamePanel.LANE_Y[1] - GamePanel.LANE_Y[0]) / 2, "Smoke test failed: enemy HP multipliers or boss footprint are not applied.");
        GamePanel endurancePanel = new GamePanel(false);
        Target standardCollision = Target.enemy(0, TargetKind.NORMAL);
        int standardCollisionCost = endurancePanel.collisionCost(standardCollision);
        failIf(standardCollisionCost != standardCollision.hp || endurancePanel.maxHp / standardCollisionCost != 6, "Smoke test failed: collision cost no longer matches real target HP.");
        standardCollision.hp = 3;
        failIf(endurancePanel.collisionCost(standardCollision) != 3, "Smoke test failed: damaged targets did not reduce collision cost.");
        int[] expectedXpCurve = {6, 8, 12, 16, 22, 30, 40};
        GamePanel xpCurvePanel = new GamePanel(false);
        failIf(xpCurvePanel.xpToNext != expectedXpCurve[0], "Smoke test failed: first upgrade XP requirement is wrong.");
        for (int i = 1; i < expectedXpCurve.length; i++) {
            xpCurvePanel.upgradeLevel = i;
            failIf(xpCurvePanel.nextXpRequirement() != expectedXpCurve[i], "Smoke test failed: early XP curve did not ramp correctly.");
        }

        GamePanel gatePanel = new GamePanel(false);
        failIf(GamePanel.LOGIC_FPS != 120 || Math.abs(GamePanel.TICK_MS - (1000.0 / 120.0)) > 0.001, "Smoke test failed: logic frame rate is not 120 FPS.");
        failIf(GamePanel.WIDTH != 2560 || GamePanel.HEIGHT != 1440
                || GamePanel.RENDER_WIDTH != 2560 || GamePanel.RENDER_HEIGHT != 1440, "Smoke test failed: render resolution changed the gameplay coordinate space.");
        failIf(gatePanel.timer.getDelay() != GamePanel.FRAME_MS, "Smoke test failed: render timer is not running at the frame cadence.");
        failIf(GamePanel.FRAME_MS != 8, "Smoke test failed: render timer is not running at 8 ms.");
        failIf(gatePanel.getPreferredSize().width != GamePanel.DEFAULT_WINDOW_WIDTH
                || gatePanel.getPreferredSize().height != GamePanel.DEFAULT_WINDOW_HEIGHT
                || gatePanel.logicalFrameBuffer().getWidth() != GamePanel.RENDER_WIDTH
                || gatePanel.logicalFrameBuffer().getHeight() != GamePanel.RENDER_HEIGHT, "Smoke test failed: default window size or render buffer is wrong.");
        failIf(GamePanel.MIN_WINDOW_WIDTH >= GamePanel.WIDTH || GamePanel.MIN_WINDOW_HEIGHT >= GamePanel.HEIGHT, "Smoke test failed: minimum window size is still bound to render size.");
        Impact movingImpact = new Impact(GamePanel.PLAYER_X, GamePanel.LANE_Y[0], 7, GamePanel.COLOR_HOMING_PURPLE);
        failIf(!movingImpact.damageColor.equals(GamePanel.COLOR_HOMING_PURPLE)
                || Math.hypot(movingImpact.travelX, movingImpact.travelY) <= GamePanel.worldAmount(20.0)
                || Math.hypot(movingImpact.startOffsetX, movingImpact.startOffsetY) <= 0.0, "Smoke test failed: damage impact did not keep color or randomized movement.");
        gatePanel.renderAlpha = 0.5;
        failIf(Math.abs(gatePanel.renderX(100.0, 80.0) - 90.0) > 0.001, "Smoke test failed: render interpolation is not blending positions.");
        gatePanel.renderAlpha = 1.0;
        GamePanel frameClockPanel = new GamePanel(false);
        frameClockPanel.started = true;
        frameClockPanel.lastFrameNanos = System.nanoTime()
                - (long) (GamePanel.TICK_MS * 2.25 * 1_000_000.0);
        frameClockPanel.advanceFrameClock();
        failIf(frameClockPanel.tick != 2, "Smoke test failed: frame clock did not preserve the fixed update cadence.");
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
        failIf(Math.abs(speedProbeTarget.x - expectedTargetX) > 0.001
                || Math.abs(speedProbeBullet.x - expectedBulletX) > 0.001, "Smoke test failed: 120 FPS logic changed movement speed.");
        gatePanel.started = false;
        gatePanel.laneWords[0] = "code";
        gatePanel.laneWords[1] = "loop";
        gatePanel.handleLetter('c');
        gatePanel.step();
        failIf(gatePanel.typed.length() != 0 || gatePanel.tick != 0, "Smoke test failed: start gate accepted input or advanced time.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_F2));
        failIf(gatePanel.language != Language.CHINESE || gatePanel.started, "Smoke test failed: F2 did not switch language before start.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_3));
        failIf(gatePanel.difficulty != Difficulty.HARD, "Smoke test failed: difficulty selection did not apply before start.");
        failIf(gatePanel.nextSpawnCooldown() != new GamePanel(false).nextSpawnCooldown(), "Smoke test failed: difficulty changed spawn pacing.");
        GamePanel freshSpawnPanel = new GamePanel(false);
        GamePanel lateSpawnPanel = new GamePanel(false);
        lateSpawnPanel.tick = GamePanel.logicTicks(430 * 12);
        lateSpawnPanel.kills = 108;
        lateSpawnPanel.bossLevel = 3;
        failIf(lateSpawnPanel.nextSpawnCooldown() != freshSpawnPanel.nextSpawnCooldown(), "Smoke test failed: progress still changed enemy spawn density.");
        GamePanel easyScalePanel = new GamePanel(false);
        easyScalePanel.difficulty = Difficulty.EASY;
        GamePanel hardScalePanel = new GamePanel(false);
        hardScalePanel.difficulty = Difficulty.HARD;
        Target easyTarget = Target.enemy(0, TargetKind.NORMAL);
        Target hardTarget = Target.enemy(0, TargetKind.NORMAL);
        easyScalePanel.scaleTarget(easyTarget);
        hardScalePanel.scaleTarget(hardTarget);
        failIf(easyTarget.hp != hardTarget.hp || easyTarget.maxHp != hardTarget.maxHp, "Smoke test failed: difficulty changed enemy HP.");
        failIf(easyTarget.speed >= hardTarget.speed, "Smoke test failed: difficulty did not change enemy movement speed.");
        GamePanel easyBossPacing = new GamePanel(false);
        easyBossPacing.difficulty = Difficulty.EASY;
        GamePanel normalBossPacing = new GamePanel(false);
        normalBossPacing.difficulty = Difficulty.NORMAL;
        GamePanel hardBossPacing = new GamePanel(false);
        hardBossPacing.difficulty = Difficulty.HARD;
        failIf(hardBossPacing.bossLaserWarningDurationTicks() != GamePanel.BOSS_LASER_WARNING_TICKS
                || hardBossPacing.bossBarrageIntervalTicks() != GamePanel.BOSS_BARRAGE_INTERVAL_TICKS
                || hardBossPacing.bossBarrageJitterTicks() != GamePanel.BOSS_BARRAGE_JITTER_TICKS, "Smoke test failed: hard difficulty did not preserve current boss pacing.");
        failIf(normalBossPacing.bossLaserWarningDurationTicks() <= hardBossPacing.bossLaserWarningDurationTicks()
                || easyBossPacing.bossLaserWarningDurationTicks() <= normalBossPacing.bossLaserWarningDurationTicks()
                || normalBossPacing.bossBarrageIntervalTicks() <= hardBossPacing.bossBarrageIntervalTicks()
                || easyBossPacing.bossBarrageIntervalTicks() <= normalBossPacing.bossBarrageIntervalTicks(), "Smoke test failed: boss pacing did not scale by difficulty.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_ENTER));
        failIf(!gatePanel.started, "Smoke test failed: Enter did not start the run.");
        gatePanel.keyTyped(typedKey(gatePanel, 'c'));
        failIf(gatePanel.typed.length() != 0, "Smoke test failed: keyTyped still changed typing progress.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_C));
        failIf(!"c".equals(gatePanel.typed), "Smoke test failed: keyboard key codes were not accepted.");
        gatePanel.handleLetter('1');
        failIf(!"c".equals(gatePanel.typed), "Smoke test failed: non-letter input changed typing progress.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_BACK_SPACE));
        failIf(gatePanel.typed.length() != 0 || gatePanel.typingLane != -1, "Smoke test failed: Backspace did not clear the typing lane.");
        gatePanel.correctTypedChars = 25;
        gatePanel.runStartTick = 0;
        gatePanel.tick = 60;
        failIf(gatePanel.currentTypingSpeedWpm() <= 0, "Smoke test failed: current typing speed was not calculated.");
        GamePanel sudoPanel = new GamePanel(false);
        sudoPanel.started = false;
        sudoPanel.handleLetter('s');
        sudoPanel.handleLetter('u');
        sudoPanel.handleLetter('d');
        sudoPanel.handleLetter('o');
        failIf(!sudoPanel.started || sudoPanel.choiceMode != ChoiceMode.TEST_BACKEND, "Smoke test failed: sudo did not open the test backend.");
        selectTestEffect(sudoPanel, UpgradeEffect.TEST_INVINCIBLE);
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ENTER));
        failIf(!sudoPanel.testInvincible || sudoPanel.collisionCost(Target.enemy(0, TargetKind.NORMAL)) != 0, "Smoke test failed: sudo invincibility did not remove HP loss.");
        int pendingBeforeBigXp = sudoPanel.pendingUpgradeChoices;
        selectTestEffect(sudoPanel, UpgradeEffect.TEST_BIG_XP);
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ENTER));
        failIf(sudoPanel.pendingUpgradeChoices <= pendingBeforeBigXp, "Smoke test failed: sudo XP cache did not grant upgrade progress.");
        int pendingBeforeSudoGrant = sudoPanel.pendingUpgradeChoices;
        selectTestEffect(sudoPanel, UpgradeEffect.CALIBRATED_DAMAGE);
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ENTER));
        failIf(sudoPanel.damageBonusPercent <= 0 || sudoPanel.choiceMode != ChoiceMode.TEST_BACKEND
                || sudoPanel.pendingUpgradeChoices != pendingBeforeSudoGrant, "Smoke test failed: test backend did not grant upgrades cleanly.");
        int expectedSudoEffects = sudoPanel.sudoToolEffects().length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.COMMON).length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.UNCOMMON).length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.HIGH).length
                + sudoPanel.gameplayPoolFor(UpgradeRarity.RED).length;
        failIf(sudoPanel.allTestUpgradeEffects().length != expectedSudoEffects, "Smoke test failed: sudo backend is not synchronized with gameplay pools.");
        UpgradeEffect[] goldEffects = {
                UpgradeEffect.DRONE_SWARM,
                UpgradeEffect.OVERFLOW_ROUND,
                UpgradeEffect.MAGNETIC_FIELD,
                UpgradeEffect.UNDYING_TOTEM,
                UpgradeEffect.ADRENALINE,
                UpgradeEffect.MELEE,
                UpgradeEffect.RED_EYE
        };
        UpgradeEffect[] highPool = sudoPanel.gameplayPoolFor(UpgradeRarity.HIGH);
        for (UpgradeEffect effect : goldEffects) {
            failIf(!containsEffect(highPool, effect) || !containsEffect(sudoPanel.allTestUpgradeEffects(), effect)
                    || sudoPanel.rarityForEffect(effect) != UpgradeRarity.HIGH, "Smoke test failed: gold talent is not wired into high-rarity pools: " + effect + ".");
        }
        failIf(containsEffect(highPool, UpgradeEffect.GOLD_TALENT_PLACEHOLDER), "Smoke test failed: placeholder gold talent leaked into the real pool.");
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
                failIf(!containsEffect(commonPool, effect) || !containsEffect(sudoEffects, effect), "Smoke test failed: group-one upgrade is not visible in pools.");
            }
        }
        sudoPanel.keyPressed(key(sudoPanel, KeyEvent.VK_ESCAPE));
        failIf(sudoPanel.choiceMode != ChoiceMode.NONE, "Smoke test failed: test backend did not close with Esc.");
        sudoPanel.language = Language.CHINESE;
        UpgradeCard localizedSudoCard = sudoPanel.createCard(UpgradeEffect.CALIBRATED_DAMAGE, UpgradeRarity.COMMON);
        failIf(!"校准伤害".equals(sudoPanel.cardTitle(localizedSudoCard))
                || !"+12% 加算伤害".equals(sudoPanel.cardDescription(localizedSudoCard))
                || !"普通".equals(sudoPanel.rarityLabel(UpgradeRarity.COMMON)), "Smoke test failed: test backend labels did not localize.");
        UpgradeCard localizedTriggerCard = sudoPanel.createCard(UpgradeEffect.TRIGGER_TUNING, UpgradeRarity.COMMON);
        failIf(!"增加一发子弹或一道激光；齐射总伤害提高后再分摊。".equals(
                sudoPanel.upgradeChoiceDescription(localizedTriggerCard)), "Smoke test failed: trigger tuning description was not dynamic.");
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
            failIf(gatePanel.laneWords[0].equals(previous) || gatePanel.laneWords[0].equals(gatePanel.laneWords[1]), "Smoke test failed: lane word repeated after refresh.");
            failIf(gatePanel.laneWords[0].charAt(0) == gatePanel.laneWords[1].charAt(0), "Smoke test failed: lane words share an ambiguous first letter.");
            failIf(gatePanel.laneWords[0].length() < GamePanel.LANE_WORD_MIN_LENGTH
                    || gatePanel.laneWords[0].length() > GamePanel.LANE_WORD_MAX_LENGTH, "Smoke test failed: lane word length changed with progress.");
        }
        GamePanel highPressureWordPanel = new GamePanel(false);
        highPressureWordPanel.tick = GamePanel.logicTicks(430 * 12);
        highPressureWordPanel.kills = 108;
        highPressureWordPanel.bossLevel = 3;
        highPressureWordPanel.laneWords[0] = "code";
        highPressureWordPanel.laneWords[1] = "loop";
        highPressureWordPanel.refreshLaneWord(0);
        failIf(highPressureWordPanel.laneWords[0].length() < GamePanel.LANE_WORD_MIN_LENGTH
                || highPressureWordPanel.laneWords[0].length() > GamePanel.LANE_WORD_MAX_LENGTH, "Smoke test failed: high pressure still changed lane word length.");
        GamePanel highPressureSpawnPanel = new GamePanel(false);
        highPressureSpawnPanel.tick = GamePanel.logicTicks(430 * 12);
        highPressureSpawnPanel.kills = 108;
        highPressureSpawnPanel.bossLevel = 3;
        highPressureSpawnPanel.spawnRandomTarget();
        failIf(highPressureSpawnPanel.targets.size() != 1, "Smoke test failed: high pressure still created extra enemy density.");
        Target spawnedAtLaneEnd = highPressureSpawnPanel.targets.get(0);
        double spawnedRightEdge = spawnedAtLaneEnd.x + GamePanel.targetHalfWidth(spawnedAtLaneEnd.kind);
        failIf(Math.abs(spawnedRightEdge - GamePanel.LANE_RIGHT_X) > 0.001, "Smoke test failed: enemy did not spawn at the lane end.");
        Bullet rangeProbe = new Bullet(GamePanel.PLAYER_X, 0, 1, 1, BulletKind.BASIC);
        failIf(Math.abs(highPressureSpawnPanel.maxBulletX(rangeProbe) - GamePanel.LANE_RIGHT_X) > 0.001, "Smoke test failed: bullet range does not cover the full lane.");
        failIf(rangeProbe.lifeTicks * GamePanel.gameplayStep(rangeProbe.speed) < GamePanel.LANE_RIGHT_X - rangeProbe.x, "Smoke test failed: bullet lifetime does not cover the full lane.");

        Target frozen = Target.enemy(0, TargetKind.NORMAL);
        frozen.x = GamePanel.PLAYER_X + GamePanel.worldAmount(200);
        gatePanel.targets.add(frozen);
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_ESCAPE));
        int pausedTick = gatePanel.tick;
        double pausedX = frozen.x;
        gatePanel.step();
        failIf(!gatePanel.paused || gatePanel.tick != pausedTick || frozen.x != pausedX, "Smoke test failed: pause did not freeze the update loop.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_ESCAPE));
        failIf(gatePanel.paused, "Smoke test failed: Esc did not resume from pause.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_P));
        failIf(gatePanel.paused, "Smoke test failed: letter P still paused the game.");
        gatePanel.paused = true;
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_R));
        failIf(!gatePanel.paused || gatePanel.targets.size() == 0, "Smoke test failed: letter R still restarted from pause.");
        gatePanel.keyPressed(key(gatePanel, KeyEvent.VK_F5));
        failIf(gatePanel.paused || gatePanel.score != 0 || gatePanel.targets.size() != 0, "Smoke test failed: F5 did not reset from pause.");
        gatePanel.setFullscreenState(true);
        failIf(!gatePanel.fullscreen, "Smoke test failed: fullscreen state flag was not applied.");
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
        failIf(panel.typed.length() != 0 || panel.typingLane != -1, "Smoke test failed: typo did not clear lane-word input progress.");
        panel.laneWords[0] = "code";
        panel.laneWords[1] = "java";
        panel.handleLetter('c');
        failIf(panel.inputPulseTicks <= 0, "Smoke test failed: correct typing did not trigger input feedback.");
        panel.handleLetter('j');
        failIf(!"j".equals(panel.typed) || panel.typingLane != 1, "Smoke test failed: mismatch did not restart with the new letter.");
        failIf(!panel.laneHasInputPulse(1) || panel.laneHasInputPulse(0), "Smoke test failed: input pulse was not strictly bound to the typing lane.");
        failIf(panel.wrongFlashTicks <= 0 || panel.wrongFlashLane != 0, "Smoke test failed: mismatch did not mark the broken lane.");
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
        failIf(panel.completePulseTicks <= 0 || panel.completePulseLane != 0, "Smoke test failed: completed lane word did not trigger finish feedback.");
        failIf(!panel.laneWordShowsActiveHighlight(0), "Smoke test failed: completed lane lost its highlight before the green pulse ended.");
        failIf(!hasBulletKind(panel, BulletKind.BASIC, false), "Smoke test failed: basic bullets did not use the line trail style.");
        GamePanel highlightPanel = new GamePanel(false);
        highlightPanel.laneWords[0] = "code";
        highlightPanel.laneWords[1] = "loop";
        typeWord(highlightPanel, "code");
        for (int i = 0; i < GamePanel.WORD_COMPLETE_PULSE_TICKS; i++) {
            highlightPanel.step();
        }
        failIf(highlightPanel.completePulseTicks != 0 || highlightPanel.laneWordShowsActiveHighlight(0), "Smoke test failed: completed lane stayed highlighted after the green pulse.");
        highlightPanel.handleLetter(highlightPanel.laneWords[0].charAt(0));
        failIf(!highlightPanel.laneWordShowsActiveHighlight(0), "Smoke test failed: completed lane highlight did not return when typing restarted.");
        for (int i = 0; i < GamePanel.logicTicks(16); i++) {
            panel.step();
        }
        failIf(panel.score <= 0 && panel.targets.size() > 0, "Smoke test failed: typing did not damage or remove target.");
        failIf(panel.breakParticles.size() == 0, "Smoke test failed: defeated target did not create break particles.");
        failIf(panel.xpOrbs.size() == 0 && panel.xp == 0, "Smoke test failed: defeated hostile unit did not produce XP.");
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
        failIf(laneEndTarget.hp >= laneEndHpBefore, "Smoke test failed: full-lane attack did not reach the lane-end target.");
        int xpBeforeOrb = panel.xp;
        panel.xpOrbs.clear();
        panel.xpOrbs.add(new XpOrb(GamePanel.PLAYER_X, panel.lane, 1));
        panel.step();
        failIf(panel.xpOrbs.size() != 0 || panel.xp <= xpBeforeOrb, "Smoke test failed: XP ball was not collected by approaching it.");

        GamePanel attractPanel = new GamePanel(false);
        attractPanel.started = true;
        attractPanel.lane = 0;
        XpOrb attractOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(240), 0, 1);
        attractPanel.xpOrbs.add(attractOrb);
        double attractStart = attractOrb.x;
        for (int i = 0; i < GamePanel.logicTicks(1); i++) {
            attractPanel.step();
        }
        failIf(attractOrb.x >= attractStart - 2.0, "Smoke test failed: nearby same-lane XP ball did not attract.");
        failIf(!attractOrb.attracted, "Smoke test failed: attracted XP ball did not lock its magnet state.");
        double switchedLaneStart = attractOrb.x;
        double switchedLaneStartY = attractOrb.y;
        attractPanel.lane = 1;
        for (int i = 0; i < GamePanel.logicTicks(1); i++) {
            attractPanel.step();
        }
        failIf(attractOrb.x >= switchedLaneStart - attractOrb.speed - 1.0, "Smoke test failed: lane switch interrupted XP ball attraction.");
        failIf(attractOrb.y <= switchedLaneStartY, "Smoke test failed: attracted XP ball did not move toward the player lane.");
        GamePanel farOrbPanel = new GamePanel(false);
        farOrbPanel.started = true;
        farOrbPanel.lane = 0;
        XpOrb farOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(340), 0, 1);
        farOrbPanel.xpOrbs.add(farOrb);
        double farStart = farOrb.x;
        farOrbPanel.step();
        failIf(farOrb.x < farStart - GamePanel.gameplayStep(farOrb.speed * farOrbPanel.progressSpeedMultiplier()) - 0.01
                || farOrb.attracted, "Smoke test failed: far XP ball attracted from too far away.");
        GamePanel earlyXpSpeedPanel = new GamePanel(false);
        earlyXpSpeedPanel.started = true;
        earlyXpSpeedPanel.lane = 0;
        XpOrb earlySpeedOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(340), 0, 1);
        earlyXpSpeedPanel.xpOrbs.add(earlySpeedOrb);
        double earlySpeedStart = earlySpeedOrb.x;
        earlyXpSpeedPanel.step();
        double earlyXpMove = earlySpeedStart - earlySpeedOrb.x;
        GamePanel lateXpSpeedPanel = new GamePanel(false);
        lateXpSpeedPanel.started = true;
        lateXpSpeedPanel.lane = 0;
        lateXpSpeedPanel.tick = GamePanel.logicTicks(430 * 12);
        lateXpSpeedPanel.kills = 108;
        lateXpSpeedPanel.bossLevel = 3;
        XpOrb lateSpeedOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(340), 0, 1);
        lateXpSpeedPanel.xpOrbs.add(lateSpeedOrb);
        double lateSpeedStart = lateSpeedOrb.x;
        lateXpSpeedPanel.step();
        double lateXpMove = lateSpeedStart - lateSpeedOrb.x;
        failIf(lateXpMove <= earlyXpMove || lateXpSpeedPanel.progressSpeedMultiplier() <= earlyXpSpeedPanel.progressSpeedMultiplier(), "Smoke test failed: XP ball speed did not scale with game progress.");

        panel.xp = panel.xpToNext - 1;
        panel.xpOrbs.add(new XpOrb(GamePanel.PLAYER_X, panel.lane, 1));
        panel.step();
        failIf(panel.pendingUpgradeChoices <= 0 || panel.choiceMode != ChoiceMode.NONE, "Smoke test failed: full XP did not create a pending upgrade without opening it.");
        panel.keyPressed(key(panel, KeyEvent.VK_SPACE));
        failIf(panel.choiceMode != ChoiceMode.UPGRADE || panel.upgradeChoices[0] == null
                || panel.upgradeChoices[1] == null || panel.upgradeChoices[2] == null, "Smoke test failed: Space did not open the three-slot upgrade menu.");
        paintSmokeFrame(panel);
        for (UpgradeCard card : panel.upgradeChoices) {
            failIf(card.rarity == UpgradeRarity.RED, "Smoke test failed: normal upgrade menu offered boss-only red weapons.");
        }
        panel.keyPressed(key(panel, KeyEvent.VK_RIGHT));
        failIf(panel.selectedUpgradeIndex != 1, "Smoke test failed: upgrade menu selection did not move right.");
        int pendingBeforeUpgrade = panel.pendingUpgradeChoices;
        panel.keyPressed(key(panel, KeyEvent.VK_ENTER));
        failIf(panel.choiceMode != ChoiceMode.NONE || panel.pendingUpgradeChoices >= pendingBeforeUpgrade, "Smoke test failed: upgrade card was not applied from the menu.");
        GamePanel multiUpgradePanel = new GamePanel(false);
        multiUpgradePanel.started = true;
        multiUpgradePanel.pendingUpgradeChoices = 2;
        multiUpgradePanel.openUpgradeMenu();
        multiUpgradePanel.keyPressed(key(multiUpgradePanel, KeyEvent.VK_ENTER));
        failIf(multiUpgradePanel.choiceMode != ChoiceMode.UPGRADE || multiUpgradePanel.pendingUpgradeChoices != 1
                || multiUpgradePanel.upgradeChoices[0] == null || multiUpgradePanel.selectedUpgradeIndex != 0, "Smoke test failed: queued upgrade choices did not keep the menu open.");
        multiUpgradePanel.keyPressed(key(multiUpgradePanel, KeyEvent.VK_ENTER));
        failIf(multiUpgradePanel.choiceMode != ChoiceMode.NONE || multiUpgradePanel.pendingUpgradeChoices != 0, "Smoke test failed: final queued upgrade choice did not close the menu.");
        GamePanel abandonPanel = new GamePanel(false);
        abandonPanel.started = true;
        abandonPanel.pendingUpgradeChoices = 1;
        abandonPanel.keyPressed(key(abandonPanel, KeyEvent.VK_SPACE));
        abandonPanel.keyPressed(key(abandonPanel, KeyEvent.VK_ESCAPE));
        failIf(abandonPanel.choiceMode != ChoiceMode.NONE || abandonPanel.pendingUpgradeChoices != 0, "Smoke test failed: upgrade abandon option did not consume a pending choice.");
        GamePanel overviewPanel = new GamePanel(false);
        overviewPanel.started = true;
        overviewPanel.xpToNext = 12;
        overviewPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT;
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_SPACE));
        failIf(overviewPanel.choiceMode != ChoiceMode.OVERVIEW || !overviewPanel.overviewSelectionActive
                || overviewPanel.pendingUpgradeChoices != 0 || overviewPanel.upgradeChoices[0] != null
                || overviewPanel.upgradeChoices[1] != null || overviewPanel.upgradeChoices[2] != null, "Smoke test failed: Space did not open the standalone upgrade overview.");
        paintSmokeFrame(overviewPanel);
        int overviewTickBefore = overviewPanel.tick;
        overviewPanel.step();
        failIf(overviewPanel.tick != overviewTickBefore, "Smoke test failed: standalone upgrade overview did not freeze logic.");
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_ENTER));
        failIf(overviewPanel.choiceMode != ChoiceMode.SELL_CONFIRM
                || overviewPanel.fireRateBonusPercent != GamePanel.TRIGGER_TUNING_STEP_PERCENT
                || overviewPanel.xp != 0, "Smoke test failed: overview sell did not require confirmation first.");
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_N));
        failIf(overviewPanel.choiceMode != ChoiceMode.OVERVIEW
                || overviewPanel.fireRateBonusPercent != GamePanel.TRIGGER_TUNING_STEP_PERCENT
                || overviewPanel.xp != 0, "Smoke test failed: cancelling overview sell still changed the card.");
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_ENTER));
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_ENTER));
        failIf(overviewPanel.fireRateBonusPercent != 0 || overviewPanel.xp != 4
                || overviewPanel.choiceMode != ChoiceMode.OVERVIEW, "Smoke test failed: standalone overview could not sell an owned card.");
        overviewPanel.keyPressed(key(overviewPanel, KeyEvent.VK_SPACE));
        failIf(overviewPanel.choiceMode != ChoiceMode.NONE, "Smoke test failed: Space did not close the standalone overview.");
        GamePanel overviewNavPanel = new GamePanel(false);
        overviewNavPanel.started = true;
        overviewNavPanel.damageBonusPercent = 12;
        overviewNavPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT;
        overviewNavPanel.perfectBonus = 1;
        overviewNavPanel.longWordRewardLevel = 1;
        overviewNavPanel.phaseSwitchLevel = 1;
        overviewNavPanel.bossBreakerLevel = 1;
        overviewNavPanel.openUpgradeOverview();
        overviewNavPanel.selectedOverviewCardIndex = 0;
        overviewNavPanel.keyPressed(key(overviewNavPanel, KeyEvent.VK_DOWN));
        failIf(overviewNavPanel.selectedOverviewCardIndex != 3, "Smoke test failed: standalone overview Down did not move one grid row.");
        overviewNavPanel.keyPressed(key(overviewNavPanel, KeyEvent.VK_UP));
        failIf(overviewNavPanel.selectedOverviewCardIndex != 0, "Smoke test failed: standalone overview Up did not move one grid row.");
        GamePanel pausedOverviewPanel = new GamePanel(false);
        pausedOverviewPanel.started = true;
        pausedOverviewPanel.paused = true;
        pausedOverviewPanel.keyPressed(key(pausedOverviewPanel, KeyEvent.VK_SPACE));
        failIf(pausedOverviewPanel.choiceMode != ChoiceMode.OVERVIEW || !pausedOverviewPanel.paused, "Smoke test failed: Space did not open overview while paused.");
        pausedOverviewPanel.keyPressed(key(pausedOverviewPanel, KeyEvent.VK_ESCAPE));
        failIf(pausedOverviewPanel.choiceMode != ChoiceMode.NONE || !pausedOverviewPanel.paused, "Smoke test failed: closing paused overview changed pause state.");

        GamePanel bossHitPanel = new GamePanel(false);
        Target wideBoss = Target.boss(1, 1);
        wideBoss.x = GamePanel.PLAYER_X + GamePanel.worldAmount(210);
        wideBoss.hp = 40;
        wideBoss.maxHp = 40;
        bossHitPanel.targets.add(wideBoss);
        Bullet crossLaneBullet = new Bullet(wideBoss.x, 0, 4, 1, BulletKind.BASIC);
        crossLaneBullet.previousX = wideBoss.x - GamePanel.worldAmount(28);
        bossHitPanel.bullets.add(crossLaneBullet);
        bossHitPanel.updateBullets();
        failIf(wideBoss.hp != 36, "Smoke test failed: boss cannot be hit from both lanes.");

        GamePanel bossAttackPanel = new GamePanel(false);
        bossAttackPanel.started = true;
        bossAttackPanel.spawnCooldown = GamePanel.logicTicks(999);
        Target frozenBoss = Target.boss(0, 1);
        frozenBoss.bossAttackCooldownTicks = 1;
        frozenBoss.freezeTicks = GamePanel.logicTicks(3);
        bossAttackPanel.targets.add(frozenBoss);
        bossAttackPanel.step();
        failIf(frozenBoss.bossAttackCooldownTicks != 1 || frozenBoss.bossLaserWarningTicks != 0
                || !bossAttackPanel.bossProjectiles.isEmpty(), "Smoke test failed: frozen boss still advanced remote attack timing.");
        frozenBoss.freezeTicks = 0;
        bossAttackPanel.step();
        failIf(frozenBoss.bossLaserWarningTicks != bossAttackPanel.bossLaserWarningDurationTicks()
                || frozenBoss.bossLaserLane < 0, "Smoke test failed: high-health boss did not start a laser warning.");

        GamePanel bossLaserPanel = new GamePanel(false);
        bossLaserPanel.started = true;
        bossLaserPanel.spawnCooldown = GamePanel.logicTicks(999);
        bossLaserPanel.bossLevel = 2;
        bossLaserPanel.lane = 1;
        Target laserBoss = Target.boss(0, 2);
        laserBoss.bossLaserWarningTicks = 1;
        laserBoss.bossLaserLane = 1;
        int hpBeforeBossLaser = bossLaserPanel.hp;
        bossLaserPanel.updateBossLaserAttack(laserBoss);
        failIf(bossLaserPanel.hp != hpBeforeBossLaser - GamePanel.BOSS_LASER_DAMAGE - bossLaserPanel.bossLevel
                || bossLaserPanel.bossLaserFlashLane != 1 || bossLaserPanel.bossLaserFlashTicks <= 0, "Smoke test failed: boss laser warning did not resolve into lane damage.");

        GamePanel bossBarragePanel = new GamePanel(false);
        bossBarragePanel.started = true;
        bossBarragePanel.spawnCooldown = GamePanel.logicTicks(999);
        bossBarragePanel.bossLevel = 2;
        Target barrageBoss = Target.boss(0, 2);
        barrageBoss.hp = barrageBoss.maxHp / 2;
        barrageBoss.bossAttackCooldownTicks = 1;
        bossBarragePanel.updateBossAttack(barrageBoss);
        failIf(bossBarragePanel.bossProjectiles.size() != 1, "Smoke test failed: low-health boss did not spawn barrage projectiles.");
        failIf(barrageBoss.bossAttackCooldownTicks < bossBarragePanel.bossBarrageIntervalTicks()
                || bossBarragePanel.bossBarrageIntervalTicks() <= GamePanel.BOSS_BARRAGE_INTERVAL_TICKS, "Smoke test failed: boss barrage interval was not lengthened.");
        BossProjectile bossProjectile = bossBarragePanel.bossProjectiles.get(0);
        bossBarragePanel.lane = bossProjectile.lane;
        bossProjectile.x = GamePanel.PLAYER_X + bossProjectile.radius + 1.0;
        int hpBeforeBarrage = bossBarragePanel.hp;
        bossBarragePanel.updateBossProjectiles();
        failIf(bossBarragePanel.hp != hpBeforeBarrage - bossProjectile.damage
                || !bossBarragePanel.bossProjectiles.isEmpty(), "Smoke test failed: boss barrage projectile did not damage and despawn.");

        GamePanel bossGatePanel = new GamePanel(false);
        bossGatePanel.started = true;
        bossGatePanel.kills = bossGatePanel.bossCooldownKills;
        bossGatePanel.spawnCooldown = 0;
        Target blocker = Target.enemy(0, TargetKind.NORMAL);
        blocker.x = GamePanel.PLAYER_X + GamePanel.worldAmount(420);
        bossGatePanel.targets.add(blocker);
        int bossGateBefore = bossGatePanel.bossCooldownKills;
        bossGatePanel.step();
        failIf(bossGatePanel.hasBoss() || bossGatePanel.bossCooldownKills != bossGateBefore
                || bossGatePanel.targets.size() != 1, "Smoke test failed: boss spawned or normal spawn continued before the field was clear.");
        bossGatePanel.targets.clear();
        bossGatePanel.step();
        failIf(!bossGatePanel.hasBoss() || bossGatePanel.bossCooldownKills <= bossGateBefore, "Smoke test failed: boss did not spawn after non-boss targets were cleared.");

        GamePanel bossDeathPanel = new GamePanel(false);
        bossDeathPanel.started = true;
        bossDeathPanel.spawnCooldown = GamePanel.logicTicks(999);
        bossDeathPanel.bossLevel = 1;
        Target dyingBoss = Target.boss(0, 1);
        dyingBoss.hp = 0;
        bossDeathPanel.targets.add(dyingBoss);
        bossDeathPanel.removeDeadAndEscaped();
        failIf(!dyingBoss.bossDeathAnimating || bossDeathPanel.targets.isEmpty()
                || bossDeathPanel.choiceMode != ChoiceMode.NONE || bossDeathPanel.screenFlashTicks != 0, "Smoke test failed: boss death did not enter the collapse animation first.");
        dyingBoss.bossDeathTicks = 0;
        bossDeathPanel.removeDeadAndEscaped();
        failIf(!bossDeathPanel.targets.isEmpty() || bossDeathPanel.screenFlashTicks != GamePanel.BOSS_SCREEN_FLASH_TICKS
                || bossDeathPanel.choiceMode != ChoiceMode.UPGRADE || !bossDeathPanel.bossRewardChoice, "Smoke test failed: boss collapse did not finish with flash and reward.");
        bossDeathPanel.bossProjectiles.add(new BossProjectile(GamePanel.PLAYER_X, 0, 1, 1.0));
        bossDeathPanel.bossLaserFlashTicks = 4;
        bossDeathPanel.bossLaserFlashLane = 1;
        bossDeathPanel.screenFlashTicks = 5;
        bossDeathPanel.reset();
        failIf(!bossDeathPanel.bossProjectiles.isEmpty() || bossDeathPanel.bossLaserFlashTicks != 0
                || bossDeathPanel.bossLaserFlashLane != -1 || bossDeathPanel.screenFlashTicks != 0, "Smoke test failed: boss visual state leaked across reset.");

        GamePanel bossRewardPanel = new GamePanel(false);
        bossRewardPanel.started = true;
        bossRewardPanel.bossLevel = 2;
        Target rewardBoss = Target.boss(0, 2);
        bossRewardPanel.rewardFor(rewardBoss);
        failIf(bossRewardPanel.choiceMode != ChoiceMode.UPGRADE || !bossRewardPanel.bossRewardChoice
                || bossRewardPanel.xpOrbs.size() != 0 || bossRewardPanel.pendingUpgradeChoices != 0, "Smoke test failed: boss reward did not open the boss-only upgrade menu.");
        failIf(bossRewardPanel.upgradeChoices[0].rarity != UpgradeRarity.RED
                || bossRewardPanel.upgradeChoices[1].rarity != UpgradeRarity.RED
                || bossRewardPanel.upgradeChoices[2].rarity != UpgradeRarity.HIGH
                || !bossRewardPanel.isWeaponEffect(bossRewardPanel.upgradeChoices[0].effect)
                || !bossRewardPanel.isWeaponEffect(bossRewardPanel.upgradeChoices[1].effect)
                || !bossRewardPanel.isGoldTalentEffect(bossRewardPanel.upgradeChoices[2].effect), "Smoke test failed: boss reward slots are not weapon/weapon/gold choices.");
        UpgradeEffect bossWeapon = bossRewardPanel.upgradeChoices[0].effect;
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_1));
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_ENTER));
        failIf(bossRewardPanel.choiceMode != ChoiceMode.NONE || bossRewardPanel.bossRewardChoice
                || !bossRewardPanel.hasHighTalent(bossWeapon) || bossRewardPanel.weaponLevel(bossWeapon) != 1, "Smoke test failed: boss red weapon choice was not installed cleanly.");
        bossRewardPanel.bossLevel = 3;
        bossRewardPanel.openBossRewardMenu();
        failIf(bossRewardPanel.upgradeChoices[0].effect != bossWeapon
                || bossRewardPanel.upgradeChoices[0].rarity != UpgradeRarity.RED, "Smoke test failed: boss reward first slot did not keep current weapon.");
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_1));
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_ENTER));
        failIf(bossRewardPanel.weaponLevel(bossWeapon) != 2 || bossRewardPanel.choiceMode != ChoiceMode.NONE, "Smoke test failed: current weapon did not upgrade to level 2.");
        bossRewardPanel.openBossRewardMenu();
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_1));
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_ENTER));
        failIf(bossRewardPanel.weaponLevel(bossWeapon) != GamePanel.MAX_WEAPON_LEVEL, "Smoke test failed: current weapon did not upgrade to max level.");
        bossRewardPanel.openBossRewardMenu();
        failIf(bossRewardPanel.upgradeChoices[0].rarity != UpgradeRarity.HIGH
                || !bossRewardPanel.isGoldTalentEffect(bossRewardPanel.upgradeChoices[0].effect)
                || bossRewardPanel.upgradeChoices[1].rarity != UpgradeRarity.RED
                || !bossRewardPanel.isWeaponEffect(bossRewardPanel.upgradeChoices[1].effect)
                || bossRewardPanel.upgradeChoices[2].rarity != UpgradeRarity.HIGH
                || !bossRewardPanel.isGoldTalentEffect(bossRewardPanel.upgradeChoices[2].effect), "Smoke test failed: maxed current weapon did not convert boss reward to gold/weapon/gold.");
        int goldBeforeBossReward = bossRewardPanel.goldTalentCount();
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_1));
        bossRewardPanel.keyPressed(key(bossRewardPanel, KeyEvent.VK_ENTER));
        failIf(bossRewardPanel.goldTalentCount() != goldBeforeBossReward + 1
                || bossRewardPanel.choiceMode != ChoiceMode.NONE, "Smoke test failed: converted boss gold reward was not installed cleanly.");
        GamePanel weaponCachePanel = new GamePanel(false);
        weaponCachePanel.started = true;
        weaponCachePanel.bossLevel = 3;
        weaponCachePanel.maxHp = 40;
        weaponCachePanel.hp = 10;
        int pendingBeforeDirectCache = weaponCachePanel.pendingUpgradeChoices;
        weaponCachePanel.applyUpgradeCard(weaponCachePanel.weaponXpCacheCard(), false, false);
        failIf(weaponCachePanel.hp != 10
                || weaponCachePanel.pendingUpgradeChoices <= pendingBeforeDirectCache, "Smoke test failed: weapon XP cache should grant XP without normal-upgrade healing.");
        GamePanel xpHealPanel = new GamePanel(false);
        xpHealPanel.started = true;
        xpHealPanel.maxHp = 40;
        xpHealPanel.hp = 10;
        xpHealPanel.pendingUpgradeChoices = 1;
        xpHealPanel.applyUpgradeCard(xpHealPanel.createCard(UpgradeEffect.COMBO_TUNING, UpgradeRarity.COMMON),
                true, true);
        failIf(xpHealPanel.hp != 30 || xpHealPanel.pendingUpgradeChoices != 0
                || xpHealPanel.perfectBonus != 1, "Smoke test failed: normal XP upgrade choice did not heal half HP after applying.");
        failIf(bossRewardPanel.highTalents.length != GamePanel.MAX_RED_WEAPONS
                || GamePanel.MAX_RED_WEAPONS != 1, "Smoke test failed: red weapons are not limited to one slot.");
        GamePanel singleWeaponPanel = new GamePanel(false);
        singleWeaponPanel.started = true;
        singleWeaponPanel.xpToNext = 999;
        singleWeaponPanel.highTalents[0] = UpgradeEffect.RHYTHM_CANNON;
        singleWeaponPanel.setWeaponLevel(UpgradeEffect.RHYTHM_CANNON, 2);
        singleWeaponPanel.applyUpgradeCard(singleWeaponPanel.createCard(UpgradeEffect.FROST_FIELD,
                UpgradeRarity.RED), false, true);
        failIf(singleWeaponPanel.choiceMode != ChoiceMode.HIGH_REPLACE
                || singleWeaponPanel.highTalents[0] != UpgradeEffect.RHYTHM_CANNON, "Smoke test failed: second red weapon did not enter replacement flow.");
        singleWeaponPanel.keyPressed(key(singleWeaponPanel, KeyEvent.VK_2));
        failIf(singleWeaponPanel.choiceMode != ChoiceMode.HIGH_REPLACE
                || singleWeaponPanel.highTalents[0] != UpgradeEffect.RHYTHM_CANNON, "Smoke test failed: invalid red weapon replacement slot was accepted.");
        singleWeaponPanel.keyPressed(key(singleWeaponPanel, KeyEvent.VK_1));
        failIf(singleWeaponPanel.choiceMode != ChoiceMode.NONE
                || singleWeaponPanel.highTalents[0] != UpgradeEffect.FROST_FIELD
                || singleWeaponPanel.weaponLevel(UpgradeEffect.RHYTHM_CANNON) != 0
                || singleWeaponPanel.weaponLevel(UpgradeEffect.FROST_FIELD) != 1
                || singleWeaponPanel.xp != 60, "Smoke test failed: one-slot red weapon replacement did not install or refund level price.");
        GamePanel basicWeaponPanel = new GamePanel(false);
        basicWeaponPanel.started = true;
        basicWeaponPanel.highTalents[0] = UpgradeEffect.BASIC_WEAPON;
        basicWeaponPanel.setWeaponLevel(UpgradeEffect.BASIC_WEAPON, 2);
        UpgradeCard basicCard = basicWeaponPanel.createCard(UpgradeEffect.BASIC_WEAPON, UpgradeRarity.RED);
        failIf(!"Basic Gun".equals(basicCard.title) || !"基础枪".equals(basicCard.titleZh), "Smoke test failed: basic weapon naming drifted.");
        basicWeaponPanel.rewardFor(Target.enemy(0, TargetKind.NORMAL));
        failIf(basicWeaponPanel.basicWeaponKillDamageBonusPercent != 1
                || basicWeaponPanel.basicWeaponDamage(100) != 101, "Smoke test failed: level-2 basic weapon did not stack kill damage.");
        failIf(!"101.000%".equals(basicWeaponPanel.basicWeaponMultiplierText())
                || !basicWeaponPanel.message.contains("101.000%")
                || !"101.000%".equals(basicWeaponPanel.basicDamageHudValue)
                || basicWeaponPanel.basicDamageHudTransitionTicks <= 0
                || !basicWeaponPanel.highTalentHudText().contains("Basic Gun Lv 2")
                || basicWeaponPanel.highTalentHudText().contains("101.000%")
                || !basicWeaponPanel.inventoryStatusText(new UpgradeInventoryCard(UpgradeEffect.BASIC_WEAPON,
                        UpgradeRarity.RED, 2, 0)).contains("101.000%"), "Smoke test failed: basic weapon multiplier UI did not update.");
        basicWeaponPanel.basicWeaponKillDamageBonusPercent = 24;
        failIf(basicWeaponPanel.basicWeaponDamage(11) != 14, "Smoke test failed: basic weapon multiplier was not applied to bullet damage.");
        basicWeaponPanel.setWeaponLevel(UpgradeEffect.BASIC_WEAPON, GamePanel.MAX_WEAPON_LEVEL);
        basicWeaponPanel.laneWords[0] = "code";
        basicWeaponPanel.laneWords[1] = "loop";
        typeWord(basicWeaponPanel, "code");
        failIf(!hasBulletKind(basicWeaponPanel, BulletKind.BUBBLE) || hasBulletKind(basicWeaponPanel, BulletKind.BASIC), "Smoke test failed: level-3 basic weapon did not replace shots with bubbles.");
        Bullet bubble = firstBulletOfKind(basicWeaponPanel, BulletKind.BUBBLE);
        Bullet weakBubble = new Bullet(0, 0, 12, 1, BulletKind.BUBBLE);
        Bullet strongBubble = new Bullet(0, 0, 80, 1, BulletKind.BUBBLE);
        failIf(basicWeaponPanel.bubbleContactIntervalTicks(strongBubble) >= basicWeaponPanel.bubbleContactIntervalTicks(weakBubble)
                || basicWeaponPanel.bubblePushStep(strongBubble) <= basicWeaponPanel.bubblePushStep(weakBubble), "Smoke test failed: high-damage bubbles did not gain pressure.");
        Target bubbleTarget = Target.enemy(0, TargetKind.TANK);
        bubbleTarget.x = bubble.x + GamePanel.worldAmount(8);
        bubbleTarget.hp = 30;
        bubbleTarget.maxHp = 30;
        basicWeaponPanel.targets.add(bubbleTarget);
        double bubbleTargetStart = bubbleTarget.x;
        int bubbleDamageStart = bubble.remainingDamage;
        for (int i = 0; i < GamePanel.logicTicks(7); i++) {
            basicWeaponPanel.step();
        }
        failIf(bubbleTarget.hp >= bubbleTarget.maxHp || bubble.remainingDamage >= bubbleDamageStart
                || bubbleTarget.x <= bubbleTargetStart, "Smoke test failed: basic weapon bubble did not drain and push targets.");
        GamePanel bubbleEdgePanel = new GamePanel(false);
        bubbleEdgePanel.started = true;
        bubbleEdgePanel.spawnCooldown = GamePanel.logicTicks(999);
        Target edgeTarget = Target.enemy(0, TargetKind.TANK);
        edgeTarget.x = bubbleEdgePanel.bubbleTargetRightEdge(edgeTarget) - GamePanel.worldAmount(2);
        edgeTarget.previousX = edgeTarget.x;
        edgeTarget.hp = 999;
        edgeTarget.maxHp = 999;
        Bullet edgeBubble = new Bullet(edgeTarget.x - GamePanel.targetHalfWidth(edgeTarget.kind)
                - GamePanel.worldAmount(40), 0, 120, 1, BulletKind.BUBBLE);
        edgeBubble.remainingDamage = 9999;
        edgeBubble.previousX = edgeBubble.x;
        bubbleEdgePanel.targets.add(edgeTarget);
        bubbleEdgePanel.bullets.add(edgeBubble);
        bubbleEdgePanel.applyBubbleContacts(edgeBubble);
        double edgeX = bubbleEdgePanel.bubbleTargetRightEdge(edgeTarget);
        double holdX = bubbleEdgePanel.bubbleHoldX(edgeBubble, edgeTarget);
        failIf(Math.abs(edgeTarget.x - edgeX) > 0.001 || Math.abs(edgeBubble.x - holdX) > 0.001, "Smoke test failed: bubble did not lock target and projectile at lane edge.");
        for (int i = 0; i < GamePanel.logicTicks(8); i++) {
            bubbleEdgePanel.step();
        }
        failIf(edgeTarget.x > edgeX + 0.001 || Math.abs(edgeBubble.x - holdX) > GamePanel.worldAmount(1.0)
                || !bubbleEdgePanel.bullets.contains(edgeBubble), "Smoke test failed: bubble edge lock did not hold over time.");

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
        failIf(panel.lane != 1 || downTarget.hp >= downHpBefore, "Smoke test failed: DOWN lane word did not move and attack that lane.");

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
        failIf(!hasBulletKind(panel, BulletKind.CONTINUOUS), "Smoke test failed: autocannon did not fire without typing.");
        failIf(!hasBulletKind(panel, BulletKind.CONTINUOUS, false), "Smoke test failed: low-rate autocannon should fire blue bullets without trails.");
        panel.laneWords[0] = "stream";
        panel.laneWords[1] = "java";
        typeWord(panel, "stream");
        failIf(panel.continuousSurgeTicks <= 0, "Smoke test failed: typing did not empower the autocannon.");
        failIf(hasBulletKind(panel, BulletKind.BASIC), "Smoke test failed: autocannon fired the basic shot while being empowered.");
        panel.bullets.clear();
        panel.step();
        failIf(!hasBulletKind(panel, BulletKind.CONTINUOUS_SURGE), "Smoke test failed: empowered autocannon did not fire surge rounds.");
        failIf(!hasBulletKind(panel, BulletKind.CONTINUOUS_SURGE, true), "Smoke test failed: high-rate autocannon bullets need golden particle trails.");
        GamePanel stackedAutoPanel = new GamePanel(false);
        stackedAutoPanel.started = true;
        stackedAutoPanel.highTalents[0] = UpgradeEffect.RHYTHM_CANNON;
        stackedAutoPanel.setWeaponLevel(UpgradeEffect.RHYTHM_CANNON, GamePanel.MAX_WEAPON_LEVEL);
        stackedAutoPanel.applyHighTalentAfterAttack("code", 0, streamTarget);
        failIf(!"x1.50".equals(stackedAutoPanel.autoRateHudValue)
                || stackedAutoPanel.autoRateHudTransitionTicks <= 0, "Smoke test failed: autocannon HUD did not animate on stack update.");
        stackedAutoPanel.autoCannonStackPercent = 120.0;
        for (int i = 0; i < GamePanel.LOGIC_FPS; i++) {
            stackedAutoPanel.decayAutoCannonStack();
        }
        failIf(Math.abs(stackedAutoPanel.autoCannonStackPercent - 100.0) > 0.001, "Smoke test failed: level-3 autocannon stack did not decay by 20 percent per second.");
        stackedAutoPanel.autoCannonStackPercent = 150.0;
        stackedAutoPanel.step();
        failIf(!hasBulletKind(stackedAutoPanel, BulletKind.CONTINUOUS)
                || hasBulletKind(stackedAutoPanel, BulletKind.CONTINUOUS_SURGE), "Smoke test failed: sub-3x autocannon should fire only blue bullets.");
        stackedAutoPanel.bullets.clear();
        stackedAutoPanel.autoFireCooldown = 0;
        stackedAutoPanel.autoCannonStackPercent = 200.0;
        stackedAutoPanel.step();
        failIf(!hasBulletKind(stackedAutoPanel, BulletKind.CONTINUOUS_SURGE, true)
                || hasBulletKind(stackedAutoPanel, BulletKind.CONTINUOUS), "Smoke test failed: 3x autocannon should fire only golden trail bullets.");

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
        failIf(!hasBulletKind(piercePanel, BulletKind.PIERCE), "Smoke test failed: Laser Gun did not fire a laser beam.");
        failIf(hasBulletKind(piercePanel, BulletKind.BASIC), "Smoke test failed: Laser Gun should replace the basic shot.");
        Bullet laserBeam = firstBulletOfKind(piercePanel, BulletKind.PIERCE);
        failIf(!laserBeam.beamResolved || laserBeam.x < GamePanel.LANE_RIGHT_X, "Smoke test failed: Laser Gun did not resolve as an instant beam.");
        failIf(laserBeam.laserPowerPercent != 100, "Smoke test failed: base Laser Gun did not keep the red power color tier.");
        Bullet pierceProbe = new Bullet(GamePanel.PLAYER_X, 0, 10, 5, BulletKind.PIERCE);
        failIf(piercePanel.bulletDamageForHit(pierceProbe) != 10, "Smoke test failed: first pierce hit did not keep full damage.");
        pierceProbe.hits = 1;
        failIf(piercePanel.bulletDamageForHit(pierceProbe) != 10, "Smoke test failed: base Laser Gun pierce damage decayed.");
        piercePanel.setWeaponLevel(UpgradeEffect.FROST_FIELD, 2);
        Target burnTarget = Target.enemy(0, TargetKind.TANK);
        burnTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(180);
        burnTarget.hp = 999;
        burnTarget.maxHp = 999;
        piercePanel.targets.add(burnTarget);
        int burnParticlesBefore = piercePanel.bulletTrailParticles.size();
        for (int i = 0; i < GamePanel.LASER_BURN_HIT_THRESHOLD; i++) {
            piercePanel.applyBulletHit(new Bullet(GamePanel.PLAYER_X, 0, 10, 5, BulletKind.PIERCE), burnTarget);
        }
        failIf(burnTarget.laserHitCount != GamePanel.LASER_BURN_HIT_THRESHOLD
                || burnTarget.laserBurnTicks <= 0
                || piercePanel.bulletTrailParticles.size() <= burnParticlesBefore, "Smoke test failed: level-2 Laser Gun did not ignite after 5 hits.");
        int earlyBurnDamage = piercePanel.laserBurnDamage(burnTarget);
        burnTarget.laserBurnAgeTicks = GamePanel.LASER_BURN_RAMP_TICKS * 2;
        failIf(piercePanel.laserBurnDamage(burnTarget) <= earlyBurnDamage, "Smoke test failed: Laser Gun burn damage did not ramp up.");
        burnTarget.laserBurnAgeTicks = 0;
        burnTarget.laserBurnTicks = GamePanel.LASER_BURN_DURATION_TICKS;
        int burnHpBefore = burnTarget.hp;
        for (int i = 0; i < GamePanel.LASER_BURN_DAMAGE_INTERVAL_TICKS; i++) {
            piercePanel.tick++;
            piercePanel.updateLaserBurns();
        }
        failIf(burnTarget.hp >= burnHpBefore, "Smoke test failed: Laser Gun burn did not deal damage over time.");
        piercePanel.setWeaponLevel(UpgradeEffect.FROST_FIELD, GamePanel.MAX_WEAPON_LEVEL);
        int sustainedBefore = countBulletKind(piercePanel, BulletKind.PIERCE);
        piercePanel.applyHighTalentAfterAttack("loop", 0, first);
        failIf(piercePanel.sustainedLaserTicks != GamePanel.SUSTAINED_LASER_DURATION_TICKS, "Smoke test failed: level-3 Laser Gun did not refresh to 2 seconds.");
        piercePanel.sustainedLaserChargeTicks = GamePanel.LASER_DAMAGE_PERCENT_UPDATE_TICKS - 1;
        failIf(piercePanel.sustainedLaserDamagePercent() != 100, "Smoke test failed: Laser Gun damage percent updated before 0.3 seconds.");
        piercePanel.updateCombatStatusHud();
        piercePanel.sustainedLaserChargeTicks = GamePanel.LASER_DAMAGE_PERCENT_UPDATE_TICKS;
        int steppedLaserPercent = piercePanel.sustainedLaserDamagePercent();
        piercePanel.updateCombatStatusHud();
        failIf(!piercePanel.laserDamageHudValue.equals(steppedLaserPercent + "%")
                || piercePanel.laserDamageHudTransitionTicks <= 0, "Smoke test failed: Laser Gun HUD did not animate on 0.3s damage step.");
        piercePanel.sustainedLaserChargeTicks = GamePanel.SUSTAINED_LASER_RAMP_TICKS;
        failIf(piercePanel.sustainedLaserDamagePercent() != 300, "Smoke test failed: sustained Laser Gun did not cap at 300 percent.");
        GamePanel laserVisualPanel = new GamePanel(false);
        laserVisualPanel.fireLaserVolley(0, 10, 100);
        int baseLaserParticleCount = laserVisualPanel.bulletTrailParticles.size();
        Bullet redLaser = firstBulletOfKind(laserVisualPanel, BulletKind.PIERCE);
        Bullet orangeLaser = new Bullet(GamePanel.PLAYER_X, 0, 10, 5, BulletKind.PIERCE);
        orangeLaser.laserPowerPercent = 200;
        Color redCore = laserVisualPanel.laserCoreColor(redLaser);
        Color orangeCore = laserVisualPanel.laserCoreColor(orangeLaser);
        laserVisualPanel.bullets.clear();
        laserVisualPanel.bulletTrailParticles.clear();
        laserVisualPanel.fireLaserVolley(0, 10, 300);
        Bullet yellowLaser = firstBulletOfKind(laserVisualPanel, BulletKind.PIERCE);
        Color yellowCore = laserVisualPanel.laserCoreColor(yellowLaser);
        failIf(redLaser.laserPowerPercent != 100 || yellowLaser.laserPowerPercent != 300
                || !(redCore.getGreen() < orangeCore.getGreen()
                        && orangeCore.getGreen() < yellowCore.getGreen())
                || laserVisualPanel.bulletTrailParticles.size() <= baseLaserParticleCount, "Smoke test failed: sustained Laser Gun visual power ramp is not red-orange-yellow with max particles.");
        piercePanel.sustainedLaserChargeTicks = 0;
        piercePanel.step();
        failIf(piercePanel.sustainedLaserTicks <= 0
                || countBulletKind(piercePanel, BulletKind.PIERCE) <= sustainedBefore, "Smoke test failed: level-3 Laser Gun did not start sustained beams.");
        for (int i = 0; i < GamePanel.logicTicks(14); i++) {
            piercePanel.step();
        }
        failIf(piercePanel.impacts.size() == 0 || first.hp >= first.maxHp || second.hp >= second.maxHp, "Smoke test failed: Laser Gun did not hit multiple lane targets.");
        for (Bullet bullet : piercePanel.bullets) {
            failIf(bullet.kind != BulletKind.PIERCE && bullet.hitTargets != null, "Smoke test failed: non-pierce bullets allocated hit target lists.");
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
        failIf(countBulletKind(tunedLaserPanel, BulletKind.PIERCE) != 2 || tunedLaserDamage != 12
                || tunedLaserTarget.hp != tunedLaserTarget.maxHp - 12 || !laserOffsetsDiffer, "Smoke test failed: Trigger Tuning did not split Laser Gun beams.");

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
        failIf(countBulletKind(homingPanel, BulletKind.HOMING_SHOT) != GamePanel.HOMING_SHOTGUN_BASE_PELLETS, "Smoke test failed: Homing Shotgun did not fire the level-1 pellet fan.");
        failIf(hasBulletKind(homingPanel, BulletKind.BASIC), "Smoke test failed: Homing Shotgun should replace the basic shot.");
        failIf(!hasBulletKind(homingPanel, BulletKind.HOMING_SHOT, true), "Smoke test failed: Homing Shotgun pellets need particle trails.");
        Bullet topFanPellet = firstBulletOfKind(homingPanel, BulletKind.HOMING_SHOT);
        double initialVy = topFanPellet.vy;
        for (int i = 0; i < GamePanel.logicTicks(18); i++) {
            homingPanel.step();
        }
        failIf(initialVy >= 0.0 || topFanPellet.vy <= 0.0 || homingPanel.bulletTrailParticles.size() == 0, "Smoke test failed: Homing Shotgun pellets did not arm, steer, and trail.");
        GamePanel homingLevelPanel = new GamePanel(false);
        homingLevelPanel.highTalents[0] = UpgradeEffect.HOMING_SHOTGUN;
        homingLevelPanel.setWeaponLevel(UpgradeEffect.HOMING_SHOTGUN, 2);
        homingLevelPanel.fireHomingShotgun(0, 10);
        failIf(countBulletKind(homingLevelPanel, BulletKind.HOMING_SHOT) != GamePanel.HOMING_SHOTGUN_BASE_PELLETS, "Smoke test failed: level-2 Homing Shotgun should start at 5 pellets.");
        homingLevelPanel.bullets.clear();
        homingLevelPanel.combo = 6;
        homingLevelPanel.fireHomingShotgun(0, 10);
        failIf(countBulletKind(homingLevelPanel, BulletKind.HOMING_SHOT) != GamePanel.HOMING_SHOTGUN_UPGRADED_PELLETS, "Smoke test failed: level-2 Homing Shotgun combo did not scale to 10 pellets.");
        homingLevelPanel.bullets.clear();
        homingLevelPanel.combo = 20;
        homingLevelPanel.fireRateBonusPercent = GamePanel.MAX_TRIGGER_TUNING_BONUS_PERCENT;
        homingLevelPanel.fireHomingShotgun(0, 10);
        failIf(countBulletKind(homingLevelPanel, BulletKind.HOMING_SHOT) != GamePanel.HOMING_SHOTGUN_UPGRADED_PELLETS
                + homingLevelPanel.triggerTuningLevel() * 2
                || !"+15".equals(homingLevelPanel.shotgunPelletHudValue)
                || homingLevelPanel.shotgunPelletHudTransitionTicks <= 0, "Smoke test failed: Trigger Tuning did not let Homing Shotgun break the 10-shot cap.");
        failIf(GamePanel.HOMING_SHOTGUN_FAN_RADIANS <= Math.toRadians(60.0), "Smoke test failed: Homing Shotgun fan angle was not widened.");
        failIf(!homingLevelPanel.bullets.get(0).accentColor.equals(GamePanel.COLOR_HOMING_HEAD)
                || !homingLevelPanel.bullets.get(5).accentColor.equals(GamePanel.COLOR_HOMING_ORANGE)
                || !homingLevelPanel.bullets.get(6).accentColor.equals(GamePanel.COLOR_HOMING_YELLOW)
                || !homingLevelPanel.bullets.get(7).accentColor.equals(GamePanel.COLOR_HOMING_GREEN)
                || !homingLevelPanel.bullets.get(8).accentColor.equals(GamePanel.COLOR_HOMING_BLUE)
                || !homingLevelPanel.bullets.get(9).accentColor.equals(GamePanel.COLOR_HOMING_PURPLE), "Smoke test failed: Homing Shotgun pellet colors did not follow the red-to-rainbow pattern.");
        int homingTrailBefore = homingLevelPanel.bulletTrailParticles.size();
        homingLevelPanel.spawnBulletTrailParticles(homingLevelPanel.bullets.get(5));
        failIf(homingLevelPanel.bulletTrailParticles.size() <= homingTrailBefore
                || !homingLevelPanel.bulletTrailParticles.get(homingTrailBefore).color.equals(GamePanel.COLOR_HOMING_ORANGE)
                || homingLevelPanel.trailLifeFor(BulletKind.HOMING_SHOT) < 18
                || homingLevelPanel.trailSizeFor(BulletKind.HOMING_SHOT) < 4.0, "Smoke test failed: Homing Shotgun trail particles are not longer and color-matched.");
        GamePanel homingImpactPanel = new GamePanel(false);
        Target homingImpactTarget = Target.enemy(0, TargetKind.NORMAL);
        homingImpactTarget.hp = 30;
        homingImpactTarget.maxHp = 30;
        Bullet homingImpactBullet = new Bullet(homingImpactTarget.x, 0, 5, 1, BulletKind.HOMING_SHOT);
        homingImpactBullet.accentColor = GamePanel.COLOR_HOMING_PURPLE;
        int impactParticlesBefore = homingImpactPanel.bulletTrailParticles.size();
        int impactsBefore = homingImpactPanel.impacts.size();
        homingImpactPanel.applyBulletHit(homingImpactBullet, homingImpactTarget);
        failIf(homingImpactPanel.bulletTrailParticles.size() <= impactParticlesBefore
                || homingImpactPanel.impacts.size() <= impactsBefore + 1
                || !homingImpactPanel.impacts.get(impactsBefore).damageColor.equals(GamePanel.COLOR_HOMING_PURPLE), "Smoke test failed: Homing Shotgun impact burst was not enlarged and color-matched.");
        GamePanel homingCrowdPanel = new GamePanel(false);
        homingCrowdPanel.highTalents[0] = UpgradeEffect.HOMING_SHOTGUN;
        homingCrowdPanel.setWeaponLevel(UpgradeEffect.HOMING_SHOTGUN, GamePanel.MAX_WEAPON_LEVEL);
        homingCrowdPanel.combo = 6;
        for (int i = 0; i < 5; i++) {
            homingCrowdPanel.targets.add(Target.enemy(i % 2, TargetKind.NORMAL));
        }
        homingCrowdPanel.fireHomingShotgun(0, 10);
        int crowdDamage = 0;
        for (Bullet bullet : homingCrowdPanel.bullets) {
            crowdDamage += bullet.damage;
        }
        failIf(crowdDamage <= 15, "Smoke test failed: level-3 Homing Shotgun did not scale with enemy count.");

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
        failIf(!hasBulletKind(dryIcePanel, BulletKind.DRY_ICE, true), "Smoke test failed: Dry-Ice Bullet did not fire the icy particle-trail round.");
        failIf(hasBulletKind(dryIcePanel, BulletKind.BASIC), "Smoke test failed: Dry-Ice Bullet should replace the basic shot.");
        Bullet rotatingDryIce = firstBulletOfKind(dryIcePanel, BulletKind.DRY_ICE);
        double dryIceRotationBefore = rotatingDryIce.shapeRotation;
        dryIcePanel.step();
        failIf(rotatingDryIce.shapeRotation == dryIceRotationBefore || rotatingDryIce.shapeSpin == 0.0, "Smoke test failed: Dry-Ice Bullet did not rotate in flight.");
        for (int i = 0; i < GamePanel.logicTicks(8); i++) {
            dryIcePanel.step();
        }
        failIf(drySide.hp >= drySide.maxHp || drySide.slowTicks <= 0
                || Math.abs(drySide.slowMultiplier - GamePanel.DRY_ICE_SLOW_MULTIPLIER) > 0.001
                || dryIcePanel.icePulses.size() == 0, "Smoke test failed: Dry-Ice Bullet did not splash, slow, and pulse.");
        Target dryWideSide = Target.enemy(0, TargetKind.NORMAL);
        dryWideSide.x = dryCenter.x + GamePanel.worldAmount(118);
        dryWideSide.hp = 20;
        dryWideSide.maxHp = 20;
        dryIcePanel.targets.add(dryWideSide);
        dryIcePanel.applyDryIceHit(new Bullet(dryCenter.x, 0, 4, 1, BulletKind.DRY_ICE), dryCenter, 4, 4);
        failIf(dryWideSide.hp >= dryWideSide.maxHp || dryWideSide.slowTicks <= 0, "Smoke test failed: Dry-Ice Bullet splash radius was not expanded.");
        dryCenter.dryIceHitStreak = 2;
        dryIcePanel.applyDryIceHit(new Bullet(dryCenter.x, 0, 2, 1, BulletKind.DRY_ICE), dryCenter, 2, 2);
        failIf(dryCenter.freezeTicks <= 0, "Smoke test failed: Dry-Ice Bullet did not freeze on the third same-target hit.");
        dryIcePanel.tick = 0;
        Color frozenA = dryIcePanel.colorFor(dryCenter);
        dryIcePanel.tick = GamePanel.logicTicks(5);
        Color frozenB = dryIcePanel.colorFor(dryCenter);
        float[] frozenHsb = Color.RGBtoHSB(frozenA.getRed(), frozenA.getGreen(), frozenA.getBlue(), null);
        failIf(frozenHsb[1] >= 0.25f || frozenHsb[2] <= 0.85f || frozenA.equals(frozenB), "Smoke test failed: frozen Dry-Ice target color is not bright, low-saturation, and flashing.");

        GamePanel frozenDamagePanel = new GamePanel(false);
        frozenDamagePanel.highTalents[0] = UpgradeEffect.DRY_ICE_BULLET;
        frozenDamagePanel.setWeaponLevel(UpgradeEffect.DRY_ICE_BULLET, 2);
        Target frozenDamageTarget = Target.enemy(0, TargetKind.TANK);
        frozenDamageTarget.hp = 40;
        frozenDamageTarget.maxHp = 40;
        frozenDamageTarget.freezeTicks = GamePanel.logicTicks(3);
        int frozenHpBefore = frozenDamageTarget.hp;
        frozenDamagePanel.applyBulletHit(new Bullet(frozenDamageTarget.x, 0, 4, 1, BulletKind.DRY_ICE),
                frozenDamageTarget);
        failIf(frozenDamageTarget.hp != frozenHpBefore - 8, "Smoke test failed: level-2 Dry-Ice Bullet did not double frozen-target damage.");

        GamePanel thawPulsePanel = new GamePanel(false);
        thawPulsePanel.started = true;
        thawPulsePanel.spawnCooldown = GamePanel.logicTicks(999);
        thawPulsePanel.highTalents[0] = UpgradeEffect.DRY_ICE_BULLET;
        thawPulsePanel.setWeaponLevel(UpgradeEffect.DRY_ICE_BULLET, 2);
        Target thawCenter = Target.enemy(0, TargetKind.TANK);
        Target thawSide = Target.enemy(0, TargetKind.NORMAL);
        thawCenter.x = GamePanel.PLAYER_X + GamePanel.worldAmount(220);
        thawSide.x = thawCenter.x + GamePanel.worldAmount(54);
        thawCenter.freezeTicks = 1;
        thawCenter.dryIceThawPulseDamage = 6;
        thawSide.hp = 30;
        thawSide.maxHp = 30;
        thawPulsePanel.targets.add(thawCenter);
        thawPulsePanel.targets.add(thawSide);
        int thawSideHpBefore = thawSide.hp;
        thawPulsePanel.step();
        failIf(thawCenter.freezeTicks != 0 || thawSide.hp != thawSideHpBefore - 6
                || thawSide.slowTicks <= 0
                || Math.abs(thawSide.slowMultiplier - GamePanel.DRY_ICE_SLOW_MULTIPLIER) > 0.001
                || thawSide.dryIceHitStreak != 0 || thawPulsePanel.icePulses.size() == 0, "Smoke test failed: level-2 Dry-Ice thaw pulse did not damage and slow without frost progress.");

        GamePanel thawStackPanel = new GamePanel(false);
        thawStackPanel.started = true;
        thawStackPanel.spawnCooldown = GamePanel.logicTicks(999);
        thawStackPanel.highTalents[0] = UpgradeEffect.DRY_ICE_BULLET;
        thawStackPanel.setWeaponLevel(UpgradeEffect.DRY_ICE_BULLET, GamePanel.MAX_WEAPON_LEVEL);
        Target thawStackCenter = Target.enemy(0, TargetKind.TANK);
        Target thawStackSide = Target.enemy(0, TargetKind.NORMAL);
        thawStackCenter.x = GamePanel.PLAYER_X + GamePanel.worldAmount(220);
        thawStackSide.x = thawStackCenter.x + GamePanel.worldAmount(54);
        thawStackCenter.freezeTicks = 1;
        thawStackCenter.dryIceThawPulseDamage = 6;
        thawStackPanel.targets.add(thawStackCenter);
        thawStackPanel.targets.add(thawStackSide);
        thawStackPanel.step();
        failIf(thawStackSide.dryIceHitStreak <= 0 || thawStackPanel.icePulses.size() == 0, "Smoke test failed: level-3 Dry-Ice thaw pulse did not spread frost progress.");

        panel.tick = GamePanel.logicTicks(430 * 6);
        panel.kills = 54;
        panel.bossLevel = 1;
        Target scaled = Target.enemy(0, TargetKind.TANK);
        int baseHp = scaled.hp;
        double baseSpeed = scaled.speed;
        panel.scaleTarget(scaled);
        failIf(scaled.hp <= baseHp || scaled.speed <= baseSpeed || panel.pressureLevel() <= 0, "Smoke test failed: pressure scaling did not strengthen enemies.");
        Target earlyNormal = Target.enemy(0, TargetKind.NORMAL);
        Target lateNormal = Target.enemy(0, TargetKind.NORMAL);
        panel.tick = 0;
        panel.kills = 0;
        panel.bossLevel = 0;
        panel.scaleTarget(earlyNormal);
        panel.tick = GamePanel.logicTicks(430 * 8);
        panel.scaleTarget(lateNormal);
        failIf(lateNormal.hp - earlyNormal.hp < 6, "Smoke test failed: enemy HP did not ramp fast enough over time.");

        panel.choiceMode = ChoiceMode.UPGRADE;
        int choiceTick = panel.tick;
        panel.step();
        failIf(panel.tick != choiceTick, "Smoke test failed: upgrade choice did not freeze pressure time.");

        panel.damageBonusPercent = 0;
        int unscaledDamage = panel.scaledDamage(10, null);
        panel.applyUpgradeCard(new UpgradeCard("Test Damage", "test", UpgradeRarity.COMMON,
                UpgradeEffect.CALIBRATED_DAMAGE));
        failIf(panel.scaledDamage(10, null) <= unscaledDamage, "Smoke test failed: percentage damage upgrade did not scale attacks.");
        failIf(panel.scaledDamage(5, null) <= 5, "Smoke test failed: low base damage did not receive a visible damage upgrade.");
        panel.damageBonusPercent = GamePanel.MAX_DAMAGE_BONUS_PERCENT;
        failIf(panel.canOfferEffect(UpgradeEffect.CALIBRATED_DAMAGE, UpgradeRarity.COMMON), "Smoke test failed: capped damage upgrade was still offerable.");
        panel.fireRateBonusPercent = GamePanel.MAX_TRIGGER_TUNING_BONUS_PERCENT;
        failIf(panel.canOfferEffect(UpgradeEffect.TRIGGER_TUNING, UpgradeRarity.COMMON), "Smoke test failed: capped trigger tuning was still offerable.");
        panel.maxHpUpgradeBonus = GamePanel.MAX_HP_UPGRADE_BONUS;
        panel.hp = panel.maxHp;
        failIf(panel.canOfferEffect(UpgradeEffect.FIELD_PATCH, UpgradeRarity.COMMON)
                || panel.canOfferEffect(UpgradeEffect.REINFORCED_CORE, UpgradeRarity.COMMON), "Smoke test failed: capped HP upgrades were still offerable.");

        GamePanel volleyPanel = new GamePanel(false);
        volleyPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT;
        volleyPanel.fireSingleShotVolley(0, 10, 1, BulletKind.BASIC);
        int volleyDamage = 0;
        for (Bullet bullet : volleyPanel.bullets) {
            volleyDamage += bullet.damage;
            failIf(bullet.damage >= 10, "Smoke test failed: trigger tuning pellet damage was not reduced.");
        }
        failIf(volleyPanel.bullets.size() != 2 || volleyDamage != 12, "Smoke test failed: trigger tuning did not split +20% total damage.");
        volleyPanel.fireRateBonusPercent = GamePanel.TRIGGER_TUNING_STEP_PERCENT * 2;
        failIf(volleyPanel.singleShotPelletCount() != 3, "Smoke test failed: trigger tuning did not add one pellet per level.");
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
        failIf(sellPanel.choiceMode != ChoiceMode.SELL_CONFIRM
                || sellPanel.fireRateBonusPercent != GamePanel.TRIGGER_TUNING_STEP_PERCENT
                || sellPanel.xp != 0, "Smoke test failed: build-manager sell did not open confirmation.");
        sellPanel.keyPressed(key(sellPanel, KeyEvent.VK_ENTER));
        failIf(sellPanel.fireRateBonusPercent != 0 || sellPanel.xp != 4 || sellPanel.choiceMode != ChoiceMode.UPGRADE, "Smoke test failed: selling an upgrade did not refund one-third level XP.");
        sellPanel.selectedOverviewCardIndex = inventoryIndexFor(sellPanel, UpgradeEffect.PHASE_SWITCH);
        sellPanel.overviewSelectionActive = true;
        sellPanel.keyPressed(key(sellPanel, KeyEvent.VK_ENTER));
        failIf(sellPanel.choiceMode != ChoiceMode.SELL_CONFIRM
                || sellPanel.phaseSwitchLevel != 1, "Smoke test failed: second build-manager sell did not wait for confirmation.");
        sellPanel.keyPressed(key(sellPanel, KeyEvent.VK_ENTER));
        failIf(sellPanel.phaseSwitchLevel != 0 || sellPanel.blueUpgradeCount != 0 || sellPanel.xp != 8, "Smoke test failed: selling a blue upgrade did not free the blue cap.");
        GamePanel goldSellPanel = new GamePanel(false);
        goldSellPanel.started = true;
        goldSellPanel.xpToNext = 999;
        goldSellPanel.goldTalents[0] = UpgradeEffect.DRONE_SWARM;
        goldSellPanel.openUpgradeOverview();
        goldSellPanel.selectedOverviewCardIndex = inventoryIndexFor(goldSellPanel, UpgradeEffect.DRONE_SWARM);
        goldSellPanel.overviewSelectionActive = true;
        goldSellPanel.keyPressed(key(goldSellPanel, KeyEvent.VK_ENTER));
        failIf(goldSellPanel.choiceMode != ChoiceMode.SELL_CONFIRM
                || goldSellPanel.goldTalents[0] != UpgradeEffect.DRONE_SWARM
                || goldSellPanel.xp != 0, "Smoke test failed: selling a gold upgrade did not require confirmation.");
        goldSellPanel.keyPressed(key(goldSellPanel, KeyEvent.VK_ENTER));
        failIf(goldSellPanel.goldTalents[0] != null || goldSellPanel.xp != 20, "Smoke test failed: selling a gold upgrade did not refund 20 XP.");
        GamePanel weaponSellPanel = new GamePanel(false);
        weaponSellPanel.started = true;
        weaponSellPanel.xpToNext = 999;
        weaponSellPanel.highTalents[0] = UpgradeEffect.BASIC_WEAPON;
        weaponSellPanel.setWeaponLevel(UpgradeEffect.BASIC_WEAPON, 3);
        weaponSellPanel.openUpgradeOverview();
        weaponSellPanel.selectedOverviewCardIndex = inventoryIndexFor(weaponSellPanel, UpgradeEffect.BASIC_WEAPON);
        weaponSellPanel.overviewSelectionActive = true;
        weaponSellPanel.keyPressed(key(weaponSellPanel, KeyEvent.VK_ENTER));
        failIf(weaponSellPanel.choiceMode != ChoiceMode.SELL_CONFIRM
                || weaponSellPanel.highTalents[0] != UpgradeEffect.BASIC_WEAPON
                || weaponSellPanel.xp != 0, "Smoke test failed: selling a red weapon did not require confirmation.");
        weaponSellPanel.keyPressed(key(weaponSellPanel, KeyEvent.VK_ENTER));
        failIf(weaponSellPanel.highTalents[0] != null
                || weaponSellPanel.weaponLevel(UpgradeEffect.BASIC_WEAPON) != 0
                || weaponSellPanel.xp != 90, "Smoke test failed: selling a red weapon did not refund 30 XP per level.");
        GamePanel goldCapPanel = new GamePanel(false);
        grantForSmoke(goldCapPanel, UpgradeEffect.DRONE_SWARM);
        grantForSmoke(goldCapPanel, UpgradeEffect.OVERFLOW_ROUND);
        grantForSmoke(goldCapPanel, UpgradeEffect.MAGNETIC_FIELD);
        failIf(goldCapPanel.goldTalentCount() != GamePanel.MAX_GOLD_TALENTS
                || goldCapPanel.canOfferEffect(UpgradeEffect.RED_EYE, UpgradeRarity.HIGH), "Smoke test failed: gold talent cap was not enforced.");
        GamePanel dronePanel = new GamePanel(false);
        grantForSmoke(dronePanel, UpgradeEffect.DRONE_SWARM);
        Target droneKill = Target.enemy(0, TargetKind.FAST);
        droneKill.x = GamePanel.PLAYER_X + GamePanel.worldAmount(220);
        Color expectedDroneColor = dronePanel.colorFor(droneKill);
        dronePanel.rewardFor(droneKill);
        failIf(dronePanel.goldDrones.size() != 1 || !dronePanel.goldDrones.get(0).color.equals(expectedDroneColor), "Smoke test failed: Drone did not spawn a color-matched drone on kill.");
        Target droneTarget = Target.enemy(0, TargetKind.TANK);
        droneTarget.x = dronePanel.goldDrones.get(0).x + GamePanel.worldAmount(120);
        droneTarget.hp = 30;
        droneTarget.maxHp = 30;
        dronePanel.targets.add(droneTarget);
        GoldDrone drone = dronePanel.goldDrones.get(0);
        drone.cooldownTicks = 0;
        int droneTargetHpBefore = droneTarget.hp;
        dronePanel.updateGoldDrones();
        failIf(droneTarget.hp >= droneTargetHpBefore || drone.laserTicks <= 0, "Smoke test failed: Drone did not fire its short laser.");
        GamePanel collisionKillPanel = new GamePanel(false);
        collisionKillPanel.started = true;
        collisionKillPanel.spawnCooldown = GamePanel.logicTicks(999);
        grantForSmoke(collisionKillPanel, UpgradeEffect.DRONE_SWARM);
        Target collisionKillTarget = Target.enemy(0, TargetKind.NORMAL);
        collisionKillTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(20);
        collisionKillTarget.hp = 4;
        collisionKillTarget.maxHp = 4;
        collisionKillPanel.targets.add(collisionKillTarget);
        collisionKillPanel.handleCollisions();
        collisionKillPanel.removeDeadAndEscaped();
        failIf(collisionKillPanel.kills != 1 || collisionKillPanel.goldDrones.size() != 1
                || collisionKillPanel.xpOrbs.size() == 0, "Smoke test failed: collision trade kills did not count as kills or spawn drones.");
        GamePanel overflowPanel = new GamePanel(false);
        grantForSmoke(overflowPanel, UpgradeEffect.OVERFLOW_ROUND);
        Target overflowTarget = Target.enemy(0, TargetKind.NORMAL);
        overflowTarget.hp = 3;
        overflowTarget.maxHp = 3;
        overflowTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(120);
        Bullet overflowSource = new Bullet(overflowTarget.x, 0, 10, 1, BulletKind.BASIC);
        overflowSource.previousX = overflowTarget.x - GamePanel.worldAmount(20);
        overflowPanel.applyBulletHit(overflowSource, overflowTarget);
        failIf(overflowPanel.overflowDamageBank != 7 || overflowPanel.overflowReadyPulseTicks != 0, "Smoke test failed: Overflow did not collect single-shot overkill.");
        overflowPanel.overflowDamageBank = GamePanel.OVERFLOW_MAX_DAMAGE;
        overflowPanel.completedWordShifted = false;
        overflowPanel.tryFireOverflowShot(0, null);
        failIf(hasBulletKind(overflowPanel, BulletKind.OVERFLOW), "Smoke test failed: Overflow fired without Shift-held word completion.");
        overflowPanel.completedWordShifted = true;
        overflowPanel.tryFireOverflowShot(0, null);
        Bullet overflowRound = firstBulletOfKind(overflowPanel, BulletKind.OVERFLOW);
        failIf(overflowRound.damage != GamePanel.OVERFLOW_SHOT_DAMAGE || overflowPanel.overflowDamageBank != 0
                || overflowPanel.bulletTrailParticles.size() == 0, "Smoke test failed: Overflow did not fire the fast 20-damage round with effects.");
        GamePanel magnetPanel = new GamePanel(false);
        magnetPanel.started = true;
        magnetPanel.lane = 0;
        grantForSmoke(magnetPanel, UpgradeEffect.MAGNETIC_FIELD);
        XpOrb farOtherLaneOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(900), 1, 1);
        magnetPanel.xpOrbs.add(farOtherLaneOrb);
        magnetPanel.step();
        failIf(!farOtherLaneOrb.attracted, "Smoke test failed: Magnet did not attract full-screen XP.");
        GamePanel totemPanel = new GamePanel(false);
        grantForSmoke(totemPanel, UpgradeEffect.UNDYING_TOTEM);
        totemPanel.hp = 1;
        totemPanel.endGame();
        failIf(totemPanel.gameOver || totemPanel.hp <= 1 || totemPanel.totemReviveAvailable, "Smoke test failed: Undying Totem did not revive immediately once.");
        totemPanel.openUpgradeOverview();
        totemPanel.selectedOverviewCardIndex = inventoryIndexFor(totemPanel, UpgradeEffect.UNDYING_TOTEM);
        totemPanel.overviewSelectionActive = true;
        totemPanel.keyPressed(key(totemPanel, KeyEvent.VK_ENTER));
        failIf(totemPanel.choiceMode == ChoiceMode.SELL_CONFIRM
                || !totemPanel.hasGoldTalent(UpgradeEffect.UNDYING_TOTEM)
                || totemPanel.xp != 0, "Smoke test failed: Undying Totem was sellable.");
        GamePanel adrenalinePanel = new GamePanel(false);
        grantForSmoke(adrenalinePanel, UpgradeEffect.ADRENALINE);
        failIf(adrenalinePanel.maxHp != (GamePanel.PLAYER_BASE_HP + 1) / 2
                || adrenalinePanel.hp != (GamePanel.PLAYER_BASE_HP + 1) / 2
                || adrenalinePanel.scaledDamage(10, null) != 15
                || Math.abs(adrenalinePanel.goldEnemySpeedMultiplier() - 1.2) > 0.001, "Smoke test failed: Adrenaline did not match damage, HP, or speed design.");
        adrenalinePanel.removeUpgradeCard(new UpgradeInventoryCard(UpgradeEffect.ADRENALINE, UpgradeRarity.HIGH, 1, 0));
        failIf(adrenalinePanel.maxHp != GamePanel.PLAYER_BASE_HP || adrenalinePanel.hasGoldTalent(UpgradeEffect.ADRENALINE), "Smoke test failed: Adrenaline side effects did not cleanly remove.");
        GamePanel meleePanel = new GamePanel(false);
        grantForSmoke(meleePanel, UpgradeEffect.MELEE);
        failIf(meleePanel.scaledDamage(10, null) != 5, "Smoke test failed: Melee did not halve weapon damage.");
        Target meleeCollisionTarget = Target.enemy(0, TargetKind.NORMAL);
        meleeCollisionTarget.hp = 9;
        failIf(meleePanel.collisionCost(meleeCollisionTarget) != 5, "Smoke test failed: Melee did not halve collision HP loss.");
        int meleeMaxBefore = meleePanel.maxHp;
        meleePanel.applyMeleeCollisionGrowth(5);
        failIf(meleePanel.maxHp <= meleeMaxBefore, "Smoke test failed: Melee collision did not increase max HP.");
        meleePanel.hp = meleePanel.maxHp - 1;
        meleePanel.tick = GamePanel.MELEE_REGEN_INTERVAL_TICKS;
        meleePanel.updateMeleeRegeneration();
        failIf(meleePanel.hp != meleePanel.maxHp, "Smoke test failed: Melee did not regenerate HP slowly.");
        Target meleeBoss = Target.boss(0, 1);
        meleeBoss.hp = 100;
        meleeBoss.maxHp = 100;
        meleeBoss.x = GamePanel.PLAYER_X + GamePanel.worldAmount(360);
        double meleeBossStartX = meleeBoss.x;
        meleePanel.startMeleeRam(meleeBoss);
        failIf(meleeBoss.hp != 100 - Math.max(1, (int) Math.round(meleePanel.maxHp * 0.20))
                || meleePanel.meleeRamTicks <= 0
                || meleePanel.screenShakeTicks < GamePanel.MELEE_BOSS_SCREEN_SHAKE_TICKS
                || meleeBoss.bossKnockbackTicks <= 0
                || Math.abs(meleeBoss.bossKnockbackTargetX - GamePanel.targetSpawnX(TargetKind.BOSS)) > 0.01
                || meleePanel.meleeRamTargetX <= GamePanel.PLAYER_X
                || meleePanel.meleeRamTargetX >= meleeBoss.x, "Smoke test failed: Melee boss ram did not deal max-HP damage with animation state.");
        failIf(meleePanel.playerRenderX() <= GamePanel.PLAYER_X, "Smoke test failed: Melee boss ram did not move the player body.");
        while (meleeBoss.bossKnockbackTicks > 0) {
            meleePanel.updateBossKnockback(meleeBoss);
        }
        failIf(meleeBoss.x <= meleeBossStartX
                || Math.abs(meleeBoss.x - GamePanel.targetSpawnX(TargetKind.BOSS)) > 0.01, "Smoke test failed: Melee boss ram did not knock the boss to the right edge.");
        GamePanel redEyePanel = new GamePanel(false);
        grantForSmoke(redEyePanel, UpgradeEffect.RED_EYE);
        redEyePanel.maxHp = 100;
        redEyePanel.hp = 100;
        int fullHpDamage = redEyePanel.scaledDamage(10, null);
        redEyePanel.hp = 50;
        int halfHpDamage = redEyePanel.scaledDamage(10, null);
        redEyePanel.hp = 0;
        int emptyHpDamage = redEyePanel.scaledDamage(10, null);
        failIf(fullHpDamage != 10 || halfHpDamage != 15 || emptyHpDamage != 20, "Smoke test failed: Red Eye did not scale up to +100% at low HP.");
        GamePanel blueCapPanel = new GamePanel(false);
        blueCapPanel.applyUpgradeCard(blueCapPanel.createCard(UpgradeEffect.PHASE_SWITCH, UpgradeRarity.UNCOMMON),
                true, true);
        blueCapPanel.applyUpgradeCard(blueCapPanel.createCard(UpgradeEffect.BOSS_BREAKER, UpgradeRarity.UNCOMMON),
                true, true);
        blueCapPanel.applyUpgradeCard(blueCapPanel.createCard(UpgradeEffect.CROSSFEED, UpgradeRarity.UNCOMMON),
                true, true);
        failIf(blueCapPanel.blueUpgradeCount != GamePanel.MAX_BLUE_UPGRADES
                || blueCapPanel.canOfferEffect(UpgradeEffect.PRESSURE_VALVE, UpgradeRarity.UNCOMMON), "Smoke test failed: blue upgrade cap was not enforced.");
        blueCapPanel.buildUpgradeChoices();
        for (UpgradeCard card : blueCapPanel.upgradeChoices) {
            failIf(card.rarity == UpgradeRarity.UNCOMMON || card.rarity == UpgradeRarity.RED, "Smoke test failed: capped blue upgrades still appeared in choices.");
        }

        panel.applyUpgradeCard(new UpgradeCard("Rhythm Cannon", "test", UpgradeRarity.RED,
                UpgradeEffect.RHYTHM_CANNON));
        failIf(!panel.hasHighTalent(UpgradeEffect.RHYTHM_CANNON), "Smoke test failed: red weapon was not recorded.");

        if (GamePanel.ENABLE_GROUP_ONE_UPGRADES) {
            GamePanel longRewardPanel = new GamePanel(false);
            longRewardPanel.hp = longRewardPanel.maxHp - 3;
            grantForSmoke(longRewardPanel, UpgradeEffect.LONG_WORD_REWARD);
            longRewardPanel.laneWords[0] = "random";
            longRewardPanel.laneWords[1] = "code";
            typeWord(longRewardPanel, "random");
            failIf(longRewardPanel.hp <= longRewardPanel.maxHp - 3, "Smoke test failed: Longword Reward did not repair HP.");

            GamePanel shortShotPanel = new GamePanel(false);
            grantForSmoke(shortShotPanel, UpgradeEffect.SHORT_WORD_QUICKSHOT);
            shortShotPanel.laneWords[0] = "code";
            shortShotPanel.laneWords[1] = "loop";
            typeWord(shortShotPanel, "code");
            failIf(!hasBulletKind(shortShotPanel, BulletKind.CONTINUOUS) || shortShotPanel.bullets.size() < 2, "Smoke test failed: Shortword Quickshot did not add a fast shot.");

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
            failIf(!hasBulletKind(burstPanel, BulletKind.BURST), "Smoke test failed: Final Letter Burst did not spawn a burst bullet.");
            for (int i = 0; i < GamePanel.logicTicks(9); i++) {
                burstPanel.step();
            }
            failIf(burstSide.hp >= burstSide.maxHp, "Smoke test failed: Final Letter Burst did not splash nearby targets.");

            GamePanel vowelPanel = new GamePanel(false);
            grantForSmoke(vowelPanel, UpgradeEffect.VOWEL_CONVERGENCE);
            XpOrb vowelOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(260), 0, 1);
            vowelPanel.xpOrbs.add(vowelOrb);
            vowelPanel.laneWords[0] = "code";
            vowelPanel.laneWords[1] = "loop";
            double vowelOrbStart = vowelOrb.x;
            typeWord(vowelPanel, "code");
            failIf(!vowelOrb.attracted || vowelOrb.x >= vowelOrbStart, "Smoke test failed: Vowel Convergence did not pull XP.");

            GamePanel hardPanel = new GamePanel(false);
            grantForSmoke(hardPanel, UpgradeEffect.HARD_CONSONANT_BREAK);
            Target hardBreakTarget = Target.enemy(0, TargetKind.TANK);
            hardBreakTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(160);
            hardPanel.targets.add(hardBreakTarget);
            hardPanel.laneWords[0] = "target";
            hardPanel.laneWords[1] = "code";
            typeWord(hardPanel, "target");
            failIf(hardPanel.bullets.size() == 0 || hardPanel.bullets.get(0).damage <= hardPanel.baseDamage, "Smoke test failed: Hard Consonant Break did not improve thick-target damage.");
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
                    UpgradeEffect.PREFIX_ILLUMINATION,
                    UpgradeEffect.FIRST_LETTER_TICKET,
                    UpgradeEffect.DUAL_PREFIX_SCAN,
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
                failIf(!containsEffect(sudoEffects, effect), "Smoke test failed: group 2-5 upgrade is missing from sudo.");
            }

            GamePanel switchPanel = new GamePanel(false);
            switchPanel.laneWords[0] = "code";
            switchPanel.laneWords[1] = "loop";
            typeWord(switchPanel, "loop");
            double switchY = switchPanel.playerRenderY();
            failIf(switchPanel.lane != 1 || switchPanel.laneSwitchAnimationTicks <= 0
                    || switchY <= GamePanel.LANE_Y[0] || switchY >= GamePanel.LANE_Y[1], "Smoke test failed: lane switch animation did not start smoothly.");
            failIf(!switchPanel.pendingLaneAttack || switchPanel.bullets.size() != 0, "Smoke test failed: lane switch attack did not wait for the animation.");
            for (int i = 0; i < GamePanel.LANE_SWITCH_ANIMATION_TICKS; i++) {
                switchPanel.step();
            }
            failIf(switchPanel.pendingLaneAttack || switchPanel.bullets.size() == 0, "Smoke test failed: lane switch attack did not fire after the animation.");

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
            failIf(sameLanePanel.sameLaneStreak < 2 || sameLaneTarget.slowTicks <= 0, "Smoke test failed: Same-Lane Suppression did not slow the target.");

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
            failIf(!swapPanel.completedWordAlternated || swapPanel.bullets.size() < bulletsBeforeSwap + 3, "Smoke test failed: Lane Swap Beat did not fire split shots.");

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
            failIf(firstLetterPanel.sameFirstLetterStreak < 3 || firstLetterPanel.xpOrbs.size() <= xpBeforeTicket, "Smoke test failed: First-Letter Ticket did not create bonus XP.");

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
            failIf(!backspacePanel.completedWordUsedBackspace || !hasBulletKind(backspacePanel, BulletKind.BURST), "Smoke test failed: Backspace Counter did not fire.");

            GamePanel comboCalibratorPanel = new GamePanel(false);
            grantForSmoke(comboCalibratorPanel, UpgradeEffect.COMBO_CALIBRATOR);
            comboCalibratorPanel.baseDamage = 5;
            comboCalibratorPanel.combo = 2;
            comboCalibratorPanel.completeLaneWord(0, "code");
            failIf(comboCalibratorPanel.bullets.size() != 1 || comboCalibratorPanel.bullets.get(0).damage <= 5, "Smoke test failed: Combo Calibrator did not add damage at combo 3.");
            comboCalibratorPanel.bullets.clear();
            comboCalibratorPanel.combo = 9;
            comboCalibratorPanel.completeLaneWord(0, "code");
            failIf(comboCalibratorPanel.bullets.size() != 1 || comboCalibratorPanel.bullets.get(0).damage <= 8, "Smoke test failed: Combo Calibrator did not add stronger damage at combo 10.");
            comboCalibratorPanel.bullets.clear();
            comboCalibratorPanel.combo = 10;
            comboCalibratorPanel.completeLaneWord(0, "code");
            failIf(comboCalibratorPanel.bullets.size() != 1 || comboCalibratorPanel.bullets.get(0).damage != 6, "Smoke test failed: Combo Calibrator added damage outside combo 3/5/10.");

            GamePanel crossfeedPanel = new GamePanel(false);
            grantForSmoke(crossfeedPanel, UpgradeEffect.CROSSFEED);
            crossfeedPanel.xpToNext = 999;
            crossfeedPanel.laneWords[0] = "flow";
            crossfeedPanel.laneWords[1] = "loop";
            typeWord(crossfeedPanel, "flow");
            failIf(crossfeedPanel.crossfeedBonusTicks <= 0, "Smoke test failed: Crossfeed did not prime after an f/t/k word.");
            crossfeedPanel.addExperience(5);
            failIf(crossfeedPanel.xp <= 5 || crossfeedPanel.crossfeedBonusTicks != 0, "Smoke test failed: Crossfeed did not boost exactly one XP pickup.");

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
            failIf(expiredCrossfeedPanel.xp != 5, "Smoke test failed: Crossfeed boost did not expire.");

            GamePanel xpPullPanel = new GamePanel(false);
            grantForSmoke(xpPullPanel, UpgradeEffect.PRECISE_PICKUP);
            XpOrb pullOrb = new XpOrb(GamePanel.PLAYER_X + GamePanel.worldAmount(260), 0, 1);
            xpPullPanel.xpOrbs.add(pullOrb);
            xpPullPanel.laneWords[0] = "code";
            xpPullPanel.laneWords[1] = "loop";
            double pullStart = pullOrb.x;
            typeWord(xpPullPanel, "code");
            failIf(!pullOrb.attracted || pullOrb.x >= pullStart, "Smoke test failed: Precise Pickup did not pull XP.");

            GamePanel dangerPanel = new GamePanel(false);
            grantForSmoke(dangerPanel, UpgradeEffect.DANGER_WORD);
            Target dangerTarget = Target.enemy(0, TargetKind.NORMAL);
            dangerTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(230);
            dangerPanel.targets.add(dangerTarget);
            dangerPanel.laneWords[0] = "code";
            dangerPanel.laneWords[1] = "loop";
            double dangerStart = dangerTarget.x;
            typeWord(dangerPanel, "code");
            failIf(dangerTarget.x <= dangerStart, "Smoke test failed: Danger Word did not knock back the target.");

            GamePanel valvePanel = new GamePanel(false);
            grantForSmoke(valvePanel, UpgradeEffect.PRESSURE_VALVE);
            Target valveTarget = Target.enemy(0, TargetKind.NORMAL);
            valveTarget.x = GamePanel.PLAYER_X + GamePanel.worldAmount(250);
            valvePanel.targets.add(valveTarget);
            valvePanel.laneWords[0] = "code";
            valvePanel.laneWords[1] = "loop";
            typeWord(valvePanel, "code");
            failIf(valvePanel.laneSlowTicks[0] <= 0, "Smoke test failed: Pressure Valve did not slow the lane.");
        }

        System.out.println("Smoke test passed: typing, XP, choices, red weapons, gold talents, and update loop are alive.");
    }

    static void failIf(boolean condition, String message) {
        if (condition) {
            throw new IllegalStateException(message);
        }
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
            failIf(word.length() < GamePanel.LANE_WORD_MIN_LENGTH || word.length() > GamePanel.LANE_WORD_MAX_LENGTH, "Smoke test failed: word length is outside lane constraints: " + word);
            for (int j = i + 1; j < GamePanel.WORDS.length; j++) {
                failIf(word.equals(GamePanel.WORDS[j]), "Smoke test failed: duplicate word in bank: " + word);
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
