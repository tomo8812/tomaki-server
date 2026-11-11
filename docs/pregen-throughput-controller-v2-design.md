# 設計再検討 V6: A1.Flex向け自律WIP制御とPAIR保存確定の整合性

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

**流量層（後段律速と判明した場合のみ、約2～5秒が候補）**: cap `c`とは独立に、新規dispatch速度`a`と工程別credit/WIPを調整する。`queue_j + 予測された下流需要 + timeout後も実行中のphysicalWorkDebt` を数・bytesで計上し、忙しく稼働している時の処理能力 `μ_busy,j`（サービス時間またはbusy時間当たりのACK）を用いて `debtSeconds_j ≈ physicalPending_j / μ_busy,j` と概算する。`arrival ACK/s`は独立の観測値であり、投入制限でwriterがidleになった時間を能力低下として学習しない。ACKが来ないときはidle／需要不足／writer停止・センサー不明を区別し、期限切れ後の未終了物理仕事を無視しない。詳細は§11。

制御則は**決め打ちしない**:
1. 最小構成: ACK率の変化、writer待機秒とdeadline近接だけで手前の投入ゲートをON/OFFするヒステリシス式。
2. 次の候補: AIMDまたは小さなtoken bucket（滞留改善時は緩やかに増やし、持続的発散・タイムアウト兆候では素早く減らす）。
3. 推定が安定していて本当に速い場合のみ: queue debt誤差に対するPI（飽和時は積分器凍結＝anti-windup）、または有限予測窓MPC。PI/MPCの使用それ自体は成果ではない。

**期限予測**: LodCommitTrackerの30秒期限、cache captureの30秒期限は起点が異なるので、実際のdeadlineごとに`timeLeft - conservativeQueueWait - predictedServiceP95`を計算。余裕が消える前に次の投入を減らして再試行CPUを減らす。見かけのqueue capacityが余っていても安全とは限らない。予測が少量サンプルで不確実なら未知として保守的に探索を止めるが、常時writerを空転させないよう、ヒステリシスと回復条件を検証する。

**未分類仕事のcredit**: ChunksmithのisChunkGenerated結果が出るまでplannedClassはUNKNOWN。保守的なNEW相当の将来需要を仮計上し、実分類後に段階別に差し替える。LOD/FULLそれぞれの将来需要creditは段階へ入場したらphysical WIPへ移し、**実終了ACK・検証済みcancel・発生しなかった段階のskip**でのみ該当部分を返す。PAIR確定で全段階を一括解放しない。timeoutは論理結果だけを終了させ、実際に残ったwriter/validation仕事の負債は保持する。creditは予測専用でCacheDiskBudget/CaptureBudgetのhard leaseを代替しない。詳細は§11。

**作用点**: 既存のBackfill用`ChunkIterator.next()` barrierはその責務のまま維持し、E-liteのadmission課金をそこへ追加しない。**スキップ判定とnative安全確認後、`dispatchTime`/`inFlight`増加と実要求開始の直前**で、実dispatchごとに一度だけ課金する。待機中にnativeの安全条件・player停止・タスク所有権を再評価できる非ブロッキング述語、または同等の有界再確認が必要。再確認できる安全なMixin注入点がない場合はcap-onlyへ退避する。server thread、native writer、normal load、外部Chunksmithタスクは妨げず、同心円順とslow-chunk計測を保つ。詳細は§11。

**ネイティブとの二重制御防止**: commandedCap、native dispatchLimit、actual inFlight、dispatch候補需要・skipped数・native幅待ち・credit待ち・`inFlight>=c`時間割合を区別して観測する。`NATIVE_LIMITED`は原則**上方向探索のみ凍結**し、既存native幅で許されるdispatchは継続する。MSPT/メモリなどの独立したhard safety異常時だけ強く減速・停止し、native AutoPause時はそのライフサイクルに従う。native幅が届いても実際の候補需要が乏しい時はcの比較を未評価にする。現行`maki$clamp()`は上方向の強制権を持たない。

**探索層（数分単位を初期候補）**: workClass構成・`μ_busy`・writer待ち・native幅・有効なcap需要・quota・MSPT・I/Oの持続変化を監視。**generation-boundではc+1の成果増加を検証**し、**storage-boundではwriterの供給を切らさない条件でc-1の非劣化／資源効率改善を検証**する。cap差が実際のdispatchに作用しなかった試験は勝敗を付けない。carryover・tailが大きい場合やサンプル不足時はINCONCLUSIVEにし、長い安定運転を優先する。

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

**E-liteを第一実装候補とし、A1の既知資源条件から初期化し、自然な生成と保存の実測で自律収束させる。** 2 OCPUは開始cap=2の理由であり上限ではない。学習中も安全なチャンク生産を継続し、長時間のA/B/Aや全cap総当たりを必須にしない。E-liteが劣る場面ではnative-only／cap-onlyへ縮退できる。最重要はIssue #83の同一生成FULL＋LODの確定と通常ワールドの保存安全。

### 11.1 非起動preflightと初期パラメータ

