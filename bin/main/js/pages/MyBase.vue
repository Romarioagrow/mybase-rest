<template>
  <v-content class="mybase-stage">
    <div class="mybase-glows">
      <span class="mybase-glow glow-a"></span>
      <span class="mybase-glow glow-b"></span>
    </div>

    <v-container fluid class="mybase-wrap">
      <div class="mybase-nav">
        <div class="mybase-brand" @click="goToLanding">
          <div class="mybase-logo">
            <v-icon small>mdi-fingerprint</v-icon>
          </div>
          <span>MyBase</span>
        </div>

        <div class="mybase-nav-actions">
          <div v-if="currentMode" class="mybase-mode" :class="currentMode === 'OWNER' ? 'owner' : 'public'">
            <v-icon x-small>{{ currentMode === 'OWNER' ? 'mdi-shield-check' : 'mdi-earth' }}</v-icon>
            {{ currentMode === 'OWNER' ? 'OWNER MODE' : 'PUBLIC MODE' }}
          </div>

          <v-btn v-if="currentView !== 'landing'" text small class="mybase-nav-btn" @click="goToLanding">
            Новый поиск
          </v-btn>

          <v-btn v-if="currentView === 'landing'" class="mybase-nav-btn primary" @click="startOwnerFlow">
            <v-icon left small>mdi-instagram</v-icon>
            Connect Instagram
          </v-btn>
        </div>
      </div>

      <main v-if="currentView === 'landing'" class="mybase-landing">
        <div class="mybase-landing-inner">
          <div class="mybase-pill">
            <v-icon x-small>mdi-earth</v-icon>
            Public Presence Analytics
          </div>

          <h1 class="mybase-landing-title">
            Осмыслите свой
            <span>цифровой ритм</span>
          </h1>

          <p class="mybase-landing-subtitle">
            Введите никнейм. Мы соберем аналитику по публично доступным данным, не нарушая приватность.
          </p>

          <div class="mybase-search">
            <div class="mybase-search-shell" :class="{ focused: isSearchFocused }">
              <div class="mybase-search-inner">
                <v-icon class="mybase-search-icon">mdi-magnify</v-icon>
                <input
                  v-model="searchInput"
                  type="text"
                  placeholder="например: @alex_dev или instagram.com/..."
                  @focus="isSearchFocused = true"
                  @blur="isSearchFocused = false"
                  @keydown.enter="handlePublicSearch"
                />
                <button
                  class="mybase-search-btn"
                  :disabled="isSyncing"
                  @click="handlePublicSearch"
                >
                  <v-icon v-if="isSyncing" small class="mdi-spin">mdi-loading</v-icon>
                  <span v-else>Найти</span>
                </button>
              </div>
            </div>
          </div>

          <div class="mybase-landing-grid">
            <div class="mybase-landing-card">
              <v-icon>mdi-check-circle-outline</v-icon>
              Только публичные данные
            </div>
            <div class="mybase-landing-card">
              <v-icon>mdi-shield-check-outline</v-icon>
              Без паролей и логина
            </div>
            <div class="mybase-landing-card">
              <v-icon>mdi-chart-bar</v-icon>
              Метрики ритма и тем
            </div>
          </div>
        </div>
      </main>

      <div v-if="currentView === 'oauth'" class="mybase-oauth">
        <div class="mybase-oauth-card">
          <div class="mybase-oauth-head">
            <v-icon color="#E1306C">mdi-instagram</v-icon>
            Instagram Graph API
          </div>
          <h2>Запрос доступа</h2>
          <p>
            Приложение <strong>MyBase Analytics</strong> запрашивает доступ к вашему
            профессиональному аккаунту Instagram.
          </p>
          <div class="mybase-oauth-perms">
            <div>
              <v-icon small color="#16a34a">mdi-check</v-icon>
              Чтение профиля и инсайтов
            </div>
            <div>
              <v-icon small color="#16a34a">mdi-check</v-icon>
              Список медиа (фото/видео)
            </div>
          </div>
          <div class="mybase-oauth-note">
            <v-icon x-small>mdi-information-outline</v-icon>
            Мы не получаем доступ к личным сообщениям и не можем публиковать контент.
          </div>
          <div class="mybase-oauth-actions">
            <v-btn text class="mybase-oauth-cancel" @click="goToLanding">Отмена</v-btn>
            <v-btn class="mybase-oauth-allow" @click="completeOAuth" :disabled="isProcessingAuth">
              <v-icon v-if="isProcessingAuth" left small class="mdi-spin">mdi-loading</v-icon>
              {{ isProcessingAuth ? 'Connecting...' : 'Разрешить' }}
            </v-btn>
          </div>
        </div>
      </div>

      <main v-if="currentView === 'dashboard' && personData" class="mybase-dashboard">
        <v-card class="mybase-profile-card" elevation="0">
          <div class="profile-left">
            <div class="profile-avatar">
              <img v-if="personData.avatar" :src="personData.avatar" alt="avatar" />
              <span v-else>{{ personData.handle.charAt(0).toUpperCase() }}</span>
              <div v-if="personData.isVerified" class="profile-verified">
                <v-icon x-small color="#3b82f6">mdi-check-decagram</v-icon>
              </div>
            </div>
            <div>
              <h1 class="profile-handle">@{{ personData.handle }}</h1>
              <div class="profile-meta">
                <span v-if="currentMode === 'OWNER'" class="meta-chip owner">
                  <v-icon x-small>mdi-check-circle</v-icon>
                  Graph API Connected
                </span>
                <span v-else class="meta-chip public">
                  <v-icon x-small>mdi-alert-circle-outline</v-icon>
                  Public Estimate
                </span>
                <span class="meta-item">{{ formatNumber(personData.mediaCount) }} posts</span>
                <span v-if="personData.bio" class="meta-item profile-bio">{{ personData.bio }}</span>
              </div>
            </div>
          </div>
          <div class="profile-actions">
            <v-btn class="mybase-refresh" @click="runSync" :disabled="isSyncing">
              <v-icon left small :class="{ 'mdi-spin': isSyncing }">mdi-refresh</v-icon>
              {{ isSyncing ? 'Синхронизация...' : 'Обновить' }}
            </v-btn>
          </div>
        </v-card>

        <div class="mybase-tabs">
          <button
            type="button"
            class="mybase-tab"
            :class="{ 'is-active': currentTab === 'overview' }"
            @click="currentTab = 'overview'"
          >
            Overview
          </button>
          <button
            type="button"
            class="mybase-tab"
            :class="{ 'is-active': currentTab === 'activity' }"
            @click="currentTab = 'activity'"
          >
            Activity
          </button>
          <button
            type="button"
            class="mybase-tab"
            :class="{ 'is-active': currentTab === 'content' }"
            @click="currentTab = 'content'"
          >
            Content
          </button>
          <button
            type="button"
            class="mybase-tab"
            :class="{ 'is-active': currentTab === 'connections' }"
            @click="currentTab = 'connections'"
          >
            Connections
          </button>
        </div>

        <div class="mybase-tab-panels">
          <section v-show="currentTab === 'overview'">
            <div class="mybase-overview">
              <v-row>
                <v-col cols="12" md="6" lg="3">
                  <div class="metric-card">
                    <v-icon class="metric-icon">mdi-flash</v-icon>
                    <div class="metric-title">Cadence (Ритм)</div>
                    <div class="metric-value">{{ metrics.postsPerWeek }} <span>/ нед.</span></div>
                    <div class="metric-foot" :class="metrics.regularityScore > 70 ? 'good' : 'warn'">
                      <v-icon x-small>{{ metrics.regularityScore > 70 ? 'mdi-trending-up' : 'mdi-trending-down' }}</v-icon>
                      {{ metrics.regularityScore }}% Regularity
                    </div>
                  </div>
                </v-col>
                <v-col cols="12" md="6" lg="3">
                  <div class="metric-card">
                    <v-icon class="metric-icon">mdi-heart-outline</v-icon>
                    <div class="metric-title">Engagement Score</div>
                    <div class="metric-value">{{ formatNumber(metrics.engagementScore) }}</div>
                    <div class="metric-foot muted">Avg. weighted interactions</div>
                  </div>
                </v-col>
                <v-col cols="12" md="6" lg="3">
                  <div class="metric-card">
                    <v-icon class="metric-icon">mdi-chart-bar</v-icon>
                    <div class="metric-title">Content Mix</div>
                    <div class="metric-bar">
                      <span class="bar-video" :style="{ width: metrics.videoRatio + '%' }"></span>
                      <span class="bar-image" :style="{ width: (100 - metrics.videoRatio) + '%' }"></span>
                    </div>
                    <div class="metric-foot split">
                      <span><i class="dot video"></i> Video {{ metrics.videoRatio }}%</span>
                      <span><i class="dot image"></i> Image {{ 100 - metrics.videoRatio }}%</span>
                    </div>
                  </div>
                </v-col>
                <v-col cols="12" md="6" lg="3">
                  <div class="metric-card">
                    <v-icon class="metric-icon">mdi-pound</v-icon>
                    <div class="metric-title">Top Hashtag</div>
                    <div class="metric-value tag">#{{ metrics.topHashtag }}</div>
                    <div class="metric-foot muted">Used in {{ metrics.topHashtagCount }} posts</div>
                  </div>
                </v-col>
              </v-row>

              <div class="mybase-ai-summary">
                <div>
                  <div class="ai-title">AI Summary</div>
                  <div class="ai-text">
                    {{ metrics.aiSummary || 'Синхронизируйте данные для получения анализа.' }}
                  </div>
                </div>
                <v-icon class="ai-icon">mdi-star-four-points</v-icon>
              </div>
            </div>
          </section>

          <section v-show="currentTab === 'activity'">
            <div class="mybase-activity">
              <div class="activity-header">
                <div>
                  <v-icon small color="#818cf8">mdi-grid</v-icon>
                  Heatmap активности
                </div>
                <span>Local Time</span>
              </div>

              <div class="activity-table">
                <div class="activity-row head">
                  <div></div>
                  <div class="activity-hour" v-for="h in 24" :key="h">{{ h - 1 }}</div>
                </div>
                <div class="activity-row" v-for="(day, dIndex) in days" :key="day">
                  <div class="activity-day">{{ day }}</div>
                  <div
                    v-for="h in 24"
                    :key="h"
                    class="activity-cell"
                    :class="getHeatmapColor(heatmapData[dIndex][h - 1])"
                  ></div>
                </div>
              </div>

              <div class="activity-legend">
                <span>Меньше</span>
                <div class="legend-cells">
                  <div class="activity-cell zero"></div>
                  <div class="activity-cell low"></div>
                  <div class="activity-cell mid"></div>
                  <div class="activity-cell high"></div>
                </div>
                <span>Больше</span>
              </div>
            </div>
          </section>

          <section v-show="currentTab === 'content'">
            <div class="mybase-content">
              <v-card class="content-card" elevation="0">
                <div class="content-head">
                  <h3>Последние посты</h3>
                  <select>
                    <option>По вовлеченности</option>
                    <option>По дате</option>
                  </select>
                </div>
                <div class="content-list">
                  <div v-for="post in contentItems" :key="post.id" class="content-item">
                    <div class="content-thumb">
                      <v-icon v-if="post.type === 'VIDEO'" x-small class="content-badge">mdi-play</v-icon>
                      <v-icon>{{ post.type === 'VIDEO' ? 'mdi-filmstrip' : 'mdi-image' }}</v-icon>
                    </div>
                    <div class="content-body">
                      <div class="content-meta">
                        <span class="content-date">{{ formatDate(post.publishedAt) }}</span>
                        <div class="content-stats">
                          <span><v-icon x-small>mdi-heart</v-icon> {{ formatNumber(post.metrics.likes) }}</span>
                          <span><v-icon x-small>mdi-message-text</v-icon> {{ formatNumber(post.metrics.comments) }}</span>
                        </div>
                      </div>
                      <p>{{ post.caption }}</p>
                      <div class="content-tags">
                        <span v-for="tag in post.hashtags.slice(0, 4)" :key="tag">#{{ tag }}</span>
                      </div>
                    </div>
                    <div class="content-score">
                      <div class="score-label">Impact Score</div>
                      <div class="score-value">{{ formatNumber(post.engagementScore) }}</div>
                      <div class="score-bar">
                        <span :style="{ width: Math.min(post.engagementScore / 50, 100) + '%' }"></span>
                      </div>
                    </div>
                  </div>
                </div>
              </v-card>
            </div>
          </section>

          <section v-show="currentTab === 'connections'">
            <v-row class="mybase-connections">
              <v-col cols="12" md="6">
                <v-card class="connections-card" elevation="0">
                  <div class="connections-head">
                    <h3>Внешние ссылки</h3>
                    <v-icon small>mdi-link-variant</v-icon>
                  </div>
                  <div v-if="links.length === 0" class="connections-empty">
                    Нет найденных ссылок в био или последних постах.
                  </div>
                  <div class="connections-list">
                    <div v-for="(link, idx) in links" :key="idx" class="connections-item">
                      <div class="connections-icon">
                        <v-icon small>mdi-earth</v-icon>
                      </div>
                      <div>
                        <div class="connections-domain">{{ link.domain }}</div>
                        <div class="connections-target">{{ link.target }}</div>
                      </div>
                      <span class="connections-chip">{{ link.source }}</span>
                    </div>
                  </div>
                </v-card>
              </v-col>
              <v-col cols="12" md="6">
                <v-card class="connections-card muted" elevation="0">
                  <div class="connections-audience">
                    <v-icon large>mdi-account-group-outline</v-icon>
                    <h3>Аудитория</h3>
                    <p>Анализ аудитории недоступен в публичном режиме. Подключите аккаунт владельца для доступа.</p>
                  </div>
                </v-card>
              </v-col>
            </v-row>
          </section>
        </div>
      </main>

      <footer class="mybase-footer">
        <div>MyBase Analytics Prototype</div>
        <div>Данные сгенерированы симулятором (Mock Backend). Реальное API не используется.</div>
      </footer>
    </v-container>
  </v-content>
