# 設計案: Chunksmithの確定FULL＋LOD最大化に向けた適応制御 V2

**ステータス: 設計レビュー用。コード・mod jar・configは変更しない。**
**対象: maki-lod-cpu / maki-chunksmith-chunk-cache / Chunksmith 4.6.0（NeoForge 1.21.1）**
**仕様上の正本: [Issue #83](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/83)**

## 1. 狙いと必須の不変条件

無人時の同心円順の先行生成で、**正常に確定した固有座標のFULL＋LODペア数／実経過時間**を最大化する。要求完了数・LOD書込数・CPU使用率は診断指標であり、最終目的ではない。新規FULL＋LOD完成と、既存の通常FULLまたは確定済みキャッシュFULLからのLOD修復は、両方とも有効な進捗と数える。ただし処理種別ごとに負荷が異なるため、比較統計は分けて扱う。

安全性を成果より優先する。通常ワールドのFULLが常に正本、ProtoChunkはFULLキャッシュに採用しない。エンティティ・POI・構造物・プレイヤーの建築変更・Millénaireの村・VS2のShipyard、昇格ACK、所有権、互換性フィンガープリント、未完了成果の不採用、座標単位の失敗隔離を保持する。Chunksmithの同心円走査と各座標の作業選択を変更せず、別のBackfill探索器や座標順の並べ替えは追加しない。

プレイヤー接続時は新たな先行生成投入を直ちに停止する。既存処理は安全にdrainし、通常プレイのロードと保存は妨げない。FULLキャッシュ＋LODの約15GiB上限とディスク空き2GiB予約を維持する。Chunksmith自身のMSPT・チャンクロード・LOD・書き込みキューなどの安全制限を無効化しない。性能探索のために完成済みキャッシュを削除したり、同じ座標を無駄に再生成したりしない。

最適化の優先順位は次の辞書式とする:
1. データ保全、プレイヤー応答性、OOMとディスク枯渇防止（破ってはならない制約）。
2. 実測の確定ペアgoodput最大化。
3. goodputが実用上同等ならCPU秒／確定ペア、GC、I/O負荷、切替回数が少ない構成。

CPU使用率が高いこと**だけ**を理由に減速しない。メモリやI/O待ちが多い場合、2コアの機械でも並列数が2を超えた方が速くなる場合がある。

## 2. 現行1.4.2の問題と根拠

現行 [AdaptivePolicy.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/AdaptivePolicy.java) の評価値は、概ね min(Chunksmith要求完了数, LODストア保存成功数)/秒。これでは「同じ座標でFULLキャッシュが公開され、対応LODも確定した」ことが保証されない。別目的のLOD更新数が全体ストアに混ざる可能性があり、FULL保存失敗と遅延も評価できない。

[CpuGovernor.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/CpuGovernor.java) はネイティブストアの総書き込み差分とFollowタスク完了を組み合わせる。非同期生成・書き込みが長く続く場合、並列上限を切り替えた後に古い設定の要求が完了しても、新しい期間の成果に混ざる。[GenerationTaskMixin.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/mixin/GenerationTaskMixin.java) は上限をclampするが、Chunksmith自身の並列制限も働くため、指示値と実効値は一致しない。

A/B/Aは地形変化を多少抑えるが、35%の基準許容差や5%の採用閾値では、長尾遅延・生成場所の変化・少量イベントの偶然に対する十分な証拠とはいえない。2026-11-10のログでは2→3、3→4、4→6の採用例がありながら、短い窓の値が大きく変動した。740件のFULL保存の実体は確認できたが、コントローラには直接その数が入っていない。

## 3. 正式な成果計測: Coordinate Completion Ledger

**保存完了を本当に判定している maki-chunksmith-chunk-cache が、読み取り専用の成功イベントを発行する。** 制御器は結果を観測するだけで、保存の採否を決定しない。

イベントの最低仕様:
- REQUEST_ADMITTED: taskEpoch, dimension, chunkPos, fingerprint, workClass, requestedCap, nativeEffectiveWidth, monotonic admission time。
- NATIVE_FINISHED: 要求終了時刻、完了・失敗、チャンクロード時間（これは成果扱いしない）。
- LOD_DURABLE: 当該座標のネイティブLOD保存成功を確認した時刻。ストア全体の差分から捏造しない。
- FULL_DURABLE: 新規FULLのatomic publishと確認、またはLOD修復における通常FULL／確定済みキャッシュFULLの権威確認。
- PAIR_CONFIRMED: 当該座標の必要なFULLとLOD、所有権と互換性が確定し、未完成から完成への状態遷移が証明できた時点で1回のみ発火。
- PAIR_FAILED / RETRY_PENDING / HANDOFF: 失敗・未確定・プレイヤー引き渡しを追跡。スキップだけでは完成イベントを増やさない。

workClassは NEW_FULL_PAIR / NORMAL_FULL_LOD_REPAIR / CACHED_FULL_LOD_REPAIR / ALREADY_COMPLETE / INELIGIBLE を区別する。ALREADY_COMPLETEの再検証を新規成果として加算しない。LOD修復は「実際に未完成から確定済みになった場合」だけ数える。

IDはtaskEpoch、dimension、chunkPos、fingerprint、当該completion世代で重複排除する。古いタスクの遅延完了が新タスクの結果を上書き・二重計上しないこと。再起動後の計測器のリセットはデータの完成状態を変更しない。既存の所有権・昇格・LOD確認を通した「確定」のみを受け取る。

**追加計測は軽量**とする。NBTの再読込、チャンクticket追加、ディスク全走査、main threadでの新たなブロッキング操作はしない。atomic aggregateと有界cohortを利用し、失敗時は計測欠損として安全側に倒す。センサーの故障をキャッシュ成功判定に伝播させない。

## 4. 数式と測定の意味

~~~text
G_total = Δ(unique NEW_FULL_PAIR confirmed + unique LOD_REPAIR confirmed) / T_eligible
G_new   = Δ(unique NEW_FULL_PAIR confirmed) / T_eligible
G_repair= Δ(unique LOD_REPAIR confirmed) / T_eligible
Efficiency = Δ(unique confirmed pairs) / Δ(process CPU seconds)
~~~

T_eligibleは、無人かつFollowタスクが所有され、ディスク容量と安全条件が許可し、対象作業を投入できる実経過時間。プレイヤー入室・外部要因のディスク容量制限で投入不可能な時間は比較から除く。ただし、試した並列数が引き起こす生成遅延、LOD待ち、保存詰まり、後処理待ちを都合よく除外してはならない。

仕事の種類が異なると単位時間あたりの件数も違うため、NEW・NORMAL REPAIR・CACHED REPAIRの層ごとに比較する。各層を元の同心円走査の実際の作業比率で合成し、層が偏りすぎて比較不能なときは採用しない。成果の計測のために生成順を変えない。

必須の補助メトリクス:
- 要求投入→ネイティブ完了→LOD ACK→FULL ACK→PAIR_CONFIRMEDの段階別p50/p95/p99と長尾pending件数
- requestedCap／実効native width／inFlight／Chunksmith native slow chunk制限と変更理由
- FULLの保存キュー長・保存失敗率・容量予約・LODキュー・同期書込fallback、失敗理由
- cgroup CPU使用時間とthrottling、process CPU、MSPTのp95/最大、heap headroom、GC、memory PSI、I/O PSI
- owner識別・workClass・measurement freshness・古いepochからの遅延完了・探索による切替回数

計測欠損を0件の完成として処理しない。「要求が完了しない」「ストアが詰まる」「Chunksmithが制限」「本当に遅い地形」を分ける。

## 5. 二層の制御

### 層A: 安全制御（原則1～2秒、緊急時はイベント即応）

hard stop: player入室、所有権喪失、ディスク枯渇／15GiB上限、互換性未確定、正常保存に危険な状態。新規先行生成投入を止めて安全にdrain。通常チャンク保存は保護する。

emergency reduction: OOM・cgroup OOM、極端なMSPT超過、深刻なメモリ不足、永続化キューの持続的発散など。安全な上限まで速やかに減速する。復帰にはヒステリシスと観測鮮度を要する。初期閾値は現行の安全閾値を踏襲し実測で調整する。

soft pressure: GC、memory PSI、I/O PSI、writer backlog、native heldを複数回観測したら探索を中断し、維持または段階減速する。CPU高使用率単独では減速しない。Chunksmithネイティブの抑制を上書きせず、ネイティブがすでに律速なら「さらに上限を増やす実験」を停止する。

missing data: 取得不能・古い観測は未知のまま記録し、新規探索は保守的に停止する。構成不明を正常値に置き換えない。既存のプレイ用ロードや保存は保護する。

### 層B: 遅延フィードバック付き局所bandit（数十秒～数分）

最良実績のある並列上限c_bestを維持し、必要な場合のみ近傍のc_best±stepを試す。探索ステップは現在の幅、未利用の実効並列、資源の余裕から調整し、無条件の1～64全探索をしない。下方向への探索はgoodputがほぼ同等でCPU・GC・I/Oコストを減らせるかを確認するためにも行う。CPU占有率そのものから最適な並列数を決めない。

**常にA/B/Aを繰り返さない。** 候補を比較する際は、タスクepochに対して決定的に初期順序を選んだABBAまたはBAABの短いswitchbackを使う。A=安定実績、B=近傍候補。元の同心円チャンク順は変更しない。移動する地形の影響を和らげるための構成で、完全な因果比較を保証するものではない。

非同期の結果帰属は、要求投入時のepoch・arm・cap・workClassを必ず保持する。設定を変えてから完了した古い作業を新しいarmへ計上しない。変更直後に旧仕事と新仕事が混ざる区間を純粋な比較期間として扱わない。候補の効果は完成したcohortだけで断定せず、pendingの年齢・未完了を含めて後から評価する。評価のために長時間生成を停止しない。上限時間内に有効なサンプルが集まらなければinconclusiveにする。

各armについて複数の時間ブロックと十分な保存確定イベントを要する。少数の個別チャンクを独立サンプルとみなさず、時間ブロックで自己相関を扱う。事前に定めた測定期間・イベント数・信頼水準、逐次検定なら誤採用率調整を用い、**実用上の改善量を超え、保守的な信頼区間でも改善している時**にのみ新設定を採用する。5%の点推定だけでは判断しない。

有意な良化が確認されなければ最良設定を保持する。性能が実用上同等ならCPU秒／確定ペアが小さい方を選ぶ。繰り返し改善なしなら探索頻度を減らし、長い安定運転を行う。生成遅延、workClass比率、native制限、CPU quotaなどの状態変化を検出した時にのみ再探索。探索に使う時間の割合に上限を設定し、「実験のために遅くなる時間」を抑える。

c_bestは絶対的な最適解ではなく、その時点で観測上最良の選択肢。学習値はキャッシュ互換性と独立した小さな状態に保存でき、破損時は破棄して安全な初期値に復帰する。キャッシュファイル自体を失効させない。

## 6. 状態機械（擬似コード）

~~~text
on_sample(s):
    if player_online or task_unowned or disk_budget_denied or fingerprint_unknown:
        stop_new_pregen_admissions()
        preserve_normal_world_and_drain_safely()
        suspend_optimizer()
        return

    if hard_pressure(s):
        reduce_to_safe_cap_with_hysteresis()
        mark_comparison_contaminated()
        return

    if sensors_stale(s) or native_backpressure(s):
        keep_or_recover_safe_cap()
        pause_exploration()
        diagnose_bottleneck()
        return

    ingest_coordinate_pair_ack_events()
    update_pending_cohorts_and_latency_histograms()

    if native_effective_width_is_limited or storage_pipeline_saturated:
        keep_cap_and_diagnose()
        return

    if comparison_active:
        continue_bounded_ABBA_or_BAAB()
        if sufficient_comparable_confirmations():
            adopt_candidate_only_if_robust_gain()
            enter_stable_run_and_apply_exploration_backoff()
        return

    if model_empty or workload_drifted or safe_recheck_due:
        candidate = select_local_neighbor(best_cap, safety_headroom)
        begin_delayed_feedback_switchback(candidate)
    else:
        use(best_cap)
~~~

現行の最大並列数はChunkSmithネイティブの上限より上げない。実効幅がずっと2なら、上限を9にする実験より「ネイティブがなぜ2に制限したか」を優先して分析する。プレイヤーがいない短いidle切替で学習済みの結果を無条件に2へリセットせず、ownership・構成・測定鮮度・安全性を評価して再利用する。

## 7. 既存MODへの実装割り当て

**maki-chunksmith-chunk-cache:** CacheManagerのPublicationGate、confirmation ledger、LOD確認を通過したPAIRED_CONFIRMEDイベントのみを発行する、軽量なPregenCompletionTelemetryを追加。修復と新規生成の区別、失敗・pending、古いepochの重複排除を担当。現行PipelineMetrics.report()を現役の定期保守経路から確実に呼び、旧Backfill制御器に結び付いたログ出力依存を解消する。保存・昇格の安全制約を変更しない。

**maki-lod-cpu:** 計測専用のPregenTelemetryBridge、安全制御のSafetyController、純粋ロジックAdaptiveThroughputOptimizerを構成。GenerationTaskMixinは要求上限と実効幅を安全に扱う。外部MODに対する強制上限解除はしない。新しい計測APIはキャッシュMODがいない・旧版・計測不能の時に互換性のある保守的な動作へ降格する。

**maki-idle-pregen / maki-millenaire-lod-compat / maki-ship-save:** 今回の制御対象ではない。player入退場、無人時freeze、通常LOD更新、村と船舶の保存について結合テストで安全性を確認する。

通常INFOログには60秒ごとのG_total / G_new / G_repair、確定失敗／保留、requested/native cap、各圧力、学習モード、採否理由と信頼度を出す。個別座標の高頻度ログや新たなI/Oは避ける。

## 8. 導入計画・比較テスト・採用基準

1. 計測契約だけを導入し、現行制御と独立してFULL+LODの確定goodput、FULL失敗、長尾pendingを測る。総LOD書込数と確定ペア数の差を明示する。
2. feature flagでV2制御器を実装する。計測の不整合・エラー時は既存の安全な経路へ戻す。現行のChunksmith安全制約を保持する。
3. モデル単体テストでは遅延完了、旧epoch、重複callback、LODだけ成功／FULLだけ成功、保存失敗、player接続、傾向変化、native並列抑制、0件窓、CPU制限変更、長尾分布を検証する。
4. CIでNeoForge 21.1.262、Chunksmith 4.6.0、Mixin適用／ABI、jar再構築、既存のキャッシュ保存・昇格・POI／Entity保護試験を検証する。
5. 本番ワールドを削除せず、同条件の複製テスト世界・同じseedと生成MOD・同じcgroup CPU制約・同じ範囲から現行1.4.2方式とV2方式を比較する。試験順を交互にし、複数回実施する。探索時間も含む全経過時間の確定goodput、資源消費、安全圧力、失敗件数、回復時間を評価する。
6. V2の採用条件は、データ保全とプレイヤー応答性の回帰0、保存失敗率や再起動後の成果不採用に悪化なし、複数回の同条件試験で統計的に信頼できる確定goodput改善、または同等goodputでより少ない資源消費があること。改善不明なら現行方式を保持する。

**仕様の保証範囲:** この方式は非定常負荷に適応して安全な実用速度を狙う設計であり、あらゆる地形・ハードウェアで世界最速という証明ではない。最終採用は同条件の実測により判断する。今回の設計PRはコード未実装のため、CI緑や動作改善を主張しない。

### 対応根拠
- [Issue #83 統一FULL＋LOD仕様](https://github.com/tomo8812/Minecraft-1.21.1-neoforge-server/issues/83)
- [AdaptivePolicy.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/AdaptivePolicy.java)
- [CpuGovernor.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/CpuGovernor.java)
- [GenerationTaskMixin.java](../custom-mods/maki-lod-cpu/src/main/java/maki/lodcpu/mixin/GenerationTaskMixin.java)
- [CacheManager.java](../custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/CacheManager.java)
- [GenerationRequestMixin.java](../custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/mixin/GenerationRequestMixin.java)
- [PipelineMetrics.java](../custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/PipelineMetrics.java)