SSHで`scripts/a1-pregen-preflight.py`を一度実行し、CPU affinity/quota、RAM/PSI、圧縮と一時同期書込の概況、ファイルシステムの空き容量をJSONへ記録する。world/cacheと同一ファイルシステムを`--dir`に指定する。preflightは2GiB空き予約＋最大一時書込＋追加安全余裕がない場合、**読み取り計測のみ続けてdisk testをskippedと記録**する。試験中も空き容量を監視し、既存ファイルを触らない。出力JSONも保存先の空き余力を確認してから書く。worldとcacheが別デバイスの場合は、接続先ごとに計測する。短いzlib・fsync測定はJava worldgen、NBT、LOD書込のサービス率を示さないため、そこから最適capやチャンク/sは算出しない。

暫定設定は`cpuQuota=2 OCPU`, `cap=2`, `JVM heap最大8GiB程度`, `system headroom約1.5GiB`。RAM 12GBやCPU割当の実測で調整する。既存15GiB cache＋LOD合算上限、2GiB空き予約と通常FULL優先は変更しない。

### 11.2 制御入力・計測と状態

- `c`：生成要求の上限。2から近傍1刻みで試し、良化が続けば4以上も探索する。ただしnativeの`dispatchLimit`と他のhard guardを上書きしない。`commanded c`／`nativeLimit`／`actualInFlight`／`capDemand`を分ける。
- `a`：**実native dispatchに対する**投入速度と工程別予測credit。LOD writer・FULL writerが詰まらなければ基本透過する。作業クラス`plannedClass=UNKNOWN/NEW/REPAIR`と実際の`resolvedClass`を別集計し、通常FULL取得やskipを不必要に抑制しない。
- 最小限の観測：`PAIR_CONFIRMED_RUNTIME/s`、`NEW/REPAIR/UNKNOWN`、writer arrival ACK/s、**busy中のサービス率`μ_busy`**、busy/idle割合、stage物理WIPとbytes、最古age、30秒期限余裕、LOD同期fallback数と`offer()`壁時計、`skipped`／`dispatchEligible`／`nativeWidthWait`／`creditWait`／cap枠満杯割合、MSPT/CPU/GC/PSI、保存と再試行失敗。

**サービス能力の推定**：`μ_busy`はそのwriterが実際に需要を抱え、サービスを供給している区間の完了数／busy時間か実測サービス時間の保守側推定とする。`ACK/s`をwriterの常時処理能力と同一視しない。aで供給を絞りidleになった区間は`μ_busy`の下降更新に使わず、鮮度付き直近値を保持する。idleで需要不足なら小さくcreditを回復して再観測する。需要と未完了queueがありACKが途絶えたらwriter停止/不明圧力として上方向探索を凍結する。`debtSeconds_j ≈ (queued+executing+futureWork+postTimeoutPhysicalWork)_j / conservative(μ_busy,j)`は観測可能な定常近傍でだけ使い、分母の0を無条件に無限債務扱いしない。

### 11.3 論理attemptと物理仕事を分ける段階別credit台帳

**`attemptOutcome`と`physicalStageWip`は別状態**。30秒の`LodCommitTracker` Future timeout／`CacheManager.captureSnapshot` Future timeoutは保存成果の論理的な不採用判定だが、worker/validation/書込そのものは止まらないことがある。timeout時にその物理WIP・bytesを消して追加投入を許すと、retry→混雑→timeoutの正帰還が生じる。反対にLODがACK済みなのにFULL publishまでLOD creditを保持するとwriterを無駄に遊ばせる。

少数のstage状態（例えば`futureLOD`, `LOD_QUEUE_OR_RUNNING`, `futureFULL`, `FULL_VALIDATE_OR_WRITE`）をattemptごとの小さなbitset/世代付き状態として保持する。UNKNOWNの将来需要を分類時に見直し、実writer入場時に「将来credit」から「物理WIP」へ移す。**各stageの実ACK・確実なキャンセル・その工程が不要と確定したskip**の時だけ対応するcreditをexactly-onceで返す。`PAIR_CONFIRMED_RUNTIME`は成果IDの重複排除と論理attempt解決を担い、残りの全stage creditを一括解放しない。timeout・handoff後のlate ACKが来ても二重返却・負数・遅延上書きを許さない。

追跡できないlate writerやカウンタ欠損がある場合、状態を`PHYSICAL_UNKNOWN`として既存のnative writer queue・CaptureWork lease・inspection permitを利用した保守的圧力へ退避する。無制限Mapは導入しない。実物理終了の通知を得られないときは、計測都合で正式なキャッシュの所有権・採否・hard leaseを強制変更しない。unknownが残る限り積極的な新規投入探索を停止し、外部の信頼できるdrain/再計測で回復させる。計測器のメモリを無限に保持せず、過大な保守的負債で永久停止しないよう、可視のwriter実状態と既存hard leaseに照合した復帰条件を用意する。

### 11.4 E-liteの安全なdispatch境界：二段階の課金

