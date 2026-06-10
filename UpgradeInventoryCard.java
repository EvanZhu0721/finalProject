final class UpgradeInventoryCard {
    final UpgradeEffect effect;
    final UpgradeRarity rarity;
    final int level;
    final int slotIndex;

    UpgradeInventoryCard(UpgradeEffect effect, UpgradeRarity rarity, int level, int slotIndex) {
        this.effect = effect;
        this.rarity = rarity;
        this.level = level;
        this.slotIndex = slotIndex;
    }
}
