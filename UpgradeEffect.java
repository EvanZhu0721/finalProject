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


enum UpgradeEffect {
    FIELD_PATCH,
    REINFORCED_CORE,
    CALIBRATED_DAMAGE,
    TRIGGER_TUNING,
    COMBO_TUNING,
    LONG_WORD_REWARD,
    SHORT_WORD_QUICKSHOT,
    FINAL_LETTER_BURST,
    VOWEL_CONVERGENCE,
    HARD_CONSONANT_BREAK,
    SAME_LANE_SUPPRESSION,
    LANE_SWAP_BEAT,
    SINGLE_LANE_BASTION,
    ALTERNATING_GUARD,
    COMBO_CALIBRATOR,
    FIRST_LETTER_LOCK,
    ACCURATE_OPENER,
    PREFIX_ILLUMINATION,
    FIRST_LETTER_TICKET,
    DUAL_PREFIX_SCAN,
    CLEAN_FINISH,
    ERROR_RESET,
    BACKSPACE_FIX,
    CALM_AFTER_ERROR,
    BACKSPACE_COUNTER,
    PRECISE_PICKUP,
    FINAL_LETTER_PULL,
    LONG_WORD_FOCUS,
    DANGER_WORD,
    PRESSURE_VALVE,
    PHASE_SWITCH,
    BOSS_BREAKER,
    CROSSFEED,
    RHYTHM_CANNON,
    FROST_FIELD,
    DRY_ICE_BULLET,
    HOMING_SHOTGUN,
    TEST_INVINCIBLE,
    TEST_BIG_XP
}
