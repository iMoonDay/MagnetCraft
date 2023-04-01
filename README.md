# 磁铁工艺

**(The English translation is below)**

**模组前置**: Cloth Config API

**模组初步开发中，如遇bug，请及时反馈。**

## 模组介绍

本模组是以磁铁为主题并延伸拓展而制作的一系列功能道具的模组。

## 物品介绍

**物品合成配方过多暂时无法展示，可使用REI查询**

### 矿物

**磁铁矿**：自然生成的新矿物，与铁矿生成机制类似，挖掘掉落粗磁铁，支持时运与精准采集。

### 基础材料

**磁铁碎片**：由粗磁铁烧炼而成，主要用于制作磁铁锭和磁铁模板。

**磁铁锭**：由磁铁碎片合成，主要用于大部分磁铁物品的制作。

**下界磁铁锭**：由磁铁锭合成，主要用于更高级磁铁物品的制作。

**磁铁粉**：由磁铁锭烧炼而成，用于炼制吸引药水。

**磁性悬浮粉**：由铁粒与磁铁碎片合成，用于合成磁悬浮系列方块。

**消磁粉**：磁性悬浮粉的合成残留物，用于制作消磁模板。

### 合成材料

**合成模块**：由磁铁锭和磁铁碎片合成，用于制作磁铁，不同的磁铁需要不同的合成模块。

**还原模块**：用于将磁铁还原成磁铁模板并返还合成模块。

**磁铁模板**：主要由磁铁锭合成，用于制作磁铁。

**过滤模块**：用于与磁铁合成，给磁铁提供过滤功能，过滤界面打开方式为原先的磁铁切换开关方式。

**提取模块**：高级砂轮的消耗材料。

**消磁模块**：用于合成合成控制器或消磁器。

**磁吸盘核心**：用于合成磁吸盘。

### 磁铁

**无极磁铁**：只有基本的吸引掉落物功能，范围25。

**电磁铁**：除了基本的吸引功能，还能直接将附近的掉落物传送至背包，传送消耗1耐久/个，耐久768，用末影珍珠修复，吸引范围15，传送范围20。

**永磁铁**：有电磁铁的全部功能，传送不消耗耐久，吸引范围35，传送范围30。

**生物磁铁**：右键生物可强行吸引生物，每30秒消耗1耐久/只，同时多只时消耗耐久的间隔更短，耐久300，用金苹果修复，吸引范围30。

**矿物磁铁**：合成时存储的矿物为空，可将磁铁与矿石产物(例如煤炭、粗铁、粗金、金粒、钻石等矿石默认掉落物)进行合成以增加某个矿石的查找能力，使用时查找以玩家为中心 20 * 20 * 20 范围内的矿物并自动挖掘放进背包，每次使用消耗5级经验，并根据不同矿物消耗不同耐久，同时进入冷却，耐久576，用绿宝石修复。

**磁铁控制器**：控制自身的全部吸引功能，可消耗1点耐久临时获取1分钟消磁效果，耐久100，用燧石修复。**快捷键：默认R**

**作物磁铁**：与矿物磁铁类似，查找以玩家为中心 30 * 30 * 30 范围内的作物，每次使用消耗饱食度，并根据数量消耗耐久，同时进入冷却，耐久2048，用金胡萝卜修复。

**便携消磁器**：放在背包内可阻止一定范围内非玩家实体的吸引效果。

**吸附磁铁**：生物磁铁的升级版，右键选择生物，潜行右键绑定吸附目标，如果目标是方块，当方块不存在时自动解除；如果目标是生物，则永久绑定。当绑定时未选择或者目标为生物自身时，解除吸附。若遇到无法潜行右键解除吸附的情况时，可先将生物绑定至方块然后挖掉方块即可。耐久320，用磁铁块修复。

**小型磁吸盘**：对方块长按进行使用，蓄力2秒后将方块吸入物品中，再次右键放置方块。耐久100，用磁铁锭修复。

**大型磁吸盘**：同小型磁吸盘，但有更多的设置以及更大的范围。耐久300，用磁铁块修复。

### 功能方块

**磁石**：吸引周围掉落物并存储进自身方块容器中，有红石激活模式与持续模式，范围0-30可调节。

**磁悬浮铁轨**：与原版相同，但不需要方块支撑，可穿透，但默认不可穿透，用磁扳手修改属性。

