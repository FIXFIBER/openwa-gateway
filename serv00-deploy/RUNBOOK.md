# OpenWA on serv00 — Free 24/7 Deploy Runbook

Goal: run your OpenWA WhatsApp gateway on **serv00.com** (free, no card) so it
keeps working when your laptop is off. You only `git push` when you change code.

## 0. One-time account setup (do in serv00 panel)
1. Sign up at serv00.com (free).
2. Add a **domain** as WWW type `nodejs`.
3. Enable **Binexec**: DevilWEB → Additional services → Run your own applications → ON.
   Then **log out and SSH back in** (required for binexec to activate).
4. In SSH, confirm: `node22 -v` prints v22.x.

## 1. Push your code to GitHub (so the host pulls it)
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
