# PR #90統合仕様：E-liteとFULLキャッシュの責務境界

対象：PR #89（生成制御）をPR #90（Chunksmith 4.8.1／FULLキャッシュ1.4.7）へ統合する作業。
PR #90の確認時点のHEAD：`4b7d69209415d0509abab25bcc71a8e27181996f`。
この文書は**実装先の要求仕様**。PR #89だけでは旧cacheとのイベント接続・Mixin統合が完了しないことを許容する。

## 1. 決める主体

| 項目 | 決定権と実装責務 |
| --- | --- |
| Chunksmith | チャンク要求、LOD生成・保存・既存LODの再利用判断、ネイティブ安全制御 |
| maki-chunksmith-chunk-cache | FULLキャッシュの生成・保存・読込・完全性・再利用・復元・通常ワールドへの引渡し・自己保護 |
| maki-lod-cpu / E-lite | 生成タスクの投入許可、`c`の自律探索、LOD/VALIDATE/FULL処理量のWIP窓、完成ペア数の性能計測、投入保留の再試行 |

E-liteはFULLやLODの**既存データの使用可否を決めない**。不足している計測証明を理由に、正常な旧FULLや旧LODを検疫・無効化・再生成しない。

## 2. PR #89でlod-cpu側に移したもの

- Chunksmithの`isChunkGenerated`呼出し前の候補投入、および`getChunkAtAsync`直前の実要求投入
- Chunksmithの判定結果に基づくNEW/REPAIR分類（*性能比較時の仕事量補正のみ*）
- LOD、FULL検証、FULL書込それぞれのWIP枠、物理terminalによるcredit返却、滞留時の枠縮小
- タスクと世代にひもづく試行token、期限切れ・停止後の競合排除
- 一時拒否座標の進捗確定抑制・再走査負債
- LOD／FULL独立通知の照合と、同一試行の`PAIR/s`計測
- FAST窓とSLOWの`c±1`探索、ハード・ソフト圧力制御、比較不能判定、warm-startプロファイル

PR #89のchunk-cache変更はすべて`main`の内容へ復元した。PR #90のcacheへ新実装を追加するので、PR #89では暫定実装や旧APIの偽装を追加しない。

## 3. chunk-cache側に実装する通知

cache側は**自分が所有しているFULL処理についてのみ**報告する。

1. FULL検証を実際に開始したら`ResourceBudgetApi.stageStarted(token,"VALIDATE")`。
2. FULL検証が物理的に終わったら`stageFinished(token,"VALIDATE",serviceNanos,success)`。論理タイムアウトだけでterminalを発行しない。
3. FULL書込を開始したら`stageStarted(token,"FULL")`。
4. FULLファイルのアトミック公開を含む実処理が終わったら`stageFinished(token,"FULL",serviceNanos,success)`。
5. キャッシュMODの**既存のFULL妥当性ルール**でFULLが受け入れられた時、またはFULL公開が確定した時に、対応する今回の生成試行へ`ResourceBudgetApi.fullReady(token)`を報告する。
6. FULL保存不成立や別所有者による引渡しが起き、当該試行のFULL完成が証明できない時は`ResourceBudgetApi.captureFailed(token)`または`pairUncertain(token,reason)`を報告する。
7. キュー件数、書込中件数、容量・メモリ圧力と、必要なら処理時間を**読取専用の低負荷メトリクス**として提供する。キャッシュ自身の保存安全上限はキャッシュMODが維持する。

`token`は、E-liteが`ResourceBudgetApi.attemptToken(dimension,x,z,owner)`で発行した**その試行に限る識別子**。異なる試行の成功を転用しない。cacheが独自の生成投入や全体WIP窓、cの探索を持たない。

**cacheは`ResourceBudgetApi.reserveCandidate`/`beginActual`/`classifyCandidate`を呼び出さない。これらの生成制御APIは公開せず、`PregenControlHooks`とlod-cpu自身のMixinからのみ使用する。**

**新設のFULL証明書を使う場合はcacheが所有する**。証明書はFULLの正常性・保存完了に関する情報とし、LOD利用可否の証明を前提にしない。旧`schema 3`のFULLを新証明書の欠如だけで無効化しない。PR #90で確定した固定24文字世代ID、旧世代からの移行、環境監視撤廃の方針を保持する。

## 4. Chunksmithから取得する通知

この部分は**lod-cpuのChunksmith観測経路**が担当し、chunk-cacheへLODの判断権限を移さない。

