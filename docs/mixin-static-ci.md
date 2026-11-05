# Mixin static checks for future PRs

The `Mixin static checks` workflow runs on every pull request (including later
commits), pushes to `main`, and manual dispatch. There is no changed-path filter,
so the check also runs on dependency updates and documentation-only PRs. It does
not start Minecraft or require a world, player account, or server credentials.

## Checks shown on a PR

- `jar-audit` inspects every `mods/maki-*.jar`, including newly added custom mods.
  It validates registered classes, TOML/JSON registration, reserved Mixin package
  boundaries, annotation retention, and annotation element ABI against the pinned
  runtime Sponge API. It rejects bundled Mixin API/stub classes.
- `source-build` builds the three currently configured projects (chunk-cache,
  ship-anchor, online-fix), then performs the same API-backed static audit on their
  output JARs. Adding a new buildable source project requires a build step; its
  shipped `maki-*` JAR is already covered automatically by `jar-audit`.
- Errors return a nonzero exit status and appear as GitHub error annotations.
  Each audit writes a job summary with JAR counts and failures, and a JSON artifact
  containing all registered-class results. Reports are uploaded even when the
  audit fails and are retained for 14 days. Setup/build failures before an audit
  still fail the check but cannot produce an audit report.
- Regression tests run even when the shipped-JAR audit fails. They include a
  missing registered class, empty inventory, corrupt JAR, reserved package collision,
  nested/Unicode annotation parsing and incorrect annotation element encoding.

## Required checks

After this workflow is merged, future PRs receive these Checks automatically.
To also prevent merging a failing PR, make `jar-audit` and `source-build` required
status checks in the repository's ruleset/branch protection for `main`. Workflow
execution and merge enforcement are separate settings; this change does not alter
the repository's rulesets.

## What static validation cannot establish

Passing these checks does not prove every runtime target, injection point, optional
dependency combination, or Mixin interaction works. In particular, it does not
load client-only targets such as Diorama. The checks catch the package and annotation
errors from Issue #32 without launching a server. The pinned Sponge artifact must
be updated with the runtime when changing NeoForge/Mixin versions; additional
annotation APIs such as MixinExtras must be supplied with `--annotation-api` if
custom mods begin using them.
