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


final class UpgradeCard {
    final String title;
    final String description;
    final String titleZh;
    final String descriptionZh;
    final UpgradeRarity rarity;
    final UpgradeEffect effect;

    UpgradeCard(String title, String description, UpgradeRarity rarity, UpgradeEffect effect) {
        this(title, description, title, description, rarity, effect);
    }

    UpgradeCard(String title, String description, String titleZh, String descriptionZh,
            UpgradeRarity rarity, UpgradeEffect effect) {
        this.title = title;
        this.description = description;
        this.titleZh = titleZh;
        this.descriptionZh = descriptionZh;
        this.rarity = rarity;
        this.effect = effect;
    }
}
