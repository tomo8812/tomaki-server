# Chunksmith ControllerとFULLキャッシュの連携

Chunksmith 4.8.1 / Millénaire 9.1.0を対象とします。

| 主体 | 責務 |
| --- | --- |
| Chunksmith | チャンク生成、LOD生成・保存・再利用判断、native安全制御 |
| Chunksmith Controller | リソース・処理実績に基づく生成投入と並列数、停止・再開、FULL＋LOD容量の観測 |
| Chunk Cache | 一時FULLの退避・保存・検証・読込・通常保存への引渡し |
| Millénaire互換mod | 事前生成時の村候補保留とプレイヤーロード時の再投入 |

## 通知

cacheは `ResourceBudgetApi.attemptToken(dimension,x,z,owner)` で今回の試行tokenを取得し、VALIDATE/FULLの `stageStarted` / `stageFinished` と `fullPublished` / `fullReused` / `captureFailed` を通知します。実処理の終了と論理タイムアウトを区別し、開始済み処理のcreditは実終了まで保持します。

ControllerはChunksmithのnative LOD offerとwriterを観測し、recordの同一性で完了を照合します。LODへの要求追加や既存LODの判定上書きは行いません。FULLの新規公開・既存利用を区別し、LODの物理完了と同一試行で一度だけPAIR成果へ計上します。計測情報の不足は既存データの利用可否に影響しません。

## 注入点と所有権

`GenerationTask`の生成投入フックはControllerに統一します。cacheのFULL所有権確認はそのフックから専用APIへ委任します。非同期処理のownerはnative ticket safe pointにも引き継ぎます。

cacheは自分のFULL保存・読込内部で所有権を確認します。Millénaire互換modはロード済みFULLとcacheの読取専用 `playerLoadReady` を確認します。引渡しはcache自身のtick・load・save経路が担当します。

## 既存データ

固定ID `f00dcace0000000000000003`、旧FULL移行、schema 3の読込を維持します。旧公開完了印 `lodCommitted` をFULL専用の `fullCommitted` とともに受け入れます。LODの再利用判断はChunksmithに委ねます。環境監視、生成MODの互換性推測、初回起動時のデータ削除はありません。

## 受入条件

- 旧FULLと旧LODを削除せず利用できること。
- cacheがLOD判定を上書きせず、FULLの公開をLOD完了待ちにしないこと。
- 通知の遅延・逆順・重複・失敗でWIPやPAIRが二重計上されないこと。
- プレイヤー到着時にFULL・Entity・POIを通常保存へ引き渡すこと。
- MillénaireのSiteSurvey・SiteHunter・通常村・孤立建築のnative経路を維持すること。
- 実サーバーでMixin適用、村生成、cache復元、再起動後の保存結果を確認すること。

現構成のControllerとcacheは相互に必要なAPIを直接呼び出します。Millénaire互換MODもcacheを必須依存とします。任意依存や反射によるAPI探索は使用しません。


追加レビューへの対応とChunksmith側に残るLOD修復・更新の課題は、[現行設計書](pregen-throughput-controller-v2-design.md#追加レビューで確認した境界)に記載しています。Controllerの未完了観測はLOD修復の実装を意味しません。
