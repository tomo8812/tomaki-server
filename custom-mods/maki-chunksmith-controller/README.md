# Maki Chunksmith Controller 1.5.0

Minecraft 1.21.1 / NeoForge 21.1.262 / Chunksmith 4.8.1用の生成制御MODです。プレイヤーの位置を中心とする事前生成タスクを、リソース使用量と処理実績に基づいて制御します。

## 通常ワールドへの生成

追従タスクは `horizon=0` で実行します。Chunksmithが実チャンクとLODを生成し、実チャンク・Entity・POIはMinecraftの通常保存経路に任せます。Epic TerrainやMillénaireなどの地形・構造物生成MODは通常の生成経路で動作します。

無人時に生成し、プレイヤー参加時は新規投入を止めて開始済み要求を完了させます。CPU、MSPT、GC、メモリ余力、native保存キューと工程滞留を観測し、並列数をc±1で調整します。FULL取得・生成とLOD書込の工程別WIPを制限します。

同一試行のnative FULL取得成功とLOD書込ACKが揃ったものをPAIR成果として一度だけ計上します。新規生成・既存FULLのLOD補完・分類不能を区別します。この計測は通常ワールドのディスク保存ACKではありません。通常保存の負荷はChunksmith本体の書込キュー制御も使用します。

タスク終了時は処理枠を回収し、開始済みFULLから遅れて投入されるLODも、元の試行IDで結果通知まで追跡します。最後の工程進捗またはタスク終了から60秒経過した観測は打ち切り、計測用の予約枠を解放します。Minecraftの処理やFutureは中断しません。古い通知による二重計上を防ぎ、古いLODの破棄はPAIRに数えません。

修正MODから受け取った既存LOD更新タスクも、リソースと共有並列枠に従って投入し、完了まで枠を保持します。この更新はプレイヤー参加中も実行できます。対象座標、変更回数、保存完了後の対象解消、失敗時の再試行は修正MODが管理します。Controllerは新規LODの失敗リストや独自の再走査を持ちません。

ワールド保存先の空き容量を5秒間隔で確認し、5GiB未満で事前生成を停止、8GiB以上で自動再開します。投入済み処理とMinecraftの通常保存は継続します。`disk-pause-free-gib`と`disk-resume-free-gib`で閾値を指定します。

`ioThrottle=false`でも負荷回復後に並列数を戻し、nativeキューと安全上限を適用します。[Chunksmith Fixes 1.0.0](../maki-chunksmith-fixes/README.md)から書込結果の通知を受け取り、処理枠とPAIR成果を計測します。LODの存在判定と更新は修正MODが担当します。Dioramaは受信した実チャンクから地図を更新します。

`/makilod resume`は稼働中なら候補を失効させず終了します。並列数の上限と観測はChunksmithの`ioThrottle`設定に依存せず適用します。

設定は[config/maki-chunksmith-controller.properties](../../config/maki-chunksmith-controller.properties)を参照してください。旧`maki-lod-cpu`からの更新には[設定移行手順](../../docs/chunksmith-controller-config-upgrade.md)があります。キャッシュ用の合計15GiB受付制限は廃止しています。

## 検証

プロジェクト内で `gradle check jar` を実行します。c±1探索、更新タスクの投入枠、native FULL・LODの独立した完了順序、失敗時のWIP解放、同一試行の成果計測、上流の注入先とMixin注釈を検証します。

Controller単独の`gradle check jar`は、`mods/maki-chunksmith-fixes-1.0.0.jar`をコンパイル入力に使用します。Fixesのビルドディレクトリは不要です。
