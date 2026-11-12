# A1.Flex向けE-lite先行生成スループット制御 — 責務分離仕様

Minecraft 1.21.1 / NeoForge 21.1.262。PR #89はChunksmith 4.6.0を基点とし、PR #90の4.8.1／cache 1.4.7へ統合する。

| 項目 | 内容 |
| --- | --- |
| 目的 | 無人時に、安全なFULL＋LOD完成座標をできるだけ速く増やす |
| 対象環境 | Oracle VM.Standard.A1.Flex、Ampere Altra ARM64、2 OCPU、12GB RAM、最大2Gbps |
| 制御方式 | **E-lite：ACK補充型の工程別WIP窓＋低頻度の生成並列数探索** |
| 実装状況 | **E-liteの制御部をlod-cpuへ移管済み**。PR #90のFULL通知・Mixin統合・配布JAR更新・実機検証は未完了 |
| 関連資料 | [PR #90のcache連携契約](e-lite-pr90-cache-integration.md) / [Issue #83](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/83) / [レビュー判断記録（67件＋先行レビュー）](pregen-throughput-review-decisions.md) / [起動前計測](../scripts/a1-pregen-preflight.py) |

**名称と後方互換性:** MODの表示名は「Maki Chunksmith Controller」。内部MOD ID `maki_chunksmith_controller`、Javaパッケージ `maki.chunksmithcontroller`、設定 `config/maki-chunksmith-controller.properties`、配布JAR名を統一した。既存ワールドの所有者記録は維持する。E-liteが本体であり、旧CPU閾値ベースの`Decision`制御は削除した。

**運用データ:** 初回起動時に旧LODやFULLキャッシュを削除する`UnifiedPregenReset`を撤去。既存LODの利用はChunksmith、FULLの利用はcacheの判断に従う。生成制御は旧データの消去・隔離を要求しない。

## 1. 目的・守るべき条件

最適化するのは、後でプレイヤーが再生成なしで使える**正しく保存されたFULLとLODの組**を、単位実時間当たりに新たに確定させる速度である。生成要求数、単独LOD書込数、既存完成物のスキップ数は成果に数えない。中心からの同心円順を守り、中心近くの未完成穴を増やし続けないことも重要な評価条件とする。

次の保存・所有権条件をすべての制御モードで保持する。

- 通常ワールドのFULL、ProtoChunk、POI、Entity、構造物とプレイヤーの変更が正本であり、先行生成FULLは通常ワールドへ直接書かず専用キャッシュへ置く。
- Millénaireの村・住民、VS2の船舶・Shipyard、プレイ中のLOD更新を保護する。正本不明・同一source不明の結果は完成と認定しない。
- プレイヤー参加、通常ロードの優先、所有権の引渡し、既存writerの安全なdrainを守る。Chunksmithのnative safety/backoff/AutoPauseを強制的に解除しない。
- FULLキャッシュ＋LODの合算上限15GiB、必要なファイルシステムごとの空き2GiB予約、既存の正式なメモリ/ディスクhard leaseを維持する。
- Chunksmithの同心円iteratorを尊重し、独立した旧Backfill生成器を増設しない。

速度向上のための手法はこの目的に従って選ぶ。現行制御A/B/A、cap-only、投入制御のみ、E-lite、構造的な保存工程改善を必要に応じて比較する。安全条件と同等の成果速度を保てるなら、単純で資源消費の少ない構成を選ぶ。

## 2. A1の事前測定と初期値

運用前にMinecraft停止状態で、world/cacheが存在する実ファイルシステムを指定して次を実行する。

```bash
python3 scripts/a1-pregen-preflight.py \
  --dir /path/to/server \
  --output /tmp/a1-pregen-profile.json
```

測定内容はCPU affinityとcgroup quota、プロセス/システムの観測範囲、メモリ空き、CPU・メモリ・I/O pressure、短い合成圧縮、および約64MiBの同期書込と小書込fsync遅延。出力ファイルは新規作成のみで、既存ファイル・symlinkを上書きしない。ディスク試験は2GiB予約と試験最大容量・安全余裕を満たすときだけ実施する。FULLとLODが別のFileStoreなら測定箇所も分ける。

**SSHで動くPythonと、systemd/コンテナ内のMinecraft JVMでは利用可能CPU・メモリ上限が異なる場合がある。** 計測プロファイルにはその範囲と未確認事項を保持し、JVM起動後に実効制約を再確認する。合成zlib/fsyncの値からチャンク/sや最適並列数を直接推定しない。

暫定の起点は生成上限`c=2`、JVM最大heap約8GiB、OS等の空き約1.5GiB以上。これらは確定した最適値ではなく、実測・GC・ネイティブメモリ・ディスク状況で安全側へ調整する。CPU使用率が高いだけで生産性が悪いとは判断しない。

## 3. 処理パイプラインと情報源

- **Chunksmith**はチャンク要求・LOD保存・LODの既存再利用を判断する。E-liteはその判定結果、native幅／inFlight、LOD物理保存完了・失敗・queueを観測する。
- **chunk-cache**はFULLキャッシュの検証、再利用、snapshot、所有権、Entity・POI、保存、promotionを判断する。E-liteへFULL固有の検証／物理書込／公開結果を通知する。
- **maki-chunksmith-controller（E-lite）**は両者の通知を試行tokenで対応付け、独立したLOD/VALIDATE/FULL WIP窓、投入制御、PAIR/s計測、cの自律探索を保持する。

```text
Chunksmith（既存LOD判定・native生成・LOD書込）
           ├─ LOD物理ACK ─────────┐
           └─ LevelChunk ─→ chunk-cache（FULLの検証・保存・再利用）
                                      └─ FULL確定 ─┐
maki-chunksmith-controller（投入許可・工程別WIP・PAIR/s・c±1）←──────┘
```

NEW/REPAIRはE-liteの性能比較用の仕事の種類。旧データの有効性判定を変更しない。新しい永続PAIR証明の有無は、既存FULLや既存LODを再利用できるかどうかと無関係である。

| 資源 | E-liteが観測する量 |
| --- | --- |
| Chunksmith | native幅、inFlight、生成要求・完了、LOD writer queue・実物理terminal、ネイティブ停止理由 |
| FULL cache | 検証・書込待ち、実処理時間、正常公開、FULL自身の容量／メモリ安全圧力 |
| サーバー | CPU affinity/quota/使用率、MSPT、heap、GC、PSI、プレイヤー参加 |
| E-lite | stage WIP／窓／最古age、試行token、完成PAIR/s、NEW/REPAIR混合率 |

監視は読み取り専用・低負荷とし、専用の全ファイル走査や追加chunk ticketを発生させない。

## 4. FULL＋LODの完成計測契約

**既存データを使えるかどうかの判定と、今回新たに完成させた仕事を計測する判定を分離する。**

- LODが使えるかどうかはChunksmithが決める。E-liteはその判定を覆さない。
- FULLキャッシュが使えるかどうかはchunk-cacheが決める。新旧形式の互換性・証明書・通常worldへの引渡しはcacheが担当する。
- E-liteは生成試行ごとにtokenを作り、Chunksmith由来のLOD書込完了とcache／通常ワールド由来のFULL確定を照合する。
- 両方が今回の試行で新たにそろい、まだ同じ試行で計上していない場合だけ`PAIR_CONFIRMED_RUNTIME`を増やす。
- 既存LODとFULLのスキップや、再起動後の再確認は新規完成PAIRとして数えない。
- 部分的なセンサー欠損・source不明・競合は`UNCERTAIN`として性能比較から除外する。既存データが有効という所有MODの判定は変更しない。

FULL保存に必要な証明書を導入する場合はchunk-cache内部で管理し、過去の正常なschema3キャッシュを新証明書不在だけで拒絶しない。**E-lite自身は永続的なFULL＋LOD再利用証明書を管理しない。**

実LOD書込とFULL書込の論理Futureがタイムアウトしても、物理仕事が終了したことにはならない。制御枠の返却は物理terminalまたは確実な中止・不要確定時だけ行う。raceした結果は計測上不明として扱う。

通知の具体的なAPI、起点、責任分担は[PR #90 cache連携契約](e-lite-pr90-cache-integration.md)を参照する。

## 5. 入場制御：Chunksmith 4.8.1のネイティブ生成経路を維持

E-liteの対象はFollow-owned Chunksmithタスクである。手動・外部タスクの生成判断には介入しない。

| 時点 | E-liteの動作 |
| --- | --- |
| `ChunkIterator.next()`の前 | Follow owner・stop epoch・共有Backfill予算・WIP窓を確認して待機する |
| 次座標を選んだ直後 | 選択座標を保持したまま、候補WIP枠を取得するまで待機する |
| `World.isChunkGenerated`呼出時 | Chunksmithの非同期判定結果を観測し、NEW／既存チャンクを分類する |
| LOD補完・force load時 | `isChunkGenerated`の呼出しが無くても、先に取得したWIP枠でnative処理を通す。仕事の種類が証明できない場合はUNKNOWNのままにする |
| `World.getChunkAtAsync`直前 | nativeの実要求を記録する。Chunksmithは直前にLOD存在インデックスを更新するため、E-liteは失敗Futureを偽造しない |
| Chunksmithのskip・shape対象外 | nativeの`update`から未開始stageの予測creditを返す |
| 物理stage終了 | 対応する物理terminalまたは確実な不要確定でcreditを一度だけ返す |

この動作はChunksmithの公開ソース`v4.6.0`と`v4.8.1`に共通する。9つのController依存ソースが一致することを確認し、既存の`horizon(1)`およびネイティブ保存・LOD判断は変更していない。

以前E-liteが生成失敗を偽装していた`DEFERRED`/`REJECTED_UPDATES`、updateのキャンセル、全域再試行負債は削除した。手動pause、player join、recenter、native AutoPause、task stop/detachでは、既存のChunksmithタスクライフサイクルを尊重する。

PR #90統合時にはchunk-cache側の重複`GenerationRequestMixin` Redirectを統合し、cache自身のFULL保存安全ガードは維持する。

## 6. FAST制御：工程別ACK補充WIP窓

2 OCPUの環境ではwriter ACKが数秒に1件未満となる期間もあり、2〜5秒ごとの件数だけから投入レートを上下させると振動し得る。第一候補は、**工程別の未完了仕事数の上限窓を持ち、実ACKで空きを補充する**方式である。

工程jの予測された未来需要と実物理仕事を合わせた`outstanding_j`を、単一の整合したatomic合計として保持する。`FUTURE → QUEUED → RUNNING`の内部遷移では合計を変えず、ACK／確実なcancel／stage不要時だけ減らす。診断用future/physical内訳は別集計でよいが、異なる時点の内訳を足して入場判定の一瞬の0を作らない。件数だけで危険を判断できない段階には保守的なbytes上限も置く。hard leaseは別管理し二重計上しない。

概念的な許容量は次で始める。

```text
window_j = bounded(μ_busy,j × target_wait_j, safe_min_j, hard_max_j)
admit    = each required stage has outstanding_j + predicted_j <= window_j
```

ここで`μ_busy`は需要が存在しwriterが実際に仕事を処理した時間に基づく**物理サービス能力**の推定である。完了ACK到着数/全壁時計秒と混同しない。成功・失敗の物理終端を能力に、保存成功率を別の歩留まりに分ける。writer idle中に推定値を下げず、需要があるのにACKが止まる区間は長時間実行中age・writer故障・センサー不明として扱う。同期fallbackが観測されるとき、共有store lockやcaller同期writeを通常workerの並列能力へ二重計上しない。

FAST tick（暫定2〜5秒）は主にqueue oldest age、物理WIP増勢、LOD queue高水位・同期fallback増加、offer壁時計、MSPT/GC/PSI、期限余裕を見て窓を縮小する。`μ_busy`の学習は実終了イベントごとに最低標本数・鮮度を確認して行い、短時間にACKがゼロでも無条件で急減速しない。writer idleで供給不足なら少しずつ窓を戻し、過大burstを避ける。AIMD・token bucket・PI/MPCはこの窓方式より実測性能が優れた場合の比較候補とする。

writerの実行中に重いFULL gzipが残る場合、成功済みの軽い仕事だけで能力を過大評価しない。実行中age（右打切り）、busy区間、未完了の残仕事量も見る。サイズのばらつきが大きい段階だけ、NEW/REPAIRと少数のbyte帯でサービス需要を区分する。cの変更で同じ2コアをworldgenとwriterが奪い合えば、古いμの信頼度を下げ、次のbusy実測から再更新する。

既存のLatencyHistogramは16秒から60秒へ大きく飛ぶため、30秒deadlineの安全判定は各attemptの単調時計`timeLeft`、queue age、必要なら期限近傍だけ細かい有界bucketを使う。LOD offer、capture、guardの異なる期限起点を混同しない。

## 7. 容量・メモリの責任分担

**容量・メモリのhard guardを所有するのは実際にデータを保存する側である。**

- chunk-cacheはFULLキャッシュ、FULL一時ファイル、Entity/POI関連の保持メモリ・空き容量・公開・復元を保護する。
- ChunksmithはLODファイルとnative writerの安全を管理する。LOD regionの容量や再利用条件をcache由来のPAIR計測で変更しない。
- E-liteは両方の読取専用圧力指標、共有ファイルシステムの空き、MSPT/heap/GC/PSIを受け取り、**新規投入を抑制**する。
- 既存の15GiB容量運用、必要なFileStoreの2GiB空き予約などは統合側の条件として保持する。実測値不明のときに余力を推定値で水増ししない。
- cache内部のrefresh、hard lease、atomic publish、promotionなどをE-liteへ移植しない。

共有I/Oの輻輳や外部プレイヤー処理を押しのけない。異常時はE-liteの投入を止め、各MOD自身の保存・復旧処理に委ねる。

## 8. SLOW制御：生成並列数の自律探索

指令上限`c`を2から開始し、native dispatchLimit、実inFlight、需要の有無、cap枠満杯時間、skip、credit待ちを別観測する。nativeが独自backoffで縮めているとき、cを上げても実dispatchは増えない。これは上方探索を凍結する条件であり、それ自体を新規生成の全面停止理由にしない。

- **SLOW-UP**：writerに余力があり、admission制限がほぼなく、候補需要が十分でcapが実際に効くときにc+1を試す。改善が続けば4以上も検討する。
- **SLOW-DOWN**：writerが継続稼働し保存が律速ならc-1を試し、PAIR/sの非劣化とCPU秒/PAIR、GC、bytes WIPの改善を確認する。
- **STABLE**：十分な改善が認められなければ現値を保ち、探索間隔を延ばす。workClass、CPU quota、writer能力、native幅や圧力の持続変化が起きた場合だけ近傍を再探索する。

cの比較中もFASTは同じ制御則で動き続ける。評価するのは「c単独の理想化性能」ではなく、**(c, 同じFASTポリシー)の閉ループ全体**である。cを切り替えた直後のwriter能力再学習、旧capの未完了仕事、変化する中心/NEW比率、sourceの観測可能率の差を記録する。実dispatch差がない、サンプルが足りない、旧仕事が混ざって因果分離できない試験は`INCONCLUSIVE`とする。

試験は安全悪化、timeout増加、writer空転、tail debt発散で即撤退。最大試験秒・最小完成サンプル・tail予算を決め、残り生成仕事で探索費用を回収できない場合はSTABLEを維持する。短い窓で成功件数だけが多い設定を採用せず、未完成の中心側穴・最古ageを同等goodput時の補助評価に使う。CPU quota throttlingの増加が当該c候補から発生した場合、その資源コストも評価に含める。

再起動時の小さな性能profileにはcontroller版、CPU affinity/quota、heap、Chunksmith/writer構成、workClass帯、測定鮮度を付ける。環境が一致する場合に限り安全clampしてwarm startし、異なる場合は初期c=2へ戻す。性能profileの有効性と生成キャッシュのfingerprintは独立させる。

## 9. プレイヤー参加・native停止・復帰

圧力は正本側で`hardBlock`、`softBackpressure`、`sensorUnknown`を同時に評価し、優先順位はhard > unknown > softとする。既存ResourceStatusの単一`pressureReason`を最初の文字列でsoft判定すると、snapshot枠満杯に隠れた空き容量不足を見落とす。hardではFollowの新規入場を閉じて既存drainと保存保護を優先する。softだけなら**安全なaゲートが稼働しているときに限り**taskを残して窓を縮小し、何度もstop/restartして全域scanしない。ゲート未実装時やセンサー不明では従来の安全な停止経路へ戻す。

Chunksmith AutoPauseからタスクが自動復元される場合も、FollowのadmissionClosed epochとplayer/manual pause/owner条件を最初の要求より前に検査する。nativeの許可幅の中で処理可能な仕事は継続してよいが、自動再開がプレイヤー参加後のFollow新規生成を許してはならない。

通常ロードは共有READ_IOのspeculative cache確認に、プレイヤー/村LOD更新は共有LOD FIFOに先行生成の残仕事を抱える場合がある。join前のFollow writer backlogを浅く維持し、join後は直ちに新規Follow入場を停止。通常FULLのREAD_IO p95/p99、プレイヤーLOD offer→ACKと入室時滞留を測り、必要が確認された場合だけ未着手speculative仕事の安全な延期や優先化を比較する。実行中I/Oを無理にキャンセルしない。

native inFlightのdrainと下流LOD/FULL物理WIPのdrainは別状態である。移行期間の旧SharedWorkBudget/Backfill切替で必要なら有界tail watermarkを使うが、すべての外部LOD仕事の終了を待つ仕組みで他タスクを永久停止させない。

## 10. 再試行・不明状態と診断

FAILED、HANDOFF、timeout、UNCERTAINは原因と次の有効再試行時刻を持つ。retry台帳は有界とし、`ledgerComplete / overflowDirty`で未記録の穴の可能性を明示する。台帳が不完全なときに範囲完成を宣言したり、全pendingが未成熟だとして無期限sleepしたりしない。

現在のChunksmith iteratorは通常、同心円の任意座標へ直接seekできない。成熟した少数のretry座標だけを実際に再投入するネイティブAPI/安全なMixin境界が確認できれば座標別retryを導入する。未確認の間は低頻度の全域再走査を暫定手段と明示し、`scannedCoordinates / effectiveRetry`を計測する。旧中心と新中心の探索器を並走させない。

physical stageの通知が欠損・台帳上限超過した場合は`PHYSICAL_UNKNOWN`としてwriter queue、実worker、hard lease、inspection permitを照合する。物理WIPを0と楽観視せず、確認できない間は積極学習を凍結する。既存の保存成功判定・所有権は計測器の欠損から変えない。

診断は`G_total / G_new / G_repair`、source証明成功率とUNCERTAIN/欠損、各stage arrival/physical terminal/success、writer busy/idle、deadline risk、candidate/actual request、native制限理由、stage WIP、retry原因、CPU/GC/MSPT/PSIを低頻度に集約する。

## 11. E-liteの範囲外にある改善

E-liteの対象は生成投入と性能計測である。以下は必要性が別途証明された場合に各所有MODのPRで扱う。

| 所有者 | 独立して検討できるもの |
| --- | --- |
| Chunksmith | LOD writer並列化、region容量、LOD compaction、LOD存在判定の高速化 |
| chunk-cache | FULL writer/VALIDATE_IO、FULL atomic publish、scanner permit、reconcile/prune、FULL容量計測、promotion |
| 通常ワールド・互換MOD | Entity・POI・Millénaire・VS2の永続化とハンドオフ |

E-liteのためだけにこれらの保存形式や有効性ルールを変更しない。

## 12. 実装・受入

| 段階 | 内容 | 状態 |
| --- | --- | --- |
| E-lite本体 | Follow投入・stop epoch・座標取得時の候補admission、実要求観測、工程別WIP、SLOW c±1・プロフィール | PR #89のControllerへ移管済み |
| cache復元 | PR #89にあったchunk-cacheの全差分と配布JARをmainと同一に戻す | 完了 |
| PR #90統合 | cacheのFULL固有の計測通知を追加し、重複Mixinを一本化。ChunksmithのLOD物理ACKをlod-cpuへ連携 | 未完了 |
| ビルド/実機 | 4.8.1実物ABI、Gradle/JAR監査、複製ワールドの旧FULL・LOD再利用、競合・失敗・player join、A1性能比較 | 未完了 |

PR #89単体での一時的不整合は、PR #90に統合するまで許容する。ソース更新後の配布JARが未ビルドの状態を完成扱いしない。

性能比較では同一ワールド条件で、実壁時計の新規完成PAIR/s、CPU/GC/MSPT、失敗・retry・writer tailを観測し、native-only/cap-only/E-liteで比較する。旧データの再利用条件やゲーム内の地形状態に影響しないことを優先する。

[PR #90 cache連携契約](e-lite-pr90-cache-integration.md) と [レビュー履歴](pregen-throughput-review-decisions.md)を参照する。


追加監査：[Chunksmith 4.8.1 Controller制御互換性](e-lite-chunksmith-4.8.1-compat-audit.md)。
