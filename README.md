# EffectTimers

Stop guessing how long your potion effects last. EffectTimers draws a
tidy list of countdown timers for every active status effect in the
top-left corner of the HUD — like "Speed 1:04" or "Strength 2:30" —
updated twice per second, so you always know exactly when to drink the
next potion.

## Features

- **Live effect timers** — one line per active effect, e.g.
  "Night Vision 7:42", "Poison 0:18"
- **Low-time amber warning** — timers under 10 seconds turn amber, so
  expiring effects catch your eye before they run out
- **Long-duration friendly** — effects over an hour render as h:mm:ss
- **Quiet when idle** — nothing renders when you have no active effects
- **Zero config** — drop it in and play

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/) 0.19.2+ for
   Minecraft 26.2
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) for
   Minecraft 26.2
3. Drop `effecttimers-1.0.0.jar` into your `mods` folder
4. Launch and play — no config needed

## Notes

- Client-side only. Works on any server (including vanilla).
- No mixins, no networking, nothing saved to disk.

## License

MIT — see [LICENSE](LICENSE).
