# Cleanup & Security Review Ruleset

## Rules
- **Do NOT change app logic or UI.**
- Fix only: security issues, dead code, dangerous configs, release blockers.
- Keep changes minimal and reversible.
- Commit each group of changes separately.

## 1. Remove Unused Code & Resources
- Remove only:
    - Unused classes / functions
    - Unused layouts, drawables, strings, values
    - Unreachable code blocks
    - Duplicated resources
- If unsure → comment out and review later.

## 2. Dependency & Vulnerability Scan
- Update only patch/minor dependency versions.
- Avoid major version changes unless fixing a known security problem.

## 3. SMB Protocol Security Review
- Remove any unused or dangerous SMB permissions or flags.
- Ensure SMB client uses the latest supported SMB version (prefer SMBv3).
- Disable plaintext SMBv1 if possible (deprecated + insecure).
- Avoid hardcoded SMB usernames/passwords directly in code.
- Confirm credentials are not logged anywhere.
- Validate file paths to avoid path traversal vulnerabilities.
- Ensure app does not expose open SMB connections externally.

## 4. Hardcoded Secrets Check
- Search for: API keys, Tokens, Passwords
- If found: Remove from code, Store in local.properties or CI secrets, Add only placeholders in public repo

## 5. AndroidManifest Security Audit
- Remove unused permissions.
- android:exported is explicitly set for all components.
- Only required components exported.
- No high-risk permissions allowed unnecessarily.

## 6. Logging Cleanup
- No sensitive information logged.
- Disable logs for release builds.
- Replace debug logs with safe logging framework if required.

## 7. WebView Security Review
- JavaScript enabled only if needed.
- File access disabled unless required.
- No addJavascriptInterface with untrusted content.

## 8. Code Review for Stability
- Add missing null checks to prevent crashes.
- Mark internal-only functions as private.
- Remove commented-out code.
- Improve readability but avoid functional changes.

## 9. Release Build Verification
- No lint errors.
- All tests pass.
- Release build compiles cleanly.
