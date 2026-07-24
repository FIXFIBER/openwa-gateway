#!/usr/bin/env bash
# ============================================================================
# ping-render.sh — keep a Render free-tier IdaWhats awake
# ----------------------------------------------------------------------------
# Render free web services sleep after 15 min idle. Hit the health endpoint
# every ~10 min so it never sleeps. Safe to run from any cron (Hermes, system,
# or a free uptime service like UptimeRobot pointing at your /api/sessions URL).
#
# Usage: RENDER_URL=https://idawhats-gateway.onrender.com ./ping-render.sh
# ============================================================================
set -e
URL="${RENDER_URL:-https://idawhats-gateway.onrender.com}/api/sessions"
# A key isn't required for a 401 response to count as "alive", but send one if set.
if [ -n "$IDAWHATS_KEY" ]; then
  curl -fsS --max-time 15 "$URL" -H "X-API-Key: $IDAWHATS_KEY" -o /dev/null \
    && echo "$(date -u) ping OK" || echo "$(date -u) ping FAIL"
else
  curl -fsS --max-time 15 "$URL" -o /dev/null \
    && echo "$(date -u) ping OK (401=alive)" || echo "$(date -u) ping FAIL"
fi
