# Chunksmith 4.8.1：E-lite生成制御の互換性対応

確認日：2026-11-12

## 調査対象と結論

調査対象は **Maki Chunksmith Controller** の制御機能だけ。ChunksmithのFULL・LOD保存形式、chunk-cache、PR #90の保存ロジックは変更しない。

- [Chunksmith `v4.6.0`](https://github.com/Kishku7/Chunksmith/tree/v4.6.0)と[`v4.8.1`](https://github.com/Kishku7/Chunksmith/tree/v4.8.1)について、Controllerが参照する`GenerationTask`、`Selection`、`LodHorizon`、`CsLodPresenceIndex`、`CsLodStoreSink`、`World`、`Chunksmith`、`ChunksmithProvider`、`ChunkResidency`の9ソースファイルを全文比較し、すべて同一であることを確認。
- 旧来の並列数キャップ・tick health測定・ネイティブ負荷制御・Follow停止再開・Backfill予算の注入先と制御方式は継続使用する。
- **新たにPR #89のE-liteが入れた生成投入フックの設計に不具合があったため、4.8.1の全ネイティブ経路を維持するよう修正した。**
- 既存の`Selection.horizon(1)`は4.6.0と4.8.1で同じ意味。今回の「バージョン対応」では変更していない。保存範囲の変更は別途の仕様変更として扱う。

## 既存の生成制御の互換性

| Controller機能 | 4.8.1側の対象 | 結果 |
| --- | --- | --- |
| 生成幅制限 | `GenerationTask.dispatchLimit`, `rampUp(boolean)` | 上流API・実装同一 |
| native inFlight、待ち・書込圧力 | `inFlight`, `adjustFromTickHealth()`, `writeQueueStalled`等 | 上流API・実装同一 |
| 成功/失敗を含むチャンク待ち時間 | `adjustFromChunkLatency(long)` | 上流API・実装同一 |
| Chunksmith側タスク開始/停止 | `run()`, `stop(boolean)`, `getGenerationTasks()` | 上流API・実装同一 |
| プレイヤー参加時停止・再開 | `PlayerCenter`のFollow-owned判定とnative stop | Controllerの既存規則を維持 |
| Backfillとの同時負荷制御 | `ChunkIterator.next()`への待機フック | 4.8.1で注入先あり |
| NEW/REPAIR観測 | `World.isChunkGenerated`および非照会経路 | 下記の改修を適用 |
| 工程別WIP制御 | E-lite `PregenFlowController` | 下記の改修を適用 |

## 実装した制御修正

### 1. WIP予約を`ChunkIterator.next()`へ移動

Chunksmithが次の座標を選んだ直後にE-liteの候補WIP枠を予約し、満杯の場合は**座標を保持して待機**する。

これにより、`isChunkGenerated`を呼ばない次の経路も同じ投入制御の対象になる。

- 既存FULLのLOD補完（`lodOnly`）
- `forceLoadExistingChunks`による強制読込
- 新規生成（従来どおり）
- 生成済みFULL＋LODのスキップ（native `update`で予測WIPを返す）

`World.isChunkGenerated`のRedirectは、既存の判定Futureに結果分類の通知だけを追加する。元の判定を偽装しない。

### 2. `getChunkAtAsync`で失敗Futureを偽造しない

Chunksmithは`CsLodPresenceIndex.markLod`を実要求の直前に実行する。`getChunkAtAsync`側でE-liteが`failedFuture`を返すと、保存されていないLODがインデックス上では存在するように見える。

このため実要求側は、試行を追跡できない場合でも**nativeの`getChunkAtAsync`をそのまま呼ぶ**。新たな仕事量の制限は必ず前段のiteratorフックで行う。

### 3. 不要な旧再試行方式を撤去

E-liteが投入拒否のために生成した失敗Future、`REJECTED_UPDATES`、`DEFERRED`、座標の更新キャンセル、再試行待機台帳を撤去。nativeの停止・再開・進捗処理をそのまま使用する。

本来の生成処理による非同期失敗やNative安全停止はChunksmithが処理する。Controllerは生成要求を失敗として偽装しない。

### 4. 計測できない仕事をNEW/REPAIRと偽称しない

既存FULLのLOD補完・強制読込のように`isChunkGenerated`を呼ばない場合は`UNKNOWN`のまま管理し、誤って`REPAIR`の件数へ加算しない。FULL/LOD両方の物理完了が取れた場合のPAIR goodputは計上できるが、未知の仕事分類を性能プロファイルの比較に混入させない。

## バイナリ／テスト

- Controllerの`build.gradle`で、`compileOnly`と`verifyIssue66ChunksmithBytecode`を4.8.1 JARに切替。
- PR #89のsource-build CIは、PR #90の既知HEAD（`4b7d69209415d0509abab25bcc71a8e27181996f`）の4.8.1 JARをワークフロー中に取得する。4.6.0へ黙ってフォールバックしない。
- Mixinのcallsite（`next`, `isChunkGenerated`, `getChunkAtAsync`, `markLod`）および生成制御の注入有無をGradle検査する。
- ソースのコンパイルとバイトコードの静的確認は、実サーバーのMixin変換・生成進捗・停止再開を完全には証明しない。実機確認はPR #90統合時に実施する。

## PR #90統合で別途必要な連携

工程別WIP枠を実際の保存処理完了で補充するためのFULL／LOD物理ACKはPR #89単体では未結線。PR #90のcache HEADで必要なFULLだけを報告する構成とし、ChunksmithのLOD通知はControllerが担当する。

また、cacheの既存`GenerationRequestMixin`とControllerの`@Redirect`が同じネイティブcallsiteを対象にするため、PR #90統合時にフックを一本化する。PR #89のchunk-cache差分は0件のまま維持する。

参照：[PR #90連携契約](e-lite-pr90-cache-integration.md)。
