# Maki Chunksmith Chunk Cache 1.5.0

Minecraft 1.21.1 / NeoForge 21.1 / Chunksmith 4.8.1用の一時実チャンクキャッシュです。

Chunksmithが生成した一時FULLチャンクを、Entityスナップショットとともに通常のregion保存先から退避します。プレイヤーがロードすると、通常FULLを優先し、必要な場合にキャッシュを復元して通常保存へ引き渡します。チャンク・Entity・POIの保存確認まで復旧用データを保持します。

LODの生成・保存・再利用・互換性判断はChunksmithが担当します。このmodはLODファイルを走査・検証・更新せず、LOD不足を補うためのチャンク生成も行いません。

## Controllerとの連携

FULL検証とFULL書込の実開始・実終了、保存確定・既存FULL再利用・失敗を、Controllerが発行した同一試行tokenで通知します。論理タイムアウト後も、開始済み処理の完了通知は実際の終了まで待ちます。生成投入・WIP窓・並列数探索・FULLとLODのPAIR計測はControllerが担当します。

## 保存形式と既存データ

固定保存先ID `f00dcace0000000000000003` とschema 3を維持します。新しい保存完了印は `fullCommitted` です。旧schema 3の `lodCommitted` は過去のFULL公開完了印として読み取ります。LODファイルの存在や内容を参照せず、旧FULLは新しい印の欠如だけで無効化しません。

旧世代からのコピー・検証・原子的な採用記録と、所有権が不明なファイルの保持を継承します。NBT形式、座標、ディメンション、FULL状態、Entityと地形セクションを検証します。Minecraft・生成MOD・設定の変更は、その変更作業時に対応します。環境監視やMOD一覧・設定のハッシュ比較は行いません。

## 検証

プロジェクト内で `gradle check jar` を実行します。FULL形式・旧世代移行・中断された引渡し・保存ACK・容量制限・タイムアウト競合を検証します。
