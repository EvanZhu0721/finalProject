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


@SuppressWarnings("serial")
final class GamePanel extends JPanel implements ActionListener, KeyListener {
    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;
    static final int PLAYER_X = 95;
    static final int LANE_LEFT_X = 55;
    static final int LANE_RIGHT_X = WIDTH - 55;
    static final int[] LANE_Y = {230, 410};
    static final int TICK_MS = 33;
    static final int FRAME_MS = 16;
    static final boolean ENABLE_GROUP_ONE_UPGRADES = true;
    static final boolean ENABLE_GROUP_TWO_TO_FIVE_UPGRADES = true;
    static final int LANE_WORD_MIN_LENGTH = 4;
    static final int LANE_WORD_MAX_LENGTH = 7;
    static final int BASE_SPAWN_COOLDOWN = 100;
    static final int SPAWN_COOLDOWN_JITTER = 15;
    static final int[] EARLY_XP_REQUIREMENTS = {6, 8, 12, 16, 22};
    static final int INITIAL_XP_TO_NEXT = EARLY_XP_REQUIREMENTS[0];
    static final int BOSS_XP_BASE = 24;
    static final int BOSS_XP_PER_LEVEL = 8;
    static final int PLAYER_BASE_HP = 36;
    static final int FIELD_PATCH_HEAL = 8;
    static final int MAX_HP_CARD_BONUS = 6;
    static final int MAX_DAMAGE_BONUS_PERCENT = 72;
    static final int TRIGGER_TUNING_STEP_PERCENT = 20;
    static final int MAX_TRIGGER_TUNING_BONUS_PERCENT = 100;
    static final int MAX_HP_UPGRADE_BONUS = 36;
    static final int AUTO_CANNON_SURGE_TICKS = 150;
    static final int PIERCING_DECAY_PERCENT = 20;
    static final int PHASE_SWITCH_TICKS = 11;
    static final int PHASE_SWITCH_BASE_COOLDOWN = 240;
    static final int PHASE_SWITCH_COOLDOWN_STEP = 38;
    static final int PHASE_SWITCH_MIN_COOLDOWN = 105;
    static final int XP_ATTRACT_RADIUS = 288;
    static final int XP_COLLECT_RADIUS = 34;
    static final int TEST_BACKEND_PAGE_SIZE = 11;
    static final int TEST_BIG_XP_AMOUNT = 500;
    static final int WORD_COMPLETE_PULSE_TICKS = 22;
    static final int LANE_SWITCH_ANIMATION_TICKS = 6;
    static final Font FONT_TARGET_TITLE = new Font("Consolas", Font.BOLD, 18);
    static final Font FONT_TARGET_HP = new Font("Consolas", Font.PLAIN, 13);
    static final Font FONT_ORB = new Font("Consolas", Font.BOLD, 12);
    static final Font FONT_IMPACT = new Font("Consolas", Font.BOLD, 13);
    static final Font FONT_HUD = new Font("Consolas", Font.PLAIN, 16);
    static final Font FONT_PRESSURE = new Font("Consolas", Font.BOLD, 14);
    static final Font FONT_LANE_LABEL = new Font("Consolas", Font.BOLD, 15);
    static final Font FONT_LANE_WORD = new Font("Consolas", Font.BOLD, 46);
    static final Font FONT_OVERLAY_TITLE = new Font("Consolas", Font.BOLD, 34);
    static final Font FONT_CHOICE_TITLE = new Font("Consolas", Font.BOLD, 30);
    static final Font FONT_OVERLAY_BODY = new Font("Consolas", Font.PLAIN, 20);
    static final Font FONT_START_BUTTON = new Font("Consolas", Font.BOLD, 22);
    static final Font FONT_HUD_ZH = new Font("Microsoft YaHei UI", Font.PLAIN, 16);
    static final Font FONT_PRESSURE_ZH = new Font("Microsoft YaHei UI", Font.BOLD, 14);
    static final Font FONT_LANE_LABEL_ZH = new Font("Microsoft YaHei UI", Font.BOLD, 15);
    static final Font FONT_OVERLAY_TITLE_ZH = new Font("Microsoft YaHei UI", Font.BOLD, 34);
    static final Font FONT_CHOICE_TITLE_ZH = new Font("Microsoft YaHei UI", Font.BOLD, 30);
    static final Font FONT_OVERLAY_BODY_ZH = new Font("Microsoft YaHei UI", Font.PLAIN, 20);
    static final Font FONT_START_BUTTON_ZH = new Font("Microsoft YaHei UI", Font.BOLD, 22);
    static final BasicStroke STROKE_LANE = new BasicStroke(4);
    static final BasicStroke STROKE_CARD_ACTIVE = new BasicStroke(3);
    static final BasicStroke STROKE_CARD_IDLE = new BasicStroke(2);
    static final BasicStroke STROKE_WORD_COMPLETE_AURA =
            new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_WORD_COMPLETE_CORE =
            new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_PIERCE_AURA = new BasicStroke(13, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_PIERCE_CORE = new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_SURGE_AURA = new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_SURGE_CORE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_CONTINUOUS = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_BASIC = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_IMPACT = new BasicStroke(3);
    static final Color COLOR_BACKGROUND = new Color(21, 24, 30);
    static final Color COLOR_PANEL_DARK = new Color(12, 15, 22, 232);
    static final Color COLOR_INK_DARK = new Color(12, 14, 18);
    static final Color COLOR_TARGET_NORMAL = new Color(94, 142, 220);
    static final Color COLOR_TARGET_FAST = new Color(229, 128, 78);
    static final Color COLOR_TARGET_TANK = new Color(180, 116, 210);
    static final Color COLOR_TARGET_SWITCHER = new Color(216, 78, 106);
    static final Color COLOR_TARGET_UPGRADE = new Color(80, 190, 130);
    static final Color COLOR_TARGET_BOSS = new Color(220, 70, 70);
    static final Color COLOR_PIERCE_AURA = new Color(70, 225, 255, 65);
    static final Color COLOR_PIERCE_CORE = new Color(115, 245, 255, 190);
    static final Color COLOR_PIERCE_HEAD = new Color(45, 215, 255);
    static final Color COLOR_SURGE_AURA = new Color(255, 236, 95, 135);
    static final Color COLOR_SURGE_CORE = new Color(130, 255, 180, 210);
    static final Color COLOR_SURGE_GLOW = new Color(255, 220, 72, 90);
    static final Color COLOR_SURGE_HEAD = new Color(255, 246, 120);
    static final Color COLOR_BURST_AURA = new Color(255, 120, 82, 105);
    static final Color COLOR_BURST_HEAD = new Color(255, 174, 92);
    static final Color COLOR_CONTINUOUS_TRAIL = new Color(100, 255, 170, 58);
    static final Color COLOR_CONTINUOUS_HEAD = new Color(75, 235, 130);
    static final Color COLOR_BASIC_TRAIL = new Color(120, 170, 255, 120);
    static final Color COLOR_BASIC_HEAD = new Color(130, 175, 255);
    static final Color COLOR_LANE_ACTIVE = new Color(85, 180, 230);
    static final Color COLOR_LANE_IDLE = new Color(70, 76, 88);
    static final Color COLOR_LANE_FILL = new Color(43, 48, 58);
    static final Color COLOR_LANE_BORDER_ACTIVE = new Color(93, 188, 238);
    static final Color COLOR_LANE_BORDER_IDLE = new Color(78, 84, 98);
    static final Color COLOR_CARD_ACTIVE = new Color(255, 214, 86);
    static final Color COLOR_CARD_IDLE = new Color(91, 105, 124);
    static final Color COLOR_LANE_TEXT_ACTIVE = new Color(255, 235, 125);
    static final Color COLOR_LANE_TEXT_IDLE = new Color(190, 202, 218);
    static final Color COLOR_TYPED_PREFIX = new Color(255, 235, 100);
    static final Color COLOR_WORD_COMPLETE = new Color(95, 245, 145);
    static final Color COLOR_TYPED_SUFFIX = new Color(225, 232, 242);
    static final Color COLOR_PLAYER = new Color(244, 205, 96);
    static final Color COLOR_PLAYER_TEXT = new Color(38, 38, 38);
    static final Color COLOR_HUD_TEXT = new Color(235, 238, 245);
    static final Color COLOR_PRESSURE_LOW = new Color(90, 210, 145);
    static final Color COLOR_PRESSURE_MID = new Color(245, 188, 75);
    static final Color COLOR_PRESSURE_HIGH = new Color(238, 86, 86);
    static final Color COLOR_PRESSURE_LOW_GLOW = new Color(90, 210, 145, 48);
    static final Color COLOR_PRESSURE_MID_GLOW = new Color(245, 188, 75, 48);
    static final Color COLOR_PRESSURE_HIGH_GLOW = new Color(238, 86, 86, 48);
    static final Color COLOR_PRESSURE_TEXT = new Color(205, 215, 230);
    static final Color COLOR_PRESSURE_TRACK = new Color(42, 48, 58);
    static final Color COLOR_CHOICE_OVERLAY = new Color(0, 0, 0, 185);
    static final Color COLOR_START_OVERLAY = new Color(0, 0, 0, 188);
    static final Color COLOR_PAUSE_OVERLAY = new Color(0, 0, 0, 172);
    static final Color COLOR_GAME_OVER_OVERLAY = new Color(0, 0, 0, 200);
    static final Color COLOR_GRID_MAJOR = new Color(62, 92, 116, 38);
    static final Color COLOR_GRID_MINOR = new Color(45, 58, 78, 28);
    static final Color COLOR_LANE_GLOW = new Color(100, 220, 255, 55);
    static final Color COLOR_CARD_GLOW = new Color(255, 220, 86, 45);
    static final Color COLOR_SCANLINE = new Color(120, 255, 210, 20);
    static final Color COLOR_PLAYER_GLOW = new Color(244, 205, 96, 65);
    static final Color COLOR_TARGET_GLOW = new Color(120, 190, 255, 45);
    static final Color COLOR_HP_BAR_BACK = new Color(11, 14, 20, 160);
    static final Color COLOR_HP_BAR_FILL = new Color(118, 255, 176);
    static final Color COLOR_IMPACT_RING = new Color(255, 235, 120);
    static final Color COLOR_IMPACT_CORE = new Color(255, 120, 85);
    static final Color COLOR_XP = new Color(110, 250, 190);
    static final Color COLOR_XP_GLOW = new Color(110, 250, 190, 70);
    static final Color COLOR_RARITY_COMMON = new Color(130, 235, 175);
    static final Color COLOR_RARITY_UNCOMMON = new Color(95, 205, 255);
    static final Color COLOR_RARITY_HIGH = new Color(255, 218, 86);
    static final Color COLOR_UPGRADE_READY = new Color(255, 218, 86);
    static final Color COLOR_UPGRADE_READY_GLOW = new Color(255, 218, 86, 82);
    static final String[] WORDS = {
            "code", "loop", "array", "class", "method", "object", "static", "return",
            "debug", "public", "private", "string", "random", "switch", "combo",
            "vector", "target", "health", "typing", "battle", "upgrade", "thread",
            "focus", "runner", "syntax", "compile", "java", "panel", "timer",
            "stack", "queue", "index", "value", "field", "token", "input",
            "output", "screen", "sprite", "bullet", "damage", "shield", "energy",
            "charge", "stream", "strike", "rocket", "signal", "system", "branch",
            "buffer", "cursor", "update", "render", "canvas", "engine", "player",
            "enemy", "motion", "target", "pickup", "attack", "defend", "survive",
            "pressure", "perfect", "control", "pattern", "through", "forward",
            "scanner", "boolean", "integer", "double", "extends", "compare",
            "contains", "iterator", "graphics", "keyboard", "collision", "velocity",
            "function", "variable", "constant", "instance", "priority", "override",
            "lambda", "module", "import", "export", "server", "client", "socket",
            "packet", "router", "cache", "query", "schema", "filter", "search",
            "sorter", "merge", "commit", "branch", "origin", "remote", "deploy",
            "script", "layout", "margin", "border", "button", "dialog", "slider",
            "toggle", "option", "select", "canvas", "bitmap", "shader", "matrix",
            "vertex", "camera", "sample", "packet", "parser", "format", "memory",
            "thread", "mutex", "atomic", "future", "worker", "signal", "socket",
            "buffer", "stream", "reader", "writer", "logger", "metric", "trace",
            "event", "action", "state", "store", "reduce", "effect", "render",
            "update", "layout", "widget", "window", "dialog", "cursor", "prompt",
            "vector", "scalar", "normal", "binary", "linear", "search", "bucket",
            "record", "entity", "model", "domain", "policy", "access", "secret",
            "token", "cipher", "hash", "salt", "nonce", "verify", "encode",
            "decode", "upload", "backup", "restore", "mirror", "queue", "retry"
    };

    final Random random = new Random(7);
    final List<Target> targets = new ArrayList<Target>();
    final List<Bullet> bullets = new ArrayList<Bullet>();
    final List<Impact> impacts = new ArrayList<Impact>();
    final List<BreakParticle> breakParticles = new ArrayList<BreakParticle>();
    final List<XpOrb> xpOrbs = new ArrayList<XpOrb>();
    final Timer timer;
    final boolean smokeMode;
    final String[] laneWords = new String[2];
    final UpgradeCard[] upgradeChoices = new UpgradeCard[3];
    BufferedImage frameBuffer;
    long lastFrameNanos = 0L;
    double accumulatedFrameMs = 0.0;
    double renderAlpha = 1.0;

    int lane = 0;
    int hp = PLAYER_BASE_HP;
    int maxHp = PLAYER_BASE_HP;
    int score = 0;
    int highScore = 0;
    int kills = 0;
    int bossLevel = 0;
    int combo = 0;
    int bestCombo = 0;
    int tick = 0;
    int spawnCooldown = 25;
    int bossCooldownKills = 10;
    int autoFireCooldown = 0;
    int continuousSurgeTicks = 0;
    int wrongFlashTicks = 0;
    int wrongFlashLane = -1;
    int inputPulseTicks = 0;
    int completePulseTicks = 0;
    int completePulseLane = -1;
    int messageTicks = 220;
    int baseDamage = 5;
    int bossDamageBonus = 0;
    int perfectBonus = 0;
    int correctTypedChars = 0;
    int runStartTick = 0;
    int xp = 0;
    int xpToNext = INITIAL_XP_TO_NEXT;
    int upgradeLevel = 0;
    int pendingUpgradeChoices = 0;
    int selectedUpgradeIndex = 0;
    int damageBonusPercent = 0;
    int fireRateBonusPercent = 0;
    int maxHpUpgradeBonus = 0;
    int phaseSwitchLevel = 0;
    int phaseSwitchTicks = 0;
    int phaseSwitchCooldownTicks = 0;
    int bossBreakerLevel = 0;
    int crossfeedLevel = 0;
    int crossfeedCooldownTicks = 0;
    int longWordRewardLevel = 0;
    int shortWordQuickshotLevel = 0;
    int finalLetterBurstLevel = 0;
    int vowelConvergenceLevel = 0;
    int hardConsonantBreakLevel = 0;
    int selectedTestUpgradeIndex = 0;
    final int[] effectLevels = new int[UpgradeEffect.values().length];
    final int[] laneBarrierCharges = new int[2];
    final int[] laneSlowTicks = new int[2];
    final UpgradeEffect[] highTalents = new UpgradeEffect[3];
    UpgradeCard pendingHighTalent = null;
    boolean returnToTestBackendAfterHighReplace = false;
    double laneSwitchFromY = LANE_Y[0];
    double laneSwitchToY = LANE_Y[0];
    int laneSwitchAnimationTicks = 0;
    boolean pendingLaneAttack = false;
    int pendingAttackLane = 0;
    String pendingAttackWord = "";
    int recentErrorTicks = 0;
    int longFocusTicks = 0;
    int calmGuardCharges = 0;
    int lastCompletedLane = -1;
    int sameLaneStreak = 0;
    int alternatingLaneStreak = 0;
    char lastCompletedFirstChar = 0;
    int sameFirstLetterStreak = 0;
    int postErrorCleanStreak = 0;
    boolean nextWordStartsAfterError = false;
    boolean currentWordUsedBackspace = false;
    boolean currentWordStartedAfterError = false;
    boolean currentWordUniqueFirst = false;
    boolean currentWordCleanPrefix3 = false;
    boolean currentWordReachedHalfPrefix = false;
    boolean currentWordHadDualPrefix = false;
    boolean completedWordWasClean = false;
    boolean completedWordUsedBackspace = false;
    boolean completedWordStartedAfterError = false;
    boolean completedWordUniqueFirst = false;
    boolean completedWordCleanPrefix3 = false;
    boolean completedWordReachedHalfPrefix = false;
    boolean completedWordHadDualPrefix = false;
    boolean completedWordAlternated = false;
    boolean completedWordSwitchedLane = false;

    String lastCompletedWord = "";
    String message = "Type lane words to fight. Collect XP balls; Space opens upgrades when ready.";
    String messageZh = "输入赛道词战斗。收集 XP 球；升级就绪时按 Space。";
    String deathReason = "";
    String deathReasonZh = "";
    String typed = "";
    String sudoBuffer = "";
    int typingLane = -1;
    ChoiceMode choiceMode = ChoiceMode.NONE;
    Difficulty difficulty = Difficulty.NORMAL;
    Language language = Language.ENGLISH;
    boolean started = false;
    boolean paused = false;
    boolean gameOver = false;
    boolean fullscreen = false;
    boolean testInvincible = false;
    Rectangle windowedBounds;

    GamePanel(boolean startTimer) {
        this.smokeMode = !startTimer;
        started = smokeMode;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(COLOR_BACKGROUND);
        setFocusable(true);
        enableInputMethods(false);
        addKeyListener(this);
        refreshLaneWord(0);
        refreshLaneWord(1);
        timer = new Timer(FRAME_MS, this);
        timer.setCoalesce(true);
        if (startTimer) {
            timer.start();
        }
    }

    public void actionPerformed(ActionEvent event) {
        advanceFrameClock();
        repaint();
    }

    void advanceFrameClock() {
        long now = System.nanoTime();
        double elapsedMs = FRAME_MS;
        if (lastFrameNanos != 0L) {
            elapsedMs = (now - lastFrameNanos) / 1_000_000.0;
        }
        lastFrameNanos = now;

        accumulatedFrameMs += Math.min(Math.max(0.0, elapsedMs), TICK_MS * 3.0);
        while (accumulatedFrameMs >= TICK_MS) {
            step();
            accumulatedFrameMs -= TICK_MS;
        }
        renderAlpha = Math.max(0.0, Math.min(1.0, accumulatedFrameMs / TICK_MS));
    }

    void resetFrameClock() {
        lastFrameNanos = 0L;
        accumulatedFrameMs = 0.0;
        renderAlpha = 1.0;
    }

    void step() {
        if (!started || paused) {
            return;
        }
        if (gameOver || choiceMode != ChoiceMode.NONE) {
            return;
        }

        tick++;
        spawnCooldown--;
        wrongFlashTicks = Math.max(0, wrongFlashTicks - 1);
        inputPulseTicks = Math.max(0, inputPulseTicks - 1);
        completePulseTicks = Math.max(0, completePulseTicks - 1);
        messageTicks = Math.max(0, messageTicks - 1);
        recentErrorTicks = Math.max(0, recentErrorTicks - 1);
        longFocusTicks = Math.max(0, longFocusTicks - 1);
        laneSwitchAnimationTicks = Math.max(0, laneSwitchAnimationTicks - 1);
        if (pendingLaneAttack && laneSwitchAnimationTicks <= 0) {
            finishPendingLaneAttack();
        }
        for (int i = 0; i < laneSlowTicks.length; i++) {
            laneSlowTicks[i] = Math.max(0, laneSlowTicks[i] - 1);
        }

        if (spawnCooldown <= 0 && !hasBoss()) {
            spawnRandomTarget();
            spawnCooldown = nextSpawnCooldown();
        }

        for (Target target : targets) {
            target.previousX = target.x;
            double speed = target.speed * laneSpeedMultiplier(target.lane);
            if (target.slowTicks > 0) {
                speed *= 0.55;
                target.slowTicks--;
            }
            target.markTicks = Math.max(0, target.markTicks - 1);
            target.x -= speed;
            if (target.kind == TargetKind.BOSS) {
                target.phaseTick++;
                if (target.phaseTick % 150 == 0) {
                    target.lane = 1 - target.lane;
                    showMessage("Boss forced a lane change. Decide: chase it or clear your lane.",
                            "Boss 强制换路。追击，还是先清当前 lane？");
                }
            }
        }

        if (!pendingLaneAttack && hasHighTalent(UpgradeEffect.RHYTHM_CANNON)) {
            runAutoFire();
        }

        updateBullets();
        updateImpacts();
        updateBreakParticles();
        updateXpOrbs();
        handleCollisions();
        removeDeadAndEscaped();
        phaseSwitchTicks = Math.max(0, phaseSwitchTicks - 1);
        phaseSwitchCooldownTicks = Math.max(0, phaseSwitchCooldownTicks - 1);
        crossfeedCooldownTicks = Math.max(0, crossfeedCooldownTicks - 1);

        if (kills >= bossCooldownKills && !hasBoss()) {
            spawnBoss();
            bossCooldownKills += 12 + bossLevel * 4;
        }
    }

    void spawnRandomTarget() {
        int targetLane = random.nextInt(2);
        int pressure = pressureLevel();
        int roll = random.nextInt(100);
        Target target;
        if (roll < Math.max(5, 11 - pressure / 2)) {
            target = Target.upgrade(targetLane);
        } else if (roll < 28 + pressure) {
            target = Target.enemy(targetLane, TargetKind.FAST);
        } else if (roll < 46 + pressure * 2) {
            target = Target.enemy(targetLane, TargetKind.TANK);
        } else if (roll < 57 + pressure * 2 && (bossLevel >= 1 || pressure >= 4)) {
            target = Target.enemy(targetLane, TargetKind.SWITCHER);
        } else {
            target = Target.enemy(targetLane, TargetKind.NORMAL);
        }
        scaleTarget(target);
        target.previousX = target.x;
        targets.add(target);
    }

    void spawnBoss() {
        bossLevel++;
        Target boss = Target.boss(random.nextInt(2), bossLevel);
        scaleTarget(boss);
        boss.previousX = boss.x;
        targets.add(boss);
        typingLane = -1;
        typed = "";
        showMessage("Boss " + bossLevel + ": large XP reward.",
                "Boss " + bossLevel + "：大量 XP 奖励。");
    }

    int pressureLevel() {
        return Math.min(14, tick / 430 + kills / 9 + bossLevel * 2);
    }

    int nextSpawnCooldown() {
        int base = BASE_SPAWN_COOLDOWN - random.nextInt(SPAWN_COOLDOWN_JITTER);
        return Math.max(12, base);
    }

    void scaleTarget(Target target) {
        int pressure = pressureLevel();
        int hpPressure = timeHpPressure() + kills / 12 + bossLevel * 2;
        double speedMultiplier = 1.0 + pressure * 0.075;
        int hpBonus = 0;
        if (target.kind == TargetKind.NORMAL) {
            hpBonus = scaleEnemyHp(hpPressure / 2);
        } else if (target.kind == TargetKind.FAST) {
            hpBonus = scaleEnemyHp(hpPressure / 3);
        } else if (target.kind == TargetKind.TANK) {
            hpBonus = scaleEnemyHp(hpPressure * 2 / 3 + bossLevel);
        } else if (target.kind == TargetKind.SWITCHER) {
            hpBonus = scaleEnemyHp(hpPressure / 2 + 1);
        } else if (target.kind == TargetKind.UPGRADE) {
            hpBonus = scaleEnemyHp(hpPressure / 3);
        } else if (target.kind == TargetKind.BOSS) {
            hpBonus = scaleBossHp(hpPressure * 2 + bossLevel * 5);
            speedMultiplier = 1.0 + pressure * 0.035;
        }
        target.hp += hpBonus;
        target.maxHp += hpBonus;
        target.speed *= speedMultiplier * difficulty.speedMultiplier;
    }

    int timeHpPressure() {
        return tick / 300;
    }

    static int scaleEnemyHp(int hp) {
        return (hp * 3 + 1) / 2;
    }

    static int scaleBossHp(int hp) {
        return hp * 2;
    }

    double laneSpeedMultiplier(int laneIndex) {
        return laneSlowTicks[laneIndex] > 0 ? 0.68 : 1.0;
    }

    void runAutoFire() {
        continuousSurgeTicks = Math.max(0, continuousSurgeTicks - 1);
        autoFireCooldown--;
        if (autoFireCooldown > 0) {
            return;
        }
        Target target = nearestTargetInLane(lane);
        if (target != null) {
            boolean surge = continuousSurgeTicks > 0;
            int damage = surge ? continuousSurgeDamage() : continuousChipDamage();
            damage = scaledDamage(damage, target);
            fireBullet(lane, damage, 1, surge ? BulletKind.CONTINUOUS_SURGE : BulletKind.CONTINUOUS);
            autoFireCooldown = surge ? 2 : 8;
        }
    }

    int continuousChipDamage() {
        return 1;
    }

    int continuousSurgeDamage() {
        return 3 + perfectBonus / 3;
    }

    void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.previousX = bullet.x;
            double maxX = maxBulletX(bullet);
            bullet.x = Math.min(bullet.x + bullet.speed, maxX);
            bullet.lifeTicks--;

            for (Target target : targets) {
                if (bullet.pierceLeft <= 0 || target.dead || target.hp <= 0 || target.lane != bullet.lane) {
                    continue;
                }
                if (bulletAlreadyHit(bullet, target)) {
                    continue;
                }
                if (bulletHitsTarget(bullet, target)) {
                    int dealt = bulletDamageForHit(bullet);
                    target.hp -= dealt;
                    target.hitFlash = 10;
                    rememberBulletHit(bullet, target);
                    bullet.hits++;
                    bullet.pierceLeft--;
                    impacts.add(new Impact(target.x, LANE_Y[target.lane], dealt));
                    if (bullet.kind == BulletKind.BURST) {
                        applyBurstSplash(bullet, target, dealt);
                    }
                }
            }

            if (bullet.x >= maxX || bullet.lifeTicks <= 0 || bullet.pierceLeft <= 0) {
                iterator.remove();
            }
        }
    }

    void applyBurstSplash(Bullet bullet, Target center, int dealt) {
        int splashDamage = Math.max(1, dealt / 2);
        for (Target target : targets) {
            if (target == center || target.dead || target.hp <= 0 || target.lane != center.lane) {
                continue;
            }
            if (Math.abs(target.x - center.x) > 92) {
                continue;
            }
            target.hp -= splashDamage;
            target.hitFlash = 10;
            impacts.add(new Impact(target.x, LANE_Y[target.lane], splashDamage));
        }
    }

    int bulletDamageForHit(Bullet bullet) {
        if (bullet.kind != BulletKind.PIERCE) {
            return Math.max(1, bullet.damage - bullet.hits);
        }
        int percent = Math.max(0, 100 - bullet.hits * PIERCING_DECAY_PERCENT);
        return Math.max(1, bullet.damage * percent / 100);
    }

    double maxBulletX(Bullet bullet) {
        return LANE_RIGHT_X;
    }

    boolean bulletAlreadyHit(Bullet bullet, Target target) {
        return bullet.hitTargets != null && bullet.hitTargets.contains(target);
    }

    void rememberBulletHit(Bullet bullet, Target target) {
        if (bullet.kind != BulletKind.PIERCE) {
            return;
        }
        if (bullet.hitTargets == null) {
            bullet.hitTargets = new ArrayList<Target>();
        }
        bullet.hitTargets.add(target);
    }

    boolean bulletHitsTarget(Bullet bullet, Target target) {
        int halfWidth = targetHalfWidth(target.kind);
        double left = target.x - halfWidth;
        double right = target.x + halfWidth;
        return bullet.x + bullet.radius >= left && bullet.previousX - bullet.radius <= right;
    }

    static int targetHalfWidth(TargetKind kind) {
        return kind == TargetKind.BOSS ? 59 : 46;
    }

    static double targetSpawnX(TargetKind kind) {
        return LANE_RIGHT_X - targetHalfWidth(kind);
    }

    void updateImpacts() {
        Iterator<Impact> iterator = impacts.iterator();
        while (iterator.hasNext()) {
            Impact impact = iterator.next();
            impact.ticks--;
            if (impact.ticks <= 0) {
                iterator.remove();
            }
        }
    }

    void updateBreakParticles() {
        Iterator<BreakParticle> iterator = breakParticles.iterator();
        while (iterator.hasNext()) {
            BreakParticle particle = iterator.next();
            particle.step();
            if (particle.life <= 0) {
                iterator.remove();
            }
        }
    }

    void updateXpOrbs() {
        Iterator<XpOrb> iterator = xpOrbs.iterator();
        while (iterator.hasNext()) {
            XpOrb orb = iterator.next();
            orb.previousX = orb.x;
            orb.previousY = orb.y;
            orb.x -= orb.speed;
            orb.pulse++;
            double playerY = LANE_Y[lane];
            double dx = PLAYER_X - orb.x;
            double dy = playerY - orb.y;
            double distance = Math.hypot(dx, dy);
            if (orb.lane == lane && distance <= XP_ATTRACT_RADIUS) {
                orb.attracted = true;
            }
            if (orb.attracted && distance > XP_COLLECT_RADIUS) {
                double pull = 2.0 + Math.max(0.0, XP_ATTRACT_RADIUS - distance) / 18.0;
                double step = Math.min(distance - XP_COLLECT_RADIUS, pull);
                orb.x += dx / distance * step;
                orb.y += dy / distance * step;
                distance = Math.hypot(PLAYER_X - orb.x, playerY - orb.y);
            }
            if ((orb.attracted || orb.lane == lane) && distance <= XP_COLLECT_RADIUS) {
                addExperience(orb.value);
                score += orb.value * 12;
                impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0));
                iterator.remove();
            } else if (orb.x < -45) {
                iterator.remove();
            }
        }
    }

    void handleCollisions() {
        for (Target target : targets) {
            if (target.x <= PLAYER_X + 32 && target.lane == lane && !target.dead && target.hp > 0) {
                if (phaseSwitchTicks > 0 && target.kind != TargetKind.BOSS) {
                    target.dead = true;
                    phaseSwitchTicks = 0;
                    showMessage("Phase switch avoided a collision.", "换路相位避开了一次碰撞。");
                    continue;
                }
                if (laneBarrierCharges[lane] > 0 && target.kind != TargetKind.BOSS) {
                    target.dead = true;
                    laneBarrierCharges[lane]--;
                    impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0));
                    showMessage("Lane guard stopped a collision.", "赛道护盾拦截了一次碰撞。");
                    continue;
                }
                int damage = collisionCost(target);
                if (calmGuardCharges > 0 && target.kind != TargetKind.BOSS) {
                    calmGuardCharges--;
                    damage = Math.max(0, damage - (6 + effectLevel(UpgradeEffect.CALM_AFTER_ERROR) * 3));
                    if (damage == 0) {
                        target.dead = true;
                        impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0));
                        showMessage("Calm guard absorbed a collision.", "冷静守护吸收了一次碰撞。");
                        continue;
                    }
                }
                hp -= damage;
                target.dead = true;
                combo = 0;
                if (target.kind == TargetKind.UPGRADE) {
                    setDeathReason("a cache target reached your lane", "缓存目标抵达了你的赛道");
                } else {
                    setDeathReason("a target reached your lane", "敌人抵达了你的赛道");
                }
                showMessage("Collision: -" + damage + " HP. Type lane words earlier or press Space deliberately.",
                        "碰撞：-" + damage + " HP。更早输入赛道词，或谨慎按 Space。");
                if (hp <= 0) {
                    endGame();
                }
            }
        }
    }

    int collisionCost(Target target) {
        if (testInvincible) {
            return 0;
        }
        return Math.max(1, Math.max(0, target.hp));
    }

    void removeDeadAndEscaped() {
        Iterator<Target> iterator = targets.iterator();
        while (iterator.hasNext()) {
            Target target = iterator.next();
            target.hitFlash = Math.max(0, target.hitFlash - 1);
            if (target.dead || target.hp <= 0) {
                if (target.hp <= 0 && !target.counted) {
                    spawnBreakParticles(target);
                    rewardFor(target);
                    target.counted = true;
                }
                iterator.remove();
            } else if (target.x < -120) {
                iterator.remove();
            }
        }
    }

    void spawnBreakParticles(Target target) {
        int count = breakParticleCount(target);
        double centerX = target.x;
        double centerY = LANE_Y[target.lane];
        Color base = colorFor(target);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double burst = 2.2 + random.nextDouble() * 4.6;
            double laneBias = target.lane == 0 ? -0.25 : 0.25;
            double vx = Math.cos(angle) * burst + random.nextDouble() * 1.8 - 0.9;
            double vy = Math.sin(angle) * burst * 0.72 + laneBias;
            double offsetX = random.nextDouble() * 64.0 - 32.0;
            double offsetY = random.nextDouble() * 34.0 - 17.0;
            double size = 3.0 + random.nextDouble() * 6.0;
            int life = 18 + random.nextInt(15);
            breakParticles.add(new BreakParticle(centerX + offsetX, centerY + offsetY, vx, vy, size, life, base));
        }
    }

    int breakParticleCount(Target target) {
        if (target.kind == TargetKind.BOSS) {
            return 48;
        }
        if (target.kind == TargetKind.TANK || target.kind == TargetKind.SWITCHER || target.kind == TargetKind.UPGRADE) {
            return 26;
        }
        return target.kind == TargetKind.FAST ? 18 : 20;
    }

    void rewardFor(Target target) {
        if (target.kind == TargetKind.BOSS) {
            score += 250 + bossLevel * 60;
            kills += 2;
            grantBossExperience();
            return;
        }

        kills++;
        score += target.kind == TargetKind.UPGRADE ? 90 : 35 + target.maxHp * 5;
        if (target.kind != TargetKind.BOSS) {
            dropXpOrb(target);
        }
    }

    void dropXpOrb(Target target) {
        int value = xpValueFor(target);
        xpOrbs.add(new XpOrb(target.x, target.lane, value));
        showMessage("XP ball dropped. Enter its lane to collect it.",
                "XP 球掉落。进入对应 lane 靠近收集。");
    }

    int xpValueFor(Target target) {
        if (target.kind == TargetKind.TANK || target.kind == TargetKind.SWITCHER || target.kind == TargetKind.UPGRADE) {
            return 4;
        }
        if (target.kind == TargetKind.FAST) {
            return 2;
        }
        return 1;
    }

    void addExperience(int amount) {
        addExperience(amount, true);
    }

    void addExperience(int amount, boolean allowCrossfeed) {
        int gained = amount;
        if (allowCrossfeed && crossfeedLevel > 0
                && crossfeedCooldownTicks <= 0 && startsWithCrossfeedLetter(lastCompletedWord)) {
            gained += Math.max(1, amount * 2 / 5);
            crossfeedCooldownTicks = 150;
        }
        xp += gained;
        while (xp >= xpToNext) {
            xp -= xpToNext;
            upgradeLevel++;
            pendingUpgradeChoices++;
            xpToNext = nextXpRequirement();
        }
        if (pendingUpgradeChoices > 0) {
            showMessage("Upgrade ready. Press Space when you want to choose.",
                    "升级就绪。想选择时按 Space。");
        }
    }

    boolean startsWithCrossfeedLetter(String word) {
        if (word == null || word.length() == 0) {
            return false;
        }
        char c = word.charAt(0);
        return c == 'f' || c == 't' || c == 'k';
    }

    int nextXpRequirement() {
        int nextUpgradeNumber = upgradeLevel + 1;
        if (nextUpgradeNumber <= EARLY_XP_REQUIREMENTS.length) {
            return EARLY_XP_REQUIREMENTS[nextUpgradeNumber - 1];
        }
        return 30 + (nextUpgradeNumber - EARLY_XP_REQUIREMENTS.length - 1) * 10;
    }

    void grantBossExperience() {
        typingLane = -1;
        typed = "";
        int reward = bossXpReward();
        int pendingBefore = pendingUpgradeChoices;
        addExperience(reward, false);
        if (pendingUpgradeChoices > pendingBefore) {
            showMessage("Boss cleared: +" + reward + " XP. Upgrade charged.",
                    "Boss 击破：+" + reward + " XP。升级已就绪。");
        } else {
            showMessage("Boss cleared: +" + reward + " XP.",
                    "Boss 击破：+" + reward + " XP。");
        }
    }

    int bossXpReward() {
        return BOSS_XP_BASE + bossLevel * BOSS_XP_PER_LEVEL;
    }

    void openUpgradeMenu() {
        if (pendingUpgradeChoices <= 0 || choiceMode != ChoiceMode.NONE || gameOver) {
            return;
        }
        buildUpgradeChoices();
        selectedUpgradeIndex = 0;
        choiceMode = ChoiceMode.UPGRADE;
        typingLane = -1;
        typed = "";
        showMessage("Choose an upgrade. Left/Right and Enter, or 1/2/3.",
                "选择升级。左右键 + Enter，或按 1/2/3。");
    }

    void buildUpgradeChoices() {
        UpgradeRarity[] slots = rollUpgradeSlots();
        for (int i = 0; i < upgradeChoices.length; i++) {
            upgradeChoices[i] = randomCardFor(slots[i], i);
        }
    }

    UpgradeRarity[] rollUpgradeSlots() {
        int roll = random.nextInt(100);
        if (upgradeLevel > 0 && upgradeLevel % 5 == 0 && roll < 35) {
            return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.UNCOMMON, UpgradeRarity.HIGH};
        }
        if (roll < 12) {
            return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.UNCOMMON, UpgradeRarity.UNCOMMON};
        }
        if (roll < 19) {
            return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.UNCOMMON, UpgradeRarity.HIGH};
        }
        return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.COMMON, UpgradeRarity.UNCOMMON};
    }

    UpgradeCard randomCardFor(UpgradeRarity rarity, int slotIndex) {
        UpgradeEffect[] pool = poolFor(rarity);
        for (int tries = 0; tries < 12; tries++) {
            UpgradeEffect effect = pool[random.nextInt(pool.length)];
            if (canOfferEffect(effect, rarity)) {
                return createCard(effect, rarity);
            }
        }
        for (int i = 0; i < pool.length; i++) {
            if (canOfferEffect(pool[i], rarity)) {
                return createCard(pool[i], rarity);
            }
        }
        if (rarity == UpgradeRarity.HIGH) {
            return randomCardFor(UpgradeRarity.UNCOMMON, slotIndex);
        }
        return createCard(pool[slotIndex % pool.length], rarity);
    }

    boolean canOfferEffect(UpgradeEffect effect, UpgradeRarity rarity) {
        if (choiceAlreadyHas(effect)) {
            return false;
        }
        return rarity != UpgradeRarity.HIGH || !hasHighTalent(effect);
    }

    UpgradeEffect[] poolFor(UpgradeRarity rarity) {
        return gameplayPoolFor(rarity);
    }

    UpgradeEffect[] gameplayPoolFor(UpgradeRarity rarity) {
        if (rarity == UpgradeRarity.HIGH) {
            return new UpgradeEffect[] {
                    UpgradeEffect.RHYTHM_CANNON,
                    UpgradeEffect.FROST_FIELD
            };
        }
        if (rarity == UpgradeRarity.UNCOMMON) {
            if (ENABLE_GROUP_TWO_TO_FIVE_UPGRADES) {
                return new UpgradeEffect[] {
                        UpgradeEffect.PHASE_SWITCH,
                        UpgradeEffect.BOSS_BREAKER,
                        UpgradeEffect.CROSSFEED,
                        UpgradeEffect.SINGLE_LANE_BASTION,
                        UpgradeEffect.ALTERNATING_GUARD,
                        UpgradeEffect.PREFIX_ILLUMINATION,
                        UpgradeEffect.FIRST_LETTER_TICKET,
                        UpgradeEffect.DUAL_PREFIX_SCAN,
                        UpgradeEffect.CALM_AFTER_ERROR,
                        UpgradeEffect.BACKSPACE_COUNTER,
                        UpgradeEffect.DANGER_WORD,
                        UpgradeEffect.PRESSURE_VALVE
                };
            }
            return new UpgradeEffect[] {
                    UpgradeEffect.PHASE_SWITCH,
                    UpgradeEffect.BOSS_BREAKER,
                    UpgradeEffect.CROSSFEED
            };
        }
        if (ENABLE_GROUP_ONE_UPGRADES) {
            if (ENABLE_GROUP_TWO_TO_FIVE_UPGRADES) {
                return new UpgradeEffect[] {
                        UpgradeEffect.FIELD_PATCH,
                        UpgradeEffect.REINFORCED_CORE,
                        UpgradeEffect.CALIBRATED_DAMAGE,
                        UpgradeEffect.TRIGGER_TUNING,
                        UpgradeEffect.COMBO_TUNING,
                        UpgradeEffect.LONG_WORD_REWARD,
                        UpgradeEffect.SHORT_WORD_QUICKSHOT,
                        UpgradeEffect.FINAL_LETTER_BURST,
                        UpgradeEffect.VOWEL_CONVERGENCE,
                        UpgradeEffect.HARD_CONSONANT_BREAK,
                        UpgradeEffect.SAME_LANE_SUPPRESSION,
                        UpgradeEffect.LANE_SWAP_BEAT,
                        UpgradeEffect.COMBO_CALIBRATOR,
                        UpgradeEffect.FIRST_LETTER_LOCK,
                        UpgradeEffect.ACCURATE_OPENER,
                        UpgradeEffect.CLEAN_FINISH,
                        UpgradeEffect.ERROR_RESET,
                        UpgradeEffect.BACKSPACE_FIX,
                        UpgradeEffect.PRECISE_PICKUP,
                        UpgradeEffect.FINAL_LETTER_PULL,
                        UpgradeEffect.LONG_WORD_FOCUS
                };
            }
            return new UpgradeEffect[] {
                    UpgradeEffect.FIELD_PATCH,
                    UpgradeEffect.REINFORCED_CORE,
                    UpgradeEffect.CALIBRATED_DAMAGE,
                    UpgradeEffect.TRIGGER_TUNING,
                    UpgradeEffect.COMBO_TUNING,
                    UpgradeEffect.LONG_WORD_REWARD,
                    UpgradeEffect.SHORT_WORD_QUICKSHOT,
                    UpgradeEffect.FINAL_LETTER_BURST,
                    UpgradeEffect.VOWEL_CONVERGENCE,
                    UpgradeEffect.HARD_CONSONANT_BREAK
            };
        }
        return new UpgradeEffect[] {
                UpgradeEffect.FIELD_PATCH,
                UpgradeEffect.REINFORCED_CORE,
                UpgradeEffect.CALIBRATED_DAMAGE,
                UpgradeEffect.TRIGGER_TUNING,
                UpgradeEffect.COMBO_TUNING
        };
    }

    UpgradeEffect[] sudoToolEffects() {
        return new UpgradeEffect[] {
                UpgradeEffect.TEST_INVINCIBLE,
                UpgradeEffect.TEST_BIG_XP
        };
    }

    boolean choiceAlreadyHas(UpgradeEffect effect) {
        for (UpgradeCard card : upgradeChoices) {
            if (card != null && card.effect == effect) {
                return true;
            }
        }
        return false;
    }

    boolean isHighTalentEffect(UpgradeEffect effect) {
        return rarityForEffect(effect) == UpgradeRarity.HIGH
                && effect != UpgradeEffect.TEST_INVINCIBLE
                && effect != UpgradeEffect.TEST_BIG_XP;
    }

    boolean isGroupTwoToFiveEffect(UpgradeEffect effect) {
        return effect == UpgradeEffect.SAME_LANE_SUPPRESSION
                || effect == UpgradeEffect.LANE_SWAP_BEAT
                || effect == UpgradeEffect.SINGLE_LANE_BASTION
                || effect == UpgradeEffect.ALTERNATING_GUARD
                || effect == UpgradeEffect.COMBO_CALIBRATOR
                || effect == UpgradeEffect.FIRST_LETTER_LOCK
                || effect == UpgradeEffect.ACCURATE_OPENER
                || effect == UpgradeEffect.PREFIX_ILLUMINATION
                || effect == UpgradeEffect.FIRST_LETTER_TICKET
                || effect == UpgradeEffect.DUAL_PREFIX_SCAN
                || effect == UpgradeEffect.CLEAN_FINISH
                || effect == UpgradeEffect.ERROR_RESET
                || effect == UpgradeEffect.BACKSPACE_FIX
                || effect == UpgradeEffect.CALM_AFTER_ERROR
                || effect == UpgradeEffect.BACKSPACE_COUNTER
                || effect == UpgradeEffect.PRECISE_PICKUP
                || effect == UpgradeEffect.FINAL_LETTER_PULL
                || effect == UpgradeEffect.LONG_WORD_FOCUS
                || effect == UpgradeEffect.DANGER_WORD
                || effect == UpgradeEffect.PRESSURE_VALVE;
    }

    int effectLevel(UpgradeEffect effect) {
        return effectLevels[effect.ordinal()];
    }

    void raiseEffectLevel(UpgradeEffect effect) {
        effectLevels[effect.ordinal()]++;
    }

    boolean hasHighTalent(UpgradeEffect effect) {
        for (UpgradeEffect talent : highTalents) {
            if (talent == effect) {
                return true;
            }
        }
        return false;
    }

    int firstEmptyHighTalentSlot() {
        for (int i = 0; i < highTalents.length; i++) {
            if (highTalents[i] == null) {
                return i;
            }
        }
        return -1;
    }

    UpgradeCard createCard(UpgradeEffect effect, UpgradeRarity rarity) {
        if (effect == UpgradeEffect.FIELD_PATCH) {
            return new UpgradeCard("Field Patch", "Heal 8 HP; if full, +6 max HP",
                    "战地修补", "回复 8 HP；满 HP 时改为 +6 上限", rarity, effect);
        }
        if (effect == UpgradeEffect.REINFORCED_CORE) {
            return new UpgradeCard("Reinforced Core", "+6 max HP",
                    "强化核心", "+6 HP 上限", rarity, effect);
        }
        if (effect == UpgradeEffect.CALIBRATED_DAMAGE) {
            return new UpgradeCard("Calibrated Damage", "+12% additive damage",
                    "校准伤害", "+12% 加算伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.TRIGGER_TUNING) {
            return new UpgradeCard("Trigger Tuning",
                    "Single-shot: +1 bullet per level, +20% total damage split across pellets. Continuous: +20% fire rate.",
                    "扳机调校",
                    "单发：每级 +1 弹，总伤害 +20% 后平分到弹丸；连发：+20% 射速。",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.COMBO_TUNING) {
            return new UpgradeCard("Combo Tuning", "Clean combo damage improves",
                    "连击调律", "稳定连击会提升伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.LONG_WORD_REWARD) {
            return new UpgradeCard("Longword Reward", "6-7 letter words repair HP",
                    "长词回报", "完成 6-7 字母词修补 HP", rarity, effect);
        }
        if (effect == UpgradeEffect.SHORT_WORD_QUICKSHOT) {
            return new UpgradeCard("Shortword Quickshot", "4-letter words fire a fast light shot",
                    "短词快枪", "完成 4 字母词追加高速轻弹", rarity, effect);
        }
        if (effect == UpgradeEffect.FINAL_LETTER_BURST) {
            return new UpgradeCard("Final Letter Burst", "Completed words add a small burst round",
                    "尾字爆点", "完成词追加小范围爆点弹", rarity, effect);
        }
        if (effect == UpgradeEffect.VOWEL_CONVERGENCE) {
            return new UpgradeCard("Vowel Convergence", "Vowel-ending words pull nearby XP",
                    "元音收束", "元音结尾词牵引同 lane XP", rarity, effect);
        }
        if (effect == UpgradeEffect.HARD_CONSONANT_BREAK) {
            return new UpgradeCard("Hard Consonant Break", "t/k/p/d/g endings crack thick targets",
                    "硬辅音破甲", "t/k/p/d/g 结尾词克制厚血目标", rarity, effect);
        }
        if (effect == UpgradeEffect.SAME_LANE_SUPPRESSION) {
            return new UpgradeCard("Same-Lane Suppression", "Repeated same-lane words add damage and slow",
                    "同路压制", "连续完成同路词会增伤并减速前排", rarity, effect);
        }
        if (effect == UpgradeEffect.LANE_SWAP_BEAT) {
            return new UpgradeCard("Lane Swap Beat", "Alternating lane words fire split light shots",
                    "上下换拍", "上下交替完成词会追加分路轻弹", rarity, effect);
        }
        if (effect == UpgradeEffect.SINGLE_LANE_BASTION) {
            return new UpgradeCard("Single-Lane Bastion", "Four same-lane words build one lane guard",
                    "单路堡垒", "同路连续 4 词生成该路护栏", rarity, effect);
        }
        if (effect == UpgradeEffect.ALTERNATING_GUARD) {
            return new UpgradeCard("Alternating Guard", "Four alternating words guard both lanes",
                    "交替护栏", "上下交替 4 词后双路生成护栏", rarity, effect);
        }
        if (effect == UpgradeEffect.COMBO_CALIBRATOR) {
            return new UpgradeCard("Combo Calibrator", "Combo 20/40/60 fires a precise shot",
                    "连击校准器", "连击 20/40/60 时追加精准弹", rarity, effect);
        }
        if (effect == UpgradeEffect.FIRST_LETTER_LOCK) {
            return new UpgradeCard("First-Letter Lock", "Unique first letters add focus damage",
                    "首字母锁定", "首字母锁定目标时追加伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.ACCURATE_OPENER) {
            return new UpgradeCard("Accurate Opener", "Clean first 3 letters add damage",
                    "准确起笔", "前三字母无回删完成时追加伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.PREFIX_ILLUMINATION) {
            return new UpgradeCard("Prefix Illumination", "Clean half-word prefixes mark targets",
                    "前缀照明", "半词前缀无回删时标记并增伤", rarity, effect);
        }
        if (effect == UpgradeEffect.FIRST_LETTER_TICKET) {
            return new UpgradeCard("First-Letter Ticket", "Three same first letters drop bonus XP",
                    "首字母税票", "连续同首字母完成词会生成额外 XP", rarity, effect);
        }
        if (effect == UpgradeEffect.DUAL_PREFIX_SCAN) {
            return new UpgradeCard("Dual Prefix Scan", "Tactical first-letter groups scan the other lane",
                    "双前缀读屏", "战术首字母组会向另一 lane 追加扫射", rarity, effect);
        }
        if (effect == UpgradeEffect.CLEAN_FINISH) {
            return new UpgradeCard("Clean Finish", "No-error words add a light shot",
                    "干净收尾", "无错且无回删完成词追加轻弹", rarity, effect);
        }
        if (effect == UpgradeEffect.ERROR_RESET) {
            return new UpgradeCard("Error Reset", "Complete soon after a mismatch to fire a reset shot",
                    "断错复位", "失配后快速完成词追加复位弹", rarity, effect);
        }
        if (effect == UpgradeEffect.BACKSPACE_FIX) {
            return new UpgradeCard("Backspace Fix", "Backspace-corrected words add damage",
                    "回删修正", "回删修正后完成词追加伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.CALM_AFTER_ERROR) {
            return new UpgradeCard("Calm After Error", "Two clean recovery words prepare a guard",
                    "错后冷静", "失配后连续干净完成 2 词获得守护", rarity, effect);
        }
        if (effect == UpgradeEffect.BACKSPACE_COUNTER) {
            return new UpgradeCard("Backspace Counter", "Backspace-corrected words fire a counter shot",
                    "回删反击", "回删修正完成词追加反击弹", rarity, effect);
        }
        if (effect == UpgradeEffect.PRECISE_PICKUP) {
            return new UpgradeCard("Precise Pickup", "Finishing near same-lane XP pulls it harder",
                    "精准拾取", "完成词时强化同路 XP 牵引", rarity, effect);
        }
        if (effect == UpgradeEffect.FINAL_LETTER_PULL) {
            return new UpgradeCard("Final-Letter Pull", "Vowel endings pull XP and slow the front target",
                    "尾字牵引", "元音结尾牵引 XP 并减速前排", rarity, effect);
        }
        if (effect == UpgradeEffect.LONG_WORD_FOCUS) {
            return new UpgradeCard("Longword Focus", "Clean 6-7 letter words start focus damage",
                    "长词专注", "干净完成 6-7 字母词获得短暂增伤", rarity, effect);
        }
        if (effect == UpgradeEffect.DANGER_WORD) {
            return new UpgradeCard("Danger Word", "Near-danger completions knock back front targets",
                    "近险成词", "近身完成词时击退前排敌人", rarity, effect);
        }
        if (effect == UpgradeEffect.PRESSURE_VALVE) {
            return new UpgradeCard("Pressure Valve", "Danger completions slow the whole lane",
                    "压力阀", "高压完成词时减速整条 lane", rarity, effect);
        }
        if (effect == UpgradeEffect.PHASE_SWITCH) {
            return new UpgradeCard("Phase Switch", "Lane change blocks 1 collision, cooldown",
                    "相位换路", "换 lane 后抵挡 1 次碰撞，有冷却", rarity, effect);
        }
        if (effect == UpgradeEffect.BOSS_BREAKER) {
            return new UpgradeCard("Boss Breaker", "+Boss damage, smaller elite bonus",
                    "Boss 破甲", "提升 Boss 伤害，并小幅克制精英", rarity, effect);
        }
        if (effect == UpgradeEffect.CROSSFEED) {
            return new UpgradeCard("Crossfeed Letters", "f/t/k words add bonus XP on pickup",
                    "交叉字母", "以 f/t/k 开头的单词会提高 XP 收集", rarity, effect);
        }
        if (effect == UpgradeEffect.FROST_FIELD) {
            return new UpgradeCard("Piercing Rail", "High talent: typed words fire a piercing shot; each hit loses 20% damage",
                    "贯穿轨道", "高稀有天赋：完成词发射贯穿弹，每次穿透衰减 20% 伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.TEST_INVINCIBLE) {
            return new UpgradeCard("Test Invincible", "sudo only: HP loss becomes 0",
                    "测试无敌", "sudo 专用：HP 消耗变为 0", rarity, effect);
        }
        if (effect == UpgradeEffect.TEST_BIG_XP) {
            return new UpgradeCard("Test XP Cache", "sudo only: +" + TEST_BIG_XP_AMOUNT + " XP",
                    "测试经验包", "sudo 专用：+" + TEST_BIG_XP_AMOUNT + " XP", rarity, effect);
        }
        return new UpgradeCard("Autocannon", "High talent: fires without typing; typed words empower it briefly",
                "自动炮", "高稀有天赋：不打字也会连续攻击；完成词会短暂强化子弹", rarity, effect);
    }

    void applyUpgradeCard(UpgradeCard card) {
        applyUpgradeCard(card, true, true);
    }

    void applyUpgradeCard(UpgradeCard card, boolean spendPendingChoice, boolean closeMenu) {
        if (card == null) {
            return;
        }
        if (isHighTalentEffect(card.effect)) {
            applyHighTalentCard(card, spendPendingChoice, closeMenu);
            return;
        }
        if (card.effect == UpgradeEffect.FIELD_PATCH) {
            if (hp >= maxHp && maxHpUpgradeBonus < MAX_HP_UPGRADE_BONUS) {
                maxHp += MAX_HP_CARD_BONUS;
                maxHpUpgradeBonus += MAX_HP_CARD_BONUS;
                hp = maxHp;
            } else {
                hp = Math.min(maxHp, hp + FIELD_PATCH_HEAL);
            }
        } else if (card.effect == UpgradeEffect.REINFORCED_CORE) {
            if (maxHpUpgradeBonus < MAX_HP_UPGRADE_BONUS) {
                maxHp += MAX_HP_CARD_BONUS;
                maxHpUpgradeBonus += MAX_HP_CARD_BONUS;
            }
        } else if (card.effect == UpgradeEffect.CALIBRATED_DAMAGE) {
            damageBonusPercent = Math.min(MAX_DAMAGE_BONUS_PERCENT, damageBonusPercent + 12);
        } else if (card.effect == UpgradeEffect.TRIGGER_TUNING) {
            fireRateBonusPercent = Math.min(MAX_TRIGGER_TUNING_BONUS_PERCENT,
                    fireRateBonusPercent + TRIGGER_TUNING_STEP_PERCENT);
        } else if (card.effect == UpgradeEffect.COMBO_TUNING) {
            perfectBonus += 1;
        } else if (card.effect == UpgradeEffect.LONG_WORD_REWARD) {
            longWordRewardLevel++;
        } else if (card.effect == UpgradeEffect.SHORT_WORD_QUICKSHOT) {
            shortWordQuickshotLevel++;
        } else if (card.effect == UpgradeEffect.FINAL_LETTER_BURST) {
            finalLetterBurstLevel++;
        } else if (card.effect == UpgradeEffect.VOWEL_CONVERGENCE) {
            vowelConvergenceLevel++;
        } else if (card.effect == UpgradeEffect.HARD_CONSONANT_BREAK) {
            hardConsonantBreakLevel++;
        } else if (card.effect == UpgradeEffect.PHASE_SWITCH) {
            phaseSwitchLevel++;
        } else if (card.effect == UpgradeEffect.BOSS_BREAKER) {
            bossBreakerLevel++;
            bossDamageBonus += 1;
        } else if (card.effect == UpgradeEffect.CROSSFEED) {
            crossfeedLevel++;
        } else if (isGroupTwoToFiveEffect(card.effect)) {
            raiseEffectLevel(card.effect);
        } else if (card.effect == UpgradeEffect.TEST_INVINCIBLE) {
            testInvincible = true;
            hp = Math.max(hp, maxHp);
        } else if (card.effect == UpgradeEffect.TEST_BIG_XP) {
            addExperience(TEST_BIG_XP_AMOUNT, false);
        }
        finishUpgradeCard(card, spendPendingChoice, closeMenu);
    }

    void applyHighTalentCard(UpgradeCard card, boolean spendPendingChoice, boolean closeMenu) {
        if (hasHighTalent(card.effect)) {
            showMessage("High talent already installed: " + card.title + ".",
                    "高稀有天赋已拥有：" + cardTitle(card) + "。");
            return;
        }
        int emptySlot = firstEmptyHighTalentSlot();
        if (emptySlot >= 0) {
            highTalents[emptySlot] = card.effect;
            finishUpgradeCard(card, spendPendingChoice, closeMenu);
            return;
        }
        pendingHighTalent = card;
        returnToTestBackendAfterHighReplace = choiceMode == ChoiceMode.TEST_BACKEND || !closeMenu;
        if (spendPendingChoice) {
            pendingUpgradeChoices = Math.max(0, pendingUpgradeChoices - 1);
        }
        clearUpgradeChoices();
        selectedUpgradeIndex = 0;
        choiceMode = ChoiceMode.HIGH_REPLACE;
        typingLane = -1;
        typed = "";
        showMessage("High talents are full. Press 1/2/3 to replace, Esc to abandon the new talent.",
                "高稀有天赋已满。按 1/2/3 替换，Esc 放弃新天赋。");
    }

    void finishUpgradeCard(UpgradeCard card, boolean spendPendingChoice, boolean closeMenu) {
        if (spendPendingChoice) {
            pendingUpgradeChoices = Math.max(0, pendingUpgradeChoices - 1);
        }
        if (closeMenu) {
            clearUpgradeChoices();
            choiceMode = ChoiceMode.NONE;
        }
        showMessage("Upgrade chosen: " + card.title + ".",
                "已选择升级：" + cardTitle(card) + "。");
    }

    void clearUpgradeChoices() {
        for (int i = 0; i < upgradeChoices.length; i++) {
            upgradeChoices[i] = null;
        }
    }

    void choose(int key) {
        if (choiceMode == ChoiceMode.TEST_BACKEND) {
            chooseTestBackend(key);
            return;
        }
        if (choiceMode == ChoiceMode.HIGH_REPLACE) {
            chooseHighReplacement(key);
            return;
        }
        if (choiceMode != ChoiceMode.UPGRADE) {
            return;
        }
        if (key == KeyEvent.VK_LEFT) {
            selectedUpgradeIndex = (selectedUpgradeIndex + 2) % 3;
        } else if (key == KeyEvent.VK_RIGHT) {
            selectedUpgradeIndex = (selectedUpgradeIndex + 1) % 3;
        } else if (key == KeyEvent.VK_1) {
            selectedUpgradeIndex = 0;
            applyUpgradeCard(upgradeChoices[selectedUpgradeIndex]);
        } else if (key == KeyEvent.VK_2) {
            selectedUpgradeIndex = 1;
            applyUpgradeCard(upgradeChoices[selectedUpgradeIndex]);
        } else if (key == KeyEvent.VK_3) {
            selectedUpgradeIndex = 2;
            applyUpgradeCard(upgradeChoices[selectedUpgradeIndex]);
        } else if (key == KeyEvent.VK_ENTER) {
            applyUpgradeCard(upgradeChoices[selectedUpgradeIndex]);
        }
    }

    void chooseHighReplacement(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            String title = pendingHighTalent == null ? "" : cardTitle(pendingHighTalent);
            pendingHighTalent = null;
            choiceMode = returnToTestBackendAfterHighReplace ? ChoiceMode.TEST_BACKEND : ChoiceMode.NONE;
            returnToTestBackendAfterHighReplace = false;
            showMessage("New high talent abandoned.",
                    title.length() == 0 ? "已放弃新高稀有天赋。" : "已放弃新高稀有天赋：" + title + "。");
            return;
        }
        int slot = -1;
        if (key == KeyEvent.VK_1) {
            slot = 0;
        } else if (key == KeyEvent.VK_2) {
            slot = 1;
        } else if (key == KeyEvent.VK_3) {
            slot = 2;
        }
        if (slot < 0 || pendingHighTalent == null) {
            return;
        }
        UpgradeEffect replaced = highTalents[slot];
        highTalents[slot] = pendingHighTalent.effect;
        UpgradeCard installed = pendingHighTalent;
        pendingHighTalent = null;
        choiceMode = returnToTestBackendAfterHighReplace ? ChoiceMode.TEST_BACKEND : ChoiceMode.NONE;
        returnToTestBackendAfterHighReplace = false;
        showMessage("High talent replaced: " + effectTitleEn(installed.effect)
                        + " over " + effectTitleEn(replaced) + ".",
                "高稀有替换：" + effectTitleZh(installed.effect)
                        + " 替换 " + effectTitleZh(replaced) + "。");
    }

    void openTestBackend() {
        if (gameOver) {
            return;
        }
        if (!started) {
            started = true;
            runStartTick = tick;
            correctTypedChars = 0;
            resetFrameClock();
        }
        paused = false;
        choiceMode = ChoiceMode.TEST_BACKEND;
        selectedTestUpgradeIndex = 0;
        typingLane = -1;
        typed = "";
        clearUpgradeChoices();
        showMessage("Test backend opened. Pick any upgrade with Enter.",
                "测试后台已打开。按 Enter 选择任意升级。");
    }

    void chooseTestBackend(int key) {
        UpgradeEffect[] effects = allTestUpgradeEffects();
        if (key == KeyEvent.VK_ESCAPE) {
            choiceMode = ChoiceMode.NONE;
            showMessage("Test backend closed.", "测试后台已关闭。");
            return;
        }
        if (key == KeyEvent.VK_UP) {
            selectedTestUpgradeIndex = (selectedTestUpgradeIndex + effects.length - 1) % effects.length;
        } else if (key == KeyEvent.VK_DOWN) {
            selectedTestUpgradeIndex = (selectedTestUpgradeIndex + 1) % effects.length;
        } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_PAGE_UP) {
            selectedTestUpgradeIndex = Math.max(0, selectedTestUpgradeIndex - TEST_BACKEND_PAGE_SIZE);
        } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_PAGE_DOWN) {
            selectedTestUpgradeIndex = Math.min(effects.length - 1, selectedTestUpgradeIndex + TEST_BACKEND_PAGE_SIZE);
        } else if (key == KeyEvent.VK_HOME) {
            selectedTestUpgradeIndex = 0;
        } else if (key == KeyEvent.VK_END) {
            selectedTestUpgradeIndex = effects.length - 1;
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
            UpgradeEffect effect = effects[selectedTestUpgradeIndex];
            UpgradeCard card = createCard(effect, rarityForEffect(effect));
            applyUpgradeCard(card, false, false);
            choiceMode = ChoiceMode.TEST_BACKEND;
        }
    }

    UpgradeEffect[] allTestUpgradeEffects() {
        UpgradeEffect[] tools = sudoToolEffects();
        UpgradeEffect[] common = gameplayPoolFor(UpgradeRarity.COMMON);
        UpgradeEffect[] uncommon = gameplayPoolFor(UpgradeRarity.UNCOMMON);
        UpgradeEffect[] high = gameplayPoolFor(UpgradeRarity.HIGH);
        UpgradeEffect[] effects = new UpgradeEffect[tools.length + common.length + uncommon.length + high.length];
        int index = copyEffects(tools, effects, 0);
        index = copyEffects(common, effects, index);
        index = copyEffects(uncommon, effects, index);
        copyEffects(high, effects, index);
        return effects;
    }

    int copyEffects(UpgradeEffect[] source, UpgradeEffect[] target, int startIndex) {
        for (int i = 0; i < source.length; i++) {
            target[startIndex + i] = source[i];
        }
        return startIndex + source.length;
    }

    UpgradeRarity rarityForEffect(UpgradeEffect effect) {
        if (effect == UpgradeEffect.TEST_INVINCIBLE || effect == UpgradeEffect.TEST_BIG_XP) {
            return UpgradeRarity.HIGH;
        }
        if (effect == UpgradeEffect.RHYTHM_CANNON || effect == UpgradeEffect.FROST_FIELD) {
            return UpgradeRarity.HIGH;
        }
        if (effect == UpgradeEffect.PHASE_SWITCH || effect == UpgradeEffect.BOSS_BREAKER
                || effect == UpgradeEffect.CROSSFEED
                || effect == UpgradeEffect.SINGLE_LANE_BASTION
                || effect == UpgradeEffect.ALTERNATING_GUARD
                || effect == UpgradeEffect.PREFIX_ILLUMINATION
                || effect == UpgradeEffect.FIRST_LETTER_TICKET
                || effect == UpgradeEffect.DUAL_PREFIX_SCAN
                || effect == UpgradeEffect.CALM_AFTER_ERROR
                || effect == UpgradeEffect.BACKSPACE_COUNTER
                || effect == UpgradeEffect.DANGER_WORD
                || effect == UpgradeEffect.PRESSURE_VALVE) {
            return UpgradeRarity.UNCOMMON;
        }
        return UpgradeRarity.COMMON;
    }

    int testLevelFor(UpgradeEffect effect) {
        if (effect == UpgradeEffect.TEST_INVINCIBLE) {
            return testInvincible ? 1 : 0;
        }
        if (effect == UpgradeEffect.CALIBRATED_DAMAGE) {
            return damageBonusPercent / 12;
        }
        if (effect == UpgradeEffect.TRIGGER_TUNING) {
            return triggerTuningLevel();
        }
        if (isHighTalentEffect(effect)) {
            return hasHighTalent(effect) ? 1 : 0;
        }
        if (isGroupTwoToFiveEffect(effect)) {
            return effectLevel(effect);
        }
        if (effect == UpgradeEffect.PHASE_SWITCH) {
            return phaseSwitchLevel;
        }
        if (effect == UpgradeEffect.BOSS_BREAKER) {
            return bossBreakerLevel;
        }
        if (effect == UpgradeEffect.CROSSFEED) {
            return crossfeedLevel;
        }
        if (effect == UpgradeEffect.LONG_WORD_REWARD) {
            return longWordRewardLevel;
        }
        if (effect == UpgradeEffect.SHORT_WORD_QUICKSHOT) {
            return shortWordQuickshotLevel;
        }
        if (effect == UpgradeEffect.FINAL_LETTER_BURST) {
            return finalLetterBurstLevel;
        }
        if (effect == UpgradeEffect.VOWEL_CONVERGENCE) {
            return vowelConvergenceLevel;
        }
        if (effect == UpgradeEffect.HARD_CONSONANT_BREAK) {
            return hardConsonantBreakLevel;
        }
        return 0;
    }

    void handleLetter(char raw) {
        char c = Character.toLowerCase(raw);
        if (c < 'a' || c > 'z') {
            return;
        }
        if (choiceMode != ChoiceMode.NONE) {
            return;
        }
        if (handleSudoCommand(c)) {
            return;
        }
        if (!started || paused || gameOver || pendingLaneAttack) {
            return;
        }

        String candidate = typed + c;
        int nextTypingLane = resolveTypingLane(candidate);
        if (nextTypingLane >= 0) {
            acceptTyping(candidate, nextTypingLane);
        } else {
            boolean hadProgress = typed.length() > 0;
            int failedLane = typingLane;
            resetTypingProgress();
            recentErrorTicks = 90;
            nextWordStartsAfterError = true;
            postErrorCleanStreak = 0;
            int restartLane = resolveTypingLane(String.valueOf(c));
            if (restartLane >= 0) {
                acceptTyping(String.valueOf(c), restartLane);
                if (!hadProgress) {
                    return;
                }
            }
            wrongFlashLane = failedLane;
            combo = 0;
            wrongFlashTicks = 12;
            setDeathReason("typing errors broke your output", "连续输入失误打断了输出");
        }
    }

    boolean handleSudoCommand(char c) {
        sudoBuffer += c;
        if (sudoBuffer.length() > 4) {
            sudoBuffer = sudoBuffer.substring(sudoBuffer.length() - 4);
        }
        if ("sudo".equals(sudoBuffer) && choiceMode == ChoiceMode.NONE && !gameOver) {
            sudoBuffer = "";
            openTestBackend();
            return true;
        }
        return false;
    }

    void acceptTyping(String candidate, int nextTypingLane) {
        if (typed.length() == 0) {
            startCurrentWord(candidate, nextTypingLane);
        } else {
            updateCurrentWordState(candidate, nextTypingLane);
        }
        typingLane = nextTypingLane;
        typed = candidate;
        inputPulseTicks = 6;
        if (typed.equals(laneWords[typingLane])) {
            int previousLane = lane;
            int completedLane = typingLane;
            String completedWord = laneWords[typingLane];
            recordCompletedWordContext(previousLane, completedLane, completedWord);
            if (completedLane != lane) {
                triggerPhaseSwitch();
                startLaneSwitchAnimation(previousLane, completedLane);
            }
            lane = completedLane;
            lastCompletedWord = completedWord;
            correctTypedChars += typed.length();
            queueOrCompleteLaneWord(previousLane, completedLane, completedWord);
            completePulseLane = completedLane;
            completePulseTicks = WORD_COMPLETE_PULSE_TICKS;
            refreshLaneWord(completedLane);
            resetTypingProgress();
        }
    }

    void queueOrCompleteLaneWord(int previousLane, int completedLane, String completedWord) {
        if (previousLane == completedLane || laneSwitchAnimationTicks <= 0) {
            completeLaneWord(completedLane, completedWord);
            return;
        }
        pendingLaneAttack = true;
        pendingAttackLane = completedLane;
        pendingAttackWord = completedWord;
    }

    void finishPendingLaneAttack() {
        int attackLane = pendingAttackLane;
        String completedWord = pendingAttackWord;
        pendingLaneAttack = false;
        pendingAttackWord = "";
        completeLaneWord(attackLane, completedWord);
    }

    void startCurrentWord(String candidate, int laneIndex) {
        currentWordUsedBackspace = false;
        currentWordStartedAfterError = recentErrorTicks > 0 || nextWordStartsAfterError;
        currentWordUniqueFirst = false;
        currentWordCleanPrefix3 = false;
        currentWordReachedHalfPrefix = false;
        currentWordHadDualPrefix = false;
        nextWordStartsAfterError = false;
        updateCurrentWordState(candidate, laneIndex);
    }

    void updateCurrentWordState(String candidate, int laneIndex) {
        if (candidate.length() == 0) {
            return;
        }
        currentWordUniqueFirst = isUniqueLaneFirst(candidate.charAt(0));
        if (!currentWordUsedBackspace && candidate.length() >= 3) {
            currentWordCleanPrefix3 = true;
        }
        int halfLength = Math.max(2, (laneWords[laneIndex].length() + 1) / 2);
        if (!currentWordUsedBackspace && candidate.length() >= halfLength) {
            currentWordReachedHalfPrefix = true;
        }
        if (isDualPrefixCandidate(candidate, laneIndex)) {
            currentWordHadDualPrefix = true;
        }
    }

    void resetTypingProgress() {
        typed = "";
        typingLane = -1;
        currentWordUsedBackspace = false;
        currentWordStartedAfterError = false;
        currentWordUniqueFirst = false;
        currentWordCleanPrefix3 = false;
        currentWordReachedHalfPrefix = false;
        currentWordHadDualPrefix = false;
    }

    void recordCompletedWordContext(int previousLane, int completedLane, String completedWord) {
        completedWordWasClean = !currentWordUsedBackspace;
        completedWordUsedBackspace = currentWordUsedBackspace;
        completedWordStartedAfterError = currentWordStartedAfterError;
        completedWordUniqueFirst = currentWordUniqueFirst;
        completedWordCleanPrefix3 = currentWordCleanPrefix3 && completedWordWasClean;
        completedWordReachedHalfPrefix = currentWordReachedHalfPrefix && completedWordWasClean;
        completedWordHadDualPrefix = currentWordHadDualPrefix;
        completedWordSwitchedLane = previousLane != completedLane;
        completedWordAlternated = lastCompletedLane >= 0 && lastCompletedLane != completedLane;
        sameLaneStreak = lastCompletedLane == completedLane ? sameLaneStreak + 1 : 1;
        alternatingLaneStreak = completedWordAlternated ? alternatingLaneStreak + 1 : 1;
        char first = Character.toLowerCase(completedWord.charAt(0));
        sameFirstLetterStreak = first == lastCompletedFirstChar ? sameFirstLetterStreak + 1 : 1;
        lastCompletedFirstChar = first;
        lastCompletedLane = completedLane;
    }

    boolean isUniqueLaneFirst(char c) {
        int matches = 0;
        char lower = Character.toLowerCase(c);
        for (String word : laneWords) {
            if (word != null && word.length() > 0 && word.charAt(0) == lower) {
                matches++;
            }
        }
        return matches == 1;
    }

    boolean isDualPrefixCandidate(String candidate, int laneIndex) {
        if (candidate.length() != 1 || laneWords[1 - laneIndex] == null
                || laneWords[1 - laneIndex].length() == 0) {
            return false;
        }
        int group = openingLetterGroup(candidate.charAt(0));
        return group > 0 && group == openingLetterGroup(laneWords[1 - laneIndex].charAt(0));
    }

    int openingLetterGroup(char c) {
        char lower = Character.toLowerCase(c);
        if (lower == 'f' || lower == 't' || lower == 'k') {
            return 1;
        }
        if (lower == 'a' || lower == 'e' || lower == 'i' || lower == 'o' || lower == 'u') {
            return 2;
        }
        if (lower == 'c' || lower == 's' || lower == 'p') {
            return 3;
        }
        return 0;
    }

    int resolveTypingLane(String candidate) {
        if (typingLane >= 0 && laneWords[typingLane].startsWith(candidate)) {
            return typingLane;
        }
        if (laneWords[lane].startsWith(candidate)) {
            return lane;
        }
        int otherLane = 1 - lane;
        if (laneWords[otherLane].startsWith(candidate)) {
            return otherLane;
        }
        return -1;
    }

    void completeLaneWord(int attackLane, String completedWord) {
        combo++;
        bestCombo = Math.max(bestCombo, combo);

        Target target = nearestTargetInLane(attackLane);
        int damage = baseDamage + combo / 8 + perfectBonus / 2
                + groupOneDamageBonus(completedWord, target)
                + groupTwoToFiveDamageBonus(completedWord, target);
        damage = scaledDamage(damage, target);
        fireSingleShotVolley(attackLane, damage, 1, BulletKind.BASIC);
        applyHighTalentAfterAttack(completedWord, attackLane, target);
        applyGroupOneAfterAttack(completedWord, attackLane, target);
        applyGroupTwoToFiveAfterAttack(completedWord, attackLane, target);
    }

    void applyHighTalentAfterAttack(String completedWord, int attackLane, Target target) {
        if (hasHighTalent(UpgradeEffect.RHYTHM_CANNON)) {
            continuousSurgeTicks = AUTO_CANNON_SURGE_TICKS;
            autoFireCooldown = 0;
            showMessage("Autocannon overcharged by typing.",
                    "自动炮已由输入强化。");
        }
        if (hasHighTalent(UpgradeEffect.FROST_FIELD)) {
            int pierceDamage = scaledDamage(4 + combo / 10
                    + groupOneDamageBonus(completedWord, target)
                    + groupTwoToFiveDamageBonus(completedWord, target), target);
            fireBullet(attackLane, pierceDamage, 5, BulletKind.PIERCE);
        }
    }

    int groupOneDamageBonus(String word, Target target) {
        if (!ENABLE_GROUP_ONE_UPGRADES || hardConsonantBreakLevel <= 0 || word == null
                || !endsWithHardConsonant(word) || target == null || !isThickTarget(target)) {
            return 0;
        }
        return 2 * hardConsonantBreakLevel;
    }

    void applyGroupOneAfterAttack(String word, int attackLane, Target target) {
        if (!ENABLE_GROUP_ONE_UPGRADES || word == null || word.length() == 0) {
            return;
        }
        if (longWordRewardLevel > 0 && isLongLaneWord(word)) {
            int beforeHp = hp;
            hp = Math.min(maxHp, hp + Math.min(3, longWordRewardLevel));
            if (hp > beforeHp) {
                impacts.add(new Impact(PLAYER_X, LANE_Y[attackLane], 0));
            }
        }
        if (shortWordQuickshotLevel > 0 && isShortLaneWord(word)) {
            int quickDamage = scaledDamage(1 + shortWordQuickshotLevel / 2, target);
            fireBullet(attackLane, quickDamage, 1, BulletKind.CONTINUOUS);
        }
        if (finalLetterBurstLevel > 0) {
            int burstDamage = scaledDamage(1 + finalLetterBurstLevel, target);
            fireBullet(attackLane, burstDamage, 1, BulletKind.BURST);
        }
        if (vowelConvergenceLevel > 0 && endsWithVowel(word)) {
            pullNearestXpOrb(attackLane);
        }
    }

    int groupTwoToFiveDamageBonus(String word, Target target) {
        if (!ENABLE_GROUP_TWO_TO_FIVE_UPGRADES || word == null || word.length() == 0) {
            return 0;
        }
        int bonus = 0;
        int level = effectLevel(UpgradeEffect.SAME_LANE_SUPPRESSION);
        if (level > 0 && sameLaneStreak >= 2) {
            bonus += Math.min(4 + level, level + sameLaneStreak / 2);
        }
        level = effectLevel(UpgradeEffect.FIRST_LETTER_LOCK);
        if (level > 0 && completedWordUniqueFirst) {
            bonus += level;
        }
        level = effectLevel(UpgradeEffect.ACCURATE_OPENER);
        if (level > 0 && completedWordCleanPrefix3) {
            bonus += 1 + level / 2;
        }
        level = effectLevel(UpgradeEffect.PREFIX_ILLUMINATION);
        if (level > 0 && completedWordReachedHalfPrefix) {
            bonus += 1 + level;
        }
        level = effectLevel(UpgradeEffect.BACKSPACE_FIX);
        if (level > 0 && completedWordUsedBackspace) {
            bonus += 2 * level;
        }
        level = effectLevel(UpgradeEffect.LONG_WORD_FOCUS);
        if (level > 0 && completedWordWasClean && isLongLaneWord(word)) {
            bonus += level;
        }
        level = effectLevel(UpgradeEffect.DANGER_WORD);
        if (level > 0 && isDangerClose(target)) {
            bonus += 2 * level;
        }
        return bonus;
    }

    void applyGroupTwoToFiveAfterAttack(String word, int attackLane, Target target) {
        if (!ENABLE_GROUP_TWO_TO_FIVE_UPGRADES || word == null || word.length() == 0) {
            return;
        }
        int level = effectLevel(UpgradeEffect.SAME_LANE_SUPPRESSION);
        if (level > 0 && sameLaneStreak >= 2 && target != null) {
            target.slowTicks = Math.max(target.slowTicks, 34 + level * 12);
        }

        level = effectLevel(UpgradeEffect.LANE_SWAP_BEAT);
        if (level > 0 && completedWordAlternated) {
            int splitDamage = scaledDamage(1 + level / 2, target);
            fireBullet(attackLane, splitDamage, 1, BulletKind.CONTINUOUS);
            fireBullet(1 - attackLane, splitDamage, 1, BulletKind.CONTINUOUS);
        }

        level = effectLevel(UpgradeEffect.SINGLE_LANE_BASTION);
        if (level > 0 && sameLaneStreak > 0 && sameLaneStreak % 4 == 0) {
            laneBarrierCharges[attackLane] = Math.min(3, laneBarrierCharges[attackLane] + 1);
            impacts.add(new Impact(PLAYER_X, LANE_Y[attackLane], 0));
        }

        level = effectLevel(UpgradeEffect.ALTERNATING_GUARD);
        if (level > 0 && alternatingLaneStreak > 0 && alternatingLaneStreak % 4 == 0) {
            for (int i = 0; i < laneBarrierCharges.length; i++) {
                laneBarrierCharges[i] = Math.min(3, laneBarrierCharges[i] + 1);
            }
            impacts.add(new Impact(PLAYER_X, LANE_Y[attackLane], 0));
        }

        level = effectLevel(UpgradeEffect.COMBO_CALIBRATOR);
        if (level > 0 && combo >= 20 && combo % 20 == 0) {
            fireBullet(attackLane, scaledDamage(3 + level, target), 1, BulletKind.BASIC);
        }

        level = effectLevel(UpgradeEffect.PREFIX_ILLUMINATION);
        if (level > 0 && completedWordReachedHalfPrefix && target != null) {
            target.markTicks = Math.max(target.markTicks, 80 + level * 20);
            target.hitFlash = Math.max(target.hitFlash, 4);
        }

        level = effectLevel(UpgradeEffect.FIRST_LETTER_TICKET);
        if (level > 0 && sameFirstLetterStreak >= 3 && sameFirstLetterStreak % 3 == 0) {
            xpOrbs.add(new XpOrb(PLAYER_X + 170, attackLane, 1 + level));
        }

        level = effectLevel(UpgradeEffect.DUAL_PREFIX_SCAN);
        if (level > 0 && completedWordHadDualPrefix) {
            fireBullet(1 - attackLane, scaledDamage(2 + level, null), 1, BulletKind.CONTINUOUS);
        }

        level = effectLevel(UpgradeEffect.CLEAN_FINISH);
        if (level > 0 && completedWordWasClean) {
            fireBullet(attackLane, scaledDamage(1 + level / 2, target), 1, BulletKind.CONTINUOUS);
        }

        level = effectLevel(UpgradeEffect.ERROR_RESET);
        if (level > 0 && completedWordStartedAfterError) {
            fireBullet(attackLane, scaledDamage(2 + level, target), 1, BulletKind.BURST);
            recentErrorTicks = 0;
        }

        level = effectLevel(UpgradeEffect.CALM_AFTER_ERROR);
        if (level > 0) {
            updateCalmAfterError(level);
        }

        level = effectLevel(UpgradeEffect.BACKSPACE_COUNTER);
        if (level > 0 && completedWordUsedBackspace) {
            fireBullet(attackLane, scaledDamage(2 + level, target), 1, BulletKind.BURST);
        }

        level = effectLevel(UpgradeEffect.PRECISE_PICKUP);
        if (level > 0 && hasNearbyLaneXp(attackLane)) {
            pullNearestXpOrb(attackLane, 46.0 + level * 18.0);
        }

        level = effectLevel(UpgradeEffect.FINAL_LETTER_PULL);
        if (level > 0 && endsWithVowel(word)) {
            pullNearestXpOrb(attackLane, 34.0 + level * 14.0);
            if (target != null) {
                target.slowTicks = Math.max(target.slowTicks, 24 + level * 10);
            }
        }

        level = effectLevel(UpgradeEffect.LONG_WORD_FOCUS);
        if (level > 0 && completedWordWasClean && isLongLaneWord(word)) {
            longFocusTicks = Math.max(longFocusTicks, 130 + level * 28);
            impacts.add(new Impact(PLAYER_X + 8, LANE_Y[attackLane], 0));
        }

        level = effectLevel(UpgradeEffect.DANGER_WORD);
        if (level > 0 && isDangerClose(target)) {
            target.x += 26 + level * 10;
            target.previousX = Math.min(target.previousX, target.x);
            impacts.add(new Impact(target.x, LANE_Y[attackLane], 0));
        }

        level = effectLevel(UpgradeEffect.PRESSURE_VALVE);
        if (level > 0 && laneHasPressure(attackLane)) {
            laneSlowTicks[attackLane] = Math.max(laneSlowTicks[attackLane], 78 + level * 18);
            impacts.add(new Impact(PLAYER_X + 40, LANE_Y[attackLane], 0));
        }
    }

    void updateCalmAfterError(int level) {
        if (!completedWordWasClean) {
            postErrorCleanStreak = 0;
            return;
        }
        if (completedWordStartedAfterError || postErrorCleanStreak > 0) {
            postErrorCleanStreak++;
            if (postErrorCleanStreak >= 2) {
                calmGuardCharges = Math.min(3, calmGuardCharges + 1);
                postErrorCleanStreak = 0;
                impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0));
                showMessage("Calm guard prepared.", "冷静守护已就绪。");
            }
        }
    }

    void pullNearestXpOrb(int laneIndex) {
        pullNearestXpOrb(laneIndex, 20.0 + vowelConvergenceLevel * 8.0);
    }

    void pullNearestXpOrb(int laneIndex, double maxStep) {
        XpOrb best = null;
        double bestDistance = Double.MAX_VALUE;
        for (XpOrb orb : xpOrbs) {
            if (orb.lane != laneIndex) {
                continue;
            }
            double distance = Math.hypot(orb.x - PLAYER_X, orb.y - LANE_Y[laneIndex]);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = orb;
            }
        }
        if (best == null) {
            return;
        }
        best.attracted = true;
        double dx = PLAYER_X - best.x;
        double dy = LANE_Y[laneIndex] - best.y;
        double distance = Math.max(1.0, Math.hypot(dx, dy));
        double step = Math.min(distance, maxStep);
        best.x += dx / distance * step;
        best.y += dy / distance * step;
        impacts.add(new Impact(best.x, best.y, 0));
    }

    boolean hasNearbyLaneXp(int laneIndex) {
        for (XpOrb orb : xpOrbs) {
            if (orb.lane == laneIndex && Math.abs(orb.x - PLAYER_X) < 420) {
                return true;
            }
        }
        return false;
    }

    boolean isDangerClose(Target target) {
        return target != null && target.x <= PLAYER_X + 260;
    }

    boolean laneHasPressure(int laneIndex) {
        for (Target target : targets) {
            if (target.lane == laneIndex && !target.dead && target.hp > 0
                    && target.x <= PLAYER_X + 320) {
                return true;
            }
        }
        return false;
    }

    boolean isShortLaneWord(String word) {
        return word.length() == 4;
    }

    boolean isLongLaneWord(String word) {
        return word.length() >= 6 && word.length() <= 7;
    }

    boolean endsWithVowel(String word) {
        char c = Character.toLowerCase(word.charAt(word.length() - 1));
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    boolean endsWithHardConsonant(String word) {
        char c = Character.toLowerCase(word.charAt(word.length() - 1));
        return c == 't' || c == 'k' || c == 'p' || c == 'd' || c == 'g';
    }

    boolean isThickTarget(Target target) {
        return target.kind == TargetKind.TANK || target.kind == TargetKind.SWITCHER
                || target.kind == TargetKind.BOSS || target.maxHp >= scaleEnemyHp(7);
    }

    int scaledDamage(int damage, Target target) {
        int percent = damageBonusPercent;
        if (bossBreakerLevel > 0 && target != null) {
            if (target.kind == TargetKind.BOSS) {
                percent += bossBreakerLevel * 15;
            } else if (target.kind == TargetKind.TANK || target.kind == TargetKind.SWITCHER) {
                percent += bossBreakerLevel * 10;
            }
        }
        if (longFocusTicks > 0) {
            percent += effectLevel(UpgradeEffect.LONG_WORD_FOCUS) * 8;
        }
        return Math.max(1, damage + damage * percent / 100);
    }

    int triggerTuningLevel() {
        return fireRateBonusPercent / TRIGGER_TUNING_STEP_PERCENT;
    }

    int singleShotPelletCount() {
        return 1 + triggerTuningLevel();
    }

    int triggerTunedTotalDamage(int baseTotalDamage) {
        if (fireRateBonusPercent <= 0) {
            return baseTotalDamage;
        }
        int numerator = baseTotalDamage * (100 + fireRateBonusPercent);
        return Math.max(singleShotPelletCount(), (numerator + 99) / 100);
    }

    void fireSingleShotVolley(int attackLane, int baseTotalDamage, int pierceLeft, BulletKind kind) {
        int pellets = singleShotPelletCount();
        int totalDamage = triggerTunedTotalDamage(baseTotalDamage);
        int basePelletDamage = totalDamage / pellets;
        int remainder = totalDamage % pellets;
        for (int i = 0; i < pellets; i++) {
            int pelletDamage = basePelletDamage + (i < remainder ? 1 : 0);
            fireBullet(attackLane, pelletDamage, pierceLeft, kind, -i * 11.0);
        }
    }

    void triggerPhaseSwitch() {
        if (phaseSwitchLevel <= 0 || phaseSwitchCooldownTicks > 0) {
            return;
        }
        phaseSwitchTicks = PHASE_SWITCH_TICKS;
        phaseSwitchCooldownTicks = Math.max(PHASE_SWITCH_MIN_COOLDOWN,
                PHASE_SWITCH_BASE_COOLDOWN - (phaseSwitchLevel - 1) * PHASE_SWITCH_COOLDOWN_STEP);
    }

    void startLaneSwitchAnimation(int fromLane, int toLane) {
        if (fromLane == toLane) {
            return;
        }
        laneSwitchFromY = playerRenderY();
        laneSwitchToY = LANE_Y[toLane];
        laneSwitchAnimationTicks = LANE_SWITCH_ANIMATION_TICKS;
    }

    double playerRenderY() {
        if (laneSwitchAnimationTicks <= 0) {
            return LANE_Y[lane];
        }
        double age = LANE_SWITCH_ANIMATION_TICKS - laneSwitchAnimationTicks + renderAlpha;
        double t = laneSwitchEase(age / LANE_SWITCH_ANIMATION_TICKS);
        return laneSwitchFromY + (laneSwitchToY - laneSwitchFromY) * t;
    }

    void fireBullet(int attackLane, int damage, int pierceLeft, BulletKind kind) {
        fireBullet(attackLane, damage, pierceLeft, kind, 0);
    }

    void fireBullet(int attackLane, int damage, int pierceLeft, BulletKind kind, double startOffset) {
        bullets.add(new Bullet(PLAYER_X + 28 + startOffset, attackLane, damage, pierceLeft, kind));
    }

    Target nearestTargetInLane(int laneIndex) {
        Target best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Target target : targets) {
            if (target.lane == laneIndex && !target.dead && target.hp > 0 && target.x > PLAYER_X - 10) {
                double distance = target.x - PLAYER_X;
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = target;
                }
            }
        }
        return best;
    }

    boolean hasBoss() {
        for (Target target : targets) {
            if (target.kind == TargetKind.BOSS && !target.dead) {
                return true;
            }
        }
        return false;
    }

    String nextWord(int min, int max) {
        for (int tries = 0; tries < 30; tries++) {
            String word = WORDS[random.nextInt(WORDS.length)];
            if (word.length() >= min && word.length() <= max) {
                return word;
            }
        }
        return WORDS[random.nextInt(WORDS.length)];
    }

    String nextLaneWord(int min, int max, String previousWord, String otherWord) {
        for (int tries = 0; tries < 80; tries++) {
            String word = nextWord(min, max);
            if (isAllowedLaneWord(word, previousWord, otherWord)) {
                return word;
            }
        }
        for (String word : WORDS) {
            if (word.length() >= min && word.length() <= max
                    && isAllowedLaneWord(word, previousWord, otherWord)) {
                return word;
            }
        }
        for (String word : WORDS) {
            if (word.length() >= min && word.length() <= max
                    && !word.equals(previousWord)
                    && !word.equals(otherWord)) {
                return word;
            }
        }
        return nextWord(min, max);
    }

    boolean isAllowedLaneWord(String word, String previousWord, String otherWord) {
        if (word == null || word.equals(previousWord) || word.equals(otherWord)) {
            return false;
        }
        return otherWord == null || word.charAt(0) != otherWord.charAt(0);
    }

    void refreshLaneWord(int laneIndex) {
        String otherWord = laneWords[1 - laneIndex];
        String previousWord = laneWords[laneIndex];
        laneWords[laneIndex] = nextLaneWord(LANE_WORD_MIN_LENGTH, LANE_WORD_MAX_LENGTH, previousWord, otherWord);
    }

    String laneName(int laneIndex) {
        return laneIndex == 0 ? "UP" : "DOWN";
    }

    String laneDisplayName(int laneIndex) {
        if (!isChinese()) {
            return laneName(laneIndex);
        }
        return laneIndex == 0 ? "上路" : "下路";
    }

    int currentTypingSpeedWpm() {
        int elapsedTicks = Math.max(1, tick - runStartTick);
        double elapsedMinutes = elapsedTicks * TICK_MS / 60000.0;
        return (int) Math.round((correctTypedChars / 5.0) / elapsedMinutes);
    }

    boolean isChinese() {
        return language == Language.CHINESE;
    }

    String ui(String english, String chinese) {
        return isChinese() ? chinese : english;
    }

    Font uiFont(Font english, Font chinese) {
        return isChinese() ? chinese : english;
    }

    String currentMessage() {
        return isChinese() ? messageZh : message;
    }

    String currentDeathReason() {
        return isChinese() ? deathReasonZh : deathReason;
    }

    void setDeathReason(String english, String chinese) {
        deathReason = english;
        deathReasonZh = chinese;
    }

    void showMessage(String nextMessage) {
        showMessage(nextMessage, nextMessage);
    }

    void showMessage(String nextMessage, String nextMessageZh) {
        message = nextMessage;
        messageZh = nextMessageZh;
        messageTicks = 170;
    }

    void toggleLanguage() {
        language = isChinese() ? Language.ENGLISH : Language.CHINESE;
        if (isChinese()) {
            showMessage("Language switched to Chinese.", "已切换为中文界面。");
        } else {
            showMessage("Language switched to English.", "已切换为英文界面。");
        }
    }

    void endGame() {
        gameOver = true;
        highScore = Math.max(highScore, score);
        timer.stop();
        if (deathReason.length() == 0) {
            setDeathReason("health reached zero", "HP 归零");
        }
    }

    public void keyTyped(KeyEvent event) {
        // Letter input is handled from keyPressed key codes so IME composition does not affect gameplay.
    }

    public void keyPressed(KeyEvent event) {
        int key = event.getKeyCode();
        if (key == KeyEvent.VK_F11) {
            toggleFullscreen();
            return;
        }
        if (key == KeyEvent.VK_F2) {
            toggleLanguage();
            repaint();
            return;
        }
        if (!started) {
            if (key == KeyEvent.VK_1) {
                difficulty = Difficulty.EASY;
            } else if (key == KeyEvent.VK_2) {
                difficulty = Difficulty.NORMAL;
            } else if (key == KeyEvent.VK_3) {
                difficulty = Difficulty.HARD;
            } else if (key == KeyEvent.VK_ENTER) {
                started = true;
                resetFrameClock();
                runStartTick = tick;
                correctTypedChars = 0;
            }
            repaint();
            return;
        }
        if (choiceMode != ChoiceMode.NONE) {
            choose(key);
            repaint();
            return;
        }
        if (gameOver && key == KeyEvent.VK_ENTER) {
            reset();
            return;
        }
        if (paused && key == KeyEvent.VK_F5) {
            reset();
            return;
        }
        if (!gameOver && key == KeyEvent.VK_ESCAPE) {
            paused = !paused;
            resetFrameClock();
            repaint();
            return;
        }
        if (paused) {
            repaint();
            return;
        }
        if (key == KeyEvent.VK_BACK_SPACE && typed.length() > 0) {
            currentWordUsedBackspace = true;
            typed = typed.substring(0, typed.length() - 1);
            if (typed.length() == 0) {
                typingLane = -1;
            }
        } else if (key == KeyEvent.VK_SPACE) {
            if (pendingUpgradeChoices > 0) {
                openUpgradeMenu();
            }
        } else {
            char letter = keyboardLetterForKeyCode(key);
            if (letter != 0) {
                handleLetter(letter);
            }
        }
        repaint();
    }

    char keyboardLetterForKeyCode(int keyCode) {
        if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
            return (char) ('a' + keyCode - KeyEvent.VK_A);
        }
        return 0;
    }

    void setFullscreenState(boolean nextFullscreen) {
        fullscreen = nextFullscreen;
    }

    void toggleFullscreen() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) {
            return;
        }
        if (!fullscreen) {
            windowedBounds = frame.getBounds();
            frame.dispose();
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
            fullscreen = true;
        } else {
            frame.dispose();
            frame.setUndecorated(false);
            frame.setExtendedState(JFrame.NORMAL);
            if (windowedBounds != null) {
                frame.setBounds(windowedBounds);
            } else {
                frame.pack();
                frame.setLocationRelativeTo(null);
            }
            frame.setVisible(true);
            fullscreen = false;
        }
        resetFrameClock();
        revalidate();
        repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                requestFocusInWindow();
            }
        });
    }

    public void keyReleased(KeyEvent event) {
        // Not used.
    }

    void reset() {
        targets.clear();
        bullets.clear();
        impacts.clear();
        breakParticles.clear();
        xpOrbs.clear();
        clearUpgradeChoices();
        lane = 0;
        hp = PLAYER_BASE_HP;
        maxHp = PLAYER_BASE_HP;
        score = 0;
        kills = 0;
        bossLevel = 0;
        combo = 0;
        bestCombo = 0;
        tick = 0;
        spawnCooldown = 25;
        bossCooldownKills = 10;
        autoFireCooldown = 0;
        continuousSurgeTicks = 0;
        wrongFlashTicks = 0;
        wrongFlashLane = -1;
        inputPulseTicks = 0;
        completePulseTicks = 0;
        completePulseLane = -1;
        baseDamage = 5;
        bossDamageBonus = 0;
        perfectBonus = 0;
        correctTypedChars = 0;
        runStartTick = tick;
        xp = 0;
        xpToNext = INITIAL_XP_TO_NEXT;
        upgradeLevel = 0;
        pendingUpgradeChoices = 0;
        selectedUpgradeIndex = 0;
        damageBonusPercent = 0;
        fireRateBonusPercent = 0;
        maxHpUpgradeBonus = 0;
        phaseSwitchLevel = 0;
        phaseSwitchTicks = 0;
        phaseSwitchCooldownTicks = 0;
        bossBreakerLevel = 0;
        crossfeedLevel = 0;
        crossfeedCooldownTicks = 0;
        longWordRewardLevel = 0;
        shortWordQuickshotLevel = 0;
        finalLetterBurstLevel = 0;
        vowelConvergenceLevel = 0;
        hardConsonantBreakLevel = 0;
        selectedTestUpgradeIndex = 0;
        for (int i = 0; i < effectLevels.length; i++) {
            effectLevels[i] = 0;
        }
        for (int i = 0; i < laneBarrierCharges.length; i++) {
            laneBarrierCharges[i] = 0;
            laneSlowTicks[i] = 0;
        }
        for (int i = 0; i < highTalents.length; i++) {
            highTalents[i] = null;
        }
        pendingHighTalent = null;
        returnToTestBackendAfterHighReplace = false;
        laneSwitchFromY = LANE_Y[0];
        laneSwitchToY = LANE_Y[0];
        laneSwitchAnimationTicks = 0;
        pendingLaneAttack = false;
        pendingAttackLane = 0;
        pendingAttackWord = "";
        recentErrorTicks = 0;
        longFocusTicks = 0;
        calmGuardCharges = 0;
        lastCompletedLane = -1;
        sameLaneStreak = 0;
        alternatingLaneStreak = 0;
        lastCompletedFirstChar = 0;
        sameFirstLetterStreak = 0;
        postErrorCleanStreak = 0;
        nextWordStartsAfterError = false;
        resetTypingProgress();
        completedWordWasClean = false;
        completedWordUsedBackspace = false;
        completedWordStartedAfterError = false;
        completedWordUniqueFirst = false;
        completedWordCleanPrefix3 = false;
        completedWordReachedHalfPrefix = false;
        completedWordHadDualPrefix = false;
        completedWordAlternated = false;
        completedWordSwitchedLane = false;
        lastCompletedWord = "";
        deathReason = "";
        deathReasonZh = "";
        sudoBuffer = "";
        refreshLaneWord(0);
        refreshLaneWord(1);
        choiceMode = ChoiceMode.NONE;
        started = true;
        paused = false;
        gameOver = false;
        testInvincible = false;
        resetFrameClock();
        showMessage("Restarted. Type a lane word to enter that lane and attack.",
                "已重新开始。输入 lane 单词进入对应路线并攻击。");
        if (!smokeMode) {
            timer.start();
        }
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D root = (Graphics2D) graphics;
        root.setColor(COLOR_BACKGROUND);
        root.fillRect(0, 0, getWidth(), getHeight());

        double scale = Math.min(getWidth() / (double) WIDTH, getHeight() / (double) HEIGHT);
        int scaledWidth = (int) Math.round(WIDTH * scale);
        int scaledHeight = (int) Math.round(HEIGHT * scale);
        int offsetX = (getWidth() - scaledWidth) / 2;
        int offsetY = (getHeight() - scaledHeight) / 2;

        BufferedImage buffer = logicalFrameBuffer();
        Graphics2D g = buffer.createGraphics();
        g.setClip(0, 0, WIDTH, HEIGHT);
        g.setColor(COLOR_BACKGROUND);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderGame(g);
        g.dispose();

        root.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        root.drawImage(buffer, offsetX, offsetY, scaledWidth, scaledHeight, null);
    }

    BufferedImage logicalFrameBuffer() {
        if (frameBuffer == null || frameBuffer.getWidth() != WIDTH || frameBuffer.getHeight() != HEIGHT) {
            frameBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        }
        return frameBuffer;
    }

    void renderGame(Graphics2D g) {
        drawNeonBackdrop(g);
        drawLanes(g);
        drawXpOrbs(g);
        drawTargets(g);
        drawBullets(g);
        drawBreakParticles(g);
        drawImpacts(g);
        drawPlayer(g);
        drawHud(g);
        drawLaneWordsPanel(g);
        if (!started) {
            drawStartOverlay(g);
        }
        if (paused) {
            drawPauseOverlay(g);
        }
        if (choiceMode != ChoiceMode.NONE) {
            drawChoiceOverlay(g);
        }
        if (gameOver) {
            drawGameOver(g);
        }
    }

    double pulse(double speed, double offset) {
        double value = 0.5 + 0.5 * Math.sin((tick + offset) * speed);
        return smoothstep(value);
    }

    double renderX(double previous, double current) {
        return previous + (current - previous) * renderAlpha;
    }

    double renderY(double previous, double current) {
        return previous + (current - previous) * renderAlpha;
    }

    static double smoothstep(double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        return clamped * clamped * (3.0 - 2.0 * clamped);
    }

    static double easeOutCubic(double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        double inverse = 1.0 - clamped;
        return 1.0 - inverse * inverse * inverse;
    }

    static double laneSwitchEase(double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        double eased = smoothstep(clamped);
        double settle = Math.sin(clamped * Math.PI) * 0.08 * (1.0 - clamped);
        return Math.max(0.0, Math.min(1.0, eased + settle));
    }

    static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    void drawNeonBackdrop(Graphics2D g) {
        int phase = (tick / 2) % 80;
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(COLOR_GRID_MINOR);
        for (int x = -80 + phase; x < WIDTH + 90; x += 80) {
            g.drawLine(x, 108, x + 110, HEIGHT - 60);
        }
        g.setColor(COLOR_GRID_MAJOR);
        for (int y = 130; y < HEIGHT - 70; y += 54) {
            int drift = (int) (Math.sin((tick + y) * 0.018) * 10);
            g.drawLine(42, y + drift, WIDTH - 42, y - drift / 2);
        }
        int scanY = 118 + (tick * 3 % (HEIGHT - 180));
        g.setColor(COLOR_SCANLINE);
        g.fillRect(55, scanY, WIDTH - 110, 2);
    }

    void drawLanes(Graphics2D g) {
        g.setStroke(STROKE_LANE);
        for (int i = 0; i < 2; i++) {
            int glow = i == lane ? 16 + (int) (pulse(0.12, i * 23) * 10) : 8;
            g.setColor(COLOR_LANE_GLOW);
            g.fillRoundRect(LANE_LEFT_X - glow / 2, LANE_Y[i] - 58 - glow / 2,
                    LANE_RIGHT_X - LANE_LEFT_X + glow, 116 + glow, 8, 8);
            g.setColor(i == lane ? COLOR_LANE_ACTIVE : COLOR_LANE_IDLE);
            g.drawLine(LANE_LEFT_X, LANE_Y[i], LANE_RIGHT_X, LANE_Y[i]);
            g.setColor(COLOR_LANE_FILL);
            g.fillRoundRect(LANE_LEFT_X, LANE_Y[i] - 58,
                    LANE_RIGHT_X - LANE_LEFT_X, 116, 8, 8);
            g.setColor(i == lane ? COLOR_LANE_BORDER_ACTIVE : COLOR_LANE_BORDER_IDLE);
            g.drawRoundRect(LANE_LEFT_X, LANE_Y[i] - 58,
                    LANE_RIGHT_X - LANE_LEFT_X, 116, 8, 8);
        }
    }

    void drawLaneWordsPanel(Graphics2D g) {
        int width = 760;
        int height = 78;
        int x = (WIDTH - width) / 2;
        int y = HEIGHT - 320;
        drawLaneWordCard(g, 0, x, y, width, height);
        drawLaneWordCard(g, 1, x, y + height + 18, width, height);
    }

    void drawLaneWordCard(Graphics2D g, int laneIndex, int x, int y, int width, int height) {
        boolean active = laneIndex == lane;
        boolean typing = typingLane == laneIndex && typed.length() > 0;
        boolean wrong = wrongFlashTicks > 0 && (wrongFlashLane < 0 || wrongFlashLane == laneIndex);
        boolean completed = completePulseTicks > 0 && completePulseLane == laneIndex;
        Color edge = active ? COLOR_CARD_ACTIVE : COLOR_CARD_IDLE;
        if (typing) {
            edge = COLOR_TYPED_PREFIX;
        }
        if (wrong) {
            edge = COLOR_TARGET_SWITCHER;
        }
        if (active || typing) {
            int pulse = 6 + (int) (pulse(0.18, laneIndex * 17) * 8) + inputPulseTicks;
            g.setColor(typing ? withAlpha(COLOR_TYPED_PREFIX, 42 + inputPulseTicks * 9) : COLOR_CARD_GLOW);
            g.fillRoundRect(x - pulse, y - pulse, width + pulse * 2, height + pulse * 2, 10, 10);
        }
        if (completed) {
            int age = WORD_COMPLETE_PULSE_TICKS - completePulseTicks;
            int grow = 18 + age * 4;
            int alpha = Math.max(24, completePulseTicks * 9);
            g.setColor(withAlpha(COLOR_WORD_COMPLETE, Math.max(18, alpha / 3)));
            g.fillRoundRect(x - grow, y - grow, width + grow * 2, height + grow * 2, 18, 18);
            g.setStroke(STROKE_WORD_COMPLETE_AURA);
            g.setColor(withAlpha(COLOR_WORD_COMPLETE, alpha));
            g.drawRoundRect(x - grow, y - grow, width + grow * 2, height + grow * 2, 18, 18);
            g.setStroke(STROKE_WORD_COMPLETE_CORE);
            g.setColor(withAlpha(Color.WHITE, Math.max(20, alpha / 2)));
            g.drawRoundRect(x - grow / 2, y - grow / 2, width + grow, height + grow, 14, 14);
        }
        if (wrong) {
            int grow = 4 + wrongFlashTicks;
            g.setColor(withAlpha(COLOR_TARGET_SWITCHER, 38 + wrongFlashTicks * 7));
            g.fillRoundRect(x - grow, y - grow, width + grow * 2, height + grow * 2, 12, 12);
        }
        g.setColor(COLOR_PANEL_DARK);
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setStroke(active ? STROKE_CARD_ACTIVE : STROKE_CARD_IDLE);
        g.setColor(edge);
        g.drawRoundRect(x, y, width, height, 8, 8);

        String word = laneWords[laneIndex];
        drawLaneArrowIcon(g, x + 45, y + height / 2, laneIndex, edge);
        g.setFont(FONT_LANE_WORD);
        FontMetrics metrics = g.getFontMetrics();
        int wordAreaX = x + 105;
        int wordAreaWidth = width - 135;
        int textX = wordAreaX + (wordAreaWidth - metrics.stringWidth(word)) / 2;
        int textY = y + 53;
        if (typing) {
            String prefix = word.substring(0, Math.min(typed.length(), word.length()));
            String suffix = word.substring(Math.min(typed.length(), word.length()));
            g.setColor(COLOR_TYPED_PREFIX);
            g.drawString(prefix, textX, textY);
            g.setColor(COLOR_TYPED_SUFFIX);
            g.drawString(suffix, textX + metrics.stringWidth(prefix), textY);
        } else {
            g.setColor(active ? Color.WHITE : COLOR_TYPED_SUFFIX);
            g.drawString(word, textX, textY);
        }
    }

    void drawLaneArrowIcon(Graphics2D g, int x, int y, int laneIndex, Color color) {
        Polygon arrow = new Polygon();
        if (laneIndex == 0) {
            arrow.addPoint(x, y - 19);
            arrow.addPoint(x - 18, y + 15);
            arrow.addPoint(x + 18, y + 15);
        } else {
            arrow.addPoint(x, y + 19);
            arrow.addPoint(x - 18, y - 15);
            arrow.addPoint(x + 18, y - 15);
        }
        g.setColor(withAlpha(color, 70));
        g.fillOval(x - 28, y - 28, 56, 56);
        g.setColor(color);
        g.fillPolygon(arrow);
    }

    void drawPlayer(Graphics2D g) {
        int y = (int) Math.round(playerRenderY());
        int pulse = 5 + (int) (pulse(0.16, 0) * 6);
        g.setColor(COLOR_PLAYER_GLOW);
        g.fillOval(PLAYER_X - 22 - pulse, y - 22 - pulse, 44 + pulse * 2, 44 + pulse * 2);
        if (laneBarrierCharges[lane] > 0 || calmGuardCharges > 0) {
            int shield = 31 + (int) (pulse(0.2, 11) * 5);
            g.setColor(withAlpha(COLOR_WORD_COMPLETE, 110));
            g.drawOval(PLAYER_X - shield, y - shield, shield * 2, shield * 2);
        }
        g.setColor(COLOR_PLAYER);
        g.fillOval(PLAYER_X - 22, y - 22, 44, 44);
        g.setColor(COLOR_PLAYER_TEXT);
        g.setFont(uiFont(FONT_TARGET_HP, FONT_PRESSURE_ZH));
        g.drawString(ui("YOU", "你"), PLAYER_X - (isChinese() ? 7 : 12), y + 5);
    }

    void drawBreakParticles(Graphics2D g) {
        for (BreakParticle particle : breakParticles) {
            double age = 1.0 - particle.life / (double) particle.maxLife;
            int alpha = Math.max(0, 230 - (int) (age * 230));
            int x = (int) Math.round(renderX(particle.previousX, particle.x));
            int y = (int) Math.round(renderY(particle.previousY, particle.y));
            int size = Math.max(2, (int) Math.round(particle.size * (1.0 - age * 0.45)));
            g.setColor(withAlpha(COLOR_TARGET_GLOW, alpha / 3));
            g.fillRect(x - size, y - size, size * 2, size * 2);
            g.setColor(withAlpha(particle.color, alpha));
            g.fillRect(x - size / 2, y - size / 2, size, size);
            if (age < 0.45) {
                g.setColor(withAlpha(Color.WHITE, 120 - (int) (age * 180)));
                g.fillRect(x, y, Math.max(1, size / 2), Math.max(1, size / 2));
            }
        }
    }

    void drawTargets(Graphics2D g) {
        g.setFont(FONT_TARGET_TITLE);
        for (Target target : targets) {
            int x = (int) Math.round(renderX(target.previousX, target.x));
            int y = LANE_Y[target.lane];
            Color color = colorFor(target);
            if (target.hitFlash > 0) {
                color = Color.WHITE;
            }
            int w = target.kind == TargetKind.BOSS ? 118 : 92;
            int h = target.kind == TargetKind.BOSS ? 70 : 50;
            int glow = target.kind == TargetKind.BOSS ? 10 : 6;
            g.setColor(COLOR_TARGET_GLOW);
            g.fillRoundRect(x - w / 2 - glow, y - h / 2 - glow, w + glow * 2, h + glow * 2, 10, 10);
            g.setColor(color);
            g.fillRoundRect(x - w / 2, y - h / 2, w, h, 8, 8);
            if (target.slowTicks > 0 || laneSlowTicks[target.lane] > 0) {
                g.setStroke(STROKE_CARD_ACTIVE);
                g.setColor(withAlpha(COLOR_PIERCE_CORE, 155));
                g.drawRoundRect(x - w / 2 - 6, y - h / 2 - 6, w + 12, h + 12, 10, 10);
            }
            if (target.markTicks > 0) {
                g.setStroke(STROKE_CARD_ACTIVE);
                g.setColor(withAlpha(COLOR_UPGRADE_READY, 165));
                g.drawRoundRect(x - w / 2 - 11, y - h / 2 - 11, w + 22, h + 22, 12, 12);
            }
            g.setColor(COLOR_INK_DARK);
            g.drawRoundRect(x - w / 2, y - h / 2, w, h, 8, 8);

            g.setColor(Color.WHITE);
            drawCentered(g, targetLabel(target), x, y - 4);
            drawTargetHealthBar(g, target, x, y + h / 2 + 7, w - 18);
            g.setFont(FONT_TARGET_HP);
            drawCentered(g, "HP " + Math.max(0, target.hp), x, y + 18);
            g.setFont(FONT_TARGET_TITLE);
        }
    }

    void drawTargetHealthBar(Graphics2D g, Target target, int cx, int y, int width) {
        int height = 4;
        int filled = Math.max(0, Math.min(width, target.hp * width / Math.max(1, target.maxHp)));
        g.setColor(COLOR_HP_BAR_BACK);
        g.fillRoundRect(cx - width / 2, y, width, height, 3, 3);
        g.setColor(COLOR_HP_BAR_FILL);
        g.fillRoundRect(cx - width / 2, y, filled, height, 3, 3);
    }

    void drawBullets(Graphics2D g) {
        for (Bullet bullet : bullets) {
            int y = LANE_Y[bullet.lane];
            int x = (int) Math.round(renderX(bullet.previousX, bullet.x));
            int previousX = (int) Math.round(bullet.previousX);
            if (bullet.kind == BulletKind.PIERCE) {
                int tail = Math.max(PLAYER_X, previousX - 78);
                g.setStroke(STROKE_PIERCE_AURA);
                g.setColor(COLOR_PIERCE_AURA);
                g.drawLine(tail, y, x, y);
                g.setStroke(STROKE_PIERCE_CORE);
                g.setColor(COLOR_PIERCE_CORE);
                g.drawLine(Math.max(PLAYER_X, previousX - 46), y, x, y);
                g.setColor(COLOR_PIERCE_HEAD);
                g.fillOval(x - 12, y - 12, 24, 24);
            } else if (bullet.kind == BulletKind.CONTINUOUS_SURGE) {
                g.setStroke(STROKE_SURGE_AURA);
                g.setColor(COLOR_SURGE_AURA);
                g.drawLine(Math.max(PLAYER_X, previousX - 52), y, x, y);
                g.setStroke(STROKE_SURGE_CORE);
                g.setColor(COLOR_SURGE_CORE);
                g.drawLine(Math.max(PLAYER_X, previousX - 34), y, x, y);
                g.setColor(COLOR_SURGE_GLOW);
                g.fillOval(x - 14, y - 14, 28, 28);
                g.setColor(COLOR_SURGE_HEAD);
                g.fillOval(x - 8, y - 8, 16, 16);
            } else if (bullet.kind == BulletKind.BURST) {
                g.setStroke(STROKE_SURGE_AURA);
                g.setColor(COLOR_BURST_AURA);
                g.drawLine(Math.max(PLAYER_X, previousX - 44), y, x, y);
                g.setColor(COLOR_BURST_HEAD);
                g.fillOval(x - 10, y - 10, 20, 20);
            } else if (bullet.kind == BulletKind.CONTINUOUS) {
                g.setStroke(STROKE_CONTINUOUS);
                g.setColor(COLOR_CONTINUOUS_TRAIL);
                g.drawLine(Math.max(PLAYER_X, previousX - 14), y, x, y);
                g.setColor(COLOR_CONTINUOUS_HEAD);
                g.fillOval(x - 3, y - 3, 6, 6);
            } else {
                g.setStroke(STROKE_BASIC);
                g.setColor(COLOR_BASIC_TRAIL);
                g.drawLine(Math.max(PLAYER_X, previousX - 34), y, x, y);
                g.setColor(COLOR_BASIC_HEAD);
                g.fillOval(x - 9, y - 9, 18, 18);
            }
            int coreRadius = bullet.kind == BulletKind.CONTINUOUS ? 2 : 4;
            g.setColor(Color.WHITE);
            g.fillOval(x - coreRadius, y - coreRadius, coreRadius * 2, coreRadius * 2);
        }
    }

    void drawXpOrbs(Graphics2D g) {
        for (XpOrb orb : xpOrbs) {
            int x = (int) Math.round(renderX(orb.previousX, orb.x));
            int y = (int) Math.round(renderY(orb.previousY, orb.y));
            int pulse = (int) (smoothstep(0.5 + 0.5 * Math.sin(orb.pulse * 0.22)) * 5);
            int radius = 11 + pulse;
            g.setColor(COLOR_XP_GLOW);
            g.fillOval(x - radius - 8, y - radius - 8, (radius + 8) * 2, (radius + 8) * 2);
            g.setColor(COLOR_XP);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            g.setColor(COLOR_INK_DARK);
            g.setFont(FONT_ORB);
            drawCentered(g, "XP" + orb.value, x, y + 4);
        }
    }


    void drawImpacts(Graphics2D g) {
        g.setStroke(STROKE_IMPACT);
        for (Impact impact : impacts) {
            int age = 14 - impact.ticks;
            double eased = easeOutCubic(age / 14.0);
            int radius = 10 + (int) (eased * 42);
            int alpha = Math.max(35, 210 - (int) (eased * 175));
            g.setColor(withAlpha(COLOR_IMPACT_RING, alpha));
            g.drawOval((int) impact.x - radius, (int) impact.y - radius, radius * 2, radius * 2);
            g.setColor(withAlpha(COLOR_IMPACT_CORE, Math.max(25, alpha - 60)));
            g.fillOval((int) impact.x - 5, (int) impact.y - 5, 10, 10);
            if (impact.damage > 0) {
                g.setColor(Color.WHITE);
                g.setFont(FONT_IMPACT);
                g.drawString("-" + impact.damage, (int) impact.x + 12, (int) impact.y - 18);
            }
        }
    }

    Color colorFor(Target target) {
        if (target.kind == TargetKind.UPGRADE) {
            return COLOR_TARGET_UPGRADE;
        }
        if (target.kind == TargetKind.FAST) {
            return COLOR_TARGET_FAST;
        }
        if (target.kind == TargetKind.TANK) {
            return COLOR_TARGET_TANK;
        }
        if (target.kind == TargetKind.SWITCHER) {
            return COLOR_TARGET_SWITCHER;
        }
        if (target.kind == TargetKind.BOSS) {
            return COLOR_TARGET_BOSS;
        }
        return COLOR_TARGET_NORMAL;
    }

    String targetLabel(Target target) {
        if (target.kind == TargetKind.UPGRADE) {
            return "CACHE";
        }
        if (target.kind == TargetKind.FAST) {
            return "FAST";
        }
        if (target.kind == TargetKind.TANK) {
            return "TANK";
        }
        if (target.kind == TargetKind.SWITCHER) {
            return "SHIFT";
        }
        if (target.kind == TargetKind.BOSS) {
            return "BOSS";
        }
        return "ENEMY";
    }

    void drawHud(Graphics2D g) {
        int panelPulse = 3 + (int) (pulse(0.11, 19) * 3);
        g.setColor(new Color(7, 11, 18, 170));
        g.fillRoundRect(14, 12, 1085, 86, 8, 8);
        g.setColor(new Color(82, 126, 164, 95));
        g.setStroke(STROKE_CARD_IDLE);
        g.drawRoundRect(14, 12, 1085, 86, 8, 8);
        if (pendingUpgradeChoices > 0) {
            g.setColor(withAlpha(COLOR_UPGRADE_READY, 22 + panelPulse * 8));
            g.fillRoundRect(14, 12, 1085, 86, 8, 8);
        }

        g.setFont(uiFont(FONT_HUD, FONT_HUD_ZH));
        g.setColor(COLOR_HUD_TEXT);
        g.drawString("HP " + hp + "/" + maxHp, 22, 32);
        g.drawString(ui("Score ", "分数 ") + score, 160, 32);
        g.drawString(ui("Kills ", "击破 ") + kills, 290, 32);
        g.drawString(ui("Combo ", "连击 ") + combo + ui(" / best ", " / 最佳 ") + bestCombo, 405, 32);
        g.drawString("v" + TypingLaneDemo.VERSION, WIDTH - 92, HEIGHT - 25);
        g.drawString(ui("Difficulty ", "难度 ") + difficultyLabel(difficulty)
                + ui("   Speed ", "   速度 ") + currentTypingSpeedWpm() + " WPM", 22, 58);
        drawPressureMeter(g, 22, 88);
        drawUpgradePrompt(g, WIDTH - 365, 18);
        drawLanguageBadge(g, WIDTH - 315, HEIGHT - 48);
        g.drawString(ui("Lane: ", "赛道：") + laneDisplayName(lane), 22, HEIGHT - 25);
        g.drawString(ui("Best ", "最高 ") + highScore, 160, HEIGHT - 25);
        g.drawString(ui("High ", "高稀有 ") + highTalentHudText(), 300, HEIGHT - 25);
    }

    void drawUpgradePrompt(Graphics2D g, int x, int y) {
        int width = 338;
        int height = 74;
        int glow = pendingUpgradeChoices > 0 ? 10 + (int) (pulse(0.22, 41) * 12) : 4;
        Color edge = pendingUpgradeChoices > 0 ? COLOR_UPGRADE_READY : COLOR_XP;
        g.setColor(pendingUpgradeChoices > 0 ? COLOR_UPGRADE_READY_GLOW : COLOR_XP_GLOW);
        g.fillRoundRect(x - glow, y - glow, width + glow * 2, height + glow * 2, 12, 12);
        g.setColor(COLOR_PANEL_DARK);
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(edge);
        g.setStroke(pendingUpgradeChoices > 0 ? STROKE_CARD_ACTIVE : STROKE_CARD_IDLE);
        g.drawRoundRect(x, y, width, height, 8, 8);

        int iconX = x + 28;
        int iconY = y + 36;
        g.setColor(edge);
        g.fillOval(iconX - 15, iconY - 15, 30, 30);
        g.setColor(COLOR_INK_DARK);
        g.setFont(FONT_ORB);
        drawCentered(g, "XP", iconX, iconY + 4);

        int barX = x + 58;
        int barY = y + 24;
        int barWidth = 126;
        int filled = pendingUpgradeChoices > 0
                ? barWidth
                : Math.max(0, Math.min(barWidth, xp * barWidth / Math.max(1, xpToNext)));
        g.setColor(COLOR_PRESSURE_TRACK);
        g.fillRoundRect(barX, barY, barWidth, 14, 7, 7);
        g.setColor(edge);
        g.fillRoundRect(barX, barY, filled, 14, 7, 7);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.setColor(COLOR_TYPED_SUFFIX);
        g.drawString("Lv" + upgradeLevel + " " + xp + "/" + xpToNext, barX, y + 55);

        if (pendingUpgradeChoices > 0) {
            drawBoltIcon(g, x + 214, y + 37, edge);
            drawKeycap(g, "SPACE", x + 238, y + 22, 74, 30, edge);
            g.setFont(FONT_PRESSURE);
            g.setColor(edge);
            g.drawString("x" + pendingUpgradeChoices, x + 318, y + 42);
        }
    }

    void drawBoltIcon(Graphics2D g, int x, int y, Color color) {
        Polygon bolt = new Polygon();
        bolt.addPoint(x - 4, y - 20);
        bolt.addPoint(x + 14, y - 20);
        bolt.addPoint(x + 3, y - 2);
        bolt.addPoint(x + 17, y - 2);
        bolt.addPoint(x - 7, y + 23);
        bolt.addPoint(x, y + 5);
        bolt.addPoint(x - 14, y + 5);
        g.setColor(color);
        g.fillPolygon(bolt);
    }

    void drawKeycap(Graphics2D g, String label, int x, int y, int width, int height, Color edge) {
        g.setColor(new Color(8, 12, 18, 232));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(edge);
        g.setStroke(STROKE_CARD_IDLE);
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setFont(FONT_PRESSURE);
        g.setColor(Color.WHITE);
        drawCentered(g, label, x + width / 2, y + 20);
    }

    void drawLanguageBadge(Graphics2D g, int x, int y) {
        int width = 225;
        int height = 30;
        g.setColor(COLOR_PANEL_DARK);
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(new Color(95, 205, 255, 145));
        g.setStroke(STROKE_CARD_IDLE);
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawCentered(g, ui("F2 Language: EN", "F2 语言：中文"), x + width / 2, y + 21);
    }

    void drawPressureMeter(Graphics2D g, int x, int y) {
        int pressure = pressureLevel();
        int width = 230;
        int filled = Math.min(width, pressure * width / 14);
        Color pressureColor = pressure < 5
                ? COLOR_PRESSURE_LOW
                : pressure < 10 ? COLOR_PRESSURE_MID : COLOR_PRESSURE_HIGH;
        Color pressureGlow = pressure < 5
                ? COLOR_PRESSURE_LOW_GLOW
                : pressure < 10 ? COLOR_PRESSURE_MID_GLOW : COLOR_PRESSURE_HIGH_GLOW;
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.setColor(COLOR_PRESSURE_TEXT);
        g.drawString(ui("PRESSURE ", "压力 ") + pressure, x, y - 8);
        g.setColor(COLOR_PRESSURE_TRACK);
        g.fillRoundRect(x, y, width, 14, 6, 6);
        g.setColor(pressureGlow);
        g.fillRoundRect(x - 3, y - 3, filled + 6, 20, 8, 8);
        g.setColor(pressureColor);
        g.fillRoundRect(x, y, filled, 14, 6, 6);
        g.setColor(COLOR_INK_DARK);
        g.drawRoundRect(x, y, width, 14, 6, 6);
    }

    void drawChoiceOverlay(Graphics2D g) {
        if (choiceMode == ChoiceMode.TEST_BACKEND) {
            drawTestBackendOverlay(g);
            return;
        }
        if (choiceMode == ChoiceMode.HIGH_REPLACE) {
            drawHighReplacementOverlay(g);
            return;
        }
        g.setColor(COLOR_CHOICE_OVERLAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(uiFont(FONT_CHOICE_TITLE, FONT_CHOICE_TITLE_ZH));
        drawCentered(g, ui("Choose Upgrade", "选择升级"), WIDTH / 2, 210);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH));
        drawCentered(g, ui("Left/Right + Enter, or 1/2/3. Letters stay reserved for typing.",
                "左右键 + Enter，或 1/2/3。字母键保留给输入。"), WIDTH / 2, 255);
        for (int i = 0; i < upgradeChoices.length; i++) {
            drawUpgradeCard(g, upgradeChoices[i], i, WIDTH / 2 - 465 + i * 310, 310);
        }
    }

    void drawHighReplacementOverlay(Graphics2D g) {
        g.setColor(COLOR_CHOICE_OVERLAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(uiFont(FONT_CHOICE_TITLE, FONT_CHOICE_TITLE_ZH));
        drawCentered(g, ui("Replace High Talent", "替换高稀有天赋"), WIDTH / 2, 165);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH));
        drawCentered(g, ui("Press 1/2/3 to replace a held talent, or Esc to abandon the new one.",
                "按 1/2/3 替换已有天赋，或 Esc 放弃新天赋。"), WIDTH / 2, 210);
        if (pendingHighTalent != null) {
            drawCompactHighCard(g, pendingHighTalent, ui("NEW", "新"), WIDTH / 2 - 170, 250, 340, 130,
                    colorFor(pendingHighTalent.rarity));
        }
        for (int i = 0; i < highTalents.length; i++) {
            UpgradeEffect effect = highTalents[i];
            UpgradeCard card = effect == null ? null : createCard(effect, rarityForEffect(effect));
            drawCompactHighCard(g, card, String.valueOf(i + 1), WIDTH / 2 - 465 + i * 310, 455, 280, 170,
                    COLOR_RARITY_HIGH);
        }
    }

    void drawTestBackendOverlay(Graphics2D g) {
        UpgradeEffect[] effects = allTestUpgradeEffects();
        int pageStart = selectedTestUpgradeIndex / TEST_BACKEND_PAGE_SIZE * TEST_BACKEND_PAGE_SIZE;
        int pageEnd = Math.min(effects.length, pageStart + TEST_BACKEND_PAGE_SIZE);
        g.setColor(new Color(0, 0, 0, 205));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(uiFont(FONT_CHOICE_TITLE, FONT_CHOICE_TITLE_ZH));
        drawCentered(g, ui("sudo backend", "sudo 测试后台"), WIDTH / 2, 155);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH));
        drawCentered(g, ui("Up/Down select  Enter grant  Esc close",
                "上下选择  Enter 授予  Esc 关闭"), WIDTH / 2, 205);

        int panelX = WIDTH / 2 - 430;
        int panelY = 245;
        int rowHeight = 64;
        int panelWidth = 860;
        int panelHeight = TEST_BACKEND_PAGE_SIZE * rowHeight + 28;
        g.setColor(COLOR_PANEL_DARK);
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 8, 8);
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(new Color(95, 205, 255, 170));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 8, 8);

        for (int i = pageStart; i < pageEnd; i++) {
            UpgradeEffect effect = effects[i];
            UpgradeRarity rarity = rarityForEffect(effect);
            UpgradeCard card = createCard(effect, rarity);
            int row = i - pageStart;
            int y = panelY + 18 + row * rowHeight;
            boolean selected = i == selectedTestUpgradeIndex;
            Color rarityColor = colorFor(rarity);
            if (selected) {
                g.setColor(withAlpha(rarityColor, 54));
                g.fillRoundRect(panelX + 12, y - 7, panelWidth - 24, rowHeight - 8, 8, 8);
            }
            g.setColor(rarityColor);
            g.fillOval(panelX + 32, y + 8, 14, 14);
            g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
            g.drawString(rarityLabel(rarity), panelX + 58, y + 21);
            g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH));
            g.setColor(Color.WHITE);
            g.drawString(cardTitle(card), panelX + 205, y + 25);
            g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
            g.setColor(COLOR_TYPED_SUFFIX);
            drawFittedString(g, cardDescription(card), panelX + 205, y + 48, panelWidth - 335);
            int level = testLevelFor(effect);
            if (level > 0) {
                g.setColor(COLOR_WORD_COMPLETE);
                g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
                g.drawString(ui("Lv ", "等级 ") + level, panelX + panelWidth - 92, y + 23);
            }
        }
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawCentered(g, (pageStart + 1) + "-" + pageEnd + " / " + effects.length,
                WIDTH / 2, panelY + panelHeight + 38);
    }

    void drawUpgradeCard(Graphics2D g, UpgradeCard card, int index, int x, int y) {
        if (card == null) {
            return;
        }
        int width = 280;
        int height = 210;
        boolean selected = index == selectedUpgradeIndex;
        Color rarityColor = colorFor(card.rarity);
        if (selected) {
            g.setColor(withAlpha(rarityColor, 70));
            g.fillRoundRect(x - 8, y - 8, width + 16, height + 16, 14, 14);
        }
        g.setColor(COLOR_PANEL_DARK);
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(rarityColor);
        g.setStroke(selected ? STROKE_CARD_ACTIVE : STROKE_CARD_IDLE);
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.drawString((index + 1) + "  " + rarityLabel(card.rarity), x + 18, y + 34);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH));
        g.setColor(Color.WHITE);
        g.drawString(cardTitle(card), x + 18, y + 80);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawWrapped(g, upgradeChoiceDescription(card), x + 18, y + 118, width - 36, 24);
        if (card.rarity == UpgradeRarity.HIGH) {
            g.setColor(rarityColor);
            g.drawString(ui("High Talent", "高稀有天赋"), x + 18, y + height - 24);
        }
    }

    void drawCompactHighCard(Graphics2D g, UpgradeCard card, String marker,
            int x, int y, int width, int height, Color accent) {
        g.setColor(COLOR_PANEL_DARK);
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(accent);
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.drawString(marker, x + 18, y + 32);
        if (card == null) {
            g.setColor(COLOR_TYPED_SUFFIX);
            g.drawString(ui("Empty", "空"), x + 18, y + 76);
            return;
        }
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH));
        g.setColor(Color.WHITE);
        drawFittedString(g, cardTitle(card), x + 18, y + 75, width - 36);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawWrapped(g, cardDescription(card), x + 18, y + 112, width - 36, 22);
    }

    Color colorFor(UpgradeRarity rarity) {
        if (rarity == UpgradeRarity.HIGH) {
            return COLOR_RARITY_HIGH;
        }
        if (rarity == UpgradeRarity.UNCOMMON) {
            return COLOR_RARITY_UNCOMMON;
        }
        return COLOR_RARITY_COMMON;
    }

    String cardTitle(UpgradeCard card) {
        return isChinese() ? card.titleZh : card.title;
    }

    String cardDescription(UpgradeCard card) {
        return isChinese() ? card.descriptionZh : card.description;
    }

    String upgradeChoiceDescription(UpgradeCard card) {
        if (card.effect != UpgradeEffect.TRIGGER_TUNING) {
            return cardDescription(card);
        }
        return ui("Fire one extra bullet; each pellet deals less damage.",
                "多发射一次子弹，但每颗弹丸伤害减少。");
    }

    String rarityLabel(UpgradeRarity rarity) {
        return isChinese() ? rarity.labelZh : rarity.label;
    }

    String difficultyLabel(Difficulty value) {
        if (!isChinese()) {
            return value.label;
        }
        if (value == Difficulty.EASY) {
            return "轻松";
        }
        if (value == Difficulty.HARD) {
            return "高压";
        }
        return "标准";
    }

    String highTalentHudText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < highTalents.length; i++) {
            if (i > 0) {
                builder.append("  ");
            }
            builder.append(i + 1).append(":");
            builder.append(highTalents[i] == null ? ui("Empty", "空") : effectTitle(highTalents[i]));
        }
        return builder.toString();
    }

    String effectTitle(UpgradeEffect effect) {
        if (effect == null) {
            return isChinese() ? "空" : "Empty";
        }
        UpgradeCard card = createCard(effect, rarityForEffect(effect));
        return cardTitle(card);
    }

    String effectTitleEn(UpgradeEffect effect) {
        if (effect == null) {
            return "Empty";
        }
        return createCard(effect, rarityForEffect(effect)).title;
    }

    String effectTitleZh(UpgradeEffect effect) {
        if (effect == null) {
            return "空";
        }
        return createCard(effect, rarityForEffect(effect)).titleZh;
    }

    void drawWrapped(Graphics2D g, String text, int x, int y, int width, int lineHeight) {
        FontMetrics metrics = g.getFontMetrics();
        boolean spaced = text.indexOf(' ') >= 0;
        String[] words = spaced ? text.split(" ") : text.split("");
        String line = "";
        int lineY = y;
        for (String word : words) {
            if (word.length() == 0) {
                continue;
            }
            String candidate = line.length() == 0 ? word : line + (spaced ? " " : "") + word;
            if (metrics.stringWidth(candidate) > width && line.length() > 0) {
                g.drawString(line, x, lineY);
                line = word;
                lineY += lineHeight;
            } else {
                line = candidate;
            }
        }
        if (line.length() > 0) {
            g.drawString(line, x, lineY);
        }
    }

    void drawFittedString(Graphics2D g, String text, int x, int y, int maxWidth) {
        g.drawString(fitText(g, text, maxWidth), x, y);
    }

    String fitText(Graphics2D g, String text, int maxWidth) {
        FontMetrics metrics = g.getFontMetrics();
        if (metrics.stringWidth(text) <= maxWidth) {
            return text;
        }
        String suffix = "...";
        int allowedWidth = Math.max(0, maxWidth - metrics.stringWidth(suffix));
        String fitted = text;
        while (fitted.length() > 0 && metrics.stringWidth(fitted) > allowedWidth) {
            fitted = fitted.substring(0, fitted.length() - 1);
        }
        return fitted + suffix;
    }

    void drawStartOverlay(Graphics2D g) {
        g.setColor(COLOR_START_OVERLAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(uiFont(FONT_OVERLAY_TITLE, FONT_OVERLAY_TITLE_ZH));
        drawCentered(g, "Typing Lane " + TypingLaneDemo.VERSION, WIDTH / 2, 170);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH));
        drawDifficultyOption(g, Difficulty.EASY, 310, "1");
        drawDifficultyOption(g, Difficulty.NORMAL, 350, "2");
        drawDifficultyOption(g, Difficulty.HARD, 390, "3");
        g.setColor(Color.WHITE);
        drawCentered(g, ui("Current typing speed target: ", "目标速度：") + difficulty.typingSpeedWpm + " WPM",
                WIDTH / 2, 448);
        drawCentered(g, ui("F2 Switch Language", "F2 切换语言"), WIDTH / 2, 482);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH));
        drawCentered(g, ui("Press Enter", "按 Enter 开始"), WIDTH / 2, 532);
    }

    void drawDifficultyOption(Graphics2D g, Difficulty option, int y, String key) {
        boolean selected = difficulty == option;
        if (selected) {
            int width = 390;
            int pulse = 3 + (int) (pulse(0.16, 7) * 4);
            g.setColor(COLOR_CARD_GLOW);
            g.fillRoundRect(WIDTH / 2 - width / 2 - pulse, y - 25 - pulse,
                    width + pulse * 2, 34 + pulse * 2, 8, 8);
        }
        g.setColor(selected ? COLOR_CARD_ACTIVE : COLOR_TYPED_SUFFIX);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH));
        String text = key + "  " + difficultyLabel(option) + ui("   target ", "   目标 ")
                + option.typingSpeedWpm + " WPM";
        drawCentered(g, text, WIDTH / 2, y);
    }

    void drawPauseOverlay(Graphics2D g) {
        g.setColor(COLOR_PAUSE_OVERLAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(uiFont(FONT_OVERLAY_TITLE, FONT_OVERLAY_TITLE_ZH));
        drawCentered(g, ui("Paused", "暂停"), WIDTH / 2, 260);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH));
        drawCentered(g, ui("Press Esc to resume, or F5 to restart", "Esc 继续，F5 重开"), WIDTH / 2, 320);
        drawCentered(g, ui("F2 Switch Language", "F2 切换语言"), WIDTH / 2, 360);
    }

    void drawGameOver(Graphics2D g) {
        g.setColor(COLOR_GAME_OVER_OVERLAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(uiFont(FONT_OVERLAY_TITLE, FONT_OVERLAY_TITLE_ZH));
        drawCentered(g, ui("Game Over", "游戏结束"), WIDTH / 2, 210);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH));
        drawCentered(g, ui("Score ", "分数 ") + score + ui("   Best ", "   最高 ") + highScore
                + ui("   Kills ", "   击破 ") + kills + ui("   Best combo ", "   最佳连击 ") + bestCombo,
                WIDTH / 2, 270);
        drawCentered(g, ui("Main cause: ", "主要原因：") + currentDeathReason(), WIDTH / 2, 310);
        drawCentered(g, ui("Press Enter to restart", "按 Enter 重开"), WIDTH / 2, 360);
        drawCentered(g, ui("F2 Switch Language", "F2 切换语言"), WIDTH / 2, 400);
    }

    void drawCentered(Graphics2D g, String text, int cx, int y) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, cx - metrics.stringWidth(text) / 2, y);
    }
}
