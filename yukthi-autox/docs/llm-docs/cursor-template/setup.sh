#!/usr/bin/env bash
# AutoX LLM Docs Setup Script
# Downloads llm-docs from the autox GitHub repo into a target project.
#
# Usage:
#   ./setup.sh /path/to/my-project
#   ./setup.sh /path/to/my-project --branch main --repo yukthitech/autox

set -euo pipefail

REPO="yukthitech/autox"
BRANCH="main"
DOCS_SUB_PATH="yukthi-autox/docs/llm-docs"
TARGET_DIR=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        --repo)
            REPO="$2"
            shift 2
            ;;
        --branch)
            BRANCH="$2"
            shift 2
            ;;
        --docs-path)
            DOCS_SUB_PATH="$2"
            shift 2
            ;;
        -h|--help)
            echo "Usage: $0 <target-project-dir> [--repo yukthitech/autox] [--branch main]"
            exit 0
            ;;
        *)
            if [[ -z "$TARGET_DIR" ]]; then
                TARGET_DIR="$1"
            else
                echo "Unknown argument: $1"
                exit 1
            fi
            shift
            ;;
    esac
done

if [[ -z "$TARGET_DIR" ]]; then
    echo "Error: target project directory is required."
    echo "Usage: $0 <target-project-dir> [--repo yukthitech/autox] [--branch main]"
    exit 1
fi

mkdir -p "$TARGET_DIR"
TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"

DOCS_DEST="$TARGET_DIR/docs/autox-llm"
CURSOR_RULES_DEST="$TARGET_DIR/.cursor/rules"
TEMP_DIR="$(mktemp -d /tmp/autox-llm-docs.XXXXXX)"

cleanup() {
    rm -rf "$TEMP_DIR"
}
trap cleanup EXIT

echo "Downloading AutoX LLM docs from $REPO ($BRANCH)..."

git clone --depth 1 --filter=blob:none --sparse \
    -b "$BRANCH" "https://github.com/$REPO.git" "$TEMP_DIR"

(
    cd "$TEMP_DIR"
    git sparse-checkout set "$DOCS_SUB_PATH"
)

SOURCE_DOCS="$TEMP_DIR/$DOCS_SUB_PATH"
if [[ ! -d "$SOURCE_DOCS" ]]; then
    echo "Error: docs path not found in repo: $DOCS_SUB_PATH"
    exit 1
fi

rm -rf "$DOCS_DEST"
mkdir -p "$DOCS_DEST"

for item in "$SOURCE_DOCS"/*; do
    name="$(basename "$item")"
    if [[ "$name" == "cursor-template" ]]; then
        continue
    fi
    cp -r "$item" "$DOCS_DEST/"
done

RULE_SOURCE="$SOURCE_DOCS/cursor-template/.cursor/rules/autox-automation.mdc"
if [[ -f "$RULE_SOURCE" ]]; then
    mkdir -p "$CURSOR_RULES_DEST"
    cp "$RULE_SOURCE" "$CURSOR_RULES_DEST/autox-automation.mdc"
    echo "Installed Cursor rule: .cursor/rules/autox-automation.mdc"
fi

echo "Installed LLM docs to: $DOCS_DEST"

if [[ -f "$DOCS_DEST/autox-version.txt" ]]; then
    echo "AutoX docs version: $(cat "$DOCS_DEST/autox-version.txt")"
fi

echo "Done. Open the project in Cursor and refer to docs/autox-llm/README.md"
