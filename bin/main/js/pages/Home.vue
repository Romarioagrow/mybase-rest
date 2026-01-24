<template>
  <v-content class="magic-stage">
    <div class="magic-sky"></div>
    <div class="magic-orbit orbit-a"></div>
    <div class="magic-orbit orbit-b"></div>
    <div class="magic-orbit orbit-c"></div>

    <v-container fluid class="magic-wrap">
      <v-row class="magic-hero" align="center" justify="center">
        <v-col cols="12" md="7" class="magic-copy">
          <div class="magic-kicker">MyBase / Astral Console</div>
          <h1 class="magic-title">
            Where goals become constellations
            <span class="magic-glow">and habits learn to glow.</span>
          </h1>
          <p class="magic-subtitle">
            A living dashboard for focus, rituals, and quiet wins. Open a page,
            and the room turns into a small universe.
          </p>
          <div class="magic-actions">
            <v-btn color="#F6D365" class="magic-btn" large @click="goTo('/auth')">
              Enter the Studio
            </v-btn>
            <v-btn outlined color="#E6E1FF" class="magic-btn" large @click="goTo('/instagram')">
              Summon Insights
            </v-btn>
          </div>
          <div class="magic-chips">
            <v-chip small class="magic-chip">mood-driven planning</v-chip>
            <v-chip small class="magic-chip">timelines that breathe</v-chip>
            <v-chip small class="magic-chip">social echoes</v-chip>
          </div>
        </v-col>

        <v-col cols="12" md="5">
          <div class="magic-card stack">
            <div class="magic-card-inner">
              <div class="magic-card-title">The Spark Index</div>
              <div class="magic-card-value">87%</div>
              <div class="magic-card-label">momentum unlocked</div>
              <div class="magic-meter">
                <span></span><span></span><span></span><span></span><span></span>
              </div>
            </div>
          </div>
          <div class="magic-card float">
            <div class="magic-card-inner">
              <div class="magic-card-title">Rituals Today</div>
              <div class="magic-card-row">
                <v-icon color="#F6D365">mdi-star-four-points</v-icon>
                <span>Morning orbit</span>
              </div>
              <div class="magic-card-row">
                <v-icon color="#B9FBC0">mdi-orbit</v-icon>
                <span>Focus loop</span>
              </div>
              <div class="magic-card-row">
                <v-icon color="#E6E1FF">mdi-moon-waning-crescent</v-icon>
                <span>Reflection</span>
              </div>
            </div>
          </div>
        </v-col>
      </v-row>

      <v-row class="magic-grid" align="stretch">
        <v-col cols="12" md="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Goals as Galaxies</div>
            <div class="magic-panel-text">
              Organize by seasons, not deadlines. Each task spins until it
              finds gravity.
            </div>
            <v-btn text color="#F6D365" @click="goTo('/goals')">Open Goals</v-btn>
          </v-card>
        </v-col>
        <v-col cols="12" md="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Spending as Weather</div>
            <div class="magic-panel-text">
              See flows, storms, and calm weeks. The map reveals what matters.
            </div>
            <v-btn text color="#B9FBC0" @click="goTo('/spending')">Open Spending</v-btn>
          </v-card>
        </v-col>
        <v-col cols="12" md="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Logs as Lore</div>
            <div class="magic-panel-text">
              Your days become stories. Trace the path, reshape the arc.
            </div>
            <v-btn text color="#E6E1FF" @click="goTo('/thelogs')">Open Logs</v-btn>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="magic-grid magic-social" align="stretch">
        <v-col cols="12">
          <div class="magic-section-title">Social signals, distilled</div>
          <div class="magic-section-subtitle">
            The core of MyBase analytics: behavior, rhythm, presence — not follower graphs.
          </div>
        </v-col>
        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Posting cadence</div>
            <div class="magic-panel-text">
              Day / week / month rhythm, bursts, and quiet phases.
            </div>
            <div class="magic-micro">
              <span>cadence map</span><span>tempo shifts</span><span>gaps</span>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Content types</div>
            <div class="magic-panel-text">
              Posts, video, stories, and visible formats — aggregated as a profile signature.
            </div>
            <div class="magic-micro">
              <span>format mix</span><span>public signals</span><span>style bias</span>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Best posts</div>
            <div class="magic-panel-text">
              Ranked by likes, comments, views — or visible engagement density.
            </div>
            <div class="magic-micro">
              <span>top spikes</span><span>repeat wins</span><span>signal quality</span>
            </div>
          </v-card>
        </v-col>

        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Time dynamics</div>
            <div class="magic-panel-text">
              Time series of counters when available, or surrogate metrics when not.
            </div>
            <div class="magic-micro">
              <span>series</span><span>surrogates</span><span>momentum</span>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Topics & hashtags</div>
            <div class="magic-panel-text">
              Top themes, persistent patterns, and topic drift across time.
            </div>
            <div class="magic-micro">
              <span>topic map</span><span>shifts</span><span>recurrence</span>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Content style</div>
            <div class="magic-panel-text">
              Text / visual / mixed; tone, motifs, and pattern signatures.
            </div>
            <div class="magic-micro">
              <span>tone</span><span>motifs</span><span>blend</span>
            </div>
          </v-card>
        </v-col>

        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Active hours</div>
            <div class="magic-panel-text">
              When posting usually happens — the creator's natural clock.
            </div>
            <div class="magic-micro">
              <span>hour bands</span><span>time windows</span><span>regularity</span>
            </div>
          </v-card>
        </v-col>
        <v-col cols="12" md="6" lg="4">
          <v-card class="magic-panel" elevation="8">
            <div class="magic-panel-title">Cross-links & mentions</div>
            <div class="magic-panel-text">
              Bio links, post mentions, and identity resolution across platforms.
            </div>
            <div class="magic-micro">
              <span>bio links</span><span>mentions</span><span>profile graph</span>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-content>