- 同一試行のLOD physical write start／terminal（成功・失敗、所要時間）
- 成功して現在の試行との対応が確認できた時の`PregenControlHooks.lodCommitted(token)`
- LOD sinkのキュー、進行中件数、同期書込、既存のネイティブ停止理由
- 元の`hasLod`／`isChunkGenerated`の結果。生成制御は判定を上書きしない

同座標の他タスク・プレイヤーLODと競合した場合、同一sourceだと確認できなければ`pairUncertain`として性能標本から除外する。必要ならE-lite側でLOD物理観測の独立Mixinを完成させる。

## 5. 正しい完成ペアの計上

- 各試行にtokenを発行する。
- Chunksmith側のLOD保存成功と、FULLを所有する側のFULL確認を、それぞれ独立に受け取る。
- E-lite内の`PregenFlowController`が**両方がそろい、試行が解決済みでないときだけ**完成ペアを一度計上する。
- 旧キャッシュ・旧LODの単純スキップや再起動後の再確認は新規`PAIR/s`に計上しない。
- NEW/REPAIRは測定区間で仕事の種類が極端に変化していないか比較するための分類。どちらのデータも有効性判定を変更しない。

`stageFinished(...success=true)`は成功した物理処理を意味する。`fullReady`はFULLが再利用可能と所有MODが判断した通知。`lodCommitted`はChunksmithのLOD書込結果であり、両者がE-liteで合流する。証明できない区間では`PAIR/s`学習を保留するが、**それによってFULL・LODの利用自体を禁止しない**。

## 6. 通知の有効化と停止

PR #90への統合で必要な通知経路がそろった時に限り、`ResourceBudgetApi.connectPairTelemetry("E_LITE_PAIR_V1")`を呼ぶ。欠けている場合はE-liteのSLOW性能学習を保留する（虚偽の0件成果として判断しない）。既存のプレイヤー優先・ネイティブ安全制御は維持する。

- 正規のtoken、dimension、x/z、owner、task epochを一致させる。
- 物理書込がまだ進行中なら、論理タイムアウト後もcreditを返さない。
- FULLとLODの成功通知が逆順でも同じ結果になる。
- 既存cacheの再利用可否を`PAIR`計測の結果で変更しない。
- タスクstop・プレイヤー参加以降は新たな投入を禁止し、既存の書込と所有権保護を尊重する。

## 7. PR #90でのMixin整理・統合条件

PR #90のchunk-cacheには既存の`GenerationRequestMixin`（`GenerationTask.run/isChunkGenerated`、`lambda$run$1/getChunkAtAsync`）、`GenerationTaskPairMixin`、`NeoForgeWorldMixin`等が存在する。PR #89のlod-cpuにも同じ呼出しへの生成制御フックを置いている。

- Chunksmithの生成投入をフックする`@Redirect`は**lod-cpuの1か所**に統一する。同じcallsiteをcache側でもRedirectしてMixin衝突を起こさない。
- cache側が従来守っていた通常FULLの保存抑制・所有者保護・プレイヤー引渡しは、**FULL処理内部の安全ガードとして維持**する。生成制御と分離して組み込む。
- ChunksmithのLOD存在判定をcache証明で上書きする処理は追加しない。
- 4.8.1の実物bytecodeで`run`、`lambda$run$1`、LOD write、NeoForgeWorld callbackの注入先を再検証する。
- キャッシュ世代の固定ID `f00dcace0000000000000003` と旧データ移行、LOD判断のChunksmith委任、監視コード削除を継承する。
- 統合後にlod-cpuとchunk-cache両方のJARを**同じコミットから再ビルド**し、配置する。PR #89単体の既存JARはソース変更後の最終成果物として扱わない。

## 8. 受入試験

1. 旧FULLと旧LODをそのまま再利用し、追加証明書の欠如で隔離・再生成しない。
2. FULLのみある場合にChunksmithの判断で必要なLODだけ補完する。
3. LODのみある場合にcacheの独自ルールを侵害せず、必要なFULL処理を行う。
4. 両方の完了通知が逆順／遅延／失敗／二重通知／別tokenでも、E-liteのPAIR計測が誤らない。
5. cacheの書込が詰まればE-liteの投入が減り、解消すれば回復する。外部タスクはE-lite制御の対象外。
6. プレイヤー参加、外部Chunksmithタスク、再起動、promotion・Entity・POI・Millénaire・VS2の既存保存経路を維持する。
7. Chunksmith 4.8.1 / Millénaire 9.1.0の実物JAR、Mixin適用、静的検査、Gradle check、実サーバーでの複製ワールド検証を実施する。
