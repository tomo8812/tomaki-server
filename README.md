# Minecraft 1.21.1 NeoForge Server

## クライアント側に入れるMOD

このサーバーへ参加するクライアントは、以下のJARを `mods/` に入れてください。

```text
Eureka-VS2-Fabric-MC1.21.1-v3.2.0.jar
GoldenAgeCombat-v21.1.3-1.21.1-NeoForge.jar
Jade-1.21.1-NeoForge-15.10.6.jar
NaturesCompass-1.21.1-3.4.0-neoforge.jar
QuadHotbar-1.21.1-neoforge-1.1.0-beta1.jar
ValkyrienSkies-Fabric-MC1.21.1-v3.2.0-connectorfix7.jar
architectury-13.0.11-neoforge.jar
chunksmith-4.8.1+1.21.1-neoforge.jar
clientsort-neoforge-3.89.2+1.21.1.jar
diorama-0.5.0+neoforge-1.21.1.jar
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

サーバーでは `custom-mods/` の12プロジェクトから生成した次のJARを1つずつ `mods/` に配置します。通常運用の状態確認には `latest.log` を使用し、定期的なstatus JSONは使用しません。

```text
maki-airship-compat-1.0.13.jar
maki-airship-foundation-compat-1.0.0.jar
maki-assembly-tracking-1.0.0.jar
maki-diorama-share-1.0.1.jar
maki-idle-pregen-1.2.0.jar
maki-chunksmith-controller-1.5.0.jar
maki-chunksmith-fixes-1.0.0.jar
maki-mods-observer-1.0.0.jar
maki-ship-anchor-fix-1.1.2.jar
maki-ship-edge-1.0.0.jar
maki-ship-footsteps-1.0.1.jar
maki-ship-save-1.0.3.jar
```

`maki-lod-cpu` は `maki-chunksmith-controller` に改名されました。旧 `maki-lod-cpu-*.jar` と `maki-lod-cpu-online-fix-*.jar` は削除し、設定を `config/maki-chunksmith-controller.properties` に移行します。クライアント側も旧 `maki-ship-anchor-fix-1.1.1.jar` を残さないでください。

`maki-lag-observer` は `maki-mods-observer` に置き換えられ、`maki-status-io` は廃止されています。更新時は旧 `maki-lag-observer-*.jar`、`maki-status-io-*.jar`、`config/maki-lag-observer.properties` を残さないでください。詳細診断が必要な場合だけ `config/maki-mods-observer.properties` でObserverを有効化します。詳細診断は既定で無効で、設定を変更しない1セッションあたり既定30分、各stream 5000イベント、各file 16 MiBに制限されます。

## 通常保存の事前生成とDiorama

Chunksmithは実チャンクとLODを生成します。Controllerの追従タスクは `horizon=0` で動き、実チャンク・Entity・POIを通常ワールドへ保存します。Epic TerrainやMillénaireなどの地形・構造物生成MODは本体の生成処理を使用します。

Chunksmith Fixesは4.8.1のLOD存在判定・変更済みチャンクのLOD更新・取得失敗通知を修正します。生成タスクの管理と負荷制御はControllerが担当します。

無人の事前生成中はIdle PregenがMillénaireの村の活動・成長・改築、村人の動作、VS物理時間を停止します。ワールド時刻は通常どおり進め、新しい村の候補探索・生成と実チャンクの生成・保存を継続します。

Diorama 0.5.0の全体マップ（Mキー）とミニマップは、プレイヤーのクライアントが受け取った実チャンクから作ります。サーバーの事前生成データやCSLODを先行配信する機能は追加しません。ChunksmithのLOD保存と、Dioramaの表示入力は別の経路です。

### 既存サーバーの切替

1. サーバーを停止します。旧`maki-lod-cpu`から更新する場合は、ファイルを更新する前に[設定移行手順](docs/chunksmith-controller-config-upgrade.md)の旧設定退避を実行します。
2. 配布する自作MODを上記の版へ揃え、設定移行を完了します。旧Controller・旧Idle Pregenを残さないでください。
3. `maki-chunksmith-chunk-cache-*.jar`、`maki-millenaire-lod-compat-*.jar`、`config/maki-chunksmith-chunk-cache.properties`を削除します。
4. ワールドの場所を指定して `python3 scripts/remove-temporary-pregen-data.py /path/to/world` を実行します。旧キャッシュ全体と独自の村候補記録を移行せず削除します。キャッシュにしかなかったFULLは、通常生成へ戻ります。
5. 通常ワールドの `region`・`entities`・`poi`、建築済みの村、ChunksmithのLOD、Dioramaの地図履歴は保持します。旧horizonタスクは引き継がず、通常保存の追従タスクを開始します。

旧`maki-lod-cpu`からの設定移行には[設定移行手順](docs/chunksmith-controller-config-upgrade.md)を使用します。Controller設定のキャッシュ用 `generation-max-storage-bytes` は廃止しています。

役割と検証は[Controller](custom-mods/maki-chunksmith-controller/README.md)、[Chunksmith Fixes](custom-mods/maki-chunksmith-fixes/README.md)、[Idle Pregen](custom-mods/maki-idle-pregen/README.md)、[Diorama共有](custom-mods/maki-diorama-share/README.md)に記載しています。再現可能な上流JAR取得は `scripts/sync-upstream-481-910.sh` を使用します。
