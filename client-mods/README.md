# クライアント用MOD

このフォルダの `.jar` ファイルを、Minecraft 1.21.1 / NeoForge 21.1.262のクライアント側 `mods/` に配置します。`README.md` と `shared-mods.txt` をコピーする必要はありません。リポジトリ直下の `mods/` はサーバー用です。

更新時はゲームを終了し、旧版JARを削除してから新版を配置してください。設定や保存済み地図、ワールドは削除しません。Diorama 0.5.0、Maki Diorama Sharing 1.0.1、Maki Diorama Camera Alignment 1.0.0は、新版と重複させないでください。

`shared-mods.txt` はサーバー配布フォルダと同じファイルを使うMODの一覧です。追加のクライアント専用MODは、このフォルダだけに配置します。共通MODの更新後は、リポジトリのルートで次を実行してコピーを揃えます。

```bash
python3 scripts/sync-client-mods.py --sync
python3 scripts/sync-client-mods.py --check
```

Dioramaの地形補完・Web地図・共有履歴の設定は[更新手順](../custom-mods/maki-diorama-share/README.md)を参照してください。
