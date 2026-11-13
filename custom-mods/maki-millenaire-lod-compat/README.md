# Maki Millénaire Chunksmith Compatibility 1.0.9

Minecraft 1.21.1 / Chunksmith 4.8.1 / Millénaire 9.1.0の事前生成互換MODです。

Chunksmithの事前生成中に村・孤立建築の候補を保留し、プレイヤーがその領域をロードしたときにMillénaireのキューへ戻します。建築前に、必要な領域のFULLがロード済みで、[Chunk Cache](../maki-chunksmith-chunk-cache/README.md)から通常保存へ引渡し済みであることを読み取ります。

候補の再投入は20tickごとに最大8件で、Millénaireの通常キューとSiteHunterの合算上限72件、孤立建築キュー上限64件の空きに従います。村の生成・建築と孤立建築のTTL・再試行はMillénaireが処理します。

ChunksmithがLODを管理し、Chunk Cacheが実チャンクの保存・復元・引渡しを担当します。このMODの処理範囲は、村候補の保持・再投入と建築準備の確認です。

プロジェクト内で`gradle check jar`を実行すると、上流API・Mixinの注入点と候補キューの予算を検証できます。
