# OpenWA — Free Hosting Deploy Runbook

Two free, no-card paths. **serv00 is currently full** (server user cap), so the
working choice right now is **Render**.

============================================================================
## PATH A — Render (FREE, no card, CURRENT recommendation)
============================================================================
Catch: Render free web services **sleep after 15 min idle**. A pinger must hit
the health URL every <15 min or it sleeps (WhatsApp session drops).

### A0. Deploy
1. Push code to GitHub (done: FIXFIBER/openwa-gateway).
2. Render dashboard → New → **Blueprint** → connect the repo → it reads `render.yaml`.
   Or CLI: `render blueprint launch` (render CLI v2.20.0 is installed locally).
3. In Render dashboard, set **API_MASTER_KEY** as a secret (or accept the random
   seeded key shown in the first deploy log).
4. Deploy. Render builds (skips Chromium, compiles TS + dashboard) and starts
   `node dist/main` on the injected PORT. Health check = `/api/sessions`.

### A1. Keep it awake (defeat 15-min sleep)
- **True 24/7 (laptop off):** add a free **UptimeRobot** HTTP monitor (5-min
  interval) pointing at `https://<your-app>.onrender.com/api/sessions`. No card.
- **Laptop-on only:** the Hermes cron `openwa-render-keepalive` (every 10 min)
  runs `serv00-deploy/ping-render.sh`. Write the URL to
  `/home/darkaxis/whatsappopenwa/.render-url` so the cron picks it up.

### A2. Link a number
Dashboard at `https://<your-app>.onrender.com/` (log in with API key) → Sessions
→ scan QR. `AUTO_START_SESSIONS=true` re-links after restarts.

============================================================================
## PATH B — serv00 (FREE, no card, but currently at user cap)
============================================================================
Use only when serv00 reopens a server (they rotate; retry later or watch
@serv00com on X). Steps preserved below.

### B0. One-time account setup
1. Sign up at serv00.com (free).
2. Add a **domain** as WWW type `nodejs`.
3. Enable **Binexec**: DevilWEB → Additional services → Run your own applications → ON.
   Then **log out and SSH back in**.
4. Confirm: `node22 -v` prints v22.x.

### B1. Push your code to GitHub (so the host pulls it)
On your laptop:
```
cd ~/whatsappopenwa
git init -q 2>/dev/null || true
git remote add origin git@github.com:YOURUSER/openwa.git   # or HTTPS
git add -A
git commit -m "openwa serv00 deploy kit"
git push -u origin main
```
> The `serv00-deploy/` folder is already in the repo and is git-ignored-safe.

## 2. First deploy on serv00 (SSH)
```
export SERV00_LOGIN=yourlogin SERV00_DOMAIN=yourdomain.com
export API_MASTER_KEY=$(openssl rand -hex 32)   # pick a strong key, SAVE IT
bash ~/domains/yourdomain.com/public_nodejs/openwa/serv00-deploy/deploy.sh
```
The script clones, installs, builds, writes `.env`, and starts the server.

## 3. Install the keep-alive cron (24/7 survival)
```
crontab -e
*/20 * * * * $HOME/domains/yourdomain.com/public_nodejs/openwa/serv00-deploy/keepalive.cron.sh
```
This curls the health endpoint every 20 min (stops serv00's 24h idle kill) and
auto-restarts if the process dies.

## 4. Link a WhatsApp number
- Dashboard: `https://yourdomain.com/` (serv00 serves it) — log in with your API key.
- Or local: `http://localhost:2785/` after `git pull` + `deploy.sh` on your laptop.
- Scan the QR (it refreshes every ~20s). `AUTO_START_SESSIONS=true` re-links the
  number automatically after any restart — no re-scan needed.

## 5. Update workflow (laptop off = fine)
Change code locally → `git push`. On serv00, either:
- run `deploy.sh` manually, or
- add a second cron that does `cd repo && git pull && bash serv00-deploy/deploy.sh`
  (then you literally never SSH again except for keys).

## Caveats (honest)
- Free shared host = less stable than a paid VPS. Baileys auth is on disk so restarts
  usually don't require re-QR, but a hard recycle can drop the session.
- For a real money bot, Oracle Always-Free (card hold) or a $5 VPS is safer.
- baileys engine = higher ban-risk than whatsapp-web.js. Use a dedicated number.
