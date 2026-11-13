# Maki Chunksmith Controller 1.4.2

Minecraft 1.21.1 / NeoForge 21.1.262 / Chunksmith 4.8.1用の生成制御MODです。プレイヤーの位置を中心とする事前生成タスクを、リソース使用量と処理実績に基づいて制御します。

## 生成制御

プレイヤー参加時は新規投入を止め、開始済みの要求を完了させます。LOD書込・FULL検証・FULL保存の工程ごとに処理中の件数（WIP）を制限し、完了件数を比較して生成並列数をc±1で調整します。CPU使用率、MSPT、GC、メモリ余力、保存キューと工程滞留を観測します。

LODを書き始める前に、Chunk CacheでFULL保存先の所有権を確認します。事前検査が3回続けて失敗した座標はWIPを解放して未完了として記録し、他の座標の処理を継続します。nativeの生成済み検査・チャンク要求が失敗した座標も未完了として保持します。`/makilod resume`はこれらの失敗が残る場合にChunksmithの通常走査を先頭から再開し、それぞれの処理の成功を確認して記録を解消します。nativeの仮LOD存在マークによるskipの制限は残ります。

FULL保存の連続失敗は未完了として記録し、実処理のない検証・保存WIPを解放します。Cacheによる同一FULLの保存成功またはプレイヤーへの引渡しで、この未完了記録を解消します。Chunksmithの要求待ちが終了しても、保存成功やPAIRとしては計上しません。

同一試行のLOD書込成功とFULL公開・再利用の確認が揃ったものをPAIR成果として一度だけ計上します。新規FULLの公開、既存FULLの再利用、分類不能の成果を分けて記録します。物理処理のWIPは実終了時に返します。

FULLキャッシュ使用量・保存待ちbytesとChunksmithのLOD保存量を合算し、`generation-max-storage-bytes`（既定15GiB）で生成受付を止めます。LODの容量はChunksmithの保存先を非同期で計測します。

設定は[config/maki-chunksmith-controller.properties](../../config/maki-chunksmith-controller.properties)を参照してください。旧`maki-lod-cpu`からの更新には[設定移行手順](../../docs/pr91-controller-config-upgrade.md)があります。

## 保存処理との連携

Chunksmithがチャンク生成とLODの生成・保存・再利用判断を担当し、[Chunk Cache](../maki-chunksmith-chunk-cache/README.md)が一時FULLの保存・復元・通常保存への引渡しを担当します。Controllerは工程の開始・終了、保存確定と失敗を観測して生成量を調整します。

LOD書込失敗は稼働中の未完了観測に残し、範囲終了時は`incomplete-pregen:<件数>`を表示します。存在マークの修復や再起動後の再試行状態はChunksmith側の課題です。[4.8.1のLOD経路確認](../../docs/chunksmith-4.8.1-native-lod-audit.md)に現構成の制限を記載しています。

## 検証

プロジェクト内で`gradle check jar`を実行します。並列数探索、工程別の完了通知、保存再試行、事前検査の停止と打切り、合算容量制限を検証します。