**更多磁悬浮系列**：拉杆、按钮、门、红石中继器、红石比较器。

**消磁器**：用红石激活，用来屏蔽范围内所有实体的吸引效果。

**吸引侦测器**：用来检测附近是否有吸引中的实体，输出最近一个实体的距离转化成红石信号。

**高级砂轮**：配合提取模块，将附魔物品的其中一个附魔提取出来，可以自行选择。提取后，物品失去所有附魔，如果物品为满耐久，则变为1点耐久，否则直接销毁。

**磁铁压力板**：仅检测玩家/骑着生物的玩家的压力板。

**垂直红石复制器**：可以检测上方/下方的红石信号，并复制到下方/上方。

**红石循环中继器**：与红石中继器相似，充能时循环输出信号，信号频率分别为 1/4/10/20 tick。

**电磁继电器**：在末端与侧面同时输入红石信号时，前端输出末端红石信号。

**磁天线**：仅可放置在磁铁块/下界磁铁块/磁石上，当底座方块充能时或者底座方块是磁石且有物品在内时，磁铁线激活。激活时，当底座是磁铁块时，在磁天线方向15格内第一个朝向自身的磁天线接收激活磁天线的红石能量并传递给底座；当底座是下界磁铁块时，距离为30格，并且范围内所有磁天线同时接收红石能量；当底座是磁石时，与磁铁块一样，红石能量为比较器输出能量。激活时亮度为15，接收时亮度为8。

### 液体：磁流体

**吸引效果**：当实体接触磁流体时，持续获得吸引效果；当实体完全浸泡在磁流体中时，每秒增1秒吸引效果。

**修复功能**：如果玩家浸泡时手持磁铁，有小概率修复磁铁，并有概率将磁流体转换成水；如果磁铁掉落物浸泡在磁流体中，同玩家一样，但是修复概率为玩家的一半，同时将磁流体转换成水的概率也为玩家一半。

**减速效果**：任何生物接触磁流体都会被减速，越靠近源头减速效果越明显。

**获取方式**：由吸引药水填充炼药锅生成装有磁流体的炼药锅，当吸引药水填满炼药锅时，用空桶取出的即是磁流体，反之亦可。

### 效果

**吸引**：持续吸引附近掉落物，范围20+，等级越高范围越大。

**消磁**：禁止周围实体使用磁铁与吸引效果，范围15。

**不可吸引**：由激活的消磁器赋予，用来阻止实体吸引。

### 附魔

**吸引**：装备持有该附魔时自动吸引附近掉落物，所有装备的吸引附魔效果叠加，最高5。

**自动采集**：挖掘工具附魔，可将挖掘后的掉落物直接放进背包中。

**自动抢夺**：武器附魔，可将杀死实体后的掉落物直接放进背包中。

**消磁保护**：胸甲附魔，可抵御消磁器的不可吸引效果。

**冷却缩减**：磁铁附魔，每级附魔减少磁铁使用时10%的冷却时间。

**磁悬浮**：靴子附魔，启用悬浮(默认快捷键左ALT)后按住跳跃键或者启用自动悬浮(默认快捷键右ALT)可持续悬空一段高度，启用悬浮时靴子以固定频率消耗耐久，附魔等级越高悬空高度越高，悬浮后免疫第一次摔落伤害，非磁铁类靴子上升速度较慢，下界磁铁靴子上升速度翻倍。

**蓄力一击**：磁扳手专属附魔，最高5级，每级提升磁扳手25%的伤害上限，即最高2.25倍伤害，可配合暴击造成最高3.375倍伤害。

### 装备、武器与工具

**磁铁装备**：属性与铁套基本相同，四件套给予效果：吸引范围提升至1.5倍。

**下界磁铁装备**：属性与下界合金套基本相同，四件套给予效果：吸引范围提升至2倍。

**磁铁武器/工具**：以铁类属性为基础略微提升攻击伤害。

**下界磁铁武器/工具**：以下界合金类属性为基础略微提升攻击伤害。

**磁铁马铠**：以铁马铠属性为基础略微提升数值，并给予吸引效果，范围5。

**磁扳手**：用于更改磁悬浮铁轨的可穿透性，右键修改单个铁轨，潜行右键同时修改两个方向上最多15个相连且相同类型铁轨。同时也属于镐子，属性接近磁铁镐，长按右键可抛出，正常默认伤害为4，蓄力时间过短伤害会降低，有33%的概率暴击，造成1.5倍伤害。当玩家或者磁扳手有吸引效果时，扔出的磁扳手自动返回。耐久512，用磁铁锭修复。

