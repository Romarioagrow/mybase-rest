<template>
  <v-content class="magic-auth-stage">
    <div class="magic-auth-sky"></div>
    <v-container fluid class="magic-auth-wrap">
      <v-row align="center" class="magic-auth-hero">
        <v-col cols="12" md="5" class="magic-auth-copy">
          <div class="magic-auth-kicker">MyBase / Access Portal</div>
          <h1 class="magic-auth-title">
            Enter the studio
            <span class="magic-auth-glow">and shape your signal.</span>
          </h1>
          <p class="magic-auth-subtitle">
            Login to continue your rituals, or create a new identity and let your
            analytics bloom.
          </p>
          <div class="magic-auth-orb">
            <div class="orb-core"></div>
            <div class="orb-ring"></div>
            <div class="orb-ring ring-two"></div>
          </div>
        </v-col>
        <v-col cols="12" md="7">
          <v-card class="magic-auth-card" elevation="12">
            <v-card-title class="magic-auth-card-title">
              <span>{{ showLogin ? 'Welcome Back' : 'Join Us' }}</span>
            </v-card-title>

            <v-card-text>
              <!-- Alert notification -->
              <transition name="slide-fade">
                <v-alert
                    v-if="has_login_response_alert"
                    :type="get_login_response_type"
                    dense
                    dismissible
                    class="magic-auth-alert mb-4"
                    @input="clearLoginResponse"
                >
                  {{ login_response_alert.message }}
                </v-alert>
              </transition>

              <!-- Tab-style toggle -->
              <div class="magic-tabs">
                <button
                    class="magic-tab"
                    :class="{ active: showLogin }"
                    @click="switchTab(true)"
                >
                  <v-icon small class="mr-2">mdi-login</v-icon>
                  Sign in
                </button>
                <button
                    class="magic-tab"
                    :class="{ active: !showLogin }"
                    @click="switchTab(false)"
                >
                  <v-icon small class="mr-2">mdi-account-plus</v-icon>
                  Create account
                </button>
                <div class="magic-tab-indicator" :class="{ right: !showLogin }"></div>
              </div>

              <!-- Form container with transition -->
              <div class="magic-form-container">
                <transition :name="slideDirection" mode="out-in">
                  <div v-if="showLogin" key="login" class="magic-form-wrapper">
                    <user-login-form
                        :setLoginResponseData="setLoginResponseData"
                        :clearLoginResponse="clearLoginResponse"
                    ></user-login-form>
                  </div>
                  <div v-else key="register" class="magic-form-wrapper">
                    <user-registration-form
                        :setLoginResponseData="setLoginResponseData"
                    ></user-registration-form>
                  </div>
                </transition>
              </div>
            </v-card-text>
          </v-card>

          <!-- Social login card -->
          <v-card class="magic-social-card" elevation="10">
            <div class="magic-social-title">
              <span class="magic-social-line"></span>
              <span>or continue with</span>
              <span class="magic-social-line"></span>
            </div>
            <v-row class="mt-2">
              <v-col cols="4">
                <v-btn class="magic-social-btn facebook" block @click="facebookAuth()">
                  <v-icon>mdi-facebook</v-icon>
                </v-btn>
              </v-col>
              <v-col cols="4">
                <v-btn class="magic-social-btn instagram" block @click="instAuth()">
                  <v-icon>mdi-instagram</v-icon>
                </v-btn>
              </v-col>
              <v-col cols="4">
                <v-btn class="magic-social-btn google" block @click="googleAuth()">
                  <v-icon>mdi-google</v-icon>
                </v-btn>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>

      <!--AUTHORIZES STORE USERS-->
      <v-row>
        <!--GOOGLE-->
        <v-col v-if="googleUser" cols="3">
          <v-card width="300" disabled>
            <v-card-title>
              <div class="regular-1">Google</div>
              <!--                <v-img contain :src="user.user_pic"></v-img>-->
            </v-card-title>
            <v-card-subtitle>
              <!--                <div>{{ user.name }}</div>-->
            </v-card-subtitle>
            <v-card-actions>
              <v-btn disabled small block color="error" @click="logout()">
                Logout
              </v-btn>
            </v-card-actions>
            <v-divider/>
          </v-card>
        </v-col>

        <v-col v-if="instProfile" cols="3">
          <v-card width="300" disabled>
            <v-card-title>
              <div class="regular-1">Instagram Graph API</div>
              <v-img contain :src="instProfile.profile_picture_url"></v-img>
            </v-card-title>
            <v-card-subtitle>
              <div>{{ instProfile.name }}</div>
            </v-card-subtitle>
            <v-card-actions>
              <v-btn disabled small block color="error" @click="logoutInstProfile()">
                Logout
              </v-btn>
            </v-card-actions>
            <v-divider/>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-content>
