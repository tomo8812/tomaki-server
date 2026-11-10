# 設計再検討 V4: A1.Flex向け軽量自律最適化とFULL＋LOD完了パイプライン

> **PR #89 設計レビュー版。ソースコード、MOD jar、設定、キャッシュ形式は未変更。** 2026-11-10のレビュー36スレッドと現行実装を踏まえる。本書の方式や3段階構造を「実装済みの最適解」とはみなさず、複数方式を同じ目的・同じ条件で比較して選定する。
>
> 現行の作業基点: main / Minecraft 1.21.1 / NeoForge 21.1.262 / Chunksmith 4.6.0 / maki-lod-cpu 1.4.2 / maki-chunksmith-chunk-cache。従来の合意事項は [Issue #83](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/83) を参照する。

## 1. 何を最適化したいのか。どこまで変えられるのか

最終目的は「プレイヤーがいない時間を使って、後に再生成せずに安全に使える地形を、できるだけ多く・早く・少ない資源の浪費で準備すること」。特定のアルゴリズム（A/B/A、bandit、PI、MPC、token bucket）、制御の階層数（2層／3層）、Chunksmithの現行writer構成、現在のMOD間の制御分担は目的ではない。

**最優先の保護価値**: 通常ワールドのFULL・ProtoChunk・POI・Entity・構造物・プレイヤーの変更を正本として保護する。Millénaireの村・住民とVS2の船舶／Shipyardを壊さない。保存の確認できない生成結果を完成として再利用しない。プレイヤーが入った時は新しい先行仕事を止め、通常のロード・保存・プレイヤー中LOD更新を妨害しない。リソースを使い切ってOOM、データ破損、通常保存失敗を起こさない。

**現在の運用上の条件（再評価できるが、無断で破らない）**: 同心円順・指定中心からの優先生成、通常ワールドに先行FULLを書かず別キャッシュへ保存、FULLとLODの対を確定、15GiBのキャッシュ＋LOD合算上限、2GiBのディスク空き予約、旧独立Backfillの再導入禁止。実測上これらが本来の目的を妨げると判明した場合は、変更範囲と損益、データ保護、必要なIssue #83更新を比較提案する。別順序への変更、LODだけ先行してFULLを後生成する設計、通常ワールドへの先行FULL常時保存などは、現行要件の逸脱として**無断実装しない**。なぜその方式を採らないかを計測上の仮説として評価することは妨げない。

評価軸を分ける。
- **主成果**: 対象座標の新規FULL＋LOD確定数と、既存FULLからのLOD修復完了数。タスクの要求完了・単なるLOD書込・既存完成座標のスキップは成果にしない。
- **副成果**: 同心円中心からの連続完成範囲・未完成の穴・プレイヤー到達時の再生成回避率。簡単な遠方チャンクを優先して件数だけ稼ぐことを禁止する。
- **資源効率**: CPU秒/完成ペア、圧縮CPU・I/O量、メモリ占有、GC、待ち・失敗・再試行、プレイヤー復帰の遅延。
- **安全性**: すべての正本と永続化の不変条件、MSPT・OOM・ディスク予約。安全性はgoodput向上と交換しない。

## 2. 先に検証すべき「本当の律速」

現行の処理を工程別に分ける。分岐する既存FULLのLOD修復と新規FULL生成を混同しない。

1. ChunksmithのisChunkGenerated、通常FULL権威／キャッシュ状態の確認、タスク・所有権とchunk ticket待ち。
2. ネイティブのチャンク取得・生成。ChunkSmithの`inFlight`はこの要求段階の終端付近で減り得る。
3. サーバースレッドでのChunkSerializer.writeとEntitySnapshotのFULL snapshot（新規FULLのみ）。
4. ネイティブLOD抽出／単一writerへのofferと当該レコードwrite ACK。
5. VALIDATE_IOの検証・通常FULL優先判定／claim確認（現行1 worker／有界64）。
6. FULLキャッシュWRITE_IOの圧縮、ファイル書込、atomic publish、receipt（現行1 worker／有界128。修復のみのケースでは不要）。
7. 正本とLODの対応／所有権が確認された時点での完成ペア確定。プレイヤー到着時の昇格・vanilla保存は別トランザクションとして計測する。

現行CaptureBudgetは64件/64MiB、INSPECTIONSは32。LOD writerはネイティブ側、FULL writerとは別。30秒のLOD offer timeoutとcache capture timeoutがある。`LodCommitTracker.offer()` はPENDING.containsValueとPENDING.values().removeIfでpending全走査を行い得る。CacheDiskBudget.refreshはLODディレクトリを10秒程度ごとの非同期refreshで全走査し、同じVALIDATE_IO系の能力を使う。これらは**コードから分かる潜在的律速**であり、実測なしに「writerが律速」「LODが律速」と断言してはならない。

初期計測は工程ごとの`到着率 λ_j`、当該段階成功ACK率`μ_j`、queue件数とbyte量、待ち時間、実行CPU時間、滞留傾き、oldest pending、失敗／期限切れの原因、Chunksmith native幅、main tickへの負荷とする。共通の時刻付きattemptを使い、非同期のcritical path（段階の重複・依存関係）を明示する。**別の母集団のp95を引き算して工程時間としない。** 1チックあたりsnapshot時間と実行回数を計測する。

収束した定常系での大まかな上界は、作業クラス別の段階需要`d_j`と実測サービス容量`μ_j`に対し`G ≲ min_j(μ_j/d_j)`。共有CPU・同期副作用・非定常なworkClassでは各工程が独立ではないため、**これは診断用の近似**。Littleの法則`WIP≈throughput×sojourn time`も十分に定常に近い場合の設計目安であり、短時間の世界生成では恒等的な制御則として扱わない。

## 3. 設計候補を対等に比べる

| 候補 | 制御内容 | 有利な仮説 | 主な弱点 |
| --- | --- | --- | --- |
| A. nativeのみ | Chunksmith自身の安全適応、余計な自作最適化なし | ネイティブのworldgen制御が既に優秀なら最小負荷 | 後続FULL publishの滞留を観測しない |
| B. 現行1.4.2 | A/B/Aによるcap探索 | 比較的単純で変更範囲が小さい | 誤ったLOD代理指標、後段遅延、切替損失 |
| C. 成果計測付きcap-only | 確定ペアgoodputを見てcapをゆっくり調整 | worldgen段階だけが律速なら十分 | 保存キューのWIPを独立に制御しない |
| D. cap固定＋投入ゲート | ACK連動のWIP・期限リスク制御 | 後段保存が律速なら無駄な生成を減らせる | native幅との二重制御、予測誤差、空転 |
| E. cap＋投入ゲート | 成果に基づきcapを低頻度学習、流量を連続調整 | worldgenと保存が別々に変動する場合に有利 | 状態・計測・整合性の複雑さ |
| F. パイプライン自体を改修 | O(P) ACK追跡、スナップショット負荷、writer、容量走査を改善 | 制御の工夫では越えられない物理的律速の解消 | CPU競合、正本保証の回帰、保守コスト |
| G. 生成・保存方式の再設計 | FULL/LOD生成・キャッシュ構成・責務境界を再配置する | 根本の二重処理や転送待ちが大きい場合 | Issue #83との適合性、第三者MOD ABI、安全性に高リスク |

**A1.Flex向けの第一実装候補は、capと投入量を自律調整する軽量なE-lite（§11）。** 後段律速が観測されなければ投入ゲートをほぼ透過させ、C相当のcap-onlyまたはA相当のnative-onlyとして働く。A～Dを比較基準とし、律速計測に基づき不要な機能を削る。Fは制御方式と独立に評価し、GはIssue #83の正本保証を満たす場合のみ検討する。E-liteが常に最速とは前提しない。

現時点の**最も価値が高い設計仮説**は、capだけではFULL snapshotとLOD/FULL writersの未完了仕事が見えないため、成功ACKに基づく軽量な投入ゲートD/Eが効果を出す可能性。ただし採用は実機比較後とする。最初の実装対象は**正確な計測と低コストな明白な無駄の削減**であり、3層制御の採用を前提にしない。

## 4. 成果イベントの厳密な契約

保存／LODの正本を管理する`maki-chunksmith-chunk-cache`が、所有権確認済みの読み取り専用スナップショットを提供する。`maki-lod-cpu`は観測・入力制御のみを行い、正本の確定判定やセーブ形式を勝手に変更しない。

**試行IDと成果IDは分離する。**
- `attemptId = taskEpoch + taskGeneration + per-task dispatch sequence`：要求の一意性、投入時のcap、実験arm、native幅、入場時刻を記録する。同座標再試行や旧タスクの遅延完了を区別する。
- `completionIdentity = dimension + chunkPos + fingerprint + authoritative completion version/transition`：同一座標の**未完了→確定済み**という正本側の状態遷移を一度だけ成果として数える。taskEpochが変わっても既存完成物の再確認を再加算しない。障害後再起動で計測器が空になっても、正本状態を見て新規完成を捏造しない。
- `plannedClass`（入場時のUNKNOWN/NEW見込み/修復見込み）と`resolvedClass`（保存確定／通常FULL勝利／hand-off/失敗時の真の種類）を分離。**実験対象の母集団は投入時分類を保持**し、後から軽い処理だけ抜き取らない。事後分類は診断に使用する。

真正な`PAIR_CONFIRMED`の条件は、対象が本当に新規に完成した正本遷移であり、**同じ生成／要求source tokenのLODレコードwriter ACKとFULLキャッシュatomic publish receipt（または対象座標の既存正本FULL確認）**が対応づけられ、所有権・互換性・失敗状態・player handoffとの競合が解決していること。単に「同じregionのFileStampが変わった」「2つのfileがある」だけでは同一生成を証明しない。現在のCsLodChunk record identityとLodCommitTracker.writtenを基点に、sourceLevelChunk／offer record／FULL snapshotの一意なトークンを安全に運ぶ。無理に証明できない場合は`UNCERTAIN`として完成イベントに加算せず、安全な既存採否判定を維持する。計測の強化が既存キャッシュの正常保存判定に新たな失敗を導入してはならない。

**保証水準を分ける。**
- `LOD_WRITE_ACK`: `CsLodRegionStore.write(record)`の成功戻り。現行が観測できる運用上のwriter ACK。fsync済みとは断言しない。
- `FULL_PUBLISHED`: 一時ファイルの保存と原子的公開の成功（実際の呼出し・例外意味論を明記）。これも機器電源断時の完全耐久性を独断で保証しない。
- `PAIR_CONFIRMED_RUNTIME`: 以上の同一source・所有権・状態遷移が成立した**実行時の運用上の完了**。goodputの主な計数。
- `PAIR_REVALIDATED_AFTER_RESTART`: 再起動後に正本とLOD内容／有効世代を再検証できた状態。安全な継続利用の判定。現行LOD形式で同一生成まで復元不能な場合は追加のprovenance protocolを検討し、未証明なのに完了扱いしない。電源断耐久性が必要ならfsync／二相commit等を別設計・別コスト評価で扱う。

現在の真の保存採否・キャッシュ形式・promote手順は変更せず、上記のイベント計測を実際の所有権遷移の最小限のフックに接続する。計測にだけ用いるowner tokenをLOD保存先の正本へ無理に埋め込まない。source確認と再起動再検証の技術的欠落があれば、パイプライン再設計候補Gとして明示する。

イベントは`REQUEST_ADMITTED`、`NATIVE_FINISHED`、`LOD_WRITE_ACK`、`FULL_PUBLISHED`、`PAIR_CONFIRMED_RUNTIME`、`REVALIDATED`、`FAILED`、`RETRY_PENDING`、`HANDOFF`に分ける。外部Chunksmith、非タスクLOD refresh、スキップ、通常プレイ中のLOD更新はFollowの新規確定成果から除く。新規FULL＋LOD、通常FULL LOD修復、キャッシュFULL LOD修復の成果を別に集計する。

記録は**有界のpending試行＋段階別集計**にとどめる。無制限の全座標Map、NBTの再読込、追加のchunk ticket、新規のディスク全走査、server threadでのブロッキングは導入しない。台帳溢れ／未知のACK／timestamp途切れでは計測unknownとして探索を止め、保存の安全状態には影響しない。期間統計は軽量atomicや有界bucketで出し、`PipelineMetrics.report()`を現役のmaintenance tickへ接続して60秒ごとの診断を復旧する。

## 5. 性能測定で都合の良い分母を作らない

主指標は、無人・タスク所有・外生的な容量停止なしという**capに依存しない**期間における実経過時間Tに対し、

```text
G_total(T)  = newly_runtime_confirmed_unique_coordinates / T_wall
G_new(T)    = newly_confirmed_FULL_pairs / T_wall
G_repair(T) = newly_confirmed_LOD_repairs / T_wall
Resource efficiency = newly_confirmed_unique_coordinates / process_CPU_seconds
```

制御器自身が引き起こしたinFlight制限、writer待機、native slow-chunk backoff、buffer full、再試行、flushとtail収束、探索切替は**すべて分母に含める**。外生停止を除外する場合はその時刻・理由を両armへ同一規則で適用し、総運転時間基準の全期間goodputも必ず併記する。完了しない仕事を窓から削除して残りの軽い成功だけを評価しない。

比較開始時点で`plannedClass`の分布を固定し、UNKNOWN、pending、失敗、hand-offも投入母数に保持する。新規と修復の重みをarmの**成功構成**から再計算しない。一次指標は全実時間のG_totalとtail debt、固定した基準workloadに対する層別比較は補助診断とする。短窓で終わった場合は終端の未確定cohort数、推定残サービス時間、最古ageと期限切れ率（tail debt）を同じarmの損失として提示し、観測済み成功だけで勝者を決めない。

オンライン切替の交絡: 旧capから投入した仕事が、新cap期間中にもCPU・LOD writer・FULL writerの資源を奪う。attemptIdの成果帰属だけで因果効果は解消しない。比較開始・切替時のinFlight、下流writer残、queue age、carryover量を記録。切替後の混在期間は分離評価するか、ブロック全体のwall-clock成果＋tail debtを評価する。無理に停止・drainして検証を成立させず、分離不能なら`INCONCLUSIVE`とする。

ABBA/BAAB等のswitchbackは**実験候補の一つに後退**させる。通常運転で強制しない。最も信頼できる因果検証は、同じseed、未生成ワールド状態、MOD/config、CPU quota、対象座標とキャッシュ・writer初期状態で比較する**複製ワールドによる反復A/B**。試験順を交互化し、再試行負担と探索時間を含む全期間を測る。オンラインの変化検知は因果証明ではない。固定「35%／5%」判定だけでは採用しない。相関の強いチャンク列は時間ブロック単位で不確実性を推定し、十分な証拠がなければ現行の安定方式を保持する。

## 6. 実運転の制御候補と安全な作用点

候補Eの3時間スケールは**条件付き提案**である。計測で後段律速が否定されたらD/Eを実装する必要はない。

**保護層（即時～約2秒）**: player入室、保存所有権・fingerprint不明、ディスク上限／予約不足なら先行仕事の**新規投入だけ**を止め、既存の安全な停止・drainに従う。OOM、メモリ切迫、MSPT悪化、GC、I/O PSI、writer backlog/失敗には安全なcap制限とヒステリシス。CPU高使用率単独では減速しない。Chunksmith自体の制限を上書きしない。

**流量層（後段律速と判明した場合のみ、約2～5秒が候補）**: cap `c`とは独立に、新規dispatchを許可する速度`a`またはcredit/WIPを調整。各段階jの対象別必要量（新規FULLではLOD＋snapshot＋FULL、修復では主にLOD）と、`queue_j + predicted_downstream_j`、推定保守的ACKサービス率から`debtSeconds_j ≈ pendingWork_j / max(conservativeServiceRate_j, ε)`を求める。queue件数が小さくても生成中の仕事からの将来の流入を算入し、容量・byteの双方で上限を掛ける。writerに需要がなくμ=0のときは`unknown/idle`を区別し、0件/sを機械的に無限借金とみなさない。

制御則は**決め打ちしない**:
1. 最小構成: ACK率の変化、writer待機秒とdeadline近接だけで手前の投入ゲートをON/OFFするヒステリシス式。
2. 次の候補: AIMDまたは小さなtoken bucket（滞留改善時は緩やかに増やし、持続的発散・タイムアウト兆候では素早く減らす）。
3. 推定が安定していて本当に速い場合のみ: queue debt誤差に対するPI（飽和時は積分器凍結＝anti-windup）、または有限予測窓MPC。PI/MPCの使用それ自体は成果ではない。

**期限予測**: LodCommitTrackerの30秒期限、cache captureの30秒期限は起点が異なるので、実際のdeadlineごとに`timeLeft - conservativeQueueWait - predictedServiceP95`を計算。余裕が消える前に次の投入を減らして再試行CPUを減らす。見かけのqueue capacityが余っていても安全とは限らない。予測が少量サンプルで不確実なら未知として保守的に探索を止めるが、常時writerを空転させないよう、ヒステリシスと回復条件を検証する。

**未分類仕事のcredit**: ChunksmithのisChunkGenerated結果が出るまでplannedClassはUNKNOWN。仮に保守的なNEW相当の下流需要を見積もり、分類が確定したら必要な段階のcreditを調整／返却する。これは制御器内のadvisory予約であり、CacheDiskBudgetやCaptureBudgetの権威あるhard leaseを代替しない。既存のdisk budgetの正式reserveを二重に加算しない。生成中のplayer handoff、失敗、timeoutで必ずcreditを解放する。

**作用点**: 自作のFollow-ownerタスクが、Chunksmith iteratorから**次の座標を選んだ後、実際のネイティブdispatch時刻の前**に位置する安全なworker-side境界でのみ投入権を確認する。同じ選択済み座標を維持して短くpark／取消可能な待機を行い、同心円順を変えない。server thread、native writer、normal load、外部のChunksmith所有タスクはブロックしない。既存のプレイヤー入退場・停止・例外経路の優先順位を守る。ゲート待機をChunkSmithのslow-chunk latency測定に混入させない。ループの有界性・スレッドの生存性を検証する。

**ネイティブとの二重制御防止**: commandedCap、Chunksmith native dispatchLimit、actual inFlight、実際のadmission rate、native slow-limit理由を別々に測定する。CAP_LIMITED / NATIVE_LATENCY_LIMITED / NATIVE_TICK_LIMITED / STORAGE_LIMITED / UNKNOWNを記録し、native側で抑制中はcap増加を「Bが不利」という実験結果にしない。PI積分／upward banditも凍結し、原因を診断。現行`maki$clamp()`は上げ方向の強制権を持たないという契約を維持する。

**探索層（測定後の採用候補、数分単位）**: 主制御が安定運転している間、workClass構成・writer ACK率・p95待機・native幅・quota・MSPT・I/OのEWMAと持続的変化点（CUSUM等）を監視。変化が続き、安全余力がある時だけcapやtargetDebtSecondsの近傍を小さく比較する。小サンプルに過学習しない。旧仕事のcarryoverが大きければオンラインABBAを避ける。探索が消費する時間と失われた推定完成ペア数を記録し、効果がなければバックオフして長い安定運転。writer律速の場合は単純にcapを固定するだけでなく、goodputを維持したまま一段低いcapがCPU秒・滞留を減らせるかも試す。

**学習の再利用**: c_bestを無条件に保持しない。別の小さなperformance profileにcontroller version、CPU quota/affinity、heap、Chunksmith native config/writer構成、workClass帯、観測日齢、成功サンプル・安全条件を最小限記録する。近い環境だけ安全clampしたwarm startと短い確認を許可。古い／破損／異なる環境では既知の安全開始値へ戻す。生成キャッシュのfingerprint・有効性には影響しない。

## 7. cap以外の根本的な処理改善も対照にする

以下の改善案は**勝手に全採用しない**。メトリクスまたはmicrobenchmarkで真の律速が確認できた場合に、単純な現状方式との同条件実測で採否を決める。

- **LOD ACK O(P)走査**: LodCommitTrackerの`PENDING.containsValue(result)`と`values().removeIf`を、record identityに紐付く登録ハンドルと条件付きremove（理想的には通常O(1)）へ替えることを比較。offer中の同期write、複数レコード、同座標再試行、timeout/exception/composite sink、停止をテスト。負荷が軽いなら変更しない。
- **main-thread snapshot**: ChunkSerializer.writeとEntitySnapshotを別スレッドへ安全性の根拠なく移さない。まず1tick当たりのsnapshot CPU時間と完了ペア/sを計測し、必要なら**生成前の投入**へtick負荷予算を戻す。native幅を増やしてもserver tickを浪費するならcapを上げない。
- **容量refreshのO(file数)負荷**: CacheDiskBudget.refreshがLOD領域のFiles.walkを繰り返す負荷を測定。大きい場合に限りwriter ACKと変更リージョンのsize差に基づく保守的な増分会計を導入し、非同期の定期全走査でreconcileする。単純累積のLOD bytesは同一region上書きによる増減を表さない。測定不確実なら15GiB/2GiB制約をfail-closedし、性能よりデータ安全優先。
- **容量境界での生成浪費**: FULL生成後にhard leaseを取る現状は、容量不足時にCPU時間が成果にならない。ワールド正本の事前判定後、新規FULL見込みのみadvisoryの圧縮後サイズ分位数・保守余裕で投入creditを予約し、生成後の既存hard leaseと照合する案を比較。hard leaseの前倒しが必要なら二重予約・timeout・hand-offリークを防ぐ正式API設計として別検討する。上限に近い条件でのみ効果があるか計測する。
- **FULL圧縮とpublishの分離**: CacheManager.publishCaptureは同じWRITE_IOでgzip圧縮と原子的公開を直列実行している。まず圧縮CPU、ファイルI/O待ち、claim/renameの各時間を分ける。**圧縮CPUが律速**なら小さな有界圧縮worker（1→2等）を比較し、immutable NBT snapshot、byte lease、座標の所有権、原子的publishを保持する。2CPU環境ではworldgenとCPU競合するため逆効果もあり、速いと証明されないなら1workerのままにする。LODリージョンwriterの無条件並列化は禁止。
- **native生成工程**: native-onlyで最速なら自作探索層を捨てる。逆にChunkSmithのnative slow-load判定が主律速なら、isChunkGenerated、ticket待ち、worldgen、server-thread snapshot、LOD／FULL writerを同じattemptの**重ならない時間区間と重なるcritical path**として分解し、真正なslow理由を診断する。第三者MODへの直接改変は、自作MODの介入や設定だけで安全に解決できないと判明した場合の最終案に限定。
- **FULLとLODを別々の処理で生成する構成**や、将来の別ストア／圧縮形式、スケジューラを再配置する案は、通常ワールド保護と同一生成結果、既存完成物再利用、15GiB制約の全体コストを評価して初めて検討する。旧データを破棄するだけの速度向上策は評価から除外する。

## 8. 実装境界・状態機械

`maki-chunksmith-chunk-cache`: 保存成功、所有権、同一生成token、ACK保証、PAIR_CONFIRMED_RUNTIME、既存の安全なpromotionの責務。読み取り専用のPregenCompletionTelemetry、段階別統計、PipelineMetrics.report定期実行を実装候補とする。計測失敗で正本判定を変えない。

`maki-lod-cpu`: 安全監視・実効native幅の観測・候補別制御器（native-only / cap-only / admission-only / combined）の切替と測定、学習profileの妥当性評価。必要になった時だけ短い安全なnative dispatch gateを実装。既存設計と混同しないfeature flagを設け、旧方式へのロールバックを確保する。

`maki-mods-observer`: 診断専用MODの責務は尊重する。新たな軽量統計のために専用ログファイルや高速な座標別INFOを乱発しない。通常INFOは60秒集計（G_new/G_repair/G_total、未完了tail、native幅、LOD/FULL writer、deadlineリスク、採否と理由）、WARNは保存失敗や安全停止を記録する。

候補方式の状態機械:

```text
observe():
    sensor = snapshot_native_and_pair_ack_and_storage_without_new_chunk_IO()
    if player_online or ownership_unknown or fingerprint_unknown or hard_disk_budget_block:
        stop_NEW_pregen_admission_only()
        complete_or_abort_inflight_under_existing_safe_rules()
        return
    if emergency_pressure:
        cut_admission_and_safe_native_cap(); freeze_learning()
        return
    if stale_or_unreliable_sensor:
        use_previous_safe_policy_without_optimistic_increase()
        return

    classify_bottleneck(generation, main_thread_snapshot, validation,
                        lod_writer, full_writer, native_safety, unknown)
    if selected_policy_uses_admission_gate and downstream_congestion_supported:
        update_bounded_dispatch_credit_avoiding_deadlines_and_queue_growth()
    if selected_policy_uses_cap_learning and stable_environment_and_no_native_suppression:
        run_rare_robust_local_test_or_keep_best_known()
    record_all_wall_clock_pairs_and_pending_tail_and_resource_cost()
```

安全監視は全候補で共有する。計測可能なすべての不変条件を守り、controllerの追加によって原子公開・正常FULL優先・LODsのnon-task更新・プレイヤー切替が壊れないことを保証する。

## 9. 比較計画・段階採用・却下条件

**P0（無条件採用）**: 既存方式を変更せず、権威のあるペア確定イベントと段階別律速計測、INFO診断復旧だけを行う。計測の正確さとパフォーマンス負荷を測る。真のLOD/世代対応が未証明な領域を隠さない。

**P1（個別の有利さを確認）**: LodCommitTracker O(P)、容量walk、FULL writer圧縮CPU、main-thread snapshotの律速を検証し、効果がある改善だけ切り出して採用する。コントローラの複雑化よりこの改善の方が速ければ優先する。

**P2（A1向け短時間自己調整）**: §11のE-liteを初期cap=2で有効化し、通常生成中の正本PAIR実測を利用して小幅に局所調整する。下流のwriterが詰まらない限りaゲートはほぼ透過させ、nativeの実効幅とPAIR goodputからcを調整する。固定A/B/A反復は通常運転の前提としない。厳密な優劣や構造変更F/Gの因果評価が必要な場合だけ、複製ワールドで同じseed・未生成状態・MOD/config・CPU quota・対象座標・保存初期条件を揃え、試験順を交互化して反復する。性能優位が不明なら既存の単純な方式を維持する。

**P3（必要な場合のみ）**: 単一FULL writer、main-thread snapshot、LOD writer/nativeインターフェース自体が高い構造的上限なら、制御方式とは独立にボトルネックの処理構成を再設計し、P2と組み合わせず単独の差で評価する。第三者MODの直接改変は必要性を証明してからに限定。

すべての試験に、探索に費やした時間、slow-chunk抑制を含む投入不可時間、30秒期限切れ、既存完成の再検証、tail debtの未確定残り、停止時のdrain、プレイヤー復帰時遅延を含める。追加の計測CPU／ロック待ちが本来の生産性を低下させるなら計測器を簡略化する。

テスト: 同一生成結果のtoken照合／LOD後続上書き、重複試行・旧epoch・restart、失敗順序とhand-off、native widthでcapが届かない状況、統計的に不安定な地形、writer ACK遅延・0件/秒・期限30秒、queue制限・memory pressure、センサー欠損・writer凍結、CGroup quota変更・learned profile破損、LOD writer同期fallback、Entity/POI/構造物保存と村・船舶保護。現行NeoForge／Chunksmithに対するMixin ABI、ビルド、既存テストも実施する。

採用判定: すべての正本とプレイヤー安全条件に回帰0。保存失敗・再試行・timeoutが悪化しないこと。複数回の同条件反復でwall-clock確定ペアgoodputが統計的に信頼できる程度改善、またはgoodputが同等でCPU秒／ペアや待ちが改善。優位性不明なら**単純な方式を維持し、複雑な制御は採用しない**。

## 10. 参考・レビューの扱い

関連: [Issue #83](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/83)、[Issue #74](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/74)、[Netflix concurrency-limits](https://github.com/Netflix/concurrency-limits)（遅延・待機量に基づく同時数制御の参考であり、この環境に適合するという証拠ではない）、[Little 1961: L=λW](https://doi.org/10.1287/opre.9.3.383)（定常系での関係）。

実装参照: [AdaptivePolicy.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/AdaptivePolicy.java)、[CpuGovernor.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/CpuGovernor.java)、[GenerationTaskMixin.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/mixin/GenerationTaskMixin.java)、[CacheManager.java](../custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/CacheManager.java)、[LodCommitTracker.java](../custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/LodCommitTracker.java)、[CacheDiskBudget.java](../custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/CacheDiskBudget.java)、[PipelineMetrics.java](../custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/PipelineMetrics.java)。

PR #89の36件のレビューは、同一生成の根拠、重複排除とattempt、分母バイアス、作業種別重み、carryover、writer律速、工程別credit、期限予測、native制御の干渉、root cause計測、容量、O(P)のACK、スナップショット、圧縮、学習profile、ablationなどを相互に整合させて本仕様へ取り込んだ。**基本採用とは設計条件として採用すること。性能改善を実測していない候補は比較対象として保留する。**

このPRの範囲は改訂した設計仕様に限る。実装・最適性・性能向上を確認したと称さない。

## 11. Oracle A1.Flex（2 OCPU・12GB）専用の軽量自律最適化

**第一実装候補は E-lite**。Oracle VM.Standard.A1.FlexのAmpere Altra / ARM64 / 2 OCPU / RAM 12GB / 最大2Gbpsをターゲットにする。初期条件を実機測定で絞り、Minecraft実運転では正本FULL＋LODの完成速度を見て自律最適化する。学習のための長時間のA/B/Aや全cap値総当たりに生成時間を費やさず、普段は生成そのものを進める。CPU2コアは初期値を決めるために使い、並列数を2や4に永久固定しない。

### 11.1 Minecraft未起動の事前計測

Python3の scripts/a1-pregen-preflight.py をSSHで実行。worldとキャッシュと同じファイルシステム上の既存ディレクトリを --dir へ指定する。

    python3 scripts/a1-pregen-preflight.py --dir /path/to/server --output /tmp/a1-pregen-profile.json

一時領域への約64MiBの同期書込、短い合成圧縮比較、CPU affinity・cgroup、空きメモリ、CPU/メモリ/I/O PSI、空き容量を測る。既存ワールドを読み書きしない。事前測定は一度だけ実施し、通常起動ごとの重いベンチは強制しない。CPU quotaや実際のファイルシステムが想定と違えば検出値を優先する。

合成zlibと短期fsyncはMinecraftのJava NBT・worldgen・LOD writerやクラウドストレージの長期処理能力そのものではない。**preflightからチャンク/秒・最適cap・保存ACK期限の余裕を直接計算しない。** 実際のwriter能力と完成ペア速度はMinecraftの通常運転中に少量のイベントから学習する。

### 11.2 二つの制御入力と既存安全機構

- **c：生成並列数**。初期2、1ずつ局所探索。改善が続けば4より上も探索する。上限は既存のhard safety guardを維持し、値を総当たりしない。
- **a：新規投入ペース／stage credit**。下流保存が空いているときは原則透過し、writerの未完了量・ACK到着速度・残りdeadlineから必要時だけ制限する。設定上のaと実際のdispatch/sは別記録とする。

実効並列数は、指令c・Chunksmith native dispatchLimit・既存hard guardの最小値になる。現在のMixinはnative側のcapを上へ強制できない。nativeに制限されているときは「cを増やしたら遅くなった」と誤学習せず、上方向探索を凍結する。

作業クラスはNEW（FULL生成＋LOD＋FULL保存）、既存通常FULLのLOD修復、既存キャッシュFULLのLOD修復、UNKNOWNを区別する。UNKNOWNには保守的な予測creditを割り当て、分類後に調整。creditは制御器の軽量見積もりでありCacheDiskBudget/CaptureBudgetのhard leaseを代替しない。失敗・timeout・handoff・PAIR完成で各段階のcreditを**一度だけ**解放する。正本の所有権・同一生成の証明は引き続きcache側の責務とする。

### 11.3 通常運転中の軽量閉ループ

**WARMUP**：c=2から開始。aは基本透過で、nativeの安全制御と既存保存上限を利用する。writerのACK率が未知の時点で0件/sとみなして停止しない。正本PAIRイベントがまだ足りなければ固定的に安全運転し、自然に集まる計測を待つ。

**FAST：下流の混雑調整**（数秒ごとの低コスト集計）。LOD/FULL writerについて、現在のqueueと進行中チャンクから将来流入する需要を、NEW/REPAIR別に件数と予測bytesで計上する。writerの成功ACK/sのEWMAと不確実性から保守的なサービス率を推定し、未処理仕事の概算処理秒数（debtSeconds）を出す。LOD offerとcache captureでは30秒期限の起点が異なるため、各attemptの実際の残期限と予測待ち・サービス時間を照合する。観測不足・idle・停止を区別し、writerに仕事がないのに恒久的に投入を絞らない。

持続的なqueue増加・期限超過リスクではaを乗法減少させ、余力が戻ったら加法増加させる **AIMD＋ヒステリシス**を第一候補にする。tokenの蓄積やバーストを有界にして、1tickあたりsnapshot負荷・GCを急増させない。単純なON/OFFヒステリシスで同等ならAIMDを省く。状態と設定は少数のEWMA・有界分位点集計・有限attempt台帳に限定する。

**SLOW：生成並列数の近傍学習**。writerに余裕がありaゲートがほぼ非制限、native実効幅がcへ届き、作業構成・地形帯・安全状態が安定している区間だけcを1段試す。比較は新しく確定した同一生成FULL＋LODペア/総経過時間で行い、旧cap由来のcarryover、未完了tail、探索時間、失敗・再試行を加味する。短い窓で成功件数が不足なら判断を保留し、無理な待機やドレインを行わない。改善が確かなら上へ探索を続け、改善が止まる・native抑制・writer混雑なら元へ戻す。同等goodputならCPU秒/PAIRとbacklogの少ないcを選ぶ。成果がない探索は間隔を延長する。

**STABLE：低負荷で長時間運転**。有効なcとaを維持し、常時A/B/Aを回さない。NEW→REPAIRへの移行、writer ACKサービス率、quota、MSPT/GC/PSI、native backoffの持続的な変化を検知した場合だけ小さく再探索。性能profileはCPU quota/affinity、heap、config hash、workClass帯、観測鮮度を照合してwarm startし、古いprofileの失効で生成キャッシュを失効させない。

**SAFETY／PAUSE**。プレイヤー入室、所有権／fingerprint不明、ディスクhard limit、OOM、MSPT急悪化、writer失敗、native抑制、測定欠損には安全な新規投入停止・cap制限・既存drainに従う。通常FULLロード、Entity/POI、Millénaire、VS2、プレイ中のLOD更新を妨害しない。CPU使用率95%だけでは減速しない。センサーが不確かなら積極探索を凍結し安全な以前の方式へ戻す。

### 11.4 軽量実装の作用点と責務

計測はcache正本からの同一source tokenに結び付くLOD writer ACK／FULL publish receipt／PAIR_CONFIRMED_RUNTIMEを使う。attemptIdとcompletionIdentityを分け、再試行や再起動で同じ完成座標を再計上しない。全座標Map・追加のディスク全走査・高頻度INFOは導入しない。台帳が溢れても保存成功を失敗扱いにせず、計測unknownとして学習だけ停止する。

aの投入ゲートは、同心円iteratorから選んだ座標を保持したままnative要求開始前のFollow-owner workerでだけ使う。server thread、native writer、外部Chunksmithのタスクを待たせない。既存SharedWorkBudgetの予約、player stop、キャンセルを最優先し、gate待機をnative slow-chunk時間に混入させない。実装前に現行Chunksmith版のMixin ABIと選択〜dispatch境界を検証する。maki-lod-cpuは観測・制御を担当し、maki-chunksmith-chunk-cacheは保存・同一生成の完成判定を担当する。

### 11.5 A1用初期値と検証の進め方

| 項目 | 初期仮説 | 実行時の扱い |
| --- | --- | --- |
| CPU | 2 OCPU | quota/affinityを検出。CPU高稼働単独で減速しない |
| RAM | 12GB、JVM最大heap約8GiBを候補 | OS・ページキャッシュ・ネイティブメモリ・GCの余裕を確認 |
| システム余力 | 最低約1.5GiBを初期目安 | 実機のメモリ圧力とOOMを優先 |
| c | 開始2、近傍±1 | 伸びるなら4以上も探索、hard guard維持 |
| a | WARMUPではほぼ透過 | writer ACK／debt／期限とbytes予算で調整 |
| 観測 | 数秒単位の軽量集計 | INFOは約60秒、座標ごとのINFO連発を避ける |
| 探索 | 自然な生成中の少数比較 | 成果不足なら保留。失敗続きなら頻度を減らす |

**導入順**：①preflightで環境条件を把握、②正本PAIRイベントと段階別律速の最小計測、③安全cap＋自然観測、④writer律速時だけaを調整、⑤cの低頻度局所学習、⑥必要ならLOD ACK O(P)走査・容量走査・圧縮writer等の構造的律速を個別改善。各段階で性能改善がない複雑な仕組みは残さない。

テストはnative幅がcに届かない状態、LOD/FULL writerの30秒deadline、queue過負荷・writer停止、NEW/REPAIR構成変化、player復帰と選択済み座標のキャンセル、同一生成世代・restart・通常FULL競合、ディスク15GiB/空き2GiB、Entity/POI・Millénaire・VS2を含む。正本保護に回帰0が必須。対照にはnative-only／現行A/B/A／cap-onlyを用い、厳密な因果比較が必要な時だけ複製ワールドで反復する。**SSH事前計測と通常運転の小さな学習で成立させる方針であり、VMで多数の独立長時間実験を強制しない。**

**PR #89の実装状況**：本章とpreflightスクリプトは設計・事前計測の準備であり、PAIRイベントやE-lite制御器のコード実装・A1 VMでの性能測定を完了した意味ではない。