### 生物：磁傀儡

**基本属性**：同铁傀儡相同。

**生成方式**：
1. 同铁傀儡相同，由村民以25%的概率召唤。
2. 同铁傀儡类似，由磁铁块与雕刻南瓜/南瓜灯摆成同铁傀儡召唤时相同的位置召唤。如果将中心的磁铁块换成磁石，召唤的磁傀儡自带吸引功能。

**吸引功能**：使用磁石右键磁傀儡可赋予磁傀儡吸引功能，并携带27格背包，右键磁傀儡打开背包。吸引距离15，物品在接触磁傀儡时会被磁傀儡放进背包。

**血量恢复功能**：使用磁铁锭右键磁傀儡消耗1个磁铁锭恢复25血量。将磁铁锭丢到地上被磁傀儡吸引进背包时也会自动使用磁铁锭回血。

**掉落物品**：磁铁碎片2-4个，磁铁锭2-4个。

## 指令介绍

### 全局黑/白名单

(使用指令需要拥有管理员权限,单机无作弊可进Mod设置界面添加物品)

`/magnet [blacklist/whitelist] [add/remove/clear/list/enable/disable] (add/remove)[item:可选]`

**blacklist**:黑名单 (快捷键：默认"**-**")

**whitelist**:白名单 (快捷键：默认"**=**")

**add**:添加物品

**remove**:移除物品

**clear**:清空物品

**list**:物品列表

**enable**:启用

**disable**:禁用

**item**:物品id(可选，如果不填则选择主手物品)

# Magnet Craft

**Pre-mod**: Cloth Config API

**During the initial development of the module, if you encounter bugs, please give feedback in time.**

## Module introduction

This module is a module of a series of functional props made with the theme of magnets and extended.

## Introduction of items

**Due to too many item synthesis recipes, please use REI to inquire**

### Mineral

**Magnetite**: A new naturally occurring mineral that has a similar mechanism to iron ore generation, mining and dropping crude magnets to support timing and precise harvesting.

### Base material

**Magnet Fragment**: Made from crude magnets sintered, mainly used to make magnet ingots and magnet templates.

**Magnetic Iron Ingot**: Synthesized from magnet fragments, mainly used for the production of most magnet items.

**Netherite Magnetic Iron Ingot**: Synthesized from magnet ingots, mainly used for the crafting of higher-grade magnet items.

**Magnet Powder**: Made by sintering magnet ingots and used to refine attraction potions.

**Magnetic Suspended Powder**: Synthesized from iron particles and magnet fragments, used to synthesize maglev series blocks.

**Demagnetized Powder**: A synthetic residue of magnetic suspension powder, used to make degaussing templates.


### Synthetic materials

**Crafting Module**: Synthesized from magnet ingots and magnet fragments to make magnets, different magnets require different synthesis modules.

**Restore Module**: Used to reduce the magnet to a magnet template and return the synthesis module.

**Magnet Template**: Mainly synthesized from magnet ingots, used to make magnets.

**Filter Module**: Used to synthesize with magnets, provide filtering functions for magnets, and open the filter interface as the original magnet switch mode.

**Extraction Module**: Consumable material for advanced grinding wheels.

**Demagnetize Module**: Used to synthesize a synthesis controller or demagnetizer.

**Magnetic Sucker Core**: Used to synthesize magnetic chuck cups.

### Magnet

**Polar Magnet**: Only basic attraction drop function, range 25.

**Electromagnet**: In addition to the basic attraction function, it can also directly teleport nearby drops to the backpack, the teleportation cost is 1 durability/piece, durability 768, repair with ender pearl, attraction range 15, teleportation range 20.

**Permanent Magnet**: has all the functions of electromagnet, transmission does not consume durability, attraction range 35, transmission range 30.

**Creature Magnet**: Right-click mob can forcibly attract mobs, consuming 1 durability/stick every 30 seconds, and consuming durability at a shorter interval of 300 when multiple at the same time, repaired with golden apples, attraction range 30.