現行Chunksmith 4.6.0では、同期skipとnative safetyループの後、native inFlight加算・dispatchTime計測の後にも、非同期isChunkGeneratedの結果によるLOD既存skipとpreparePregenRequestの最終guard拒否がある。したがって「すべてのskip後、かつnativeの計測開始前」という単一フックは存在しない。前段で実要求数に確定課金するというV5の記述は撤回する。

第一候補は同期skip・native安全確認後に小さな候補WIPを仮予約し、非同期skip・guard拒否・stopでは確実に返し、既存GenerationRequestMixinが横取りする実getChunkAtAsync開始時にactualRequestを一度記録する二段階契約。既存のnext() RedirectはBackfill用のままとし、同じgetChunkAtAsyncへ第二のRedirectを作らない。非同期callbackでのparkやFuture繰延はnative latencyを汚染・デッドロックさせるためしない。native健康ループと再入停止・所有権・player入室を再検査できる安全な仮予約点を証明できなければaゲートを無効化しcap-onlyへ退避する。

WIP creditと時間あたりのrate tokenは別勘定にする。第一候補のFASTはrate tokenを必須とせず、ACKで補充するstage WIP窓を使う。Follow専用のatomic admission epochを用い、AutoPauseからのTaskLoader復元後も最初のdispatch前に閉鎖世代とplayers/manualPause/ownerを再確認する。外部タスクと通常ロードは制御しない。詳細は§13。
  
### 11.5 状態別の閉ループと二方向cap探索

WARMUPは2 OCPU/12GBからc=2・小さな保存WIP窓で開始する。FASTは工程別のoutstanding（future＋physical）をACK時に個別返却する方式を第一候補にする。2〜5秒間隔のtickはdeadline・age・同期fallback・容量/メモリ安全性へ即応し、低頻度ACKのbusyサービス時間学習はイベント数・鮮度に応じて別周期で更新する。AIMDは対照候補とし、単純なACK-windowが十分なら追加のrate積分器を置かない。物理writerのTIMEOUT後WIPは実ACKまたは検証済みcancelまで保持する。

SLOW-UPは生成需要がありcap差が実際のdispatchに効く場合だけc+1を試す。SLOW-DOWNは保存writerが連続稼働しa制限が効いている場合だけc-1で同等goodputと資源効率改善を試す。FASTの安全制御則はc比較中も動かし、比較対象は(c, 同じFAST制御則)の閉ループ全体。c変更時のwriter能力失効、native幅に達しない試行、非同期skip主体、少標本、観測欠損はINCONCLUSIVE。最大試行時間・tail budget・残生成仕事と探索費用の採算条件を設け、不要な再探索は控える。STABLEは設定を維持し実変化時だけ再探索する。

NATIVE_LIMITEDではnativeが許す幅の仕事を継続し、上方探索だけ凍結する。SAFETYは実際のownership、player、容量hard違反、MSPT/OOM、未確認物理仕事に対応する。softなqueue圧力だけなら安全なaゲートが実装・有効な場合に限りタスクを再生成せず投入を抑え、soft/hard/unknownは同時に報告してhardを優先する。現行の保護を緩めず、詳細な導入条件は§13。
  
### 11.6 容量tailとretryの無駄を削る

15GiBのFULL＋LOD合算上限と2GiB予約に近づいたら、観測済み`activeBytes+lodBytes+hardLease`だけでなく、**writer queue・現在生成中・既に投入したLODの追加予測bytes（保守的な上位分位＋不確実性マージン）**を事前のadmission判断へ加える。LODにはFULL capture相当のハード容量leaseがないため、これは厳密保証ではない。観測が古い/他のwriterが活動中なら早めに新規Follow投入を止め、既存inFlightの安全なdrainを妨げず、そのtailが減るまで再開しない。外部の保存が原因で残容量が減った場合も既存のhard判定を尊重する。境界近傍でなければ余計なフル走査や広い容量予約を増やさない。

`PlayerCenter`完走後の`pregenRetryPending`と`CacheManager.preparePregenRequest`の拒否が重なると、少数の永久UNKNOWNだけで同心円全範囲を何度もスキャンする恐れがある。単一探索フローと同心円順を維持し、**原因別retry debtの有界スケジュール**を導入候補とする。`reason/nextEligibleAt/failureCount/neededStateChange`を未完了座標にだけ有限保持し、すべてのpendingがまだ再試行不可なら大域タスクの再起動を抑止する。所有権解放、容量回復、fingerprint/config更新、writer復旧、プレイヤー退出で早期再評価し、理由不明や不変の失敗は上限付き指数backoffで再試行する。未完成を「完了」として扱わず、failure holeを監視する。既存の全座標Mapや第二の独立探索器は作らない。

### 11.7 実装契約、検証と採用条件

cache正本のPAIR・所有権・LOD/FULL ACKと物理writerの観測を`maki-lod-cpu`へ軽量な読み取り専用APIで渡す。イベントは有界で、stageごとのcredit更新は通常定数時間、診断は約60秒にまとめる。台帳上限やsource token欠損で実際の保存成果判定を変更しない。no-op/safe fallbackのfeature flagを用意する。実機preflightだけで生成効率が最適と主張しない。