</template>

<script>
export default {
  computed: {
    user() {
      return this.$store.state.currentUser
    }
  },
  methods: {
    goTo(link) {
      this.$router.push(link)
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Space+Grotesk:wght@300;400;600&display=swap');

.magic-stage {
  min-height: calc(100vh - 64px);
  position: relative;
  overflow: hidden;
  background: radial-gradient(1200px 800px at 10% 20%, #2a1a47 0%, #0f1027 45%, #090b15 100%);
}

.magic-sky {
  position: absolute;
  inset: -20% -10%;
  background:
    radial-gradient(2px 2px at 10% 20%, rgba(255,255,255,0.5) 40%, transparent 60%),
    radial-gradient(2px 2px at 25% 80%, rgba(255,255,255,0.4) 40%, transparent 60%),
    radial-gradient(1px 1px at 70% 30%, rgba(255,255,255,0.5) 40%, transparent 60%),
    radial-gradient(1px 1px at 85% 60%, rgba(255,255,255,0.4) 40%, transparent 60%),
    radial-gradient(2px 2px at 55% 75%, rgba(255,255,255,0.35) 40%, transparent 60%);
  opacity: 0.6;
  animation: drift 40s linear infinite;
}

.magic-orbit {
  position: absolute;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 50%;
  animation: spin 30s linear infinite;
}

.orbit-a { width: 520px; height: 520px; right: -120px; top: -160px; }
.orbit-b { width: 760px; height: 760px; left: -240px; top: 120px; animation-duration: 46s; }
.orbit-c { width: 420px; height: 420px; right: 15%; bottom: -120px; animation-duration: 36s; }

.magic-wrap {
  position: relative;
  z-index: 2;
  padding-top: 48px;
  padding-bottom: 64px;
}

.magic-hero {
  margin-bottom: 24px;
}

.magic-kicker {
  font-family: "Space Grotesk", sans-serif;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  font-size: 0.75rem;
  color: #b9b5ff;
  margin-bottom: 12px;
}

.magic-title {
  font-family: "Cinzel", serif;
  font-size: clamp(2.2rem, 4vw, 3.6rem);
  line-height: 1.1;
  color: #f7f4ff;
  margin: 0 0 16px 0;
}

.magic-glow {
  display: block;
  color: #f6d365;
  text-shadow: 0 0 18px rgba(246, 211, 101, 0.45);
}

.magic-subtitle {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.85);
  font-size: 1.05rem;
  max-width: 520px;
}

.magic-actions {
  margin-top: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.magic-btn {
  border-radius: 28px;
  font-weight: 600;
  text-transform: none;
  box-shadow: 0 10px 26px rgba(15, 16, 39, 0.55);
}

.magic-chips {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.magic-chip {
  background: rgba(255,255,255,0.1) !important;
  color: #e6e1ff !important;
  border: 1px solid rgba(255,255,255,0.12);
}

.magic-card {
  position: relative;
  margin: 16px 0;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(255,255,255,0.08), rgba(255,255,255,0.02));
  border: 1px solid rgba(255,255,255,0.12);
  box-shadow: 0 25px 45px rgba(8, 9, 20, 0.6);
}

.magic-card-inner {
  padding: 22px 24px;
  color: #f7f4ff;
  font-family: "Space Grotesk", sans-serif;
}

.magic-card-title {
  letter-spacing: 0.12em;
  text-transform: uppercase;
  font-size: 0.75rem;
  color: #b9b5ff;
  margin-bottom: 10px;
}

.magic-card-value {
  font-size: 2.6rem;
  font-weight: 600;
}

.magic-card-label {
  color: rgba(230,225,255,0.7);
  margin-bottom: 14px;
}

.magic-card-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 8px 0;
}

.magic-meter {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 6px;
}

.magic-meter span {
  height: 10px;
  border-radius: 6px;
  background: linear-gradient(90deg, #f6d365, #fda085);
  opacity: 0.85;
}

.stack {
  transform: rotate(-2deg);
}

.float {
  transform: translateY(-12px);
  animation: floaty 6s ease-in-out infinite;
}

.magic-grid {
  margin-top: 24px;
}

.magic-panel {
  background: rgba(12, 14, 30, 0.7) !important;
  border: 1px solid rgba(255,255,255,0.08);
  padding: 18px;
  color: #f7f4ff;
  min-height: 210px;
}

.magic-panel-title {
  font-family: "Cinzel", serif;
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: #f7f4ff;
}

.magic-panel-text {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230,225,255,0.75);
  margin-bottom: 12px;
}

.magic-panel .v-btn {
  color: #f6d365 !important;
}

.magic-social {
  margin-top: 12px;
}

.magic-section-title {
  font-family: "Cinzel", serif;
  font-size: clamp(1.6rem, 2.4vw, 2.2rem);
  color: #f7f4ff;
  margin-bottom: 6px;
}

.magic-section-subtitle {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230,225,255,0.7);
  margin-bottom: 18px;
}

.magic-micro {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: rgba(246,211,101,0.85);
  font-size: 0.8rem;
  text-transform: lowercase;
}

.magic-micro span {
  padding: 4px 8px;
  border-radius: 999px;
  border: 1px solid rgba(246,211,101,0.2);
  background: rgba(246,211,101,0.08);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes drift {
  0% { transform: translate3d(0, 0, 0); }
  50% { transform: translate3d(4%, -2%, 0); }
  100% { transform: translate3d(0, 0, 0); }
}

@keyframes floaty {
  0%, 100% { transform: translateY(-12px); }
  50% { transform: translateY(4px); }
}

@media (max-width: 960px) {
  .magic-wrap { padding-top: 32px; }
  .magic-actions { justify-content: flex-start; }
}
</style>
