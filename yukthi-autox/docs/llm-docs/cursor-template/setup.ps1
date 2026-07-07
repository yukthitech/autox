# AutoX LLM Docs Setup Script (PowerShell)
# Downloads llm-docs from the autox GitHub repo into a target project.
#
# Usage:
#   .\setup.ps1 -TargetDir "C:\path\to\my-project"
#   .\setup.ps1 -TargetDir "C:\path\to\my-project" -Branch "main" -Repo "yukthitech/autox"

param(
    [Parameter(Mandatory = $true)]
    [string]$TargetDir,

    [string]$Repo = "yukthitech/autox",
    [string]$Branch = "main",
    [string]$DocsSubPath = "yukthi-autox/docs/llm-docs"
)

$ErrorActionPreference = "Stop"

$TargetDir = Resolve-Path $TargetDir -ErrorAction SilentlyContinue
if (-not $TargetDir) {
    $TargetDir = (New-Item -ItemType Directory -Force -Path $TargetDir).FullName
} else {
    $TargetDir = $TargetDir.Path
}

$DocsDest = Join-Path $TargetDir "docs\autox-llm"
$CursorRulesDest = Join-Path $TargetDir ".cursor\rules"
$TempDir = Join-Path $env:TEMP ("autox-llm-docs-" + [guid]::NewGuid().ToString())

Write-Host "Downloading AutoX LLM docs from $Repo ($Branch)..."

try {
    # Sparse clone for minimal download
    git clone --depth 1 --filter=blob:none --sparse `
        -b $Branch "https://github.com/$Repo.git" $TempDir 2>&1 | Out-Null

    Push-Location $TempDir
    git sparse-checkout set $DocsSubPath 2>&1 | Out-Null
    Pop-Location

    $SourceDocs = Join-Path $TempDir $DocsSubPath
    if (-not (Test-Path $SourceDocs)) {
        throw "Docs path not found in repo: $DocsSubPath"
    }

    # Copy docs (exclude cursor-template folder from docs dest)
    if (Test-Path $DocsDest) {
        Remove-Item -Recurse -Force $DocsDest
    }
    New-Item -ItemType Directory -Force -Path $DocsDest | Out-Null

    Get-ChildItem $SourceDocs -Exclude "cursor-template" | ForEach-Object {
        Copy-Item -Recurse -Force $_.FullName (Join-Path $DocsDest $_.Name)
    }

    # Copy Cursor rule
    $RuleSource = Join-Path $SourceDocs "cursor-template\.cursor\rules\autox-automation.mdc"
    if (Test-Path $RuleSource) {
        New-Item -ItemType Directory -Force -Path $CursorRulesDest | Out-Null
        Copy-Item -Force $RuleSource (Join-Path $CursorRulesDest "autox-automation.mdc")
        Write-Host "Installed Cursor rule: .cursor/rules/autox-automation.mdc"
    }

    Write-Host "Installed LLM docs to: $DocsDest"

    $VersionFile = Join-Path $DocsDest "autox-version.txt"
    if (Test-Path $VersionFile) {
        $version = Get-Content $VersionFile -Raw
        Write-Host "AutoX docs version: $version"
    }
}
finally {
    if (Test-Path $TempDir) {
        Remove-Item -Recurse -Force $TempDir
    }
}

Write-Host "Done. Open the project in Cursor and refer to docs/autox-llm/README.md"