受入試験： (a) skipが多いタスクでも課金は実dispatch数のみ、(b) nativeの制限/AutoPause中は安全な幅で生成を続けc探索を止める、(c) busy writer→a制限→idleで能力誤学習せず復帰、(d) LODが先にACKしFULLが後で終わる時にLOD枠を再利用、(e) timeout後に残る物理writer仕事・late ACK・再試行でもcredit負数/漏れなし、(f) queue満杯同期fallback中のoffer時間とMSPT、(g) 15GiB/2GiB境界と未書込LOD tail、(h) retry穴1件による範囲再走査抑制と状態変化での復帰、(i) c UP/DOWNの有効需要とwriter idle、(j) player参加/stop・通常FULL競合/Entity/POI/Millénaire/VS2・restart、(k) preflight空き2GiB・2GiB+32MiB・十分な空きでdisk test skip/実行を確認。主指標は全期間の固有PAIR/s・tail debt・CPU秒/PAIR・writer空転・timeout・安全性。優位がなければ簡単な方式へ戻す。

**PR #89は設計とpreflightの更新まで。** E-lite制御器・正本PAIR計測API・runtime最適性の実証は未完了で、実機SSH測定もまだ行っていない。実機で長時間のABBAを必須にはせず、自然運転の小さな局所学習を優先する。

## 12. 追加インライン指摘（16件）の取り込み・判断記録

| 指摘コメントID | 設計上の扱い | 対応節 |
| --- | --- | --- |
| 4525886419 / 4525901427 | **採用**：logical timeoutと物理writer WIPを分離 | §11.3 |
| 4525910892 | **採用**：未来credit／物理stageを分離し、stageごとに個別解放 | §11.3 |
| 4525886425 / 4525901462 / 4525910882 | **採用（安全フック確認が条件）**：skip後、native health再確認付きの実dispatch直前に課金。不可ならcap-only | §11.4 |
| 4525886438 | **採用候補**：原因別のretry成熟時刻で無駄な全域再走査を抑制。完了状態の偽装は禁止 | §11.6 |
| 4525886452 / 4525901473 / 4525910939 | **採用・preflight修正**：2GiB reserve＋最大試験容量＋余裕を維持、空き不足ならdiskのみskipped | §11.1, script |
| 4525901410 | **採用**：ACK到着率とbusy writerサービス能力を分離し、idle低下で自己停止しない | §11.2 |
| 4525901441 | **採用**：native幅縮小のみでSAFETY停止しない。探索凍結しnative許可幅で継続 | §11.5 |
| 4525901451 | **採用**：generation-boundはc+1、storage-boundはgoodput非劣化のc-1を許す | §11.5 |
| 4525910905 | **採用**：LOD同期fallbackは30秒timeoutとは独立の早期危険信号 | §11.5 |
| 4525910915 | **採用（保証水準限定）**：停止後のLOD tail bytesを予測headroomへ反映 | §11.6 |
| 4525910925 | **採用**：cap差が実際のdispatchに作用しなければ比較はINCONCLUSIVE | §11.2, §11.5 |

指摘の採用は**設計契約または比較すべき実装候補としての採用**を意味する。ゲーム実機での最速性やMixin互換、全物理ACKの捕捉を実証したものではない。レビューへの個別返信は不要とし、対応済みスレッドの解決状態で管理する。

## 13. V6レビュー67件の設計判断と新たな第一候補

本節は整理済みのR01〜R67を、単なる指摘数削減でなく独立した故障経路・測定対象ごとに再検討したもの。性能優位が未測定の追加機能は、採用すべき「安全な契約」と、実測してから導入する「高速化候補」に区別する。2 OCPUのA1に常時多変量モデルや巨大な台帳を追加しない。

### 13.1 保存正本の確定と再起動時の証明

CaptureFlightのputIfAbsentで合流した複数の呼出元Futureの成功を、別々の生成sourceのFULL完成と解釈しない。最初のcaptureId・owner/task epoch・source identity・LOD offer/record identityを不変に記録し、後着はJOINED_EXISTINGなどの別結果として観測する。同一FULL公開は単一のcompletionIdentityだけに結び付ける。

LODのstore ACKだけでは、queueに残る旧recordが同期fallbackで先に書かれた新recordを後から同座標上書きしないことまで証明できない。まずnative側に同一座標の書込順序保証があるか検証し、無い場合は古い物理LOD WIPが終わるまで新世代のPAIR確定を保留、競合した座標だけ再読込で再検証する。通常プレイ/村LODの後続更新を損なわない。正確な書込版を証明できなければUNCERTAINとする。

再起動後の同一source FULL＋LOD証明は、メモリ上のCsLodChunk object identityやファイルの存在では足りない。両側ACK後に発行する小さな永続completion receiptを比較対象として具体化する。dimension/pos/fingerprint、FULL/LOD内容識別、source世代、完了状態を保持し、再起動時の実内容と通常FULL所有権を再照合する。sidecar存在だけで完成扱いせず、後続LOD更新で失効または再検証する。atomically renameできても電源断後の耐久性は別問題であり、fsyncやdirectory syncと復旧規則を実ファイルシステムで確認する。

