## 本次更新内容

### 新增

1. 红石循环中继器(功能方块)：与红石中继器相似，充能时循环输出信号，信号频率分别为 1/4/10/20 tick。
2. 电磁继电器(功能方块)：在末端与侧面同时输入红石信号时，前端输出末端红石信号。
3. 吸附磁铁(磁铁)：生物磁铁的升级版，右键选择生物，潜行右键绑定吸附目标，如果目标是方块，当方块不存在时自动解除；如果目标是生物，则永久绑定。当绑定时未选择或者目标为生物自身时，解除吸附。若遇到无法潜行右键解除吸附的情况时，可先将生物绑定至方块然后挖掉方块即可。耐久320，用磁铁块修复。
4. 磁吸盘核心(基础材料)：用于合成磁吸盘。
5. 小型磁吸盘(磁铁)：对方块长按进行使用，蓄力2秒后将方块吸入物品中，再次右键放置方块。耐久100，用磁铁锭修复。
6. 大型磁吸盘(磁铁)：同小型磁吸盘，但有更多的设置以及更大的范围。耐久300，用磁铁块修复。
7. 磁天线(功能方块)：仅可放置在磁铁块/下界磁铁块/磁石上，当底座方块充能时或者底座方块是磁石且有物品在内时，磁铁线激活。激活时，当底座是磁铁块时，在磁天线方向15格内第一个朝向自身的磁天线接收激活磁天线的红石能量并传递给底座；当底座是下界磁铁块时，距离为30格，并且范围内所有磁天线同时接收红石能量；当底座是磁石时，与磁铁块一样，红石能量为比较器输出能量。激活时亮度为15，接收时亮度为8。
8. 磁扳手(镐子类工具)：用于更改磁悬浮铁轨的可穿透性，右键修改单个铁轨，潜行右键同时修改两个方向上最多15个相连且相同类型铁轨。同时也属于镐子，属性接近磁铁镐，长按右键可抛出，正常默认伤害为4，蓄力时间过短伤害会降低，有33%的概率暴击，造成1.5倍伤害。当玩家或者磁扳手有吸引效果时，扔出的磁扳手自动返回。耐久512，用磁铁锭修复。
9. 蓄力一击(附魔)：磁扳手专属附魔，最高5级，每级提升磁扳手25%的伤害上限，即最高2.25倍伤害，可配合暴击造成最高3.375倍伤害。

### 修改

1. 磁悬浮铁轨默认不可穿透，用磁扳手修改属性。
2. 磁铁压力板也检测骑着生物的玩家
3. 创造物品栏分组修改

### 修复

1. 部分方块无法掉落或者无法挖掘

## This update

### New

1. Circular Repeater (Function Block): Similar to Redstone Repeater, it cycles the output signal when charging, and the signal frequency is 141020 ticks.
2. Electromagnetic Relay (functional block): When the redstone signal is input at the end and the side at the same time, the front end outputs the terminal redstone signal.
3. Adsorption Magnet (magnet): an upgraded version of the biological magnet, right-click to select the creature, sneak right-click to bind the adsorption target, if the target is a block, it will be automatically lifted when the block does not exist; If the target is a creature, it is permanently bound. When the binding is not selected or the target is the organism itself, the adsorption is released. If you encounter a situation where you can't sneak the right button to unabsorb it, you can bind the creature to the block and then dig out the block. Durable 320, repaired with magnet blocks.
4. Magnetic Sucker Core (base material): used to synthesize magnetic chuck cups.
5. Small Magnetic Sucker (magnet): Press and hold the block to use, draw the block into the object after 2 seconds, and right-click to place the block again. Durable 100, repaired with magnet ingots.
6. Large Magnetic Sucker (magnet): Same as small magnetic cup, but with more settings and larger range. Durable 300, repaired with magnet blocks.
7. Magnetic Antenna (functional block): can only be placed on the magnet block lower boundary magnet block magnet, when the base block is charged or when the base block is a magnet and there are items included, the magnet wire is activated. When activated, when the base is a magnet block, the first magnetic antenna facing itself within 15 blocks of the magnetic antenna direction receives the redstone energy of the activated magnetic antenna and transmits it to the base; When the base is a nether magnet block, the distance is 30 tiles, and all magnetic antennas within range receive redstone energy at the same time; When the base is a magnet, like the magnet block, the redstone energy outputs energy to the comparator. The brightness is 15 when activated and 8 when receiving.
8. Magnetic Wrench (pickaxe type tool): Used to change the permeability of maglev rails, right-click to modify a single rail, and sneak right-click to modify up to 15 connected rails of the same type in both directions. At the same time, it is also a pickaxe, the attribute is close to the magnet pickaxe, long press the right button to throw, the normal default damage is 4, the damage will be reduced if the charge time is too short, there is a 33% probability of critical hit, causing 1.5 times the damage. When the player or the wrench has an attraction effect, the thrown wrench automatically returns. Durable 512, repaired with magnet ingots.
9. Accumulator (Enchantment): Magnetic wrench-exclusive enchantment, up to level 5, each level increases the magnetic wrench's damage limit by 25%, that is, up to 2.25 times damage, and can deal up to 3.375 times damage with critical hits.

### Revise

1. Maglev rails are impenetrable by default, modify the properties with a magnetic wrench.
2. Magnet pressure plates also detect players riding creatures.
3. Create inventory group modifications.

### Repair

1. Some blocks cannot be dropped or digged.
