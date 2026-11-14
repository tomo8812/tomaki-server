# クライアント用MOD

このフォルダの `.jar` ファイルを、Minecraft 1.21.1 / NeoForge 21.1.262のクライアント側 `mods/` に配置します。`README.md` と `shared-mods.txt` をコピーする必要はありません。リポジトリ直下の `mods/` はサーバー用です。

配布セットは共通MOD33個とクライアント専用MOD14個の計47個です。Diorama・共有MOD・カメラ補正MODは更新版へ揃え、その他は既存クライアント構成の版を維持しています。`.connector/` は生成キャッシュのため管理しません。

Voxyは同梱のRoxyが変換して読み込む構成です。`voxy-0.2.16-beta+1.21.11.jar` はファイル名やFabricの対応範囲だけで別のJARへ交換せず、Roxy・Sodium・Irisと一緒に管理します。船対応修正版のCool RainとRoxyも同梱のものを使用します。

更新時はゲームを終了し、旧版JARを削除してから新版を配置してください。設定や保存済み地図、ワールドは削除しません。Diorama 0.5.0、Maki Diorama Sharing 1.0.1、Maki Diorama Camera Alignment 1.0.0は、新版と重複させないでください。

`shared-mods.txt` はサーバー配布フォルダと同じファイルを使うMODの一覧です。追加のクライアント専用MODは、このフォルダだけに配置します。共通MODの更新後は、リポジトリのルートで次を実行してコピーを揃えます。

```bash
python3 scripts/sync-client-mods.py --sync
python3 scripts/sync-client-mods.py --check
```

Dioramaの地形補完・Web地図・共有履歴の設定は[更新手順](../custom-mods/maki-diorama-share/README.md)を参照してください。