</template>

<script>
export default {
  data() {
    return {
      currentView: 'landing',
      currentMode: null,
      currentTab: 'overview',
      searchInput: '',
      isProcessingAuth: false,
      isSyncing: false,
      isSearchFocused: false,
      personData: null,
      contentItems: [],
      days: ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс']
    }
  },
  computed: {
    metrics() {
      if (!this.contentItems.length) {
        return {
          postsPerWeek: '0.0',
          regularityScore: 0,
          engagementScore: 0,
          videoRatio: 0,
          topHashtag: 'none',
          topHashtagCount: 0,
          aiSummary: ''
        }
      }

      const dates = this.contentItems.map((i) => new Date(i.publishedAt))
      const weeksSpan = Math.max(1, (dates[0] - dates[dates.length - 1]) / (1000 * 60 * 60 * 24 * 7))
      const postsPerWeek = (this.contentItems.length / weeksSpan).toFixed(1)

      const videoCount = this.contentItems.filter((i) => i.type === 'VIDEO').length
      const videoRatio = Math.round((videoCount / this.contentItems.length) * 100)

      const allTags = this.contentItems.flatMap((i) => i.hashtags)
      const tagCounts = allTags.reduce((acc, tag) => {
        acc[tag] = (acc[tag] || 0) + 1
        return acc
      }, {})
      const sortedTags = Object.entries(tagCounts).sort((a, b) => b[1] - a[1])
      const topHashtag = sortedTags[0] ? sortedTags[0][0] : 'none'
      const topHashtagCount = sortedTags[0] ? sortedTags[0][1] : 0

      const avgEng = this.contentItems.reduce((sum, i) => sum + i.engagementScore, 0) / this.contentItems.length

      const handle = this.personData ? this.personData.handle : ''
      const summary =
        this.currentMode === 'OWNER'
          ? `Отличная работа! Ваш профиль показывает высокую активность (${postsPerWeek} постов/нед). Видео контент генерирует на 30% больше вовлеченности. Рекомендуем продолжать использовать #${topHashtag}.`
          : `Пользователь @${handle} активен в основном в будние дни. Основной фокус: ${topHashtag}. Публичные данные указывают на растущий интерес аудитории.`

      return {
        postsPerWeek,
        regularityScore: Math.min(Math.round(postsPerWeek * 18), 98),
        engagementScore: Math.round(avgEng),
        videoRatio,
        topHashtag,
        topHashtagCount,
        aiSummary: summary
      }
    },
    heatmapData() {
      const map = Array.from({ length: 7 }, () => Array(24).fill(0))
      this.contentItems.forEach((item) => {
        const date = new Date(item.publishedAt)
        const day = date.getDay()
        const hour = date.getHours()
        const uiDayIndex = day === 0 ? 6 : day - 1
        map[uiDayIndex][hour] += 1
      })
      return map
    },
    links() {
      const res = []
      if (this.personData) {
        if (this.personData.bio && this.personData.bio.includes('linktr.ee')) {
          res.push({ source: 'BIO', target: `https://linktr.ee/${this.personData.handle}`, domain: 'linktr.ee' })
        }
        const postWithLink = this.contentItems.find((p) => p.caption.includes('http'))
        if (postWithLink) {
          res.push({ source: 'POST', target: 'https://youtube.com/watch?v=...', domain: 'youtube.com' })
        }
      }
      return res
    }
  },
  methods: {
    generateMockContent(mode, count = 20) {
      const items = []
      const now = new Date()
      const topics = ['tech', 'design', 'lifestyle', 'ai', 'startup', 'coding', 'travel']
      const types = ['VIDEO', 'IMAGE', 'CAROUSEL']

      for (let i = 0; i < count; i += 1) {
        const date = new Date(now.getTime() - Math.random() * 90 * 24 * 60 * 60 * 1000)
        date.setHours(9 + Math.floor(Math.random() * 14))

        const type = types[Math.floor(Math.random() * types.length)]
        const likes = mode === 'OWNER' ? Math.floor(Math.random() * 5000) + 100 : Math.floor(Math.random() * 5000) * 0.8
        const comments = Math.floor(likes * (Math.random() * 0.05))
        const views = type === 'VIDEO' ? likes * (2 + Math.random() * 5) : null
        const topic = topics[Math.floor(Math.random() * topics.length)]

        items.push({
          id: `media_${i}_${Date.now()}`,
          type,
          publishedAt: date.toISOString(),
          caption: `Exploring the future of #${topic}. What do you think about the new updates? Let's build something great together! 🚀✨ #mybase #${topic} #innovation`,
          hashtags: [topic, 'mybase', 'future', 'innovation'],
          metrics: {
            likes: Math.floor(likes),
            comments: Math.floor(comments),
            views: views ? Math.floor(views) : null
          },
          engagementScore: Math.floor(likes + comments * 2 + (views ? views * 0.1 : 0))
        })
      }

      return items.sort((a, b) => new Date(b.publishedAt) - new Date(a.publishedAt))
    },
    getPublicProfile(username) {
      return new Promise((resolve) => {
        setTimeout(() => {
          const cleanHandle = username.replace('@', '').replace('https://instagram.com/', '').replace('/', '')
          resolve({
            handle: cleanHandle,
            avatar: null,
            isVerified: Math.random() > 0.8,
            mediaCount: Math.floor(Math.random() * 500) + 20,
            bio: `Digital creator | Passionate about Tech & Design. 📍 Global Citizen. 🔗 linktr.ee/${cleanHandle}`,
            mode: 'PUBLIC'
          })
        }, 800)
      })
    },
    getOwnerProfile() {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            handle: 'alex_creator_pro',
            avatar: null,
            isVerified: true,
            mediaCount: 1250,
            bio: 'Official Account. Professional Developer. Building MyBase. Contact: email@example.com',
            mode: 'OWNER'
          })
        }, 1500)
      })
    },
    syncContent(handle, mode) {
      return new Promise((resolve) => {
        setTimeout(() => {
          const count = mode === 'OWNER' ? 60 : 25
          resolve(this.generateMockContent(mode, count))
        }, 1200)
      })
    },
    async handlePublicSearch() {
      if (!this.searchInput) return

      this.isSyncing = true
      try {
        const profile = await this.getPublicProfile(this.searchInput)
        this.personData = profile
        this.currentMode = 'PUBLIC'
        this.contentItems = await this.syncContent(profile.handle, 'PUBLIC')
        this.currentView = 'dashboard'
      } catch (e) {
        this.$emit('error', e)
      } finally {
        this.isSyncing = false
      }
    },
    startOwnerFlow() {
      this.currentView = 'oauth'
    },
    async completeOAuth() {
      this.isProcessingAuth = true
      try {
        const profile = await this.getOwnerProfile('fake_token_123')
        this.personData = profile
        this.currentMode = 'OWNER'
        this.contentItems = await this.syncContent(profile.handle, 'OWNER')
        this.currentView = 'dashboard'
      } catch (e) {
        this.$emit('error', e)
      } finally {
        this.isProcessingAuth = false
      }
    },
    async runSync() {
      if (!this.personData) return
      this.isSyncing = true
      try {
        this.contentItems = await this.syncContent(this.personData.handle, this.currentMode)
      } finally {
        this.isSyncing = false
      }
    },
    goToLanding() {
      this.currentView = 'landing'
      this.personData = null
      this.contentItems = []
      this.currentMode = null
      this.searchInput = ''
      this.currentTab = 'overview'
    },
    formatDate(isoString) {
      const d = new Date(isoString)
      return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' })
    },
    formatNumber(num) {
      if (!num) return '0'
      return num >= 1000 ? `${(num / 1000).toFixed(1)}k` : num
    },
    getHeatmapColor(count) {
      if (count === 0) return 'zero'
      if (count < 2) return 'low'
      if (count < 4) return 'mid'
      return 'high'
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Space+Grotesk:wght@300;400;600&display=swap');

.mybase-stage {
  min-height: calc(100vh - 64px);
  position: relative;
  overflow: hidden;
  background: radial-gradient(1200px 800px at 15% 10%, rgba(74, 44, 117, 0.75) 0%, rgba(12, 15, 30, 0.95) 55%, #0b0d18 100%);
  color: #f7f4ff;
}

.mybase-glows {
  position: absolute;
  inset: -20% -10%;
  pointer-events: none;
}

.mybase-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(140px);
  opacity: 0.45;
}

.glow-a {
  width: 520px;
  height: 520px;
  background: rgba(99, 102, 241, 0.45);
  top: -8%;
  left: -6%;
}

.glow-b {
  width: 560px;
  height: 560px;
  background: rgba(16, 185, 129, 0.35);
  bottom: -12%;
  right: -6%;
}

.mybase-wrap {
  position: relative;
  z-index: 2;
  padding: 32px 24px 56px;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 64px);
  max-width: 1240px;
  margin: 0 auto;
}