FULLのPromotionClaim.publishにATOMIC_MOVE非対応時の非atomic fallbackがある点を正本契約で明示する。第一候補は非atomic環境で先行PAIR公開をfail-closedとし、そこで運用を続ける必要がある場合だけjournal/marker付き二段公開を別比較する。timeoutと公開の競合にはPREPARED→COMMITTING→PUBLISHED/ABORTEDの単一線形化点を設け、timeout勝利後の旧publish禁止、公開後の結果とreceiptの一貫性、通常FULL勝利を証明する。

論理attempt完了と実物理仕事は別状態。timeout後もFULL writer・LOD writerが生きていれば同座標のphysicalFlightと世代fenceを保持し、古い物理仕事が後着の新世代を公開で上書きしない。LODには論理Futureとは独立した有界record-identity handleを用意し、timeoutでFuture相関を消してもlate writer ACKが物理WIPから差し引かれるようにする。段階別creditは物理ACK・実cancel・stage不要確定時に一度だけ返す。UNKNOWNの実仕事を架空のゼロにしない。未開始のtimeout済VALIDATE/WRITE runnableのみ、キューから安全にremoveできる場合に限り二重permit返却に注意して回収を比較する。guard開始とLOD offer開始の30秒時計も分離する。

### 13.2 FAST：ACK補充stage窓を標準候補、AIMDは比較候補へ

工程jのoutstandingStageCreditはfuture→queued→runningの移行で合計を変更せず、実終了・確実な取消・不要確定だけで減る原子的カウンタを第一候補とする。カウンタ内訳は診断用に記録し、複数値を非原子的に足して入場判定時にWIPが一瞬ゼロに見える競合を防ぐ。予測stage bytesは同一状態世代での保守値を使い、既存hard leaseは二重計上しない。

許容WIP窓はbusy時のサービス能力と、30秒より小さい安全なqueue待ち目標に基づき、実測に応じて低頻度に調整する。数秒ごとのFAST観測はqueue長/最古age・同期fallback・MSPT・heap/PSIへの反応に使う。ACKが0件の短い窓をwriter速度0と断定しない。writer idleを能力低下と誤学習せず、需要不足なら少数の安全な再開を許可。連続のwriter停止・失敗はサービス率低下とは別の安全状態。AIMD/token bucket/PI/MPCは本方式が実際のPAIR goodputで劣る場合のみ比較する。

成功ACK数だけでなく、物理的な成功＋失敗終端数/実busy時間と保存成功歩留まりを別計測する。LOD通常writer・queue満杯の呼出元同期fallback・synchronized store lock待ち・ACK直後の同期callback（fileStamp属性I/Oを含み得る）を無差別に合算して処理能力を水増ししない。共用writerの村/プレイヤーLODは全体圧力に加えるがFollow以外をaで止めない。source不明や同期fallback発生時にはμの上方学習を凍結する。c変更時は2 OCPU上のwriter CPU競合を考慮して以前のμの鮮度を下げる。

長い実行中書込を完了サンプルから落とす右打切りバイアス、NEW/REPAIRおよび可変サイズ、実service timeとqueue待機を区別する。まず件数窓で開始し、予測誤差が有意な工程だけ少数サイズ帯/保守的な実行ageを導入。期限付近のLatencyHistogramの粗い16→60秒bucketを30秒期限の判定へ流用せず、単調時計のtimeLeftとstage age、必要なら固定の細かいbucketで判定する。

PAIRの残り時間は並列に進むLOD ACKとVALIDATE/scannerの遅い方に、その後のFULL writer待ち/圧縮/公開を加える依存DAGで考える。各stage秒数の総和や単純最大だけを正解としない。見積が不確かなら保守的なWIP windowに戻す。FULL側だけのsoft予測予約が詰まる場合に限り、未分類UNKNOWNを少数分類するprobeを比較し、LOD修復を不当に飢餓させない。容量・実WIP不明のhard条件ではprobeを許さない。

### 13.3 ボトルネック計測：処理段階を実際の資源へ対応させる

VALIDATE_IOのworkerとMinecraft chunk scanner（NormalChunkStorageの64 permit）は別資源であり、scanner limitによるUNKNOWNをFULL writer能力不足へ帰属させない。既存READ_IO再検証、初回RecoveryClaims.initializeの同期walk・旧claim回復、世代reconcile、quarantine pruneを通常のwriter serviceと分離計測し、実占有時間は当然end-to-end goodputから除外しない。起動直後の回復が有意な時だけprewarmを比較し、通常ロード/owner fail-closedを保護する。

FULL WRITE_IOはpublish以外にreconcile・prune・claim-retirementを実行する。直列化すべき世代移動と原子的公開を勝手に別workerへ移さず、保守仕事と圧縮の占有時間を分ける。LOD ACK待ちとFULL一時圧縮を重ねるTEMP_PREPAREは、同じsource、validation、容量とtimeout/cleanupを満たせる時にだけ、1枠ほどの有界な比較候補とする。A1でCPU競合やLOD失敗時の無駄が上回れば採用しない。

