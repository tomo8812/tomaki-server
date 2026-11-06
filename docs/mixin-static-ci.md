# Mixin static checks

Every PR, update to `main`, and manual dispatch receives a `Mixin static checks`
workflow. The job summary lists the selected builds and checks. A new commit
cancels older queued or running executions for the same PR or branch.

## Change selection

| Change | Shipped JAR audit | Source builds | CI regression tests |
| --- | --- | --- | --- |
| Root/project documentation only | Skip | Skip | Skip |
| Project source, resources, tests or Gradle settings | All custom JARs | Changed project and consumers of its built JAR | Skip |
| Deployed JAR | All custom JARs | Consumers referencing that JAR | Skip |
| Audit scripts/tests | All custom JARs | Skip | Run |
| Workflow, build selector or shared Gradle settings | All custom JARs | All projects | Run |
| Manual dispatch or unavailable diff | All custom JARs | All projects | Run |

Projects are discovered from `custom-mods/*/build.gradle*`; there is no fixed MOD
count or build list. Local JAR input paths are read from literal paths in each
project's Gradle files, including bootstrap JARs and JARs used by verification
tasks. References to another project's `build/` directory define build ordering
and transitive consumers. New computed paths or external build inputs must be
covered by the selector when introducing them. Deployment JARs without maintained
Gradle projects still receive the full static audit when JARs change.

The selector compares the complete PR merge commit against the event's base SHA,
or the complete push against its before SHA. Added and deleted paths are both
included in renames. An unavailable/invalid base selects full validation. A cyclic
local build dependency fails the check.

Documentation-only changes fetch the workflow and maintained sources with sparse
checkout. Deployed JARs, Python setup, the annotation API download, Java setup,
test execution and the build runner are omitted. Other changes audit all shipped
custom JARs together so duplicate MOD IDs and cross-file deployment checks remain
covered. Check names remain present on PRs even when the heavier work is skipped.

## Builds and caching

`source-build` starts only when selected projects exist and the shipped audit
succeeds. Selected projects run in dependency order on one runner, sharing the
NeoForge artifacts and Gradle dependency cache. Each invokes `check jar` with
`--build-cache`. The Gradle action restores/saves reusable state; trusted branches
and same-repository PRs may write caches, while fork PRs consume them read-only.
Only JARs produced by the selected projects enter the rebuilt-JAR audit.

## Results

The static audit validates annotation retention and element ABI against the pinned
runtime Sponge API, config/TOML registration, registered-class presence, reserved
Mixin packages, and bundled API stubs. Deployment checks also verify unique custom
MOD IDs and maintained embedded Java sources. A failure produces a failed Check,
error annotations, a job summary and a detailed JSON report. Audit reports are
uploaded even after an audit failure and retained for 14 days. Setup/build failures
before an audit appear in the failed step and cannot produce that audit's report.

To enforce a merge gate, require `jar-audit` and `source-build` in the `main`
ruleset. Workflow execution and merge enforcement are separate repository settings.

Static validation does not establish runtime target/injection compatibility,
optional-dependency combinations or client behavior. Update the pinned annotation
API together with runtime changes and supply additional APIs through
`--annotation-api` when introducing their annotations.
