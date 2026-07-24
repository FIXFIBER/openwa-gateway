#!/usr/bin/env bash
# ============================================================================
# keepalive.cron.sh — serv00 keep-alive + self-heal for IdaWhats
# ----------------------------------------------------------------------------
# serv00 kills idle apps after 24h with no requests. A WhatsApp gateway must
# stay warm 24/7, so we:
#   1. curl the health endpoint every 20 min (prevents idle kill)
#   2. if the process is dead, re-run deploy.sh to restart it
#
# Install on serv00:
#   crontab -e
#   */20 * * * * $HOME/domains/YOURDOMAIN/public_nodejs/idawhats/serv00-deploy/keepalive.cron.sh
# ============================================================================
set -e
LOGIN="${SERV00_LOGIN:-yourlogin}"
DOMAIN="${SERV00_DOMAIN:-yourdomain.com}"
REPO_DIR="$HOME/domains/$DOMAIN/public_nodejs/idawhats"
HEALTH="http://localhost:2785/api/sessions"

export PATH="$HOME/bin:$PATH"

if curl -fsS --max-time 10 "$HEALTH" -o /dev/null; then
  # alive and warm — nothing to do
  exit 0
fi

echo "[keepalive] IdaWhats not responding at $(date) — restarting"
"$REPO_DIR/serv00-deploy/deploy.sh" >> "$REPO_DIR/data/keepalive.log" 2>&1