dispatch前のLOD skip用fileStampとregion再読込、presence index全失効、POI ACTIVEの毎回全走査、owner反射判定なども各々独立のO(N)/同期I/O候補。計測で主要律速と分かった場合だけ、同一regionの世代付きsnapshot、局所invalidate、POI manager identity index等を比較する。外部LOD書込と正本所有権を無視したcache最適化は不可。PipelineMetrics.reportの重い文字列化/ログ出力は、集計ロック内の短いsnapshot後にロック外へ移す低リスク候補。必要性が確認されるまで新しい高頻度座標ログを作らない。

### 13.4 プレイヤー・native・soft/hard pressureの協調

PlayerCenterの一時pressure停止は、E-liteが安定してaゲートでsoft backlogを制限できる段階でのみ、タスクを保ったままの流量待機へ置換する。CAPTURE_BUDGET/INSPECTIONS枯渇のsoftとディスク2GiB/正本所有権のhardは別々に評価する。ResourceStatus.pressureReasonの最初の文字列だけでhard/softを決めない。hard/unknown/softは同時に保持し優先順位hard > unknown > soft、ゲート未実装・計測不明なら現行fail-closedへ戻す。

nativeの単なる幅縮小は追加停止の理由にならない。commandedCapを単一writer主体で管理し、nativeが既に狭めた幅を無理に引き上げない。AutoPauseからTaskLoaderがFollow taskを復元する場合も、player join/manual pause/所有権変更後のadmissionClosed epochを復元後の初回dispatchまで持ち越し、他人のnative taskを横取りしない。native inFlight=0と下流LOD/FULL物理tailゼロは別であり、旧SharedWorkBudgetのBackfill切替期間のみ安全なtail watermarkを比較する。将来の単一Followフローへ不要なBackfill分岐を持ち込まない。

プレイヤー入室時は新規Followを即閉鎖し、既に残るLOD FIFO backlogと共有READ_IOのspeculative読み込みが通常LOD・通常FULLロードを遅らせていないか測る。入室前のFollow LOD queue待機秒数を浅めにする制御を比較し、player/village LODの優先・保存を損なわない。foreground READ_IOのp95/p99悪化時だけ安全な未開始バックグラウンド読み込みの延期または有界優先制御を検討する。

### 13.5 ディスク・RAM・探索費用の非過小評価

FULL/LODのrealPathとFileStoreを識別し、同一FSなら15GiBの論理合算＋共有2GiB reserve、異なるFSなら15GiB論理合算＋各FSの2GiB reserveを照合する。symlink・mount変更・計測不可は楽観視せずFollowだけfail-closed。CacheDiskBudget.refreshが古いusableBytes値で並行FULL publishのdebitを巻き戻すraceを、世代付き差分あるいは同等の保守的スナップショット規則で排除する。LODの未書込tail、再書込のappendによる物理増幅、TEMP_PREPARE一時領域を見込む。

.cslodは同座標更新でもappendされ得るため、region物理増分と新規有効bytesを分け、単一regionのsigned 32-bit offsetが2GiBへ近づく危険を15GiB合算上限と独立に扱う。安全な回復契約がないうちは危険regionへのFollow書込を止める。ただし通常のLOD書込も同じnative writerを使うため、恒久的対策にはnative仕様確認が必要。compactionを行うならopen region handle、atomic交換、stamp失効、再検証、一時空き/電源断復旧をまとめて証明するまで提案段階に留める。

CaptureBudgetのNBT論理bytesを実ヒープ保持上限と同一視しない。ticket/LevelChunk残留、snapshotの一時割当、圧縮bufferのGC、NBT retained bytes増幅を低コストに計測し、必要な場合だけ少数クラスのmemory予測へ反映する。hot pathでJFRや全object走査はしない。2 OCPUでc増加時のcgroup throttlingとwriterサービス低下を同じ窓で観測し、候補自身が起こしたthrottleを都合よく比較から外さない。

retry debtはreason・再試行時刻だけでなくledgerComplete/overflowDirtyも持つ。成熟した座標だけを実際に再投入できるnative入口が証明できるなら座標別retryで全域再走査を削減する。現行iteratorに任意座標seekがない場合、原因別cooldown付き全域再走査を暫定fallbackと明示し、完了の偽装はしない。台帳overflow/再起動で取りこぼした穴がある時は長期sleep・範囲完了宣言をしない。

cの試行はFASTが同じ制御則で動き続ける閉ループ全体を比較する。安全悪化・writer空転増・tail発散で即撤退。c変更が実効dispatchに作用しなかった/分類やsource証明の観測可能率が不均一ならINCONCLUSIVE。試行上限秒・完了件数・tail予算と、残り同心円仕事に対して学習費用を回収できるかの簡易条件を置く。中心側の未完成穴と最古ageは同等goodput時の補助評価とし、外側の軽い仕事だけを数える誤採用を避ける。

### 13.6 検証順と実装を増やし過ぎないルール

P0：正本PAIR、source証明の観測可能率、物理WIP、hard/soft/unknown、非同期skip/guard、各実資源と待機ageの測定を最小限実装。P0の観測が低コストで正確な場合だけ、実制御せずwouldDelay/wouldAllowを記録するshadow gateを短く試す。shadowから実際のgoodput優位は証明できない。

