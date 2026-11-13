# Chunksmith 4.8.1のLOD経路確認

確認対象は`mods/chunksmith-4.8.1+1.21.1-neoforge.jar`と、上流の[`v4.8.1`](https://github.com/Kishku7/Chunksmith/tree/v4.8.1)です。JARのSHA-256は`bf6371873cb78f2a7645001eb1ef0f7f6d7e5e80c51a52667b7d37ee898888ca`です。配布JARを逆コンパイルし、プラットフォーム共通のタグ付きソースと、実際のNeoForge用Mixin・`NeoForgeWorld`の経路を照合しました。

現設定は`forceLoadExistingChunks=false`、`lodFromExploredChunks=true`、Controllerの`horizon=1`です。

| 確認事項 | 現構成での結果 | 条件・限界 |
| --- | --- | --- |
| 村の増改築後の既存LOD更新 | 通常保存・アンロードでは自動更新しない | 本体の保存形式は上書き可能。既存LODをスキップする入口の規則が原因 |
| LOD書込失敗後の再試行 | writerは失敗レコードを再投入せず、投入時の存在ビットも戻さない | 同一インデックスにビットが残る間の問題。再起動・LRU追い出し後の再読込では、未保存なら欠落を認識できる |
| LODだけ残ったHorizon座標のFULL補完 | LODありの時点でスキップし、FULL欠落を検査しない | 本体のLOD専用リングの仕様。FULLキャッシュの欠落を補完する経路がない |

## 村の増改築と既存LOD

NeoForgeの`ChunkMapHorizonSaveMixin`は保存時に`ExploredLodGuard.onSave()`を呼び、`ServerLevelExploredLodMixin`はアンロード時に`onUnload()`を呼びます。両方とも最終的に同じ`take()`へ進みます。

[`ExploredLodGuard`](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/_codegen/cog_sources/lod/ExploredLodGuard.java#L92)は`presence.hasLod(x,z)`を渡し、[`ExploredLod.shouldTake()`](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/lod/ExploredLod.java#L59)は`!storeHasIt`を要求します。FULLがdirtyでも既存LODがあれば`LodSupport.offer()`を呼びません。村だけでなく、通常の建築変更にも同じ規則が適用されます。

一方、`LodSupport.offer()`自身は既存LODを拒否せず、`CsLodRegionStore.write()`も同一座標のヘッダを新しいレコードへ付け替えられます。通常プレイでの既存LOD更新には、変更されたFULLを再キャプチャする経路が必要です。

[`GenerationTask`](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/GenerationTask.java#L1068)には`forceLoadExistingChunks`で既存座標も再処理する経路があります。ただし現在はfalseで、trueにすると同じ`run()`内でLOD horizonが利用不可と判定されます。現在のHorizon生成と併用する自動更新策にはなりません。

## LOD書込失敗

[`GenerationTask`](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/GenerationTask.java#L1275)は非同期チャンク要求より前に`markLod()`します。`NeoForgeWorld.getChunkAtAsync()`のFutureはFULLの取得とLODへのofferを待ちますが、非同期writerのディスク保存成功は待ちません。

[`CsLodStoreSink.persist()`](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/lod/CsLodStoreSink.java#L116)は`store.write()`の`IOException`をログにして終了します。失敗レコードをキューに戻さず、存在インデックスを無効化しません。`GenerationTask.whenComplete()`も成否によらず座標進捗を更新します。

[`CsLodPresenceIndex`](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/lod/CsLodPresenceIndex.java#L131)のビットは投入時に立ちます。インデックスはディメンション単位でサーバー稼働中共有されます。`invalidate()`は存在しますが、配布JAR内にその呼出しはありません。

ただし、ビットマップは256regionのLRUです。追い出し後や再起動後はディスクのヘッダを読み直すため、保存されなかったLODを欠落として認識し直せます。存在検査はヘッダに基づくため、レコード本体の破損の検証は別に必要です。

## LODだけ残ったHorizon座標

[`GenerationTask.run()`](https://github.com/Kishku7/Chunksmith/blob/v4.8.1/shared_common/src/main/java/com/kishku7/chunksmith/GenerationTask.java#L1122)の順序は次のとおりです。

```java
if (ring && lodIndex.hasLod(x, z)) {
    // skip and update progress
} else {
    // normal generated check and getChunkAtAsync
}
```

この判定には通常FULLや自作FULLキャッシュの存在確認がありません。LOD保存済み・FULL未公開でクラッシュした場合、再起動してもLODヘッダが残れば同じ分岐に入り、キャッシュ読込や生成要求へ到達しません。自作MODの保存要求・完了Futureがプロセスをまたいで復元される経路も現実装にはありません。

上流ソースの同じ箇所には、リングのFULLは保存しないためLODを判定の基準にする、という意図が明記されています。LOD専用の事前生成では、このスキップによって再生成を省けます。一方、FULLキャッシュも必要な構成では、LODだけ残った座標のFULLを補完する連携が必要です。`CsLodBackfill`もクライアントへのLOD送信範囲を広げる状態管理であり、FULLを生成する復旧器ではありません。

## 配布JARでの再現確認

JDK 21で配布JARの実クラスを呼び出し、以下を確認しました。

- dirty・FULL・LOD有効の条件を同じにして、`ExploredLod.shouldTake()`が既存LODを拒否し、欠落LODだけを許可することを確認。
- `CsLodRegionStore.write()`で同じ座標に異なるblock paletteを保存し、読込が後のレコードを返すことを確認。
- LODだけを書いた一時ディレクトリから新しい`CsLodPresenceIndex`を作り、FULLなしでもHorizonのskip条件が成立することを確認。
- 保存先を通常ファイルにして`persist()`へ確実に`IOException`を起こし、成功件数0・再試行キュー0・既存インデックスのビットはtrueのままであることを確認。新しいインデックスと明示的な`invalidate()`ではfalseへ戻ることも確認。

実NeoForgeサーバーでの村建築・電源断試験は未実施です。