.mybase-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 12px 16px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(10, 12, 22, 0.6);
  backdrop-filter: blur(8px);
}

.mybase-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  cursor: pointer;
  font-family: "Space Grotesk", sans-serif;
  color: #f7f4ff;
}

.mybase-logo {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  background: rgba(99, 102, 241, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12px 22px rgba(99, 102, 241, 0.35);
}

.mybase-nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.mybase-mode {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.16em;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.mybase-mode.owner {
  background: rgba(99, 102, 241, 0.12);
  color: #b9b5ff;
  border-color: rgba(99, 102, 241, 0.4);
}

.mybase-mode.public {
  background: rgba(16, 185, 129, 0.12);
  color: #7efac6;
  border-color: rgba(16, 185, 129, 0.35);
}

.mybase-nav-btn {
  text-transform: none !important;
  border-radius: 12px;
  font-weight: 600;
  color: #e6e1ff !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 0.8rem !important;
  height: 34px;
  padding: 0 14px !important;
}

.mybase-nav-btn.primary {
  background: linear-gradient(120deg, rgba(99, 102, 241, 0.9), rgba(99, 102, 241, 0.7)) !important;
  border: none;
}

.mybase-landing {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.mybase-landing-inner {
  max-width: 780px;
  width: 100%;
}

.mybase-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 0.7rem;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(230, 225, 255, 0.7);
  margin-bottom: 16px;
}

.mybase-landing-title {
  font-family: "Cinzel", serif;
  font-size: clamp(2.4rem, 4.5vw, 4.4rem);
  margin-bottom: 12px;
  line-height: 1.05;
}

.mybase-landing-title span {
  display: block;
  background: linear-gradient(120deg, #d7d7ff, #7efac6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.mybase-landing-subtitle {
  color: rgba(230, 225, 255, 0.75);
  font-size: 1.05rem;
  margin: 0 auto 28px;
  max-width: 520px;
}

.mybase-search {
  display: flex;
  justify-content: center;
}

.mybase-search-shell {
  padding: 2px;
  border-radius: 16px;
  background: linear-gradient(120deg, rgba(99, 102, 241, 0.7), rgba(16, 185, 129, 0.7));
  max-width: 520px;
  width: 100%;
}

.mybase-search-shell.focused {
  box-shadow: 0 18px 36px rgba(16, 185, 129, 0.25);
}

.mybase-search-inner {
  display: flex;
  align-items: center;
  background: #0b0f1d;
  border-radius: 14px;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  gap: 10px;
}

.mybase-search-icon {
  color: rgba(230, 225, 255, 0.6) !important;
}

.mybase-search-inner input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: #fff;
  font-family: "Space Grotesk", sans-serif;
}

.mybase-search-btn {
  border: none;
  background: #6366f1;
  color: #fff;
  border-radius: 12px;
  padding: 10px 18px;
  cursor: pointer;
  font-weight: 600;
}

.mybase-search-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.mybase-landing-grid {
  margin-top: 28px;
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.mybase-landing-card {
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(15, 18, 36, 0.6);
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.85rem;
  color: rgba(230, 225, 255, 0.8);
}

.mybase-oauth {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 50;
}

.mybase-oauth-card {
  background: #ffffff;
  color: #0f172a;
  padding: 28px;
  border-radius: 24px;
  max-width: 460px;
  width: 100%;
  display: grid;
  gap: 16px;
}

.mybase-oauth-head {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  color: #e1306c;
}

.mybase-oauth-perms {
  display: grid;
  gap: 10px;
  background: #f1f5f9;
  padding: 12px;
  border-radius: 12px;
}

.mybase-oauth-note {
  display: flex;
  gap: 6px;
  font-size: 0.75rem;
  color: #64748b;
}

.mybase-oauth-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.mybase-oauth-allow {
  background: #1877f2 !important;
  color: #fff !important;
  text-transform: none !important;
  border-radius: 12px;
}

.mybase-oauth-cancel {
  text-transform: none !important;
  border-radius: 12px;
  background: #f1f5f9 !important;
  color: #475569 !important;
}

.mybase-dashboard {
  display: flex;
  flex-direction: column;
  gap: 18px;
  color: #e6e1ff;
}

.mybase-profile-card {
  background: linear-gradient(120deg, rgba(24, 32, 55, 0.9), rgba(12, 16, 32, 0.7)) !important;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 22px 26px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  box-shadow: 0 18px 36px rgba(6, 10, 24, 0.6);
}

.profile-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(120deg, #f59e0b, #ec4899, #8b5cf6);
  padding: 2px;
  position: relative;
}

.profile-avatar img,
.profile-avatar span {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #0b0f1d;
  color: #fff;
  font-weight: 700;
  font-size: 1.6rem;
}

.profile-verified {
  position: absolute;
  right: -2px;
  bottom: -2px;
  background: #fff;
  border-radius: 50%;
  padding: 2px;
}

.profile-handle {
  font-size: 1.6rem;
  margin: 0 0 6px 0;
  font-weight: 700;
  color: #f8fafc;
}

.profile-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  color: rgba(230, 225, 255, 0.7);
  font-size: 0.85rem;
}

.profile-meta .meta-item {
  position: relative;
  padding-left: 12px;
}

.profile-meta .meta-item::before {
  content: "";
  position: absolute;
  left: 4px;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 12px;
  background: rgba(255, 255, 255, 0.12);
}

.profile-bio {
  max-width: 360px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: rgba(230, 225, 255, 0.5);
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

.meta-chip.owner {
  background: rgba(16, 185, 129, 0.15);
  color: #7efac6;
}

.meta-chip.public {
  background: rgba(251, 191, 36, 0.15);
  color: #facc15;
}

.mybase-refresh {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #e6e1ff !important;
  text-transform: none !important;
  border-radius: 12px;
}

.mybase-tabs {
  background: rgba(15, 18, 36, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 18px;
  padding: 6px;
  display: inline-flex;
  align-self: flex-start;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.03), 0 12px 24px rgba(5, 8, 20, 0.35);
  gap: 6px;
}

.mybase-tab-panels {
  margin-top: 14px;
}

.mybase-tab {
  text-transform: none;
  font-weight: 600;
  color: rgba(230, 225, 255, 0.65) !important;
  border-radius: 12px;
  min-width: 0;
  min-height: 34px;
  padding: 0 20px;
  height: 36px;
  font-size: 0.9rem;
  letter-spacing: 0.01em;
  transition: background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
  background: transparent;
  border: none;
  cursor: pointer;
}

.mybase-tab:not(.is-active):hover {
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.85) !important;
}

.mybase-tab.is-active {
  background: #4f46e5;
  color: #ffffff !important;
  box-shadow: 0 10px 20px rgba(79, 70, 229, 0.35);
}

.mybase-overview {
  padding-top: 16px;
}

.metric-card {
  background: linear-gradient(150deg, rgba(22, 28, 46, 0.95), rgba(12, 16, 32, 0.85));
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 20px;
  padding: 20px 22px;
  display: grid;
  gap: 8px;
  min-height: 160px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 18px 36px rgba(6, 10, 24, 0.55);
}

.metric-card::before {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(140px 90px at 85% 18%, rgba(99, 102, 241, 0.18), transparent 60%);
  pointer-events: none;
}

.metric-card::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.06), transparent 40%);
  pointer-events: none;
  opacity: 0.4;
}

