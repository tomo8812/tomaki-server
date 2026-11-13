# Chunksmith Controller設定移行

既存サーバーの`maki-lod-cpu.properties`にある有効な設定値を、新しく同梱される`maki-chunksmith-controller.properties`へ引き継ぎます。新設定ファイルが既に配置されている場合も、初回起動前に実行してください。サーバーは停止した状態にします。

更新済みサーバーディレクトリで以下を実行します。旧設定と新設定をそれぞれ退避し、新設定の現行キーに対応する旧値だけを移します。廃止したBackfill設定や旧CPU閾値設定は移しません。

```bash
python3 - <<'PY'
from pathlib import Path
from shutil import copy2
legacy = Path('config/maki-lod-cpu.properties')
current = Path('config/maki-chunksmith-controller.properties')
old = legacy.read_text()
new = current.read_text()
for source in (legacy, current):
    backup = source.with_name(source.name + '.before-pr91')
    if backup.exists():
        raise SystemExit(f'バックアップが既にあります: {backup}')
for source in (legacy, current):
    copy2(source, source.with_name(source.name + '.before-pr91'))
values = {}
for line in old.splitlines():
    if line.strip() and not line.lstrip().startswith(('#', '!')) and '=' in line:
        key, value = line.split('=', 1)
        values[key.strip()] = value.strip()
output = []
for line in new.splitlines():
    if line.strip() and not line.lstrip().startswith(('#', '!')) and '=' in line:
        key = line.split('=', 1)[0].strip()
        if key in values:
            line = key + '=' + values[key]
    output.append(line)
current.write_text('\n'.join(output) + '\n')
PY
```

起動前に`follow-enabled`、`follow-player`、`follow-player-uuid`、`min-concurrent`、`max-concurrent`と追従範囲を確認してください。起動後はログの読込設定と追従対象を確認します。旧設定を使用していない新規構築では、この移行手順は不要です。
