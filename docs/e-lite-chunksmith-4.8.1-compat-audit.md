# Chunksmith 4.8.1 / E-lite（PR #89）統合前互換性監査

確認日：2026-11-12

対象：
- E-lite PR #89（本監査開始時のHEAD：`b1e2d331f61bd9c4150a8bd791ff964068077a0a`）
- Chunksmith 4.8.1（上流 `Kishku7/Chunksmith` の `v4.8.1` タグ）
- PR #90のChunksmith 4.8.1バイナリと静的検査（HEAD `4b7d69209415d0509abab25bcc71a8e27181996f`）

**判定：現行E-liteは4.8.1で正常に動作すると判断できない。以下のコード経路は修正が必要。** PR #89のchunk-cache変更は0件のままにして、E-liteの変更とPR #90のcache側連携を別々に行う。

## 注入先・API互換性（静的確認）

4.8.1の`GenerationTask`ソースに存在するもの：
- `run()`, `stop(boolean)`, `adjustFromTickHealth()`, `rampUp(boolean)`, `adjustFromChunkLatency(long)`
- `update(int,int,boolean,boolean)`（Java記述子`(IIZZ)V`）
- `dispatchLimit`、`inFlight`、`selection`、`writeQueueStalled`、`chunkResidencyStalled`、`heapStalled`、`stopped`の各フィールド
- `run()`内の`ChunkIterator.next()`と`World.isChunkGenerated(int,int)`
- `run()`の非同期ラムダ内に`World.getChunkAtAsync(int,int)`の呼び出しが2か所

PR #90の`.github/scripts/verify_upstream_481_910.py`は、**配布JAR**の`lambda$run$1`中の`World.getChunkAtAsync`呼び出し2か所を検証している。PR #90のMixin static checksは成功している。ただしPR #89の追加Mixinを4.8.1配布JARで実際に変換・起動したわけではない。PR #89のbuild.gradleはまだ4.6.0 JARを`compileOnly`としている。

## P0-1: `Selection.horizon(1)`によりほぼ全域がLOD-onlyになる

PR #89 `PlayerCenter.java` は新タスク構築時に `.horizon(1)` としており、タスク一致・admissionの判定でも `selection.horizon()==1` を要求している。

Chunksmith 4.8.1の`LodHorizon.horizonChunks(horizonBlocks)`は`ceil(horizonBlocks/16)`、`isBeyond`はさらに`SEAL_CHUNKS=2`を足す。つまり`horizon(1)`は中心から半径約3チャンク以外をLOD-onlyにする（円形なら半径3チャンクで判定）。`GenerationTask.run`は`ring`の場合に`lodOnlyRing`を設定し、`update(..., persisted=false)`を呼ぶ。

**結果：** FULL＋LODペアを生成するつもりの先行生成が、中心付近以外でFULLを保存しない。LOD-onlyが利用不能ならタスク自体を停止する可能性もある。

**修正条件：** FULLを保存する選択では`horizon(0)`（境界無効）を使い、admission/選択比較も一貫して変更。Chunksmithの通常LOD書込は別の機能であることを確認。既存選択の復元・移行時には意図せぬLOD-onlyタスクを再開しない。

## P0-2: 既存FULLのLOD補完・強制読込経路で投入を拒否する

4.8.1の`GenerationTask.run`は`forceLoadExistingChunks || lodOnly`のとき `World.isChunkGenerated`を**呼ばずに**`completedFuture(false)`で進む。既存FULLにLODがない`lodOnly`は通常経路であり、`getChunkAtAsync`へ進む。

PR #89 `GenerationTaskMixin.maki$admitNativeCandidate`は`World.isChunkGenerated`呼び出しだけをフックする。一方 `maki$admitNativeRequest`は`PregenControlHooks.beginActual`へ進み、`PregenFlowController.beginClassified`が以前の候補`Attempt`を要求する。存在しなければ`false`を返す。

**結果：** LOD補完の呼び出しが`IllegalStateException("E-lite actual WIP admission rejected")`で失敗。強制読込でも同じ問題。

**修正条件：** `isChunkGenerated`を経由しないネイティブ経路にも試行を作る。NEW/REPAIRはChunksmithの経路・結果で分類し、cacheの有効性判定に介入しない。