**Mineral Magnet**: The mineral stored during synthesis is empty, the magnet can be synthesized with ore products (such as coal, crude iron, crude gold, gold grains, diamonds and other ore default drops) to increase the search ability of a certain ore, when used to find minerals in the range of 20 * 20 * 20 centered on the player and automatically dig into the backpack, each use costs 5 levels of experience, and consumes different durability according to different minerals, while entering cooling, durability 576, repaired with emerald.

**Magnet Controller**: Control all the attraction functions of itself, can consume 1 point of durability to temporarily obtain 1 minute demagnetization effect, durability 100, repair with flint. **Shortcut: Default R**

**Crop Magnet**: Similar to the mineral magnet, find crops in the 30 * 30 * 30 range centered on the player, consume satiety per use, and consume durability according to the quantity, while going into cooling, durability 2048, repaired with golden carrots.

**Portable Demagnetizer**: Fits inside a backpack to prevent attraction from non-player entities within a certain range.

**Adsorption Magnet**: An upgraded version of the biological magnet, right-click to select the creature, sneak right-click to bind the adsorption target, if the target is a block, it will be automatically lifted when the block does not exist; If the target is a creature, it is permanently bound. When the binding is not selected or the target is the organism itself, the adsorption is released. If you encounter a situation where you can't sneak the right button to unabsorb it, you can bind the creature to the block and then dig out the block. Durable 320, repaired with magnet blocks.

**Small Magnetic Sucker**: Press and hold the block to use, charge for 2 seconds and then draw the block into the object, right-click to place the block again. Durable 100, repaired with magnet ingots.

**Large Magnetic Sucker**: Same as small magnetic cup, but with more settings and larger range. Durable 300, repaired with magnet blocks.

### Function block

**Lodestone**: Attracts surrounding drops and stores them in its own block container, with Redstone Activation Mode and Persistence Mode, adjustable from 0-30.

**Maglev Rail**: Same as the original, but does not require block support, is permeable, but is impenetrable by default, and modifies the properties with a magnetic wrench.

**More Maglev Series**: Tie rods, buttons, doors, redstone repeaters, redstone comparators.

**Demagnetizer**: Activated with redstone, it is used to shield the attraction effect of all entities within range.

**Attract Sensor**: Used to detect whether there are attractive entities nearby, and output the distance of the nearest entity into a redstone signal.

**Advanced Grindstone**: In conjunction with the extraction module, one of the enchanted items can be extracted by yourself. After extraction, the item loses all enchantment, and if the item is full durability, it becomes 1 point of durability, otherwise it is directly destroyed.

**Magnetic Pressure Plate**: A pressure plate that only detects players/players riding mobs.

**Vertical Redstone Replicators**: Can detect above/below redstone signals and replicate below/above.

**Circular Repeater**: Similar to Redstone Repeater, it cycles the output signal when charged with a signal frequency of 1/4/10/20 ticks, respectively.

**Electromagnetic Relay**: When the redstone signal is input at the end and the side at the same time, the front end outputs the terminal redstone signal.

**Magnetic Antenna**: Can only be placed on a magnet block/nether magnet block/magnet, the magnet line activates when the base block is charged or when the base block is a magnet and there are items included. When activated, when the base is a magnet block, the first magnetic antenna facing itself within 15 blocks of the magnetic antenna direction receives the redstone energy of the activated magnetic antenna and transmits it to the base; When the base is a nether magnet block, the distance is 30 tiles, and all magnetic antennas within range receive redstone energy at the same time; When the base is a magnet, like the magnet block, the redstone energy outputs energy to the comparator. The brightness is 15 when activated and 8 when receiving.

### Liquid: Magnetic fluid

**Attraction effect**: When the entity comes into contact with the magnetic fluid, the attraction effect is continuously obtained; When the entity is completely immersed in the magnetic fluid, the attraction effect increases by 1 second per second.

**Repair function**: If the player holds the magnet while immersing, there is a small probability of repairing the magnet and the probability of converting the magnetic fluid into water; If a magnet drop is immersed in a magnetic fluid, it is the same as the player, but the probability of repairing it is half that of the player, and the probability of converting the magnetic fluid to water is also half that of the player.

**Deceleration effect**: Any biological contact with magnetic fluid will be slowed down, and the closer to the source, the more obvious the deceleration effect.

**How to get it**: A pot containing magnetic fluid is generated by filling the pot with attraction pot, and when the attraction pot fills the pot, the magnetic fluid is taken out with an empty barrel, and vice versa.

### Effect

