<template>
  <v-content class="oauth2-redirect-page">
    <v-container fluid fill-height>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="oauth2-card" elevation="12">
            <v-card-text class="text-center">
              <!-- Loading state -->
              <div v-if="loading" class="oauth2-loading">
                <v-progress-circular
                    indeterminate
                    size="64"
                    color="#f6d365"
                ></v-progress-circular>
                <h3 class="mt-4">Completing authentication...</h3>
                <p class="oauth2-subtitle">Please wait while we sign you in</p>
              </div>

              <!-- Success state -->
              <div v-else-if="success" class="oauth2-success">
                <v-icon size="64" color="success">mdi-check-circle</v-icon>
                <h3 class="mt-4">Authentication Successful!</h3>
                <p class="oauth2-subtitle">Redirecting to your dashboard...</p>
              </div>

              <!-- Error state -->
              <div v-else-if="error" class="oauth2-error">
                <v-icon size="64" color="error">mdi-alert-circle</v-icon>
                <h3 class="mt-4">Authentication Failed</h3>
                <p class="oauth2-subtitle error-message">{{ error }}</p>
                <v-btn
                    class="mt-4"
                    color="primary"
                    @click="goToLogin"
                >
                  Try Again
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-content>
</template>

<script>
export default {
  name: 'OAuth2Redirect',
  data() {
    return {
      loading: true,
      success: false,
      error: null
    }
  },
  async created() {
    await this.handleCallback();
  },
  methods: {
    async handleCallback() {
      // Get query parameters
      const urlParams = new URLSearchParams(window.location.search);
      const token = urlParams.get('token');
      const refreshToken = urlParams.get('refreshToken');
      const error = urlParams.get('error');

      console.log('OAuth2 callback received:', { token: !!token, refreshToken: !!refreshToken, error });

      try {
        const result = await this.$store.dispatch('handleOAuth2Callback', {
          token,
          refreshToken,
          error
        });

        if (result.success) {
          this.loading = false;
          this.success = true;

          // Redirect to home after a short delay
          setTimeout(() => {
            this.$router.push('/');
          }, 1500);
        } else {
          this.loading = false;
          this.error = result.error || 'Authentication failed';
        }
      } catch (err) {
        console.error('OAuth2 callback error:', err);
        this.loading = false;
        this.error = err.message || 'An unexpected error occurred';
      }
    },
    goToLogin() {
      this.$router.push('/auth');
    }
  }
}
</script>

<style scoped>
.oauth2-redirect-page {
  min-height: 100vh;
  background: radial-gradient(1200px 800px at 50% 30%, #2a1a47 0%, #0f1027 45%, #090b15 100%);
}

.oauth2-card {
  background: rgba(12, 14, 30, 0.92) !important;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 40px 20px;
}

.oauth2-loading h3,
.oauth2-success h3,
.oauth2-error h3 {
  color: #f7f4ff;
  font-family: "Space Grotesk", sans-serif;
}

.oauth2-subtitle {
  color: rgba(230, 225, 255, 0.7);
  font-family: "Space Grotesk", sans-serif;
  margin-top: 8px;
}

.error-message {
  color: #ff6b6b !important;
  word-break: break-word;
}
</style>
