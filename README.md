# Android版 mediapipe holistic landmarker

- Author: Hiroaki Yaguchi, 947D Tech.
- License: Apache 2.0

## 概要

androidプラットフォームでmediapipe最新版のholisticを実行し、
その内容をUDPで送信するアプリケーションです。

claudeとのペアプログラミングによって作成しています。
また、公式のサンプルプログラムを参考にしています。

https://github.com/google-ai-edge/mediapipe-samples/tree/main/examples/holistic_landmarker/android

動作環境はsnapdragon 8以上を推奨します。
8であれば少し古めの端末でも快適に動作することを確認しています。
一方7では新しくても動作が重すぎる端末も確認しています。

### 動作確認済み

- Xperia 1 IV
- Galaxy S26

### 動くが重い、など推奨できない端末

- Torque G07 (重い)


## ビルド方法

Android studioで本プロジェクトを開いてビルドしてください。
特に他のライブラリなどは必要ありません。

## 操作方法

初回起動時にカメラの使用を許可してください。
右上のボタンから使用するカメラを選択してください。
左上のボタンからUDPの送信先を設定してください。

アプリケーションが前面で動作している間はスリープしないように設定されています。
その代わりにバックグラウンドでは処理が行われません。
使用する場合は必ず前面で動作させてください。
使用しない場合はホームボタンなどを使ってください。
