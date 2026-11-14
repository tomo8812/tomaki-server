# Chunksmith Controller設定移行

旧設定の退避は、サーバーを停止し、リポジトリや配布ファイルを更新する**前**に行います。更新では旧ファイルが削除されるためです。

```bash
cp -n config/maki-lod-cpu.properties config/maki-lod-cpu.properties.before-controller-upgrade
```

退避ファイルが作成され、引き継ぐ設定値が入っていることを確認してから更新します。更新後、次の処理で退避済み旧設定の有効な値を新設定へ移します。廃止したBackfill設定や旧CPU閾値設定は移しません。

```bash
python3 - <<'PY'
from pathlib import Path
from shutil import copy2
legacy = Path('config/maki-lod-cpu.properties.before-controller-upgrade')
current = Path('config/maki-chunksmith-controller.properties')
old = legacy.read_text()
new = current.read_text()
backup = current.with_name(current.name + '.before-controller-upgrade')
if backup.exists():
    raise SystemExit(f'バックアップが既にあります: {backup}')
copy2(current, backup)
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
