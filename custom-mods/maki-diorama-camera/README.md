# Maki Diorama Camera Alignment 1.0.1

Minecraft 1.21.1 / NeoForge 21.1.262 / Diorama 0.10.1向けのクライアント用MODです。船などで最終描画カメラが補正された後、実際のワールド描画へ渡るカメラ位置・ビュー行列・投影行列をDioramaへ渡し、ウェイポイント・死亡地点の投影を合わせます。

添付版1.0.0のソースを管理対象にし、対応Dioramaの依存範囲を更新して再ビルドしました。新版の `WorldProjection.capture` は `Matrix4fc` を受け取るため、古いJARの依存範囲を書き換えるだけではなく再コンパイルが必要です。

クライアントを停止し、旧 `maki-diorama-camera-1.0.0.jar` を削除して `maki-diorama-camera-1.0.1.jar` へ交換します。サーバーへの導入は不要です。Diorama 0.5.0には対応しません。地形共有やWeb地図の処理は行いません。

`gradle check jar` で、実際のMinecraftの描画メソッド・呼出先・ラッパー引数と、Dioramaの投影APIへの呼出をバイトコードで照合します。船に乗った際の見た目は実クライアントでの確認が別途必要です。