</template>

<script>
import facebookLogin from 'facebook-login-vuejs';
import axios from "axios";
import UserRegistrationForm from "components/UserRegistrationForm.vue";
import UserLoginForm from "components/UserLoginForm.vue";

export default {
  components: {
    UserLoginForm,
    UserRegistrationForm,
    facebookLogin
  },
  data() {
    return {
      showLogin: true,
      slideDirection: 'slide-left',
      login_response_alert: {
        has_response: false,
        type: '',
        message: ''
      },
      user: {},
      oldNameText: '',
      isConnected: false,
      name: '',
      personalID: '',
      picture: '',
      loginIncorrect: false,
    }
  },
  created() {
    //this.user = this.$store.state.currentUser
    //this.oldNameText = this.$store.state.currentUser.name
    //console.log(this.$store.state.currentUser)
  },
  computed: {
    get_login_response_type() {
      return this.login_response_alert.type
    },

    has_login_response_alert() {
      return this.login_response_alert.has_response
    },
    instProfile() {
      // return this.$store.state.instProfile
      return null
    },
    googleUser() {
      return null
      //return this.$store.state.currentUser
    },
    userAuth() {
      return null
      //return this.$store.state.currentUser
    }
  },
  methods: {
    switchTab(isLogin) {
      if (this.showLogin === isLogin) return;
      this.slideDirection = isLogin ? 'slide-right' : 'slide-left';
      this.showLogin = isLogin;
      this.clearLoginResponse();
    },
   
    logoutInstProfile() {
      this.$store.dispatch('doLogout')
    },
    facebookAuth() {
      // Use Spring Security OAuth2 for Facebook authentication
      this.$store.dispatch('doFacebookAuth')
    },

    googleAuth() {
      this.$store.dispatch("doGoogleAuth")
    },
    instAuth() {
      this.$store.dispatch("doInstagramAuth")
    },
    logout() {
      this.$store.dispatch("doLogout")
      this.user = {}
    },
    renameUser(user) {
      let newName = this.oldNameText
      console.log(newName)
    },
    
    clearLoginResponse() {
      this.setLoginResponseData('', '', false)
      /*this.login_response_alert.has_response = false
      this.login_response_alert.type = 'info'
      this.login_response_alert.message = ''*/
    },
    setLoginResponseData(type, message, hasResponse) {
      console.log('setLoginResponseData')
      this.login_response_alert.has_response = hasResponse
      this.login_response_alert.type = type
      this.login_response_alert.message = message
    }
  },
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Space+Grotesk:wght@300;400;600&display=swap');

.magic-auth-stage {
  min-height: calc(100vh - 64px);
  position: relative;
  overflow: hidden;
  background: radial-gradient(1200px 800px at 15% 10%, #2a1a47 0%, #0f1027 45%, #090b15 100%);
}

.magic-auth-sky {
  position: absolute;
  inset: -20% -10%;
  background:
    radial-gradient(2px 2px at 12% 20%, rgba(255,255,255,0.45) 40%, transparent 60%),
    radial-gradient(2px 2px at 78% 30%, rgba(255,255,255,0.35) 40%, transparent 60%),
    radial-gradient(1px 1px at 55% 80%, rgba(255,255,255,0.5) 40%, transparent 60%);
  opacity: 0.5;
  animation: drift 34s linear infinite;
}

.magic-auth-wrap {
  position: relative;
  z-index: 2;
  padding-top: 48px;
  padding-bottom: 64px;
}

.magic-auth-kicker {
  font-family: "Space Grotesk", sans-serif;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  font-size: 0.75rem;
  color: #b9b5ff;
  margin-bottom: 12px;
}

.magic-auth-title {
  font-family: "Cinzel", serif;
  font-size: clamp(2.2rem, 4vw, 3.4rem);
  line-height: 1.1;
  color: #f7f4ff;
  margin: 0 0 16px 0;
}

.magic-auth-glow {
  display: block;
  color: #f6d365;
  text-shadow: 0 0 18px rgba(246, 211, 101, 0.45);
}

.magic-auth-subtitle {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.85);
  font-size: 1.05rem;
  max-width: 420px;
}

.magic-auth-card {
  background: rgba(12, 14, 30, 0.92) !important;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 24px;
  box-shadow: 0 24px 48px rgba(8, 9, 20, 0.6);
  overflow: hidden;
}

.magic-auth-card-title {
  font-family: "Cinzel", serif;
  color: #f7f4ff;
  font-size: 1.5rem;
  padding: 24px 24px 8px;
}

.magic-auth-alert {
  border-radius: 12px;
}

/* Tabs */
.magic-tabs {
  display: flex;
  position: relative;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 16px;
  padding: 4px;
  margin-bottom: 24px;
}

.magic-tab {
  flex: 1;
  padding: 14px 20px;
  background: transparent;
  border: none;
  color: rgba(230, 225, 255, 0.6);
  font-family: "Space Grotesk", sans-serif;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  border-radius: 12px;
}

.magic-tab:hover:not(.active) {
  color: rgba(230, 225, 255, 0.85);
}

.magic-tab.active {
  color: #1b1833;
}

.magic-tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: linear-gradient(120deg, #f6d365, #fda085);
  border-radius: 12px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(246, 211, 101, 0.3);
}

.magic-tab-indicator.right {
  transform: translateX(100%);
}

/* Form container */
.magic-form-container {
  min-height: 280px;
  position: relative;
}

.magic-form-wrapper {
  width: 100%;
}

/* Social card */
.magic-social-card {
  margin-top: 16px;
  padding: 20px;
  background: rgba(12, 14, 30, 0.75) !important;
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 20px;
}

.magic-social-title {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.5);
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 16px;
  text-transform: none;
  letter-spacing: normal;
}