.metric-card > * {
  position: relative;
  z-index: 1;
}

.metric-icon {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 40px;
  opacity: 0.2;
  color: #7c83ff !important;
  pointer-events: none;
}

.metric-title {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.16em;
  color: rgba(230, 225, 255, 0.55);
}

.metric-value {
  font-size: 2rem;
  font-weight: 700;
  color: #f8fafc;
}

.metric-value span {
  font-size: 0.8rem;
  color: rgba(230, 225, 255, 0.6);
}

.metric-value.tag {
  color: #a5b4fc;
  text-shadow: 0 8px 18px rgba(99, 102, 241, 0.25);
}

.metric-foot {
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 6px;
}

.metric-foot.good {
  color: #34d399;
}

.metric-foot.warn {
  color: #fbbf24;
}

.metric-foot.muted {
  color: rgba(230, 225, 255, 0.55);
}

.metric-foot.split {
  display: flex;
  justify-content: space-between;
  color: rgba(230, 225, 255, 0.75);
}

.metric-bar {
  height: 10px;
  width: 100%;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 999px;
  overflow: hidden;
  display: flex;
}

.metric-bar span {
  height: 100%;
}

.bar-video {
  background: linear-gradient(90deg, #8b5cf6, #a855f7);
}

.bar-image {
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.dot.video {
  background: rgba(168, 85, 247, 0.8);
}

.dot.image {
  background: rgba(59, 130, 246, 0.8);
}

.mybase-ai-summary {
  margin-top: 16px;
  padding: 16px;
  border-radius: 18px;
  background: rgba(67, 56, 202, 0.2);
  border: 1px solid rgba(99, 102, 241, 0.4);
  display: flex;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
}

.ai-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.ai-text {
  color: rgba(230, 225, 255, 0.8);
}

.ai-icon {
  color: #a5b4fc !important;
  opacity: 0.9;
  font-size: 26px;
}

.mybase-activity {
  padding: 18px 0;
}

.activity-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.9rem;
  color: rgba(230, 225, 255, 0.7);
  margin-bottom: 16px;
}

.activity-table {
  overflow-x: auto;
}

.activity-row {
  display: grid;
  grid-template-columns: 40px repeat(24, minmax(16px, 1fr));
  gap: 4px;
  align-items: center;
  margin-bottom: 4px;
}

.activity-row.head {
  font-size: 0.6rem;
  text-transform: uppercase;
  color: rgba(230, 225, 255, 0.5);
}

.activity-hour {
  text-align: center;
}

.activity-day {
  font-size: 0.75rem;
  color: rgba(230, 225, 255, 0.6);
}

.activity-cell {
  height: 14px;
  border-radius: 4px;
}

.activity-cell.zero {
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(51, 65, 85, 0.4);
}

.activity-cell.low {
  background: rgba(99, 102, 241, 0.3);
  border: 1px solid rgba(99, 102, 241, 0.4);
}

.activity-cell.mid {
  background: rgba(99, 102, 241, 0.6);
  box-shadow: 0 0 8px rgba(99, 102, 241, 0.3);
}

.activity-cell.high {
  background: rgba(16, 185, 129, 0.9);
  box-shadow: 0 0 10px rgba(16, 185, 129, 0.4);
}

.activity-legend {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.7rem;
  color: rgba(230, 225, 255, 0.6);
}

.legend-cells {
  display: flex;
  gap: 4px;
}

.mybase-content {
  padding: 16px 0;
}

.content-card {
  background: rgba(12, 14, 30, 0.8) !important;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
}

.content-head {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.content-head h3 {
  margin: 0;
  color: #f8fafc;
}

.content-head select {
  background: rgba(15, 18, 36, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  color: rgba(230, 225, 255, 0.8);
  padding: 6px 10px;
  font-size: 0.75rem;
}

.content-list {
  display: grid;
}

.content-item {
  display: grid;
  grid-template-columns: 90px 1fr 140px;
  gap: 16px;
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.content-thumb {
  background: rgba(15, 18, 36, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.content-thumb .content-badge {
  position: absolute;
  top: 6px;
  right: 6px;
}

.content-body {
  display: grid;
  gap: 8px;
  font-size: 0.85rem;
  color: rgba(230, 225, 255, 0.8);
}

.content-body p {
  color: rgba(230, 225, 255, 0.8);
}

.content-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.75rem;
}

.content-date {
  background: rgba(99, 102, 241, 0.15);
  padding: 2px 8px;
  border-radius: 8px;
  color: #a5b4fc;
}

.content-stats {
  display: flex;
  gap: 12px;
  color: rgba(230, 225, 255, 0.6);
}

.content-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  font-size: 0.7rem;
}

.content-tags span {
  background: rgba(15, 18, 36, 0.9);
  border-radius: 999px;
  padding: 2px 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.content-score {
  display: grid;
  gap: 6px;
  align-content: center;
  font-size: 0.75rem;
}

.score-label {
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: rgba(230, 225, 255, 0.5);
}

.score-value {
  font-size: 1.2rem;
  font-weight: 700;
}

.score-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  overflow: hidden;
}

.score-bar span {
  display: block;
  height: 100%;
  background: linear-gradient(120deg, #6366f1, #a855f7);
}

.mybase-connections {
  padding: 16px 0;
}

.connections-card {
  background: rgba(12, 14, 30, 0.8) !important;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 16px;
}

.connections-card.muted {
  background: rgba(12, 14, 30, 0.5) !important;
}

.connections-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #f8fafc;
}

.connections-empty {
  color: rgba(230, 225, 255, 0.5);
  font-size: 0.8rem;
  padding: 12px 0;
}

.connections-list {
  display: grid;
  gap: 10px;
}

.connections-item {
  display: grid;
  grid-template-columns: 40px 1fr auto;
  gap: 12px;
  align-items: center;
  padding: 10px;
  border-radius: 12px;
  background: rgba(15, 18, 36, 0.8);
}

.connections-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(99, 102, 241, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a5b4fc;
}

.connections-domain {
  color: #f7f4ff;
  font-size: 0.85rem;
}

.connections-target {
  font-size: 0.7rem;
  color: rgba(230, 225, 255, 0.5);
}

.connections-chip {
  background: rgba(255, 255, 255, 0.08);
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 0.7rem;
  text-transform: uppercase;
  color: rgba(230, 225, 255, 0.6);
}

.connections-audience {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 8px;
  color: rgba(230, 225, 255, 0.7);
}

.connections-audience h3 {
  color: #f8fafc;
}

.connections-audience p {
  font-size: 0.85rem;
  color: rgba(230, 225, 255, 0.5);
}

.mybase-footer {
  margin-top: auto;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  text-align: center;
  font-size: 0.75rem;
  color: rgba(230, 225, 255, 0.5);
  display: grid;
  gap: 6px;
}

@media (max-width: 960px) {
  .mybase-nav {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .content-item {
    grid-template-columns: 80px 1fr;
  }

  .content-score {
    display: none;
  }
}

@media (max-width: 600px) {
  .activity-row {
    grid-template-columns: 32px repeat(24, minmax(12px, 1fr));
  }

  .profile-bio {
    display: none;
  }
}
</style>