**Attract**: Continuously attracts nearby drops, range 20+, the higher the level, the greater the range.

**Degaussing**: Prohibit the use of magnets with attraction effects by surrounding entities, range 15.

**Unattract**: Granted by an activated demagnetizer to prevent entity attraction.

### Enchantment

**Attract**: Equipment automatically attracts nearby drops when holding this enchantment, and the attraction enchantment effect of all equipment is stacked, up to a maximum of 5.

**Automatic Collection**: The digging tool enchants the drop after digging directly into the backpack.

**Automatic Looting**: A weapon enchanted to place dropped objects after killing entities directly into the backpack.

**Degaussing Protection**: Breastplate enchantment to resist the unattractive effect of the demagnetizer.

**Faster Cooldown**: Magnet enchantment, each level of enchantment reduces the cooldown time when the magnet is used by 10%.

**Magnetic Levitation**: Boots enchanted, enable levitation (default shortcut left ALT) and hold down the jump key or enable auto-levitation (default shortcut right ALT) to continuously hang for a certain height, when levitation is enabled, boots consume durability at a fixed frequency, the higher the enchantment level, the higher the suspension height, immunity to the first fall damage after levitation, non-magnet boots rise slower, Nether magnet boots rise twice as fast.

**Accumulator**: Magnetic wrench-exclusive enchantment, up to level 5, each level increases the magnetic wrench's damage limit by 25%, that is, up to 2.25x damage, and can deal up to 3.375x damage with critical hits.

### Equipment, Weapons & Tools

**Magnetic Iron Equipment**: The attributes are basically the same as the iron sleeve, and the four-piece set gives the effect: the attraction range is increased to 1.5 times.

**Netherite Magnetic Iron Equipment**: The properties are basically the same as the Nether Alloy Set, and the four-piece set gives the effect: the attraction range is increased to 2x.

**Magnetic Iron Weapon/Tool**: Slightly increases attack damage based on iron attributes.

**Netherite Magnetic Iron Weapon/Tool**: Slightly increases attack damage based on Nether alloy attributes.

**Magnetic Iron Horse Armor**: Slightly increases the value based on the Iron Horse Armor attribute and gives an attraction effect, range 5.

**Magnetic Wrench**: Used to change the permeability of maglev rails, right-click to modify a single rail, and sneak right-click to modify up to 15 connected rails of the same type in both directions. At the same time, it is also a pickaxe, the attribute is close to the magnet pickaxe, long press the right button to throw, the normal default damage is 4, the damage will be reduced if the charge time is too short, there is a 33% probability of critical hit, causing 1.5 times the damage. When the player or the wrench has an attraction effect, the thrown wrench automatically returns. Durable 512, repaired with magnet ingots.

### Creature: Magnetic Iron Golem

**Basic Attributes**: Same as Iron Golem.

**Build by**:

1. Same as the Iron Golem, summoned by the villagers with a 25% probability.
2. Similar to the Iron Golem, the magnet block and the carved pumpkin/jack-o-lantern are summoned in the same position as the Iron Golem when summoned. If you replace the magnet block in the center with a magnet, the summoned magnetic golem has a self-attraction function.

**Attraction function**: Use the right-click magnetic puppet to give the magnetic puppet attraction function, and carry a 27-compartment backpack, right-click the magnetic puppet to open the backpack. Attraction distance 15, items will be placed in the backpack by the magnetic golem when they touch it.

**HP Recovery Function**: Use the right button of the magnet ingot to consume 1 magnet ingot to restore 25 HP. When you drop the magnet ingot to the ground and are attracted to the backpack by the magnetic golem, you will also automatically use the magnet ingot to return blood.

**Drop items**: 2-4 magnet fragments, 2-4 magnet ingots.

## Introduction of Commands

### Global black/white list

(Administrator privileges are required to use instructions, and you can add items to the Mod setting interface without cheating)

`/magnet [blacklist/whitelist] [add/remove/clear/list/enable/disable] (add/remove)[item:Optional]`

**blacklist**: Blacklist (Shortcuts: Default "**-**") 

**whitelist**: Whitelist (Shortcuts: Default "**=**")

**add**: Add item to list

**remove**: Remove item from list

**clear**: Clear item list

**list**: Get item list 

**enable**: Enable list

**disable**: Disable list

**item**: Item ID (optional, if not filled in, select the mainhand item)
