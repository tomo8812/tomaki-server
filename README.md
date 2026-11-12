# Minecraft 1.21.1 NeoForge Server

## クライアント側に入れるMOD

このサーバーへ参加するクライアントは、以下のJARを `mods/` に入れてください。

```text
Eureka-VS2-Fabric-MC1.21.1-v3.2.0.jar
GoldenAgeCombat-v21.1.3-1.21.1-NeoForge.jar
Jade-1.21.1-NeoForge-15.10.6.jar
QuadHotbar-1.21.1-neoforge-1.1.0-beta1.jar
ValkyrienSkies-Fabric-MC1.21.1-v3.2.0-connectorfix7.jar
architectury-13.0.11-neoforge.jar
chunksmith-4.8.1+1.21.1-neoforge.jar
clientsort-neoforge-3.89.2+1.21.1.jar
connector-2.0.0-beta.17+1.21.1-full-keyfix1.jar
emi-1.1.24+1.21.1+neoforge.jar
forgified-fabric-api-0.116.15+2.3.6+1.21.1.jar
gravestone-neoforge-1.21.1-1.0.40.jar
letsdo-bakery-neoforge-2.1.11.jar
letsdo-brewery-neoforge-2.1.13.jar
letsdo-candlelight-neoforge-2.1.15.jar
letsdo-farm_and_charm-neoforge-1.1.29.jar
letsdo-herbalbrews-neoforge-1.1.4.jar
maki-airship-compat-1.0.13.jar
maki-airship-foundation-compat-1.0.0.jar
maki-diorama-share-1.0.1.jar
maki-ship-anchor-fix-1.1.2.jar
maki-ship-edge-1.0.0.jar
maki-ship-footsteps-1.0.1.jar
millenaire-9.1.0.jar
puzzleslib-v21.1.66-mc1.21.1+neoforge.jar
sound-physics-remastered-neoforge-1.21.1-1.5.2.jar
terrain_slabs-neoforge-3.1.2.jar
```

ここに記載していない `mods/` 内のJARは、クライアント側へ入れる必要はありません。


## サーバー側の自作MOD

サーバーでは `custom-mods/` の13プロジェクトから生成した次のJARを1つずつ `mods/` に配置します。通常運用の状態確認には `latest.log` を使用し、定期的なstatus JSONは使用しません。

```text
maki-airship-compat-1.0.13.jar
maki-airship-foundation-compat-1.0.0.jar
maki-assembly-tracking-1.0.0.jar
maki-chunksmith-chunk-cache-1.5.0.jar
maki-diorama-share-1.0.1.jar
maki-idle-pregen-1.1.2.jar
maki-chunksmith-controller-1.4.2.jar
maki-millenaire-lod-compat-1.0.9.jar
maki-mods-observer-1.0.0.jar
maki-ship-anchor-fix-1.1.2.jar
maki-ship-edge-1.0.0.jar
maki-ship-footsteps-1.0.1.jar
maki-ship-save-1.0.3.jar
```

`maki-lod-cpu` は `maki-chunksmith-controller` に改名されました。旧 `maki-lod-cpu-*.jar` と `maki-lod-cpu-online-fix-*.jar` は残さないでください。設定ファイルも `config/maki-chunksmith-controller.properties` に移行します。今回更新した `maki-lod-cpu-1.4.1.jar`、`maki-chunksmith-chunk-cache-1.4.0.jar`、`maki-chunksmith-chunk-cache-1.4.1.jar` と `maki-ship-anchor-fix-1.1.1.jar` も、新しいJARと重複して配置しないでください。クライアント側も旧 `maki-ship-anchor-fix-1.1.1.jar` を削除してください。

`maki-lag-observer` は `maki-mods-observer` に置き換えられ、`maki-status-io` は廃止されています。更新時は旧 `maki-lag-observer-*.jar`、`maki-status-io-*.jar`、`config/maki-lag-observer.properties` を残さないでください。詳細診断が必要な場合だけ `config/maki-mods-observer.properties` でObserverを有効化します。詳細診断は既定で無効で、設定を変更しない1セッションあたり既定30分、各stream 5000イベント、各file 16 MiBに制限されます。

Chunksmithのタスク・キャッシュ・カーソル、LOD、Diorama、ワールド内のMODデータ、移行バックアップ・監査データは運用データなので削除しません。

## Chunksmith 4.8.1 / Millénaire 9.1.0 更新

この組合せでは、サーバーとクライアント双方の Chunksmith / Millénaire を更新してください。サーバー上の自作MODは、上記の版へ **同時に** 更新します。古い同名MODのJARを残すと重複ロードになります。

Controllerはリソースに基づくChunksmithの生成制御、cacheは一時FULLの退避とプレイヤーロード時の引渡し、Millénaire互換modは村候補の保留と再投入を担当します。LOD生成・保存・再利用はChunksmithに委ねます。cacheによるLOD走査・Backfill生成・環境監視、Millénaire互換modによるLOD更新・チャンク保存変更は撤去しています。既存のワールドとChunksmithのLOD/保存済みデータは削除しないでください。まずワールドをバックアップして、新規生成・村候補の保留と再投入・FULL/LOD保存・船舶保護を確認してください。

Millénaire 9.1.0 の村は、プレイヤーが周辺へ来て実FULLチャンクが読み込まれ、建築に必要な全領域で自作キャッシュMODから通常保存への引渡しが完了した後に建築します。事前生成時の地形調査は保持し、キャッシュ復元済み・通常生成済みのいずれもMinecraftがFULLを読み込んでから判定します。

再現可能なJAR取得とハッシュ検証は `scripts/sync-upstream-481-910.sh` を使用します。

現構成のController・FULL cache・Millénaire互換MODは必須依存として直接連携します。村候補の再投入はnativeキューの空きと再試行待ちだけを扱い、Chunksmithの並列数・LODキュー・独自MSPT/GC判定には依存しません。