## P0-3: 拒否時にLODの存在インデックスが偽陽性になる

4.8.1の`GenerationTask.run`は`getChunkAtAsync`直前に`CsLodPresenceIndex.markLod(x,z)`を呼び出す。これは非同期書込前にビットを立てる仕様。

PR #89は**その後**の`getChunkAtAsync`呼び出しに対してE-liteの入場可否を判定し、WIP枠不足なら`failedFuture`を返す。この時点でLODインデックスは存在扱いになっている。E-liteの`PregenFlowController.defer`は独自の再試行負債を記録するが、Chunksmithの`CsLodPresenceIndex`の偽陽性を解消しない。

**結果：** 失敗・未保存座標を再走査しても、同一サーバー稼働中の`hasLod`でスキップしうる。

**修正条件：** LOD予約・生成開始を拒否する場合は、`markLod`より前で止める。後段で拒否する設計を採用しない。LODインデックスはChunksmithの所有物とし、cacheにLOD判定を代行させない。

## P1-4: 工程別WIPのACKが接続されていない（PR #89単体の既知の未完成）

`PregenFlowController`はNEW候補にLOD、VALIDATE、FULLの3枠を先行予約する。FULL初期窓は2件。`physicalTerminal`、`stageUnavailable`、`lodCommitted`、`fullReady`等が適切に発火しないと、枠が戻らず`admissionBlocked()`がtrueのままになる。現在のPR #89はchunk-cacheをmainに復元しており、FULL通知は未実装。LOD物理完了の新規観測も未結線。

**結果：** 現行PR #89単体では新規生成が少数の要求で止まる。これは4.8.1固有のABI問題とは別。

**修正条件：** PR #90統合時に、実際に始まった物理仕事だけをterminalで解放し、不必要なステージは明示的に返却。LOD sinkはChunksmith、FULLキャッシュはcacheからそれぞれ読み取り専用で通知。欠損した計測で旧FULL/LODの再利用を禁止しない。

## P1-5: PR #90のcache Mixinと同一callsiteに重複`@Redirect`

PR #90の`maki-chunksmith-chunk-cache/mixin/GenerationRequestMixin`が`GenerationTask.run/World.isChunkGenerated`と`lambda$run$1/World.getChunkAtAsync`を`@Redirect`している。PR #89のE-liteも同じ箇所へ`@Redirect`する。

**結果：** このまま両JARを同時ロードするとMixin衝突・片方のフック欠落・起動失敗の危険が高い。

**修正条件：** Chunksmithの生成投入制御はE-liteの1か所に集約。cacheのFULL保存・読込・正常ワールドへの引渡しガードは独立フックへ再配置。PR #90のキャッシュHEAD上で実施する。

## 次の受入試験

1. E-liteをChunksmith 4.8.1配布JARでコンパイルし、実際のMixinを適用したNeoForgeサーバーで起動する。
2. 新規座標でFULLとLODの双方が保存される（LOD-onlyにならない）。
3. 既存FULL・LODなしの座標でLODだけ補完する。既存FULL・LODを壊さない。
4. `forceLoadExistingChunks`が有効でも無意味なAdmission拒否をしない。
5. WIP枠満杯・非同期失敗・停止／再開時に未保存LODを生成済みとして飛ばさない。
6. ALLステージのcreditを物理terminal／明示的不要確定で正しく一度だけ返す。
7. 同じ2つのcallsiteに`@Redirect`を重ねない。
8. 他MODによる生成タスクをE-liteが管理せず、プレイヤー参加時はFollow-ownedの新規先行生成を停止する。
9. PR #90の旧FULLを再利用し、ペア計測の証明不足を理由に隔離しない。

上流の根拠：
- [Chunksmith v4.8.1 GenerationTask.java](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/GenerationTask.java)
- [Chunksmith v4.8.1 LodHorizon.java](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/lod/LodHorizon.java)
- [Chunksmith v4.8.1 CsLodPresenceIndex.java](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/lod/CsLodPresenceIndex.java)
- [PR #90 upstream bytecode verifier](https://github.com/tomo8812/tomaki-server/blob/compat/chunksmith-4.8.1-millenaire-9.1.0/.github/scripts/verify_upstream_481_910.py)
