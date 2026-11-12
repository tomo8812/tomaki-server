# 先行生成スループット制御 — レビュー判断記録

[設計仕様へ戻る](pregen-throughput-controller-v2-design.md) | [PR #89](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89)

本書は設計判断の追跡資料です。技術仕様は[主設計書](pregen-throughput-controller-v2-design.md)に集約しています。指摘の「正本・安全要件」はその安全性を満たす必要があるという意味で、提案された実装方法をすべて確定採用した意味ではありません。「計測後に採否」は性能上の効果と保守負荷を比較して判断します。

## 整理済みレビュー R01–R67

| 指摘 | 判断 | 設計箇所 |
| --- | --- | --- |
| [R01 二段階admissionと非同期skip](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339651) | 正本・安全要件 | §5 |
| [R02 既存Redirectと計測フックの実装境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339664) | 正本・安全要件 | §5 |
| [R03 最終guard拒否の返却](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339680) | 正本・安全要件 | §5 |
| [R04 WIP creditとrate token](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339523) | 計測後に採否 | §5 |
| [R05 停止の可視性とepoch](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339617) | 正本・安全要件 | §5 |
| [R06 AutoPause自動再開境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339823) | 正本・安全要件 | §5 |
| [R07 retryの座標別処理](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339919) | 計測後に採否 | §10 |
| [R08 retry ledgerの完全性](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339937) | 正本・安全要件 | §10 |
| [R09 CaptureFlightのsource証明](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339020) | 正本・安全要件 | §4 |
| [R10 LOD同座標追越し](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339048) | 正本・安全要件 | §4 |
| [R11 永続PAIR receipt](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339063) | 正本・安全要件 | §4 |
| [R12 FULL atomic publish](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339076) | 正本・安全要件 | §4 |
| [R13 single-flightと世代fence](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339540) | 正本・安全要件 | §4 |
| [R14 timeoutとcommit確定線](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339489) | 正本・安全要件 | §4 |
| [R15 LOD late ACK handle](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339556) | 正本・安全要件 | §4 |
| [R16 物理WIP観測欠損](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339592) | 正本・安全要件 | §4・§6 |
| [R17 WIP残数スナップショット](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339561) | 正本・安全要件 | §4・§6 |
| [R18 timeout済queue回収](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339608) | 計測後に採否 | §4・§6 |
| [R19 LOD guard timer起点](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339499) | 正本・安全要件 | §4・§6 |
| [R20 snapshot予測予約](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339570) | 計測後に採否 | §7 |
| [R21 NBTとheap占有](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339580) | 計測後に採否 | §7 |
| [R22 chunk/ticket滞留](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339314) | 計測後に採否 | §7 |
| [R23 PAIR critical path](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339125) | 正本・安全要件 | §3・§6 |
| [R24 期限分位の解像度](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339095) | 計測後に採否 | §3・§6 |
| [R25 LOD同期fallbackとμ_busy](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339366) | 計測後に採否 | §6 |
| [R26 LOD storeの排他ロックとμ測定境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339376) | 計測後に採否 | §6 |
| [R27 ACK callback仕事](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339389) | 計測後に採否 | §6 |
| [R28 共有writer source帰属](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339402) | 計測後に採否 | §6 |
| [R29 c変化とμの鮮度](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339416) | 計測後に採否 | §6 |
| [R30 物理能力と成功歩留まり](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339437) | 正本・安全要件 | §6 |
| [R31 実行中右打ち切り](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339454) | 計測後に採否 | §6 |
| [R32 サイズ帯別debt](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339468) | 計測後に採否 | §6 |
| [R33 低頻度ACK統計](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339693) | 計測後に採否 | §6 |
| [R34 ACK補充WIP窓比較](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339709) | 制御の第一候補 | §6 |
| [R35 UNKNOWN有界分類probe](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339302) | 計測後に採否 | §5・§6 |
| [R36 shadow admission評価](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339284) | 計測後に採否 | §5・§6 |
| [R37 FAST込みc比較](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339753) | 正本・安全要件 | §8 |
| [R38 online CPU throttle](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339740) | 計測後に採否 | §8 |
| [R39 残仕事に対する探索採算](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339810) | 計測後に採否 | §8 |
| [R40 c試行早期終了規則](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339781) | 正本・安全要件 | §8 |
| [R41 中心側の穴](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339797) | 計測後に採否 | §8 |
| [R42 player LOD待機予算](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339726) | 計測後に採否 | §9 |
| [R43 foreground READ_IO優先](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339267) | 計測後に採否 | §9 |
| [R44 VALIDATEとscanner分離](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531338940) | 計測後に採否 | §3 |
| [R45 SCANS permit制約](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339328) | 計測後に採否 | §3 |
| [R46 soft pressureとtask存続](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339837) | 計測後に採否 | §9 |
| [R47 hard/soft同時原因](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339850) | 正本・安全要件 | §9 |
| [R48 cap単一更新主体](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339247) | 正本・安全要件 | §9 |
| [R49 Backfill下流tail](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339630) | 計測後に採否 | §9 |
| [R50 TEMP_PREPARE並走](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339149) | 計測後に採否 | §3・§11 |
| [R51 FULL reconcile占有](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339170) | 計測後に採否 | §3・§11 |
| [R52 FULL prune占有](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339197) | 計測後に採否 | §3・§11 |
| [R53 RecoveryClaims cold start](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531338969) | 計測後に採否 | §3・§11 |
| [R54 LOD append増幅](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339869) | 計測後に採否 | §7 |
| [R55 region 2GiB境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339880) | 正本・安全要件 | §7 |
| [R56 compactionとstamp失効](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339890) | 計測後に採否 | §7 |
| [R57 FileStore/symlink境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339905) | 正本・安全要件 | §7 |
| [R58 usableBytes refresh競合](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339134) | 正本・安全要件 | §7 |
| [R59 LOD skip stamp I/O](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531338982) | 計測後に採否 | §3・§11 |
| [R60 LOD presence index全失効](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339211) | 計測後に採否 | §3・§11 |
| [R61 ownership reflection overhead](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339232) | 計測後に採否 | §3・§11 |
| [R62 POI ACTIVE全走査](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339005) | 計測後に採否 | §3・§11 |
| [R63 PipelineMetricsロック外化](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339108) | 計測後に採否 | §3・§11 |
| [R64 source証明観測可能率](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339346) | 正本・安全要件 | §3・§11 |
| [R65 preflight cgroup資源](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339969) | 測定スクリプトに反映 | §2 |
| [R66 preflight scope差](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339951) | 測定スクリプトに反映 | §2 |
| [R67 preflight出力非破壊](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339983) | 測定スクリプトに反映 | §2 |

## 先行レビュー16件の対応

| コメント | 設計への反映 | 設計箇所 |
| --- | --- | --- |
| [4525886419](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525886419) / [4525901427](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525901427) | timeout後の物理writer WIPを追跡 | §4・§6 |
| [4525910892](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525910892) | 未来creditと物理stageを分離して段階別解放 | §6 |
| [4525886425](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525886425) / [4525901462](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525901462) / [4525910882](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525910882) | native安全とasync skipを考慮した二段階admission | §5 |
| [4525886438](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525886438) | retry原因と成熟時刻、座標別処理の実現性 | §10 |
| [4525886452](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525886452) / [4525901473](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525901473) / [4525910939](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525910939) | 2GiB余裕を維持する事前計測とskip | §2 |
| [4525901410](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525901410) | ACK到着率とbusyサービス能力の分離 | §6 |
| [4525901441](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525901441) | native幅縮小時の上方探索凍結、許可仕事は継続 | §8・§9 |
| [4525901451](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525901451) | generation-boundのc+1、storage-boundのc-1 | §8 |
| [4525910905](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525910905) | 同期fallbackをタイムアウトと独立した早期警報にする | §6 |
| [4525910915](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525910915) | LOD tailの推定容量とheadroom | §7 |
| [4525910925](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4525910925) | 実dispatch差が無いcap試行は判定不能 | §8 |

その他の既解決レビューも[PR #89のレビュー履歴](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89)から追跡できます。ここで記録する判断は設計上の扱いであり、制御MODの実装・VM上の性能改善・電源断耐久性を検証済みとするものではありません。
