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
chunksmith-4.6.0+1.21.1-neoforge.jar
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
maki-ship-anchor-fix-1.1.1.jar
maki-ship-edge-1.0.0.jar
maki-ship-footsteps-1.0.1.jar
millenaire-9.0.3.jar
puzzleslib-v21.1.66-mc1.21.1+neoforge.jar
sound-physics-remastered-neoforge-1.21.1-1.5.2.jar
terrain_slabs-neoforge-3.1.2.jar
```

ここに記載していない `mods/` 内のJARは、クライアント側へ入れる必要はありません。


## サーバー側の自作MOD

サーバーでは `custom-mods/` の14プロジェクトから生成した次のJARを1つずつ `mods/` に配置します。通常運用の状態確認には `latest.log` を使用し、定期的なstatus JSONは使用しません。

```text
maki-airship-compat-1.0.13.jar
maki-airship-foundation-compat-1.0.0.jar
maki-assembly-tracking-1.0.0.jar
maki-chunksmith-chunk-cache-1.4.2.jar
maki-diorama-share-1.0.1.jar
maki-idle-pregen-1.1.1.jar
maki-lod-cpu-1.4.1.jar
maki-lod-cpu-online-fix-1.0.3.jar
maki-millenaire-lod-compat-1.0.6.jar
maki-mods-observer-1.0.0.jar
maki-ship-anchor-fix-1.1.1.jar
maki-ship-edge-1.0.0.jar
maki-ship-footsteps-1.0.1.jar
maki-ship-save-1.0.2.jar
```

`maki-lag-observer` は `maki-mods-observer` に置き換えられ、`maki-status-io` は廃止されています。更新時は旧 `maki-lag-observer-*.jar`、`maki-status-io-*.jar`、`config/maki-lag-observer.properties` を残さないでください。詳細診断が必要な場合だけ `config/maki-mods-observer.properties` でObserverを有効化します。詳細診断は既定で無効で、設定を変更しない1セッションあたり既定30分、各stream 5000イベント、各file 16 MiBに制限されます。

Chunksmithのタスク・キャッシュ・カーソル、LOD、Diorama、ワールド内のMODデータ、移行バックアップ・監査データは運用データなので削除しません。