.magic-social-line {
  flex: 1;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
}

.magic-social-btn {
  text-transform: none;
  border-radius: 14px;
  font-weight: 600;
  min-height: 52px;
  color: #f7f4ff !important;
  border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.04) !important;
  transition: all 0.25s ease;
}

.magic-social-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.3);
}

.magic-social-btn.facebook {
  background: rgba(59, 89, 152, 0.2) !important;
  border-color: rgba(59, 89, 152, 0.4);
}
.magic-social-btn.facebook:hover {
  background: rgba(59, 89, 152, 0.35) !important;
}

.magic-social-btn.instagram {
  background: rgba(214, 41, 118, 0.15) !important;
  border-color: rgba(214, 41, 118, 0.4);
}
.magic-social-btn.instagram:hover {
  background: rgba(214, 41, 118, 0.3) !important;
}

.magic-social-btn.google {
  background: rgba(66, 133, 244, 0.15) !important;
  border-color: rgba(66, 133, 244, 0.4);
}
.magic-social-btn.google:hover {
  background: rgba(66, 133, 244, 0.3) !important;
}

/* Orb */
.magic-auth-orb {
  margin-top: 24px;
  width: 180px;
  height: 180px;
  position: relative;
}

.orb-core {
  position: absolute;
  inset: 32px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(246,211,101,0.9), rgba(246,211,101,0.1));
  box-shadow: 0 0 28px rgba(246,211,101,0.6);
}

.orb-ring {
  position: absolute;
  inset: 8px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.12);
  animation: spin 18s linear infinite;
}

.ring-two {
  inset: 20px;
  animation-duration: 26s;
}

/* Transitions */
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes drift {
  0% { transform: translate3d(0, 0, 0); }
  50% { transform: translate3d(4%, -2%, 0); }
  100% { transform: translate3d(0, 0, 0); }
}

@media (max-width: 960px) {
  .magic-auth-wrap { padding-top: 32px; }
  .magic-auth-orb { margin-bottom: 24px; }
  .magic-tab { padding: 12px 16px; font-size: 0.85rem; }
}
</style>