P1：データ競合と保護条件（2段admission、同座標LOD旧record追越し、commit/timeout確定線、再起動後のPAIR証明、preflightの非破壊）を先に成立させ、シンプルなcap-onlyへ安全に退避できる。P2：ACK補充型WIP窓＋局所c探索。AIMDは対照であり最初から必須ではない。P3：計測で明確に必要ならTEMP_PREPARE、retry座標別入口、容量や索引の構造改善。新規thread/worker増設・compaction・PI/MPCは2 OCPUでの全期間PAIR goodputと安全性が明瞭に上がる時だけ比較する。

試験にはsync/async skip、最後のguard拒否、AutoPause復元、timeout中のFULL publish、旧LOD queueとsync fallbackの追越し、late ACK、scanner64枠不足、READ_IO優先、hard＋soft同時圧力、refresh/publish競合、2GiB region offset、低頻度ACK、観測欠損、NEW→REPAIR、中心移動と穴1件、player join／村／船舶／通常FULLを含む。最適化値ではなく正本安全に回帰0を要求し、性能優位不明なら単純な方式を維持する。

**ここまで設計改訂・preflight診断コードの整備であり、ゲームMOD実装やA1実測の性能改善は未完了。**

### 13.7 補足：実装主体、回収、プロファイルの境界

制御値cはFollow-owner内で単一の意思決定主体から更新し、native側のrampUp/backoffと競合して高い値を強制書込しない。複数の安全ガードはmin clampで合流させ、nativeが実幅を回復しないときはcap性能を採否しない（R48）。

timeout後の下流Runnableは、VALIDATE_IO/WRITE_IOキューに残っても着手時にno-opとなることがある。実害がある場合のみ、未開始と証明できるRunnableをThreadPoolExecutor.remove等で回収する候補を比較する。remove成功時のInspectionPermit/lease一回だけ返却と、dequeue競合時の実WIP保持を契約にする。native LOD writerや進行中I/Oはこの方法ではキャンセルできない（R18）。

maki-lod-cpuが正本を観測できるとは限らないので、Source証明対象数・検証成功数・UNCERTAIN/late ACK/イベント欠損率を同じ試行で併記する。c変更で観測可能率が変化したらINCONCLUSIVE。ログ集計のロック自体がA1の2コアを浪費しないようにする（R63/R64）。

preflightはSSH Python自身のaffinity、/proc/statのsystem CPU、cgroup v2のcpu.stat/pressureを**異なるスコープ**として記録し、Minecraftのsystemd unitやコンテナと同じ割当だと断定しない。ターゲットVMの実効CPU/heapはJVM起動後に再検証する。--outputは既存ファイルを上書きせず、O_EXCL/O_NOFOLLOWによる新規作成のみ許す。空き容量が乏しい時はdisk testもJSON出力も安全にスキップして画面へ診断を残す。これらは短期ベンチの生成速度への変換を意味しない（R65〜R67）。

## 14. 整理レビュー67件の採否と追跡

本表は67件を意味で乱暴に統合せず、R番号ごとに独立の採否を残す。各リンクは整理後のインライン指摘。**必須**は実装時に満たす安全・整合性契約、**観測／比較**は計測やABI確認後だけ採用、**第一候補**は最初に試す方式、**修正済**は現PRのpreflight実装に反映した項目。レビュー済みは実装完了・実性能向上の証明を意味しない。

