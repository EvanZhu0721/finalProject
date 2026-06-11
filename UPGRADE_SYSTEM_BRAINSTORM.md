# Typing Lane 升级系统备忘

状态：1.2.0 发布版已接入，后续补充更多具体 Buff。

## 目标

用 XP 进度承接击败敌人与 Boss 奖励，让升级选择服务 lane 决策、输入节奏和构筑成长。

## 当前规则

- 玩家初始 HP 为 36，早期普通碰撞约 6 次容错。
- 碰撞按目标当前真实 HP 扣除玩家 HP。
- 普通和精英目标掉落 lane 绑定 XP 球。
- XP 球靠近玩家同 lane 时小范围吸附。
- Boss 击破后直接奖励大量 XP：`24 + BossLevel * 8`。
- XP 进度满后生成待选升级。
- Space 主动打开升级菜单。
- 升级菜单使用 3 卡选择：Left / Right + Enter，或 1 / 2 / 3。
- 字母键始终服务打字输入。

## XP 经济

- NORMAL：1 XP。
- FAST：2 XP。
- TANK / SHIFT / CACHE：4 XP。
- Boss：大量直接 XP。

升级阈值：

- 第 1 次升级：6 XP。
- 第 2 次升级：8 XP。
- 第 3 次升级：12 XP。
- 第 4 次升级：16 XP。
- 第 5 次升级：22 XP。
- 第 6 次起：30 XP、40 XP、50 XP，之后每级 +10 XP。

## 稀有度槽位

- 常规：Common + Common + Uncommon。
- 进阶：Common + Uncommon + Uncommon。
- 高阶：Common + Uncommon + High。
- 每 5 个升级等级提高 High 槽出现率。

## 已实现卡牌

Common：

- Field Patch：回复 8 HP，满 HP 时提升 6 HP 上限。
- Reinforced Core：提升 6 HP 上限。
- Calibrated Damage：加算伤害百分比。
- Trigger Tuning：加算射速百分比。
- Ram Plating：降低主动撞击消耗。
- Combo Tuning：提升稳定连击收益。

Uncommon：

- Phase Switch：换 lane 后获得短暂碰撞保护。
- Boss Breaker：提升 Boss 与精英目标伤害。
- Crossfeed Letters：f / t / k 开头单词提高 XP 收集收益。

High：

- Rhythm Cannon：Weapon 构筑槽。
- Frost Field：Control 构筑槽。
- Proofread Drone：Automation 构筑槽。

## 后续 Buff 方向

- Weapon：改变攻击节奏、弹道形态或爆发窗口。
- Control：减速、冻结、压制 Boss 行动窗口。
- Automation：无人机、自动补刀、漏怪优先级。
- Economy：围绕 lane 收集、词首字母、Combo 的 XP 变体。
- Defense：相位、护盾、撞击成本、HP 上限联动。

## 验证目标

- Normal 和 Hard 约 5 分钟种子局：升级节奏保持在每分钟 1-2 次。
- lane 收集测试：击败与收集位置继续影响收益节奏。
- 主动撞击测试：高 HP 构筑仍有明确代价。
- DPS 测试：高伤害和高射速叠加后仍保留有效输入压力。
- Boss 节奏测试：Boss 1、3、6 保持多词击破节奏。
