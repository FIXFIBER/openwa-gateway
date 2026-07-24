#!/usr/bin/env bash
# ============================================================================
# OpenWA deploy script for serv00.com (free, no-card, 24/7)
# ----------------------------------------------------------------------------
# Model (research-backed, see serv00 docs):
#   - serv00 Node runs under Phusion Passenger by default, BUT a 24/7 WhatsApp
#     gateway needs a raw long-lived process (baileys holds a live WS socket).
#     So we enable Binexec and run `node dist/main` ourselves, then keep it
#     alive with a cron (serv00 kills idle apps after 24h).
#   - Node 22 is the serv00 default (`node22`) = satisfies OpenWA >=22.13.
#   - Native module better-sqlite3 needs Binexec + clang to compile.
#
# USAGE on serv00 (after `devil binexec on` + SSH re-login):
#   ./serv00-deploy/deploy.sh
#
# This script is idempotent: safe to re-run on every `git pull`.
# ============================================================================
set -e

# ---- serv00 paths (edit LOGIN / DOMAIN to yours) ----
LOGIN="${SERV00_LOGIN:-yourlogin}"          # serv00 account login
DOMAIN="${SERV00_DOMAIN:-yourdomain.com}"   # the WWW domain you added as 'nodejs' type
APP_DIR="$HOME/domains/$DOMAIN/public_nodejs"
REPO_DIR="$APP_DIR/openwa"                  # where the code lives
DATA_DIR="$REPO_DIR/data"

echo "==> [1/6] ensure node22 + npm22 on PATH"
mkdir -p ~/bin
ln -fs /usr/local/bin/node22 ~/bin/node
ln -fs /usr/local/bin/npm22  ~/bin/npm
export PATH="$HOME/bin:$PATH"
node -v

echo "==> [2/6] clone or pull repo"
mkdir -p "$APP_DIR"
if [ -d "$REPO_DIR/.git" ]; then
  cd "$REPO_DIR" && git pull --ff-only
else
  git clone https://github.com/rmyndharis/OpenWA.git "$REPO_DIR"
  cd "$REPO_DIR"
fi

echo "==> [3/6] install deps (skip Chromium — we use baileys engine)"
export PUPPETEER_SKIP_DOWNLOAD=true
export PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true
export CC=clang CXX=clang++            # serv00: compile native modules with clang
npm install

echo "==> [4/6] build (TS -> dist) + dashboard"
npm run build
npm run dashboard:build

echo "==> [5/6] write runtime .env (baileys + sqlite, auto-start sessions)"
mkdir -p "$DATA_DIR"
cat > "$REPO_DIR/.env" <<EOF
NODE_ENV=production
PORT=2785
AUTO_START_SESSIONS=true
ENGINE_TYPE=baileys
SESSION_DATA_PATH=./data/sessions
DATABASE_TYPE=sqlite
DATABASE_NAME=./data/openwa.sqlite
DATABASE_SYNCHRONIZE=true
STORAGE_TYPE=local
STORAGE_LOCAL_PATH=./data/media
REDIS_ENABLED=false
QUEUE_ENABLED=false
CACHE_ENABLED=false
POSTGRES_BUILTIN=false
MINIO_BUILTIN=false
MCP_ENABLED=true
MCP_READONLY=false
EOF
# NOTE: set your own API_MASTER_KEY here or via serv00 ~/.bash_profile

echo "==> [6/6] (re)start the server under nohup"
pkill -f "node dist/main" 2>/dev/null || true
cd "$REPO_DIR"
nohup node dist/main > "$DATA_DIR/openwa.log" 2>&1 &
echo "started pid $!"
sleep 4
curl -fsS http://localhost:2785/api/sessions -H "X-API-Key: ${API_MASTER_KEY:-none}" -o /dev/null \
  && echo "HEALTH OK" || echo "WARN: health check failed (see $DATA_DIR/openwa.log)"