| R番号と論点 | 採否 | 反映先 |
| --- | --- | --- |
| [R01 二段階admissionと非同期skip](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339651) | 必須契約 | §13.1/13.2 |
| [R02 既存Redirectと計測フックの実装境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339664) | 必須契約 | §13.1/13.2 |
| [R03 最終guard拒否の返却](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339680) | 必須契約 | §13.1/13.2 |
| [R04 WIP creditとrate token](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339523) | 観測／比較 | §13.1/13.2 |
| [R05 停止の可視性とepoch](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339617) | 必須契約 | §13.1/13.2 |
| [R06 AutoPause自動再開境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339823) | 必須契約 | §13.1/13.2 |
| [R07 retryの座標別処理](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339919) | 観測／比較 | §13.1/13.2 |
| [R08 retry ledgerの完全性](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339937) | 必須契約 | §13.1/13.2 |
| [R09 CaptureFlightのsource証明](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339020) | 必須契約 | §13.1/13.2 |
| [R10 LOD同座標追越し](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339048) | 必須契約 | §13.1/13.2 |
| [R11 永続PAIR receipt](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339063) | 必須契約 | §13.1/13.2 |
| [R12 FULL atomic publish](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339076) | 必須契約 | §13.1/13.2 |
| [R13 single-flightと世代fence](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339540) | 必須契約 | §13.1/13.2 |
| [R14 timeoutとcommit確定線](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339489) | 必須契約 | §13.1/13.2 |
| [R15 LOD late ACK handle](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339556) | 必須契約 | §13.1/13.2 |
| [R16 物理WIP観測欠損](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339592) | 必須契約 | §13.1/13.2 |
| [R17 WIP残数スナップショット](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339561) | 必須契約 | §13.1/13.2 |
| [R18 timeout済queue回収](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339608) | 観測／比較 | §13.1/13.2 |
| [R19 LOD guard timer起点](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339499) | 必須契約 | §13.1/13.2 |
| [R20 snapshot予測予約](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339570) | 観測／比較 | §13.2/13.5 |
| [R21 NBTとheap占有](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339580) | 観測／比較 | §13.2/13.5 |
| [R22 chunk/ticket滞留](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339314) | 観測／比較 | §13.2/13.5 |
| [R23 PAIR critical path](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339125) | 必須契約 | §13.2/13.5 |
| [R24 期限分位の解像度](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339095) | 観測／比較 | §13.2/13.5 |
| [R25 LOD同期fallbackとμ_busy](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339366) | 観測／比較 | §13.2 |
| [R26 LOD storeの排他ロックとμ測定境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339376) | 観測／比較 | §13.2 |
| [R27 ACK callback仕事](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339389) | 観測／比較 | §13.2 |
| [R28 共有writer source帰属](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339402) | 観測／比較 | §13.2 |
| [R29 c変化とμの鮮度](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339416) | 観測／比較 | §13.2 |
| [R30 物理能力と成功歩留まり](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339437) | 必須契約 | §13.2 |
| [R31 実行中右打ち切り](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339454) | 観測／比較 | §13.2 |
| [R32 サイズ帯別debt](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339468) | 観測／比較 | §13.2 |
| [R33 低頻度ACK統計](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339693) | 採用（計測先行） | §13.2 |
| [R34 ACK補充WIP窓比較](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339709) | 第一候補（ACK-WIP窓） | §13.2/13.5 |
| [R35 UNKNOWN有界分類probe](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339302) | 観測／比較 | §13.2/13.5 |
| [R36 shadow admission評価](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339284) | 観測／比較 | §13.2/13.5 |
| [R37 FAST込みc比較](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339753) | 必須契約 | §13.2/13.5 |
| [R38 online CPU throttle](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339740) | 採用（計測先行） | §13.2/13.5 |
| [R39 残仕事に対する探索採算](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339810) | 観測／比較 | §13.2/13.5 |
| [R40 c試行早期終了規則](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339781) | 必須契約 | §13.2/13.5 |
| [R41 中心側の穴](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339797) | 観測／比較 | §13.2/13.5 |
| [R42 player LOD待機予算](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339726) | 観測／比較 | §13.3/13.4 |
| [R43 foreground READ_IO優先](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339267) | 観測／比較 | §13.3/13.4 |
| [R44 VALIDATEとscanner分離](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531338940) | 観測／比較 | §13.3/13.4 |
| [R45 SCANS permit制約](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339328) | 観測／比較 | §13.3/13.4 |
| [R46 soft pressureとtask存続](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339837) | 観測／比較 | §13.3/13.4 |
| [R47 hard/soft同時原因](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339850) | 必須契約 | §13.3/13.4 |
| [R48 cap単一更新主体](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339247) | 必須契約 | §13.3/13.4 |
| [R49 Backfill下流tail](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339630) | 観測／比較 | §13.3/13.4 |
| [R50 TEMP_PREPARE並走](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339149) | 観測／比較 | §13.3 |
| [R51 FULL reconcile占有](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339170) | 観測／比較 | §13.3 |
| [R52 FULL prune占有](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339197) | 観測／比較 | §13.3 |
| [R53 RecoveryClaims cold start](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531338969) | 観測／比較 | §13.3 |
| [R54 LOD append増幅](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339869) | 観測／比較 | §13.5 |
| [R55 region 2GiB境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339880) | 必須契約 | §13.5 |
| [R56 compactionとstamp失効](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339890) | 観測／比較 | §13.5 |
| [R57 FileStore/symlink境界](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339905) | 必須契約 | §13.5 |
| [R58 usableBytes refresh競合](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339134) | 必須契約 | §13.5 |
| [R59 LOD skip stamp I/O](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531338982) | 観測／比較 | §13.3 |
| [R60 LOD presence index全失効](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339211) | 観測／比較 | §13.3 |
| [R61 ownership reflection overhead](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339232) | 観測／比較 | §13.3 |
| [R62 POI ACTIVE全走査](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339005) | 観測／比較 | §13.3 |
| [R63 PipelineMetricsロック外化](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339108) | 観測／比較 | §13.3 |
| [R64 source証明観測可能率](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339346) | 必須契約 | §13.3 |
| [R65 preflight cgroup資源](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339969) | 修正済（preflight） | §13.7・script |
| [R66 preflight scope差](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339951) | 修正済（preflight） | §13.7・script |
| [R67 preflight出力非破壊](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/pull/89#discussion_r4531339983) | 修正済（preflight） | §13.7・script |

制御器・キャッシュのソースコード変更はこのPRではまだ行わない。レビューの設計条件が実装可能かどうかは、native ABI・Minecraft本体の非同期契約・既存ワールドの正本保証を検証して判断する。
