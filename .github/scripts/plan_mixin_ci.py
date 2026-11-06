"""Select builds from the complete PR/push diff and local Gradle JAR inputs."""
import argparse
import json
import os
from pathlib import Path
import posixpath
import re
import subprocess

ROOT = Path(__file__).resolve().parents[2]
WORKFLOW = '.github/workflows/mixin-annotation-audit.yml'
PLANNER = '.github/scripts/plan_mixin_ci.py'


def projects_and_inputs(root):
    projects = {}
    for build in sorted((root / 'custom-mods').glob('*/build.gradle*')):
        project = build.parent.name
        text = '\n'.join(p.read_text(encoding='utf-8') for p in build.parent.glob('*.gradle*') if p.is_file())
        # Includes compile inputs, bootstrap JARs and JARs inspected by check
        # tasks, not just dependencies {}. Resolve literals without running Gradle.
        inputs = set()
        upstream = set()
        for literal in re.findall(r'''["']([^"'\n]+\.jar)["']''', text):
            path = posixpath.normpath(f'custom-mods/{project}/{literal}')
            if path.startswith('mods/'):
                inputs.add(path)
            match = re.match(r'custom-mods/([^/]+)/build/', path)
            if match:
                upstream.add(match[1])
        projects[project] = {'inputs': inputs, 'upstream': upstream}
    return projects


def plan(root, changed, full=False):
    projects = projects_and_inputs(root)
    shared = {WORKFLOW, PLANNER, 'build.gradle', 'build.gradle.kts',
              'settings.gradle', 'settings.gradle.kts', 'gradle.properties'}
    full = full or any(p in shared or p.startswith('gradle/') for p in changed)
    selected = set(projects) if full else set()
    for name, project in projects.items():
        prefix = f'custom-mods/{name}/'
        for path in changed:
            # Documentation does not affect Gradle inputs. All other project
            # paths are conservative inputs, including tests and resources.
            relative = path[len(prefix):] if path.startswith(prefix) else ''
            documentation = relative.startswith('docs/') or ('/' not in relative and relative.lower().endswith('.md'))
            own = bool(relative) and not documentation
            if own or path in project['inputs']:
                selected.add(name)
    # A source-built dependency can replace its deployed fallback JAR. Rebuild
    # downstream consumers against it; execute dependencies first in one runner.
    while True:
        added = {name for name, p in projects.items() if p['upstream'] & selected}
        if added <= selected:
            break
        selected |= added
    ordered, visiting = [], set()
    def visit(name):
        if name in ordered:
            return
        if name in visiting:
            raise ValueError(f'cyclic local build dependency: {name}')
        visiting.add(name)
        for dep in sorted(projects[name]['upstream'] & selected):
            visit(dep)
        visiting.remove(name)
        ordered.append(name)
    for name in sorted(selected):
        visit(name)
    audit = full or bool(selected) or any(
        p.startswith('mods/') and p.endswith('.jar') or p.startswith('.github/scripts/')
        for p in changed)
    tests = full or any(p.startswith('.github/scripts/') for p in changed)
    return {'projects': ordered, 'audit': audit, 'tests': tests, 'full': full}


def changed_paths(root, base):
    if not base or set(base) == {'0'}:
        raise ValueError('no usable base commit')
    # PR checkout is the synthetic merge commit: base..HEAD is the complete
    # merged PR diff, including both sides of renames/deletions. No file API cap.
    subprocess.run(['git', 'fetch', '--no-tags', '--depth=1', 'origin', base], cwd=root, check=True)
    result = subprocess.run(['git', 'diff', '--name-only', '--no-renames', '-z', base, 'HEAD'],
                            cwd=root, check=True, capture_output=True)
    return [p.decode('utf-8') for p in result.stdout.split(b'\0') if p]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--base', default='')
    parser.add_argument('--full', action='store_true')
    args = parser.parse_args()
    reason = 'Changed files'
    try:
        paths = [] if args.full else changed_paths(ROOT, args.base)
        result = plan(ROOT, paths, args.full)
    except (ValueError, OSError, subprocess.CalledProcessError, UnicodeError) as exc:
        print(f'::warning::Cannot establish change scope; running full checks: {exc}')
        result = plan(ROOT, [], True)
        reason = 'Full validation (diff unavailable)'
    print(json.dumps(result))
    output = os.environ.get('GITHUB_OUTPUT')
    if output:
        with open(output, 'a', encoding='utf-8') as stream:
            for key, value in result.items():
                stream.write(f'{key}={json.dumps(value, separators=(",", ":"))}\n')
    summary = os.environ.get('GITHUB_STEP_SUMMARY')
    if summary:
        with open(summary, 'a', encoding='utf-8') as stream:
            stream.write('## Mixin CI scope\n\n' + reason + '\n\n')
            stream.write(f'- Source builds: {len(result["projects"])} / {len(projects_and_inputs(ROOT))}\n')
            stream.write('- Selected: ' + (', '.join(result['projects']) or 'none') + '\n')
            stream.write(f'- Shipped JAR audit: {result["audit"]}\n- Regression tests: {result["tests"]}\n')


if __name__ == '__main__':
    main()
