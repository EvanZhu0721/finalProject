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
    static final int BASE_WIDTH = 1920;
    static final int BASE_HEIGHT = 1080;
    static final int WIDTH = 2560;
    static final int HEIGHT = 1440;
    static final int RENDER_WIDTH = WIDTH;
    static final int RENDER_HEIGHT = HEIGHT;
    static final int DEFAULT_WINDOW_WIDTH = 1600;
    static final int DEFAULT_WINDOW_HEIGHT = 900;
    static final int MIN_WINDOW_WIDTH = 960;
    static final int MIN_WINDOW_HEIGHT = 540;
    static final double COORD_SCALE_X = WIDTH / (double) BASE_WIDTH;
    static final double COORD_SCALE_Y = HEIGHT / (double) BASE_HEIGHT;
    static final double COORD_SCALE = COORD_SCALE_X;
    static final double RENDER_SCALE_X = RENDER_WIDTH / (double) WIDTH;
    static final double RENDER_SCALE_Y = RENDER_HEIGHT / (double) HEIGHT;
    static final int PLAYER_X = sx(95);
    static final int LANE_LEFT_X = sx(55);
    static final int LANE_RIGHT_X = WIDTH - sx(55);
    static final int[] LANE_Y = {sy(230), sy(410)};
    static final int LOGIC_FPS = 120;
    static final double TICK_MS = 1000.0 / LOGIC_FPS;
    static final double LEGACY_TICK_MS = 33.0;
    static final double GAMEPLAY_STEP_SCALE = TICK_MS / LEGACY_TICK_MS;
    static final int FRAME_MS = 8;
    static final boolean ENABLE_GROUP_ONE_UPGRADES = true;
    static final boolean ENABLE_GROUP_TWO_TO_FIVE_UPGRADES = true;
    static final int LANE_WORD_MIN_LENGTH = 4;
    static final int LANE_WORD_MAX_LENGTH = 7;
    static final int BASE_SPAWN_COOLDOWN = logicTicks(100);
    static final int SPAWN_COOLDOWN_JITTER = logicTicks(15);
    static final int[] EARLY_XP_REQUIREMENTS = {6, 8, 12, 16, 22};
    static final int INITIAL_XP_TO_NEXT = EARLY_XP_REQUIREMENTS[0];
    static final int BOSS_XP_BASE = 24;
    static final int BOSS_XP_PER_LEVEL = 8;
    static final int BOSS_LASER_WARNING_TICKS = logicTicks(60);
    static final int BOSS_ATTACK_BASE_COOLDOWN_TICKS = logicTicks(150);
    static final int BOSS_ATTACK_JITTER_TICKS = logicTicks(34);
    static final int BOSS_BARRAGE_INTERVAL_TICKS = logicTicks(48);
    static final int BOSS_BARRAGE_JITTER_TICKS = logicTicks(14);
    static final int BOSS_LASER_DAMAGE = 8;
    static final int BOSS_BARRAGE_DAMAGE = 5;
    static final double BOSS_BARRAGE_SPEED = 20.0;
    static final int BOSS_DEATH_ANIMATION_TICKS = logicTicks(72);
    static final int BOSS_SCREEN_FLASH_TICKS = logicTicks(20);
    static final int PLAYER_BASE_HP = 36;
    static final int FIELD_PATCH_HEAL = 8;
    static final int MAX_HP_CARD_BONUS = 6;
    static final int MAX_DAMAGE_BONUS_PERCENT = 300;
    static final int TRIGGER_TUNING_STEP_PERCENT = 20;
    static final int MAX_TRIGGER_TUNING_BONUS_PERCENT = 100;
    static final int MAX_HP_UPGRADE_BONUS = 36;
    static final int MAX_BLUE_UPGRADES = 3;
    static final int MAX_GOLD_TALENTS = 3;
    static final int MAX_RED_WEAPONS = 1;
    static final int MAX_WEAPON_LEVEL = 3;
    static final int WEAPON_SELL_XP_PER_LEVEL = 30;
    static final int GOLD_TALENT_SELL_XP = 20;
    static final int OVERFLOW_MAX_DAMAGE = 40;
    static final int OVERFLOW_SHOT_DAMAGE = 20;
    static final int OVERFLOW_READY_PULSE_TICKS = logicTicks(60);
    static final int MAX_GOLD_DRONES = 3;
    static final int GOLD_DRONE_LIFE_TICKS = logicTicks(420);
    static final int GOLD_DRONE_ATTACK_INTERVAL_TICKS = logicTicks(72);
    static final int GOLD_DRONE_DAMAGE = 2;
    static final double GOLD_DRONE_ATTACK_RANGE = worldAmount(520.0);
    static final double GOLD_DRONE_MOVE_SPEED = 8.2;
    static final int MELEE_REGEN_INTERVAL_TICKS = logicTicks(28);
    static final int MELEE_RAM_ANIMATION_TICKS = logicTicks(16);
    static final int MELEE_SCREEN_SHAKE_TICKS = logicTicks(8);
    static final int MELEE_BOSS_KNOCKBACK_TICKS = logicTicks(10);
    static final int MELEE_BOSS_SCREEN_SHAKE_TICKS = logicTicks(18);
    static final int AUTO_CANNON_QUICK_SURGE_TICKS = logicTicks(60);
    static final int AUTO_CANNON_DECAY_SURGE_TICKS = logicTicks(120);
    static final int AUTO_CANNON_BASE_COOLDOWN = logicTicks(16);
    static final int AUTO_CANNON_PEAK_COOLDOWN = logicTicks(3);
    static final double AUTO_CANNON_STACK_DECAY_PER_SECOND = 20.0;
    static final int LASER_BURN_HIT_THRESHOLD = 5;
    static final int LASER_BURN_DURATION_TICKS = logicTicks(120);
    static final int LASER_BURN_DAMAGE_INTERVAL_TICKS = logicTicks(10);
    static final int LASER_BURN_RAMP_TICKS = logicTicks(60);
    static final int SUSTAINED_LASER_DURATION_TICKS = logicTicks(60);
    static final int SUSTAINED_LASER_RAMP_TICKS = logicTicks(240);
    static final int SUSTAINED_LASER_INTERVAL_TICKS = logicTicks(6);
    static final int LASER_DAMAGE_PERCENT_UPDATE_TICKS = logicTicks(9);
    static final int STATUS_HUD_TRANSITION_TICKS = logicTicks(12);
    static final int MAX_DYNAMIC_HUD_ITEMS = 64;
    static final double DRY_ICE_SPLASH_RADIUS = worldAmount(132.0);
    static final double DRY_ICE_SHATTER_RADIUS = worldAmount(164.0);
    static final double DRY_ICE_SLOW_MULTIPLIER = 0.5;
    static final int HOMING_SHOTGUN_BASE_PELLETS = 5;
    static final int HOMING_SHOTGUN_UPGRADED_PELLETS = 10;
    static final double HOMING_SHOTGUN_FAN_RADIANS = Math.toRadians(68.0);
    static final double HOMING_ARM_DISTANCE = worldAmount(190.0);
    static final double HOMING_STEER = 0.18;
    static final int PHASE_SWITCH_TICKS = logicTicks(11);
    static final int PHASE_SWITCH_BASE_COOLDOWN = logicTicks(240);
    static final int PHASE_SWITCH_COOLDOWN_STEP = logicTicks(38);
    static final int PHASE_SWITCH_MIN_COOLDOWN = logicTicks(105);
    static final int XP_ATTRACT_RADIUS = sx(288);
    static final int XP_COLLECT_RADIUS = sx(34);
    static final int TEST_BACKEND_PAGE_SIZE = 11;
    static final int TEST_BIG_XP_AMOUNT = 500;
    static final int WORD_COMPLETE_PULSE_TICKS = logicTicks(22);
    static final int WORD_TRIGGER_FLASH_TICKS = logicTicks(20);
    static final int LANE_SWITCH_ANIMATION_TICKS = logicTicks(6);
    static final int WORD_TRIGGER_LONG_REWARD = 0;
    static final int WORD_TRIGGER_SHORT_QUICKSHOT = 1;
    static final int WORD_TRIGGER_VOWEL_CONVERGENCE = 2;
    static final int WORD_TRIGGER_HARD_CONSONANT = 3;
    static final int WORD_TRIGGER_CROSSFEED = 4;
    static final int WORD_TRIGGER_FINAL_PULL = 5;
    static final int WORD_TRIGGER_LONG_FOCUS = 6;
    static final int WORD_TRIGGER_SAME_LANE = 7;
    static final int WORD_TRIGGER_LANE_SWAP = 8;
    static final int WORD_TRIGGER_FIRST_LOCK = 9;
    static final int WORD_TRIGGER_PREFIX_MARK = 10;
    static final int WORD_TRIGGER_FIRST_TICKET = 11;
    static final int WORD_TRIGGER_DUAL_PREFIX = 12;
    static final int WORD_TRIGGER_BACKSPACE_FIX = 13;
    static final int WORD_TRIGGER_BACKSPACE_COUNTER = 14;
    static final int WORD_TRIGGER_PRECISE_PICKUP = 15;
    static final int WORD_TRIGGER_DANGER_WORD = 16;
    static final int WORD_TRIGGER_PRESSURE_VALVE = 17;
    static final int WORD_TRIGGER_OVERFLOW = 18;
    static final int WORD_TRIGGER_MELEE = 19;
    static final int WORD_TRIGGER_COUNT = 20;
    static final Font FONT_TARGET_TITLE = font("Consolas", Font.BOLD, 18);
    static final Font FONT_TARGET_HP = font("Consolas", Font.PLAIN, 13);
    static final Font FONT_ORB = font("Consolas", Font.BOLD, 12);
    static final Font FONT_IMPACT = font("Consolas", Font.BOLD, 13);
    static final Font FONT_DAMAGE_POP = font("Comic Sans MS", Font.BOLD, 42);
    static final Font FONT_HUD = font("Consolas", Font.PLAIN, 16);
    static final Font FONT_STATUS_VALUE = font("Consolas", Font.BOLD, 36);
    static final Font FONT_STATUS_LABEL = font("Consolas", Font.BOLD, 15);
    static final Font FONT_PRESSURE = font("Consolas", Font.BOLD, 14);
    static final Font FONT_LANE_LABEL = font("Consolas", Font.BOLD, 15);
    static final Font FONT_LANE_WORD = font("Consolas", Font.BOLD, 46);
    static final Font FONT_OVERLAY_TITLE = font("Consolas", Font.BOLD, 34);
    static final Font FONT_CHOICE_TITLE = font("Consolas", Font.BOLD, 30);
    static final Font FONT_OVERLAY_BODY = font("Consolas", Font.PLAIN, 20);
    static final Font FONT_START_BUTTON = font("Consolas", Font.BOLD, 22);
    static final Font FONT_HUD_ZH = font("Microsoft YaHei UI", Font.PLAIN, 16);
    static final Font FONT_STATUS_VALUE_ZH = font("Microsoft YaHei UI", Font.BOLD, 36);
    static final Font FONT_STATUS_LABEL_ZH = font("Microsoft YaHei UI", Font.BOLD, 15);
    static final Font FONT_PRESSURE_ZH = font("Microsoft YaHei UI", Font.BOLD, 14);
    static final Font FONT_LANE_LABEL_ZH = font("Microsoft YaHei UI", Font.BOLD, 15);
    static final Font FONT_OVERLAY_TITLE_ZH = font("Microsoft YaHei UI", Font.BOLD, 34);
    static final Font FONT_CHOICE_TITLE_ZH = font("Microsoft YaHei UI", Font.BOLD, 30);
    static final Font FONT_OVERLAY_BODY_ZH = font("Microsoft YaHei UI", Font.PLAIN, 20);
    static final Font FONT_START_BUTTON_ZH = font("Microsoft YaHei UI", Font.BOLD, 22);
    static final BasicStroke STROKE_LANE = stroke(4);
    static final BasicStroke STROKE_CARD_ACTIVE = stroke(3);
    static final BasicStroke STROKE_CARD_IDLE = stroke(2);
    static final BasicStroke STROKE_WORD_COMPLETE_AURA =
            stroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_WORD_COMPLETE_CORE =
            stroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_PIERCE_AURA = stroke(13, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_PIERCE_CORE = stroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_LASER_AURA = stroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_LASER_CORE = stroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_LASER_HOT = stroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_SURGE_AURA = stroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_SURGE_CORE = stroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_CONTINUOUS = stroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_BASIC = stroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    static final BasicStroke STROKE_IMPACT = stroke(3);
    static final Color COLOR_BACKGROUND = new Color(21, 24, 30);
    static final Color COLOR_PANEL_DARK = new Color(12, 15, 22, 232);
    static final Color COLOR_INK_DARK = new Color(12, 14, 18);
    static final Color COLOR_TARGET_NORMAL = new Color(94, 142, 220);
    static final Color COLOR_TARGET_FAST = new Color(229, 128, 78);
    static final Color COLOR_TARGET_TANK = new Color(180, 116, 210);
    static final Color COLOR_TARGET_SWITCHER = new Color(216, 78, 106);
    static final Color COLOR_TARGET_UPGRADE = new Color(80, 190, 130);
    static final Color COLOR_TARGET_BOSS = new Color(220, 70, 70);
    static final Color COLOR_BOSS_WARNING = new Color(255, 66, 70, 150);
    static final Color COLOR_BOSS_PROJECTILE = new Color(255, 116, 68, 230);
    static final Color COLOR_BOSS_PROJECTILE_CORE = new Color(255, 230, 180, 245);
    static final Color COLOR_PIERCE_AURA = new Color(70, 225, 255, 65);
    static final Color COLOR_PIERCE_CORE = new Color(115, 245, 255, 190);
    static final Color COLOR_PIERCE_HEAD = new Color(45, 215, 255);
    static final Color COLOR_LASER_AURA = new Color(255, 25, 50, 95);
    static final Color COLOR_LASER_CORE = new Color(255, 72, 78, 220);
    static final Color COLOR_LASER_HOT = new Color(255, 225, 205, 245);
    static final Color COLOR_LASER_SPARK = new Color(255, 96, 70, 185);
    static final Color COLOR_LASER_BURN = new Color(255, 132, 38, 210);
    static final Color COLOR_LASER_EMBER = new Color(255, 208, 82, 190);
    static final Color COLOR_LASER_MAX = new Color(255, 226, 72, 235);
    static final Color COLOR_HOMING_TRAIL = new Color(255, 84, 82, 116);
    static final Color COLOR_HOMING_HEAD = new Color(255, 72, 76);
    static final Color COLOR_HOMING_CORE = new Color(255, 235, 220);
    static final Color COLOR_HOMING_ORANGE = new Color(255, 150, 42);
    static final Color COLOR_HOMING_YELLOW = new Color(255, 226, 48);
    static final Color COLOR_HOMING_GREEN = new Color(90, 238, 115);
    static final Color COLOR_HOMING_BLUE = new Color(82, 178, 255);
    static final Color COLOR_HOMING_PURPLE = new Color(184, 105, 255);
    static final Color COLOR_SURGE_AURA = new Color(255, 232, 86, 145);
    static final Color COLOR_SURGE_CORE = new Color(255, 246, 165, 225);
    static final Color COLOR_SURGE_GLOW = new Color(255, 205, 58, 105);
    static final Color COLOR_SURGE_HEAD = new Color(255, 226, 72);
    static final Color COLOR_BURST_AURA = new Color(255, 120, 82, 105);
    static final Color COLOR_BURST_HEAD = new Color(255, 174, 92);
    static final Color COLOR_CONTINUOUS_TRAIL = new Color(82, 180, 255, 62);
    static final Color COLOR_CONTINUOUS_HEAD = new Color(72, 172, 255);
    static final Color COLOR_BASIC_TRAIL = new Color(120, 170, 255, 120);
    static final Color COLOR_BASIC_HEAD = new Color(130, 175, 255);
    static final Color COLOR_BUBBLE_AURA = new Color(145, 215, 255, 72);
    static final Color COLOR_BUBBLE_CORE = new Color(190, 236, 255, 168);
    static final Color COLOR_BUBBLE_TEXT = new Color(255, 255, 255);
    static final Color COLOR_DRY_ICE_TRAIL = new Color(162, 232, 255, 135);
    static final Color COLOR_DRY_ICE_HEAD = new Color(194, 246, 255);
    static final Color COLOR_DRY_ICE_CORE = new Color(94, 206, 255, 185);
    static final Color COLOR_DRY_ICE_PULSE = new Color(154, 228, 255);
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
    static final Color COLOR_RARITY_RED = new Color(255, 82, 92);
    static final Color COLOR_GOLD_DRONE = new Color(255, 226, 118);
    static final Color COLOR_OVERFLOW = new Color(255, 244, 132);
    static final Color COLOR_MELEE = new Color(255, 112, 88);
    static final Color COLOR_RED_EYE = new Color(255, 62, 82);
    static final Color COLOR_UPGRADE_READY = new Color(255, 218, 86);
    static final Color COLOR_UPGRADE_READY_GLOW = new Color(255, 218, 86, 82);
    static final String[] WORDS = {
            "code", "loop", "array", "class", "method", "object", "static", "return",
            "debug", "public", "private", "string", "random", "switch", "combo", "vector",
            "target", "health", "typing", "battle", "upgrade", "thread", "focus", "runner",
            "syntax", "compile", "java", "panel", "timer", "stack", "queue", "index",
            "value", "field", "token", "input", "output", "screen", "sprite", "bullet",
            "damage", "shield", "energy", "charge", "stream", "strike", "rocket", "signal",
            "system", "branch", "buffer", "cursor", "update", "render", "canvas", "engine",
            "player", "enemy", "motion", "pickup", "attack", "defend", "survive", "perfect",
            "control", "pattern", "through", "forward", "scanner", "boolean", "integer", "double",
            "extends", "compare", "lambda", "module", "import", "export", "server", "client",
            "socket", "packet", "router", "cache", "query", "schema", "filter", "search",
            "sorter", "merge", "commit", "origin", "remote", "deploy", "script", "layout",
            "margin", "border", "button", "dialog", "slider", "toggle", "option", "select",
            "bitmap", "shader", "matrix", "vertex", "camera", "sample", "parser", "format",
            "memory", "mutex", "atomic", "future", "worker", "reader", "writer", "logger",
            "metric", "trace", "event", "action", "state", "store", "reduce", "effect",
            "widget", "window", "prompt", "scalar", "normal", "binary", "linear", "bucket",
            "record", "entity", "model", "domain", "policy", "access", "secret", "cipher",
            "hash", "salt", "nonce", "verify", "encode", "decode", "upload", "backup",
            "restore", "mirror", "retry", "agent", "align", "alloc", "angle", "armor",
            "asset", "async", "audio", "batch", "beacon", "block", "bound", "bounds",
            "build", "caller", "chain", "chart", "check", "chunk", "circle", "claim",
            "clean", "clone", "cloud", "codec", "color", "column", "config", "cookie",
            "copper", "craft", "create", "daemon", "delta", "depth", "direct", "docker",
            "driver", "editor", "error", "escape", "factor", "fetch", "fiber", "frame",
            "freeze", "fusion", "gauge", "global", "graph", "guard", "handle", "header",
            "helper", "hidden", "hook", "hover", "icon", "image", "insert", "intent",
            "kernel", "launch", "layer", "ledger", "light", "limit", "loader", "local",
            "logic", "macro", "marker", "master", "match", "media", "mobile", "modal",
            "mouse", "native", "network", "node", "notify", "number", "page", "paint",
            "parent", "patch", "pause", "pixel", "plugin", "portal", "preset", "probe",
            "profile", "proxy", "pulse", "raster", "region", "resize", "result", "rotate",
            "route", "runtime", "scope", "scroll", "secure", "sensor", "shadow", "shape",
            "shared", "shell", "shift", "short", "solver", "source", "space", "stage",
            "status", "storage", "stride", "submit", "symbol", "table", "task", "theme",
            "ticker", "tile", "track", "tree", "tunnel", "unit", "unlock", "valid",
            "vision", "voice", "wallet", "watch", "world", "yield", "anchor", "arena",
            "basic", "blend", "boost", "break", "bridge", "bright", "broker", "cancel",
            "catch", "center", "choice", "clamp", "clock", "close", "compact", "credit",
            "danger", "detail", "device", "effort", "extend", "flight", "follow", "inject",
            "inside", "leader", "legend", "listen", "market", "repair", "strict"
    };

    static final class HudMetric {
        final String id;
        final String label;
        final String value;
        final String detail;
        final Color accent;
        final double progress;

        HudMetric(String id, String label, String value, String detail, Color accent) {
            this(id, label, value, detail, accent, -1.0);
        }

        HudMetric(String id, String label, String value, String detail, Color accent, double progress) {
            this.id = id;
            this.label = label;
            this.value = value;
            this.detail = detail;
            this.accent = accent;
            this.progress = progress;
        }
    }

    final Random random = new Random(7);
    final List<Target> targets = new ArrayList<Target>();
    final List<Bullet> bullets = new ArrayList<Bullet>();
    final List<BulletTrailParticle> bulletTrailParticles = new ArrayList<BulletTrailParticle>();
    final List<GoldDrone> goldDrones = new ArrayList<GoldDrone>();
    final List<BossProjectile> bossProjectiles = new ArrayList<BossProjectile>();
    final List<Impact> impacts = new ArrayList<Impact>();
    final List<IcePulse> icePulses = new ArrayList<IcePulse>();
    final List<BreakParticle> breakParticles = new ArrayList<BreakParticle>();
    final List<XpOrb> xpOrbs = new ArrayList<XpOrb>();
    final String[] dynamicHudIds = new String[MAX_DYNAMIC_HUD_ITEMS];
    final String[] dynamicHudValues = new String[MAX_DYNAMIC_HUD_ITEMS];
    final String[] dynamicHudPreviousValues = new String[MAX_DYNAMIC_HUD_ITEMS];
    final int[] dynamicHudTransitionTicks = new int[MAX_DYNAMIC_HUD_ITEMS];
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
    int spawnCooldown = logicTicks(25);
    int bossCooldownKills = 10;
    int autoFireCooldown = 0;
    int continuousSurgeTicks = 0;
    double autoCannonStackPercent = 0.0;
    int sustainedLaserTicks = 0;
    int sustainedLaserChargeTicks = 0;
    int sustainedLaserCooldown = 0;
    String autoRateHudValue = "";
    String autoRateHudPreviousValue = "";
    int autoRateHudTransitionTicks = 0;
    String laserDamageHudValue = "";
    String laserDamageHudPreviousValue = "";
    int laserDamageHudTransitionTicks = 0;
    String basicDamageHudValue = "";
    String basicDamageHudPreviousValue = "";
    int basicDamageHudTransitionTicks = 0;
    String shotgunPelletHudValue = "";
    String shotgunPelletHudPreviousValue = "";
    int shotgunPelletHudTransitionTicks = 0;
    int basicWeaponKillDamageBonusPercent = 0;
    int wrongFlashTicks = 0;
    int wrongFlashLane = -1;
    int inputPulseTicks = 0;
    int completePulseTicks = 0;
    int completePulseLane = -1;
    int messageTicks = logicTicks(220);
    int screenFlashTicks = 0;
    int bossLaserFlashTicks = 0;
    int bossLaserFlashLane = -1;
    int baseDamage = 3;
    int perfectBonus = 0;
    int correctTypedChars = 0;
    int runStartTick = 0;
    int xp = 0;
    int xpToNext = INITIAL_XP_TO_NEXT;
    int upgradeLevel = 0;
    int pendingUpgradeChoices = 0;
    int selectedUpgradeIndex = 0;
    int selectedOverviewCardIndex = 0;
    int damageBonusPercent = 0;
    int fireRateBonusPercent = 0;
    int maxHpUpgradeBonus = 0;
    int phaseSwitchLevel = 0;
    int phaseSwitchTicks = 0;
    int phaseSwitchCooldownTicks = 0;
    int bossBreakerLevel = 0;
    int crossfeedLevel = 0;
    int crossfeedCooldownTicks = 0;
    int crossfeedBonusTicks = 0;
    int blueUpgradeCount = 0;
    int longWordRewardLevel = 0;
    int shortWordQuickshotLevel = 0;
    int finalLetterBurstLevel = 0;
    int vowelConvergenceLevel = 0;
    int hardConsonantBreakLevel = 0;
    int selectedTestUpgradeIndex = 0;
    final int[] effectLevels = new int[UpgradeEffect.values().length];
    final int[] weaponLevels = new int[UpgradeEffect.values().length];
    final int[] laneBarrierCharges = new int[2];
    final int[] laneSlowTicks = new int[2];
    final int[][] wordTriggerFlashTicks = new int[2][WORD_TRIGGER_COUNT];
    final boolean[] completedLaneHighlightSuppressed = new boolean[2];
    final UpgradeEffect[] highTalents = new UpgradeEffect[MAX_RED_WEAPONS];
    final UpgradeEffect[] goldTalents = new UpgradeEffect[MAX_GOLD_TALENTS];
    UpgradeCard pendingHighTalent = null;
    UpgradeInventoryCard pendingSellCard = null;
    ChoiceMode sellConfirmReturnMode = ChoiceMode.NONE;
    boolean sellConfirmReturnOverviewSelectionActive = false;
    boolean returnToTestBackendAfterHighReplace = false;
    boolean bossRewardChoice = false;
    boolean overviewSelectionActive = false;
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
    boolean currentWordReachedHalfPrefix = false;
    boolean currentWordHadDualPrefix = false;
    boolean completedWordWasClean = false;
    boolean completedWordUsedBackspace = false;
    boolean completedWordStartedAfterError = false;
    boolean completedWordUniqueFirst = false;
    boolean completedWordReachedHalfPrefix = false;
    boolean completedWordHadDualPrefix = false;
    boolean completedWordAlternated = false;
    boolean completedWordSwitchedLane = false;
    boolean shiftHeld = false;
    boolean currentWordShifted = false;
    boolean completedWordShifted = false;
    boolean pendingAttackShifted = false;
    boolean totemReviveAvailable = false;
    boolean adrenalineHpPenaltyApplied = false;
    int overflowDamageBank = 0;
    int overflowReadyPulseTicks = 0;
    int meleeRamTicks = 0;
    int screenShakeTicks = 0;
    double meleeRamStartY = LANE_Y[0];
    double meleeRamTargetX = PLAYER_X;
    double meleeRamTargetY = LANE_Y[0];

    String lastCompletedWord = "";
    String message = "Type lane words to fight. Collect XP balls to grow the build.";
    String messageZh = "输入赛道词战斗。收集 XP 球强化构筑。";
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
        setPreferredSize(new Dimension(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT));
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
        screenFlashTicks = Math.max(0, screenFlashTicks - 1);
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
        bossLaserFlashTicks = Math.max(0, bossLaserFlashTicks - 1);
        overflowReadyPulseTicks = Math.max(0, overflowReadyPulseTicks - 1);
        screenShakeTicks = Math.max(0, screenShakeTicks - 1);
        recentErrorTicks = Math.max(0, recentErrorTicks - 1);
        if (recentErrorTicks == 0) {
            nextWordStartsAfterError = false;
        }
        longFocusTicks = Math.max(0, longFocusTicks - 1);
        laneSwitchAnimationTicks = Math.max(0, laneSwitchAnimationTicks - 1);
        meleeRamTicks = Math.max(0, meleeRamTicks - 1);
        if (pendingLaneAttack && laneSwitchAnimationTicks <= 0) {
            finishPendingLaneAttack();
        }
        for (int i = 0; i < laneSlowTicks.length; i++) {
            laneSlowTicks[i] = Math.max(0, laneSlowTicks[i] - 1);
        }
        for (int laneIndex = 0; laneIndex < wordTriggerFlashTicks.length; laneIndex++) {
            for (int trigger = 0; trigger < WORD_TRIGGER_COUNT; trigger++) {
                wordTriggerFlashTicks[laneIndex][trigger] = Math.max(0, wordTriggerFlashTicks[laneIndex][trigger] - 1);
            }
        }

        if (spawnCooldown <= 0 && !hasBoss() && !bossReadyToSpawn()) {
            spawnRandomTarget();
            spawnCooldown = nextSpawnCooldown();
        }

        for (Target target : targets) {
            target.previousX = target.x;
            double speed = target.speed * goldEnemySpeedMultiplier()
                    * (target.kind == TargetKind.BOSS ? 1.0 : laneSpeedMultiplier(target.lane));
            boolean frozenThisFrame = target.freezeTicks > 0;
            if (target.kind == TargetKind.BOSS && target.bossDeathAnimating) {
                updateBossDeathAnimation(target);
                continue;
            }
            boolean bossKnockbackThisFrame = false;
            if (target.freezeTicks > 0) {
                speed = 0.0;
                target.freezeTicks--;
                if (target.freezeTicks == 0 && weaponLevel(UpgradeEffect.DRY_ICE_BULLET) >= 2
                        && target.hp > 0 && !target.dead) {
                    applyDryIceThawPulse(target);
                }
            } else if (target.slowTicks > 0) {
                speed *= target.slowMultiplier;
                target.slowTicks--;
            } else {
                target.slowMultiplier = 0.55;
            }
            target.markTicks = Math.max(0, target.markTicks - 1);
            if (target.kind == TargetKind.BOSS && target.bossKnockbackTicks > 0) {
                updateBossKnockback(target);
                bossKnockbackThisFrame = true;
            } else {
                target.x -= gameplayStep(speed);
            }
            if (target.kind == TargetKind.BOSS) {
                if (!frozenThisFrame && !bossKnockbackThisFrame) {
                    updateBossAttack(target);
                }
            }
        }
        updateLaserBurns();
        updateGoldDrones();
        updateMeleeRegeneration();

        if (!pendingLaneAttack && hasHighTalent(UpgradeEffect.RHYTHM_CANNON)) {
            runAutoFire();
        }
        updateSustainedLaser();

        updateBulletTrailParticles();
        updateBullets();
        updateImpacts();
        updateIcePulses();
        updateBreakParticles();
        updateBossProjectiles();
        updateXpOrbs();
        updateCombatStatusHud();
        handleCollisions();
        removeDeadAndEscaped();
        phaseSwitchTicks = Math.max(0, phaseSwitchTicks - 1);
        phaseSwitchCooldownTicks = Math.max(0, phaseSwitchCooldownTicks - 1);
        crossfeedCooldownTicks = Math.max(0, crossfeedCooldownTicks - 1);
        crossfeedBonusTicks = Math.max(0, crossfeedBonusTicks - 1);

        if (bossReadyToSpawn() && !hasActiveNonBossTarget()) {
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
        resetBossAttackTimer(boss, BOSS_ATTACK_BASE_COOLDOWN_TICKS / 2);
        boss.previousX = boss.x;
        targets.add(boss);
        typingLane = -1;
        typed = "";
        showMessage("Boss " + bossLevel + ": red weapon reward.",
                "Boss " + bossLevel + "：红色武器奖励。");
    }

    void resetBossAttackTimer(Target boss) {
        resetBossAttackTimer(boss, BOSS_ATTACK_BASE_COOLDOWN_TICKS
                + random.nextInt(BOSS_ATTACK_JITTER_TICKS + 1));
    }

    void resetBossAttackTimer(Target boss, int ticks) {
        boss.bossAttackCooldownTicks = Math.max(1, ticks);
        boss.bossLaserWarningTicks = 0;
        boss.bossLaserLane = -1;
    }

    int bossLaserWarningDurationTicks() {
        return scaledBossTimer(BOSS_LASER_WARNING_TICKS, difficulty.bossLaserWarningMultiplier);
    }

    int bossBarrageIntervalTicks() {
        return scaledBossTimer(BOSS_BARRAGE_INTERVAL_TICKS, difficulty.bossBarrageIntervalMultiplier);
    }

    int bossBarrageJitterTicks() {
        return Math.max(0, scaledBossTimer(BOSS_BARRAGE_JITTER_TICKS, difficulty.bossBarrageIntervalMultiplier));
    }

    int scaledBossTimer(int baseTicks, double multiplier) {
        return Math.max(1, (int) Math.round(baseTicks * multiplier));
    }

    void updateBossAttack(Target boss) {
        if (boss.hp <= 0 || boss.dead || boss.bossDeathAnimating) {
            return;
        }
        if (boss.hp * 2 > boss.maxHp) {
            updateBossLaserAttack(boss);
        } else {
            updateBossBarrageAttack(boss);
        }
    }

    void updateBossKnockback(Target boss) {
        int duration = Math.max(1, boss.bossKnockbackDurationTicks);
        int elapsed = duration - boss.bossKnockbackTicks + 1;
        double progress = Math.max(0.0, Math.min(1.0, elapsed / (double) duration));
        double eased = easeOutCubic(progress);
        boss.x = boss.bossKnockbackStartX
                + (boss.bossKnockbackTargetX - boss.bossKnockbackStartX) * eased;
        boss.bossKnockbackTicks--;
        if (boss.bossKnockbackTicks <= 0) {
            boss.bossKnockbackTicks = 0;
            boss.x = boss.bossKnockbackTargetX;
        }
    }

    void updateBossLaserAttack(Target boss) {
        if (boss.bossLaserWarningTicks > 0) {
            boss.bossLaserWarningTicks--;
            if (boss.bossLaserWarningTicks <= 0) {
                fireBossLaser(boss, boss.bossLaserLane < 0 ? random.nextInt(2) : boss.bossLaserLane);
                resetBossAttackTimer(boss);
            }
            return;
        }
        boss.bossAttackCooldownTicks--;
        if (boss.bossAttackCooldownTicks <= 0) {
            boss.bossLaserLane = random.nextInt(2);
            boss.bossLaserWarningTicks = bossLaserWarningDurationTicks();
            showMessage("Boss laser warning: dodge the marked lane.",
                    "Boss 激光预警：离开被标记的 lane。");
        }
    }

    void updateBossBarrageAttack(Target boss) {
        boss.bossLaserWarningTicks = 0;
        boss.bossLaserLane = -1;
        boss.bossAttackCooldownTicks--;
        if (boss.bossAttackCooldownTicks <= 0) {
            spawnBossProjectile(boss, random.nextInt(2));
            boss.bossAttackCooldownTicks = bossBarrageIntervalTicks()
                    + random.nextInt(bossBarrageJitterTicks() + 1);
        }
    }

    void fireBossLaser(Target boss, int attackLane) {
        bossLaserFlashLane = attackLane;
        bossLaserFlashTicks = logicTicks(14);
        spawnBossLaserParticles(boss, attackLane);
        if (lane == attackLane) {
            applyBossRemoteDamage(BOSS_LASER_DAMAGE + bossLevel, "boss laser", "Boss 激光");
        } else {
            impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0, COLOR_BOSS_WARNING));
            showMessage("Boss laser missed your lane.", "Boss 激光擦过另一条 lane。");
        }
    }

    void spawnBossLaserParticles(Target boss, int attackLane) {
        double startX = boss.x - targetHalfWidth(TargetKind.BOSS);
        double endX = PLAYER_X + worldAmount(20);
        double y = LANE_Y[attackLane];
        int count = 34;
        for (int i = 0; i < count; i++) {
            double t = (i + random.nextDouble()) / count;
            double x = startX + (endX - startX) * t;
            double offsetY = random.nextDouble() * 26.0 - 13.0;
            double vx = -0.7 - random.nextDouble() * 1.6;
            double vy = random.nextDouble() * 1.4 - 0.7;
            double size = 3.0 + random.nextDouble() * 7.5;
            int life = 10 + random.nextInt(12);
            int sides = 3 + random.nextInt(4);
            bulletTrailParticles.add(new BulletTrailParticle(x, y + offsetY, vx, vy, size, life,
                    random.nextBoolean() ? COLOR_LASER_HOT : COLOR_BOSS_WARNING, sides,
                    random.nextDouble() * Math.PI * 2.0, (random.nextDouble() - 0.5) * 0.42));
        }
    }

    void spawnBossProjectile(Target boss, int attackLane) {
        BossProjectile projectile = new BossProjectile(boss.x - targetHalfWidth(TargetKind.BOSS), attackLane,
                BOSS_BARRAGE_DAMAGE + bossLevel / 2, BOSS_BARRAGE_SPEED + bossLevel * 0.35);
        projectile.waveOffset = random.nextDouble() * Math.PI * 2.0;
        bossProjectiles.add(projectile);
        showMessage("Boss barrage: switch lanes between shots.",
                "Boss 弹幕：在弹幕间隔中切换 lane 躲避。");
    }

    void updateBossProjectiles() {
        Iterator<BossProjectile> iterator = bossProjectiles.iterator();
        while (iterator.hasNext()) {
            BossProjectile projectile = iterator.next();
            projectile.previousX = projectile.x;
            projectile.previousY = projectile.y;
            projectile.x -= gameplayStep(projectile.speed);
            projectile.y = LANE_Y[projectile.lane]
                    + Math.sin((projectile.lifeTicks * 0.18 + projectile.waveOffset) * GAMEPLAY_STEP_SCALE)
                            * worldAmount(9.0);
            projectile.lifeTicks--;
            spawnBossProjectileTrail(projectile);
            if (projectile.lane == lane && projectile.x <= PLAYER_X + projectile.radius
                    && projectile.previousX >= PLAYER_X - projectile.radius) {
                applyBossRemoteDamage(projectile.damage, "boss barrage", "Boss 弹幕");
                iterator.remove();
            } else if (projectile.x < PLAYER_X - worldAmount(90) || projectile.lifeTicks <= 0) {
                iterator.remove();
            }
        }
    }

    void spawnBossProjectileTrail(BossProjectile projectile) {
        double dx = projectile.x - projectile.previousX;
        double dy = projectile.y - projectile.previousY;
        int count = 2 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double t = (i + random.nextDouble()) / count;
            double x = projectile.previousX + dx * t;
            double y = projectile.previousY + dy * t + random.nextDouble() * 12.0 - 6.0;
            bulletTrailParticles.add(new BulletTrailParticle(x, y, 0.45 + random.nextDouble() * 0.85,
                    random.nextDouble() * 0.7 - 0.35, 3.6 + random.nextDouble() * 4.6,
                    9 + random.nextInt(8), COLOR_BOSS_PROJECTILE));
        }
    }

    void applyBossRemoteDamage(int damage, String reason, String reasonZh) {
        int finalDamage = testInvincible ? 0 : Math.max(0, damage);
        impacts.add(new Impact(PLAYER_X, LANE_Y[lane], finalDamage, COLOR_BOSS_WARNING));
        if (finalDamage <= 0) {
            showMessage("Boss attack deflected by test invincibility.",
                    "测试无敌拦下了 Boss 远程攻击。");
            return;
        }
        hp -= finalDamage;
        combo = 0;
        setDeathReason(reason, reasonZh);
        showMessage("Boss hit: -" + finalDamage + " HP. Dodge the warning lane.",
                "Boss 命中：-" + finalDamage + " HP。躲开预警 lane。");
        if (hp <= 0) {
            endGame();
        }
    }

    int pressureLevel() {
        return Math.min(14, tick / logicTicks(430) + kills / 9 + bossLevel * 2);
    }

    int nextSpawnCooldown() {
        int base = BASE_SPAWN_COOLDOWN - random.nextInt(SPAWN_COOLDOWN_JITTER);
        return Math.max(12, base);
    }

    void scaleTarget(Target target) {
        int pressure = pressureLevel();
        int hpPressure = timeHpPressure() + kills / 12 + bossLevel * 2;
        double speedMultiplier = progressSpeedMultiplier();
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
            speedMultiplier = (1.0 + pressure * 0.035) * difficulty.speedMultiplier;
        }
        target.hp += hpBonus;
        target.maxHp += hpBonus;
        target.speed *= speedMultiplier;
    }

    double progressSpeedMultiplier() {
        return (1.0 + pressureLevel() * 0.075) * difficulty.speedMultiplier;
    }

    int timeHpPressure() {
        return tick / logicTicks(300);
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

    double goldEnemySpeedMultiplier() {
        return hasGoldTalent(UpgradeEffect.ADRENALINE) ? 1.2 : 1.0;
    }

    void updateMeleeRegeneration() {
        if (!hasGoldTalent(UpgradeEffect.MELEE) || hp >= maxHp || MELEE_REGEN_INTERVAL_TICKS <= 0) {
            return;
        }
        if (tick % MELEE_REGEN_INTERVAL_TICKS == 0) {
            hp = Math.min(maxHp, hp + 1);
            impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0, COLOR_MELEE));
        }
    }

    void updateGoldDrones() {
        Iterator<GoldDrone> iterator = goldDrones.iterator();
        while (iterator.hasNext()) {
            GoldDrone drone = iterator.next();
            drone.previousX = drone.x;
            drone.previousY = drone.y;
            drone.lifeTicks--;
            drone.cooldownTicks = Math.max(0, drone.cooldownTicks - 1);
            drone.laserTicks = Math.max(0, drone.laserTicks - 1);
            Target target = nearestTargetForDrone(drone);
            if (target != null) {
                double desiredX = Math.max(PLAYER_X + worldAmount(80),
                        Math.min(LANE_RIGHT_X - worldAmount(70),
                                target.x - targetHalfWidth(target.kind) - worldAmount(64)));
                double desiredY = targetCenterY(target)
                        + Math.sin((tick * 0.08 + drone.waveOffset) * GAMEPLAY_STEP_SCALE) * worldAmount(34);
                moveDroneToward(drone, desiredX, desiredY);
                double distance = Math.hypot(target.x - drone.x, targetCenterY(target) - drone.y);
                if (drone.cooldownTicks <= 0 && distance <= GOLD_DRONE_ATTACK_RANGE) {
                    fireGoldDroneLaser(drone, target);
                }
            } else {
                double driftX = Math.max(PLAYER_X + worldAmount(120),
                        Math.min(LANE_RIGHT_X - worldAmount(130),
                                drone.x + Math.sin((tick * 0.035 + drone.waveOffset) * GAMEPLAY_STEP_SCALE)
                                        * gameplayStep(1.6)));
                double driftY = drone.y + Math.cos((tick * 0.042 + drone.waveOffset) * GAMEPLAY_STEP_SCALE)
                        * gameplayStep(1.1);
                moveDroneToward(drone, driftX, driftY);
            }
            if (tick % 3 == 0) {
                bulletTrailParticles.add(new BulletTrailParticle(drone.x, drone.y,
                        -0.35 - random.nextDouble() * 0.65, random.nextDouble() * 0.8 - 0.4,
                        2.2 + random.nextDouble() * 2.6, 10 + random.nextInt(7),
                        withAlpha(drone.color, 155)));
            }
            if (drone.lifeTicks <= 0) {
                iterator.remove();
            }
        }
    }

    void moveDroneToward(GoldDrone drone, double desiredX, double desiredY) {
        double dx = desiredX - drone.x;
        double dy = desiredY - drone.y;
        double distance = Math.max(0.001, Math.hypot(dx, dy));
        double step = Math.min(distance, gameplayStep(GOLD_DRONE_MOVE_SPEED));
        drone.x += dx / distance * step;
        drone.y += dy / distance * step;
    }

    Target nearestTargetForDrone(GoldDrone drone) {
        Target best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Target target : targets) {
            if (target.dead || target.hp <= 0 || target.bossDeathAnimating) {
                continue;
            }
            double dx = target.x - drone.x;
            double dy = targetCenterY(target) - drone.y;
            double distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = target;
            }
        }
        return best;
    }

    void fireGoldDroneLaser(GoldDrone drone, Target target) {
        int damage = Math.max(1, GOLD_DRONE_DAMAGE);
        target.hp -= damage;
        target.hitFlash = Math.max(target.hitFlash, logicTicks(6));
        drone.laserStartX = drone.x;
        drone.laserStartY = drone.y;
        drone.laserEndX = target.x;
        drone.laserEndY = targetCenterY(target);
        drone.laserTicks = logicTicks(12);
        drone.cooldownTicks = GOLD_DRONE_ATTACK_INTERVAL_TICKS;
        impacts.add(new Impact(target.x, targetCenterY(target), damage, drone.color));
        for (int i = 0; i < 9; i++) {
            double t = (i + random.nextDouble()) / 9.0;
            double x = drone.laserStartX + (drone.laserEndX - drone.laserStartX) * t;
            double y = drone.laserStartY + (drone.laserEndY - drone.laserStartY) * t;
            bulletTrailParticles.add(new BulletTrailParticle(x, y, random.nextDouble() * 0.8 - 0.4,
                    random.nextDouble() * 0.8 - 0.4, 2.4 + random.nextDouble() * 3.8,
                    9 + random.nextInt(7), drone.color, 3 + random.nextInt(3),
                    random.nextDouble() * Math.PI * 2.0, (random.nextDouble() - 0.5) * 0.34));
        }
    }

    void spawnGoldDrone(Target target) {
        if (!hasGoldTalent(UpgradeEffect.DRONE_SWARM) || target.kind == TargetKind.UPGRADE) {
            return;
        }
        while (goldDrones.size() >= MAX_GOLD_DRONES) {
            goldDrones.remove(0);
        }
        Color color = colorFor(target);
        GoldDrone drone = new GoldDrone(target.x, targetCenterY(target), color, GOLD_DRONE_LIFE_TICKS,
                GOLD_DRONE_ATTACK_INTERVAL_TICKS / 2 + random.nextInt(Math.max(1, GOLD_DRONE_ATTACK_INTERVAL_TICKS / 2)),
                random.nextDouble() * Math.PI * 2.0);
        goldDrones.add(drone);
        for (int i = 0; i < 14; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double speed = 0.8 + random.nextDouble() * 2.6;
            bulletTrailParticles.add(new BulletTrailParticle(drone.x, drone.y, Math.cos(angle) * speed,
                    Math.sin(angle) * speed, 2.5 + random.nextDouble() * 4.2,
                    12 + random.nextInt(9), color, 3 + random.nextInt(4),
                    random.nextDouble() * Math.PI * 2.0, (random.nextDouble() - 0.5) * 0.38));
        }
    }

    void runAutoFire() {
        if (hasGoldTalent(UpgradeEffect.MELEE) && hasBoss()) {
            decayAutoCannonStack();
            return;
        }
        continuousSurgeTicks = Math.max(0, continuousSurgeTicks - 1);
        autoFireCooldown--;
        if (autoFireCooldown > 0) {
            decayAutoCannonStack();
            return;
        }
        Target target = nearestTargetInLane(lane);
        double fireRateMultiplier = autoCannonFireRateMultiplier();
        boolean goldShot = fireRateMultiplier >= 3.0;
        int damage = goldShot ? continuousSurgeDamage() : continuousChipDamage();
        damage = scaledDamage(damage, target);
        fireAutoCannonBullet(lane, damage, goldShot ? BulletKind.CONTINUOUS_SURGE : BulletKind.CONTINUOUS,
                goldShot);
        autoFireCooldown = autoCannonCooldownTicks();
        decayAutoCannonStack();
    }

    void decayAutoCannonStack() {
        if (autoCannonStackPercent > 0.0) {
            autoCannonStackPercent = Math.max(0.0,
                    autoCannonStackPercent - AUTO_CANNON_STACK_DECAY_PER_SECOND / Math.max(1, LOGIC_FPS));
        }
    }

    double autoCannonFireRateMultiplier() {
        int weaponLevel = weaponLevel(UpgradeEffect.RHYTHM_CANNON);
        double multiplier = 1.0;
        if (weaponLevel >= 3) {
            multiplier += autoCannonStackPercent / 100.0;
        } else if (weaponLevel >= 2 && continuousSurgeTicks > 0) {
            multiplier = 1.0 + 4.0 * continuousSurgeTicks / Math.max(1.0, AUTO_CANNON_DECAY_SURGE_TICKS);
        } else if (continuousSurgeTicks > 0) {
            multiplier = 5.0;
        }
        return Math.max(1.0, multiplier);
    }

    int autoCannonCooldownTicks() {
        double multiplier = autoCannonFireRateMultiplier();
        return Math.max(AUTO_CANNON_PEAK_COOLDOWN,
                (int) Math.ceil(AUTO_CANNON_BASE_COOLDOWN / multiplier));
    }

    void fireAutoCannonBullet(int attackLane, int damage, BulletKind kind, boolean particleTrail) {
        double startOffset = random.nextDouble() * 18.0 - 9.0;
        Bullet bullet = new Bullet(PLAYER_X + worldAmount(28 + startOffset), attackLane, damage, 1, kind,
                particleTrail);
        bullet.yOffset = worldAmount(random.nextDouble() * 28.0 - 14.0);
        bullet.y = LANE_Y[attackLane] + bullet.yOffset;
        bullet.previousY = bullet.y;
        bullet.launchY = bullet.y;
        bullets.add(bullet);
    }

    void updateSustainedLaser() {
        if (weaponLevel(UpgradeEffect.FROST_FIELD) < MAX_WEAPON_LEVEL) {
            sustainedLaserTicks = 0;
            sustainedLaserChargeTicks = 0;
            sustainedLaserCooldown = 0;
            return;
        }
        if (hasGoldTalent(UpgradeEffect.MELEE) && hasBoss()) {
            return;
        }
        if (sustainedLaserTicks <= 0) {
            sustainedLaserChargeTicks = 0;
            sustainedLaserCooldown = 0;
            return;
        }
        sustainedLaserTicks--;
        sustainedLaserChargeTicks = Math.min(SUSTAINED_LASER_RAMP_TICKS, sustainedLaserChargeTicks + 1);
        sustainedLaserCooldown--;
        if (sustainedLaserCooldown > 0) {
            return;
        }
        int laserPowerPercent = sustainedLaserDamagePercent();
        int damage = Math.max(1, scaledDamage(1 + perfectBonus / 4, nearestTargetInLane(lane))
                * laserPowerPercent / 100);
        fireLaserVolley(lane, damage, laserPowerPercent);
        sustainedLaserCooldown = SUSTAINED_LASER_INTERVAL_TICKS;
    }

    int sustainedLaserDamagePercent() {
        if (sustainedLaserChargeTicks >= SUSTAINED_LASER_RAMP_TICKS) {
            return 300;
        }
        int quantum = Math.max(1, LASER_DAMAGE_PERCENT_UPDATE_TICKS);
        int quantizedTicks = sustainedLaserChargeTicks / quantum * quantum;
        return 100 + Math.min(200, quantizedTicks * 200 / Math.max(1, SUSTAINED_LASER_RAMP_TICKS));
    }

    void updateCombatStatusHud() {
        for (int i = 0; i < dynamicHudTransitionTicks.length; i++) {
            dynamicHudTransitionTicks[i] = Math.max(0, dynamicHudTransitionTicks[i] - 1);
        }
        autoRateHudTransitionTicks = Math.max(0, autoRateHudTransitionTicks - 1);
        laserDamageHudTransitionTicks = Math.max(0, laserDamageHudTransitionTicks - 1);
        basicDamageHudTransitionTicks = Math.max(0, basicDamageHudTransitionTicks - 1);
        shotgunPelletHudTransitionTicks = Math.max(0, shotgunPelletHudTransitionTicks - 1);
        if (hasHighTalent(UpgradeEffect.RHYTHM_CANNON) && autoRateHudValue.length() == 0) {
            setAutoRateHudValue(autoRateHudText());
        }
        if (weaponLevel(UpgradeEffect.FROST_FIELD) >= MAX_WEAPON_LEVEL) {
            setLaserDamageHudValue(laserDamageHudText());
        }
        if (hasHighTalent(UpgradeEffect.BASIC_WEAPON)) {
            setBasicDamageHudValue(basicWeaponMultiplierText());
        }
        if (hasHighTalent(UpgradeEffect.HOMING_SHOTGUN)) {
            setShotgunPelletHudValue(shotgunPelletHudText());
        }
    }

    String autoRateHudText() {
        return "x" + String.format(java.util.Locale.US, "%.2f", autoCannonFireRateMultiplier());
    }

    String laserDamageHudText() {
        return sustainedLaserDamagePercent() + "%";
    }

    String shotgunPelletHudText() {
        return "+" + Math.max(0, homingShotgunPelletCount() - HOMING_SHOTGUN_BASE_PELLETS);
    }

    void setAutoRateHudValue(String value) {
        if (value.equals(autoRateHudValue)) {
            return;
        }
        autoRateHudPreviousValue = autoRateHudValue;
        autoRateHudValue = value;
        autoRateHudTransitionTicks = STATUS_HUD_TRANSITION_TICKS;
    }

    void setLaserDamageHudValue(String value) {
        if (value.equals(laserDamageHudValue)) {
            return;
        }
        laserDamageHudPreviousValue = laserDamageHudValue;
        laserDamageHudValue = value;
        laserDamageHudTransitionTicks = STATUS_HUD_TRANSITION_TICKS;
    }

    void setBasicDamageHudValue(String value) {
        if (value.equals(basicDamageHudValue)) {
            return;
        }
        basicDamageHudPreviousValue = basicDamageHudValue;
        basicDamageHudValue = value;
        basicDamageHudTransitionTicks = STATUS_HUD_TRANSITION_TICKS;
    }

    void setShotgunPelletHudValue(String value) {
        if (value.equals(shotgunPelletHudValue)) {
            return;
        }
        shotgunPelletHudPreviousValue = shotgunPelletHudValue;
        shotgunPelletHudValue = value;
        shotgunPelletHudTransitionTicks = STATUS_HUD_TRANSITION_TICKS;
    }

    int dynamicHudIndex(String id) {
        int emptyIndex = -1;
        for (int i = 0; i < dynamicHudIds.length; i++) {
            if (id.equals(dynamicHudIds[i])) {
                return i;
            }
            if (emptyIndex < 0 && dynamicHudIds[i] == null) {
                emptyIndex = i;
            }
        }
        if (emptyIndex < 0) {
            return 0;
        }
        dynamicHudIds[emptyIndex] = id;
        dynamicHudValues[emptyIndex] = "";
        dynamicHudPreviousValues[emptyIndex] = "";
        dynamicHudTransitionTicks[emptyIndex] = 0;
        return emptyIndex;
    }

    void setDynamicHudValue(String id, String value) {
        int index = dynamicHudIndex(id);
        String current = dynamicHudValues[index] == null ? "" : dynamicHudValues[index];
        if (value.equals(current)) {
            return;
        }
        dynamicHudPreviousValues[index] = current;
        dynamicHudValues[index] = value;
        dynamicHudTransitionTicks[index] = STATUS_HUD_TRANSITION_TICKS;
    }

    String dynamicHudValue(String id) {
        String value = dynamicHudValues[dynamicHudIndex(id)];
        return value == null ? "" : value;
    }

    String dynamicHudPreviousValue(String id) {
        String value = dynamicHudPreviousValues[dynamicHudIndex(id)];
        return value == null ? "" : value;
    }

    int dynamicHudTransitionTicksFor(String id) {
        return dynamicHudTransitionTicks[dynamicHudIndex(id)];
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
            bullet.previousY = bullet.y;
            if (bullet.kind == BulletKind.PIERCE) {
                if (!bullet.beamResolved) {
                    bullet.x = maxBulletX(bullet);
                    bullet.y = LANE_Y[bullet.lane];
                    resolveLaserBeam(bullet);
                    spawnLaserBeamParticles(bullet);
                    bullet.beamResolved = true;
                }
                bullet.lifeTicks--;
                if (bullet.lifeTicks <= 0) {
                    iterator.remove();
                }
                continue;
            }

            double maxX = maxBulletX(bullet);
            if (bullet.kind == BulletKind.HOMING_SHOT) {
                updateHomingShot(bullet);
            } else {
                bullet.x = Math.min(bullet.x + gameplayStep(bullet.speed), maxX);
                bullet.y = LANE_Y[bullet.lane] + bullet.yOffset;
            }
            if (bullet.kind == BulletKind.DRY_ICE) {
                bullet.shapeRotation += bullet.shapeSpin * GAMEPLAY_STEP_SCALE;
            }
            bullet.lifeTicks--;
            if (bullet.particleTrail && bullet.kind != BulletKind.BASIC) {
                spawnBulletTrailParticles(bullet);
            }

            if (bullet.kind == BulletKind.BUBBLE) {
                applyBubbleContacts(bullet);
            } else {
                for (Target target : targets) {
                    if (bullet.pierceLeft <= 0 || target.dead || target.hp <= 0
                            || (target.kind != TargetKind.BOSS && bullet.kind != BulletKind.HOMING_SHOT
                                    && target.lane != bullet.lane)) {
                        continue;
                    }
                    if (bulletAlreadyHit(bullet, target)) {
                        continue;
                    }
                    if (bulletHitsTarget(bullet, target)) {
                        applyBulletHit(bullet, target);
                    }
                }
            }

            if (bullet.x >= maxX || bullet.y < -worldAmount(80) || bullet.y > HEIGHT + worldAmount(80)
                    || bullet.lifeTicks <= 0
                    || (bullet.kind == BulletKind.BUBBLE ? bullet.remainingDamage <= 0 : bullet.pierceLeft <= 0)) {
                iterator.remove();
            }
        }
    }

    void updateHomingShot(Bullet bullet) {
        double traveled = Math.hypot(bullet.x - bullet.launchX, bullet.y - bullet.launchY);
        if (traveled >= HOMING_ARM_DISTANCE) {
            Target target = nearestTargetForHoming(bullet);
            if (target != null) {
                double dx = target.x - bullet.x;
                double dy = targetCenterY(target) - bullet.y;
                double distance = Math.max(0.001, Math.hypot(dx, dy));
                double desiredVx = dx / distance * bullet.speed;
                double desiredVy = dy / distance * bullet.speed;
                bullet.vx += (desiredVx - bullet.vx) * HOMING_STEER;
                bullet.vy += (desiredVy - bullet.vy) * HOMING_STEER;
                double currentSpeed = Math.max(0.001, Math.hypot(bullet.vx, bullet.vy));
                bullet.vx = bullet.vx / currentSpeed * bullet.speed;
                bullet.vy = bullet.vy / currentSpeed * bullet.speed;
            }
        }
        bullet.x += gameplayStep(bullet.vx);
        bullet.y += gameplayStep(bullet.vy);
    }

    Target nearestTargetForHoming(Bullet bullet) {
        Target best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Target target : targets) {
            if (target.dead || target.hp <= 0 || target.x < PLAYER_X - worldAmount(30)) {
                continue;
            }
            double dx = target.x - bullet.x;
            double dy = LANE_Y[target.lane] - bullet.y;
            double distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = target;
            }
        }
        return best;
    }

    void resolveLaserBeam(Bullet bullet) {
        bullet.previousX = PLAYER_X + worldAmount(28);
        bullet.x = maxBulletX(bullet);
        bullet.y = LANE_Y[bullet.lane];
        bullet.previousY = bullet.y;
        while (bullet.pierceLeft > 0) {
            Target next = null;
            double nextX = Double.MAX_VALUE;
            for (Target target : targets) {
                if (target.dead || target.hp <= 0
                        || (target.kind != TargetKind.BOSS && target.lane != bullet.lane)
                        || bulletAlreadyHit(bullet, target) || !bulletHitsTarget(bullet, target)) {
                    continue;
                }
                if (target.x < nextX) {
                    nextX = target.x;
                    next = target;
                }
            }
            if (next == null) {
                return;
            }
            applyBulletHit(bullet, next);
        }
    }

    void applyBubbleContacts(Bullet bullet) {
        if (bullet.remainingDamage <= 0) {
            return;
        }
        Target contact = null;
        double nearestX = Double.MAX_VALUE;
        for (Target target : targets) {
            if (target.dead || target.hp <= 0
                    || (target.kind != TargetKind.BOSS && target.lane != bullet.lane)
                    || !bulletHitsTarget(bullet, target)) {
                continue;
            }
            if (target.x < nearestX) {
                nearestX = target.x;
                contact = target;
            }
        }
        if (contact == null) {
            return;
        }
        double push = bubblePushStep(bullet);
        double laneEdgeX = bubbleTargetRightEdge(contact);
        contact.x = Math.min(laneEdgeX, contact.x + push);
        if (contact.x >= laneEdgeX - 0.001) {
            bullet.x = bubbleHoldX(bullet, contact);
            bullet.previousX = bullet.x;
        }
        contact.previousX = Math.min(contact.previousX, contact.x);
        contact.hitFlash = Math.max(contact.hitFlash, logicTicks(5));
        if (tick % bubbleContactIntervalTicks(bullet) != 0) {
            return;
        }
        int dealt = Math.min(Math.min(contact.hp, bullet.remainingDamage), Math.max(1, bullet.damage / 8));
        contact.hp -= dealt;
        bullet.remainingDamage -= dealt;
        impacts.add(new Impact(contact.x, targetCenterY(contact), dealt, damageTextColorFor(bullet.kind)));
    }

    int bubbleContactIntervalTicks(Bullet bullet) {
        return Math.max(logicTicks(1), logicTicks(6) - Math.max(0, bullet.damage) / 4);
    }

    double bubblePushStep(Bullet bullet) {
        double pressure = 3.2 + Math.min(14.0, Math.max(0, bullet.damage) * 0.16);
        return gameplayStep(pressure);
    }

    double bubbleTargetRightEdge(Target target) {
        return LANE_RIGHT_X - targetHalfWidth(target.kind);
    }

    double bubbleHoldX(Bullet bullet, Target target) {
        return Math.max(PLAYER_X + bullet.radius,
                target.x - targetHalfWidth(target.kind) - bullet.radius);
    }

    void applyBulletHit(Bullet bullet, Target target) {
        int baseDealt = bulletDamageForHit(bullet);
        int dealt = baseDealt;
        if (bullet.kind == BulletKind.DRY_ICE && target.freezeTicks > 0
                && weaponLevel(UpgradeEffect.DRY_ICE_BULLET) >= 2) {
            dealt *= 2;
        }
        int hpBefore = Math.max(0, target.hp);
        target.hp -= dealt;
        target.hitFlash = logicTicks(10);
        rememberBulletHit(bullet, target);
        bullet.hits++;
        bullet.pierceLeft--;
        impacts.add(new Impact(target.x, targetCenterY(target), dealt, damageTextColorFor(bullet)));
        collectOverflowDamage(bullet, target, Math.max(0, dealt - hpBefore));
        if (bullet.kind == BulletKind.BURST) {
            applyBurstSplash(bullet, target, dealt);
        } else if (bullet.kind == BulletKind.DRY_ICE) {
            applyDryIceHit(bullet, target, dealt, baseDealt);
        } else if (bullet.kind == BulletKind.PIERCE) {
            applyLaserHitEffects(target);
        } else if (bullet.kind == BulletKind.HOMING_SHOT) {
            spawnHomingImpactBurst(bullet, target);
        }
    }

    void collectOverflowDamage(Bullet bullet, Target target, int overkillDamage) {
        if (!hasGoldTalent(UpgradeEffect.OVERFLOW_ROUND) || overflowDamageBank >= OVERFLOW_MAX_DAMAGE
                || overkillDamage <= 0 || target.hp > 0 || !isOverflowEligibleBullet(bullet)
                || target.kind == TargetKind.UPGRADE) {
            return;
        }
        int before = overflowDamageBank;
        overflowDamageBank = Math.min(OVERFLOW_MAX_DAMAGE, overflowDamageBank + overkillDamage);
        if (overflowDamageBank > before && overflowDamageBank >= OVERFLOW_MAX_DAMAGE) {
            overflowReadyPulseTicks = OVERFLOW_READY_PULSE_TICKS;
            impacts.add(new Impact(target.x, targetCenterY(target), 0, COLOR_OVERFLOW));
            showMessage("Overflow charged. Hold Shift while completing a word to fire it.",
                    "溢流已充满。按住 Shift 完成单词即可发射。");
        }
    }

    boolean isOverflowEligibleBullet(Bullet bullet) {
        return bullet.kind == BulletKind.BASIC
                || bullet.kind == BulletKind.BURST
                || bullet.kind == BulletKind.DRY_ICE
                || bullet.kind == BulletKind.HOMING_SHOT;
    }

    void applyLaserHitEffects(Target target) {
        if (weaponLevel(UpgradeEffect.FROST_FIELD) < 2 || target.dead || target.hp <= 0) {
            return;
        }
        target.laserHitCount++;
        spawnLaserBurnParticles(target, 4, false);
        if (target.laserHitCount < LASER_BURN_HIT_THRESHOLD) {
            return;
        }
        if (target.laserBurnTicks <= 0) {
            target.laserBurnAgeTicks = 0;
        }
        target.laserBurnTicks = Math.max(target.laserBurnTicks, LASER_BURN_DURATION_TICKS);
        spawnLaserBurnParticles(target, 12, true);
    }

    void updateLaserBurns() {
        for (Target target : targets) {
            if (target.dead || target.hp <= 0 || target.laserBurnTicks <= 0) {
                continue;
            }
            target.laserBurnTicks--;
            target.laserBurnAgeTicks++;
            if (tick % logicTicks(3) == 0) {
                spawnLaserBurnParticles(target, 2 + Math.min(4, target.laserBurnAgeTicks / LASER_BURN_RAMP_TICKS), true);
            }
            if (target.laserBurnAgeTicks % LASER_BURN_DAMAGE_INTERVAL_TICKS == 0) {
                int damage = Math.min(target.hp, laserBurnDamage(target));
                target.hp -= damage;
                target.hitFlash = Math.max(target.hitFlash, logicTicks(5));
                impacts.add(new Impact(target.x, LANE_Y[target.lane], damage, COLOR_LASER_BURN));
                spawnLaserBurnParticles(target, 8, true);
            }
            if (target.laserBurnTicks <= 0) {
                target.laserBurnAgeTicks = 0;
            }
        }
    }

    int laserBurnDamage(Target target) {
        int ramp = Math.min(8, target.laserBurnAgeTicks / LASER_BURN_RAMP_TICKS);
        return Math.max(1, 1 + ramp);
    }

    void updateIcePulses() {
        Iterator<IcePulse> iterator = icePulses.iterator();
        while (iterator.hasNext()) {
            IcePulse pulse = iterator.next();
            pulse.ticks--;
            if (pulse.ticks <= 0) {
                iterator.remove();
            }
        }
    }

    void updateBulletTrailParticles() {
        Iterator<BulletTrailParticle> iterator = bulletTrailParticles.iterator();
        while (iterator.hasNext()) {
            BulletTrailParticle particle = iterator.next();
            particle.step();
            if (particle.life <= 0) {
                iterator.remove();
            }
        }
    }

    void spawnBulletTrailParticles(Bullet bullet) {
        double dx = bullet.x - bullet.previousX;
        double dy = bullet.y - bullet.previousY;
        double distance = Math.max(0.0, Math.hypot(dx, dy));
        int count = Math.max(2, Math.min(8, (int) Math.ceil(distance / 5.0)));
        if (bullet.kind == BulletKind.DRY_ICE) {
            count = Math.max(3, count);
        } else if (bullet.kind == BulletKind.HOMING_SHOT) {
            count = Math.max(4, Math.min(9, count + 2));
        } else if (bullet.kind == BulletKind.OVERFLOW) {
            count = Math.max(8, Math.min(14, count + 5));
        }
        Color color = trailColorFor(bullet);
        for (int i = 0; i < count; i++) {
            double t = (i + random.nextDouble()) / count;
            double x = bullet.previousX + dx * t;
            double baseY = bullet.previousY + dy * t;
            double spread = bullet.kind == BulletKind.CONTINUOUS ? 3.0 : bullet.kind == BulletKind.HOMING_SHOT ? 4.8 : 5.0;
            double y = baseY + random.nextDouble() * spread * 2.0 - spread;
            double driftBack = -0.45 - random.nextDouble() * 0.85;
            double driftY = random.nextDouble() * 0.7 - 0.35;
            double size = trailSizeFor(bullet.kind) * (0.72 + random.nextDouble() * 0.56);
            int life = trailLifeFor(bullet.kind) + random.nextInt(4);
            if (bullet.kind == BulletKind.DRY_ICE) {
                int sides = 5 + random.nextInt(3);
                double rotation = random.nextDouble() * Math.PI * 2.0;
                double spin = (random.nextDouble() - 0.5) * 0.22;
                bulletTrailParticles.add(new BulletTrailParticle(x, y, driftBack, driftY, size, life, color,
                        sides, rotation, spin));
            } else if (bullet.kind == BulletKind.OVERFLOW) {
                int sides = 4 + random.nextInt(4);
                double rotation = random.nextDouble() * Math.PI * 2.0;
                double spin = (random.nextDouble() - 0.5) * 0.58;
                bulletTrailParticles.add(new BulletTrailParticle(x, y, driftBack * 1.25,
                        driftY * 2.2, size, life, random.nextBoolean() ? COLOR_OVERFLOW : color,
                        sides, rotation, spin));
            } else if (bullet.kind == BulletKind.HOMING_SHOT) {
                int sides = 3 + random.nextInt(3);
                double rotation = random.nextDouble() * Math.PI * 2.0;
                double spin = (random.nextDouble() - 0.5) * 0.4;
                bulletTrailParticles.add(new BulletTrailParticle(x, y, driftBack * 0.72, driftY * 1.45,
                        size, life, color, sides, rotation, spin));
            } else {
                bulletTrailParticles.add(new BulletTrailParticle(x, y, driftBack, driftY, size, life, color));
            }
        }
    }

    void spawnLaserBeamParticles(Bullet bullet) {
        double startX = PLAYER_X + worldAmount(28);
        double endX = maxBulletX(bullet);
        double beamLength = Math.max(1.0, endX - startX);
        double y = bullet.y;
        boolean maxPower = isMaxPowerLaser(bullet);
        int count = maxPower ? 58 : 24;
        for (int i = 0; i < count; i++) {
            double t = (i + random.nextDouble()) / count;
            double x = startX + beamLength * t;
            double side = random.nextBoolean() ? -1.0 : 1.0;
            double offsetY = side * (3.0 + random.nextDouble() * (maxPower ? 20.0 : 12.0));
            double vx = maxPower ? -1.2 - random.nextDouble() * 2.2 : -0.75 - random.nextDouble() * 1.35;
            double vy = side * (0.35 + random.nextDouble() * (maxPower ? 2.1 : 1.2));
            double size = maxPower ? 3.0 + random.nextDouble() * 7.6 : 2.2 + random.nextDouble() * 4.8;
            int life = maxPower ? 12 + random.nextInt(13) : 9 + random.nextInt(10);
            int sides = maxPower ? 4 + random.nextInt(4) : 3 + random.nextInt(3);
            double rotation = random.nextDouble() * Math.PI * 2.0;
            double spin = (random.nextDouble() - 0.5) * (maxPower ? 0.56 : 0.34);
            Color color = maxPower && random.nextBoolean() ? COLOR_LASER_HOT : laserParticleColor(bullet);
            bulletTrailParticles.add(new BulletTrailParticle(x, y + offsetY, vx, vy, size, life, color,
                    sides, rotation, spin));
        }
    }

    boolean isMaxPowerLaser(Bullet bullet) {
        return bullet.kind == BulletKind.PIERCE && bullet.laserPowerPercent >= 300;
    }

    Color laserParticleColor(Bullet bullet) {
        return blendColor(laserCoreColor(bullet), COLOR_LASER_HOT, isMaxPowerLaser(bullet) ? 0.42 : 0.18);
    }

    void spawnLaserBurnParticles(Target target, int count, boolean flame) {
        int halfWidth = targetHalfWidth(target.kind);
        int halfHeight = targetHalfHeight(target.kind);
        for (int i = 0; i < count; i++) {
            double x = target.x + random.nextDouble() * halfWidth * 1.5 - halfWidth * 0.75;
            double y = LANE_Y[target.lane] + random.nextDouble() * halfHeight * 1.4 - halfHeight * 0.75;
            double vx = random.nextDouble() * 1.0 - 0.5;
            double vy = flame ? -0.45 - random.nextDouble() * 1.4 : random.nextDouble() * 0.8 - 0.4;
            double size = flame ? 2.8 + random.nextDouble() * 6.6 : 1.8 + random.nextDouble() * 3.2;
            int life = flame ? 11 + random.nextInt(11) : 7 + random.nextInt(7);
            int sides = flame ? 3 + random.nextInt(3) : 0;
            double rotation = random.nextDouble() * Math.PI * 2.0;
            double spin = (random.nextDouble() - 0.5) * (flame ? 0.32 : 0.14);
            Color color = flame && random.nextBoolean() ? COLOR_LASER_EMBER : COLOR_LASER_BURN;
            bulletTrailParticles.add(new BulletTrailParticle(x, y, vx, vy, size, life, color,
                    sides, rotation, spin));
        }
    }

    void spawnHomingImpactBurst(Bullet bullet, Target target) {
        Color color = bullet.accentColor == null ? COLOR_HOMING_HEAD : bullet.accentColor;
        double x = target.x;
        double y = LANE_Y[target.lane];
        int count = 18;
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double speed = 1.0 + random.nextDouble() * 3.2;
            double vx = Math.cos(angle) * speed - 0.45;
            double vy = Math.sin(angle) * speed * 0.72;
            double size = 3.0 + random.nextDouble() * 7.4;
            int life = 15 + random.nextInt(13);
            int sides = 3 + random.nextInt(4);
            double rotation = random.nextDouble() * Math.PI * 2.0;
            double spin = (random.nextDouble() - 0.5) * 0.58;
            bulletTrailParticles.add(new BulletTrailParticle(x, y, vx, vy, size, life, color,
                    sides, rotation, spin));
        }
        impacts.add(new Impact(x, y, 0, color));
    }

    Color trailColorFor(Bullet bullet) {
        if (bullet.accentColor != null) {
            return bullet.accentColor;
        }
        return trailColorFor(bullet.kind);
    }

    Color trailColorFor(BulletKind kind) {
        if (kind == BulletKind.PIERCE) {
            return COLOR_LASER_CORE;
        }
        if (kind == BulletKind.OVERFLOW) {
            return COLOR_OVERFLOW;
        }
        if (kind == BulletKind.HOMING_SHOT) {
            return COLOR_HOMING_TRAIL;
        }
        if (kind == BulletKind.CONTINUOUS_SURGE) {
            return COLOR_SURGE_HEAD;
        }
        if (kind == BulletKind.BURST) {
            return COLOR_BURST_HEAD;
        }
        if (kind == BulletKind.DRY_ICE) {
            return COLOR_DRY_ICE_TRAIL;
        }
        if (kind == BulletKind.CONTINUOUS) {
            return COLOR_CONTINUOUS_HEAD;
        }
        return COLOR_BASIC_HEAD;
    }

    Color damageTextColorFor(Bullet bullet) {
        if (bullet.accentColor != null) {
            return bullet.accentColor;
        }
        return damageTextColorFor(bullet.kind);
    }

    Color damageTextColorFor(BulletKind kind) {
        if (kind == BulletKind.PIERCE) {
            return COLOR_LASER_CORE;
        }
        if (kind == BulletKind.OVERFLOW) {
            return COLOR_OVERFLOW;
        }
        if (kind == BulletKind.HOMING_SHOT) {
            return COLOR_HOMING_HEAD;
        }
        if (kind == BulletKind.BUBBLE) {
            return COLOR_BUBBLE_CORE;
        }
        if (kind == BulletKind.CONTINUOUS_SURGE) {
            return COLOR_SURGE_HEAD;
        }
        if (kind == BulletKind.BURST) {
            return COLOR_BURST_HEAD;
        }
        if (kind == BulletKind.DRY_ICE) {
            return COLOR_DRY_ICE_HEAD;
        }
        if (kind == BulletKind.CONTINUOUS) {
            return COLOR_CONTINUOUS_HEAD;
        }
        return COLOR_BASIC_HEAD;
    }

    Color impactColorFor(Impact impact, int alpha) {
        return withAlpha(impact.damageColor, alpha);
    }

    double trailSizeFor(BulletKind kind) {
        if (kind == BulletKind.PIERCE || kind == BulletKind.BURST) {
            return 5.6;
        }
        if (kind == BulletKind.OVERFLOW) {
            return 7.2;
        }
        if (kind == BulletKind.HOMING_SHOT) {
            return 4.6;
        }
        if (kind == BulletKind.DRY_ICE) {
            return 4.2;
        }
        if (kind == BulletKind.CONTINUOUS_SURGE) {
            return 4.8;
        }
        if (kind == BulletKind.CONTINUOUS) {
            return 2.8;
        }
        return 4.2;
    }

    int trailLifeFor(BulletKind kind) {
        if (kind == BulletKind.PIERCE) {
            return 15;
        }
        if (kind == BulletKind.OVERFLOW) {
            return 19;
        }
        if (kind == BulletKind.HOMING_SHOT) {
            return 19;
        }
        if (kind == BulletKind.BURST || kind == BulletKind.CONTINUOUS_SURGE || kind == BulletKind.DRY_ICE) {
            return 13;
        }
        if (kind == BulletKind.CONTINUOUS) {
            return 8;
        }
        return 11;
    }

    void applyBurstSplash(Bullet bullet, Target center, int dealt) {
        int splashDamage = Math.max(1, dealt / 2);
        for (Target target : targets) {
            if (target == center || target.dead || target.hp <= 0 || target.lane != center.lane) {
                continue;
            }
            if (Math.abs(target.x - center.x) > worldAmount(92)) {
                continue;
            }
            target.hp -= splashDamage;
            target.hitFlash = logicTicks(10);
            impacts.add(new Impact(target.x, LANE_Y[target.lane], splashDamage, damageTextColorFor(bullet.kind)));
        }
    }

    int bulletDamageForHit(Bullet bullet) {
        if (bullet.kind != BulletKind.PIERCE) {
            return Math.max(1, bullet.damage - bullet.hits);
        }
        return Math.max(1, bullet.damage);
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
        if (bullet.kind == BulletKind.HOMING_SHOT) {
            int halfHeight = targetHalfHeight(target.kind);
            double targetY = targetCenterY(target);
            double top = targetY - halfHeight;
            double bottom = targetY + halfHeight;
            double bulletLeft = Math.min(bullet.previousX, bullet.x) - bullet.radius;
            double bulletRight = Math.max(bullet.previousX, bullet.x) + bullet.radius;
            double bulletTop = Math.min(bullet.previousY, bullet.y) - bullet.radius;
            double bulletBottom = Math.max(bullet.previousY, bullet.y) + bullet.radius;
            return bulletRight >= left && bulletLeft <= right && bulletBottom >= top && bulletTop <= bottom;
        }
        return bullet.x + bullet.radius >= left && bullet.previousX - bullet.radius <= right;
    }

    static int targetHalfWidth(TargetKind kind) {
        return kind == TargetKind.BOSS ? sx(82) : sx(46);
    }

    static int targetHalfHeight(TargetKind kind) {
        return kind == TargetKind.BOSS ? sy(118) : sy(25);
    }

    static double targetCenterY(Target target) {
        return target.kind == TargetKind.BOSS ? (LANE_Y[0] + LANE_Y[1]) / 2.0 : LANE_Y[target.lane];
    }

    static double targetSpawnX(TargetKind kind) {
        return LANE_RIGHT_X - targetHalfWidth(kind);
    }

    static int sx(int value) {
        return (int) Math.round(value * COORD_SCALE_X);
    }

    static int sy(int value) {
        return (int) Math.round(value * COORD_SCALE_Y);
    }

    static double worldAmount(double legacyAmount) {
        return legacyAmount * COORD_SCALE;
    }

    static Font font(String name, int style, int size) {
        return new Font(name, style, sx(size));
    }

    static BasicStroke stroke(float width) {
        return new BasicStroke((float) worldAmount(width));
    }

    static BasicStroke stroke(float width, int cap, int join) {
        return new BasicStroke((float) worldAmount(width), cap, join);
    }

    static int logicTicks(int legacyTicks) {
        return Math.max(1, (int) Math.round(legacyTicks / GAMEPLAY_STEP_SCALE));
    }

    static double gameplayStep(double legacyAmount) {
        return worldAmount(legacyAmount) * GAMEPLAY_STEP_SCALE;
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
            double speedMultiplier = progressSpeedMultiplier();
            orb.x -= gameplayStep(orb.speed * speedMultiplier);
            orb.pulse++;
            boolean fullScreenMagnet = hasGoldTalent(UpgradeEffect.MAGNETIC_FIELD);
            double playerY = LANE_Y[lane];
            double dx = PLAYER_X - orb.x;
            double dy = playerY - orb.y;
            double distance = Math.hypot(dx, dy);
            if (fullScreenMagnet || (orb.lane == lane && distance <= XP_ATTRACT_RADIUS)) {
                orb.attracted = true;
            }
            if (orb.attracted && distance > XP_COLLECT_RADIUS) {
                double pull = fullScreenMagnet ? 8.0 + Math.max(0.0, distance) / 120.0
                        : 2.0 + Math.max(0.0, XP_ATTRACT_RADIUS - distance) / 18.0;
                double step = Math.min(distance - XP_COLLECT_RADIUS, gameplayStep(pull * speedMultiplier));
                orb.x += dx / distance * step;
                orb.y += dy / distance * step;
                distance = Math.hypot(PLAYER_X - orb.x, playerY - orb.y);
            }
            if ((orb.attracted || orb.lane == lane) && distance <= XP_COLLECT_RADIUS) {
                addExperience(orb.value);
                score += orb.value * 12;
                impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0));
                iterator.remove();
            } else if (orb.x < -worldAmount(45)) {
                iterator.remove();
            }
        }
    }

    void handleCollisions() {
        for (Target target : targets) {
            if (target.x <= PLAYER_X + worldAmount(32) && (target.lane == lane || target.kind == TargetKind.BOSS)
                    && !target.dead && target.hp > 0) {
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
                applyMeleeCollisionGrowth(damage);
                if (damage > 0) {
                    target.hp = 0;
                }
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
        int cost = Math.max(1, Math.max(0, target.hp));
        if (hasGoldTalent(UpgradeEffect.MELEE)) {
            cost = Math.max(1, (cost + 1) / 2);
        }
        return cost;
    }

    void applyMeleeCollisionGrowth(int damage) {
        if (!hasGoldTalent(UpgradeEffect.MELEE) || damage <= 0) {
            return;
        }
        int gain = Math.max(1, (int) Math.ceil(damage * 0.30));
        maxHp += gain;
        impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0, COLOR_MELEE));
    }

    void removeDeadAndEscaped() {
        Iterator<Target> iterator = targets.iterator();
        while (iterator.hasNext()) {
            Target target = iterator.next();
            target.hitFlash = Math.max(0, target.hitFlash - 1);
            if (target.kind == TargetKind.BOSS && target.bossDeathAnimating) {
                if (target.bossDeathTicks <= 0) {
                    finishBossDeathAnimation(target);
                    iterator.remove();
                }
                continue;
            }
            if (target.dead || target.hp <= 0) {
                if (target.kind == TargetKind.BOSS && target.hp <= 0 && !target.counted) {
                    startBossDeathAnimation(target);
                    continue;
                }
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

    void startBossDeathAnimation(Target boss) {
        boss.hp = 0;
        boss.dead = false;
        boss.counted = true;
        boss.bossDeathAnimating = true;
        boss.bossDeathTicks = BOSS_DEATH_ANIMATION_TICKS;
        boss.bossLaserWarningTicks = 0;
        boss.bossLaserLane = -1;
        boss.bossAttackCooldownTicks = 0;
        bossProjectiles.clear();
        showMessage("Boss core collapsing. Brace for the flash.",
                "Boss 核心崩解中，准备迎接白闪。");
    }

    void updateBossDeathAnimation(Target boss) {
        boss.bossDeathTicks = Math.max(0, boss.bossDeathTicks - 1);
        if (boss.bossDeathTicks % logicTicks(4) == 0) {
            spawnBossDeathSparks(boss, 5);
        }
    }

    void finishBossDeathAnimation(Target boss) {
        spawnBossDeathSparks(boss, 72);
        spawnBreakParticles(boss);
        screenFlashTicks = BOSS_SCREEN_FLASH_TICKS;
        rewardFor(boss);
    }

    void spawnBossDeathSparks(Target boss, int count) {
        double centerX = boss.x;
        double centerY = targetCenterY(boss);
        double progress = bossDeathProgress(boss);
        Color color = blendColor(COLOR_TARGET_BOSS, Color.WHITE, 0.35 + progress * 0.55);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double speed = 1.1 + random.nextDouble() * (3.6 + progress * 3.8);
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed * 0.72;
            double size = 3.5 + random.nextDouble() * (7.0 + progress * 7.0);
            int life = 12 + random.nextInt(18);
            int sides = 3 + random.nextInt(4);
            bulletTrailParticles.add(new BulletTrailParticle(centerX + random.nextDouble() * sx(120) - sx(60),
                    centerY + random.nextDouble() * sy(145) - sy(72), vx, vy, size, life, color,
                    sides, random.nextDouble() * Math.PI * 2.0, (random.nextDouble() - 0.5) * 0.65));
        }
    }

    double bossDeathProgress(Target boss) {
        if (!boss.bossDeathAnimating) {
            return 0.0;
        }
        return 1.0 - boss.bossDeathTicks / (double) Math.max(1, BOSS_DEATH_ANIMATION_TICKS);
    }

    void spawnBreakParticles(Target target) {
        int count = breakParticleCount(target);
        double centerX = target.x;
        double centerY = targetCenterY(target);
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
        boolean basicWeaponStacked = false;
        if (target.kind != TargetKind.UPGRADE && hasHighTalent(UpgradeEffect.BASIC_WEAPON)
                && weaponLevel(UpgradeEffect.BASIC_WEAPON) >= 2) {
            basicWeaponKillDamageBonusPercent++;
            basicWeaponStacked = true;
        }
        if (target.kind == TargetKind.BOSS) {
            score += 250 + bossLevel * 60;
            kills += 2;
            openBossRewardMenu();
            if (basicWeaponStacked) {
                showBasicWeaponMultiplierMessage();
            }
            return;
        }

        kills++;
        score += target.kind == TargetKind.UPGRADE ? 90 : 35 + target.maxHp * 5;
        spawnGoldDrone(target);
        if (target.kind != TargetKind.BOSS) {
            dropXpOrb(target);
        }
        if (basicWeaponStacked) {
            showBasicWeaponMultiplierMessage();
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
                && crossfeedBonusTicks > 0 && crossfeedCooldownTicks <= 0) {
            gained += Math.max(1, amount * 2 / 5);
            crossfeedCooldownTicks = logicTicks(150);
            crossfeedBonusTicks = 0;
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

    void openBossRewardMenu() {
        clearSellConfirmation();
        typingLane = -1;
        typed = "";
        buildBossRewardChoices();
        selectedUpgradeIndex = 0;
        selectedOverviewCardIndex = 0;
        bossRewardChoice = true;
        overviewSelectionActive = false;
        choiceMode = ChoiceMode.UPGRADE;
        showMessage("Boss cleared: choose a weapon or gold reward.",
                "Boss 击破：选择武器或金色奖励。");
    }

    int bossXpReward() {
        return BOSS_XP_BASE + bossLevel * BOSS_XP_PER_LEVEL;
    }

    void openUpgradeMenu() {
        if (pendingUpgradeChoices <= 0 || choiceMode != ChoiceMode.NONE || gameOver) {
            return;
        }
        clearSellConfirmation();
        buildUpgradeChoices();
        selectedUpgradeIndex = 0;
        selectedOverviewCardIndex = 0;
        bossRewardChoice = false;
        overviewSelectionActive = false;
        choiceMode = ChoiceMode.UPGRADE;
        typingLane = -1;
        typed = "";
        showMessage("Choose an upgrade.", "选择升级。");
    }

    void openUpgradeOverview() {
        if (choiceMode != ChoiceMode.NONE || gameOver) {
            return;
        }
        clearSellConfirmation();
        clearUpgradeChoices();
        selectedUpgradeIndex = 0;
        selectedOverviewCardIndex = 0;
        bossRewardChoice = false;
        List<UpgradeInventoryCard> cards = currentUpgradeCards();
        clampOverviewSelection(cards);
        overviewSelectionActive = cards.size() > 0;
        choiceMode = ChoiceMode.OVERVIEW;
        typingLane = -1;
        typed = "";
        showMessage("Upgrade overview opened.", "升级总览已打开。");
    }

    void buildUpgradeChoices() {
        UpgradeRarity[] slots = rollUpgradeSlots();
        for (int i = 0; i < upgradeChoices.length; i++) {
            upgradeChoices[i] = randomCardFor(slots[i], i);
        }
    }

    UpgradeRarity[] rollUpgradeSlots() {
        boolean canRollGold = goldTalentCount() < MAX_GOLD_TALENTS;
        int roll = random.nextInt(100);
        if (blueUpgradeCount >= MAX_BLUE_UPGRADES) {
            if (canRollGold && roll < 18) {
                return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.COMMON, UpgradeRarity.HIGH};
            }
            return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.COMMON, UpgradeRarity.COMMON};
        }
        if (canRollGold && roll < 28) {
            return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.UNCOMMON, UpgradeRarity.HIGH};
        }
        if (roll < 35) {
            return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.UNCOMMON, UpgradeRarity.UNCOMMON};
        }
        return new UpgradeRarity[] {UpgradeRarity.COMMON, UpgradeRarity.COMMON, UpgradeRarity.UNCOMMON};
    }

    void buildBossRewardChoices() {
        UpgradeEffect currentWeapon = currentWeaponEffect();
        if (currentWeapon == null) {
            upgradeChoices[0] = randomCardFor(UpgradeRarity.RED, 0);
        } else if (weaponLevel(currentWeapon) >= MAX_WEAPON_LEVEL) {
            upgradeChoices[0] = bossGoldRewardCard(0);
        } else {
            upgradeChoices[0] = weaponLevelCard(currentWeapon);
        }
        upgradeChoices[1] = randomCardFor(UpgradeRarity.RED, 1);
        upgradeChoices[2] = bossGoldRewardCard(2);
    }

    UpgradeCard bossGoldRewardCard(int slotIndex) {
        UpgradeCard card = randomCardFor(UpgradeRarity.HIGH, slotIndex);
        if (card != null && card.effect != UpgradeEffect.GOLD_TALENT_PLACEHOLDER) {
            return card;
        }
        if (!choiceAlreadyHas(UpgradeEffect.WEAPON_XP_CACHE)) {
            return weaponXpCacheCard();
        }
        return randomCardFor(UpgradeRarity.RED, slotIndex);
    }

    UpgradeCard weaponXpCacheCard() {
        int xpReward = bossXpReward();
        return new UpgradeCard("Weapon Mastery Cache", "Current weapon is maxed: gain +" + xpReward + " XP now",
                "武器满级补给", "当前武器已满级：立即获得 +" + xpReward + " XP",
                UpgradeRarity.RED, UpgradeEffect.WEAPON_XP_CACHE);
    }

    UpgradeCard weaponLevelCard(UpgradeEffect effect) {
        UpgradeCard base = createCard(effect, UpgradeRarity.RED);
        int nextLevel = Math.min(MAX_WEAPON_LEVEL, Math.max(1, weaponLevel(effect)) + 1);
        return new UpgradeCard(base.title + " Lv " + nextLevel, weaponLevelDescription(effect, nextLevel),
                base.titleZh + " Lv " + nextLevel, weaponLevelDescriptionZh(effect, nextLevel),
                UpgradeRarity.RED, effect);
    }

    String weaponLevelDescription(UpgradeEffect effect, int level) {
        if (effect == UpgradeEffect.BASIC_WEAPON) {
            return level == 2 ? "Upgrade: every enemy kill adds +1% basic-weapon damage with no cap"
                    : "Upgrade: typed words launch a huge slow bubble that spends remaining damage while pushing enemies";
        }
        if (effect == UpgradeEffect.FROST_FIELD) {
            return level == 2 ? "Upgrade: after 5 hits on one target, it burns for ramping damage"
                    : "Upgrade: correct words refresh a 2s sustained laser that reaches 300% after 8s";
        }
        if (effect == UpgradeEffect.DRY_ICE_BULLET) {
            return level == 2 ? "Upgrade: frozen targets take double damage, then thaw into a damaging slow pulse"
                    : "Upgrade: thaw pulses also add frost progress to nearby enemies";
        }
        if (effect == UpgradeEffect.HOMING_SHOTGUN) {
            return level == 2 ? "Upgrade: combo adds pellets to the fan, up to 10 shots"
                    : "Upgrade: more enemies on field increase shotgun damage";
        }
        return level == 2 ? "Upgrade: correct words push the autocannon straight to peak rate, then decay"
                : "Upgrade: correct words stack firing speed; stacks decay over time and survive lane swaps";
    }

    String weaponLevelDescriptionZh(UpgradeEffect effect, int level) {
        if (effect == UpgradeEffect.BASIC_WEAPON) {
            return level == 2 ? "升级：每击败一个敌方单位，基础武器伤害无上限 +1%"
                    : "升级：完成词发射巨大慢速泡泡，接触敌人时消耗剩余伤害并推动敌人";
        }
        if (effect == UpgradeEffect.FROST_FIELD) {
            return level == 2 ? "升级：同一单位被命中 5 次后开始燃烧，燃烧越久伤害越高"
                    : "升级：每次正确输入刷新 2 秒持续激光，持续 8 秒达到 300% 伤害";
        }
        if (effect == UpgradeEffect.DRY_ICE_BULLET) {
            return level == 2 ? "升级：冰冻目标受到双倍伤害，解冻时释放伤害减速冲击波"
                    : "升级：解冻冲击波也会给附近敌人叠加冷冻进度";
        }
        if (effect == UpgradeEffect.HOMING_SHOTGUN) {
            return level == 2 ? "升级：combo 会增加散弹数量，最高 10 发"
                    : "升级：场上敌人越多，散弹总伤害越高";
        }
        return level == 2 ? "升级：正确输入后自动炮立即到达峰值射速，并逐渐回落"
                : "升级：正确输入会累计射速，随时间衰减，切换 lane 不重置";
    }

    UpgradeCard randomCardFor(UpgradeRarity rarity, int slotIndex) {
        UpgradeEffect[] pool = poolFor(rarity);
        if (pool.length == 0) {
            if (rarity == UpgradeRarity.HIGH) {
                return reservedGoldTalentCard(slotIndex);
            }
            return null;
        }
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
            return reservedGoldTalentCard(slotIndex);
        }
        if (rarity == UpgradeRarity.RED) {
            return createCard(pool[slotIndex % pool.length], rarity);
        }
        return createCard(pool[slotIndex % pool.length], rarity);
    }

    boolean canOfferEffect(UpgradeEffect effect, UpgradeRarity rarity) {
        if (effect == UpgradeEffect.GOLD_TALENT_PLACEHOLDER) {
            return false;
        }
        if (choiceAlreadyHas(effect)) {
            return false;
        }
        if (isEffectAtCap(effect)) {
            return false;
        }
        if (rarity == UpgradeRarity.UNCOMMON && blueUpgradeCount >= MAX_BLUE_UPGRADES) {
            return false;
        }
        if (rarity == UpgradeRarity.HIGH && goldTalentCount() >= MAX_GOLD_TALENTS) {
            return false;
        }
        if (rarity == UpgradeRarity.HIGH && hasGoldTalent(effect)) {
            return false;
        }
        return rarity != UpgradeRarity.RED || !hasHighTalent(effect);
    }

    boolean isEffectAtCap(UpgradeEffect effect) {
        if (effect == UpgradeEffect.REINFORCED_CORE) {
            return maxHpUpgradeBonus >= MAX_HP_UPGRADE_BONUS;
        }
        if (effect == UpgradeEffect.FIELD_PATCH) {
            return hp >= maxHp && maxHpUpgradeBonus >= MAX_HP_UPGRADE_BONUS;
        }
        if (effect == UpgradeEffect.CALIBRATED_DAMAGE) {
            return damageBonusPercent >= MAX_DAMAGE_BONUS_PERCENT;
        }
        if (effect == UpgradeEffect.TRIGGER_TUNING) {
            return fireRateBonusPercent >= MAX_TRIGGER_TUNING_BONUS_PERCENT;
        }
        return false;
    }

    UpgradeEffect[] poolFor(UpgradeRarity rarity) {
        return gameplayPoolFor(rarity);
    }

    UpgradeEffect[] gameplayPoolFor(UpgradeRarity rarity) {
        if (rarity == UpgradeRarity.RED) {
            return new UpgradeEffect[] {
                    UpgradeEffect.BASIC_WEAPON,
                    UpgradeEffect.RHYTHM_CANNON,
                    UpgradeEffect.FROST_FIELD,
                    UpgradeEffect.DRY_ICE_BULLET,
                    UpgradeEffect.HOMING_SHOTGUN
            };
        }
        if (rarity == UpgradeRarity.HIGH) {
            return new UpgradeEffect[] {
                    UpgradeEffect.DRONE_SWARM,
                    UpgradeEffect.OVERFLOW_ROUND,
                    UpgradeEffect.MAGNETIC_FIELD,
                    UpgradeEffect.UNDYING_TOTEM,
                    UpgradeEffect.ADRENALINE,
                    UpgradeEffect.MELEE,
                    UpgradeEffect.RED_EYE
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

    UpgradeCard reservedGoldTalentCard(int slotIndex) {
        return createCard(UpgradeEffect.GOLD_TALENT_PLACEHOLDER, UpgradeRarity.HIGH);
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
        return isWeaponEffect(effect);
    }

    boolean isWeaponEffect(UpgradeEffect effect) {
        return effect == UpgradeEffect.BASIC_WEAPON
                || effect == UpgradeEffect.RHYTHM_CANNON
                || effect == UpgradeEffect.FROST_FIELD
                || effect == UpgradeEffect.DRY_ICE_BULLET
                || effect == UpgradeEffect.HOMING_SHOTGUN;
    }

    UpgradeEffect currentWeaponEffect() {
        return highTalents.length == 0 ? null : highTalents[0];
    }

    int weaponLevel(UpgradeEffect effect) {
        if (!isWeaponEffect(effect)) {
            return 0;
        }
        return weaponLevels[effect.ordinal()];
    }

    void setWeaponLevel(UpgradeEffect effect, int level) {
        if (isWeaponEffect(effect)) {
            weaponLevels[effect.ordinal()] = Math.max(0, Math.min(MAX_WEAPON_LEVEL, level));
        }
    }

    int raiseWeaponLevel(UpgradeEffect effect) {
        int currentLevel = hasHighTalent(effect) ? Math.max(1, weaponLevel(effect)) : weaponLevel(effect);
        int nextLevel = Math.min(MAX_WEAPON_LEVEL, Math.max(1, currentLevel + 1));
        setWeaponLevel(effect, nextLevel);
        return nextLevel;
    }

    boolean isGoldTalentEffect(UpgradeEffect effect) {
        return rarityForEffect(effect) == UpgradeRarity.HIGH
                && effect != UpgradeEffect.GOLD_TALENT_PLACEHOLDER
                && effect != UpgradeEffect.WEAPON_XP_CACHE
                && effect != UpgradeEffect.TEST_INVINCIBLE
                && effect != UpgradeEffect.TEST_BIG_XP;
    }

    int goldTalentCount() {
        int count = 0;
        for (UpgradeEffect talent : goldTalents) {
            if (talent != null) {
                count++;
            }
        }
        return count;
    }

    int firstEmptyGoldTalentSlot() {
        for (int i = 0; i < goldTalents.length; i++) {
            if (goldTalents[i] == null) {
                return i;
            }
        }
        return -1;
    }

    boolean hasGoldTalent(UpgradeEffect effect) {
        for (UpgradeEffect talent : goldTalents) {
            if (talent == effect) {
                return true;
            }
        }
        return false;
    }

    boolean isRealGoldTalent(UpgradeEffect effect) {
        return effect == UpgradeEffect.DRONE_SWARM
                || effect == UpgradeEffect.OVERFLOW_ROUND
                || effect == UpgradeEffect.MAGNETIC_FIELD
                || effect == UpgradeEffect.UNDYING_TOTEM
                || effect == UpgradeEffect.ADRENALINE
                || effect == UpgradeEffect.MELEE
                || effect == UpgradeEffect.RED_EYE;
    }

    boolean isGroupTwoToFiveEffect(UpgradeEffect effect) {
        return effect == UpgradeEffect.SAME_LANE_SUPPRESSION
                || effect == UpgradeEffect.LANE_SWAP_BEAT
                || effect == UpgradeEffect.SINGLE_LANE_BASTION
                || effect == UpgradeEffect.ALTERNATING_GUARD
                || effect == UpgradeEffect.COMBO_CALIBRATOR
                || effect == UpgradeEffect.FIRST_LETTER_LOCK
                || effect == UpgradeEffect.PREFIX_ILLUMINATION
                || effect == UpgradeEffect.FIRST_LETTER_TICKET
                || effect == UpgradeEffect.DUAL_PREFIX_SCAN
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
                    "+1 shot/beam per level; +20% total damage split across the volley.",
                    "扳机调校",
                    "每级 +1 发/光束；总伤害 +20% 后平分到齐射。",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.COMBO_TUNING) {
            return new UpgradeCard("Combo Tuning", "Combo damage improves",
                    "连击调律", "连击伤害提升", rarity, effect);
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
            return new UpgradeCard("Vowel Convergence", "Vowel-ending words pull same-lane XP",
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
            return new UpgradeCard("Single-Lane Bastion", "Four same-lane words build one non-Boss lane guard",
                    "单路堡垒", "同路连续 4 词生成该路非 Boss 护栏", rarity, effect);
        }
        if (effect == UpgradeEffect.ALTERNATING_GUARD) {
            return new UpgradeCard("Alternating Guard", "Four alternating words guard both lanes from non-Boss collisions",
                    "交替护栏", "上下交替 4 词后双路生成非 Boss 护栏", rarity, effect);
        }
        if (effect == UpgradeEffect.COMBO_CALIBRATOR) {
            return new UpgradeCard("Combo Calibrator", "Combo 3/5/10 adds +1/+2/+4 damage per level",
                    "连击校准器", "连击 3/5/10 时每级追加 +1/+2/+4 伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.FIRST_LETTER_LOCK) {
            return new UpgradeCard("First-Letter Lock", "Unique first letters add focus damage",
                    "首字母锁定", "首字母锁定目标时追加伤害", rarity, effect);
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
        if (effect == UpgradeEffect.BACKSPACE_FIX) {
            return new UpgradeCard("Backspace Fix", "Words completed after Backspace add damage",
                    "回删修正", "使用回删后完成词追加伤害", rarity, effect);
        }
        if (effect == UpgradeEffect.CALM_AFTER_ERROR) {
            return new UpgradeCard("Calm After Error", "Two clean recovery words prepare a non-Boss guard",
                    "错后冷静", "失配后连续干净完成 2 词获得非 Boss 守护", rarity, effect);
        }
        if (effect == UpgradeEffect.BACKSPACE_COUNTER) {
            return new UpgradeCard("Backspace Counter", "Words completed after Backspace fire a counter shot",
                    "回删反击", "使用回删后完成词追加反击弹", rarity, effect);
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
            return new UpgradeCard("Phase Switch", "Lane change blocks 1 non-Boss collision, cooldown",
                    "相位换路", "换 lane 后抵挡 1 次非 Boss 碰撞，有冷却", rarity, effect);
        }
        if (effect == UpgradeEffect.BOSS_BREAKER) {
            return new UpgradeCard("Boss Breaker", "+Boss damage, smaller elite bonus",
                    "Boss 破甲", "提升 Boss 伤害，并小幅克制精英", rarity, effect);
        }
        if (effect == UpgradeEffect.CROSSFEED) {
            return new UpgradeCard("Crossfeed Letters", "f/t/k words add bonus XP on pickup",
                    "交叉字母", "以 f/t/k 开头的单词会提高 XP 收集", rarity, effect);
        }
        if (effect == UpgradeEffect.BASIC_WEAPON) {
            return new UpgradeCard("Basic Gun",
                    "Weapon: keeps the familiar shot, then upgrades into stacking damage and a huge pressure bubble",
                    "基础枪",
                    "红色武器：保留熟悉的基础射击，升级后获得击杀叠伤和巨大压制泡泡",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.FROST_FIELD) {
            return new UpgradeCard("Laser Gun",
                    "Weapon: typed words fire an instant red beam; piercing damage does not decay",
                    "激光枪",
                    "红色武器：完成词瞬间发射红色光束；穿透敌人时伤害不衰减",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.DRY_ICE_BULLET) {
            return new UpgradeCard("Dry-Ice Bullet",
                    "Weapon: typed words fire an icy pentagon round; splash applies 50% slow and every third hit freezes the same target",
                    "干冰子弹",
                    "红色武器：完成词发射冰质五边形弹；范围 50% 减速，连续三次命中同一目标会冻结",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.HOMING_SHOTGUN) {
            return new UpgradeCard("Homing Shotgun",
                    "Weapon: typed words fire a wide fan of pellets that arm, then chase the nearest enemy",
                    "追踪散弹",
                    "红色武器：完成词射出大量扇形弹丸，飞出一段距离后追踪最近敌人",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.DRONE_SWARM) {
            return new UpgradeCard("Drone", "Kills create long-lived color-matched drones that drift near enemies and fire low-damage short lasers",
                    "无人机", "击败敌人后生成同色小无人机，长时间跟随附近敌人并以低伤害短激光攻击",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.OVERFLOW_ROUND) {
            return new UpgradeCard("Overflow", "Single-shot overkill fills a 40-damage bar; when full, hold Shift while typing to fire a very fast 20-damage round",
                    "溢流", "单发武器击杀溢出伤害填充 40 点进度；满后按住 Shift 完成词发射高速 20 伤害子弹",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.MAGNETIC_FIELD) {
            return new UpgradeCard("Magnet", "All XP on screen is pulled toward you at once",
                    "磁力", "同时吸取全屏范围内所有经验",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.UNDYING_TOTEM) {
            return new UpgradeCard("Undying Totem", "Permanently occupies one gold slot, cannot be sold, and revives you once immediately after death",
                    "不死图腾", "永久占用一个金色槽位，不可出售；死亡后立即复活一次",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.ADRENALINE) {
            return new UpgradeCard("Adrenaline", "+50% damage; max/current HP are halved; enemies move 20% faster",
                    "肾上腺素", "伤害 +50%；当前/最大生命减半；敌方速度 +20%",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.MELEE) {
            return new UpgradeCard("Melee", "Weapon damage halves, HP regenerates slowly, collision HP loss halves, and collisions add 30% of that loss to max HP; boss words ram for 20% max HP",
                    "肉搏", "武器伤害减半，缓慢回血；撞击消耗减半并把 30% 撞击伤害转为生命上限；Boss 战正确输入改为冲撞",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.RED_EYE) {
            return new UpgradeCard("Red Eye", "Lower current HP grants higher damage, up to +100%",
                    "红眼", "当前生命比例越低伤害越高，最多 +100%",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.GOLD_TALENT_PLACEHOLDER) {
            return new UpgradeCard("Gold Talent Slot",
                    "Reserved gold-talent interface; concrete talents will be added later",
                    "金色天赋接口",
                    "金色天赋接口已预留；稍后会加入具体天赋",
                    rarity, effect);
        }
        if (effect == UpgradeEffect.WEAPON_XP_CACHE) {
            return weaponXpCacheCard();
        }
        if (effect == UpgradeEffect.TEST_INVINCIBLE) {
            return new UpgradeCard("Test Invincible", "sudo only: HP loss becomes 0",
                    "测试无敌", "sudo 专用：HP 消耗变为 0", rarity, effect);
        }
        if (effect == UpgradeEffect.TEST_BIG_XP) {
            return new UpgradeCard("Test XP Cache", "sudo only: +" + TEST_BIG_XP_AMOUNT + " XP",
                    "测试经验包", "sudo 专用：+" + TEST_BIG_XP_AMOUNT + " XP", rarity, effect);
        }
        return new UpgradeCard("Autocannon",
                "Weapon: fires by itself; below 3x rate it shoots blue rounds, at 3x+ it shoots golden trail rounds",
                "自动炮",
                "红色武器：自动开火；三倍射速以下发射蓝色弹，三倍及以上发射带拖尾金色弹",
                rarity, effect);
    }

    void applyUpgradeCard(UpgradeCard card) {
        applyUpgradeCard(card, true, true);
    }

    void applyUpgradeCard(UpgradeCard card, boolean spendPendingChoice, boolean closeMenu) {
        if (card == null) {
            return;
        }
        if (card.effect == UpgradeEffect.GOLD_TALENT_PLACEHOLDER) {
            showMessage("Gold talents are reserved for the next design pass.",
                    "金色天赋接口已预留，稍后加入具体天赋。");
            return;
        }
        if (isHighTalentEffect(card.effect)) {
            applyHighTalentCard(card, spendPendingChoice, closeMenu);
            return;
        }
        if (isGoldTalentEffect(card.effect)) {
            applyGoldTalentCard(card, spendPendingChoice, closeMenu);
            return;
        }
        if (card.effect == UpgradeEffect.WEAPON_XP_CACHE) {
            addExperience(bossXpReward(), false);
            finishUpgradeCard(card, spendPendingChoice, closeMenu);
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

    void applyGoldTalentCard(UpgradeCard card, boolean spendPendingChoice, boolean closeMenu) {
        if (hasGoldTalent(card.effect)) {
            showMessage("Gold talent already installed: " + card.title + ".",
                    "金色天赋已拥有：" + cardTitle(card) + "。");
            return;
        }
        int emptySlot = firstEmptyGoldTalentSlot();
        if (emptySlot < 0) {
            showMessage("Gold talent slots are full.",
                    "金色天赋槽已满。");
            return;
        }
        goldTalents[emptySlot] = card.effect;
        applyGoldTalentSideEffects(card.effect);
        finishUpgradeCard(card, spendPendingChoice, closeMenu);
    }

    void applyGoldTalentSideEffects(UpgradeEffect effect) {
        if (effect == UpgradeEffect.ADRENALINE) {
            applyAdrenalineHpPenalty();
        } else if (effect == UpgradeEffect.UNDYING_TOTEM) {
            totemReviveAvailable = true;
        }
    }

    void removeGoldTalentSideEffects(UpgradeEffect effect) {
        if (effect == UpgradeEffect.ADRENALINE) {
            removeAdrenalineHpPenalty();
        } else if (effect == UpgradeEffect.OVERFLOW_ROUND) {
            overflowDamageBank = 0;
            overflowReadyPulseTicks = 0;
        }
    }

    void applyAdrenalineHpPenalty() {
        if (adrenalineHpPenaltyApplied) {
            return;
        }
        maxHp = Math.max(1, (maxHp + 1) / 2);
        hp = Math.max(1, Math.min(maxHp, (hp + 1) / 2));
        adrenalineHpPenaltyApplied = true;
    }

    void removeAdrenalineHpPenalty() {
        if (!adrenalineHpPenaltyApplied) {
            return;
        }
        maxHp = Math.max(PLAYER_BASE_HP, maxHp * 2);
        hp = Math.max(1, Math.min(maxHp, hp * 2));
        adrenalineHpPenaltyApplied = false;
    }

    void applyHighTalentCard(UpgradeCard card, boolean spendPendingChoice, boolean closeMenu) {
        if (hasHighTalent(card.effect)) {
            if (weaponLevel(card.effect) >= MAX_WEAPON_LEVEL) {
                showMessage("Weapon already at max level: " + card.title + ".",
                        "红色武器已满级：" + cardTitle(card) + "。");
                return;
            }
            int level = raiseWeaponLevel(card.effect);
            finishUpgradeCard(card, spendPendingChoice, closeMenu);
            showMessage(effectTitleEn(card.effect) + " upgraded to Lv " + level + ".",
                    effectTitleZh(card.effect) + " 已升级到 Lv " + level + "。");
            return;
        }
        int emptySlot = firstEmptyHighTalentSlot();
        if (emptySlot >= 0) {
            highTalents[emptySlot] = card.effect;
            setWeaponLevel(card.effect, Math.max(1, weaponLevel(card.effect)));
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
            showMessage("Weapon slot is full.", "红色武器槽已满。");
    }

    void finishUpgradeCard(UpgradeCard card, boolean spendPendingChoice, boolean closeMenu) {
        boolean spentXpChoice = spendPendingChoice && pendingUpgradeChoices > 0;
        if (spendPendingChoice && card.rarity == UpgradeRarity.UNCOMMON) {
            blueUpgradeCount = Math.min(MAX_BLUE_UPGRADES, blueUpgradeCount + 1);
        }
        if (spendPendingChoice) {
            pendingUpgradeChoices = Math.max(0, pendingUpgradeChoices - 1);
            if (spentXpChoice) {
                healAfterXpUpgradeChoice();
            }
        }
        if (closeMenu) {
            if (spendPendingChoice && pendingUpgradeChoices > 0 && !bossRewardChoice) {
                buildUpgradeChoices();
                selectedUpgradeIndex = 0;
                selectedOverviewCardIndex = 0;
                overviewSelectionActive = false;
                showMessage("Upgrade chosen: " + card.title + ". " + pendingUpgradeChoices + " queued.",
                        "已选择升级：" + cardTitle(card) + "。剩余 " + pendingUpgradeChoices + " 次。");
                return;
            }
            clearUpgradeChoices();
            choiceMode = ChoiceMode.NONE;
            bossRewardChoice = false;
            overviewSelectionActive = false;
        }
        showMessage("Upgrade chosen: " + card.title + ".",
                "已选择升级：" + cardTitle(card) + "。");
    }

    void healAfterXpUpgradeChoice() {
        int heal = Math.max(1, (maxHp + 1) / 2);
        int beforeHp = hp;
        hp = Math.min(maxHp, hp + heal);
        if (hp > beforeHp) {
            impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0, COLOR_HP_BAR_FILL));
        }
    }

    void clearUpgradeChoices() {
        for (int i = 0; i < upgradeChoices.length; i++) {
            upgradeChoices[i] = null;
        }
    }

    List<UpgradeInventoryCard> currentUpgradeCards() {
        List<UpgradeInventoryCard> cards = new ArrayList<UpgradeInventoryCard>();
        for (int i = 0; i < highTalents.length; i++) {
            if (highTalents[i] != null) {
                cards.add(new UpgradeInventoryCard(highTalents[i], UpgradeRarity.RED,
                        Math.max(1, weaponLevel(highTalents[i])), i));
            }
        }
        for (int i = 0; i < goldTalents.length; i++) {
            if (goldTalents[i] != null) {
                cards.add(new UpgradeInventoryCard(goldTalents[i], UpgradeRarity.HIGH, 1, i));
            }
        }
        addOwnedUpgradeCard(cards, UpgradeEffect.REINFORCED_CORE,
                maxHpUpgradeBonus / Math.max(1, MAX_HP_CARD_BONUS));
        addOwnedUpgradeCard(cards, UpgradeEffect.CALIBRATED_DAMAGE, damageBonusPercent / 12);
        addOwnedUpgradeCard(cards, UpgradeEffect.TRIGGER_TUNING, triggerTuningLevel());
        addOwnedUpgradeCard(cards, UpgradeEffect.COMBO_TUNING, perfectBonus);
        addOwnedUpgradeCard(cards, UpgradeEffect.LONG_WORD_REWARD, longWordRewardLevel);
        addOwnedUpgradeCard(cards, UpgradeEffect.SHORT_WORD_QUICKSHOT, shortWordQuickshotLevel);
        addOwnedUpgradeCard(cards, UpgradeEffect.FINAL_LETTER_BURST, finalLetterBurstLevel);
        addOwnedUpgradeCard(cards, UpgradeEffect.VOWEL_CONVERGENCE, vowelConvergenceLevel);
        addOwnedUpgradeCard(cards, UpgradeEffect.HARD_CONSONANT_BREAK, hardConsonantBreakLevel);
        addOwnedUpgradeCard(cards, UpgradeEffect.PHASE_SWITCH, phaseSwitchLevel);
        addOwnedUpgradeCard(cards, UpgradeEffect.BOSS_BREAKER, bossBreakerLevel);
        addOwnedUpgradeCard(cards, UpgradeEffect.CROSSFEED, crossfeedLevel);
        for (UpgradeEffect effect : UpgradeEffect.values()) {
            if (effectLevel(effect) > 0) {
                addOwnedUpgradeCard(cards, effect, effectLevel(effect));
            }
        }
        return cards;
    }

    void addOwnedUpgradeCard(List<UpgradeInventoryCard> cards, UpgradeEffect effect, int level) {
        if (level <= 0 || effect == UpgradeEffect.TEST_INVINCIBLE || effect == UpgradeEffect.TEST_BIG_XP
                || effect == UpgradeEffect.WEAPON_XP_CACHE
                || effect == UpgradeEffect.GOLD_TALENT_PLACEHOLDER || isWeaponEffect(effect)) {
            return;
        }
        cards.add(new UpgradeInventoryCard(effect, rarityForEffect(effect), level, -1));
    }

    int sellExperienceValue() {
        return Math.max(1, (xpToNext + 2) / 3);
    }

    int sellExperienceValue(UpgradeInventoryCard card) {
        if (card == null) {
            return 0;
        }
        if (card.rarity == UpgradeRarity.RED && isWeaponEffect(card.effect)) {
            return Math.max(1, card.level) * WEAPON_SELL_XP_PER_LEVEL;
        }
        if (card.rarity == UpgradeRarity.HIGH) {
            return GOLD_TALENT_SELL_XP;
        }
        return sellExperienceValue();
    }

    int weaponReplacementExperienceValue(UpgradeEffect effect) {
        if (!isWeaponEffect(effect)) {
            return 0;
        }
        return Math.max(1, weaponLevel(effect)) * WEAPON_SELL_XP_PER_LEVEL;
    }

    int selectedSellExperienceValue(List<UpgradeInventoryCard> cards) {
        if (cards.size() == 0) {
            return 0;
        }
        clampOverviewSelection(cards);
        UpgradeInventoryCard card = cards.get(selectedOverviewCardIndex);
        return canSellUpgradeCard(card) ? sellExperienceValue(card) : 0;
    }

    boolean canSellUpgradeCard(UpgradeInventoryCard card) {
        return card != null && card.effect != UpgradeEffect.UNDYING_TOTEM;
    }

    void clampOverviewSelection(List<UpgradeInventoryCard> cards) {
        if (cards.size() == 0) {
            selectedOverviewCardIndex = 0;
            overviewSelectionActive = false;
            return;
        }
        selectedOverviewCardIndex = Math.max(0, Math.min(selectedOverviewCardIndex, cards.size() - 1));
    }

    void requestSellSelectedOverviewUpgrade() {
        if ((choiceMode != ChoiceMode.UPGRADE && choiceMode != ChoiceMode.OVERVIEW) || bossRewardChoice) {
            return;
        }
        List<UpgradeInventoryCard> cards = currentUpgradeCards();
        clampOverviewSelection(cards);
        if (cards.size() == 0) {
            return;
        }
        UpgradeInventoryCard card = cards.get(selectedOverviewCardIndex);
        if (!canSellUpgradeCard(card)) {
            if (card.effect == UpgradeEffect.UNDYING_TOTEM) {
                showMessage("Undying Totem is locked in its gold slot.",
                        "不死图腾永久占用金色槽位，不能出售。");
            } else {
                showMessage("This upgrade cannot be sold.",
                        "这个升级不能出售。");
            }
            return;
        }
        pendingSellCard = card;
        sellConfirmReturnMode = choiceMode;
        sellConfirmReturnOverviewSelectionActive = overviewSelectionActive;
        choiceMode = ChoiceMode.SELL_CONFIRM;
        int xpValue = sellExperienceValue(card);
        showMessage("Confirm sale: " + effectTitleEn(card.effect) + " for +" + xpValue + " XP.",
                "确认出售：" + effectTitleZh(card.effect) + "，可获得 +" + xpValue + " XP。");
    }

    void confirmPendingSell() {
        if (choiceMode != ChoiceMode.SELL_CONFIRM || pendingSellCard == null) {
            return;
        }
        UpgradeInventoryCard card = pendingSellCard;
        ChoiceMode returnMode = sellConfirmReturnMode == ChoiceMode.NONE
                ? ChoiceMode.OVERVIEW : sellConfirmReturnMode;
        boolean returnOverviewSelection = sellConfirmReturnOverviewSelectionActive;
        int xpValue = sellExperienceValue(card);
        clearSellConfirmation();
        choiceMode = returnMode;
        overviewSelectionActive = returnOverviewSelection;
        if (!removeUpgradeCard(card)) {
            List<UpgradeInventoryCard> cards = currentUpgradeCards();
            clampOverviewSelection(cards);
            return;
        }
        addExperience(xpValue, false);
        List<UpgradeInventoryCard> after = currentUpgradeCards();
        clampOverviewSelection(after);
        showMessage("Sold " + effectTitleEn(card.effect) + " for +" + xpValue + " XP.",
                "已出售 " + effectTitleZh(card.effect) + "，获得 +" + xpValue + " XP。");
    }

    void cancelPendingSell() {
        if (choiceMode != ChoiceMode.SELL_CONFIRM) {
            return;
        }
        ChoiceMode returnMode = sellConfirmReturnMode == ChoiceMode.NONE
                ? ChoiceMode.OVERVIEW : sellConfirmReturnMode;
        boolean returnOverviewSelection = sellConfirmReturnOverviewSelectionActive;
        clearSellConfirmation();
        choiceMode = returnMode;
        overviewSelectionActive = returnOverviewSelection;
        List<UpgradeInventoryCard> cards = currentUpgradeCards();
        clampOverviewSelection(cards);
        showMessage("Sale cancelled.",
                "已取消出售。");
    }

    void clearSellConfirmation() {
        pendingSellCard = null;
        sellConfirmReturnMode = ChoiceMode.NONE;
        sellConfirmReturnOverviewSelectionActive = false;
    }

    boolean removeUpgradeCard(UpgradeInventoryCard card) {
        if (!canSellUpgradeCard(card)) {
            if (card != null && card.effect == UpgradeEffect.UNDYING_TOTEM) {
                showMessage("Undying Totem is locked in its gold slot.",
                        "不死图腾永久占用金色槽位，不能出售。");
            }
            return false;
        }
        if (card.rarity == UpgradeRarity.RED && card.slotIndex >= 0) {
            setWeaponLevel(highTalents[card.slotIndex], 0);
            highTalents[card.slotIndex] = null;
            return true;
        }
        if (card.rarity == UpgradeRarity.HIGH && card.slotIndex >= 0) {
            if (card.effect == UpgradeEffect.UNDYING_TOTEM) {
                showMessage("Undying Totem is locked in its gold slot.",
                        "不死图腾永久占用金色槽位，不能出售。");
                return false;
            }
            removeGoldTalentSideEffects(card.effect);
            goldTalents[card.slotIndex] = null;
            return true;
        }
        if (card.effect == UpgradeEffect.REINFORCED_CORE && maxHpUpgradeBonus > 0) {
            int reduction = Math.min(MAX_HP_CARD_BONUS, maxHpUpgradeBonus);
            maxHpUpgradeBonus -= reduction;
            maxHp = Math.max(PLAYER_BASE_HP, maxHp - reduction);
            hp = Math.min(hp, maxHp);
            return true;
        }
        if (card.effect == UpgradeEffect.CALIBRATED_DAMAGE && damageBonusPercent > 0) {
            damageBonusPercent = Math.max(0, damageBonusPercent - 12);
            return true;
        }
        if (card.effect == UpgradeEffect.TRIGGER_TUNING && fireRateBonusPercent > 0) {
            fireRateBonusPercent = Math.max(0, fireRateBonusPercent - TRIGGER_TUNING_STEP_PERCENT);
            return true;
        }
        if (card.effect == UpgradeEffect.COMBO_TUNING && perfectBonus > 0) {
            perfectBonus--;
            return true;
        }
        if (card.effect == UpgradeEffect.LONG_WORD_REWARD && longWordRewardLevel > 0) {
            longWordRewardLevel--;
            return true;
        }
        if (card.effect == UpgradeEffect.SHORT_WORD_QUICKSHOT && shortWordQuickshotLevel > 0) {
            shortWordQuickshotLevel--;
            return true;
        }
        if (card.effect == UpgradeEffect.FINAL_LETTER_BURST && finalLetterBurstLevel > 0) {
            finalLetterBurstLevel--;
            return true;
        }
        if (card.effect == UpgradeEffect.VOWEL_CONVERGENCE && vowelConvergenceLevel > 0) {
            vowelConvergenceLevel--;
            return true;
        }
        if (card.effect == UpgradeEffect.HARD_CONSONANT_BREAK && hardConsonantBreakLevel > 0) {
            hardConsonantBreakLevel--;
            return true;
        }
        if (card.effect == UpgradeEffect.PHASE_SWITCH && phaseSwitchLevel > 0) {
            phaseSwitchLevel--;
            blueUpgradeCount = Math.max(0, blueUpgradeCount - 1);
            return true;
        }
        if (card.effect == UpgradeEffect.BOSS_BREAKER && bossBreakerLevel > 0) {
            bossBreakerLevel--;
            blueUpgradeCount = Math.max(0, blueUpgradeCount - 1);
            return true;
        }
        if (card.effect == UpgradeEffect.CROSSFEED && crossfeedLevel > 0) {
            crossfeedLevel--;
            blueUpgradeCount = Math.max(0, blueUpgradeCount - 1);
            return true;
        }
        if (effectLevel(card.effect) > 0) {
            effectLevels[card.effect.ordinal()]--;
            if (card.rarity == UpgradeRarity.UNCOMMON) {
                blueUpgradeCount = Math.max(0, blueUpgradeCount - 1);
            }
            return true;
        }
        return false;
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
        if (choiceMode == ChoiceMode.SELL_CONFIRM) {
            chooseSellConfirmation(key);
            return;
        }
        if (choiceMode == ChoiceMode.OVERVIEW) {
            chooseOverviewOnly(key);
            return;
        }
        if (choiceMode != ChoiceMode.UPGRADE) {
            return;
        }
        if (overviewSelectionActive) {
            chooseOverviewCard(key);
            return;
        }
        if (key == KeyEvent.VK_LEFT) {
            selectedUpgradeIndex = (selectedUpgradeIndex + 2) % 3;
        } else if (key == KeyEvent.VK_RIGHT) {
            selectedUpgradeIndex = (selectedUpgradeIndex + 1) % 3;
        } else if (key == KeyEvent.VK_DOWN && !bossRewardChoice) {
            List<UpgradeInventoryCard> cards = currentUpgradeCards();
            if (cards.size() > 0) {
                clampOverviewSelection(cards);
                overviewSelectionActive = true;
            }
        } else if ((key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_BACK_SPACE) && !bossRewardChoice) {
            abandonUpgradeChoice();
        } else if (key == KeyEvent.VK_1) {
            selectedUpgradeIndex = 0;
        } else if (key == KeyEvent.VK_2) {
            selectedUpgradeIndex = 1;
        } else if (key == KeyEvent.VK_3) {
            selectedUpgradeIndex = 2;
        } else if (key == KeyEvent.VK_ENTER) {
            applySelectedUpgradeCard();
        }
    }

    void chooseOverviewOnly(int key) {
        List<UpgradeInventoryCard> cards = currentUpgradeCards();
        clampOverviewSelection(cards);
        if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_SPACE) {
            closeUpgradeOverview();
        } else if (key == KeyEvent.VK_LEFT && cards.size() > 0) {
            moveOverviewSelectionHorizontal(cards, -1);
        } else if (key == KeyEvent.VK_RIGHT && cards.size() > 0) {
            moveOverviewSelectionHorizontal(cards, 1);
        } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP) && cards.size() > 0) {
            moveOverviewSelectionVertical(cards, key == KeyEvent.VK_DOWN ? 1 : -1);
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DELETE || key == KeyEvent.VK_S) {
            overviewSelectionActive = cards.size() > 0;
            requestSellSelectedOverviewUpgrade();
        }
    }

    void chooseOverviewCard(int key) {
        List<UpgradeInventoryCard> cards = currentUpgradeCards();
        clampOverviewSelection(cards);
        if (choiceMode == ChoiceMode.OVERVIEW
                && (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_SPACE)) {
            closeUpgradeOverview();
        } else if (choiceMode == ChoiceMode.UPGRADE && key == KeyEvent.VK_UP
                && selectedOverviewCardIndex < overviewNavigationColumns()) {
            overviewSelectionActive = false;
        } else if (key == KeyEvent.VK_LEFT && cards.size() > 0) {
            moveOverviewSelectionHorizontal(cards, -1);
        } else if (key == KeyEvent.VK_RIGHT && cards.size() > 0) {
            moveOverviewSelectionHorizontal(cards, 1);
        } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP) && cards.size() > 0) {
            moveOverviewSelectionVertical(cards, key == KeyEvent.VK_DOWN ? 1 : -1);
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_DELETE || key == KeyEvent.VK_S) {
            requestSellSelectedOverviewUpgrade();
        } else if ((key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_BACK_SPACE) && !bossRewardChoice) {
            abandonUpgradeChoice();
        } else if (choiceMode == ChoiceMode.UPGRADE && key == KeyEvent.VK_1) {
            overviewSelectionActive = false;
            selectedUpgradeIndex = 0;
        } else if (choiceMode == ChoiceMode.UPGRADE && key == KeyEvent.VK_2) {
            overviewSelectionActive = false;
            selectedUpgradeIndex = 1;
        } else if (choiceMode == ChoiceMode.UPGRADE && key == KeyEvent.VK_3) {
            overviewSelectionActive = false;
            selectedUpgradeIndex = 2;
        }
    }

    void chooseSellConfirmation(int key) {
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_Y) {
            confirmPendingSell();
        } else if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_BACK_SPACE
                || key == KeyEvent.VK_N || key == KeyEvent.VK_SPACE) {
            cancelPendingSell();
        }
    }

    int overviewNavigationColumns() {
        return (choiceMode == ChoiceMode.OVERVIEW || choiceMode == ChoiceMode.UPGRADE) ? 3 : 1;
    }

    void moveOverviewSelectionHorizontal(List<UpgradeInventoryCard> cards, int direction) {
        if (cards.size() == 0) {
            return;
        }
        overviewSelectionActive = true;
        selectedOverviewCardIndex = (selectedOverviewCardIndex + cards.size() + direction) % cards.size();
    }

    void moveOverviewSelectionVertical(List<UpgradeInventoryCard> cards, int direction) {
        if (cards.size() == 0) {
            return;
        }
        overviewSelectionActive = true;
        int columns = Math.max(1, overviewNavigationColumns());
        if (columns == 1) {
            moveOverviewSelectionHorizontal(cards, direction);
            return;
        }
        int next = selectedOverviewCardIndex + direction * columns;
        int column = selectedOverviewCardIndex % columns;
        if (next < 0) {
            int rows = (cards.size() + columns - 1) / columns;
            next = (rows - 1) * columns + column;
            if (next >= cards.size()) {
                next -= columns;
            }
        } else if (next >= cards.size()) {
            next = Math.min(column, cards.size() - 1);
        }
        selectedOverviewCardIndex = Math.max(0, Math.min(next, cards.size() - 1));
    }

    void applySelectedUpgradeCard() {
        UpgradeCard card = upgradeChoices[selectedUpgradeIndex];
        applyUpgradeCard(card, !bossRewardChoice, true);
    }

    void abandonUpgradeChoice() {
        if (choiceMode != ChoiceMode.UPGRADE || pendingUpgradeChoices <= 0) {
            return;
        }
        clearSellConfirmation();
        pendingUpgradeChoices = Math.max(0, pendingUpgradeChoices - 1);
        clearUpgradeChoices();
        choiceMode = ChoiceMode.NONE;
        bossRewardChoice = false;
        overviewSelectionActive = false;
        showMessage("Upgrade abandoned.",
                "已放弃本次升级。");
    }

    void closeUpgradeOverview() {
        if (choiceMode != ChoiceMode.OVERVIEW) {
            return;
        }
        clearSellConfirmation();
        clearUpgradeChoices();
        choiceMode = ChoiceMode.NONE;
        bossRewardChoice = false;
        overviewSelectionActive = false;
        showMessage("Upgrade overview closed.",
                "升级总览已关闭。");
    }

    void chooseHighReplacement(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            String title = pendingHighTalent == null ? "" : cardTitle(pendingHighTalent);
            pendingHighTalent = null;
            choiceMode = returnToTestBackendAfterHighReplace ? ChoiceMode.TEST_BACKEND : ChoiceMode.NONE;
            returnToTestBackendAfterHighReplace = false;
            bossRewardChoice = false;
            showMessage("New weapon abandoned.",
                    title.length() == 0 ? "已放弃新红色武器。" : "已放弃新红色武器：" + title + "。");
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
        if (slot < 0 || slot >= highTalents.length || pendingHighTalent == null) {
            return;
        }
        UpgradeEffect replaced = highTalents[slot];
        int replacementXp = weaponReplacementExperienceValue(replaced);
        highTalents[slot] = pendingHighTalent.effect;
        setWeaponLevel(replaced, 0);
        setWeaponLevel(pendingHighTalent.effect, Math.max(1, weaponLevel(pendingHighTalent.effect)));
        UpgradeCard installed = pendingHighTalent;
        pendingHighTalent = null;
        choiceMode = returnToTestBackendAfterHighReplace ? ChoiceMode.TEST_BACKEND : ChoiceMode.NONE;
        returnToTestBackendAfterHighReplace = false;
        bossRewardChoice = false;
        if (replacementXp > 0) {
            addExperience(replacementXp, false);
        }
        String xpText = replacementXp > 0 ? " +" + replacementXp + " XP" : "";
        showMessage("Weapon replaced: " + effectTitleEn(installed.effect)
                        + " over " + effectTitleEn(replaced) + "." + xpText,
                "红色武器替换：" + effectTitleZh(installed.effect)
                        + " 替换 " + effectTitleZh(replaced) + "。" + xpText);
    }

    void openTestBackend() {
        if (gameOver) {
            return;
        }
        clearSellConfirmation();
        if (!started) {
            started = true;
            runStartTick = tick;
            correctTypedChars = 0;
            resetFrameClock();
        }
        paused = false;
        choiceMode = ChoiceMode.TEST_BACKEND;
        bossRewardChoice = false;
        overviewSelectionActive = false;
        selectedTestUpgradeIndex = 0;
        typingLane = -1;
        typed = "";
        clearUpgradeChoices();
        showMessage("Test backend opened.", "测试后台已打开。");
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
        UpgradeEffect[] red = gameplayPoolFor(UpgradeRarity.RED);
        UpgradeEffect[] effects = new UpgradeEffect[
                tools.length + common.length + uncommon.length + high.length + red.length];
        int index = copyEffects(tools, effects, 0);
        index = copyEffects(common, effects, index);
        index = copyEffects(uncommon, effects, index);
        index = copyEffects(high, effects, index);
        copyEffects(red, effects, index);
        return effects;
    }

    int copyEffects(UpgradeEffect[] source, UpgradeEffect[] target, int startIndex) {
        for (int i = 0; i < source.length; i++) {
            target[startIndex + i] = source[i];
        }
        return startIndex + source.length;
    }

    UpgradeRarity rarityForEffect(UpgradeEffect effect) {
        if (effect == UpgradeEffect.WEAPON_XP_CACHE) {
            return UpgradeRarity.RED;
        }
        if (effect == UpgradeEffect.TEST_INVINCIBLE || effect == UpgradeEffect.TEST_BIG_XP) {
            return UpgradeRarity.HIGH;
        }
        if (effect == UpgradeEffect.GOLD_TALENT_PLACEHOLDER) {
            return UpgradeRarity.HIGH;
        }
        if (isRealGoldTalent(effect)) {
            return UpgradeRarity.HIGH;
        }
        if (isWeaponEffect(effect)) {
            return UpgradeRarity.RED;
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
            return hasHighTalent(effect) ? Math.max(1, weaponLevel(effect)) : 0;
        }
        if (isRealGoldTalent(effect)) {
            return hasGoldTalent(effect) ? 1 : 0;
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
            recentErrorTicks = logicTicks(90);
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
            wrongFlashTicks = logicTicks(12);
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
        inputPulseTicks = logicTicks(6);
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
            completedLaneHighlightSuppressed[completedLane] = true;
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
        pendingAttackShifted = completedWordShifted;
    }

    void finishPendingLaneAttack() {
        int attackLane = pendingAttackLane;
        String completedWord = pendingAttackWord;
        completedWordShifted = pendingAttackShifted;
        pendingLaneAttack = false;
        pendingAttackWord = "";
        pendingAttackShifted = false;
        completeLaneWord(attackLane, completedWord);
    }

    void startCurrentWord(String candidate, int laneIndex) {
        completedLaneHighlightSuppressed[laneIndex] = false;
        currentWordUsedBackspace = false;
        currentWordShifted = shiftHeld;
        currentWordStartedAfterError = recentErrorTicks > 0 || nextWordStartsAfterError;
        currentWordUniqueFirst = false;
        currentWordReachedHalfPrefix = false;
        currentWordHadDualPrefix = false;
        nextWordStartsAfterError = false;
        updateCurrentWordState(candidate, laneIndex);
    }

    void updateCurrentWordState(String candidate, int laneIndex) {
        if (candidate.length() == 0) {
            return;
        }
        if (shiftHeld) {
            currentWordShifted = true;
        }
        currentWordUniqueFirst = isUniqueLaneFirst(candidate.charAt(0));
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
        currentWordReachedHalfPrefix = false;
        currentWordHadDualPrefix = false;
        currentWordShifted = false;
    }

    void recordCompletedWordContext(int previousLane, int completedLane, String completedWord) {
        completedWordWasClean = !currentWordUsedBackspace;
        completedWordUsedBackspace = currentWordUsedBackspace;
        completedWordStartedAfterError = currentWordStartedAfterError;
        completedWordUniqueFirst = currentWordUniqueFirst;
        completedWordReachedHalfPrefix = currentWordReachedHalfPrefix && completedWordWasClean;
        completedWordHadDualPrefix = currentWordHadDualPrefix;
        completedWordShifted = currentWordShifted;
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
        primeCrossfeed(completedWord);

        Target target = nearestTargetInLane(attackLane);
        flashActivatedWordTriggers(attackLane, completedWord, target);
        int damage = baseDamage + combo / 8 + perfectBonus / 2
                + groupOneDamageBonus(completedWord, target)
                + groupTwoToFiveDamageBonus(completedWord, target);
        damage = scaledDamage(damage, target);
        firePrimaryWeapon(completedWord, attackLane, target, damage);
        tryFireOverflowShot(attackLane, target);
        applyHighTalentAfterAttack(completedWord, attackLane, target);
        applyGroupOneAfterAttack(completedWord, attackLane, target);
        applyGroupTwoToFiveAfterAttack(completedWord, attackLane, target);
    }

    void tryFireOverflowShot(int attackLane, Target target) {
        if (!hasGoldTalent(UpgradeEffect.OVERFLOW_ROUND) || overflowDamageBank < OVERFLOW_MAX_DAMAGE
                || !completedWordShifted) {
            return;
        }
        int damage = scaledDamage(OVERFLOW_SHOT_DAMAGE, target);
        overflowDamageBank = 0;
        overflowReadyPulseTicks = 0;
        fireBullet(attackLane, damage, 1, BulletKind.OVERFLOW);
        spawnOverflowMuzzleBurst(attackLane);
        showMessage("Overflow round fired.",
                "溢流子弹已发射。");
    }

    void spawnOverflowMuzzleBurst(int attackLane) {
        double x = PLAYER_X + worldAmount(38);
        double y = LANE_Y[attackLane];
        for (int i = 0; i < 24; i++) {
            double angle = (random.nextDouble() - 0.5) * Math.PI * 0.75;
            double speed = 1.4 + random.nextDouble() * 4.6;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            bulletTrailParticles.add(new BulletTrailParticle(x, y, vx, vy,
                    3.0 + random.nextDouble() * 6.0, 12 + random.nextInt(12),
                    random.nextBoolean() ? COLOR_OVERFLOW : Color.WHITE, 3 + random.nextInt(5),
                    random.nextDouble() * Math.PI * 2.0, (random.nextDouble() - 0.5) * 0.6));
        }
        impacts.add(new Impact(x + worldAmount(18), y, 0, COLOR_OVERFLOW));
    }

    void firePrimaryWeapon(String completedWord, int attackLane, Target target, int damage) {
        if (hasHighTalent(UpgradeEffect.RHYTHM_CANNON)) {
            return;
        }
        if (hasGoldTalent(UpgradeEffect.MELEE) && target != null && target.kind == TargetKind.BOSS) {
            startMeleeRam(target);
            return;
        }
        BulletKind primaryKind = primaryWeaponKind();
        if (primaryKind == BulletKind.PIERCE) {
            int pierceDamage = scaledDamage(4 + combo / 10
                    + groupOneDamageBonus(completedWord, target)
                    + groupTwoToFiveDamageBonus(completedWord, target), target);
            fireLaserVolley(attackLane, pierceDamage);
        } else if (primaryKind == BulletKind.HOMING_SHOT) {
            fireHomingShotgun(attackLane, damage);
        } else if (primaryKind == BulletKind.BUBBLE) {
            fireBubbleShot(attackLane, basicWeaponDamage(damage));
        } else {
            fireSingleShotVolley(attackLane, basicWeaponDamage(damage), 1, primaryKind);
        }
    }

    BulletKind primaryWeaponKind() {
        for (UpgradeEffect talent : highTalents) {
            if (talent == UpgradeEffect.BASIC_WEAPON) {
                return weaponLevel(UpgradeEffect.BASIC_WEAPON) >= MAX_WEAPON_LEVEL
                        ? BulletKind.BUBBLE : BulletKind.BASIC;
            }
            if (talent == UpgradeEffect.FROST_FIELD) {
                return BulletKind.PIERCE;
            }
            if (talent == UpgradeEffect.DRY_ICE_BULLET) {
                return BulletKind.DRY_ICE;
            }
            if (talent == UpgradeEffect.HOMING_SHOTGUN) {
                return BulletKind.HOMING_SHOT;
            }
        }
        return BulletKind.BASIC;
    }

    int basicWeaponDamage(int damage) {
        if (!hasHighTalent(UpgradeEffect.BASIC_WEAPON) || weaponLevel(UpgradeEffect.BASIC_WEAPON) < 2
                || basicWeaponKillDamageBonusPercent <= 0) {
            return damage;
        }
        return Math.max(1, (int) Math.ceil(damage * basicWeaponDisplayedMultiplier()));
    }

    double basicWeaponDisplayedMultiplier() {
        if (!hasHighTalent(UpgradeEffect.BASIC_WEAPON) || weaponLevel(UpgradeEffect.BASIC_WEAPON) < 2
                || basicWeaponKillDamageBonusPercent <= 0) {
            return 1.0;
        }
        return Math.pow(1.01, basicWeaponKillDamageBonusPercent);
    }

    String basicWeaponMultiplierText() {
        return String.format(java.util.Locale.US, "%.3f%%", basicWeaponDisplayedMultiplier() * 100.0);
    }

    void showBasicWeaponMultiplierMessage() {
        setBasicDamageHudValue(basicWeaponMultiplierText());
        showMessage("Basic Gun multiplier " + basicWeaponMultiplierText() + ".",
                "基础枪倍率 " + basicWeaponMultiplierText() + "。");
    }

    void startMeleeRam(Target boss) {
        if (boss == null || boss.kind != TargetKind.BOSS || boss.dead || boss.hp <= 0 || boss.bossDeathAnimating) {
            return;
        }
        int damage = Math.max(1, (int) Math.round(maxHp * 0.20));
        int halfWidth = targetHalfWidth(boss.kind);
        double impactX = Math.max(PLAYER_X + worldAmount(54), boss.x - halfWidth - worldAmount(8));
        double impactY = targetCenterY(boss);
        double knockbackTargetX = targetSpawnX(TargetKind.BOSS);
        boss.hp -= damage;
        boss.hitFlash = Math.max(boss.hitFlash, logicTicks(12));
        boss.bossKnockbackStartX = boss.x;
        boss.bossKnockbackTargetX = knockbackTargetX;
        boss.bossKnockbackDurationTicks = MELEE_BOSS_KNOCKBACK_TICKS;
        boss.bossKnockbackTicks = MELEE_BOSS_KNOCKBACK_TICKS;
        meleeRamTicks = MELEE_RAM_ANIMATION_TICKS;
        meleeRamStartY = playerRenderY();
        meleeRamTargetX = impactX;
        meleeRamTargetY = impactY;
        screenShakeTicks = Math.max(screenShakeTicks, MELEE_BOSS_SCREEN_SHAKE_TICKS);
        impacts.add(new Impact(impactX, impactY, damage, COLOR_MELEE));
        impacts.add(new Impact(impactX + worldAmount(34), impactY, 0, Color.WHITE));
        spawnMeleeBossImpactParticles(impactX, impactY);
    }

    void spawnMeleeBossImpactParticles(double impactX, double impactY) {
        for (int i = 0; i < 40; i++) {
            boolean forward = i % 4 != 0;
            double angle = forward
                    ? (random.nextDouble() - 0.5) * Math.PI * 0.5
                    : Math.PI + (random.nextDouble() - 0.5) * Math.PI * 0.75;
            double speed = forward ? 3.4 + random.nextDouble() * 8.2 : 1.4 + random.nextDouble() * 4.0;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed * 0.72;
            double size = (forward ? 4.5 : 3.0) + random.nextDouble() * 7.5;
            int life = (forward ? 14 : 10) + random.nextInt(forward ? 16 : 10);
            Color color = i % 5 == 0 ? Color.WHITE : COLOR_MELEE;
            bulletTrailParticles.add(new BulletTrailParticle(impactX, impactY, vx, vy, size, life, color,
                    3 + random.nextInt(5), random.nextDouble() * Math.PI * 2.0,
                    (random.nextDouble() - 0.5) * 0.7));
        }
    }

    void applyHighTalentAfterAttack(String completedWord, int attackLane, Target target) {
        if (hasGoldTalent(UpgradeEffect.MELEE) && target != null && target.kind == TargetKind.BOSS) {
            return;
        }
        if (hasHighTalent(UpgradeEffect.RHYTHM_CANNON)) {
            int weaponLevel = weaponLevel(UpgradeEffect.RHYTHM_CANNON);
            if (weaponLevel >= MAX_WEAPON_LEVEL) {
                autoCannonStackPercent = Math.min(500.0, autoCannonStackPercent + 50.0);
                continuousSurgeTicks = 0;
            } else if (weaponLevel >= 2) {
                continuousSurgeTicks = AUTO_CANNON_DECAY_SURGE_TICKS;
            } else {
                continuousSurgeTicks = AUTO_CANNON_QUICK_SURGE_TICKS;
            }
            autoFireCooldown = 0;
            setAutoRateHudValue(autoRateHudText());
            showMessage("Autocannon overcharged by typing.",
                    "自动炮已由输入强化。");
        }
        if (weaponLevel(UpgradeEffect.FROST_FIELD) >= MAX_WEAPON_LEVEL) {
            sustainedLaserTicks = SUSTAINED_LASER_DURATION_TICKS;
            sustainedLaserCooldown = 0;
        }
    }

    void primeCrossfeed(String completedWord) {
        if (crossfeedLevel > 0 && startsWithCrossfeedLetter(completedWord)) {
            crossfeedBonusTicks = Math.max(crossfeedBonusTicks, logicTicks(150));
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
        level = effectLevel(UpgradeEffect.COMBO_CALIBRATOR);
        if (level > 0) {
            bonus += comboCalibratorDamageBonus(level);
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
            target.slowTicks = Math.max(target.slowTicks, logicTicks(34 + level * 12));
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

        level = effectLevel(UpgradeEffect.PREFIX_ILLUMINATION);
        if (level > 0 && completedWordReachedHalfPrefix && target != null) {
            target.markTicks = Math.max(target.markTicks, logicTicks(80 + level * 20));
            target.hitFlash = Math.max(target.hitFlash, logicTicks(4));
        }

        level = effectLevel(UpgradeEffect.FIRST_LETTER_TICKET);
        if (level > 0 && sameFirstLetterStreak >= 3 && sameFirstLetterStreak % 3 == 0) {
            xpOrbs.add(new XpOrb(PLAYER_X + worldAmount(170), attackLane, 1 + level));
        }

        level = effectLevel(UpgradeEffect.DUAL_PREFIX_SCAN);
        if (level > 0 && completedWordHadDualPrefix) {
            fireBullet(1 - attackLane, scaledDamage(2 + level, null), 1, BulletKind.CONTINUOUS);
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
            pullNearestXpOrb(attackLane, worldAmount(46.0 + level * 18.0));
        }

        level = effectLevel(UpgradeEffect.FINAL_LETTER_PULL);
        if (level > 0 && endsWithVowel(word)) {
            pullNearestXpOrb(attackLane, worldAmount(34.0 + level * 14.0));
            if (target != null) {
                target.slowTicks = Math.max(target.slowTicks, logicTicks(24 + level * 10));
            }
        }

        level = effectLevel(UpgradeEffect.LONG_WORD_FOCUS);
        if (level > 0 && completedWordWasClean && isLongLaneWord(word)) {
            longFocusTicks = Math.max(longFocusTicks, logicTicks(130 + level * 28));
                impacts.add(new Impact(PLAYER_X + worldAmount(8), LANE_Y[attackLane], 0));
        }

        level = effectLevel(UpgradeEffect.DANGER_WORD);
        if (level > 0 && isDangerClose(target)) {
            target.x += worldAmount(26 + level * 10);
            target.previousX = Math.min(target.previousX, target.x);
            impacts.add(new Impact(target.x, LANE_Y[attackLane], 0));
        }

        level = effectLevel(UpgradeEffect.PRESSURE_VALVE);
        if (level > 0 && laneHasPressure(attackLane)) {
            laneSlowTicks[attackLane] = Math.max(laneSlowTicks[attackLane], logicTicks(78 + level * 18));
            impacts.add(new Impact(PLAYER_X + worldAmount(40), LANE_Y[attackLane], 0));
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
        pullNearestXpOrb(laneIndex, worldAmount(20.0 + vowelConvergenceLevel * 8.0));
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
            if (orb.lane == laneIndex && Math.abs(orb.x - PLAYER_X) < worldAmount(420)) {
                return true;
            }
        }
        return false;
    }

    boolean hasLaneXp(int laneIndex) {
        for (XpOrb orb : xpOrbs) {
            if (orb.lane == laneIndex) {
                return true;
            }
        }
        return false;
    }

    boolean isDangerClose(Target target) {
        return target != null && target.x <= PLAYER_X + worldAmount(260);
    }

    int comboCalibratorDamageBonus(int level) {
        if (combo == 10) {
            return 4 * level;
        }
        if (combo == 5) {
            return 2 * level;
        }
        if (combo == 3) {
            return level;
        }
        return 0;
    }

    boolean laneHasPressure(int laneIndex) {
        for (Target target : targets) {
            if (target.lane == laneIndex && !target.dead && target.hp > 0
                    && target.x <= PLAYER_X + worldAmount(320)) {
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
        if (target == null) {
            return false;
        }
        return target.kind == TargetKind.TANK || target.kind == TargetKind.SWITCHER
                || target.kind == TargetKind.BOSS || target.maxHp >= scaleEnemyHp(7);
    }

    int scaledDamage(int damage, Target target) {
        int percent = damageBonusPercent;
        if (hasGoldTalent(UpgradeEffect.ADRENALINE)) {
            percent += 50;
        }
        if (hasGoldTalent(UpgradeEffect.RED_EYE) && maxHp > 0) {
            double missingRatio = 1.0 - Math.max(0.0, Math.min(1.0, hp / (double) maxHp));
            percent += (int) Math.round(100.0 * missingRatio);
        }
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
        int bonus = damage * percent / 100;
        if (percent > 0 && bonus == 0) {
            bonus = 1;
        }
        int result = Math.max(1, damage + bonus);
        if (hasGoldTalent(UpgradeEffect.MELEE)) {
            result = Math.max(1, (result + 1) / 2);
        }
        return result;
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

    void fireBubbleShot(int attackLane, int baseTotalDamage) {
        int totalDamage = triggerTunedTotalDamage(baseTotalDamage + Math.max(8, baseTotalDamage));
        Bullet bullet = new Bullet(PLAYER_X + worldAmount(35), attackLane, totalDamage, 1, BulletKind.BUBBLE,
                false);
        bullets.add(bullet);
    }

    void fireLaserVolley(int attackLane, int baseTotalDamage) {
        fireLaserVolley(attackLane, baseTotalDamage, 100);
    }

    void fireLaserVolley(int attackLane, int baseTotalDamage, int laserPowerPercent) {
        int beams = singleShotPelletCount();
        int totalDamage = triggerTunedTotalDamage(baseTotalDamage);
        int baseBeamDamage = totalDamage / beams;
        int remainder = totalDamage % beams;
        double middle = (beams - 1) / 2.0;
        for (int i = 0; i < beams; i++) {
            int beamDamage = baseBeamDamage + (i < remainder ? 1 : 0);
            double visualOffsetY = (i - middle) * sy(6);
            fireLaserBeam(attackLane, beamDamage, 5, visualOffsetY, laserPowerPercent);
        }
    }

    void fireHomingShotgun(int attackLane, int baseTotalDamage) {
        int pellets = homingShotgunPelletCount();
        setShotgunPelletHudValue("+" + Math.max(0, pellets - HOMING_SHOTGUN_BASE_PELLETS));
        int baseDamage = baseTotalDamage + Math.max(4, baseTotalDamage / 2);
        if (weaponLevel(UpgradeEffect.HOMING_SHOTGUN) >= MAX_WEAPON_LEVEL) {
            baseDamage = baseDamage * homingShotgunCrowdDamagePercent() / 100;
        }
        int tunedDamage = triggerTunedTotalDamage(baseDamage);
        int totalDamage = Math.max(pellets, tunedDamage);
        int basePelletDamage = totalDamage / pellets;
        int remainder = totalDamage % pellets;
        double startX = PLAYER_X + worldAmount(31);
        double centerY = LANE_Y[attackLane];
        double middle = (pellets - 1) / 2.0;
        for (int i = 0; i < pellets; i++) {
            int pelletDamage = basePelletDamage + (i < remainder ? 1 : 0);
            Bullet bullet = new Bullet(startX - worldAmount(i * 1.8), attackLane, pelletDamage, 1,
                    BulletKind.HOMING_SHOT, true);
            bullet.accentColor = homingShotgunPelletColor(i);
            double t = pellets == 1 ? 0.5 : i / (double) (pellets - 1);
            double angle = -HOMING_SHOTGUN_FAN_RADIANS / 2.0 + HOMING_SHOTGUN_FAN_RADIANS * t;
            bullet.y = centerY + worldAmount((i - middle) * 1.4);
            bullet.previousY = bullet.y;
            bullet.launchX = bullet.x;
            bullet.launchY = bullet.y;
            bullet.vx = Math.cos(angle) * bullet.speed;
            bullet.vy = Math.sin(angle) * bullet.speed;
            bullets.add(bullet);
        }
    }

    int homingShotgunPelletCount() {
        int weaponLevel = Math.max(1, weaponLevel(UpgradeEffect.HOMING_SHOTGUN));
        int pellets = HOMING_SHOTGUN_BASE_PELLETS;
        if (weaponLevel >= 2) {
            int comboBonus = Math.max(0, combo - 1);
            pellets = Math.min(HOMING_SHOTGUN_UPGRADED_PELLETS,
                    HOMING_SHOTGUN_BASE_PELLETS + comboBonus);
        }
        return pellets + triggerTuningLevel() * 2;
    }

    Color homingShotgunPelletColor(int pelletIndex) {
        if (pelletIndex < HOMING_SHOTGUN_BASE_PELLETS) {
            return COLOR_HOMING_HEAD;
        }
        switch ((pelletIndex - HOMING_SHOTGUN_BASE_PELLETS) % 5) {
            case 0:
                return COLOR_HOMING_ORANGE;
            case 1:
                return COLOR_HOMING_YELLOW;
            case 2:
                return COLOR_HOMING_GREEN;
            case 3:
                return COLOR_HOMING_BLUE;
            default:
                return COLOR_HOMING_PURPLE;
        }
    }

    int homingShotgunCrowdDamagePercent() {
        int livingTargets = 0;
        for (Target target : targets) {
            if (!target.dead && target.hp > 0) {
                livingTargets++;
            }
        }
        return 100 + Math.min(180, livingTargets * 12);
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
        if (meleeRamTicks > 0) {
            return meleeRamPlayerY();
        }
        if (laneSwitchAnimationTicks <= 0) {
            return LANE_Y[lane];
        }
        double age = LANE_SWITCH_ANIMATION_TICKS - laneSwitchAnimationTicks + renderAlpha;
        double t = laneSwitchEase(age / LANE_SWITCH_ANIMATION_TICKS);
        return laneSwitchFromY + (laneSwitchToY - laneSwitchFromY) * t;
    }

    double playerRenderX() {
        if (meleeRamTicks > 0) {
            return meleeRamPlayerX();
        }
        return PLAYER_X;
    }

    double meleeRamProgress() {
        if (meleeRamTicks <= 0) {
            return 0.0;
        }
        double age = MELEE_RAM_ANIMATION_TICKS - meleeRamTicks + renderAlpha;
        return Math.max(0.0, Math.min(1.0, age / Math.max(1.0, MELEE_RAM_ANIMATION_TICKS)));
    }

    double meleeRamPlayerX() {
        double t = meleeRamProgress();
        double outboundEnd = 0.46;
        if (t <= outboundEnd) {
            double p = easeOutCubic(t / outboundEnd);
            return PLAYER_X + (meleeRamTargetX - PLAYER_X) * p;
        }
        double p = smoothstep((t - outboundEnd) / (1.0 - outboundEnd));
        return meleeRamTargetX + (PLAYER_X - meleeRamTargetX) * p;
    }

    double meleeRamPlayerY() {
        double t = meleeRamProgress();
        return meleeRamStartY + (meleeRamTargetY - meleeRamStartY)
                * Math.sin(Math.min(1.0, t * 1.35) * Math.PI / 2.0);
    }

    void fireBullet(int attackLane, int damage, int pierceLeft, BulletKind kind) {
        fireBullet(attackLane, damage, pierceLeft, kind, 0);
    }

    void fireBullet(int attackLane, int damage, int pierceLeft, BulletKind kind, double startOffset) {
        fireBullet(attackLane, damage, pierceLeft, kind, startOffset, true);
    }

    void fireBullet(int attackLane, int damage, int pierceLeft, BulletKind kind,
            double startOffset, boolean particleTrail) {
        if (kind == BulletKind.PIERCE) {
            fireLaserBeam(attackLane, damage, pierceLeft, 0.0);
            return;
        }
        boolean resolvedParticleTrail = particleTrail && kind != BulletKind.BASIC && kind != BulletKind.PIERCE;
        Bullet bullet = new Bullet(PLAYER_X + worldAmount(28 + startOffset), attackLane, damage, pierceLeft, kind,
                resolvedParticleTrail);
        bullets.add(bullet);
    }

    void fireLaserBeam(int attackLane, int damage, int pierceLeft, double visualOffsetY) {
        fireLaserBeam(attackLane, damage, pierceLeft, visualOffsetY, 100);
    }

    void fireLaserBeam(int attackLane, int damage, int pierceLeft, double visualOffsetY, int laserPowerPercent) {
        Bullet bullet = new Bullet(PLAYER_X + worldAmount(28), attackLane, damage, pierceLeft, BulletKind.PIERCE,
                false);
        bullet.laserPowerPercent = Math.max(100, Math.min(300, laserPowerPercent));
        bullet.x = maxBulletX(bullet);
        resolveLaserBeam(bullet);
        bullet.y = LANE_Y[attackLane] + visualOffsetY;
        bullet.previousY = bullet.y;
        spawnLaserBeamParticles(bullet);
        bullet.beamResolved = true;
        bullets.add(bullet);
    }

    Target nearestTargetInLane(int laneIndex) {
        Target best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Target target : targets) {
            if ((target.lane == laneIndex || target.kind == TargetKind.BOSS)
                    && !target.dead && target.hp > 0 && !target.bossDeathAnimating
                    && target.x > PLAYER_X - worldAmount(10)) {
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

    boolean bossReadyToSpawn() {
        return kills >= bossCooldownKills && !hasBoss();
    }

    boolean hasActiveNonBossTarget() {
        for (Target target : targets) {
            if (target.kind != TargetKind.BOSS && !target.dead && target.hp > 0) {
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
        messageTicks = logicTicks(170);
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
        if (tryUseTotemRevive()) {
            return;
        }
        gameOver = true;
        highScore = Math.max(highScore, score);
        timer.stop();
        if (deathReason.length() == 0) {
            setDeathReason("health reached zero", "HP 归零");
        }
    }

    boolean tryUseTotemRevive() {
        if (!hasGoldTalent(UpgradeEffect.UNDYING_TOTEM) || !totemReviveAvailable) {
            return false;
        }
        totemReviveAvailable = false;
        hp = Math.max(1, maxHp / 2);
        combo = 0;
        deathReason = "";
        deathReasonZh = "";
        screenFlashTicks = Math.max(screenFlashTicks, logicTicks(12));
        impacts.add(new Impact(PLAYER_X, LANE_Y[lane], 0, COLOR_RARITY_HIGH));
        showMessage("Undying Totem shattered. Revived at " + hp + " HP.",
                "不死图腾碎裂。已以 " + hp + " HP 复活。");
        return true;
    }

    public void keyTyped(KeyEvent event) {
        // Letter input is handled from keyPressed key codes so IME composition does not affect gameplay.
    }

    public void keyPressed(KeyEvent event) {
        int key = event.getKeyCode();
        if (key == KeyEvent.VK_SHIFT) {
            shiftHeld = true;
            return;
        }
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
        if (!gameOver && key == KeyEvent.VK_SPACE) {
            if (pendingUpgradeChoices > 0) {
                openUpgradeMenu();
            } else {
                openUpgradeOverview();
            }
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
        if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
            shiftHeld = false;
        }
    }

    void reset() {
        targets.clear();
        bullets.clear();
        bulletTrailParticles.clear();
        goldDrones.clear();
        bossProjectiles.clear();
        impacts.clear();
        icePulses.clear();
        breakParticles.clear();
        xpOrbs.clear();
        clearUpgradeChoices();
        clearSellConfirmation();
        lane = 0;
        hp = PLAYER_BASE_HP;
        maxHp = PLAYER_BASE_HP;
        score = 0;
        kills = 0;
        bossLevel = 0;
        combo = 0;
        bestCombo = 0;
        tick = 0;
        spawnCooldown = logicTicks(25);
        bossCooldownKills = 10;
        autoFireCooldown = 0;
        continuousSurgeTicks = 0;
        autoCannonStackPercent = 0.0;
        sustainedLaserTicks = 0;
        sustainedLaserChargeTicks = 0;
        sustainedLaserCooldown = 0;
        autoRateHudValue = "";
        autoRateHudPreviousValue = "";
        autoRateHudTransitionTicks = 0;
        laserDamageHudValue = "";
        laserDamageHudPreviousValue = "";
        laserDamageHudTransitionTicks = 0;
        basicDamageHudValue = "";
        basicDamageHudPreviousValue = "";
        basicDamageHudTransitionTicks = 0;
        shotgunPelletHudValue = "";
        shotgunPelletHudPreviousValue = "";
        shotgunPelletHudTransitionTicks = 0;
        for (int i = 0; i < dynamicHudIds.length; i++) {
            dynamicHudIds[i] = null;
            dynamicHudValues[i] = "";
            dynamicHudPreviousValues[i] = "";
            dynamicHudTransitionTicks[i] = 0;
        }
        basicWeaponKillDamageBonusPercent = 0;
        wrongFlashTicks = 0;
        wrongFlashLane = -1;
        inputPulseTicks = 0;
        completePulseTicks = 0;
        completePulseLane = -1;
        screenFlashTicks = 0;
        bossLaserFlashTicks = 0;
        bossLaserFlashLane = -1;
        overflowDamageBank = 0;
        overflowReadyPulseTicks = 0;
        meleeRamTicks = 0;
        screenShakeTicks = 0;
        shiftHeld = false;
        currentWordShifted = false;
        completedWordShifted = false;
        pendingAttackShifted = false;
        totemReviveAvailable = false;
        adrenalineHpPenaltyApplied = false;
        meleeRamStartY = LANE_Y[0];
        meleeRamTargetX = PLAYER_X;
        meleeRamTargetY = LANE_Y[0];
        baseDamage = 5;
        perfectBonus = 0;
        correctTypedChars = 0;
        runStartTick = tick;
        xp = 0;
        xpToNext = INITIAL_XP_TO_NEXT;
        upgradeLevel = 0;
        pendingUpgradeChoices = 0;
        selectedUpgradeIndex = 0;
        selectedOverviewCardIndex = 0;
        overviewSelectionActive = false;
        clearSellConfirmation();
        blueUpgradeCount = 0;
        damageBonusPercent = 0;
        fireRateBonusPercent = 0;
        maxHpUpgradeBonus = 0;
        phaseSwitchLevel = 0;
        phaseSwitchTicks = 0;
        phaseSwitchCooldownTicks = 0;
        bossBreakerLevel = 0;
        crossfeedLevel = 0;
        crossfeedCooldownTicks = 0;
        crossfeedBonusTicks = 0;
        longWordRewardLevel = 0;
        shortWordQuickshotLevel = 0;
        finalLetterBurstLevel = 0;
        vowelConvergenceLevel = 0;
        hardConsonantBreakLevel = 0;
        selectedTestUpgradeIndex = 0;
        for (int i = 0; i < effectLevels.length; i++) {
            effectLevels[i] = 0;
            weaponLevels[i] = 0;
        }
        for (int i = 0; i < laneBarrierCharges.length; i++) {
            laneBarrierCharges[i] = 0;
            laneSlowTicks[i] = 0;
            completedLaneHighlightSuppressed[i] = false;
            for (int trigger = 0; trigger < WORD_TRIGGER_COUNT; trigger++) {
                wordTriggerFlashTicks[i][trigger] = 0;
            }
        }
        for (int i = 0; i < highTalents.length; i++) {
            highTalents[i] = null;
        }
        for (int i = 0; i < goldTalents.length; i++) {
            goldTalents[i] = null;
        }
        pendingHighTalent = null;
        returnToTestBackendAfterHighReplace = false;
        bossRewardChoice = false;
        laneSwitchFromY = LANE_Y[0];
        laneSwitchToY = LANE_Y[0];
        laneSwitchAnimationTicks = 0;
        pendingLaneAttack = false;
        pendingAttackLane = 0;
        pendingAttackWord = "";
        pendingAttackShifted = false;
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
        completedWordReachedHalfPrefix = false;
        completedWordHadDualPrefix = false;
        completedWordAlternated = false;
        completedWordSwitchedLane = false;
        completedWordShifted = false;
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

        double scale = Math.min(getWidth() / (double) RENDER_WIDTH, getHeight() / (double) RENDER_HEIGHT);
        int scaledWidth = (int) Math.round(RENDER_WIDTH * scale);
        int scaledHeight = (int) Math.round(RENDER_HEIGHT * scale);
        int offsetX = (getWidth() - scaledWidth) / 2;
        int offsetY = (getHeight() - scaledHeight) / 2;

        BufferedImage buffer = logicalFrameBuffer();
        Graphics2D g = buffer.createGraphics();
        g.scale(RENDER_SCALE_X, RENDER_SCALE_Y);
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
        if (frameBuffer == null || frameBuffer.getWidth() != RENDER_WIDTH || frameBuffer.getHeight() != RENDER_HEIGHT) {
            frameBuffer = new BufferedImage(RENDER_WIDTH, RENDER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        }
        return frameBuffer;
    }

    void renderGame(Graphics2D g) {
        drawNeonBackdrop(g);
        int shakeX = screenShakeOffsetX();
        int shakeY = screenShakeOffsetY();
        if (shakeX != 0 || shakeY != 0) {
            g.translate(shakeX, shakeY);
        }
        drawLanes(g);
        drawBossAttackWarnings(g);
        drawXpOrbs(g);
        drawTargets(g);
        drawGoldDrones(g);
        drawBulletTrailParticles(g);
        drawBullets(g);
        drawIcePulses(g);
        drawBreakParticles(g);
        drawImpacts(g);
        drawBossProjectiles(g);
        drawMeleeRam(g);
        drawPlayer(g);
        if (shakeX != 0 || shakeY != 0) {
            g.translate(-shakeX, -shakeY);
        }
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
        drawScreenFlash(g);
    }

    double pulse(double speed, double offset) {
        double value = 0.5 + 0.5 * Math.sin((tick * GAMEPLAY_STEP_SCALE + offset) * speed);
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

    int screenShakeOffsetX() {
        if (screenShakeTicks <= 0) {
            return 0;
        }
        double fade = screenShakeTicks / (double) Math.max(1, MELEE_SCREEN_SHAKE_TICKS);
        return (int) Math.round(Math.sin(tick * 2.7) * sx(6) * fade);
    }

    int screenShakeOffsetY() {
        if (screenShakeTicks <= 0) {
            return 0;
        }
        double fade = screenShakeTicks / (double) Math.max(1, MELEE_SCREEN_SHAKE_TICKS);
        return (int) Math.round(Math.cos(tick * 3.1) * sy(4) * fade);
    }

    static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    static Color blendColor(Color start, Color end, double value) {
        double t = Math.max(0.0, Math.min(1.0, value));
        int red = (int) Math.round(start.getRed() + (end.getRed() - start.getRed()) * t);
        int green = (int) Math.round(start.getGreen() + (end.getGreen() - start.getGreen()) * t);
        int blue = (int) Math.round(start.getBlue() + (end.getBlue() - start.getBlue()) * t);
        int alpha = (int) Math.round(start.getAlpha() + (end.getAlpha() - start.getAlpha()) * t);
        return new Color(red, green, blue, alpha);
    }

    Color laserCoreColor(Bullet bullet) {
        int percent = Math.max(100, Math.min(300, bullet.laserPowerPercent));
        if (percent <= 200) {
            return blendColor(COLOR_LASER_CORE, COLOR_LASER_BURN, (percent - 100) / 100.0);
        }
        return blendColor(COLOR_LASER_BURN, COLOR_LASER_MAX, (percent - 200) / 100.0);
    }

    Color laserAuraColor(Bullet bullet) {
        return blendColor(COLOR_LASER_AURA, laserCoreColor(bullet), 0.38);
    }

    Color laserHotColor(Bullet bullet) {
        return blendColor(laserCoreColor(bullet), COLOR_LASER_HOT, isMaxPowerLaser(bullet) ? 0.22 : 0.36);
    }

    void drawNeonBackdrop(Graphics2D g) {
        int gridStep = sx(80);
        int phase = ((int) (tick * GAMEPLAY_STEP_SCALE / 2.0 * COORD_SCALE)) % gridStep;
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(COLOR_GRID_MINOR);
        for (int x = -gridStep + phase; x < WIDTH + sx(90); x += gridStep) {
            g.drawLine(x, sy(108), x + sx(110), HEIGHT - sy(60));
        }
        g.setColor(COLOR_GRID_MAJOR);
        for (int y = sy(130); y < HEIGHT - sy(70); y += sy(54)) {
            int drift = (int) (Math.sin((tick * GAMEPLAY_STEP_SCALE + y / COORD_SCALE_Y) * 0.018) * sx(10));
            g.drawLine(sx(42), y + drift, WIDTH - sx(42), y - drift / 2);
        }
        int scanY = sy(118) + ((int) (tick * GAMEPLAY_STEP_SCALE * worldAmount(3.0)) % (HEIGHT - sy(180)));
        g.setColor(COLOR_SCANLINE);
        g.fillRect(sx(55), scanY, WIDTH - sx(110), sy(2));
    }

    void drawLanes(Graphics2D g) {
        g.setStroke(STROKE_LANE);
        for (int i = 0; i < 2; i++) {
            int glow = i == lane ? sx(16) + (int) (pulse(0.12, i * 23) * sx(10)) : sx(8);
            g.setColor(COLOR_LANE_GLOW);
            g.fillRoundRect(LANE_LEFT_X - glow / 2, LANE_Y[i] - sy(58) - glow / 2,
                    LANE_RIGHT_X - LANE_LEFT_X + glow, sy(116) + glow, sx(8), sy(8));
            g.setColor(i == lane ? COLOR_LANE_ACTIVE : COLOR_LANE_IDLE);
            g.drawLine(LANE_LEFT_X, LANE_Y[i], LANE_RIGHT_X, LANE_Y[i]);
            g.setColor(COLOR_LANE_FILL);
            g.fillRoundRect(LANE_LEFT_X, LANE_Y[i] - sy(58),
                    LANE_RIGHT_X - LANE_LEFT_X, sy(116), sx(8), sy(8));
            g.setColor(i == lane ? COLOR_LANE_BORDER_ACTIVE : COLOR_LANE_BORDER_IDLE);
            g.drawRoundRect(LANE_LEFT_X, LANE_Y[i] - sy(58),
                    LANE_RIGHT_X - LANE_LEFT_X, sy(116), sx(8), sy(8));
        }
    }

    void drawLaneWordsPanel(Graphics2D g) {
        int cardWidth = sx(760);
        int height = sy(78);
        int labelGap = sx(14);
        int labelWidth = sx(250);
        int rowGap = sy(28);
        int x = (WIDTH - cardWidth) / 2;
        int labelX = x + cardWidth + labelGap;
        int y = HEIGHT - sy(474);
        drawLaneWordCard(g, 0, x, y, cardWidth, height);
        drawWordTriggerSidePanel(g, 0, labelX, y, labelWidth, height);
        int secondY = y + height + rowGap;
        drawLaneWordCard(g, 1, x, secondY, cardWidth, height);
        drawWordTriggerSidePanel(g, 1, labelX, secondY, labelWidth, height);
    }

    boolean laneHasInputPulse(int laneIndex) {
        return inputPulseTicks > 0 && typingLane == laneIndex && typed.length() > 0;
    }

    boolean laneCompletionPulseActive(int laneIndex) {
        return completePulseTicks > 0 && completePulseLane == laneIndex;
    }

    boolean laneWordHighlightSuppressed(int laneIndex) {
        return completedLaneHighlightSuppressed[laneIndex] && !laneCompletionPulseActive(laneIndex);
    }

    boolean laneWordShowsActiveHighlight(int laneIndex) {
        return laneIndex == lane && !laneWordHighlightSuppressed(laneIndex);
    }

    void flashActivatedWordTriggers(int laneIndex, String word, Target target) {
        if (laneIndex < 0 || laneIndex >= wordTriggerFlashTicks.length || word == null) {
            return;
        }
        for (int trigger = 0; trigger < WORD_TRIGGER_COUNT; trigger++) {
            if (wordTriggerActivates(trigger, laneIndex, word, target)) {
                wordTriggerFlashTicks[laneIndex][trigger] = WORD_TRIGGER_FLASH_TICKS;
            }
        }
    }

    boolean wordTriggerVisible(int trigger, int laneIndex) {
        if (laneIndex < 0 || laneIndex >= laneWords.length) {
            return false;
        }
        return wordTriggerPotential(trigger, laneIndex, laneWords[laneIndex]);
    }

    boolean wordTriggerPotential(int trigger, int laneIndex, String word) {
        if (word == null || word.length() == 0) {
            return false;
        }
        Target target = nearestTargetInLane(laneIndex);
        if (trigger == WORD_TRIGGER_LONG_REWARD) {
            return longWordRewardLevel > 0 && isLongLaneWord(word) && hp < maxHp;
        }
        if (trigger == WORD_TRIGGER_SHORT_QUICKSHOT) {
            return shortWordQuickshotLevel > 0 && isShortLaneWord(word);
        }
        if (trigger == WORD_TRIGGER_VOWEL_CONVERGENCE) {
            return vowelConvergenceLevel > 0 && endsWithVowel(word) && hasLaneXp(laneIndex);
        }
        if (trigger == WORD_TRIGGER_HARD_CONSONANT) {
            return hardConsonantBreakLevel > 0 && endsWithHardConsonant(word) && isThickTarget(target);
        }
        if (trigger == WORD_TRIGGER_CROSSFEED) {
            return crossfeedLevel > 0 && startsWithCrossfeedLetter(word);
        }
        if (trigger == WORD_TRIGGER_FINAL_PULL) {
            return effectLevel(UpgradeEffect.FINAL_LETTER_PULL) > 0 && endsWithVowel(word)
                    && (hasLaneXp(laneIndex) || target != null);
        }
        if (trigger == WORD_TRIGGER_LONG_FOCUS) {
            return effectLevel(UpgradeEffect.LONG_WORD_FOCUS) > 0 && isLongLaneWord(word);
        }
        if (trigger == WORD_TRIGGER_SAME_LANE) {
            return effectLevel(UpgradeEffect.SAME_LANE_SUPPRESSION) > 0 && lastCompletedLane == laneIndex;
        }
        if (trigger == WORD_TRIGGER_LANE_SWAP) {
            return effectLevel(UpgradeEffect.LANE_SWAP_BEAT) > 0
                    && lastCompletedLane >= 0 && lastCompletedLane != laneIndex;
        }
        if (trigger == WORD_TRIGGER_FIRST_LOCK) {
            return effectLevel(UpgradeEffect.FIRST_LETTER_LOCK) > 0 && isUniqueLaneFirst(word.charAt(0));
        }
        if (trigger == WORD_TRIGGER_PREFIX_MARK) {
            return effectLevel(UpgradeEffect.PREFIX_ILLUMINATION) > 0;
        }
        if (trigger == WORD_TRIGGER_FIRST_TICKET) {
            char first = Character.toLowerCase(word.charAt(0));
            return effectLevel(UpgradeEffect.FIRST_LETTER_TICKET) > 0
                    && sameFirstLetterStreak >= 2 && first == lastCompletedFirstChar;
        }
        if (trigger == WORD_TRIGGER_DUAL_PREFIX) {
            return effectLevel(UpgradeEffect.DUAL_PREFIX_SCAN) > 0
                    && isDualPrefixCandidate(String.valueOf(word.charAt(0)), laneIndex);
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_FIX) {
            return effectLevel(UpgradeEffect.BACKSPACE_FIX) > 0
                    && typingLane == laneIndex && currentWordUsedBackspace;
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_COUNTER) {
            return effectLevel(UpgradeEffect.BACKSPACE_COUNTER) > 0
                    && typingLane == laneIndex && currentWordUsedBackspace;
        }
        if (trigger == WORD_TRIGGER_PRECISE_PICKUP) {
            return effectLevel(UpgradeEffect.PRECISE_PICKUP) > 0 && hasNearbyLaneXp(laneIndex);
        }
        if (trigger == WORD_TRIGGER_DANGER_WORD) {
            return effectLevel(UpgradeEffect.DANGER_WORD) > 0 && isDangerClose(target);
        }
        if (trigger == WORD_TRIGGER_PRESSURE_VALVE) {
            return effectLevel(UpgradeEffect.PRESSURE_VALVE) > 0 && laneHasPressure(laneIndex);
        }
        if (trigger == WORD_TRIGGER_OVERFLOW) {
            return hasGoldTalent(UpgradeEffect.OVERFLOW_ROUND) && overflowDamageBank >= OVERFLOW_MAX_DAMAGE;
        }
        if (trigger == WORD_TRIGGER_MELEE) {
            return hasGoldTalent(UpgradeEffect.MELEE) && target != null && target.kind == TargetKind.BOSS;
        }
        return false;
    }

    boolean wordTriggerActivates(int trigger, int laneIndex, String word, Target target) {
        if (word == null || word.length() == 0) {
            return false;
        }
        if (trigger == WORD_TRIGGER_LONG_REWARD) {
            return longWordRewardLevel > 0 && isLongLaneWord(word) && hp < maxHp;
        }
        if (trigger == WORD_TRIGGER_SHORT_QUICKSHOT) {
            return shortWordQuickshotLevel > 0 && isShortLaneWord(word);
        }
        if (trigger == WORD_TRIGGER_VOWEL_CONVERGENCE) {
            return vowelConvergenceLevel > 0 && endsWithVowel(word) && hasLaneXp(laneIndex);
        }
        if (trigger == WORD_TRIGGER_HARD_CONSONANT) {
            return hardConsonantBreakLevel > 0 && endsWithHardConsonant(word) && isThickTarget(target);
        }
        if (trigger == WORD_TRIGGER_CROSSFEED) {
            return crossfeedLevel > 0 && startsWithCrossfeedLetter(word);
        }
        if (trigger == WORD_TRIGGER_FINAL_PULL) {
            return effectLevel(UpgradeEffect.FINAL_LETTER_PULL) > 0 && endsWithVowel(word)
                    && (hasLaneXp(laneIndex) || target != null);
        }
        if (trigger == WORD_TRIGGER_LONG_FOCUS) {
            return effectLevel(UpgradeEffect.LONG_WORD_FOCUS) > 0
                    && completedWordWasClean && isLongLaneWord(word);
        }
        if (trigger == WORD_TRIGGER_SAME_LANE) {
            return effectLevel(UpgradeEffect.SAME_LANE_SUPPRESSION) > 0 && sameLaneStreak >= 2;
        }
        if (trigger == WORD_TRIGGER_LANE_SWAP) {
            return effectLevel(UpgradeEffect.LANE_SWAP_BEAT) > 0 && completedWordAlternated;
        }
        if (trigger == WORD_TRIGGER_FIRST_LOCK) {
            return effectLevel(UpgradeEffect.FIRST_LETTER_LOCK) > 0 && completedWordUniqueFirst;
        }
        if (trigger == WORD_TRIGGER_PREFIX_MARK) {
            return effectLevel(UpgradeEffect.PREFIX_ILLUMINATION) > 0 && completedWordReachedHalfPrefix;
        }
        if (trigger == WORD_TRIGGER_FIRST_TICKET) {
            return effectLevel(UpgradeEffect.FIRST_LETTER_TICKET) > 0
                    && sameFirstLetterStreak >= 3 && sameFirstLetterStreak % 3 == 0;
        }
        if (trigger == WORD_TRIGGER_DUAL_PREFIX) {
            return effectLevel(UpgradeEffect.DUAL_PREFIX_SCAN) > 0 && completedWordHadDualPrefix;
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_FIX) {
            return effectLevel(UpgradeEffect.BACKSPACE_FIX) > 0 && completedWordUsedBackspace;
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_COUNTER) {
            return effectLevel(UpgradeEffect.BACKSPACE_COUNTER) > 0 && completedWordUsedBackspace;
        }
        if (trigger == WORD_TRIGGER_PRECISE_PICKUP) {
            return effectLevel(UpgradeEffect.PRECISE_PICKUP) > 0 && hasNearbyLaneXp(laneIndex);
        }
        if (trigger == WORD_TRIGGER_DANGER_WORD) {
            return effectLevel(UpgradeEffect.DANGER_WORD) > 0 && isDangerClose(target);
        }
        if (trigger == WORD_TRIGGER_PRESSURE_VALVE) {
            return effectLevel(UpgradeEffect.PRESSURE_VALVE) > 0 && laneHasPressure(laneIndex);
        }
        if (trigger == WORD_TRIGGER_OVERFLOW) {
            return hasGoldTalent(UpgradeEffect.OVERFLOW_ROUND) && overflowDamageBank >= OVERFLOW_MAX_DAMAGE
                    && completedWordShifted;
        }
        if (trigger == WORD_TRIGGER_MELEE) {
            return hasGoldTalent(UpgradeEffect.MELEE) && target != null && target.kind == TargetKind.BOSS;
        }
        return false;
    }

    UpgradeEffect wordTriggerEffect(int trigger) {
        if (trigger == WORD_TRIGGER_LONG_REWARD) {
            return UpgradeEffect.LONG_WORD_REWARD;
        }
        if (trigger == WORD_TRIGGER_SHORT_QUICKSHOT) {
            return UpgradeEffect.SHORT_WORD_QUICKSHOT;
        }
        if (trigger == WORD_TRIGGER_VOWEL_CONVERGENCE) {
            return UpgradeEffect.VOWEL_CONVERGENCE;
        }
        if (trigger == WORD_TRIGGER_HARD_CONSONANT) {
            return UpgradeEffect.HARD_CONSONANT_BREAK;
        }
        if (trigger == WORD_TRIGGER_CROSSFEED) {
            return UpgradeEffect.CROSSFEED;
        }
        if (trigger == WORD_TRIGGER_FINAL_PULL) {
            return UpgradeEffect.FINAL_LETTER_PULL;
        }
        if (trigger == WORD_TRIGGER_LONG_FOCUS) {
            return UpgradeEffect.LONG_WORD_FOCUS;
        }
        if (trigger == WORD_TRIGGER_SAME_LANE) {
            return UpgradeEffect.SAME_LANE_SUPPRESSION;
        }
        if (trigger == WORD_TRIGGER_LANE_SWAP) {
            return UpgradeEffect.LANE_SWAP_BEAT;
        }
        if (trigger == WORD_TRIGGER_FIRST_LOCK) {
            return UpgradeEffect.FIRST_LETTER_LOCK;
        }
        if (trigger == WORD_TRIGGER_PREFIX_MARK) {
            return UpgradeEffect.PREFIX_ILLUMINATION;
        }
        if (trigger == WORD_TRIGGER_FIRST_TICKET) {
            return UpgradeEffect.FIRST_LETTER_TICKET;
        }
        if (trigger == WORD_TRIGGER_DUAL_PREFIX) {
            return UpgradeEffect.DUAL_PREFIX_SCAN;
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_FIX) {
            return UpgradeEffect.BACKSPACE_FIX;
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_COUNTER) {
            return UpgradeEffect.BACKSPACE_COUNTER;
        }
        if (trigger == WORD_TRIGGER_PRECISE_PICKUP) {
            return UpgradeEffect.PRECISE_PICKUP;
        }
        if (trigger == WORD_TRIGGER_DANGER_WORD) {
            return UpgradeEffect.DANGER_WORD;
        }
        if (trigger == WORD_TRIGGER_PRESSURE_VALVE) {
            return UpgradeEffect.PRESSURE_VALVE;
        }
        if (trigger == WORD_TRIGGER_OVERFLOW) {
            return UpgradeEffect.OVERFLOW_ROUND;
        }
        if (trigger == WORD_TRIGGER_MELEE) {
            return UpgradeEffect.MELEE;
        }
        return UpgradeEffect.COMBO_TUNING;
    }

    String wordTriggerLabel(int trigger) {
        if (trigger == WORD_TRIGGER_LONG_REWARD) {
            return ui("LONG", "长词");
        }
        if (trigger == WORD_TRIGGER_SHORT_QUICKSHOT) {
            return ui("SHORT", "短词");
        }
        if (trigger == WORD_TRIGGER_VOWEL_CONVERGENCE) {
            return ui("VOWEL", "元音");
        }
        if (trigger == WORD_TRIGGER_HARD_CONSONANT) {
            return ui("HARD", "硬辅");
        }
        if (trigger == WORD_TRIGGER_CROSSFEED) {
            return "F/T/K";
        }
        if (trigger == WORD_TRIGGER_FINAL_PULL) {
            return ui("PULL", "牵引");
        }
        if (trigger == WORD_TRIGGER_LONG_FOCUS) {
            return ui("FOCUS", "专注");
        }
        if (trigger == WORD_TRIGGER_SAME_LANE) {
            return ui("CHAIN", "同路");
        }
        if (trigger == WORD_TRIGGER_LANE_SWAP) {
            return ui("SWAP", "换拍");
        }
        if (trigger == WORD_TRIGGER_FIRST_LOCK) {
            return ui("LOCK", "首锁");
        }
        if (trigger == WORD_TRIGGER_PREFIX_MARK) {
            return ui("MARK", "标记");
        }
        if (trigger == WORD_TRIGGER_FIRST_TICKET) {
            return ui("TICKET", "税票");
        }
        if (trigger == WORD_TRIGGER_DUAL_PREFIX) {
            return ui("DUAL", "双前");
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_FIX) {
            return ui("FIX", "回修");
        }
        if (trigger == WORD_TRIGGER_BACKSPACE_COUNTER) {
            return ui("COUNTER", "反击");
        }
        if (trigger == WORD_TRIGGER_PRECISE_PICKUP) {
            return "XP";
        }
        if (trigger == WORD_TRIGGER_DANGER_WORD) {
            return ui("DANGER", "近险");
        }
        if (trigger == WORD_TRIGGER_PRESSURE_VALVE) {
            return ui("VALVE", "压力");
        }
        if (trigger == WORD_TRIGGER_OVERFLOW) {
            return ui("SHIFT", "溢流");
        }
        if (trigger == WORD_TRIGGER_MELEE) {
            return ui("RAM", "冲撞");
        }
        return "";
    }

    int visibleWordTriggerCount(int laneIndex) {
        int count = 0;
        for (int trigger = 0; trigger < WORD_TRIGGER_COUNT; trigger++) {
            if (wordTriggerVisible(trigger, laneIndex)) {
                count++;
            }
        }
        return count;
    }

    void drawLaneWordCard(Graphics2D g, int laneIndex, int x, int y, int width, int height) {
        boolean active = laneWordShowsActiveHighlight(laneIndex);
        boolean typing = typingLane == laneIndex && typed.length() > 0;
        boolean inputPulse = laneHasInputPulse(laneIndex);
        boolean wrong = wrongFlashTicks > 0 && (wrongFlashLane < 0 || wrongFlashLane == laneIndex);
        boolean completed = laneCompletionPulseActive(laneIndex);
        Color edge = active ? COLOR_CARD_ACTIVE : COLOR_CARD_IDLE;
        if (typing) {
            edge = COLOR_TYPED_PREFIX;
        }
        if (wrong) {
            edge = COLOR_TARGET_SWITCHER;
        }
        if (active || typing) {
            int keyPulse = inputPulse ? inputPulseTicks : 0;
            int pulse = sx(6) + (int) (pulse(0.18, laneIndex * 17) * sx(8)) + sx(keyPulse);
            g.setColor(typing ? withAlpha(COLOR_TYPED_PREFIX, 42 + keyPulse * 9) : COLOR_CARD_GLOW);
            g.fillRoundRect(x - pulse, y - pulse, width + pulse * 2, height + pulse * 2, sx(10), sy(10));
        }
        if (completed) {
            int age = WORD_COMPLETE_PULSE_TICKS - completePulseTicks;
            int grow = sx(18) + sx((int) Math.round(age * GAMEPLAY_STEP_SCALE * 4.0));
            int alpha = Math.max(24, completePulseTicks * 9);
            g.setColor(withAlpha(COLOR_WORD_COMPLETE, Math.max(18, alpha / 3)));
            g.fillRoundRect(x - grow, y - grow, width + grow * 2, height + grow * 2, sx(18), sy(18));
            g.setStroke(STROKE_WORD_COMPLETE_AURA);
            g.setColor(withAlpha(COLOR_WORD_COMPLETE, alpha));
            g.drawRoundRect(x - grow, y - grow, width + grow * 2, height + grow * 2, sx(18), sy(18));
            g.setStroke(STROKE_WORD_COMPLETE_CORE);
            g.setColor(withAlpha(Color.WHITE, Math.max(20, alpha / 2)));
            g.drawRoundRect(x - grow / 2, y - grow / 2, width + grow, height + grow, sx(14), sy(14));
        }
        if (wrong) {
            int grow = sx(4) + sx((int) Math.round(wrongFlashTicks * GAMEPLAY_STEP_SCALE));
            g.setColor(withAlpha(COLOR_TARGET_SWITCHER, 38 + wrongFlashTicks * 7));
            g.fillRoundRect(x - grow, y - grow, width + grow * 2, height + grow * 2, sx(12), sy(12));
        }
        g.setColor(COLOR_PANEL_DARK);
        g.fillRoundRect(x, y, width, height, sx(8), sy(8));
        g.setStroke(active ? STROKE_CARD_ACTIVE : STROKE_CARD_IDLE);
        g.setColor(edge);
        g.drawRoundRect(x, y, width, height, sx(8), sy(8));

        String word = laneWords[laneIndex];
        drawLaneArrowIcon(g, x + sx(45), y + height / 2, laneIndex, edge);
        g.setFont(FONT_LANE_WORD);
        FontMetrics metrics = g.getFontMetrics();
        int wordAreaX = x + sx(105);
        int wordAreaWidth = Math.max(sx(160), width - sx(135));
        int textX = wordAreaX + (wordAreaWidth - metrics.stringWidth(word)) / 2;
        int textY = y + sy(53);
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

    void drawWordTriggerSidePanel(Graphics2D g, int laneIndex, int x, int y, int width, int height) {
        int visibleCount = visibleWordTriggerCount(laneIndex);
        if (visibleCount == 0) {
            return;
        }
        Color stripAccent = laneIndex == lane ? COLOR_CARD_ACTIVE : COLOR_CARD_IDLE;
        int pulseAlpha = 18 + (int) Math.round(pulse(0.16, laneIndex * 29) * 14);
        g.setColor(withAlpha(stripAccent, pulseAlpha));
        g.fillRoundRect(x, y, width, height, sx(8), sy(8));
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(withAlpha(stripAccent, 118));
        g.drawRoundRect(x, y, width, height, sx(8), sy(8));

        int padding = sx(9);
        int gapX = sx(7);
        int gapY = sy(6);
        int rows = 2;
        int innerWidth = width - padding * 2;
        int innerHeight = height - padding * 2;
        int chipHeight = Math.max(sy(22), (innerHeight - gapY * (rows - 1)) / rows);
        int minChipWidth = sx(94);
        int columns = Math.max(1, (innerWidth + gapX) / Math.max(1, minChipWidth + gapX));
        int maxSlots = Math.max(1, rows * columns);
        int visibleSlots = Math.min(visibleCount, maxSlots);
        boolean collapseExtra = visibleCount > visibleSlots;
        int triggerSlots = collapseExtra ? Math.max(0, visibleSlots - 1) : visibleSlots;
        int chipWidth = Math.max(sx(58), (innerWidth - gapX * Math.max(0, columns - 1))
                / Math.max(1, columns));
        int drawn = 0;
        int hidden = 0;
        int hiddenFlash = 0;
        for (int trigger = 0; trigger < WORD_TRIGGER_COUNT; trigger++) {
            if (!wordTriggerVisible(trigger, laneIndex)) {
                continue;
            }
            if (drawn >= triggerSlots) {
                hidden++;
                hiddenFlash = Math.max(hiddenFlash, wordTriggerFlashTicks[laneIndex][trigger]);
                continue;
            }
            UpgradeEffect effect = wordTriggerEffect(trigger);
            Color accent = colorFor(rarityForEffect(effect));
            int flash = wordTriggerFlashTicks[laneIndex][trigger];
            int chipX = x + padding + (drawn % columns) * (chipWidth + gapX);
            int chipY = y + padding + (drawn / columns) * (chipHeight + gapY);
            drawWordTriggerChip(g, wordTriggerLabel(trigger), accent, flash, chipX, chipY,
                    chipWidth, chipHeight);
            drawn++;
        }
        if (hidden > 0) {
            int chipX = x + padding + (drawn % columns) * (chipWidth + gapX);
            int chipY = y + padding + (drawn / columns) * (chipHeight + gapY);
            Color accent = COLOR_XP;
            drawWordTriggerChip(g, "+" + hidden, accent, hiddenFlash, chipX, chipY, chipWidth, chipHeight);
        }
    }

    void drawWordTriggerChip(Graphics2D g, String label, Color accent, int flash,
            int x, int y, int width, int height) {
        if (flash > 0) {
            int grow = sx(3) + (int) Math.round(sx(7) * flash / (double) WORD_TRIGGER_FLASH_TICKS);
            g.setColor(withAlpha(accent, 58 + flash * 5));
            g.fillRoundRect(x - grow, y - grow, width + grow * 2, height + grow * 2, sx(9), sy(9));
        }
        g.setColor(withAlpha(accent, flash > 0 ? 76 : 34));
        g.fillRoundRect(x, y, width, height, sx(7), sy(7));
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(withAlpha(accent, flash > 0 ? 245 : 170));
        g.drawRoundRect(x, y, width, height, sx(7), sy(7));
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(12f));
        FontMetrics metrics = g.getFontMetrics();
        int textY = y + (height + metrics.getAscent() - metrics.getDescent()) / 2;
        g.setColor(flash > 0 ? Color.WHITE : COLOR_TYPED_SUFFIX);
        drawFittedString(g, label, x + sx(8), textY, width - sx(16));
    }

    void drawLaneArrowIcon(Graphics2D g, int x, int y, int laneIndex, Color color) {
        Polygon arrow = new Polygon();
        if (laneIndex == 0) {
            arrow.addPoint(x, y - sy(19));
            arrow.addPoint(x - sx(18), y + sy(15));
            arrow.addPoint(x + sx(18), y + sy(15));
        } else {
            arrow.addPoint(x, y + sy(19));
            arrow.addPoint(x - sx(18), y - sy(15));
            arrow.addPoint(x + sx(18), y - sy(15));
        }
        g.setColor(withAlpha(color, 70));
        g.fillOval(x - sx(28), y - sy(28), sx(56), sy(56));
        g.setColor(color);
        g.fillPolygon(arrow);
    }

    void drawPlayer(Graphics2D g) {
        int x = (int) Math.round(playerRenderX());
        int y = (int) Math.round(playerRenderY());
        int pulse = sx(5) + (int) (pulse(0.16, 0) * sx(6));
        g.setColor(COLOR_PLAYER_GLOW);
        g.fillOval(x - sx(22) - pulse, y - sy(22) - pulse, sx(44) + pulse * 2, sy(44) + pulse * 2);
        if (laneBarrierCharges[lane] > 0 || calmGuardCharges > 0) {
            int shield = sx(31) + (int) (pulse(0.2, 11) * sx(5));
            g.setColor(withAlpha(COLOR_WORD_COMPLETE, 110));
            g.drawOval(x - shield, y - shield, shield * 2, shield * 2);
        }
        g.setColor(COLOR_PLAYER);
        g.fillOval(x - sx(22), y - sy(22), sx(44), sy(44));
        g.setColor(COLOR_PLAYER_TEXT);
        g.setFont(uiFont(FONT_TARGET_HP, FONT_PRESSURE_ZH));
        g.drawString(ui("YOU", "你"), x - sx(isChinese() ? 7 : 12), y + sy(5));
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

    void drawBossAttackWarnings(Graphics2D g) {
        for (Target target : targets) {
            if (target.kind != TargetKind.BOSS || target.bossDeathAnimating || target.bossLaserWarningTicks <= 0
                    || target.bossLaserLane < 0) {
                continue;
            }
            double progress = 1.0
                    - target.bossLaserWarningTicks / (double) Math.max(1, bossLaserWarningDurationTicks());
            int y = LANE_Y[target.bossLaserLane];
            int alpha = 38 + (int) (95 * smoothstep(progress))
                    + (int) (pulse(0.75, target.bossLaserLane) * 42);
            g.setColor(withAlpha(COLOR_BOSS_WARNING, alpha));
            g.fillRoundRect(LANE_LEFT_X, y - sy(58), LANE_RIGHT_X - LANE_LEFT_X, sy(116), sx(8), sy(8));
            g.setStroke(STROKE_LASER_CORE);
            g.setColor(withAlpha(COLOR_LASER_CORE, Math.min(245, alpha + 70)));
            int warningX = (int) Math.round(target.x - targetHalfWidth(TargetKind.BOSS) * progress);
            g.drawLine(warningX, y, PLAYER_X + sx(18), y);
        }
        if (bossLaserFlashTicks > 0 && bossLaserFlashLane >= 0) {
            double fade = bossLaserFlashTicks / (double) Math.max(1, logicTicks(14));
            int y = LANE_Y[bossLaserFlashLane];
            g.setStroke(STROKE_LASER_AURA);
            g.setColor(withAlpha(COLOR_LASER_AURA, (int) (210 * fade)));
            g.drawLine(LANE_RIGHT_X, y, PLAYER_X, y);
            g.setStroke(STROKE_LASER_CORE);
            g.setColor(withAlpha(COLOR_LASER_HOT, (int) (245 * fade)));
            g.drawLine(LANE_RIGHT_X, y, PLAYER_X, y);
        }
    }

    void drawBossProjectiles(Graphics2D g) {
        for (BossProjectile projectile : bossProjectiles) {
            int x = (int) Math.round(renderX(projectile.previousX, projectile.x));
            int y = (int) Math.round(renderY(projectile.previousY, projectile.y));
            int radius = (int) Math.round(projectile.radius);
            int pulse = (int) (sx(5) * pulse(0.44, projectile.waveOffset));
            g.setColor(withAlpha(COLOR_BOSS_PROJECTILE, 72));
            g.fillOval(x - radius - pulse, y - radius - pulse, (radius + pulse) * 2, (radius + pulse) * 2);
            g.setColor(COLOR_BOSS_PROJECTILE);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            g.setColor(COLOR_BOSS_PROJECTILE_CORE);
            fillCenteredOval(g, x, y, sx(9), sy(9));
        }
    }

    void drawScreenFlash(Graphics2D g) {
        if (screenFlashTicks <= 0) {
            return;
        }
        double fade = screenFlashTicks / (double) Math.max(1, BOSS_SCREEN_FLASH_TICKS);
        int alpha = (int) Math.round(245 * smoothstep(fade));
        g.setColor(new Color(255, 255, 255, Math.max(0, Math.min(255, alpha))));
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    void drawTargets(Graphics2D g) {
        g.setFont(FONT_TARGET_TITLE);
        for (Target target : targets) {
            int x = (int) Math.round(renderX(target.previousX, target.x));
            int y = (int) Math.round(targetCenterY(target));
            Color color = colorFor(target);
            if (target.hitFlash > 0 && target.freezeTicks <= 0) {
                color = Color.WHITE;
            }
            double bossDeathProgress = bossDeathProgress(target);
            if (target.bossDeathAnimating) {
                color = blendColor(COLOR_TARGET_BOSS, Color.WHITE, 0.35 + bossDeathProgress * 0.6);
            }
            double deathScale = target.bossDeathAnimating ? 1.0 + bossDeathProgress * 0.14 : 1.0;
            int w = (int) Math.round((target.kind == TargetKind.BOSS ? sx(164) : sx(92)) * deathScale);
            int h = (int) Math.round((target.kind == TargetKind.BOSS ? sy(236) : sy(50)) * deathScale);
            int glow = target.kind == TargetKind.BOSS ? sx(10) : sx(6);
            g.setColor(COLOR_TARGET_GLOW);
            g.fillRoundRect(x - w / 2 - glow, y - h / 2 - glow, w + glow * 2, h + glow * 2, sx(10), sy(10));
            if (target.bossDeathAnimating) {
                int burstGlow = sx(16 + (int) (bossDeathProgress * 32));
                g.setColor(withAlpha(Color.WHITE, (int) (75 + bossDeathProgress * 115)));
                g.fillRoundRect(x - w / 2 - burstGlow, y - h / 2 - burstGlow,
                        w + burstGlow * 2, h + burstGlow * 2, sx(18), sy(18));
            }
            g.setColor(color);
            g.fillRoundRect(x - w / 2, y - h / 2, w, h, sx(8), sy(8));
            if (target.laserBurnTicks > 0) {
                g.setStroke(STROKE_CARD_ACTIVE);
                int burnAlpha = 120 + (int) (pulse(0.32, target.laserHitCount) * 80);
                g.setColor(withAlpha(COLOR_LASER_BURN, burnAlpha));
                g.drawRoundRect(x - w / 2 - sx(12), y - h / 2 - sy(12), w + sx(24), h + sy(24),
                        sx(14), sy(14));
            }
            if (target.freezeTicks > 0) {
                g.setStroke(STROKE_CARD_ACTIVE);
                g.setColor(withAlpha(COLOR_DRY_ICE_PULSE, 190));
                g.drawRoundRect(x - w / 2 - sx(9), y - h / 2 - sy(9), w + sx(18), h + sy(18),
                        sx(12), sy(12));
            } else if (target.slowTicks > 0 || laneSlowTicks[target.lane] > 0) {
                g.setStroke(STROKE_CARD_ACTIVE);
                g.setColor(withAlpha(COLOR_PIERCE_CORE, 155));
                g.drawRoundRect(x - w / 2 - sx(6), y - h / 2 - sy(6), w + sx(12), h + sy(12),
                        sx(10), sy(10));
            }
            if (target.markTicks > 0) {
                g.setStroke(STROKE_CARD_ACTIVE);
                g.setColor(withAlpha(COLOR_UPGRADE_READY, 165));
                g.drawRoundRect(x - w / 2 - sx(11), y - h / 2 - sy(11), w + sx(22), h + sy(22),
                        sx(12), sy(12));
            }
            g.setColor(COLOR_INK_DARK);
            g.drawRoundRect(x - w / 2, y - h / 2, w, h, sx(8), sy(8));

            g.setColor(Color.WHITE);
            drawCentered(g, targetLabel(target), x, y - sy(4));
            drawTargetHealthBar(g, target, x, y + h / 2 + sy(7), w - sx(18));
            g.setFont(FONT_TARGET_HP);
            drawCentered(g, "HP " + Math.max(0, target.hp), x, y + sy(18));
            g.setFont(FONT_TARGET_TITLE);
        }
    }

    void drawTargetHealthBar(Graphics2D g, Target target, int cx, int y, int width) {
        int height = sy(4);
        int filled = Math.max(0, Math.min(width, target.hp * width / Math.max(1, target.maxHp)));
        g.setColor(COLOR_HP_BAR_BACK);
        g.fillRoundRect(cx - width / 2, y, width, height, sx(3), sy(3));
        g.setColor(COLOR_HP_BAR_FILL);
        g.fillRoundRect(cx - width / 2, y, filled, height, sx(3), sy(3));
    }

    void drawGoldDrones(Graphics2D g) {
        for (GoldDrone drone : goldDrones) {
            int x = (int) Math.round(renderX(drone.previousX, drone.x));
            int y = (int) Math.round(renderY(drone.previousY, drone.y));
            if (drone.laserTicks > 0) {
                double fade = drone.laserTicks / (double) Math.max(1, logicTicks(12));
                g.setStroke(STROKE_SURGE_AURA);
                g.setColor(withAlpha(drone.color, (int) (120 * fade)));
                g.drawLine((int) Math.round(drone.laserStartX), (int) Math.round(drone.laserStartY),
                        (int) Math.round(drone.laserEndX), (int) Math.round(drone.laserEndY));
                g.setStroke(STROKE_SURGE_CORE);
                g.setColor(withAlpha(Color.WHITE, (int) (210 * fade)));
                g.drawLine((int) Math.round(drone.laserStartX), (int) Math.round(drone.laserStartY),
                        (int) Math.round(drone.laserEndX), (int) Math.round(drone.laserEndY));
            }
            int pulse = sx(4) + (int) (pulse(0.32, drone.waveOffset) * sx(4));
            g.setColor(withAlpha(drone.color, 72));
            fillCenteredOval(g, x, y, sx(26) + pulse, sy(26) + pulse);
            g.setColor(drone.color);
            fillCenteredOval(g, x, y, sx(10), sy(10));
            g.setColor(Color.WHITE);
            fillCenteredOval(g, x + sx(2), y - sy(2), sx(4), sy(4));
        }
    }

    void drawMeleeRam(Graphics2D g) {
        if (meleeRamTicks <= 0) {
            return;
        }
        double x = meleeRamPlayerX();
        double y = meleeRamPlayerY();
        double t = meleeRamProgress();
        int ix = (int) Math.round(x);
        int iy = (int) Math.round(y);
        int tailStart = (int) Math.round(Math.min(PLAYER_X, x) - sx(52));
        int alpha = t < 0.62 ? 130 : Math.max(0, (int) Math.round(130 * (1.0 - smoothstep((t - 0.62) / 0.38))));
        g.setStroke(STROKE_LASER_AURA);
        g.setColor(withAlpha(COLOR_MELEE, alpha));
        g.drawLine(tailStart, iy, ix, iy);
        g.setStroke(STROKE_LASER_CORE);
        g.setColor(withAlpha(Color.WHITE, Math.min(210, alpha + 70)));
        g.drawLine(Math.max(LANE_LEFT_X, tailStart + sx(22)), iy, ix, iy);
    }

    void drawBulletTrailParticles(Graphics2D g) {
        for (BulletTrailParticle particle : bulletTrailParticles) {
            double age = 1.0 - particle.life / (double) particle.maxLife;
            int alpha = Math.max(0, 205 - (int) (age * 205));
            int x = (int) Math.round(renderX(particle.previousX, particle.x));
            int y = (int) Math.round(renderY(particle.previousY, particle.y));
            int size = Math.max(1, (int) Math.round(particle.size * (1.0 - age * 0.58)));
            if (particle.sides >= 3) {
                g.setColor(withAlpha(particle.color, alpha / 4));
                fillParticlePolygon(g, x, y, size, particle.sides, particle.rotation);
                g.setColor(withAlpha(particle.color, alpha));
                fillParticlePolygon(g, x, y, Math.max(1, size / 2), particle.sides, -particle.rotation * 0.7);
            } else {
                g.setColor(withAlpha(particle.color, alpha / 3));
                g.fillOval(x - size, y - size, size * 2, size * 2);
                g.setColor(withAlpha(particle.color, alpha));
                g.fillOval(x - size / 2, y - size / 2, size, size);
            }
        }
    }

    void fillParticlePolygon(Graphics2D g, int x, int y, int radius, int sides, double rotation) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < sides; i++) {
            double angle = rotation + i * Math.PI * 2.0 / sides;
            double pointRadius = radius * (0.78 + 0.18 * ((i % 2 == 0) ? 1.0 : 0.35));
            polygon.addPoint(x + (int) Math.round(Math.cos(angle) * pointRadius),
                    y + (int) Math.round(Math.sin(angle) * pointRadius));
        }
        g.fillPolygon(polygon);
    }

    void drawIcePulses(Graphics2D g) {
        g.setStroke(STROKE_SURGE_CORE);
        for (IcePulse pulse : icePulses) {
            int age = pulse.maxTicks - pulse.ticks;
            double eased = easeOutCubic(age / (double) pulse.maxTicks);
            int radius = 18 + (int) (pulse.radius * eased);
            int alpha = Math.max(0, 170 - (int) (eased * 150));
            g.setColor(withAlpha(COLOR_DRY_ICE_PULSE, alpha));
            g.drawOval((int) pulse.x - radius, (int) pulse.y - radius, radius * 2, radius * 2);
            g.setColor(withAlpha(COLOR_DRY_ICE_PULSE, alpha / 3));
            g.fillOval((int) pulse.x - radius / 2, (int) pulse.y - radius / 2, radius, radius);
        }
    }

    void applyDryIceHit(Bullet bullet, Target center, int dealt, int baseDealt) {
        double radius = DRY_ICE_SPLASH_RADIUS;
        int level = Math.max(1, weaponLevel(UpgradeEffect.DRY_ICE_BULLET));
        int splashDamage = Math.max(1, baseDealt / 2);
        center.dryIceHitStreak++;
        center.slowMultiplier = Math.min(center.slowMultiplier, DRY_ICE_SLOW_MULTIPLIER);
        center.slowTicks = Math.max(center.slowTicks, logicTicks(level >= 2 ? 104 : 70));
        if (center.freezeTicks > 0) {
            center.dryIceThawPulseDamage = Math.max(center.dryIceThawPulseDamage, baseDealt);
        }
        if (center.dryIceHitStreak >= 3) {
            center.freezeTicks = Math.max(center.freezeTicks, logicTicks(96));
            center.dryIceThawPulseDamage = Math.max(center.dryIceThawPulseDamage, baseDealt);
            center.dryIceHitStreak = 0;
        }
        icePulses.add(new IcePulse(center.x, LANE_Y[center.lane], radius));
        if (level >= 2) {
            spawnDryIceMist(center.x, LANE_Y[center.lane], 10);
        }
        for (Target target : targets) {
            if (target == center || target.dead || target.hp <= 0 || target.lane != center.lane) {
                continue;
            }
            if (Math.abs(target.x - center.x) > radius) {
                continue;
            }
            target.hp -= splashDamage;
            target.slowMultiplier = Math.min(target.slowMultiplier, DRY_ICE_SLOW_MULTIPLIER);
            target.slowTicks = Math.max(target.slowTicks, logicTicks(level >= 2 ? 88 : 58));
            target.hitFlash = Math.max(target.hitFlash, logicTicks(8));
            impacts.add(new Impact(target.x, LANE_Y[target.lane], splashDamage, damageTextColorFor(bullet.kind)));
        }
    }

    void applyDryIceThawPulse(Target center) {
        int level = Math.max(1, weaponLevel(UpgradeEffect.DRY_ICE_BULLET));
        if (level < 2 || center.dryIceThawPulseDamage <= 0) {
            center.dryIceThawPulseDamage = 0;
            return;
        }
        double radius = DRY_ICE_SHATTER_RADIUS;
        int pulseDamage = Math.max(1, center.dryIceThawPulseDamage);
        center.dryIceThawPulseDamage = 0;
        icePulses.add(new IcePulse(center.x, LANE_Y[center.lane], radius));
        spawnDryIceMist(center.x, LANE_Y[center.lane], level >= MAX_WEAPON_LEVEL ? 30 : 22);
        for (Target target : targets) {
            if (target == center || target.dead || target.hp <= 0 || target.lane != center.lane) {
                continue;
            }
            if (Math.abs(target.x - center.x) > radius) {
                continue;
            }
            target.hp -= pulseDamage;
            target.slowMultiplier = Math.min(target.slowMultiplier, DRY_ICE_SLOW_MULTIPLIER);
            target.slowTicks = Math.max(target.slowTicks, logicTicks(96));
            if (level >= MAX_WEAPON_LEVEL) {
                target.dryIceHitStreak = Math.min(2, target.dryIceHitStreak + 1);
            }
            target.hitFlash = Math.max(target.hitFlash, logicTicks(10));
            impacts.add(new Impact(target.x, LANE_Y[target.lane], pulseDamage, COLOR_DRY_ICE_PULSE));
        }
    }

    void spawnDryIceMist(double x, double y, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double speed = 0.35 + random.nextDouble() * 1.45;
            double size = 4.0 + random.nextDouble() * 8.0;
            int life = 13 + random.nextInt(16);
            bulletTrailParticles.add(new BulletTrailParticle(x, y, Math.cos(angle) * speed,
                    Math.sin(angle) * speed * 0.7, size, life, Color.WHITE));
        }
    }

    void drawBullets(Graphics2D g) {
        for (Bullet bullet : bullets) {
            int y = (int) Math.round(renderY(bullet.previousY, bullet.y));
            int x = (int) Math.round(renderX(bullet.previousX, bullet.x));
            int previousX = (int) Math.round(bullet.previousX);
            if (bullet.kind == BulletKind.PIERCE) {
                drawLaserBeam(g, bullet);
                continue;
            } else if (bullet.kind == BulletKind.HOMING_SHOT) {
                drawHomingPellet(g, bullet, x, y);
                continue;
            } else if (bullet.kind == BulletKind.BUBBLE) {
                drawBubbleBullet(g, bullet, x, y);
                continue;
            } else if (bullet.kind == BulletKind.OVERFLOW) {
                int tail = sx(96);
                g.setStroke(STROKE_SURGE_AURA);
                g.setColor(withAlpha(COLOR_OVERFLOW, 120));
                g.drawLine(Math.max(PLAYER_X, previousX - tail), y, x, y);
                g.setStroke(STROKE_SURGE_CORE);
                g.setColor(withAlpha(Color.WHITE, 205));
                g.drawLine(Math.max(PLAYER_X, previousX - tail / 2), y, x, y);
                int pulse = sx(4) + (int) (pulse(0.5, bullet.damage) * sx(6));
                g.setColor(withAlpha(COLOR_OVERFLOW, 96));
                fillCenteredOval(g, x, y, sx(34) + pulse, sy(34) + pulse);
                g.setColor(COLOR_OVERFLOW);
                fillCenteredOval(g, x, y, sx(18), sy(18));
            } else if (bullet.kind == BulletKind.CONTINUOUS_SURGE) {
                if (!bullet.particleTrail) {
                    g.setStroke(STROKE_SURGE_AURA);
                    g.setColor(COLOR_SURGE_AURA);
                    g.drawLine(Math.max(PLAYER_X, previousX - sx(52)), y, x, y);
                    g.setStroke(STROKE_SURGE_CORE);
                    g.setColor(COLOR_SURGE_CORE);
                    g.drawLine(Math.max(PLAYER_X, previousX - sx(34)), y, x, y);
                }
                g.setColor(COLOR_SURGE_GLOW);
                fillCenteredOval(g, x, y, sx(28), sy(28));
                g.setColor(COLOR_SURGE_HEAD);
                fillCenteredOval(g, x, y, sx(16), sy(16));
            } else if (bullet.kind == BulletKind.BURST) {
                if (!bullet.particleTrail) {
                    g.setStroke(STROKE_SURGE_AURA);
                    g.setColor(COLOR_BURST_AURA);
                    g.drawLine(Math.max(PLAYER_X, previousX - sx(44)), y, x, y);
                }
                g.setColor(COLOR_BURST_HEAD);
                fillCenteredOval(g, x, y, sx(20), sy(20));
            } else if (bullet.kind == BulletKind.CONTINUOUS) {
                if (!bullet.particleTrail) {
                    g.setStroke(STROKE_CONTINUOUS);
                    g.setColor(COLOR_CONTINUOUS_TRAIL);
                    g.drawLine(Math.max(PLAYER_X, previousX - sx(14)), y, x, y);
                }
                g.setColor(COLOR_CONTINUOUS_HEAD);
                fillCenteredOval(g, x, y, sx(6), sy(6));
            } else if (bullet.kind == BulletKind.DRY_ICE) {
                if (!bullet.particleTrail) {
                    g.setStroke(STROKE_BASIC);
                    g.setColor(COLOR_BASIC_TRAIL);
                    g.drawLine(Math.max(PLAYER_X, previousX - sx(34)), y, x, y);
                }
                g.setColor(COLOR_DRY_ICE_HEAD);
                drawDryIcePentagon(g, bullet, x, y);
            } else {
                g.setStroke(STROKE_BASIC);
                g.setColor(COLOR_BASIC_TRAIL);
                g.drawLine(Math.max(PLAYER_X, previousX - sx(34)), y, x, y);
                g.setColor(COLOR_BASIC_HEAD);
                fillCenteredOval(g, x, y, sx(18), sy(18));
            }
            int coreRadius = bullet.kind == BulletKind.CONTINUOUS ? sx(2) : bullet.kind == BulletKind.DRY_ICE ? sx(5) : sx(4);
            g.setColor(Color.WHITE);
            g.fillOval(x - coreRadius, y - coreRadius, coreRadius * 2, coreRadius * 2);
        }
    }

    void drawLaserBeam(Graphics2D g, Bullet bullet) {
        double remaining = bullet.maxLifeTicks <= 0 ? 0.0 : bullet.lifeTicks / (double) bullet.maxLifeTicks;
        double fade = smoothstep(remaining);
        int y = (int) Math.round(bullet.y);
        int startX = PLAYER_X + sx(28);
        int endX = LANE_RIGHT_X;
        int auraAlpha = (int) (105 * fade);
        int coreAlpha = (int) (215 * fade);
        int hotAlpha = (int) (245 * fade);
        Color auraColor = laserAuraColor(bullet);
        Color coreColor = laserCoreColor(bullet);
        Color hotColor = laserHotColor(bullet);

        g.setStroke(STROKE_LASER_AURA);
        g.setColor(withAlpha(auraColor, auraAlpha));
        g.drawLine(startX, y, endX, y);
        g.setStroke(STROKE_LASER_CORE);
        g.setColor(withAlpha(coreColor, coreAlpha));
        g.drawLine(startX, y, endX, y);
        g.setStroke(STROKE_LASER_HOT);
        g.setColor(withAlpha(hotColor, hotAlpha));
        g.drawLine(startX, y, endX, y);

        int flare = sx(18) + (int) Math.round(sx(12) * fade);
        g.setColor(withAlpha(auraColor, auraAlpha));
        g.fillOval(startX - flare, y - flare, flare * 2, flare * 2);
        g.setColor(withAlpha(hotColor, hotAlpha));
        fillCenteredOval(g, startX, y, sx(16), sy(16));

        int length = Math.max(1, endX - startX);
        int glintCount = 7;
        g.setStroke(STROKE_LASER_HOT);
        for (int i = 0; i < glintCount; i++) {
            int offset = (int) ((i * 337L + bullet.lifeTicks * 17L) % length);
            int glintX = startX + offset;
            int glintLength = sx(26 + (i % 3) * 10);
            int glintY = y + sy((i % 2 == 0 ? -1 : 1) * (2 + i % 3));
            g.setColor(withAlpha(hotColor, (int) (90 * fade)));
            g.drawLine(glintX, glintY, Math.min(endX, glintX + glintLength), glintY);
        }
    }

    void drawHomingPellet(Graphics2D g, Bullet bullet, int x, int y) {
        double angle = Math.atan2(bullet.vy, bullet.vx);
        Color pelletColor = bullet.accentColor == null ? COLOR_HOMING_HEAD : bullet.accentColor;
        int glow = sx(18);
        g.setColor(withAlpha(pelletColor, 86));
        g.fillOval(x - glow, y - glow, glow * 2, glow * 2);

        int tip = sx(13);
        int side = sx(7);
        int back = sx(9);
        double dirX = Math.cos(angle);
        double dirY = Math.sin(angle);
        double perpX = -dirY;
        double perpY = dirX;
        Polygon dart = new Polygon();
        dart.addPoint(x + (int) Math.round(dirX * tip), y + (int) Math.round(dirY * tip));
        dart.addPoint(x + (int) Math.round(-dirX * back + perpX * side),
                y + (int) Math.round(-dirY * back + perpY * side));
        dart.addPoint(x + (int) Math.round(-dirX * (back + sx(3))),
                y + (int) Math.round(-dirY * (back + sx(3))));
        dart.addPoint(x + (int) Math.round(-dirX * back - perpX * side),
                y + (int) Math.round(-dirY * back - perpY * side));
        g.setColor(pelletColor);
        g.fillPolygon(dart);
        g.setColor(blendColor(pelletColor, Color.WHITE, 0.72));
        fillCenteredOval(g, x, y, sx(6), sy(6));
    }

    void drawBubbleBullet(Graphics2D g, Bullet bullet, int x, int y) {
        int radius = (int) Math.round(bullet.radius);
        int pulse = (int) Math.round(sx(5) * pulse(0.18, bullet.damage));
        g.setColor(COLOR_BUBBLE_AURA);
        g.fillOval(x - radius - pulse, y - radius - pulse, (radius + pulse) * 2, (radius + pulse) * 2);
        g.setStroke(STROKE_SURGE_CORE);
        g.setColor(COLOR_BUBBLE_CORE);
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        g.setColor(withAlpha(COLOR_BUBBLE_CORE, 72));
        fillCenteredOval(g, x, y, radius, radius);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(24f));
        g.setColor(COLOR_BUBBLE_TEXT);
        drawCentered(g, String.valueOf(Math.max(0, bullet.remainingDamage)), x, y + sy(8));
    }

    void fillCenteredOval(Graphics2D g, int x, int y, int width, int height) {
        g.fillOval(x - width / 2, y - height / 2, width, height);
    }

    void drawDryIcePentagon(Graphics2D g, Bullet bullet, int x, int y) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < bullet.shapeAngles.length; i++) {
            double angle = bullet.shapeAngles[i] + bullet.shapeRotation;
            polygon.addPoint(x + (int) Math.round(Math.cos(angle) * bullet.shapeRadii[i]),
                    y + (int) Math.round(Math.sin(angle) * bullet.shapeRadii[i]));
        }
        g.fillPolygon(polygon);
        g.setColor(COLOR_DRY_ICE_CORE);
        g.drawPolygon(polygon);
    }

    void drawXpOrbs(Graphics2D g) {
        for (XpOrb orb : xpOrbs) {
            int x = (int) Math.round(renderX(orb.previousX, orb.x));
            int y = (int) Math.round(renderY(orb.previousY, orb.y));
            int pulse = (int) (smoothstep(0.5 + 0.5 * Math.sin(gameplayStep(orb.pulse) * 0.22)) * sx(5));
            int radius = sx(11) + pulse;
            g.setColor(COLOR_XP_GLOW);
            g.fillOval(x - radius - sx(8), y - radius - sy(8), (radius + sx(8)) * 2, (radius + sy(8)) * 2);
            g.setColor(COLOR_XP);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            g.setColor(COLOR_INK_DARK);
            g.setFont(FONT_ORB);
            drawCentered(g, "XP" + orb.value, x, y + sy(4));
        }
    }


    void drawImpacts(Graphics2D g) {
        g.setStroke(STROKE_IMPACT);
        for (Impact impact : impacts) {
            int age = impact.maxTicks - impact.ticks;
            double progress = age / (double) impact.maxTicks;
            double eased = easeOutCubic(Math.min(1.0, progress * 1.55));
            double movement = easeOutCubic(progress);
            double visualX = impact.x + impact.startOffsetX + impact.travelX * movement;
            double visualY = impact.y + impact.startOffsetY + impact.travelY * movement
                    - impact.popHeight * Math.sin(Math.min(1.0, progress) * Math.PI);
            int radius = sx(10) + (int) (eased * sx(42));
            int alpha = Math.max(20, 210 - (int) (eased * 190));
            Color ringColor = blendColor(impact.damageColor, COLOR_IMPACT_RING, 0.18);
            g.setColor(withAlpha(ringColor, alpha));
            g.drawOval((int) Math.round(visualX) - radius, (int) Math.round(visualY) - radius,
                    radius * 2, radius * 2);
            g.setColor(withAlpha(impact.damageColor, Math.max(25, alpha - 60)));
            fillCenteredOval(g, (int) Math.round(visualX), (int) Math.round(visualY), sx(10), sy(10));
            if (impact.damage > 0) {
                double pop = Math.sin(Math.min(1.0, progress * 1.35) * Math.PI);
                double fade = 1.0 - smoothstep(Math.max(0.0, (progress - 0.34) / 0.66));
                int textAlpha = Math.max(0, Math.min(255, (int) (255 * fade)));
                double scale = 1.02 + pop * 1.05 + (1.0 - progress) * 0.16;
                float fontSize = (float) (sx(42) * scale);
                Font popFont = FONT_DAMAGE_POP.deriveFont(fontSize);
                g.setFont(popFont);
                FontMetrics metrics = g.getFontMetrics();
                String text = "-" + impact.damage;
                int textX = (int) Math.round(visualX - metrics.stringWidth(text) / 2.0);
                int textY = (int) Math.round(visualY - sy(18) - pop * sy(22));
                g.setColor(withAlpha(COLOR_INK_DARK, Math.min(220, textAlpha)));
                g.drawString(text, textX - sx(3), textY + sy(3));
                g.drawString(text, textX + sx(3), textY + sy(3));
                g.drawString(text, textX - sx(2), textY - sy(2));
                g.drawString(text, textX + sx(2), textY - sy(2));
                g.setColor(impactColorFor(impact, textAlpha));
                g.drawString(text, textX, textY);
                g.setColor(withAlpha(Color.WHITE, textAlpha / 3));
                g.drawString(text, textX - 1, textY - Math.max(2, (int) (fontSize * 0.08)));
            }
        }
    }

    Color colorFor(Target target) {
        if (target.freezeTicks > 0) {
            return frozenTargetColor(target);
        }
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

    Color frozenTargetColor(Target target) {
        double flash = 0.5 + 0.5 * Math.sin(tick * GAMEPLAY_STEP_SCALE * 0.9 + target.x * 0.01);
        float saturation = (float) (0.06 + flash * 0.14);
        float brightness = (float) (0.86 + flash * 0.14);
        return new Color(Color.HSBtoRGB(0.535f, saturation, brightness));
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
        drawTopHud(g);
        drawDynamicHud(g);
    }

    void drawTopHud(Graphics2D g) {
        int panelPulse = 3 + (int) (pulse(0.11, 19) * 3);
        g.setColor(new Color(7, 11, 18, 170));
        g.fillRoundRect(14, 12, WIDTH - 28, 108, 8, 8);
        g.setColor(new Color(82, 126, 164, 95));
        g.setStroke(STROKE_CARD_IDLE);
        g.drawRoundRect(14, 12, WIDTH - 28, 108, 8, 8);
        if (pendingUpgradeChoices > 0) {
            g.setColor(withAlpha(COLOR_UPGRADE_READY, 22 + panelPulse * 8));
            g.fillRoundRect(14, 12, WIDTH - 28, 108, 8, 8);
        }

        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(22f));
        g.setColor(COLOR_HUD_TEXT);
        g.drawString("HP " + hp + "/" + maxHp, 28, 42);
        g.drawString(ui("SCORE ", "分数 ") + score, 188, 42);
        g.drawString(ui("KILLS ", "击破 ") + kills, 360, 42);
        g.drawString(ui("COMBO ", "连击 ") + combo + "/" + bestCombo, 524, 42);
        g.drawString(ui("BEST ", "最高 ") + highScore, 734, 42);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(20f));
        g.drawString(difficultyLabel(difficulty) + "   " + currentTypingSpeedWpm() + " WPM", 28, 78);
        drawPressureMeter(g, 28, 94);

        int badgeRight = WIDTH - 32;
        int badgeY = 24;
        int badgeHeight = 42;
        int badgeGap = 24;
        int versionWidth = 148;
        int languageWidth = 92;
        int weaponWidth = 418;
        int versionX = badgeRight - versionWidth;
        int languageX = versionX - badgeGap - languageWidth;
        int weaponX = languageX - badgeGap - weaponWidth;
        drawTopHudBadge(g, weaponX, badgeY, weaponWidth, badgeHeight, highTalentHudText(), COLOR_RARITY_RED);
        drawTopHudBadge(g, languageX, badgeY, languageWidth, badgeHeight, languageHudText(), COLOR_XP);
        drawTopHudBadge(g, versionX, badgeY, versionWidth, badgeHeight,
                "v" + TypingLane.VERSION, COLOR_UPGRADE_READY);
    }

    void drawTopHudBadge(Graphics2D g, int x, int y, int width, int height, String value, Color accent) {
        g.setColor(withAlpha(accent, 28));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(new Color(7, 11, 18, 218));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(withAlpha(accent, 165));
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(22f));
        g.setColor(COLOR_HUD_TEXT);
        drawFittedString(g, value, x + 14, y + height / 2 + 8, width - 28);
    }

    String languageHudText() {
        return isChinese() ? ui("Chinese", "中文") : "EN";
    }

    void drawDynamicHud(Graphics2D g) {
        List<HudMetric> metrics = dynamicHudMetrics();
        if (metrics.isEmpty()) {
            return;
        }
        for (HudMetric metric : metrics) {
            setDynamicHudValue(metric.id, metric.value);
        }
        int margin = sx(55);
        int rows = metrics.size() > 6 ? 2 : 1;
        int gapX = rows == 1 ? sx(12) : sx(22);
        int gapY = rows == 1 ? sy(8) : sy(14);
        int rowHeight = rows == 1 ? sy(88) : sy(64);
        int totalHeight = rows * rowHeight + (rows - 1) * gapY;
        int y = HEIGHT - sy(28) - totalHeight;
        int cols = (metrics.size() + rows - 1) / rows;
        int availableWidth = WIDTH - margin * 2;
        int index = 0;
        for (int row = 0; row < rows && index < metrics.size(); row++) {
            int rowCount = Math.min(cols, metrics.size() - index);
            int maxFittingCardWidth = (availableWidth - gapX * (rowCount - 1)) / Math.max(1, rowCount);
            int preferredCardWidth = rows == 1 ? sx(312) : sx(300);
            int compactCardWidth = Math.min(preferredCardWidth, maxFittingCardWidth);
            int cardWidth = rows == 1 || rowCount <= 4 ? compactCardWidth : maxFittingCardWidth;
            int rowWidth = cardWidth * rowCount + gapX * (rowCount - 1);
            int x = margin + Math.max(0, (availableWidth - rowWidth) / 2);
            for (int col = 0; col < rowCount && index < metrics.size(); col++) {
                drawDynamicHudCard(g, metrics.get(index), x, y + row * (rowHeight + gapY), cardWidth, rowHeight,
                        rows == 1);
                x += cardWidth + gapX;
                index++;
            }
        }
    }

    List<HudMetric> dynamicHudMetrics() {
        List<HudMetric> metrics = new ArrayList<HudMetric>();
        metrics.add(new HudMetric("xp", "XP", pendingUpgradeChoices > 0 ? "READY x" + pendingUpgradeChoices
                : "Lv " + upgradeLevel + "  " + xp + "/" + xpToNext,
                pendingUpgradeChoices > 0 ? ui("ready", "可升级") : ui("next upgrade", "下次升级"),
                pendingUpgradeChoices > 0 ? COLOR_UPGRADE_READY : COLOR_XP,
                pendingUpgradeChoices > 0 ? 1.0 : xp / (double) Math.max(1, xpToNext)));

        UpgradeEffect weapon = currentWeaponEffect();
        if (weapon != null) {
            metrics.add(new HudMetric("weapon-level", ui("WEAPON", "武器"),
                    "Lv " + Math.max(1, weaponLevel(weapon)) + "/" + MAX_WEAPON_LEVEL,
                    effectTitle(weapon), hudAccentForEffect(weapon),
                    Math.max(1, weaponLevel(weapon)) / (double) MAX_WEAPON_LEVEL));
            addWeaponHudMetrics(metrics, weapon);
        }
        addBlueHudMetrics(metrics);
        addGoldHudMetrics(metrics);
        return metrics;
    }

    void addWeaponHudMetrics(List<HudMetric> metrics, UpgradeEffect weapon) {
        Color accent = hudAccentForEffect(weapon);
        if (weapon == UpgradeEffect.BASIC_WEAPON) {
            metrics.add(new HudMetric("basic-multiplier", ui("BASIC DAMAGE", "基础枪倍率"),
                    basicWeaponMultiplierText(),
                    "+" + basicWeaponKillDamageBonusPercent + ui(" stacks", " 层"),
                    accent,
                    Math.min(1.0, basicWeaponKillDamageBonusPercent / 100.0)));
        } else if (weapon == UpgradeEffect.RHYTHM_CANNON) {
            String detail = weaponLevel(weapon) >= MAX_WEAPON_LEVEL
                    ? "+" + String.format(java.util.Locale.US, "%.1f", autoCannonStackPercent) + "%"
                    : continuousSurgeTicks > 0 ? secondsText(continuousSurgeTicks) : ui("ready", "就绪");
            double progress = continuousSurgeTicks > 0
                    ? continuousSurgeTicks / (double) Math.max(1, AUTO_CANNON_DECAY_SURGE_TICKS)
                    : Math.min(1.0, autoCannonFireRateMultiplier() / 5.0);
            metrics.add(new HudMetric("auto-rate", ui("AUTO RATE", "自动射速"),
                    autoRateHudText(), detail, accent, progress));
        } else if (weapon == UpgradeEffect.FROST_FIELD) {
            if (weaponLevel(weapon) >= MAX_WEAPON_LEVEL) {
                metrics.add(new HudMetric("laser-damage", ui("LASER DAMAGE", "激光伤害"),
                        laserDamageHudText(), sustainedLaserTicks > 0 ? secondsText(sustainedLaserTicks)
                                : ui("type to charge", "输入充能"),
                        accent,
                        sustainedLaserTicks > 0
                                ? sustainedLaserTicks / (double) Math.max(1, SUSTAINED_LASER_DURATION_TICKS)
                                : sustainedLaserDamagePercent() / 300.0));
            }
        } else if (weapon == UpgradeEffect.DRY_ICE_BULLET) {
            metrics.add(new HudMetric("dry-ice", ui("DRY ICE", "干冰"),
                    DRY_ICE_SLOW_MULTIPLIER == 0.5 ? "50%" : String.valueOf(DRY_ICE_SLOW_MULTIPLIER),
                    ui("slow / freeze x3", "减速 / 三连冻结"), accent,
                    weaponLevel(weapon) / (double) MAX_WEAPON_LEVEL));
        } else if (weapon == UpgradeEffect.HOMING_SHOTGUN) {
            int pellets = homingShotgunPelletCount();
            metrics.add(new HudMetric("shotgun-pellets", ui("SHOTGUN", "追踪散弹"),
                    shotgunPelletHudText(), pellets + ui(" pellets", " 发"),
                    accent,
                    Math.min(1.0, pellets / (double) (HOMING_SHOTGUN_UPGRADED_PELLETS
                            + triggerTuningLevel() * 2))));
        }
    }

    void addBlueHudMetrics(List<HudMetric> metrics) {
        if (damageBonusPercent > 0) {
            metrics.add(new HudMetric("damage-bonus", ui("DAMAGE", "伤害加成"),
                    "+" + damageBonusPercent + "%", ui("global", "全局"),
                    hudAccentForEffect(UpgradeEffect.CALIBRATED_DAMAGE),
                    Math.min(1.0, damageBonusPercent / (double) MAX_DAMAGE_BONUS_PERCENT)));
        }
        if (fireRateBonusPercent > 0) {
            metrics.add(new HudMetric("trigger-tuning", ui("TRIGGER", "扳机调教"),
                    "+" + fireRateBonusPercent + "%", "+" + triggerTuningLevel() + ui(" shot", " 弹"),
                    hudAccentForEffect(UpgradeEffect.TRIGGER_TUNING),
                    Math.min(1.0, fireRateBonusPercent / (double) Math.max(1, MAX_TRIGGER_TUNING_BONUS_PERCENT))));
        }
        if (perfectBonus > 0) {
            metrics.add(new HudMetric("combo-tuning", ui("PERFECT", "精准调校"),
                    "+" + perfectBonus, ui("base damage", "基础伤害"),
                    hudAccentForEffect(UpgradeEffect.COMBO_TUNING)));
        }
        if (maxHpUpgradeBonus > 0) {
            metrics.add(new HudMetric("core-hp", ui("CORE", "核心强化"),
                    "+" + maxHpUpgradeBonus, "HP MAX",
                    hudAccentForEffect(UpgradeEffect.REINFORCED_CORE),
                    Math.min(1.0, maxHpUpgradeBonus / (double) MAX_HP_UPGRADE_BONUS)));
        }
        if (phaseSwitchLevel > 0) {
            String value = phaseSwitchTicks > 0 ? secondsText(phaseSwitchTicks)
                    : phaseSwitchCooldownTicks > 0 ? secondsText(phaseSwitchCooldownTicks) : ui("READY", "就绪");
            String detail = phaseSwitchTicks > 0 ? ui("active", "生效中")
                    : phaseSwitchCooldownTicks > 0 ? ui("cooldown", "冷却") : ui("guarded swap", "换路护盾");
            double progress = phaseSwitchTicks > 0
                    ? phaseSwitchTicks / (double) Math.max(1, PHASE_SWITCH_TICKS)
                    : phaseSwitchCooldownTicks > 0
                            ? 1.0 - phaseSwitchCooldownTicks / (double) Math.max(1, PHASE_SWITCH_BASE_COOLDOWN)
                            : 1.0;
            metrics.add(new HudMetric("phase-switch", ui("PHASE", "相位"),
                    value, detail, hudAccentForEffect(UpgradeEffect.PHASE_SWITCH), progress));
        }
        if (bossBreakerLevel > 0) {
            metrics.add(new HudMetric("boss-breaker", ui("BOSS BREAK", "Boss 破甲"),
                    "+" + bossBreakerLevel * 15 + "%", ui("vs boss", "对 Boss"),
                    hudAccentForEffect(UpgradeEffect.BOSS_BREAKER),
                    Math.min(1.0, bossBreakerLevel / 3.0)));
        }
        if (crossfeedLevel > 0 && (crossfeedBonusTicks > 0 || crossfeedCooldownTicks > 0)) {
            String value = crossfeedBonusTicks > 0 ? secondsText(crossfeedBonusTicks)
                    : crossfeedCooldownTicks > 0 ? secondsText(crossfeedCooldownTicks) : ui("READY", "就绪");
            String detail = crossfeedBonusTicks > 0 ? ui("XP primed", "经验触发")
                    : crossfeedCooldownTicks > 0 ? ui("cooldown", "冷却") : "f/t/k";
            int total = logicTicks(150);
            double progress = crossfeedBonusTicks > 0 ? crossfeedBonusTicks / (double) total
                    : crossfeedCooldownTicks > 0 ? 1.0 - crossfeedCooldownTicks / (double) total : 1.0;
            metrics.add(new HudMetric("crossfeed", ui("CROSSFEED", "交叉供能"),
                    value, detail, hudAccentForEffect(UpgradeEffect.CROSSFEED), progress));
        }
        if (longFocusTicks > 0) {
            int level = effectLevel(UpgradeEffect.LONG_WORD_FOCUS);
            metrics.add(new HudMetric("long-focus", ui("FOCUS", "长词专注"),
                    secondsText(longFocusTicks),
                    "+" + level * 8 + "%", hudAccentForEffect(UpgradeEffect.LONG_WORD_FOCUS),
                    longFocusTicks / (double) logicTicks(130 + level * 28)));
        }
        addGroupLevelMetrics(metrics);
    }

    void addGroupLevelMetrics(List<HudMetric> metrics) {
        for (UpgradeEffect effect : UpgradeEffect.values()) {
            int level = effectLevel(effect);
            if (level <= 0 || !isGroupTwoToFiveEffect(effect) || effect == UpgradeEffect.LONG_WORD_FOCUS) {
                continue;
            }
            if (effect == UpgradeEffect.SINGLE_LANE_BASTION || effect == UpgradeEffect.ALTERNATING_GUARD) {
                int charges = laneBarrierCharges[0] + laneBarrierCharges[1];
                if (charges > 0) {
                    metrics.add(new HudMetric("level-" + effect.name(), effectTitle(effect),
                            String.valueOf(charges), ui("barrier", "屏障"),
                            hudAccentForEffect(effect), Math.min(1.0, charges / 6.0)));
                }
            } else if (effect == UpgradeEffect.CALM_AFTER_ERROR) {
                if (calmGuardCharges > 0) {
                    metrics.add(new HudMetric("level-" + effect.name(), effectTitle(effect),
                            String.valueOf(calmGuardCharges), ui("guard", "守护"),
                            hudAccentForEffect(effect), Math.min(1.0, calmGuardCharges / 3.0)));
                }
            } else if (effect == UpgradeEffect.PRESSURE_VALVE) {
                int slowTicks = Math.max(laneSlowTicks[0], laneSlowTicks[1]);
                if (slowTicks > 0) {
                    metrics.add(new HudMetric("level-" + effect.name(), effectTitle(effect),
                            secondsText(slowTicks), ui("lane slow", "区域减速"),
                            hudAccentForEffect(effect),
                            slowTicks / (double) logicTicks(78 + level * 18)));
                }
            }
        }
    }

    Color hudAccentForEffect(UpgradeEffect effect) {
        return isWeaponEffect(effect) ? COLOR_RARITY_RED : colorFor(rarityForEffect(effect));
    }

    void addGoldHudMetrics(List<HudMetric> metrics) {
        if (hasGoldTalent(UpgradeEffect.DRONE_SWARM)) {
            metrics.add(new HudMetric("gold-drones", ui("DRONES", "无人机"),
                    goldDrones.size() + "/" + MAX_GOLD_DRONES,
                    ui("active swarm", "当前数量"), hudAccentForEffect(UpgradeEffect.DRONE_SWARM),
                    goldDrones.size() / (double) MAX_GOLD_DRONES));
        }
        if (hasGoldTalent(UpgradeEffect.OVERFLOW_ROUND)) {
            metrics.add(new HudMetric("overflow", ui("OVERFLOW", "溢流"),
                    overflowDamageBank + "/" + OVERFLOW_MAX_DAMAGE,
                    overflowDamageBank >= OVERFLOW_MAX_DAMAGE ? ui("Hold Shift", "按住 Shift") : ui("bank", "蓄能"),
                    hudAccentForEffect(UpgradeEffect.OVERFLOW_ROUND),
                    overflowDamageBank / (double) Math.max(1, OVERFLOW_MAX_DAMAGE)));
        }
        if (hasGoldTalent(UpgradeEffect.UNDYING_TOTEM)) {
            metrics.add(new HudMetric("totem", ui("TOTEM", "图腾"),
                    totemReviveAvailable ? ui("READY", "就绪") : ui("BROKEN", "已碎"),
                    ui("revive once", "一次复活"), hudAccentForEffect(UpgradeEffect.UNDYING_TOTEM),
                    totemReviveAvailable ? 1.0 : 0.0));
        }
        if (hasGoldTalent(UpgradeEffect.ADRENALINE)) {
            metrics.add(new HudMetric("adrenaline", ui("ADRENALINE", "肾上腺素"),
                    "+50%", ui("damage / speed", "伤害 / 速度"), hudAccentForEffect(UpgradeEffect.ADRENALINE),
                    1.0));
        }
        if (hasGoldTalent(UpgradeEffect.MELEE)) {
            metrics.add(new HudMetric("melee", ui("MELEE", "肉搏"),
                    "3x", ui("regen / ram", "回血 / 冲撞"), hudAccentForEffect(UpgradeEffect.MELEE), 1.0));
        }
        if (hasGoldTalent(UpgradeEffect.RED_EYE)) {
            int percent = maxHp <= 0 ? 0 : (int) Math.round(100.0
                    * (1.0 - Math.max(0.0, Math.min(1.0, hp / (double) maxHp))));
            metrics.add(new HudMetric("red-eye", ui("RED EYE", "红眼"),
                    "+" + percent + "%", ui("missing HP", "已损生命"),
                    hudAccentForEffect(UpgradeEffect.RED_EYE), percent / 100.0));
        }
    }

    String secondsText(int ticks) {
        return String.format(java.util.Locale.US, "%.1fs", Math.max(0.0, ticks * TICK_MS / 1000.0));
    }

    void drawDynamicHudCard(Graphics2D g, HudMetric metric, int x, int y, int width, int height,
            boolean large) {
        int glow = large ? sx(5) + (int) (pulse(0.18, x + y) * sx(5)) : sx(3);
        int radiusX = large ? sx(20) : sx(16);
        int radiusY = large ? sy(20) : sy(16);
        g.setColor(withAlpha(metric.accent, 28));
        g.fillRoundRect(x - glow, y - glow, width + glow * 2, height + glow * 2,
                radiusX + glow, radiusY + glow);
        g.setColor(new Color(7, 11, 18, 224));
        g.fillRoundRect(x, y, width, height, radiusX, radiusY);
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(withAlpha(metric.accent, 176));
        g.drawRoundRect(x, y, width, height, radiusX, radiusY);

        int pad = large ? sx(18) : sx(16);

        String text = hudMetricMainText(metric, dynamicHudValue(metric.id));
        String previous = hudMetricMainText(metric, dynamicHudPreviousValue(metric.id));
        int transitionTicks = dynamicHudTransitionTicksFor(metric.id);
        int textAlpha = 255;
        int textYOffset = 0;
        if (transitionTicks > 0 && previous.length() > 0) {
            double transition = 1.0 - transitionTicks / (double) STATUS_HUD_TRANSITION_TICKS;
            if (transition < 0.5) {
                text = previous;
                textAlpha = (int) Math.round(255 * (1.0 - transition * 2.0));
                textYOffset = -(int) Math.round(sy(7) * transition);
            } else {
                textAlpha = (int) Math.round(255 * ((transition - 0.5) * 2.0));
                textYOffset = (int) Math.round(sy(7) * (1.0 - transition));
            }
        }

        g.setFont(uiFont(FONT_STATUS_VALUE, FONT_STATUS_VALUE_ZH).deriveFont(large ? 32f : 24f));
        FontMetrics valueMetrics = g.getFontMetrics();
        int valueY = y + (height - valueMetrics.getHeight()) / 2 + valueMetrics.getAscent()
                + textYOffset - (metric.progress >= 0.0 ? (large ? sy(5) : sy(4)) : 0);
        g.setColor(withAlpha(Color.BLACK, Math.min(180, textAlpha)));
        drawFittedString(g, text, x + pad + sx(2), valueY + sy(2), width - pad * 2);
        g.setColor(withAlpha(Color.WHITE, textAlpha));
        drawFittedString(g, text, x + pad, valueY, width - pad * 2);

        if (metric.progress >= 0.0) {
            int barX = x + pad;
            int barY = y + height - (large ? sy(16) : sy(13));
            int barWidth = width - pad * 2;
            int barHeight = Math.max(4, large ? sy(6) : sy(5));
            int filled = Math.max(0, Math.min(barWidth,
                    (int) Math.round(barWidth * Math.max(0.0, Math.min(1.0, metric.progress)))));
            g.setColor(COLOR_PRESSURE_TRACK);
            g.fillRoundRect(barX, barY, barWidth, barHeight, 5, 5);
            g.setColor(metric.accent);
            g.fillRoundRect(barX, barY, filled, barHeight, 5, 5);
        }
    }

    String hudMetricMainText(HudMetric metric, String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        if (metric.label == null || metric.label.length() == 0) {
            return value;
        }
        return metric.label + " " + value;
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
        g.setColor(withAlpha(Color.BLACK, 210));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if (choiceMode == ChoiceMode.SELL_CONFIRM) {
            if (sellConfirmReturnMode == ChoiceMode.UPGRADE) {
                drawUpgradeChoicePage(g);
            } else {
                drawUpgradeOverviewPage(g);
            }
            drawSellConfirmationOverlay(g);
            return;
        }
        if (choiceMode == ChoiceMode.OVERVIEW) {
            drawUpgradeOverviewPage(g);
            return;
        }
        drawUpgradeChoicePage(g);
    }

    void drawSellConfirmationOverlay(Graphics2D g) {
        UpgradeInventoryCard inventoryCard = pendingSellCard;
        if (inventoryCard == null) {
            return;
        }
        UpgradeCard card = createCard(inventoryCard.effect, inventoryCard.rarity);
        Color accent = colorFor(inventoryCard.rarity);
        int panelWidth = 820;
        int panelHeight = 300;
        int x = (WIDTH - panelWidth) / 2;
        int y = (HEIGHT - panelHeight) / 2;
        int glow = 11 + (int) (pulse(0.22, inventoryCard.effect.ordinal()) * 12);

        g.setColor(withAlpha(Color.BLACK, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(withAlpha(accent, 40 + glow * 3));
        g.fillRoundRect(x - glow, y - glow, panelWidth + glow * 2, panelHeight + glow * 2, 18, 18);
        g.setColor(new Color(9, 14, 23, 248));
        g.fillRoundRect(x, y, panelWidth, panelHeight, 16, 16);
        g.setStroke(STROKE_CARD_ACTIVE);
        g.setColor(withAlpha(accent, 235));
        g.drawRoundRect(x, y, panelWidth, panelHeight, 16, 16);

        g.setFont(uiFont(FONT_CHOICE_TITLE, FONT_CHOICE_TITLE_ZH).deriveFont(38f));
        g.setColor(Color.WHITE);
        drawCentered(g, ui("Confirm Sale", "确认出售"), x + panelWidth / 2, y + 66);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(30f));
        g.setColor(accent);
        drawCentered(g, cardTitle(card), x + panelWidth / 2, y + 122);
        g.setColor(withAlpha(accent, 160));
        g.fillRoundRect(x + 120, y + 143, panelWidth - 240, 4, 3, 3);

        int xpValue = sellExperienceValue(inventoryCard);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH).deriveFont(24f));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawCentered(g, ui("This cannot be undone during the run.", "本局内出售后不可撤销。"),
                x + panelWidth / 2, y + 186);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(34f));
        g.setColor(COLOR_XP);
        drawCentered(g, "+" + xpValue + " XP", x + panelWidth / 2, y + 230);

        int buttonY = y + panelHeight - 58;
        int buttonWidth = 260;
        int gap = 28;
        drawCommandPill(g, x + panelWidth / 2 - buttonWidth - gap / 2, buttonY,
                buttonWidth, COLOR_WORD_COMPLETE, ui("Enter / Y Confirm", "Enter / Y 确认"));
        drawCommandPill(g, x + panelWidth / 2 + gap / 2, buttonY,
                buttonWidth, COLOR_TARGET_SWITCHER, ui("Esc / N Cancel", "Esc / N 取消"));
    }

    void drawUpgradeChoicePage(Graphics2D g) {
        List<UpgradeInventoryCard> cards = currentUpgradeCards();
        clampOverviewSelection(cards);
        String title = bossRewardChoice ? ui("Boss Reward", "Boss 奖励") : ui("Choose Upgrade", "选择升级");
        int pageX = 110;
        int pageY = 118;
        int pageWidth = WIDTH - pageX * 2;
        int pageHeight = 1180;
        drawUpgradeShell(g, pageX, pageY, pageWidth, pageHeight, title);

        int railX = pageX + 38;
        int railY = pageY + 98;
        int railWidth = 430;
        int railHeight = pageHeight - 136;
        drawUpgradeChoiceRail(g, railX, railY, railWidth, railHeight);

        int contentX = railX + railWidth + 34;
        int contentY = railY;
        int contentWidth = pageX + pageWidth - 38 - contentX;
        int contentHeight = railHeight;
        int detailWidth = 520;
        int detailGap = 28;
        int inventoryWidth = contentWidth - detailWidth - detailGap;
        drawFullOverviewInventory(g, contentX, contentY, inventoryWidth, contentHeight, cards);
        drawSelectedOverviewDetails(g, contentX + inventoryWidth + detailGap, contentY,
                detailWidth, contentHeight, cards);
    }

    void drawUpgradeOverviewPage(Graphics2D g) {
        List<UpgradeInventoryCard> cards = currentUpgradeCards();
        clampOverviewSelection(cards);
        int pageX = 110;
        int pageY = 118;
        int pageWidth = WIDTH - pageX * 2;
        int pageHeight = 1180;
        drawUpgradeShell(g, pageX, pageY, pageWidth, pageHeight, ui("Upgrade Overview", "升级总览"));

        int railX = pageX + 38;
        int railY = pageY + 98;
        int railWidth = 430;
        int railHeight = pageHeight - 136;
        drawXpProgressRail(g, railX, railY, railWidth, railHeight);

        int contentX = railX + railWidth + 34;
        int contentY = railY;
        int contentWidth = pageX + pageWidth - 38 - contentX;
        int contentHeight = railHeight;
        int detailWidth = 520;
        int detailGap = 28;
        int inventoryWidth = contentWidth - detailWidth - detailGap;
        drawFullOverviewInventory(g, contentX, contentY, inventoryWidth, contentHeight, cards);
        drawSelectedOverviewDetails(g, contentX + inventoryWidth + detailGap, contentY,
                detailWidth, contentHeight, cards);
    }

    void drawUpgradeShell(Graphics2D g, int x, int y, int width, int height, String title) {
        int glow = 12 + (int) (pulse(0.14, 41) * 8);
        g.setColor(withAlpha(Color.WHITE, 28 + glow * 3));
        g.fillRoundRect(x - glow, y - glow, width + glow * 2, height + glow * 2, 14, 14);
        g.setColor(new Color(7, 10, 16, 238));
        g.fillRoundRect(x, y, width, height, 12, 12);
        g.setStroke(STROKE_CARD_ACTIVE);
        g.setColor(withAlpha(Color.WHITE, 190));
        g.drawRoundRect(x, y, width, height, 12, 12);

        g.setFont(uiFont(FONT_CHOICE_TITLE, FONT_CHOICE_TITLE_ZH).deriveFont(38f));
        g.setColor(Color.WHITE);
        g.drawString(title, x + 38, y + 62);
    }

    void drawUpgradeChoiceRail(Graphics2D g, int x, int y, int width, int height) {
        drawNeutralPanel(g, x, y, width, height, false);
        int gap = 18;
        int selectedHeight = 270;
        int collapsedHeight = 112;
        int cardX = x + 22;
        int cardY = y + 24;
        int cardWidth = width - 44;
        for (int i = 0; i < upgradeChoices.length; i++) {
            boolean selected = i == selectedUpgradeIndex && !overviewSelectionActive;
            int cardHeight = selected ? selectedHeight : collapsedHeight;
            drawUpgradeRailCard(g, upgradeChoices[i], i, cardX, cardY, cardWidth, cardHeight, selected);
            cardY += cardHeight + gap;
        }
    }

    void drawUpgradeRailCard(Graphics2D g, UpgradeCard card, int index, int x, int y,
            int width, int height, boolean selected) {
        if (card == null) {
            return;
        }
        Color rarityColor = colorFor(card.rarity);
        if (selected) {
            int glow = 6 + (int) (pulse(0.2, index * 29) * 7);
            g.setColor(withAlpha(Color.WHITE, 34 + glow * 3));
            g.fillRoundRect(x - glow, y - glow, width + glow * 2, height + glow * 2, 12, 12);
            g.setColor(withAlpha(rarityColor, 34 + glow * 2));
            g.fillRoundRect(x - glow / 2, y - glow / 2, width + glow, height + glow, 12, 12);
        }
        g.setColor(new Color(10, 14, 22, selected ? 246 : 226));
        g.fillRoundRect(x, y, width, height, 10, 10);
        g.setStroke(selected ? STROKE_CARD_ACTIVE : STROKE_CARD_IDLE);
        g.setColor(selected ? withAlpha(rarityColor, 245) : withAlpha(Color.WHITE, 122));
        g.drawRoundRect(x, y, width, height, 10, 10);
        if (selected) {
            g.setStroke(STROKE_CARD_IDLE);
            g.setColor(withAlpha(rarityColor, 110));
            g.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 8, 8);
        }
        g.setColor(rarityColor);
        g.fillRoundRect(x + 10, y + 12, 5, height - 24, 4, 4);

        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(15f));
        g.setColor(rarityColor);
        drawFittedString(g, rarityLabel(card.rarity), x + 28, y + 32, width - 96);

        g.setColor(withAlpha(Color.WHITE, selected ? 232 : 165));
        g.setStroke(STROKE_CARD_IDLE);
        g.drawRoundRect(x + width - 58, y + 18, 38, 34, 7, 7);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(20f));
        g.setColor(Color.WHITE);
        drawCentered(g, String.valueOf(index + 1), x + width - 39, y + 42);

        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(selected ? 28f : 24f));
        g.setColor(Color.WHITE);
        drawFittedString(g, cardTitle(card), x + 28, y + (selected ? 86 : 78), width - 56);

        if (!selected) {
            return;
        }
        g.setColor(withAlpha(rarityColor, 170));
        g.fillRoundRect(x + 28, y + 108, width - 56, 4, 3, 3);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH).deriveFont(18f));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawWrappedLimited(g, upgradeChoiceDescription(card), x + 28, y + 148, width - 56, 26, 4);
    }

    void drawXpProgressRail(Graphics2D g, int x, int y, int width, int height) {
        drawNeutralPanel(g, x, y, width, height, false);
        double progress = Math.max(0.0, Math.min(1.0, xp / (double) Math.max(1, xpToNext)));
        int barWidth = 106;
        int barHeight = height - 210;
        int barX = x + (width - barWidth) / 2;
        int barY = y + 96;
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(30f));
        g.setColor(Color.WHITE);
        drawCentered(g, "XP", x + width / 2, y + 54);

        g.setColor(new Color(12, 16, 24, 245));
        g.fillRoundRect(barX, barY, barWidth, barHeight, 18, 18);
        g.setStroke(STROKE_CARD_ACTIVE);
        g.setColor(withAlpha(Color.WHITE, 145));
        g.drawRoundRect(barX, barY, barWidth, barHeight, 18, 18);
        int fillHeight = (int) Math.round((barHeight - 18) * progress);
        int fillY = barY + barHeight - 9 - fillHeight;
        g.setColor(withAlpha(Color.WHITE, 205));
        g.fillRoundRect(barX + 9, fillY, barWidth - 18, fillHeight, 14, 14);
        g.setColor(withAlpha(Color.WHITE, 70));
        g.fillRoundRect(barX + 18, fillY + 8, barWidth - 36, Math.max(0, fillHeight - 16), 10, 10);

        g.setFont(uiFont(FONT_CHOICE_TITLE, FONT_CHOICE_TITLE_ZH).deriveFont(34f));
        g.setColor(Color.WHITE);
        drawCentered(g, xp + "/" + xpToNext, x + width / 2, y + height - 72);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(17f));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawCentered(g, "Lv " + upgradeLevel, x + width / 2, y + height - 36);
    }

    void drawNeutralPanel(Graphics2D g, int x, int y, int width, int height, boolean selected) {
        g.setColor(new Color(9, 13, 21, selected ? 246 : 226));
        g.fillRoundRect(x, y, width, height, 10, 10);
        g.setStroke(selected ? STROKE_CARD_ACTIVE : STROKE_CARD_IDLE);
        g.setColor(withAlpha(Color.WHITE, selected ? 210 : 118));
        g.drawRoundRect(x, y, width, height, 10, 10);
    }

    void drawHighReplacementOverlay(Graphics2D g) {
        g.setColor(COLOR_CHOICE_OVERLAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(uiFont(FONT_CHOICE_TITLE, FONT_CHOICE_TITLE_ZH));
        drawCentered(g, ui("Replace Weapon", "替换红色武器"), WIDTH / 2, 165);
        if (pendingHighTalent != null) {
            drawCompactHighCard(g, pendingHighTalent, ui("NEW", "新"), WIDTH / 2 - 170, 230, 340, 130,
                    colorFor(pendingHighTalent.rarity));
        }
        for (int i = 0; i < highTalents.length; i++) {
            UpgradeEffect effect = highTalents[i];
            UpgradeCard card = effect == null ? null : createCard(effect, rarityForEffect(effect));
            drawCompactHighCard(g, card, String.valueOf(i + 1), WIDTH / 2 - 140, 425, 280, 170,
                    COLOR_RARITY_RED);
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

        int panelX = WIDTH / 2 - 430;
        int panelY = 215;
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

    void drawFullOverviewInventory(Graphics2D g, int x, int y, int width, int height,
            List<UpgradeInventoryCard> cards) {
        drawNeutralPanel(g, x, y, width, height, false);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(26f));
        g.setColor(Color.WHITE);
        g.drawString(ui("Owned Upgrade Cards", "已拥有升级卡"), x + 26, y + 46);

        int gridX = x + 24;
        int gridY = y + 78;
        int gap = 18;
        int columns = 3;
        int cardWidth = (width - 48 - gap * (columns - 1)) / columns;
        int cardHeight = 152;
        int rows = Math.max(1, (height - 116) / (cardHeight + gap));
        int maxVisibleCards = Math.max(columns, rows * columns);
        if (cards.size() == 0) {
            drawEmptyUpgradeInventoryCard(g, gridX, gridY, width - 48, 180);
            return;
        }

        int pageStart = selectedOverviewCardIndex / maxVisibleCards * maxVisibleCards;
        int pageEnd = Math.min(cards.size(), pageStart + maxVisibleCards);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(16f));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawFittedString(g, (pageStart + 1) + "-" + pageEnd + " / " + cards.size(),
                x + width - 135, y + 46, 105);
        for (int i = pageStart; i < pageEnd; i++) {
            int local = i - pageStart;
            int col = local % columns;
            int row = local / columns;
            int cardX = gridX + col * (cardWidth + gap);
            int cardY = gridY + row * (cardHeight + gap);
            drawOwnedUpgradeCard(g, cards.get(i), cardX, cardY, cardWidth, cardHeight,
                    overviewSelectionActive && i == selectedOverviewCardIndex);
        }
    }

    void drawSelectedOverviewDetails(Graphics2D g, int x, int y, int width, int height,
            List<UpgradeInventoryCard> cards) {
        drawNeutralPanel(g, x, y, width, height, false);

        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(26f));
        g.setColor(Color.WHITE);
        g.drawString(ui("Selected Card", "当前选中卡"), x + 24, y + 46);

        if (cards.size() == 0) {
            drawEmptyUpgradeInventoryCard(g, x + 24, y + 78, width - 48, 160);
            int nextY = drawSlotLoadout(g, x + 24, y + 300, width - 48,
                    ui("Gold Slots", "金色槽位"), COLOR_RARITY_HIGH, goldTalents);
            drawSlotLoadout(g, x + 24, nextY + 18, width - 48,
                    ui("Weapon Slots", "武器槽位"), COLOR_RARITY_RED, highTalents);
            return;
        }

        UpgradeInventoryCard inventoryCard = cards.get(selectedOverviewCardIndex);
        UpgradeCard card = createCard(inventoryCard.effect, inventoryCard.rarity);
        Color accent = colorFor(inventoryCard.rarity);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(16f));
        g.setColor(accent);
        drawFittedString(g, rarityLabel(inventoryCard.rarity) + "  " + inventoryStatusText(inventoryCard),
                x + 24, y + 88, width - 48);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(28f));
        g.setColor(Color.WHITE);
        drawFittedString(g, cardTitle(card), x + 24, y + 128, width - 48);
        g.setColor(withAlpha(accent, 150));
        g.fillRoundRect(x + 24, y + 148, width - 48, 4, 3, 3);
        g.setFont(uiFont(FONT_OVERLAY_BODY, FONT_OVERLAY_BODY_ZH).deriveFont(18f));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawWrappedLimited(g, cardDescription(card), x + 24, y + 188, width - 48, 25, 4);

        int sellY = y + 286;
        boolean canSell = !bossRewardChoice && canSellUpgradeCard(inventoryCard);
        g.setColor(withAlpha(Color.WHITE, canSell ? 26 : 14));
        g.fillRoundRect(x + 24, sellY, width - 48, 86, 8, 8);
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(withAlpha(Color.WHITE, canSell ? 150 : 80));
        g.drawRoundRect(x + 24, sellY, width - 48, 86, 8, 8);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(15f));
        g.setColor(COLOR_TYPED_SUFFIX);
        String sellLabel = canSell ? ui("Sell refund", "出售返还")
                : bossRewardChoice ? ui("Reward locked", "奖励锁定") : ui("Locked talent", "锁定天赋");
        g.drawString(sellLabel, x + 44, sellY + 28);
        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(30f));
        g.setColor(Color.WHITE);
        drawFittedString(g, canSell ? "+" + sellExperienceValue(inventoryCard) + " XP" : ui("Cannot sell", "不可出售"),
                x + 44, sellY + 66, width - 88);

        int nextY = drawSlotLoadout(g, x + 24, sellY + 122, width - 48,
                ui("Gold Slots", "金色槽位"), COLOR_RARITY_HIGH, goldTalents);
        drawSlotLoadout(g, x + 24, nextY + 18, width - 48,
                ui("Weapon Slots", "武器槽位"), COLOR_RARITY_RED, highTalents);
    }

    void drawEmptyUpgradeInventoryCard(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(withAlpha(COLOR_TYPED_SUFFIX, 18));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(withAlpha(COLOR_TYPED_SUFFIX, 92));
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH));
        g.setColor(COLOR_TYPED_SUFFIX);
        drawCentered(g, ui("No owned upgrade cards yet.", "还没有已拥有的升级卡片。"), x + width / 2,
                y + height / 2 + 5);
    }

    void drawOwnedUpgradeCard(Graphics2D g, UpgradeInventoryCard inventoryCard,
            int x, int y, int width, int height, boolean selected) {
        Color accent = colorFor(inventoryCard.rarity);
        if (selected) {
            int glow = 5 + (int) (pulse(0.2, inventoryCard.effect.ordinal()) * 7);
            g.setColor(withAlpha(Color.WHITE, 32 + glow * 3));
            g.fillRoundRect(x - glow, y - glow, width + glow * 2, height + glow * 2, 10, 10);
        }
        g.setColor(new Color(11, 15, 24, 238));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setStroke(selected ? STROKE_CARD_ACTIVE : STROKE_CARD_IDLE);
        g.setColor(withAlpha(Color.WHITE, selected ? 210 : 112));
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setColor(withAlpha(Color.WHITE, selected ? 28 : 16));
        g.fillRoundRect(x + 2, y + 2, width - 4, 30, 7, 7);

        UpgradeCard card = createCard(inventoryCard.effect, inventoryCard.rarity);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(14f));
        g.setColor(accent);
        drawFittedString(g, rarityLabel(inventoryCard.rarity), x + 14, y + 22, width - 128);
        g.setColor(Color.WHITE);
        drawFittedString(g, inventoryStatusText(inventoryCard), x + width - 104, y + 22, 90);

        g.setFont(uiFont(FONT_START_BUTTON, FONT_START_BUTTON_ZH).deriveFont(height >= 140 ? 22f : 19f));
        g.setColor(Color.WHITE);
        drawFittedString(g, cardTitle(card), x + 14, y + 60, width - 28);
        if (height >= 140) {
            g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(14f));
            g.setColor(COLOR_TYPED_SUFFIX);
            drawWrappedLimited(g, ownedCardDescription(inventoryCard, card), x + 14, y + 90, width - 28, 20, 2);
        }
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(14f));
        boolean canSell = canSellUpgradeCard(inventoryCard);
        g.setColor(COLOR_TYPED_SUFFIX);
        drawFittedString(g, canSell ? ui("Sell +", "售价 +") + sellExperienceValue(inventoryCard) + " XP"
                        : ui("Locked", "不可出售"),
                x + 14, y + height - 14, width - 28);
    }

    void drawCommandPill(Graphics2D g, int x, int y, int width, Color accent, String text) {
        g.setColor(withAlpha(accent, 35));
        g.fillRoundRect(x, y, width, 44, 8, 8);
        g.setStroke(STROKE_CARD_IDLE);
        g.setColor(withAlpha(accent, 180));
        g.drawRoundRect(x, y, width, 44, 8, 8);
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(15f));
        g.setColor(Color.WHITE);
        drawFittedString(g, text, x + 14, y + 28, width - 28);
    }

    int drawSlotLoadout(Graphics2D g, int x, int y, int width, String title, Color accent, UpgradeEffect[] effects) {
        g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(16f));
        g.setColor(accent);
        g.drawString(title, x, y + 18);
        int rowY = y + 34;
        for (int i = 0; i < effects.length; i++) {
            g.setColor(withAlpha(accent, effects[i] == null ? 18 : 34));
            g.fillRoundRect(x, rowY, width, 34, 7, 7);
            g.setStroke(STROKE_CARD_IDLE);
            g.setColor(withAlpha(accent, effects[i] == null ? 90 : 160));
            g.drawRoundRect(x, rowY, width, 34, 7, 7);
            g.setFont(uiFont(FONT_PRESSURE, FONT_PRESSURE_ZH).deriveFont(14f));
            g.setColor(Color.WHITE);
            String slotText = effects[i] == null ? ui("Empty", "空") : slotEffectText(effects[i]);
            drawFittedString(g, (i + 1) + "  " + slotText, x + 12, rowY + 23, width - 24);
            rowY += 42;
        }
        return rowY;
    }

    String inventoryStatusText(UpgradeInventoryCard inventoryCard) {
        if (inventoryCard.rarity == UpgradeRarity.RED) {
            return ui("Weapon ", "武器 ") + (inventoryCard.slotIndex + 1)
                    + "  " + weaponLevelText(inventoryCard.effect, inventoryCard.level);
        }
        if (inventoryCard.rarity == UpgradeRarity.HIGH) {
            return ui("Gold ", "金色 ") + (inventoryCard.slotIndex + 1);
        }
        return "Lv " + inventoryCard.level;
    }

    int highTalentCount() {
        int count = 0;
        for (UpgradeEffect effect : highTalents) {
            if (effect != null) {
                count++;
            }
        }
        return count;
    }

    String compactSlotSummary(UpgradeEffect[] effects) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < effects.length; i++) {
            if (i > 0) {
                builder.append("  ");
            }
            builder.append(i + 1).append(":");
            if (effects[i] == null) {
                builder.append(ui("Empty", "空"));
            } else {
                builder.append(effectTitle(effects[i]));
                if (isWeaponEffect(effects[i])) {
                    builder.append(" ").append(weaponLevelText(effects[i], weaponLevel(effects[i])));
                }
            }
        }
        return builder.toString();
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
        if (rarity == UpgradeRarity.RED) {
            return COLOR_RARITY_RED;
        }
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

    String ownedCardDescription(UpgradeInventoryCard inventoryCard, UpgradeCard card) {
        String description = cardDescription(card);
        if (shouldShowBasicWeaponMultiplier(inventoryCard.effect, inventoryCard.level)) {
            description += "  " + ui("Current multiplier ", "当前倍率 ") + basicWeaponMultiplierText();
        }
        return description;
    }

    String upgradeChoiceDescription(UpgradeCard card) {
        if (card.effect != UpgradeEffect.TRIGGER_TUNING) {
            return cardDescription(card);
        }
        return ui("Add one bullet or laser beam; total volley damage rises, then splits.",
                "增加一发子弹或一道激光；齐射总伤害提高后再分摊。");
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
        UpgradeEffect weapon = currentWeaponEffect();
        return weapon == null ? effectTitle(UpgradeEffect.BASIC_WEAPON) : slotEffectText(weapon);
    }

    String slotEffectText(UpgradeEffect effect) {
        if (effect == null) {
            return ui("Empty", "空");
        }
        if (!isWeaponEffect(effect)) {
            return effectTitle(effect);
        }
        return effectTitle(effect) + " Lv " + Math.max(1, weaponLevel(effect));
    }

    String weaponLevelText(UpgradeEffect effect, int level) {
        String text = "Lv " + Math.max(1, level);
        if (shouldShowBasicWeaponMultiplier(effect, level)) {
            text += " " + basicWeaponMultiplierText();
        }
        return text;
    }

    boolean shouldShowBasicWeaponMultiplier(UpgradeEffect effect, int level) {
        return effect == UpgradeEffect.BASIC_WEAPON && Math.max(1, level) >= 2;
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

    void drawWrappedLimited(Graphics2D g, String text, int x, int y, int width, int lineHeight, int maxLines) {
        FontMetrics metrics = g.getFontMetrics();
        boolean spaced = text.indexOf(' ') >= 0;
        String[] words = spaced ? text.split(" ") : text.split("");
        String line = "";
        int lineY = y;
        int drawn = 0;
        for (String word : words) {
            if (word.length() == 0) {
                continue;
            }
            String separator = spaced && line.length() > 0 ? " " : "";
            String candidate = line + separator + word;
            if (metrics.stringWidth(candidate) > width && line.length() > 0) {
                if (drawn >= maxLines - 1) {
                    drawFittedString(g, line + (spaced ? " " : "") + word, x, lineY, width);
                    return;
                }
                g.drawString(line, x, lineY);
                drawn++;
                lineY += lineHeight;
                line = word;
            } else {
                line = candidate;
            }
        }
        if (line.length() > 0 && drawn < maxLines) {
            drawFittedString(g, line, x, lineY, width);
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
        drawCentered(g, "Typing Lane " + TypingLane.VERSION, WIDTH / 2, 170);
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
