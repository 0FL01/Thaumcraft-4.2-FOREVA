# Thaumcraft 4.2.3.5 — Forge 1.12.2 移植版

将 Thaumcraft 4.2.3.5 从 Minecraft 1.7.10 移植到 1.12.2。

原模组由 Azanor (2013-2015) 开发。这是一个非官方的社区移植版本。

## 展示

<p align="center">
  <img src="raw/showcase-thaumonomicon.jpg" width="48%" alt="Thaumonomicon research tree">
  <img src="raw/showcase-research.jpg" width="48%" alt="Research system">
</p>

<p align="center">
  <img src="raw/showcase-world.jpg" width="80%" alt="In-game showcase">
</p>

## 状态

处于活跃开发中的移植项目。核心系统已搭建完成，2026年7月的一次大规模清理修复了许多可见的 TC4 一致性问题：包括神秘百科（Thaumonomicon）的合成页、聚焦视觉效果、TC 粒子渲染、罐子、精华瓶、罐中脑行为，以及旅行/守护铺路石的一致性。

剩余工作主要集中在特定物品的逻辑处理、运行时一致性测试以及最终的视觉/渲染打磨。

## 项目技术栈

- **语言：** Java 8
- **运行时：** Minecraft Forge 1.12.2 (14.23.5.2847)
- **映射表 (Mappings)：** stable_39
- **构建工具：** Gradle (ForgeGradle 2.3)
- **依赖项：** Baubles (CurseMaven)
- **内置库：** CodeChicken Lib (thaumcraft.codechicken.*)

## 已实现功能

- 核心炼金与合成系统
- 注能 (Infusion)
- 傀儡 (Golems)
- 研究系统
- 神秘百科的研究树、文本/图像以及合成页
- 神秘百科中奥术/坩埚/注能合成页的渲染
- 世界生成 / 灵气 (Aura)
- 怪物生成、AI 及材质
- 外域 (Outer Lands) 维度
- 许多客户端效果的 TC4 风格粒子路径
- 罐子渲染、精华液体显示、标签以及罐中脑视觉效果
- 已恢复聚焦（如 Pech 和 Hellbat）的魔杖聚焦视觉效果
- 旅行铺路石和守护铺路石的行为，包括红石状态一致性、不可见守护灵气碰撞以及 TC4 符文粒子视觉效果

## 已知限制

- 部分特定物品的逻辑仍未完成，或需要针对剩余的边缘情况物品进行一致性审查
- 注能合成的游戏玩法仍需完整的运行时验证；注能阵渲染器本身已过加固
- 7 月清理后视觉一致性大幅提升，但剩余的模型/渲染器/材质仍需与 TC4 进行对比
- 未经测试的系统可能仍存在缺陷

## 开发情况

个人项目。开发过程中由使用多种模型的 LLM 智能体（GPT 5.5, GLM 5.2, Deepseek V4, MiMo V2.5 等）提供协助。

### 主要已知问题

渲染/模型的一致性仍然是目前最大的工作量所在。虽然许多高可见度的 TC4 视觉效果已恢复，但原版 1.7.10 的渲染管线仍无法完美映射到 Forge 1.12.2 的烘焙模型（baked models）。剩余工作包括 UV 清理、JSON 模型转换、显示变换（display transforms）以及单项模型路由。

## 下载

预构建的二进制文件可在 [Releases](https://github.com/0FL01/Thaumcraft-4.2-FOREVA/releases) 中获取。请从最新版本中下载 `Thaumcraft-1.0.0-universal.jar` —— 构建版本在每次推送时会自动发布。

## 许可证

MIT 许可证。详情请参阅 `LICENSE`。

原版 Thaumcraft 4.2.3.5 (c) 2013-2015 Azanor。
