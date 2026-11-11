# A1.Flex向け先行生成スループット制御 — 設計仕様

Minecraft 1.21.1 / NeoForge 21.1.262 / Chunksmith 4.6.0 / maki-lod-cpu 1.4.2 / maki-chunksmith-chunk-cache

| 項目 | 内容 |
| --- | --- |
| 目的 | 無人時に、安全なFULL＋LOD完成座標をできるだけ速く増やす |
| 対象環境 | Oracle VM.Standard.A1.Flex、Ampere Altra ARM64、2 OCPU、12GB RAM、最大2Gbps |
| 制御方式 | **E-lite：ACK補充型の工程別WIP窓＋低頻度の生成並列数探索** |
| 実装状況 | **設計段階**。制御MOD本体とPAIR計測APIは未実装、A1実測も未実施 |
| 関連資料 | [Issue #83](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/83) / [レビュー判断記録（67件＋先行レビュー）](pregen-throughput-review-decisions.md) / [起動前計測](../scripts/a1-pregen-preflight.py) |

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

## 3. 処理パイプラインと観測する資源

FULL生成と既存FULLのLOD修復では必要な工程が異なる。各仕事は入場時の`UNKNOWN / NEW / REPAIR`と、解決後の実際の作業クラスを分けて追跡する。

```text
Follow所有権・中心からの座標選択
  ├─ 既存FULL/LODのskip判定・READ_IO
  └─ native安全確認 → async isChunkGenerated
        ├─ skip / guard拒否 / handoff
        └─ getChunkAtAsync → ネイティブ生成・ticket・LevelChunk
              ├─ LOD抽出 → LOD offer → 共有store/writer → 実write ACK
              └─ NEWのみ: サーバーthread snapshot
                    → VALIDATE_IO → 非同期Minecraft scanner/owner確認
                    → FULL WRITE_IO圧縮・公開 → FULL publish receipt
              └─ 同一source・所有権・確定線の確認 → PAIR確定
```

LOD ACKとvalidationは一部並行に進み、FULL公開は通常その両方の成功後に進む。したがってNEWの残りcritical pathは概念的に`max(LOD残待ち, validation残待ち) + FULL公開待ち/実行`となる。各工程のp95の単純合計や最大値だけを期限予測へ流用しない。

資源別に以下を区別する。

| 資源 | 観測する量 | 誤診しやすい点 |
| --- | --- | --- |
| native生成 | 指令cap、native dispatchLimit、実inFlight、候補数、skip、slow理由 | cap増加が実dispatchへ反映されない |
| main-thread snapshot | 1tickの処理回数と時間、MSPT、ticket/LevelChunk滞留 | 圧縮後bytesではヒープ負荷が分からない |
| LOD store | queue/最古age、write ACK、同期fallback、offer壁時計 | queue満杯で呼出元が同期writeする |
| VALIDATE_IO | executor待ち、RecoveryClaims初回回復、scanner投入 | scanner Futureと64 permitは別資源 |
| Minecraft scanner | inFlight、permit拒否、完了時間、UNKNOWN理由 | permit枯渇をwriter律速と誤認する |
| FULL WRITE_IO | publish圧縮/rename、reconcile、prune、claim保守の各占有 | メンテナンス中の低ACKは圧縮性能を示さない |
| READ_IO | 事前キャッシュ検証と通常ロードの待ち時間 | player復帰時にFIFO先行処理が残る |
| 正本・容量 | source照合、物理WIP、所有権、hard lease、FileStore | 論理timeout後に実writerが残る |

新たな全ファイル走査やchunk ticketを計測のために追加しない。非同期の段階境界に定数時間相当の有界カウンタを置き、60秒程度で診断を集約する。PipelineMetricsのログ整形・出力は集計ロック外へ移す方式を比較し、計測そのものがwriterやserver threadを待たせないようにする。

## 4. FULL＋LOD完成の正本契約

**attemptId** はFollow所有taskのepoch・世代・要求番号に基づく試行識別子であり、再試行ごとに異なる。**completionIdentity** はdimension・座標・fingerprint・正本の新たな完成遷移の識別子である。再試行や旧callback、再起動後の再確認で同じ完成を二重計上しない。NEW/REPAIR分類の成功分布だけで評価母集団を組み替えない。

段階の保証水準は次のとおり。

| イベント | 保証内容 |
| --- | --- |
| `LOD_WRITE_ACK` | 当該LOD recordの物理書込呼出しが成功終了した。永続fsyncや後続上書き不在は含意しない |
| `FULL_PUBLISHED` | 所有権と確定線を検証し、要件を満たすFULL公開が成立した |
| `PAIR_CONFIRMED_RUNTIME` | 同一生成sourceのLOD・FULL、完了の新規遷移、所有権が実行時に証明された |
| `PAIR_REVALIDATED_AFTER_RESTART` | 再起動後の内容・世代・通常FULL優先を再検証した。新規runtime成果数には再加算しない |
| `UNCERTAIN` | 上記を証明できない。完成として計上しない |

CaptureFlightで同座標のFutureへ後続呼出しが合流したときは、最初のcaptureId・source・LOD offer/recordとの対応だけが正本receiptを発行する。後着のJOINED_EXISTING/SUPERSEDEDを新しい成功としない。

LOD writerのqueueと同期fallbackは同座標の古いrecord/new recordの**物理書込追越し**が起きる可能性がある。書込時に順序・世代を保証できることを確認し、保証がなければ古い物理LOD仕事が完了するまで新しいPAIR確定を保留し、競合座標だけ再検証する。プレイ中のLOD更新との競合も含め、証明不能ならUNCERTAINにする。

FULLの公開は、timeoutと同座標別世代の公開が競合しても一つの確定線で処理する。`PREPARED → COMMITTING → PUBLISHED/ABORTED`の状態遷移または同等のCAS/ロックで、timeout先行時の旧公開禁止、公開先行時のreceipt、通常FULLの勝利を整合させる。ATOMIC_MOVE非対応の非atomic fallbackは原子性を満たさないため、先行PAIR公開では第一候補としてfail-closedとし、対応環境が必要な場合はjournal/marker方式を別途評価する。

論理Futureの30秒timeoutは物理workerの終了を意味しない。FULLのphysicalFlightとLOD record-identity physical handleを論理結果から独立して追跡する。timeout後のlate ACKも実物理仕事の終端へ対応させ、古い世代が新しい正本を公開で上書きしないようgeneration fenceを掛ける。LodCommitTrackerのpending除去だけを物理完了としない。

再起動後に「同じ生成結果」を証明するため、小さい永続的な完成receiptを設計比較する。dimension/pos/fingerprint、source世代、FULL内容識別、LOD record/版識別、公開状態を含め、実ファイルを検証する。sidecarの存在のみでは確定しない。rename成功と電源断時の耐久性は別の保証であり、fsync・ディレクトリ同期と回復経路を実機で検証する。永続の証明が実装できるまで、再起動後の互換性・完成判定を楽観視しない。

PregenBorrowLedgerのguard開始と実LOD offer開始は異なるため、30秒の論理期限もそれぞれの実起点から追跡する。offer前のguard timeoutをwriter完了とみなさず、停止・復帰・旧task callbackとの競合を保護する。

## 5. 入場制御：非同期skipを含む二段階契約

Chunksmithでは同期skipとnative safetyを通過した後にも、`inFlight++`・`dispatchTime`の後で非同期`isChunkGenerated`がskipする。最終の`preparePregenRequest`もgetChunkを拒否し得る。すべてのskip後でnative計時前という単一フックは存在しない。

| 時点 | 行うこと |
| --- | --- |
| 同期skipとnative safety通過 | Follow-owned候補に限り、有界の`CANDIDATE_RESERVED`仮WIP permitを確保 |
| 非同期isChunkGeneratedでskip | 必要のなくなった将来stage creditをexactly-once返却 |
| 最終guardで拒否 | 同様に返却し、理由別retryを記録 |
| 実getChunkAtAsync開始 | `ACTUAL_REQUEST_STARTED`を一度記録し、必要なstageの予約へ転換 |
| 物理stage終了 | 対象工程のACK/確実なcancel/不要確定でcreditを一度だけ解放 |

既存`GenerationRequestMixin.maki$measureRequest`のgetChunkAtAsync Redirect二箇所を計測入口として利用し、同じinvokeへの二重Redirectを避ける。`ChunkIterator.next()`で働く既存Backfill barrierは保持する。非同期callback内でparkしてcreditを待つとnativeのslow-chunk計測を汚染するため、仮予約はnative安全ループと協調した非ブロッキング経路に置く。安全な注入点とABIの整合が証明できなければaゲートを無効化しcap-onlyへ戻す。

候補WIP permitは返却可能な同時仕掛枠である。必要になった場合の時間当たりrate tokenは別会計とし、skipの返却によって過去の流量tokenを無限に再発行しない。UNKNOWNは保守的な需要を持つが、FULLのsoft creditだけが詰まり、hard条件が正常なときに限り、極小の有界分類probeを許可する。別の座標探索器は作らない。

プレイヤーjoin、手動pause、recenter、stop、AutoPauseからのタスク復元ではFollow-owned admissionのatomicなepoch/closedを更新・再検証する。native taskの非volatileなstoppedだけに安全を依存させない。復元されたFollow taskの最初の要求前にはplayers・owner・center・manual pauseを確認する。外部Chunksmithや別dimension taskを誤って取得しない。

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

## 7. 容量・メモリと安全余裕

**容量判定には二つの独立した上限がある。** FULLキャッシュ＋LODの合計15GiBと、実際のファイルシステムで必要な空き2GiBである。FULL/LODのrealPathとFileStoreを照合し、別デバイスの場合は各FSの空き予約を独立に評価する。symlink・mount変更で実体と消費量を把握できなければFollowの新規投入を止め、通常ワールド保存を優先する。

CacheDiskBudgetの容量refreshが始まった後にFULL publishが発生しても、古い`usableBytes`スナップショットでその減算を巻き戻さない。publish世代と差分の保守的反映、または同等の整合性契約を設ける。正本のhard leaseに加えて、writer queue、進行中チャンク、LODの将来物理増分、FULL一時ファイルを投入前の余力予測に含める。予測値は保証ではないため、hard guardは最後まで残す。

ChunksmithのLOD regionは既存座標更新でも末尾へappendし得る。物理ファイル増分と有効レコードbytesを区別し、同一regionのsigned 32-bit offset（約2GiB）限界を合計15GiBとは別に検査する。危険regionへの先行LOD更新は安全側に制限する。通常LODまで含む根本対策やcompactionにはnative writer・readerのopen handle、同期、原子置換、fileStamp失効、必要な一時容量、電源断回復の保証が必要であり、性能測定後に独立評価する。

メモリには圧縮前NBTの論理bytes、実ヒープ上のsnapshot/オブジェクト/圧縮buffer、保持LevelChunk/ticketを別々に考慮する。CaptureBudgetの64MiB（論理NBT）を実heap最大値とは見なさない。必要な場合のみ低頻度のretained/allocated/GCサンプルから保守係数を調整し、ホットパスでJFRやオブジェクト全走査を実行しない。

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

## 11. 計測で判断する構造的な改善

次の案は、律速が確認された工程にだけ適用する。E-liteの設計と独立に単体の効果を比較する。

| 対象 | 比較候補 | 採用時の確認 |
| --- | --- | --- |
| `LodCommitTracker` | O(P)の`containsValue/removeIf`からrecord identityごとの有界handleへ | 同期fallback、late ACK、複数offer、cleanupの整合 |
| FULL writer | gzip・temp I/O・publishを測定し、必要ならLOD ACK待ちと有界`TEMP_PREPARE`を並走 | 一時容量、2コアCPU競合、失敗時破棄、最終publishの所有権 |
| メンテナンス | FULL reconcile/pruneとpublishの占有を分類、必要ならアイドル期に小分け | 世代入替/削除と公開の排他を維持 |
| 容量走査 | `Files.walk`の時間を測り、保守的増分計数＋定期reconcileを比較 | stale scan、並行publish、15GiB/2GiB条件 |
| skip判定 | region fileStamp再読込・LOD presence index全失効の頻度を測定 | 外部writer変更時の座標単位再検証 |
| POIと所有権 | POI ACTIVE全走査、反射owner判定の実コストを確認 | manager identity、ワールド寿命、handoffを維持 |
| scanner | 64 permit枯渇・UNKNOWN、cold-start RecoveryClaimsを独立計測 | 通常FULL優先とfail-closed |
| retry queue | timeoutした未開始Runnableだけを安全にremoveする候補 | dequeue競合、inspection permit、二重lease返却 |

大幅なwriter並列化、LOD region compaction、保存形式変更、通常ワールドへのFULL先行保存は、独立した安全設計と実機検証が必要な構造変更として扱う。

## 12. 実装順・受入試験

| 段階 | 実装内容 | 完了条件 |
| --- | --- | --- |
| P0 | 正本PAIR/source証明、physical handle、stage/pressure・native/IO計測、preflight | timeout後の物理仕事・観測欠損を正しく表現。計測負荷を評価 |
| P1 | 確定線・世代fence・LOD追越し対策、二段階admission安全点、stop epoch | 所有権・通常FULL・同一sourceに回帰なし。安全点が不明ならcap-only |
| P2 | ACK補充WIP窓、cの近傍学習、STABLE/安全退避 | 全期間PAIR goodputとCPU/GC/timeout/tailがnative-only/cap-onlyに対して非劣化以上 |
| P3 | 実際に律速だった構造改善だけを個別適用 | 改善が計測可能で、安全性と保守負荷に見合う |

受入試験には、同期/非同期skipと最終guard拒否、AUTO PAUSE後のplayer join、capture重複と旧source、LOD旧queueの後書き、timeoutとatomic/non-atomic publish競合、再起動と永続receipt検証、late ACK、scanner permit不足、LOD同期fallback・低頻度ACK、FULL prune/reconcile、2GiB FileStore reserveとLOD region offset境界、soft＋hard圧力、先行READ_IOと通常ロード、村/VS2/POI/Entity、retry台帳overflowと中心移動を含める。Mixinの実バイトコードとNeoForge/ChunksmithのABIを確認する。

成果比較は固定seed・未生成状態・MOD/config・quota・同じ座標集合とキャッシュ/LOD初期状態の複製ワールドで行う。全壁時計時間に探索・停止・writer tail・native backoff・retryを含め、成功済みだけを採用評価の分母にしない。A1本番では長時間の強制A/B/Aを常時実行せず、自然生成中の短い観測と必要な局所探索に留める。

[設計判断の67件のレビュー追跡と先行レビュー](pregen-throughput-review-decisions.md)を参照。
