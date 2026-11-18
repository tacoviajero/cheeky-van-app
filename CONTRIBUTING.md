# Contributing

Use `main` for reviewed, validated integration. Work in short-lived `feature/*` or
`fix/*` branches and open pull requests into `main`. Use Conventional Commits, such as
`feat: add simulated telemetry` or `fix: preserve unavailable sensor state`. Tagged
releases use Semantic Versioning (`v0.1.0` after validation).

Run `spotlessCheck`, `lintDebug`, `testDebugUnitTest`, `assembleDebug`, and
`assembleDebugAndroidTest` before requesting review. Execute applicable connected-device
tests separately and record device/API and results. Add tests for behavior and failure
paths, update docs, and record architectural decisions in `docs/decisions/`.

Before adding any transport operation, review its safety against the explicit read-only
allowlist. Verify cancellation, connection cleanup, bounded input handling, and missing
data semantics. No arbitrary command entry, DTC clearing, or ECU writes are permitted.

Exclude credentials, signing material, local configs, generated output, diagnostic
identifiers, and private user data. Review `git diff --cached` before each commit.

After the remote is connected, configure branch protection on `main`: require a pull
request, reviewer approval, and the `Validate Android` check; require dependency review
where supported. Restrict bypasses and disallow force pushes. This setup is an owner
action and must be verified in repository settings.

Actions are pinned to commits, annotated with the release tag. Dependabot opens updates
for Gradle and Actions. Verify a proposed SHA in its upstream repository, review release
notes and permissions, then run the entire validation pipeline before merging. For a
private repository, enable dependency graph and the required security product before
setting repository variable `ENABLE_DEPENDENCY_REVIEW=true`. Public PRs run dependency
review automatically; high/critical newly introduced vulnerabilities fail it. The check
is incremental, so also review existing dependency security alerts. No secrets are
required by CI, and pull requests use `pull_request`, never `pull_request_target`.

Merge, release, branch deletion, force push, and published-history changes require the
project owner's explicit approval.
