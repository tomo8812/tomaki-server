# Maki Diorama Sharing 1.0.1

Diorama 0.5.0 / Minecraft 1.21.1向けのウェイポイント・死亡地点共有MODです。地形・実チャンク・LODを追加配信する機能はありません。

## 地図に使うチャンク

Diorama 0.5.0は `MapSession` でクライアントの `ClientChunkCache` から取得済みFULLだけを `ChunkSnapshot` にします。取得時のcreate引数はfalseで、未到達チャンクの生成やサーバーの保存済みregion取得を要求しません。保存した地図履歴もこの入力から作られます。

Mキーの全体マップとミニマップは、どちらも同じ `MapSession.map()` を `MapViewport.draw()` に渡します。したがって、サーバーでChunksmithが事前生成しただけのチャンクは両方とも表示対象になりません。プレイヤーが実際にロードした範囲の地図履歴は残ります。

Chunksmithの保存するCSLODはDiorama 0.5.0の入力ではありません。この構成ではDiorama用のLOD配信器や実チャンク先行配信器を追加しません。通常のMinecraftプレイに必要な実チャンク通信は維持します。

## 検証

`gradle check jar` は共有状態の回帰テストに加え、依存先Diorama本体のバイトコードを調べます。3つのチャンク取得経路がclientの取得済みFULLに限定されること、全体マップ・ミニマップが同じ地図入力を使うことを検証します。
